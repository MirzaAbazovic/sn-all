package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;

@SuppressWarnings({ "unused", "FieldCanBeLocal" })
public class EGType2SIPDomainBuilder extends AbstractCCIDModelBuilder<EGType2SIPDomainBuilder, EGType2SIPDomain>
        implements IServiceObject {

    @ReferencedEntityId("egType")
    private EGTypeBuilder egTypeBuilder;
    private EGType egType;
    private HWSwitch hwSwitch;
    private Reference sipDomainRef;

    public EGType2SIPDomainBuilder withEGTypeBuilder(EGTypeBuilder egTypeBuilder) {
        this.egTypeBuilder = egTypeBuilder;
        return this;
    }

    public EGType2SIPDomainBuilder withHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
        return this;
    }

    public EGType2SIPDomainBuilder withSIPDomainRef(Reference sipDomainRef) {
        this.sipDomainRef = sipDomainRef;
        return this;
    }
}
