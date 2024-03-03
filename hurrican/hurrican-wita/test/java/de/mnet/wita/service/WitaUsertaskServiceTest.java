/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2011 14:20:57
 */
package de.mnet.wita.service;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.Endstelle.*;
import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;
import static de.mnet.wita.model.UserTask.UserTaskStatus.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AufnehmenderProviderBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.UserTask2AuftragDaten;
import de.mnet.wita.model.VorabstimmungAbgebendBuilder;

@Test(groups = SERVICE)
public class WitaUsertaskServiceTest extends AbstractServiceTest {
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
    private Provider<VorabstimmungAbgebendBuilder> vorabstimmungAbgebendBuilder;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WitaUsertaskService witaUsertaskService;

    public void createKueDtUserTask() throws FindException {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        KueDtUserTask kueDtUserTask = witaUsertaskService.createKueDtUserTask(new ErledigtMeldungBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).build());
        assertNotNull(kueDtUserTask);

        List<AuftragDaten> auftragDaten = witaUsertaskService.findAuftragDatenForUserTask(kueDtUserTask);
        assertThat(auftragDaten, Matchers.hasSize(1));
    }

    public void createKueDtUserTaskWithMultipleCb() throws FindException {
        Carrierbestellung carrierbestellung1 = createCarrierbestellungWithAuftragDaten();
        Carrierbestellung carrierbestellung2 = createCarrierbestellungWithAuftragDaten();
        assertEquals(carrierbestellung1.getVtrNr(), carrierbestellung2.getVtrNr());

        KueDtUserTask kueDtUserTask = witaUsertaskService.createKueDtUserTask(new ErledigtMeldungBuilder()
                .withVertragsnummer(carrierbestellung1.getVtrNr()).build());

        List<AuftragDaten> auftragDaten = witaUsertaskService.findAuftragDatenForUserTask(kueDtUserTask);
        assertThat(auftragDaten, Matchers.hasSize(2));
    }

    public void shouldFindCancelledCbs() throws Exception {
        Carrierbestellung activeCb = createCarrierbestellungWithAuftragDaten();
        Carrierbestellung cancelledCb = createCarrierbestellungWithAuftragDaten(carrierbestellungBuilder.get()
                .withKuendBestaetigungCarrier(new Date()));
        assertEquals(activeCb.getVtrNr(), cancelledCb.getVtrNr());

        KueDtUserTask kueDtUserTask = witaUsertaskService.createKueDtUserTask(new ErledigtMeldungBuilder()
                .withVertragsnummer(activeCb.getVtrNr()).build());

        List<AuftragDaten> auftragDaten = witaUsertaskService.findAuftragDatenForUserTask(kueDtUserTask);
        assertNotEmpty(auftragDaten);
        assertEquals(auftragDaten.size(), 2);
    }

    public void cancelledCbsShouldAssignOrder() throws Exception {
        Carrierbestellung cancelledCb = createCarrierbestellungWithAuftragDaten(carrierbestellungBuilder.get()
                .withKuendBestaetigungCarrier(new Date()));

        KueDtUserTask kueDtUserTask = witaUsertaskService.createKueDtUserTask(new ErledigtMeldungBuilder()
                .withVertragsnummer(cancelledCb.getVtrNr()).build());

        List<AuftragDaten> result = witaUsertaskService.findAuftragDatenForUserTask(kueDtUserTask);
        assertNotEmpty(result, "AuftragDaten not found but expected!");
    }

    public void createAkmPvUserTask() {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        AkmPvUserTask akmPvUserTask = witaUsertaskService.createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).build());
        assertNotNull(akmPvUserTask);

        AkmPvUserTask achieved = witaUsertaskService.findAkmPvUserTask(akmPvUserTask.getExterneAuftragsnummer());
        assertEquals(achieved, akmPvUserTask);
    }

    public void createAkmPvUserTask2AuftragDaten() {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        AkmPvUserTask akmPvUserTask = witaUsertaskService.createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).build());
        AkmPvUserTask foundUserTask = witaUsertaskService.findAkmPvUserTask(akmPvUserTask.getExterneAuftragsnummer());
        assertNotNull(foundUserTask.getId());
        UserTask2AuftragDaten userTaskAuftragDaten = Iterables.getOnlyElement(foundUserTask.getUserTaskAuftragDaten());

        assertNotNull(userTaskAuftragDaten.getAuftragId());
        assertEquals(userTaskAuftragDaten.getCbId(), carrierbestellung.getId());
    }

    public void aufnehmenderProviderShouldBeWritten() {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProviderBuilder().build();
        AkmPvUserTask akmPvUserTask = witaUsertaskService.createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).withAufnehmenderProvider(aufnehmenderProvider)
                .build());

        AkmPvUserTask achieved = witaUsertaskService.findAkmPvUserTask(akmPvUserTask.getExterneAuftragsnummer());

        assertEquals(achieved.getAntwortFrist(), aufnehmenderProvider.getAntwortFrist());
        assertEquals(achieved.getAufnehmenderProvider(), aufnehmenderProvider.getProvidernameAufnehmend());
        assertEquals(achieved.getGeplantesKuendigungsDatum(), aufnehmenderProvider.getUebernahmeDatumGeplant());
    }

    public void leitungsbezeichnungShouldBeWritten() {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        Leitung leitung = new LeitungBuilder().buildValid();
        AkmPvUserTask akmPvUserTask = witaUsertaskService.createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).withLeitung(leitung).build());

        AkmPvUserTask achieved = witaUsertaskService.findAkmPvUserTask(akmPvUserTask.getExterneAuftragsnummer());

        assertEquals(achieved.getLeitungsBezeichnung(), leitung.getLeitungsBezeichnung().getLeitungsbezeichnungString());
    }

    public void createClosedAkmPvUserTask() {
        Carrierbestellung carrierbestellung = createCarrierbestellungWithAuftragDaten();
        AkmPvUserTask akmPvUserTask = witaUsertaskService.createAkmPvUserTask(new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr()).build());
        akmPvUserTask.changeAkmPvStatus(ABM_PV_EMPFANGEN);
        akmPvUserTask.setStatus(GESCHLOSSEN);
        witaUsertaskService.storeUserTask(akmPvUserTask);

        AkmPvUserTask achieved = witaUsertaskService.findAkmPvUserTask(akmPvUserTask.getExterneAuftragsnummer());
        assertSame(achieved, akmPvUserTask);
        assertEquals(achieved.getStatus(), GESCHLOSSEN);
        assertEquals(achieved.getAkmPvStatus(), ABM_PV_EMPFANGEN);
    }

    @DataProvider
    public Object[][] getAutomaticAnwerForAkmPvWithWitaVorabstimmung() throws FindException {
        LocalDate uebernahmeDatumGeplant = LocalDate.now().plusDays(15);
        Carrier carrierDtag = carrierService.findCarrier(Carrier.ID_DTAG);
        Carrier carrierO2 = carrierService.findCarrier(Carrier.ID_O2);

        // @formatter:off
        return new Object[][] {
                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_A, carrierDtag.getId(), uebernahmeDatumGeplant, true, RuemPvAntwortCode.OK },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierDtag.getId(), uebernahmeDatumGeplant, true, RuemPvAntwortCode.OK },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierDtag.getId(), uebernahmeDatumGeplant, false, null },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierDtag.getId(), uebernahmeDatumGeplant.plusDays(1), true, null },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierO2.getId(), uebernahmeDatumGeplant, true, null },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierO2.getId(), uebernahmeDatumGeplant.plusDays(2), true, null },

                { new AufnehmenderProviderBuilder()
                .withUebernahmeDatumGeplant(uebernahmeDatumGeplant)
                .withProvidernameAufnehmend(carrierDtag.getWitaProviderNameAufnehmend()),
                ENDSTELLEN_TYP_B, carrierO2.getId(), uebernahmeDatumGeplant.plusDays(2), false, null },
        };
        // @formatter:on
    }

    @Test(dataProvider = "getAutomaticAnwerForAkmPvWithWitaVorabstimmung")
    public void testGetAutomaticAnwerForAkmPvWithWitaVorabstimmung(AufnehmenderProviderBuilder aufnehmenderProvider,
            String endstellenTyp, Long carrierId, LocalDate uebernahmeGeplant, Boolean rueckmeldeCode,
            RuemPvAntwortCode ruemPvAntwort) throws FindException {

        Carrierbestellung2EndstelleBuilder currentCb2EsBuilder = cb2EsBuilder.get();
        AuftragBuilder auftragBuilder2 = auftragBuilder.get();
        endstelleBuilder
                .get()
                .withEndstelleTyp(endstellenTyp)
                .withAuftragTechnikBuilder(
                        auftragTechnikBuilder.get().withAuftragBuilder(
                                auftragBuilder2.withAuftragDatenBuilder(auftragDatenBuilder.get()))
                )
                .withCb2EsBuilder(currentCb2EsBuilder).build();

        Carrierbestellung carrierbestellung = carrierbestellungBuilder.get().withCb2EsBuilder(currentCb2EsBuilder)
                .withVtrNr("123452356").build();

        Carrier carrier = carrierService.findCarrier(carrierId);
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withVertragsnummer(carrierbestellung.getVtrNr())
                .withAufnehmenderProvider(aufnehmenderProvider.build()).build();

        vorabstimmungAbgebendBuilder.get().withAuftragBuilder(auftragBuilder2).withEndstelleTyp(endstellenTyp)
                .withAbgestimmterProdiverwechselTermin(uebernahmeGeplant).withCarrier(carrier)
                .withRueckmeldung(rueckmeldeCode).build();
        flushAndClear();

        Map<WitaTaskVariables, Object> witaTaskVariablesObjectMap = witaUsertaskService.getAutomaticAnswerForAkmPv(akmPv);
        assertEquals(witaTaskVariablesObjectMap.get(WitaTaskVariables.RUEM_PV_ANTWORTCODE), ruemPvAntwort);
    }

    private Carrierbestellung createCarrierbestellungWithAuftragDaten() {
        return createCarrierbestellungWithAuftragDaten(carrierbestellungBuilder.get());
    }

    private Carrierbestellung createCarrierbestellungWithAuftragDaten(CarrierbestellungBuilder cbBuilder) {

        Carrierbestellung2EndstelleBuilder currentCb2EsBuilder = cb2EsBuilder.get();

        Endstelle endstelle = endstelleBuilder
                .get()
                .withAuftragTechnikBuilder(
                        auftragTechnikBuilder.get().withAuftragBuilder(
                                auftragBuilder.get().withAuftragDatenBuilder(auftragDatenBuilder.get()))
                )
                .withCb2EsBuilder(currentCb2EsBuilder).build();

        Carrierbestellung carrierbestellung = cbBuilder.withCb2EsBuilder(currentCb2EsBuilder).withVtrNr("123452356")
                .build();
        assertNotNull(carrierbestellung.getId());
        assertEquals(carrierbestellung.getCb2EsId(), endstelle.getCb2EsId());

        return carrierbestellung;
    }

}
