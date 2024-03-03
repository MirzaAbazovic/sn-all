package com.bitconex.danalyst.funmodmap.model;


import javafx.scene.paint.Color;

import java.util.List;
public interface FuncModelNode{
    String getId();
    String getIndex();
    Double getValue();
    void setValue(Double value);
    NodeType getType();
    void setType(NodeType type);
    String getName();
    void setName(String name);
    Color getColor();
    void setColor(Color newColor);
    FuncModelNode getParent();
    List<FuncModelNode> getChildNodes();
    void addChildNode(FuncModelNode node);
    void removeChildNode(String nodeId);
    FuncModelNode findNode(String nodeId);
    void moveToParent(FuncModelNode parent);
    void moveChildPosition(int oldIdx, int newIdx);
    FuncModelNode cloneNode();
}
