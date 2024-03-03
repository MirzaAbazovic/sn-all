package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

import com.bitconex.danalyst.funmodmap.model.NodeType;
import javafx.scene.paint.Color;

public interface TreeActionListener {
    void addNode(String parentNodeId);
    void removeNode(String nodeId);
    void changeValue(String nodeId, Double newValue);
    void changeType(String nodeId, NodeType newType);
    void changeName(String nodeId, String newName);
    void changeColor(String nodeId, Color newColor);
    void nodeSelected(String nodeId);
    void moveNode(String nodeId, String newParentId);
    void changeIndexPosition(String nodeId, int newIndex);
}
