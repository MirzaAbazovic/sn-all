/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:46:10
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
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.UNIT)
public class GeschaeftsfallProduktAggregatorTest extends BaseTest {

    @InjectMocks
    private GeschaeftsfallProduktAggregator cut;
    @Mock
    private StandortKundeAggregator standortKundeAggregatorMock;
    @Mock
    private StandortKundeRexMkAggregator standortKundeRexMkAggregatorMock;
    @Mock
    private StandortKollokationAggregator standortKollokationAggregatorMock;
    @Mock
    private SchaltangabenAggregator schaltangabenAggregatorMock;
    @Mock
    private MontageleistungAggregator montageleistungAggregatorMock;
    @Mock
    private LeitungsbezeichnungAggregator leitungsbezeichnungAggregatorMock;

    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        cut = new GeschaeftsfallProduktAggregator();
        MockitoAnnotations.initMocks(this);

        setupAggregatorMock(standortKundeAggregatorMock);
        setupAggregatorMock(standortKollokationAggregatorMock);
        setupAggregatorMock(schaltangabenAggregatorMock);
        setupAggregatorMock(montageleistungAggregatorMock);
        setupAggregatorMock(leitungsbezeichnungAggregatorMock);
        setupAggregatorMock(standortKundeRexMkAggregatorMock);
    }

    private <T extends MwfEntity> void setupAggregatorMock(AbstractWitaDataAggregator<T> aggregatorMock) {
        Class<T> aggregationType = ReflectionTools.getTypeArgument(AbstractWitaDataAggregator.class,
                aggregatorMock.getClass());
        when(aggregatorMock.getAggregationType()).thenReturn(aggregationType);
    }

    public void aggregate() {
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
        cbVorgang.setVorabstimmungsId("DEU.MNET.V000000001");

        when(standortKundeAggregatorMock.aggregate(cbVorgang)).thenReturn(new StandortKunde());
        when(standortKollokationAggregatorMock.aggregate(cbVorgang)).thenReturn(new StandortKollokation());
        when(schaltangabenAggregatorMock.aggregate(cbVorgang)).thenReturn(new Schaltangaben());
        when(montageleistungAggregatorMock.aggregate(cbVorgang)).thenReturn(new Montageleistung());
        when(leitungsbezeichnungAggregatorMock.aggregate(cbVorgang)).thenReturn(new LeitungsBezeichnung());

        GeschaeftsfallProdukt result = cut.aggregate(cbVorgang);
        assertNotNull(result, "GeschaeftsfallProdukt wurde nicht generiert!");
        assertNotNull(result.getStandortKunde(), "Kundenstandort wurde nicht generiert!");
        assertNotNull(result.getStandortKollokation(), "Kollokationsstandort wurde nicht generiert!");
        assertNotNull(result.getSchaltangaben(), "Schaltangaben wurden nicht generiert!");
        assertNotNull(result.getMontageleistung(), "Montageleistung wurde nicht generiert!");
        assertNotNull(result.getVorabstimmungsId(), "VorabstimmungsId wurde nicht gesetzt!");
        assertNull(result.getLeitungsBezeichnung());

        verify(standortKundeAggregatorMock).aggregate(cbVorgang);
        verify(standortKollokationAggregatorMock).aggregate(cbVorgang);
        verify(schaltangabenAggregatorMock).aggregate(cbVorgang);
        verify(montageleistungAggregatorMock).aggregate(cbVorgang);
        verify(leitungsbezeichnungAggregatorMock, times(0)).aggregate(cbVorgang);
    }

    public void aggregateForKuendigung() {
        cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.KUENDIGUNG_KUNDE)
                .setPersist(false).build();
        cbVorgang.setVorabstimmungsId("DEU.MNET.V000000001");

        when(standortKundeAggregatorMock.aggregate(cbVorgang)).thenReturn(new StandortKunde());
        when(standortKollokationAggregatorMock.aggregate(cbVorgang)).thenReturn(new StandortKollokation());
        when(schaltangabenAggregatorMock.aggregate(cbVorgang)).thenReturn(new Schaltangaben());
        when(montageleistungAggregatorMock.aggregate(cbVorgang)).thenReturn(new Montageleistung());
        when(leitungsbezeichnungAggregatorMock.aggregate(cbVorgang)).thenReturn(new LeitungsBezeichnung());

        GeschaeftsfallProdukt result = cut.aggregate(cbVorgang);
        assertNotNull(result);
        assertNotNull(result.getLeitungsBezeichnung(), "LeitungsBezeichnung wurde nicht generiert!");
        assertNull(result.getStandortKunde());
        assertNull(result.getStandortKollokation());
        assertNull(result.getSchaltangaben());
        assertNull(result.getMontageleistung());
        assertNotNull(result.getVorabstimmungsId(), "VorabstimmungsId wurde nicht gesetzt!");

        verify(standortKundeAggregatorMock, times(0)).aggregate(cbVorgang);
        verify(standortKollokationAggregatorMock, times(0)).aggregate(cbVorgang);
        verify(schaltangabenAggregatorMock, times(0)).aggregate(cbVorgang);
        verify(montageleistungAggregatorMock, times(0)).aggregate(cbVorgang);
        verify(leitungsbezeichnungAggregatorMock).aggregate(cbVorgang);
    }

}
