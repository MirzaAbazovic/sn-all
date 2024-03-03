/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2011 14:57:46
 */
package de.mnet.wita.message.common;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.validators.FirmennameAnredeValid;

@Entity
@DiscriminatorValue(value = "firma")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")

@FirmennameAnredeValid
public class Firmenname extends Kundenname {

    private static final long serialVersionUID = 3390715260001344688L;
    private String ersterTeil;
    private String zweiterTeil;

    @Size(min = 1, max = 30, message = "Der erste Teil eines Firmennamens muss zwischen 1 und 30 Zeichen lang sein. Korrigieren Sie die Anschlussinhaberadresse und lösen sie den Vorgang erneut aus.")
    @NotNull
    public String getErsterTeil() {
        return ersterTeil;
    }

    public void setErsterTeil(String ersterTeil) {
        this.ersterTeil = ersterTeil;
    }

    @Size(min = 1, max = 30, message = "Der zweite Teil eines Firmennamens muss zwischen 1 und 30 Zeichen lang sein. Korrigieren Sie die Anschlussinhaberadresse und lösen sie den Vorgang erneut aus.")
    public String getZweiterTeil() {
        return zweiterTeil;
    }

    public void setZweiterTeil(String zweiterTeil) {
        this.zweiterTeil = zweiterTeil;
    }

    @Override
    public String toString() {
        return "Firmenname [ersterTeil=" + ersterTeil + ", zweiterTeil=" + zweiterTeil + ", toString()="
                + super.toString() + "]";
    }

}
