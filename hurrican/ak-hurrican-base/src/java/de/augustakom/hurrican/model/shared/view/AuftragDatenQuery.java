/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 12:15:57
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche ueber Auftragsdaten.
 *
 *
 */
public class AuftragDatenQuery extends AbstractHurricanQuery implements KundenModel {

    private static final long serialVersionUID = -9060334257836769965L;

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private String bestellNr = null;
    private String lbzKunde = null;
    private Long auftragStatusMin = null;
    private Long auftragStatusMax = null;
    private Long prodId = null;
    private Long kundeNo = null;
    private Long produktGruppeId = null;
    private String intAccount = null;
    private Long auftragStatusId;
    private Boolean isVierDraht = null;

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the bestellNr.
     */
    public String getBestellNr() {
        return bestellNr;
    }

    /**
     * @param bestellNr The bestellNr to set.
     */
    public void setBestellNr(String bestellNr) {
        this.bestellNr = bestellNr;
    }

    /**
     * @return Returns the auftragStatusMax.
     */
    public Long getAuftragStatusMax() {
        return auftragStatusMax;
    }

    /**
     * @param auftragStatusMax The auftragStatusMax to set.
     */
    public void setAuftragStatusMax(Long auftragStatusMax) {
        this.auftragStatusMax = auftragStatusMax;
    }

    /**
     * @return Returns the auftragStatusMin.
     */
    public Long getAuftragStatusMin() {
        return auftragStatusMin;
    }

    /**
     * @param auftragStatusMin The auftragStatusMin to set.
     */
    public void setAuftragStatusMin(Long auftragStatusMin) {
        this.auftragStatusMin = auftragStatusMin;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the produktGruppeId.
     */
    public Long getProduktGruppeId() {
        return produktGruppeId;
    }

    /**
     * @param produktGruppeId The produktGruppeId to set.
     */
    public void setProduktGruppeId(Long produktGruppeId) {
        this.produktGruppeId = produktGruppeId;
    }

    /**
     * @return Returns the intAccount.
     */
    public String getIntAccount() {
        return intAccount;
    }

    /**
     * @param intAccount The intAccount to set.
     */
    public void setIntAccount(String intAccount) {
        this.intAccount = intAccount;
    }

    /**
     * @return Returns the lbzKunde.
     */
    public String getLbzKunde() {
        return lbzKunde;
    }

    /**
     * @param lbzKunde The lbzKunde to set.
     */
    public void setLbzKunde(String lbzKunde) {
        this.lbzKunde = lbzKunde;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return this.prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }


    public Long getAuftragStatusId() {
        return auftragStatusId;
    }

    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;

    }

    /**
     * @return the isVierDraht
     */
    public Boolean getIsVierDraht() {
        return isVierDraht;
    }

    /**
     * @param isVierDraht the isVierDraht to set
     */
    public void setIsVierDraht(Boolean isVierDraht) {
        this.isVierDraht = isVierDraht;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getBestellNr())) {
            return false;
        }
        if (StringUtils.isNotBlank(getLbzKunde())) {
            return false;
        }
        if (getAuftragId() != null) {
            return false;
        }
        if (getAuftragNoOrig() != null) {
            return false;
        }
        if (getAuftragStatusMin() != null) {
            return false;
        }
        if (getAuftragStatusMax() != null) {
            return false;
        }
        if (getProdId() != null) {
            return false;
        }
        if (getKundeNo() != null) {
            return false;
        }
        if (getProduktGruppeId() != null) {
            return false;
        }
        if (getAuftragStatusId() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getIntAccount())) {
            return false;
        }
        if (getIsVierDraht() != null) {
            return false;
        }

        return true;
    }


}


