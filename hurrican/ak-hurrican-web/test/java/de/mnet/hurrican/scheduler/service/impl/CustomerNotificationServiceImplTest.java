package de.mnet.hurrican.scheduler.service.impl;

import static de.mnet.wita.config.WitaConstants.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.SmsConfig;
import de.augustakom.hurrican.model.cc.SmsMontage;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.esb.cdm.customer.messagedeliveryservice.v1.MessageDeliveryService;
import de.mnet.hurrican.scheduler.service.impl.CustomerNotificationServiceImpl.MeldungscodeInDb;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;

@Test(groups = BaseTest.UNIT)
public class CustomerNotificationServiceImplTest {
    @Spy
    @InjectMocks
    CustomerNotificationServiceImpl cut;

    @Mock
    MessageDeliveryService messageDeliveryService;

    @Mock
    CarrierElTALService carrierElTALService;

    @Mock
    CCAuftragService ccAuftragService;

    @Mock
    AnsprechpartnerService ansprechpartnerService;

    @Mock
    MwfEntityService mwfEntityService;

    @Mock
    QueryCCService queryCCService;

    @Mock
    RegistryService registryService;

    @Mock
    OEService oeService;

    @Captor
    private ArgumentCaptor<List<Meldung<?>>> meldungCaptor;

    @BeforeMethod
    public void initTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testREPLACE_SCHALTTERMIN() throws Exception {
        final LocalDate schalttermin = LocalDate.now().plusDays(2);
        final String result =
                CustomerNotificationServiceImpl.REPLACE_SCHALTTERMIN.apply(Date.from(schalttermin.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        "Der neue Schalttermin Ihrer Leitung ist am @schalttermin@.");
        assertThat(
                result,
                equalTo(String.format("Der neue Schalttermin Ihrer Leitung ist am %s.",
                        schalttermin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
        );
    }

    @Test
    public void testREPLACE_ZEITFENSTER() throws Exception {
        final String zeitfenster = Kundenwunschtermin.Zeitfenster.SLOT_9.getShortDescription();
        final String result =
                CustomerNotificationServiceImpl.REPLACE_ZEITFENSTER
                        .apply("Der Schalttermin Ihrer Leitung ist am xx.xx.xxxx @zeitfenster@.", zeitfenster);
        assertThat(result,
                equalTo(String.format("Der Schalttermin Ihrer Leitung ist am xx.xx.xxxx %s.", zeitfenster)));
    }

    @Test
    public void testProcessTemplate() throws Exception {
        final LocalDate schalttermin = LocalDate.now().plusDays(2);
        final String template = "Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. " +
                "Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team";
        Kundenwunschtermin.Zeitfenster zeitfenster = Kundenwunschtermin.Zeitfenster.SLOT_9;
        final String expectedResult = String.format("Der Schalttermin Ihrer Leitung ist am %s %s. " +
                        "Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team",
                schalttermin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                zeitfenster.getShortDescription()
        );

        final String result = cut.processTemplate(template,
                Date.from(schalttermin.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                zeitfenster);

        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void thatMeldungsCodeForTaTamIs6012() {
        Meldung<?> tam = tam("", MELDUNGSCODE_6012, "test");
        MeldungscodeInDb meldungsCode = cut.getDBMeldungscodeForTam(tam);
        assertThat(meldungsCode , equalTo(MeldungscodeInDb.SMS_CONFIG_MELDUNGSCODE_6012));
    }

    @Test
    public void thatMeldungsCodeForNonTaTamIsDefaultMeldungsCode() {
        Meldung<?> tam = tam("", "6014", "test");
        MeldungscodeInDb meldungsCode = cut.getDBMeldungscodeForTam(tam);
        assertThat(meldungsCode , equalTo(MeldungscodeInDb.SMS_CONFIG_MELDUNGSCODE_DEFAULT));
    }

    private static Meldung tam(final String extAuftragNo, final String meldungsCode, final String meldungsText)
    {
        final TerminAnforderungsMeldung tam = new TerminAnforderungsMeldung();
        tam.setExterneAuftragsnummer(extAuftragNo);
        tam.setGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        tam.setAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
        tam.getMeldungsPositionen().add(new MeldungsPosition(meldungsCode, meldungsText));
        return tam;
    }

    static private Meldung meldung(String extAuftragNo, long versandZeitstempel) {
        ErledigtMeldung out = new ErledigtMeldung();
        out.setExterneAuftragsnummer(extAuftragNo);
        out.setVersandZeitstempel(Date.from(Instant.ofEpochMilli(versandZeitstempel)));  // stupid, isn't it?
        return out;
    }

    @Test
    public void testSortMeldungGroup() {
        Meldung<?> m11 = meldung("1", 1);
        Meldung<?> m12 = meldung("1", 2);
        Meldung<?> m13 = meldung("1", 3);
        Meldung<?> m21 = meldung("2", 4);
        Meldung<?> m22 = meldung("2", 5);
        Meldung<?> m23 = meldung("2", 6);
        List<Meldung<?>> in = ImmutableList.of(m22, m13, m11, m12, m23, m21);

        ListMultimap<String, Meldung<?>> out = cut.groupByExterneAuftragId(in);

        assertThat(out.keySet(), hasSize(2));
        assertThat(out.keySet(), containsInAnyOrder("1", "2"));
        assertThat(out.get("1"), hasSize(3));
        assertThat(out.get("1"), contains(m13, m12, m11));
        assertThat(out.get("2"), hasSize(3));
        assertThat(out.get("2"), contains(m23, m22, m21));
    }

    @Test
    public void testSendSmsForGruppe() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        Ansprechpartner ansprechpartner = getAnsprechpartnerForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setTemplateText(
                "Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(ansprechpartnerService.findPreferredAnsprechpartner(any(Typ.class), any(Long.class)))
                .thenReturn(ansprechpartner);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.GESENDET));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendSmsForGruppeWithOutTemplate() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(new ArrayList<SmsConfig>());
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.KEINE_CONFIG));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendEmailForGruppeUnvalid() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(null);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(new ArrayList<SmsConfig>());
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendEmailForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getEmailStatus(), equalTo(EmailStatus.UNGUELTIG));
        assertThat(arguments.get(1).getEmailStatus(), equalTo(EmailStatus.UNGUELTIG));
    }

