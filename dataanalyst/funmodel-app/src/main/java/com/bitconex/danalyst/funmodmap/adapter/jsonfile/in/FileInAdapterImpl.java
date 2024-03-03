package com.bitconex.danalyst.funmodmap.adapter.jsonfile.in;

import com.bitconex.danalyst.funmodmap.adapter.jsonfile.FuncModelTreeDTO;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInAdapterImpl implements FileInAdapter {
    @Override
    public FuncModelTree loadFromFile(File file) {
        try {
            String jsonInString = Files.readString(Path.of(file.toURI()), Charset.forName("UTF-8"));
            ObjectMapper jsonMapper = new ObjectMapper();
            FuncModelTreeDTO loadedTreeDTO = jsonMapper.readValue(jsonInString, FuncModelTreeDTO.class);
            return FuncModelTreeDTO.toFuncModelTree(loadedTreeDTO);
        } catch (IOException e) {
            throw new RuntimeException("Error loading tree from the file: " +
                    file.getAbsolutePath() + ". " + e.getMessage(), e );
        }
    }
}
