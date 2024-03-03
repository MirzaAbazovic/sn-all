/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;
import com.google.common.collect.Lists;

/**
 */
public class PortierungWarnings {
    private final List<PortierungWarning> warnings = Lists.newArrayList();

    public void addWarning(PortierungWarning hint) {
        warnings.add(hint);
    }

    public List<PortierungWarning> getWarnings() {
        return Lists.newArrayList(warnings);
    }

    public boolean isEmpty() {
        return warnings.isEmpty();
    }
}

