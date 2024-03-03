/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2004 10:26:33
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.auftrag.actions.PrintOnlineAction;
import de.augustakom.hurrican.gui.auftrag.shared.IntAccountTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel fuer die Darstellung der Internet-Daten (Accounts, EMails, Domains, IPs etc).
 *
 *
 */
public class InternetPanel extends AbstractDataPanel implements AKModelOwner, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(InternetPanel.class);

    private static final String PRINT_ONLINE = "print.online";
    private static final String ALLOCATE_NEW_IP = "allocate.new.ip";
    static final String DEALLOCATE_IP = "deallocate.ip";
    private static final String CREATE_ACCOUNT = "create.account";
    private static final String EDIT_ACCOUNT = "edit.account";
    private static final String ADD_ACCOUNT = "add.account";
    private static final long serialVersionUID = -824160153359340787L;

    // Services
    private ProduktService produktService;
    private AccountService accountService;
    private CCAuftragService auftragService;

    // GUI-Komponenten
    private AKJTextField tfVWAccount;
    private AKJButton btnPrint;
    private AKJTable tbAccounts;
    private IntAccountTableModel tbMdlAccounts;
    private AKJButton btnCreateAccount;
    private AKJButton btnEditAccount;
    private AKJButton btnAddAccount;
    private AKJTabbedPane tpDetails;

    // Manageable Components
    private final List<AKManageableComponent> managedComponents = new ArrayList<>();

    // Sub-Panels
    private DomainPanel domainPanel;
    private IPPanel ipPanel;
    private IpRoutePanel ipRoutePanel;
    private AKJButton btnAllocateNewIp;
    private AKJButton btnDeallocateIp;

    // Modelle
    private CCAuftragModel model;
    private IntAccount vwAccount;


    /**
     * Konstruktor.
     */
    public InternetPanel() {
        super("de/augustakom/hurrican/gui/auftrag/internet/resources/InternetPanel.xml");

        try {
            produktService = getCCService(ProduktService.class);
            accountService = getCCService(AccountService.class);
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
        AKJLabel lblVWAccount = getSwingFactory().createLabel("verwaltungs.account");
        tfVWAccount = getSwingFactory().createTextField("verwaltungs.account", false);
        btnPrint = getSwingFactory().createButton(PRINT_ONLINE, getActionListener());
        AKJPanel topPanel = new AKJPanel(new GridBagLayout());
        topPanel.add(lblVWAccount, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        topPanel.add(tfVWAccount, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 20, 2, 2)));
        topPanel.add(btnPrint, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        topPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 20, 2, 2)));

        tbMdlAccounts = new IntAccountTableModel();
        tbAccounts = new AKJTable(tbMdlAccounts, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAccounts.attachSorter();
        tbAccounts.addTableListener(this);
        tbAccounts.fitTable(new int[] { 100, 120, 100, 45, 80 });
        AKJScrollPane spAccounts = new AKJScrollPane(tbAccounts);
        spAccounts.setPreferredSize(new Dimension(480, 90));

        btnCreateAccount = getSwingFactory().createButton(CREATE_ACCOUNT, getActionListener(), null);
        btnAddAccount = getSwingFactory().createButton(ADD_ACCOUNT, getActionListener(), null);
        btnEditAccount = getSwingFactory().createButton(EDIT_ACCOUNT, getActionListener(), null);
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnCreateAccount, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnAddAccount, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnEditAccount, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        ChangeDNAction changeDNAction = new ChangeDNAction();
        changeDNAction.setParentClass(this.getClass());
        tbAccounts.addPopupAction(changeDNAction);

        ShowPasswordAction showPWAction = new ShowPasswordAction();
        showPWAction.setParentClass(this.getClass());
        tbAccounts.addPopupAction(showPWAction);

        AKJPanel tbPanel = new AKJPanel(new BorderLayout());
        tbPanel.setBorder(BorderFactory.createTitledBorder("Accounts"));
        tbPanel.add(btnPnl, BorderLayout.WEST);
        tbPanel.add(spAccounts, BorderLayout.CENTER);

        domainPanel = new DomainPanel();

        btnAllocateNewIp = getSwingFactory().createButton(ALLOCATE_NEW_IP, getActionListener(), null);
        btnDeallocateIp = getSwingFactory().createButton(DEALLOCATE_IP, getActionListener(), null);

        AKJPanel panelIps = new AKJPanel(new GridBagLayout(), "IPs");
        createIPPanel(panelIps);

        ipRoutePanel = new IpRoutePanel();

        tpDetails = new AKJTabbedPane();
        tpDetails.addTab("IP-Adressen", panelIps);
        tpDetails.addTab("IP Routen", ipRoutePanel);
        tpDetails.addTab("Domains", domainPanel);

        AKJPanel upperPanel = new AKJPanel(new GridBagLayout());
        upperPanel.add(topPanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        upperPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        upperPanel.add(tbPanel, GBCFactory.createGBC(100, 0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(upperPanel, BorderLayout.NORTH);
        this.add(tpDetails, BorderLayout.CENTER);

        managedComponents.add(btnPrint);
        managedComponents.add(changeDNAction);
        managedComponents.add(showPWAction);
        managedComponents.add(btnAddAccount);
        managedComponents.add(btnEditAccount);
        manageGUI(managedComponents.toArray(new AKManageableComponent[managedComponents.size()]));
    }

    private void createIPPanel(AKJPanel panelIps) {
        ipPanel = new IPPanel();
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        //@formatter:off
        btnPnl.add(btnAllocateNewIp     ,GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnDeallocateIp      ,GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel()       ,GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        panelIps.add(btnPnl             ,GBCFactory.createGBC(  0,100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        panelIps.add(ipPanel            ,GBCFactory.createGBC(100,100, 1, 0, 1, 2, GridBagConstraints.BOTH));
       //@formatter:on

        managedComponents.add(btnAllocateNewIp);
        managedComponents.add(btnDeallocateIp);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof CCAuftragModel) ? (CCAuftragModel) model : null;
        domainPanel.setModel(model);
        ipPanel.setModel(model);
        ipRoutePanel.setModel(model);
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {

    }

    private static class WorkerResult {
        private List<AccountArt> accountArten;
        private List<IntAccount> intAccounts;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        clear();
        if (this.model != null) {
            setWaitCursor();
            enableGuiElements(false);
            final SwingWorker<WorkerResult, Void> worker = new SwingWorker<WorkerResult, Void>() {
                final CCAuftragModel modelInput = model;

                @Override
                protected WorkerResult doInBackground() throws Exception {
                    WorkerResult wResult = new WorkerResult();
                    // Accounts laden
                    wResult.accountArten = accountService.findAccountArten();
                    wResult.intAccounts = accountService.findIntAccounts4Auftrag(modelInput.getAuftragId());
                    return wResult;
                }

                @Override
                protected void done() {
                    try {
                        WorkerResult wResult = get();

                        // sonstige Accounts setzen
                        tbMdlAccounts.setAccountArten(wResult.accountArten);
                        tbMdlAccounts.setData(wResult.intAccounts);
                        enableGuiElements(true);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        showValues();
                        validateButtons();
                        setDefaultCursor();
                    }
                }

            };
            worker.execute();
        }
    }

    private void enableGuiElements(boolean enable) {
        btnAddAccount.setEnabled(enable);
        btnAllocateNewIp.setEnabled(enable);
        btnDeallocateIp.setEnabled(enable);
        btnCreateAccount.setEnabled(enable);
        btnEditAccount.setEnabled(enable);
        btnPrint.setEnabled(enable);
        tpDetails.setVisible(enable);
        if (enable) {
            manageGUI(managedComponents.toArray(new AKManageableComponent[managedComponents.size()]));
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        // Panel nur fuer Anzeige!!!
        MessageHelper.showInfoDialog(this,
                "Internet-Daten werden nur von IT-Services eingetragen. Diese Maske dient nur zur Anzeige.",
                null, true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return model;
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
        switch (command) {
            case PRINT_ONLINE:
                PrintOnlineAction action = new PrintOnlineAction();
                action.putValue(PrintOnlineAction.MODEL_OWNER, this);
                action.actionPerformed(new ActionEvent(this, 0, "print.account"));
                break;
            case ALLOCATE_NEW_IP:
                allocateNewIp();
                break;
            case DEALLOCATE_IP:
                deallocateIPs();
                break;
            case CREATE_ACCOUNT:
                createAccount();
                break;
            case EDIT_ACCOUNT:
                editAccount();
                break;
            case ADD_ACCOUNT:
                addAccount();
                break;
            default:
                break;
        }
    }

    private void editAccount() {
        try {
            IntAccount selectedAccount = getSelectedAccount();
            if (selectedAccount == null) {
                throw new HurricanGUIException("Es wurde kein Account ausgewählt.");
            }
            if (!selectedAccount.isEinwahlaccount()) {
                throw new HurricanGUIException("Es dürfen nur Einwahlaccounts bearbeitet werden.");
            }
            IntAccountDialog dialog = new IntAccountDialog(selectedAccount);
            Object result = DialogHelper.showDialog(getMainFrame(), dialog, true, true);

            if (result instanceof IntAccount) {
                IntAccount accountInput = (IntAccount) result;
                IntAccount foundAccount = accountService.findIntAccount(accountInput.getAccount());
                if ((foundAccount == null) || selectedAccount.getId().equals(foundAccount.getId())) {
                    selectedAccount.setAccount(accountInput.getAccount());
                    selectedAccount.setPasswort(accountInput.getPasswort());
                    accountService.saveIntAccount(selectedAccount, false);

                    AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(model.getAuftragId());
                    auftragTechnik.setIntAccountId(selectedAccount.getId());
                    auftragService.saveAuftragTechnik(auftragTechnik, false);
                    readModel();
                }
                else {
                    throw new HurricanGUIException("Der Account is bereits vergeben. Die Account-Namen müssen allerdings eindeutig sein.");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Reserviert eine neue IP-Adresse */
    private void allocateNewIp() {
        if (model != null) {
            try {
                AssignIPAddressDialog dlg = new AssignIPAddressDialog(model);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Integer && result.equals(JOptionPane.OK_OPTION)) {
                    ipPanel.readModel();
                    ipRoutePanel.readModel();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    private void deallocateIPs() {
        ipPanel.deAllocateSelectedIps();
    }

    /*
     * Generiert einen neuen Account fuer den Auftrag.
     * Voraussetzungen:
     *  - Auftrag hat noch keinen Account
     *  - Produkt hat einen Account-Prefix definiert
     */
    private void createAccount() {
        try {
            if (tbMdlAccounts.getRowCount() > 0) {
                throw new HurricanGUIException("Dem Auftrag ist bereits ein Account zugeordnet!");
            }

            Produkt produkt = produktService.findProdukt4Auftrag(model.getAuftragId());
            if ((produkt == null) || StringUtils.isBlank(produkt.getAccountVorsatz())) {
                throw new HurricanGUIException("Für das Produkt konnte kein Account-Prefix ermittelt werden!");
            }

            AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(model.getAuftragId());
            accountService.createIntAccount(auftragTechnik, produkt.getAccountVorsatz(), IntAccount.LINR_EINWAHLACCOUNT_KONFIG);

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
     * Manuell einen Account anlegen
     * Voraussetzungen:
     *  - Auftrag hat noch keinen Account
     * Bei Eingabe eines bereits existierenden Accounts wird gefragt ob dieser
     * zugeordnet werden soll, sonst wird nichts gemacht.
     */
    private void addAccount() {
        try {
            if (tbMdlAccounts.getRowCount() > 0) {
                throw new HurricanGUIException("Dem Auftrag ist bereits ein Account zugeordnet!");
            }

            IntAccount account = new IntAccount();
            account.setLiNr(IntAccount.LINR_EINWAHLACCOUNT_KONFIG);
            account.setGueltigVon(new Date());
            account.setGueltigBis(DateTools.getHurricanEndDate());

            IntAccountDialog dialog = new IntAccountDialog(account);
            Object result = DialogHelper.showDialog(getMainFrame(), dialog, true, true);

            if (result instanceof IntAccount) {
                account = (IntAccount) result;
                IntAccount foundAccount = accountService.findIntAccount(account.getAccount());
                if (foundAccount == null) {
                    accountService.saveIntAccount(account, false);
                    AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(model.getAuftragId());
                    auftragTechnik.setIntAccountId(account.getId());
                    auftragService.saveAuftragTechnik(auftragTechnik, false);
                }
                else {
                    int opt = MessageHelper.showYesNoQuestion(this,
                            "Es gibt bereits einen Account mit diesem Namen. Soll dieser dem Auftrag zugeordnet werden?"
                            , "Zuordnen?");

                    if (opt == JOptionPane.YES_OPTION) {
                        AuftragTechnik auftragTechnik = auftragService.findAuftragTechnikByAuftragId(model.getAuftragId());
                        auftragTechnik.setIntAccountId(foundAccount.getId());
                        auftragService.saveAuftragTechnik(auftragTechnik, false);
                    }
                }
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Zeigt die aktuellen Daten an. */
    private void showValues() {
        if (vwAccount != null) {
            tfVWAccount.setText(vwAccount.getAccount());
        }
    }

    /* 'Loescht' alle Anzeigen. */
    private void clear() {
        tbMdlAccounts.setData(null);
        tfVWAccount.setText("");
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    private void validateButtons() {
        boolean enabled = true;
        if (model == null) {
            enabled = false;
        }

        btnAllocateNewIp.setEnabled(enabled);
        btnDeallocateIp.setEnabled(enabled);
        btnCreateAccount.setEnabled(enabled);
        btnAddAccount.setEnabled(enabled);
        btnEditAccount.setEnabled(enabled);
    }

    /* Gibt den Account zurueck, der in der Tabelle selektiert ist. */
    private IntAccount getSelectedAccount() {
        @SuppressWarnings("unchecked")
        AKMutableTableModel<IntAccount> model = (AKMutableTableModel<IntAccount>) tbAccounts.getModel();
        return model.getDataAtRow(tbAccounts.getSelectedRow());
    }

    /*
     * Action, um das Passwort eines Einwahlaccounts
     * in einem Dialog anzuzeigen.
     */
    class ShowPasswordAction extends AKAbstractAction {
        private static final long serialVersionUID = -2629103433214465011L;
        private IntAccount selectedAccount = null;

        /**
         * Default-Konstruktor.
         */
        public ShowPasswordAction() {
            super();
            setName("Passwort anzeigen");
            setActionCommand("show.password");
            setTooltip("Zeigt das Passwort des Einwahlaccounts in einem Dialog an.");
        }

        /**
         * @see javax.swing.AbstractAction#isEnabled()
         */
        @Override
        public boolean isEnabled() {
            selectedAccount = getSelectedAccount();
            return selectedAccount != null && selectedAccount.isEinwahlaccount() && super.isEnabled();
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled() && (selectedAccount != null)) {
                StringBuilder sb = new StringBuilder(
                        getSwingFactory().getText("show.password.msg", new Object[] { selectedAccount.getAccount() }));
                sb.append(SystemUtils.LINE_SEPARATOR);
                sb.append(SystemUtils.LINE_SEPARATOR);
                sb.append(selectedAccount.getPasswort());

                MessageHelper.showInfoDialog(getMainFrame(), sb.toString());
            }
        }
    }

    /*
     * Action, um die Rufnummer des aktuell selektierten Accounts zu aendern.
     */
    class ChangeDNAction extends AKAbstractAction {
        private static final long serialVersionUID = -1669816832893423815L;
        private IntAccount selectedAccount = null;

        /**
         * Default-Const.
         */
        public ChangeDNAction() {
            super();
            setName("Rufnummer ändern");
            setActionCommand("change.dn4account");
            putValue(Action.SHORT_DESCRIPTION, "Ändert die Rufnummer für den aktuellen Account");
        }

        /**
         * @see javax.swing.AbstractAction#isEnabled()
         */
        @Override
        public boolean isEnabled() {
            selectedAccount = getSelectedAccount();
            return selectedAccount != null && NumberTools.equal(selectedAccount.getLiNr(), IntAccount.LINR_EINWAHLACCOUNT) && super.isEnabled();
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled() && (selectedAccount != null)) {
                Long kundeNoOrig = (getModel() instanceof KundenModel)
                        ? ((KundenModel) getModel()).getKundeNo() : null;

                ChangeDN4AccountDialog dlg =
                        new ChangeDN4AccountDialog(kundeNoOrig, model.getAuftragId(), selectedAccount);
                DialogHelper.showDialog(getMainFrame(), dlg, true, true);

                tbMdlAccounts.fireTableDataChanged();
            }
        }
    }
}


