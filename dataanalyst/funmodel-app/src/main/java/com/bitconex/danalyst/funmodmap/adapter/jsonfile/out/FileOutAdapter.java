package com.bitconex.danalyst.funmodmap.adapter.jsonfile.out;

import com.bitconex.danalyst.funmodmap.model.FuncModelTree;

import java.io.File;

public interface FileOutAdapter {
    void writeToFile(FuncModelTree modelTree, File file);
}
