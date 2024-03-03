/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 13:55:46
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * Builder fuer EG-Type-Objekte.
 */
@SuppressWarnings("unused")
public class EGTypeBuilder extends AbstractCCIDModelBuilder<EGTypeBuilder, EGType> implements IServiceObject {

    private String hersteller = "Cisco";
    private String modell = "Cisco Modell";
    private List<EG> egGruppen = new ArrayList<>();
    private List<HWSwitch> orderedCertifiedSwitches = new ArrayList<>();

    public EGTypeBuilder withHersteller(String hersteller) {
        this.hersteller = hersteller;
        return this;
    }

    public EGTypeBuilder withModell(String modell) {
        this.modell = modell;
        return this;
    }

    public EGTypeBuilder withEgGruppen(List<EG> egGruppen) {
        this.egGruppen = egGruppen;
        return this;
    }

    public EGTypeBuilder withOrderedCertifiedSwitches(List<HWSwitch> orderedCertifiedSwitches) {
        this.orderedCertifiedSwitches = orderedCertifiedSwitches;
        return this;
    }

}
