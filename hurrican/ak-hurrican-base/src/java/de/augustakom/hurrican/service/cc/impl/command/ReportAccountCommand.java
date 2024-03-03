/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2005 08:39:57
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.impl.reportdata.AKOnlineJasperDS;
import de.augustakom.hurrican.service.cc.impl.reportdata.AdresseJasperDS;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Command-Klasse um die Reports fuer Auftraege mit Accounts zu erstellen.
 *
 *
 */
public class ReportAccountCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(ReportAccountCommand.class);

    /**
     * Value fuer die prepare-Methode, um die Auftrags-ID zu uebergeben.
     */
    public static final String AUFTRAG_ID = "auftrag.id";
    /**
     * Value fuer die prepare-Methode, um die Session-ID zu uebergeben.
     */
    public static final String SESSION_ID = "session.id";

    private Long auftragId = null;
    private Long sessionId = null;
    private String bearbeiter = null;

    // Liste mit den gedruckten Account-IDs.
    private List<Long> accIds = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        checkValues();

        try {
            AKUserService us = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            AKUser user = us.findUserBySessionId(sessionId);
            bearbeiter = (user != null) ? user.getName() : "System";
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return createReport();
    }

    /*
     * Erstellt den Report fuer das Account-Anschreiben.
     */
    private JasperPrint createReport() throws AKReportException {
        try {
            JasperPrint jasperPrint;

            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag auftrag = as.findAuftragById(auftragId);
            AuftragDaten auftragDaten = as.findAuftragDatenByAuftragId(auftragId);
            AuftragTechnik auftragTechnik = as.findAuftragTechnikByAuftragId(auftragId);

            if ((auftrag == null) || (auftragDaten == null) || (auftragTechnik == null)) {
                throw new AKReportException("Benötigte Auftrags-Daten konnten nicht ermittelt werden!");
            }

            long status = auftragDaten.getStatusId();
            if (status >= AuftragStatus.KUENDIGUNG) {
                if (status > AuftragStatus.KUENDIGUNG_ERFASSEN) {
                    jasperPrint = reportAKOnlineKuend(auftrag, auftragDaten);
                }
                else {
                    throw new AKReportException("Die Kündigung wurde noch nicht an Dispo geschickt!\n" +
                            "Ausdruck ist deshalb nicht möglich.");
                }
            }
            else if (status <= AuftragStatus.ABSAGE) {
                throw new AKReportException("Dieser Auftrag befindet sich in einem Status, in dem kein " +
                        "Kundenanschreiben erstellt werden kann.");
            }
            else {
                if ((auftragTechnik.getVpnId() != null) && (auftragTechnik.getVpnId() > 0)) {
                    throw new AKReportException("Der Auftrag ist einem VPN zugeordnet.\n " +
                            "Ausdruck der Accounts bitte über die VPN-Konfiguration.");
                }
                else {
                    jasperPrint = reportAKOnlineActive(auftrag);
                }
            }

            if (jasperPrint != null) {
                savePrintHistory(accIds, sessionId);
            }

            return jasperPrint;
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Der Report mit den Online-Daten konnte nicht erstellt werden!", e);
        }
    }

    /* Erstellt den Report fuer 'aktive' Online-Auftraege */
    private JasperPrint reportAKOnlineActive(Auftrag auftrag) throws AKReportException {
        try {
            AccountService accs = getCCService(AccountService.class);
            List<AuftragIntAccountView> views =
                    accs.findAuftragAccountView(auftrag.getKundeNo(), null);
            IntAccount vwAccount = accs.findIntAccount("" + auftrag.getKundeNo(),
                    IntAccount.LINR_VERWALTUNGSACCOUNT);
            accIds = new ArrayList<>();
            if (views != null) {
                for (AuftragIntAccountView v : views) {
                    accIds.add(v.getAccountId());
                }
            }

            if (vwAccount != null) {
                accIds.add(vwAccount.getId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("KUNDE__NO", auftrag.getKundeNo());

        AKJasperReportContext ctx = new AKJasperReportContext(
                "de/augustakom/hurrican/reports/auftrag/AKAccountMaster.jasper",
                params, new AKOnlineJasperDS(auftrag.getKundeNo()));

        AKJasperReportHelper jrh = new AKJasperReportHelper();
        return jrh.createReport(ctx);
    }

    /* Erstellt den Report fuer gekuendigte Online-Auftraege */
    private JasperPrint reportAKOnlineKuend(Auftrag auftrag, AuftragDaten auftragDaten) throws AKReportException {
        try {
            AdresseJasperDS adresseDS = new AdresseJasperDS(auftrag.getKundeNo());

            ProduktService ps = getCCService(ProduktService.class);
            Produkt produkt = ps.findProdukt(auftragDaten.getProdId());

            Map<String, Object> params = new HashMap<>();
            params.put("ADRESS_DATASOURCE", adresseDS);
            params.put("KUNDE__NO", auftrag.getKundeNo());
            params.put("BEARBEITER", bearbeiter);
            params.put("ANSCHLUSSART", produkt.getAnschlussart());
            params.put("KUENDIGUNGSTERMIN", auftragDaten.getKuendigung());

            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/auftrag/AKOnlineKuendigung.jasper",
                    params, new AKOnlineJasperDS(auftrag.getKundeNo()));

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AKReportException(e.getMessage(), e);
        }
    }

    /*
     * Erstellt einen Print-History Eintrag fuer alle gedruckten Accounts.
     */
    private void savePrintHistory(List<Long> accIds, Long sessionId) {
        try {
            if (accIds != null && !accIds.isEmpty() && auftragId != null) {
                // Report-Historie erstellen
                ReportService rs = getReportService(ReportService.class);
                CCAuftragService as = getCCService(CCAuftragService.class);

                ReportRequest req = new ReportRequest();
                req.setAuftragId(auftragId);
                req.setRepId(Report.REPORT_ID_JASPER_ACCOUNT);
                req.setRequestAt(DateTools.getActualSQLDate());
                req.setRequestFinishedAt(DateTools.getActualSQLDate());
                req.setReportDownloadedAt(DateTools.getActualSQLDate());
                Auftrag auf = as.findAuftragById(auftragId);
                if ((auf != null) && (auf.getKundeNo() != null)) {
                    req.setKundeNo(auf.getKundeNo());
                }
                AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
                if ((ad != null) && (ad.getAuftragNoOrig() != null)) {
                    req.setOrderNoOrig(ad.getAuftragNoOrig());
                }

                // User ermitteln
                AKUserService us = getAuthenticationService(
                        AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                AKUser user = us.findUserBySessionId(sessionId);

                req.setRequestFrom(((user != null) && (user.getLoginName() != null)) ? user.getLoginName() : bearbeiter);
                req.setRequestType("Jasper");

                // Teste not-null-Felder und speichere Datensatz
                if ((req.getRepId() != null) && (req.getKundeNo() != null)
                        && (req.getRequestFrom() != null) && (req.getRequestAt() != null)) {
                    rs.saveReportRequest(req);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /* Ueberprueft, ob alle benoetigten Daten uebergeben wurden. */
    private void checkValues() throws ServiceCommandException {
        auftragId = (getPreparedValue(AUFTRAG_ID) instanceof Long) ? (Long) getPreparedValue(AUFTRAG_ID) : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Auftrags-ID fehlt!");
        }

        sessionId = (getPreparedValue(SESSION_ID) instanceof Long) ? (Long) getPreparedValue(SESSION_ID) : null;
        if (sessionId == null) {
            throw new HurricanServiceCommandException("Session-ID fehlt!");
        }
    }

}


