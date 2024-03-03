/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2014 07:31
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.command.cps.CpsFacade;
import de.augustakom.hurrican.service.cc.impl.command.cps.PbitHelper;

@Test(groups = { BaseTest.UNIT })
public class CpsGetDataServiceImplGetFttbDataTest {

    @InjectMocks
    private CPSGetDataServiceImpl cut = new CPSGetDataServiceImpl();

    @Mock
    private HVTService hvtService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private HWService hwService;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private PbitHelper pbitHelper;
    @Mock
    private VlanService vlanService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private EkpFrameContractService ekpFrameContractService;
    @Mock
    private ProfileService profileService;
    @Mock
    private CpsFacade cpsFacade;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    @DataProvider
    public Object[][] fttbDataProvider() {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setRackTyp(HWRack.RACK_TYPE_DPO);
        HWMdu hwMdu = new HWMdu();
        hwMdu.setRackTyp(HWRack.RACK_TYPE_MDU);
        hwMdu.setIpAddress("10.10.10.10");
        return new Object[][] {
                { hwDpo },
                { hwMdu }
        };
    }

    @Test(dataProvider = "fttbDataProvider")
    public void testGetFttbData(HWOltChild hwOltChild) throws FindException {
        final String portName = "WV10048354--Jiang";
        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().setPersist(false).build();
        final Date when = new Date();
        initData(hwOltChild, auftragDaten, when);

        CpsAccessDevice result = cut.getFttbData(auftragDaten.getAuftragId(), portName, when);
        assertThat(result.getItems(), hasSize(1));
        if (HWRack.RACK_TYPE_MDU.equals(hwOltChild.getRackTyp())) {
            CpsItem cpsItem = result.getItems().get(0);
            assertEquals(cpsItem.getEndpointDevice().getIpAddress(), "10.10.10.10");
        }
    }

    @Test(dataProvider = "fttbDataProvider")
    public void testGetFttbDataWithWholesaleProduct(HWOltChild hwOltChild) throws FindException {
        final String portName = "WV10048354--Jiang";
        final String ekpId = "QSC";
        final String frameContractId = "QSC-001";
        final String a10Nsp = "A10-NSP";
        final String vbz = "DEU.MNET.000001";
        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().withProdId(600L)
                .setPersist(false).build();
        final Date when = new Date();

        initData(hwOltChild, auftragDaten, when);

        CpsAccessDevice result = cut.getFttbData(auftragDaten.getAuftragId(), portName, when);
        assertThat(result.getItems(), hasSize(1));
        if (HWRack.RACK_TYPE_MDU.equals(hwOltChild.getRackTyp())) {
            CpsItem cpsItem = result.getItems().get(0);
            assertEquals(cpsItem.getEndpointDevice().getIpAddress(), "10.10.10.10");
        }
    }

    private void initData(final HWOltChild hwOltChild, final AuftragDaten auftragDaten, final Date when) throws FindException {
        HWBaugruppenTypBuilder baugruppenTypBuilder = new HWBaugruppenTypBuilder().withProfileAssignable(false);
        final HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder().withRandomId().setPersist(false).withBaugruppenTypBuilder(baugruppenTypBuilder);
        final HWBaugruppe baugruppe = baugruppeBuilder.build();
        final EquipmentBuilder eqInBuilder = new EquipmentBuilder().withRandomId().withBaugruppeBuilder(baugruppeBuilder).setPersist(false);
        final Equipment port = eqInBuilder.build();
        final RangierungBuilder rangierungBuilder = new RangierungBuilder().withEqInBuilder(eqInBuilder).withRandomId().setPersist(false);
        final Rangierung rangierung = rangierungBuilder.build();
        final HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withStandortTypRefId(1L).withRandomId().setPersist(false);
        final Endstelle endstelle = new EndstelleBuilder()
                .withRangierungBuilder(rangierungBuilder)
                .withAuftragTechnikBuilder(new AuftragTechnikBuilder()
                        .withAuftragTechnik2EndstelleBuilder(new AuftragTechnik2EndstelleBuilder().withRandomId().setPersist(false)).withRandomId().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();
        final HWRack networkRack = new HWRack();
        networkRack.setId(999L);
        networkRack.setHwProducer(1L);
        networkRack.setRackTyp(HWRack.RACK_TYPE_OLT);

        hwOltChild.setId(1000L);
        hwOltChild.setOltRackId(networkRack.getId());

        when(endstellenService.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(rangierungsService.findRangierung(endstelle.getRangierId())).thenReturn(rangierung);
        when(rangierungsService.findEquipment(rangierung.getEqInId())).thenReturn(eqInBuilder.build());
        when(hwService.findBaugruppe(port.getHwBaugruppenId())).thenReturn(baugruppe);
        when(hwService.findRackForBaugruppe(baugruppe.getId())).thenReturn(hwOltChild);
        when(hwService.findRackById(hwOltChild.getOltRackId())).thenReturn(networkRack);
        when(dslamService.findDslamProfile4AuftragOrCalculateDefault(auftragDaten.getAuftragId(), when)).thenReturn(new DSLAMProfileBuilder().withRandomId().setPersist(false).build());
        when(hvtService.findHVTTechnik(networkRack.getHwProducer())).thenReturn(new HVTTechnikBuilder().withRandomId().setPersist(false).build());
        when(hvtService.findHVTTechnik(hwOltChild.getHwProducer())).thenReturn(new HVTTechnikBuilder().withRandomId().setPersist(false).build());
        when(hvtService.findHVTGruppe4Standort(hwOltChild.getHvtIdStandort())).thenReturn(new HVTGruppe());
        when(hvtService.findHVTStandort(hwOltChild.getHvtIdStandort())).thenReturn(hvtStandortBuilder.build());
        when(referenceService.findReference(anyLong())).thenReturn(new ReferenceBuilder().withRandomId().withStrValue("LOCATION").setPersist(false).build());
        when(cpsFacade.findFttxPort(endstelle)).thenReturn(port);
        when(cpsFacade.findBaugruppeByPort(port)).thenReturn(baugruppe);
        when(cpsFacade.getEndstelleBWithStandortId(auftragDaten.getAuftragId())).thenReturn(endstelle);
        when(cpsFacade.findRackByPort(port)).thenReturn(hwOltChild);
    }

}
