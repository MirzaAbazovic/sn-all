/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2004 13:47:48
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.lang.math.LongRange;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel;
import de.augustakom.hurrican.gui.base.IAuftragStatusValidator;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Panel fuer die VPN-Konfiguration eines Auftrags.
 *
 *
 */
public class VPNKonfigPanel extends AbstractAuftragPanel implements AKModelOwner, IAuftragStatusValidator {

    private static final Logger LOGGER = Logger.getLogger(VPNKonfigPanel.class);

    private static final String VPN_KONF_KANALBUENDELUNG = "vpn.konf.kanalbuendelung";
    private static final String VPN_KONF_VBZ = "vpn.konf.vbz";
    private static final String VPN_KONF_VBZ_REMOVE = "vpn.konf.vbz.remove";
    private static final String PRINT_VPN_ACCOUNT = "print.vpn.account";
    private static final String ASSIGN_VPN = "assign.vpn";

    // GUI-Komponenten
    private AKJPanel labelPanel = null;
    private AKJCheckBox chbKanal = null;
    private AKJComboBox cbKanaele = null;
    private AKJCheckBox chbDialOut = null;
    private AKJTextField tfVplsId = null;
    private AKJTextField tfVbz = null;
    private AKJButton btnVbzAdd = null;
    private AKJButton btnVbzRemove = null;
    private AKJTextField tfVpnName = null;
    private AKReferenceField rfVpnType = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJTextField tfProjektleiter = null;
    private AKJTextField tfEinwahl = null;
    private AKJTextArea taBemerkung = null;

    // Modelle
    private CCAuftragModel model = null;
    private VPN vpn = null;
    private VPNKonfiguration vpnKonf = null;
    private VerbindungsBezeichnung verbindungsBezeichnung = null;

    // Services
    private VPNService vpnService;
    private QueryCCService queryService;
    private CCAuftragService auftragService;
    private PhysikService physikService;

    // Sonstiges
    private int lastIndexKanaele = -1;
    private boolean guiCreated = false;
    private static final String WATCH_VPN_KONF = "watch.vpn.konf";

