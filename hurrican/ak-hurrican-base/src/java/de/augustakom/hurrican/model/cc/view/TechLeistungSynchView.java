/*
 * Copyright (c) 2009 - M-Net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.11.2009 15:37:42
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * View-Modell zum abgleich der einmaligen Leistungen zu technischen Leistungen <br>
 *
 *
 */
public class TechLeistungSynchView extends AbstractCCModel {

    private Long techLstId = null;
    private Long quantity = null;
    private Date date = null;
    private String techLstName = null;

    /**
     * @return the techLstId
     */
    public Long getTechLstId() {
        return techLstId;
    }

    /**
     * @param techLstId the techLstId to set
     */
    public void setTechLstId(Long techLstId) {
        this.techLstId = techLstId;
    }

    /**
     * @return the quantity
     */
    public Long getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the techLstName
     */
    public String getTechLstName() {
        return techLstName;
    }

    /**
     * @param techLstName the techLstName to set
     */
    public void setTechLstName(String techLstName) {
        this.techLstName = techLstName;
    }
}


