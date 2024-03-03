/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2011 09:15:01
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

/**
 * Entity Builder fuer {@link ExtServiceProvider} Objekte.
 */
public class ExtServiceProviderBuilder extends EntityBuilder<ExtServiceProviderBuilder, ExtServiceProvider> {

    private String name = "Nachname";
    private String firstname = "Vorname";
    private String street = null;
    private String houseNum = null;
    private String postalCode = null;
    private String city = null;
    private String phone = null;
    private String email = "test@test.de";
    private String fax = null;
    private Integer contactType = null;
}


