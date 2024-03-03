/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2005 09:10:08
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.utils.GuiInfo;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungStatus;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog zur Anzeige aller Bemerkungen zu einem best. Verlauf.
 *
 *
 */
public class VerlaufsBemerkungenPanel extends AbstractServicePanel implements SavePanel {
    private static final Logger LOGGER = Logger.getLogger(VerlaufsBemerkungenPanel.class);

    private static final String ABTEILUNG = "abteilung";
    private static final String NIEDERLASSUNG = "niederlassung";
    private static final String BEMERKUNG = "bemerkung";
    private static final String BEMERKUNG_PREFIX = "verlauf.bemerkung.";
    private static final String STATUS = "status";
    private static final String BEARBEITER = "bearbeiter";
    private static final String DATUM_AN = "datum.an";
    private static final String ERLEDIGT_AM = "erledigt.am";
    private static final String NOT_POSSIBLE = "not.possible";
    private static final String NOT_POSSIBLE_REASON = "not.possible.reason";
    private static final String SAVE = "save";
    private static final String ABTEILUNG_STATUS = "abteilung.status";
    private static final String ABTEILUNG_STATUS_PREFIX = "verlauf.abteilung.status.";
    private static final String WIEDERVORLAGE = "wiedervorlage";
    private static final String WIEDERVORLAGE_PREFIX = "verlauf.wiedervorlage.";
    private static final long serialVersionUID = -2062815499661569559L;

    private final boolean showSaveButton;
    private final AbstractVerlaufPanel parentPanel;
    private final Map<Pair<Long, Long>, EditableFields> fieldsMap = new HashMap<>();

    private Long verlaufId = null;
    private List<VerlaufAbteilung> verlaufAbts = null;

    private Map<Long, String> abteilungen = null;
    private Map<Long, String> verlaufStati = null;
    private Map<Long, String> niederlassungen;
    private Map<Long, Map<Long, VerlaufAbteilungStatus>> verlaufAbteilungStati;
    private List<Reference> notPossibleReasons = null;


    private AKJPanel infoPanel;
    private AKJButton btnSave;

