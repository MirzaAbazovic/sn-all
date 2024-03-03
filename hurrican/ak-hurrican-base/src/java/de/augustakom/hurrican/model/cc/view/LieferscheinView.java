/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2007 11:39:32
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * View für die Darstellung vonm Lieferscheinen
 *
 *
 */
public class LieferscheinView extends AbstractCCModel implements KundenModel {

    private Long lieferscheinId = null;
    private String prefix = null;
    private Long auftragId = null;
    private Long orderNoOrig = null;
    private Long kundeNo = null;
    private Long lieferscheinStatusId = null;
    private String lieferscheinStatus = null;
    private String lieferadresse = null;
    private Date printedAt = null;
    private Date generatedAt = null;
    private Integer anzahlEGs = null;
    private Long requestId = null;
    private Date realisierungstermin = null;
    private Long retoureGrundId = null;
    private String retoureGrund = null;
    private String auftragBearbeiter = null;
    private String lsBearbeiter = null;
    private Date versandDatum = null;
    private Date versandDatumExtern = null;
    private Boolean sofortVersand = null;
    private String trackingNo = null;

    /**
     * @return anzahlEGs
     */
    public Integer getAnzahlEGs() {
        return anzahlEGs;
    }

    /**
     * @param anzahlEGs Festzulegender anzahlEGs
     */
    public void setAnzahlEGs(Integer anzahlEGs) {
        this.anzahlEGs = anzahlEGs;
    }

    /**
     * @return auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId Festzulegender auftragId
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return generatedAt
     */
    public Date getGeneratedAt() {
        return generatedAt;
    }

    /**
     * @param generatedAt Festzulegender generatedAt
     */
    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
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
     * @return lieferadresse
     */
    public String getLieferadresse() {
        return lieferadresse;
    }

    /**
     * @param lieferadresse Festzulegender lieferadresse
     */
    public void setLieferadresse(String lieferadresse) {
        this.lieferadresse = lieferadresse;
    }

    /**
     * @return lieferscheinId
     */
    public Long getLieferscheinId() {
        return lieferscheinId;
    }

    /**
     * @param lieferscheinId Festzulegender lieferscheinId
     */
    public void setLieferscheinId(Long lieferscheinId) {
        this.lieferscheinId = lieferscheinId;
    }

    /**
     * @return lieferscheinStatus
     */
    public String getLieferscheinStatus() {
        return lieferscheinStatus;
    }

    /**
     * @param lieferscheinStatus Festzulegender lieferscheinStatus
     */
    public void setLieferscheinStatus(String lieferscheinStatus) {
        this.lieferscheinStatus = lieferscheinStatus;
    }

    /**
     * @return lieferscheinStatusId
     */
    public Long getLieferscheinStatusId() {
        return lieferscheinStatusId;
    }

    /**
     * @param lieferscheinStatusId Festzulegender lieferscheinStatusId
     */
    public void setLieferscheinStatusId(Long lieferscheinStatusId) {
        this.lieferscheinStatusId = lieferscheinStatusId;
    }

    /**
     * @return orderNoOrig
     */
    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    /**
     * @param orderNoOrig Festzulegender orderNoOrig
     */
    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    /**
     * @return printedAt
     */
    public Date getPrintedAt() {
        return printedAt;
    }

    /**
     * @param printedAt Festzulegender printedAt
     */
    public void setPrintedAt(Date printedAt) {
        this.printedAt = printedAt;
    }

    /**
     * @return requestId
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * @param requestId Festzulegender requestId
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * @return realisierungsDatum
     */
    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    /**
     * @param realisierungsDatum Festzulegender realisierungsDatum
     */
    public void setRealisierungstermin(Date realisierungsDatum) {
        this.realisierungstermin = realisierungsDatum;
    }

    /**
     * @return retoureGrund
     */
    public String getRetoureGrund() {
        return retoureGrund;
    }

    /**
     * @param retoureGrund Festzulegender retoureGrund
     */
    public void setRetoureGrund(String retoureGrund) {
        this.retoureGrund = retoureGrund;
    }

    /**
     * @return retoureGrundId
     */
    public Long getRetoureGrundId() {
        return retoureGrundId;
    }

    /**
     * @param retoureGrundId Festzulegender retoureGrundId
     */
    public void setRetoureGrundId(Long retoureGrundId) {
        this.retoureGrundId = retoureGrundId;
    }

    /**
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix Festzulegender prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return auftragBearbeiter
     */
    public String getAuftragBearbeiter() {
        return auftragBearbeiter;
    }

    /**
     * @param auftragBearbeiter Festzulegender auftragBearbeiter
     */
    public void setAuftragBearbeiter(String auftragBearbeiter) {
        this.auftragBearbeiter = auftragBearbeiter;
    }

    /**
     * @return lsBearbeiter
     */
    public String getLsBearbeiter() {
        return lsBearbeiter;
    }

    /**
     * @param lsBearbeiter Festzulegender lsBearbeiter
     */
    public void setLsBearbeiter(String lsBearbeiter) {
        this.lsBearbeiter = lsBearbeiter;
    }

    /**
     * @return versandDatum
     */
    public Date getVersandDatum() {
        return versandDatum;
    }

    /**
     * @param versandDatum Festzulegender versandDatum
     */
    public void setVersandDatum(Date versandDatum) {
        this.versandDatum = versandDatum;
    }

    /**
     * @return sofortVersand
     */
    public Boolean getSofortVersand() {
        return sofortVersand;
    }

    /**
     * @param sofortVersand Festzulegender sofortVersand
     */
    public void setSofortVersand(Boolean sofortVersand) {
        this.sofortVersand = sofortVersand;
    }

    /**
     * @return trackingNo
     */
    public String getTrackingNo() {
        return trackingNo;
    }

    /**
     * @param trackingNo Festzulegender trackingNo
     */
    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    /**
     * @return versandDatumExtern
     */
    public Date getVersandDatumExtern() {
        return versandDatumExtern;
    }

    /**
     * @param versandDatumExtern Festzulegender versandDatumExtern
     */
    public void setVersandDatumExtern(Date versandDatumExtern) {
        this.versandDatumExtern = versandDatumExtern;
    }

}


