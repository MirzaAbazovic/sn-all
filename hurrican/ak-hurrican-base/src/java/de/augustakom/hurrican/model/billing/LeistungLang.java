/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2006 13:09:10
 */
package de.augustakom.hurrican.model.billing;


/**
 * Uebersetzungsmodell fuer Leistung.
 *
 *
 */
public class LeistungLang extends AbstractLanguageModel {

    private Long leistungLangNo = null;
    private Long leistungNo = null;
    private String value = null;

    /**
     * @return Returns the leistungNo.
     */
    public Long getLeistungNo() {
        return this.leistungNo;
    }

    /**
     * @param leistungNo The leistungNo to set.
     */
    public void setLeistungNo(Long leistungNo) {
        this.leistungNo = leistungNo;
    }

    /**
     * @return Returns the leistungLangNo.
     */
    public Long getLeistungLangNo() {
        return this.leistungLangNo;
    }

    /**
     * @param leistungLangNo The leistungLangNo to set.
     */
    public void setLeistungLangNo(Long leistungLangNo) {
        this.leistungLangNo = leistungLangNo;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

}


