/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = BaseTest.UNIT)
public class TalQueryServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private TalQueryServiceImpl underTest;

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private CarrierElTALService carrierElTALService;
    @Mock
    private CCAuftragService auftragService;
    @SuppressWarnings("unused")
    @Mock
    private PhysikService physikService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private WitaUsertaskService witaUsertaskService;
    @Mock
    private CommonWorkflowService commonWorkflowService;

    @BeforeMethod
    public void setup() {
        underTest = new TalQueryServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    public void getDtagPortOfSubOrder_EndstelleIsNull_ReturnsNull() throws FindException {
        Endstelle endstelle = null;

        String result = underTest.getDtagPortOfSubOrder(endstelle);

        assertEquals(result, null);
    }

    public void getDtagPortOfSubOrder_EndstelleHasNoRangierId_ReturnsNull() throws FindException {
        Endstelle endstelle = new Endstelle();

        String result = underTest.getDtagPortOfSubOrder(endstelle);

        assertEquals(result, null);
    }

    public void getDtagPortOfSubOrder_EndstelleNotNullAndHasRangierIdDtagEqIsNull_ReturnsNull() throws FindException {
        Endstelle endstelle = new Endstelle();
        endstelle.setRangierId(1000L);

        Rangierung rangierung = new Rangierung();
        rangierung.setEqOutId(1000L);
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);
        when(rangierungsService.findEquipment(anyLong())).thenReturn(null);

        String result = underTest.getDtagPortOfSubOrder(endstelle);

        assertEquals(result, null);
    }

    public void getDtagPortOfSubOrder_EndstelleNotNullAndHasRangierIdDtagEqIsNotNull_ReturnsDtagVerteilerLeisteStift() throws FindException {
        Endstelle endstelle = new Endstelle();
        endstelle.setRangierId(1000L);

        Rangierung rangierung = new Rangierung();
        rangierung.setEqOutId(1000L);
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);

        Equipment equipment = new Equipment();
        equipment.setRangStift1("stift1");
        when(rangierungsService.findEquipment(anyLong())).thenReturn(equipment);

        String result = underTest.getDtagPortOfSubOrder(endstelle);

        assertThat(result, containsString("stift1"));
    }

    public void findPossibleSubOrdersForErlmk() throws FindException {
        Endstelle endstelle = createMockedEndstelle();
        AuftragDaten auftragDaten = createMockedAuftragDaten(endstelle);
        Carrierbestellung carrierbestellung = createMockedCarrierbestellung(endstelle);
        WitaCBVorgang cbVorgang = createMockedCbVorgangWithRunningProcessInstance(carrierbestellung);
        createMockedTamVorgang(carrierbestellung, cbVorgang);

        List<TalSubOrder> subOrdersForErlmk = underTest.findPossibleSubOrdersForErlmk(cbVorgang, auftragDaten);
        verify(underTest).findPossibleSubOrders(cbVorgang.getCbId(), null, auftragDaten,
                Predicates.<AuftragDaten> alwaysTrue(), true);
        assertNotNull(subOrdersForErlmk);
        assertThat(subOrdersForErlmk, hasSize(1));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRexMkAndNonRexMkStorno() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        WitaCBVorgang cbVorgang2 = new WitaCBVorgang();

        underTest.findPossibleSubOrdersForStorno(ImmutableList.of(cbVorgang,
                        cbVorgang2),
                auftragDaten
        );
    }

    public void testRexMkStorno() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(56758768L);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(123L);
        cbVorgang.setCarrierRefNr("12345");
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        List<TalSubOrder> subOrdersForStorno = underTest.findPossibleSubOrdersForStorno(ImmutableList.of(cbVorgang),
                auftragDaten);
        assertTalSubOrderCorrect(Iterables.getOnlyElement(subOrdersForStorno), auftragDaten, cbVorgang);
    }

    public void testRexMkStornoForMultipleVorgaenge() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(56758768L);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(123L);
        cbVorgang.setCarrierRefNr("12345");
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        WitaCBVorgang cbVorgang2 = new WitaCBVorgang();
        cbVorgang2.setId(1234L);
        cbVorgang2.setCarrierRefNr("123456");
        cbVorgang2.setTyp(CBVorgang.TYP_REX_MK);

        List<TalSubOrder> subOrdersForStorno = underTest.findPossibleSubOrdersForStorno(ImmutableList.of(cbVorgang,
                cbVorgang2), auftragDaten);

        Iterator<TalSubOrder> it = subOrdersForStorno.iterator();
        assertTalSubOrderCorrect(it.next(), auftragDaten, cbVorgang);
        assertTalSubOrderCorrect(it.next(), auftragDaten, cbVorgang2);
        assertFalse(it.hasNext());
    }

    public void testNeuStorno() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(56758768L);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(123L);
        cbVorgang.setCbId(12L);
        cbVorgang.setCarrierRefNr("12345");
        cbVorgang.setAuftragsKlammer(6789L);
        cbVorgang.setTyp(CBVorgang.TYP_NEU);

        final TalSubOrder talSubOrder = new TalSubOrder(auftragDaten.getAuftragId(), null, null, cbVorgang.getId(), cbVorgang.getCarrierRefNr());
        doReturn(Arrays.asList(talSubOrder)).when(underTest)
                .findPossibleSubOrders(cbVorgang.getCbId(), cbVorgang.getAuftragsKlammer(), auftragDaten,
                        Predicates.<AuftragDaten> alwaysTrue(), false);

        List<TalSubOrder> subOrdersForStorno = underTest.findPossibleSubOrdersForStorno(ImmutableList.of(cbVorgang),
                auftragDaten);
        verify(underTest).findPossibleSubOrders(cbVorgang.getCbId(), cbVorgang.getAuftragsKlammer(), auftragDaten,
                Predicates.<AuftragDaten>alwaysTrue(), false);
        assertTalSubOrderCorrect(Iterables.getOnlyElement(subOrdersForStorno), auftragDaten, cbVorgang);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRexMkAndNonRexMkTerminverschiebung() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        WitaCBVorgang cbVorgang2 = new WitaCBVorgang();

        underTest.findPossibleSubOrdersForTerminverschiebung(ImmutableList.of(cbVorgang,
                        cbVorgang2),
                auftragDaten
        );
    }

    public void testRexMkTerminverschiebung() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(56758768L);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(123L);
        cbVorgang.setCarrierRefNr("12345");
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        List<TalSubOrder> subOrdersForTerminverschiebung = underTest.findPossibleSubOrdersForTerminverschiebung(ImmutableList.of(cbVorgang),
                auftragDaten);
        assertTalSubOrderCorrect(Iterables.getOnlyElement(subOrdersForTerminverschiebung), auftragDaten, cbVorgang);
    }

    public void testRexMkTerminverschiebungForMultipleVorgaenge() throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(56758768L);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(123L);
        cbVorgang.setCarrierRefNr("12345");
        cbVorgang.setTyp(CBVorgang.TYP_REX_MK);

        WitaCBVorgang cbVorgang2 = new WitaCBVorgang();
        cbVorgang2.setId(1234L);
        cbVorgang2.setCarrierRefNr("123456");
        cbVorgang2.setTyp(CBVorgang.TYP_REX_MK);

        List<TalSubOrder> subOrdersForTerminverschiebung = underTest.findPossibleSubOrdersForTerminverschiebung(ImmutableList.of(cbVorgang,
                cbVorgang2), auftragDaten);

        Iterator<TalSubOrder> it = subOrdersForTerminverschiebung.iterator();
        assertTalSubOrderCorrect(it.next(), auftragDaten, cbVorgang);
        assertTalSubOrderCorrect(it.next(), auftragDaten, cbVorgang2);
        assertFalse(it.hasNext());
    }


    private void assertTalSubOrderCorrect(TalSubOrder talSubOrder, AuftragDaten auftragDaten, WitaCBVorgang cbVorgang) {
        assertThat(talSubOrder.getAuftragId(), equalTo(auftragDaten.getAuftragId()));
        assertThat(talSubOrder.getCbVorgangId(), equalTo(cbVorgang.getId()));
        assertThat(talSubOrder.getDtagPort(), nullValue());
        assertThat(talSubOrder.getExterneAuftragsnummer(), equalTo(cbVorgang.getCarrierRefNr()));
        assertThat(talSubOrder.getSelected(), equalTo(true));
        assertThat(talSubOrder.getVbz(), nullValue());
    }

    public void findEndstelleForWitaCBVorgang() throws FindException {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(
                GeschaeftsfallTyp.BEREITSTELLUNG).withAuftragId(123L).withCbId(4536L).build();

        Carrierbestellung carrierbestellung = new Carrierbestellung();
        carrierbestellung.setCb2EsId(4256874L);
        Endstelle endstelle = new Endstelle();
        endstelle.setCb2EsId(carrierbestellung.getCb2EsId());

        when(carrierService.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenService.findEndstellen4Auftrag(cbVorgang.getAuftragId())).thenReturn(
                ImmutableList.<Endstelle>of(endstelle));

        assertNotNull(underTest.findEndstelleForCBVorgang(cbVorgang));
    }

    public void findEndstelleForRexmk() throws FindException {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withWitaGeschaeftsfallTyp(
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG).withAuftragId(123L).build();

        when(endstellenService.findEndstelle4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(
                new Endstelle());

        assertNotNull(underTest.findEndstelleForCBVorgang(cbVorgang));
    }

    public void findPossibleSubOrders() throws FindException {
        Endstelle endstelle = createMockedEndstelle();
        AuftragDaten auftragDaten1 = createMockedAuftragDaten(endstelle);
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .setPersist(false).build();
        AuftragDaten auftragDaten3 = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .setPersist(false).build();

        Long klammer = 567L;
        WitaCBVorgang cbv1 = new WitaCBVorgangBuilder().withAuftragId(auftragDaten1.getAuftragId()).withCbId(1L)
                .withAuftragsKlammer(klammer).setPersist(false).build();
        WitaCBVorgang cbv2 = new WitaCBVorgangBuilder().withAuftragId(auftragDaten2.getAuftragId())
                .withAuftragsKlammer(klammer).setPersist(false).build();
        WitaCBVorgang cbv3 = new WitaCBVorgangBuilder().withAuftragId(auftragDaten3.getAuftragId())
                .withAuftragsKlammer(klammer).setPersist(false).build();

        List<AuftragDaten> subOrders = new ArrayList<>();
        subOrders.add(auftragDaten1);
        subOrders.add(auftragDaten2);

        // HUR-Auftraege zum gleichen TAI-Auftrag
        when(auftragService.findAuftragDaten4OrderNoOrigTx(anyLong())).thenReturn(subOrders);

        // zusaetzlicher Auftrag, der ueber WITA-Klammer ermittelt wird (kann auch anderer TAI-Auftrag sein)
        when(carrierElTALService.findCBVorgaengeByExample(any(WitaCBVorgang.class))).thenReturn(
                Arrays.asList(cbv1, cbv2, cbv3));

        when(auftragService.findAuftragDatenByAuftragIdTx(auftragDaten3.getAuftragId())).thenReturn(auftragDaten3);
        when(endstellenService.findEndstelle4CarrierbestellungAndAuftrag(cbv1.getCbId(), cbv1.getAuftragId()))
                .thenReturn(endstelle);

        TalSubOrder talSubOrder1 = new TalSubOrder(auftragDaten1.getAuftragId(), "", "", cbv1.getCbVorgangRefId(), "");
        TalSubOrder talSubOrder2 = new TalSubOrder(auftragDaten2.getAuftragId(), "", "", cbv1.getCbVorgangRefId(), "");
        TalSubOrder talSubOrder3 = new TalSubOrder(auftragDaten3.getAuftragId(), "", "", cbv1.getCbVorgangRefId(), "");

        doReturn(Arrays.asList(talSubOrder1))
                .when(underTest).getSubOrderForSelection(Endstelle.ENDSTELLEN_TYP_B, auftragDaten1, true);
        doReturn(Arrays.asList(talSubOrder2))
                .when(underTest).getSubOrderForSelection(Endstelle.ENDSTELLEN_TYP_B, auftragDaten2, true);
        doReturn(Arrays.asList(talSubOrder3))
                .when(underTest).getSubOrderForSelection(Endstelle.ENDSTELLEN_TYP_B, auftragDaten3, true);

        List<TalSubOrder> result = underTest.findPossibleSubOrders(1L, klammer, auftragDaten1,
                Predicates.<AuftragDaten> alwaysTrue(), true);
        assertNotEmpty(result);
        assertEquals(result.size(), 3);
        assertTrue(result.contains(talSubOrder1));
        assertTrue(result.contains(talSubOrder2));
        assertTrue(result.contains(talSubOrder3));

        verify(carrierElTALService, times(1)).findCBVorgaengeByExample(any(WitaCBVorgang.class));
        verify(underTest, times(3)).getSubOrderForSelection(anyString(), any(AuftragDaten.class), anyBoolean());
    }
    

    private void createMockedTamVorgang(Carrierbestellung carrierbestellung, WitaCBVorgang cbVorgang) {
        TamVorgang tamVorgang = new TamVorgang();
        tamVorgang.setCbId(carrierbestellung.getId());
        tamVorgang.setCbVorgang(cbVorgang);
        when(witaUsertaskService.findOpenTamTasks()).thenReturn(ImmutableList.of(tamVorgang));
    }

    private WitaCBVorgang createMockedCbVorgangWithRunningProcessInstance(Carrierbestellung carrierbestellung)
            throws FindException {
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setCbId(carrierbestellung.getId());
        cbVorgang.setCarrierRefNr("123");

        when(carrierElTALService.findCBVorgaenge4CB(carrierbestellung.getId())).thenReturn(
                ImmutableList.<CBVorgang>of(cbVorgang));

        when(commonWorkflowService.isProcessInstanceAlive(cbVorgang.getCarrierRefNr())).thenReturn(true);

        return cbVorgang;
    }

    private Carrierbestellung createMockedCarrierbestellung(Endstelle endstelle) throws FindException {
        Carrierbestellung carrierbestellung = new Carrierbestellung();
        carrierbestellung.setId(12334L);
        carrierbestellung.setCb2EsId(endstelle.getCb2EsId());

        when(carrierService.findCB(carrierbestellung.getId())).thenReturn(carrierbestellung);
        when(carrierService.findCBsTx(carrierbestellung.getCb2EsId())).thenReturn(ImmutableList.of(carrierbestellung));
        when(endstellenService.findEndstelle4CarrierbestellungAndAuftrag(eq(carrierbestellung.getId()), anyLong()))
                .thenReturn(endstelle);
        return carrierbestellung;
    }

    private AuftragDaten createMockedAuftragDaten(Endstelle endstelle) throws FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragNoOrig(123L);
        auftragDaten.setAuftragId(13454L);

        AuftragDaten subOrderAuftragDaten = new AuftragDaten();
        subOrderAuftragDaten.setAuftragId(auftragDaten.getAuftragId() + 1);

        when(auftragService.findAuftragDaten4OrderNoOrigTx(auftragDaten.getAuftragNoOrig())).thenReturn(
                ImmutableList.of(subOrderAuftragDaten));
        when(endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId())).thenReturn(
                ImmutableList.of(endstelle));
        when(endstellenService.findEndstelle4Auftrag(eq(subOrderAuftragDaten.getAuftragId()), anyString())).thenReturn(
                endstelle);
        return auftragDaten;
    }

    private Endstelle createMockedEndstelle() throws FindException {
        Endstelle endstelle = new Endstelle();
        endstelle.setCb2EsId(43563567L);
        endstelle.setRangierId(3246L);
        endstelle.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        when(rangierungsService.findRangierung(endstelle.getRangierId())).thenReturn(new Rangierung());

        Equipment equipment = new Equipment();
        equipment.setHwEQN("hwEqn");
        when(rangierungsService.findEquipment(anyLong())).thenReturn(equipment);

        return endstelle;
    }

    
}
