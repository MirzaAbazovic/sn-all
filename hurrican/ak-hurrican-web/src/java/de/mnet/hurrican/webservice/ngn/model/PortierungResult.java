/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2016
 */
package de.mnet.hurrican.webservice.ngn.model;

import java.util.*;
import com.google.common.collect.Maps;

/**
 */
public class PortierungResult {
    private Map<Long, PortierungStatus> statusMap = Maps.newHashMap();
    private Map<Long, Optional<PortierungWarnings>> warningsMap = Maps.newHashMap();

    public void putStatus(Long id, PortierungStatusEnum portierungStatusEnum, String message) {
        Optional<PortierungWarnings> warnings = warningsMap.getOrDefault(id, Optional.empty());
        PortierungStatus status = new PortierungStatus(portierungStatusEnum, message, warnings);
        statusMap.put(id, status);
    }

    public void addWarning(Long id, PortierungWarning warning) {
        Optional<PortierungWarnings> warningsOptional = getWarnings(id);
        PortierungWarnings warnings;
        if (warningsOptional.isPresent()) {
            warnings = warningsOptional.get();
        }
        else {
            warnings = new PortierungWarnings();
            this.warningsMap.put(id, Optional.of(warnings));
        }
        warnings.addWarning(warning);

        updateWarningsInStatusIfPresent(id);
    }

    private void updateWarningsInStatusIfPresent(long id) {
        if (statusMap.containsKey(id)) {
            PortierungStatus status = statusMap.get(id);
            Optional<PortierungWarnings> updatedWarningsOptional = getWarnings(id);
            PortierungStatus updatedStatus =
                    new PortierungStatus(status.getPortierungStatusEnum(), status.getMessage(), updatedWarningsOptional);
            statusMap.put(id, updatedStatus);
        }
    }

    public Map<Long, PortierungStatus> getPortierungResults() {
        return statusMap;
    }

    private Optional<PortierungWarnings> getWarnings(long id) {
        return warningsMap.getOrDefault(id, Optional.empty());
    }
}

