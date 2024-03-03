/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 14:16:31
 */
package de.mnet.wita.message.meldung.position;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Email;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.Anrede;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_ANSPRECHPARTNER_DTAG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_ANSPRECHPARTNER_DTAG_0", allocationSize = 1)
public class AnsprechpartnerTelekom extends MwfEntity {

    private static final long serialVersionUID = -1049137540575964949L;

    private Anrede anrede;
    private String vorname;
    private String nachname;
    private String telefonNummer;
    private String emailAdresse;

    public AnsprechpartnerTelekom() {
        // for hibernate
    }

    public AnsprechpartnerTelekom(Anrede anrede, String vorname, String nachname, String telefonNummer) {
        this.anrede = anrede;
        this.vorname = vorname;
        this.nachname = nachname;
        this.telefonNummer = telefonNummer;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @Size(min = 0, max = 320, message = "Die Email-Adresse muss zwischen 0 und 320 Zeichen lang sein.")
    @Email(message = "Die Email-Adresse muss eine gültige Email-Adresse sein.")
    public String getEmailAdresse() {
        return emailAdresse;
    }

    public void setEmailAdresse(String emailAdresse) {
        this.emailAdresse = emailAdresse;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "varchar2(1)")
    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    @Size(min = 1, max = 30)
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Size(min = 0, max = 30)
    public String getTelefonNummer() {
        return telefonNummer;
    }

    public void setTelefonNummer(String telefonNummer) {
        this.telefonNummer = telefonNummer;
    }

    @Transient
    public String asString() {
        return getNachname() + " " + getVorname() + " (" + getTelefonNummer() + ")";
    }
}
