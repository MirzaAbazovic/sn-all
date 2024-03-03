/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 13:25:47
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.math.NumberRange;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.IconTableHeaderRenderer;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.AssignEquipmentDialog;
import de.augustakom.hurrican.gui.shared.Equipment4RangierungTableModel;
import de.augustakom.hurrican.gui.shared.PortsDialog;
import de.augustakom.hurrican.gui.tools.physik.BreakRangierungDialog;
import de.augustakom.hurrican.gui.tools.physik.PortChangeDialog;
import de.augustakom.hurrican.gui.tools.tal.AssignEquipmentHelper;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.SdslEquipmentService;

/**
 * Panel fuer die Darstellung der Rangierungen fuer eine Endstelle.
 *
 *
 */
public class RangierungPanel extends AbstractAuftragPanel implements IServiceCallback {

    private static final long serialVersionUID = -3790992613684398447L;
    private static final Logger LOGGER = Logger.getLogger(RangierungPanel.class);
    private static final String BREAK_RANGIERUNG = "break.rangierung";
    private static final String PORT_CHANGE = "port.change";
    private static final String DOPPELTE_BELEGUNG = "doppelte.belegung";
    private static final String RANGIERUNGS_HISTORY = "rangierungs.history";
    private static final String CREATE_RANGIERUNG = "create.rangierung";
    private static final String CROSS_CONNECTIONS_ZUORDNUNG = "cross.connections.zuordnung";
    private static final String VLANS_ZUORDNUNG = "vlans.zuordnung";
    private static final String NDRAHT_SDSL_ZUORDNUNG = "ndraht.sdsl.zuordnung";
    private static final String PHYSIK_DETAILED = "physik.detailed";
    private static final String PHYSIK_MANUELL = "physik.manuell";
    private static final String PHYSIK_UEBERNEHMEN = "physik.uebernehmen";
    private static final String PHYSIK_ZUORDNEN = "physik.zuordnen";
    private static final String RANGIERUNG_ENTFERNEN = "rangierung.entfernen";
    private Endstelle endstelle = null;
    private Rangierung rangierung = null;
    private Rangierung rangierungAdd = null;
    private AuftragEndstellenPanel esPanel = null;
    private Equipment4RangierungTableModel tbMdlEquipment = null;
    private AKJButton btnPhysikZuordnen = null;
    private AKJButton btnPhysikDetailed = null;
    private AKJButton btnPhysikUebernahme = null;
    private AKJButton btnPhysikManuell = null;
    private AKJButton btnNdrahtSDSL = null;
    private AKJButton btnCrossConnectionsOrVlans = null;
    private boolean nDrahtEnabled;

