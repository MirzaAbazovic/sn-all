/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2011 11:10:32
 */
package de.mnet.wita.message.meldung.attribute;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_AUFNEHMENDER_PROVIDER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_AUFNEHMENDER_PROVIDER", allocationSize = 1)
public class AufnehmenderProvider extends MwfEntity {

    private static final long serialVersionUID = -3585335286978689833L;

    private String providernameAufnehmend;
    private LocalDate uebernahmeDatumGeplant;
    private LocalDate antwortFrist;
    private LocalDate uebernahmeDatumVerbindlich;

    @NotNull
    @Size(min = 1, max = 160)
    @Column(name = "PROVIDERNAME_AUFNEHMEND")
    public String getProvidernameAufnehmend() {
        return providernameAufnehmend;
    }

    public void setProvidernameAufnehmend(String providernameAufnehmend) {
        this.providernameAufnehmend = providernameAufnehmend;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "UEBERNAHME_DATUM_GEPLANT")
    public LocalDate getUebernahmeDatumGeplant() {
        return uebernahmeDatumGeplant;
    }

    public void setUebernahmeDatumGeplant(LocalDate uebernahmeDatumGeplant) {
        this.uebernahmeDatumGeplant = uebernahmeDatumGeplant;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "ANTWORT_FRIST")
    public LocalDate getAntwortFrist() {
        return antwortFrist;
    }

    public void setAntwortFrist(LocalDate antwortFrist) {
        this.antwortFrist = antwortFrist;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "UEBERNAHME_DATUM_VERBINDLICH")
    public LocalDate getUebernahmeDatumVerbindlich() {
        return uebernahmeDatumVerbindlich;
    }

    public void setUebernahmeDatumVerbindlich(LocalDate uebernahmeDatumVerbindlich) {
        this.uebernahmeDatumVerbindlich = uebernahmeDatumVerbindlich;
    }


    @Override
    public String toString() {
        return "AufnehmenderProvider [providernameAufnehmend=" + providernameAufnehmend + ", uebernahmeDatumGeplant="
                + uebernahmeDatumGeplant + ", antwortFrist=" + antwortFrist + ", uebernahmeDatumVerbindlich="
                + uebernahmeDatumVerbindlich + "]";
    }

}
