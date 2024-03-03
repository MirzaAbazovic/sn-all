/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2012 13:47:31
 */
package de.augustakom.hurrican.service.wholesale.impl;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.wholesale.WholesaleVdslProfile;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.wholesale.WholesaleServiceException;

@Test(groups = BaseTest.UNIT)
public class WholesaleFaultClearanceServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private WholesaleFaultClearanceServiceImpl cut;

    @Mock
    DSLAMService dslamService;
    @Mock
    CCLeistungsService ccLeistungsService;
    @Mock
    CCAuftragService auftragService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "NP_NONNULL_PARAM_VIOLATION",
            justification = "test should check if exception if thrown if parameter is null!")
    public void checkIfChangePortIsPossibleRangierungToUseNullYieldsException() {
        try {
            cut.checkIfChangePortIsPossible(Long.valueOf(1), new Endstelle(), null);
        }
        catch (WholesaleServiceException e) {
            assertTrue(StringUtils.startsWithIgnoreCase(e.getFehlerBeschreibung(),
                    "WholesaleService caused an Error! Port-Change not possible because defined port does not exist"));
            return;
        }
        fail("should have thrown an exception!");
    }


    @Test
    public void checkIfChangePortIsPossibleRangierungToUseIsAssignedYieldsException() {
        try {
            Rangierung rangierung = new RangierungBuilder()
                    .withEsId(Long.valueOf(999))
                    .setPersist(false).build();
            cut.checkIfChangePortIsPossible(Long.valueOf(1), new Endstelle(), rangierung);
        }
        catch (WholesaleServiceException e) {
            assertTrue(StringUtils.startsWithIgnoreCase(e.getFehlerBeschreibung(),
                    "WholesaleService caused an Error! Port-Change not possible because port is not defined or selected port is already assigned to another order"));
            return;
        }
        fail("should have thrown an exception!");
    }

    @Test
    public void checkIfChangePortIsPossibleRangierungToUseIsHistorizedYieldsException() {
        try {
            Rangierung rangierung = new RangierungBuilder().withEsId(Long.valueOf(999)).withGueltigBis(new Date())
                    .setPersist(false).build();
            cut.checkIfChangePortIsPossible(Long.valueOf(1), new Endstelle(), rangierung);
        }
        catch (WholesaleServiceException e) {
            assertTrue(StringUtils
                    .startsWithIgnoreCase(e.getFehlerBeschreibung(),
                            "WholesaleService caused an Error! Port-Change not possible because defined port (Rangierung) is historized!"));
            return;
        }
        fail("should have thrown an exception!");
    }

    @Test
    public void checkIfChangePortIsPossibleDifferentLocationsYieldsException() {
        try {
            Endstelle endstelle = new EndstelleBuilder()
                    .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                    .withHvtStandortBuilder(new HVTStandortBuilder().withId(Long.valueOf(1)).setPersist(false))
                    .setPersist(false).build();

            Rangierung rangierung = new RangierungBuilder()
                    .withHvtStandortBuilder(new HVTStandortBuilder().withId(Long.valueOf(2)).setPersist(false))
                    .setPersist(false).build();
            cut.checkIfChangePortIsPossible(Long.valueOf(1), endstelle, rangierung);
        }
        catch (WholesaleServiceException e) {
            assertTrue(StringUtils.startsWithIgnoreCase(e.getFehlerBeschreibung(),
                    "WholesaleService caused an Error! The selected port is at another location than the order! Port change not possible."));
            return;
        }
        fail("should have thrown an exception!");
    }

    @Test
    public void getPossibleVdslProfiles() throws Exception {
        AuftragBuilder auftragBuilder = new AuftragBuilder();
        AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withProdBuilder(new ProduktBuilder().withId(Produkt.PROD_ID_WHOLESALE_FTTX))
                .withAuftragBuilder(auftragBuilder);
        Auftrag auftrag = auftragBuilder.get();
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        TechLeistung downstreamTechLs = new TechLeistungBuilder().withTyp(TechLeistung.TYP_DOWNSTREAM)
                .withLongValue(50000L).get();
        TechLeistung upstreamTechLs = new TechLeistungBuilder().withTyp(TechLeistung.TYP_UPSTREAM)
                .withLongValue(5000L).get();
        List<DSLAMProfile> dslamProfiles = new ArrayList<DSLAMProfile>();
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(100000, 10000)
                .withName("100000_10000").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(100000, 5000)
                .withName("100000_5000").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(50000, 10000)
                .withName("50000_10000").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(50000, 5000)
                .withName("50000_5000").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(50000, 2500)
                .withName("50000_2500").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(25000, 5000)
                .withName("25000_5000").build());
        dslamProfiles.add(new DSLAMProfileBuilder().withBandwidth(25000, 2500)
                .withName("25000_2500").build());

        Mockito.when(auftragService.findActiveOrderByLineId(Mockito.anyString(), Mockito.any(LocalDate.class)))
                .thenReturn(auftrag);
        Mockito.when(auftragService.findAuftragDatenByAuftragIdTx(Mockito.anyLong())).thenReturn(auftragDaten);
        Mockito.when(ccLeistungsService.findTechLeistung4Auftrag(Mockito.anyLong(),
                Mockito.eq(TechLeistung.TYP_DOWNSTREAM), Mockito.any(Date.class))).thenReturn(downstreamTechLs);
        Mockito.when(ccLeistungsService.findTechLeistung4Auftrag(Mockito.anyLong(),
                Mockito.eq(TechLeistung.TYP_UPSTREAM), Mockito.any(Date.class))).thenReturn(upstreamTechLs);
        Mockito.when(dslamService.findValidDSLAMProfiles4Auftrag(Mockito.anyLong())).thenReturn(dslamProfiles);

        List<WholesaleVdslProfile> possibleVdslProfiles = cut.getPossibleVdslProfiles("dummy");
        Assert.assertEquals(possibleVdslProfiles.size(), 4);
        Assert.assertEquals(possibleVdslProfiles.get(0).getName(), "50000_5000");
        Assert.assertEquals(possibleVdslProfiles.get(1).getName(), "50000_2500");
        Assert.assertEquals(possibleVdslProfiles.get(2).getName(), "25000_5000");
        Assert.assertEquals(possibleVdslProfiles.get(3).getName(), "25000_2500");
    }
}
