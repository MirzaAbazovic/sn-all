/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 09:16:37
 */
package de.augustakom.authentication.gui.tree;

import javax.swing.tree.*;

import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;


/**
 * Interface zur Definition von Methoden fuer ein TreeModel des Admin-Trees.
 */
public interface IAdminTreeModel {

    /**
     * Laedt die Children fuer ein bestimmtes Objekt nach und ordnet die Nodes dem Parent hinzu.
     */
    public void loadChildren(TreePath parent);

    /**
     * Sucht nach einem TreeNode, dessen User-Object dem definierten Typ (<code>type</code>) und dessen Methode
     * <code>getId()</code> den Wert <code>id</code> besitzt.
     *
     * @param type     Typ des User-Objects
     * @param id       ID des Objekts
     * @param idMethod Methode auf dem Objekt, um die ID zu erhalten.
     * @return gefundener TreeNode oder <code>null</code>
     */
    public AKJDefaultMutableTreeNode findNode(Class<?> type, Object id, String idMethod);

    /**
     * Verschiebt den TreeNode <code>nodeToMove</code> nach <code>newParent</code>.
     *
     * @param newParent  neuer Parent-Node
     * @param nodeToMove Node, der verschoben werden soll
     */
    public void moveNode(AKJDefaultMutableTreeNode newParent, AKJDefaultMutableTreeNode nodeToMove);

    /**
     * Aktuallisiert das User Objekt einer {@code AdminTreeNode}
     */
    public void updateUserObject4Node(AdminTreeNode toUpdate, Object userObject);
}
