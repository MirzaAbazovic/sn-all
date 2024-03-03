/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.01.2010 10:48:40
 */
package de.augustakom.hurrican.gui.base.tree.tools;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;


/**
 *
 */
public class DynamicTreeNodeComparator implements Comparator<DynamicTreeNode> {

    private final TreeSortEntry treeSortEntry;
    private final int order;

    public DynamicTreeNodeComparator(TreeSortEntry treeSortEntry) {
        this.treeSortEntry = treeSortEntry;
        this.order = treeSortEntry.getAscending() ? 1 : -1;
    }

    // unchecked: <T extends Comparable<?>>.compareTo(<T extends Comparable<?>>) impossible
    @SuppressWarnings({ "unchecked" })
    @Override
    public int compare(DynamicTreeNode n1, DynamicTreeNode n2) {
        if (treeSortEntry.getNodeType().equals(n1.getClass()) && treeSortEntry.getNodeType().equals(n2.getClass())) {
            Object o1 = treeSortEntry.getNodeProperty().getFrom(n1);
            Object o2 = treeSortEntry.getNodeProperty().getFrom(n2);

            if (o1 == null) {
                if (o2 == null) { return 0; }
                else {return 1 * order; }
            }
            else if (o2 == null) { return -1 * order; }

            if (treeSortEntry.getNodeProperty().getComparator() != null) {
                return ((Comparator) treeSortEntry.getNodeProperty().getComparator()).compare(o1, o2);
            }
            else if ((o1 instanceof Comparable<?>) && (o2 instanceof Comparable<?>)) {
                return ((Comparable) o1).compareTo(o2) * order;
            }
        }
        // If nodes are of different type, or no comparator was set and they are not comparable,
        // compare their class names... until you got a better idea.
        return n1.getClass().getSimpleName().compareTo(n2.getClass().getSimpleName());
    }
}
