/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.mnet.wbci.model.builder;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
public class AbbruchmeldungBuilderTest {
    @Test
    public void testWithBegruendung() throws Exception {

        Abbruchmeldung abbm = new AbbruchmeldungBuilder().build();
        Assert.assertNull(abbm.getBegruendung());
        Assert.assertFalse(isMeldungsCodeSonstIncluded(abbm.getMeldungsPositionen()));

        abbm = new AbbruchmeldungBuilder().withBegruendung("Begründung").build();
        Assert.assertEquals(abbm.getBegruendung(), "Begründung");
        Assert.assertTrue(isMeldungsCodeSonstIncluded(abbm.getMeldungsPositionen()));

        abbm = new AbbruchmeldungBuilder().withBegruendung("Begründung").withBegruendung(null).build();
        Assert.assertNull(abbm.getBegruendung());
        Assert.assertFalse(isMeldungsCodeSonstIncluded(abbm.getMeldungsPositionen()));

        abbm = new AbbruchmeldungBuilder()
                .addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder().withMeldungsCode(MeldungsCode.TV_ABG).build())
                .withBegruendung("Begründung").build();
        Assert.assertEquals(abbm.getBegruendung(), "Begründung");
        Assert.assertFalse(isMeldungsCodeSonstIncluded(abbm.getMeldungsPositionen()));

        abbm = new AbbruchmeldungBuilder()
                .addMeldungPosition(new MeldungPositionAbbruchmeldungBuilder().withMeldungsCode(MeldungsCode.STORNO_ABG).build())
                .withBegruendung("Begründung").build();
        Assert.assertEquals(abbm.getBegruendung(), "Begründung");
        Assert.assertFalse(isMeldungsCodeSonstIncluded(abbm.getMeldungsPositionen()));
    }

    private <M extends MeldungPosition> boolean isMeldungsCodeSonstIncluded(Set<M> meldungPositionSet) {
        for (M pos : meldungPositionSet) {
            if (MeldungsCode.SONST.equals(pos.getMeldungsCode())) {
                return true;
            }
        }
        return false;
    }
}
