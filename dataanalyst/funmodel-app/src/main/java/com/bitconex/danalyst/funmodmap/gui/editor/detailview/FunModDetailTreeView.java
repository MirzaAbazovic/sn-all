package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelRoot;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.bitconex.danalyst.funmodmap.model.StandardFuncModelNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Optional;


public class FunModDetailTreeView extends ScrollPane {

    private Pane contentGroup = new Pane();
    private TreeActionListener treeActionListener;

    private ObjectProperty<FuncModelTree> funcModelTreeProperty =
            new SimpleObjectProperty<>(this, "funcModelTree");



    public FunModDetailTreeView(TreeActionListener actionListener) {
        super();
        this.treeActionListener = actionListener;
        // Beim neuem Setzen der Model-Property soll das Tree neu aufgebaut werden
        funcModelTreeProperty.addListener((observableValue, funcModelTree, t1) -> initTree());
        setContent(contentGroup);
        // Selection Handler initilisieren
        NodeSelectionDetector nodeSelectionDetector = eventTarget -> {
            SelectableNode selectableNode = null;
            TreeNode rootNode = getRootNode();
            if(eventTarget instanceof Rectangle && rootNode != null) {
                selectableNode = rootNode.getTreeNodeByRect((Rectangle) eventTarget);
            }
            return selectableNode;
        };
        SelectionHandler selectionHandler = new SelectionHandler(contentGroup, nodeSelectionDetector);
        contentGroup.addEventFilter(MouseEvent.MOUSE_CLICKED, selectionHandler.getMousePressedEventHandler());
    }

    public ObjectProperty<FuncModelTree> funcModelTreeProperty() {return this.funcModelTreeProperty;}
    public FuncModelTree getFuncModelTree() {return funcModelTreeProperty.get();}
    public void setFuncModelTree(FuncModelTree tree){funcModelTreeProperty.set(tree);}

    public void selectItem(String nodeId) {
        TreeNode selectedNode = findNodeById(nodeId);
        if(selectedNode != null) {
            expandParent(selectedNode);
            scrollAndMakeNodeVisible(selectedNode);
        }
    }

    private TreeNode getRootNode() {
        TreeNode ret = null;
        FuncModelRoot rootModel = funcModelTreeProperty.get().getRootNode();
        if(rootModel != null) {
            ret = findNodeByName(rootModel.getName());
        }
        return ret;
    }

    private TreeNode findNodeByName(String name) {
        Optional<Node> nodeOpt = contentGroup.getChildren().stream().
                filter(node -> (node instanceof TreeNode && ((TreeNode)node).getName().equals(name))).findFirst();
         return nodeOpt.isPresent() ? (TreeNode) nodeOpt.get() : null;
    }

    private TreeNode findNodeById(String id) {
        Optional<Node> nodeOpt = contentGroup.getChildren().stream().
                filter(node -> (node instanceof TreeNode && ((TreeNode)node).getNodeId().equals(id))).findFirst();
        return nodeOpt.isPresent() ? (TreeNode) nodeOpt.get() : null;
    }

    private void expandParent(TreeNode node) {
        if(node.getParentNode() != null) {
            expandParent(node.getParentNode());
            node.getParentNode().setExpandState(true);
        }
    }

    private void scrollAndMakeNodeVisible(TreeNode treeNode) {
        double heightViewPort = getViewportBounds().getHeight();
        double heightScrollPane = getContent().getBoundsInLocal().getHeight();
        double y = treeNode.getBoundsInParent().getMaxY() - (TreeNode.HEIGHT_RECT/2);
        if (y<(heightViewPort/2)){
            setVvalue(0);
            // below 0 of scrollpane

        }else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))){
            // between 0 and 1 of scrollpane
            setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
        }
        else if(y>= (heightScrollPane-(heightViewPort/2))){
            // above 1 of scrollpane
            setVvalue(1);
        }

        double widthViewPort = getViewportBounds().getWidth();
        double widthScrollPane = getContent().getBoundsInLocal().getWidth();
        double x = treeNode.getBoundsInParent().getMaxX() - TreeNode.INIT_WIDTH_BORDER;

        if (x<(widthViewPort/2)){
            setHvalue(0);
            // below 0 of scrollpane

        }else if ((x>=(widthViewPort/2))&(x<=(widthScrollPane-widthViewPort/2))){
            // between 0 and 1 of scrollpane
            setHvalue((x-(widthViewPort/2))/(widthScrollPane-widthViewPort));
        }
        else if(x>= (widthScrollPane-(widthViewPort/2))){
            // above 1 of scrollpane
            setHvalue(1);
        }
    }

    private void initTree() {
        createTreeNodeFromBusinessItem(null, getFuncModelTree().getRootNode());
    }



    private TreeNode createTreeNodeFromBusinessItem (TreeNode parent, FuncModelNode node) {
        TreeNode treeNode;
        StandardFuncModelNode standardNode = (StandardFuncModelNode) node;
        if(parent==null) { // Root Node
            treeNode = new TreeNode(null, null, null);
        } else { // Not Root node
            treeNode = parent.addChild();
        }
        //Bind attributes from business model to gui model
        treeNode.nodeIdProperty().bind(standardNode.idProperty());
        treeNode.nameProperty().bind(standardNode.nameProperty());
        treeNode.valueProperty().bind(standardNode.valueProperty());
        treeNode.typeProperty().bind(standardNode.typeProperty());
        treeNode.indexProperty().bind(standardNode.indexProperty());
        treeNode.colorProperty().bind(standardNode.colorProperty());
        //Add new Tree-Element to the content
        contentGroup.getChildren().add(treeNode);

        //Add listener to the busines model. Extend GUI-Tree model when new nodes are added to the business model
        standardNode.childListProperty().addListener(new ListChangeListener<FuncModelNode>() {
            @Override
            public void onChanged(Change<? extends FuncModelNode> change) {
                while (change.next()) {
                    if(change.wasAdded()) {
                        for(FuncModelNode node : change.getAddedSubList()) {
                            if(node instanceof StandardFuncModelNode) {
                                StandardFuncModelNode standardFuncModelNode = (StandardFuncModelNode) node;
                                createTreeNodeFromBusinessItem(treeNode, standardFuncModelNode);
                                if(change.getTo() < treeNode.getChildList().size()) { // Item soll nicht ans Ende hinzugefügt werden
                                    treeNode.moveChildPosition(treeNode.getChildList().size()-1, change.getFrom());
                                }
                            }
                        }
                    } else if (change.wasRemoved()) {
                        for(FuncModelNode node : change.getRemoved()) {
                            treeNode.removeChild(node.getId());
                        }
                    } else if(change.wasReplaced()) {
                        int oldIndex = change.getFrom();
                        int newIndex = change.getTo();
                        treeNode.moveChildPosition(oldIndex, newIndex);
                    }
                }
            }});
        // Add action listener to the new node in GUI-Model
        treeNode.setTreeActionListener(treeActionListener);

        //Call this method recursively for each child of business model node
        node.getChildNodes().stream().forEach(funcModelNode -> createTreeNodeFromBusinessItem(treeNode, funcModelNode));
        return parent==null ? treeNode : parent;
    }
}
