package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;
import javafx.application.Platform;

public abstract class NodeAction{
    private TreeActionListener treeActionListener;

    public NodeAction(TreeActionListener treeActionListener) {
        this.treeActionListener = treeActionListener;
    }

    abstract void doExecute(TreeActionListener treeActionListener);

    public final void executeAction() {
        Platform.runLater(() -> doExecute(treeActionListener));
    }


}
