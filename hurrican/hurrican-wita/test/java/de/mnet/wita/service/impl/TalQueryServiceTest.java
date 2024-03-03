/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static com.google.common.collect.ImmutableList.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.inject.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = BaseTest.SERVICE)
public class TalQueryServiceTest extends AbstractServiceTest {

    private static final String RANG_VERTEILER = "0001";
    private static final String RANG_LEISTE1 = "01";
    private static final String RANG_STIFT1 = "01";
    private static final String DTAG_PORT = RANG_VERTEILER + " " + RANG_LEISTE1 + " " + RANG_STIFT1;

    private static final String EXT_AUFTRAGSNUMMER = "testetest123";

    private TalQueryServiceImpl underTest;

    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private PhysikService physikService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private CarrierElTALService carrierElTALService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private Provider<WitaCBVorgangBuilder> witaCbVorgangBuilder;
    @Autowired
    private Provider<CBVorgangBuilder> cbVorgangBuilder;
    @Autowired
    private Provider<CarrierbestellungBuilder> carrierbestellungBuilder;
    @Autowired
    private Provider<Carrierbestellung2EndstelleBuilder> cb2EsBuilder;
    @Autowired
    private Provider<AuftragTechnikBuilder> auftragTechnikBuilder;
    @Autowired
    private Provider<AuftragDatenBuilder> auftragDatenBuilder;
    @Autowired
    private Provider<AuftragBuilder> auftragBuilder;
    @Autowired
    private Provider<EndstelleBuilder> endstelleBuilder;
    @Autowired
    private Provider<NiederlassungBuilder> niederlassungBuilder;
    @Autowired
    private Provider<RangierungBuilder> rangierungBuilder;
    @Autowired
    private Provider<EquipmentBuilder> eqOutBuilder;

    private List<TamVorgang> createdTams;
    private CommonWorkflowService commonWorkflowService;

    private Carrierbestellung2EndstelleBuilder currentCb2EsBuilder;

    @BeforeMethod
    public void reset() {
        // No autowiring possible as witaUsertaskService and commonWorkflowService should be mocked!
        underTest = new TalQueryServiceImpl();
        underTest.auftragService = auftragService;
        underTest.carrierService = carrierService;
        underTest.carrierElTalService = carrierElTALService;
        underTest.endstellenService = endstellenService;
        underTest.physikService = physikService;
        underTest.rangierungsService = rangierungsService;

        createdTams = Lists.newArrayList();

        WitaUsertaskService witaUsertaskService = mock(WitaUsertaskService.class);
        underTest.witaUsertaskService = witaUsertaskService;
        when(witaUsertaskService.findOpenTamTasks()).thenReturn(createdTams);

        commonWorkflowService = mock(CommonWorkflowService.class);
        underTest.commonWorkflowService = commonWorkflowService;
        when(commonWorkflowService.isProcessInstanceAlive(anyString())).thenReturn(true);
    }

    public void testHasNoEsaaOrInterneCBVorgaenge() throws FindException {
        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().build();
        assertNotNull(carrierbestellung.getId());

        witaCbVorgangBuilder.get().withCbId(carrierbestellung.getId()).build();

        assertFalse(underTest.hasEsaaOrInterneCBVorgaenge(carrierbestellung.getId()));
    }

