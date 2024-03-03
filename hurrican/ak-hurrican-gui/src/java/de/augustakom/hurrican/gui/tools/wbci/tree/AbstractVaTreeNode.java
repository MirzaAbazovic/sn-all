/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.mnet.wbci.model.IOType;

/**
 *
 */
public abstract class AbstractVaTreeNode extends DynamicTreeNode {

    private static final long serialVersionUID = -287743684067888445L;

    protected static final String INCOMING_ICON = IMAGE_BASE + "right.gif";
    protected static final String OUTGOING_ICON = IMAGE_BASE + "left.gif";

    public AbstractVaTreeNode(DynamicTree tree, boolean allowsChildren) {
        super(tree, true);
    }

    protected abstract IOType getIoType();

    @Override
    public String getIcon() {
        if (IOType.IN.equals(getIoType())) {
            return INCOMING_ICON;
        }
        return OUTGOING_ICON;
    }

}
