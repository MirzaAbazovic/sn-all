/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;

/**
 * Created by glinkjo on 23.02.2015.
 */
public abstract class AbstractIaLevelTreeNode extends DynamicTreeNode {

    private static final long serialVersionUID = 2861621176129899378L;

    public AbstractIaLevelTreeNode(DynamicTree tree, boolean allowsChildren) {
        super(tree, allowsChildren);
    }

    public abstract IaLevel1 getIaLevel1OfCurrentNode();

}
