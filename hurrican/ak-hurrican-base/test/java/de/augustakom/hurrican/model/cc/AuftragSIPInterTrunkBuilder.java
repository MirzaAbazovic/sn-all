/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 11:40:04
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * EntityBuilder fuer Objekte des Typs {@link AuftragSIPInterTrunk}.
 */
@SuppressWarnings("unused")
public class AuftragSIPInterTrunkBuilder extends EntityBuilder<AuftragSIPInterTrunkBuilder, AuftragSIPInterTrunk> {

    private AuftragBuilder auftragBuilder;
    private HWSwitch hwSwitch;
    private String trunkGroup;
    private String userW;

    public AuftragSIPInterTrunkBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public AuftragSIPInterTrunkBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public AuftragSIPInterTrunkBuilder withTrunkGroup(String trunkGroup) {
        this.trunkGroup = trunkGroup;
        return this;
    }

}


