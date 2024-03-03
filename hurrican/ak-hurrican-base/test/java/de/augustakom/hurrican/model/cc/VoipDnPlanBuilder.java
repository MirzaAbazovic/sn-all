/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.11.13 11:52
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.common.service.iface.IServiceObject;

/**
 *
 */
public class VoipDnPlanBuilder extends AbstractCCIDModelBuilder<VoipDnPlanBuilder, VoipDnPlan> implements
        IServiceObject {

    private Date gueltigAb = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    private List<VoipDnBlock> voipDnBlocks = Lists.newArrayList();

    private String sipLogin;
    private String sipHauptrufnummer;

    public VoipDnPlanBuilder() {
    }

    public static VoipDnPlanBuilder aVoipDnPlan() {
        return new VoipDnPlanBuilder();
    }

    public VoipDnPlanBuilder withGueltigAb(Date gueltigAb) {
        this.gueltigAb = gueltigAb;
        return this;
    }

    public VoipDnPlanBuilder withVoipDnBlocks(List<VoipDnBlock> voipDnBlocks) {
        this.voipDnBlocks = voipDnBlocks;
        return this;
    }

    public VoipDnPlanBuilder withSipLogin(String sipLogin) {
        this.sipLogin = sipLogin;
        return this;
    }

    public VoipDnPlanBuilder withSipHauptrufnummer(String sipHauptrufnummer) {
        this.sipHauptrufnummer = sipHauptrufnummer;
        return this;
    }

}
