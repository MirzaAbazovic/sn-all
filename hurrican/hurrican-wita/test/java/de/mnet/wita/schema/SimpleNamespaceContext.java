/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2016
 */
package de.mnet.wita.schema;

import java.util.*;
import javax.xml.namespace.*;

/**
 * All namespaces that you intend to select from in the source XML must be associated with a prefix in the host
 * language. In Java/JAXP this is done by specifying the URI for each namespace prefix using an instance of
 * javax.xml.namespace.NamespaceContext
 */
public class SimpleNamespaceContext implements NamespaceContext {

    private final Map<String, String> PREF_MAP = new HashMap<String, String>();

    public SimpleNamespaceContext(final Map<String, String> prefMap) {
        PREF_MAP.putAll(prefMap);
    }

    public String getNamespaceURI(String prefix) {
        return PREF_MAP.get(prefix);
    }

    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}
