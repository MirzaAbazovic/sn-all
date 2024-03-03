/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:50:29
 */
package de.mnet.wita.message.common.portierung;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.EinzelanschlussRufnummerValid;
import de.mnet.wita.validators.OnkzValid;
import de.mnet.wita.validators.RufnummerValid;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@EinzelanschlussRufnummerValid
@Entity
@Table(name = "T_MWF_PORT_EINZELRUFNUMMER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_PORT_ERN_0", allocationSize = 1)
public class EinzelanschlussRufnummer extends MwfEntity {

    private static final long serialVersionUID = -1722640588030805765L;

    public static final Comparator<EinzelanschlussRufnummer> FACHLICHER_COMPARATOR = new Comparator<EinzelanschlussRufnummer>() {
        @Override
        public int compare(EinzelanschlussRufnummer arg0, EinzelanschlussRufnummer arg1) {
            int compareVon = arg0.getOnkz().compareTo(arg1.getOnkz());
            if (compareVon != 0) {
                return compareVon;
            }
            return arg0.getRufnummer().compareTo(arg1.getRufnummer());
        }
    };

    public EinzelanschlussRufnummer() {
        // Required by Hibernate
    }

    public EinzelanschlussRufnummer(String onkz, String rufnummer) {
        this.onkz = onkz;
        this.rufnummer = rufnummer;
    }

    private String onkz;
    private String rufnummer;

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    @NotNull
    @OnkzValid
    public String getOnkz() {
        return onkz;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    @RufnummerValid
    @NotNull
    public String getRufnummer() {
        return rufnummer;
    }

    @Override
    public String toString() {
        return "EinzelanschlussRufnummer [Vorwahl: " + onkz + ", Rufnummerstamm: " + rufnummer + "]";
    }

}

