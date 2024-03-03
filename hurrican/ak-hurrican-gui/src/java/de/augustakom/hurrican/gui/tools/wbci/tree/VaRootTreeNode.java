/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.mnet.wbci.model.WbciRequest;

/**
 * TreeNode fuer den Root der Vorabstimmungen.
 */
public class VaRootTreeNode extends DynamicTreeNode {

    private static final long serialVersionUID = -234905170707737795L;

    private static final String ROOT_ICON = IMAGE_BASE + "wbci.gif";

    private final List<WbciRequest> wbciRequests;

    public VaRootTreeNode(DynamicTree tree, List<WbciRequest> wbciRequests) {
        super(tree, true);
        this.wbciRequests = orderById(wbciRequests);
    }

    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();
        if (wbciRequests != null) {
            for (WbciRequest request : wbciRequests) {
                result.add(new VaRequestTreeNode(getTree(), request));
            }
        }

        return result;
    }

    @Override
    public String getDisplayName() {
        return "WBCI Nachrichten";
    }

    @Override
    public String getIcon() {
        return ROOT_ICON;
    }


    private List<WbciRequest> orderById(List<WbciRequest> wbciRequests) {
        if (wbciRequests == null) {
            return null;
        }

        List<WbciRequest> sorted = new ArrayList<>(wbciRequests);
        Collections.sort(sorted, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        return sorted;
    }

}
