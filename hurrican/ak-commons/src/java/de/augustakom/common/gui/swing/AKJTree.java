/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004
 */
package de.augustakom.common.gui.swing;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;


/**
 * AK-Implementierung eines JTrees.
 *
 *
 * @see javax.swing.JTree
 */
public class AKJTree extends JTree {

    /**
     * Erzeugt einen neuen Tree.
     */
    public AKJTree() {
        super();
    }

    /**
     * Erzeugt einen neuen Tree mit den Elementen des Arrays.
     *
     * @param value
     */
    public AKJTree(Object[] value) {
        super(value);
    }

    /**
     * Erzeugt einen neuen Tree mit den (Value-)Elementen aus der Hashtable.
     *
     * @param value
     */
    public AKJTree(Hashtable<?, ?> value) {
        super(value);
    }

    /**
     * Erzeugt einen neuen Tree mit den Elementen aus dem Vector.
     *
     * @param value
     */
    public AKJTree(Vector<?> value) {
        super(value);
    }

    /**
     * Erzeugt einen neuen Tree. <br> Die Nodes werden aus dem TableModel gelesen.
     *
     * @param newModel
     */
    public AKJTree(TreeModel newModel) {
        super(newModel);
    }

    /**
     * Erzeugt einen neuen Tree mit Angabe des Root-Knotens.
     *
     * @param root
     */
    public AKJTree(TreeNode root) {
        super(root);
    }

    /**
     * Erzeugt einen neuen Tree mit Angabe des Root-Knotens. Ueber <code>asksAllowsChildren</code> wird definiert, ob
     * Knoten auf Kinder ueberprueft werden sollen.
     *
     * @param root
     * @param asksAllowsChildren if false, any node without children is a leaf node; if true, only nodes that do not
     *                           allow children are leaf nodes
     */
    public AKJTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

}
