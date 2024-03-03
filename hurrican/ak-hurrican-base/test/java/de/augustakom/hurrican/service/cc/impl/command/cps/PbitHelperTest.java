/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.13 09:32
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.Iterables;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 *
 */
@Test(groups = { BaseTest.UNIT })
public class PbitHelperTest {

    @InjectMocks
    PbitHelper cut;

    @Mock
    CCLeistungsService ccLeistungsService;

    @BeforeMethod
    public void setUp() {
        cut = new PbitHelper();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddPbit() throws Exception {
        final TechLeistung techLs = new TechLeistungBuilder().withLongValue(10L).build();
        final CPSDSLData dslData = testAddPbit(techLs);
        final CPSPBITData pbit = Iterables.getOnlyElement(dslData.getPbits());
        assertThat(pbit.service, equalTo("VOIP"));
        assertThat(pbit.limit, equalTo(100L));

    }

    @Test(expectedExceptions = { FindException.class })
    public void testAddPbitWhenNoPrioIsConfigured() throws Exception {
        final TechLeistung techLs = new TechLeistungBuilder().build();
        testAddPbit(techLs);
    }

    private CPSDSLData testAddPbit(final TechLeistung techLs) throws Exception {
        final CPSDSLData dslData = new CPSDSLData();
        final DSLAMProfile dslamProfile = new DSLAMProfileBuilder().withBandwidth(1000).build();
        final long auftragId = 815L;
        final Date execTime = new Date();

        when(ccLeistungsService.findTechLeistung4Auftrag(auftragId,
                TechLeistung.TYP_SIPTRUNK_QOS_PROFILE, execTime)).thenReturn(techLs);

        cut.addVoipPbitIfNecessary(dslData, dslamProfile, auftragId, execTime);

        return dslData;
    }
}
