/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 09:42:02
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.base.DefaultDAOService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung.MassenbenachrichtigungDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.temp.MassenbenachrichtigungDaten;
import de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.MassenbenachrichtigungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Implementierung der Massenbenachrichtigungen.
 */
@CcTxRequiredReadOnly
public class MassenbenachrichtigungServiceImpl extends DefaultDAOService implements MassenbenachrichtigungService {

    private static final Logger LOGGER = Logger.getLogger(MassenbenachrichtigungServiceImpl.class);

    private final static Long TSX_TYP_ID_FAX = Long.valueOf(2);
    private final static Long TSX_TYP_ID_EMAIL = Long.valueOf(1);
    private final static Long TSX_TYP_ID_FAX_EMAIL = Long.valueOf(3);

    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    private AnsprechpartnerService ansprechpartnerService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.dao.exmodules.massenbenachrichtigung.MassenbenachrichtigungDAO")
    MassenbenachrichtigungDAO massenbenachrichtigungDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Autowired
    private AKUserService userService;


    /**
     * Nutzt eine vorhandene Massenbenachrichtigungs-Funktionalitaet, indem die fuer die Massenbenachrichtigung
     * relevanten Daten in eine Tabelle geschrieben werden, die ein Datenbanklink auf die KuP-Datenbank ist.
     */
    @Override
    @CcTxRequired
    public void triggerMassenbenachrichtigung(List<MassenbenachrichtigungDaten> auftragsDatenList, String template,
            boolean fax, boolean email, String ticketNr, String text, Date from, Date till, Long sessionId) {
        try {
            AKUser user = userService.findUserBySessionId(sessionId);

            Date now = new Date();
            for (MassenbenachrichtigungDaten auftragsDaten : auftragsDatenList) {
                TServiceExp model = createFromAuftrag(auftragsDaten.getAuftragDaten());
                model.setTsxVpvAsamPort(auftragsDaten.getPort());        // TSX_VPV_ASAMPORT        Port-Nummer
                model.setTsxVpvAsambezeichnung(auftragsDaten.getRack()); // TSX_VPV_ASAMBEZEICHNUNG Managementbezeichnung aus Hurrican (zB AB-HIRAU003-01)
                model.setTsxVpvAsamkarte(auftragsDaten.getKarte());      // TSX_VPV_ASAMKARTE       Karte
                model.setTsxTypId(getTsxTypId(fax, email, model)); // TSX_TYP_ID Siehe Text1 (abhaengig Checkboxen
                // Fax/Email) (Formular)
                model.setTsxTicketNr(ticketNr);                     // TSX_TICKETNR            Eingegebene Nr (Formular)
                model.setTsxText1(text);                            // TSX_TEXT1               Eingegebener Text (Formular)
                model.setTsxVon(from);                              // TSX_VON                 Von-Datum (Formular)
                model.setTsxTicketNrAm(till);                       // TSX_TICKETNR_AM         Bis-Datum (Formular)
                model.setTsxTimestamp(now);                         // TSX_TIMESTAMP           Aktueller Zeitstempel
                model.setTsxTemplate(template); // TSX_TEMPLATE Gemaess Formular, Werte siehe Sheet FAXVORLAGEN
                model.setTsxUserName(user.getLoginName());          // TSX_USERNAME            Username

                LOGGER.info("Triggering Massenbenachrichtigung: " + model.toString());
                massenbenachrichtigungDAO.store(model);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (AKAuthenticationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Setzt die tsxTypId (abhaengig Checkboxen Fax/Email) (Formular)
     */
    private Long getTsxTypId(boolean fax, boolean email, TServiceExp model) {
        if (fax && email) {
            // TSX_TYP_ID Siehe Text1 (abhaengig Checkboxen Fax/Email) (Formular)
            return MassenbenachrichtigungServiceImpl.TSX_TYP_ID_FAX_EMAIL;
        }
        else if (fax && !email) {
            return MassenbenachrichtigungServiceImpl.TSX_TYP_ID_FAX;
        }
        else if (!fax && email) {
            return MassenbenachrichtigungServiceImpl.TSX_TYP_ID_EMAIL;
        }
        LOGGER.warn("setTsxTypId() - neither fax and email should be sent!");
        return null;
    }


    @Override
    public TServiceExp createFromAuftrag(AuftragDaten auftragDaten) throws ServiceNotFoundException, FindException {
        TServiceExp model = new TServiceExp();
        model.setTsxAufId(auftragDaten.getAuftragId());                  // TSX_AUF_ID        Auftrags-ID
        model.setTsxIstTermin(auftragDaten.getInbetriebnahme());         // TSX_ISTTERMIN            Inbetriebnahmetermin
        model.setTsxKuendigungZum(auftragDaten.getKuendigung()); // TSX_KUENDIGUNGZUM Kuendigungstermin

        String produktName = produktService.generateProduktName4Auftrag(auftragDaten.getAuftragId());
        model.setTsxProdukt(produktName);                               // TSX_PRODUKT              Produkt Hurrican

        AuftragStatus auftragStatus = ccAuftragService.findAuftragStatus(auftragDaten.getStatusId());

        if (null != auftragStatus) {
            model.setTsxStatus(auftragStatus.getStatusText());          // TSX_STATUS               Auftragsstatus (Hurrican)
        }

        if (auftragDaten.getAuftragNoOrig() != null) {
            VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
            if (vbz != null) {
                model.setTsxAuftragsnummer(vbz.getVbz());                   // TSX_AUFTRAGSNUMMER       Verbindungsbezeichnung
            }

            BAuftrag bAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
            if (bAuftrag != null) {
                // Postadresse laden -> Name
                // KundenService.getAdresse4Kunde
                Adresse adresse = billingAuftragService.findAnschlussAdresse4Auftrag(bAuftrag.getAuftragNoOrig(), Endstelle.ENDSTELLEN_TYP_B);

                if (null != adresse) {
                    model.setTsxKunde(adresse.getName());                   // TSX_KUNDE                Name des Kunden

                    // Postadresse laden -> Name
                    // KundenService.getAdresse4Kunde
                    model.setTsxPlz(adresse.getPlz());                      // TSX_PLZ                  Postadresse des Kunden (aus Taifun)
                    model.setTsxOrt(adresse.getOrt());                      // TSX_ORT                  Postadresse des Kunden (aus Taifun)
                    model.setTsxStrasse(adresse.getStrasse());              // TSX_STRASSE              Postadresse des Kunden (aus Taifun)
                    model.setTsxHausnr(adresse.getNummer());                // TSX_HAUSNR               Postadresse des Kunden (aus Taifun)
                    model.setTsxWohnung(adresse.getFloor());                // TSX_WOHNUNG              Postadresse des Kunden (aus Taifun)
                }
            }
        }

        CCAddress addressHotlineService = getAdressForAnsprechpartner(model.getTsxAufId(), Ansprechpartner.Typ.HOTLINE_SERVICE);
        CCAddress addressTechService = getAdressForAnsprechpartner(model.getTsxAufId(), Ansprechpartner.Typ.TECH_SERVICE);
        CCAddress addressKunde = getAdressForAnsprechpartner(model.getTsxAufId(), Ansprechpartner.Typ.KUNDE);

        if (null != addressHotlineService) {
            model.setTsxAnspr1(addressHotlineService.getName());       // TSX_ANSPR1               Ansprechpartner Typ Hotline Service
            model.setTsxAnsprTel1(addressHotlineService.getTelefon()); // TSX_ANSPR_TEL1           Ansprechpartner Typ Hotline Service
            model.setTsxAnsprFax1(addressHotlineService.getFax());     // TSX_ANSPR_FAX1           Ansprechpartner Typ Hotline Service
            model.setTsxAnsprEmail1(addressHotlineService.getEmail()); // TSX_ANSPR_EMAIL1         Ansprechpartner Typ Hotline Service
        }

        if ((addressHotlineService == null) ||
                (StringUtils.isEmpty(addressHotlineService.getEmail()) && StringUtils.isEmpty(addressHotlineService.getFax()))) {
            if (null != addressTechService) {
                model.setTsxAnspr2(addressTechService.getName());       // TSX_ANSPR2               Ansprechpartner Typ Technischer Service
                model.setTsxAnsprTel2(addressTechService.getTelefon()); // TSX_ANSPR_TEL2           Ansprechpartner Typ Technischer Service
                model.setTsxAnsprFax2(addressTechService.getFax());     // TSX_ANSPR_FAX2           Ansprechpartner Typ Technischer Service
                model.setTsxAnsprEmail2(addressTechService.getEmail()); // TSX_ANSPR_EMAIL2         Ansprechpartner Typ Technischer Service
            }

            if (null != addressKunde) {
                model.setTsxAnspr3(addressKunde.getName());             // TSX_ANSPR3               Ansprechpartner Typ Kunde
                model.setTsxAnsprTel3(addressKunde.getTelefon());       // TSX_ANSPR_TEL3           Ansprechpartner Typ Kunde
                model.setTsxAnsprFax3(addressKunde.getFax());           // TSX_ANSPR_FAX3           Ansprechpartner Typ Kunde
                model.setTsxAnsprEmail3(addressKunde.getEmail());       // TSX_ANSPR_EMAIL3         Ansprechpartner Typ Kunde
            }
        }
        return model;
    }

    /**
     * Ermittelt die Adresse des Ansprechpartners in Abhaengigkeit von Auftrag und Typ
     *
     * @param auftragId
     * @param typ
     * @return CCAddress
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    private CCAddress getAdressForAnsprechpartner(Long auftragId, Ansprechpartner.Typ typ) throws ServiceNotFoundException, FindException {
        List<Ansprechpartner> ansprechpartnerList = ansprechpartnerService.findAnsprechpartner(typ, auftragId);

        if (CollectionTools.isNotEmpty(ansprechpartnerList)) {
            Ansprechpartner ansprechpartner = ansprechpartnerList.get(0);
            CCAddress address = ansprechpartner.getAddress();
            return address;
        }
        return null;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * Injected
     */
    public void setAnsprechpartnerService(AnsprechpartnerService ansprechpartnerService) {
        this.ansprechpartnerService = ansprechpartnerService;
    }

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }
}
