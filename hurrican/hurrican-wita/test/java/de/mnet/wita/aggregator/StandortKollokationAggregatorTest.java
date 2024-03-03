/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:56:57
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.KvzAdresseBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link StandortKollokationAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class StandortKollokationAggregatorTest extends BaseTest {

    @InjectMocks
    @Spy
    private StandortKollokationAggregator cut;

    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private WitaDataService witaDataServiceMock;

    private WitaCBVorgang cbVorgang;
    private HVTGruppeBuilder hvtGruppeBuilder;
    private HVTStandort hvtStandort;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().build();
        hvtGruppeBuilder = new HVTGruppeBuilder().withRandomId().withOnkz("00821").withHausNr("1A");
        hvtStandort = new HVTStandortBuilder().withRandomId().withHvtGruppeBuilder(hvtGruppeBuilder).build();
    }

    public void aggregateHvt() throws FindException {
        when(hvtServiceMock.findHVTGruppe4Standort(hvtStandort.getId())).thenReturn(hvtGruppeBuilder.get());
        doReturn(hvtStandort).when(cut).loadHvtStandort(cbVorgang);

        StandortKollokation result = cut.aggregate(cbVorgang);

        verify(cut, times(1)).loadHvtStandort(cbVorgang);
        verify(cut, times(1)).createHvtStandort(hvtGruppeBuilder.get(), hvtStandort);

        assertEquals(result.getPostleitzahl(), hvtGruppeBuilder.get().getPlz());
        assertEquals(result.getOrtsname(), hvtGruppeBuilder.get().getOrt());
        assertEquals(result.getStrassenname(), hvtGruppeBuilder.get().getStrasse() + " "
                + hvtGruppeBuilder.get().getHausNr());
        assertEquals(result.getHausnummer(), "0");
        assertNull(result.getHausnummernZusatz());
        assertEquals(result.getOnkz(), "821");
        assertEquals(result.getAsb(), String.format("%s", hvtStandort.getAsb()));
    }

    public void aggregateKvz() throws FindException {
        hvtStandort.setStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        Equipment eq = new EquipmentBuilder().withKvzNummer("123").build();
        KvzAdresse adr = new KvzAdresseBuilder().build();

        when(hvtServiceMock.findHVTGruppe4Standort(hvtStandort.getId())).thenReturn(
                hvtGruppeBuilder.withHausNr(null).build());
        when(hvtServiceMock.findKvzAdresse(hvtStandort.getId(), eq.getKvzNummer())).thenReturn(adr);
        when(witaDataServiceMock.loadDtagEquipmentsWithNoRollback(cbVorgang)).thenReturn(ImmutableList.of(eq));
        doReturn(hvtStandort).when(cut).loadHvtStandort(cbVorgang);

        StandortKollokation result = cut.aggregate(cbVorgang);

        verify(cut, times(1)).loadHvtStandort(cbVorgang);

        assertEquals(result.getPostleitzahl(), adr.getPlz());
        assertEquals(result.getOrtsname(), adr.getOrt());
        assertEquals(result.getStrassenname(), adr.getStrasse() + " " + adr.getHausNr());
        assertEquals(result.getHausnummer(), "0");
        assertNull(result.getHausnummernZusatz());
        assertEquals(result.getOnkz(), hvtGruppeBuilder.build().getOnkzWithoutLeadingNulls());
        assertEquals(result.getAsb(), String.format("%s", hvtStandort.getAsb()));
    }
}
