/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 14:13:26
 */
package de.augustakom.hurrican.service.cc.impl.command.dslamprofilemonitor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * Test fuer die Command-Klasse CheckNeedsDSLAMProfileMonitoringCommand
 */
@Test(groups = { BaseTest.UNIT })
public class CheckNeedsDSLAMProfileMonitoringCommandTest extends BaseTest {

    private final Long ccAuftragId = RandomTools.createLong();

    @Spy
    private CheckNeedsDSLAMProfileMonitoringCommand cut;
    @Mock
    private CCAuftragService asMock;
    @Mock
    private BAService basMock;
    @Mock
    private EndstellenService esMock;
    @Mock
    private HVTService hvtsMock;
    @Mock
    private CCLeistungsService lsMock;

    @BeforeMethod
    void setUp() {
        cut = new CheckNeedsDSLAMProfileMonitoringCommand();
        cut.prepare(CheckNeedsDSLAMProfileMonitoringCommand.CCAUFTRAG_ID, ccAuftragId);
        initMocks(this);
    }

    @DataProvider(name = "checkIsAuftragInRealisierungDP")
    public Object[][] checkIsAuftragInRealisierungDP() {
        // @formatter:off
        return new Object[][] {
                { AuftragStatus.TECHNISCHE_REALISIERUNG, true},
                { AuftragStatus.IN_BETRIEB, false},
                { AuftragStatus.ERFASSUNG, false},
                { AuftragStatus.KUENDIGUNG_TECHN_REAL, false}
            };
        // @formatter:on
    }

    @Test(dataProvider = "checkIsAuftragInRealisierungDP")
    public void checkIsAuftragInRealisierung(Long auftragStatus, boolean expected) throws ServiceNotFoundException,
            FindException {
        //@formatter:off
        AuftragDaten aDaten = new AuftragDatenBuilder().setPersist(false)
                                .withAuftragId(ccAuftragId)
                                .withStatusId(auftragStatus)
                                .build();
        //@formatter:on
        doReturn(asMock).when(cut).getCCService(CCAuftragService.class);
        when(asMock.findAuftragDatenByAuftragIdTx(eq(ccAuftragId))).thenReturn(aDaten);
        assertEquals(cut.checkIsAuftragInRealisierung(ccAuftragId), expected);
        verify(asMock, times(1)).findAuftragDatenByAuftragIdTx(eq(ccAuftragId));
    }

    @DataProvider(name = "checkIsRealDateTodayDP")
    public Object[][] checkIsRealDateTodayDP() {
        final Date today = new Date();
        // @formatter:off
        return new Object[][] {
                { today, BAVerlaufAnlass.NEUSCHALTUNG, true},
                { today, BAVerlaufAnlass.ANSCHLUSSUEBERNAHME, true},
                { today, BAVerlaufAnlass.KUENDIGUNG, false},
                { DateTools.changeDate(today, Calendar.DAY_OF_YEAR, -1), BAVerlaufAnlass.NEUSCHALTUNG, false},
                { DateTools.changeDate(today, Calendar.DAY_OF_YEAR, +1), BAVerlaufAnlass.NEUSCHALTUNG, false},
            };
        // @formatter:on
    }

    @Test(dataProvider = "checkIsRealDateTodayDP")
    public void checkIsRealDateToday(Date realDate, Long verlaufAnlass, boolean expected)
            throws ServiceNotFoundException, FindException {
        //@formatter:off
        Verlauf verlauf = new VerlaufBuilder().setPersist(false)
                            .withAnlass(verlaufAnlass)
                            .withRealisierungstermin(realDate)
                            .build();
        //@formatter:on
        doReturn(basMock).when(cut).getCCService(BAService.class);
        when(basMock.findActVerlauf4Auftrag(eq(ccAuftragId), eq(false))).thenReturn(verlauf);
        assertEquals(cut.checkIsRealDateToday(ccAuftragId), expected);
        verify(basMock, times(1)).findActVerlauf4Auftrag(eq(ccAuftragId), eq(false));
    }

