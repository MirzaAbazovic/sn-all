/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2015
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

/**
 *
 */
@Entity
@Table(name = "T_EG_TYPE_2_SIP_DOMAIN")
@SuppressWarnings("JpaDataSourceORMInspection")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_EG_TYPE_2_SIP_DOMAIN_0", allocationSize = 1)
public class EGType2SIPDomain extends AbstractCCIDModel {

    public static final String TYPE_HWSWITCH_ID = "hwSwitch.id";
    public static final String TYPE_EGTYPE_ID = "egType.id";
    public static final String TYPE_SIPDOMAINREF_ID = "sipDomainRef.id";

    private EGType egType;
    private HWSwitch hwSwitch;
    private Reference sipDomainRef;
    private String userW;
    private Date dateW;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EG_TYPE_ID", nullable = true)
    public EGType getEgType() {
        return egType;
    }

    public void setEgType(EGType egType) {
        this.egType = egType;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_SWITCH_ID", nullable = false)
    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @SuppressWarnings("JpaAttributeTypeInspection")
    @JoinColumn(name = "SIP_DOMAIN_REF_ID", nullable = false)
    public Reference getSipDomainRef() {
        return sipDomainRef;
    }

    public void setSipDomainRef(Reference sipDomainRef) {
        this.sipDomainRef = sipDomainRef;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "DATEW")
    public Date getDateW() {
        return (dateW != null) ? new Date(dateW.getTime()) : null;
    }

    public void setDateW(Date dateW) {
        this.dateW = (dateW != null) ? new Date(dateW.getTime()) : null;
    }

}
