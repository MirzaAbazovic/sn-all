/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.constraints.CheckAbbmTermin;
import de.mnet.wbci.validation.constraints.CheckOutgoingAbbmBegruendung;
import de.mnet.wbci.validation.constraints.CheckOutgoingAbbmMeldungsCodes;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1MeldungStorno;
import de.mnet.wbci.validation.groups.V1MeldungTv;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ABBM")
@CheckAbbmTermin(groups = V1MeldungTv.class)
@CheckOutgoingAbbmBegruendung(groups = V1Meldung.class)
@CheckOutgoingAbbmMeldungsCodes(groups = V1Meldung.class)
public class Abbruchmeldung extends Meldung<MeldungPositionAbbruchmeldung> implements AenderungsIdAware, StornoIdAware {

    private static final long serialVersionUID = -3941277745630658635L;

    public static final String BEGRUENDUNG_DOPPELTE_VA =
            "Eine aktive Vorabstimmung mit der VA-ID: %s ist bereits vorhanden";
    public static final String BEGRUENDUNG_DOPPELTE_TV =
            "Terminverschiebung ungueltig, weil zur Vorabstimmung bereits eine Terminverschiebung aktiv ist, die noch nicht beantwortet wurde.";
    public static final String BEGRUENDUNG_ABBM_TV_KEINE_RUEMVA =
            "Terminverschiebung ungueltig, weil bisher zur Vorabstimmung noch keine RUEM-VA verschickt wurde.";
    public static final String BEGRUENDUNG_ABBM_TV_DONATING_CARRIER =
            "Terminverschiebung ungueltig, weil sie vom abgebenden Provider verschickt wurde.";
    public static final String BEGRUENDUNG_TV_UNBEKANNTE_VA_REFID =
            "Terminverschiebung ungueltig, weil die erhaltene VorabstimmungsId unbekannt ist.";
    public static final String BEGRUENDUNG_ABBM_STORNO_KEINE_RUEMVA =
            "Stornoanfrage ungueltig, weil bisher zur Vorabstimmung keine Antwort erhalten wurde.";
    public static final String BEGRUENDUNG_STORNO_UNBEKANNTE_VA_REFID =
            "Stornoanfrage ungueltig, weil die erhaltene VorabstimmungsId unbekannt ist.";
    public static final String BEGRUENDUNG_DOPPELTE_STORNO =
            "Stornoanfrage ungueltig, weil zur Vorabstimmung bereits eine Stornoanfrage aktiv ist, die noch nicht beantwortet wurde.";
    public static final String BEGRUENDUNG_AENDERUNG_VORGEHALTEN =
            "Anfrage ungueltig, weil zur Vorabstimmung bereits eine aktive %s Anfrage vorhanden ist, die demnaechst rausgeschickt wird.";
    public static final String BEGRUENDUNG_AENDERUNG_AKTIV =
            "Anfrage ungueltig, weil zur Vorabstimmung bereits eine %s Anfrage aktiv ist, die noch nicht beantwortet wurde.";
    public static final String BEGRUENDUNG_FRIST_UNTERSCHRITTEN = "Frist unterschritten.";
    public static final String BEGRUENDUNG_WECHSEL_RRNP_MRNORN =
            "Ein Wechsel des Geschäftsfall-Typs von RRNP zu KUE-MRN bzw KUE-ORN über eine STR-AEN ist nicht erlaubt! Ursprüngliche VA-Id: %s";

    private LocalDate wechseltermin;
    private String stornoIdRef;
    private String aenderungsIdRef;
    private String begruendung;

    public Abbruchmeldung() {
        super(MeldungTyp.ABBM);
    }

    @NotNull(groups = V1MeldungTv.class)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getWechseltermin() {
        return wechseltermin;
    }

    public void setWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
    }

    @NotEmpty(groups = V1MeldungStorno.class)
    @Column(name = "STORNO_ID_REF")
    public String getStornoIdRef() {
        return stornoIdRef;
    }

    public void setStornoIdRef(String stornoIdRef) {
        this.stornoIdRef = stornoIdRef;
    }

    @NotEmpty(groups = V1MeldungTv.class)
    @Column(name = "AENDERUNG_ID_REF")
    public String getAenderungsIdRef() {
        return aenderungsIdRef;
    }

    public void setAenderungsIdRef(String aenderungsIdRef) {
        this.aenderungsIdRef = aenderungsIdRef;
    }

    @NotEmpty(groups = V1MeldungStorno.class)
    @Column(name = "BEGRUENDUNG")
    public String getBegruendung() {
        return begruendung;
    }

    public void setBegruendung(String begruendung) {
        this.begruendung = begruendung;
    }

    /**
     * Helper to determine if the Abbruchmeldung is for a Vorabstimmung. This helper distinguishes between Vorabstimmung
     * Abbruchmeldungen and Storno or TV Abbruchmeldungen.
     *
     * @return true if this Abbruchmeldung is for a VA, otherwise false.
     */
    @Transient
    public boolean isAbbruchmeldungForVorabstimmung() {
        return this.getClass().equals(Abbruchmeldung.class);
    }

    /**
     * Examines the Meldung to see if it contains MeldungsCodes other than <b>just</b> ADF MeldungsCodes.
     *
     * @return true when only ADF codes are found
     */
    @Transient
    public boolean isMeldungWithOnlyADFCodes() {
        Set<MeldungPositionAbbruchmeldung> meldungsPositionen = getMeldungsPositionen();
        if(meldungsPositionen != null && !meldungsPositionen.isEmpty()) {
            for (MeldungPosition meldungPosition : meldungsPositionen) {
                if(!meldungPosition.getMeldungsCode().isADFCode()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Ergebnis der Methode ist {@code true}, wenn die Meldung keine MeldungsPosition enthaelt.
     */
    @Transient
    public boolean isMeldungLackingAnyMeldungsPosition() {
        return getMeldungsPositionen() == null || getMeldungsPositionen().isEmpty();
    }

    @Override
    public String toString() {
        return "Abbruchmeldung [begruendung=" + getBegruendung() + ", wechselTermin=" + getWechseltermin() +
                ", aenderungsIdRef" + getAenderungsIdRef() + ", stornoIdRef=" + getStornoIdRef() +
                ", toString=" + super.toString() + "]";
    }

}
