/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.converter;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.math.RandomUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.AbmMeldungsCode;
import de.mnet.wita.bpm.converter.cbvorgang.AbbmCbVorgangConverter;
import de.mnet.wita.bpm.converter.cbvorgang.AbmCbVorgangConverter;
import de.mnet.wita.bpm.converter.cbvorgang.AbstractMwfCbVorgangConverter;
import de.mnet.wita.bpm.converter.cbvorgang.ErlmCbVorgangConverter;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = BaseTest.UNIT)
public class MwfCbVorgangConverterServiceTest extends BaseTest {

    @Spy
    @InjectMocks
    private final MwfCbVorgangConverterService mwfCbVorgangConverterService = new MwfCbVorgangConverterService();

    @Mock
    private CarrierElTALService carrierElTALService;
    @Mock
    private MwfEntityService mwfEntityService;
    @Mock
    private WitaDataService witaDataService;

    @InjectMocks
    private final AbmCbVorgangConverter abmCbVorgangConvertor = new AbmCbVorgangConverter();

    @InjectMocks
    private final AbbmCbVorgangConverter abbmCbVorgangConvertor = new AbbmCbVorgangConverter();

    @InjectMocks
    private final ErlmCbVorgangConverter erlmCbVorgangConvertor = new ErlmCbVorgangConverter();

    private AuftragsBestaetigungsMeldung abm;
    private AbbruchMeldung abbm;
    private ErledigtMeldung erlm;
    private WitaCBVorgang cbVorgang;

    private final Map<AnsprechpartnerTelekom, String> ansprechpartnerMap = ImmutableMap
            .<AnsprechpartnerTelekom, String>builder()
            .put(new AnsprechpartnerTelekom(Anrede.HERR, "Peter", "Schmidt", "12345"), "Schmidt Peter (12345)")
            .put(new AnsprechpartnerTelekom(Anrede.FRAU, "Jana", "Schmidt", "23456"), "Schmidt Jana (23456)").build();

    private final static LocalDate LIEFERTERMIN = LocalDate.now();
    private final static String VERTRAGSNUMMER = "12345";

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mwfCbVorgangConverterService.mwfCbVorgangConverters = ImmutableList
                .<AbstractMwfCbVorgangConverter<? extends Meldung<?>>>of(abmCbVorgangConvertor,
                        abbmCbVorgangConvertor, erlmCbVorgangConvertor);
        mwfCbVorgangConverterService.init();

