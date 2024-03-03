/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 13:54:54
 */
package de.augustakom.authentication.gui.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAccountService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKJTree;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;
import de.augustakom.common.tools.lang.WildcardTools;


/**
 * Panel zur Darstellung des Trees und der Filter-Optionen.
 */
public class AdminTreePanel extends AKJAbstractPanel {

    private static final Logger LOGGER = Logger.getLogger(AdminTreePanel.class);
    private static final String RESOURCE = "de/augustakom/authentication/gui/tree/resources/AdminTreePanel.xml";

    private static final String FILTER = "filter";
    private static final String FILTER_ALL = "filter.all";
    private static final String FILTER_ROLE = "filter.role";
    private static final String FILTER_ACCOUNT = "filter.account";

    private static final String SUCHE_LABEL = "suche.label";
    private static final String SUCHE_TEXT = "suche.textfield";
    private static final String SUCHE_BUTTON = "suche.button";
    private static final String SUCHE_COMBOBOX = "suche.combobox";


    private AKJTree tree = null;
    private AKJDefaultMutableTreeNode rootNode = null;
    private AdminTreeModel treeModel = null;

    private AKJComboBox cobFilter = null;

    private JSplitPane splitPane = null;

    private List<AKRole> roles = null;
    private List<AKAccount> accounts = null;


    private AKJTextField txtSuche = null;
    private AKJComboBox cobSuche = null;
    private List<MyAKUser> users = null;


    /**
     * Standardkonstruktor.
     */
    public AdminTreePanel() {
        super(RESOURCE, new BorderLayout());
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        GUISystemRegistry.instance().setValue(GUISystemRegistry.REGKEY_TREE_PANEL, this);

        AKJLabel lblFilter = getSwingFactory().createLabel(FILTER);
        cobFilter = getSwingFactory().createComboBox(FILTER);
        cobFilter.setRenderer(new FilterListCellRenderer());
        AKJButton btnFilter = getSwingFactory().createButton(FILTER, getActionListener());

        AKJRadioButton rbFilterAll = getSwingFactory().createRadioButton(FILTER_ALL, getActionListener(), true);
        rbFilterAll.setBackground(Color.white);
        AKJRadioButton rbFilterRole = getSwingFactory().createRadioButton(FILTER_ROLE, getActionListener(), false);
        rbFilterRole.setBackground(Color.white);
        AKJRadioButton rbFilterAccount = getSwingFactory().createRadioButton(FILTER_ACCOUNT, getActionListener(), false);
        rbFilterAccount.setBackground(Color.white);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbFilterAll);
        bg.add(rbFilterAccount);
        bg.add(rbFilterRole);

