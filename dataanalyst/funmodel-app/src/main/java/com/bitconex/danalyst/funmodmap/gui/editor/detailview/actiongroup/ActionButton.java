package com.bitconex.danalyst.funmodmap.gui.editor.detailview.actiongroup;

import com.bitconex.danalyst.funmodmap.gui.commons.ImageHelper;
import com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions.NodeAction;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class ActionButton extends ImageView implements ActionGroupItem {
    private double size;
    private String imageClasspathUri;
    private NodeAction action;

    public ActionButton(String imageURI, NodeAction action) {
        this(imageURI, action, 16);
    }

    public ActionButton(String imageURI, NodeAction action, double size) {
        this.action = action;
        this.size = size;
        imageClasspathUri = imageURI;
        setImageStandardSize();
        hoverProperty().addListener((observableValue, oldVal, newVal) -> {
            if(newVal) {
                setImageOnFocusSize();
            } else {
                setImageStandardSize();
            }
        } );

        setOnMouseClicked(mouseEvent -> executeAction());
    }

    private void executeAction() {
        action.executeAction();
    };


    private void setImageStandardSize() {
        setImage(createImage(imageClasspathUri, size, size));
    }

    private void setImageOnFocusSize() {
        double newSize = size + (10 * size/100);
        setImage(createImage(imageClasspathUri, newSize, newSize));
    }

    private Image createImage(String classPathURI, double width, double height) {
        return ImageHelper.createImage(classPathURI, width, height);
    }

    @Override
    public Node getItemNode() {
        return this;
    }
}
