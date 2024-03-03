package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class MoveNodeAction extends NodeAction {
    private String newParentId;
    String nodeId;
    public MoveNodeAction(TreeActionListener treeActionListener, String nodeId, String newParentId) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newParentId = newParentId;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.moveNode(nodeId, newParentId);
    }
}
