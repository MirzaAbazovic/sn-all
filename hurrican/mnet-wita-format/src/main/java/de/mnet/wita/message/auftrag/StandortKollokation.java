/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 11:58:37
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Abbildung des WITA Objekts 'StandortB' (Kollokation).
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_STANDORT_KOLLOKATION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_STANDORT_KOLLOKATION_0", allocationSize = 1)
public class StandortKollokation extends AbstractStandort {

    private static final long serialVersionUID = -4077928880553941061L;

    private String onkz;
    private String asb;

    @Override
    public String toString() {
        return "Standort [standort=" + super.toString() + ", onkz=" + onkz + ", asb=" + asb + "]";
    }

    @NotNull
    @Digits(integer = 9999, fraction = 1)
    @Size(min = 1, max = 4)
    public String getAsb() {
        return asb;
    }

    public void setAsb(String asb) {
        this.asb = asb;
    }

    @NotNull
    @Digits(integer = 99999, fraction = 1)
    @Size(min = 2, max = 5)
    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    /**
     * Vergleicht alle Adressfelder von 2 Kollokationen.
     */
    public boolean equalsKollokation(StandortKollokation other) {
        if (other == null) {
            return false;
        }
        return new EqualsBuilder()
                .append(getOnkz(), other.onkz)
                .append(getAsb(), other.asb)
                .append(getStrassenname(), other.getStrassenname())
                .append(getHausnummer(), other.getHausnummer())
                .append(getHausnummernZusatz(), other.getHausnummernZusatz())
                .append(getPostleitzahl(), other.getPostleitzahl())
                .append(getOrtsname(), other.getOrtsname())
                .append(getOrtsteil(), other.getOrtsteil())
                .append(getLand(), other.getLand())
                .isEquals();
    }

    /**
     * Kopiert alle Adressfelder von dem gegebenen StandortKollokation.
     */
    public void copyKollokation(StandortKollokation other) {
        if (other == null) {
            return;
        }
        setOnkz(other.getOnkz());
        setAsb(other.getAsb());
        setStrassenname(other.getStrassenname());
        setHausnummer(other.getHausnummer());
        setHausnummernZusatz(other.getHausnummernZusatz());
        setPostleitzahl(other.getPostleitzahl());
        setOrtsname(other.getOrtsname());
        setOrtsteil(other.getOrtsteil());
        setLand(other.getLand());
    }
}
