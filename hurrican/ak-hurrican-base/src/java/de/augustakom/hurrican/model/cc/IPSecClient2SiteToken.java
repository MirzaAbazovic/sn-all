/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2009 10:39:51
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modell fuer IPSecClient2Site Tokens.
 *
 *
 */
public class IPSecClient2SiteToken extends AbstractCCIDModel implements CCAuftragModel {

    /**
     * Reference-ID fuer die Angabe eines aktiven Tokens.
     */
    public static final Long REF_ID_TOKEN_STATUS_ACTIVE = Long.valueOf(21000);
    /**
     * Reference-ID fuer die Angabe eines sich im Austausch befindlichen Tokens.
     */
    public static final Long REF_ID_TOKEN_STATUS_IN_EXCHANGE = Long.valueOf(21001);
    /**
     * Reference-ID fuer die Angabe eines abgelaufenen Tokens.
     */
    public static final Long REF_ID_TOKEN_STATUS_EXPIRED = Long.valueOf(21002);

    private Long auftragId;
    private String serialNumber;
    public static final String SERIAL_NUMBER = "serialNumber";
    private Integer laufzeitInMonaten;
    public static final String LAUFZEIT_IN_MONATEN = "laufzeitInMonaten";
    private Date lieferdatum;
    public static final String LIEFERDATUM = "lieferdatum";
    private String bemerkung;
    public static final String BEMERKUNG = "bemerkung";
    private Date batterieEnde;
    public static final String BATTERIE_ENDE = "batterieEnde";
    private String sapOrderId;
    public static final String SAP_ORDER_ID = "sapOrderId";
    private String batch;
    public static final String BATCH = "batch";
    private Long statusRefId;
    public static final String STATUS_REF_ID = "statusRefId";


    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getLaufzeitInMonaten() {
        return laufzeitInMonaten;
    }

    public void setLaufzeitInMonaten(Integer laufzeitInMonaten) {
        this.laufzeitInMonaten = laufzeitInMonaten;
    }

    public Date getLieferdatum() {
        return lieferdatum;
    }

    public void setLieferdatum(Date lieferdatum) {
        this.lieferdatum = lieferdatum;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getBatterieEnde() {
        return batterieEnde;
    }

    public void setBatterieEnde(Date batterieEnde) {
        this.batterieEnde = batterieEnde;
    }

    public String getSapOrderId() {
        return sapOrderId;
    }

    public void setSapOrderId(String sapOrderId) {
        this.sapOrderId = sapOrderId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setStatusRefId(Long statusRefId) {
        this.statusRefId = statusRefId;
    }

    public Long getStatusRefId() {
        return statusRefId;
    }

}
