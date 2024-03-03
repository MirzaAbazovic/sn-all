/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 09:58:40
 */
package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class DSLAMProfileTest extends BaseTest {

    public void testDslamProfileComparator() {
        DSLAMProfile prof1 = new DSLAMProfileBuilder().withRandomId().withBandwidth(3552, 256).setPersist(false).build();
        DSLAMProfile prof2 = new DSLAMProfileBuilder().withRandomId().withBandwidth(1152, 256).setPersist(false).build();
        DSLAMProfile prof3 = new DSLAMProfileBuilder().withRandomId().withBandwidth(1152, 128).setPersist(false).build();

        List<DSLAMProfile> list = Arrays.asList(prof1, prof2, prof3);
        Collections.sort(list, DSLAMProfile.DSLAMPROFILE_COMPARATOR);
        assertEquals(list.get(0).getId(), prof3.getId());
        assertEquals(list.get(1).getId(), prof2.getId());
        assertEquals(list.get(2).getId(), prof1.getId());
    }

    @DataProvider
    public Object[][] profilesForFilterBestFitting() {
        // @formatter:off
        DSLAMProfile profileWithBaugruppenTyp =new DSLAMProfileBuilder().withBandwidth(7168, 640).withBaugruppenTypId(1L).setPersist(false).build();
        DSLAMProfile profileNoBaugruppenTyp = new DSLAMProfileBuilder().withBandwidth(7168, 640).setPersist(false).build();
        return new Object[][] {
            // Profile mit BaugruppentypID zuerst
            { Arrays.asList(
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    profileWithBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).setPersist(false).build(),
                    profileNoBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).setPersist(false).build()
                ),
                profileWithBaugruppenTyp},
            // Profile ohne BaugruppentypID zuerst
            { Arrays.asList(
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).setPersist(false).build(),
                    profileNoBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    profileWithBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).withBaugruppenTypId(1L).setPersist(false).build()
                ),
                profileNoBaugruppenTyp},
            // ohne Sortierung
            { Arrays.asList(
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 641).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).setPersist(false).build(),
                    profileNoBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 640).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(6752, 640).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(7168, 576).withBaugruppenTypId(1L).setPersist(false).build(),
                    profileWithBaugruppenTyp,
                    new DSLAMProfileBuilder().withBandwidth(7168, 641).withBaugruppenTypId(1L).setPersist(false).build(),
                    new DSLAMProfileBuilder().withBandwidth(8000, 576).withBaugruppenTypId(1L).setPersist(false).build()
                ),
                profileNoBaugruppenTyp},
        };
        // @formatter:on
    }

}


