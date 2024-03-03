package com.bitconex.danalyst.funmodmap.model;

import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundaryException;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StandardFuncModelNode implements FuncModelNode {

    private final static Color DEFAULT_COLOR = Color.GHOSTWHITE;
    private StringProperty idProperty = new SimpleStringProperty(this, "id");
    private StringProperty nameProperty = new SimpleStringProperty(this, "name");
    private DoubleProperty valueProperty = new SimpleDoubleProperty(this, "value");
    private ObjectProperty<NodeType> typeProperty = new SimpleObjectProperty<>(this, "type");
    private StringProperty indexProperty = new SimpleStringProperty(this, "index");
    private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(this, "color");
    private ObjectProperty<FuncModelNode> parentProperty = new SimpleObjectProperty<>(this, "parent");
    private ListProperty<FuncModelNode> childListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());


    public StandardFuncModelNode(FuncModelNode parent,
                                  String id,
                                  String name) {
        this.parentProperty.set(parent);
        if(parent != null) {
            this.setColor(parent.getColor());
            this.setType(parent.getType());
            parent.addChildNode(this);
        } else {
            this.setColor(DEFAULT_COLOR);
            this.setType(NodeType.BULLET);
        }
        this.idProperty.set(id);
        this.nameProperty.set(name);
    }


    public StringProperty idProperty() {return idProperty;};

    public StringProperty nameProperty() {return nameProperty;}

    public DoubleProperty valueProperty() {return valueProperty;}

    public ObjectProperty<NodeType> typeProperty() {return typeProperty;}

    public StringProperty indexProperty() {return  indexProperty;}

    public ObjectProperty<Color> colorProperty() {return colorProperty;}

    public ObjectProperty parentProperty() {return parentProperty;}

    public ListProperty<FuncModelNode> childListProperty() {return childListProperty;}




    @Override
    public String getName() {
        return nameProperty.get();
    }

    @Override
    public String getId() {
        return idProperty.get();
    }

    @Override
    public Double getValue() {
        return valueProperty.get();
    }

    @Override
    public void setValue(Double value) {
        valueProperty.set(value);
    }

    @Override
    public NodeType getType() {
        return typeProperty.get();
    }

    @Override
    public void setType(NodeType type) {
        typeProperty.set(type);
    }

    @Override
    public Color getColor() {
        return colorProperty.get();
    }

    @Override
    public void setColor(Color newColor) {
        colorProperty.set(newColor);
    }

    @Override
    public FuncModelNode getParent() {
        return parentProperty.get();
    }

    @Override
    public List<FuncModelNode> getChildNodes() {
        return childListProperty.get();
    }

    @Override
    public String getIndex() {
        return indexProperty.get();
    }

    @Override
    public void addChildNode(FuncModelNode node) {
        getChildNodes().add(node);
        ((StandardFuncModelNode)node).setIndexValue(getChildNodes().size());
        ((StandardFuncModelNode)node).recalculateChildIndexValues();
    }

    @Override
    public void removeChildNode(String nodeId) throws FuncModelBoundaryException {
        Optional<FuncModelNode> childNodeOpt =
                this.getChildNodes().stream().filter(n -> n.getId().equals(nodeId)).findFirst();
        if(childNodeOpt.isPresent()) {
            StandardFuncModelNode childNode = (StandardFuncModelNode) childNodeOpt.get();
            childNode.removeChildren();
            childNode.parentProperty.set(null);
            this.getChildNodes().remove(childNode);
        }
        recalculateChildIndexValues();
    }

    private void removeChildren() {
        this.getChildNodes().stream().forEach(ch -> {
            ((StandardFuncModelNode) ch).removeChildren();
        });
        this.getChildNodes().clear();
    }

    @Override
    public FuncModelNode findNode(String nodeId) {
        FuncModelNode node =  null;
        if(this.getId().equals(nodeId)) {
            node = this;
        } else {
            for (FuncModelNode child: getChildNodes()) {
                node = child.findNode(nodeId);
                if(node!=null) {
                    break;
                }
            }
        }
        return node;
    }

    @Override
    public void moveToParent(FuncModelNode newParent) {
        StandardFuncModelNode cloneOfThis = (StandardFuncModelNode) this.cloneNode();
        if(this.getParent() != null) {
            this.getParent().removeChildNode(this.getId());
        }
        cloneOfThis.parentProperty.set(newParent);
        newParent.addChildNode(cloneOfThis);
    }

    @Override
    public void moveChildPosition(int oldIdx, int newIdx) {
        FuncModelNode nodeToMove = getChildNodes().get(oldIdx);
        StandardFuncModelNode cloneOfNodeToMove = (StandardFuncModelNode) nodeToMove.cloneNode();
        cloneOfNodeToMove.parentProperty.set(this);
        removeChildNode(nodeToMove.getId());
        getChildNodes().add(
                newIdx,
                cloneOfNodeToMove);
        recalculateChildIndexValues();
    }

    @Override
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    @Override
    public FuncModelNode cloneNode()  {
        StandardFuncModelNode clonedNode = new StandardFuncModelNode(null, UUID.randomUUID().toString() , this.getName());
        clonedNode.setType(this.getType());
        clonedNode.setColor(this.getColor());
        clonedNode.setValue(this.getValue());
        getChildNodes().stream().forEach(ch -> {
            StandardFuncModelNode clonedChild = (StandardFuncModelNode) ch.cloneNode();
            clonedChild.parentProperty().set(clonedNode);
            clonedNode.addChildNode(clonedChild);
        });
        return clonedNode;
    }

    private void setIndexValue(int childIdx) {
        if(this.getParent()!=null) {
            this.indexProperty.set(this.getParent().getIndex() + "." + childIdx);
        }
    }

    private void recalculateChildIndexValues() {
        Iterator<FuncModelNode> nodeIterator = getChildNodes().listIterator();
        int idx = 1;
        while(nodeIterator.hasNext()){
            StandardFuncModelNode chNode = (StandardFuncModelNode) nodeIterator.next();
            chNode.setIndexValue(idx++);
            chNode.recalculateChildIndexValues();
        }
    }
}
