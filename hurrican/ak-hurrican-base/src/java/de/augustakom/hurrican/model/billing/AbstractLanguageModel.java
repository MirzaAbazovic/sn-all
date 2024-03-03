/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2006 13:10:01
 */
package de.augustakom.hurrican.model.billing;


/**
 * Abstrakte Basisklasse fuer Uebersetzungsmodelle.
 *
 *
 */
public abstract class AbstractLanguageModel extends AbstractBillingModel {

    protected String name = null;
    protected String language = null;

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

}


