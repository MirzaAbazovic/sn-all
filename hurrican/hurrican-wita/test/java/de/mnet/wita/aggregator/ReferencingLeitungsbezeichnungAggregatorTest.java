/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2011 09:19:06
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link ReferencingLeitungsbezeichnungAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class ReferencingLeitungsbezeichnungAggregatorTest extends BaseTest {

    private ReferencingLeitungsbezeichnungAggregator cut;

    private WitaCBVorgang cbVorgang;
    private Carrierbestellung actualCarrierbestellung;
    private Carrierbestellung referencingCarrierbestellung;

    @Mock
    private CarrierService carrierServiceMock;

    @Mock
    private WitaDataService witaDataService;

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
        actualCarrierbestellung = new CarrierbestellungBuilder()
                .withAuftrag4TalNaBuilder(new AuftragBuilder().withRandomId().setPersist(false))
                .setPersist(false).build();
        referencingCarrierbestellung = new CarrierbestellungBuilder().withLbz("96W/08900/82100/123456").setPersist(
                false).build();

        cut = new ReferencingLeitungsbezeichnungAggregator();
        cut.carrierService = carrierServiceMock;
        cut.witaDataService = witaDataService;

        when(carrierServiceMock.findCB(any(Long.class))).thenReturn(actualCarrierbestellung);
    }

    @Test
    public void aggregate() {
        ReferencingLeitungsbezeichnungAggregator cutSpy = spy(cut);
        when(witaDataService.getReferencingCarrierbestellung(
                cbVorgang, actualCarrierbestellung)).thenReturn(referencingCarrierbestellung);

        LeitungsBezeichnung result = cutSpy.aggregate(cbVorgang);
        assertNotNull(result);
        assertEquals(result.getLeitungsSchluesselZahl(), "96W");
        assertEquals(result.getOnkzKunde(), "89");
        assertEquals(result.getOnkzKollokation(), "821");
        assertEquals(result.getOrdnungsNummer(), "0000123456");
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateWithMissingCarrierbestellung() {
        ReferencingLeitungsbezeichnungAggregator cutSpy = spy(cut);
        doReturn(null).when(witaDataService).getReferencingCarrierbestellung(cbVorgang,
                actualCarrierbestellung);

        cutSpy.aggregate(cbVorgang);
    }

}
