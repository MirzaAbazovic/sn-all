/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2009 18:17:18
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;


/**
 *
 */
@SuppressWarnings("unused")
public class RangierungBuilder extends AbstractCCIDModelBuilder<RangierungBuilder, Rangierung> {

    @DontCreateBuilder
    @ReferencedEntityId("esId")
    private EndstelleBuilder endstelleBuilder;
    private Long esId = null;

    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private EquipmentBuilder eqInBuilder;
    private EquipmentBuilder eqOutBuilder;
    private PhysikTypBuilder physikTypBuilder;
    private Long physikTypId;
    private Freigegeben freigegeben = Freigegeben.freigegeben;
    private Date freigabeAb = null;
    private Integer leitungGesamtId = null;
    private Integer leitungLfdNr = null;
    private Boolean leitungLoeschen = null;
    private String bemerkung = null;
    private String userW = null;
    private Date dateW = null;
    private Integer historyFrom = null;
    private Integer historyCount = null;
    private Long rangierungsAuftragId = null;
    private String ontId = null;

    private Date gueltigVon = new Date(0);
    private Date gueltigBis = DateTools.getHurricanEndDate();


    @Override
    protected void initialize() {
        eqInBuilder.withHvtStandortBuilder(hvtStandortBuilder);
        eqOutBuilder.withHvtStandortBuilder(hvtStandortBuilder);
    }

    @Override
    protected void afterBuild(final Rangierung rangierung) {
        // Zirkulaere Referenz
        if ((endstelleBuilder != null) && !endstelleBuilder.isBuilding()) {
            endstelleBuilder.get().setRangierId(rangierung.getId());
            rangierung.setEsId(endstelleBuilder.get().getId());
        }
    }

    public EquipmentBuilder getEqInBuilder() {
        return eqInBuilder;
    }

    public EquipmentBuilder getEqOutBuilder() {
        return eqOutBuilder;
    }

    public EndstelleBuilder getEndstelleBuilder() {
        return endstelleBuilder;
    }

    public HVTStandortBuilder getHvtStandortBuilder() {
        return hvtStandortBuilder;
    }

    public Integer getLeitungGesamtId() {
        return leitungGesamtId;
    }

    public RangierungBuilder withRangierungsAuftragId(final Long rangierungsAuftragId) {
        this.rangierungsAuftragId = rangierungsAuftragId;
        return this;
    }

    public RangierungBuilder withEqInBuilder(final EquipmentBuilder eqInBuilder) {
        this.eqInBuilder = eqInBuilder;
        return this;
    }

    public RangierungBuilder withEqOutBuilder(final EquipmentBuilder eqOutBuilder) {
        this.eqOutBuilder = eqOutBuilder;
        return this;
    }

    public RangierungBuilder withPhysikTypBuilder(final PhysikTypBuilder physikTypBuilder) {
        this.physikTypBuilder = physikTypBuilder;
        return this;
    }

    public RangierungBuilder withPhysikTypId(final Long physikTypId) {
        this.physikTypId = physikTypId;
        return this;
    }

    public RangierungBuilder withHvtStandortBuilder(final HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        if (eqInBuilder != null) {
            eqInBuilder.withHvtStandortBuilder(hvtStandortBuilder);
        }

        if (eqOutBuilder != null) {
            eqOutBuilder.withHvtStandortBuilder(hvtStandortBuilder);
        }
        return this;
    }

    public RangierungBuilder withOntId(final String ontId) {
        this.ontId = ontId;
        return this;
    }

    public RangierungBuilder withFreigegeben(final Freigegeben freigegeben) {
        this.freigegeben = freigegeben;
        return this;
    }

    public RangierungBuilder withLeitungGesamtId(final Integer leitungGesamtId) {
        this.leitungGesamtId = leitungGesamtId;
        return this;
    }

    public RangierungBuilder withRandomLeitungGesamtId() {
        this.leitungGesamtId = randomInt(Integer.MAX_VALUE / 2, Integer.MAX_VALUE);
        return this;
    }

    public RangierungBuilder withLeitungLfdNr(final Integer leitungLfdNr) {
        this.leitungLfdNr = leitungLfdNr;
        return this;
    }

    public RangierungBuilder withFreigabeAb(final Date freigabeAb) {
        this.freigabeAb = freigabeAb;
        return this;
    }

    public RangierungBuilder withBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
        return this;
    }

    public RangierungBuilder withEndstelleBuilder(final EndstelleBuilder endstelleBuilder) {
        if (esId != null) {
            throw new IllegalStateException("Choose either withEndstelleBuilder or withEsId");
        }
        this.endstelleBuilder = endstelleBuilder;
        if ((endstelleBuilder.getRangierungBuilder() != this) && (endstelleBuilder.getRangierungAdditionalBuilder() != this)) {
            endstelleBuilder.withRangierungBuilder(this);
        }
        return this;
    }

    public RangierungBuilder withEsId(final Long esId) {
        if (endstelleBuilder != null) {
            throw new IllegalStateException("Choose either withEndstelleBuilder or withEsId");
        }
        this.esId = esId;
        return this;
    }

    public RangierungBuilder withGueltigBis(final Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }
}
