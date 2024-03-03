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
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5;

/**
 * TreeNode zur Darstellung der IA-Level3
 */
public class IaLevel5TreeNode extends AbstractIaLevelTreeNode {

    private static final long serialVersionUID = -9178232060144981684L;

    private final IaLevel5 iaLevel5;

    public IaLevel5TreeNode(DynamicTree tree, IaLevel5 iaLevel5) {
        super(tree, false);
        this.iaLevel5 = iaLevel5;
        setUserObject(iaLevel5);
    }

    @Override
    public IaLevel1 getIaLevel1OfCurrentNode() {
        return (IaLevel1) ((DynamicTreeNode) getParent().getParent()).getUserObject();
    }

    public IaLevel3 getIaLevel3OfCurrentNode() {
        return (IaLevel3) ((DynamicTreeNode) getParent()).getUserObject();
    }

    @Override
    public String getDisplayName() {
        return iaLevel5.getName();
    }

    @Override
    public String getIcon() {
        return "de/augustakom/hurrican/gui/images/number5.png";
    }
}
