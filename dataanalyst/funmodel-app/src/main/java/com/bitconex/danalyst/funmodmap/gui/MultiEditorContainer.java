package com.bitconex.danalyst.funmodmap.gui;

import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundaryImpl;
import com.bitconex.danalyst.funmodmap.gui.editor.FunModTreeEditor;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class MultiEditorContainer extends TabPane {
    public FunModTreeEditor openNewEditor(FuncModelTree modelTree) {
        FunModTreeEditor treeEditor = new FunModTreeEditor(modelTree);
        Tab newTab = new Tab(modelTree.getName(), treeEditor);
        treeEditor.isChangedProperty().addListener((observableValue, oldVal, newVal) -> {
            if(newVal) {
                newTab.setText(modelTree.getName() + " *");
            } else {
                newTab.setText(modelTree.getName());
            }
        });
        getTabs().add(newTab);
        getSelectionModel().select(newTab);
        return treeEditor;
    }

    public FunModTreeEditor getActiveEditor() {
        Tab selectedTab = getSelectionModel().getSelectedItem();
        return (FunModTreeEditor) selectedTab.getContent();
    }

    public void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Function model files", "*." + FunModTreeEditor.FUN_MODEL_FILE_EXTENSION));
        fileChooser.setTitle("Load Functional Model");
        File treeFile = fileChooser.showOpenDialog(getScene().getWindow());
        if(treeFile != null) {
            FuncModelTree loadedTree = FuncModelBoundaryImpl.getInstance().loadTree(treeFile);
            loadedTree.setName(treeFile.getName());
            openNewEditor(loadedTree);
        }
    }
}
