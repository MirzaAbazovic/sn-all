/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2011 15:19:11
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;

/**
 * Konfiguration der SIP Domänen.
 */
public enum SIPDomainReference {
    PRIVATE(22340L),
    BUSINESS(22341L),
    INTERNET(22342L);

    private final Long refId;

    private SIPDomainReference(Long refId) {
        this.refId = refId;
    }

    public Long getRefId() {
        return refId;
    }

    public static final SIPDomainReference findById(Long refId) {
        if (refId == null) { return null;}

        SIPDomainReference[] values = SIPDomainReference.values();
        for (SIPDomainReference status : values) {
            if (NumberTools.equal(status.getRefId(), refId)) {
                return status;
            }
        }
        return null;
    }

}


