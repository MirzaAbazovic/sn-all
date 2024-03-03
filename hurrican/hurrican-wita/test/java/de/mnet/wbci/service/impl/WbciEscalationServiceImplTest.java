/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2014
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.*;
import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.EscalationListType.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierContactBuilder;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailAttachment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.helper.MailEscalationHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.EscalationPreAgreementVO;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wbci.ticketing.escalation.EscalationReportGenerator;
import de.mnet.wbci.ticketing.escalation.EscalationSpreadsheetConverter;
import de.mnet.wbci.ticketing.escalation.OverdueWitaSpreadsheetConverter;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WbciEscalationServiceImplTest {

    @Mock
    private MailService mailService;
    @Mock
    private EscalationReportGenerator escalationReportGenerator;
    @Mock
    private RegistryService registryService;
    @Mock
    private CarrierService carrierService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Mock
    private QueryCCService queryService;
    @Mock
    private Carrier carrierDTAG;
    @InjectMocks
    @Spy
    private WbciEscalationServiceImpl testling;

    private String toEmail = "temp-to@m-net.de";
    private String fromEmail = "temp-from@m-net.de";


    private static String generateString(int length) {
        StringBuilder outputBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            outputBuilder.append("a");
        }
        return outputBuilder.toString();
    }

    @DataProvider
    public static Object[][] sendEscalationMail() {
        return new Object[][] {
                { null },
                { generateString(4000) },
                { generateString(4001) },
        };
    }

    @DataProvider
    public static Object[][] isEscalationPossibleDataProvider() {
        return new Object[][] {
                { getDateInWorkingDaysFromNow(0).toLocalDate(), false, false },
                { getDateInWorkingDaysFromNow(1).toLocalDate(), false, false },
                { getDateInWorkingDaysFromNow(-1).toLocalDate(), false, true },
                { getDateInWorkingDaysFromNow(-1).toLocalDate(), true, false },
        };
    }

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        when(registryService.getStringValue(RegistryService.REGID_WBCI_ESCALATION_TO)).thenReturn(toEmail);
        when(registryService.getStringValue(RegistryService.REGID_MAIL_FROM_HURRICAN)).thenReturn(fromEmail);
    }

    @Test
    public void testSendEscaltionOverviewReport() throws Exception {
        final XSSFWorkbook overviewWB = EscalationSpreadsheetConverter.convert(new ArrayList<EscalationPreAgreementVO>(), new EscalationListConfiguration(CARRIER_OVERVIEW, null, null));
        final String fileName = "Liste_WBCI_Eskalationen_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN) + ".xlsx";
        when(escalationReportGenerator.generateCarrierEscalationOverviewReport(fileName)).thenReturn(overviewWB);

        testling.sendCarrierEscalationOverviewReport();
        verify(registryService, times(2)).getStringValue(anyLong());
        verify(escalationReportGenerator).generateCarrierEscalationOverviewReport(fileName);
        verify(mailService).sendMailFromHurricanServer(argThat(new BaseMatcher<Mail>() {
            @Override
            public boolean matches(Object o) {
                final Mail mail = (Mail) o;
                assertMailWithShortText(mail, true);
                assertFalse(mail.getSubject().contains(CarrierCode.DTAG.getITUCarrierCode()));
                assertTrue(mail.getSubject().startsWith("Liste WBCI-Eskalationen"), "started with: " + mail.getSubject());
                MailAttachment mailAttachment = mail.getMailAttachments().iterator().next();
                assertFalse(mailAttachment.getFileName().contains(CarrierCode.DTAG.getITUCarrierCode()));
                assertEquals(mailAttachment.getFileName(), fileName);
                assertNotNull(mailAttachment.getAttachmentFile());
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Some unexpected error occurs during the generation of the carrier escalation overview report!")
    public void expectExceptionWhenSendExternalEscalationOverviewReport() throws Exception {
        when(escalationReportGenerator.generateCarrierEscalationOverviewReport(null)).thenReturn(new XSSFWorkbook());
        doThrow(Exception.class).when(testling).createMailAttachment(any(XSSFWorkbook.class), anyString());
        testling.sendCarrierEscalationOverviewReport();
    }

    @Test
    public void testSendInternalEscaltionOverviewReport() throws Exception {
        final XSSFWorkbook internalWbAUF = EscalationSpreadsheetConverter.convert(new ArrayList<EscalationPreAgreementVO>(), new EscalationListConfiguration(INTERNAL, null, CarrierRole.AUFNEHMEND));
        final XSSFWorkbook internalWbABG = EscalationSpreadsheetConverter.convert(new ArrayList<EscalationPreAgreementVO>(), new EscalationListConfiguration(INTERNAL, null, CarrierRole.ABGEBEND));
        final XSSFWorkbook abmPv = OverdueWitaSpreadsheetConverter.convert(new ArrayList<OverdueAbmPvVO>(), true);
        final XSSFWorkbook witaOrder = OverdueWitaSpreadsheetConverter.convert(new ArrayList<OverdueWitaOrderVO>(), false);
        final String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN);
        final String fileNameAUF = String.format("Pruefliste_WBCI_Antwortfristen_AUF_%s.xlsx", formattedDate);
        final String fileNameABG = String.format("Pruefliste_WBCI_Antwortfristen_ABG_%s.xlsx", formattedDate);
        final String fileNameABMPV = String.format("WBCI_fehlende_ABM-PVs_%s.xlsx", formattedDate);
        final String fileNameWITA = String.format("WBCI_fehlerhafte_WITA_Bestellungen_%s.xlsx", formattedDate);

        when(escalationReportGenerator.generateInternalDeadlineOverviewReport(CarrierRole.AUFNEHMEND, fileNameAUF)).thenReturn(internalWbAUF);
        when(escalationReportGenerator.generateInternalDeadlineOverviewReport(CarrierRole.ABGEBEND, fileNameABG)).thenReturn(internalWbABG);
        doReturn(abmPv).when(testling).generateOverdueAbmPvReport(fileNameABMPV);
        doReturn(witaOrder).when(testling).generateOverdueWitaOrdersReport(fileNameWITA);

        testling.sendInternalOverviewReport();
        verify(registryService, times(2)).getStringValue(anyLong());
        verify(escalationReportGenerator).generateInternalDeadlineOverviewReport(CarrierRole.AUFNEHMEND, fileNameAUF);
        verify(escalationReportGenerator).generateInternalDeadlineOverviewReport(CarrierRole.ABGEBEND, fileNameABG);
        verify(testling).generateOverdueWitaOrdersReport(fileNameWITA);
        verify(testling).generateOverdueAbmPvReport(fileNameABMPV);
        verify(mailService).sendMailFromHurricanServer(argThat(new BaseMatcher<Mail>() {
            @Override
            public boolean matches(Object o) {
                final Mail mail = (Mail) o;
                assertMailWithLongText(mail, true);
                assertFalse(mail.getSubject().contains(CarrierCode.DTAG.getITUCarrierCode()));
                assertTrue(mail.getSubject().startsWith("Interne Prueflisten WBCI (Stand "), "started with: " + mail.getSubject());

                Iterator<MailAttachment> it = mail.getMailAttachments().iterator();
                MailAttachment maInternalWbAUF = it.next();
                assertFalse(maInternalWbAUF.getFileName().contains(CarrierCode.DTAG.getITUCarrierCode()));
                assertEquals(maInternalWbAUF.getFileName(), fileNameAUF);
                assertNotNull(maInternalWbAUF.getAttachmentFile());

                MailAttachment maInternalWbABG = it.next();
                assertFalse(maInternalWbABG.getFileName().contains(CarrierCode.DTAG.getITUCarrierCode()));
                assertEquals(maInternalWbABG.getFileName(), fileNameABG);
                assertNotNull(maInternalWbABG.getAttachmentFile());

                MailAttachment abmPvWb = it.next();
                assertEquals(abmPvWb.getFileName(), fileNameABMPV);
                assertNotNull(abmPvWb.getAttachmentFile());

                MailAttachment witaWb = it.next();
                assertEquals(witaWb.getFileName(), fileNameWITA);
                assertNotNull(witaWb.getAttachmentFile());

                assertFalse(it.hasNext());
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }


    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the generation of file 'WBCI_fehlende_ABM-PVs.xlsx'")
    public void expectExceptionWhenGenerateAbmPVReport() throws Exception {
        doThrow(Exception.class).when(wbciCommonService).findPreagreementsWithOverdueAbmPv();
        testling.generateOverdueAbmPvReport("WBCI_fehlende_ABM-PVs.xlsx");
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the generation of file 'WBCI_fehlerhafte_WITA_Bestellungen.xlsx'")
    public void expectExceptionWhenGenerateWitaReport() throws Exception {
        doThrow(Exception.class).when(wbciWitaServiceFacade).getOverduePreagreementsWithMissingWitaOrder();
        testling.generateOverdueWitaOrdersReport("WBCI_fehlerhafte_WITA_Bestellungen.xlsx");
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Some unexpected error occurs during the generation of the internal overview report!")
    public void expectExceptionWhenSendInternalEscalationOverviewReport() throws Exception {
        when(escalationReportGenerator.generateInternalDeadlineOverviewReport(any(CarrierRole.class), anyString())).thenReturn(new XSSFWorkbook());
        doReturn(new XSSFWorkbook()).when(testling).generateOverdueAbmPvReport(anyString());
        doReturn(new XSSFWorkbook()).when(testling).generateOverdueWitaOrdersReport(anyString());
        doThrow(Exception.class).when(testling).sendEscalationMail(anyString(), anyString(), anyBoolean(), any(MailAttachment.class));
        testling.sendInternalOverviewReport();
    }

    @Test
    public void testSortVOs() throws Exception {
        Date baseDate = new Date();
        OverdueWitaOrderVO vo1 = new OverdueWitaOrderVO();
        vo1.setWechseltermin(baseDate);
        OverdueWitaOrderVO vo2 = new OverdueWitaOrderVO();
        vo2.setWechseltermin(DateUtils.addDays(baseDate, 1));

        List<OverdueWitaOrderVO> result = WbciEscalationServiceImpl.sortVOs(Arrays.asList(vo2, vo1));
        Assert.notNull(result);
        assertEquals(result.get(0), vo1);
        assertEquals(result.get(1), vo2);
    }

    @Test(dataProvider = "sendEscalationMail")
    public void testSendEscalationMail(final String mailText) throws Exception {
        final String subject = "SUBJECT";
        final boolean html = false;
        final MailAttachment mailAttachment = new MailAttachment();
        testling.sendEscalationMail(subject, mailText, html, mailAttachment);
        verify(mailService).sendMailFromHurricanServer(argThat(new BaseMatcher<Mail>() {
            @Override
            public boolean matches(Object o) {
                final Mail mail = (Mail) o;
                if (mailText == null) {
                    assertNull(mail.getText());
                    assertNull(mail.getTextLong());
                }
                else if (mailText.length() > 4000) {
                    assertMailWithLongText(mail, html);
                }
                else {
                    assertMailWithShortText(mail, html);
                }
                assertEquals(mail.getSubject(), subject);
                assertEquals(mail.getMailAttachments().iterator().next(), mailAttachment);
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    @Test
    public void testSendEscalationCarrierReports() throws Exception {
        final String fileName = "Liste_WBCI_Eskalationen_" + CarrierCode.DTAG.getITUCarrierCode() + "_"
                + DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN) + ".xlsx";

        when(carrierDTAG.getItuCarrierCode()).thenReturn(CarrierCode.DTAG.getITUCarrierCode());
        when(carrierService.findWbciAwareCarrier()).thenReturn(Arrays.asList(carrierDTAG));
        final XSSFWorkbook carrierWB = EscalationSpreadsheetConverter.convert(new ArrayList<EscalationPreAgreementVO>(), new EscalationListConfiguration(CARRIER_SPECIFIC, CarrierCode.DTAG, null));
        when(escalationReportGenerator.generateEscalationReportForCarrier(CarrierCode.DTAG, fileName)).thenReturn(carrierWB);
        final List<CarrierCode> carrierCodes = testling.sendCarrierSpecificEscalationReports();
        assertEquals(carrierCodes.size(), 1);
        assertEquals(carrierCodes.get(0), CarrierCode.DTAG);
        verify(registryService, times(2)).getStringValue(anyLong());
        verify(escalationReportGenerator).generateEscalationReportForCarrier(CarrierCode.DTAG, fileName);
        verify(mailService).sendMailFromHurricanServer(argThat(new BaseMatcher<Mail>() {
            @Override
            public boolean matches(Object o) {
                final Mail mail = (Mail) o;
                assertMailWithShortText(mail, true);
                assertTrue(mail.getText().startsWith(MailEscalationHelper.HTML_HEADER));
                assertTrue(mail.getText().endsWith(MailEscalationHelper.HTML_FOOTER));
                assertTrue(mail.getSubject().contains(CarrierCode.DTAG.getITUCarrierCode()));

                Iterator<MailAttachment> it = mail.getMailAttachments().iterator();
                MailAttachment mailAttachment = it.next();
                assertEquals(mailAttachment.getFileName(), fileName);
                assertNotNull(mailAttachment);
                assertFalse(it.hasNext());
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Some unexpected error occurs during the generation of the carrier escalation report for DEU.DTAG!")
    public void expectExceptionWhenSendCarrierEscalationOverviewReport() throws Exception {
        when(carrierDTAG.getItuCarrierCode()).thenReturn(CarrierCode.DTAG.getITUCarrierCode());
        when(carrierService.findWbciAwareCarrier()).thenReturn(Arrays.asList(carrierDTAG));
        when(escalationReportGenerator.generateEscalationReportForCarrier(any(CarrierCode.class), anyString())).thenReturn(new XSSFWorkbook());
        doThrow(Exception.class).when(testling).createMailAttachment(any(XSSFWorkbook.class), anyString());
        testling.sendCarrierSpecificEscalationReports();
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the search for WBCI aware carrier")
    public void testExceptionDuringFindWbciCarrier() throws Exception {
        when(carrierService.findWbciAwareCarrier()).thenThrow(new FindException());
        testling.getWbciCarriers();
    }

    private void assertMailWithShortText(Mail mail, boolean isHtml) {
        assertMail(mail);
        assertNotNull(mail.getText());
        assertNull(mail.getTextLong());
        assertEquals(mail.getIsTextHTML(), isHtml);
    }

    private void assertMailWithLongText(Mail mail, boolean isHtml) {
        assertMail(mail);
        assertNotNull(mail.getTextLong());
        assertNull(mail.getText());
        assertEquals(mail.getIsTextLongHTML(), isHtml);
    }

    private void assertMail(Mail mail) {
        assertEquals(mail.getFrom(), fromEmail);
        assertEquals(mail.getTo(), toEmail);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Es konnte kein.*")
    public void testSendEscalationMailToCarrierNoEscalationContact() throws FindException {
        final String vorabstimmungsId = "DEU.MNET.V123456789";
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastWbciRequest(vorabstimmungsId)).thenReturn(vorabstimmungsAnfrage);
        doReturn(true).when(testling).isEscalationPossible(vorabstimmungsAnfrage);
        doReturn(null).when(testling).retrieveEscalationCarrierContact(vorabstimmungsAnfrage);
        testling.sendVaEscalationMailToCarrier(vorabstimmungsAnfrage.getVorabstimmungsId(), new AKUserBuilder().build());
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Es konnte kein.*")
    public void testSendEscalationMailToCarrierNoEscalationContactEmail() throws FindException {
        final String vorabstimmungsId = "DEU.MNET.V123456789";
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastWbciRequest(vorabstimmungsId)).thenReturn(vorabstimmungsAnfrage);
        doReturn(true).when(testling).isEscalationPossible(vorabstimmungsAnfrage);
        final CarrierContact carrierContact = new CarrierContactBuilder().build();
        doReturn(carrierContact).when(testling).retrieveEscalationCarrierContact(vorabstimmungsAnfrage);
        testling.sendVaEscalationMailToCarrier(vorabstimmungsAnfrage.getVorabstimmungsId(), new AKUserBuilder().build());
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Für den Benutzer .*")
    public void testSendEscalationMailToCarrierNoUserEmail() throws FindException {
        final String vorabstimmungsId = "DEU.MNET.V123456789";
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastWbciRequest(vorabstimmungsId)).thenReturn(vorabstimmungsAnfrage);
        doReturn(true).when(testling).isEscalationPossible(vorabstimmungsAnfrage);
        final CarrierContact carrierContact =
                new CarrierContactBuilder()
                        .withFaultClearingEmail("wbci@telekom.de")
                        .build();
        doReturn(carrierContact).when(testling).retrieveEscalationCarrierContact(vorabstimmungsAnfrage);
        testling.sendVaEscalationMailToCarrier(vorabstimmungsAnfrage.getVorabstimmungsId(), new AKUserBuilder().withEmail(null).build());
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Die Eskalation ist für diesen Vorgang nicht möglich.")
    public void testSendEscalationMailToCarrierEscalationNotPossible() throws FindException {
        final String vorabstimmungsId = "DEU.MNET.V123456789";
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastWbciRequest(vorabstimmungsId)).thenReturn(vorabstimmungsAnfrage);
        doReturn(false).when(testling).isEscalationPossible(vorabstimmungsAnfrage);
        testling.sendVaEscalationMailToCarrier(vorabstimmungsAnfrage.getVorabstimmungsId(), new AKUserBuilder().build());
    }

    @Test
    public void testSendEscalationMailToCarrier() throws FindException {
        final String vorabstimmungsId = "DEU.MNET.V123456789";
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withVorabstimmungsId(vorabstimmungsId)
                                        .build()
                        )
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastWbciRequest(vorabstimmungsId)).thenReturn(vorabstimmungsAnfrage);
        doReturn(true).when(testling).isEscalationPossible(vorabstimmungsAnfrage);
        final CarrierContact carrierContact =
                new CarrierContactBuilder()
                        .withFaultClearingEmail("wbci@telekom.de")
                        .build();
        doReturn(carrierContact).when(testling).retrieveEscalationCarrierContact(vorabstimmungsAnfrage);
        doReturn(vorabstimmungsId).when(testling).retrieveVorabstimmungsId(vorabstimmungsAnfrage);
        final String carrierEscalationEmailTextAsHtml = "html mail text";
        doReturn(carrierEscalationEmailTextAsHtml).when(testling).getCarrierEscalationEmailTextAsHtml(
                vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp(), vorabstimmungsId,
                vorabstimmungsAnfrage.getRequestStatus());
        final String carrierEscalationEmailTextAsComment = "comment mail text";
        doReturn(carrierEscalationEmailTextAsComment).when(testling).getCarrierEscalationEmailTextAsComment(
                vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp(), vorabstimmungsId,
                vorabstimmungsAnfrage.getRequestStatus());
        final AKUser user = new AKUserBuilder().build();
        Mail mail = mock(Mail.class);
        doReturn(mail).when(testling).createEscalationMail(user, carrierContact, vorabstimmungsAnfrage,
                carrierEscalationEmailTextAsHtml);

        testling.sendVaEscalationMailToCarrier(vorabstimmungsAnfrage.getVorabstimmungsId(), user);

        verify(wbciCommonService).findLastWbciRequest(vorabstimmungsId);
        verify(testling).retrieveEscalationCarrierContact(vorabstimmungsAnfrage);
        verify(testling).retrieveVorabstimmungsId(vorabstimmungsAnfrage);
        verify(testling).getCarrierEscalationEmailTextAsHtml(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp(),
                vorabstimmungsId, vorabstimmungsAnfrage.getRequestStatus());
        verify(testling).getCarrierEscalationEmailTextAsComment(vorabstimmungsAnfrage.getWbciGeschaeftsfall().getTyp(),
                vorabstimmungsId, vorabstimmungsAnfrage.getRequestStatus());
        verify(testling).createEscalationMail(user, carrierContact, vorabstimmungsAnfrage, carrierEscalationEmailTextAsHtml);
        verify(mailService).sendMailFromHurricanServer(mail);
        verify(wbciCommonService).addComment(eq(vorabstimmungsId),
                argThat(new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        return ((String) o).contains(carrierEscalationEmailTextAsComment);
                    }
                }),
                eq(user)
        );
    }

    @Test(expectedExceptions = WbciServiceException.class,
            expectedExceptionsMessageRegExp = "Waehrend der Suche nach dem Carrier-Kontakt für den Carrier-Code.*")
    public void testRetrieveEscalationCarrierContactExceptionThrown() throws Exception {
        final TerminverschiebungsAnfrage<WbciGeschaeftsfall> terminverschiebungsAnfrage =
                new TerminverschiebungsAnfrageBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withAbgebenderEKP(CarrierCode.MNET)
                                        .withAufnehmenderEKP(CarrierCode.DTAG)
                                        .build()
                        )
                        .build();
        when(carrierService.findCarrierByCarrierCode(any(CarrierCode.class))).thenThrow(new FindException());
        testling.retrieveEscalationCarrierContact(terminverschiebungsAnfrage);
    }

    @Test(dataProvider = "isEscalationPossibleDataProvider")
    public void testIsEscalationPossible(LocalDate answerDeadline, boolean isMnetDeadline, boolean expectedResult) {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> vorabstimmungsAnfrage =
                new VorabstimmungsAnfrageTestBuilder<>()
                        .withAnswerDeadline(answerDeadline)
                        .withIsMnetDeadline(isMnetDeadline)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        assertEquals(testling.isEscalationPossible(vorabstimmungsAnfrage), expectedResult);
    }

    @Test
    public void testRetrieveEscalationCarrierContact() throws Exception {
        final TerminverschiebungsAnfrage<WbciGeschaeftsfall> terminverschiebungsAnfrage =
                new TerminverschiebungsAnfrageBuilder<>()
                        .withWbciGeschaeftsfall(
                                new WbciGeschaeftsfallKueMrnBuilder()
                                        .withAbgebenderEKP(CarrierCode.MNET)
                                        .withAufnehmenderEKP(CarrierCode.DTAG)
                                        .build()
                        )
                        .build();
        when(carrierDTAG.getId()).thenReturn(1L);
        when(carrierService.findCarrierByCarrierCode(eq(CarrierCode.DTAG))).thenReturn(carrierDTAG);
        CarrierContact carrierContactDb = new CarrierContactBuilder().build();
        when(queryService.findUniqueByExample(any(CarrierContact.class), eq(CarrierContact.class))).thenReturn(
                carrierContactDb);

        final CarrierContact escalationCarrierContact = testling
                .retrieveEscalationCarrierContact(terminverschiebungsAnfrage);
        assertEquals(escalationCarrierContact, carrierContactDb);
        verify(carrierDTAG).getId();
        verify(carrierService).findCarrierByCarrierCode(eq(CarrierCode.DTAG));
        verify(queryService).findUniqueByExample(any(CarrierContact.class), eq(CarrierContact.class));
    }

}
