package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

import javafx.event.EventTarget;

public interface NodeSelectionDetector {
    SelectableNode getSelectedNode(EventTarget eventTarget);
}