    @Test
    public void testDetermineZeitfensterNull() throws Exception {
        WitaCBVorgang witaCbVorgang = new WitaCBVorgang();
        witaCbVorgang.setRealisierungsZeitfenster(null);

        Kundenwunschtermin.Zeitfenster determinedZeitfenster = cut.determineZeitfenster(witaCbVorgang);

        // wenn kein Zeitfenster in CBVorgang gesetzt, dann muss Zeitfenster.SLOT_8 standardmäßig zurückgegeben werden
        assertThat(determinedZeitfenster, equalTo(Kundenwunschtermin.Zeitfenster.SLOT_8));
    }

    @Test
    public void testDetermineZeitfensterNotInstanceOfWitaCBVorgang() throws Exception {
        CBVorgang cbVorgang = new WitaCBVorgang();

        Kundenwunschtermin.Zeitfenster determinedZeitfenster = cut.determineZeitfenster(cbVorgang);

        // wenn CBVorgang keine Instanz von WitaCBVorgang, dann muss Zeitfenster.SLOT_8 standardmäßig zurückgegeben werden
        assertThat(determinedZeitfenster, equalTo(Kundenwunschtermin.Zeitfenster.SLOT_8));
    }

    @Test
    public void testProcessEmailTemplate() {
        String template = "Sehr geehrte Kundin, sehr geehrter Kunde,\n"
                + "            leider konnte Ihr Anschluss @produkt@ nicht geschaltet werden.\n"
                + "            Bitte rufen Sie uns an, um das weitere Vorgehen gemeinsam zu besprechen.\n"
                + "            Unter 0800 7080810 sind wir Montag bis Freitag in der Zeit @zeitfenster@ sowie Samstag in der Zeit von 09:00 bis 15:00 Uhr für Sie da.\n"
                + "\n"
                + "            Viele Grüße\n"
                + "\n"
                + "            Ihr M-net Kundenservice'\n";

        String testProdukt = "TestProdukt";
        String taifunNo = "TaifunNo";
        String replaceDelimiter = "@";

        Kundenwunschtermin.Zeitfenster zeitfenster = Kundenwunschtermin.Zeitfenster.SLOT_9;
        String message = cut.processEmailTemplate(template, new Date(), zeitfenster, testProdukt, taifunNo);

        Assert.assertTrue(!message.contains(replaceDelimiter));
        Assert.assertTrue(message.contains(testProdukt));
        Assert.assertTrue(message.contains(zeitfenster.getShortDescription()));
    }

