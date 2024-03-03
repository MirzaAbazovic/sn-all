/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:05:57
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;

/**
 * Abbildung der M-net Kunden-Informationen fuer die WITA Schnittstelle.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_KUNDE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_KUNDE_0", allocationSize = 1)
public class Kunde extends MwfEntity {

    // <kunde>
    // <kundennummer>1100100122</kundennummer>
    // <leistungsnummer>0000004487</leistungsnummer>
    // </kunde>

    private static final long serialVersionUID = 3566049446320788016L;

    private String kundennummer;
    private String leistungsnummer;

    @Override
    public String toString() {
        return "Kunde [kundennummer=" + kundennummer + ", leistungsnummer=" + leistungsnummer + "]";
    }

    @NotNull
    @Size(min = 10, max = 10)
    public String getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
    }

    @NotNull
    @Size(min = 10, max = 10)
    public String getLeistungsnummer() {
        return leistungsnummer;
    }

    public void setLeistungsnummer(String leistungsnummer) {
        this.leistungsnummer = leistungsnummer;
    }

}
