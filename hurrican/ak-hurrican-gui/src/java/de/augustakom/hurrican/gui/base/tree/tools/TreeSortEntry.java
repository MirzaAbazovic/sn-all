/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 10:30:41
 */
package de.augustakom.hurrican.gui.base.tree.tools;

import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;


/**
 * Immutable class
 *
 *
 */
public class TreeSortEntry {
    private final Class<? extends DynamicTreeNode> nodeType;
    private final NodeProperty<?> nodeProperty;
    private final boolean ascending;

    /**
     * Default constructor - use factory method to save typing
     */
    public TreeSortEntry(Class<? extends DynamicTreeNode> nodeType, NodeProperty<?> nodeProperty, boolean ascending) {
        this.nodeType = nodeType;
        this.nodeProperty = nodeProperty;
        this.ascending = ascending;
    }

    public static <T> TreeSortEntry create(Class<? extends DynamicTreeNode> nodeType,
            NodeProperty<T> nodeProperty, boolean ascending) {
        return new TreeSortEntry(nodeType, nodeProperty, ascending);
    }


    public NodeProperty<?> getNodeProperty() {
        return nodeProperty;
    }

    public boolean getAscending() {
        return ascending;
    }

    public Class<? extends DynamicTreeNode> getNodeType() {
        return nodeType;
    }
}