    @DataProvider(name = "checkIsStandortTypHVTDP")
    public Object[][] checkIsStandortTypHVTDP() {
        return new Object[][] { { HVTStandort.HVT_STANDORT_TYP_HVT, true },
                { HVTStandort.HVT_STANDORT_TYP_HVT - 1, false }, { HVTStandort.HVT_STANDORT_TYP_HVT + 1, false }, };
    }

    @Test(dataProvider = "checkIsStandortTypHVTDP")
    public void checkIsStandortTypHVT(Long standortTyp, boolean expected) throws FindException,
            ServiceNotFoundException {
        //@formatter:off
        final HVTStandort standort = new HVTStandortBuilder().setPersist(false)
                                        .withRandomId()
                                        .withStandortTypRefId(standortTyp)
                                        .build();
        final Endstelle endstelle = new Endstelle();
        endstelle.setHvtIdStandort(standort.getHvtIdStandort());
        //@formatter:on
        doReturn(esMock).when(cut).getCCService(EndstellenService.class);
        doReturn(hvtsMock).when(cut).getCCService(HVTService.class);
        when(esMock.findEndstelle4Auftrag(eq(ccAuftragId), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelle);
        when(hvtsMock.findHVTStandort(eq(endstelle.getHvtIdStandort()))).thenReturn(standort);
        assertEquals(cut.checkIsStandortTypHVT(ccAuftragId), expected);
        verify(esMock, times(1)).findEndstelle4Auftrag(eq(ccAuftragId), eq(Endstelle.ENDSTELLEN_TYP_B));
        verify(hvtsMock, times(1)).findHVTStandort(eq(endstelle.getHvtIdStandort()));
    }

    @DataProvider(name = "checkIsDownstream18000DP")
    public Object[][] checkIsDownstream18000DP() {
        return new Object[][] { { 18000L, true }, { 17999L, false }, { 18001L, false } };
    }

    @Test(dataProvider = "checkIsDownstream18000DP")
    public void checkIsDownstream18000(long downstream, boolean expected) throws FindException,
            ServiceNotFoundException {
        //@formatter:off
        final TechLeistung techLs = new TechLeistungBuilder().setPersist(false)
                                        .withLongValue(downstream)
                                        .build();
        //@formatter:on
        doReturn(lsMock).when(cut).getCCService(CCLeistungsService.class);
        when(lsMock.findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM), any(Date.class)))
                .thenReturn(techLs);
        assertEquals(cut.checkIsDownstream18000(ccAuftragId), expected);
        verify(lsMock, times(1)).findTechLeistung4Auftrag(eq(ccAuftragId), eq(TechLeistung.TYP_DOWNSTREAM),
                any(Date.class));
    }

    @DataProvider(name = "checkIsNeuschaltungDP")
    public Object[][] checkIsNeuschaltungDP() {
        return new Object[][] { { true, true, true }, { false, true, true }, { true, false, true }, { false, false, false } };
    }

    @Test(dataProvider = "checkIsNeuschaltungDP")
    public void checkIsNeuschaltung(boolean auftragInBetrieb, boolean realDateToday, boolean expected)
            throws FindException, ServiceNotFoundException {
        doReturn(auftragInBetrieb).when(cut).checkIsAuftragInRealisierung(ccAuftragId);
        doReturn(realDateToday).when(cut).checkIsRealDateToday(ccAuftragId);
        assertEquals(cut.checkIsNeuschaltung(ccAuftragId), expected);
    }

    @DataProvider(name = "executeDP")
    public Object[][] executeDP() {
        return new Object[][] {
                { true, true, true, true },
                { false, true, true, false },
                { true, false, true, false },
                { true, true, false, false },
                { false, false, true, false },
                { false, true, false, false },
                { false, false, false, false },
                { true, false, false, false },
        };
    }

    @Test(dataProvider = "executeDP")
    public void execute(boolean isNeuschaltung, boolean isStandortTypHvt, boolean isDownStream18000, boolean expected)
            throws Exception {
        doReturn(isNeuschaltung).when(cut).checkIsNeuschaltung(ccAuftragId);
        doReturn(isStandortTypHvt).when(cut).checkIsStandortTypHVT(ccAuftragId);
        doReturn(isDownStream18000).when(cut).checkIsDownstream18000(ccAuftragId);
        assertEquals(cut.execute(), expected);
    }
}
