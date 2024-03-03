package com.bitconex.danalyst.funmodmap.adapter.csv.out;

import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVOutAdapterImpl implements CSVOutAdapter{
    @Override
    public void writeToFile(FuncModelTree modelTree, File file) {
        try {
            String csvString = nodeToString(modelTree.getRootNode());
            Files.writeString(Paths.get(file.toURI()), csvString, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Fehler exporting tree to " + file.getAbsolutePath());
        }
    }

    String nodeToString (FuncModelNode node) {
        StringBuffer nodeStr = new StringBuffer(node.getIndex()).append(";").append(node.getName()).append("\n");
        node.getChildNodes().forEach(funcModelNode -> nodeStr.append(nodeToString(funcModelNode)));
        return nodeStr.toString();
    }
}
