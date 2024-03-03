/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.2005 07:59:42
 */
package de.augustakom.hurrican.model.cc.dn;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell stellt eine Beziehung zwischen einer Rufnummer und einer Rufnummern-Leistung dar.
 *
 *
 */
public class Leistung2DN extends AbstractCCIDModel {

    /**
     * User-Name fuer vom System abgeschlossene Rufnummernleistungen VoIP.
     */
    public static final String EWSD_USER_VOIP_MIDDLEWARE = "VoIP-Middleware";

    /**
     * Trennzeichen fuer Parameterwerte.
     */
    public static final String PARAMETER_SEP = "&";
    /**
     * Trennzeichen unterteilt einen Parameter in ID-Value
     */
    public static final String PARAMETER_ID_VALUE_SEP = "-";

    private Long dnNo = null;
    private Long lbId = null;
    private Long leistung4DnId = null;
    private String leistungParameter = null;
    private String scvUserRealisierung = null;
    private Date scvRealisierung = null;
    private String scvUserKuendigung = null;
    private Date scvKuendigung = null;
    private String ewsdUserRealisierung = null;
    private Date ewsdRealisierung = null;
    private String ewsdUserKuendigung = null;
    private Date ewsdKuendigung = null;
    private Boolean billingCheck = null;
    private Long parameterId = null;
    private Long cpsTxIdCreation = null;
    private Long cpsTxIdCancel = null;

    /**
     * @return Returns the billingCheck.
     */
    public Boolean getBillingCheck() {
        return billingCheck;
    }

    /**
     * @param billingCheck The billingCheck to set.
     */
    public void setBillingCheck(Boolean billingCheck) {
        this.billingCheck = billingCheck;
    }

    /**
     * @return Returns the dnNo.
     */
    public Long getDnNo() {
        return dnNo;
    }

    /**
     * @param dnNo The dnNo to set.
     */
    public void setDnNo(Long dnNo) {
        this.dnNo = dnNo;
    }

    /**
     * @return Returns the ewsdKuendigung.
     */
    public Date getEwsdKuendigung() {
        return ewsdKuendigung;
    }

    /**
     * @param ewsdKuendigung The ewsdKuendigung to set.
     */
    public void setEwsdKuendigung(Date ewsdKuendigung) {
        this.ewsdKuendigung = ewsdKuendigung;
    }

    /**
     * @return Returns the ewsdRealisierung.
     */
    public Date getEwsdRealisierung() {
        return ewsdRealisierung;
    }

    /**
     * @param ewsdRealisierung The ewsdRealisierung to set.
     */
    public void setEwsdRealisierung(Date ewsdRealisierung) {
        this.ewsdRealisierung = ewsdRealisierung;
    }

    /**
     * @return Returns the ewsdUserKuendigung.
     */
    public String getEwsdUserKuendigung() {
        return ewsdUserKuendigung;
    }

    /**
     * @param ewsdUserKuendigung The ewsdUserKuendigung to set.
     */
    public void setEwsdUserKuendigung(String ewsdUserKuendigung) {
        this.ewsdUserKuendigung = ewsdUserKuendigung;
    }

    /**
     * @return Returns the ewsdUserRealisierung.
     */
    public String getEwsdUserRealisierung() {
        return ewsdUserRealisierung;
    }

    /**
     * @param ewsdUserRealisierung The ewsdUserRealisierung to set.
     */
    public void setEwsdUserRealisierung(String ewsdUserRealisierung) {
        this.ewsdUserRealisierung = ewsdUserRealisierung;
    }

    /**
     * @return Returns the lbId.
     */
    public Long getLbId() {
        return this.lbId;
    }

    /**
     * @param lbId The lbId to set.
     */
    public void setLbId(Long lbId) {
        this.lbId = lbId;
    }

    /**
     * @return Returns the leistungDnNo.
     */
    public Long getLeistung4DnId() {
        return leistung4DnId;
    }

    /**
     * @param leistungDnNo The leistungDnNo to set.
     */
    public void setLeistung4DnId(Long leistungDnNo) {
        this.leistung4DnId = leistungDnNo;
    }

    /**
     * @return Returns the leistungParameter.
     */
    public String getLeistungParameter() {
        return leistungParameter;
    }

