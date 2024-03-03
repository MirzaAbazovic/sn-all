/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2004 09:03:33
 */
package de.augustakom.authentication.gui.role;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.AKGUIComponent;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKGUIService;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKParentAware;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDialog;
import de.augustakom.common.gui.swing.AKJFrame;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * Dialog, um die Rollenrechte fuer eine best. GUI-Komponente anzuzeigen und zu editieren.
 */
public class RoleRightsDialog extends AKJAbstractOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(RoleRightsDialog.class);

    private static final String CMD_SAVE = "save";
    private static final String CMD_DELETE = "delete";
    private static final String CMD_CANCEL = "cancel";

    private AKManageableComponent componentToManage = null;
    private AKGUIComponent guiComponent = null;
    private Long applicationId = null;

    private AKGUIService guiService = null;

    private AKJTextField tfName = null;
    private AKJTextField tfParent = null;
    private AKJTextField tfType = null;
    private AKJTextArea taDesc = null;
    private AKJTable tbRoleRights = null;
    private AKJButton btnDelete = null;

    /* HashMap mit Objekten des Typs AKCompBehavior. Als Key wird die Role-ID verwendet. */
    private Map<Long, AKCompBehavior> compBehaviors = null;
    private List<AKRole> roles = null;

    /**
     * Konstruktor mit Angabe der Komponente, deren Rollenrechte angezeigt/editiert werden sollen.
     */
    public RoleRightsDialog(AKManageableComponent componentToManage, Long applicationId) {
        super("de/augustakom/authentication/gui/role/resources/RoleRightsDialog.xml");
        this.componentToManage = componentToManage;
        this.applicationId = applicationId;

        if (this.componentToManage == null) {
            throw new IllegalArgumentException("Es wurde keine GUI-Komponente definiert, die geprueft werden soll!");
        }

        if (this.applicationId == null) {
            throw new IllegalArgumentException("Um die GUI-Komponente zu ueberpruefen, wird eine Application-ID benoetigt!");
        }

        init();

        search4Component();
        createGUI();
    }

    /* Initialisiert den Dialog */
    private void init() {
        try {
            guiService = ServiceLocator.instance().getService(AKAuthenticationServiceNames.GUI_SERVICE, AKGUIService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(null, e);
            this.setValue(RoleRightsDialog.CANCEL_OPTION);
        }

        if (!this.componentToManage.isManagementCalled()) {
            String msg = getSwingFactory().getText("comp.not.managed");
            String title = getSwingFactory().getText("comp.not.managed.title");
            MessageHelper.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        String title = getSwingFactory().getText("title");
        if (guiComponent != null) {
            title += " " + guiComponent.getName() + " (ID: " + guiComponent.getId() + ")";
        }
        else {
            title += " " + componentToManage.getComponentName() + " (neu)";
        }
        setTitle(title);
        setIconURL(getSwingFactory().getText("icon"));

        AKJLabel lblName = getSwingFactory().createLabel("comp.name");
        AKJLabel lblParent = getSwingFactory().createLabel("comp.parent");
        AKJLabel lblType = getSwingFactory().createLabel("comp.type");
        AKJLabel lblDesc = getSwingFactory().createLabel("comp.description");

        tfName = getSwingFactory().createTextField("comp.name");
        tfName.setEditable(false);
        tfParent = getSwingFactory().createTextField("comp.parent");
        tfType = getSwingFactory().createTextField("comp.type");
        taDesc = getSwingFactory().createTextArea("comp.description");
        AKJScrollPane spDesc = new AKJScrollPane(taDesc);
        spDesc.setPreferredSize(new Dimension(150, 70));

        AKJButton btnSave = getSwingFactory().createButton(CMD_SAVE, getActionListener());
        btnDelete = getSwingFactory().createButton(CMD_DELETE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(CMD_CANCEL, getActionListener());

        tbRoleRights = new AKJTable();
        tbRoleRights.setDefaultRenderer(AKRole.class, new RoleRightsTableCellRenderer());
        tbRoleRights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbRoleRights.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        AKJScrollPane spTable = new AKJScrollPane(tbRoleRights);
        spTable.setPreferredSize(new Dimension(550, 200));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblParent, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfParent, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblType, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfType, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblDesc, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(spDesc, GBCFactory.createGBC(100, 100, 2, 0, 1, 3, GridBagConstraints.BOTH));

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnDelete, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(right, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(spTable, GBCFactory.createGBC(100, 100, 0, 1, 3, 1, GridBagConstraints.BOTH));
        getChildPanel().add(btnPanel, GBCFactory.createGBC(100, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));

        read();
        fitTable();
        validateCommands();
    }

    /* Passt die Spaltenbreiten der Tabelle an. */
    private void fitTable() {
        TableColumn column = null;
        for (int i = 0; i < tbRoleRights.getColumnCount(); i++) {
            column = tbRoleRights.getColumnModel().getColumn(i);
            if ((i >= RoleRightsTableModel.COL_VISIBLE) && (i <= RoleRightsTableModel.COL_EXECUTABLE)) {
                column.setPreferredWidth(100);
            }
            else {
                column.setPreferredWidth(200);
            }
        }
    }

    /* Validiert die Buttons des Dialogs */
    private void validateCommands() {
        if ((guiComponent == null) || (guiComponent.getId() == null)) {
            btnDelete.setEnabled(false);
        }
    }

    /* Liest alle Daten ein und stellt sie in den entsprechenden Fields dar. */
    private void read() {
        if (guiComponent != null) {
            tfName.setText(guiComponent.getName());
            tfParent.setText(guiComponent.getParent());
            tfType.setText(guiComponent.getType());
            taDesc.setText(guiComponent.getDescription());
        }
        else {
            tfName.setText(componentToManage.getComponentName());
            if (componentToManage instanceof AKJMenuItem) {
                tfParent.setEditable(false);
            }

            if ((componentToManage instanceof AKParentAware) &&
                    StringUtils.isNotBlank(((AKParentAware) componentToManage).getParentClassName())) {
                tfParent.setText(((AKParentAware) componentToManage).getParentClassName());
            }
            else if (componentToManage instanceof Component) {
                Component parent = ((Component) componentToManage).getParent();
                while (parent != null) {
                    if ((parent instanceof AKJAbstractPanel) || (parent instanceof AKJInternalFrame) ||
                            (parent instanceof AKJFrame) || (parent instanceof AKJDialog) || (parent instanceof AKJAbstractOptionDialog)) {
                        tfParent.setText(parent.getClass().getName());
                        break;
                    }
                    if (parent.getParent() == null) {
                        tfParent.setText(parent.getClass().getName());
                    }
                    parent = parent.getParent();
                }
            }
        }

        // Roles und zugehoerige RoleRights laden
        loadRoles();
    }

    /* Laedt alle fuer die Applikation verfuegbaren Rollen */
    private void loadRoles() {
        try {
            AKRoleService roleService =
                    ServiceLocator.instance().getService(AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
            List<AKRole> tmpRoles = roleService.findByApplication(applicationId);

            // Rollen mit Admin-Rechten werden ausgefiltert.
            roles = new ArrayList<AKRole>();
            if (tmpRoles != null) {
                for (int i = 0; i < tmpRoles.size(); i++) {
                    if (!(tmpRoles.get(i)).isAdmin()) {
                        roles.add(tmpRoles.get(i));
                    }
                }
            }

            loadBehaviors4Roles();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
     * Laedt die Komponenten-Verhaltensweisen fuer die Rollen und speichert
     * sie in einer HashMap. Als Key wird die Role-ID verwendet.
     */
    private void loadBehaviors4Roles() {
        compBehaviors = new HashMap<Long, AKCompBehavior>();
        if ((roles != null) && (guiService != null)) {
            for (int i = 0; i < roles.size(); i++) {
                AKCompBehavior behavior = guiService.findBehavior4Role(roles.get(i), guiComponent);
                if (behavior != null) {
                    compBehaviors.put(behavior.getRoleId(), behavior);
                }
            }
        }

        RoleRightsTableModel tableModel = new RoleRightsTableModel(roles, compBehaviors);
        tbRoleRights.setModel(tableModel);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CMD_SAVE.equals(command)) {
            doSave();
        }
        else if (CMD_DELETE.equals(command)) {
            doDelete();
        }
        else if (CMD_CANCEL.equals(command)) {
            setValue(RoleRightsDialog.CANCEL_OPTION);
        }
    }

    /* Speichert die vorgenommenen Einstellungen. */
    private void doSave() {
        if (guiService != null) {
            try {
                // GUI-Komponente speichern
                if (guiComponent == null) {
                    guiComponent = new AKGUIComponent();
                }
                guiComponent.setName(tfName.getText());
                guiComponent.setParent(tfParent.getText());
                guiComponent.setType(tfType.getText());
                guiComponent.setDescription(taDesc.getText());
                guiComponent.setApplicationId(applicationId);

                guiService.saveGUIComponent(guiComponent);

                // Rollenrechte fuer GUI-Komponente speichern
                Collection<AKCompBehavior> behaviors = compBehaviors.values();
                Iterator<AKCompBehavior> it = behaviors.iterator();
                while (it.hasNext()) {
                    AKCompBehavior cb = it.next();
                    if (cb.getComponentId() == null) {
                        cb.setComponentId(guiComponent.getId());
                    }
                    guiService.saveComponentBehavior(cb);
                }

                setValue(RoleRightsDialog.OK_OPTION);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /* Loescht die GUI-Komponente aus der Datenbank. */
    private void doDelete() {
        try {
            String title = getSwingFactory().getText("delete.component.title");
            String msg = getSwingFactory().getText("delete.component.msg");

            int selection = MessageHelper.showConfirmDialog(this, msg, title,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (selection == JOptionPane.YES_OPTION) {
                guiService.deleteGUIComponent(guiComponent);
                setValue(RoleRightsDialog.OK_OPTION);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /*
     * Ueberprueft, ob die Komponente in der Datenbank eingetragen ist und
     * dadurch die Rollenrechte angepasst werden koennen.
     */
    private void search4Component() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Suche nach DB-Eintrag fuer Komponente mit Namen: " + componentToManage.getComponentName());
            LOGGER.debug("............Applikations-ID: " + applicationId);
        }

        if (componentToManage instanceof Component) {
            if (componentToManage instanceof AKParentAware) {
                String parentClass = ((AKParentAware) componentToManage).getParentClassName();
                guiComponent = guiService.findGUIComponent(componentToManage.getComponentName(), parentClass, applicationId);
            }

            if (guiComponent == null) {
                Component parent = ((Component) componentToManage).getParent();
                while (parent != null) {
                    String parentClass = parent.getClass().getName();

                    LOGGER.debug("............Parent-Class: " + parentClass);
                    guiComponent = guiService.findGUIComponent(
                            componentToManage.getComponentName(), parentClass, applicationId);
                    if (guiComponent != null) {
                        break;
                    }

                    parent = parent.getParent();
                }
            }
        }
    }

    /**
     * TableCellRenderer fuer die Darstellung von Role-Objekten.
     */
    class RoleRightsTableCellRenderer extends DefaultTableCellRenderer {
        /**
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
         * boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof AKRole) {
                AKRole role = (AKRole) value;
                ((JLabel) c).setText(role.getName());

                if (!isConfigured(role.getId())) {
                    if (isSelected) {
                        ((JLabel) c).setForeground(Color.orange);
                    }
                    else {
                        ((JLabel) c).setForeground(Color.red);
                    }
                }
                else {
                    if (isSelected) {
                        ((JLabel) c).setForeground(Color.white);
                    }
                    else {
                        ((JLabel) c).setForeground(Color.black);
                    }
                }

                if (column == 0) {
                    ((JLabel) c).setToolTipText(getHtmlTooltip(role.getDescription()));
                }
            }

            return c;
        }

        /* Ueberprueft, ob die Komponente fuer eine best. Rolle konfiguriert ist. */
        private boolean isConfigured(Long roleId) {
            if (compBehaviors != null) {
                AKCompBehavior behavior = compBehaviors.get(roleId);
                if (behavior != null) {
                    return true;
                }
            }
            return false;
        }

        /* Gibt den String als HTML zurueck. */
        private String getHtmlTooltip(String tooltip) {
            return "<html>" + tooltip + "</html>";
        }
    }
}


