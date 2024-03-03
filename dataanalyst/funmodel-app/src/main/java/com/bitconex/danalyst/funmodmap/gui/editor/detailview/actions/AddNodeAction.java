package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class AddNodeAction extends NodeAction {

    private String parentNodeId;

    public AddNodeAction(TreeActionListener actionListener, String parentNodeId) {
        super(actionListener);
        this.parentNodeId = parentNodeId;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.addNode(parentNodeId);
    }
}
