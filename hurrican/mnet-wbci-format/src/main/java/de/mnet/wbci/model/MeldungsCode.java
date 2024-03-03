/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.model;

import java.util.*;

/**
 *
 */
public enum MeldungsCode {

    /**
     * Zugestimmt wie angefragt.
     */
    ZWA("8001", "Auftragsbestätigung (ZWA)", MeldungPositionTyp.RUEM_VA),

    /**
     * Neuer Ausführungstermin.
     */
    NAT("8002", "Auftragsbestätigung (NAT)", MeldungPositionTyp.RUEM_VA),

    /**
     * Terminverschiebung ok.
     */
    TV_OK("8003", "Terminverschiebung ok", null, MeldungPositionTyp.ERLM),

    /**
     * Storno ok.
     */
    STORNO_OK("8004", "Storno ok", null, MeldungPositionTyp.ERLM),

    /**
     * Meldecode zur AKM-TR.
     */
    AKMTR_CODE("8010", "Antwort des aufnehmenden Anbieters", null, MeldungPositionTyp.AKM_TR),

    /**
     * Rufnummer nicht geschaltet.
     */
    RNG("8103", "Rufnummer nicht geschaltet (RNG)", MeldungPositionTyp.ABBM),

    /**
     * Von Adresse abweichende PLZ.
     */
    ADAPLZ("8104", "Adresse abweichend PLZ (ADAPLZ)", MeldungPositionTyp.RUEM_VA),

    /**
     * Von Adresse abweichender Ort.
     */
    ADAORT("8105", "Adresse abweichend Ort (ADAORT)", MeldungPositionTyp.RUEM_VA),

    /**
     * Von Adresse abweichende Straße.
     */
    ADASTR("8106", "Adresse abweichend Straße (ADASTR)", MeldungPositionTyp.RUEM_VA),

    /**
     * Von Adresse abweichende Hausnummer.
     */
    ADAHSNR("8107", "Adresse abweichend Hausnummer (ADAHSNR)", MeldungPositionTyp.RUEM_VA),

    /**
     * PLZ in Adresse falsch.
     */
    ADFPLZ("8108", "Adresse falsch Postleitzahl (ADFPLZ)", MeldungPositionTyp.ABBM),

    /**
     * Ort in Adresse falsch.
     */
    ADFORT("8109", "Adresse falsch Ort (ADFORT)", MeldungPositionTyp.ABBM),

    /**
     * Strasse in Adresse falsch.
     */
    ADFSTR("8110", "Adresse falsch Straße (ADFSTR)", MeldungPositionTyp.ABBM),

    /**
     * Hausnummer in Adresse falsch.
     */
    ADFHSNR("8111", "Adresse falsch Hausnummer (ADFHSNR)", MeldungPositionTyp.ABBM),

    /**
     * Anschlussinhaber falsch.
     */
    AIF("8112", "Anschlussinhaber falsch (AIF)", MeldungPositionTyp.ABBM),

    /**
     * Vorname von Anschlussinhaber falsch.
     */
    AIFVN("8113", "Anschlussinhaber falsch Vorname (AIFVN)", MeldungPositionTyp.ABBM),

    /**
     * Weitere Anschlussinhaber.
     */
    WAI("8114", "Weitere Anschlussinhaber (WAI)", MeldungPositionTyp.ABBM),

    /**
     * Bereits verwendete ID in der Anfrage.
     */
    BVID("8115", "Vorabstimmungs-,Terminverschiebungs-,Storno-, ID wird bereits verwendet.", MeldungPositionTyp.ABBM),

    /**
     * Sonstiges
     */
    SONST("8116", "Sonstiges", MeldungPositionTyp.ABBM),

    /**
     * Kunde nicht identifizerbar.
     */
    KNI("8117", "Kunde nicht identifizierbar (KNI)", MeldungPositionTyp.ABBM),

    /**
     * Vorabstimmung mit anderen oder gleichen EKP bereits erfolgt.
     */
    VAE("8118", "Vorabstimmung mit anderen oder gleichen EKP bereits erfolgt (VAE)", MeldungPositionTyp.ABBM),

    /**
     * Unbekannte Vorabstimmungs ID abgelehnt.
     */
    VAID_ABG("8140", "Vorabstimmungs ID nicht bekannt", MeldungPositionTyp.ABBM,
            MeldungPositionTyp.ABBM_TR),

