package com.bitconex.danalyst.funmodmap.boundary;

import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.bitconex.danalyst.funmodmap.model.NodeType;
import javafx.scene.paint.Color;

import java.io.File;

public interface FuncModelBoundary {
    /**
     * Creates a new RootNode for a new Tree
     * @return a new RootNode with Empty childList
     */
    FuncModelTree createNewTree();

    /**
     * Adds new Node as subNode of given parent node. Node will be added at the last position of the
     * parents children list. New created node will be returned
     * @param treeId - id of the tree which should be extended by new node
     * @param parentId - id of the parent of new node
     * @return new node
     */
    FuncModelNode addNode(String treeId, String parentId) throws FuncModelBoundaryException;

    /**
     * Removes a node from the tree.
     * @param treeId - id of the tree
     * @param nodeId - id of node which should be removed from the parents child list
     * @return modified parent node
     */
    void  removeNode (String treeId, String nodeId) throws FuncModelBoundaryException;

    /**
     * Moves a node from the current parent node to the child list of the new parent node
     * @param treeId - Id of the tree
     * @param nodeId - Id of the node to be moved
     * @param newParentId - Id of the new parent node
     */
    void moveNode(String treeId, String nodeId, String newParentId) throws FuncModelBoundaryException;

    /**
     *
     * @param treeId - Id of the tree
     * @param nodeId - Id of the node to be moved in the child list
     * @param newIdx - new index position of the node inside the parents child list.
     * @throws FuncModelBoundaryException
     */
    void changeNodePosition(String treeId, String nodeId, int newIdx) throws FuncModelBoundaryException;

    /**
     * Changes color of a specific node in a given tree
     * @param treeId - Id of tree
     * @param nodeId - Id of node to be changed
     * @param newColor - Color which should be set to the node
     * @throws FuncModelBoundaryException
     */
    void changeNodeColor(String treeId, String nodeId, Color newColor) throws FuncModelBoundaryException;

    /**
     * Changes name of a specific node
     * @param treeId - Id of tree containing the node
     * @param nodeId - Id of the node to be changed
     * @param newName - New name for the node
     * @throws FuncModelBoundaryException
     */
    void changeNodeName(String treeId, String nodeId, String newName) throws FuncModelBoundaryException;

    /**
     * Changes value (double) of a specific node
     * @param treeId - Id of tree containing the node
     * @param nodeId - Id of the node to be changed
     * @param newValue - New value for the node
     * @throws FuncModelBoundaryException
     */
    void changeNodeValue(String treeId, String nodeId, Double newValue) throws FuncModelBoundaryException;

    /**
     * Changes type of a specidfic node
     * @param treeId - Id of the tree
     * @param nodeId - Id of the node to be changed
     * @param newNodeType - New type od the node
     * @throws FuncModelBoundaryException
     */
    void changeNodeType(String treeId, String nodeId, NodeType newNodeType) throws FuncModelBoundaryException;

    /**
     * Saves a tree to a file. Tree will be serialized in JSON-Format
     * @param treeId - Id of the tree to be saved
     * @param targetFile - Target file for save the tree
     */
    void saveTree(String treeId, File targetFile);

    /**
     * Loads a tree from a file and adds it to the repository
     * @param treeFile - file containing serialized tree
     */
    FuncModelTree loadTree(File treeFile);

    /**
     * Exports the content of tree to CSV-Format
     * @param treeId - ID of the tree to be exported
     * @param targetFile - Target file for export the tree
     */
    void exportTreeToCSV(String treeId, File targetFile);

}
