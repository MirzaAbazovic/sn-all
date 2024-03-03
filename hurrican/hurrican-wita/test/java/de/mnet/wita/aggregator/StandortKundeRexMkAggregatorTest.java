/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.10.2011 09:07:28
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link StandortKundeRexMkAggregator}.
 */
@Test(groups = BaseTest.UNIT)
public class StandortKundeRexMkAggregatorTest extends BaseTest {

    @InjectMocks
    private StandortKundeRexMkAggregator cut;

    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        cut = new StandortKundeRexMkAggregator();
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
    }


    public void findAdresseStandort() throws FindException {
        CCAddress address = new CCAddressBuilder().withOrt("old.location").setPersist(false).build();
        Vorabstimmung vorabstimmung = new VorabstimmungBuilder().withPreviousLocationAdress(address).setPersist(false).build();
        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(vorabstimmung);

        AddressModel result = cut.findAdresseStandort(cbVorgang);
        assertNotNull(result);
        assertEquals(result.getOrt(), address.getOrt());
    }

}