    /**
     * Termiverschiebung abgelehnt.
     */
    TV_ABG("8141", "Terminverschiebung abgelehnt", MeldungPositionTyp.ABBM),

    /**
     * Storno abgelehnt.
     */
    STORNO_ABG("8142", "Storno abgelehnt", MeldungPositionTyp.ABBM),

    /**
     * WITA Vertragsnummer unbekannt.
     */
    WVNR_ABG("8143", "WITA Vertragsnummer unbekannt", null, MeldungPositionTyp.ABBM_TR),

    /**
     * Line ID unbekannt.
     */
    LID_ABG("8144", "Line ID unbekannt", null, MeldungPositionTyp.ABBM_TR),

    /**
     * WITA Vertragsnummer ohne passende Vorabstimmungs ID.
     */
    WVNR_OVAID("8145", "WITA Vertragsnummer gehört nicht zur Vorabstimmungs ID", null,
            MeldungPositionTyp.ABBM_TR),

    /**
     * Line ID ohne passende Vorabstimmungs ID.
     */
    LID_OVAID("8146", "Line ID gehört nicht zur Vorabstimmungs ID", null, MeldungPositionTyp.ABBM_TR),

    /**
     * Übernahme der technischen Ressource nicht möglich.
     */
    UETN_NM("8147", "Übernahme der Technischen Ressource nicht möglich", null, MeldungPositionTyp.ABBM_TR),

    /**
     * Übernahme der technischen Ressource wurde bereits bestätigt (Doppelte AKM-TR).
     */
    UETN_BB("8148", "Übernahme der Technischen Ressource wurde bereits bestätigt", null,
            MeldungPositionTyp.ABBM_TR),

    /**
     * Unbekannter Projektkenner.
     */
    UPK("8149", "Projektkenner unbekannt", MeldungPositionTyp.ABBM),

    /**
     * Medlungscode ist unbekannt.
     */
    UNKNOWN(null, null, null, null);
    private final String code;
    private final String standardText;
    private final List<MeldungPositionTyp> allowedMeldungPosTyps;

    private MeldungsCode(String code, String standardText, MeldungPositionTyp... allowedMeldungPosTyps) {
        this.code = code;
        this.standardText = standardText;
        this.allowedMeldungPosTyps = Arrays.asList(allowedMeldungPosTyps);
    }

    public static MeldungsCode buildFromCode(String code) {
        if (code != null) {
            for (MeldungsCode meldungsCode : MeldungsCode.values()) {
                if (meldungsCode.getCode() != null && meldungsCode.getCode().equals(code)) {
                    return meldungsCode;
                }
            }
        }
        return UNKNOWN;
    }

    public static MeldungsCode buildFromName(String name) {
        try {
            return MeldungsCode.valueOf(name);
        }
        catch (NullPointerException | IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * @return the error code, which is defined in the WBCI specification
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the standard Meldungstext, which is defined in the WBCI specification
     */
    public String getStandardText() {
        return standardText;
    }

    /**
     * checks if this MeldungsCode is allowed.
     *
     * @param meldungPosTyps which should be checked
     * @return true if is allowed, false if not.
     */
    public boolean isValidForMeldungPosTyps(MeldungPositionTyp... meldungPosTyps) {
        return allowedMeldungPosTyps.containsAll(Arrays.asList(meldungPosTyps));
    }

    /**
     * @return true if the Meldungscode equals to {@link #ADAHSNR}, {@link #ADAORT}, {@link #ADAPLZ} or {@link #ADASTR}.
     */
    public boolean isADACode() {
        return getADACodes().contains(this);
    }

    /**
     * Returns the list of all ADA meldungscodes
     *
     * @return
     */
    public static List<MeldungsCode> getADACodes() {
        return Arrays.asList(ADAHSNR, ADAORT, ADAPLZ, ADASTR);
    }

    /**
     * @return true if the Meldungscode equals to {@link #ADFHSNR}, {@link #ADFORT}, {@link #ADFPLZ} or {@link #ADFSTR}.
     */
    public boolean isADFCode() {
        return getADFCodes().contains(this);
    }

    /**
     * Returns the list of all ADF meldungscodes
     *
     * @return
     */
    public static List<MeldungsCode> getADFCodes() {
        return Arrays.asList(ADFHSNR, ADFORT, ADFPLZ, ADFSTR);
    }
}
