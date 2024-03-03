/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2009 07:20:24
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;

/**
 * Modell-Klasse fuer die Abbildung von IP-Routes der Typen v4 und v6.
 *
 *
 */
@XStreamAlias("ROUTE")
public final class CPSRadiusRoutesData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("PREFIXLENGTH")
    private Long prefixLength = Long.valueOf(0);
    @XStreamAlias("METRIC")
    private Long metric = Long.valueOf(0);
    @XStreamAlias("IPV4ADDRESS")
    private String ipV4Address = null;
    @XStreamAlias("IPV6ADDRESS")
    private String ipV6Address = null;

    public static CPSRadiusRoutesData createIPv4(String ipAddress) {
        CPSRadiusRoutesData data = new CPSRadiusRoutesData();
        data.setIpV4Address(ipAddress);
        data.setIpV6Address(null);

        int prefix = IPToolsV4.instance().getPrefixLength4Address(ipAddress);
        data.setPrefixLength(((prefix < 0) || (prefix == IPToolsV4.instance().getMaximumBits())) ? Long.valueOf(0) : Long.valueOf(prefix));
        return data;
    }

    public static CPSRadiusRoutesData createIPv6(String ipAddress) {
        CPSRadiusRoutesData data = new CPSRadiusRoutesData();
        data.setIpV6Address(ipAddress);
        data.setIpV4Address(null);

        int prefix = IPToolsV6.instance().getPrefixLength4Address(ipAddress);
        data.setPrefixLength(((prefix < 0) || (prefix == IPToolsV6.instance().getMaximumBits())) ? Long.valueOf(0) : Long.valueOf(prefix));
        return data;
    }

    private CPSRadiusRoutesData() {
    }

    public CPSRadiusRoutesData withMetric(long metric) {
        setMetric(metric);
        return this;
    }

    /**
     * @return the prefixLength
     */
    public Long getPrefixLength() {
        return prefixLength;
    }

    /**
     * @param prefixLength the prefixLength to set
     */
    void setPrefixLength(Long prefixLength) {
        this.prefixLength = prefixLength;
    }

    /**
     * @return the metric
     */
    public Long getMetric() {
        return metric;
    }

    /**
     * @param metric the metric to set
     */
    void setMetric(Long metric) {
        this.metric = metric;
    }

    /**
     * @return the ipV4Address
     */
    public String getIpV4Address() {
        return ipV4Address;
    }

    /**
     * @param ipV4Address the ipV4Address to set
     */
    void setIpV4Address(String ipV4Address) {
        this.ipV4Address = ipV4Address;
    }

    /**
     * @return the ipV6Address
     */
    public String getIpV6Address() {
        return ipV6Address;
    }

    /**
     * @param ipV6Address the ipV6Address to set
     */
    void setIpV6Address(String ipV6Address) {
        this.ipV6Address = ipV6Address;
    }

}  // end


