/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 18.02.2010 11:09:53
  */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.ReferencedEntityId;


/**
 *
 */
@SuppressWarnings("unused")
public class RangierungsAuftragBuilder extends AbstractCCIDModelBuilder<RangierungsAuftragBuilder, RangierungsAuftrag> {
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Integer anzahlPorts;
    private Long physiktypParent;
    private String auftragVon;
    private Date auftragAm;
    private Date ausgefuehrtAm;

    public RangierungsAuftragBuilder withHvtStandortBuilder(final HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public RangierungsAuftragBuilder withAnzahlPorts(final int anzahlPorts) {
        this.anzahlPorts = anzahlPorts;
        return this;
    }

    public RangierungsAuftragBuilder withPhysiktypParent(final Long physiktypParent) {
        this.physiktypParent = physiktypParent;
        return this;
    }

    public RangierungsAuftragBuilder withAuftragVon(final String auftragVon) {
        this.auftragVon = auftragVon;
        return this;
    }

    public RangierungsAuftragBuilder withAuftragAm(final Date auftragAm) {
        this.auftragAm = auftragAm;
        return this;
    }

    public RangierungsAuftragBuilder withAusgefuehrtAm(final Date ausgefuehrtAm) {
        this.ausgefuehrtAm = ausgefuehrtAm;
        return this;
    }
}
