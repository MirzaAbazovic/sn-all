/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2007 08:36:36
 */
package de.augustakom.hurrican.model.billing;


/**
 * Bildet den Auftragzusatz 'Kombi' aus dem Billing-System ab.
 */
public class BAuftragKombi extends AbstractBillingModel {

    private Long auftragNo = null;
    private String switchId = null;
    private String zielnummerKuerzung = null;
    private Long kwk = null;
    private Long timeSlotNo = null;

    /**
     * @return auftragNo
     */
    public Long getAuftragNo() {
        return auftragNo;
    }

    /**
     * @param auftragNo Festzulegender auftragNo
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * <b>Name</b> des Switches.
     *
     * @return the switchId
     */
    public String getSwitchId() {
        return switchId;
    }

    /**
     * @param switchId the switchId to set
     */
    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    /**
     * @return kwk
     */
    public Long getKwk() {
        return kwk;
    }

    /**
     * @param kwk Festzulegender kwk
     */
    public void setKwk(Long kwk) {
        this.kwk = kwk;
    }

    /**
     * @return zielnummerKuerzung
     */
    public String getZielnummerKuerzung() {
        return zielnummerKuerzung;
    }

    /**
     * @param zielnummerKuerzung Festzulegender zielnummerKuerzung
     */
    public void setZielnummerKuerzung(String zielnummerKuerzung) {
        this.zielnummerKuerzung = zielnummerKuerzung;
    }

    /**
     * @return the timeSlotNo
     */
    public Long getTimeSlotNo() {
        return timeSlotNo;
    }

    /**
     * @param timeSlotNo the timeSlotNo to set
     */
    public void setTimeSlotNo(Long timeSlotNo) {
        this.timeSlotNo = timeSlotNo;
    }


}


