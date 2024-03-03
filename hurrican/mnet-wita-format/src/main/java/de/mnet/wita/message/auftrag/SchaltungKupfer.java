/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:58
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.Uebertragungsverfahren;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_SCHALTUNG_KUPFER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_SCHALTUNG_KUPFER_0", allocationSize = 1)
public class SchaltungKupfer extends MwfEntity {

    private static final long serialVersionUID = -3404331816411727835L;

    public static final String SCHALTUNG_KUPFER_PROPERTY_PATH = "geschaeftsfall.auftragsPosition.geschaeftsfallProdukt.schaltangaben.schaltungKupfer";

    private Uebertragungsverfahren uebertragungsverfahren;
    private String uevt;
    private String evs;
    private String doppelader;

    @Override
    public String toString() {
        return "SchaltungKupferType [UEVT=" + uevt + ", EVS=" + evs + ", doppelader=" + doppelader + ", uebertragungsverfahren=" + uebertragungsverfahren + "]";
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 3, columnDefinition = "varchar2(3)")
    public Uebertragungsverfahren getUebertragungsverfahren() {
        return uebertragungsverfahren;
    }

    public void setUebertragungsverfahren(Uebertragungsverfahren uebertragungsverfahren) {
        this.uebertragungsverfahren = uebertragungsverfahren;
    }

    @NotNull
    @Size(min = 1, max = 4)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    public String getUEVT() {
        return uevt;
    }

    public void setUEVT(String uevt) {
        this.uevt = uevt;
    }

    @NotNull
    @Size(min = 1, max = 2)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    public String getEVS() {
        return evs;
    }

    public void setEVS(String evs) {
        this.evs = evs;
    }

    @NotNull
    @Size(min = 1, max = 2)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    public String getDoppelader() {
        return doppelader;
    }

    public void setDoppelader(String doppelader) {
        this.doppelader = doppelader;
    }
}
