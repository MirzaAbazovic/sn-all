/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2011 14:36:49
 */
package de.mnet.wita.message.meldung.position;

import static com.google.common.collect.Lists.*;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.StandortKundeKorrektur;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.validators.groups.V1;

@Entity
@Table(name = "T_MWF_POSITIONSATTRIBUTE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_POSITIONSATTRIBUTE_0", allocationSize = 1)
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class Positionsattribute extends MwfEntity {

    private static final long serialVersionUID = -1367978890108331823L;

    private LocalDate erledigungsterminOffenerAuftrag;
    private String alternativprodukt;
    private StandortKundeKorrektur standortKundeKorrektur;
    private String fehlauftragsnummer;
    private List<LeitungsBezeichnung> doppeladerBelegt = newArrayList();
    private AnschlussPortierungKorrekt anschlussPortierungKorrekt;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "ERLTERMIN_OFFENER_AUFTRAG")
    public LocalDate getErledigungsterminOffenerAuftrag() {
        return erledigungsterminOffenerAuftrag;
    }

    public void setErledigungsterminOffenerAuftrag(LocalDate erledigungsterminOffenerAuftrag) {
        this.erledigungsterminOffenerAuftrag = erledigungsterminOffenerAuftrag;
    }

    @Size(groups = V1.class, min = 1, max = 100, message = "Das Alternativprodukt muss zwischen {min} und {max} Zeichen haben.")
    public String getAlternativprodukt() {
        return alternativprodukt;
    }

    public void setAlternativprodukt(String alternativprodukt) {
        this.alternativprodukt = alternativprodukt;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STANDORT_KUNDE_KORREKTUR_ID")
    public StandortKundeKorrektur getStandortKundeKorrektur() {
        return standortKundeKorrektur;
    }

    public void setStandortKundeKorrektur(StandortKundeKorrektur standortKundeKorrektur) {
        this.standortKundeKorrektur = standortKundeKorrektur;
    }

    public String getFehlauftragsnummer() {
        return fehlauftragsnummer;
    }

    public void setFehlauftragsnummer(String fehlauftragsnummer) {
        this.fehlauftragsnummer = fehlauftragsnummer;
    }

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "POSITIONSATTRIBUTE_ID")
    public List<LeitungsBezeichnung> getDoppeladerBelegt() {
        return doppeladerBelegt;
    }

    @Transient
    public void addDoppeladerBelegt(LeitungsBezeichnung doppeladerToAdd) {
        doppeladerBelegt.add(doppeladerToAdd);
    }

    /**
     * Required by Hibernate
     */
    @SuppressWarnings("unused")
    private void setDoppeladerBelegt(List<LeitungsBezeichnung> doppeladerBelegt) {
        this.doppeladerBelegt = doppeladerBelegt;
    }

    @Embedded
    public AnschlussPortierungKorrekt getAnschlussPortierungKorrekt() {
        return anschlussPortierungKorrekt;
    }

    public void setAnschlussPortierungKorrekt(AnschlussPortierungKorrekt anschlussPortierungKorrekt) {
        this.anschlussPortierungKorrekt = anschlussPortierungKorrekt;
    }

    @Override
    public String toString() {
        return "Positionsattribute [erledigungsterminOffenerAuftrag=" + erledigungsterminOffenerAuftrag
                + ", alternativprodukt=" + alternativprodukt + ", standortKundeKorrektur=" + standortKundeKorrektur
                + ", fehlauftragsnummer=" + fehlauftragsnummer + ", doppeladerBelegt=" + doppeladerBelegt
                + ", anschlussPortierungKorrekt=" + anschlussPortierungKorrekt + ", getId()=" + getId() + "]";
    }

}
