/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:53
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.Uebertragungsverfahren;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_SCHALTUNG_KVZ")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_SCHALTUNG_KVZ_0", allocationSize = 1)
public class SchaltungKvzTal extends MwfEntity {

    private static final long serialVersionUID = -2949125361205468103L;

    public static final String SCHALTUNG_KVZ_PROPERTY_PATH = "geschaeftsfall.auftragsPosition.geschaeftsfallProdukt.schaltangaben.schaltungKvzTal";

    private String kvz;
    private String kvzSchaltnummer;
    private Uebertragungsverfahren uebertragungsverfahren;

    @Override
    public String toString() {
        return "SchaltungKVZTALType [KVZ=" + kvz + ", KVZSchaltnummer=" + kvzSchaltnummer + "]";
    }

    @NotNull
    public String getKvz() {
        return kvz;
    }

    public void setKvz(String kvz) {
        this.kvz = kvz;
    }

    @NotNull
    public String getKvzSchaltnummer() {
        return kvzSchaltnummer;
    }

    public void setKvzSchaltnummer(String kvzSchaltnummer) {
        this.kvzSchaltnummer = kvzSchaltnummer;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 3, columnDefinition = "varchar2(3)")
    public Uebertragungsverfahren getUebertragungsverfahren() {
        return uebertragungsverfahren;
    }

    public void setUebertragungsverfahren(Uebertragungsverfahren uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
    }
}
