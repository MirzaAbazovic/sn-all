/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 08:30:50
 */
package de.augustakom.hurrican.model.cc.equipment;

import java.util.*;
import javax.persistence.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;


/**
 * Modell-Klasse fuer die Definition von Baugruppen-Aenderungen vom Typ 'einfacher Kartenwechsel' und 'Karten
 * zusammenlegen'.
 *
 *
 */
@Entity
@Table(name = "T_HW_BG_CHANGE_CARD")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BG_CHANGE_CARD_0", allocationSize = 1)
public class HWBaugruppenChangeCard extends AbstractCCIDModel {

    private SortedSet<HWBaugruppe> hwBaugruppenNew = Sets.newTreeSet();

    private SortedSet<HWBaugruppe> hwBaugruppenSource = Sets.newTreeSet();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_HW_BG_CHANGE_CARD_NEW",
            joinColumns = @JoinColumn(name = "HW_BG_CHANGE_CARD_ID"),
            inverseJoinColumns = @JoinColumn(name = "HW_BAUGRUPPE_ID")
    )
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppe> getHwBaugruppenNew() {
        return hwBaugruppenNew;
    }

    public boolean addHwBaugruppeNew(final HWBaugruppe bg) {
        return hwBaugruppenNew.add(bg);
    }

    public boolean removeHwBaugruppeNew(final HWBaugruppe bg) {
        return hwBaugruppenNew.remove(bg);
    }

    @Transient
    public HWBaugruppe getHwBaugruppeNew() {
        return Iterables.getOnlyElement(this.hwBaugruppenNew, null);
    }

    protected void setHwBaugruppenNew(final Set<HWBaugruppe> hwBaugruppenNew) {
        this.hwBaugruppenNew = new TreeSet<>(hwBaugruppenNew);
    }

    @SuppressWarnings("JpaAttributeTypeInspection")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_HW_BG_CHANGE_CARD_SOURCE",
            joinColumns = @JoinColumn(name = "HW_BG_CHANGE_CARD_ID"),
            inverseJoinColumns = @JoinColumn(name = "HW_BAUGRUPPE_ID")
    )
    @Fetch(FetchMode.SUBSELECT)
    public Set<HWBaugruppe> getHwBaugruppenSource() {
        return hwBaugruppenSource;
    }

    public boolean addHwBaugruppeSource(final HWBaugruppe bg) {
        return hwBaugruppenSource.add(bg);
    }

    public boolean removeHwBaugruppeSource(final HWBaugruppe bg) {
        return hwBaugruppenSource.remove(bg);
    }

    protected void setHwBaugruppenSource(final Set<HWBaugruppe> hwBaugruppenSource) {
        this.hwBaugruppenSource = new TreeSet(hwBaugruppenSource);
    }
}


