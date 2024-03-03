/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2015 14:25
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CPSGetDSLDataCommandTest {

    @Mock
    CCAuftragService ccAuftragService;
    @Mock
    EndstellenService endstellenService;
    @Mock
    RangierungsService rangierungsService;
    @Mock
    HWService hwService;
    @Mock
    RegularExpressionService regularExpressionService;

    @Spy
    @InjectMocks
    private CPSGetDSLDataCommand cut;

    @BeforeMethod
    void setUp()    {
        cut = new CPSGetDSLDataCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFttcBondingSlavePorts() throws Exception {
        Date now = new Date();
        final String hwEqn = "000-001-02-03-04";
        final String slavePortExpected = "0-1-2-3-4";
        final AuftragDaten sdslAuftrag = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .withAuftragNoOrig(815L)
                .build();
        final AuftragDaten ndrahtOption = new AuftragDatenBuilder()
                .withRandomId()
                .withRandomAuftragId()
                .withAuftragNoOrig(sdslAuftrag.getAuftragNoOrig())
                .build();

        final HWBaugruppenTypBuilder bgTyp = new HWBaugruppenTypBuilder()
                .withRandomId()
                .withHwTypeName("test1234")
                .withBondingCapable(Boolean.TRUE);
        final HWBaugruppeBuilder baugruppe = new HWBaugruppeBuilder()
                .withRandomId()
                .withBaugruppenTypBuilder(bgTyp);
        final EquipmentBuilder equipment = new EquipmentBuilder()
                .withRandomId()
                .withBaugruppeBuilder(baugruppe);
        final RangierungBuilder rangierung = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(equipment);
        final Endstelle endstelleB = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangierung)
                .build();

        doReturn(new CPSTransactionBuilder().withEstimatedExecTime(now).setPersist(false).build()).when(cut).getCPSTransaction();
        doReturn(sdslAuftrag).when(cut).getAuftragDaten();
        when(ccAuftragService.findAllAuftragDaten4OrderNoOrigTx(sdslAuftrag.getAuftragNoOrig()))
                .thenReturn(Lists.newArrayList(ndrahtOption, sdslAuftrag));
        when(endstellenService.findEndstelle4Auftrag(ndrahtOption.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(endstelleB);
        when(rangierungsService.findRangierungTx(endstelleB.getRangierId())).thenReturn(rangierung.get());
        when(rangierungsService.findEquipment(rangierung.get().getEqInId())).thenReturn(equipment.get());
        when(hwService.findBaugruppe(baugruppe.get().getId())).thenReturn(baugruppe.get());
        when(regularExpressionService.match(bgTyp.get().getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.CPS_DSLAM_PORT, equipment.get().getHwEQN()))
                .thenReturn(hwEqn);

        doReturn(ImmutableSet.of(sdslAuftrag.getAuftragId(), ndrahtOption.getAuftragId())).when(cut).getOrderIDs4CPSTx();

        final List<String> result = cut.getFttcBondingSlavePorts();
        assertThat(Iterables.getOnlyElement(result), equalTo(slavePortExpected));
    }

    public void testMarshalling()   {
        final List<String> ports = Lists.newArrayList("1-2-3-4");
        final CPSDSLData.SlavePorts slavePorts = new CPSDSLData.SlavePorts(ports);
        final CPSDSLData cpsDslData = new CPSDSLData();
        cpsDslData.fttcBondingSlavePorts = slavePorts;

        XStreamMarshaller xsm = new XStreamMarshaller();
        xsm.setAnnotatedClasses(CPSDSLData.class);
        String xml = xsm.getXStream().toXML(cpsDslData);
        assertTrue(xml.contains("<SLAVE__PORTS>\n"
                + "    <SLAVE__PORT>1-2-3-4</SLAVE__PORT>\n"
                + "  </SLAVE__PORTS>"));
    }
}
