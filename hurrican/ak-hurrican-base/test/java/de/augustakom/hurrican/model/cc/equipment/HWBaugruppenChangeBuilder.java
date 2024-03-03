/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 13:57:16
 */
package de.augustakom.hurrican.model.cc.equipment;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;


/**
 * EntityBuilder fuer Objekte des Typs {@link HWBaugruppenChange}
 */
@SuppressWarnings("unused")
public class HWBaugruppenChangeBuilder extends EntityBuilder<HWBaugruppenChangeBuilder, HWBaugruppenChange> {

    private Date plannedDate = new Date();
    private HVTStandort hvtStandort;
    private Reference changeType;
    private Reference changeState;
    private String plannedFrom = randomString(25);
    private Date executedAt;
    private String executedFrom;
    private Date cancelledAt;
    private String cancelledFrom;
    private Date closedAt;
    private String closedFrom;
    private Set<HWBaugruppenChangePort2Port> port2Port;
    private Set<HWBaugruppenChangeBgType> hwBgChangeBgType = new HashSet<HWBaugruppenChangeBgType>();
    private Set<HWBaugruppenChangeCard> hwBgChangeCard = new HashSet<HWBaugruppenChangeCard>();
    private Set<HWBaugruppenChangeDslamSplit> hwBgChangeDslamSplit = new HashSet<HWBaugruppenChangeDslamSplit>();
    private Set<HWBaugruppenChangeDlu> hwBgChangeDlu = new HashSet<HWBaugruppenChangeDlu>();
    private PhysikTypBuilder physikTypNewBuilder;


    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        if (changeState == null) {
            changeState = getBuilder(ReferenceBuilder.class)
                    .withId(randomLong(500000, 999999))
                    .withType(Reference.REF_TYPE_PORT_SCHWENK_STATUS)
                    .build();
        }
    }

    public HWBaugruppenChangeBuilder withHvtStandort(HVTStandort hvtStandort) {
        this.hvtStandort = hvtStandort;
        return this;
    }

    public HWBaugruppenChangeBuilder withChangeTypeReference(Reference changeType) {
        this.changeType = changeType;
        return this;
    }

    public HWBaugruppenChangeBuilder withChangeStateReference(Reference changeState) {
        this.changeState = changeState;
        return this;
    }

    public HWBaugruppenChangeBuilder withHWBaugruppenChangePort2Port(HWBaugruppenChangePort2Port port2PortToAdd) {
        if (port2Port == null) {
            port2Port = new HashSet<HWBaugruppenChangePort2Port>();
        }
        port2Port.add(port2PortToAdd);
        return this;
    }

    public HWBaugruppenChangeBuilder withHWBaugruppenChangeBgType(HWBaugruppenChangeBgType hwBaugruppenChangeBgTypeToAdd) {
        hwBgChangeBgType.add(hwBaugruppenChangeBgTypeToAdd);
        return this;
    }

    public HWBaugruppenChangeBuilder withHWBaugruppenChangeCard(HWBaugruppenChangeCard hwBaugruppenChangeCardToAdd) {
        hwBgChangeCard.add(hwBaugruppenChangeCardToAdd);
        return this;
    }

    public HWBaugruppenChangeBuilder withHWBaugruppenChangeDslamSplit(HWBaugruppenChangeDslamSplit hwBaugruppenChangeDslamSplitToAdd) {
        hwBgChangeDslamSplit.add(hwBaugruppenChangeDslamSplitToAdd);
        return this;
    }

    public HWBaugruppenChangeBuilder withHWBaugruppenChangeDlu(HWBaugruppenChangeDlu hwBgChangeDluToAdd) {
        hwBgChangeDlu.add(hwBgChangeDluToAdd);
        return this;
    }

    public HWBaugruppenChangeBuilder withCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
        return this;
    }

    public HWBaugruppenChangeBuilder withClosedAt(Date closedAt) {
        this.closedAt = closedAt;
        return this;
    }

    public HWBaugruppenChangeBuilder withPhysikTypNewBuilder(PhysikTypBuilder physikTypBuilder) {
        this.physikTypNewBuilder = physikTypBuilder;
        return this;
    }

}


