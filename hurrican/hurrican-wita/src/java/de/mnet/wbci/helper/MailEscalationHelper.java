/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2014
 */
package de.mnet.wbci.helper;

import java.io.*;
import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.augustakom.common.service.iface.IServiceMode;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.mail.HtmlMailConstants;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.ticketing.escalation.XssfHtmlConverter;

/**
 *
 */
public class MailEscalationHelper implements HtmlMailConstants {

    private static final String[] escalataionTextLines = new String[] {
            "Sehr geehrte Damen und Herren,",
            "zum folgenden Vorgang fehlt uns die R\u00fcckmeldung:",
            "Gesch\u00e4ftsfalltyp:",
            "VA-ID:",
            "Aktueller Status:",
            "Mit freundlichen Gr\u00fc\u00dfen",
            "M-net Telekommunikations GmbH"
    };

    public static String getApplicationMode() {
        String mode = System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
        return (mode == null)? "<nicht gesetzt>": mode;
    }

    public static String generateCarrierOverviewReportSubject(Date date) {
        return "Liste WBCI-Eskalationen (Stand "
                + DateTools.formatDate(date, DateTools.PATTERN_DAY_MONTH_YEAR)
                + ", Applikationsmodus " + getApplicationMode()
                + ")";
    }

    public static String generateInternalOverviewReportSubject(Date date) {
        return "Interne Prueflisten WBCI (Stand " + DateTools.formatDate(date, DateTools.PATTERN_DAY_MONTH_YEAR)
                + ", Applikationsmodus " + getApplicationMode()
                + ")";
    }

    public static String generateCarrierReportSubject(CarrierCode carrierCode, Date date) {
        return "Liste WBCI-Eskalationen f\u00fcr " + carrierCode.getITUCarrierCode()
                + " (Stand " + DateTools.formatDate(date, DateTools.PATTERN_DAY_MONTH_YEAR)
                + ", Applikationsmodus " + getApplicationMode()
                + ")";
    }

    public static String generateCarrierOverviewReportEmailText(XSSFWorkbook overviewReport) throws IOException {
        return new StringBuilder()
                .append(getEscalationMailTitleIntern())
                .append("<p>zu den folgenden Vorg\u00e4ngen der unterschiedlichen Carriern fehlen uns die R\u00fcckmeldungen: </p> ")
                .append(convertToHtml(overviewReport))
                .append(getEscalationMailSignatureIntern())
                .toString();
    }

    public static String generateInternalOverviewReportEmailText(XSSFWorkbook... reports) throws IOException {
        StringBuilder sb = new StringBuilder().append(getEscalationMailTitleIntern())
                .append("<p>zu den folgenden Vorg\u00e4ngen der unterschiedlichen Carriern sind Fristen \u00fcberschritten:</p> ");
        if (reports != null) {
            for (XSSFWorkbook report : reports) {
                sb.append("<div><p/><p>").append(convertToHtml(report)).append("</p></div>");
            }
        }

        return sb.append(getEscalationMailSignatureIntern())
                .toString();
    }

    public static String getEscalationMailSignatureExtern() {
        return "<p>Mit freundlichen Gr\u00fc\u00dfen<br/>M-net Telekommunikations GmbH</p>" + HTML_FOOTER;
    }

    public static String getEscalationMailSignatureIntern() {
        return "<p>Viele Gr\u00fc\u00dfe<br/>M-Net Admin</p>" + HTML_FOOTER;
    }

    public static String getEscalationMailTitleExtern() {
        return HTML_HEADER + "Sehr geehrte Damen und Herren,";
    }

    public static String getEscalationMailTitleIntern() {
        return HTML_HEADER + "Hallo M-net-Kollegen,";
    }


    public static String getCarrierReportEmailText(XSSFWorkbook carrierReport) throws IOException {
        return new StringBuilder()
                .append(getEscalationMailTitleExtern())
                .append("<p>zu folgenden Vorg\u00e4ngen fehlen uns die R\u00fcckmeldungen: </p> ")
                .append(convertToHtml(carrierReport))
                .append(getEscalationMailSignatureExtern())
                .toString();
    }

    protected static String convertToHtml(XSSFWorkbook carrierReport) throws IOException {
        return XssfHtmlConverter.createEscalationHtmlReport(carrierReport);
    }

    public static String getCarrierEscalationEmailTextAsHtml(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId,
            WbciRequestStatus requestStatus) {
        final StringBuilder emailTextBuilder = new StringBuilder(HTML_HEADER)
                .append(escalataionTextLines[0])
                .append("<p>").append(escalataionTextLines[1]).append("</p>")
                .append("<b>").append(escalataionTextLines[2]).append("</b> %s<br/>")
                .append("<b>").append(escalataionTextLines[3]).append("</b> %s<br/>")
                .append("<b>").append(escalataionTextLines[4]).append("</b> %s<br/>")
                .append("<p>").append(escalataionTextLines[5]).append("<br/>")
                .append(escalataionTextLines[6]).append("</p>")
                .append(HTML_FOOTER);
        return getCarrierEscalationEmailText(emailTextBuilder, geschaeftsfallTyp, vorabstimmungsId, requestStatus);
    }

    public static String getCarrierEscalationEmailTextAsComment(GeschaeftsfallTyp geschaeftsfallTyp,
            String vorabstimmungsId,
            WbciRequestStatus requestStatus) {
        final StringBuilder commentTextBuilder = new StringBuilder()
                .append(escalataionTextLines[0]).append("\n")
                .append(escalataionTextLines[1]).append("\n\n")
                .append(escalataionTextLines[2]).append(" %s").append("\n")
                .append(escalataionTextLines[3]).append(" %s").append("\n")
                .append(escalataionTextLines[4]).append(" %s").append("\n\n")
                .append(escalataionTextLines[5]).append("\n")
                .append(escalataionTextLines[6]).append("\n");
        return getCarrierEscalationEmailText(commentTextBuilder, geschaeftsfallTyp, vorabstimmungsId, requestStatus);
    }

    private static String getCarrierEscalationEmailText(StringBuilder textBuilder, GeschaeftsfallTyp geschaeftsfallTyp,
            String vorabstimmungsId, WbciRequestStatus requestStatus) {
        return String.format(textBuilder.toString(), geschaeftsfallTyp, vorabstimmungsId, requestStatus);
    }

}
