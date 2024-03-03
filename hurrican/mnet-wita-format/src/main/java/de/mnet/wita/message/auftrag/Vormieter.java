/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011 15:07:16
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.VormieterValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_VORMIETER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_VORMIETER_0", allocationSize = 1)
@VormieterValid
public class Vormieter extends MwfEntity {

    private static final long serialVersionUID = -9043376405177913173L;

    private String vorname;
    private String nachname;
    private String onkz;
    private String rufnummer;
    private String ufaNummer;

    public Vormieter() {
        // required for Hibernate
    }

    public Vormieter(String vorname, String nachname, String onkz, String rufnummer, String ufaNummer) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.onkz = onkz;
        this.rufnummer = rufnummer;
        this.ufaNummer = ufaNummer;
    }

    @Size(min = 1, max = 30, message = "Vorname muss zwischen {min} und {max} Zeichen haben.")
    @Column(name = "VORNAME")
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @Size(min = 1, max = 30, message = "Nachname muss zwischen {min} und {max} Zeichen haben.")
    @Column(name = "NACHNAME")
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Size(min = 2, max = 5, message = "Onkz muss zwischen {min} und {max} Ziffern haben.")
    @Pattern(regexp = "[1-9][0-9]{1,4}", message = "Onkz darf keine führende 0 haben und muss aus Ziffern bestehen.")
    @Column(name = "ONKZ")
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    @Size(min = 1, max = 14, message = "Rufnummer muss zwischen {min} und {max} Ziffern haben.")
    @Pattern(regexp = "[0-9]+", message = "Rufnummer darf nur Ziffern enthalten.")
    @Column(name = "RUFNUMMER")
    public String getRufnummer() {
        return rufnummer;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    @Pattern(regexp = "[0-9]{2,5}C[0-9]{6,7}", message = "Ufa-Nummer muss folgendes Format haben: '[N1]C[N2]'"
            + ", wo [N1] - 2 bis 5-stellige Zahl, C - Trennzeichen, [N2] - 6 bis 7-stellige Zahl; Beispiel: 12345C1234567")
    @Column(name = "UFANUMMER")
    public String getUfaNummer() {
        return ufaNummer;
    }

    public void setUfaNummer(String ufaNummer) {
        this.ufaNummer = ufaNummer;
    }

    @Override
    public String toString() {
        return "Vormieter [vorname=" + vorname + ", nachname=" + nachname + ", onkz=" + onkz + ", rufnummer="
                + rufnummer + ", ufaNummer=" + ufaNummer + "]";
    }
}
