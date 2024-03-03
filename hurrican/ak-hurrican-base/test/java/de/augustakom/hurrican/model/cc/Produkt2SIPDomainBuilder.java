/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2011 17:06:02
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class Produkt2SIPDomainBuilder extends AbstractCCIDModelBuilder<Produkt2SIPDomainBuilder, Produkt2SIPDomain> implements IServiceObject {

    @ReferencedEntityId("prodId")
    private ProduktBuilder produktBuilder;
    private Long prodId;
    private HWSwitch hwSwitch;
    private Reference sipDomainRef;
    private Boolean defaultDomain;

    public Produkt2SIPDomainBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public Produkt2SIPDomainBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public Produkt2SIPDomainBuilder withSIPDomainRef(Reference sipDomainRef) {
        this.sipDomainRef = sipDomainRef;
        return this;
    }

    public Produkt2SIPDomainBuilder withDefaultDomain(Boolean defaultDomain) {
        this.defaultDomain = defaultDomain;
        return this;
    }

}
