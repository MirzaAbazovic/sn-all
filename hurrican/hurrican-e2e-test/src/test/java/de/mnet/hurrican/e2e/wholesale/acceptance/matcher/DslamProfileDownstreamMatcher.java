/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 11:30:34
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.DSLAMProfile;

public class DslamProfileDownstreamMatcher extends TypeSafeMatcher<DSLAMProfile> {
    private final int downstream;

    public DslamProfileDownstreamMatcher(int downstream) {
        this.downstream = downstream;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("downstream ").appendValue(downstream);
    }

    @Override
    protected void describeMismatchSafely(DSLAMProfile item, Description mismatchDescription) {
        mismatchDescription.appendText("downstream was ").appendValue(item.getBandwidth().getDownstream());
    }

    @Override
    protected boolean matchesSafely(DSLAMProfile item) {
        return Integer.valueOf(item.getBandwidth().getDownstream()).equals(downstream);
    }

}

