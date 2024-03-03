package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions;

import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;
import javafx.scene.paint.Color;

public class ChangeColorAction extends NodeAction {
    private Color newColor;
    String nodeId;
    public ChangeColorAction(TreeActionListener treeActionListener, String nodeId, Color color) {
        super(treeActionListener);
        this.nodeId = nodeId;
        this.newColor = color;
    }

    @Override
    void doExecute(TreeActionListener treeActionListener) {
        treeActionListener.changeColor(nodeId, newColor);
    }
}
