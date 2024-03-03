/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.05.2007 09:27:57
 */
package de.augustakom.hurrican.model.reporting;


/**
 * Modell-Klasse für eine Gruppe von Text-Baustein-Gruppen
 *
 *
 */
public class TxtBausteinGruppe extends AbstractReportLongIdModel {

    private String name = null;
    private String description = null;
    private Boolean mandatory = null;

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Festzulegender description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory Festzulegender mandatory
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Festzulegender name
     */
    public void setName(String name) {
        this.name = name;
    }

}
