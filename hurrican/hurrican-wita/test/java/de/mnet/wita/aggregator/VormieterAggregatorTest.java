/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2011
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieterBuilder;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link StandortKundeAggregator}.
 */
@Test(groups = BaseTest.UNIT)
public class VormieterAggregatorTest extends BaseTest {

    private VormieterAggregator cut;
    private WitaCBVorgang cbVorgang;

    @Mock
    private WitaDataService witaDataService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cut = new VormieterAggregator();
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();

        cut.witaDataService = witaDataService;
    }

    public void testCreateVormieter() throws Exception {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder()
                .withCarrierbestellungVormieterBuilder(
                        (new CarrierbestellungVormieterBuilder()).withUfaNummer("789C567890"))
                .setPersist(false).build();
        when(witaDataService.loadCarrierbestellung(cbVorgang)).thenReturn(carrierbestellung);

        Vormieter result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Vormieter wurde nicht generiert!");
        assertEquals(result.getUfaNummer(), "789C567890");
    }

    public void testCreateVormieterWithEmptyData() throws Exception {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder()
                .withCarrierbestellungVormieterBuilder(
                        (new CarrierbestellungVormieterBuilder()))
                .setPersist(false).build();
        when(witaDataService.loadCarrierbestellung(cbVorgang)).thenReturn(carrierbestellung);

        Vormieter result = cut.aggregate(cbVorgang);
        assertNull(result);
    }

}
