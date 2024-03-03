/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:14:07
 */
package de.augustakom.authentication.gui.tree;

import java.util.*;

import de.augustakom.authentication.gui.exceptions.TreeException;

/**
 * Service-Implementierung, um alle Children des Root-Knotens zu laden.
 */
public class RootTreeService extends AbstractTreeService<AbstractTreeGroup, Object> {

    private List<AbstractTreeGroup> treeGroups = null;

    @Override
    public List<AbstractTreeGroup> getChildren(Object parent, Object filter) throws TreeException {
        return treeGroups;
    }

    @Override
    protected void fillNode(AdminTreeNode nodeToFill, Object data) throws TreeException {
    }

    /**
     * Setzt eine Liste von Modellen, die von <code>AbstractTreeGroup</code> ableiten. Diese Objekte werden unterhalb
     * des Root-Knotens im Baum dargestellt.
     *
     * @param treeGroups Liste von TreeGroups
     */
    public void setTreeGroups(List<AbstractTreeGroup> treeGroups) {
        this.treeGroups = treeGroups;
    }
}
