/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 11:33:36
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import java.time.*;
import java.util.*;
import javax.validation.constraints.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.mnet.common.tools.DateConverterUtils;

public class DslamProfileValidFromMatcher extends TypeSafeMatcher<Auftrag2DSLAMProfile> {

    private final LocalDate validFrom;

    public static DslamProfileValidFromMatcher dslamProfileValidFrom(LocalDate validFrom) {
        return new DslamProfileValidFromMatcher(validFrom);
    }

    public DslamProfileValidFromMatcher(@NotNull LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("profile valid from ").appendValue(validFrom);
    }

    @Override
    protected void describeMismatchSafely(Auftrag2DSLAMProfile item, Description mismatchDescription) {
        Date gueltigVon = item.getGueltigVon();
        LocalDate gueltigVonLocalDate = (gueltigVon == null) ? null : DateConverterUtils.asLocalDate(gueltigVon);
        mismatchDescription.appendText("valid from was ").appendValue(gueltigVonLocalDate);
    }

    @Override
    protected boolean matchesSafely(Auftrag2DSLAMProfile item) {
        return Date.from(validFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()).equals(item.getGueltigVon());
    }

}