    public void noPossibleSubOrdersForErlmk() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDatenAndTam();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        List<TalSubOrder> subOrdersForErlmk = underTest.findPossibleSubOrdersForErlmk(cbVorgang, auftragDaten);
        assertNotNull(subOrdersForErlmk);
        assertThat(subOrdersForErlmk, hasSize(1));
    }

    public void possibleSubOrdersForErlmk() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDatenAndTam();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag
        AuftragDaten subAuftragDaten = createCarrierbestellungWithAuftragDatenAndTam(auftragDaten.getAuftragNoOrig())
                .getSecond();
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForErlmk(cbVorgang, auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten, subAuftragDaten);
    }

    public void possibleSubOrdersForErlmkWithoutTam() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDatenAndTam();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag but without tam
        createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), true).getSecond();
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForErlmk(cbVorgang, auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten);
    }

    public void noPossibleSubOrdersForStorno() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with different taifun auftrag
        createCbVorgangWithAuftragDaten();

        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForStorno(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten);
    }

    public void possibleSubOrdersForStorno() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag
        AuftragDaten subAuftragDaten = createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), true)
                .getSecond();
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForStorno(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten, subAuftragDaten);
    }

    public void possibleSubOrdersForStornoWithMultipleVorgaenge() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        WitaCBVorgang cbVorgang2 = createSecondCbVorgang(auftragDaten);

        ImmutableList<WitaCBVorgang> cbVorgangList = of(cbVorgang, cbVorgang2);
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForStorno(cbVorgangList, auftragDaten);

        assertTalSubOrdersCorrect(subOrders, auftragDaten, cbVorgangList);
    }

    public void possibleSubOrdersForStornoWithoutCbVorgang() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag
        createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), false);
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForStorno(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten);
    }

    public void noPossibleSubOrdersForTerminverschiebung() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with different taifun auftrag
        createCbVorgangWithAuftragDaten();

        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForTerminverschiebung(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten);
    }

    public void possibleSubOrdersForTerminverschiebung() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag
        AuftragDaten subAuftragDaten = createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), true)
                .getSecond();
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForTerminverschiebung(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten, subAuftragDaten);
    }

    public void possibleSubOrdersForTerminverschiebungWithoutCbVorgang() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag but without CbVorgang
        createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), false).getSecond();
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForTerminverschiebung(of(cbVorgang), auftragDaten);

        assertInSubOrderList(subOrders, auftragDaten);
    }

    public void possibleSubOrdersForTerminverschiebungWithStatus() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        // Create suborder with same taifun Auftrag in state STORNO
        createCarrierbestellungWithAuftragDaten(auftragDaten.getAuftragNoOrig(), AuftragStatus.STORNO);

        flushAndClear();

        List<TalSubOrder> result = underTest.findPossibleSubOrdersForTerminverschiebung(of(cbVorgang), auftragDaten);
        assertInSubOrderList(result, auftragDaten);
    }

    public void possibleSubOrdersForTerminverschiebungWithMultipleVorgaenge() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        WitaCBVorgang cbVorgang2 = createSecondCbVorgang(auftragDaten);

        ImmutableList<WitaCBVorgang> cbVorgangList = of(cbVorgang, cbVorgang2);
        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForTerminverschiebung(cbVorgangList, auftragDaten);

        assertTalSubOrdersCorrect(subOrders, auftragDaten, cbVorgangList);
    }

    public void possibleSubOrdersForTerminverschiebungWithEsaaVorgaenge() throws Exception {
        Pair<WitaCBVorgang, AuftragDaten> cbVorgangWithAuftragDaten = createCbVorgangWithAuftragDaten();
        WitaCBVorgang cbVorgang = cbVorgangWithAuftragDaten.getFirst();
        AuftragDaten auftragDaten = cbVorgangWithAuftragDaten.getSecond();

        Carrierbestellung cb2 = carrierbestellungBuilder.get().withCb2EsBuilder(currentCb2EsBuilder).build();
        cbVorgangBuilder.get().withCarrierRefNr("123xx").withTyp(CBVorgang.TYP_NEU)
                .withAuftragId(auftragDaten.getAuftragId()).withCbId(cb2.getId()).build();
        cbVorgangBuilder.get().withCarrierRefNr(null).withTyp(CBVorgang.TYP_STORNO)
                .withAuftragId(auftragDaten.getAuftragId()).withCbId(cb2.getId()).build();
        when(commonWorkflowService.isProcessInstanceAlive("123xx")).thenReturn(false);

        List<TalSubOrder> subOrders = underTest.findPossibleSubOrdersForTerminverschiebung(of(cbVorgang), auftragDaten);

        assertTalSubOrdersCorrect(subOrders, auftragDaten, of(cbVorgang));
    }

    private void assertTalSubOrdersCorrect(List<TalSubOrder> subOrders, AuftragDaten auftragDaten,
            ImmutableList<WitaCBVorgang> cbVorgangList) throws FindException {
        Iterator<TalSubOrder> it = subOrders.iterator();
        for (WitaCBVorgang cbVorgangToCheck : cbVorgangList) {
            TalSubOrder talSubOrder = it.next();
            assertThat(talSubOrder.getAuftragId(), equalTo(auftragDaten.getAuftragId()));
            assertThat(talSubOrder.getCbVorgangId(), equalTo(cbVorgangToCheck.getId()));
            assertThat(talSubOrder.getDtagPort(), equalTo(DTAG_PORT));
            assertThat(talSubOrder.getExterneAuftragsnummer(), equalTo(cbVorgangToCheck.getCarrierRefNr()));
            assertThat(talSubOrder.getSelected(), equalTo(true));
            assertThat(talSubOrder.getVbz(),
                    equalTo(physikService.findVerbindungsBezeichnungByAuftragId(talSubOrder.getAuftragId()).getVbz()));
        }
        assertFalse(it.hasNext());
    }

    private void assertInSubOrderList(final List<TalSubOrder> subOrderList, AuftragDaten... auftragDaten) {
        assertNotNull(subOrderList);
        assertThat(subOrderList, hasSize(auftragDaten.length));

        for (AuftragDaten auftrag : auftragDaten) {
            boolean found = false;
            for (TalSubOrder talSubOrder : subOrderList) {
                if (NumberTools.equal(auftrag.getAuftragId(), talSubOrder.getAuftragId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("AuftragDaten not found in SubOrderList:" + auftrag);
            }
        }
    }

    private Pair<Carrierbestellung, AuftragDaten> createCarrierbestellungWithAuftragDaten(Long auftragNoOrig,
            Long auftragStatus, boolean createCbVorgang, boolean createTam) throws Exception {
        Carrierbestellung carrierbestellung = createCarrierbestellung(auftragNoOrig, auftragStatus, createCbVorgang,
                createTam);
        return new Pair<>(carrierbestellung, getAuftragDaten(carrierbestellung));
    }

    private AuftragDaten getAuftragDaten(Carrierbestellung carrierbestellung) throws Exception {
        Endstelle endstelle = Iterables.getOnlyElement(endstellenService
                .findEndstellen4Carrierbestellung(carrierbestellung));
        return auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
    }

    private Pair<Carrierbestellung, AuftragDaten> createCarrierbestellungWithAuftragDatenAndTam(Long auftragNoOrig)
            throws Exception {
        return createCarrierbestellungWithAuftragDaten(auftragNoOrig, AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, true, true);
    }

    private Pair<WitaCBVorgang, AuftragDaten> createCbVorgangWithAuftragDatenAndTam() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbWithAuftragDaten = createCarrierbestellungWithAuftragDatenAndTam(EntityBuilder
                .getLongId());

        WitaCBVorgang cbVorgang = (WitaCBVorgang) Iterables.getOnlyElement(carrierElTALService
                .findCBVorgaenge4CB(cbWithAuftragDaten.getFirst().getId()));

        return Pair.create(cbVorgang, cbWithAuftragDaten.getSecond());

    }

    private Pair<Carrierbestellung, AuftragDaten> createCarrierbestellungWithAuftragDaten(Long auftragNoOrig,
            Long auftragStatus, boolean createCbVorgang) throws Exception {
        return createCarrierbestellungWithAuftragDaten(auftragNoOrig, auftragStatus, createCbVorgang, false);
    }

    private Pair<Carrierbestellung, AuftragDaten> createCarrierbestellungWithAuftragDaten(Long auftragNoOrig,
            Long auftragStatus) throws Exception {
        return createCarrierbestellungWithAuftragDaten(auftragNoOrig, auftragStatus, true);
    }

    private Pair<Carrierbestellung, AuftragDaten> createCarrierbestellungWithAuftragDaten(Long auftragNoOrig,
            boolean createCbVorgang) throws Exception {
        return createCarrierbestellungWithAuftragDaten(auftragNoOrig, AuftragStatus.AUS_TAIFUN_UEBERNOMMEN,
                createCbVorgang);
    }

    private Pair<WitaCBVorgang, AuftragDaten> createCbVorgangWithAuftragDaten() throws Exception {
        Pair<Carrierbestellung, AuftragDaten> cbWithAuftragDaten = createCarrierbestellungWithAuftragDaten(
                EntityBuilder.getLongId(), AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, true);
        WitaCBVorgang cbVorgang = (WitaCBVorgang) Iterables.getOnlyElement(carrierElTALService
                .findCBVorgaenge4CB(cbWithAuftragDaten.getFirst().getId()));

        return Pair.create(cbVorgang, cbWithAuftragDaten.getSecond());
    }

    private Carrierbestellung createCarrierbestellung(Long auftragNoOrig, Long auftragStatus,
            boolean createCbVorgang, boolean createTam) throws Exception {
        currentCb2EsBuilder = cb2EsBuilder.get();

        Endstelle endstelle = endstelleBuilder
                .get()
                .withRangierungBuilder(
                        rangierungBuilder.get().withEqOutBuilder(
                                eqOutBuilder.get().withRangVerteiler(RANG_VERTEILER).withRangLeiste1(RANG_LEISTE1)
                                        .withRangStift1(RANG_STIFT1)
                        )
                )
                .withAuftragTechnikBuilder(
                        auftragTechnikBuilder
                                .get()
                                .withNiederlassungBuilder(niederlassungBuilder.get())
                                .withAuftragBuilder(
                                        auftragBuilder.get().withAuftragDatenBuilder(
                                                auftragDatenBuilder.get().withAuftragNoOrig(auftragNoOrig)
                                                        .withStatusId(auftragStatus)
                                        )
                                )
                )
                .withCb2EsBuilder(currentCb2EsBuilder).build();

        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().withCb2EsBuilder(currentCb2EsBuilder)
                .build();

        assertNotNull(carrierbestellung.getId());
        assertEquals(carrierbestellung.getCb2EsId(), endstelle.getCb2EsId());

        if (createCbVorgang) {
            WitaCBVorgang cbVorgang = witaCbVorgangBuilder.get()
                    .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG).withCarrierRefNr(EXT_AUFTRAGSNUMMER)
                    .withAuftragId(getAuftragDaten(carrierbestellung).getAuftragId())
                    .withCbId(carrierbestellung.getId()).build();

            if (createTam) {
                TerminAnforderungsMeldung tam = new TerminAnforderungsMeldungBuilder().withExterneAuftragsnummer(
                        cbVorgang.getCarrierRefNr()).build();
                mwfEntityDao.store(tam);
                createdTams.add(new TamVorgang(cbVorgang, tam, 1, "", auftragNoOrig, "323/23434/232", false, false));
            }
        }
        return carrierbestellung;
    }

    private WitaCBVorgang createSecondCbVorgang(AuftragDaten auftragDaten) {
        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().withCb2EsBuilder(currentCb2EsBuilder)
                .build();

        assertNotNull(carrierbestellung.getId());

        return witaCbVorgangBuilder.get()
                .withWitaGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG)
                .withCarrierRefNr(EXT_AUFTRAGSNUMMER + "_distinct").withAuftragId(auftragDaten.getAuftragId())
                .withCbId(carrierbestellung.getId()).build();
    }

}
