/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 11:00:56
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

/**
 * EntityBuilder for HWRack objects
 */
@SuppressWarnings("unused")
public abstract class HWRackBuilder<BUILDER extends AbstractCCIDModelBuilder<BUILDER, ENTITY>, ENTITY extends HWRack>
        extends AbstractCCIDModelBuilder<BUILDER, ENTITY> {

    private String rackTyp = null;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    @ReferencedEntityId("hwProducer")
    private HVTTechnikBuilder hwProducerBuilder;

    private String geraeteBez = randomString(20);
    private String managementBez;

    private Date gueltigVon = new Date();
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public HVTStandortBuilder getHvtStandortBuilder() {
        return hvtStandortBuilder;
    }

    public HVTTechnikBuilder getHwProducerBuilder() {
        return hwProducerBuilder;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withHwProducerBuilder(HVTTechnikBuilder hwProducerBuilder) {
        this.hwProducerBuilder = hwProducerBuilder;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withRackTyp(String rackTyp) {
        this.rackTyp = rackTyp;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withManagementBez(String managementBez) {
        this.managementBez = managementBez;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return (BUILDER) this;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return (BUILDER) this;
    }

}
