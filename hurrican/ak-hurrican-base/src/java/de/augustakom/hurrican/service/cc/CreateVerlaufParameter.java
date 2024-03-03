/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2012 10:47:11
 */
package de.augustakom.hurrican.service.cc;

import java.io.*;
import java.util.*;

/**
 * Parameter-Object fuer {@link BAService#createVerlauf(CreateVerlaufParameter)}
 */
public class CreateVerlaufParameter implements Serializable {
    public Long auftragId;
    public Date realisierungsTermin;
    public Long anlass;
    public Long installType;
    public boolean anZentraleDispo;
    public Long sessionId;
    public Set<Long> subAuftragsIds;

    public CreateVerlaufParameter() {
    }

    public CreateVerlaufParameter(final Long auftragId, final Date realisierungsTermin, final Long anlass, final Long installType,
            final boolean anZentraleDispo, final Long sessionId, final Set<Long> subAuftragsIds) {
        this.auftragId = auftragId;
        this.realisierungsTermin = realisierungsTermin;
        this.anlass = anlass;
        this.installType = installType;
        this.anZentraleDispo = anZentraleDispo;
        this.sessionId = sessionId;
        this.subAuftragsIds = subAuftragsIds;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Date getRealisierungsTermin() {
        return realisierungsTermin;
    }

    public void setRealisierungsTermin(Date realisierungsTermin) {
        this.realisierungsTermin = realisierungsTermin;
    }

    public Long getAnlass() {
        return anlass;
    }

    public void setAnlass(Long anlass) {
        this.anlass = anlass;
    }

    public Long getInstallType() {
        return installType;
    }

    public void setInstallType(Long installType) {
        this.installType = installType;
    }

    public boolean isAnZentraleDispo() {
        return anZentraleDispo;
    }

    public void setAnZentraleDispo(boolean anZentraleDispo) {
        this.anZentraleDispo = anZentraleDispo;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Set<Long> getSubAuftragsIds() {
        return subAuftragsIds;
    }

    public void setSubAuftragsIds(Set<Long> subAuftragsIds) {
        this.subAuftragsIds = subAuftragsIds;
    }
}

