/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 14:57:44
 */
package de.mnet.wita.dao.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.AkmPvUserTaskBuilder;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamUserTaskBuilder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = SERVICE)
public class TaskDaoImplTest extends AbstractServiceTest {

    private static class EqualTaskIdPredicate implements Predicate<AbgebendeLeitungenVorgang> {
        private final AbgebendeLeitungenUserTask userTask;

        private EqualTaskIdPredicate(AbgebendeLeitungenUserTask userTask) {
            this.userTask = userTask;
        }

        @Override
        public boolean apply(AbgebendeLeitungenVorgang input) {

            return input.getUserTask().getId().equals(userTask.getId());
        }
    }

    @Autowired
    private TaskDaoImpl taskDao;
    @Autowired
    private Provider<WitaCBVorgangBuilder> cbVorgangBuilder;
    @Autowired
    private Provider<TamUserTaskBuilder> tamUserTaskBuilder;
    @Autowired
    private Provider<AkmPvUserTaskBuilder> akmPvUserTaskBuilder;
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
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private CarrierElTALService carrierElTALService;
    @Autowired
    private WitaUsertaskService witaUsertaskService;

    private WitaCBVorgang witaCBVorgang;
    private TerminAnforderungsMeldung tam;

    public void findTamVorgaengeTest() {
        WitaCBVorgang witaCbVorgang = buildVorgangWithTam();
        List<TamVorgang> tasks = taskDao.findOpenTamTasksWithWiedervorlage();
        Assert.assertTrue(tasks.size() >= 1);
        for (TamVorgang task : tasks) {
            if (task.getCbVorgang().equals(witaCbVorgang)) {
                assertNotNull(task.getAuftragNoOrig());
                assertNotNull(task.getNiederlassung());
                return;
            }
        }
        Assert.fail("Der im Test erstellte Vorgang konnte nicht gefunden werden.");
    }

    public void findTamVorgaengeWithWiedervorlageTest() {
        WitaCBVorgang witaCbVorgang = buildVorgangWithTamAndWiedervorlage();
        List<TamVorgang> tasks = taskDao.findOpenTamTasksWithWiedervorlage();
        Assert.assertTrue(tasks.size() >= 0);
        for (TamVorgang task : tasks) {
            Assert.assertFalse(task.getCbVorgang().equals(witaCbVorgang));
        }
    }

    public void doNotFindAbgebendeLeitungeUserTask() {
        AkmPvUserTask akmPv = akmPvUserTaskBuilder.get().withAkmPvStatus(AkmPvStatus.ABM_PV_EMPFANGEN)
                .withWiedervorlageAm(LocalDateTime.now().plusMinutes(2)).build();

        List<AbgebendeLeitungenVorgang> tasks = taskDao.findOpenAbgebendeLeitungenTasksWithWiedervorlage();
        for (AbgebendeLeitungenVorgang task : tasks) {
            Assert.assertFalse(task.getAbgebendeLeitungenUserTask().equals(akmPv));
        }
    }


    public void findKueDtTaskTest() {
        buildErlm();

        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();

        final KueDtUserTask createKueDtUserTask = witaUsertaskService.createKueDtUserTask(
                new ErledigtMeldungBuilder().withVertragsnummer(carrierbestellung.getVtrNr())
                        .build()
        );
        final AkmPvUserTask createAkmPvUserTask = witaUsertaskService
                .createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder().withVertragsnummer(
                        carrierbestellung.getVtrNr()).build());
        taskDao.flushSession();

        List<AbgebendeLeitungenVorgang> tasks = taskDao.findOpenAbgebendeLeitungenTasksWithWiedervorlage();
        assertTrue(Iterables.any(tasks, new EqualTaskIdPredicate(createKueDtUserTask)));