    @Test
    public void testSendSmsForGruppeWhenNoCbVorgangExists() throws Exception {

        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(null);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.KEINE_CONFIG));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendSmsForGruppeWithOldInbetriebnahmeDate() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        auftragDaten.setInbetriebnahme(DateTools.getPreviousDay(new Date()));
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setTemplateText("Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.VERALTET));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendSmsForGruppeWithAuftragStorno() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        auftragDaten.setStatusId(AuftragStatus.STORNO);
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setTemplateText("Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.FALSCHER_AUFTRAGSTATUS));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendSmsForGruppeWithoutAuftragSmsVersandFlag() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        auftragDaten.setAutoSmsAndMailVersand(false);
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setTemplateText("Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.UNERWUENSCHT));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testSendSmsForGruppeWithoutAnsprechpartner() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setTemplateText("Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(ansprechpartnerService.findPreferredAnsprechpartner(any(Typ.class), any(Long.class)))
                .thenReturn(null);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendSmsForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getSmsStatus(), equalTo(SmsStatus.KEINE_RN));
        assertThat(arguments.get(1).getSmsStatus(), equalTo(SmsStatus.UNGUELTIG));
    }

    @Test
    public void testGetRufnummer() {
        Ansprechpartner a1 = getAnsprechpartnerForTest();

        a1.getAddress().setHandy("015123");
        a1.getAddress().setTelefon(null);
        assertThat(cut.getSmsRufnummer(a1), equalTo("015123"));

        a1.getAddress().setHandy(" ");
        a1.getAddress().setTelefon("015123");
        assertThat(cut.getSmsRufnummer(a1), equalTo("015123"));

        a1.getAddress().setHandy(" ");
        a1.getAddress().setTelefon("089123");
        assertThat(cut.getSmsRufnummer(a1), equalTo(null));

        a1.getAddress().setHandy(" ");
        a1.getAddress().setTelefon("0");
        assertThat(cut.getSmsRufnummer(a1), equalTo(null));

        a1.getAddress().setHandy(" ");
        a1.getAddress().setTelefon("015");
        assertThat(cut.getSmsRufnummer(a1), equalTo(null));
    }

    @Test
    public void testRufnummernormierung() {
        assertThat(cut.normiereRufnummer(null), equalTo(null));
        assertThat(cut.normiereRufnummer(" "), equalTo(null));
        assertThat(cut.normiereRufnummer("089/401297"), equalTo("089401297"));
        assertThat(cut.normiereRufnummer("+49 89 66007014"), equalTo("08966007014"));
        assertThat(cut.normiereRufnummer("+49 0 16098942649"), equalTo("016098942649"));
        assertThat(cut.normiereRufnummer("+49 (0) 16098942649"), equalTo("016098942649"));
        assertThat(cut.normiereRufnummer("+49 0172 1380365"), equalTo("01721380365"));
        assertThat(cut.normiereRufnummer("0049 172 1380365"), equalTo("01721380365"));
        assertThat(cut.normiereRufnummer("0049 (0) 172 1380365"), equalTo("01721380365"));
        assertThat(cut.normiereRufnummer("(0162) 5305794"), equalTo("01625305794"));
    }

    @Test
    public void testSendSmsForGruppeWithoutMeldungen() throws Exception {
        cut.sendSmsForGroup(new ArrayList<Meldung<?>>());
        verify(cut, never()).sendData(anyObject(), anyObject());
    }


    @Test
    public void testSendEmailForGruppeOK() throws Exception {

        CBVorgang cbVorgang = getCbVorgangForTest();
        AuftragDaten auftragDaten = getAuftragDatenForTest();
        Auftrag auftrag = getAuftragForTest();

        Ansprechpartner ansprechpartner = getAnsprechpartnerForTest();

        List<Meldung<?>> abmOffen = new ArrayList<Meldung<?>>();
        Meldung<?> meldung = getMeldungForTest(1L);
        abmOffen.add(meldung);
        Meldung<?> meldung2 = getMeldungForTest(2L);
        abmOffen.add(meldung2);

        SmsConfig example = new SmsConfig();
        example.setId(1L);
        example.setMontage(SmsMontage.YES);
        example.setEmailText("TestEmailText");

        example.setTemplateText(
                "Der Schalttermin Ihrer Leitung ist am @schalttermin@ @zeitfenster@. Bitte stellen Sie sicher, dass jemand vor Ort ist. Ihr M-net Team");
        List<SmsConfig> templates = new ArrayList<SmsConfig>();
        templates.add(example);

        when(ccAuftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(ccAuftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(ansprechpartnerService.findPreferredAnsprechpartner(any(Typ.class), any(Long.class)))
                .thenReturn(ansprechpartner);
        when(carrierElTALService.findCBVorgangByCarrierRefNr(meldung.getExterneAuftragsnummer()))
                .thenReturn(cbVorgang);
        when(queryCCService.findByExample(anyObject(), eq(SmsConfig.class))).thenReturn(templates);
        when(mwfEntityService.store(eq(meldung))).thenReturn((Meldung) meldung);
        when(mwfEntityService.store(eq(meldung2))).thenReturn((Meldung) meldung2);

        cut.sendEmailForGroup(abmOffen);

        verify(cut).setOtherToInvalid(meldungCaptor.capture(), anyObject());

        List<Meldung<?>> arguments = meldungCaptor.getValue();
        assertThat(arguments.get(0).getEmailStatus(), equalTo(EmailStatus.GESENDET));
        assertThat(arguments.get(1).getEmailStatus(), equalTo(EmailStatus.UNGUELTIG));
    }

    private Meldung<?> getMeldungForTest(Long id) {
        Meldung<?> meldung = new AuftragsBestaetigungsMeldungBuilder().build();
        meldung.setId(id);
        meldung.setExterneAuftragsnummer("1");
        meldung.setAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
        meldung.setGeschaeftsfallTyp(GeschaeftsfallTyp.BEREITSTELLUNG);
        meldung.setSmsStatus(SmsStatus.OFFEN);
        meldung.setEmailStatus(EmailStatus.OFFEN);
        return meldung;
    }

    private Ansprechpartner getAnsprechpartnerForTest() {
        CCAddress add = new CCAddress();
        add.setName(RandomTools.createString());
        Ansprechpartner ansprechpartner = new Ansprechpartner();
        add.setName(RandomTools.createString());
        add.setVorname(RandomTools.createString());
        add.setEmail(RandomTools.createString());
        add.setFax(RandomTools.createString());
        add.setHandy("12345");
        add.setTelefon(RandomTools.createString());
        ansprechpartner.setAddress(add);
        return ansprechpartner;
    }

    private Auftrag getAuftragForTest() {
        Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        auftrag.setKundeNo(1234L);
        return auftrag;
    }

    private AuftragDaten getAuftragDatenForTest() {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomId().build();
        auftragDaten.setAuftragId(10L);
        auftragDaten.setAutoSmsAndMailVersand(true);
        auftragDaten.setInbetriebnahme(DateTools.plusWorkDays(1));
        return auftragDaten;
    }

    private CBVorgang getCbVorgangForTest() {
        CBVorgang cbVorgang = new CBVorgangBuilder().withRandomId().build();
        cbVorgang.setMontagehinweis("YES");
        cbVorgang.setReturnRealDate(new Date());
        return cbVorgang;
    }

}
