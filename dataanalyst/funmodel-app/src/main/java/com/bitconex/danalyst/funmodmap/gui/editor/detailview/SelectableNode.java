package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

/**
 * This interface is based on jfxtras-labs
 * <a href="https://github.com/JFXtras/jfxtras-labs/blob/8.0/src/main/java/jfxtras/labs/scene/control/window/SelectableNode.java">SelectableNode</a>
 */
public interface SelectableNode {
    boolean requestSelection(boolean select);

    void notifySelection(boolean select);
}