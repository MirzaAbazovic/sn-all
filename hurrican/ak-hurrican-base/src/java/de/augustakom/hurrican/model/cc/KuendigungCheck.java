/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 17:53:24
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Modell-Klasse zur Definition / Konfiguration von Kuendigungs-Checks. <br/> Ueber die Kuendigungs-Checks kann
 * berechnet werden, was das fruehest moegliche Kuendigungsdatum fuer einen (Taifun) Auftrag ist.
 */
@ObjectsAreNonnullByDefault
@Entity
@Table(name = "T_KUENDIGUNG_CHECK")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_KUENDIGUNG_CHECK_0", allocationSize = 1)
public class KuendigungCheck extends AbstractCCIDModel {

    private static final long serialVersionUID = -2360642958506268304L;

    private Long oeNoOrig;
    private Boolean durchVertrieb;
    private Set<KuendigungFrist> kuendigungFristen;

    /**
     * Filtert die Liste mit den {@link KuendigungFrist}en und versucht, die zum Auftrag passende Konfiguration zu
     * finden.
     *
     * @param billingOrder
     * @return
     */
    public
    @Nullable
    KuendigungFrist getRelevantKuendigungFrist(BAuftrag billingOrder) {
        boolean withMvlz = billingOrder.getVertragsLaufzeit() != null && billingOrder.getVertragsLaufzeit() > 0;
        List<KuendigungFrist> filterdByMvlz =
                Lists.newArrayList(Collections2.filter(kuendigungFristen, KuendigungFrist.filterByMvlz(withMvlz)));

        // filteredByMvlz sortieren nach Vertrag_ab Monat/Jahr, da sonst Floppy Verhalten, wenn mehrere Eintraege!
        List<KuendigungFrist> filterdByMvlzSorted = sortKuendigungsFristen(filterdByMvlz);

        // KuendigungFristen auf Jahr/Monat filtern!
        List<KuendigungFrist> filteredByVertragsdatum =
                Lists.newArrayList(Collections2.filter(filterdByMvlzSorted, KuendigungFrist.filterByYearAndMonth(billingOrder)));
        if (!filteredByVertragsdatum.isEmpty()) {
            // spaetestes matchendes VertragsDatum beruecksichtigen!
            KuendigungFrist latest = null;
            for (KuendigungFrist frist : filteredByVertragsdatum) {
                if (latest == null || latest.getVertragsdatum().isBefore(frist.getVertragsdatum())) {
                    latest = frist;
                }
            }
            return latest;
        }

        return (filterdByMvlzSorted != null && !filterdByMvlzSorted.isEmpty()) ? filterdByMvlzSorted.get(0) : null;
    }

    List<KuendigungFrist> sortKuendigungsFristen(List<KuendigungFrist> filterdByMvlz) {
        // @formatter:off
        Comparator<KuendigungFrist> byMonth = (kf1, kf2) -> (
                (kf1.getVertragsabschlussMonat() != null)
                        ? (kf2.getVertragsabschlussMonat() != null)
                            ? kf1.getVertragsabschlussMonat().compareTo(kf2.getVertragsabschlussMonat())
                            : 1
                        : -1);

        Comparator<KuendigungFrist> byYear = (kf1, kf2) -> (
                (kf1.getVertragsabschlussJahr() != null)
                        ? (kf2.getVertragsabschlussJahr() != null)
                            ? kf1.getVertragsabschlussJahr().compareTo(kf2.getVertragsabschlussJahr())
                            : 1
                        : -1);
        // @formatter:on

        return filterdByMvlz.stream().sorted(byYear.thenComparing(byMonth)).collect(Collectors.toList());
    }

    @Column(name = "OE__NO")
    @NotNull
    public Long getOeNoOrig() {
        return oeNoOrig;
    }

    public void setOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
    }

    @Column(name = "DURCH_VERTRIEB")
    @NotNull
    public Boolean getDurchVertrieb() {
        return durchVertrieb;
    }

    public void setDurchVertrieb(Boolean durchVertrieb) {
        this.durchVertrieb = durchVertrieb;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "KUENDIGUNG_CHECK_ID", nullable = false)
    public Set<KuendigungFrist> getKuendigungFristen() {
        return kuendigungFristen;
    }

    public void setKuendigungFristen(Set<KuendigungFrist> kuendigungFristen) {
        this.kuendigungFristen = kuendigungFristen;
    }
}