        createCBVorgang();
        createAbm();
        createAbbm();
        erlm = new ErledigtMeldungBuilder().build();
    }

    @DataProvider
    public Object[][] meldungsProvider() {
        createCBVorgang();

        createAbm();
        createAbbm();
        erlm = new ErledigtMeldungBuilder().build();
        return new Object[][] { { abm }, { abbm }, { erlm } };
    }

    @Test(dataProvider = "meldungsProvider")
    public <T extends Meldung<?>> void testWrite(T meldung) throws Exception {
        AbmCbVorgangConverter abmCbVorgangConvertorMock = mock(AbmCbVorgangConverter.class);
        when(abmCbVorgangConvertorMock.getMeldungsType()).thenReturn(AuftragsBestaetigungsMeldung.class);
        AbbmCbVorgangConverter abbmCbVorgangConvertorMock = mock(AbbmCbVorgangConverter.class);
        when(abbmCbVorgangConvertorMock.getMeldungsType()).thenReturn(AbbruchMeldung.class);
        ErlmCbVorgangConverter erlmCbVorgangConvertorMock = mock(ErlmCbVorgangConverter.class);
        when(erlmCbVorgangConvertorMock.getMeldungsType()).thenReturn(ErledigtMeldung.class);
        mwfCbVorgangConverterService.mwfCbVorgangConverters = ImmutableList
                .<AbstractMwfCbVorgangConverter<? extends Meldung<?>>>of(abmCbVorgangConvertorMock,
                        abbmCbVorgangConvertorMock, erlmCbVorgangConvertorMock);
        mwfCbVorgangConverterService.init();

        mwfCbVorgangConverterService.write(cbVorgang, meldung);

        switch (meldung.getMeldungsTyp()) {
            case ABM:
                verify(abmCbVorgangConvertorMock).write(cbVorgang, meldung);
                break;
            case ABBM:
                verify(abbmCbVorgangConvertorMock).write(cbVorgang, meldung);
                break;
            case ERLM:
                verify(erlmCbVorgangConvertorMock).write(cbVorgang, meldung);
                break;
            default:
        }
        verify(carrierElTALService).saveCBVorgang(cbVorgang);
    }

    public void testWriteAbm() throws Exception {
        writeAbm();

        assertThat("Return OK should be true", cbVorgang.getReturnOk(), equalTo(true));
        assertThat("Status should be answered", cbVorgang.hasAnswer(), equalTo(true));
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertThat("SecondAbmReceived must not be set", cbVorgang.getSecondAbmReceived(), equalTo(false));

        assertThat("KundeForOrt must be false", cbVorgang.getReturnKundeVorOrt(), equalTo(false));
        assertThat("Liefertermin must be set correctly", cbVorgang.getReturnRealDate(), equalTo(Date.from(LIEFERTERMIN
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertThat("Vertragsnummer must be set correctly", cbVorgang.getReturnVTRNR(), equalTo(VERTRAGSNUMMER));

        assertThat("Leitungsbezeichnung must be set correctly", cbVorgang.getReturnLBZ(),
                equalTo("1234/1234/1234/1234"));
        assertThat("Leitungslaenge must be set correctly", cbVorgang.getReturnLL(), equalTo("100/50"));
        assertThat("Leitungsquerschnitt must be set correctly", cbVorgang.getReturnAQS(), equalTo("50/25"));
    }

    public void testWriteSecondAbm() throws StoreException {
        writeAbm();
        abm.setVerbindlicherLiefertermin(abm.getVerbindlicherLiefertermin().plusDays(1));
        writeAbm();
        assertThat("SecondAbmReceived must be true", cbVorgang.getSecondAbmReceived(), equalTo(true));
    }

    public void testWriteSecondAbmWithSameRealDate() throws StoreException {
        writeAbm();
        writeAbm();
        assertFalse(cbVorgang.getSecondAbmReceived(), "SecondAbmReceived must not be set");
    }

    public void testWriteAbmWithKundeVorOrt() throws Exception {
        AnsprechpartnerTelekom ansprechpartner = Iterables.getLast(ansprechpartnerMap.keySet());
        abm.addMeldungsPosition(new MeldungsPositionWithAnsprechpartner(AbmMeldungsCode.CUSTOMER_REQUIRED,
                ansprechpartner));

        writeAbm();
        assertThat("KundeForOrt must be true", cbVorgang.getReturnKundeVorOrt(), equalTo(true));
        assertThat("Ansprechpartner must be set correctly", cbVorgang.getCarrierBearbeiter(),
                equalTo(ansprechpartnerMap.get(ansprechpartner)));
    }

    public void testWriteAbmWithMultipleAnsprechpartner() throws Exception {
        List<String> expected = new ArrayList<>();
        Long randomId = RandomUtils.nextLong();
        long count = 0;
        for (Map.Entry<AnsprechpartnerTelekom, String> entry : ansprechpartnerMap.entrySet()) {
            count++;
            MeldungsPositionWithAnsprechpartner abmPos = new MeldungsPositionWithAnsprechpartner(
                    AbmMeldungsCode.NO_CHANGE, entry.getKey());
            abmPos.setId(randomId - count);
            abm.addMeldungsPosition(abmPos);
            expected.add(entry.getValue());
        }

        writeAbm();

        String carrierBearbeiterString = cbVorgang.getCarrierBearbeiter();
        Iterable<String> carrierBearbeiter = Splitter.on(",").split(carrierBearbeiterString);
        assertThat("Unexpected number of Bearbeiter", Iterables.size(carrierBearbeiter),
                equalTo(Iterables.size(expected)));
    }

    public void testWriteAbmWithLeadingZerosInLBZ() throws StoreException {
        abm.getLeitung().setLeitungsBezeichnung(new LeitungsBezeichnung("0001", "0002", "0003", "0004"));
        assertThat("LBZ should be unset before write operation", cbVorgang.getReturnLBZ(), equalTo(null));
        writeAbm();
        assertThat("LBZ should contain leading zeros", cbVorgang.getReturnLBZ(), equalTo("0001/0002/0003/0004"));
    }

    public void testWriteAbbmOnStorno() throws Exception {
        abbm.setAenderungsKennzeichen(STORNO);

        writeAbm();
        mwfCbVorgangConverterService.writeStorno(cbVorgang, null);
        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertReturnOkWithRealDate();
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(STORNO));
    }

    public void testWriteAbbmOnStornoWithoutAbm() throws Exception {
        abbm.setAenderungsKennzeichen(STORNO);

        cbVorgang.setWiedervorlageAm(new Date());
        mwfCbVorgangConverterService.writeStorno(cbVorgang, null);
        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertOpenWithBemerkung();
        assertNull(cbVorgang.getWiedervorlageAm());
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(STORNO));
    }

    public void testWriteAbbmOnTvWithoutAbm() throws Exception {
        abbm.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);

        cbVorgang.setWiedervorlageAm(new Date());
        mwfCbVorgangConverterService.writeTerminVerschiebung(cbVorgang,
                abm.getVerbindlicherLiefertermin().plusDays(10), null);
        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertOpenWithBemerkung();
        assertNull(cbVorgang.getWiedervorlageAm());
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(TERMINVERSCHIEBUNG));
    }

    public void testWriteAbbmOnTv() throws Exception {
        abbm.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);

        writeAbm();
        mwfCbVorgangConverterService.writeTerminVerschiebung(cbVorgang,
                abm.getVerbindlicherLiefertermin().plusDays(10), null);
        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertReturnOkWithRealDate();
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(TERMINVERSCHIEBUNG));
    }

    public void testWriteAbmOnTv() throws Exception {
        abm.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);

        mwfCbVorgangConverterService.writeTerminVerschiebung(cbVorgang, abm.getVerbindlicherLiefertermin(), null);
        writeAbm();

        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertTrue(cbVorgang.getReturnOk());
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertThat("Real return date must be set to Vorgabe Mnet",
                DateConverterUtils.asLocalDate(cbVorgang.getReturnRealDate()), equalTo(abm.getVerbindlicherLiefertermin()));
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(TERMINVERSCHIEBUNG));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(STANDARD));
    }

    public void testWriteAbbmOnAbm() throws Exception {
        cbVorgang.setAenderungsKennzeichen(STANDARD);
        abbm.setAenderungsKennzeichen(STANDARD);

        writeAbm();
        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertFalse(cbVorgang.getReturnOk());
        assertTrue(cbVorgang.getReturnBemerkung().contains("0123 : Fehler"));
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertThat("Real return date must be set to Vorgabe Mnet",
                DateConverterUtils.asLocalDate(cbVorgang.getReturnRealDate()), equalTo(abm.getVerbindlicherLiefertermin()));
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));

        assertTrue(cbVorgang.getAbbmOnAbm());
    }

    public void testWriteAbbm() throws Exception {
        cbVorgang.setAenderungsKennzeichen(STANDARD);
        abbm.setAenderungsKennzeichen(STANDARD);

        mwfCbVorgangConverterService.write(cbVorgang, abbm);

        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertFalse(cbVorgang.getReturnOk());
        assertThat(cbVorgang.getReturnBemerkung(), containsString("0123 : Fehler"));
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertNull(cbVorgang.getReturnRealDate(), "Real return date must not be set!");
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));

        assertFalse(cbVorgang.getAbbmOnAbm());
    }

    public void testWriteErlm() throws Exception {
        mwfCbVorgangConverterService.write(cbVorgang, erlm);

        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertTrue(cbVorgang.getReturnOk());
    }

    public void testWriteStandardErlmOnStorno() throws StoreException {
        cbVorgang.setAenderungsKennzeichen(STORNO);
        erlm.setAenderungsKennzeichen(STANDARD);

        mwfCbVorgangConverterService.write(cbVorgang, erlm);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), STANDARD);
    }

    public void testWriteStornoErlmOnStorno() throws StoreException {
        cbVorgang.setAenderungsKennzeichen(STORNO);
        erlm.setAenderungsKennzeichen(STORNO);

        mwfCbVorgangConverterService.write(cbVorgang, erlm);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), STORNO);
    }

    public void testWriteStandardErlmOnTv() throws StoreException {
        cbVorgang.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);
        erlm.setAenderungsKennzeichen(STANDARD);

        mwfCbVorgangConverterService.write(cbVorgang, erlm);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), TERMINVERSCHIEBUNG);
    }

    public void testWriteTvErlmOnTv() throws StoreException {
        cbVorgang.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);
        erlm.setAenderungsKennzeichen(TERMINVERSCHIEBUNG);

        mwfCbVorgangConverterService.write(cbVorgang, erlm);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), TERMINVERSCHIEBUNG);
    }

    public void testWriteTvOnAbm() throws Exception {
        writeAbm();
        mwfCbVorgangConverterService.writeTerminVerschiebung(cbVorgang, LocalDate.now(), null);

        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(TERMINVERSCHIEBUNG));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(STANDARD));
    }

    public void testWriteStornoOnAbm() throws Exception {
        writeAbm();
        mwfCbVorgangConverterService.writeStorno(cbVorgang, null);

        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STORNO));
        assertThat(cbVorgang.getLetztesGesendetesAenderungsKennzeichen(), equalTo(STANDARD));
    }

    private void writeAbm() throws StoreException {
        mwfCbVorgangConverterService.write(cbVorgang, abm);
    }

    private void assertOpenWithBemerkung() {
        assertFalse(cbVorgang.isAnswered());
        assertNull(cbVorgang.getReturnOk());
        assertTrue(cbVorgang.getReturnBemerkung().contains("0123 : Fehler"));
        assertThat("Real return date must not be set", cbVorgang.getReturnRealDate(), nullValue());
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
    }

    private void assertReturnOkWithRealDate() {
        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertTrue(cbVorgang.getReturnOk());
        assertTrue(cbVorgang.getReturnBemerkung().contains("0123 : Fehler"));
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertThat("Real return date must be set to Vorgabe Mnet",
                DateConverterUtils.asLocalDate(cbVorgang.getReturnRealDate()), equalTo(abm.getVerbindlicherLiefertermin()));
        assertThat(cbVorgang.getAenderungsKennzeichen(), equalTo(STANDARD));
    }

    private void createCBVorgang() {
        Long cbId = RandomUtils.nextLong();

        cbVorgang = new WitaCBVorgang();
        cbVorgang.setCbId(cbId);
        cbVorgang.setCarrierId(Carrier.ID_DTAG);
        cbVorgang.setAuftragId(Long.valueOf(11));
        cbVorgang.setVorgabeMnet(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, 14));
        cbVorgang.setTyp(CBVorgang.TYP_NEU);
        cbVorgang.setUsecaseId(7L);
        cbVorgang.setCarrierRefNr(String.format("%s", cbId));
        cbVorgang.setWitaGeschaeftsfallTyp(BEREITSTELLUNG);
    }

    private void createAbm() {
        abm = new AuftragsBestaetigungsMeldung(cbVorgang.getCarrierRefNr(), "5920312290", LIEFERTERMIN);
        abm.setVertragsNummer(VERTRAGSNUMMER);
        abm.setGeschaeftsfallTyp(BEREITSTELLUNG);
        abm.setAenderungsKennzeichen(STANDARD);

        LeitungsBezeichnung bezeichnung = new LeitungsBezeichnung("1234", "1234", "1234", "1234");
        Leitung leitung = new Leitung(bezeichnung);
        leitung.addLeitungsAbschnitt(new LeitungsAbschnitt(5, "0000", "0000"));
        leitung.addLeitungsAbschnitt(new LeitungsAbschnitt(2, "50", "25"));
        leitung.addLeitungsAbschnitt(new LeitungsAbschnitt(1, "100", "50"));
        leitung.addLeitungsAbschnitt(new LeitungsAbschnitt(3, "0", "0"));

        abm.setLeitung(leitung);
    }

    private void createAbbm() {
        abbm = new AbbruchMeldung(cbVorgang.getCarrierRefNr(), "5920312290");
        abbm.getMeldungsPositionen().add(new MeldungsPosition("0123", "Fehler"));
        abbm.setGeschaeftsfallTyp(BEREITSTELLUNG);
        abbm.setAenderungsKennzeichen(STANDARD);
    }

}
