/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import java.util.*;
import java.util.stream.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Root-Node fuer den Tree zur Administration der IA-Levels
 */
public class IaLevelRootNode extends DynamicTreeNode {

    private static final long serialVersionUID = -7887175896246475531L;

    public IaLevelRootNode(DynamicTree tree) {
        super(tree, true);
    }

    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        InnenauftragService iaService = CCServiceFinder.instance().getCCService(InnenauftragService.class);
        List<IaLevel1> iaLevel1s = iaService.findIaLevels();

        return iaLevel1s.stream()
                .map(i -> new IaLevel1TreeNode(getTree(), i))
                .collect(Collectors.toList());
    }

    @Override
    public String getDisplayName() {
        return "Ebenen";
    }

    @Override
    public String getIcon() {
        return null;
    }
}
