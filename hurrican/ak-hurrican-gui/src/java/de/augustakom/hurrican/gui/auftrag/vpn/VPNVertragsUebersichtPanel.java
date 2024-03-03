/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 15:24:46
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJComboBoxCellEditor;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.shared.VPNVertragViewTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.billing.view.BVPNAuftragView;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.shared.view.VPNVertragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Panel fuer die Darstellung der VPN-Vertrags-Uebersicht.
 *
 *
 */
public class VPNVertragsUebersichtPanel extends AbstractDataPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(VPNVertragsUebersichtPanel.class);

    private AKJComboBox cbNiederlassung;

    private VPNVertragTable tbVPNs = null;
    private VPNVertragViewTableModel tbMdlVPNs = null;

    /**
     * Konstruktor.
     */
    public VPNVertragsUebersichtPanel() {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNVertragsUebersichtPanel.xml");
        createGUI();
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        cbNiederlassung = getSwingFactory().createComboBox("niederlassung", new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));

        tbMdlVPNs = new VPNVertragViewTableModel(true);
        tbVPNs = new VPNVertragTable(tbMdlVPNs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVPNs.attachSorter();
        tbVPNs.fitTable(new int[] { 80, 80, 120, 80, 120, 100, 100, 80, 80, 120 });
        tbVPNs.addKeyListener(getRefreshKeyListener());
        tbVPNs.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbVPNs.setDefaultEditor(Niederlassung.class, new AKJComboBoxCellEditor(cbNiederlassung));
        tbVPNs.setDefaultRenderer(Niederlassung.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : ((Niederlassung) value).getName());
            }
        });
        AKJScrollPane spVPNs = new AKJScrollPane(tbVPNs);
        spVPNs.setPreferredSize(new Dimension(1000, 300));

        AKJButton btnVPNEdit = getSwingFactory().createButton("edit", getActionListener());
        AKJButton btnVPNUebernahme = getSwingFactory().createButton("uebernahme", getActionListener());
        AKJButton btnVPNDetails = getSwingFactory().createButton("details", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save", getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnVPNUebernahme, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnVPNEdit, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnVPNDetails, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(spVPNs, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(btnPanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnVPNEdit, btnVPNUebernahme, btnVPNDetails, btnSave);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        setWaitCursor();
        try {
            List<Niederlassung> nls = getCCService(NiederlassungService.class).findNiederlassungen();
            cbNiederlassung.removeAllItems();
            cbNiederlassung.addItems(nls);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }

        final SwingWorker<List<VPNVertragView>, Void> worker = new SwingWorker<List<VPNVertragView>, Void>() {

            @Override
            protected List<VPNVertragView> doInBackground() throws Exception {
                VPNService service = getCCService(VPNService.class);
                List<VPNVertragView> vpns = service.findVPNVertraege();
                return vpns;
            }

            @Override
            protected void done() {
                try {
                    List<VPNVertragView> views = get();
                    tbMdlVPNs.setData(views);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };
        showProgressBar("lade VPNs...");
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("edit".equals(command)) {
            editVPN();
        }
        else if ("uebernahme".equals(command)) {
            vpnUebernehmen();
        }
        else if ("details".equals(command)) {
            vpnDetails();
        }
        else if ("save".equals(command)) {
            vpnSpeichern();
        }
    }

    private void vpnDetails() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<VPNVertragView> tm = (AKMutableTableModel<VPNVertragView>) tbVPNs.getModel();
        Object selection = tm.getDataAtRow(tbVPNs.getSelectedRow());
        if (selection instanceof VPNVertragView) {
            showVPNDetails((VPNVertragView) selection);
        }
    }

    private void vpnSpeichern() {
        final Set<VPNVertragView> modified = tbMdlVPNs.getModified();
        final SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                VPNService vpnService = getCCService(VPNService.class);
                for (VPNVertragView view : modified) {
                    VPN vpn = vpnService.findVPN(view.getId());
                    Niederlassung niederlassung = view.getNiederlassung();
                    vpn.setNiederlassungId(niederlassung == null ? null : niederlassung.getId());
                    vpn.setQos(view.getQos());
                    vpn.setProjektleiter(view.getProjektleiter());
                    vpn.setSalesRep(view.getSalesRep());
                    vpn.setBemerkung(view.getBemerkung());
                    vpnService.saveVPN(vpn);
                }
                return modified.size();
            }

            @Override
            protected void done() {
                try {
                    get();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                    tbMdlVPNs.removeAll();
                    readModel();
                }
            }
        };
        showProgressBar("speichere VPNs...");
        worker.execute();
    }

    /* Oeffnet einen Dialog, um den selektierten VPN-Auftrag zu editieren. */
    private void editVPN() {
        VPNVertragView view = null;

        @SuppressWarnings("unchecked")
        AKMutableTableModel<VPNVertragView> tm = (AKMutableTableModel<VPNVertragView>) tbVPNs.getModel();
        Object selection = tm.getDataAtRow(tbVPNs.getSelectedRow());
        if (selection instanceof VPNVertragView) {
            view = (VPNVertragView) selection;
            VPNEditDialog dlg = new VPNEditDialog(view.getId());
            Object result = DialogHelper.showDialog(this, dlg, true, true);
            if ((result instanceof Integer) && (((Integer) result).intValue() == VPNEditDialog.OK_OPTION)) {
                refresh();
            }
        }
    }

    /* Oeffnet einen Dialog, ueber den ein VPN-Auftrag aus dem Billing-System
     * ins Hurrican uebernommen werden kann.
     */
    private void vpnUebernehmen() {
        try {
            setWaitCursor();
            List<BVPNAuftragView> vpns = getUnusedBillingVPNs();
            VPNUebernahmeDialog dlg = new VPNUebernahmeDialog(vpns);
            Object result = DialogHelper.showDialog(this, dlg, true, true);
            if (result instanceof BVPNAuftragView) {
                // VPN anlegen
                BVPNAuftragView vpnView = (BVPNAuftragView) result;
                VPN vpn = new VPN();
                vpn.setVpnNr(vpnView.getAuftragNoOrig());
                vpn.setKundeNo(vpnView.getKundeNo());
                vpn.setDatum(new Date());
                vpn.setProjektleiter("");

                VPNService vpnService = getCCService(VPNService.class);
                vpnService.saveVPN(vpn);
                readModel();
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

    /* Gibt eine Liste mit allen VPN-Auftraegen zurueck, die noch NICHT in Hurrican eingetragen sind. */
    private List<BVPNAuftragView> getUnusedBillingVPNs() throws ServiceNotFoundException, FindException {
        Collection<VPNVertragView> ccVPNs = tbMdlVPNs.getData();
        BillingAuftragService bas = getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
        List<BVPNAuftragView> bVPNs = bas.findVPNAuftraege();
        if (bVPNs != null) {
            List<BVPNAuftragView> result = new ArrayList<>();
            for (BVPNAuftragView billingVPN : bVPNs) {
                if (ccVPNs != null) {
                    boolean vpnInCC = false;
                    for (Iterator<VPNVertragView> iter = ccVPNs.iterator(); iter.hasNext(); ) {
                        VPNVertragView ccView = iter.next();
                        if (NumberTools.equal(ccView.getVpnNr(), billingVPN.getAuftragNoOrig())) {
                            vpnInCC = true;
                            break;
                        }
                    }

                    if (!vpnInCC) {
                        result.add(billingVPN);
                    }
                }
                else {
                    result.add(billingVPN);
                }
            }

            return result;
        }

        return null;
    }

    /* Zeigt die Details zu einem VPN-Auftrag in einem eigenen Frame an. */
    private void showVPNDetails(VPNVertragView vpnView) {
        AbstractMDIMainFrame mainFrame = getMainFrame();
        AKJInternalFrame[] frames =
                mainFrame.findInternalFrames(VPNAuftragFrame.class);
        VPNAuftragFrame vpnFrame = null;
        if ((frames != null) && (frames.length == 1)) {
            vpnFrame = (VPNAuftragFrame) frames[0];
            mainFrame.activateInternalFrame(vpnFrame.getUniqueName());
        }
        else {
            vpnFrame = new VPNAuftragFrame(vpnView);
            mainFrame.registerFrame(vpnFrame, false);
        }

        vpnFrame.setModel(vpnView);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractDataPanel#refresh()
     */
    @Override
    protected void refresh() {
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof VPNVertragView) {
            showVPNDetails((VPNVertragView) selection);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
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

}


