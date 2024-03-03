/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3;

/**
 * TreeNode zur Darstellung der IA-Level3
 */
public class IaLevel3TreeNode extends AbstractIaLevelTreeNode {

    private static final long serialVersionUID = -9178232060144981684L;

    private final IaLevel3 iaLevel3;

    public IaLevel3TreeNode(DynamicTree tree, IaLevel3 iaLevel3) {
        super(tree, true);
        this.iaLevel3 = iaLevel3;
        setUserObject(iaLevel3);
    }

    @Override
    public List<DynamicTreeNode> doLoadChildren() throws Exception {
        if (CollectionUtils.isEmpty(iaLevel3.getLevel5s())) {
            return Collections.emptyList();
        }

        return iaLevel3.getLevel5s()
                .stream()
                .map(i -> new IaLevel5TreeNode(getTree(), i))
                .collect(Collectors.toList());
    }

    @Override
    public IaLevel1 getIaLevel1OfCurrentNode() {
        return (IaLevel1) ((DynamicTreeNode) getParent()).getUserObject();
    }

    @Override
    public String getDisplayName() {
        return iaLevel3.getName();
    }

    @Override
    public String getIcon() {
        return "de/augustakom/hurrican/gui/images/number3.png";
    }
}
