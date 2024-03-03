package de.augustakom.hurrican.model.cc.sip;

import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;

/**
 * SipSbcIpSetBuilder
 */
public class SipSbcIpSetBuilder extends AbstractCCIDModelBuilder<SipSbcIpSetBuilder, SipSbcIpSet>
        implements IServiceObject {

    private Date gueltigAb;
    private List<IPAddress> sbcIps = Lists.newArrayList();

    @Override
    protected void beforeBuild() {
        if (gueltigAb == null) {
            gueltigAb = new Date();
        }
    }

    public SipSbcIpSetBuilder withGueltigAb(Date gueltigAb) {
        this.gueltigAb = gueltigAb;
        return this;
    }

    public SipSbcIpSetBuilder withSbcIps(List<IPAddress> sbcIps) {
        this.sbcIps = sbcIps;
        return this;
    }
}
