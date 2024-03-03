/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2011 09:44:06
 */
package de.mnet.wita.bpm.converter.cbvorgang;

import static de.mnet.wita.AbmMeldungsCode.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.position.LeitungBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class AbmCbVorgangConverterTest extends BaseTest {

    @InjectMocks
    private AbmCbVorgangConverter abmCbVorgangConvertor;

    @Mock
    private WitaUsertaskService witaUsertaskService;
    @Mock
    private MwfEntityService mwfEntityService;
    @Mock
    private WitaDataService witaDataService;

    @BeforeMethod
    public void setupMocks() {
        abmCbVorgangConvertor = new AbmCbVorgangConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void testGetCarrierBearbeiterString() {
        Set<String> bearbeiter = new HashSet<>();
        assertEquals(abmCbVorgangConvertor.getCarrierBearbeiterString(bearbeiter), "");

        bearbeiter.add("bla");
        bearbeiter.add("blub");
        String bearbeiterString = abmCbVorgangConvertor.getCarrierBearbeiterString(bearbeiter);
        assertTrue(bearbeiterString.contains("bla"));
        assertTrue(bearbeiterString.contains("blub"));
    }

    public void testWriteDataFromAbm() throws Exception {
        Auftrag request = new AuftragBuilder(new GeschaeftsfallBuilder(BEREITSTELLUNG)
                .withKundenwunschtermin(LocalDate.now(), SLOT_9)).buildValid();

        Leitung leitung = (new LeitungBuilder())
                .withLeitungsbezeichnung(new LeitungsBezeichnung("96W", "089", "089", "123456789"))
                .withLeitungsAbschnittList(LeitungsAbschnitt.valueOf("100/50", "10/20")).withMaxBruttoBitrate("3500")
                .buildValid();
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).withLeitung(leitung).build();

        when(mwfEntityService.getAuftragOfCbVorgang(anyLong())).thenReturn(request);
        when(witaDataService.transformWitaZeitfenster(request, abm)).thenReturn(TalRealisierungsZeitfenster.GANZTAGS);

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        abmCbVorgangConvertor.writeData(cbVorgang, abm);

        assertThat(cbVorgang.getReturnLBZ(), equalTo("96W/089/089/123456789"));
        assertThat(cbVorgang.getReturnLL(), equalTo("100/50"));
        assertThat(cbVorgang.getReturnAQS(), equalTo("10/20"));
        assertThat(cbVorgang.getReturnMaxBruttoBitrate(), equalTo("3500"));
        assertThat(cbVorgang.getReturnKundeVorOrt(), equalTo(Boolean.FALSE));
        assertThat(cbVorgang.getReturnOk(), equalTo(true));
        assertThat(cbVorgang.getStatus(), equalTo(CBVorgang.STATUS_ANSWERED));
        assertThat(cbVorgang.getTalRealisierungsZeitfenster(), equalTo(TalRealisierungsZeitfenster.GANZTAGS));

        verify(mwfEntityService).getAuftragOfCbVorgang(anyLong());
        verify(witaDataService).transformWitaZeitfenster(request, abm);
    }

    public void testWriteKundeVorOrtFromAbm() throws Exception {
        AuftragsBestaetigungsMeldung abm = buildAbm();

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        abmCbVorgangConvertor.writeData(cbVorgang, abm);

        assertThat(cbVorgang.getReturnKundeVorOrt(), equalTo(Boolean.TRUE));
    }

    public void testCloseUserTask() throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();

        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setStatus(UserTaskStatus.OFFEN);
        tamUserTask.setTamBearbeitungsStatus(TamBearbeitungsStatus.IN_BEARBEITUNG);
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setTamUserTask(tamUserTask);

        abmCbVorgangConvertor.writeData(cbVorgang, abm);

        verify(witaUsertaskService).closeUserTask(tamUserTask);
        verifyNoMoreInteractions(witaUsertaskService);
    }

    public void testDoNotCloseUserTaskOnTv60() throws Exception {
        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();

        TamUserTask tamUserTask = new TamUserTask();
        tamUserTask.setStatus(UserTaskStatus.OFFEN);
        tamUserTask.setTv60Sent(true);
        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setTamUserTask(tamUserTask);

        abmCbVorgangConvertor.writeData(cbVorgang, abm);

        verifyZeroInteractions(witaUsertaskService);
    }

    public void testWiedervorlageReset() throws Exception {
        AuftragsBestaetigungsMeldung abm = buildAbm();

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setWiedervorlageAm(new Date());
        abmCbVorgangConvertor.writeData(cbVorgang, abm);

        assertNull(cbVorgang.getWiedervorlageAm());
    }

    AuftragsBestaetigungsMeldung buildAbm() {
        AnsprechpartnerTelekom ap = new AnsprechpartnerTelekom(null, "Andreas", "Schmid", "0123 456789");
        MeldungsPositionWithAnsprechpartner mp = new MeldungsPositionWithAnsprechpartner(CUSTOMER_REQUIRED, ap);
        return new AuftragsBestaetigungsMeldungBuilder().addMeldungsposition(mp).build();
    }
}
