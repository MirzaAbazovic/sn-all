/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class SiteBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Site> {

    private String type;
    private String cluster;
    private String kvzNumber;
    private String lineId;
    private OrderTechnicalParams.Site.Location location;
    private OrderTechnicalParams.Site.Device device;
    private String hwSwitch;
    private String hvt;
    private String portType;
    private String lineType;
    private String areaDialingCode;
    private BigInteger downstream;
    private BigInteger upstream;
    private String asb;
    private String lbz;
    private String vtrNr;
    private String ll;
    private String aqs;
    private String lastValidDSLAMProfile;
    private String talProvisioningDate;
    private Boolean customerOnSite;
    private List<OrderTechnicalParams.Site.WiringData> wiringData;

    @Override
    public OrderTechnicalParams.Site build() {
        OrderTechnicalParams.Site site = new OrderTechnicalParams.Site();
        site.setType(this.type);
        site.setCluster(this.cluster);
        site.setKVZNumber(this.kvzNumber);
        site.setLineId(this.lineId);
        site.setLocation(this.location);
        site.setDevice(this.device);
        site.setSwitch(this.hwSwitch);
        site.setHVT(this.hvt);
        site.setPortType(this.portType);
        site.setLineType(this.lineType);
        site.setAreaDialingCode(this.areaDialingCode);
        site.setDownstream(this.downstream);
        site.setUpstream(this.upstream);
        site.setASB(this.asb);
        site.setLBZ(this.lbz);
        site.setVtrNr(this.vtrNr);
        site.setLL(this.ll);
        site.setAQS(this.aqs);
        site.setLastValidDSLAMProfile(this.lastValidDSLAMProfile);
        site.setTALProvisioningDate(this.talProvisioningDate);

        if (customerOnSite != null) {
            site.setCustomerOnSite(this.customerOnSite ? "Ja" : "Nein");
        }

        if (null != this.wiringData) {
            site.getWiringData().addAll(this.wiringData);
        }
        return site;
    }

    public SiteBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public SiteBuilder withCluster(String cluster) {
        this.cluster = cluster;
        return this;
    }

    public SiteBuilder withKvzNumber(String kvzNumber) {
        this.kvzNumber = kvzNumber;
        return this;
    }

    public SiteBuilder withLineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public SiteBuilder withLocation(OrderTechnicalParams.Site.Location location) {
        this.location = location;
        return this;
    }

    public SiteBuilder withDevice(OrderTechnicalParams.Site.Device device) {
        this.device = device;
        return this;
    }

    public SiteBuilder withSwitch(String hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public SiteBuilder withSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch != null ? hwSwitch.getName() : null;
        return this;
    }

    public SiteBuilder withHvt(String hvt) {
        this.hvt = hvt;
        return this;
    }

    public SiteBuilder withHvtGruppe(HVTGruppe hvtGruppe) {
        if (hvtGruppe != null) {
            this.hvt = String.format("%s (%s, %s)",
                    hvtGruppe.getOrtsteil(),
                    hvtGruppe.getStreetAndHouseNum(),
                    hvtGruppe.getOrt());
        }
        return this;
    }

    public SiteBuilder withPortType(String portType) {
        this.portType = portType;
        return this;
    }

    public SiteBuilder withLineType(String lineType) {
        this.lineType = lineType;
        return this;
    }

    public SiteBuilder withAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
        return this;
    }

    public SiteBuilder withDownstream(BigInteger downstream) {
        this.downstream = downstream;
        return this;
    }

    public SiteBuilder withDownstream(Long longValue) {
        if (longValue != null) {
            this.downstream = new BigInteger(longValue.toString());
        }
        return this;
    }

    public SiteBuilder withUpstream(BigInteger upstream) {
        this.upstream = upstream;
        return this;
    }

    public SiteBuilder withUpstream(Long longValue) {
        if (longValue != null) {
            this.upstream = new BigInteger(longValue.toString());
        }
        return this;
    }

    public SiteBuilder withAsb(String asb) {
        this.asb = asb;
        return this;
    }

    public SiteBuilder withLbz(String lbz) {
        this.lbz = lbz;
        return this;
    }

    public SiteBuilder withVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
        return this;
    }

    public SiteBuilder withLl(String ll) {
        this.ll = ll;
        return this;
    }

    public SiteBuilder withAqs(String aqs) {
        this.aqs = aqs;
        return this;
    }

    public SiteBuilder withLastValidDSLAMProfile(String lastValidDSLAMProfile) {
        this.lastValidDSLAMProfile = lastValidDSLAMProfile;
        return this;
    }

    public SiteBuilder withTalProvisioningDate(String talProvisioningDate) {
        this.talProvisioningDate = talProvisioningDate;
        return this;
    }

    public SiteBuilder withCustomerOnSite(Boolean customerOnSite) {
        this.customerOnSite = customerOnSite;
        return this;
    }

    public SiteBuilder withWiringData(List<OrderTechnicalParams.Site.WiringData> wiringData) {
        if (CollectionUtils.isNotEmpty(wiringData)) {
            this.wiringData = wiringData;
        }
        return this;
    }

    public SiteBuilder addWiringData(OrderTechnicalParams.Site.WiringData wiringData) {
        if (null == this.wiringData) {

            this.wiringData = new ArrayList<>();
        }
        this.wiringData.add(wiringData);
        return this;
    }

}
