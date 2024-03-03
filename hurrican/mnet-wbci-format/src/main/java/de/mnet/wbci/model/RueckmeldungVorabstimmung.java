/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.13
 */
package de.mnet.wbci.model;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import de.mnet.wbci.validation.constraints.CheckKundenwunschtermin;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminIgnoringNextDay;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminNotInRange;
import de.mnet.wbci.validation.constraints.CheckRuemVaMandatoryFields;
import de.mnet.wbci.validation.constraints.CheckRuemVaMeldungscodes;
import de.mnet.wbci.validation.constraints.CheckRuemVaRufnummer;
import de.mnet.wbci.validation.constraints.CheckRuemVaTerminBeforeKwt;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrn;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1MeldungVaRrnp;
import de.mnet.wbci.validation.groups.V1MeldungWarn;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@CheckRuemVaMeldungscodes(groups = V1Meldung.class)
@CheckRuemVaTerminBeforeKwt(groups = V1MeldungWarn.class)
@CheckRuemVaMandatoryFields(groups = V1Meldung.class)
@CheckRuemVaRufnummer(groups = V1Meldung.class)
@DiscriminatorValue("RUEM-VA")
public class RueckmeldungVorabstimmung extends Meldung<MeldungPositionRueckmeldungVa> {

    private static final long serialVersionUID = 5353327338377276302L;

    private Technologie technologie;
    private LocalDate wechseltermin;

    private Rufnummernportierung rufnummernportierung;

    private Set<TechnischeRessource> technischeRessourcen = new HashSet<>();

    /**
     * Constructor using RUEM-VA meldung typ.
     */
    public RueckmeldungVorabstimmung() {
        super(MeldungTyp.RUEM_VA);
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "RUFNUMMERPORTIERUNG_ID")
    @Valid
    public Rufnummernportierung getRufnummernportierung() {
        return rufnummernportierung;
    }

    public void setRufnummernportierung(Rufnummernportierung rufnummernportierung) {
        this.rufnummernportierung = rufnummernportierung;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "MELDUNG_ID")
    @Valid
    public Set<TechnischeRessource> getTechnischeRessourcen() {
        return technischeRessourcen;
    }

    public void setTechnischeRessourcen(Set<TechnischeRessource> technischeRessourcen) {
        this.technischeRessourcen = technischeRessourcen;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TECHNOLOGIE")
    public Technologie getTechnologie() {
        return technologie;
    }

    public void setTechnologie(Technologie technologie) {
        this.technologie = technologie;
    }

    @NotNull(groups = V1Meldung.class, message = "darf nicht leer sein")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @CheckKundenwunschterminIgnoringNextDay(groups = { V1MeldungVaRrnp.class }, minimumLeadTime = 1)
    @CheckKundenwunschtermin(groups = { V1MeldungVaKueMrn.class, V1MeldungVaKueOrn.class }, minimumLeadTime = 5)
    @CheckKundenwunschterminNotInRange(groups = { V1MeldungVaKueMrnWarn.class, V1MeldungVaKueOrnWarn.class }, from = 5, to = 7)
    public LocalDate getWechseltermin() {
        return wechseltermin;
    }

    public void setWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
    }

    @Override
    public String toString() {
        return "RueckMeldungVa [technologie=" + getTechnologie() + ", wechselTermin=" + getWechseltermin() +
                ", technischeRessourcen=" + getTechnischeRessourcen() + ", rufnummernportierung="
                + getRufnummernportierung() +
                ", toString=" + super.toString() + "]";
    }

    @Transient
    public boolean hasWitaVtrNr() {
        if (getTechnischeRessourcen() != null) {
            for (TechnischeRessource tr : technischeRessourcen) {
                if (StringUtils.isNotBlank(tr.getVertragsnummer())) {
                    return true;
                }
            }
        }
        return false;
    }
}
