package com.bitconex.danalyst.funmodmap.model;

public class FuncModelRoot extends StandardFuncModelNode implements FuncModelNode{
    public FuncModelRoot( String name, String id) {
        super(null, id, name);
        this.indexProperty().set("1");
    }

    @Override
    public FuncModelNode getParent() {
        return null;
    }
}
