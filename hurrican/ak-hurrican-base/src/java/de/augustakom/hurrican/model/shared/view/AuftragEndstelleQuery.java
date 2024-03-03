/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 10:37:25
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche uber Enstellen- und Leitungsdaten.
 *
 *
 */
public class AuftragEndstelleQuery extends AbstractHurricanQuery {

    // Filter-Parameter
    private String vbz = null;
    private Long endstelleId = null;
    private String endstelle = null;
    private String endstelleOrt = null;
    private Long vpnId = null;
    private String endstelleTyp = null;
    private Boolean inVPN = null;
    private List<Long> kundeNos = null;
    private Long rangierId = null;
    private Long cb2EsID = null;
    private String ltgArt = null;

    // Order-Parameter
    private boolean orderByKundeNo = false;
    private boolean orderByAuftragId = false;
    private boolean orderByAuftragNoOrig = false;

    /**
     * @return Returns the endstelle.
     */
    public String getEndstelle() {
        return endstelle;
    }

    /**
     * @param endstelle The endstelle to set.
     */
    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    /**
     * @return Returns the endstelleOrt.
     */
    public String getEndstelleOrt() {
        return endstelleOrt;
    }

    /**
     * @param endstelleOrt The endstelleOrt to set.
     */
    public void setEndstelleOrt(String endstelleOrt) {
        this.endstelleOrt = endstelleOrt;
    }

    /**
     * @return Returns the endstelleId.
     */
    public Long getEndstelleId() {
        return endstelleId;
    }

    /**
     * @param endstelleId The endstelleId to set.
     */
    public void setEndstelleId(Long endstelleId) {
        this.endstelleId = endstelleId;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    /**
     * @return Returns the endstelleTyp.
     */
    public String getEndstelleTyp() {
        return endstelleTyp;
    }

    /**
     * @param endstelleTyp The endstelleTyp to set.
     */
    public void setEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
    }

    /**
     * @return Returns the vpnId.
     */
    public Long getVpnId() {
        return vpnId;
    }

    /**
     * @param vpnId The vpnId to set.
     */
    public void setVpnId(Long vpnId) {
        this.vpnId = vpnId;
    }

    /**
     * @return Returns the inVPN.
     */
    public Boolean getInVPN() {
        return inVPN;
    }

    /**
     * @param inVPN The inVPN to set.
     */
    public void setInVPN(Boolean inVPN) {
        this.inVPN = inVPN;
    }

    /**
     * @return Returns the kundeNos.
     */
    public List<Long> getKundeNos() {
        return kundeNos;
    }

    /**
     * @param kundeNos The kundeNos to set.
     */
    public void setKundeNos(List<Long> kundeNos) {
        this.kundeNos = kundeNos;
    }

    /**
     * @return Returns the orderByAuftragId.
     */
    public boolean isOrderByAuftragId() {
        return orderByAuftragId;
    }

    /**
     * @param orderByAuftragId The orderByAuftragId to set.
     */
    public void setOrderByAuftragId(boolean orderByAuftragId) {
        this.orderByAuftragId = orderByAuftragId;
    }

    /**
     * @return Returns the orderByAuftragNoOrig.
     */
    public boolean isOrderByAuftragNoOrig() {
        return orderByAuftragNoOrig;
    }

    /**
     * @param orderByAuftragNoOrig The orderByAuftragNoOrig to set.
     */
    public void setOrderByAuftragNoOrig(boolean orderByAuftragNoOrig) {
        this.orderByAuftragNoOrig = orderByAuftragNoOrig;
    }

    /**
     * @return Returns the orderByKundeNo.
     */
    public boolean isOrderByKundeNo() {
        return orderByKundeNo;
    }

    /**
     * @param orderByKundeNo The orderByKundeNo to set.
     */
    public void setOrderByKundeNo(boolean orderByKundeNo) {
        this.orderByKundeNo = orderByKundeNo;
    }

    /**
     * @return Returns the rangierId.
     */
    public Long getRangierId() {
        return rangierId;
    }

    /**
     * @param rangierId The rangierId to set.
     */
    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    /**
     * @return Returns the cb2EsID.
     */
    public Long getCb2EsID() {
        return cb2EsID;
    }

    /**
     * @param cb2EsID The cb2EsID to set.
     */
    public void setCb2EsID(Long cb2EsID) {
        this.cb2EsID = cb2EsID;
    }

    /**
     * @return Returns the ltgArt.
     */
    public String getLtgArt() {
        return ltgArt;
    }

    /**
     * @param ltgArt The ltgArt to set.
     */
    public void setLtgArt(String ltgArt) {
        this.ltgArt = ltgArt;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getEndstelleId() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getEndstelle())) {
            return false;
        }
        if (StringUtils.isNotBlank(getEndstelleOrt())) {
            return false;
        }
        if (StringUtils.isNotBlank(getVbz())) {
            return false;
        }
        if (StringUtils.isNotBlank(getEndstelleTyp())) {
            return false;
        }
        if (getVpnId() != null) {
            return false;
        }
        if (getInVPN() != null) {
            return false;
        }
        if (getKundeNos() != null) {
            return false;
        }
        if (getRangierId() != null) {
            return false;
        }
        if (getCb2EsID() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getLtgArt())) {
            return false;
        }

        return true;
    }

}


