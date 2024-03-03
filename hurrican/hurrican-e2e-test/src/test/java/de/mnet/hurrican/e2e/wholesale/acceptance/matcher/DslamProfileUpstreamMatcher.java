/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.04.2012 08:31:45
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.DSLAMProfile;

/**
 *
 */
public class DslamProfileUpstreamMatcher extends TypeSafeMatcher<DSLAMProfile> {

    private final int upstream;

    public DslamProfileUpstreamMatcher(int upstream) {
        this.upstream = upstream;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("upstream ").appendValue(upstream);
    }

    @Override
    protected void describeMismatchSafely(DSLAMProfile item, Description mismatchDescription) {
        mismatchDescription.appendText("upstream was ").appendValue(item.getBandwidth().getUpstream());
    }

    @Override
    protected boolean matchesSafely(DSLAMProfile item) {
        return Integer.valueOf(item.getBandwidth().getUpstream()).equals(upstream);
    }

}


