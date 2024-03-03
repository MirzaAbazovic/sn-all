/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2011 07:41:27
 */
package de.augustakom.hurrican.model.cc.view;

import java.io.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;


/**
 * View-Modell fuer die Abbildung von Port-Mappings fuer einen Baugruppen-Schwenk ohne(!) Auftragsdaten, dafuer mit
 * Angabe der Leisten und Stifte.<br> Diese View kann z.B. als Print-Basis verwendet werden.
 */
@Entity
@Table(name = "V_HW_BG_PORT2PORT_DETAIL")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class HWBaugruppenChangePort2PortDetailView extends AbstractObservable implements Serializable, HwEqnAwareModel {

    public static final String PORT2PORT_ID = "port2PortId";
    private Long port2PortId;
    public static final String HW_BG_CHANGE_ID = "hwBgChangeId";
    private Long hwBgChangeId;
    public static final String RANG_BUCHT_OLD = "rangBuchtOld";
    private String rangBuchtOld;
    public static final String RANG_VERTEILER_OLD = "rangVerteilerOld";
    private String rangVerteilerOld;
    public static final String RANG_LEISTE1_OLD = "rangLeiste1Old";
    private String rangLeiste1Old;
    public static final String RANG_STIFT1_OLD = "rangStift1Old";
    private String rangStift1Old;
    public static final String EQUIPMENT_ID_OLD = "equipmentIdOld";
    private Integer equipmentIdOld;
    public static final String HW_EQN_OLD = "hwEqnOld";
    private String hwEqnOld;
    public static final String RANG_BUCHT_NEW = "rangBuchtNew";
    private String rangBuchtNew;
    public static final String RANG_VERTEILER_NEW = "rangVerteilerNew";
    private String rangVerteilerNew;
    public static final String RANG_LEISTE1_NEW = "rangLeiste1New";
    private String rangLeiste1New;
    public static final String RANG_STIFT1_NEW = "rangStift1New";
    private String rangStift1New;
    public static final String EQUIPMENT_ID_NEW = "equipmentIdNew";
    private Integer equipmentIdNew;
    public static final String HW_EQN_NEW = "hwEqnNew";
    private String hwEqnNew;

    public static final String[] TABLE_COLUMN_NAMES = new String[] {
            "HW_EQN alt", "Bucht alt", "Verteiler alt", "Leiste1 alt", "Stift1 alt",
            "HW_EQN neu", "Bucht neu", "Verteiler neu", "Leiste1 neu", "Stift1 neu" };
    public static final String[] TABLE_PROPERTY_NAMES = new String[] {
            HW_EQN_OLD, RANG_BUCHT_OLD, RANG_VERTEILER_OLD, RANG_LEISTE1_OLD, RANG_STIFT1_OLD,
            HW_EQN_NEW, RANG_BUCHT_NEW, RANG_VERTEILER_NEW, RANG_LEISTE1_NEW, RANG_STIFT1_NEW };
    public static final Class<?>[] TABLE_CLASS_TYPES = new Class[] {
            String.class, String.class, String.class, String.class, String.class,
            String.class, String.class, String.class, String.class, String.class };
    public static final int[] TABLE_FIT = new int[] { 80, 80, 80, 80, 80, 80, 80, 80, 80, 80 };


    @Id
    @Column(name = "PORT2PORT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getPort2PortId() {
        return port2PortId;
    }

    public void setPort2PortId(Long port2PortId) {
        this.port2PortId = port2PortId;
    }

    @Column(name = "HW_BG_CHANGE_ID")
    public Long getHwBgChangeId() {
        return hwBgChangeId;
    }

    public void setHwBgChangeId(Long hwBgChangeId) {
        this.hwBgChangeId = hwBgChangeId;
    }

    @Column(name = "HW_EQN_OLD")
    public String getHwEqnOld() {
        return hwEqnOld;
    }

    public void setHwEqnOld(String hwEqnOld) {
        this.hwEqnOld = hwEqnOld;
    }

    @Override
    @Transient
    public String getHwEQN() {
        return getHwEqnOld();
    }

    @Column(name = "HW_EQN_NEW")
    public String getHwEqnNew() {
        return hwEqnNew;
    }

    public void setHwEqnNew(String hwEqnNew) {
        this.hwEqnNew = hwEqnNew;
    }

    @Column(name = "RANG_BUCHT_OLD")
    public String getRangBuchtOld() {
        return rangBuchtOld;
    }

    public void setRangBuchtOld(String rangBuchtOld) {
        this.rangBuchtOld = rangBuchtOld;
    }

    @Column(name = "RANG_VERTEILER_OLD")
    public String getRangVerteilerOld() {
        return rangVerteilerOld;
    }

    public void setRangVerteilerOld(String rangVerteilerOld) {
        this.rangVerteilerOld = rangVerteilerOld;
    }

    @Column(name = "RANG_LEISTE1_OLD")
    public String getRangLeiste1Old() {
        return rangLeiste1Old;
    }

    public void setRangLeiste1Old(String rangLeiste1Old) {
        this.rangLeiste1Old = rangLeiste1Old;
    }

    @Column(name = "RANG_STIFT1_OLD")
    public String getRangStift1Old() {
        return rangStift1Old;
    }

    public void setRangStift1Old(String rangStift1Old) {
        this.rangStift1Old = rangStift1Old;
    }

    @Column(name = "EQ_ID_OLD")
    public Integer getEquipmentIdOld() {
        return equipmentIdOld;
    }

    public void setEquipmentIdOld(Integer equipmentIdOld) {
        this.equipmentIdOld = equipmentIdOld;
    }

    @Column(name = "RANG_BUCHT_NEW")
    public String getRangBuchtNew() {
        return rangBuchtNew;
    }

    public void setRangBuchtNew(String rangBuchtNew) {
        this.rangBuchtNew = rangBuchtNew;
    }

    @Column(name = "RANG_VERTEILER_NEW")
    public String getRangVerteilerNew() {
        return rangVerteilerNew;
    }

    public void setRangVerteilerNew(String rangVerteilerNew) {
        this.rangVerteilerNew = rangVerteilerNew;
    }

    @Column(name = "RANG_LEISTE1_NEW")
    public String getRangLeiste1New() {
        return rangLeiste1New;
    }

    public void setRangLeiste1New(String rangLeiste1New) {
        this.rangLeiste1New = rangLeiste1New;
    }

    @Column(name = "RANG_STIFT1_NEW")
    public String getRangStift1New() {
        return rangStift1New;
    }

    public void setRangStift1New(String rangStift1New) {
        this.rangStift1New = rangStift1New;
    }

    @Column(name = "EQ_ID_NEW")
    public Integer getEquipmentIdNew() {
        return equipmentIdNew;
    }

    public void setEquipmentIdNew(Integer equipmentIdNew) {
        this.equipmentIdNew = equipmentIdNew;
    }

}


