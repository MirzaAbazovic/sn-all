/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2009 14:21:40
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.IdFieldName;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;

/**
 * EntityBuilder for HVTStandort objects
 *
 *
 */
@SuppressWarnings("unused")
public class HVTStandortBuilder extends AbstractCCIDModelBuilder<HVTStandortBuilder, HVTStandort> {
    private Date gueltigVon = DateTools.minusWorkDays(5);
    private Date gueltigBis = DateTools.plusWorkDays(5);
    private HVTGruppeBuilder hvtGruppeBuilder;
    private Long hvtGruppeId;
    private Integer asb = new Integer(45);
    private final String kvzNummer = null;
    private final String kvzSchaltnummer = null;
    private Long standortTypRefId = null;
    private String gesicherteRealisierung = null;
    private Integer ewsdOr1 = null;
    private final Integer ewsdOr2 = null;
    private final String beschreibung = null;
    private final Boolean virtuell = null;
    private Long carrierId = null;
    private Long carrierKennungId = null;
    private final Long carrierContactId = null;
    private Boolean cpsProvisioning = null;
    private Boolean autoVerteilen = null;
    private Boolean breakRangierung = null;
    private String clusterId = null;
    @ReferencedEntityId("betriebsraumId")
    @IdFieldName("id")
    @DontCreateBuilder
    private HVTStandortBuilder betriebsRaumBuilder;
    private String gfastStartfrequenz;

    public HVTStandortBuilder withGfastStartfrequenz(final String gfastStartfrequenz)   {
        this.gfastStartfrequenz = gfastStartfrequenz;
        return this;
    }

    public HVTGruppeBuilder getHvtGruppeBuilder() {
        return hvtGruppeBuilder;
    }

    public HVTStandortBuilder withHvtGruppeBuilder(HVTGruppeBuilder hvtGruppeBuilder) {
        this.hvtGruppeBuilder = hvtGruppeBuilder;
        return this;
    }

    public Integer getAsb() {
        return asb;
    }

    public HVTStandortBuilder withBetriebsraumBuilder(HVTStandortBuilder betriebsRaumBuilder) {
        this.betriebsRaumBuilder = betriebsRaumBuilder;
        return this;
    }

    public HVTStandortBuilder withAsb(Integer asb) {
        this.asb = asb;
        return this;
    }

    public HVTStandortBuilder withHvtGruppeId(Long hvtGruppeId) {
        this.hvtGruppeId = hvtGruppeId;
        return this;
    }

    public HVTStandortBuilder withEwsdOr1(Integer ewsdOr1) {
        this.ewsdOr1 = ewsdOr1;
        return this;
    }

    public HVTStandortBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }

    public HVTStandortBuilder withCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
        return this;
    }

    public HVTStandortBuilder withAutoVerteilen(Boolean autoVerteilen) {
        this.autoVerteilen = autoVerteilen;
        return this;
    }

    public HVTStandortBuilder withCarrierId(Long carrierId) {
        this.carrierId = carrierId;
        return this;
    }

    public HVTStandortBuilder withCarrierKennungId(Long carrierKennungId) {
        this.carrierKennungId = carrierKennungId;
        return this;
    }

    public HVTStandortBuilder withBreakRangierung(Boolean breakRangierung) {
        this.breakRangierung = breakRangierung;
        return this;
    }

    public HVTStandortBuilder withClusterId(String clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public HVTStandortBuilder withActivation() {
        this.gesicherteRealisierung = HVTStandort.GESICHERT_IN_BETRIEB;
        this.gueltigVon = new Date(0);
        this.gueltigBis = DateTools.getHurricanEndDate();
        return this;
    }

    public HVTStandortBuilder withGesichertNichtInBetrieb() {
        this.gesicherteRealisierung = HVTStandort.GESICHERT_NICHT_IN_BETRIEB;
        return this;
    }

    public HVTStandortBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public HVTStandortBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }
}
