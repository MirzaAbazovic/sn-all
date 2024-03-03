/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 12:01:16
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.tools.lang.DateTools;


/**
 * EntityBuilder for EQCrossConnection objects
 *
 *
 */
@SuppressWarnings("unused")
public class EQCrossConnectionBuilder extends AbstractCCIDModelBuilder<EQCrossConnectionBuilder, EQCrossConnection> {

    private EquipmentBuilder equipmentBuilder;
    @DontCreateBuilder
    private BrasPoolBuilder brasPoolBuilder;
    private Long crossConnectionTypeRefId = EQCrossConnection.REF_ID_XCONN_HSI_XCONN;
    private Integer ntOuter = 212;
    private Integer ntInner = 115;
    private Integer ltOuter = 1;
    private Integer ltInner = 32;
    private Integer brasOuter = 224;
    private Integer brasInner = 111;
    private Date gueltigVon = DateTools.minusWorkDays(5);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private String userW = "UnitTest";
    @ReferencedEntityId("crossConnectionTypeRefId")
    private ReferenceBuilder referenceBuilder;

    public EQCrossConnectionBuilder withEquipmentBuilder(EquipmentBuilder equipmentBuilder) {
        this.equipmentBuilder = equipmentBuilder;
        return this;
    }

    public EQCrossConnectionBuilder withBrasPoolBuilder(BrasPoolBuilder brasPoolBuilder) {
        this.brasPoolBuilder = brasPoolBuilder;
        return this;
    }

    public EQCrossConnectionBuilder withReferenceBuilder(ReferenceBuilder referenceBuilder) {
        this.referenceBuilder = referenceBuilder;
        return this;
    }

    public EQCrossConnectionBuilder withValidFrom(Date validFrom) {
        this.gueltigVon = validFrom;
        return this;
    }

    public EQCrossConnectionBuilder withValidTo(Date validTo) {
        this.gueltigBis = validTo;
        return this;
    }

    public EQCrossConnectionBuilder withCrossConnectionTypeRefId(Long crossConnectionTypeRefId) {
        this.crossConnectionTypeRefId = crossConnectionTypeRefId;
        return this;
    }

    public EQCrossConnectionBuilder withUserW(String userW) {
        this.userW = userW;
        return this;
    }

    public EQCrossConnectionBuilder withLtInner(Integer ltInner) {
        this.ltInner = ltInner;
        return this;
    }

    public EQCrossConnectionBuilder withBrasInner(Integer brasInner) {
        this.brasInner = brasInner;
        return this;
    }

    public EQCrossConnectionBuilder withBrasOuter(Integer brasOuter) {
        this.brasOuter = brasOuter;
        return this;
    }

}
