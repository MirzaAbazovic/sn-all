/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 17:53:24
 */
package de.augustakom.hurrican.model.cc.fttx;

import java.util.*;
import javax.persistence.*;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Port des A10-NSP. Auch A10-NSP LAG (Logical aggregation group) genannt.
 * <p/>
 * Siehe: {@link A10Nsp}
 */
@ObjectsAreNonnullByDefault
@Entity
@Table(name = "T_FTTX_A10_NSP_PORT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FTTX_A10_NSP_PORT_0", allocationSize = 1)
public class A10NspPort extends AbstractCCIDModel {

    /**
     * Der A10-NSP des Ports
     */
    private A10Nsp a10Nsp;

    /**
     * Verbindungsbezeichnuns des Ports immer mit Prefix "FE" (Festverbindung Ethernet)
     */
    private VerbindungsBezeichnung vbz;

    private Set<HWOlt> olt = Sets.newHashSet();

    /**
     * needed by Builder and Hibernate- don't use
     */
    @SuppressWarnings("unused")
    private A10NspPort() {
    }

    /**
     * Erstellt einen neuen Port mit allen Pflichtattributen.
     */
    public A10NspPort(A10Nsp a10Nsp, VerbindungsBezeichnung vbz) {
        this.a10Nsp = a10Nsp;
        this.vbz = vbz;
    }

    // ManyToOne cascadiert per default nicht
    @ManyToOne(optional = false)
    @JoinColumn(name = "A10_NSP_ID")
    public A10Nsp getA10Nsp() {
        return a10Nsp;
    }

    public void setA10Nsp(A10Nsp a10Nsp) {
        this.a10Nsp = a10Nsp;
    }

    @OneToOne(optional = false)
    @JoinColumn(name = "VBZ_ID")
    public VerbindungsBezeichnung getVbz() {
        return vbz;
    }

    protected void setVbz(VerbindungsBezeichnung vbz) {
        this.vbz = vbz;
    }

    @OneToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "T_FTTX_A10_PORT_2_OLT",
            joinColumns = { @JoinColumn(name = "A10_NSP_PORT_ID") },
            inverseJoinColumns = { @JoinColumn(name = "HW_RACK_OLT_ID") })
    public Set<HWOlt> getOlt() {
        return olt;
    }

    /**
     * Only for Hibernate
     */
    @SuppressWarnings("unused")
    private void setOlt(Set<HWOlt> olt) {
        this.olt = olt;
    }

}


