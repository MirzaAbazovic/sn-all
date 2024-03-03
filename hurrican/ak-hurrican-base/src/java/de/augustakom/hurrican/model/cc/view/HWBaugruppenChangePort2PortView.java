/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2010 14:32:11
 */
package de.augustakom.hurrican.model.cc.view;

import java.io.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;


/**
 * View-Modell fuer die Abbildung von Port-Mappings fuer einen Baugruppen-Schwenk inkl. Auftragsdaten.
 */
@Entity
@Table(name = "V_HW_BG_PORT2PORT")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class HWBaugruppenChangePort2PortView extends AbstractObservable implements Serializable, CCAuftragModel, HwEqnAwareModel {

    public static final String PORT2PORT_ID = "port2PortId";
    private Long port2PortId;
    public static final String HW_BG_CHANGE_ID = "hwBgChangeId";
    private Long hwBgChangeId;
    public static final String EQUIPMENT_ID_OLD = "equipmentIdOld";
    private Long equipmentIdOld;
    public static final String HW_EQN_OLD = "hwEqnOld";
    private String hwEqnOld;
    public static final String EQUIPMENT_ID_NEW = "equipmentIdNew";
    private Long equipmentIdNew;
    public static final String HW_EQN_NEW = "hwEqnNew";
    private String hwEqnNew;
    public static final String EQUIPMENT_STATUS_OLD = "equipmentStatusOld";
    private String equipmentStatusOld;
    private Long auftragId;
    public static final String AUFTRAG_STATUS = "auftragStatus";
    private String auftragStatus;
    public static final String TAIFUN_AUFTRAG_ID = "orderNoOrig";
    private Long orderNoOrig;
    public static final String PRODUKT = "produkt";
    private String produkt;
    public static final String VPN_NR = "vpnNr";
    private Long vpnNr;
    public static final String HAS_SUCCESSFUL_CPS_TX = "hasSuccessfulCpsTx";
    public static final String LAST_SUCCESSFUL_CPS_TX = "lastSuccessfulCpsTx";
    private Long lastSuccessfulCpsTx;
    public static final String EQ_OLD_MANUAL_CONFIGURATION = "eqOldManualConfiguration";
    private Boolean eqOldManualConfiguration;
    public static final String EQ_NEW_MANUAL_CONFIGURATION = "eqNewManualConfiguration";
    private Boolean eqNewManualConfiguration;


    public static final String[] TABLE_COLUMN_NAMES = new String[] {
            "HW_EQN alt", "HW_EQN neu", "Port Status alt",
            "Auftrag Id", "Taifun A-Id", "Produkt", "Auftragsstatus",
            "VPN ID", "CPS Init", "man. CC (alt)", "man. CC (neu)" };
    public static final String[] TABLE_PROPERTY_NAMES = new String[] {
            HW_EQN_OLD, HW_EQN_NEW, EQUIPMENT_STATUS_OLD,
            AUFTRAG_ID, TAIFUN_AUFTRAG_ID, PRODUKT, AUFTRAG_STATUS,
            VPN_NR, HAS_SUCCESSFUL_CPS_TX, EQ_OLD_MANUAL_CONFIGURATION, EQ_NEW_MANUAL_CONFIGURATION };
    public static final Class<?>[] TABLE_CLASS_TYPES = new Class[] {
            String.class, String.class, String.class,
            Long.class, Long.class, String.class, String.class,
            Long.class, Boolean.class, Boolean.class, Boolean.class };
    public static final int[] TABLE_FIT = new int[] { 80, 80, 85, 70, 70, 110, 95, 65, 65, 85, 85 };


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

    @Column(name = "PORT_STATE_OLD")
    public String getEquipmentStatusOld() {
        return equipmentStatusOld;
    }

    public void setEquipmentStatusOld(String equipmentStatusOld) {
        this.equipmentStatusOld = equipmentStatusOld;
    }

    @Column(name = "AUFTRAG_ID")
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @Column(name = "TAIFUN_ORDER_NO")
    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    @Column(name = "PRODUKT_NAME")
    public String getProdukt() {
        return produkt;
    }

    public void setProdukt(String produkt) {
        this.produkt = produkt;
    }

    @Column(name = "VPN_NR")
    public Long getVpnNr() {
        return vpnNr;
    }

    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    @Column(name = "LAST_SUCCESSFUL_CPS_TX")
    public Long getLastSuccessfulCpsTx() {
        return lastSuccessfulCpsTx;
    }

    public void setLastSuccessfulCpsTx(Long lastSuccessfulCpsTx) {
        this.lastSuccessfulCpsTx = lastSuccessfulCpsTx;
    }

    @Transient
    public Boolean getHasSuccessfulCpsTx() {
        return (getLastSuccessfulCpsTx() != null) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Column(name = "EQ_OLD_MANUAL_CONFIGURATION")
    public Boolean getEqOldManualConfiguration() {
        return eqOldManualConfiguration;
    }

    public void setEqOldManualConfiguration(Boolean eqOldManualConfiguration) {
        this.eqOldManualConfiguration = eqOldManualConfiguration;
    }

    @Column(name = "EQ_NEW_MANUAL_CONFIGURATION")
    public Boolean getEqNewManualConfiguration() {
        return eqNewManualConfiguration;
    }

    public void setEqNewManualConfiguration(Boolean eqNewManualConfiguration) {
        this.eqNewManualConfiguration = eqNewManualConfiguration;
    }

    @Column(name = "EQ_ID_OLD")
    public Long getEquipmentIdOld() {
        return equipmentIdOld;
    }

    public void setEquipmentIdOld(Long equipmentIdOld) {
        this.equipmentIdOld = equipmentIdOld;
    }

    @Column(name = "EQ_ID_NEW")
    public Long getEquipmentIdNew() {
        return equipmentIdNew;
    }

    public void setEquipmentIdNew(Long equipmentIdNew) {
        this.equipmentIdNew = equipmentIdNew;
    }

    @Column(name = "AUFTRAG_STATUS")
    public String getAuftragStatus() {
        return auftragStatus;
    }

    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

}


