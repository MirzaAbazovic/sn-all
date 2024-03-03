package com.bitconex.danalyst.funmodmap.boundary;

import com.bitconex.danalyst.funmodmap.adapter.csv.out.CSVOutAdapter;
import com.bitconex.danalyst.funmodmap.adapter.csv.out.CSVOutAdapterImpl;
import com.bitconex.danalyst.funmodmap.adapter.jsonfile.in.FileInAdapter;
import com.bitconex.danalyst.funmodmap.adapter.jsonfile.in.FileInAdapterImpl;
import com.bitconex.danalyst.funmodmap.adapter.jsonfile.out.FileOutAdapter;
import com.bitconex.danalyst.funmodmap.adapter.jsonfile.out.FileOutAdapterImpl;
import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.bitconex.danalyst.funmodmap.model.NodeType;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FuncModelBoundaryImpl implements FuncModelBoundary {
    private final static FuncModelBoundaryImpl instance = new FuncModelBoundaryImpl();
    private final static String NEW_TREE_NAME = "funcModel";
    private  final Map<String, FuncModelTree> treeRepository;
    private FileInAdapter fileInAdapter = new FileInAdapterImpl();
    private FileOutAdapter fileOutAdapter = new FileOutAdapterImpl();
    private CSVOutAdapter csvOutAdapter = new CSVOutAdapterImpl();

    private FuncModelBoundaryImpl() {
        treeRepository = new HashMap<>();
    }

    public static FuncModelBoundary getInstance() {return instance;}

    @Override
    public FuncModelTree createNewTree() {
        FuncModelTree tree = new FuncModelTree(NEW_TREE_NAME);
        treeRepository.put(tree.getId(), tree);
        return tree;
    }

    @Override
    public FuncModelNode addNode(String treeId, String parentId) throws FuncModelBoundaryException{
        return getTree(treeId).addNode(parentId);
    }

    @Override
    public void removeNode(String treeId, String nodeId) throws FuncModelBoundaryException{
         getTree(treeId).removeNode(nodeId);
    }

    @Override
    public void moveNode(String treeId, String nodeId, String newParentId) throws FuncModelBoundaryException {
        getTree(treeId).moveNode(newParentId, nodeId);
    }

    @Override
    public void changeNodePosition(String treeId, String nodeId, int newIdx) throws FuncModelBoundaryException {
        getTree(treeId).changeNodePosition(nodeId, newIdx);
    }

    @Override
    public void changeNodeValue(String treeId, String nodeId, Double newValue) throws FuncModelBoundaryException {
        getTree(treeId).changeNodeValue(nodeId, newValue);
    }

    @Override
    public void changeNodeName(String treeId, String nodeId, String newName) throws FuncModelBoundaryException {
        getTree(treeId).changeNodeName(nodeId, newName);
    }

    @Override
    public void changeNodeType(String treeId, String nodeId, NodeType newNodeType) throws FuncModelBoundaryException {
        getTree(treeId).changeNodeType(nodeId, newNodeType);
    }

    @Override
    public void changeNodeColor(String treeId, String nodeId, Color newColor) throws FuncModelBoundaryException {
        getTree(treeId).changeNodeColor(nodeId, newColor);
    }

    @Override
    public void saveTree(String treeId, File targetFile) {
        getTree(treeId).setName(targetFile.getName());
        fileOutAdapter.writeToFile(getTree(treeId), targetFile);
    }


    @Override
    public FuncModelTree loadTree(File treeFile) {
        FuncModelTree loadedTree = fileInAdapter.loadFromFile(treeFile);
        this.treeRepository.put(loadedTree.getId(), loadedTree);
        return loadedTree;
    }

    @Override
    public void exportTreeToCSV(String treeId, File targetFile) {
        csvOutAdapter.writeToFile(getTree(treeId), targetFile);
    }

    private FuncModelTree getTree (String treeId) throws FuncModelBoundaryException {
        if(treeRepository.containsKey(treeId)) {
            return treeRepository.get(treeId);
        } else {
            throw new FuncModelBoundaryException("Unknown tree: " + treeId);
        }
    }

    public void setFileInAdapter(FileInAdapter fileInAdapter) {
        this.fileInAdapter = fileInAdapter;
    }

    public void setFileOutAdapter(FileOutAdapter fileOutAdapter) {
        this.fileOutAdapter = fileOutAdapter;
    }
}
