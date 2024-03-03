/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 14:03:50
 */
package de.mnet.wita.aggregator;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Projekt;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = UNIT)
public class ProjektAggregatorTest extends BaseTest {

    @InjectMocks
    private ProjektAggregator underTest = new ProjektAggregator();

    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cbVorgang = new WitaCBVorgang();
    }

    public void testRexmk() {
        cbVorgang.setCbId(null);
        assertNull(underTest.aggregate(cbVorgang));
    }

    public void testNotRexmk() {
        cbVorgang.setCbId(123L);
        cbVorgang.setProjektKenner("projektKenner");
        cbVorgang.setKopplungsKenner("kopplungskenner");

        Projekt projekt = underTest.aggregate(cbVorgang);
        assertEquals(projekt.getProjektKenner(), cbVorgang.getProjektKenner());
        assertEquals(projekt.getKopplungsKenner(), cbVorgang.getKopplungsKenner());
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void kopplungskennerOhneProjektkennerIsNotAllowed() {
        cbVorgang.setCbId(123L);
        cbVorgang.setProjektKenner(null);
        cbVorgang.setKopplungsKenner("Kopplungskenner");
        underTest.aggregate(cbVorgang);
    }

    public void testPlainOldNeubestellung() {
        cbVorgang.setCbId(123L);
        cbVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        assertNull(underTest.aggregate(cbVorgang));
    }

    public void testAnbieterwechselWithoutWitaCarrier() {
        cbVorgang.setCbId(123L);
        cbVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.PROVIDERWECHSEL);
        cbVorgang.setKopplungsKenner("kopplungskenner");
        cbVorgang.setTyp(CBVorgang.TYP_ANBIETERWECHSEL);

        Carrier noWitaCarrier = new Carrier();
        noWitaCarrier.setHasWitaInterface(false);
        Vorabstimmung vorabstimmung = new Vorabstimmung();
        vorabstimmung.setCarrier(noWitaCarrier);

        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(vorabstimmung);

        Projekt projekt = underTest.aggregate(cbVorgang);
        assertEquals(projekt.getProjektKenner(), WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA);

        cbVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.VERBUNDLEISTUNG);
        assertEquals(projekt.getProjektKenner(), WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA);
    }

    public void testAnbieterwechselForCarrierNotOnWita() {
        cbVorgang.setCbId(123L);
        cbVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        cbVorgang.setKopplungsKenner("kopplungskenner");
        cbVorgang.setTyp(CBVorgang.TYP_ANBIETERWECHSEL);

        Carrier noWitaCarrier = new Carrier();
        noWitaCarrier.setHasWitaInterface(false);
        Vorabstimmung vorabstimmung = new Vorabstimmung();
        vorabstimmung.setCarrier(noWitaCarrier);

        when(witaDataService.loadVorabstimmung(cbVorgang)).thenReturn(vorabstimmung);

        Projekt projekt = underTest.aggregate(cbVorgang);
        assertEquals(projekt.getProjektKenner(), WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA);

        cbVorgang.setWitaGeschaeftsfallTyp(GeschaeftsfallTyp.VERBUNDLEISTUNG);
        assertEquals(projekt.getProjektKenner(), WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA);
    }

}
