/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.groups.V1Meldung;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_MPOS_ABBM_RUFNUMMER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_MPOS_ABBM_RUFNUMMER_0", allocationSize = 1)
public class MeldungPositionAbbmRufnummer extends WbciEntity {

    private static final long serialVersionUID = 5900739562800616714L;

    public MeldungPositionAbbmRufnummer() {
    }

    public MeldungPositionAbbmRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    @Pattern(groups = V1Meldung.class, regexp = "\\d{1,14}", message = "Rufnummer darf nur max. 15 nummerische Zeichen enthalten")
    public String getRufnummer() {
        return rufnummer;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    private String rufnummer;

    @Override
    public String toString() {
        return "MeldungPositionAbbmRufnummer{" +
                ", rufnummer='" + rufnummer + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }

}
