package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class ChangeChildIndexAction extends NodeAction {
    private int newIndex;
    String nodeId;
    public ChangeChildIndexAction(TreeActionListener treeActionListener, String nodeId, int newIdx ) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newIndex = newIdx;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.changeIndexPosition(nodeId, newIndex);
    }
}
