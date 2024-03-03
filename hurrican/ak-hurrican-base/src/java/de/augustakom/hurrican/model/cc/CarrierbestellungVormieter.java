/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.validation.cc.CarrierbestellungVormieterValid;

/**
 * Modell zur Abbildung der Vormieter-Daten zu einer Carrierbestellung. (Notwendig fuer diverse WITA Geschaeftsfaelle.)
 */
@Entity
@Table(name = "T_CARRIERBESTELLUNG_VORMIETER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CARRIERBESTELLUNG_VORMIE_0", allocationSize = 1)
@CarrierbestellungVormieterValid
public class CarrierbestellungVormieter extends AbstractCCIDModel {

    private static final long serialVersionUID = 3369237283935974751L;

    private String vorname;
    private String nachname;
    private String onkz;
    private String rufnummer;
    private String ufaNummer;


    /**
     * Ueberprueft, ob Vormieter-Daten auf dem Objekt konfiguriert sind.
     *
     * @return
     */
    @Transient
    public boolean isEmpty() {
        if (StringUtils.isBlank(vorname)
                && StringUtils.isBlank(nachname)
                && StringUtils.isBlank(onkz)
                && StringUtils.isBlank(rufnummer)
                && StringUtils.isBlank(ufaNummer)) {
            return true;
        }
        return false;
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

    @Size(min = 1, max = 14, message = "Ufa-Nummer muss zwischen {min} und {max} Zeichen haben.")
    @Pattern(regexp = "[0-9]{2,5}C[0-9]{6,7}", message = "Ufa-Nummer muss folgendes Format haben: '[N1]C[N2]'"
            + ", wo [N1] - 2 bis 5-stellige Zahl, C - Trennzeichen, [N2] - 6 bis 7-stellige Zahl; Beispiel: 12345C1234567")
    @Column(name = "UFANUMMER")
    public String getUfaNummer() {
        return ufaNummer;
    }

    public void setUfaNummer(String ufaNummer) {
        this.ufaNummer = ufaNummer;
    }
}
