/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 12:59:33
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.converter;

import java.util.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNServiceData;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;


/**
 * Converter-Implementierung, um aus einer Liste von Modellen des Typs <code>CPSDNServiceData</code> XML-Elemente in der
 * Form {@code <name>value</name>} zu erzeugen. <br> Falls die Rufnummernleistung mehrere Parameter besitzt, werden die
 * einzelnen Values in Sub-Elements geschrieben.
 *
 *
 */
public class CPSDNServiceConverter implements Converter {

    private static final String SUB_NODE_NAME = "VALUE";

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSDNServiceData> dnData = (List<CPSDNServiceData>) value;
        if (CollectionTools.isNotEmpty(dnData)) {
            for (CPSDNServiceData service : dnData) {
                writer.startNode(service.getServiceName());
                if (service.getServiceValue() != null) {
                    if (StringUtils.contains(service.getServiceValue(), CPSDNServiceData.CPS_VALUE_DELIMITER) ||
                            StringUtils.contains(service.getServiceValue(), Leistung2DN.PARAMETER_ID_VALUE_SEP)) {
                        String[] substrings = StringUtils.split(service.getServiceValue(), CPSDNServiceData.CPS_VALUE_DELIMITER);
                        for (String paramValue : substrings) {
                            writer.startNode(SUB_NODE_NAME);
                            writer.setValue(paramValue);
                            writer.endNode();
                        }
                    }
                    else {
                        writer.setValue(service.getServiceValue());
                    }
                }
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext ctx) {
        List<CPSDNServiceData> dnData = new ArrayList<CPSDNServiceData>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String name = reader.getNodeName();
            String value = reader.getValue();

            CPSDNServiceData dnService = new CPSDNServiceData();
            dnService.setServiceName(name);
            if (reader.hasMoreChildren()) {
                StringBuilder sb = new StringBuilder();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    String subName = reader.getNodeName();
                    if (StringUtils.equalsIgnoreCase(subName, SUB_NODE_NAME)) {
                        String subValue = reader.getValue();

                        if (sb.length() > 0) {
                            sb.append(CPSDNServiceData.CPS_VALUE_DELIMITER);
                        }
                        sb.append(subValue);

                        reader.moveUp();
                    }
                }
                dnService.setServiceValue(sb.toString());
            }
            else {
                dnService.setServiceValue(value);
            }
            dnData.add(dnService);

            reader.moveUp();
        }

        return dnData;
    }

}


