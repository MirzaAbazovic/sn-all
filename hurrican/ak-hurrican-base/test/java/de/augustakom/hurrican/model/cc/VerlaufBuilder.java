/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2009 14:49:40
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;

@SuppressWarnings("unused")
public class VerlaufBuilder extends AbstractCCIDModelBuilder<VerlaufBuilder, Verlauf> {

    private AuftragBuilder auftragBuilder = null;
    private Long auftragId = null;

    private Long anlass = BAVerlaufAnlass.INTERN_WORK;
    private Long verlaufStatusId = VerlaufStatus.BEI_TECHNIK;
    private Long statusIdAlt = null;
    private Boolean projektierung = null;
    private Date realisierungstermin = DateTools.createDate(2010, 0, 1);
    private Boolean akt = null;
    private Boolean manuellVerteilt = false;
    private Long portierungsartId = null;
    private Boolean verschoben = null;
    private Boolean observeProcess = null;
    private String bemerkung = "Testverlauf";
    private Long installationRefId = null;
    private Boolean preventCPSProvisioning = null;
    private Boolean notPossible = null;
    private Set<Long> subAuftragsIds = null;
    private Long verlaufStatusIdAlt;
    private String workforceOrderId = null;

    public VerlaufBuilder withVerlaufStatusIdAlt(final Long verlaufStatusIdAlt) {
        this.statusIdAlt = verlaufStatusIdAlt;
        return this;
    }

    public VerlaufBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public VerlaufBuilder withAnlass(Long baVerlaufAnlass) {
        this.anlass = baVerlaufAnlass;
        return this;
    }

    public VerlaufBuilder withVerlaufStatusId(Long verlaufStatusId) {
        this.verlaufStatusId = verlaufStatusId;
        return this;
    }

    public VerlaufBuilder withProjektierung(boolean projektierung) {
        this.projektierung = projektierung;
        return this;
    }

    public VerlaufBuilder withAkt(Boolean akt) {
        this.akt = akt;
        return this;
    }

    public VerlaufBuilder withPreventCPSProvisioning(Boolean preventCPSProvisioning) {
        this.preventCPSProvisioning = preventCPSProvisioning;
        return this;
    }

    public VerlaufBuilder withSubAuftragsIds(Set<Long> subAuftragsIds) {
        this.subAuftragsIds = subAuftragsIds;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public Long getAnlass() {
        return anlass;
    }

    public Long getVerlaufStatusId() {
        return verlaufStatusId;
    }

    public Boolean getAkt() {
        return akt;
    }

    public VerlaufBuilder withRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
        return this;
    }

    public VerlaufBuilder withWorkforceOrderId(String workforceOrderId) {
        this.workforceOrderId = workforceOrderId;
        return this;
    }

    public VerlaufBuilder withObserveProcess(Boolean observeProcess) {
        this.observeProcess = observeProcess;
        return this;
    }

    public VerlaufBuilder withNotPossible(Boolean notPossible) {
        this.notPossible = notPossible;
        return this;
    }

    public VerlaufBuilder withAuftragId(Long auftragId) {
        this.auftragId=auftragId;
        return this;
    }
}
