package com.bitconex.danalyst.funmodmap.model;

public enum NodeType {
    BULLET("Bullet: Standard Function", "/images/icons8-xbox-b-50.png"),
    WARNING("Warning: Further work needed", "/images/icons8-gefüllte-flagge-2-48.png"),
    POWER_X("Power X", "/images/icons8-eingekreistes-x-50.png"),
    OUTPUT_Y("Output Y", "/images/icons8-eingekreistes-y-50.png"),
    WASTE_AVOID("Waste/Avoid", "/images/icons8-fehler-48.png"),
    STOP_OUT_OF_SCOPE("Stop: Out of Scope", "/images/icons8-stoppschild-48.png");
    private String label;
    private String imgPath;
    private NodeType(String label, String imgPath) {
        this.label = label;
        this.imgPath = imgPath;
    }

    public String getLabel() {
        return label;
    }

    public String getImgPath() {
        return imgPath;
    }
}
