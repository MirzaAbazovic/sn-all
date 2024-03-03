/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 11:27:57
 */
package de.augustakom.hurrican.model.reporting;

import java.util.*;


/**
 * Modell-Klasse für Text-Bausteine
 *
 *
 */
public class TxtBaustein extends AbstractReportLongIdModel {

    private Long idOrig = null;
    private String name = null;
    private String text = null;
    private Boolean editable = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private Boolean mandatory = null;

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

    /**
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text Festzulegender text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return editable
     */
    public Boolean getEditable() {
        return editable;
    }

    /**
     * @param editable Festzulegender editable
     */
    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    /**
     * @return gueltigBis
     */
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @param gueltigBis Festzulegender gueltigBis
     */
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    /**
     * @return gueltigVon
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon Festzulegender gueltigVon
     */
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
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
     * @return idOrig
     */
    public Long getIdOrig() {
        return idOrig;
    }

    /**
     * @param idOrig Festzulegender idOrig
     */
    public void setIdOrig(Long idOrig) {
        this.idOrig = idOrig;
    }

}
