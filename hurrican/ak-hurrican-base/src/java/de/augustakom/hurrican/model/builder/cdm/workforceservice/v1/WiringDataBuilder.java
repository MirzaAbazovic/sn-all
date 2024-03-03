/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class WiringDataBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Site.WiringData> {

    private String type;
    private String deviceName;
    private String managementDescription;
    private String moduleType;
    private String ontid;
    private String physicType;
    private String hweqn;
    private String layer2Protocol;
    private String distributor;
    private String panelPin1;
    private String panelPin2;
    private OrderTechnicalParams.Site.WiringData.Crossing crossing;
    private String chassisIdentifier;
    private String chassisSlot;

    @Override
    public OrderTechnicalParams.Site.WiringData build() {
        OrderTechnicalParams.Site.WiringData wiringData = new OrderTechnicalParams.Site.WiringData();
        wiringData.setType(this.type);
        wiringData.setDeviceName(this.deviceName);
        wiringData.setManagementDescription(this.managementDescription);
        wiringData.setModuleType(this.moduleType);
        wiringData.setONTID(this.ontid);
        wiringData.setPhysicType(this.physicType);
        wiringData.setHWEQN(this.hweqn);
        wiringData.setLayer2Protocol(this.layer2Protocol);
        wiringData.setDistributor(this.distributor);
        wiringData.setPanelPin1(this.panelPin1);
        wiringData.setPanelPin2(this.panelPin2);
        wiringData.setCrossing(this.crossing);
        wiringData.setChassisIdentifier(this.chassisIdentifier);
        wiringData.setChassisSlot(this.chassisSlot);
        return wiringData;
    }


    public WiringDataBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public WiringDataBuilder withDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public WiringDataBuilder withManagementDescription(String managementDescription) {
        this.managementDescription = managementDescription;
        return this;
    }

    public WiringDataBuilder withModuleType(String moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    public WiringDataBuilder withONTID(String ontid) {
        this.ontid = ontid;
        return this;
    }

    public WiringDataBuilder withPhysicType(String physicType) {
        this.physicType = physicType;
        return this;
    }

    public WiringDataBuilder withHWEQN(String hweqn) {
        this.hweqn = hweqn;
        return this;
    }

    public WiringDataBuilder withLayer2Protocol(String layer2Protocol) {
        this.layer2Protocol = layer2Protocol;
        return this;
    }

    public WiringDataBuilder withDistributor(String distributor) {
        this.distributor = distributor;
        return this;
    }

    public WiringDataBuilder withPanelPin1(String panelPin1) {
        if (StringUtils.isNotBlank(panelPin1)) {
            this.panelPin1 = panelPin1;
        }
        return this;
    }

    public WiringDataBuilder withPanelPin2(String panelPin2) {
        if (StringUtils.isNotBlank(panelPin2)) {
            this.panelPin2 = panelPin2;
        }
        return this;
    }

    public WiringDataBuilder withCrossing(final OrderTechnicalParams.Site.WiringData.Crossing crossing) {
        this.crossing = crossing;
        return this;
    }

    public WiringDataBuilder withChassisIdentifier(String chassisIdentifier) {
        this.chassisIdentifier = chassisIdentifier;
        return this;
    }

    public WiringDataBuilder withChassisSlot(String chassisSlot) {
        this.chassisSlot = chassisSlot;
        return this;
    }

}