    /**
     * Konstruktor mit Angabe des Panels mit den Endstellen.
     *
     * @param esPanel
     */
    public RangierungPanel(AuftragEndstellenPanel esPanel) {
        super("de/augustakom/hurrican/gui/auftrag/resources/RangierungPanel.xml");
        this.esPanel = esPanel;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        tbMdlEquipment = new Equipment4RangierungTableModel();
        AKJTable tbEquipment = new AKJTable(tbMdlEquipment, JTable.AUTO_RESIZE_OFF,
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbMdlEquipment.setTable(tbEquipment);
        tbEquipment.fitTable(new int[] { 105, 125, 125, 125, 125 });
        tbEquipment.setFilterEnabled(false);
        tbEquipment.setPopupChangeSelection(false);
        EditDtagPortAction editDtagPortAction = new EditDtagPortAction();
        editDtagPortAction.setParentClass(this.getClass());
        tbEquipment.addPopupAction(editDtagPortAction);
        ChangeLayer2ProtocolAction changeLayer2ProtocolAction = new ChangeLayer2ProtocolAction();
        changeLayer2ProtocolAction.setParentClass(this.getClass());
        tbEquipment.addPopupAction(changeLayer2ProtocolAction);
        tbEquipment.addPopupSeparator();
        tbEquipment.addPopupAction(new ShowPortsAction());
        tbEquipment.getColumnModel().getColumn(0).setHeaderRenderer(new IconTableHeaderRenderer());

        AKJScrollPane spTable = new AKJScrollPane(tbEquipment);
        spTable.setPreferredSize(new Dimension(650, 235));

        btnPhysikZuordnen = getSwingFactory().createButton(PHYSIK_ZUORDNEN, getActionListener());
        btnPhysikUebernahme = getSwingFactory().createButton(PHYSIK_UEBERNEHMEN, getActionListener());
        btnPhysikManuell = getSwingFactory().createButton(PHYSIK_MANUELL, getActionListener());
        btnPhysikDetailed = getSwingFactory().createButton(PHYSIK_DETAILED, getActionListener());
        btnNdrahtSDSL = getSwingFactory().createButton(NDRAHT_SDSL_ZUORDNUNG, getActionListener());

        // je nach Standorttyp wird crossConnection oder VLAN Dialog angezogen siehe #readModel()
        btnCrossConnectionsOrVlans = getSwingFactory().createButton(CROSS_CONNECTIONS_ZUORDNUNG, getActionListener());
        AKJButton btnCreateRangierung = getSwingFactory().createButton(CREATE_RANGIERUNG, getActionListener());
        AKJButton btnRangHistory = getSwingFactory().createButton(RANGIERUNGS_HISTORY, getActionListener());
        AKJButton btnDopBelegung = getSwingFactory().createButton(DOPPELTE_BELEGUNG, getActionListener());
        AKJButton btnPortChange = getSwingFactory().createButton(PORT_CHANGE, getActionListener());
        AKJButton btnBreakRangierung = getSwingFactory().createButton(BREAK_RANGIERUNG, getActionListener());
        AKJButton btnRangEntfernen = getSwingFactory().createButton(RANGIERUNG_ENTFERNEN, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnPhysikZuordnen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnPhysikDetailed, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnPhysikUebernahme, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnPhysikManuell, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCreateRangierung, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnNdrahtSDSL, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCrossConnectionsOrVlans, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));
        btnPanel.add(btnRangHistory, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnBreakRangierung, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnDopBelegung, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnPortChange, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnRangEntfernen, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(spTable, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(btnPanel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnPhysikZuordnen, btnPortChange, btnBreakRangierung,
                btnPhysikUebernahme, btnPhysikManuell, btnPhysikDetailed, btnCreateRangierung, btnNdrahtSDSL,
                btnCrossConnectionsOrVlans, editDtagPortAction, btnRangEntfernen);
        btnNdrahtSDSL.setEnabled(false);
    }

    @Override
    public void readModel() {
        try {
            tbMdlEquipment.removeAll();

            if (this.endstelle != null) {
                setWaitCursor();

                rangierung = null;
                rangierungAdd = null;
                tbMdlEquipment.setEndstelle(endstelle);

                // Rangierung zur Endstelle laden
                RangierungsService rs = getCCService(RangierungsService.class);
                rangierung = rs.findRangierungWithEQ(endstelle.getRangierId());
                rangierungAdd = (endstelle.getRangierIdAdditional() != null) ? rs.findRangierungWithEQ(endstelle
                        .getRangierIdAdditional()) : null;

                if ((rangierung != null) || (rangierungAdd != null)) {
                    tbMdlEquipment.setRangierung(rangierung, rangierungAdd);
                }
                if ((rangierung != null) && (endstelle.getHvtIdStandort() != null)
                        && getCCService(HVTService.class).findHVTStandort(endstelle.getHvtIdStandort()).isFtthOrFttb()) {
                    btnCrossConnectionsOrVlans.setActionCommand(VLANS_ZUORDNUNG);
                    btnCrossConnectionsOrVlans.setText("VLANs");
                    btnCrossConnectionsOrVlans.setToolTipText("VLANs bearbeiten");
                }
                setNdrahtEnabled();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void setNdrahtEnabled() throws FindException,
            de.augustakom.common.service.exceptions.ServiceNotFoundException {
        final AuftragDaten auftragDaten = getCCService(CCAuftragService.class)
                .findAuftragDatenByEndstelleTx(this.endstelle.getId());
        if (auftragDaten != null) {
            final Produkt produkt = getCCService(ProduktService.class).findProdukt(auftragDaten.getProdId());
            if (produkt != null && produkt.getSdslNdraht() != null) {
                nDrahtEnabled = true;
                btnNdrahtSDSL.setEnabled(nDrahtEnabled);
            }
        }
    }

    @Override
    public void saveModel() throws AKGUIException {
        LOGGER.warn("RangierungPanel.saveModel() not implemented");
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof Endstelle) {
            this.endstelle = (Endstelle) model;
        }
        else {
            this.endstelle = null;
        }

        readModel();
    }

    private void reloadEsPanel() {
        List<PositionParameter> positionParameters = esPanel.getPositionParameters();
        esPanel.readModel();
        esPanel.setPositionParameters(positionParameters);
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case PHYSIK_ZUORDNEN:
                assignPhysik2ES();
                break;
            case PHYSIK_UEBERNEHMEN:
                useExistingPhysik();
                break;
            case PHYSIK_MANUELL:
                physikZuordnungManuell();
                break;
            case CREATE_RANGIERUNG:
                createRangierung();
                break;
            case NDRAHT_SDSL_ZUORDNUNG:
                physikZuordnungNdrahtSDSL();
                break;
            case RANGIERUNGS_HISTORY:
                showRangierungsHistory();
                break;
            case DOPPELTE_BELEGUNG:
                showAuftraege4Rangierung();
                break;
            case PHYSIK_DETAILED:
                physikZuordnungDetailed();
                break;
            case CROSS_CONNECTIONS_ZUORDNUNG:
                showCrossConections();
                break;
            case VLANS_ZUORDNUNG:
                showVlans();
                break;
            case PORT_CHANGE:
                portChange();
                break;
            case BREAK_RANGIERUNG:
                breakRangierung();
                break;
            case RANGIERUNG_ENTFERNEN:
                rangierungEntfernen();
                break;
            default:
                break;
        }
    }

    /**
     * Setzt die Buttons auf enabled/disabled.
     */
    private void enableButtons(boolean enabled) {
        btnPhysikZuordnen.setEnabled(enabled);
        btnPhysikUebernahme.setEnabled(enabled);
        btnPhysikDetailed.setEnabled(enabled);
        btnPhysikManuell.setEnabled(enabled);
        btnCrossConnectionsOrVlans.setEnabled(enabled);
    }

    /**
     * Physikzuordnung durch manuelle Auswahl der zu verwendenden Rangierung.
     */
    private void physikZuordnungManuell() {
        try {
            if (rangierung != null) {
                throw new HurricanGUIException("Der Endstelle ist bereits eine Physik zugeordnet!");
            }

            PhysikZuordnungDialog dlg = new PhysikZuordnungDialog(endstelle, true, true);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if ((result instanceof Integer) && NumberTools.equal((Integer) result, JOptionPane.OK_OPTION)) {
                reloadEsPanel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den ein Port-Wechsel durchgefuehrt werden kann.
     */
    private void portChange() {
        try {
            PortChangeDialog dlg = new PortChangeDialog(endstelle, rangierung, rangierungAdd);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if ((result instanceof Integer) && NumberTools.equal((Integer) result, JOptionPane.OK_OPTION)) {
                reloadEsPanel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den definiert werden kann, welcher Rangierungsbestandteile entfernt werden sollen.
     */
    private void breakRangierung() {
        try {
            BreakRangierungDialog dlg = new BreakRangierungDialog(rangierung, rangierungAdd);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if ((result instanceof Integer) && NumberTools.equal((Integer) result, JOptionPane.OK_OPTION)) {
                reloadEsPanel();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Oeffnet einen Dialog, ueber den eine Rangierung manuell aufgebaut werden kann.
     */
    private void createRangierung() {
        try {
            AssignEquipmentDialog dlg = new AssignEquipmentDialog(this.endstelle);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            reloadEsPanel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Physikzuordnung mit vorhergehender Auswahl der notwendigen Port-Typen.
     */
    private void physikZuordnungDetailed() {
        try {
            if (rangierung != null) {
                throw new HurricanGUIException("Der Endstelle ist bereits eine Physik zugeordnet!");
            }

            setWaitCursor();
            enableButtons(false);
            if (isEndstelleOk()) {
                // Auswahl der Eigenschaften der zuzuordnenden Ports
                Object selection = MessageHelper.showInputDialog(getMainFrame(),
                        CarrierEquipmentDetails.getCEDetails(), new AKCustomListCellRenderer<>(
                                CarrierEquipmentDetails.class, CarrierEquipmentDetails::getName), "Auswahl CuDA-Art",
                        "Bitte wählen Sie die gewünschte CuDA-Art aus.", "CuDA-Art:"
                );
                if (selection instanceof CarrierEquipmentDetails) {
                    CarrierEquipmentDetails ced = (CarrierEquipmentDetails) selection;

                    RangierungsService rs = getCCService(RangierungsService.class);
                    rs.assignRangierung2ES(endstelle.getId(), ced, this);
                    reloadEsPanel();
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            enableButtons(true);
            setDefaultCursor();
        }
    }

    /**
     * Zuordnung einer N-Draht SDSL-Physik fuer die Endstelle
     */
    private void physikZuordnungNdrahtSDSL() {
        try {
            if (this.nDrahtEnabled) {
                final AKWarnings warnings =
                        getCCService(SdslEquipmentService.class).assignSdslNdraht(endstelle.getId(), new Date());
                if (warnings.isNotEmpty()) {
                    MessageHelper.showWarningDialog(getMainFrame(), warnings.getWarningsAsText(), true);
                }
            }
            reloadEsPanel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Ordnet der Endstelle eine neue Physik zu.
     */
    private void assignPhysik2ES() {
        try {
            setWaitCursor();
            enableButtons(false);

            // check FTTH
            if (AssignEquipmentHelper.isFTTH(endstelle.getHvtIdStandort())) {
                throw new HurricanGUIException("Keine Zuordnung bei FTTH-Auftrag möglich.");
            }

            // check Gewofag
            if (AssignEquipmentHelper.isGewofag(endstelle.getHvtIdStandort())) {
                Object result = DialogHelper.showDialog(getMainFrame(), new ChooseGewofagWohnungDialog(endstelle),
                        true, true);
                if (result instanceof GewofagWohnung) {
                    GewofagService gewofagService = getCCService(GewofagService.class);
                    EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
                    gewofagService.wohnungsZuordnung(endstelle, (GewofagWohnung) result, HurricanSystemRegistry
                            .instance().getSessionId());
                    endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);
                }
                return;
            }

            if (isEndstelleOk()) {
                RangierungsService rs = getCCService(RangierungsService.class);
                rs.assignRangierung2ES(endstelle.getId(), this);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            enableButtons(true);
            setDefaultCursor();
            reloadEsPanel();
        }
    }

    /**
     * Zeigt den Dialog zur Bearbeitung der Cross Connections an.
     */
    private void showCrossConections() {
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            RangierungsService rangierungsService = getCCService(RangierungsService.class);
            EQCrossConnectionService ccService = getCCService(EQCrossConnectionService.class);

            final String message = ccService.checkCcsAllowed(endstelle);
            if (message != null) {
                throw new HurricanGUIException(message);
            }

            Equipment eqInPort = rangierungsService.findEquipment(rangierung.getEqInId());
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());

            CrossConnectionDialog ccDialog = new CrossConnectionDialog(eqInPort, auftragDaten.getAuftragId());
            DialogHelper.showDialog(getMainFrame(), ccDialog, true, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void showVlans() {
        try {
            if (rangierung == null) {
                throw new HurricanGUIException("Die Endstelle hat keine Rangierung.");
            }
            Long eqInPortId = rangierung.getEqInId();
            if (eqInPortId == null) {
                throw new HurricanGUIException("Die Rangierung hat keinen EQ-In Port.");
            }
            VlanDialog ccDialog = new VlanDialog(eqInPortId, endstelle);
            DialogHelper.showDialog(getMainFrame(), ccDialog, true, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Entfernt die aktuelle Rangierung nach einigen Konsistenzchecks
     */
    private void rangierungEntfernen() {
        try {
            AKDateSelectionDialog dateSelectionDialog = new AKDateSelectionDialog("Rangierung entfernen",
                    "<html><body><p>Falls ein Freigabe-Ab-Datum eingegeben wird, wird<br/>"
                            + "die Rangierung bis zu diesem diesem Datum für die<br/>"
                            + "automatische Vergabe gesperrt. Ansonsten steht<br/>"
                            + "die Rangierung sofort wieder für die automatische<br/>"
                            + "Vergabe zur Verfügung.</p></body></html>", "Freigabe ab:", true
            );
            Object result = DialogHelper.showDialog(this, dateSelectionDialog, true, true);
            if (result != AKDateSelectionDialog.CANCEL_OPTION) {
                Date freigabeAb = null;
                if (result instanceof Date) {
                    freigabeAb = (Date) result;
                }
                String bemerkung = "Freigabe durch "
                        + HurricanSystemRegistry.instance().getCurrentUser().getFirstNameAndName();
                RangierungFreigabeService rangierungFreigabeService = getCCService(RangierungFreigabeService.class);
                EndgeraeteService endgeraeteService = getCCService(EndgeraeteService.class);
                rangierungFreigabeService.removeRangierung(endstelle, rangierung, rangierungAdd, freigabeAb, bemerkung,
                        HurricanSystemRegistry.instance().getSessionId());
                endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);
                refreshAuftragFrame();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        if (callbackAction == RangierungsService.CALLBACK_ASK_4_BUENDEL_PRODUKT) {
            // Frage nach dem zugehoerigen Buendel-Produkt
            Long parentProdId = (Long) parameters.get(RangierungsService.CALLBACK_PARAM_PARENT_PRODUKT_ID);
            try {
                ProduktService ps = getCCService(ProduktService.class);
                List<Produkt> childProdukte = ps.findChildProdukte(parentProdId);

                if ((childProdukte != null) && (!childProdukte.isEmpty())) {
                    ProduktSelectionDialog dlg = new ProduktSelectionDialog(childProdukte);
                    return DialogHelper.showDialog(this, dlg, true, true);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else if (callbackAction == RangierungsService.CALLBACK_ASK_KUENDIGUNG_AUTOMATIC_4_ANSCHLUSSUEBERNAHME) {
            // Frage, ob der 'alte' Auftrag bei einer Anschlussuebernahme auf gekuendigt gesetzt werden soll
            Long auftragId = (Long) parameters
                    .get(RangierungsService.CALLBACK_PARAM_AUFTRAG_ID_4_ANSCHLUSSUEBERNAHME);
            String msg = getSwingFactory().getText("ask.auftrag.kuendigen", "" + auftragId);
            String title = getSwingFactory().getText("ask.auftrag.kuendigen.title");
            int option = MessageHelper.showConfirmDialog(getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            return (option == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (callbackAction == RangierungsService.CALLBACK_ASK_4_ACCOUNT_UEBERNAHME) {
            // Frage, ob der IntAccount uebernommen werden soll
            IntAccount account = (IntAccount) parameters.get(RangierungsService.CALLBACK_PARAM_ACCOUNT);
            String title = getSwingFactory().getText("ask.4.account.uebernahme.title");
            String msg = getSwingFactory().getText("ask.4.account.uebernahme", account.getAccount());
            int option = MessageHelper.showConfirmDialog(getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            return (option == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (callbackAction == RangierungsService.CALLBACK_ASK_4_VPN_UEBERNAHME) {
            // Frage, ob die VPN-Daten uebernommen werden sollen
            Long vpnNr = (Long) parameters.get(RangierungsService.CALLBACK_PARAM_VPN_NR);
            String title = getSwingFactory().getText("ask.4.vpn.uebernahme.title");
            String msg = getSwingFactory().getText("ask.4.vpn.uebernahme", "" + vpnNr);
            int option = MessageHelper.showConfirmDialog(getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            return (option == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }
        else if (callbackAction == RangierungsService.CALLBACK_ASK_4_KUENDIGUNGS_DATUM) {
            return ask4KuendigungsDatum(parameters);
        }
        else if (callbackAction == RangierungsService.CALLBACK_ASK_4_REAL_DATE) {
            return ask4RealDate();
        }
        else if (callbackAction == IPAddressService.CALLBACK_ASK_4_IP_ADDRESS_UEBERNAHME) {
            // Frage, ob die IP Adressen uebernommen werden sollen
            String title = getSwingFactory().getText("ask.4.ip.adress.uebernahme.title");
            String msg = getSwingFactory().getText("ask.4.ip.adress.uebernahme");
            int option = MessageHelper.showConfirmDialog(getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            return (option == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }

        return null;
    }

    /**
     * Service-Callback, um das Kuendigungsdatum fuer einen Auftrag abzufragen.
     */
    private Date ask4KuendigungsDatum(Map<String, ?> params) {
        Date vorgabeSCV = (Date) params.get(RangierungsService.CALLBACK_PARAM_VORGABE_SCV);
        String title = getSwingFactory().getText("ask.4.kuend.date.title");
        String sub = getSwingFactory().getText("ask.4.kuend.date.sub");
        String label = getSwingFactory().getText("ask.4.kuend.date");

        AKDateSelectionDialog dlg = new AKDateSelectionDialog(title, sub, label);
        dlg.showDate(vorgabeSCV);
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        return (result instanceof Date) ? (Date) result : null;
    }

    /**
     * Service-Callback, um das Realisierungsdatum fuer einen Auftrag abzufragen.
     */
    private Date ask4RealDate() {
        String title = getSwingFactory().getText("ask.4.real.date.title");
        String sub = getSwingFactory().getText("ask.4.real.date.sub");
        String label = getSwingFactory().getText("ask.4.real.date");

        AKDateSelectionDialog dlg = new AKDateSelectionDialog(title, sub, label);
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        return (result instanceof Date) ? (Date) result : null;
    }

    /**
     * Ordnet der Endstelle eine Physik eines anderen Auftrags zu.
     */
    private void useExistingPhysik() {
        try {
            setWaitCursor();
            enableButtons(false);
            if (isEndstelleOk()) {
                PhysikUebernahmeDialog dlg = new PhysikUebernahmeDialog();
                Object selection = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (selection instanceof Long) {
                    Long select = (Long) selection;
                    if (!(select == JOptionPane.CANCEL_OPTION)) {
                        // selection is an Long
                        NumberRange range = new NumberRange(PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME,
                                PhysikaenderungsTyp.STRATEGY_BANDBREITENAENDERUNG);
                        String title;
                        String message;

                        if (range.containsInteger(select)) {
                            message = getSwingFactory().getText("uebernahme.text");
                            title = getSwingFactory().getText("uebernahme.title");
                        }
                        else if (NumberTools.equal(select, PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG)) {
                            message = getSwingFactory().getText("uebernahme.dsl.text");
                            title = getSwingFactory().getText("uebernahme.dsl.title");
                        }
                        else {
                            throw new HurricanGUIException("Übernahmestrategie wird nicht unterstützt!");
                        }

                        int question = MessageHelper.showConfirmDialog(getMainFrame(), message, title,
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (question == JOptionPane.YES_OPTION) {
                            selectAuftrag4Uebernahme(select);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            enableButtons(true);
            setDefaultCursor();
        }
    }

    /**
     * Oeffnet einen Dialog zur Auswahl des Auftrags, dessen Physik uebernommen werden soll.
     */
    private void selectAuftrag4Uebernahme(Long strategy) {
        if (endstelle.getGeoId() == null) {
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("no.geo.id"), "GeoID fehlt", null,
                    true);
            return;
        }

        if ((this.rangierung != null) && (this.rangierung.getId() != null)) {
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("rangierung.vorhanden"),
                    "Rangierung bereits zugeordnet", null, true);
            return;
        }

        Long auftragIdSrc = null;
        Long auftragIdDest = null;
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragTechnik at = as.findAuftragTechnik4ESGruppe(this.endstelle.getEndstelleGruppeId());
            auftragIdDest = (at != null) ? at.getAuftragId() : null;

            boolean selectAuftrag = selectAuftrag(strategy);
            if (selectAuftrag) {
                EndstellenSelectionDialog dlg = new EndstellenSelectionDialog(strategy, endstelle.getId());
                Object selection = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (selection instanceof AuftragEndstelleView) {
                    AuftragEndstelleView view = (AuftragEndstelleView) selection;
                    auftragIdSrc = view.getAuftragId();
                }
                else if ((selection instanceof Integer)
                        && NumberTools.equal((Integer) selection, JOptionPane.CANCEL_OPTION)) {
                    return;
                }
            }

            if ((auftragIdSrc == null) && (PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG.equals(strategy))) {
                // bei DSL-Kreuzung innerhalb DSLplus ist der Ziel-Auftrag gleichzeitig der Source-Auftrag
                auftragIdSrc = auftragIdDest;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        try {
            setWaitCursor();
            RangierungsService rs = getCCService(RangierungsService.class);
            AKWarnings warnings = rs.physikAenderung(strategy, auftragIdSrc, auftragIdDest, this,
                    HurricanSystemRegistry.instance().getSessionId());
            if ((warnings != null) && warnings.isNotEmpty()) {
                MessageHelper.showInfoDialog(getMainFrame(), warnings.getWarningsAsText(), null, true);
            }

            MessageHelper.showMessageDialog(getMainFrame(), getSwingFactory().getText("physik.uebernommen"),
                    "Physik übernommen", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            refreshAuftragFrame(); // Refresh durchfuehren, damit geaenderte Daten geladen werden!
            setDefaultCursor();
        }
    }

    /**
     * Prueft, ob fuer die Uebernahme-Strategie ein Basis-Auftrags ausgewaehlt werden muss.
     */
    private boolean selectAuftrag(Long strategy) throws HurricanGUIException {
        if (PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG.equals(strategy)) {
            try {
                CCAuftragService as = getCCService(CCAuftragService.class);
                AuftragTechnik auftragTechnik = as.findAuftragTechnik4ESGruppe(endstelle.getEndstelleGruppeId());
                AuftragDaten auftragDaten = (auftragTechnik != null) ? as.findAuftragDatenByAuftragId(auftragTechnik
                        .getAuftragId()) : null;
                if (auftragDaten == null) {
                    StringBuilder msg = new StringBuilder(getSwingFactory().getText("dsl.kreuzung.impossible"));
                    msg.append(getSwingFactory().getText("dsl.kreuzung.auftrag.fehlt"));
                    throw new HurricanGUIException(msg.toString());
                }

                List<AuftragDaten> buendel = as.findAuftragDaten4Buendel(auftragDaten.getBuendelNr(),
                        auftragDaten.getBuendelNrHerkunft());
                return (buendel == null) || (buendel.size() <= 2);

            }
            catch (HurricanGUIException e) {
                throw e;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                StringBuilder msg = new StringBuilder(getSwingFactory().getText("dsl.kreuzung.impossible"));
                msg.append(e.getMessage());
                throw new HurricanGUIException(msg.toString());
            }
        }

        return true;
    }

    /**
     * Ueberprueft, ob die Endstellendaten i.O. sind.
     */
    private boolean isEndstelleOk() {
        if (this.endstelle != null) {
            if (this.endstelle.getId() != null) {
                esPanel.saveModel();
                return true;
            }
            MessageHelper.showInfoDialog(this, getSwingFactory().getText("endstelle.speichern"));
        }
        else {
            MessageHelper.showInfoDialog(this, getSwingFactory().getText("keine.endstelle"));
        }

        return false;
    }

    /**
     * Zeigt alle Auftraege an, die der aktuellen Rangierung zugeordnet sind bzw. waren.
     */
    private void showAuftraege4Rangierung() {
        if (this.rangierung != null) {
            Auftraege4RangierungDialog dlg = new Auftraege4RangierungDialog(this.rangierung);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        else {
            MessageHelper.showInfoDialog(this, getSwingFactory().getText("keine.rangierung"));
        }
    }

    /**
     * Oeffnet einen Dialog, um die Rangierungs-History anzuzeigen.
     */
    private void showRangierungsHistory() {
        if (this.rangierung != null) {
            RangierungsHistoryDialog dlg = new RangierungsHistoryDialog(this.rangierung);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        else {
            MessageHelper.showInfoDialog(this, getSwingFactory().getText("keine.rangierung.historie"));
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    /**
     * Action, die Daten der Ports (im Moment nur des DTAG Ports) zu editieren.
     */
    class EditDtagPortAction extends AKAbstractAction {
        private static final long serialVersionUID = 8418875579132978257L;

        public EditDtagPortAction() {
            setName("DTAG Port editieren...");
            setActionCommand("edit.port.dtag");
            setTooltip("Oeffnet einen Dialog, um das Übertragungsverfahren und die Physikart des Ports zu editieren");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (endstelle == null) {
                    MessageHelper.showInfoDialog(getMainFrame(), "Kein DTAG-Port vorhanden!");
                    return;
                }
                CarrierService carrierService = getCCService(CarrierService.class);
                Carrierbestellung cb = carrierService.findLastCB4Endstelle(endstelle.getId());

                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                List<Equipment> eqList = new ArrayList<>();
                if (rangierung != null) {
                    eqList.add(rangierungsService.findEquipment(rangierung.getEqInId()));
                    eqList.add(rangierungsService.findEquipment(rangierung.getEqOutId()));
                }
                if (rangierungAdd != null) {
                    eqList.add(rangierungsService.findEquipment(rangierungAdd.getEqInId()));
                    eqList.add(rangierungsService.findEquipment(rangierungAdd.getEqOutId()));
                }

                Equipment equipment = null;
                for (Equipment eq : eqList) {
                    if ((eq != null) && Carrier.CARRIER_DTAG.equals(eq.getCarrier())) {
                        equipment = eq;
                    }
                }

                if (equipment == null) {
                    MessageHelper.showMessageDialog(getMainFrame(), "Kein DTAG-Port vorhanden!");
                    return;
                }
                EditDtagPortDialog dlg = new EditDtagPortDialog(equipment, cb);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Equipment) {
                    refreshAuftragFrame(); // Refresh durchfuehren, damit geaenderte Daten geladen werden!
                }
            }
            catch (Exception exception) {
                MessageHelper.showErrorDialog(getMainFrame(), exception);
            }
        }
    }

    /**
     * Action oeffnet bei SDSL Ports einen Dialog, ueber den das Layer2-Protokoll des Ports definiert werden kann.
     */
    class ChangeLayer2ProtocolAction extends AKAbstractAction {
        private static final long serialVersionUID = -8936885139024756992L;

        public ChangeLayer2ProtocolAction() {
            setName("Schicht2-Protokoll ändern...");
            setActionCommand("edit.port.layer2");
            setTooltip("Öffnet einen Dialog um das verwendete Schicht2-Protokoll einzustellen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                List<Equipment> eqList = new ArrayList<>();
                if (rangierung != null) {
                    eqList.add(rangierungsService.findEquipment(rangierung.getEqInId()));
                    eqList.add(rangierungsService.findEquipment(rangierung.getEqOutId()));
                }
                if (rangierungAdd != null) {
                    eqList.add(rangierungsService.findEquipment(rangierungAdd.getEqInId()));
                    eqList.add(rangierungsService.findEquipment(rangierungAdd.getEqOutId()));
                }

                Equipment equipment = null;
                for (Equipment eq : eqList) {
                    if ((eq != null) && Equipment.HW_SCHNITTSTELLE_SDSL_OUT.equals(eq.getHwSchnittstelle())) {
                        equipment = eq;
                    }
                }

                if (equipment == null) {
                    MessageHelper.showMessageDialog(getMainFrame(), "Kein SDSL-Port vorhanden!");
                }
                else {
                    ChangeLayer2ProtocolDialog dlg = new ChangeLayer2ProtocolDialog(equipment);
                    Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    if (result instanceof Equipment) {
                        refreshAuftragFrame(); // Refresh durchfuehren, damit geaenderte Daten geladen werden!
                    }
                }
            }
            catch (Exception exception) {
                MessageHelper.showErrorDialog(getMainFrame(), exception);
            }
        }
    }

    /**
     * Action oeffnet einen Dialog, der alle Ports zum aktuellen Taifun Auftrag anzeigt.
     */
    class ShowPortsAction extends AKAbstractAction {
        private static final long serialVersionUID = -7670434901745466358L;

        public ShowPortsAction() {
            setName("Ports anzeigen...");
            setActionCommand("show.ports");
            setTooltip("Öffnet einen Dialog, der alle Ports zum Taifun Auftrag anzeigt");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());
                if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
                    throw new HurricanGUIException("Taifun Auftrag konnte nicht ermittelt werden!");
                }

                PortsDialog portsDialog = PortsDialog.createWithAuftragNoOrig(auftragDaten.getAuftragNoOrig());
                DialogHelper.showDialog(getMainFrame(), portsDialog, true, true);
            }
            catch (Exception ex) {
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }
}
