/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 11:36:02
 */
package de.mnet.wita.message.meldung.position;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.validators.groups.Workflow;

/**
 * Angaben zur bestellten / bestaetigten Leitung.
 */
@Entity
@Table(name = "T_MWF_LEITUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_LEITUNG_0", allocationSize = 1)
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class Leitung extends MwfEntity {

    private static final long serialVersionUID = 1411246447217205550L;
    public static final Comparator<LeitungsAbschnitt> BY_LAUFENDE_NUMMER = new Comparator<LeitungsAbschnitt>() {
        @Override
        public int compare(LeitungsAbschnitt arg0, LeitungsAbschnitt arg1) {
            return arg0.getLaufendeNummer().compareTo(arg1.getLaufendeNummer());
        }
    };
    public static final Function<LeitungsAbschnitt, String> GET_LEITUNGS_LAENGE = new Function<LeitungsAbschnitt, String>() {
        @Override
        public String apply(LeitungsAbschnitt input) {
            return input.getLeitungsLaenge();
        }
    };
    public static final Function<LeitungsAbschnitt, String> GET_LEITUNGS_DURCHMESSER = new Function<LeitungsAbschnitt, String>() {
        @Override
        public String apply(LeitungsAbschnitt input) {
            return input.getLeitungsDurchmesser();
        }
    };

    private LeitungsBezeichnung leitungsBezeichnung;
    private String schleifenWiderstand;
    private List<LeitungsAbschnitt> leitungsAbschnitte = new ArrayList<LeitungsAbschnitt>();
    private String maxBruttoBitrate;

    public Leitung() {
        // required by Hibernate
    }

    public Leitung(LeitungsBezeichnung leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
    }

    @Column(name = "SCHLEIFEN_WIDERSTAND")
    public String getSchleifenWiderstand() {
        return schleifenWiderstand;
    }

    public void setSchleifenWiderstand(String schleifenWiderstand) {
        this.schleifenWiderstand = schleifenWiderstand;
    }

    @Column(name = "MAX_BRUTTO_BITRATE")
    public String getMaxBruttoBitrate() {
        return maxBruttoBitrate;
    }

    public void setMaxBruttoBitrate(String maxBruttoBitrate) {
        this.maxBruttoBitrate = maxBruttoBitrate;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @NotNull(groups = Workflow.class, message = "Leitungsbezeichnung muss gesetzt sein.")
    public LeitungsBezeichnung getLeitungsBezeichnung() {
        return leitungsBezeichnung;
    }

    public void setLeitungsBezeichnung(LeitungsBezeichnung leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
    }

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "LEITUNG_ID", nullable = false)
    public List<LeitungsAbschnitt> getLeitungsAbschnitte() {
        return leitungsAbschnitte;
    }

    public void setLeitungsAbschnitte(List<LeitungsAbschnitt> leitungsAbschnitte) {
        this.leitungsAbschnitte = leitungsAbschnitte;
    }

    public void addLeitungsAbschnitt(LeitungsAbschnitt abschnitt) {
        leitungsAbschnitte.add(abschnitt);
    }

    @Override
    public String toString() {
        return "Leitung [leitungsBezeichnung=" + leitungsBezeichnung + ", schleifenWiderstand=" + schleifenWiderstand
                + ", leitungsAbschnitte=" + leitungsAbschnitte + ", maxBruttoBitrate=" + maxBruttoBitrate + "]";
    }
}
