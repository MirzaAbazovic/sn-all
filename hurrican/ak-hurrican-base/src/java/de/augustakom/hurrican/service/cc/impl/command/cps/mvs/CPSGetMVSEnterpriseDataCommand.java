/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 15:03:25
 */
package de.augustakom.hurrican.service.cc.impl.command.cps.mvs;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterprise;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSAdminAccount;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSEnterpriseData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSMVSEnterpriseLicences;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 * Ermittelt alle Daten die fuer die CPS-Provisionierung von MVS Enterprise Auftraegen noetig sind
 */
public class CPSGetMVSEnterpriseDataCommand extends AbstractGetMVSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetMVSEnterpriseDataCommand.class);

    private CPSMVSAdminAccount adminAcc;
    private CPSMVSEnterpriseLicences licenses;
    private Long resellerId;
    private String domain;

    private Long ccAuftragId;
    private AuftragMVSEnterprise auftragMVSEnterprise;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        String resultMsg = null;
        int cmdResult = ServiceCommandResult.CHECK_STATUS_OK;

        try {
            resellerId = findResellerId();
            domain = findDomain();
            adminAcc = findAdminAcc();
            licenses = findLicences();

            final CPSMVSEnterpriseData epData = new CPSMVSEnterpriseData(resellerId, adminAcc, domain, licenses);
            final CPSMVSData mvsData = new CPSMVSData(epData);
            final CPSServiceOrderData soData = getServiceOrderData();

            soData.setMvs(mvsData);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            cmdResult = ServiceCommandResult.CHECK_STATUS_INVALID;
            resultMsg = e.getMessage();
        }
        return ServiceCommandResult.createCmdResult(cmdResult, resultMsg, getClass());
    }

    @Override
    Long findResellerId() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        Auftrag auftrag = (getCCAuftragIdMVSEp() != null) ? getCCService(CCAuftragService.class).findAuftragById(
                getCCAuftragIdMVSEp()) : null;
        Long kundeNo = (auftrag != null) ? auftrag.getKundeNo() : null;
        Kunde kunde = (kundeNo != null) ? getBillingService(KundenService.class).findKunde(kundeNo) : null;
        Long resellerId = (kunde != null) ? kunde.getResellerKundeNo() : null;
        if (resellerId == null) {
            throw new HurricanServiceCommandException(String.format("Zum techn. Auftrag %s konnte keine Reseller "
                    + "ID ermittelt werden!", getCCAuftragIdMVSEp()));
        }
        return resellerId;
    }

    /**
     * @return Domaene die dem MVS Enterprise Auftrag zugeordnet wurde
     * @throws ServiceNotFoundException        falls ein benoetigter Service nicht gefunden wurde
     * @throws FindException                   im Falle eines unerwarteten Fehlers
     * @throws HurricanServiceCommandException falls dem Auftrag keine Domain zugeordnet wurde oder es sich nicht um ein
     *                                         MVS Enterprise Auftrag handelt
     */
    String findDomain() throws ServiceNotFoundException, FindException, HurricanServiceCommandException {
        if ((getAuftragMVSEnterprise() == null) || (getAuftragMVSEnterprise().getDomain() == null)) {
            throw new HurricanServiceCommandException(String.format("Zum techn. Auftrag %s konnte keine Enterprise "
                    + "Domain ermittelt werden!", getCCAuftragIdMVSEp()));
        }
        return getAuftragMVSEnterprise().getDomain();
    }

    /**
     * @return Email, Passwort und Benutzername die dem MVS Enterprise Auftrag zugeordnet wurden
     * @throws FindException                   im Falle eines unerwarteten Fehlers
     * @throws ServiceNotFoundException        falls ein benoetigter Service nicht gefunden wurde
     * @throws HurricanServiceCommandException falls dem Auftrag keine Email, Passwort oder Benutzername zugeordnet
     *                                         wurden oder es sich nicht um ein MVS Enterprise Auftrag handelt
     */
    CPSMVSAdminAccount findAdminAcc() throws FindException, ServiceNotFoundException, HurricanServiceCommandException {
        if (getAuftragMVSEnterprise() == null) {
            throw new HurricanServiceCommandException(String.format(
                    "Zum techn. Auftrag %s konnten keine MVS Enterprise Daten ermittelt werden!",
                    getCCAuftragIdMVSEp()));
        }
        final AuftragMVSEnterprise auftragMvsEP = getAuftragMVSEnterprise();
        if ((auftragMvsEP.getUserName() == null) || (auftragMvsEP.getMail() == null)
                || (auftragMvsEP.getPassword() == null)) {
            throw new HurricanServiceCommandException(String.format("Zum techn. Auftrag %s müssen die MVS Enterprise "
                            + "Daten 'Benutzername' %s, 'Passwort' %s und 'Email-Adress' %s angegeben sein!",
                    getCCAuftragIdMVSEp(), auftragMvsEP.getUserName(), auftragMvsEP.getPassword(),
                    auftragMvsEP.getMail()
            ));
        }
        final String username = auftragMvsEP.getUserName(), email = auftragMvsEP.getMail(), password = auftragMvsEP
                .getPassword();
        return new CPSMVSAdminAccount(username, password, email);
    }

    /**
     * Ermittelt die tatsächliche Anzahl aller zum Ausführungszeitpunkt aktiven Leistungen/Auftragspositionen auf dem
     * MVS Enterprise Auftrag aus Taifun, die fuer die CPS-Provisionierun relevant sind.
     *
     * @return die Anzahl an CPS-relevanten Taifun - Leistungen je Auftragsposition unter Beruecksichtigung von
     * Wertelisten (Wertelisten werden in diesem Kontext fuer Lizenzpakete verwendet und geben die Anzahl an Leistungen
     * pro Mengenwert je Auftragsposition an)
     * @throws FindException                   im Falle eines unerwarteten Fehlers
     * @throws ServiceNotFoundException        falls ein benoetigter Service nicht gefunden wurde
     * @throws HurricanServiceCommandException wenn nicht mindestens ein Lizenzpaket (extMiscNo == 1000) als
     *                                         Auftragsposition festgelegt wurde
     */
    CPSMVSEnterpriseLicences findLicences() throws FindException, ServiceNotFoundException,
            HurricanServiceCommandException {
        Map<Long, Long> lsCntByMiscNo = findLeistungen(Produkt.PROD_ID_MVS_ENTERPRISE);

        if ((lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_LIZENZPAKET) == null)
                || NumberTools.equal(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_LIZENZPAKET), 0L)) {
            final Long auftragNoOrig = getAuftragDaten().getAuftragNoOrig();
            throw new HurricanServiceCommandException(String.format(
                    "Es konnte kein Lizenzpaket zum Taifun-Auftrag mit Id %d ermittelt werden!", auftragNoOrig));
        }

        return createLicensesFromMap(lsCntByMiscNo);
    }

    private CPSMVSEnterpriseLicences createLicensesFromMap(Map<Long, Long> lsCntByMiscNo) {
        CPSMVSEnterpriseLicences licenses = new CPSMVSEnterpriseLicences();
        licenses.setAttendantConsole(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_ATTENDANT_CONSOLE));
        licenses.setFaxToMail(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_FAX2MAIL));
        licenses.setIvr(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_IVR));
        licenses.setLines(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_LIZENZPAKET));
        licenses.setMobileClient(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_MOBILE_CLIENT));
        licenses.setThreeWayConference(lsCntByMiscNo.get(Leistung.EXT_MISC_NO_MVS_3WAY_CONF));
        return licenses;
    }

    @Override
    Long getCCAuftragIdMVSEp() {
        if (ccAuftragId == null) {
            ccAuftragId = getAuftragDaten().getAuftragId();
        }
        return ccAuftragId;
    }

    @Override
    AuftragMVSEnterprise getAuftragMVSEnterprise() throws FindException, ServiceNotFoundException {
        if (auftragMVSEnterprise == null) {
            auftragMVSEnterprise = getCCService(MVSService.class).findMvsEnterprise4Auftrag(
                    getCCAuftragIdMVSEp());
        }
        return auftragMVSEnterprise;
    }
}
