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
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpePortForwardingData;

/**
 * XStream Converter-Implementierung, um aus einer Liste von {@link CPSBusinessCpePortForwardingData} Objekten die
 * notwendige XML Strukturen als Subtree des {@code <PORT_FORWARDING>} Tags zu generieren.
 */
public class CPSBusinessCpePortForwardingConverter implements Converter {
    private static final String NODE_DEST_IPV4 = "DESTINATION_IPV4";
    private static final String NODE_SOURCE_IPV4 = "SOURCE_IPV4";
    private static final String NODE_DEST_IPV6 = "DESTINATION_IPV6";
    private static final String NODE_SOURCE_IPV6 = "SOURCE_IPV6";
    private static final String NODE_DEST_PORT = "DESTINATION_PORT";
    private static final String NODE_SOURCE_PORT = "SOURCE_PORT";
    private static final String NODE_PROTOCOL = "PROTOCOL";
    private static final String NODE_NAME = "CONFIG";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSBusinessCpePortForwardingData> portForwardings = (List<CPSBusinessCpePortForwardingData>) source;
        if (CollectionTools.isNotEmpty(portForwardings)) {
            for (CPSBusinessCpePortForwardingData portForwarding : portForwardings) {
                writer.startNode(NODE_NAME);
                if (portForwarding.getProtocol() != null) {
                    writer.startNode(NODE_PROTOCOL);
                    writer.setValue(portForwarding.getProtocol());
                    writer.endNode();
                }
                if ((portForwarding.getSourceIpV4() != null) || (portForwarding.getDestinationIpV4() != null)) {
                    if ((portForwarding.getSourceIpV4() != null)) {
                        writer.startNode(NODE_SOURCE_IPV4);
                        writer.setValue(portForwarding.getSourceIpV4());
                        writer.endNode();
                    }
                    if ((portForwarding.getDestinationIpV4() != null)) {
                        writer.startNode(NODE_DEST_IPV4);
                        writer.setValue(portForwarding.getDestinationIpV4());
                        writer.endNode();
                    }
                }
                else if ((portForwarding.getSourceIpV6() != null) || (portForwarding.getDestinationIpV6() != null)) {
                    if (portForwarding.getSourceIpV6() != null) {
                        writer.startNode(NODE_SOURCE_IPV6);
                        writer.setValue(portForwarding.getSourceIpV6());
                        writer.endNode();
                    }
                    if (portForwarding.getDestinationIpV6() != null) {
                        writer.startNode(NODE_DEST_IPV6);
                        writer.setValue(portForwarding.getDestinationIpV6());
                        writer.endNode();
                    }
                }
                if (portForwarding.getSourcePort() != null) {
                    writer.startNode(NODE_SOURCE_PORT);
                    writer.setValue(portForwarding.getSourcePort().toString());
                    writer.endNode();
                }
                if (portForwarding.getDestinationPort() != null) {
                    writer.startNode(NODE_DEST_PORT);
                    writer.setValue(portForwarding.getDestinationPort().toString());
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
