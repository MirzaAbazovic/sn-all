package com.bitconex.danalyst.funmodmap.adapter.jsonfile.out;

import com.bitconex.danalyst.funmodmap.adapter.jsonfile.FuncModelTreeDTO;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileOutAdapterImpl implements FileOutAdapter {
    @Override
    public void writeToFile(FuncModelTree modelTree, File file) {
        try {
            FuncModelTreeDTO treeDTO = FuncModelTreeDTO.fromFuncModelTree(modelTree);
            ObjectMapper jsonMapper = new ObjectMapper();
            String jsonOutString = jsonMapper.writeValueAsString(treeDTO);
            Files.writeString(Paths.get(file.toURI()), jsonOutString, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error saving tree to the file: " +
                    file.getAbsolutePath() + ". " + e.getMessage(), e );
        }
    }
}
