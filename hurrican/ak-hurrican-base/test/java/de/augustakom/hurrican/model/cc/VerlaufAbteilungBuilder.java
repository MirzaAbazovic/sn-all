/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2010 08:35:49
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;


/**
 * Entity Builder fuer {@link VerlaufAbteilung} Objekte.
 */
@SuppressWarnings("unused")
public class VerlaufAbteilungBuilder extends AbstractCCIDModelBuilder<VerlaufAbteilungBuilder, VerlaufAbteilung> {

    @DontCreateBuilder
    private VerlaufBuilder verlaufBuilder;
    private Long verlaufId;

    private Long abteilungId = Abteilung.ST_ONLINE;
    private Long parentVerlaufAbteilungId;
    private Date datumAn;
    private Date datumErledigt = null;
    private Date ausgetragenAm;
    private String ausgetragenVon;
    private Long verlaufStatusId = VerlaufStatus.BEI_DISPO;
    private String bearbeiter;
    private String bemerkung;
    private Date realisierungsdatum;
    @DontCreateBuilder
    private ExtServiceProviderBuilder extServiceProviderBuilder;
    private Long extServiceProviderId;
    private Long niederlassungId = Niederlassung.ID_MUENCHEN;
    private Integer zusatzAufwand;
    private Boolean notPossible;
    private Long notPossibleReasonRefId;
    private Date wiedervorlage;
    private Long verlaufAbteilungStatusId;

    public VerlaufBuilder getVerlaufBuilder() {
        return verlaufBuilder;
    }

    public VerlaufAbteilungBuilder withVerlaufBuilder(VerlaufBuilder verlaufBuilder) {
        this.verlaufBuilder = verlaufBuilder;
        return this;
    }

    public VerlaufAbteilungBuilder withAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
        return this;
    }

    public VerlaufAbteilungBuilder withRealisierungsdatum(Date realisierungsdatum) {
        this.realisierungsdatum = realisierungsdatum;
        return this;
    }

    public VerlaufAbteilungBuilder withVerlaufStatusId(Long verlaufStatusId) {
        this.verlaufStatusId = verlaufStatusId;
        return this;
    }

    public VerlaufAbteilungBuilder withExtServiceProviderBuilder(ExtServiceProviderBuilder extServiceProviderBuilder) {
        this.extServiceProviderBuilder = extServiceProviderBuilder;
        return this;
    }

    public VerlaufAbteilungBuilder withParentVerlaufAbteilungId(final Long parentVerlaufAbteilungId) {
        this.parentVerlaufAbteilungId = parentVerlaufAbteilungId;
        return this;
    }

    public VerlaufAbteilungBuilder withBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }

    public VerlaufAbteilungBuilder withDatumErledigt(final Date datumErledigt) {
        this.datumErledigt = datumErledigt;
        return this;
    }

    public VerlaufAbteilungBuilder withNotPossible(final Boolean notPossible) {
        this.notPossible = notPossible;
        return this;
    }

    public VerlaufAbteilungBuilder withVerlaufAbteilungStatusId(final Long verlaufAbteilungStatusId) {
        this.verlaufAbteilungStatusId = verlaufAbteilungStatusId;
        return this;
    }

    public VerlaufAbteilungBuilder withBearbeiter(final String bearbeiter) {
        this.bearbeiter = bearbeiter;
        return this;
    }
}


