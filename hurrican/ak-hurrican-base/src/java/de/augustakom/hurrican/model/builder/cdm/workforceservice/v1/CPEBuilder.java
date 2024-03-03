/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;
import java.math.*;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.FfmHelper;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class CPEBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.CPE> {

    private String type;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String macAddress;
    private OrderTechnicalParams.CPE.DHCP dhcp;
    private String nat;
    private String dns;
    private String dnsServerIp;
    private String qoS;
    private String accessData;
    private String wanvpvc;
    private String layer2Protocol;
    private String softwarestand;
    private Integer mtu;
    private String cpeBemerkung;
    private String snmpMnet;
    private String snmpCustomer;
    private List<OrderTechnicalParams.CPE.ACL> acls;

    @Override
    public OrderTechnicalParams.CPE build() {
        OrderTechnicalParams.CPE cpe = new OrderTechnicalParams.CPE();
        cpe.setType(this.type);
        cpe.setManufacturer(this.manufacturer);
        cpe.setModel(this.model);
        cpe.setSerialNumber(this.serialNumber);
        cpe.setMacAddress(this.macAddress);
        cpe.setDHCP(this.dhcp);
        cpe.setNAT(this.nat);
        cpe.setQoS(this.qoS);
        cpe.setAccessData(this.accessData);
        cpe.setWANVPVC(this.wanvpvc);
        cpe.setLayer2Protocol(this.layer2Protocol);
        cpe.setDNS(this.dns);
        cpe.setDNSIpAddress(this.dnsServerIp);
        cpe.setSoftwareVersion(this.softwarestand);
        cpe.setMTU((this.mtu != null) ? this.mtu.toString() : null);
        cpe.setNotice(this.cpeBemerkung);
        cpe.setSNMPMnet(this.snmpMnet);
        cpe.setSNMPCustomer(this.snmpCustomer);
        if (acls != null) {
            cpe.getACL().addAll(acls);
        }
        return cpe;
    }

    public CPEBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public CPEBuilder withManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public CPEBuilder withModel(String model) {
        this.model = model;
        return this;
    }

    public CPEBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public CPEBuilder withMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public CPEBuilder withDHCP(OrderTechnicalParams.CPE.DHCP dhcp) {
        this.dhcp = dhcp;
        return this;
    }

    public CPEBuilder withNAT(Boolean nat) {
        this.nat = FfmHelper.convertBoolean(nat);
        return this;
    }

    public CPEBuilder withQoS(Boolean qos) {
        this.qoS = FfmHelper.convertBoolean(qos);
        return this;
    }

    public CPEBuilder withAccessData(String accessData) {
        this.accessData = accessData;
        return this;
    }

    public CPEBuilder withWANVPVC(String wanvpvc) {
        this.wanvpvc = wanvpvc;
        return this;
    }

    public CPEBuilder withLayer2Protocol(String layer2Protocol) {
        this.layer2Protocol = layer2Protocol;
        return this;
    }

    public CPEBuilder withDns(Boolean dns) {
        this.dns = FfmHelper.convertBoolean(dns);
        return this;
    }

    public CPEBuilder withDnsServerIp(String dnsServerIp) {
        this.dnsServerIp = dnsServerIp;
        return this;
    }

    public CPEBuilder withMtu(Integer mtu) {
        this.mtu = mtu;
        return this;
    }

    public CPEBuilder withCpeBemerkung(String cpeBemerkung) {
        this.cpeBemerkung = cpeBemerkung;
        return this;
    }

    public CPEBuilder withSnmpMnet(Boolean snmpMnet) {
        this.snmpMnet = FfmHelper.convertBoolean(snmpMnet);
        return this;
    }

    public CPEBuilder withSnmpCustomer(Boolean snmpCustomer) {
        this.snmpCustomer = FfmHelper.convertBoolean(snmpCustomer);
        return this;
    }

    public CPEBuilder withSoftwarestand(String softwarestand) {
        this.softwarestand = softwarestand;
        return this;
    }

    public CPEBuilder withACL(List<OrderTechnicalParams.CPE.ACL> acls) {
        this.acls = acls;
        return this;
    }

    public CPEBuilder addACL(OrderTechnicalParams.CPE.ACL acl) {
        if (this.acls == null) {
            this.acls = new ArrayList<>();
        }
        this.acls.add(acl);
        return this;
    }

}
