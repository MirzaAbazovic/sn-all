/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2010 16:20:53
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;


/**
 * EntityBuilder fuer Objekte des Typs {@link HVTStandortTechType}.
 *
 *
 */
@SuppressWarnings("unused")
public class HVTStandortTechTypeBuilder extends AbstractCCIDModelBuilder<HVTStandortTechTypeBuilder, HVTStandortTechType>
        implements IServiceObject {

    private Long hvtIdStandort = null;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Reference technologyTypeReference = null;
    private Date availableFrom = null;
    private Date availableTo = null;
    private String userW = null;
    private Date dateW = null;

    public HVTStandortTechTypeBuilder withHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
        return this;
    }

    public HVTStandortTechTypeBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public HVTStandortBuilder getHVTStandortBuilder() {
        return hvtStandortBuilder;
    }

    public HVTStandortTechTypeBuilder withTechnologyTypeReference(Reference technologyTypeReference) {
        this.technologyTypeReference = technologyTypeReference;
        return this;
    }

}