        assertTrue(Iterables.any(tasks, new EqualTaskIdPredicate(createAkmPvUserTask)));
    }

    public void findKueDtUserTaskForExterneAuftragsnummerTest() {
        buildErlm();

        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();

        KueDtUserTask kueDtTask = witaUsertaskService.createKueDtUserTask(
                new ErledigtMeldungBuilder().withVertragsnummer(carrierbestellung.getVtrNr()).build());
        taskDao.flushSession();

        KueDtUserTask task = taskDao.findKueDtTask(kueDtTask.getExterneAuftragsnummer());
        assertNotNull(task);
        assertEquals(task.getExterneAuftragsnummer(), kueDtTask.getExterneAuftragsnummer());
    }

    public void findAbgebendeLeitungUserTaskKueDt() {
        buildErlm();

        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();

        final KueDtUserTask kueDtUserTask = witaUsertaskService.createKueDtUserTask(
                new ErledigtMeldungBuilder().withVertragsnummer(carrierbestellung.getVtrNr())
                        .build()
        );

        List<AbgebendeLeitungenUserTask> foundAbgeLeitungen = taskDao.findAbgebendeLeitungenUserTask(kueDtUserTask
                .getExterneAuftragsnummer());
        assertEquals(foundAbgeLeitungen, ImmutableList.of(kueDtUserTask));

    }

    public void findAbgebendeLeitungUserTaskAkmPv() {
        buildErlm();

        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();

        final AkmPvUserTask akmPvUserTask = witaUsertaskService
                .createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder().withVertragsnummer(
                        carrierbestellung.getVtrNr()).build());
        taskDao.flushSession();

        List<AbgebendeLeitungenUserTask> foundAbgeLeitungen = taskDao.findAbgebendeLeitungenUserTask(akmPvUserTask
                .getExterneAuftragsnummer());
        assertEquals(foundAbgeLeitungen, ImmutableList.of(akmPvUserTask));

    }

    private Carrierbestellung createCarrierbestellungWithAuftragDaten() {

        Carrierbestellung2EndstelleBuilder currentCb2EsBuilder = cb2EsBuilder.get();

        Endstelle endstelle = endstelleBuilder.get().
                withAuftragTechnikBuilder(auftragTechnikBuilder.get().
                        withAuftragBuilder(auftragBuilder.get().
                                withAuftragDatenBuilder(auftragDatenBuilder.get()))).
                withCb2EsBuilder(currentCb2EsBuilder).build();

        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().withCb2EsBuilder(
                currentCb2EsBuilder).withVtrNr("123452356").build();
        assertNotNull(carrierbestellung.getId());
        assertEquals(carrierbestellung.getCb2EsId(), endstelle.getCb2EsId());

        taskDao.flushSession();

        return carrierbestellung;
    }

    public void saveTamVorgaengeTest() throws Exception {
        buildVorgangWithTam();
        List<TamVorgang> tasks = taskDao.findOpenTamTasksWithWiedervorlage();
        WitaCBVorgang cbVorgang = tasks.get(0).getCbVorgang();
        assertNotNull(cbVorgang.getTamUserTask().getId());
        carrierElTALService.saveCBVorgang(cbVorgang);
        assertNotNull(cbVorgang.getTamUserTask().getId());
    }


    @SuppressWarnings("unchecked")
    public void checkOrdering() {

        WitaCBVorgang cbVorgang1 = buildVorgangWithTam();
        TerminAnforderungsMeldung firstTam = tam;

        WitaCBVorgang cbVorgang2 = buildVorgangWithTam(getDefaultCbVBuilder().withCarrierRefNr("ARST" + 25));
        buildTam(new TerminAnforderungsMeldungBuilder().withVersandZeitstempel(LocalDateTime.now().plusSeconds(10)));
        TerminAnforderungsMeldung secondTam = tam;

        assertThat(Date.from(firstTam.getVersandZeitstempel().toInstant()), lessThan(Date.from(secondTam.getVersandZeitstempel().toInstant())));

        List<TamVorgang> tasks = taskDao.findOpenTamTasksWithWiedervorlage();
        Assert.assertTrue(tasks.size() >= 2);

        for (TamVorgang task : tasks) {
            if (task.getId().equals(cbVorgang1.getId()) ||
                    task.getId().equals(cbVorgang2.getId())) {
                assertThat(task.getTam().getVersandZeitstempel(), equalTo(firstTam.getVersandZeitstempel()));
                return;
            }
        }

        Assert.fail("Konnte die neu erstellten Tam-Tasks nicht finden.");
    }

    private WitaCBVorgang buildVorgangWithTam(WitaCBVorgangBuilder builder) {
        witaCBVorgang = builder.build();
        assertNotNull(witaCBVorgang.getCarrierRefNr());
        buildTam();
        return witaCBVorgang;
    }

    private WitaCBVorgang buildVorgangWithTam() {
        return buildVorgangWithTam(getDefaultCbVBuilder());
    }

    private WitaCBVorgang buildVorgangWithTamAndWiedervorlage() {
        return buildVorgangWithTam(getCbVBuilderWithWiedervorlage());
    }

    private WitaCBVorgangBuilder getDefaultCbVBuilder() {
        return cbVorgangBuilder
                .get()
                .withAuftragBuilder(
                        auftragBuilder
                                .get()
                                .withAuftragTechnikBuilder(auftragTechnikBuilder.get())
                                .withAuftragDatenBuilder(
                                        auftragDatenBuilder.get().withProdId(Produkt.PROD_ID_MAXI_DSL_ANALOG))
                )
                .withCarrierRefNr("ARST1234767").withTamUserTask(tamUserTaskBuilder.get());
    }

    private WitaCBVorgangBuilder getCbVBuilderWithWiedervorlage() {
        return cbVorgangBuilder
                .get()
                .withAuftragBuilder(
                        auftragBuilder
                                .get()
                                .withAuftragTechnikBuilder(auftragTechnikBuilder.get())
                                .withAuftragDatenBuilder(
                                        auftragDatenBuilder.get().withProdId(Produkt.PROD_ID_MAXI_DSL_ANALOG))
                )
                .withCarrierRefNr("ARST1234767").withTamUserTask(tamUserTaskBuilder.get().withWiedervorlageAm(LocalDateTime.now().plusMinutes(2)));
    }

    private void buildTam() {
        buildTam(new TerminAnforderungsMeldungBuilder());
    }

    private void buildTam(TerminAnforderungsMeldungBuilder builder) {
        tam = builder.withExterneAuftragsnummer(
                witaCBVorgang.getCarrierRefNr()).build();
        mwfEntityDao.store(tam);
    }

    private void buildErlm() {
        buildErlm(new ErledigtMeldungBuilder());
    }

    private void buildErlm(ErledigtMeldungBuilder builderErlm) {
        ErledigtMeldung erlm = builderErlm.withExterneAuftragsnummer("123").build();
        mwfEntityDao.store(erlm);
    }

}
