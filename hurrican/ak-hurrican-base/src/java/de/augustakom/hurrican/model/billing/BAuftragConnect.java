/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.2008 15:41:25
 */
package de.augustakom.hurrican.model.billing;


/**
 * Billing-Modell fuer die Abbildung des Auftragszusatz fuer Connect-Auftraege.
 *
 *
 */
public class BAuftragConnect extends AbstractBillingModel {

    private Long auftragNo;
    private String lbzMnet;
    private String lbzKunde;
    private String accessPointA;
    private String accessPointB;
    private Long accessPointAAddress;
    private Long accessPointBAddress;

    /**
     * @return Returns the auftragNo.
     */
    public Long getAuftragNo() {
        return auftragNo;
    }

    /**
     * @param auftragNo The auftragNo to set.
     */
    public void setAuftragNo(Long auftragNo) {
        this.auftragNo = auftragNo;
    }

    /**
     * @return Returns the lbzMnet.
     */
    public String getLbzMnet() {
        return lbzMnet;
    }

    /**
     * @param lbzMnet The lbzMnet to set.
     */
    public void setLbzMnet(String lbzMnet) {
        this.lbzMnet = lbzMnet;
    }

    /**
     * @return Returns the accessPointA.
     */
    public String getAccessPointA() {
        return accessPointA;
    }

    /**
     * @param accessPointA The accessPointA to set.
     */
    public void setAccessPointA(String accessPointA) {
        this.accessPointA = accessPointA;
    }

    /**
     * @return Returns the accessPointB.
     */
    public String getAccessPointB() {
        return accessPointB;
    }

    /**
     * @param accessPointB The accessPointB to set.
     */
    public void setAccessPointB(String accessPointB) {
        this.accessPointB = accessPointB;
    }


    public Long getAccessPointAAddress() {
        return accessPointAAddress;
    }


    public void setAccessPointAAddress(Long accessPointAAddress) {
        this.accessPointAAddress = accessPointAAddress;
    }


    public Long getAccessPointBAddress() {
        return accessPointBAddress;
    }


    public void setAccessPointBAddress(Long accessPointBAddress) {
        this.accessPointBAddress = accessPointBAddress;
    }


    /**
     * @return the lbzKunde
     */
    public String getLbzKunde() {
        return lbzKunde;
    }


    /**
     * @param lbzKunde the lbzKunde to set
     */
    public void setLbzKunde(String lbzKunde) {
        this.lbzKunde = lbzKunde;
    }

}


