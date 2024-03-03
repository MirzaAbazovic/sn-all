/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2014
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link de.mnet.wita.aggregator.StandortKundeVblAggregator}.
 */
@Test(groups = BaseTest.UNIT)
public class StandortKundeVblAggregatorTest extends BaseTest {

    @InjectMocks
    @Spy
    private StandortKundeVblAggregator cut;

    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        cut = spy(new StandortKundeVblAggregator());
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
    }


    public void findAdresseStandort() throws FindException {
        CCAddress address = new CCAddressBuilder()
                .setPersist(false)
                .build();

        Endstelle es = new EndstelleBuilder().withGeoIdBuilder(
                new GeoIdBuilder()
                        .withId(Long.valueOf(99))
                        .setPersist(false)
        )
                .setPersist(false)
                .build();

        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().withPreviousLocationAdress(address).setPersist(false).build();
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(vorabstimmung);
        doReturn(address).when(cut).loadAnschlussinhaberAdresse(cbVorgang);
        doReturn(es).when(cut).loadEndstelle(cbVorgang);
        doReturn(address).when(cut).loadDtagNameFailSave(anyLong(), any(AddressModel.class));

        AddressModel result = cut.findAdresseStandort(cbVorgang);

        verify(cut).loadDtagNameFailSave(es.getGeoId(), address);
        assertNotNull(result);
        assertEquals(result.getOrt(), address.getOrt());
        assertEquals(result.getName(), address.getName());
    }

}
