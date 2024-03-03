package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actiongroup;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

public class ActionGroup extends Group {
    private final static double ELEMENT_SIZE = 16;
    List<ActionGroupItem> actionButtonList = new ArrayList<>();
  /*
    private DoubleProperty xProperty = new SimpleDoubleProperty(this, "x");
    private DoubleProperty yProperty = new SimpleDoubleProperty(this, "y");
*/
    private ActionGroup(ActionGroupItem ... actionGroupItems) {
        if(actionGroupItems != null) {
            for(ActionGroupItem item: actionGroupItems) {
                addItem(item);
            }
        }
    }
/*
    public DoubleProperty xProperty() {return xProperty;}
    public DoubleProperty yProperty() {return yProperty;}

    public Double getX() {this.xProperty.get();}
    public void setX(Double x) {this.xProperty.set(x);}

    public Double getY() {this.yProperty.get();}
    public void setY(Double y) {this.yProperty.set(y);}
*/
    public void addItem(ActionGroupItem newItem) {
        DoubleProperty refXProperty;
        DoubleProperty refYProperty;
        if(actionButtonList.isEmpty()) {
            refXProperty = this.layoutXProperty();
            refYProperty = this.layoutYProperty();
        } else {
            refXProperty = actionButtonList.get(actionButtonList.size()-1).getNodeXProperty();
            refYProperty = actionButtonList.get(actionButtonList.size()-1).getNodeYProperty();
        }
        newItem.getNodeXProperty().bind(refXProperty);
        newItem.getNodeYProperty().bind(refYProperty.add(ELEMENT_SIZE));
    }
}
