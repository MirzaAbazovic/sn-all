/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2009 14:13:47
 */
package de.augustakom.hurrican.model.cc.hardware;

/**
 * ONT Hardware.
 *
 *
 */
public class HWOnt extends HWOltChild {

    private static final long serialVersionUID = 2125315780253931124L;
    /**
     * neue Huawei ONT
     */
    public static String ONT_TYPE_O123T = "O-123-T";

    private String ontType;

    public String getOntType() {
        return ontType;
    }

    public void setOntType(String ontType) {
        this.ontType = ontType;
    }

}


