/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import javax.annotation.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Predicate;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Modellklasse zur Abbildung einer Konfiguration, die eine Kuendigungsfrist fuer einen Auftrag definiert. <br/> Eine
 * Kuendigungsfrist kann abhaengig davon sein, ob der Auftrag eine Mindestvertragslaufzeit (MVLZ) besitzt bzw. wann der
 * Vertragsbeginn des Auftrags war. <br/>
 */
@ObjectsAreNonnullByDefault
@Entity
@Table(name = "T_KUENDIGUNG_FRIST")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_KUENDIGUNG_FRIST_0", allocationSize = 1)
public class KuendigungFrist extends AbstractCCIDModel {

    private static final long serialVersionUID = -2719914209645812342L;

    public enum FristAuf {
        // Frist zum Ende der Mindestvertragslaufzeit
        ENDE_MVLZ,
        // Frist auf das Monatsende
        MONATSENDE,
        // Frist auf Eingangsdatum der Kuendigung
        EINGANGSDATUM;
    }

    private Boolean mitMvlz;
    private Long fristInWochen;
    private FristAuf fristAuf;
    private Long autoVerlaengerung;
    private Long vertragsabschlussJahr;
    private Long vertragsabschlussMonat;
    private String description;

    /**
     * Predicate, um {@link KuendigungFrist}en abhaengig von der Mindestvertragslaufzeit zu filtern
     */
    public static Predicate<KuendigungFrist> filterByMvlz(final boolean withMvlz) {
        return new Predicate<KuendigungFrist>() {
            @Override
            public boolean apply(KuendigungFrist input) {
                if (withMvlz && BooleanTools.nullToFalse(input.getMitMvlz())) {
                    return true;
                }
                else if (!withMvlz && !BooleanTools.nullToFalse(input.getMitMvlz())) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Predicate, um {@link KuendigungFrist}en abhaengig vom Vertragsbeginn zu filtern
     */
    public static Predicate<KuendigungFrist> filterByYearAndMonth(final BAuftrag billingOrder) {
        return new Predicate<KuendigungFrist>() {
            @Override
            public boolean apply(KuendigungFrist input) {
                if (input.getVertragsabschlussJahr() != null) {
                    LocalDateTime vertragsdatum = Instant.ofEpochMilli(billingOrder.getVertragsdatum().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    return input.getVertragsdatum().isBefore(vertragsdatum);
                }
                return false;
            }
        };
    }


    /**
     * Berechnet die naechste gueltige Mindestvertragslaufzeit. <br/> So lange das {@code vertragsende} vor dem {@code
     * cancellationIncome} liegt, wird die {@code autoVerlaengerung} zum {@code vertragsende} addiert. <br/> Sollte
     * keine Auto-Verlaengerung eingetragen sein, wird das {@code vertragsende} als Mindestvertragslaufzeit zurueck
     * gegeben. <br/><br/> Beispiele:
     * <pre>
     *   Vertragsende=15.12.2013; Verlaengerung: 3 Monate
     *     Kuendigungseingangsdatum=01.06.2013  -->  Mindestvertragslaufzeit=15.12.2013
     *     Kuendigungseingangsdatum=01.12.2013  -->  Mindestvertragslaufzeit=15.03.2014
     *     Kuendigungseingangsdatum=01.02.2014  -->  Mindestvertragslaufzeit=15.03.2014
     *
     *  Vertragsende=15.12.2013; Verlaengerung: 0 Monate
     *     unabhaengig von Kuendigungsdatum: immer Mindestvertragslaufzeit=15.12.2013
     * </pre>
     *
     * @param cancellationIncome Datum, zu dem die Kuendigung eingegangen ist
     * @param vertragsende       urspruengliches Vertragsende
     * @return naechstes berechnetes Vertragsende
     */
    @Transient
    public LocalDateTime calculateNextMvlz(@NotNull LocalDateTime cancellationIncome, @NotNull LocalDateTime vertragsende) {
        LocalDateTime mvlz = vertragsende;
        while (mvlz.isBefore(cancellationIncome) && hasAutoVerlaengerung()) {
            mvlz = mvlz.plusMonths(getAutoVerlaengerung().intValue());
        }
        return mvlz;
    }

    @Transient
    public LocalDateTime getVertragsdatum() {
        return LocalDateTime.of(getVertragsabschlussJahr().intValue(), getVertragsabschlussMonat().intValue(), 1, 0, 0, 0, 0);
    }

    @Column(name = "MIT_MVLZ")
    @NotNull
    public Boolean getMitMvlz() {
        return mitMvlz;
    }

    public void setMitMvlz(Boolean mitMvlz) {
        this.mitMvlz = mitMvlz;
    }

    @Column(name = "FRIST_IN_WOCHEN")
    @NotNull
    public Long getFristInWochen() {
        return fristInWochen;
    }

    public void setFristInWochen(Long fristInWochen) {
        this.fristInWochen = fristInWochen;
    }

    @Column(name = "FRIST_AUF")
    @Enumerated(EnumType.STRING)
    @NotNull
    public FristAuf getFristAuf() {
        return fristAuf;
    }

    public void setFristAuf(FristAuf fristAuf) {
        this.fristAuf = fristAuf;
    }

    /**
     * Anzahl Monate, um die sich eine Mindestvertragslaufzeit automatisch verlaengert.
     *
     * @return
     */
    @Column(name = "AUTO_VERLAENGERUNG")
    @NotNull
    public Long getAutoVerlaengerung() {
        return autoVerlaengerung;
    }

    public void setAutoVerlaengerung(Long autoVerlaengerung) {
        this.autoVerlaengerung = autoVerlaengerung;
    }
    
    @Transient
    public boolean hasAutoVerlaengerung() {
        return autoVerlaengerung.longValue() > 0;
    }

    @Column(name = "VERTRAG_AB_JAHR")
    @Nullable
    public Long getVertragsabschlussJahr() {
        return vertragsabschlussJahr;
    }

    public void setVertragsabschlussJahr(Long vertragsabschlussJahr) {
        this.vertragsabschlussJahr = vertragsabschlussJahr;
    }

    @Column(name = "VERTRAG_AB_MONAT")
    @Nullable
    public Long getVertragsabschlussMonat() {
        return vertragsabschlussMonat;
    }

    public void setVertragsabschlussMonat(Long vertragsabschlussMonat) {
        this.vertragsabschlussMonat = vertragsabschlussMonat;
    }

    @Column(name = "DESCRIPTION")
    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
