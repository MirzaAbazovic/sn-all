/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 12:55:46
 */
package de.mnet.wita.message.common.portierung;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.validators.RufnummernPortierungEinzelanschlussValid;

/**
 * Einzelanschluss Rufnummernportierung.
 * <p/>
 * Anmerkung: Das Feld alleRufnummern, welches von der WITA Schnittstelle angeboten wird, wird von m-net z.Z. nicht
 * benutzt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@RufnummernPortierungEinzelanschlussValid
@Entity
@DiscriminatorValue("einzelanschluss")
public class RufnummernPortierungEinzelanschluss extends RufnummernPortierung {

    private static final long serialVersionUID = -128113626731545806L;

    private List<EinzelanschlussRufnummer> rufnummern = new ArrayList<EinzelanschlussRufnummer>();

    @Transient
    public boolean isFachlichEqual(RufnummernPortierungEinzelanschluss other) {
        Set<EinzelanschlussRufnummer> rns = new TreeSet<EinzelanschlussRufnummer>(
                EinzelanschlussRufnummer.FACHLICHER_COMPARATOR);
        rns.addAll(rufnummern);
        for (EinzelanschlussRufnummer rn : other.getRufnummern()) {
            if (!rns.contains(rn)) {
                return false;
            }
            rns.remove(rn);
        }
        return rns.isEmpty();
    }

    @Transient
    public boolean isSingleDn() {
        return ((rufnummern != null) && (rufnummern.size() == 1)) ? true : false;
    }

    @Transient
    public boolean isMultipleDn() {
        return ((rufnummern != null) && (rufnummern.size() > 1)) ? true : false;
    }

    @Size(min = 1, max = 10, message = "RufnummernPortierungEinzelanschluss muss 1..10 Nummern enthalten")
    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "PORTIERUNGS_ID", nullable = false)
    public List<EinzelanschlussRufnummer> getRufnummern() {
        return rufnummern;
    }

    /**
     * Required by Hibernate
     */
    @SuppressWarnings("unused")
    private void setRufnummern(List<EinzelanschlussRufnummer> rufnummern) {
        this.rufnummern = rufnummern;
    }

    public void addRufnummer(EinzelanschlussRufnummer rufnummer) {
        this.rufnummern.add(rufnummer);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rufnummerportierung für Einzelanschluss");
        for (EinzelanschlussRufnummer rn : rufnummern) {
            sb.append("\n").append(rn.toString());
        }
        return sb.toString();
    }

}
