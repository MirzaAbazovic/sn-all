/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2014
 */
package de.mnet.wbci.service.impl;

import java.io.*;
import java.time.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailAttachment;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.helper.AnswerDeadlineCalculationHelper;
import de.mnet.wbci.helper.MailEscalationHelper;
import de.mnet.wbci.model.BaseOverdueVo;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.OverdueAbmPvVO;
import de.mnet.wbci.model.OverdueWitaOrderVO;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciEscalationService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wbci.ticketing.escalation.EscalationReportGenerator;
import de.mnet.wbci.ticketing.escalation.OverdueWitaSpreadsheetConverter;

/**
 *
 */
public class WbciEscalationServiceImpl implements WbciEscalationService {

    private static final Logger LOGGER = Logger.getLogger(WbciEscalationServiceImpl.class);

    @Autowired
    private MailService mailService;
    @Autowired
    private EscalationReportGenerator escalationReportGenerator;
    @Autowired
    private RegistryService registryService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Autowired
    private QueryCCService queryService;

    protected static <T extends BaseOverdueVo> List<T> sortVOs(List<T> voList) {
        Collections.sort(voList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                Date date1 = o1.getWechseltermin();
                Date date2 = o2.getWechseltermin();
                if (date1 == date2) {
                    return 0;
                }
                if (date1 == null) {
                    return -1;
                }
                if (date2 == null) {
                    return 1;
                }
                return date1.compareTo(date2);
            }
        });
        return voList;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void sendCarrierEscalationOverviewReport() {
        LOGGER.info("start generating escalation overview report");
        String fileName = "Liste_WBCI_Eskalationen_" + DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN) + ".xlsx";
        XSSFWorkbook overviewReport = escalationReportGenerator.generateCarrierEscalationOverviewReport(fileName);
        try {
            sendEscalationMail(
                    MailEscalationHelper.generateCarrierOverviewReportSubject(new Date()),
                    MailEscalationHelper.generateCarrierOverviewReportEmailText(overviewReport),
                    true,
                    createMailAttachment(overviewReport, fileName)
            );

            LOGGER.info("end of generating carrier escalation overview report");
        }
        catch (Exception e) {
            throw new WbciServiceException("Some unexpected error occurs during the generation of the carrier escalation overview report!", e);
        }
    }

    @Override
    public void sendInternalOverviewReport() {
        LOGGER.info("start generating of the internal overview report");
        final String formattedDate = DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN);
        final String internalReportFileNameAufnehmend = String.format("Pruefliste_WBCI_Antwortfristen_AUF_%s.xlsx", formattedDate);
        final String internalReportFileNameAbgebend = String.format("Pruefliste_WBCI_Antwortfristen_ABG_%s.xlsx", formattedDate);
        final String abmPvReportFileName = String.format("WBCI_fehlende_ABM-PVs_%s.xlsx", formattedDate);
        final String witaOrdersReportFileName = String.format("WBCI_fehlerhafte_WITA_Bestellungen_%s.xlsx", formattedDate);

        final XSSFWorkbook internalReportAufnehmend = escalationReportGenerator.generateInternalDeadlineOverviewReport(CarrierRole.AUFNEHMEND, internalReportFileNameAufnehmend);
        final XSSFWorkbook internalReportAbgebend = escalationReportGenerator.generateInternalDeadlineOverviewReport(CarrierRole.ABGEBEND, internalReportFileNameAbgebend);
        final XSSFWorkbook abmPvReport = generateOverdueAbmPvReport(abmPvReportFileName);
        final XSSFWorkbook witaOrdersReport = generateOverdueWitaOrdersReport(witaOrdersReportFileName);

        try {
            sendEscalationMail(
                    MailEscalationHelper.generateInternalOverviewReportSubject(new Date()),
                    MailEscalationHelper.generateInternalOverviewReportEmailText(internalReportAufnehmend, internalReportAbgebend, abmPvReport, witaOrdersReport),
                    true,
                    createMailAttachment(internalReportAufnehmend, internalReportFileNameAufnehmend),
                    createMailAttachment(internalReportAbgebend, internalReportFileNameAbgebend),
                    createMailAttachment(abmPvReport, abmPvReportFileName),
                    createMailAttachment(witaOrdersReport, witaOrdersReportFileName)
            );

            LOGGER.info("end of generating of the internal overview report");
        }
        catch (Exception e) {
            throw new WbciServiceException("Some unexpected error occurs during the generation of the internal overview report!", e);
        }
    }

    protected void sendEscalationMail(final String subject, final String emailText, final boolean emailTextHTML, final MailAttachment... mailAttachments) throws FindException {
        Mail mail = new Mail();
        mail.setFrom(registryService.getStringValue(RegistryService.REGID_MAIL_FROM_HURRICAN));
        mail.setTo(registryService.getStringValue(RegistryService.REGID_WBCI_ESCALATION_TO));
        mail.setSubject(subject);

        if (emailText != null && emailText.length() > Mail.MAIL_TEXT_MAX_COLUMN_SIZE) {
            mail.setTextLong(emailText);
            mail.setIsTextLongHTML(emailTextHTML);
        }
        else {
            mail.setText(emailText);
            mail.setIsTextHTML(emailTextHTML);
        }

        if (mailAttachments != null) {
            for (MailAttachment mailAttachment : mailAttachments) {
                mail.addMailAttachment(mailAttachment);
            }
        }
        mailService.sendMailFromHurricanServer(mail);
    }

    @Override
    public List<CarrierCode> sendCarrierSpecificEscalationReports() {
        List<Carrier> carries = getWbciCarriers();
        List<CarrierCode> mailSentToCarriers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(carries)) {
            for (Carrier carrier : carries) {
                CarrierCode carrierCode = getCarrierCode(carrier);
                if (carrierCode != null) {
                    LOGGER.info("start of generating escalation overview report for carrier " + carrierCode.getITUCarrierCode());
                    String fileName = "Liste_WBCI_Eskalationen_" + carrierCode.getITUCarrierCode() + "_"
                            + DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR_HYPHEN) + ".xlsx";
                    XSSFWorkbook carrierReport = escalationReportGenerator.generateEscalationReportForCarrier(carrierCode, fileName);

                    try {
                        sendEscalationMail(
                                MailEscalationHelper.generateCarrierReportSubject(carrierCode, new Date()),
                                MailEscalationHelper.getCarrierReportEmailText(carrierReport),
                                true,
                                createMailAttachment(carrierReport, fileName)
                        );
                    }
                    catch (Exception e) {
                        throw new WbciServiceException(String.format("Some unexpected error occurs during the generation of the carrier escalation report for %s!", carrierCode.getITUCarrierCode()), e);
                    }

                    mailSentToCarriers.add(carrierCode);
                    LOGGER.info("end of generating escalation report for carrier " + carrierCode.getITUCarrierCode());
                }
            }
        }
        else {
            LOGGER.info("no WBCI carriers active => no carrier based escalation report generated!");
        }
        return mailSentToCarriers;

    }

    /**
     * returns a list with all WBCI Carriers or returns an empty List if no one have found!
     */
    protected List<Carrier> getWbciCarriers() {
        try {
            return carrierService.findWbciAwareCarrier();
        }
        catch (FindException e) {
            throw new WbciServiceException("Unexpected error during the search for WBCI aware carrier", e);
        }
    }

    @Override
    public void sendVaEscalationMailToCarrier(String vorabstimmungsId, AKUser user) {
        final WbciRequest wbciRequest = wbciCommonService.findLastWbciRequest(vorabstimmungsId);
        if (isEscalationPossible(wbciRequest)) {
            if (StringUtils.isBlank(user.getEmail())) {
                throw new WbciServiceException(
                        String.format("Für den Benutzer '%s' ist im System keine E-mail-Adresse hinterlegt," +
                                        "deshalb ist das Versenden einer Eskalationsmail nicht möglich.",
                                user.getLoginName()
                        )
                );
            }

            CarrierContact carrierContact = retrieveEscalationCarrierContact(wbciRequest);
            if (carrierContact == null || StringUtils.isBlank(carrierContact.getFaultClearingEmail())) {
                throw new WbciServiceException(
                        String.format("Es konnte kein WBCI-Eskalationskontakt für den Partner-Carrier '%s' gefunden" +
                                        "werden. Oder die E-mail-Adresse des WBCI-Eskalationskontakts ist nicht gesetzt. " +
                                        "Das Versenden einer Eskalationsmail ist deshalb nicht möglich.",
                                wbciRequest.getEKPPartner()
                        )
                );
            }

            final String requestVorabstimmungsId = retrieveVorabstimmungsId(wbciRequest);
            final String carrierEscalationEmailText = getCarrierEscalationEmailTextAsHtml(
                    wbciRequest.getWbciGeschaeftsfall().getTyp(),
                    requestVorabstimmungsId,
                    wbciRequest.getRequestStatus()
            );

            Mail mail = createEscalationMail(user, carrierContact, wbciRequest, carrierEscalationEmailText);
            mailService.sendMailFromHurricanServer(mail);

            final String carrierEscalationEmailTextAsComment = getCarrierEscalationEmailTextAsComment(
                    wbciRequest.getWbciGeschaeftsfall().getTyp(),
                    requestVorabstimmungsId,
                    wbciRequest.getRequestStatus());
            addMailToComment(wbciRequest.getWbciGeschaeftsfall(), user, carrierEscalationEmailTextAsComment);
        }
        else {
            throw new WbciServiceException("Die Eskalation ist für diesen Vorgang nicht möglich.");
        }
    }

    protected String getCarrierEscalationEmailTextAsHtml(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId, WbciRequestStatus requestStatus) {
        return MailEscalationHelper.getCarrierEscalationEmailTextAsHtml(geschaeftsfallTyp, vorabstimmungsId, requestStatus);
    }

    protected String getCarrierEscalationEmailTextAsComment(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId, WbciRequestStatus requestStatus) {
        return MailEscalationHelper.getCarrierEscalationEmailTextAsComment(geschaeftsfallTyp, vorabstimmungsId, requestStatus);
    }

    protected XSSFWorkbook generateOverdueAbmPvReport(String fileName) {
        try {
            LOGGER.info("start generating overdue ABM-PV report");
            List<OverdueAbmPvVO> overdueAbmPvVOs = sortVOs(wbciCommonService.findPreagreementsWithOverdueAbmPv());
            XSSFWorkbook result = OverdueWitaSpreadsheetConverter.convert(overdueAbmPvVOs, true);
            LOGGER.info("end of generating overdue ABM-PV report");
            return result;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Unexpected error during the generation of file '%s'", fileName), e);
        }
    }

    protected XSSFWorkbook generateOverdueWitaOrdersReport(String fileName) {
        try {
            LOGGER.info("start generating overdue WITA orders report");
            List<OverdueWitaOrderVO> overdueWitaOrderVOs = sortVOs(wbciWitaServiceFacade.getOverduePreagreementsWithMissingWitaOrder());
            XSSFWorkbook result = OverdueWitaSpreadsheetConverter.convert(overdueWitaOrderVOs, false);
            LOGGER.info("end of generating overdue WITA orders report");
            return result;
        }
        catch (Exception e) {
            throw new WbciServiceException(String.format("Unexpected error during the generation of file '%s'", fileName), e);
        }
    }

    protected boolean isEscalationPossible(WbciRequest wbciRequest) {
        Date deadlineDate = (wbciRequest.getAnswerDeadline() != null) ? Date.from(wbciRequest.getAnswerDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        final Integer daysUntilDeadline = AnswerDeadlineCalculationHelper.calculateDaysUntilDeadlinePartner(
                deadlineDate, wbciRequest.getIsMnetDeadline());
        return daysUntilDeadline != null && daysUntilDeadline < 0;
    }

    private void addMailToComment(WbciGeschaeftsfall gf, AKUser user, String mailText) {
        final StringBuilder commentBuilder = new StringBuilder()
                .append("Vorgang wurde am %s bei %s eskaliert, weil keine Rückmeldung vorlag!").append("\n")
                .append("Folgende E-mail wurde am Partnercarrier verschickt:").append("\n");
        final String comment = String.format(commentBuilder.toString(),
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR), gf.getEKPPartner());
        wbciCommonService.addComment(gf.getVorabstimmungsId(), comment + mailText, user);
    }

    protected Mail createEscalationMail(AKUser user, CarrierContact carrierContact, WbciRequest wbciRequest, String text) {
        Mail mail = new Mail();
        mail.setFrom(user.getEmail());
        mail.setTo(carrierContact.getFaultClearingEmail());
        String subject = "WBCI-Eskalation %s (%s) %s (Stand %s, Applikationsmodus %s)";
        subject = String.format(subject,
                wbciRequest.getWbciGeschaeftsfall().getTyp().getShortName(),
                wbciRequest.getEKPPartner().getITUCarrierCode(),
                retrieveVorabstimmungsId(wbciRequest),
                DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR),
                MailEscalationHelper.getApplicationMode());
        mail.setSubject(subject);

        mail.setText(text);
        mail.setIsTextHTML(true);
        return mail;
    }

    protected String retrieveVorabstimmungsId(WbciRequest wbciRequest) {
        if (wbciRequest instanceof VorabstimmungsAnfrage) {
            return wbciRequest.getVorabstimmungsId();
        }
        else if (wbciRequest instanceof TerminverschiebungsAnfrage) {
            return ((TerminverschiebungsAnfrage) wbciRequest).getAenderungsId();
        }
        else if (wbciRequest instanceof StornoAnfrage) {
            return ((StornoAnfrage) wbciRequest).getAenderungsId();
        }
        return null;
    }

    protected CarrierContact retrieveEscalationCarrierContact(WbciRequest wbciRequest) {
        try {
            Carrier carrier = carrierService.findCarrierByCarrierCode(wbciRequest.getEKPPartner());
            CarrierContact carrierContact = new CarrierContact();
            carrierContact.setCarrierId(carrier.getId());
            carrierContact.setContactType(CarrierContact.TYP_WBCI_ESCALATION_CONTACT);
            carrierContact = queryService.findUniqueByExample(carrierContact, CarrierContact.class);
            return carrierContact;
        }
        catch (FindException e) {
            throw new WbciServiceException(
                    String.format("Waehrend der Suche nach dem Carrier-Kontakt für den Carrier-Code '%s' zur " +
                                    "Vorabstimmungs-Id '%s' ist ein unerwarteter Fehler aufgetreten.",
                            wbciRequest.getEKPPartner(), wbciRequest.getVorabstimmungsId()
                    ), e
            );
        }
    }

    private CarrierCode getCarrierCode(Carrier carrier) {
        final String ituCarrierCode = carrier != null ? carrier.getItuCarrierCode() : null;
        return ituCarrierCode != null ? CarrierCode.fromITUCarrierCode(ituCarrierCode) : null;
    }

    protected MailAttachment createMailAttachment(XSSFWorkbook overviewReport, String fileName) throws IOException {
        //create byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        overviewReport.write(out);
        out.close();
        byte[] byteArray = out.toByteArray();

        MailAttachment attachment = new MailAttachment();
        attachment.setFileName(fileName);
        attachment.setAttachmentFile(byteArray);

        return attachment;
    }

}
