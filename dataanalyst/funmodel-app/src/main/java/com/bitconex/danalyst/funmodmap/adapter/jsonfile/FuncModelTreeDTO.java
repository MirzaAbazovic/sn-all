package com.bitconex.danalyst.funmodmap.adapter.jsonfile;

import com.bitconex.danalyst.funmodmap.model.FuncModelRoot;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;

public class FuncModelTreeDTO {
    public String id;
    public String name;
    public FuncModelNodeDTO rootNode;

    public static FuncModelTreeDTO fromFuncModelTree (FuncModelTree funcModelTree) {
        FuncModelTreeDTO treeDTO = new FuncModelTreeDTO();
        treeDTO.id = funcModelTree.getId();
        treeDTO.name = funcModelTree.getName();
        treeDTO.rootNode = FuncModelNodeDTO.fromFuncModelNode(funcModelTree.getRootNode());
        return treeDTO;
    }

    public static FuncModelTree toFuncModelTree (FuncModelTreeDTO funcModelTreeDTO) {
        return new FuncModelTree(
                funcModelTreeDTO.name,
                funcModelTreeDTO.id,
                (FuncModelRoot) FuncModelNodeDTO.toFuncModelNode(
                        funcModelTreeDTO.rootNode, null));
    }

}
