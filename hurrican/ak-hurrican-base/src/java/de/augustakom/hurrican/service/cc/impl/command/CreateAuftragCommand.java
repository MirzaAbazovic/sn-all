/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.08.2004 13:56:39
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.billing.BAuftragConnect;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Wohnheim;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Command-Klasse, um einen neuen CC-Auftrag anzulegen.
 *
 *
 */
@CcTxRequired
public class CreateAuftragCommand extends AbstractServiceCommand {

    /* Definition der Keys fuer die Methode <code>prepare(String, Object)</code> */
    public static final String KEY_SESSION_ID = "session.id";
    public static final String KEY_KUNDE_NO = "kunden.no";
    public static final String KEY_AUFTRAG_TECHNIK = "auftrag.technik";
    public static final String KEY_AUFTRAG_DATEN = "auftrag.daten";
    public static final String KEY_AUFTRAG_DAO = "auftrag.dao";
    public static final String KEY_WOHNHEIM = "wohnheim";
    public static final String KEY_SERVICE_CALLBACK = "service.callback";

    private Long sessionId = null;
    private AuftragTechnik auftragTechnik = null;
    private AuftragDaten auftragDaten = null;
    private Produkt ccProdukt = null;

    @Resource(name = "auftragTechnikDAO")
    private AuftragTechnikDAO auftragTechnikDAO;
    @Resource(name = "auftragDatenDAO")
    private AuftragDatenDAO auftragDatenDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "auftragDAO")
    private CCAuftragDAO auftragDAO;

    @Override
    public Object execute() throws ServiceCommandException {
        try {
            sessionId = (Long) getPreparedValue(KEY_SESSION_ID);
            auftragTechnik = (AuftragTechnik) getPreparedValue(KEY_AUFTRAG_TECHNIK);
            auftragDaten = (AuftragDaten) getPreparedValue(KEY_AUFTRAG_DATEN);
            readProdukt();
            readOrCreateVbz();
            Auftrag auftrag = createAuftrag();
            return auftrag;
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Liest das Produkt ein. */
    private void readProdukt() throws ServiceCommandException {
        try {
            ccProdukt = produktService.findProdukt(auftragDaten.getProdId());

            if (ccProdukt == null) {
                throw new HurricanServiceCommandException(
                        String.format("Das Hurrican Produkt mit Id %s konnte nicht ermittelt werden!", auftragDaten.getProdId()));
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException("Bei der Ermittlung des zugehörigen Hurrican-Produkts ist ein Fehler aufgetreten.", e);
        }
    }

    /* Erzeugt die Auftrags-Objekte. */
    final Auftrag createAuftrag() throws ServiceNotFoundException, StoreException, FindException {
        Auftrag auftrag = new Auftrag();
        auftrag.setKundeNo((Long) getPreparedValue(KEY_KUNDE_NO));

        auftragDAO.save(auftrag);

        Date now = new Date();
        Date end = DateTools.getHurricanEndDate();

        // AuftragDaten-Objekt modifizieren und speichern
        auftragDaten.setAuftragId(auftrag.getId());
        auftragDaten.setGueltigVon(now);
        auftragDaten.setGueltigBis(end);
        if (auftragDaten.getStatusId() == null) {
            auftragDaten.setStatusId(AuftragStatus.UNDEFINIERT);
        }
        auftragDaten.setAutoSmsAndMailVersand(ccProdukt.getSmsVersand());
        auftragDatenDAO.store(auftragDaten);

        // AuftragTechnik-Objekt modifizieren und speichern
        auftragTechnik.setAuftragId(auftrag.getId());
        auftragTechnik.setGueltigVon(now);
        auftragTechnik.setGueltigBis(end);
        if (auftragTechnik.getAuftragsart() == null) {
            auftragTechnik.setAuftragsart(BAVerlaufAnlass.NEUSCHALTUNG);
        }
        auftragTechnikDAO.store(auftragTechnik);

        if (auftragDaten.getAuftragNoOrig() != null) {
            // Leistungen des Auftrags anlegen
            List<LeistungsDiffView> diffs = ccLeistungsService.findLeistungsDiffs(auftrag.getId(),
                    auftragDaten.getAuftragNoOrig(), auftragDaten.getProdId());

            if(ccLeistungsService.hasVoipLeistungFromNowOn(diffs)) {
                auftragTechnik.setHwSwitch(ccProdukt.getHwSwitch());
                auftragTechnikDAO.store(auftragTechnik);
            }

            ccLeistungsService.synchTechLeistung4Auftrag(auftrag.getId(), auftragDaten.getProdId(), null, true,
                    sessionId, diffs);
        }

        BAuftragConnect auftragConnect = billingAuftragService.findAuftragConnectByAuftragNo(auftragDaten.getAuftragNoOrig());
        if ((auftragConnect != null) && (auftragConnect.getLbzKunde() != null) && StringUtils.isBlank(auftragDaten.getLbzKunde())) {
            auftragDaten.setLbzKunde(auftragConnect.getLbzKunde());
            auftragDatenDAO.store(auftragDaten);
        }

        return auftrag;
    }

    /*
     * Benoetigt der Auftrag eine VerbindungsBezeichnung (Leitungsnummer), wird diese
     * neu erzeugt oder ueber ein Wohnheim ermittelt.
     */
    private void readOrCreateVbz() throws ServiceNotFoundException, FindException, StoreException {
        if (BooleanTools.nullToFalse(ccProdukt.getLeitungsNrAnlegen())) {
            VerbindungsBezeichnung verbindungsBezeichnung =
                    physikService.createVerbindungsBezeichnung(auftragDaten.getProdId(), auftragDaten.getAuftragNoOrig());

            if (verbindungsBezeichnung != null) {
                auftragTechnik.setVbzId(verbindungsBezeichnung.getId());
            }
            else {
                throw new StoreException("VerbindungsBezeichnung not found or created!");
            }
        }
        else if (Produkt.PROD_ID_APPARTEMENT.equals(ccProdukt.getId())) {
            Object o = getPreparedValue(KEY_WOHNHEIM);
            if (o instanceof Wohnheim) {
                Wohnheim wohnheim = (Wohnheim) o;
                VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnung(wohnheim.getVbz());
                auftragTechnik.setVbzId(verbindungsBezeichnung.getId());
            }
        }
    }

}
