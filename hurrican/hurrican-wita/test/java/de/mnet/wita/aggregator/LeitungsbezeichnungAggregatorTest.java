/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 13:40:57
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link LeitungsbezeichnungAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class LeitungsbezeichnungAggregatorTest extends BaseTest {

    private LeitungsbezeichnungAggregator cut;

    private WitaCBVorgang cbVorgang;

    @Mock
    private CarrierService carrierServiceMock;

    @Mock
    private WitaDataService witaDataServiceMock;

    @Mock
    private EndstellenService endstellenServiceMock;

    @Mock
    private CCAuftragService auftragServiceMock;

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).withAuftragId(123L).build();
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withLbz("96W/00111/821/123456").build();

        List<Endstelle> endstellen = Arrays.asList(new EndstelleBuilder().setPersist(false).withId(1L).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build());
        AuftragDaten auftragDaten = new AuftragDatenBuilder().setPersist(false).withAuftragId(123L).build();
        cut = new LeitungsbezeichnungAggregator();
        cut.carrierService = carrierServiceMock;
        cut.witaDataService = witaDataServiceMock;
        cut.endstellenService = endstellenServiceMock;
        cut.ccAuftragService = auftragServiceMock;

        when(carrierServiceMock.findCB(any(Long.class))).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Carrierbestellung(any(Carrierbestellung.class))).thenReturn(endstellen);
        when(auftragServiceMock.findAuftragDatenByEndstelleTx(endstellen.get(0).getId())).thenReturn(auftragDaten);
        when(witaDataServiceMock.loadHVTStandortOnkz4Auftrag(any(Long.class), any(String.class))).thenReturn("821");
    }

    public void testAggregate() {
        LeitungsBezeichnung result = cut.aggregate(cbVorgang);
        assertNotNull(result);
        assertEquals(result.getLeitungsSchluesselZahl(), "96W");
        assertEquals(result.getOnkzKunde(), "111");
        assertEquals(result.getOnkzKollokation(), "821");
        assertEquals(result.getOrdnungsNummer(), "0000123456");
    }

}
