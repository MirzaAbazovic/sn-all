package com.bitconex.danalyst.funmodmap.gui.editor.treeview;

import com.bitconex.danalyst.funmodmap.gui.commons.ImageHelper;
import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.StandardFuncModelNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;

public class FuncModeTreeViewModel {
    private final static double SIZE_IMAGE=16;
    private FuncModelNode funcModelNode;
    private ObjectProperty<ImageView> nodeImageProperty = new SimpleObjectProperty<>(this, "nodeImage");

    public FuncModeTreeViewModel(FuncModelNode funcModelNode) {
        this.funcModelNode = funcModelNode;
        //Set Image initial
        setNodeImage(
                new ImageView(ImageHelper.createImage(funcModelNode.getType().getImgPath(), SIZE_IMAGE, SIZE_IMAGE )));

        //Change image by changes of type
        ((StandardFuncModelNode) this.funcModelNode).typeProperty().addListener((observableValue, oldVal, newVal) -> {
            setNodeImage(new ImageView(ImageHelper.createImage(newVal.getImgPath(), SIZE_IMAGE, SIZE_IMAGE )));
        });
    }

    public ObjectProperty<ImageView> nodeImageProperty() {return this.nodeImageProperty;}

    public ImageView getNodeImage() {return this.nodeImageProperty.get();}
    public void setNodeImage(ImageView nodeImage) {this.nodeImageProperty.set(nodeImage);}

    public FuncModelNode getFuncModelNode() {
        return funcModelNode;
    }

    @Override
    public String toString() {
        return funcModelNode.getName();
    }
}
