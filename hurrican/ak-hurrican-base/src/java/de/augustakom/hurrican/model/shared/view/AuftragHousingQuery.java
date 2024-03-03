/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2011 12:21:56
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche ueber Housing Schluessel.
 *
 *
 */
public class AuftragHousingQuery extends AbstractHurricanQuery {

    // Filter-Parameter
    private Long transponderNr = null;
    private String firstName = null;
    private String lastName = null;


    // Order-Parameter
    private boolean orderByKundeNo = false;
    private boolean orderByAuftragNoOrig = false;


    public Long getTransponderNr() {
        return transponderNr;
    }

    public void setTransponderNr(Long transponderNr) {
        this.transponderNr = transponderNr;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isOrderByKundeNo() {
        return orderByKundeNo;
    }

    public void setOrderByKundeNo(boolean orderByKundeNo) {
        this.orderByKundeNo = orderByKundeNo;
    }

    public boolean isOrderByAuftragNoOrig() {
        return orderByAuftragNoOrig;
    }

    public void setOrderByAuftragNoOrig(boolean orderByAuftragNoOrig) {
        this.orderByAuftragNoOrig = orderByAuftragNoOrig;
    }

    @Override
    public boolean isEmpty() {

        if (getTransponderNr() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getFirstName())) {
            return false;
        }
        if (StringUtils.isNotBlank(getLastName())) {
            return false;
        }

        return true;
    }

}


