/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.2012 08:09:32
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.dao.cc.DSLAMProfileDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

@Test(groups = UNIT)
public class DSLAMServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private DSLAMServiceImpl cut;

    @SuppressWarnings("unused")
    @Mock
    private DSLAMProfileDAO dslamProfileDao;

    @BeforeMethod
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void modifyDslamProfiles4Auftrag() throws FindException, StoreException {
        Date pastStart = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date pastEnd = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate toModify = LocalDate.now().plusDays(10);
        LocalDate modifiedDate = LocalDate.now().plusDays(20);

        // @formatter:off
        Auftrag auftrag = new AuftragBuilder().withRandomId().setPersist(false).build();
        Auftrag2DSLAMProfile notToChangeBecauseInPast = new Auftrag2DSLAMProfileBuilder()
            .withRandomId()
            .withGueltigVon(pastStart)
            .withGueltigBis(pastEnd)
            .setPersist(false)
            .build();  // gueltigVon=heute-1Jahr; gueltigBis=heute-1Monat
        Auftrag2DSLAMProfile changeEnd1 = new Auftrag2DSLAMProfileBuilder()
            .withRandomId()
            .withGueltigVon(Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .withGueltigBis(Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .setPersist(false)
            .build(); // gueltigVon=heute-1Jahr; gueltigBis=gueltigVon
        Auftrag2DSLAMProfile changeEnd2 = new Auftrag2DSLAMProfileBuilder()
            .withRandomId()
            .withGueltigVon(pastEnd)
            .withGueltigBis(Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .setPersist(false)
            .build(); // gueltigVon=heute-1Monat; gueltigBis=heute+10Tage
        Auftrag2DSLAMProfile changeStart = new Auftrag2DSLAMProfileBuilder()
            .withRandomId()
            .withGueltigVon(Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .withGueltigBis(DateTools.getHurricanEndDate())
            .setPersist(false)
            .build();  // gueltigVon=heute+10Tage; gueltigBis=01.01.2200
        // @formatter:on

        doReturn(Arrays.asList(notToChangeBecauseInPast, changeStart, changeEnd1, changeEnd2)).when(cut)
                .findAuftrag2DSLAMProfiles(auftrag.getAuftragId());

        List<Auftrag2DSLAMProfile> result = cut.modifyDslamProfiles4Auftrag(auftrag.getAuftragId(), toModify, modifiedDate, null);
        assertNotEmpty(result);
        assertEquals(result.size(), 3);
        for (Auftrag2DSLAMProfile profile : result) {
            if (NumberTools.equal(profile.getId(), changeStart.getId())) {
                assertThat(profile.getGueltigVon(), equalTo(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
                assertThat(profile.getGueltigBis(), equalTo(DateTools.getHurricanEndDate()));
            }
            else if (NumberTools.equal(profile.getId(), changeEnd1.getId())) {
                assertThat(profile.getGueltigVon(), equalTo(Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant())));
                assertThat(profile.getGueltigBis(), equalTo(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            }
            else if (NumberTools.equal(profile.getId(), changeEnd2.getId())) {
                assertThat(profile.getGueltigVon(), equalTo(changeEnd2.getGueltigVon()));
                assertThat(profile.getGueltigBis(), equalTo(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
            }
        }
    }

}


