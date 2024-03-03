/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 16:44:26
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import java.time.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;

/**
 *
 */
public class Auftrag2EkpFrameContractAssignedToMatcher extends TypeSafeMatcher<Auftrag2EkpFrameContract> {

    public static Auftrag2EkpFrameContractAssignedToMatcher isAssignedTo(LocalDate assignedTo) {
        return new Auftrag2EkpFrameContractAssignedToMatcher(assignedTo);
    }

    private LocalDate assignedTo;

    public Auftrag2EkpFrameContractAssignedToMatcher(LocalDate assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("assignedTo ").appendValue(assignedTo);
    }

    @Override
    protected boolean matchesSafely(Auftrag2EkpFrameContract item) {
        return assignedTo.isEqual(item.getAssignedTo());
    }

}


