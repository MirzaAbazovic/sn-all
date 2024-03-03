/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2011 18:56:35
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wita.model.IoArchive;

class IoArchiveTreeModel implements TreeModel {

    private final IoArchive ROOT = new IoArchive();
    private final List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree;

    public IoArchiveTreeModel(List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree) {
        this.ioArchiveTree = ioArchiveTree;
    }

    @Override
    public Object getRoot() {
        return ROOT;
    }

    private Pair<IoArchive, List<IoArchive>> findByKey(Object node) {
        IoArchive ioArchive = (IoArchive) node;
        for (Pair<IoArchive, List<IoArchive>> current : ioArchiveTree) {
            if (current.getFirst().equals(ioArchive)) { return current; }
        }
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (ROOT.equals(parent)) { return ioArchiveTree.get(index).getFirst(); }
        Pair<IoArchive, List<IoArchive>> node = findByKey(parent);
        if (node != null) { return node.getSecond().get(index); }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (ROOT.equals(parent)) { return ioArchiveTree.size(); }
        Pair<IoArchive, List<IoArchive>> node = findByKey(parent);
        if (node != null) { return node.getSecond().size(); }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (ROOT.equals(node)) { return CollectionTools.isEmpty(ioArchiveTree); }
        Pair<IoArchive, List<IoArchive>> current = findByKey(node);
        return current == null || CollectionTools.isEmpty(current.getSecond());
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (ROOT.equals(parent)) {
            int index = 0;
            for (Pair<IoArchive, List<IoArchive>> current : ioArchiveTree) {
                if (current.getFirst().equals(child)) { return index; }
                index++;
            }
        }

        Pair<IoArchive, List<IoArchive>> parrentIo = findByKey(parent);
        if (parrentIo != null) { return parrentIo.getSecond().indexOf(child); }
        return 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // do nothing
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // do nothing
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // do nothing
    }


}
