/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 13:28:59
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Email;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid;
import de.mnet.wita.validators.groups.V1;

/**
 * Abbildung der WITA Auftragsstruktur 'Montageleistung'.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_MONTAGELEISTUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_MONTAGELEISTUNG_0", allocationSize = 1)
public class Montageleistung extends MwfEntity {

    private static final long serialVersionUID = -9035274493258001910L;

    private Personenname personenname;
    private String telefonnummer;
    private String emailadresse;
    private String montagehinweis;

    private String terminReservierungsId;

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PERSONENNAME_ID")
    public Personenname getPersonenname() {
        return personenname;
    }

    public void setPersonenname(Personenname personenname) {
        this.personenname = personenname;
    }

    @TelefonnummerValid
    @NotNull(message = "Der Ansprechpartner für die Montageleistung (Endstellen-Ansprechpartner) benötigt eine Telefonnummer.")
    @Size(min = 1, max = 30, message = "Die Telefonnummer des Ansprechpartners der Montageleistung (Endstellen-Ansprechpartner) muss zwischen 1 und 30 Zeichen lang sein.")
    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    @Size(max = 320, message = "Die Email-Adresse des Ansprechpartners der Montageleistung (Endstellen-Ansprechpartner) darf nicht länger als 320 Zeichen lang sein.")
    @Email(message = "Die Email-Adresse des Ansprechpartners der Montageleistung (Endstellen-Ansprechpartner) ist keine gültige Email-Adresse.")
    public String getEmailadresse() {
        return emailadresse;
    }

    public void setEmailadresse(String emailadresse) {
        this.emailadresse = emailadresse;
    }

    @Size.List({
            @Size(groups = V1.class, min = 1, max = 160, message = "Der Montagehinweis muss mindestens 1 und darf maximal 160 Zeichen lang sein.")
    })
    public String getMontagehinweis() {
        return montagehinweis;
    }

    public void setMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
    }

    @Transient
    public String getTerminReservierungsId() {
        return terminReservierungsId;
    }

    public void setTerminReservierungsId(String terminReservierungsId) {
        this.terminReservierungsId = terminReservierungsId;
    }

    @Override
    public String toString() {
        return "Montageleistung [personenname=" + personenname + ", telefonnummer=" + telefonnummer + ", emailadresse="
                + emailadresse + ", montagehinweis=" + montagehinweis + ", toString()=" + super.toString() + "]";
    }

}
