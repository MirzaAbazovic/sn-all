/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.14 07:35
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.QoSProfilService;

/**
 *
 */
public class QoSProfilServiceImplTest {

    @InjectMocks
    QoSProfilServiceImpl cut;

    @Mock
    CCLeistungsService ccLeistungsService;
    @Mock
    DSLAMService dslamService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testGetQoSProfilDownStreamAndValidDate4Auftrag() throws Exception {
        final long auftragId = 1234L;
        final Date validDate = new Date();

        final List<Auftrag2TechLeistung> auftrag2TechLeistungs = ImmutableList.of(
                new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        .build(),
                new Auftrag2TechLeistungBuilder().withRandomId().withTechLeistungId(460L)
                        .withAktivVon(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())).build()
        );

        final TechLeistung qosProfilTechLs = new TechLeistungBuilder().withRandomId().withLongValue(50L).build();

        when(ccLeistungsService.findAuftrag2TechLeistungen(auftragId,
                new String[] { TechLeistung.TYP_SIPTRUNK_QOS_PROFILE }, validDate, true)).thenReturn(
                auftrag2TechLeistungs);
        when(ccLeistungsService.findTechLeistung(auftrag2TechLeistungs.get(1).getTechLeistungId()))
                .thenReturn(qosProfilTechLs);

        when(dslamService.findDSLAMProfile4AuftragNoEx(auftragId, auftrag2TechLeistungs.get(1).getAktivVon(), false))
                .thenReturn(new DSLAMProfileBuilder().withBandwidth(Bandwidth.create(5000)).build());

        QoSProfilService.QosProfileWithValidFromAndDownstream result =
                cut.getQoSProfilDownStreamAndValidDate4Auftrag(auftragId, validDate);

        assertThat(result.qosProfile, equalTo(qosProfilTechLs));
        assertThat(result.downstream, equalTo(2500L));
        assertThat(result.validFrom, equalTo(auftrag2TechLeistungs.get(1).getAktivVon()));
    }

    @Test
    public void testGetDownStream4AuftragWithConnectLeitungAsFallback() throws FindException {
        final long auftragId = 1234L;
        final Date validDate = new Date();
        final TechLeistung techLeistung = new TechLeistungBuilder().withRandomId().withLongValue(1234L).build();

        when(ccLeistungsService.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_CONNECT_LEITUNG, validDate))
                .thenReturn(techLeistung);

        final long result = cut.getDownStream4Auftrag(auftragId, validDate);
        assertThat(result, equalTo(techLeistung.getLongValue()));
    }
}
