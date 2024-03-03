/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 07:55:56
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Routing;


/**
 * Modell-Klasse fuer die Abbildung von statischen Routen fuer ein Business CPE.
 */
public class CPSBusinessCpeStaticRouteData extends AbstractCPSServiceOrderDataModel {

    private String destinationIpV4;
    private String destinationIpV6;
    private String prefix;
    private String nextHopIpV4;

    /**
     * Konstruktor mit Angabe des {@link Routing} Objekts, aus dem das CPS-Objekt aufgebaut werden soll.
     *
     * @param routing
     */
    public CPSBusinessCpeStaticRouteData(Routing routing) {
        setNextHopIpV4(routing.getNextHop());
        if (routing.getDestinationAdressRef() != null) {
            if (routing.getDestinationAdressRef().isIPV4()) {
                setV4(routing, routing.getDestinationAdressRef());
            }
            else if (routing.getDestinationAdressRef().isIPV6()) {
                setV6(routing.getDestinationAdressRef());
            }
        }
    }

    private String getAbsoluteAddressOrDefault(IPAddress ipAddress) {
        String address = ipAddress.getAbsoluteAddress();
        if (address == null) {
            address = ipAddress.getAddress();
        }
        return address;
    }

    void setV4(Routing routing, IPAddress ipAddress) {
        setDestinationIpV4(getAbsoluteAddressOrDefault(ipAddress));
        int prefix = (ipAddress != null) ? IPToolsV4.instance().getPrefixLength4Address(ipAddress.getAddress()) : -1;
        setPrefix((prefix < 0) ? "0" : String.format("%s", prefix));
    }

    void setV6(IPAddress ipAddress) {
        setDestinationIpV6(getAbsoluteAddressOrDefault(ipAddress));
        int prefix = (ipAddress != null) ? IPToolsV6.instance().getPrefixLength4Address(ipAddress.getAddress()) : -1;
        setPrefix((prefix < 0) ? "0" : String.format("%s", prefix));
    }

    public String getDestinationIpV4() {
        return destinationIpV4;
    }

    public void setDestinationIpV4(String destinationIpV4) {
        this.destinationIpV4 = destinationIpV4;
    }

    public String getDestinationIpV6() {
        return destinationIpV6;
    }

    public void setDestinationIpV6(String destinationIpV6) {
        this.destinationIpV6 = destinationIpV6;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNextHopIpV4() {
        return nextHopIpV4;
    }

    public void setNextHopIpV4(String nextHopIpV4) {
        this.nextHopIpV4 = StringUtils.deleteWhitespace(nextHopIpV4);
    }

}


