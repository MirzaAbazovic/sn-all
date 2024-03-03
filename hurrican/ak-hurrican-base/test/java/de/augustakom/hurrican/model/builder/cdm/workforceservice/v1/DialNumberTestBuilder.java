/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.time.*;
import com.google.common.collect.Lists;

/**
 *
 */
public class DialNumberTestBuilder extends DialNumberBuilder {

    public DialNumberTestBuilder() {
        withAreaDialingCode("089");
        withMain(Boolean.FALSE);
        withNumber("12234311");
        withPortMode("PORTIERUNG_K");
        withValidFrom(LocalDate.of(2014, 6, 2));
        withValidTo(LocalDate.of(9999, 11, 30));
        // withNumberRange(new NumberRangeTestBuilder().build());
        withVoIPLogin(new VoipLoginTestBuilder().build());
        addRufnummerplans(Lists.newArrayList(new DialNumberPlanTestBuilder().build()));
    }
}
