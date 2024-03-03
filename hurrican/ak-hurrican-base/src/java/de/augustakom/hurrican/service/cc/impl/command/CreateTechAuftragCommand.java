/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2009 10:09:11
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Service-Command, um einen technischen Auftrag zu erstellen. <br>
 *
 *
 */
public class CreateTechAuftragCommand extends AbstractVerlaufCommand {

    public static final String SESSION_ID = "session.id";
    /**
     * Key-Value fuer die prepare-Methode, um die Auftrags-ID zu uebergeben.
     */
    public static final String AUFTRAG_NO = "auftrag.no";
    /**
     * Key-Value fuer die prepare-Methode, um die Kundennummer zu uebergeben.
     */
    public static final String KUNDE_NO = "kunde.no";

    // Parameter fuer die Command-Klasse
    private Long sessionId = null;
    private Long auftragNo = null;
    private Long kundeNo = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        checkValues();
        return createTechOrder();
    }

    /* Legt den Auftrag an */
    private Auftrag createTechOrder() throws ServiceNotFoundException, FindException, StoreException {
        BillingAuftragService bas = getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
        ProduktService ps = getCCService(ProduktService.class);
        CCAuftragService cas = getCCService(CCAuftragService.class);

        // Pruefe, ob bereits eine Hurrican-Auftrag zum Billing-Auftrag besteht.
        List<AuftragDaten> ads = cas.findAuftragDaten4OrderNoOrig(auftragNo);
        if (CollectionTools.isNotEmpty(ads)) {
            throw new StoreException("Zu diesem Billing-Auftrag besteht bereits ein technischer Auftrag. "
                    + "Technischer Auftrag kann nicht angelegt werden.");
        }

        // Ermittle Billing-Auftrag
        BAuftrag bAuftrag = bas.findAuftrag(auftragNo);

        if (bAuftrag == null) {
            throw new StoreException("Der Billing-Auftrag " + auftragNo + " konnte nicht ermittelt werden. "
                    + "Technischer Auftrag wurde nicht angelegt.");
        }

        // Produkt-Id ermitteln
        Produkt prod = ps.doProduktMapping4AuftragNo(auftragNo);
        if (prod == null) {
            throw new StoreException("Das Produkt konnte nicht ermittelt werden."
                    + " Technischer Auftrag wurde nicht angelegt.");
        }

        // Auftrag-Daten und -Technik anlegen
        AuftragTechnik auftragTechnik = new AuftragTechnik();
        AuftragDaten ad = new AuftragDaten();
        ad.setAuftragNoOrig(auftragNo);
        ad.setBearbeiter("WebService");
        ad.setVorgabeSCV(bAuftrag.getGueltigVon());
        ad.setAuftragDatum(bAuftrag.getVertragsdatum());
        ad.setStatusmeldungen(prod.getSendStatusUpdates());

        ad.setProdId(prod.getId());
        ad.setStatusId(lookupErstellStatus(prod, AuftragStatus.AUS_TAIFUN_UEBERNOMMEN));

        // Auftrag anlegen
        Auftrag auftrag = cas.createAuftrag(kundeNo, ad, auftragTechnik, null, null);

        // Betrachte Taifun-Auftrag-Buendel-Logik
        if (BooleanTools.nullToFalse(prod.getBuendelProdukt())) {
            if (NumberTools.notEqual(bAuftrag.getBundleOrderNo(), 0)) {
                ad.setBuendelNr(bAuftrag.getBundleOrderNo());
                ad.setBuendelNrHerkunft(AuftragDaten.BUENDEL_HERKUNFT_BILLING_PRODUKT);

                // zugehoerige Buendel-Auftraege ermitteln
                List<AuftragDaten> buendelAuftraege = cas.findAuftragDaten4Buendel(ad.getBuendelNr(), ad.getBuendelNrHerkunft());
                for (AuftragDaten auftragDaten : buendelAuftraege) {
                    if (NumberTools.notEqual(auftragDaten.getAuftragId(), ad.getAuftragId())) {
                        ad.setBestellNr(auftragDaten.getBestellNr());

                        // AuftragTechnik - Auftragsart setzen
                        AuftragTechnik atBuendel = cas.findAuftragTechnikByAuftragId(auftragDaten.getAuftragId());
                        if ((atBuendel != null) && (ads.size() == 1)) {
                            // Auftragsart nur setzen, wenn genau 1 zug. Buendel-Auftrag gefunden
                            // wurde - ansonsten koennte es z.B. ein Up/Downgrade sein!
                            auftragTechnik.setAuftragsart(atBuendel.getAuftragsart());
                        }
                        break;
                    }
                }
            }
            else {
                throw new StoreException("In Taifun wurde kein Bündelauftrag definiert. " +
                        "Der technische Auftrag kann deshalb nicht angelegt werden!");
            }
        }
        //Betrachte Taifun Haupt-/Unterauftrag Struktur
        else if (Boolean.TRUE.equals(prod.getBuendelBillingHauptauftrag())) {
            cas.createBillingHauptauftragsBuendel(ad, bAuftrag);
        }

        // Pruefe Standort-Adresse
        if (!BooleanTools.nullToFalse(prod.getCreateAPAddress())
                && NumberTools.notEqual(prod.getEndstellenTyp(), Produkt.ES_TYP_KEINE_ENDSTELLEN)
                && (bAuftrag.getApAddressNo() == null)) {
            throw new StoreException("Für Auftrag " + bAuftrag.getAuftragNoOrig()
                    + " ist in Taifun keine Endstellen-Adresse erfasst."
                    + " Technischer Auftrag wurde nicht angelegt.");

        }

        // Endstellen anlegen (wenn notwendig)
        if (!NumberTools.equal(prod.getEndstellenTyp(), Produkt.ES_TYP_KEINE_ENDSTELLEN)) {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            List<Endstelle> endstellen = esSrv.createEndstellen(auftragTechnik, prod.getEndstellenTyp(), sessionId);
            if (CollectionTools.isNotEmpty(endstellen)) {
                for (Endstelle endstelle : endstellen) {
                    // Falls Endstelle B, kopiere Adressdaten von AP-Adresse aus Taifun auf die Endstelle
                    if (endstelle.isEndstelleB()) {
                        esSrv.copyAPAddress(ad, sessionId);
                    }
                }
                if (BooleanTools.nullToFalse(prod.getBuendelProdukt()) && NumberTools.isGreater(ad.getBuendelNr(), 0)) {
                    // bei Buendel-Produkt Endstellendaten evtl. kopieren - falls von anderem Auftrag vorhanden.
                    esSrv.transferBuendel2Endstellen(endstellen, ad.getBuendelNr(), ad.getBuendelNrHerkunft());
                }
            }
            else {
                throw new StoreException(
                        "Es wurden keine Endstellen für den Auftrag angelegt! Ursache: unbekannt.");
            }
        }
        // Setze Auftragsart
        if (auftragTechnik.getAuftragsart() == null) {
            auftragTechnik.setAuftragsart(BAVerlaufAnlass.NEUSCHALTUNG);
            cas.saveAuftragTechnik(auftragTechnik, false);
        }

        // Niederlassung zuordnen
        NiederlassungService ns = getCCService(NiederlassungService.class);
        ns.setNiederlassung4Auftrag(ad.getAuftragId());

        // Accounts anlegen (wenn notwendig)
        AccountService as = getCCService(AccountService.class);
        as.createAccount4Auftrag(ad.getAuftragId(), null);

        return auftrag;
    }



    /**
     * Looks up the erstell-status based on the product. If the product has no erstell-status defined then the fallback
     * status is returned.
     *
     * @param prod
     * @param fallbackStatus
     * @return the product erstell-status or the fallback-status
     */
    long lookupErstellStatus(Produkt prod, Long fallbackStatus) {
        Long erstellStatus = prod.getErstellStatusId();
        if (erstellStatus != null) {
            return erstellStatus;
        }
        return fallbackStatus;
    }

    /* Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden. */
    void checkValues() throws FindException {
        sessionId = getPreparedValue(SESSION_ID, Long.class, false, "Session-ID not defined!");

        Object id = getPreparedValue(AUFTRAG_NO);
        auftragNo = (id instanceof Long) ? (Long) id : null;
        if (auftragNo == null) {
            throw new FindException("Es wurde keine Auftragsnummer angegeben!");
        }

        id = getPreparedValue(KUNDE_NO);
        kundeNo = (id instanceof Long) ? (Long) id : null;
        if (kundeNo == null) {
            throw new FindException("Es wurde keine Kundennummer angegeben!");
        }
    }

}


