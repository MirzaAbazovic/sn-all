/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2011 14:57:46
 */
package de.mnet.wita.message.common;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.validators.PersonennameAnredeValid;

@Entity
@DiscriminatorValue(value = "person")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@PersonennameAnredeValid
public class Personenname extends Kundenname {

    private static final long serialVersionUID = 3390715260001344688L;

    public static final int VORNAME_MAX_SIZE = 30;
    public static final int NACHNAME_MAX_SIZE = 30;

    private String vorname;
    private String nachname;

    @Size(min = 1, max = VORNAME_MAX_SIZE, message = "Der Vorname des Ansprechpartners ist nicht gesetzt oder zu lang. Bitte passen Sie den entsprechenden Ansprechpartner an (entweder für Standort Kunde, Anschlussinhaber oder Montageleistung). Evtl. kann auch der Adress-Typ (BUSINESS/RESIDENTIAL) angepasst werden.")
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @NotNull
    @Size(min = 1, max = NACHNAME_MAX_SIZE, message = "Der Nachname des Ansprechpartners ist nicht gesetzt oder zu lang.  Bitte passen Sie den entsprechenden Ansprechpartner an (entweder für Standort Kunde, Anschlussinhaber oder Montageleistung). Evtl. kann auch der Adress-Typ (BUSINESS/RESIDENTIAL) angepasst werden.")
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Override
    public String toString() {
        return "Personenname [vorname=" + vorname + ", nachname=" + nachname + ", toString()=" + super.toString() + "]";
    }
}
