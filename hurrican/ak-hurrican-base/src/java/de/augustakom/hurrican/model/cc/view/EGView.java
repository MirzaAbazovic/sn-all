/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2006 10:12:32
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Stellt die Ansicht eines Endgeraetes zu einem Auftrag und evtl. einem Paket dar
 *
 *
 */
public class EGView extends AbstractCCModel {

    private Long exportId = null; //ID aus T_EG_2_AUFTRAG
    private Long eg2AuftragProdakOrderNoOrig = null;
    private Long eg2AuftragProdakAdresseNo = null;
    private Long eg2AuftragEgId = null;
    private Long eg2AuftragPaketId = null;
    private String seriennummer = null;
    private String trackingNo = null;
    private Boolean selbstabholung = null;
    private Date ausgabedatum = null;
    private Date exportdatum = null;
    private Long egId = null;
    private Long egInterneId = null;
    private String egName = null;
    private String egBeschreibung = null;
    private Integer egTyp = null;
    private Long extLeistungNo = null;
    private Date egverfuegbarVon = null;
    private Date egVerfuegbarBis = null;
    private String egGarantiezeit = null;
    private Long paketId = null;
    private Long paketInterneId = null;
    private String paketName = null;
    private String paketBeschreibung = null;
    private Long paketTyp = null;
    private String paketBillingBez = null;
    private Date paketVerfuegbarVon = null;
    private Date paketVerfuegbarBis = null;
    private Long lieferadresseId = null;
    private Long versandStatusId = null;
    private Long versandArtId = null;

    /**
     * @return Returns the ausgabedatum.
     */
    public Date getAusgabedatum() {
        return ausgabedatum;
    }

    /**
     * @param ausgabedatum The ausgabedatum to set.
     */
    public void setAusgabedatum(Date ausgabedatum) {
        this.ausgabedatum = ausgabedatum;
    }

    /**
     * @return Returns the eg2AuftragEgId.
     */
    public Long getEg2AuftragEgId() {
        return eg2AuftragEgId;
    }

    /**
     * @param eg2AuftragEgId The eg2AuftragEgId to set.
     */
    public void setEg2AuftragEgId(Long eg2AuftragEgId) {
        this.eg2AuftragEgId = eg2AuftragEgId;
    }

    /**
     * @return Returns the eg2AuftragPaketId.
     */
    public Long getEg2AuftragPaketId() {
        return eg2AuftragPaketId;
    }

    /**
     * @param eg2AuftragPaketId The eg2AuftragPaketId to set.
     */
    public void setEg2AuftragPaketId(Long eg2AuftragPaketId) {
        this.eg2AuftragPaketId = eg2AuftragPaketId;
    }

    /**
     * @return Returns the eg2AuftragProdakOrderNoOrig.
     */
    public Long getEg2AuftragProdakOrderNoOrig() {
        return eg2AuftragProdakOrderNoOrig;
    }

    /**
     * @param eg2AuftragProdakOrderNoOrig The eg2AuftragProdakOrderNoOrig to set.
     */
    public void setEg2AuftragProdakOrderNoOrig(Long eg2AuftragProdakOrderNoOrig) {
        this.eg2AuftragProdakOrderNoOrig = eg2AuftragProdakOrderNoOrig;
    }

    /**
     * @return Returns the egBeschreibung.
     */
    public String getEgBeschreibung() {
        return egBeschreibung;
    }

    /**
     * @param egBeschreibung The egBeschreibung to set.
     */
    public void setEgBeschreibung(String egBeschreibung) {
        this.egBeschreibung = egBeschreibung;
    }

    /**
     * @return Returns the egGarantiezeit.
     */
    public String getEgGarantiezeit() {
        return egGarantiezeit;
    }

    /**
     * @param egGarantiezeit The egGarantiezeit to set.
     */
    public void setEgGarantiezeit(String egGarantiezeit) {
        this.egGarantiezeit = egGarantiezeit;
    }

    /**
     * @return Returns the egId.
     */
    public Long getEgId() {
        return egId;
    }

    /**
     * @param egId The egId to set.
     */
    public void setEgId(Long egId) {
        this.egId = egId;
    }

    /**
     * @return Returns the egInterneId.
     */
    public Long getEgInterneId() {
        return egInterneId;
    }

    /**
     * @param egInterneId The egInterneId to set.
     */
    public void setEgInterneId(Long egIntenrneId) {
        this.egInterneId = egIntenrneId;
    }

    /**
     * @return Returns the egName.
     */
    public String getEgName() {
        return egName;
    }

    /**
     * @param egName The egName to set.
     */
    public void setEgName(String egName) {
        this.egName = egName;
    }

    /**
     * @return Returns the egTyp.
     */
    public Integer getEgTyp() {
        return egTyp;
    }

    /**
     * @param egTyp The egTyp to set.
     */
    public void setEgTyp(Integer egTyp) {
        this.egTyp = egTyp;
    }

    /**
     * @return Returns the egVerfuegbarBis.
     */
    public Date getEgVerfuegbarBis() {
        return egVerfuegbarBis;
    }

    /**
     * @param egVerfuegbarBis The egVerfuegbarBis to set.
     */
    public void setEgVerfuegbarBis(Date egVerfuegbarBis) {
        this.egVerfuegbarBis = egVerfuegbarBis;
    }

    /**
     * @return Returns the egverfuegbarVon.
     */
    public Date getEgverfuegbarVon() {
        return egverfuegbarVon;
    }

    /**
     * @param egverfuegbarVon The egverfuegbarVon to set.
     */
    public void setEgverfuegbarVon(Date egverfuegbarVon) {
        this.egverfuegbarVon = egverfuegbarVon;
    }

