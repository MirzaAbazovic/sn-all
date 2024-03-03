package com.bitconex.danalyst.funmodmap.adapter.jsonfile.in;

import com.bitconex.danalyst.funmodmap.model.FuncModelTree;

import java.io.File;

public interface FileInAdapter {
    FuncModelTree loadFromFile(File file);
}
