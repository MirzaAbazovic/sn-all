package com.bitconex.danalyst.funmodmap.gui.editor;

import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundary;
import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundaryImpl;
import com.bitconex.danalyst.funmodmap.gui.editor.detailview.FunModDetailTreeView;
import com.bitconex.danalyst.funmodmap.gui.editor.detailview.TreeActionListener;
import com.bitconex.danalyst.funmodmap.gui.editor.treeview.FunModTreeView;
import com.bitconex.danalyst.funmodmap.gui.editor.treeview.FuncModeTreeViewModel;
import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.bitconex.danalyst.funmodmap.model.NodeType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class FunModTreeEditor extends BorderPane {

    public final static String FUN_MODEL_FILE_EXTENSION = "fmd";

    private FunModTreeView navTreeView ;
    private FunModDetailTreeView funModDetailTreeViewer;
    private FuncModelBoundary funcModelBoundary =  FuncModelBoundaryImpl.getInstance();
    private FuncModelTree funcModelTree;
    private TreeActionListener treeActionListener;
    private BooleanProperty isChangedProperty = new SimpleBooleanProperty(this, "isChanged");
    private File treeFile;

    public FunModTreeEditor(FuncModelTree modelTree) {
        super();
        treeActionListener = new TreeActionListener() {
            @Override
            public void addNode(String parentNodeId) {
                funcModelBoundary.addNode(funcModelTree.getId(), parentNodeId);
                setIsChanged(true);
            }

            @Override
            public void removeNode(String nodeId) {
                funcModelBoundary.removeNode(funcModelTree.getId(), nodeId);
                setIsChanged(true);
            }

            @Override
            public void changeValue(String nodeId, Double newValue) {
                funcModelBoundary.changeNodeValue(funcModelTree.getId(), nodeId, newValue);
                setIsChanged(true);
            }

            @Override
            public void changeType(String nodeId, NodeType newType) {
                funcModelBoundary.changeNodeType(funcModelTree.getId(), nodeId, newType);
                setIsChanged(true);
            }

            @Override
            public void changeColor(String nodeId, Color newColor) {
                funcModelBoundary.changeNodeColor(funcModelTree.getId(), nodeId, newColor);
                setIsChanged(true);
            }

            @Override
            public void changeName(String nodeId, String newName) {
                funcModelBoundary.changeNodeName(funcModelTree.getId(), nodeId, newName);
                setIsChanged(true);
            }

            @Override
            public void nodeSelected(String nodeId) {
                navTreeView.expandTreeNode(nodeId);
            }

            @Override
            public void moveNode(String nodeId, String newParentId) {
                funcModelBoundary.moveNode(funcModelTree.getId(), nodeId, newParentId);
                setIsChanged(true);
            }

            @Override
            public void changeIndexPosition(String nodeId, int newIndex) {
                funcModelBoundary.changeNodePosition(funcModelTree.getId(), nodeId, newIndex);
            }
        };
        navTreeView = new FunModTreeView();
        funModDetailTreeViewer = new FunModDetailTreeView(treeActionListener);
        setLeft(navTreeView);
        setCenter(funModDetailTreeViewer);
        funcModelTree = modelTree;
        navTreeView.setFuncModelTree(funcModelTree);
        funModDetailTreeViewer.setFuncModelTree(funcModelTree);
        navTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<FuncModeTreeViewModel>>() {
            @Override
            public void changed(ObservableValue <? extends TreeItem<FuncModeTreeViewModel>> observableValue,
                                TreeItem<FuncModeTreeViewModel> oldVal, TreeItem<FuncModeTreeViewModel> nevVal) {
                FuncModelNode selectedNode = nevVal.getValue().getFuncModelNode();
                funModDetailTreeViewer.selectItem(selectedNode.getId());
            }
        });
    }

    public BooleanProperty isChangedProperty() {return isChangedProperty;}
    public boolean isChanged() {return isChangedProperty.get();}
    public void setIsChanged(boolean newVal) {isChangedProperty.set(newVal);}
    public FuncModelTree getFuncModelTree() {
        return funcModelTree;
    }

    public File getTreeFile() {
        return treeFile;
    }

    public void setTreeFile(File treeFile) {
        this.treeFile = treeFile;
    }

    public void saveTree() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Function model files", "*." + FUN_MODEL_FILE_EXTENSION));
        String fileNameSuffix = hasFunModelSuffix(funcModelTree.getName()) ? "" : "." + FUN_MODEL_FILE_EXTENSION;
        fileChooser.setInitialFileName(funcModelTree.getName() + fileNameSuffix);
        fileChooser.setTitle("Save Functional Model");
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            if (!file.getAbsolutePath().endsWith("." + FUN_MODEL_FILE_EXTENSION)) {
                file = new File(file.getAbsolutePath() + "." + FUN_MODEL_FILE_EXTENSION);
                if(file.exists()) {
                    throw new RuntimeException("File " + file.getAbsolutePath() + " exists!");
                }
            }
            // Here save file logic
            System.out.println("Target file choosen: " + file.getAbsolutePath());
            FuncModelBoundaryImpl.getInstance().saveTree(
                    funcModelTree.getId(),
                    file);
            this.setIsChanged(false);
        }
    }

    public void exportTreeToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files", "*.csv" ));
        String fileNameSuffix = hasFunModelSuffix(funcModelTree.getName()) ? "" : ".csv";
        fileChooser.setInitialFileName(funcModelTree.getName() + fileNameSuffix);
        fileChooser.setTitle("Export Functional Model to CSV-Format");
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            if (!file.getAbsolutePath().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
                if(file.exists()) {
                    throw new RuntimeException("File " + file.getAbsolutePath() + " exists!");
                }
            }
            FuncModelBoundaryImpl.getInstance().exportTreeToCSV(
                    funcModelTree.getId(),
                    file);
        }
    }

    private boolean hasFunModelSuffix(String s) {
        return s.endsWith("." + FUN_MODEL_FILE_EXTENSION);
    }
}
