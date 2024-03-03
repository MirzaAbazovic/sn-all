/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 07:59:23
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSBusinessCpeDhcpSettingConverter;

/**
 * Modell-Klasse fuer die Abbildung von DHCP-Konfigurationen fuer ein Business CPE. <br> Das Streaming erfolgt ueber die
 * Konverter-Klasse {@link CPSBusinessCpeDhcpSettingConverter}.
 */
public class CPSBusinessCpeDhcpSettingData extends AbstractCPSServiceOrderDataModel {

    private String dhcpStartIpV4;
    private String dhcpEndIpV4;
    private String dhcpStartIpV6;
    private String dhcpEndIpV6;

    public CPSBusinessCpeDhcpSettingData(EGConfig egConfig) {
        setDhcpStart(egConfig.getDhcpPoolFromRef());
        setDhcpEnd(egConfig.getDhcpPoolToRef());
    }

    /**
     * setzt die DHCP-Endadresse anhand des gegebenen Typs.
     *
     * @param dhcpEndAddress
     */
    void setDhcpEnd(IPAddress dhcpEndAddress) {
        if (dhcpEndAddress != null) {
            if (dhcpEndAddress.isIPV4()) {
                setDhcpEndIpV4(IPToolsV4.instance().getAddressWithoutPrefix(dhcpEndAddress.getAddress()));
            }
            else if (dhcpEndAddress.isIPV6()) {
                setDhcpEndIpV6(IPToolsV6.instance().getAddressWithoutPrefix(dhcpEndAddress.getAddress()));
            }
        }
    }

    /**
     * setzt die DHCP-Startadresse anhand des gegebenen Typs.
     *
     * @param dhcpStartAddress
     */
    void setDhcpStart(IPAddress dhcpStartAddress) {
        if (dhcpStartAddress != null) {
            if (dhcpStartAddress.isIPV4()) {
                setDhcpStartIpV4(IPToolsV4.instance().getAddressWithoutPrefix(dhcpStartAddress.getAddress()));
            }
            else if (dhcpStartAddress.isIPV6()) {
                setDhcpStartIpV6(IPToolsV6.instance().getAddressWithoutPrefix(dhcpStartAddress.getAddress()));
            }
        }
    }

    public String getDhcpStartIpV4() {
        return dhcpStartIpV4;
    }

    void setDhcpStartIpV4(String dhcpStartIpV4) {
        this.dhcpStartIpV4 = dhcpStartIpV4;
    }

    public String getDhcpEndIpV4() {
        return dhcpEndIpV4;
    }

    void setDhcpEndIpV4(String dhcpEndIpV4) {
        this.dhcpEndIpV4 = dhcpEndIpV4;
    }

    /**
     * @return Returns the dhcpStartIpV6.
     */
    public String getDhcpStartIpV6() {
        return dhcpStartIpV6;
    }

    /**
     * @param dhcpStartIpV6 The dhcpStartIpV6 to set.
     */
    void setDhcpStartIpV6(String dhcpStartIpV6) {
        this.dhcpStartIpV6 = dhcpStartIpV6;
    }

    /**
     * @return Returns the dhcpEndIpV6.
     */
    public String getDhcpEndIpV6() {
        return dhcpEndIpV6;
    }

    /**
     * @param dhcpEndIpV6 The dhcpEndIpV6 to set.
     */
    void setDhcpEndIpV6(String dhcpEndIpV6) {
        this.dhcpEndIpV6 = dhcpEndIpV6;
    }

} // end


