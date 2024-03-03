/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2008 11:58:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.WebgatePW;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.ReportException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.billing.WebgatePWService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.reporting.ReportService;


/**
 * Command-Klasse, um alle Daten für das Maxi-Portal-Anschreiben zu ermitteln
 *
 *
 */
public class GetMaxiPortalDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetMaxiPortalDatenCommand.class);

    public static final String KUNDENOORIG = "KundeNoOrig";
    public static final String AUFTRAGID = "Auftrag_ID";
    public static final String PASSWORT = "Passwort";
    public static final String LOGIN = "Login";
    public static final String TAIFUNFORMATTEDADDRESS = "TaifunFormattedAddress";
    public static final String TAIFUNFORMATTEDANREDE = "TaifunFormattedAnrede";
    public static final String PARAMETER_RINFO = "RInfoNo";

    private Long kundeNoOrig = null;
    private Long orderNoOrig = null;
    private Long rInfoNo = null;
    private Long requestId = null;
    private String loginName = null;
    private Long auftragId = null;
    private Map<String, Object> map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap<String, Object>();
            rInfoNo = null;

            readMaxiPortal();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);

        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return MAXI_PORTAL;
    }

    /*
     * Ermittelt alle Daten für das Maxi-Portal-Anschreiben
     */
    private void readMaxiPortal() throws HurricanServiceCommandException {
        try {
            BillingAuftragService baService = getBillingService(BillingAuftragService.class);
            WebgatePWService pwService = getBillingService(WebgatePWService.class);
            ReportService repService = getReportService(ReportService.class);

            // Prüfe ob R-Info direkt übergeben wurde
            String rInfoString = repService.findReportParameter(requestId, PARAMETER_RINFO);
            if (StringUtils.isNotBlank(rInfoString)) {
                try {
                    rInfoNo = Long.valueOf(rInfoString);
                }
                catch (NumberFormatException e) {
                    LOGGER.debug("readMaxiPortal() - NFE für " + rInfoString);
                }
            }

            WebgatePW pw = null;

            // Ermittle R-Info zu Auftrag
            if (rInfoNo == null) {
                if (orderNoOrig == null) {
                    CCAuftragService aufService = getCCService(CCAuftragService.class);
                    AuftragDaten ad = aufService.findAuftragDatenByAuftragId(auftragId);
                    if (ad == null) {
                        throw new ReportException("Kann AuftragDaten-Objekt nicht ermitteln!");
                    }
                    orderNoOrig = ad.getAuftragNoOrig();
                }

                BAuftrag bAuftrag = baService.findAuftrag(orderNoOrig);
                if ((bAuftrag != null) && (bAuftrag.getRechInfoNoOrig() != null)) {
                    rInfoNo = bAuftrag.getRechInfoNoOrig();

                    // Zugangsdaten anhand der R-Info-No ermitteln
                    pw = pwService.findWebgatePW4RInfo(rInfoNo);
                }
                // Kein Billing-Auftrag vorhanden
                else {
                    pw = pwService.findFirstWebgatePW4Kunde(kundeNoOrig);
                    if (pw != null) {
                        rInfoNo = pw.getRinfoNoOrig();
                    }
                }
            }
            else {
                // Zugangsdaten anhand der R-Info-No ermitteln
                pw = pwService.findWebgatePW4RInfo(rInfoNo);
            }

            if (pw == null) {
                throw new ReportException("Konnte Zugangsdaten nicht ermitteln!");
            }

            RechnungsService reService = getBillingService(RechnungsService.class);
            // Bisher wurde die RInfo mit HistStatus=AKT verwendet
            RInfo rinfo = reService.findRInfo(rInfoNo);
            if (rinfo == null) {
                throw new ReportException("Kann R-Info nicht ermitteln!");
            }

            // Taifun-Adresse formatiert als Liste von Strings einfügen
            KundenService ks = getBillingService(KundenService.class);
            Adresse address = ks.getAdresseByAdressNo(rinfo.getAdresseNo());
            // Falls keine R-Info Adresse vorhanden, verwende Kundenadresse
            if (address == null) {
                address = ks.getAdresse4Kunde(kundeNoOrig);
            }
            String[] formatted = ks.formatAddress(address, AddressFormat.FORMAT_DEFAULT);
            List<String> adressList = null;
            if (formatted != null) {
                adressList = new ArrayList<>();
                Collections.addAll(adressList, formatted);
            }

            map.put(getPropName(TAIFUNFORMATTEDADDRESS), adressList);
            map.put(getPropName(LOGIN), NumberTools.convertToString(pw.getRinfoNoOrig(), null));
            map.put(getPropName(PASSWORT), pw.getPassword());
            map.put(getPropName(KUNDENOORIG), NumberTools.convertToString(kundeNoOrig, null));
            map.put(getPropName(AUFTRAGID), (auftragId != null) ? NumberTools.convertToString(auftragId, null) : null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }


    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;

        tmpId = getPreparedValue(KUNDE_NO_ORIG);
        kundeNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (kundeNoOrig == null) {
            throw new HurricanServiceCommandException("Kunde__No wurde dem Command-Objekt nicht uebergeben!");
        }

        tmpId = getPreparedValue(ORDER_NO_ORIG);
        orderNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;

        tmpId = getPreparedValue(REQUEST_ID);
        requestId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (requestId == null) {
            throw new HurricanServiceCommandException("RequestId wurde dem Command-Objekt nicht uebergeben!");
        }

        tmpId = getPreparedValue(USER_LOGINNAME);
        loginName = (tmpId instanceof String) ? (String) tmpId : null;
        if (loginName == null) {
            throw new HurricanServiceCommandException("User-Objekt wurde dem Command-Objekt nicht uebergeben!");
        }
    }


    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetMaxiPortalDatenCommand.properties";
    }

}


