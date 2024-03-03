/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 16:49:05
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import java.time.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;

/**
 *
 */
public class Auftrag2EkpFrameContractAssignedFromMatcher extends TypeSafeMatcher<Auftrag2EkpFrameContract> {

    public static Auftrag2EkpFrameContractAssignedFromMatcher isAssignedFrom(LocalDate assignedFrom) {
        return new Auftrag2EkpFrameContractAssignedFromMatcher(assignedFrom);
    }

    private LocalDate assignedFrom;

    public Auftrag2EkpFrameContractAssignedFromMatcher(LocalDate assignedFrom) {
        this.assignedFrom = assignedFrom;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("assignedFrom ").appendValue(assignedFrom);
    }

    @Override
    protected boolean matchesSafely(Auftrag2EkpFrameContract item) {
        return assignedFrom.isEqual(item.getAssignedFrom());
    }

}


