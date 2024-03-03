package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SelectionHandler {
    private Clipboard clipboard;

    private EventHandler<MouseEvent> mousePressedEventHandler;
    private NodeSelectionDetector nodeSelectionDetector;

    public SelectionHandler(final Parent root, NodeSelectionDetector nodeSelectionDetector) {
        this.nodeSelectionDetector = nodeSelectionDetector;
        this.clipboard = new Clipboard();
        this.mousePressedEventHandler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                SelectionHandler.this.doOnMousePressed(root, event);
            }
        };
    }

    public EventHandler<MouseEvent> getMousePressedEventHandler() {
        return mousePressedEventHandler;
    }

    private void doOnMousePressed(Parent root, MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY) || event.getButton().equals(MouseButton.SECONDARY)) { // Linke Maustaste
            Node target = (Node) event.getTarget();
            if(target.equals(root)) {
                clipboard.unselectAll();
            }
            SelectableNode selectableTarget = nodeSelectionDetector.getSelectedNode(event.getTarget());
            if((!event.isControlDown()) && !(event.isMetaDown())) {
                clipboard.unselectAll();
            }
            if(selectableTarget != null) {
                clipboard.select(selectableTarget);
            } else {
                clipboard.unselectAll();
            }
        }

    }
}
