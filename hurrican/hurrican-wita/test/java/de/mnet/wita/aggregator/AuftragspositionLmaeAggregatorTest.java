/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.07.2011 09:58:02
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

/**
 * TestNG Klasse fuer den {@link AuftragspositionLmaeAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class AuftragspositionLmaeAggregatorTest extends BaseTest {

    @InjectMocks
    private AuftragspositionLmaeAggregator cut = new AuftragspositionLmaeAggregator();
    @Mock
    private AuftragspositionAggregator auftragspositionAggregatorMock;
    @Mock
    private SchaltangabenAggregator schaltangabenAggregatorMock;
    @Mock
    private ProduktBezeichnerAggregator produktBezeichnerAggregator;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
    }

    public void testAggregate() throws Exception {
        StandortKollokation standortKollokation = new StandortKollokation();
        GeschaeftsfallProdukt geschaeftsfallProdukt = new GeschaeftsfallProdukt();
        geschaeftsfallProdukt.setStandortKollokation(standortKollokation);
        Auftragsposition auftragsposition = new Auftragsposition();
        auftragsposition.setAktionsCode(AktionsCode.AENDERUNG);
        auftragsposition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        when(auftragspositionAggregatorMock.aggregate(cbVorgang)).thenReturn(auftragsposition);
        when(produktBezeichnerAggregator.aggregate(any(Auftrag.class))).thenReturn(ProduktBezeichner.HVT_2N);

        Auftragsposition result = cut.aggregate(cbVorgang);
        verify(auftragspositionAggregatorMock, times(1)).aggregate(cbVorgang);
        verify(schaltangabenAggregatorMock, times(1)).aggregate(cbVorgang);

        assertNotNull(result);
        assertEquals(result.getAktionsCode(), AktionsCode.AENDERUNG);
        assertNotNull(result.getPosition());
        assertEquals(result.getPosition().getAktionsCode(), AktionsCode.AENDERUNG);
        assertSame(result.getPosition().getGeschaeftsfallProdukt().getStandortKollokation(), standortKollokation);

        // wird kuenftig ueber den WitaDataAggregationTask ermittelt!
        assertNull(result.getPosition().getProduktBezeichner());
    }
}
