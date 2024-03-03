/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 13:51:46
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import java.util.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSdslDslamPortData;


/**
 * XStream Converter-Implementierung, um aus einem {@link CPSSdslDslamPortData} Objekt die notwendige XML Struktur in
 * From von {@code <DSLAM_PORT>value<DSLAM_PORT>} zu generieren.
 */
public class CPSSdslDslamPortConverter implements Converter {

    private static final String NODE_NAME = "DSLAM_PORT";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSSdslDslamPortData> sdslPorts = (List<CPSSdslDslamPortData>) source;
        if (CollectionTools.isNotEmpty(sdslPorts)) {
            for (CPSSdslDslamPortData sdslPort : sdslPorts) {
                writer.startNode(NODE_NAME);
                writer.setValue(sdslPort.getDslamPort());
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

}