    /**
     * @param leistungParameter The leistungParameter to set.
     */
    public void setLeistungParameter(String leistungParameter) {
        this.leistungParameter = leistungParameter;
    }

    /**
     * @return Returns the scvKuendigung.
     */
    public Date getScvKuendigung() {
        return scvKuendigung;
    }

    /**
     * @param scvKuendigung The scvKuendigung to set.
     */
    public void setScvKuendigung(Date scvKuendigung) {
        this.scvKuendigung = scvKuendigung;
    }

    /**
     * @return Returns the scvRealisierung.
     */
    public Date getScvRealisierung() {
        return scvRealisierung;
    }

    /**
     * @param scvRealisierung The scvRealisierung to set.
     */
    public void setScvRealisierung(Date scvRealisierung) {
        this.scvRealisierung = scvRealisierung;
    }

    /**
     * @return Returns the scvUserKuendigung.
     */
    public String getScvUserKuendigung() {
        return scvUserKuendigung;
    }

    /**
     * @param scvUserKuendigung The scvUserKuendigung to set.
     */
    public void setScvUserKuendigung(String scvUserKuendigung) {
        this.scvUserKuendigung = scvUserKuendigung;
    }

    /**
     * @return Returns the scvUserRealisierung.
     */
    public String getScvUserRealisierung() {
        return scvUserRealisierung;
    }

    /**
     * @param scvUserRealisierung The scvUserRealisierung to set.
     */
    public void setScvUserRealisierung(String scvUserRealisierung) {
        this.scvUserRealisierung = scvUserRealisierung;
    }

    /**
     * @return Returns the parameterId.
     */
    public Long getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId The parameterId to set.
     */
    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * @return the cpsTxIdCancel
     */
    public Long getCpsTxIdCancel() {
        return cpsTxIdCancel;
    }

    /**
     * @param cpsTxIdCancel the cpsTxIdCancel to set
     */
    public void setCpsTxIdCancel(Long cpsTxIdCancel) {
        this.cpsTxIdCancel = cpsTxIdCancel;
    }

    /**
     * @return the cpsTxIdCreation
     */
    public Long getCpsTxIdCreation() {
        return cpsTxIdCreation;
    }

