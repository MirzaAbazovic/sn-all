/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 13:40:58
 */
package de.augustakom.hurrican.gui.base.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKParentAware;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJDialog;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.ManageGuiComponentHelper;
import de.augustakom.hurrican.gui.base.tree.actions.OpenChildsAction;


/**
 * MouseListener fuer den Dynamic-Tree. <br> <br> Durch einen Double-Click auf einen TreeNode wird das Default-Panel zu
 * dem Modell ermittelt, das dem UserObject des Nodes entspricht (ermittelt aus <code>DynamicTreeNodeModel.getModelClass</code>).
 * <br> Das Default-Panel muss vom Typ <code>AKSimpleModelOwner</code> sein. Dadurch wird dem Panel das UserObject des
 * aktuellen Nodes mit uebergeben; und dadurch auch die Modell-Klasse sowie die ID des aktuellen Objekts.
 */
public class DynamicTreeMouseListener extends MouseAdapter {

    private static final Logger LOGGER = Logger.getLogger(DynamicTreeMouseListener.class);

    protected final JComponent panelContainer;
    protected final AbstractDynamicTreeFrame frame;

    /**
     * Konstruktor fuer den MouseListener.
     *
     * @param panelContainer Angabe des Containers, in dem die Panels fuer die TreeNodes dargestellt werden sollen.
     */
    public DynamicTreeMouseListener(AbstractDynamicTreeFrame frame, JComponent panelContainer) {
        this.frame = frame;
        this.panelContainer = panelContainer;
    }


    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showContextMenu(e);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
            defaultAction(e);
        }
    }


    /**
     * Fuehrt die Standard-Aktion des Nodes fuer einen Doppelklick durch
     */
    private void defaultAction(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        try {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

            DynamicTreeNode node = null;
            Object lastPathComponent = null;
            if (selPath != null) {
                lastPathComponent = selPath.getLastPathComponent();
            }
            if ((lastPathComponent == null) || !(lastPathComponent instanceof DynamicTreeNode)) {
                LOGGER.info("Could not determine node, or node is not of type DynamicTreeNode");
                return;
            }
            else {
                node = (DynamicTreeNode) lastPathComponent;
            }
            node.defaultAction();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
        }
        finally {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }


    /**
     * Stellt ein PopupMenu mit den verfuegbaren Kontexten des TreeNode User-Objects dar.
     */
    private void showContextMenu(MouseEvent e) {
        DynamicTree tree = (DynamicTree) e.getSource();
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

        try {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (selPath != null) {
                handleTreeSelection(e, tree, selPath);

                Set<DynamicTreeNode> nodes = new HashSet<>();
                TreePath[] paths = tree.getSelectionPaths();
                if (paths != null) {
                    for (TreePath treePath : paths) {
                        nodes.add((DynamicTreeNode) treePath.getLastPathComponent());
                    }
                }

                AKJPopupMenu popup = new AKJPopupMenu();

                if (nodes.size() == 1) {
                    addActionsToPopupMenu(popup, Collections.singletonList((AKAbstractAction) new OpenChildsAction(tree, nodes.iterator().next())));

                    // Hier kann man die Node nach ihrem Kontext-Menu fragen, und zusaetzliche
                    // Node-Spezifische Kontext-Menu-Eintraege anzeigen
                    DynamicTreeNode selectedNode = nodes.iterator().next();
                    List<AKAbstractAction> nodeActionsForContextMenu = selectedNode.getNodeActionsForContextMenu();
                    if ((nodeActionsForContextMenu != null) && (!nodeActionsForContextMenu.isEmpty())) {
                        popup.addSeparator();
                        addActionsToPopupMenu(popup, nodeActionsForContextMenu);
                    }
                }

                if (popup.getComponentCount() > 0) {
                    popup.addSeparator();
                }
                addTreeSpecificContextMenuItems(nodes, popup);

                if (popup.getComponentCount() != 0) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
        }
        finally {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Fuegt die angegebenen Actions dem Popup-Menu hinzu und verrechtet die Komponenten.
     */
    protected void addActionsToPopupMenu(AKJPopupMenu popup, List<? extends AKAbstractAction> actionsToAdd) {
        if (CollectionTools.isNotEmpty(actionsToAdd)) {
            for (AKAbstractAction action : actionsToAdd) {
                JMenuItem menuItem = popup.add(action);
                if (menuItem != null) {
                    AdministrationMouseListener adminML = new AdministrationMouseListener();
                    menuItem.addMouseListener(adminML);
                    menuItem.addMenuKeyListener(adminML);
                    if (menuItem instanceof AKParentAware) {
                        ((AKParentAware) menuItem).setParentClass(frame.getClass());
                    }
                }
            }

            AKManageableComponent[] toManage = actionsToAdd.toArray(new AKManageableComponent[actionsToAdd.size()]);
            for (AKManageableComponent comp : toManage) {
                if (comp instanceof AKParentAware) {
                    ((AKParentAware) comp).setParentClass(frame.getClass());
                }
            }
            ManageGuiComponentHelper.manageGUI(frame, toManage);
        }
    }

    /**
     * Fuegt dem ContextMenu Tree-Spezifische Menu-Eintraege hinzu.
     */
    protected void addTreeSpecificContextMenuItems(Set<DynamicTreeNode> nodes, AKJPopupMenu popup) {
        // Default implementation does nothing
    }

    /**
     * Erreichen des gewohnten/erwarteten Klick- und Rechtsklick-Verhaltens.
     */
    private void handleTreeSelection(MouseEvent e, JTree tree, TreePath selPath) {
        boolean isSelected = false;
        boolean ctrlDown = false;
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null) {
            for (TreePath treePath : selectionPaths) {
                if (selPath.equals(treePath)) {
                    isSelected = true;
                }
            }
        }
        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
            ctrlDown = true;
        }

        if (!isSelected) {
            if (ctrlDown) {
                tree.addSelectionPath(selPath);
            }
            else {
                tree.setSelectionPath(selPath);
            }
        }
    }


    /**
     * Zeigt das Panel <code>panelToShow</code> in einer TabbedPane an.
     *
     * @param panelToShow anzuzeigendes Panel
     * @param title       Titel fuer das Panel bzw. den TabbedPane Reiter
     */
    public void showPanel(JPanel panelToShow, String title) {
        AKJTabbedPane tabPane = new AKJTabbedPane();
        if (tabPane.getTabCount() == 0) {
            tabPane.addTab(title, panelToShow);
        }
        else {
            tabPane.setTabComponentAt(0, panelToShow);
            tabPane.setTitleAt(0, title);
        }

        if (!tabPane.isShowing()) {
            if (panelContainer instanceof JScrollPane) {
                ((JScrollPane) panelContainer).setViewportView(tabPane);
            }
            else {
                panelContainer.add(tabPane);
            }
        }
    }

    /**
     * Abstrakte Action-Klasse, ueber die die GUI-Elemente aus TreeGUIConfig geoeffnet werden koennen. <br> Panels
     * werden in einer TabbedPane auf dem <code>container4Panels</code> angezeigt. GUI-Elemente vom Typ 'Dialog' werden
     * ueber den DialogHelper angezeigt.
     */
    class DynamicTreePopupAction extends AKAbstractAction {
        private static final long serialVersionUID = -2865523410009442687L;
        private final Class<?> guiClass;

        /**
         * Konstruktor mit Angabe des zugehoerigen TreeGUIConfig-Objekts
         */
        public DynamicTreePopupAction(String title, String iconUrl, String actionCommand, Class<?> guiClass) {
            setName(title);
            setActionCommand(actionCommand);
            setIcon(iconUrl);
            this.guiClass = guiClass;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object guiInstance = guiClass.newInstance();

                if ((guiInstance instanceof AKSimpleModelOwner) && (e.getSource() instanceof Observable)) {
                    // Uebergibt dem Panel den aktuellen Knoten
                    ((AKSimpleModelOwner) guiInstance).setModel((Observable) e.getSource());
                }
                else {
                    throw new HurricanGUIException("Defined GUI class is not of type AKSimpleModelOwner!");
                }

                if (guiInstance instanceof JPanel) {
                    showPanel((JPanel) guiInstance, getName());
                }
                else if (guiInstance instanceof AKJDialog) {
                    DialogHelper.showDialog(null, (AKJDialog) guiInstance, true, true);
                }
                else if (guiInstance instanceof AKJOptionDialog) {
                    DialogHelper.showDialog(null, (AKJOptionDialog) guiInstance, true, true);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                MessageHelper.showErrorDialog(
                        (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), ex);
            }
        }
    }
}
