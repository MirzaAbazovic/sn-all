/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 16:52:39
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;

/**
 *
 */
public class Auftrag2EkpFrameContractEkpFrameContractMatcher extends TypeSafeMatcher<Auftrag2EkpFrameContract> {

    public static Auftrag2EkpFrameContractEkpFrameContractMatcher hasEkpFrameContract(EkpFrameContract ekpFrameContract) {
        return new Auftrag2EkpFrameContractEkpFrameContractMatcher(ekpFrameContract);
    }

    private EkpFrameContract ekpFrameContract;

    public Auftrag2EkpFrameContractEkpFrameContractMatcher(EkpFrameContract ekpFrameContract) {
        this.ekpFrameContract = ekpFrameContract;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ekpFrameContract ").appendValue(ekpFrameContract);
    }

    @Override
    protected boolean matchesSafely(Auftrag2EkpFrameContract item) {
        return ekpFrameContract.equals(item.getEkpFrameContract());
    }

}


