/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2008 15:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;

/**
 * Modell, um eine Hardware Baugruppentyp abzubilden.
 *
 *
 */
@Entity
@Table(name = "T_HW_BAUGRUPPEN_TYP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_HW_BAUGRUPPEN_TYP_0", allocationSize = 1)
public class HWBaugruppenTyp extends AbstractCCIDModel {

    public static enum Tunneling {
        VLAN,
        CC
    }

    /**
     * Baugruppen fuer ADSL/XDSL
     */
    public static final String HW_SCHNITTSTELLE_ADSL = "ADSL";
    /**
     * Baugruppen fuer SDSL
     */
    public static final String HW_SCHNITTSTELLE_SDSL = "SDSL";
    /**
     * Baugruppen fuer AB
     */
    public static final String HW_SCHNITTSTELLE_AB = "AB";
    /**
     * Baugruppen fuer UK0
     */
    public static final String HW_SCHNITTSTELLE_UK0 = "UK0";
    /**
     * Baugruppen fuer MVO (Mnet-vor-Ort
     */
    public static final String HW_SCHNITTSTELLE_MVO = "MVO";
    /**
     * Baugruppen fuer V52
     */
    public static final String HW_SCHNITTSTELLE_V52 = "V52";
    /**
     * Baugruppen fuer PDH
     */
    public static final String HW_SCHNITTSTELLE_PDH = "PDH";
    /**
     * Baugruppen fuer SDH
     */
    public static final String HW_SCHNITTSTELLE_SDH = "SDH";
    /**
     * Baugruppen fuer VDSL
     */
    public static final String HW_SCHNITTSTELLE_VDSL2 = "VDSL2";
    /**
     * Baugruppen fuer FTTH_ETH (Ethernet)
     */
    public static final String HW_SCHNITTSTELLE_ETH = "ETH";
    /**
     * Baugruppen fuer POTS Ports (Plain Old Telephon)
     */
    public static final String HW_SCHNITTSTELLE_POTS = "POTS";
    /**
     * Baugruppen fuer G.fast
     */
    public static final String HW_SCHNITTSTELLE_GFAST = "GFAST";

    /**
     * HW Typ: ONT
     */
    public static final String HW_TYPE_ONT = "ONT";

    /**
     * HW Typ: DPU
     */
    public static final String HW_TYPE_DPU = "DPU";

    /**
     * Huawei ADBF Baugruppe
     */
    public static final Long HW_ADBF = Long.valueOf(14);
    /**
     * Huawei ADBF2 Baugruppe
     */
    public static final Long HW_ADBF2 = Long.valueOf(311);

    public static final String TYP_NGLTA_NAME = "NGLTA";

    /**
     * Prefix fuer HW_PORT, der EWSD-Baugruppen kennzeichnet.
     */
    public static final String HW_PORT_EWSD_PREFIX = "SLM";
    /**
     * Wert fuer HW_PORT, der noch nicht definierte EWSD-Baugruppen kennzeichnet.
     */
    public static final String HW_PORT_EWSD_FREE = "SLx";

    /**
     * Bezeichner fuer NAME
     */
    public static final String NAME = "name";
    /**
     * Bezeichner fuer HVT_TECHNIK
     */
    public static final String HVT_TECHNIK = "hvtTechnik";

    private String name = null;
    private String description = null;
    private Integer portCount = null;
    private Boolean isActive = null;
    private String hwSchnittstelleName = null;
    private String hwTypeName = null;
    /**
     * Der Hersteller
     */
    private HVTTechnik hvtTechnik = null;
    /**
     * Maximale Bandbreite der Baugruppe
     */
    private Bandwidth maxBandwidth = null;
    private Tunneling tunneling;
    private Schicht2Protokoll defaultSchicht2Protokoll;
    private Boolean bondingCapable = null;
    private Boolean profileAssignable;

    /**
     *
     */
    @Transient
    public boolean isFtthEthernetPort() {
        return StringUtils.equals(getHwSchnittstelleName(), HWBaugruppenTyp.HW_SCHNITTSTELLE_ETH)
                && StringUtils.equals(getHwTypeName(), HWBaugruppenTyp.HW_TYPE_ONT);
    }


    /**
     * Gibt einen Display-Namen fuer den Baugruppen-Typ zurueck. (Zusammengesetzt aus Name + Description)
     */
    @Transient
    public String getDisplayName() {
        return StringTools.join(new String[] { getName(), getDescription() }, " - ", true);
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PORT_COUNT")
    public Integer getPortCount() {
        return portCount;
    }

    public void setPortCount(Integer portCount) {
        this.portCount = portCount;
    }

    @Column(name = "IS_ACTIVE")
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Column(name = "HW_SCHNITTSTELLE_NAME")
    public String getHwSchnittstelleName() {
        return hwSchnittstelleName;
    }

    public void setHwSchnittstelleName(String hwSchnittstelleName) {
        this.hwSchnittstelleName = hwSchnittstelleName;
    }

    @Column(name = "HW_TYPE_NAME")
    public String getHwTypeName() {
        return hwTypeName;
    }

    public void setHwTypeName(String hwTypeName) {
        this.hwTypeName = hwTypeName;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HVT_TECHNIK_ID")
    public HVTTechnik getHvtTechnik() {
        return hvtTechnik;
    }

    public void setHvtTechnik(HVTTechnik hvtTechnik) {
        this.hvtTechnik = hvtTechnik;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "downstream", column = @Column(name = "MAX_BANDWIDTH_DOWNSTREAM")),
            @AttributeOverride(name = "upstream", column = @Column(name = "MAX_BANDWIDTH_UPSTREAM"))
    })
    public Bandwidth getMaxBandwidth() {
        return maxBandwidth;
    }

    public void setMaxBandwidth(Bandwidth maxBandwidth) {
        this.maxBandwidth = maxBandwidth;
    }

    @Column(name = "TUNNELING")
    @Enumerated(EnumType.STRING)
    public Tunneling getTunneling() {
        return tunneling;
    }

    public void setTunneling(final Tunneling tunneling) {
        this.tunneling = tunneling;
    }

    @Column(name = "DEF_SCHICHT2_PROTOKOLL")
    @Enumerated(EnumType.STRING)
    public Schicht2Protokoll getDefaultSchicht2Protokoll() {
        return defaultSchicht2Protokoll;
    }

    public void setDefaultSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.defaultSchicht2Protokoll = schicht2Protokoll;
    }

    @Column(name = "PROFILE_ASSIGNABLE")
    public Boolean isProfileAssignable() {
        return profileAssignable;
    }

    public void setProfileAssignable(Boolean profileAssignable) {
        this.profileAssignable = profileAssignable;
    }

    /**
     * Flag definiert, ob auf der Baugruppe Ports fuer n-Draht Schaltungen verwendet werden koennen.
     * @return
     */
    @Column(name = "BONDING_CAPABLE")
    @NotNull
    public Boolean getBondingCapable() {
        return bondingCapable;
    }

    public void setBondingCapable(Boolean bondingCapable) {
        this.bondingCapable = bondingCapable;
    }

}
