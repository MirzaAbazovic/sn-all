/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015 11:26
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class VoipLoginTestBuilder extends VoipLoginDataBuilder {

    public VoipLoginTestBuilder() {
        withSipMainNumber("+499845205320");
        withSipLogin("+499845205320@biz.m-call.de");
        withSipPassword("AsdFjkl");
    }
}
