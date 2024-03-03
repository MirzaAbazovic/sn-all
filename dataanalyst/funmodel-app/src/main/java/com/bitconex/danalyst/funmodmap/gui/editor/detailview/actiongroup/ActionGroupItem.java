package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actiongroup;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;

public interface ActionGroupItem {
    Node getItemNode();
    DoubleProperty getNodeXProperty();
    DoubleProperty getNodeYProperty();
}