    /**
     * Konstruktor.
     */
    public VPNKonfigPanel() {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNKonfigPanel.xml");
        init();
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblLock = getSwingFactory().createLabel("lock");
        lblLock.setFontStyle(Font.BOLD);

        // VPN-Konfiguration
        AKJLabel lblKanal = getSwingFactory().createLabel(VPN_KONF_KANALBUENDELUNG);
        AKJLabel lblKanaele = getSwingFactory().createLabel("vpn.konf.kanaele");
        AKJLabel lblDialOut = getSwingFactory().createLabel("vpn.konf.dialout");
        AKJLabel lblVplsId = getSwingFactory().createLabel("vpn.konf.vpls.id");
        AKJLabel lblVbz = getSwingFactory().createLabel(VPN_KONF_VBZ);
        chbKanal = getSwingFactory().createCheckBox(VPN_KONF_KANALBUENDELUNG);
        chbKanal.addActionListener(getActionListener());
        cbKanaele = getSwingFactory().createComboBox("vpn.konf.kanaele");
        cbKanaele.setPreferredSize(new Dimension(80, 22));
        chbDialOut = getSwingFactory().createCheckBox("vpn.konf.dialout");
        tfVplsId = getSwingFactory().createTextField("vpn.konf.vpls.id");
        tfVbz = getSwingFactory().createTextField(VPN_KONF_VBZ, false);
        btnVbzAdd = getSwingFactory().createButton(VPN_KONF_VBZ, getActionListener());
        btnVbzAdd.setBorder(null);
        btnVbzRemove = getSwingFactory().createButton(VPN_KONF_VBZ_REMOVE, getActionListener());
        btnVbzRemove.setBorder(null);
        AKJButton btnPrint = getSwingFactory().createButton(PRINT_VPN_ACCOUNT, getActionListener());
        AKJButton btnAssignVPN = getSwingFactory().createButton(ASSIGN_VPN, getActionListener());

        AKJPanel konfPanel = new AKJPanel(new GridBagLayout());
        konfPanel.setBorder(BorderFactory.createTitledBorder("VPN-Konfiguration"));
        konfPanel.add(lblKanal, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        konfPanel.add(chbKanal, GBCFactory.createGBC(10, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        konfPanel.add(lblKanaele, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(cbKanaele, GBCFactory.createGBC(10, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(lblDialOut, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(chbDialOut, GBCFactory.createGBC(10, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2)));
        konfPanel.add(lblVplsId, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(tfVplsId, GBCFactory.createGBC(10, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(tfVbz, GBCFactory.createGBC(10, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(btnVbzAdd, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.NONE));
        konfPanel.add(btnVbzRemove, GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.NONE));
        konfPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        konfPanel.add(btnPrint, GBCFactory.createGBC(0, 0, 6, 4, 1, 1, GridBagConstraints.NONE));

        // VPN-Vertrag
        AKJLabel lblVpnName = getSwingFactory().createLabel("vpn.name");
        AKJLabel lblVpnType = getSwingFactory().createLabel("vpn.type");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblProjektleiter = getSwingFactory().createLabel("vpn.projektleiter");
        AKJLabel lblEinwahl = getSwingFactory().createLabel("vpn.einwahl");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("vpn.bemerkung");
        tfVpnName = getSwingFactory().createTextField("vpn.name", false);
        rfVpnType = getSwingFactory().createReferenceField(
                "vpn.type", Reference.class, "id", "strValue", new Reference(Reference.REF_TYPE_VPNTYPE));
        rfVpnType.setEnabled(false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        tfProjektleiter = getSwingFactory().createTextField("vpn.projektleiter", false);
        tfEinwahl = getSwingFactory().createTextField("vpn.einwahl", false);
        taBemerkung = getSwingFactory().createTextArea("vpn.bemerkung", false);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        spBemerkung.setPreferredSize(new Dimension(165, 70));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(35, 10));
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(btnAssignVPN, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVpnName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(fill, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfVpnName, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVpnType, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfVpnType, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProjektleiter, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProjektleiter, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblEinwahl, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfEinwahl, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(spBemerkung, GBCFactory.createGBC(100, 100, 2, 0, 1, 2, GridBagConstraints.BOTH));

        AKJPanel vpnPanel = new AKJPanel(new GridBagLayout());
        vpnPanel.setBorder(BorderFactory.createTitledBorder("VPN-Details"));
        vpnPanel.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        vpnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        vpnPanel.add(right, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.VERTICAL));

        labelPanel = new AKJPanel(new GridBagLayout());
        labelPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        labelPanel.add(lblLock, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(labelPanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(konfPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(vpnPanel, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 3, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnVbzAdd, btnVbzRemove, btnPrint, btnAssignVPN);

        labelPanel.setVisible(false);
        validateKanaele();
        guiCreated = true;
    }

    private void init() {
        try {
            vpnService = getCCService(VPNService.class);
            queryService = getCCService(QueryCCService.class);
            auftragService = getCCService(CCAuftragService.class);
            physikService = getCCService(PhysikService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        if (model instanceof CCAuftragModel) {
            this.model = (CCAuftragModel) model;
        }
        else {
            this.model = null;
        }

        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        vpn = null;
        vpnKonf = null;
        clear();
        if (this.model != null) {
            AuftragDaten auftragDaten = null;
            try {
                setWaitCursor();

                rfVpnType.setFindService(queryService);

                vpn = vpnService.findVPNByAuftragId(model.getAuftragId());
                vpnKonf = vpnService.findVPNKonfiguration4Auftrag(model.getAuftragId());

                if (vpnKonf != null) {
                    verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(vpnKonf.getPhysAuftragId());
                }
                else {
                    vpnKonf = new VPNKonfiguration();
                    vpnKonf.setAuftragId(model.getAuftragId());
                    vpnKonf.setKanalbuendelung(Boolean.FALSE);
                    vpnKonf.setDialOut(Boolean.FALSE);
                }

                // AuftragDaten fuer Status-Validierung laden
                auftragDaten = auftragService.findAuftragDatenByAuftragId(model.getAuftragId());

                addObjectToWatch(WATCH_VPN_KONF, vpnKonf);
                showValues();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                Long tmpStatusId = (auftragDaten != null) ? auftragDaten.getStatusId() : null;
                validate4Status(tmpStatusId);
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        if (hasModelChanged()) {
            try {
                VPNService vpnService = getCCService(VPNService.class);
                this.vpnKonf = vpnService.saveVPNKonfiguration(vpnKonf, true);
                addObjectToWatch(WATCH_VPN_KONF, vpnKonf);
                showValues();
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

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        if (guiCreated) {
            setValues();
            if (hasChanged(WATCH_VPN_KONF, vpnKonf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (VPN_KONF_KANALBUENDELUNG.equals(command)) {
            validateKanaele();
        }
        else if (VPN_KONF_VBZ.equals(command)) {
            selectVbz4VPN();
        }
        else if (VPN_KONF_VBZ_REMOVE.equals(command)) {
            removeVbzFromVPN();
        }
        else if (PRINT_VPN_ACCOUNT.equals(command)) {
            printAccount4VPN();
        }
        else if (ASSIGN_VPN.equals(command)) {
            assignVPN();
        }
    }

    /* Sucht nach Auftraegen, die als phys. Leitung fuer den VPN verwendet werden koennen
     * und ordnet den gewaehlten Auftrag dem VPN zu.
     */
    private void selectVbz4VPN() {
        try {
            VbzAuswahl4VPNDialog dlg = new VbzAuswahl4VPNDialog(((KundenModel) model).getKundeNo());
            Object selection = DialogHelper.showDialog(this, dlg, true, true);
            if (selection instanceof CCAuftragProduktVbzView) {
                CCAuftragProduktVbzView view = (CCAuftragProduktVbzView) selection;
                vpnKonf.setPhysAuftragId(view.getAuftragId());
                tfVbz.setText(view.getVbz());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this,
                    new Exception("Aufträge konnten nicht ermittelt werden, weil die Kundennummer nicht verfügbar ist.", e));
        }
    }

    /* Entfernt die Zuordnung zw. dem VPN-Auftrag und dem phys. Auftag. */
    private void removeVbzFromVPN() {
        if ((vpnKonf != null) && (vpnKonf.getPhysAuftragId() != null)) {
            String title = "Zuordnung aufheben?";
            String msg = "Soll die Zuordnung zu dem physikalischen Auftrag\nwirklich aufgehoben werden?";
            int option = MessageHelper.showConfirmDialog(this, msg, title,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                vpnKonf.setPhysAuftragId(null);
                tfVbz.setText("");
            }
        }
    }

    /* Validiert die ComboBox fuer die Kanalanzahl. */
    private void validateKanaele() {
        if (chbKanal.isSelected()) {
            cbKanaele.setEnabled(true);
            if (lastIndexKanaele >= 0) {
                cbKanaele.setSelectedIndex(lastIndexKanaele);
            }
        }
        else {
            lastIndexKanaele = cbKanaele.getSelectedIndex();
            cbKanaele.setSelectedIndex(0);
            cbKanaele.setEnabled(false);
        }
    }

    /* Druckt die Account-Daten des Auftrags. */
    private void printAccount4VPN() {
        try {
            JasperPrint jp = vpnService.reportVPNAccount(model.getAuftragId());
            JasperPrintManager.printReport(jp, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Oeffnet einen Dialog zur VPN Auswahl und ordnet das gewaehlte VPN dem Auftrag zu */
    private void assignVPN() {
        try {
            VPNSearchDialog searchDlg = new VPNSearchDialog();
            Object result = DialogHelper.showDialog(getMainFrame(), searchDlg, true, true);
            if (result instanceof VPNVertragView) {
                VPNVertragView vpn = (VPNVertragView) result;
                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll der Auftrag wirklich dem VPN " + vpn.getVpnNr() + " zugeordnet werden?", "VPN zuordnen?");
                if (option == JOptionPane.YES_OPTION) {
                    AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(model.getAuftragId());
                    vpnService.addAuftrag2VPN(vpn.getId(), auftragTechnik);

                    refreshAuftragFrame();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* 'Loescht' alle Felder. */
    private void clear() {
        GuiTools.cleanFields(this);
    }

    /* Zeigt die Daten des Objekts <code>vpnKonf</code> an. */
    private void showValues() {
        if (vpn == null) {
            labelPanel.setVisible(true);
            enableFields(false);
        }
        else {
            labelPanel.setVisible(false);
            enableFields(true);
            tfVpnName.setText(vpn.getVpnName());
            rfVpnType.setReferenceId(vpn.getVpnType());
            tfVpnNr.setValue(vpn.getVpnNr());
            tfProjektleiter.setText(vpn.getProjektleiter());
            tfEinwahl.setText(vpn.getEinwahl());
            taBemerkung.setText(vpn.getBemerkung());
        }

        if (vpnKonf != null) {
            chbKanal.setSelected(vpnKonf.hasKanalbuendelung());
            validateKanaele();
            cbKanaele.selectItem("toString", String.class, ((vpnKonf.getAnzahlKanaele() != null)
                    ? vpnKonf.getAnzahlKanaele().toString() : ""));
            chbDialOut.setSelected(vpnKonf.getDialOut());
            tfVplsId.setText(vpnKonf.getVplsId());
        }

        if (verbindungsBezeichnung != null) {
            tfVbz.setText(verbindungsBezeichnung.getVbz());
        }
    }

    /* Uebergibt die angezeigten Werte dem VPNKonfigurations-Objekt */
    private void setValues() {
        if (vpnKonf != null) {
            vpnKonf.setKanalbuendelung(chbKanal.isSelected());
            vpnKonf.setAnzahlKanaele((cbKanaele.getSelectedIndex() == 0)
                    ? null : Short.valueOf(cbKanaele.getSelectedItem().toString()));
            vpnKonf.setDialOut(chbDialOut.isSelected());
            vpnKonf.setVplsId(tfVplsId.getText(null));
            // phys. Auftrags-ID wird ueber Methode 'selectVbz4VPN' gesetzt
        }
    }

    /* Setzt die Fields auf enabled/disabled */
    private void enableFields(boolean enable) {
        chbKanal.setEnabled(enable);
        cbKanaele.setEnabled(enable);
        chbDialOut.setEnabled(enable);
        tfVplsId.setEnabled(enable);
        btnVbzAdd.setEnabled(enable);
        btnVbzRemove.setEnabled(enable);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.IAuftragStatusValidator#validate4Status(java.lang.Integer)
     */
    @Override
    public void validate4Status(Long auftragStatus) {
        if (vpn != null) {
            LongRange range = new LongRange(AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG,
                    AuftragStatus.ANSCHREIBEN_KUNDE_KUEND);
            if (NumberTools.isIn(auftragStatus, new Number[] { AuftragStatus.UNDEFINIERT, AuftragStatus.ERFASSUNG,
                    AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, AuftragStatus.ERFASSUNG_SCV, AuftragStatus.PROJEKTIERUNG,
                    AuftragStatus.PROJEKTIERUNG_ERLEDIGT }) ||
                    range.containsLong(auftragStatus)) {
                enableFields(true);
            }
            else {
                enableFields(false);
            }
        }
    }
}