    /**
     * Konstruktor mit Angabe des Parent Panels, welches die BA-Tabelle enthaelt
     */
    public VerlaufsBemerkungenPanel(AbstractVerlaufPanel parentPanel, boolean showSaveButton) throws IllegalArgumentException {
        super("de/augustakom/hurrican/gui/verlauf/resources/VerlaufsBemerkungenPanel.xml");
        this.parentPanel = parentPanel;
        this.showSaveButton = showSaveButton;
        loadDefaultData();
        createGUI();
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJPanel hLine = new AKJPanel();
        hLine.setOpaque(false);
        hLine.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        hLine.setPreferredSize(new Dimension(50, 1));

        btnSave = getSwingFactory().createButton(SAVE, getActionListener());

        infoPanel = new AKJPanel(new GridBagLayout());

        AKJPanel childPanel = new AKJPanel(new GridBagLayout());
        if (showSaveButton) {
            childPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            childPanel.add(hLine, GBCFactory.createGBC(100, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        }
        childPanel.add(infoPanel, GBCFactory.createGBC(100, 100, 0, 2, 2, 1, GridBagConstraints.BOTH));

        setLayout(new BorderLayout());
        add(new AKJScrollPane(childPanel), BorderLayout.CENTER);

        setMaximumSize(GuiInfo.getScreenSize());
    }


    /**
     * Zeigt die Daten an
     */
    private void createVerlaufGui() {
        infoPanel.removeAll();
        btnSave.setEnabled(!verlaufAbts.isEmpty());
        int y = 0;
        fieldsMap.clear();
        for (VerlaufAbteilung va : verlaufAbts) {
            AKJTextField tfAbt = getSwingFactory().createTextField(ABTEILUNG, false);
            tfAbt.setText(((abteilungen != null))
                    ? abteilungen.get(va.getAbteilungId()) : null);
            AKJTextField tfNl = getSwingFactory().createTextField(NIEDERLASSUNG, false);
            tfNl.setText(((niederlassungen != null))
                    ? niederlassungen.get(va.getNiederlassungId()) : null);

            AKJTextArea taBem = getSwingFactory().createTextArea(BEMERKUNG);
            taBem.setName(BEMERKUNG_PREFIX + va.getAbteilungId());
            taBem.getAccessibleContext().setAccessibleName(BEMERKUNG_PREFIX + va.getAbteilungId());
            taBem.setLineWrap(true);
            taBem.setText(va.getBemerkung());
            AKJScrollPane spBemerkung = new AKJScrollPane(taBem);
            spBemerkung.setPreferredSize(new Dimension(350, 60));

            AKJLabel lblStatus = getSwingFactory().createLabel(STATUS);
            AKJTextField tfStatus = getSwingFactory().createTextField(STATUS, false);
            tfStatus.setText(((verlaufStati != null))
                    ? verlaufStati.get(va.getVerlaufStatusId()) : null);

            AKJLabel lblBearbeiter = getSwingFactory().createLabel(BEARBEITER);
            AKJTextField tfBearbeiter = getSwingFactory().createTextField(BEARBEITER, false);
            tfBearbeiter.setText(va.getBearbeiter());

            AKJLabel lblDatumAn = getSwingFactory().createLabel(DATUM_AN);
            AKJDateComponent dcDatumAn = getSwingFactory().createDateComponent(DATUM_AN, false);
            dcDatumAn.setDate(va.getDatumAn());

            AKJLabel lblErledigt = getSwingFactory().createLabel(ERLEDIGT_AM);
            AKJDateComponent dcErledigt = getSwingFactory().createDateComponent(ERLEDIGT_AM, false);
            dcErledigt.setDate(va.getDatumErledigt());

            AKJLabel lblNotPossible = getSwingFactory().createLabel(NOT_POSSIBLE);
            AKJCheckBox chbNotPossible = getSwingFactory().createCheckBox(NOT_POSSIBLE, false);
            chbNotPossible.setSelected(va.getNotPossible());

            AKJLabel lblNotPossReason = getSwingFactory().createLabel(NOT_POSSIBLE_REASON);
            AKReferenceField rfNotPossReason = getSwingFactory().createReferenceField(NOT_POSSIBLE_REASON);
            rfNotPossReason.setEnabled(false);
            rfNotPossReason.setReferenceList(notPossibleReasons);
            rfNotPossReason.setReferenceId(va.getNotPossibleReasonRefId());

            AKJLabel lblWiedervorlage = getSwingFactory().createLabel(WIEDERVORLAGE);
            AKJDateComponent dcWiedervorlage = getSwingFactory().createDateComponent(WIEDERVORLAGE, true);
            dcWiedervorlage.setName(WIEDERVORLAGE_PREFIX + va.getAbteilungId());
            dcWiedervorlage.getAccessibleContext().setAccessibleName(WIEDERVORLAGE_PREFIX + va.getAbteilungId());
            dcWiedervorlage.setDate(va.getWiedervorlage());

            AKJLabel lblAbteilungStatus = getSwingFactory().createLabel(ABTEILUNG_STATUS);
            AKJComboBox cbAbteilungStatus = getSwingFactory().createComboBox(ABTEILUNG_STATUS);
            cbAbteilungStatus.setName(ABTEILUNG_STATUS_PREFIX + va.getAbteilungId());
            cbAbteilungStatus.getAccessibleContext().setAccessibleName(ABTEILUNG_STATUS_PREFIX + va.getAbteilungId());
            cbAbteilungStatus.setRenderer(new AKCustomListCellRenderer<>(VerlaufAbteilungStatus.class, VerlaufAbteilungStatus::getStatus));
            Map<Long, VerlaufAbteilungStatus> vaMap = verlaufAbteilungStati.get(va.getAbteilungId());
            if (vaMap != null) {
                cbAbteilungStatus.addItems(vaMap.values(), true, VerlaufAbteilungStatus.class);
            }
            cbAbteilungStatus.selectItem("getId", VerlaufAbteilungStatus.class, va.getVerlaufAbteilungStatusId());

            fieldsMap.put(new Pair<>(va.getAbteilungId(), va.getNiederlassungId()), new EditableFields(taBem, dcWiedervorlage, cbAbteilungStatus));

            y++;
            infoPanel.add(tfAbt, GBCFactory.createGBC(0, 0, 0, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(tfNl, GBCFactory.createGBC(0, 0, 0, y + 1, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, y + 2, 1, 1, GridBagConstraints.VERTICAL));
            infoPanel.add(spBemerkung, GBCFactory.createGBC(0, 0, 2, y, 1, 3, GridBagConstraints.BOTH));
            infoPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, y, 1, 1, GridBagConstraints.NONE));
            infoPanel.add(lblDatumAn, GBCFactory.createGBC(0, 0, 4, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(dcDatumAn, GBCFactory.createGBC(0, 0, 5, y, 1, 1, GridBagConstraints.NONE));
            infoPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, y, 1, 1, GridBagConstraints.NONE));
            infoPanel.add(lblStatus, GBCFactory.createGBC(0, 0, 7, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(tfStatus, GBCFactory.createGBC(0, 0, 8, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 9, y, 1, 1, GridBagConstraints.NONE));
            infoPanel.add(lblWiedervorlage, GBCFactory.createGBC(0, 0, 10, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(dcWiedervorlage, GBCFactory.createGBC(0, 0, 11, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(lblErledigt, GBCFactory.createGBC(0, 0, 4, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(dcErledigt, GBCFactory.createGBC(0, 0, 5, y, 1, 1, GridBagConstraints.NONE));
            infoPanel.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 7, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(tfBearbeiter, GBCFactory.createGBC(0, 0, 8, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(lblAbteilungStatus, GBCFactory.createGBC(0, 0, 10, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(cbAbteilungStatus, GBCFactory.createGBC(0, 0, 11, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(lblNotPossible, GBCFactory.createGBC(0, 0, 4, ++y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(chbNotPossible, GBCFactory.createGBC(0, 0, 5, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(lblNotPossReason, GBCFactory.createGBC(0, 0, 7, y, 1, 1, GridBagConstraints.HORIZONTAL));
            infoPanel.add(rfNotPossReason, GBCFactory.createGBC(0, 0, 8, y, 1, 1, GridBagConstraints.HORIZONTAL));

            manageGUI(this.getClass().getName(), taBem, dcWiedervorlage, cbAbteilungStatus);
        }
        infoPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 12, ++y, 1, 1, GridBagConstraints.BOTH));
        infoPanel.revalidate();
    }


    /**
     * Laedt Daten, die fuer alle Verlaeufe gleich sind.
     */
    private void loadDefaultData() {
        try {
            getMainFrame().setWaitCursor();

            // Abteilungen laden und in Map speichern
            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Abteilung> abts = ns.findAbteilungen();
            abteilungen = new HashMap<>();
            CollectionMapConverter.convert2Map(abts, abteilungen, "getId", "getName");

            // Niederlassungen laden und in Map speichern
            List<Niederlassung> nls = ns.findNiederlassungen();
            niederlassungen = new HashMap<>();
            CollectionMapConverter.convert2Map(nls, niederlassungen, "getId", "getName");

            // Verlauf-Stati laden und in Map speichern
            BAService bas = getCCService(BAService.class);
            List<VerlaufStatus> stati = bas.findVerlaufStati();
            verlaufStati = new HashMap<>();
            CollectionMapConverter.convert2Map(stati, verlaufStati, "getId", "getStatus");

            List<VerlaufAbteilungStatus> vaStati = bas.findVerlaufAbteilungStati();
            verlaufAbteilungStati = new HashMap<>();
            for (VerlaufAbteilungStatus verlaufAbteilungStatus : vaStati) {
                Map<Long, VerlaufAbteilungStatus> vaStatiMap = verlaufAbteilungStati.get(verlaufAbteilungStatus.getAbteilungId());
                if (vaStatiMap == null) {
                    vaStatiMap = new HashMap<>();
                    verlaufAbteilungStati.put(verlaufAbteilungStatus.getAbteilungId(), vaStatiMap);
                }
                vaStatiMap.put(verlaufAbteilungStatus.getId(), verlaufAbteilungStatus);
            }

            ReferenceService rs = getCCService(ReferenceService.class);
            notPossibleReasons = rs.findReferencesByType(Reference.REF_TYPE_VERLAUF_REASONS, Boolean.TRUE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            getMainFrame().setDefaultCursor();
        }
    }


    /**
     * Laedt die Daten.
     */
    public void setVerlaufId(Long verlaufId) throws IllegalArgumentException {
        this.verlaufId = verlaufId;
        if (verlaufId == null) {
            verlaufAbts = new ArrayList<>();
            createVerlaufGui();
        }
        else {
            try {
                getMainFrame().setWaitCursor();
                BAService bas = getCCService(BAService.class);
                verlaufAbts = bas.findVerlaufAbteilungen(verlaufId);
                createVerlaufGui();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                getMainFrame().setDefaultCursor();
            }

            if ((verlaufAbts == null) || (verlaufAbts.isEmpty())) {
                throw new IllegalArgumentException("Bemerkungen zu dem Verlauf " + verlaufId + " konnten nicht ermittelt werden!");
            }
        }
    }


    /**
     * @see SavePanel#doSave()
     */
    @Override
    public void doSave() throws GUIException {
        try {
            BAService bas = getCCService(BAService.class);

            boolean dataChanged = false;
            Iterator<Pair<Long, Long>> keyIt = fieldsMap.keySet().iterator();
            while (keyIt.hasNext()) {
                Pair<Long, Long> abtAndNl = keyIt.next();
                Long abtId = abtAndNl.getFirst();
                Long niederlassungId = abtAndNl.getSecond();
                EditableFields fields = fieldsMap.get(abtAndNl);
                if (fields.taBemerkung.isEditable() || fields.dcWiedervorlage.isEnabled()) {
                    VerlaufAbteilung va = bas.findVerlaufAbteilung(verlaufId, abtId, niederlassungId);
                    if (va != null) {
                        String bemerkungNeu = appendUserAndDateIfChanged(va.getBemerkung(), fields.taBemerkung.getText());
                        va.setBemerkung(bemerkungNeu);

                        va.setWiedervorlage(fields.dcWiedervorlage.getDate(null));
                        VerlaufAbteilungStatus status = (VerlaufAbteilungStatus) fields.cbAbteilungStatus.getSelectedItem();
                        va.setVerlaufAbteilungStatusId(status != null ? status.getId() : null);
                        bas.saveVerlaufAbteilung(va);

                        // Change in table data, too (if necessary)
                        if ((parentPanel != null) && parentPanel.getAbteilungId().equals(va.getAbteilungId())) {
                            AKMutableTableModel tableModel = (AKMutableTableModel) parentPanel.getTable().getModel();
                            Collection<?> data = tableModel.getData();
                            for (Object object : data) {
                                if (object instanceof AbstractBauauftragView) {
                                    AbstractBauauftragView view = (AbstractBauauftragView) object;
                                    if (va.getId().equals(view.getVerlaufAbtId())) {
                                        view.setWiedervorlage(fields.dcWiedervorlage.getDate(null));
                                        view.setVerlaufAbteilungStatus(status != null ? status.getStatus() : null);
                                        view.setVerlaufAbteilungStatusId(status != null ? status.getId() : null);
                                        dataChanged = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (dataChanged) {
                parentPanel.getTable().repaint();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GUIException(GUIException.USER_SAVING_ERROR, e);
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (SAVE.equals(command)) {
            try {
                doSave();
            }
            catch (GUIException e) {
                LOGGER.error("execute() - Error while trying to save data", e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    private static class EditableFields {
        final AKJTextArea taBemerkung;
        final AKJDateComponent dcWiedervorlage;
        final AKJComboBox cbAbteilungStatus;

        public EditableFields(AKJTextArea textArea, AKJDateComponent dateComponent, AKJComboBox cbAbteilungStatus) {
            this.taBemerkung = textArea;
            this.dcWiedervorlage = dateComponent;
            this.cbAbteilungStatus = cbAbteilungStatus;
        }
    }
}


