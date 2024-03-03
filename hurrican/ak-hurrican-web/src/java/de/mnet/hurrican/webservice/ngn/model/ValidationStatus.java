/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

public class ValidationStatus {
    private ValidationStatusEnum statusEnum;
    private String msg;

    public ValidationStatus(ValidationStatusEnum statusEnum, String msg) {

        this.statusEnum = statusEnum;
        this.msg = msg;
    }

    public ValidationStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public String getMsg() {
        return msg;
    }
}

