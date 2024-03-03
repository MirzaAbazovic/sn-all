/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 09:16:37
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.utils.PrintVerlaufHelper;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;


/**
 * Dialog zur Anzeige der Verlaufs-History zu einem best. Auftrag.
 *
 *
 */
public class VerlaufHistoryDialog extends AbstractServiceOptionDialog implements AKNavigationBarListener {

    private static final long serialVersionUID = -4093718959223637648L;

    private static final Logger LOGGER = Logger.getLogger(VerlaufHistoryDialog.class);
    private static final String FFM_UPDATE = "update.ffm";

    private Long auftragId = null;

    // GUI-Komponenten
    private AKJNavigationBar navBar = null;
    private AKJTextField tfVerlaufstyp = null;
    private AKJTextField tfAnlass = null;
    private AKJTextField tfStatus = null;
    private AKReferenceField rfInstall = null;
    private AKJCheckBox cbAktiv = null;
    private AKJDateComponent dcRealDate = null;
    private AKJCheckBox chbNotPossible = null;
    private AKJCheckBox cbManuell = null;
    private AKJTable tbVerlAbts = null;
    private AKReferenceAwareTableModel<VerlaufAbteilung> tbMdlVerlAbts = null;
    private VerlaufActionPanel verlaufActionPanel = null;
    private AKJTextField tfPhysikInfo = null;
    private AKJTextArea taBemerkung = null;
    private AKJTabbedPane tabPane = null;
    private VerlaufOrdersPanel verlaufOrdersPanel = null;

    // Geladene Daten
    private List<Verlauf> verlaeufe = null;
    private Map<Long, List<VerlaufAbteilung>> verlaufAbts = null;
    private Map<Long, VerlaufStatus> verlaufStatusMap = null;

    // Sonstiges
    private Verlauf actVerlauf = null;
    private boolean guiCreated = false;

