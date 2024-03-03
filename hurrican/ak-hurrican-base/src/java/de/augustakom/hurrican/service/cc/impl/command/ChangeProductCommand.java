/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2006 08:51:17
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Implementierung, um einen Produktwechsel durchzufuehren. Das Command fuehrt folgende Aktionen durch: <br>
 * <ul> <li>'alten' Auftrag inkl. Physik kuendigen <li>neuen Auftrag mit Basisdaten des alten Auftrags anlegen
 * <li>Physik des alten Auftrags uebernehmen (genaue Funktion abhaengig vom Physikaenderungstyp) <ul>
 *
 *
 */
@CcTxRequired
public class ChangeProductCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(ChangeProductCommand.class);

    public static final String KEY_SESSION_ID = "session.id";
    public static final String KEY_SERVICE_CALLBACK = "service.callback";
    public static final String KEY_PHYSIKAENDERUNGSTYP = "physikaenderungstyp";
    public static final String KEY_OLD_AUFTRAGS_ID = "auftrags.id.old";
    public static final String KEY_CHANGE_DATE = "change.date";
    public static final String KEY_NEW_PRODUCT_ID = "new.product.id";
    public static final String KEY_NEW_ORDER__NO = "new.order.no";

    private Long sessionId = null;
    private IServiceCallback serviceCallback = null;
    private Long auftragIdOld = null;
    private Long physikaendTyp = null;
    private Date changeDate = null;
    private Long newProductId = null;
    private Long orderNoOrig = null;

    private Auftrag createdAuftrag = null;
    private List<Endstelle> createdEndstellen = null;
    private Produkt newProdukt = null;
    private AKUser user = null;

    @Override
    public Object execute() throws Exception {
        try {
            checkValues();
            loadRequiredData();

            doKuendigung();
            createNewAuftrag();
            doPhysikChange();
            return createdAuftrag;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim Produktwechsel: " + e.getMessage(), e);
        }
    }

    /* Kuendigt den alten Auftrag inkl. Physik */
    private void doKuendigung() throws Exception {
        try {
            EndstellenService endstellenService = getCCService(EndstellenService.class);
            List<Endstelle> endstellenOld = endstellenService.findEndstellen4Auftrag(auftragIdOld);
            if (endstellenOld != null) {
                RangierungsService rangierungsService = getCCService(RangierungsService.class);
                for (Endstelle es : endstellenOld) {
                    rangierungsService.rangierungFreigabebereit(es, changeDate);
                }
            }

            CCAuftragStatusService auftragStatusService = getCCService(CCAuftragStatusService.class);
            auftragStatusService.kuendigeAuftrag(auftragIdOld, changeDate, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Kuendigung des Ursprungs-Auftrags: " + e.getMessage(), e);
        }
    }

    /* Erzeugt den neuen Auftrag mit den Basis-Daten des Alt-Auftrags */
    private void createNewAuftrag() throws Exception {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag auftragOld = as.findAuftragById(auftragIdOld);

            AuftragDaten adNew = new AuftragDaten();
            adNew.setVorgabeSCV(changeDate);
            adNew.setProdId(newProductId);
            adNew.setAuftragNoOrig(orderNoOrig);
            adNew.setStatusId(AuftragStatus.ERFASSUNG_SCV);
            adNew.setBearbeiter(user.getName());

            // nicht ueber PropertyUtils.copyProperty kopieren, da sonst Probleme mit Transaction...
            AuftragTechnik atNew = new AuftragTechnik();
            atNew.setAuftragsart(BAVerlaufAnlass.NEUSCHALTUNG);

            createdAuftrag = as.createAuftrag(auftragOld.getKundeNo(), adNew, atNew, sessionId, serviceCallback);

            ProduktService prodServ = getCCService(ProduktService.class);
            newProdukt = prodServ.findProdukt(newProductId);

            EndstellenService esSrv = getCCService(EndstellenService.class);
            createdEndstellen = esSrv.createEndstellen(atNew, newProdukt.getEndstellenTyp(), sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Anlegen des neuen Auftrags: " + e.getMessage(), e);
        }
    }

    /* Fuehrt die Physikaenderung durch */
    private void doPhysikChange() throws Exception {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            RangierungsService rs = getCCService(RangierungsService.class);
            if (NumberTools.equal(physikaendTyp, PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG)) {
                // zuerst eine neue Physik zuordnen
                for (Endstelle es : createdEndstellen) {
                    // Geo ID und HVT von 'alten' Endstellen kopieren
                    Endstelle esOld = esSrv.findEndstelle4Auftrag(auftragIdOld, es.getEndstelleTyp());
                    if (esOld != null) {
                        es.setGeoId(esOld.getGeoId());
                        es.setHvtIdStandort(esOld.getHvtIdStandort());
                        esSrv.saveEndstelle(es);

                        if (es.isEndstelleA() && NumberTools.equal(newProdukt.getEndstellenTyp(), Produkt.ES_TYP_A_UND_B)) {
                            rs.assignRangierung2ES(es.getId(), serviceCallback);
                        }
                        else if (es.isEndstelleB() && NumberTools.isIn(newProdukt.getEndstellenTyp(),
                                new Number[] { Produkt.ES_TYP_A_UND_B, Produkt.ES_TYP_NUR_B })) {
                            rs.assignRangierung2ES(es.getId(), serviceCallback);
                        }
                    }
                }
            }

            AKWarnings warnings = rs.physikAenderung(physikaendTyp,
                    auftragIdOld, createdAuftrag.getId(), serviceCallback, sessionId);

            if (warnings != null) {
                LOGGER.warn("WARNINGS on product change: " + warnings.getWarningsAsText());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Physikuebernahme: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft die gesetzten Values. */
    private void checkValues() throws FindException {
        sessionId = getPreparedValue(KEY_SESSION_ID, Long.class, false,
                "Es wurde keine Session-ID angegeben!");

        serviceCallback = getPreparedValue(KEY_SERVICE_CALLBACK, IServiceCallback.class,
                false, "Es wurde kein ServiceCallback angegeben!");

        physikaendTyp = getPreparedValue(KEY_PHYSIKAENDERUNGSTYP, Long.class, false,
                "Es wurde kein Physikaenderungstyp angegeben!");

        auftragIdOld = getPreparedValue(KEY_OLD_AUFTRAGS_ID, Long.class, false,
                "Es wurde kein zu kuendigender Auftrag angegeben!");

        newProductId = getPreparedValue(KEY_NEW_PRODUCT_ID, Long.class, false,
                "Es wurde keine Produkt-ID fuer den neuen Auftrag angegeben!");

        orderNoOrig = getPreparedValue(KEY_NEW_ORDER__NO, Long.class, false,
                "Die Auftragsnummer aus dem Billing-System wurde nicht angegeben.");

        changeDate = getPreparedValue(KEY_CHANGE_DATE, Date.class, false,
                "Es wurde kein Datum fuer den Produktwechsel angegeben!");
    }

    @Override
    protected void loadRequiredData() throws FindException {
        try {
            IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService us = locator.getService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class, null);
            user = us.findUserBySessionId(sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}


