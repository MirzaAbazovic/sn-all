package com.bitconex.danalyst.funmodmap.model;

import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundaryException;
import javafx.scene.paint.Color;

import java.util.UUID;

public class FuncModelTree {
    private final static String DEFAULT_NEW_NAME = "New Node";

    private String name;
    private String id;
    private FuncModelRoot rootNode;


    public FuncModelTree(
           String name,
           String id,
           FuncModelRoot rootNode) {
        this.name = name;
        this.id = id;
        this.rootNode = rootNode;
    }
    public FuncModelTree(String name) {
        this(
                name,
                UUID.randomUUID().toString(),
                new FuncModelRoot("Root", UUID.randomUUID().toString()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FuncModelRoot getRootNode() {
        return rootNode;
    }

    public void setRootNode(FuncModelRoot rootNode) {
        this.rootNode = rootNode;
    }

    public String getId() {
        return id;
    }

    public FuncModelNode addNode(String parentId) throws FuncModelBoundaryException {
        FuncModelNode newNode = null;
        if(parentId == null) {
            throw new FuncModelBoundaryException("parentId must not be  null");
        }
        FuncModelNode parentNode = rootNode.findNode(parentId);
        if(parentId == null) {
            throw new FuncModelBoundaryException("Node with id: " + parentId + " does not exist in the tree "
                    + this.getId());
        } else {
            String newNodeId = UUID.randomUUID().toString();
            newNode = new StandardFuncModelNode(parentNode, newNodeId, DEFAULT_NEW_NAME);
        }
        return newNode;
    }

    public void removeNode(String nodeId) throws FuncModelBoundaryException {
        if(nodeId == null) {
            throw new FuncModelBoundaryException("nodeId must not be  null");
        }
        FuncModelNode nodeToRemove = findNode(nodeId);
        if (nodeToRemove != null && nodeToRemove.getParent() != null) {
            nodeToRemove.getParent().removeChildNode(nodeId);
        }
    }

    public void moveNode (String newParentId, String nodeId) {
        if(nodeId == null) {
            throw new FuncModelBoundaryException("nodeId must not be  null");
        }
        FuncModelNode nodeToMove = findNode(nodeId);
        FuncModelNode newParentNode = findNode(newParentId);
        nodeToMove.moveToParent(newParentNode);
    }

    public void changeNodePosition (String nodeId, int newIndex) {
        if(nodeId == null) {
            throw new FuncModelBoundaryException("nodeId must not be  null");
        }
        FuncModelNode nodeToMove = findNode(nodeId);
        nodeToMove.getParent().moveChildPosition(
                nodeToMove.getParent().getChildNodes().indexOf(nodeToMove),
                newIndex
        );
    }

    public void changeNodeValue(String nodeId, Double newValue) {
        FuncModelNode nodeToChange = findNode(nodeId);
        nodeToChange.setValue(newValue);
    }

    public void changeNodeType(String nodeId, NodeType newNodeType) {
        FuncModelNode nodeToChange = findNode(nodeId);
        nodeToChange.setType(newNodeType);
    }

    public void changeNodeName(String nodeId, String newNodeName) {
        FuncModelNode nodeToChange = findNode(nodeId);
        nodeToChange.setName(newNodeName);
    }

    public void changeNodeColor(String nodeId, Color newColor) {
        FuncModelNode nodeToChange = findNode(nodeId);
        nodeToChange.setColor(newColor);
    }

    private FuncModelNode findNode(String nodeId) throws FuncModelBoundaryException{
        if(nodeId == null) {
            throw new FuncModelBoundaryException("parentId must not be  null");
        }

        FuncModelNode parentNode = rootNode.findNode(nodeId);
        if(parentNode == null) {
            throw new FuncModelBoundaryException("Node with id: " + nodeId + " does not exist in the tree "
                    + this.getId());
        }
        return parentNode;
    }



}
