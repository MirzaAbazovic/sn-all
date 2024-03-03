/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 14:40:08
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell bildet einen Abteilungs-bezogenen Verlauf ab.
 *
 *
 */
public class VerlaufAbteilung extends AbstractCCIDModel {

    public static final int MAX_CHARS_BEMERKUNG = 1000;

    /**
     * {@link Reference}-Id fuer "technisch nicht realisierbar"
     */
    public static final Long NOT_POSSIBLE_REF_ID_TNFE = 1304L;
    /**
     * {@link Reference}-Id fuer "Kunde nicht vor Ort"
     */
    public static final Long NOT_POSSIBLE_REF_ID_CUST = 1306L;
    /**
     * {@link Reference}-Id fuer "sonstiges"
     */
    public static final Long NOT_POSSIBLE_REF_ID_OTHER = 1307L;

    private Long verlaufId;
    public static final String VERLAUF_ID = "verlaufId";
    private Long abteilungId;
    public static final String ABTEILUNG_ID = "abteilungId";
    /**
     * Id des VerlaufAbteilung-Objekts, aus dem dieses Objekt generiert wurde.
     */
    private Long parentVerlaufAbteilungId;
    public static final String PARENT_VERLAUF_ABTEILUNG_ID = "parentVerlaufAbteilungId";
    private Date datumAn;
    private Date datumErledigt;
    private Date ausgetragenAm;
    private String ausgetragenVon;
    private Long verlaufStatusId;
    private String bearbeiter;
    public static final int BEMERKUNG_MAX_LENGTH = 999;
    private String bemerkung;
    private Date realisierungsdatum;
    private Long extServiceProviderId;
    private Long niederlassungId;
    private Long zusatzAufwand;
    private Boolean notPossible;
    private Long notPossibleReasonRefId;
    private Date wiedervorlage;
    private Long verlaufAbteilungStatusId;


    public boolean isErledigt() {
        return NumberTools.isIn(getVerlaufStatusId(), new Number[]{
                VerlaufStatus.STATUS_ERLEDIGT,
                VerlaufStatus.STATUS_ERLEDIGT_CPS,
                VerlaufStatus.STATUS_ERLEDIGT_SYSTEM});
    }

    public Long getAbteilungId() {
        return abteilungId;
    }

    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getDatumAn() {
        return datumAn;
    }

    public void setDatumAn(Date datumAn) {
        this.datumAn = datumAn;
    }

    public Date getDatumErledigt() {
        return datumErledigt;
    }

    public void setDatumErledigt(Date datumErledigt) {
        this.datumErledigt = datumErledigt;
    }

    public Long getVerlaufId() {
        return verlaufId;
    }

    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    public Long getVerlaufStatusId() {
        return verlaufStatusId;
    }

    public void setVerlaufStatusId(Long verlaufStatusId) {
        this.verlaufStatusId = verlaufStatusId;
    }

    public Date getAusgetragenAm() {
        return ausgetragenAm;
    }

    public void setAusgetragenAm(Date ausgetragenAm) {
        this.ausgetragenAm = ausgetragenAm;
    }

    public String getAusgetragenVon() {
        return ausgetragenVon;
    }

    public void setAusgetragenVon(String ausgetragenVon) {
        this.ausgetragenVon = ausgetragenVon;
    }

    public Long getExtServiceProviderId() {
        return extServiceProviderId;
    }

    public void setExtServiceProviderId(Long extServiceProviderId) {
        this.extServiceProviderId = extServiceProviderId;
    }

    public Date getRealisierungsdatum() {
        return realisierungsdatum;
    }

    public void setRealisierungsdatum(Date realisierungsdatum) {
        this.realisierungsdatum = realisierungsdatum;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Long getZusatzAufwand() {
        return zusatzAufwand;
    }

    public void setZusatzAufwand(Long zusatzAufwand) {
        this.zusatzAufwand = zusatzAufwand;
    }

    public Boolean getNotPossible() {
        return notPossible;
    }

    public void setNotPossible(Boolean notPossible) {
        this.notPossible = notPossible;
    }

    public Long getNotPossibleReasonRefId() {
        return notPossibleReasonRefId;
    }

    public void setNotPossibleReasonRefId(Long notPossibleReasonRefId) {
        this.notPossibleReasonRefId = notPossibleReasonRefId;
    }

    public Long getParentVerlaufAbteilungId() {
        return parentVerlaufAbteilungId;
    }

    public void setParentVerlaufAbteilungId(Long parentVerlaufAbteilungId) {
        this.parentVerlaufAbteilungId = parentVerlaufAbteilungId;
    }

    public Date getWiedervorlage() {
        return wiedervorlage;
    }

    public void setWiedervorlage(Date wiedervorlage) {
        this.wiedervorlage = wiedervorlage;
    }

    public Long getVerlaufAbteilungStatusId() {
        return verlaufAbteilungStatusId;
    }

    public void setVerlaufAbteilungStatusId(Long abteilungStatusId) {
        this.verlaufAbteilungStatusId = abteilungStatusId;
    }
}


