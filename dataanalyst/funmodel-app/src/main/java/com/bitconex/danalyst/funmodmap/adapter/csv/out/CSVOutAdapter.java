package com.bitconex.danalyst.funmodmap.adapter.csv.out;

import com.bitconex.danalyst.funmodmap.model.FuncModelTree;

import java.io.File;

public interface CSVOutAdapter {
    void writeToFile(FuncModelTree modelTree, File file);
}
