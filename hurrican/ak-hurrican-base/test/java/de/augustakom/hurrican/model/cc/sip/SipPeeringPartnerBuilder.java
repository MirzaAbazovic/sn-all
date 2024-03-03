package de.augustakom.hurrican.model.cc.sip;

import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

/**
 * SipPeeringPartnerBuilder
 */
public class SipPeeringPartnerBuilder extends AbstractCCIDModelBuilder<SipPeeringPartnerBuilder, SipPeeringPartner>
        implements IServiceObject {

    private String name;
    private Boolean isActive;
    private List<SipSbcIpSet> sbcIpSets = Lists.newArrayList();

    @Override
    protected void beforeBuild() {
        if (name == null) {
            name = randomString(10);
        }
        if (isActive == null) {
            isActive = Boolean.TRUE;
        }
    }

    public SipPeeringPartnerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SipPeeringPartnerBuilder withIsActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public SipPeeringPartnerBuilder withSbcIpSets(List<SipSbcIpSet> sbcIpSets) {
        this.sbcIpSets = sbcIpSets;
        return this;
    }
}
