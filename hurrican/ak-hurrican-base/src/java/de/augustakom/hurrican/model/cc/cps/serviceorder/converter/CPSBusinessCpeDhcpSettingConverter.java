/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 13:51:46
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeDhcpSettingData;

/**
 * XStream Converter-Implementierung, um aus einem {@link CPSBusinessCpeDhcpSettingData} Objekt die notwendige XML
 * Struktur in Form von <br> {@code <DHCP_START_IPV4></DHCP_START_IPV4>} <br> {@code <DHCP_END_IPV4></DHCP_END_IPV4>}
 * <br> {@code <DHCP_START_IPV6></DHCP_START_IPV6>} <br> {@code <DHCP_END_IPV6></DHCP_END_IPV6>} <br> zu generieren.
 */
public class CPSBusinessCpeDhcpSettingConverter implements Converter {

    private static final String DHCP_START_IPV4 = "DHCP_START_IPV4";
    private static final String DHCP_END_IPV4 = "DHCP_END_IPV4";
    private static final String DHCP_START_IPV6 = "DHCP_START_IPV6";
    private static final String DHCP_END_IPV6 = "DHCP_END_IPV6";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return CPSBusinessCpeDhcpSettingData.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        CPSBusinessCpeDhcpSettingData dhcpSettings = (CPSBusinessCpeDhcpSettingData) source;
        if (dhcpSettings != null) {
            if (StringUtils.isNotBlank(dhcpSettings.getDhcpStartIpV4())) {
                writer.startNode(DHCP_START_IPV4);
                writer.setValue(dhcpSettings.getDhcpStartIpV4());
                writer.endNode();
            }
            if (StringUtils.isNotBlank(dhcpSettings.getDhcpEndIpV4())) {
                writer.startNode(DHCP_END_IPV4);
                writer.setValue(dhcpSettings.getDhcpEndIpV4());
                writer.endNode();
            }
            if (StringUtils.isNotBlank(dhcpSettings.getDhcpStartIpV6())) {
                writer.startNode(DHCP_START_IPV6);
                writer.setValue(dhcpSettings.getDhcpStartIpV6());
                writer.endNode();
            }
            if (StringUtils.isNotBlank(dhcpSettings.getDhcpEndIpV6())) {
                writer.startNode(DHCP_END_IPV6);
                writer.setValue(dhcpSettings.getDhcpEndIpV6());
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

} // end
