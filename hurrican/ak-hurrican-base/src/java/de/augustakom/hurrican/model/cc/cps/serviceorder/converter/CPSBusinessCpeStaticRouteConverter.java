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
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeStaticRouteData;

/**
 * XStream Converter-Implementierung, um aus einer Liste von {@link CPSBusinessCpeStaticRouteData} Objekten die
 * notwendige XML Strukturen als Subtree des {@code <STATIC_ROUTES>} Tags zu generieren.
 */
public class CPSBusinessCpeStaticRouteConverter implements Converter {

    private static final String NODE_DEST_IPV4 = "DESTINATION_IPV4";
    private static final String NODE_DEST_IPV6 = "DESTINATION_IPV6";
    private static final String NODE_PREFIX = "PREFIX";
    private static final String NODE_NETX_HOP = "NEXT_HOP_IPV4";
    private static final String NODE_NAME = "CONFIG";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSBusinessCpeStaticRouteData> staticRoutes = (List<CPSBusinessCpeStaticRouteData>) source;
        if (CollectionTools.isNotEmpty(staticRoutes)) {
            for (CPSBusinessCpeStaticRouteData staticRoute : staticRoutes) {
                writer.startNode(NODE_NAME);
                if (staticRoute.getDestinationIpV4() != null) {
                    writer.startNode(NODE_DEST_IPV4);
                    writer.setValue(staticRoute.getDestinationIpV4());
                    writer.endNode();
                }
                else if (staticRoute.getDestinationIpV6() != null) {
                    writer.startNode(NODE_DEST_IPV6);
                    writer.setValue(staticRoute.getDestinationIpV6());
                    writer.endNode();
                }
                if (staticRoute.getPrefix() != null) {
                    writer.startNode(NODE_PREFIX);
                    writer.setValue(staticRoute.getPrefix());
                    writer.endNode();
                }
                if (staticRoute.getNextHopIpV4() != null) {
                    writer.startNode(NODE_NETX_HOP);
                    writer.setValue(staticRoute.getNextHopIpV4());
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