    /**
     * Konstruktor mit Angabe der ID des Auftrags, fuer den die Verlaufs-History ermittelt/angezeigt werden soll.
     *
     * @param auftragId
     */
    public VerlaufHistoryDialog(Long auftragId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufHistoryDialog.xml");
        this.auftragId = auftragId;
        if (this.auftragId == null) {
            throw new IllegalArgumentException("Ungueltige Auftrags-ID. Verlaufs-Historie kann nicht angezeigt werden.");
        }

        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle("Verlaufs-Historie zu Auftrag " + auftragId);
        configureButton(CMD_SAVE, "OK", "Schliesst den Dialog", true, true);
        configureButton(CMD_CANCEL, null, null, false, false);

        AKJLabel lblVerlaufstyp = getSwingFactory().createLabel("verlaufstyp");
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblStatus = getSwingFactory().createLabel("status");
        AKJLabel lblInstall = getSwingFactory().createLabel("install.type");
        AKJLabel lblAktiv = getSwingFactory().createLabel("aktiv");
        AKJLabel lblRealDate = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblNotPossible = getSwingFactory().createLabel("not.possible");
        AKJLabel lblManuell = getSwingFactory().createLabel("manuell");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        navBar = getSwingFactory().createNavigationBar("nav.bar", this, true, true);
        tfVerlaufstyp = getSwingFactory().createTextField("verlaufstyp", false);
        tfAnlass = getSwingFactory().createTextField("anlass", false);
        tfStatus = getSwingFactory().createTextField("status", false);
        rfInstall = getSwingFactory().createReferenceField("install.type");
        rfInstall.setEnabled(false);
        cbAktiv = getSwingFactory().createCheckBox("aktiv", false);
        dcRealDate = getSwingFactory().createDateComponent("realisierungstermin", false);
        chbNotPossible = getSwingFactory().createCheckBox("not.possible", false);
        cbManuell = getSwingFactory().createCheckBox("manuell", false);
        AKJButton btnBemerkung = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnPrintDetails = getSwingFactory().createButton("print.details", getActionListener());
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        AKJButton btnUpdateFfm = getSwingFactory().createButton(FFM_UPDATE, getActionListener());
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(150, 35));
        AKJPanel btnsPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        btnsPanel.add(btnPrint          , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnsPanel.add(btnPrintDetails   , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnsPanel.add(btnUpdateFfm      , GBCFactory.createGBC(  0 , 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnsPanel.add(new AKJPanel()    , GBCFactory.createGBC(100,100, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        tbMdlVerlAbts = new AKReferenceAwareTableModel<>(
                new String[] { "Abteilung", "NL", "Real.Datum", "an", "ab",
                        "ausgetragen", "Bearbeiter", "Status", "NICHT real.", "Bemerkung" },
                new String[] { "abteilungId", "niederlassungId", "realisierungsdatum", "datumAn", "datumErledigt",
                        "ausgetragenAm", "bearbeiter", "verlaufStatusId", "notPossible", "bemerkung" },
                new Class[] { String.class, String.class, Date.class, Date.class, Date.class,
                        Date.class, String.class, String.class, Boolean.class, String.class }
        );
        tbVerlAbts = new AKJTable(tbMdlVerlAbts, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbVerlAbts.fitTable(new int[] { 170, 60, 70, 100, 70, 110, 90, 100, 70, 150 });
        TableColumnModel tbCM = tbVerlAbts.getColumnModel();
        tbCM.getColumn(3).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG));
        tbCM.getColumn(5).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME_LONG));
        AKJScrollPane spVerlAbt = new AKJScrollPane(tbVerlAbts, new Dimension(1000, 220));

        verlaufActionPanel = new VerlaufActionPanel();
        verlaufOrdersPanel = new VerlaufOrdersPanel();

        // @formatter:off
        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(navBar          , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(btnBemerkung    , GBCFactory.createGBC(  0,  0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblVerlaufstyp  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfVerlaufstyp   , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.NONE));
        top.add(lblStatus       , GBCFactory.createGBC(  0,  0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 5, 1, 1, 1, GridBagConstraints.NONE));
        top.add(tfStatus        , GBCFactory.createGBC(100,  0, 6, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblAnlass       , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfAnlass        , GBCFactory.createGBC(100,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblRealDate     , GBCFactory.createGBC(  0,  0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(dcRealDate      , GBCFactory.createGBC(100,  0, 6, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblInstall      , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(rfInstall       , GBCFactory.createGBC(100,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblNotPossible  , GBCFactory.createGBC(  0,  0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbNotPossible  , GBCFactory.createGBC(  0,  0, 6, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblManuell      , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(cbManuell       , GBCFactory.createGBC(100,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblAktiv        , GBCFactory.createGBC(  0,  0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(cbAktiv         , GBCFactory.createGBC(100,  0, 6, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblPhysikInfo   , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfPhysikInfo    , GBCFactory.createGBC(100,  0, 2, 5, 5, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblBemerkung    , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(spBemerkung     , GBCFactory.createGBC(100,  0, 2, 6, 5, 2, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,100, 0, 8, 1, 1, GridBagConstraints.VERTICAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, 7, 0, 1, 1, GridBagConstraints.NONE));

        tabPane = new AKJTabbedPane();
        tabPane.addTab("Leistungen", verlaufActionPanel);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT, BorderFactory.createEmptyBorder());
        split.setDividerLocation(0.5);
        split.setTopComponent(spVerlAbt);
        split.setBottomComponent(tabPane);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(top           , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnsPanel     , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(split         , GBCFactory.createGBC(100,100, 0, 2, 2, 1, GridBagConstraints.BOTH));
        // @formatter:on

        guiCreated = true;

        manageGUI(new AKManageableComponent[] { btnUpdateFfm });
    }

    @Override
    protected void validateSaveButton() {
    }

    /* Laedt die Verlaufs-Daten zu dem Auftrag. */
    private void loadData() {
        if (this.auftragId == null) {
            throw new IllegalArgumentException("Zur Anzeige der Verlaufs-History wird eine Auftrags-ID benoetigt.");
        }

        try {
            setWaitCursor();

            Map<Long, Abteilung> abteilungMap = new HashMap<>();
            Map<Long, Niederlassung> niederlassungMap = new HashMap<>();
            verlaufStatusMap = new HashMap<>();

            // Abteilungen laden
            NiederlassungService nls = getCCService(NiederlassungService.class);
            List<Abteilung> abts = nls.findAbteilungen();
            CollectionMapConverter.convert2Map(abts, abteilungMap, "getId", null);
            tbMdlVerlAbts.addReference(0, abteilungMap, "name");

            List<Niederlassung> niederlassungen = nls.findNiederlassungen();
            CollectionMapConverter.convert2Map(niederlassungen, niederlassungMap, "getId", null);
            tbMdlVerlAbts.addReference(1, niederlassungMap, "name");

            // Verlauf-Stati laden
            BAService bas = getCCService(BAService.class);
            List<VerlaufStatus> stati = bas.findVerlaufStati();
            CollectionMapConverter.convert2Map(stati, verlaufStatusMap, "getId", null);
            tbMdlVerlAbts.addReference(7, verlaufStatusMap, "status");

            // Installationstypen laden
            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> refs = rs.findReferencesByType(Reference.REF_TYPE_INSTALLATION_TYPE, true);
            rfInstall.setReferenceList(refs);
            rfInstall.setFindService(getCCService(QueryCCService.class));

            verlaeufe = bas.findVerlaeufe4Auftrag(auftragId);
            if ((verlaeufe != null) && (!verlaeufe.isEmpty())) {
                verlaufAbts = new HashMap<>();
                for (Verlauf v : verlaeufe) {
                    List<VerlaufAbteilung> vabts = bas.findVerlaufAbteilungen(v.getId());
                    verlaufAbts.put(v.getId(), vabts);
                }
                navBar.setData(verlaeufe);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }

        if ((verlaeufe == null) || (verlaeufe.isEmpty())) {
            throw new IllegalArgumentException("Zu dem Auftrag (" + auftragId + ") wurden keine Verläufe gefunden!");
        }
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (guiCreated) {
            if (obj instanceof Verlauf) {
                try {
                    setWaitCursor();
                    actVerlauf = (Verlauf) obj;

                    String BAUAUFTRAG = "Bauauftrag";
                    String PROJEKTIERUNG = "Projektierung";
                    tfVerlaufstyp.setText(
                            ((actVerlauf.getProjektierung() != null) && actVerlauf.getProjektierung())
                                    ? PROJEKTIERUNG : BAUAUFTRAG
                    );
                    cbManuell.setSelected(actVerlauf.getManuellVerteilt());
                    cbAktiv.setSelected(actVerlauf.getAkt());
                    VerlaufStatus status = verlaufStatusMap.get(actVerlauf.getVerlaufStatusId());
                    tfStatus.setText((status != null) ? status.getStatus() : null);
                    dcRealDate.setDate(actVerlauf.getRealisierungstermin());
                    rfInstall.setReferenceId(actVerlauf.getInstallationRefId());
                    chbNotPossible.setSelected(actVerlauf.getNotPossible());
                    tfPhysikInfo.setText(getPhysikInfo());
                    taBemerkung.setText(actVerlauf.getBemerkung());

                    try {
                        BAConfigService bas = getCCService(BAConfigService.class);
                        BAVerlaufAnlass baAnlass = bas.findBAVerlaufAnlass(actVerlauf.getAnlass());
                        tfAnlass.setText((baAnlass != null) ? baAnlass.getName() : null);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    List<VerlaufAbteilung> vas = verlaufAbts.get(actVerlauf.getId());
                    tbMdlVerlAbts.setData(vas);
                    tbVerlAbts.repaint();

                    // Leistungs-Differenzen laden
                    CCLeistungsService ls = getCCService(CCLeistungsService.class);
                    verlaufActionPanel.setVerlaufActions(actVerlauf.getId(),
                            ls.findAuftrag2TechLeistungen4Verlauf(actVerlauf.getId()));
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }
            else {
                actVerlauf = null;
                tbMdlVerlAbts.setData(null);
                verlaufActionPanel.setVerlaufActions(null, null);
            }
            updateAuftragsPanel();
        }
    }

    /**
     * Gibt eine Information zur verwendeten Physik zurueck.
     */
    private String getPhysikInfo() {
        if (actVerlauf != null) {
            try {
                BAService bas = getCCService(BAService.class);
                return bas.getPhysikInfo4Verlauf(actVerlauf);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    protected void execute(String command) {
        if ("bemerkungen".equals(command)) {
            if (actVerlauf != null) {
                try {
                    VerlaufsBemerkungenDialog dlg = new VerlaufsBemerkungenDialog(actVerlauf.getId());
                    DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                }
                catch (IllegalArgumentException e) {
                    MessageHelper.showInfoDialog(getMainFrame(), e.getMessage(), null, true);
                }
            }
        }
        else if ("print".equals(command)) {
            printVerlauf(false);
        }
        else if ("print.details".equals(command)) {
            printVerlauf(true);
        }
        else if (FFM_UPDATE.equals(command)) {
            updateFfm();
        }
    }

    /**
     * Druckt den aktuell angezeigten Verlauf aus.
     */
    private void printVerlauf(boolean withDetails) {
        try {
            PrintVerlaufHelper helper = new PrintVerlaufHelper();
            boolean projektierung = (actVerlauf.getProjektierung() != null)
                    ? actVerlauf.getProjektierung() : false;

            helper.printVerlauf(actVerlauf.getId(), projektierung, withDetails, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            SwingHelper.moveToScreenFront(getMainFrame());
            this.requestFocus();
        }
    }

    @Override
    protected void doSave() {
        prepare4Close();
        setValue(OK_OPTION);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private void updateAuftragsPanel() {
        if (!CollectionTools.isEmpty(actVerlauf.getSubAuftragsIds())) {
            tabPane.add("Aufträge", verlaufOrdersPanel);
            verlaufOrdersPanel.setVerlaufId(actVerlauf.getId());
        }
        else {
            tabPane.remove(verlaufOrdersPanel);
        }
    }

    private void updateFfm() {
        if (actVerlauf != null) {
            try {
                getCCService(FFMService.class).updateAndSendOrder(actVerlauf);
                MessageHelper.showInfoDialog(this, "Es wurde ein Update an FFM übermittelt.");
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }
}
