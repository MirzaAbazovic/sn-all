/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2009 10:31:55
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;


/**
 *
 */
@SuppressWarnings("unused")
public class EndstelleBuilder extends AbstractCCIDModelBuilder<EndstelleBuilder, Endstelle> {

    private AuftragTechnik2EndstelleBuilder endstelleGruppeBuilder;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    @ReferencedEntityId("rangierId")
    private RangierungBuilder rangierungBuilder = null;
    @DontCreateBuilder
    @ReferencedEntityId("rangierIdAdditional")
    private RangierungBuilder rangierungAdditionalBuilder = null;
    @DontCreateBuilder
    private CCAddressBuilder addressBuilder = null;
    private String endstelleTyp = Endstelle.ENDSTELLEN_TYP_B;
    @DontCreateBuilder
    private Carrierbestellung2EndstelleBuilder cb2EsBuilder = null;
    private String endstelle = randomString(120);
    private String name = null;
    private String ort = null;
    private String plz = null;
    private String geschaltenUeber1 = null;
    private String geschaltenUeber2 = null;
    private Long anschlussart = Anschlussart.ANSCHLUSSART_HVT;
    private String bemerkungStawa = null;
    private Date lastChange = DateTools.minusWorkDays(5);
    private Boolean taifunExport = null;
    @ReferencedEntityId("geoId")
    private GeoIdBuilder geoIdBuilder = null;

    @Override
    protected void initialize() {
        rewire();
        // Nicht in rewire() wegen Historisierung
        rangierungBuilder.withEndstelleBuilder(this);
    }

    @Override
    protected void beforeBuild() {
        if ((rangierungAdditionalBuilder != null) && (rangierungAdditionalBuilder.getEndstelleBuilder() == null)) {
            rangierungAdditionalBuilder.withEndstelleBuilder(this);
        }
    }

    @Override
    protected void afterBuild(Endstelle endstelle) {
        // Zirkulaere Referenz mit Historisierungs-Abhaengigkeit. Phew!
        if ((rangierungBuilder != null) && !rangierungBuilder.isBuilding()) {
            if (rangierungBuilder.getEndstelleBuilder() == this) {
                rangierungBuilder.get().setEsId(endstelle.getId());
            }
            endstelle.setRangierId(rangierungBuilder.get().getId());
        }
        if ((rangierungAdditionalBuilder != null) && (rangierungAdditionalBuilder.getEndstelleBuilder() == this)) {
            rangierungAdditionalBuilder.get().setEsId(endstelle.getId());
        }
    }


    public AuftragTechnik2EndstelleBuilder getEndstelleGruppeBuilder() {
        return endstelleGruppeBuilder;
    }

    public RangierungBuilder getRangierungBuilder() {
        return rangierungBuilder;
    }

    public RangierungBuilder getRangierungAdditionalBuilder() {
        return rangierungAdditionalBuilder;
    }


    /**
     * Erneuert die Referenzen der referenzierten Objekte auf dieses Objekt
     */
    private void rewire() {
        endstelleGruppeBuilder.withEndstelleBuilder(this);
        if (rangierungBuilder != null) {
            rangierungBuilder.withHvtStandortBuilder(hvtStandortBuilder);
        }
    }


    public EndstelleBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        rewire();
        return this;
    }

    public EndstelleBuilder withAuftragTechnikBuilder(AuftragTechnikBuilder auftragTechnikBuilder) {
        this.withEndstelleGruppeBuilder(auftragTechnikBuilder.getAuftragTechnik2EndstelleBuilder());
        return this;
    }

    public EndstelleBuilder withEndstelleGruppeBuilder(AuftragTechnik2EndstelleBuilder endstelleGruppeBuilder) {
        this.endstelleGruppeBuilder = endstelleGruppeBuilder;
        rewire();
        return this;
    }

    public EndstelleBuilder withEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
        return this;
    }

    public EndstelleBuilder withRangierungBuilder(RangierungBuilder rangierungBuilder) {
        this.rangierungBuilder = rangierungBuilder;
        // Rangierung koennte schon zu einer anderen Endstelle gehoeren. Stichwort: Historisierung!
        if ((rangierungBuilder != null) && (rangierungBuilder.getEndstelleBuilder() == null)) {
            rangierungBuilder.withEndstelleBuilder(this);
        }
        return this;
    }

    public EndstelleBuilder withRangierungAdditionalBuilder(RangierungBuilder rangierungAdditionalBuilder) {
        this.rangierungAdditionalBuilder = rangierungAdditionalBuilder;
        if ((rangierungAdditionalBuilder != null) && (rangierungAdditionalBuilder.getEndstelleBuilder() == null)) {
            rangierungAdditionalBuilder.withEndstelleBuilder(this);
        }
        return this;
    }

    public EndstelleBuilder withCb2EsBuilder(Carrierbestellung2EndstelleBuilder cb2EsBuilder) {
        this.cb2EsBuilder = cb2EsBuilder;
        return this;
    }

    public EndstelleBuilder withOrt(String ort) {
        this.ort = ort;
        return this;
    }

    public EndstelleBuilder withPlz(String plz) {
        this.plz = plz;
        return this;
    }

    public EndstelleBuilder withAddressBuilder(CCAddressBuilder addressBuilder) {
        this.addressBuilder = addressBuilder;
        return this;
    }

    public String getEndstelle() {
        return endstelle;
    }

    public EndstelleBuilder withEndstelle(String endstelle) {
        this.endstelle = endstelle;
        return this;
    }

    public EndstelleBuilder withoutRangierung() {
        this.rangierungBuilder = null;
        this.rangierungAdditionalBuilder = null;
        return this;
    }

    public EndstelleBuilder withGeoIdBuilder(GeoIdBuilder geoIdBuilder) {
        this.geoIdBuilder = geoIdBuilder;
        return this;
    }

    public EndstelleBuilder withAnschlussart(Long anschlussart) {
        this.anschlussart = anschlussart;
        return this;
    }
}
