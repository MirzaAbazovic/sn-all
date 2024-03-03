/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 14:05:31
 */
package de.augustakom.authentication.gui.tree;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * TreeModel fuer den Admin-Tree.
 */
public class AdminTreeModel extends DefaultTreeModel implements IAdminTreeModel {

    private static final Logger LOGGER = Logger.getLogger(AdminTreeModel.class);

    private static final String SERVICENAME_ROOT = "TreeService.Root";
    private AKJDefaultMutableTreeNode searchedNode = null;

    /**
     * Konstruktor mit Angabe des Root-Knotens fuer den Tree bzw. das TreeModel.
     *
     * @param root zu verwendender Root-Knoten.
     */
    public AdminTreeModel(TreeNode root) {
        super(root);
    }

    @Override
    public void loadChildren(TreePath parent) {
        AKJDefaultMutableTreeNode parentNode = (AKJDefaultMutableTreeNode) parent.getLastPathComponent();
        try {
            if (!parentNode.isLoaded()) {
                AbstractTreeService<?, ?> treeService = null;
                if (parentNode.isRoot()) {
                    treeService = ServiceLocator.instance().getService(SERVICENAME_ROOT, AbstractTreeService.class);
                }
                else {
                    treeService = ServiceLocator.instance().getService(
                            parentNode.getUserObject().getClass().getName(), AbstractTreeService.class);
                }

                List<?> children = treeService.getChildren(
                        parentNode.getUserObject(), GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_TREE_FILTER));

                if (children != null) {
                    for (int i = 0; i < children.size(); i++) {
                        Object child = children.get(i);
                        AdminTreeNode node = new AdminTreeNode();

                        @SuppressWarnings("unchecked")
                        AbstractTreeService<Object, Object> nodeTS = ServiceLocator.instance().getService(child.getClass().getName(),
                                AbstractTreeService.class);
                        nodeTS.fillNode(node, child);

                        parentNode.add(node);
                    }
                }

                nodeStructureChanged(parentNode);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), e);
        }
        finally {
            parentNode.setLoaded(true);
        }
    }

    /**
     * @see de.augustakom.authentication.gui.tree.IAdminTreeModel#findNode(Class, Object, String)
     */
    @Override
    public AKJDefaultMutableTreeNode findNode(Class<?> type, Object id, String idMethod) {
        setSearchedNode(null);
        findNodeRecursion((AKJDefaultMutableTreeNode) getRoot(), type, id, idMethod);
        return searchedNode;
    }

    /**
     * Rekursion fuer die Methode findNode(Class, Object, String). <br> Sollte ein TreeNode gefunden werden, wird er
     * ueber die Methode <code>setSearchedNode</code> gespeichert.
     *
     * @param node     Node, der rekursiv durchsucht werden soll
     * @param type     Typ, den das User-Object des Nodes besitzen muss
     * @param id       ID des User-Objects, nach dem gesucht wird
     * @param idMethod Methodenname, um die ID des gesuchten Objekts zu ermitteln.
     */
    private void findNodeRecursion(AKJDefaultMutableTreeNode node, Class<?> type, Object id, String idMethod) {
        if ((this.searchedNode == null) && (node.getChildCount() >= 0)) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                AKJDefaultMutableTreeNode next = (AKJDefaultMutableTreeNode) e.nextElement();
                Object userObject = next.getUserObject();

                if (type.isInstance(userObject)) {
                    if ((idMethod == null) || idMethod.trim().equals("")) {
                        setSearchedNode(next);
                        break;
                    }

                    try {
                        // Nach ID-Methode suchen
                        Method m = type.getMethod(idMethod, new Class[] { });
                        if (m != null) {
                            Object objId = m.invoke(userObject, new Object[] { });
                            if (id.equals(objId)) {
                                setSearchedNode(next);
                                break;
                            }
                        }
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }

                if (searchedNode == null) {
                    findNodeRecursion(next, type, id, idMethod);
                }
            }
        }
    }

    /*
     * Setzt das Ergebnis der TreeNode-Suche. <br>
     * @param node gefundener TreeNode.
     */
    private void setSearchedNode(AKJDefaultMutableTreeNode node) {
        searchedNode = node;
    }

    /**
     * Verschiebt den TreeNode <code>nodeToMove</code> nach <code>newParent</code>.
     *
     * @param newParent  neuer Parent-Node
     * @param nodeToMove Node, der verschoben werden soll
     */
    @Override
    public void moveNode(AKJDefaultMutableTreeNode newParent, AKJDefaultMutableTreeNode nodeToMove) {
        if ((newParent != null) && (nodeToMove != null)) {
            if (nodeToMove.getParent() != null) {
                removeNodeFromParent(nodeToMove);
            }

            // Ist der Parent bereits geladen, dann wird der TreeNode eingefuegt,
            // ansonsten wird er durch das Expandieren des Knotens geladen.
            if (newParent.isLoaded()) {
                insertNodeInto(nodeToMove, newParent, newParent.getChildCount());
                nodeStructureChanged((TreeNode) getRoot());
            }

            ((JTree) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_TREE))
                    .expandPath(new TreePath(newParent.getPath()));
        }
        else {
            LOGGER.warn("Node could not be moved. New Parent or node to move is null!");
        }
    }

    @Override
    public void updateUserObject4Node(AdminTreeNode toUpdate, Object userObject) {
        try {
            @SuppressWarnings("unchecked")
            AbstractTreeService<Object, Object> nodeTS = ServiceLocator.instance().getService(
                    userObject.getClass().getName(), AbstractTreeService.class);
            nodeTS.fillNode(toUpdate, userObject);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(
                    (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), e);
        }
    }
}
