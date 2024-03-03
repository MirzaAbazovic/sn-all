/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 12:10:02
 */
package de.mnet.wita.message.meldung.position;

import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.message.MwfEntity;

@Entity
@Table(name = "T_MWF_LEITUNGS_ABSCHNITT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_LEITUNGS_ABSCHNITT_0", allocationSize = 1)
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class LeitungsAbschnitt extends MwfEntity {

    private static final long serialVersionUID = -17890577634984282L;

    /**
     * Ermittelt aus den Strings für Leitungslänge (z.B. 100/50) und Aderquerschnitt (z.B. 10/20) die zugehörigen {@link
     * LeitungsAbschnitt}e. Falls die Anzahl der Elemente in Leitungslänge und Aderquerschnitt unterschiedlich sind,
     * wird eine leere Liste zurückgegeben.
     */
    public static List<LeitungsAbschnitt> valueOf(String ll, String aqs) {
        List<LeitungsAbschnitt> result = new ArrayList<LeitungsAbschnitt>();
        if (StringUtils.isNotBlank(ll) && StringUtils.isNotBlank(aqs)) {
            String[] lls = ll.split("/");
            String[] aqss = aqs.split("/");
            if (lls.length == aqss.length) {
                for (int idx = 0; idx < lls.length; idx++) {
                    result.add(new LeitungsAbschnitt(idx + 1, lls[idx], aqss[idx]));
                }
            }
        }
        return result;
    }

    private Integer laufendeNummer;
    private String leitungsLaenge;
    private String leitungsDurchmesser;

    public LeitungsAbschnitt() {
        // required by Hibernate
    }

    public LeitungsAbschnitt(Integer laufendeNummer, String leitungsLaenge, String leitungsDurchmesser) {
        super();
        this.laufendeNummer = laufendeNummer;
        this.leitungsLaenge = leitungsLaenge;
        this.leitungsDurchmesser = leitungsDurchmesser;
    }

    @Column(name = "LFD_NR")
    public Integer getLaufendeNummer() {
        return laufendeNummer;
    }

    public void setLaufendeNummer(Integer laufendeNummer) {
        this.laufendeNummer = laufendeNummer;
    }

    @Column(name = "LEITUNGS_LAENGE")
    public String getLeitungsLaenge() {
        return leitungsLaenge;
    }

    public void setLeitungsLaenge(String leitungsLaenge) {
        this.leitungsLaenge = leitungsLaenge;
    }

    @Column(name = "LEITUNGS_DURCHMESSER")
    public String getLeitungsDurchmesser() {
        return leitungsDurchmesser;
    }

    public void setLeitungsDurchmesser(String leitungsDurchmesser) {
        this.leitungsDurchmesser = leitungsDurchmesser;
    }


    @Override
    public String toString() {
        return "LeitungsAbschnitt [laufendeNummer=" + laufendeNummer + ", leitungsLaenge=" + leitungsLaenge
                + ", leitungsDurchmesser=" + leitungsDurchmesser + "]";
    }
}