        AKJPanel filterPanel = new AKJPanel(new GridBagLayout());
        filterPanel.setBackground(Color.white);
        filterPanel.add(lblFilter, GBCFactory.createGBC(0, 0, 0, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(getFillPanel(), GBCFactory.createGBC(10, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(rbFilterAll, GBCFactory.createGBC(100, 0, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(getFillPanel(), GBCFactory.createGBC(40, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(rbFilterRole, GBCFactory.createGBC(100, 0, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(rbFilterAccount, GBCFactory.createGBC(100, 0, 1, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(cobFilter, GBCFactory.createGBC(0, 0, 1, 4, 3, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(getFillPanel(), GBCFactory.createGBC(10, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(btnFilter, GBCFactory.createGBC(0, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(getFillPanel(), GBCFactory.createGBC(10, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblSuche = getSwingFactory().createLabel(SUCHE_LABEL);
        txtSuche = getSwingFactory().createTextField(SUCHE_TEXT);
        txtSuche.addActionListener(getActionListener());
        AKJButton btnSuche = getSwingFactory().createButton(SUCHE_BUTTON, getActionListener());
        cobSuche = getSwingFactory().createComboBox(SUCHE_COMBOBOX);
        cobSuche.addActionListener(getActionListener());
        AKJPanel suchPanel = new AKJPanel(new GridBagLayout());

        suchPanel.setBackground(Color.white);
        suchPanel.add(lblSuche, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        suchPanel.add(txtSuche, GBCFactory.createGBC(50, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        suchPanel.add(btnSuche, GBCFactory.createGBC(100, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        suchPanel.add(cobSuche, GBCFactory.createGBC(100, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("tab.suche"), suchPanel);
        tabbedPane.addTab(getSwingFactory().getText("tab.filter"), filterPanel);

        createTree();

        splitPane = new JSplitPane();
        splitPane.setDividerSize(2);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setLeftComponent(new AKJScrollPane(tree));
        splitPane.setRightComponent(new AKJScrollPane(tabbedPane));
        this.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Kann aufgerufen werden, um dem Frame mitzuteilen, dass es angezeigt wird.
     */
    public void panelsIsShown() {
        if (splitPane != null) {
            splitPane.setDividerLocation(0.75d);
            cobFilter.setPreferredSize(cobFilter.getSize());
        }
    }

    /* Erzeugt den Tree */
    private void createTree() {
        createTreeModel();

        tree = new AKJTree(treeModel);
        tree.setModel(treeModel);
        GUISystemRegistry.instance().setValue(GUISystemRegistry.REGKEY_TREE, tree);

        tree.addTreeWillExpandListener(new AdminTreeWillExpandListener());
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new AdminTreeRenderer());
        tree.addMouseListener(new AdminTreeMouseListener());
        tree.addKeyListener(new RefreshTreeKeyListener());

        DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setSelectionModel(selectionModel);

        expandRoot();

        loadAllUsers();
    }

    /* Erzeugt das TreeModel */
    private void createTreeModel() {
        rootNode = new AKJDefaultMutableTreeNode("AugustaKom");
        rootNode.setLeaf(false);
        rootNode.setText("AugustaKom");

        treeModel = new AdminTreeModel(rootNode);
        GUISystemRegistry.instance().setValue(GUISystemRegistry.REGKEY_TREE_MODEL, treeModel);
    }

    /**
     * Aktualisiert den Tree. <br> Achtung: Das gesamte TreeModel wird durch diese Methode neu aufgebaut!
     */
    public void refreshTree() {
        createTreeModel();
        tree.setModel(treeModel);
        expandRoot();
    }

    /* Expandiert den Root-Knoten des Trees. */
    private void expandRoot() {
        try {
            tree.fireTreeWillExpand(new TreePath(rootNode.getPath()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        try {
            setWaitCursor();
            if (FILTER.equals(command)) {
                doFilter();
            }
            else if (FILTER_ALL.equals(command)) {
                cleanFilter();
            }
            else if (FILTER_ROLE.equals(command)) {
                showRoles();
            }
            else if (FILTER_ACCOUNT.equals(command)) {
                showAccounts();
            }
            else if (SUCHE_BUTTON.equals(command) ||
                    SUCHE_TEXT.equals(command)) {
                findUsers();
            }
            else if (SUCHE_COMBOBOX.equals(command)) {
                selectUsers();
            }
        }
        finally {
            setDefaultCursor();
        }
    }


    private void loadAllUsers() {
        AKJDefaultMutableTreeNode deptNode = (AKJDefaultMutableTreeNode) rootNode.getChildAt(0);
        tree.expandPath(new TreePath(deptNode.getPath()));
        for (Enumeration<?> e = deptNode.children(); e.hasMoreElements(); ) {

            AKJDefaultMutableTreeNode next = (AKJDefaultMutableTreeNode) e.nextElement();
            treeModel.loadChildren(new TreePath(next.getPath()));

        }
        tree.collapsePath(new TreePath(deptNode.getPath()));
    }

    private void findUsers() {
        tree.collapsePath(new TreePath(((AKJDefaultMutableTreeNode) rootNode.getChildAt(0)).getPath()));
        cobSuche.removeAll();
        findUserByName(txtSuche.getText(), rootNode.getChildAt(0));
        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        cobSuche.copyList2Model(users, mdl);
        cobSuche.setModel(mdl);
    }


    private void selectUsers() {
        tree.collapsePath(new TreePath(((AKJDefaultMutableTreeNode) rootNode.getChildAt(0)).getPath()));
        if (cobSuche.getSelectedItem() instanceof MyAKUser) {
            MyAKUser selObj = (MyAKUser) cobSuche.getSelectedItem();
            TreePath tp = new TreePath(selObj.toNode().getPath());
            tree.expandPath(tp);
            tree.setSelectionPath(tp);
            tree.scrollPathToVisible(tp);
        }
    }


    public void findUserByName(String searchText, TreeNode startNode) {
        int idx = 0;
        users = new ArrayList<>();
        if (StringUtils.isEmpty(searchText)) {
            return; // nothing to search
        }
        for (int i = 0; i < startNode.getChildCount(); i++) {
            AKJDefaultMutableTreeNode deptNode = (AKJDefaultMutableTreeNode) startNode.getChildAt(i);
            if (deptNode.getUserObject() instanceof AKDepartment) {
                AKDepartment tempDept = (AKDepartment) deptNode.getUserObject();
                TreeNode newStartnode = startNode.getChildAt(i);
                for (int j = 0; j < newStartnode.getChildCount(); j++) {
                    AKJDefaultMutableTreeNode userNode = (AKJDefaultMutableTreeNode) newStartnode.getChildAt(j);
                    if (userNode.getUserObject() instanceof AKUser) {
                        AKUser tempUser = (AKUser) userNode.getUserObject();
                        final boolean found = WildcardTools.matchIgnoreCase(tempUser.getName(), searchText);
                        if (found) {
                            users.add(idx++, new MyAKUser(userNode, tempUser.getLoginName() + " / " + tempDept.getName()));
                        }
                    }

                }

            }

        }
        // sort result
        users.sort((u1, u2) -> u1.nodeLabel.toLowerCase().compareTo(u2.nodeLabel.toLowerCase()));
    }

    static class MyAKUser {
        AKJDefaultMutableTreeNode troot = null;
        String nodeLabel = null;

        MyAKUser(AKJDefaultMutableTreeNode tmpNode, String tmpLabel) {
            this.troot = tmpNode;
            this.nodeLabel = tmpLabel;
        }

        public AKJDefaultMutableTreeNode toNode() {
            return troot;
        }

        public String toString() {
            return nodeLabel;
        }

    }


    /* Setzt den ausgewaehlten Filter und aktualisiert den Tree */
    private void doFilter() {
        UserFilter filter = null;
        if (cobFilter.getSelectedItem() instanceof AKRole) {
            filter = new UserFilter();
            filter.setRoleId(((AKRole) cobFilter.getSelectedItem()).getId());
        }
        else if (cobFilter.getSelectedItem() instanceof AKAccount) {
            filter = new UserFilter();
            filter.setAccountId(((AKAccount) cobFilter.getSelectedItem()).getId());
        }

        GUISystemRegistry.instance().setValue(GUISystemRegistry.REGKEY_TREE_FILTER, filter);
        refreshTree();
    }

    /* Entfernt alle Elemente aus der ComboBox */
    private void cleanFilter() {
        cobFilter.removeAllItems();
    }

    /* Zeigt alle AKRole-Objekte in der ComboBox an */
    private void showRoles() {
        if (roles == null) {
            loadRoles();
        }

        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        cobFilter.copyList2Model(roles, mdl);
        cobFilter.setModel(mdl);
    }

    /* Zeigt alle AKAccount-Objekte in der ComboBox an */
    private void showAccounts() {
        if (accounts == null) {
            loadAccounts();
        }

        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        cobFilter.copyList2Model(accounts, mdl);
        cobFilter.setModel(mdl);
    }

    /* Laedt alle AKRole-Objekte und speichert sie in der Variable <code>roles</code> */
    private void loadRoles() {
        try {
            Object tmp = getAuthenticationService(AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
            if (tmp != null) {
                AKRoleService rs = (AKRoleService) tmp;
                roles = rs.findAll();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            if (roles == null) {
                roles = new ArrayList<>();
            }

            AKRole emptyRole = new AKRole();
            emptyRole.setId(Long.MIN_VALUE);
            emptyRole.setName(getSwingFactory().getText("without.role"));
            roles.add(0, emptyRole);
        }
    }

    /* Laedt alle AKAccount-Objekte und speichert sie in der Variable <code>accounts</code> */
    private void loadAccounts() {
        try {
            Object tmp = getAuthenticationService(AKAuthenticationServiceNames.ACCOUNT_SERVICE, AKAccountService.class);
            if (tmp != null) {
                AKAccountService as = (AKAccountService) tmp;
                accounts = as.findAll();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            if (accounts == null) {
                accounts = new ArrayList<>();
            }

            AKAccount emptyAcc = new AKAccount();
            emptyAcc.setId(Long.MIN_VALUE);
            emptyAcc.setName(getSwingFactory().getText("without.account"));
            accounts.add(0, emptyAcc);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /* Gibt ein Panel zurueck, dass zum Auffuellen von Leerraeumen verwendet werden kann */
    private AKJPanel getFillPanel() {
        AKJPanel p = new AKJPanel();
        p.setBackground(Color.white);
        return p;
    }

    /*
     * Laedt den Service <code>serviceName</code> aus dem
     * <code>AKAuthenticationServiceLocator</code> und gibt
     * ihn zurueck. <br>
     * Ueber die Services, die diese Methode zurueck gibt, koennen
     * die Children des Trees geladen werden.
     *
     * @param serviceName Name des gesuchten Services
     * @param type Typ des gesuchten Services
     * @return Service-Objekt oder <code>null</code>
     */
    private <T> T getAuthenticationService(String serviceName, Class<T> type) throws ServiceNotFoundException {
        return ServiceLocator.instance().getService(serviceName, type);
    }

    /**
     * KeyListener fuer den Tree. <br> Der KeyListener reagiert auf die Betaetigung von 'F5' und aktualisiert dann den
     * Tree.
     */
    class RefreshTreeKeyListener extends KeyAdapter {
        /**
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F5) {
                refreshTree();
            }
        }
    }

    /**
     * Renderer fuer die Filter-ComboBox
     */
    static class FilterListCellRenderer extends DefaultListCellRenderer {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (comp instanceof JLabel) {
                if (value instanceof AKRole) {
                    ((JLabel) comp).setText(((AKRole) value).getName());
                }
                else if (value instanceof AKAccount) {
                    ((JLabel) comp).setText(((AKAccount) value).getName());
                }
            }

            return comp;
        }
    }
}
