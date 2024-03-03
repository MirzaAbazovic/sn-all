package com.bitconex.danalyst.funmodmap.gui.editor.treeview;

import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.bitconex.danalyst.funmodmap.model.StandardFuncModelNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.Optional;

public class FunModTreeView extends TreeView<FuncModeTreeViewModel> {

    private ObjectProperty<FuncModelTree> funcModelTreeProperty =
            new SimpleObjectProperty<>(this, "funcModelTree");


    public FunModTreeView() {
        super();
        funcModelTreeProperty.addListener((observableValue, funcModelTree, t1) -> initTree());
    }

    public ObjectProperty<FuncModelTree> funcModelTreeProperty() {return this.funcModelTreeProperty;}
    public FuncModelTree getFuncModelTree() {return funcModelTreeProperty.get();}
    public void setFuncModelTree(FuncModelTree tree){funcModelTreeProperty.set(tree);}
    public void expandTreeNode(String nodeId) {
        expandTreeNode(getRoot(), nodeId);
    }

    private boolean expandTreeNode(TreeItem<FuncModeTreeViewModel> item, String nodeId) {
        if(item.getValue().getFuncModelNode().getId().equals(nodeId)) {
            return true;
        } else {
            boolean anyFound = item.getChildren().stream().anyMatch(i -> expandTreeNode(i, nodeId));
            if(anyFound) {
                item.setExpanded(true);
            }
            return anyFound;
        }
    }

    private void initTree() {
        TreeItem<FuncModeTreeViewModel> rootNode = createTreeItemFromBusinessModel(null, getFuncModelTree().getRootNode());
        setRoot(rootNode);
    }

    private TreeItem<FuncModeTreeViewModel> createTreeItemFromBusinessModel(TreeItem<FuncModeTreeViewModel> parentTreeItem, FuncModelNode node) {
        FuncModeTreeViewModel treeViewModel = new FuncModeTreeViewModel(node);
        TreeItem<FuncModeTreeViewModel> treeItem = new TreeItem<>(treeViewModel);
        treeItem.graphicProperty().bind(treeViewModel.nodeImageProperty());
        ((StandardFuncModelNode) node).nameProperty().addListener((observableValue, oldVal, newVal) -> {
            treeItem.setValue(new FuncModeTreeViewModel(node));
        }); //to change Label
        if(parentTreeItem!=null) {
            parentTreeItem.getChildren().add(treeItem);
        }
        StandardFuncModelNode standardNode = (StandardFuncModelNode) node;
        //Add listener to the busines model. Extend GUI-Tree model when new nodes are added to the business model
        standardNode.childListProperty().addListener(new ListChangeListener<FuncModelNode>() {
            @Override
            public void onChanged(Change<? extends FuncModelNode> change) {
                while (change.next()) {
                    if(change.wasAdded()) {
                        for(FuncModelNode addedNode : change.getAddedSubList()) {
                            createTreeItemFromBusinessModel(treeItem, addedNode);
                        }
                    } else if (change.wasRemoved()) {
                        for(FuncModelNode node : change.getRemoved()) {
                            removeItem(node.getId());
                        }
                    }
                }
            }});
        node.getChildNodes().stream().forEach(funcModelNode -> createTreeItemFromBusinessModel(treeItem, funcModelNode));
        return parentTreeItem==null ? treeItem : parentTreeItem;
    }

    private void removeItem(String modelNodeId) {
        TreeItem<FuncModeTreeViewModel> rootItem = this.getRoot();
        if(rootItem != null ) {
            removeChild(rootItem, modelNodeId);
        }
    }

    private void removeChild(TreeItem<FuncModeTreeViewModel> parent, String childNodeId) {
        Optional<TreeItem<FuncModeTreeViewModel>> childOpt = parent.getChildren().stream().filter(
                item -> item.getValue().getFuncModelNode().getId().equals(childNodeId)).findFirst();
        childOpt.ifPresentOrElse(
                child -> parent.getChildren().remove(child),
                ()-> parent.getChildren().stream().forEach(item -> removeChild(item, childNodeId))
        );
    }
}
