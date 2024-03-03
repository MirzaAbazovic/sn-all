package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class RemoveNodeAction extends NodeAction {

    private String nodeId;

    public RemoveNodeAction(TreeActionListener actionListener, String nodeId) {
        super(actionListener);
        this.nodeId = nodeId;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.removeNode(nodeId);
    }
}
