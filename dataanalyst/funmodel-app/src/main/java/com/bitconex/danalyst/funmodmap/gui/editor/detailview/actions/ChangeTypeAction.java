package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;
import com.bitconex.danalyst.funmodmap.model.NodeType;

public class ChangeTypeAction extends NodeAction {
    private NodeType newType;
    String nodeId;
    public ChangeTypeAction(TreeActionListener treeActionListener, String nodeId, NodeType type) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newType = type;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.changeType(nodeId, newType);
    }
}
