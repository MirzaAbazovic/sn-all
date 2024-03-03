/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class OrderTechnicalParamsBuilder implements WorkforceTypeBuilder<OrderTechnicalParams> {

    private OrderTechnicalParams.Common common;
    private OrderTechnicalParams.VPN vpn;
    private OrderTechnicalParams.ISDNBackup isdnBackup;
    private OrderTechnicalParams.Housing housing;
    private OrderTechnicalParams.PSE pse;

    private List<OrderTechnicalParams.CPE> cpe;
    private List<OrderTechnicalParams.IPAddress> ipAddress;
    private List<OrderTechnicalParams.PortForwarding> portForwarding;
    private List<OrderTechnicalParams.StaticRoute> staticRoute;
    private List<OrderTechnicalParams.VLAN> vlan;
    private List<OrderTechnicalParams.CrossConnection> crossConnection;
    private List<OrderTechnicalParams.DialUpAccess> dialUpAccess;
    private List<OrderTechnicalParams.DialNumber> dialNumber;
    private List<OrderTechnicalParams.Site> site;

    @Override
    public OrderTechnicalParams build() {
        OrderTechnicalParams otp = new OrderTechnicalParams();
        otp.setCommon(common);
        if (null != this.cpe) {
            otp.getCPE().addAll(this.cpe);
        }
        otp.setVPN(vpn);
        if (null != this.ipAddress) {
            otp.getIPAddress().addAll(this.ipAddress);
        }
        if (null != this.portForwarding) {
            otp.getPortForwarding().addAll(this.portForwarding);
        }
        if (null != this.staticRoute) {
            otp.getStaticRoute().addAll(this.staticRoute);
        }
        otp.setISDNBackup(isdnBackup);
        if (null != this.vlan) {
            otp.getVLAN().addAll(this.vlan);
        }
        if (null != this.crossConnection) {
            otp.getCrossConnection().addAll(this.crossConnection);
        }
        if (null != this.dialUpAccess) {
            otp.getDialUpAccess().addAll(this.dialUpAccess);
        }
        if (null != this.dialNumber) {
            otp.getDialNumber().addAll(this.dialNumber);
        }
        if (null != this.site) {
            otp.getSite().addAll(this.site);
        }
        otp.setHousing(this.housing);
        otp.setPSE(this.pse);
        return otp;
    }

    public OrderTechnicalParamsBuilder withCommon(OrderTechnicalParams.Common common) {
        this.common = common;
        return this;
    }

    public OrderTechnicalParamsBuilder withVPN(OrderTechnicalParams.VPN vpn) {
        this.vpn = vpn;
        return this;
    }

    public OrderTechnicalParamsBuilder withISDNBackup(OrderTechnicalParams.ISDNBackup isdnBackup) {
        this.isdnBackup = isdnBackup;
        return this;
    }

    public OrderTechnicalParamsBuilder withCPE(List<OrderTechnicalParams.CPE> cpe) {
        this.cpe = cpe;
        return this;
    }

    public OrderTechnicalParamsBuilder addCPE(OrderTechnicalParams.CPE cpe) {
        if (null == this.cpe) {

            this.cpe = new ArrayList<>();
        }
        this.cpe.add(cpe);
        return this;
    }

    public OrderTechnicalParamsBuilder withIPAddress(List<OrderTechnicalParams.IPAddress> ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public OrderTechnicalParamsBuilder addIPAddress(OrderTechnicalParams.IPAddress ipAddress) {
        if (null == this.ipAddress) {

            this.ipAddress = new ArrayList<>();
        }
        this.ipAddress.add(ipAddress);
        return this;
    }

    public OrderTechnicalParamsBuilder withPortForwarding(List<OrderTechnicalParams.PortForwarding> portForwarding) {
        this.portForwarding = portForwarding;
        return this;
    }

    public OrderTechnicalParamsBuilder addPortForwarding(OrderTechnicalParams.PortForwarding portForwarding) {
        if (null == this.portForwarding) {

            this.portForwarding = new ArrayList<>();
        }
        this.portForwarding.add(portForwarding);
        return this;
    }

    public OrderTechnicalParamsBuilder withStaticRoute(List<OrderTechnicalParams.StaticRoute> staticRoute) {
        this.staticRoute = staticRoute;
        return this;
    }

    public OrderTechnicalParamsBuilder addStaticRoute(OrderTechnicalParams.StaticRoute staticRoute) {
        if (null == this.staticRoute) {

            this.staticRoute = new ArrayList<>();
        }
        this.staticRoute.add(staticRoute);
        return this;
    }


    public OrderTechnicalParamsBuilder withVLAN(List<OrderTechnicalParams.VLAN> vlan) {
        this.vlan = vlan;
        return this;
    }

    public OrderTechnicalParamsBuilder addVLAN(OrderTechnicalParams.VLAN vlan) {
        if (null == this.vlan) {

            this.vlan = new ArrayList<>();
        }
        this.vlan.add(vlan);
        return this;
    }


    public OrderTechnicalParamsBuilder withCrossConnection(List<OrderTechnicalParams.CrossConnection> crossConnection) {
        this.crossConnection = crossConnection;
        return this;
    }

    public OrderTechnicalParamsBuilder addCrossConnection(OrderTechnicalParams.CrossConnection crossConnection) {
        if (null == this.crossConnection) {

            this.crossConnection = new ArrayList<>();
        }
        this.crossConnection.add(crossConnection);
        return this;
    }

    public OrderTechnicalParamsBuilder withDialUpAccess(List<OrderTechnicalParams.DialUpAccess> dialUpAccess) {
        this.dialUpAccess = dialUpAccess;
        return this;
    }

    public OrderTechnicalParamsBuilder addDialUpAccess(OrderTechnicalParams.DialUpAccess dialUpAccess) {
        if (null == this.dialUpAccess) {

            this.dialUpAccess = new ArrayList<>();
        }
        this.dialUpAccess.add(dialUpAccess);
        return this;
    }

    public OrderTechnicalParamsBuilder withDialNumber(List<OrderTechnicalParams.DialNumber> dialNumber) {
        this.dialNumber = dialNumber;
        return this;
    }

    public OrderTechnicalParamsBuilder addDialNumber(OrderTechnicalParams.DialNumber dialNumber) {
        if (null == this.dialNumber) {

            this.dialNumber = new ArrayList<>();
        }
        this.dialNumber.add(dialNumber);
        return this;
    }

    public OrderTechnicalParamsBuilder withSite(List<OrderTechnicalParams.Site> site) {
        this.site = site;
        return this;
    }

    public OrderTechnicalParamsBuilder addSite(OrderTechnicalParams.Site site) {
        if (null == this.site) {

            this.site = new ArrayList<>();
        }
        this.site.add(site);
        return this;
    }

    public OrderTechnicalParamsBuilder withHousing(OrderTechnicalParams.Housing housing) {
        this.housing = housing;
        return this;
    }

    public OrderTechnicalParamsBuilder withPse(final OrderTechnicalParams.PSE pse) {
        this.pse = pse;
        return this;
    }
}