    /**
     * @return Returns the exportdatum.
     */
    public Date getExportdatum() {
        return exportdatum;
    }

    /**
     * @param exportdatum The exportdatum to set.
     */
    public void setExportdatum(Date exportdatum) {
        this.exportdatum = exportdatum;
    }

    /**
     * @return Returns the exportId.
     */
    public Long getExportId() {
        return exportId;
    }

    /**
     * @param exportId The exportId to set.
     */
    public void setExportId(Long exportId) {
        this.exportId = exportId;
    }

    /**
     * @return Returns the paketBeschreibung.
     */
    public String getPaketBeschreibung() {
        return paketBeschreibung;
    }

    /**
     * @param paketBeschreibung The paketBeschreibung to set.
     */
    public void setPaketBeschreibung(String paketBeschreibung) {
        this.paketBeschreibung = paketBeschreibung;
    }

    /**
     * @return Returns the paketBillingBez.
     */
    public String getPaketBillingBez() {
        return paketBillingBez;
    }

    /**
     * @param paketBillingBez The paketBillingBez to set.
     */
    public void setPaketBillingBez(String paketBillingBez) {
        this.paketBillingBez = paketBillingBez;
    }

    /**
     * @return Returns the paketId.
     */
    public Long getPaketId() {
        return paketId;
    }

    /**
     * @param paketId The paketId to set.
     */
    public void setPaketId(Long paketId) {
        this.paketId = paketId;
    }

    /**
     * @return Returns the paketInterneId.
     */
    public Long getPaketInterneId() {
        return paketInterneId;
    }

    /**
     * @param paketInterneId The paketInterneId to set.
     */
    public void setPaketInterneId(Long paketInterneId) {
        this.paketInterneId = paketInterneId;
    }

    /**
     * @return Returns the paketName.
     */
    public String getPaketName() {
        return paketName;
    }

    /**
     * @param paketName The paketName to set.
     */
    public void setPaketName(String paketName) {
        this.paketName = paketName;
    }

    /**
     * @return Returns the paketTyp.
     */
    public Long getPaketTyp() {
        return paketTyp;
    }

    /**
     * @param paketTyp The paketTyp to set.
     */
    public void setPaketTyp(Long paketTyp) {
        this.paketTyp = paketTyp;
    }

    /**
     * @return Returns the paketVerfuegbarBis.
     */
    public Date getPaketVerfuegbarBis() {
        return paketVerfuegbarBis;
    }

    /**
     * @param paketVerfuegbarBis The paketVerfuegbarBis to set.
     */
    public void setPaketVerfuegbarBis(Date paketVerfuegbarBis) {
        this.paketVerfuegbarBis = paketVerfuegbarBis;
    }

    /**
     * @return Returns the paketVerfuegbarVon.
     */
    public Date getPaketVerfuegbarVon() {
        return paketVerfuegbarVon;
    }

    /**
     * @param paketVerfuegbarVon The paketVerfuegbarVon to set.
     */
    public void setPaketVerfuegbarVon(Date paketVerfuegbarVon) {
        this.paketVerfuegbarVon = paketVerfuegbarVon;
    }

    /**
     * @return Returns the selbstabholung.
     */
    public Boolean getSelbstabholung() {
        return selbstabholung;
    }

    /**
     * @param selbstabholung The selbstabholung to set.
     */
    public void setSelbstabholung(Boolean selbstabholung) {
        this.selbstabholung = selbstabholung;
    }

    /**
     * @return Returns the seriennummer.
     */
    public String getSeriennummer() {
        return seriennummer;
    }

    /**
     * @param seriennummer The seriennummer to set.
     */
    public void setSeriennummer(String seriennummer) {
        this.seriennummer = seriennummer;
    }

    /**
     * @return Returns the eg2AuftragProdakAdresseNo.
     */
    public Long getEg2AuftragProdakAdresseNo() {
        return eg2AuftragProdakAdresseNo;
    }

    /**
     * @param eg2AuftragProdakAdresseNo The eg2AuftragProdakAdresseNo to set.
     */
    public void setEg2AuftragProdakAdresseNo(Long eg2AuftragProdakAdresseNo) {
        this.eg2AuftragProdakAdresseNo = eg2AuftragProdakAdresseNo;
    }

    /**
     * @return Returns the extLeistungNo.
     */
    public Long getExtLeistungNo() {
        return this.extLeistungNo;
    }

    /**
     * @param extLeistungNo The extLeistungNo to set.
     */
    public void setExtLeistungNo(Long extLeistungNo) {
        this.extLeistungNo = extLeistungNo;
    }

    /**
     * @return Returns the trackingNo.
     */
    public String getTrackingNo() {
        return trackingNo;
    }


    /**
     * @param trackingNo The trackingNo to set.
     */
    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    /**
     * @return lieferadresseId
     */
    public Long getLieferadresseId() {
        return lieferadresseId;
    }

    /**
     * @param lieferadresseId Festzulegender lieferadresseId
     */
    public void setLieferadresseId(Long lieferadresseId) {
        this.lieferadresseId = lieferadresseId;
    }

    /**
     * @return versandArtId
     */
    public Long getVersandArtId() {
        return versandArtId;
    }

    /**
     * @param versandArtId Festzulegender versandArtId
     */
    public void setVersandArtId(Long versandArtId) {
        this.versandArtId = versandArtId;
    }

    /**
     * @return versandStatusId
     */
    public Long getVersandStatusId() {
        return versandStatusId;
    }

    /**
     * @param versandStatusId Festzulegender versandStatusId
     */
    public void setVersandStatusId(Long versandStatusId) {
        this.versandStatusId = versandStatusId;
    }

}


