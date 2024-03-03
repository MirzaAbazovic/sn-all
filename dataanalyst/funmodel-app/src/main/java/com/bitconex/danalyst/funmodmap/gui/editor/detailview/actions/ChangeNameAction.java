package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;

public class ChangeNameAction extends NodeAction {
    private String newName;
    String nodeId;
    public ChangeNameAction(TreeActionListener treeActionListener, String nodeId, String name) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newName = name;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.changeName(nodeId, newName);
    }
}
