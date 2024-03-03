package com.bitconex.danalyst.funmodmap.adapter.jsonfile;

import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelRoot;
import com.bitconex.danalyst.funmodmap.model.NodeType;
import com.bitconex.danalyst.funmodmap.model.StandardFuncModelNode;

import java.util.ArrayList;
import java.util.List;

public class FuncModelNodeDTO {

    public String id;
    public String name;
    public Double value;
    public String type;
    public ColorDTO color = new ColorDTO();
    public final List  <FuncModelNodeDTO> childList = new ArrayList<>();

    public static FuncModelNodeDTO fromFuncModelNode  (FuncModelNode funcModelNode) {
        FuncModelNodeDTO nodeDTO = new FuncModelNodeDTO();
        nodeDTO.id = funcModelNode.getId();
        nodeDTO.name = funcModelNode.getName();
        nodeDTO.color = ColorDTO.fromColor(funcModelNode.getColor());
        nodeDTO.value = funcModelNode.getValue();
        nodeDTO.type = funcModelNode.getType().name();
        if(funcModelNode.getChildNodes() != null ) {
            funcModelNode.getChildNodes().stream().forEach(ch -> nodeDTO.childList.add(fromFuncModelNode(ch)));
        }
        return nodeDTO;
    }

    public static FuncModelNode toFuncModelNode(FuncModelNodeDTO nodeDTO, FuncModelNode parent) {
        FuncModelNode node = parent==null ?
                new FuncModelRoot(nodeDTO.name, nodeDTO.id) :
                new StandardFuncModelNode(parent, nodeDTO.id, nodeDTO.name);
        node.setColor(ColorDTO.toColor(nodeDTO.color));
        node.setValue(nodeDTO.value);
        node.setType(NodeType.valueOf(nodeDTO.type));
        nodeDTO.childList.stream().forEach(chDTO -> toFuncModelNode(chDTO, node));
        return node;
    }
}
