/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:52:40
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 * EntityBuild for Equipment objects
 *
 *
 */
@SuppressWarnings("unused")
public class EquipmentBuilder extends AbstractCCIDModelBuilder<EquipmentBuilder, Equipment> {

    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private HWSwitch hwSwitch = null;
    private String hwEQN = "1-2-3-4";
    private String hwSchnittstelle = "SDSL-OUT";
    private final String ts1 = null;
    private final String ts2 = null;
    private String rangVerteiler = null;
    private final String rangReihe = null;
    private String rangBucht = null;
    private String rangLeiste1 = null;
    private String rangStift1 = null;
    private String rangLeiste2 = null;
    private String rangStift2 = null;
    private RangSchnittstelle rangSchnittstelle = null;
    private String carrier = null;
    private EqStatus status = null;
    private final String bemerkung = null;
    private final String userW = null;
    private final Date dateW = null;
    private String rangSSType = null;
    private Uebertragungsverfahren uetv = null;
    private HWBaugruppeBuilder hwBaugruppenBuilder;
    private final Integer cvlan = null;
    private Boolean manualConfiguration = null;
    private String v5Port = null;
    private String kvzNummer = null;
    private String kvzDoppelader = null;
    private Schicht2Protokoll schicht2Protokoll;
    private Integer uevtClusterNo = null;
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public HWBaugruppeBuilder getHwBaugruppenBuilder() {
        return hwBaugruppenBuilder;
    }

    @Override
    protected void beforeBuild() {
        if ((hwBaugruppenBuilder != null) &&
                (hwBaugruppenBuilder.getRackBuilder() != null)) {
            hwBaugruppenBuilder.getRackBuilder().withHvtStandortBuilder(hvtStandortBuilder);
        }
    }

    public HVTStandortBuilder getHvtStandortBuilder() {
        return hvtStandortBuilder;
    }

    public EquipmentBuilder withStatus(EqStatus status) {
        this.status = status;
        return this;
    }

    public EquipmentBuilder withUETV(Uebertragungsverfahren uetv) {
        this.uetv = uetv;
        return this;
    }

    public EquipmentBuilder withHwEQN(String hwEQN) {
        this.hwEQN = hwEQN;
        return this;
    }

    public EquipmentBuilder withRangSSType(String rangSSType) {
        this.rangSSType = rangSSType;
        return this;
    }

    public EquipmentBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public EquipmentBuilder withBaugruppeBuilder(HWBaugruppeBuilder hwBaugruppeBuilder) {
        this.hwBaugruppenBuilder = hwBaugruppeBuilder;
        return this;
    }

    public EquipmentBuilder withHwSchnittstelle(String hwSchnittstelle) {
        this.hwSchnittstelle = hwSchnittstelle;
        return this;
    }

    public EquipmentBuilder withRangVerteiler(String rangVerteiler) {
        this.rangVerteiler = rangVerteiler;
        return this;
    }

    public EquipmentBuilder withRangBucht(String rangBucht) {
        this.rangBucht = rangBucht;
        return this;
    }

    public EquipmentBuilder withManualConfiguration(Boolean manualConfiguration) {
        this.manualConfiguration = manualConfiguration;
        return this;
    }

    public EquipmentBuilder withRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
        return this;
    }

    public EquipmentBuilder withRangStift1(String rangStift1) {
        this.rangStift1 = rangStift1;
        return this;
    }

    public EquipmentBuilder withRangLeiste2(String rangLeiste2) {
        this.rangLeiste2 = rangLeiste2;
        return this;
    }

    public EquipmentBuilder withRangStift2(String rangStift2) {
        this.rangStift2 = rangStift2;
        return this;
    }

    public EquipmentBuilder withV5Port(String v5Port) {
        this.v5Port = v5Port;
        return this;
    }

    public EquipmentBuilder withCarrier(String carrier) {
        this.carrier = carrier;
        return this;
    }

    public EquipmentBuilder withRangSchnittstelle(RangSchnittstelle rangSchnittstelle) {
        this.rangSchnittstelle = rangSchnittstelle;
        return this;
    }

    public EquipmentBuilder withKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
        return this;
    }

    public EquipmentBuilder withKvzDoppelader(String kvzDoppelader) {
        this.kvzDoppelader = kvzDoppelader;
        return this;
    }

    public EquipmentBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public EquipmentBuilder withSchicht2Protokoll(Schicht2Protokoll schicht2Protokoll) {
        this.schicht2Protokoll = schicht2Protokoll;
        return this;
    }

    public EquipmentBuilder withUevtClusterNo(Integer uevtClusterNo) {
        this.uevtClusterNo = uevtClusterNo;
        return this;
    }

    public EquipmentBuilder withDtagValues() {
        this.carrier = Carrier.CARRIER_DTAG;
        this.rangVerteiler = "0101";
        this.rangBucht = "0101";
        this.rangLeiste1 = "08";
        this.rangStift1 = "37";
        this.rangSchnittstelle = RangSchnittstelle.H;
        this.rangSSType = Equipment.RANG_SS_2H;
        this.uetv = Uebertragungsverfahren.H13;
        return this;
    }

}
