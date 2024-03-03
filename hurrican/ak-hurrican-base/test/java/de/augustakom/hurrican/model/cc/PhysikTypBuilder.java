/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 08:11:49
 */
package de.augustakom.hurrican.model.cc;


/**
 * Entity Builder for PhysikTyp objects
 *
 *
 */
@SuppressWarnings("unused")
public class PhysikTypBuilder extends AbstractCCIDModelBuilder<PhysikTypBuilder, PhysikTyp> {

    {id = getLongId();}

    private String name = "TestPhysik-Phone-" + randomString(32);
    private String beschreibung = null;
    private HVTTechnikBuilder hvtTechnikBuilder = null;
    private Bandwidth maxBandwidth = null;
    private String hwSchnittstelle = "AB";
    private Long ptGroup = PhysikTyp.PT_GROUP_PHONE;
    private String cpsTransferMethod = null;


    public PhysikTypBuilder withHvtTechnikBuilder(HVTTechnikBuilder hvtTechnikBuilder) {
        this.hvtTechnikBuilder = hvtTechnikBuilder;
        return this;
    }

    public PhysikTypBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PhysikTypBuilder withNameRandomSuffix(String name) {
        this.name = name + "-" + randomString(49 - name.length());
        return this;
    }

    public PhysikTypBuilder withCpsTransferMethod(String cpsTransferMethod) {
        this.cpsTransferMethod = cpsTransferMethod;
        return this;
    }

    public PhysikTypBuilder withHwSchnittstelle(String hwSchnittstelle) {
        this.hwSchnittstelle = hwSchnittstelle;
        return this;
    }

    public PhysikTypBuilder withPtGroup(Long physikTypGroup) {
        this.ptGroup = physikTypGroup;
        return this;
    }
}


