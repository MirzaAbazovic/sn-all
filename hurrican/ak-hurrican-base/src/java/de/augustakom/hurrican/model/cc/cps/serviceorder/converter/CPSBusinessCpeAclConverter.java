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
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeAclData;


/**
 * XStream Converter-Implementierung, um aus einer Liste von {@link CPSBusinessCpeAclData} Objekten die notwendige XML
 * Struktur in Form von {@code <ACL>value<ACL>} zu generieren.
 */
public class CPSBusinessCpeAclConverter implements Converter {

    private static final String NODE_NAME = "ACL";

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<CPSBusinessCpeAclData> acls = (List<CPSBusinessCpeAclData>) source;
        if (CollectionTools.isNotEmpty(acls)) {
            for (CPSBusinessCpeAclData acl : acls) {
                writer.startNode(NODE_NAME);
                writer.setValue(acl.getAcl());
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

}


