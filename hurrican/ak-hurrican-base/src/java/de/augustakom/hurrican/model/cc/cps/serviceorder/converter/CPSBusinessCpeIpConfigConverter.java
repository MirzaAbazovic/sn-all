/**
 *
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import java.util.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeIpConfigData;

/**
 * XStream Converter-Implementierung, um aus einer Liste von {@link CPSBusinessCpeIpConfigData} Objekten die notwendige
 * XML Strukturen als Subtree des {@code <IP_CONFIG>} Tags zu generieren.
 */
public class CPSBusinessCpeIpConfigConverter implements Converter {

    private static final String NODE_ADDRESS_IPV4 = "ADDRESS_IPV4";
    private static final String NODE_ADDRESS_IPV6 = "ADDRESS_IPV6";
    private static final String NODE_PREFIX = "PREFIX";
    private static final String NODE_TYPE = "TYPE";
    private static final String NODE_NAME = "CONFIG";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSBusinessCpeIpConfigData> ipConfigs = (List<CPSBusinessCpeIpConfigData>) source;
        if (CollectionTools.isNotEmpty(ipConfigs)) {
            for (CPSBusinessCpeIpConfigData ipConfig : ipConfigs) {
                writer.startNode(NODE_NAME);
                if (ipConfig.getIpV4Address() != null) {
                    writer.startNode(NODE_ADDRESS_IPV4);
                    writer.setValue(ipConfig.getIpV4Address());
                    writer.endNode();
                }
                else if (ipConfig.getIpV6Address() != null) {
                    writer.startNode(NODE_ADDRESS_IPV6);
                    writer.setValue(ipConfig.getIpV6Address());
                    writer.endNode();
                }
                if (ipConfig.getPrefix() != null) {
                    writer.startNode(NODE_PREFIX);
                    writer.setValue(ipConfig.getPrefix());
                    writer.endNode();
                }
                if (ipConfig.getType() != null) {
                    writer.startNode(NODE_TYPE);
                    writer.setValue(ipConfig.getType());
                    writer.endNode();
                }
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }
}
