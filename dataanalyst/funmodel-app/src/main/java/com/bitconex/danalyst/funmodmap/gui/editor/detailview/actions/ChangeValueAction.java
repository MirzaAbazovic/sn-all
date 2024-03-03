package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class ChangeValueAction extends NodeAction {
    private Double newValue;
    String nodeId;
    public ChangeValueAction(TreeActionListener treeActionListener, String nodeId, Double value) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newValue = value;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.changeValue(nodeId, newValue);
    }
}
