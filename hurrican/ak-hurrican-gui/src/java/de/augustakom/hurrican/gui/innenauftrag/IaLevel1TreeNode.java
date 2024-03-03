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

/**
 * TreeNode zur Darstellung der IA-Level1
 */
public class IaLevel1TreeNode extends AbstractIaLevelTreeNode {

    private static final long serialVersionUID = -9178232060144981684L;

    private final IaLevel1 iaLevel1;

    public IaLevel1TreeNode(DynamicTree tree, IaLevel1 iaLevel1) {
        super(tree, true);
        this.iaLevel1 = iaLevel1;
        setUserObject(iaLevel1);
    }

    @Override
    public List<DynamicTreeNode> doLoadChildren() throws Exception {
        if (CollectionUtils.isEmpty(iaLevel1.getLevel3s())) {
            return Collections.emptyList();
        }

        return iaLevel1.getLevel3s()
                .stream()
                .map(i -> new IaLevel3TreeNode(getTree(), i))
                .collect(Collectors.toList());
    }

    @Override
    public IaLevel1 getIaLevel1OfCurrentNode() {
        return iaLevel1;
    }

    @Override
    public String getDisplayName() {
        return iaLevel1.getName();
    }

    @Override
    public String getIcon() {
        return "de/augustakom/hurrican/gui/images/number1.png";
    }
}
