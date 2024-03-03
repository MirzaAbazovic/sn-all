/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.math.*;
import java.util.*;

import de.augustakom.common.tools.lang.DateTools;

/**
 *
 */
public class SiteTestBuilder extends SiteBuilder {

    public SiteTestBuilder() {
        withType("Endstelle_B");
        withCluster("MUC-14");
        withHvt("MUC-MANPL-010");
        withDownstream(BigInteger.valueOf(50000));
        withUpstream(BigInteger.valueOf(50000));
        withDevice(new DeviceTestBuilder().build());
        withLocation(new LocationTestBuilder().build());
        addWiringData(new WiringDataTestBuilder().build());

        withAreaDialingCode("089");
        withAqs("aqs");
        withLastValidDSLAMProfile("dslamProfile");
        withTalProvisioningDate(DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR));
        withCustomerOnSite(true);
        withAsb("asb");
        withKvzNumber("kvz number");
        withLbz("lbz");
        withLineId("lineId");
        withLineType("line type");
        withLl("ll");
        withPortType("port type");
        withSwitch("switch");
        withVtrNr("vtr nr");
    }
}