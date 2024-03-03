/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.SystemUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrn;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnp;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("AKM-TR")
public class UebernahmeRessourceMeldung extends Meldung<MeldungPositionUebernahmeRessourceMeldung> {

    private static final long serialVersionUID = -9128333464533753009L;

    private Boolean uebernahme;
    private Boolean sichererHafen;
    private Set<Leitung> leitungen = new HashSet<>();
    private String portierungskennungPKIauf;

    public UebernahmeRessourceMeldung() {
        super(MeldungTyp.AKM_TR);
    }

    public Boolean isUebernahme() {
        return uebernahme;
    }

    public void setUebernahme(boolean uebernahme) {
        this.uebernahme = uebernahme;
    }

    @Column(name = "SICHERER_HAFEN")
    public Boolean isSichererHafen() {
        return sichererHafen;
    }

    public void setSichererHafen(boolean sichererHafen) {
        this.sichererHafen = sichererHafen;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "UEBERNAHME_MELDUNG_ID")
    @Valid
    public Set<Leitung> getLeitungen() {
        return leitungen;
    }

    public void setLeitungen(Set<Leitung> leitungen) {
        this.leitungen = leitungen;
    }

    /**
     * Ermittelt alle angegebenen Leitungen und erstellt daraus einen String
     *
     * @return
     */
    @Transient
    public String extractLeitungen() {
        StringBuilder result = new StringBuilder();
        if (getLeitungen() != null) {
            for (Leitung ltg : getLeitungen()) {
                if (result.length() > 0) {
                    result.append(SystemUtils.LINE_SEPARATOR);
                }

                if (ltg.getLineId() != null) {
                    result.append(ltg.getLineId());
                }

                if (ltg.getVertragsnummer() != null) {
                    result.append(ltg.getVertragsnummer());
                }
            }
        }

        return result.toString();
    }

    @NotEmpty(groups = { V1MeldungVaKueMrn.class, V1MeldungVaRrnp.class })
    @Null(groups = V1MeldungVaKueOrn.class)
    public String getPortierungskennungPKIauf() {
        return portierungskennungPKIauf;
    }

    public void setPortierungskennungPKIauf(String portierungskennungPKIauf) {
        this.portierungskennungPKIauf = portierungskennungPKIauf;
    }

    @Override
    public String toString() {
        return "UebernahmeRessourceMeldung [uebernahme=" + isUebernahme() + ", sichererHafen=" + isSichererHafen() +
                ", leitungen=" + getLeitungen() + ", portierungskennungPKIauf="
                + getPortierungskennungPKIauf() +
                ", toString=" + super.toString() + "]";
    }
}
