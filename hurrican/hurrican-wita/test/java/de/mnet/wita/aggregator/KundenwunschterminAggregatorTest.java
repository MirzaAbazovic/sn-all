/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 09:31:39
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.impl.DateTimeCalculationService;

/**
 * TestNG Klasse fuer {@link KundenwunschterminAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class KundenwunschterminAggregatorTest extends BaseTest {

    private KundenwunschterminAggregator cut;

    private WitaCBVorgang cbVorgang;
    private ZeitfensterAggregator zeitfensterAggregator = new ZeitfensterAggregator();

    @Mock
    private WitaConfigService witaConfigService;

    @Mock
    private WbciCommonService wbciCommonService;
    
    @Mock
    private CarrierElTALService carrierElTALService;

    @Mock
    private DateTimeCalculationService dateTimeCalculationService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cut = new KundenwunschterminAggregator();
        cut.zeitfensterAggregator = zeitfensterAggregator;
        cut.dateTimeCalculationService = dateTimeCalculationService;
        cut.wbciCommonService = wbciCommonService;
        cut.witaConfigService = witaConfigService;
        cut.carrierElTALService = carrierElTALService;

        zeitfensterAggregator.witaConfigService = witaConfigService;

        when(
                dateTimeCalculationService.isKundenwunschTerminValid(any(LocalDateTime.class), any(LocalDateTime.class),
                        any(Boolean.class), any(Boolean.class), any(GeschaeftsfallTyp.class), any(String.class), any(Boolean.class))
        )
                .thenReturn(true);
    }

    @DataProvider
    public Object[][] usecases() {
        return new Object[][] {
                { GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1, Zeitfenster.SLOT_9 },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE, WitaCdmVersion.V1, Zeitfenster.SLOT_2 },
                { GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, WitaCdmVersion.V1, Zeitfenster.SLOT_9 },
                { GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, WitaCdmVersion.V1, Zeitfenster.SLOT_9 },
        };
    }

    @Test(dataProvider = "usecases")
    public void aggregate(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion version, Zeitfenster expectedZeitfenster) {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(version);

        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(geschaeftsfallTyp).setPersist(false).build();
        Kundenwunschtermin result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Es wurde kein Kundenwunschtermin erzeugt!");
        assertEquals(result.getDatum(), DateConverterUtils.asLocalDate(cbVorgang.getVorgabeMnet()),
                "Kundenwunschtermin ist abweichend von Vorgabe!");
        assertEquals(result.getZeitfenster(), expectedZeitfenster);
    }


    public void aggregateWithWbciVaIdAndTypKuendigung() throws StoreException {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V1);
        when(witaConfigService.getWbciWitaKuendigungsOffset()).thenReturn(3);

        Date now = Date.from(ZonedDateTime.of(2014, 1, 20, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant());
        cbVorgang = new WitaCBVorgangBuilder()
                .withTyp(WitaCBVorgang.TYP_KUENDIGUNG)
                .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_KUNDE)
                .withVorabstimmungsId("DEU.DTAG.1234")
                .withVorgabeMnet(now)
                .setPersist(false).build();
        Kundenwunschtermin result = cut.aggregate(cbVorgang);
        assertNotNull(result);
        assertNotNull(result.getDatum());
        assertTrue(DateTools.isDateEqual(Date.from(result.getDatum().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(ZonedDateTime.of(2014, 1, 23, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())));
        verify(carrierElTALService).saveCBVorgang(cbVorgang);
    }


    public void aggregateWithWbciVaIdAndTypNeu() throws StoreException {
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.V1);
        when(witaConfigService.getWbciWitaKuendigungsOffset()).thenReturn(3);

        Date now = Date.from(ZonedDateTime.of(2014, 1, 20, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant());
        cbVorgang = new WitaCBVorgangBuilder()
                .withTyp(WitaCBVorgang.TYP_NEU)
                .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG)
                .withVorabstimmungsId("DEU.DTAG.1234")
                .withVorgabeMnet(now)
                .setPersist(false).build();
        Kundenwunschtermin result = cut.aggregate(cbVorgang);
        assertNotNull(result);
        assertNotNull(result.getDatum());
        assertTrue(DateTools.isDateEqual(Date.from(result.getDatum().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(ZonedDateTime.of(2014, 1, 20, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())));
        verify(carrierElTALService, times(0)).saveCBVorgang(cbVorgang);
    }

}
