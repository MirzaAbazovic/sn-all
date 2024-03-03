/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.2015
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;

/**
 *
 */
public class Auftrag2PeeringPartnerBuilder extends AbstractCCIDModelBuilder<Auftrag2PeeringPartnerBuilder, Auftrag2PeeringPartner>
        implements IServiceObject {

    private Long auftragId = null;
    private Long peeringPartnerId = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;

    public Auftrag2PeeringPartnerBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public Auftrag2PeeringPartnerBuilder withPeeringPartnerId(Long peeringPartnerId) {
        this.peeringPartnerId = peeringPartnerId;
        return this;
    }

    public Auftrag2PeeringPartnerBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public Auftrag2PeeringPartnerBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

}