    /**
     * @param cpsTxIdCreation the cpsTxIdCreation to set
     */
    public void setCpsTxIdCreation(Long cpsTxIdCreation) {
        this.cpsTxIdCreation = cpsTxIdCreation;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((billingCheck == null) ? 0 : billingCheck.hashCode());
        result = (prime * result) + ((cpsTxIdCancel == null) ? 0 : cpsTxIdCancel.hashCode());
        result = (prime * result) + ((cpsTxIdCreation == null) ? 0 : cpsTxIdCreation.hashCode());
        result = (prime * result) + ((dnNo == null) ? 0 : dnNo.hashCode());
        result = (prime * result) + ((ewsdKuendigung == null) ? 0 : ewsdKuendigung.hashCode());
        result = (prime * result) + ((ewsdRealisierung == null) ? 0 : ewsdRealisierung.hashCode());
        result = (prime * result) + ((ewsdUserKuendigung == null) ? 0 : ewsdUserKuendigung.hashCode());
        result = (prime * result) + ((ewsdUserRealisierung == null) ? 0 : ewsdUserRealisierung.hashCode());
        result = (prime * result) + ((lbId == null) ? 0 : lbId.hashCode());
        result = (prime * result) + ((leistung4DnId == null) ? 0 : leistung4DnId.hashCode());
        result = (prime * result) + ((leistungParameter == null) ? 0 : leistungParameter.hashCode());
        result = (prime * result) + ((parameterId == null) ? 0 : parameterId.hashCode());
        result = (prime * result) + ((scvKuendigung == null) ? 0 : scvKuendigung.hashCode());
        result = (prime * result) + ((scvRealisierung == null) ? 0 : scvRealisierung.hashCode());
        result = (prime * result) + ((scvUserKuendigung == null) ? 0 : scvUserKuendigung.hashCode());
        result = (prime * result) + ((scvUserRealisierung == null) ? 0 : scvUserRealisierung.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Leistung2DN other = (Leistung2DN) obj;
        if (billingCheck == null) {
            if (other.billingCheck != null) {
                return false;
            }
        }
        else if (!billingCheck.equals(other.billingCheck)) {
            return false;
        }
        if (cpsTxIdCancel == null) {
            if (other.cpsTxIdCancel != null) {
                return false;
            }
        }
        else if (!cpsTxIdCancel.equals(other.cpsTxIdCancel)) {
            return false;
        }
        if (cpsTxIdCreation == null) {
            if (other.cpsTxIdCreation != null) {
                return false;
            }
        }
        else if (!cpsTxIdCreation.equals(other.cpsTxIdCreation)) {
            return false;
        }
        if (dnNo == null) {
            if (other.dnNo != null) {
                return false;
            }
        }
        else if (!dnNo.equals(other.dnNo)) {
            return false;
        }
        if (ewsdKuendigung == null) {
            if (other.ewsdKuendigung != null) {
                return false;
            }
        }
        else if (!ewsdKuendigung.equals(other.ewsdKuendigung)) {
            return false;
        }
        if (ewsdRealisierung == null) {
            if (other.ewsdRealisierung != null) {
                return false;
            }
        }
        else if (!ewsdRealisierung.equals(other.ewsdRealisierung)) {
            return false;
        }
        if (ewsdUserKuendigung == null) {
            if (other.ewsdUserKuendigung != null) {
                return false;
            }
        }
        else if (!ewsdUserKuendigung.equals(other.ewsdUserKuendigung)) {
            return false;
        }
        if (ewsdUserRealisierung == null) {
            if (other.ewsdUserRealisierung != null) {
                return false;
            }
        }
        else if (!ewsdUserRealisierung.equals(other.ewsdUserRealisierung)) {
            return false;
        }
        if (lbId == null) {
            if (other.lbId != null) {
                return false;
            }
        }
        else if (!lbId.equals(other.lbId)) {
            return false;
        }
        if (leistung4DnId == null) {
            if (other.leistung4DnId != null) {
                return false;
            }
        }
        else if (!leistung4DnId.equals(other.leistung4DnId)) {
            return false;
        }
        if (leistungParameter == null) {
            if (other.leistungParameter != null) {
                return false;
            }
        }
        else if (!leistungParameter.equals(other.leistungParameter)) {
            return false;
        }
        if (parameterId == null) {
            if (other.parameterId != null) {
                return false;
            }
        }
        else if (!parameterId.equals(other.parameterId)) {
            return false;
        }
        if (scvKuendigung == null) {
            if (other.scvKuendigung != null) {
                return false;
            }
        }
        else if (!scvKuendigung.equals(other.scvKuendigung)) {
            return false;
        }
        if (scvRealisierung == null) {
            if (other.scvRealisierung != null) {
                return false;
            }
        }
        else if (!scvRealisierung.equals(other.scvRealisierung)) {
            return false;
        }
        if (scvUserKuendigung == null) {
            if (other.scvUserKuendigung != null) {
                return false;
            }
        }
        else if (!scvUserKuendigung.equals(other.scvUserKuendigung)) {
            return false;
        }
        if (scvUserRealisierung == null) {
            if (other.scvUserRealisierung != null) {
                return false;
            }
        }
        else if (!scvUserRealisierung.equals(other.scvUserRealisierung)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format("Leistung2DN [dnNo=%s, lbId=%s, leistung4DnId=%s, leistungParameter=%s, scvUserRealisierung=%s, scvRealisierung=%s, scvUserKuendigung=%s, scvKuendigung=%s, ewsdUserRealisierung=%s, ewsdRealisierung=%s, ewsdUserKuendigung=%s, ewsdKuendigung=%s, billingCheck=%s, parameterId=%s, cpsTxIdCreation=%s, cpsTxIdCancel=%s]",
                        dnNo, lbId, leistung4DnId, leistungParameter, scvUserRealisierung, scvRealisierung,
                        scvUserKuendigung, scvKuendigung, ewsdUserRealisierung, ewsdRealisierung, ewsdUserKuendigung,
                        ewsdKuendigung, billingCheck, parameterId, cpsTxIdCreation, cpsTxIdCancel);
    }

} // end


