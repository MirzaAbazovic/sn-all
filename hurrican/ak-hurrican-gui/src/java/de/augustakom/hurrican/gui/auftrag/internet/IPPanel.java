/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2004 15:11:30
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.shared.IPAddressTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Panel fuer die Darstellung von IP-Daten.
 *
 *
 */
public class IPPanel extends AbstractDataPanel implements AKModelOwner {

    private static final Logger LOGGER = Logger.getLogger(IPPanel.class);

    private static final String IP_DEALLOCATE = "ip.deallocate.menue";
    private static final String IP_MOVE = "move.ip";

    // GUI-Komponenten
    private AKJTable tbIPs = null;
    private IPAddressTableModel tbMdlIPs = null;

    // Modelle
    private CCAuftragModel model = null;
    private AuftragDaten auftragDaten = null;

    private final IPAddressService ipAddressService;
    private final CCAuftragService auftragService;

    public IPPanel() {
        super(null);
        try {
            ipAddressService = getCCService(IPAddressService.class);
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlIPs = new IPAddressTableModel();
        tbIPs = new AKJTable(tbMdlIPs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbIPs.attachSorter();
        tbIPs.fitTable(new int[] { 200, 120, 150, 90, 60, 60 });
        AKJScrollPane spIPs = new AKJScrollPane(tbIPs);
        spIPs.setPreferredSize(new Dimension(250, 50));
        MoveIpAction moveIpAction = new MoveIpAction();
        moveIpAction.setParentClass(this.getClass());
        tbIPs.addPopupAction(moveIpAction);

        DeAllocateIpAction deAllocateIpAction = new DeAllocateIpAction();
        deAllocateIpAction.setParentClass(this.getClass());
        tbIPs.addPopupAction(deAllocateIpAction);

        this.setLayout(new BorderLayout());
        this.add(spIPs, BorderLayout.CENTER);

        manageGUI(moveIpAction, deAllocateIpAction);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.auftragDaten = null;
        this.model = (model instanceof CCAuftragModel) ? (CCAuftragModel) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        tbMdlIPs.setData(null);
        if (this.model != null) {
            try {
                setWaitCursor();
                auftragDaten = auftragService.findAuftragDatenByAuftragId(this.model.getAuftragId());

                if (auftragDaten != null) {
                    List<IPAddressPanelView> ipViews = ipAddressService.findAllIPAddressPanelViews(
                            auftragDaten.getAuftragNoOrig());
                    tbMdlIPs.setData(ipViews);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * @return Returns the tbIPs.
     */
    private AKJTable getTbIPs() {
        return tbIPs;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        // Panel nur fuer Anzeige!!!
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
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    private List<IPAddress> selectedIPAddressIds(int[] rows) {
        List<IPAddress> ipIds = new ArrayList<>(rows.length);
        for (int selectedRow : rows) {
            @SuppressWarnings("unchecked")
            IPAddress selectedIPAddr = ((AKMutableTableModel<IPAddressPanelView>) getTbIPs().getModel())
                    .getDataAtRow(selectedRow).getIpAddress();
            ipIds.add(selectedIPAddr);
        }
        return ipIds;
    }

    void deAllocateSelectedIps() {
        int[] selectedRows = tbIPs.getSelectedRows();
        List<IPAddress> ipsToDeAllocate = selectedIPAddressIds(selectedRows);
        if (!ipsToDeAllocate.isEmpty()) {
            try {
                AKWarnings checkWarnings = checkIpsToDeallocate(ipsToDeAllocate);
                if (checkWarnings.isNotEmpty()) {
                    MessageHelper.showInfoDialog(getMainFrame(), String.format(
                            "Die Freigabe kann nicht durchgeführt werden. Folgende Fehler sind aufgetreten:%n%s" +
                                    "%nHinweis: IP Adressen müssen vom Auftrag freigegeben werden, dem sie aktiv zugeordnet sind!",
                            checkWarnings.getWarningsAsText()
                    ));
                    return;
                }

                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll(en) die IP-Adresse(n) wirklich zu sofort gelöscht werden?", "IPs löschen?");
                if (option == JOptionPane.YES_OPTION) {
                    AKWarnings releaseWarnings = ipAddressService.releaseIPAddressesNow(ipsToDeAllocate,
                            HurricanSystemRegistry.instance().getSessionId());
                    if (releaseWarnings.isNotEmpty()) {
                        MessageHelper.showWarningDialog(this, String.format(
                                "Während der Freigabe sind folgende Warnungen aufgetreten:%n%s",
                                releaseWarnings.getWarningsAsText()), true);
                    }
                    else {
                        MessageHelper.showInfoDialog(this, "Freigabe ist abgeschlossen. Bitte starten Sie nun eine neue CPS-Transaktion."
                                + " Dabei kann es abhängig vom Produkt notwendig sein, eine neue IP Adresse im Vorfeld zu ziehen!");
                    }
                }
            }
            catch (Exception ex) {
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
            finally {
                readModel();
            }
        }
    }

    private AKWarnings checkIpsToDeallocate(List<IPAddress> ipsToDeAllocate) {
        AKWarnings warnings = new AKWarnings();
        for (IPAddress ipAddress : ipsToDeAllocate) {
            if (ipAddress.getFreigegeben() != null) {
                warnings.addAKWarning(this,
                        String.format("Die IP Addresse %s ist bereits freigegeben!", ipAddress.getAddress()));
            }
            else if (DateTools.compareDates(ipAddress.getGueltigBis(), DateTools.getHurricanEndDate()) != 0) {
                // wenn IP Adresse nicht mehr aktiv auf aktuellem Auftrag, aber
                // mindestens noch einem anderen aktiven Auftrag zugeordnet ->
                // Fehler
                // IP Adresse sollte auf dem aktiven Auftrag freigegeben werden.
                warnings.addAKWarnings(ipAddressService.ipIsAssignedOnceOnly(ipAddress));
            }
        }
        return warnings;
    }

    /* Action, um eine IP-Adresse auf einen anderen Taifun-Auftrag zu verschieben. */
    private class MoveIpAction extends AKAbstractAction {
        public MoveIpAction() {
            super();
            setName("IP umziehen...");
            setTooltip("Umzug der IP-Adresse auf einen anderen Billing Auftrag");
            setActionCommand(IPPanel.IP_MOVE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IPAddress ipToMove = getIpAddressToMove();
            if (ipToMove == null) {
                return;
            }
            Long billingOrderNo = getAnswerOfBillingOrderNoMovementDialog();
            if (billingOrderNo != null) {
                int option = getAnswerOfYesNoDialogMovingIpAddressToOtherBillingNo(billingOrderNo);
                if (option == JOptionPane.YES_OPTION) {
                    try {
                        findAuftrag(billingOrderNo);
                        moveIpAddress(ipToMove, billingOrderNo);
                        readModel();
                    }
                    catch (Exception ex) {
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }

        /**
         * @param ipToMove
         * @param billingOrderNo
         * @throws StoreException
         * @throws ServiceNotFoundException
         */
        private void moveIpAddress(IPAddress ipToMove, Long billingOrderNo) throws StoreException {
            ipAddressService.moveIPAddress(ipToMove, billingOrderNo,
                    HurricanSystemRegistry.instance().getSessionId());
        }

        /**
         * @param billingOrderNo
         * @throws ServiceNotFoundException
         * @throws FindException
         * @throws HurricanGUIException
         */
        private void findAuftrag(Long billingOrderNo) throws ServiceNotFoundException, FindException,
                HurricanGUIException {
            BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
            BAuftrag billingAuftrag = billingAuftragService.findAuftrag(billingOrderNo);
            if (billingAuftrag == null) {
                throw new HurricanGUIException("Zu der angegebenen Billing Auftragsnummer wurde kein Billing "
                        + "Auftrag gefunden!");
            }
        }

        private Long getAnswerOfBillingOrderNoMovementDialog() {
            String billingOrderNoStr = MessageHelper.showInputDialog(getMainFrame(), "neue Billing Auftragsnummer:",
                    "IP V4 Umzug", JOptionPane.PLAIN_MESSAGE);
            if (billingOrderNoStr != null) {
                try {
                    return Long.valueOf(billingOrderNoStr);

                }
                catch (NumberFormatException ex) {
                    MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException(
                            "Bitte geben Sie eine gueltige Taifun Auftragsnummer ein!"));
                }
            }
            return null;
        }

        private int getAnswerOfYesNoDialogMovingIpAddressToOtherBillingNo(Long billingOrderNo) {
            return MessageHelper.showYesNoQuestion(getMainFrame(), String.format(
                    "Soll die IP V4 Adresse wirklich auf den Billing%nAuftrag %s umgezogen werden?",
                    billingOrderNo), "IP V4 umziehen?");
        }

        @SuppressWarnings("unchecked")
        private IPAddress getIpAddressToMove() {
            return ((AKMutableTableModel<IPAddressPanelView>) getTbIPs().getModel()).getDataAtRow(getTbIPs()
                    .getSelectedRow()).getIpAddress();
        }
    } // end

    private class DeAllocateIpAction extends AKAbstractAction {
        public DeAllocateIpAction() {
            super();
            setName("IP löschen...");
            setTooltip("Entfernt die IP-Adresse von dem Auftrag");
            setActionCommand(IPPanel.IP_DEALLOCATE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deAllocateSelectedIps();
        }
    }
}
