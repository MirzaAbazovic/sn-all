/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 11:23:36
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;

import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;

/**
 * Modell bildet ein Mapping zwischen einem Produkt und den moeglichen SIP Domänen ab.
 */
@Entity
@Table(name = "T_PROD_2_SIP_DOMAIN")
@SuppressWarnings("JpaDataSourceORMInspection")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROD_2_SIP_DOMAIN_0", allocationSize = 1)
public class Produkt2SIPDomain extends AbstractCCIDModel implements CCProduktModel {

    public static final String TYPE_PROD_ID = "prodId";
    public static final String TYPE_DEFAULT_DOMAIN = "defaultDomain";
    public static final String TYPE_HWSWITCH_ID = "hwSwitch.id";
    public static final String TYPE_SIPDOMAINREF_ID = "sipDomainRef.id";
    private static final long serialVersionUID = -3748968098736900612L;

    private Long prodId;
    private HWSwitch hwSwitch;
    private Reference sipDomainRef;
    private Boolean defaultDomain;
    private String userW;
    private Date dateW;

    @Override
    @Column(name = "PROD_ID", nullable = true)
    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HW_SWITCH", nullable = false)
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

    @Column(name = "DEFAULT_DOMAIN")
    public Boolean getDefaultDomain() {
        return defaultDomain;
    }

    public void setDefaultDomain(Boolean defaultDomain) {
        this.defaultDomain = defaultDomain;
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
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

}
