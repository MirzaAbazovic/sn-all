/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class CommunicationTestBuilder extends CommunicationBuilder {

    public CommunicationTestBuilder() {
        addEmail("ffm-info@m-net.de");
        addFax("089/45200-78351");
        addMobile("0177-11223344");
        addPhone("089/45200-0000");
    }
}