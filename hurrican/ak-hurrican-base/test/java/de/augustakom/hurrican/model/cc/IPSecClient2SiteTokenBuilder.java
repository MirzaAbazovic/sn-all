/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2009 11:34:52
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.tools.lang.DateTools;


/**
 *
 */
@SuppressWarnings("unused")
public class IPSecClient2SiteTokenBuilder extends AbstractCCIDModelBuilder<IPSecClient2SiteTokenBuilder, IPSecClient2SiteToken> {

    @DontCreateBuilder
    private AuftragBuilder auftragBuilder;

    private String serialNumber = "s100002" + getLongId();
    private Integer laufzeitInMonaten = 24;
    private Date lieferdatum = DateTools.createDate(2008, 2, 21);
    private String bemerkung;
    private Date batterieEnde = DateTools.createDate(2012, 2, 21);
    ;
    private String sapOrderId = "S20002123";

    public IPSecClient2SiteTokenBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public IPSecClient2SiteTokenBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

}
