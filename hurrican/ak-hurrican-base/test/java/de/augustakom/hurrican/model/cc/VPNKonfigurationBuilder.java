/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2012 11:31:10
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;

/**
 *
 */
@SuppressWarnings("unused")
public class VPNKonfigurationBuilder extends AbstractCCIDModelBuilder<VPNKonfigurationBuilder, VPNKonfiguration> {

    private Boolean kanalbuendelung;
    private Short anzahlKanaele;
    private Boolean dialOut;
    private AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
    private Long physAuftragId;
    private String vplsId;

    private Date gueltigVon = new Date(0);
    private Date gueltigBis = DateTools.getHurricanEndDate();

    public VPNKonfigurationBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public VPNKonfigurationBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public VPNKonfigurationBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public VPNKonfigurationBuilder withVplsId(String vplsId) {
        this.vplsId = vplsId;
        return this;
    }
}


