/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 07:55:20
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;


/**
 * Modell-Klasse fuer die Abbildung von IP-Konfigurationen fuer ein Business CPE.
 */
public class CPSBusinessCpeIpConfigData extends AbstractCPSServiceOrderDataModel {

    private static final long serialVersionUID = -5345983278913608163L;
    private String ipV4Address;
    private String ipV6Address;
    private String prefix;
    private String type;

    /**
     * Konstruktor mit Angabe des {@link EndgeraetIp} Objekts, aus dem das CPS Modell aufgebaut werden soll.
     *
     * @param egIp
     */
    public CPSBusinessCpeIpConfigData(EndgeraetIp egIp) {
        setType(egIp.getAddressType());
        if (egIp.getIpAddressRef() != null) {
            if (egIp.getIpAddressRef().isIPV4()) {
                setV4(egIp.getIpAddressRef());
            }
            else if (egIp.getIpAddressRef().isIPV6()) {
                setV6(egIp.getIpAddressRef());
            }
        }
    }

    private String getAbsoluteAddressOrDefault(IPAddress ipAddress) {
        String addressV4 = ipAddress.getAbsoluteAddress();
        if (addressV4 == null) {
            addressV4 = ipAddress.getAddress();
        }
        return addressV4;
    }

    void setV4(IPAddress ipAddress) {
        setIpV4Address(getAbsoluteAddressOrDefault(ipAddress));
        if ("WAN".equalsIgnoreCase(type)) {
            setPrefix(Integer.toString(IPToolsV4.instance().getMaximumBits()));
        }
        else {
            int prefix = (ipAddress != null) ? IPToolsV4.instance().netmaskSize(ipAddress.getAddress()) : -1;
            setPrefix((prefix < 0) ? "0" : String.format("%s", prefix));
        }
    }

    void setV6(IPAddress ipAddress) {
        setIpV6Address(getAbsoluteAddressOrDefault(ipAddress));
        if ("WAN".equalsIgnoreCase(type)) {
            setPrefix(Integer.toString(IPToolsV6.instance().getMaximumBits()));
        }
        else {
            int prefix = IPToolsV6.instance().getPrefixLength4Address(ipAddress.getAddress());
            setPrefix((prefix < 0) ? "0" : String.format("%s", prefix));
        }
    }

    public String getIpV4Address() {
        return ipV4Address;
    }

    public void setIpV4Address(String ipV4Address) {
        this.ipV4Address = ipV4Address;
    }

    public String getIpV6Address() {
        return ipV6Address;
    }

    public void setIpV6Address(String ipV6Address) {
        this.ipV6Address = ipV6Address;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}


