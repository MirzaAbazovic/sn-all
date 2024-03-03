/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2009 09:48:19
 */
package de.augustakom.authentication.model;


/**
 * Modell fuer Niederlassung
 *
 *
 */
public class AKNiederlassungView extends AbstractAuthenticationModel {

    private Long id = null;
    private String name = null;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


}


