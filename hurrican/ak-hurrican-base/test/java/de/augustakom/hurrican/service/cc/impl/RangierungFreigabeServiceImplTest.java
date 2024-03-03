/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2013 17:05:48
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.SdslEquipmentService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;

/**
 * TestNG class for {@link RangierungFreigabeService}
 */
@Test(groups = { BaseTest.UNIT })
public class RangierungFreigabeServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private RangierungFreigabeServiceImpl cut;

    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private VlanService vlanServiceMock;
    @Mock
    private RangierungsService rangierungsServiceMock;
    @Mock
    private EQCrossConnectionService crossconnectionServiceMock;
    @Mock
    private SdslEquipmentService sdslEquipmentServiceMock;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testFreigebenRangierungAdsl() throws FindException, StoreException, DeleteException {
        RangierungBuilder rangierungBuilder = new RangierungBuilder();
        EquipmentBuilder eqInBuilder = new EquipmentBuilder().withManualConfiguration(Boolean.TRUE)
                .withRandomId().setPersist(false);
        Rangierung rangierung = rangierungBuilder.withEqInBuilder(eqInBuilder).withPhysikTypId(PhysikTyp
                .PHYSIKTYP_ADSL2P_HUAWEI).setPersist(false).build();
        ArgumentCaptor<Equipment> equipmentArgumentCaptor = ArgumentCaptor.forClass(Equipment.class);

        when(rangierungsServiceMock.findEquipment(rangierung.getEqInId())).thenReturn(eqInBuilder.get());

        cut.freigebenRangierung(rangierung, null, false);

        verify(rangierungsServiceMock).saveEquipment(equipmentArgumentCaptor.capture());
        Equipment eqIn = equipmentArgumentCaptor.getValue();
        assertThat(eqIn.getManualConfiguration(), equalTo(Boolean.FALSE));
        verify(rangierungsServiceMock).saveEquipment(any(Equipment.class));
        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
        verify(vlanServiceMock).removeEqVlans(eqInBuilder.get().getId());
        verify(crossconnectionServiceMock).deleteEQCrossConnectionsOfEquipment(eqInBuilder.get().getId());
    }

    @Test
    public void migrateAtm2EsmIfPossible() throws Exception {
        final RangierungBuilder rangierungBuilder = new RangierungBuilder();
        final EquipmentBuilder eqInBuilder = new EquipmentBuilder()
                .withSchicht2Protokoll(Schicht2Protokoll.ATM)
                .withRandomId();
        final Rangierung rangierung = rangierungBuilder
                .withEqInBuilder(eqInBuilder)
                .build();

        final List<Equipment> viererBlock = ImmutableList.of(
                eqInBuilder.get(),
                new EquipmentBuilder().withRandomId().withSchicht2Protokoll(Schicht2Protokoll.ATM).build()
        );

        when(sdslEquipmentServiceMock.findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(rangierung.getEqInId()))
                .thenReturn(viererBlock);
        when(rangierungsServiceMock.isListeOfPortsFree(viererBlock)).thenReturn(true);

        cut.migrateAtm2EfmIfPossible(rangierung);

        final ArgumentCaptor<Equipment> eqArgCaptor = ArgumentCaptor.forClass(Equipment.class);

        verify(rangierungsServiceMock, times(viererBlock.size())).saveEquipment(eqArgCaptor.capture());

        for (final Equipment migrated : viererBlock) {
            assertThat(migrated.getSchicht2Protokoll(), equalTo(Schicht2Protokoll.EFM));
            assertThat(eqArgCaptor.getAllValues().contains(migrated), is(true));
        }
    }

    @DataProvider
    public Object[][] freigebenRangierungDataProvider() {
        return new Object[][] {
                {PhysikTyp.PHYSIKTYP_FTTB_VDSL},
                {PhysikTyp.PHYSIKTYP_FTTB_DPO_VDSL},
                {PhysikTyp.PHYSIKTYP_FTTB_POTS},
                {PhysikTyp.PHYSIKTYP_FTTB_RF},
                {PhysikTyp.PHYSIKTYP_FTTH_ETH},
                {PhysikTyp.PHYSIKTYP_FTTH_POTS},
                {PhysikTyp.PHYSIKTYP_FTTH_RF},
        };
    }

    @Test(dataProvider = "freigebenRangierungDataProvider")
    public void testFreigebenRangierung(Long physikTyp) throws FindException, StoreException, DeleteException {
        EquipmentBuilder eqInBuilder = new EquipmentBuilder().withManualConfiguration(Boolean.TRUE)
                .withRandomId().withStatus(EqStatus.rang).setPersist(false);
        EquipmentBuilder eqOutBuilder = new EquipmentBuilder().withRandomId().setPersist(false);
        Rangierung rangierung = new RangierungBuilder().withEqOutBuilder(eqOutBuilder).withEqInBuilder(eqInBuilder)
                .withPhysikTypId(physikTyp).setPersist(false).build();
        ArgumentCaptor<Equipment> equipmentArgumentCaptor = ArgumentCaptor.forClass(Equipment.class);

        when(rangierungsServiceMock.findEquipment(rangierung.getEqInId())).thenReturn(eqInBuilder.get());

        String bemerkung = "Das ist eine Bemerkung";
        cut.freigebenRangierung(rangierung, bemerkung, false);

        verify(rangierungsServiceMock).saveEquipment(equipmentArgumentCaptor.capture());
        Equipment eqIn = equipmentArgumentCaptor.getValue();
        assertThat(eqIn.getStatus(), equalTo(EqStatus.rang));
        assertThat(eqIn.getManualConfiguration(), equalTo(Boolean.FALSE));
        assertNull(rangierung.getEqOutId());
        assertEquals(rangierung.getBemerkung(), bemerkung);
        assertNull(rangierung.getFreigabeAb());
        assertNull(rangierung.getEsId());
        assertThat(rangierung.getGueltigBis(), equalTo(DateTools.getHurricanEndDate()));
        assertThat(rangierung.getFreigegeben(), equalTo(Freigegeben.freigegeben));
        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
        verify(vlanServiceMock).removeEqVlans(eqInBuilder.get().getId());
        verify(crossconnectionServiceMock).deleteEQCrossConnectionsOfEquipment(eqInBuilder.get().getId());
    }

    @Test
    public void testFreigebenRangierungFttH() throws FindException, StoreException, DeleteException {
        final Date now = new Date();
        RangierungBuilder rangierungBuilder = new RangierungBuilder();
        EquipmentBuilder eqInBuilder = new EquipmentBuilder().withManualConfiguration(Boolean.TRUE)
                .withRandomId().setPersist(false);
        Rangierung rangierung = rangierungBuilder.withEqInBuilder(eqInBuilder)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_FTTH).withFreigabeAb(now).setPersist(false).build();
        ArgumentCaptor<Equipment> equipmentArgumentCaptor = ArgumentCaptor.forClass(Equipment.class);

        when(rangierungsServiceMock.findEquipment(rangierung.getEqInId())).thenReturn(eqInBuilder.get());

        cut.freigebenRangierung(rangierung, null, false);

        verify(rangierungsServiceMock, times(2)).saveEquipment(equipmentArgumentCaptor.capture());
        Equipment eqIn = equipmentArgumentCaptor.getValue();
        assertThat(eqIn.getManualConfiguration(), equalTo(Boolean.FALSE));
        assertThat(eqIn.getStatus(), equalTo(EqStatus.locked));
        assertThat(eqIn.getGueltigBis(), equalTo(now));
        assertThat(rangierung.getGueltigBis(), equalTo(now));
        assertThat(rangierung.getFreigegeben(), equalTo(Freigegeben.gesperrt));
        assertNull(rangierung.getEsId());
        assertNull(rangierung.getFreigabeAb());
        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
    }

    @Test
    public void testFreigebenRangierungBreakRangierung() throws FindException, StoreException, DeleteException, ValidationException {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withRandomId()
                .withBreakRangierung(Boolean.TRUE).setPersist(false);
        RangierungBuilder rangierungBuilder = new RangierungBuilder();
        EquipmentBuilder eqOutBuilder = new EquipmentBuilder().withRandomId().setPersist(false);
        Rangierung rangierung = rangierungBuilder.withEqOutBuilder(eqOutBuilder)
                .withPhysikTypId(PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI)
                .withHvtStandortBuilder(hvtStandortBuilder).setPersist(false).build();

        when(rangierungsServiceMock.findEquipment(rangierung.getEqOutId())).thenReturn(eqOutBuilder.get());
        when(rangierungsServiceMock.breakRangierung(rangierung, false, true, true)).thenReturn(rangierung);
        when(hvtServiceMock.findHVTStandort(rangierung.getHvtIdStandort())).thenReturn(
                rangierungBuilder.getHvtStandortBuilder().get());

        cut.freigebenRangierung(rangierung, null, false);

        verify(rangierungsServiceMock).breakRangierung(rangierung, false, true, true);
    }

}
