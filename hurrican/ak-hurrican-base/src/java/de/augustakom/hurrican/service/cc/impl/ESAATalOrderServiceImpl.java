/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2011 12:02:58
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.tal.ProduktDtag;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ESAATalOrderService;

/**
 * Implementierung der TAL-Bestellung ueber die ESAA-Schnittstelle der DTAG.
 */
@CcTxRequired
public class ESAATalOrderServiceImpl extends DefaultCCService implements ESAATalOrderService {

    private static final Logger LOGGER = Logger.getLogger(ESAATalOrderServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    private CarrierElTALService carrierElTalService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;

    @Override
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            Long sessionId) throws StoreException {
        AKUser user = getAKUserBySessionIdSilent(sessionId);
        if (user == null) {
            throw new StoreException(
                    "Benutzer konnte nicht ermittelt werden. TAL-Bestellung nicht durchgefuehrt!");
        }
        return createCBVorgang(cbId, auftragId, subOrders4Klammer, carrierId, vorgabe, typ, usecaseId, vierDraht,
                montagehinweis, user);
    }

    @Override
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            AKUser user) throws StoreException {
        throw new StoreException("ESAA Bestellungen können nicht mehr ausgelöst werden - bitte WITA benutzten.");
    }

    @Override
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Long carrierId, Date vorgabe, Long typ,
            String montagehinweis, AKUser user) throws StoreException {
        throw new StoreException("ESAA Bestellungen können nicht mehr ausgelöst werden - bitte WITA benutzten.");
    }

    @Override
    public CBVorgang closeCBVorgang(Long id, Long sessionId) throws StoreException, ValidationException {
        if (id == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            CBVorgang cbv = carrierElTalService.findCBVorgang(id);
            if (NumberTools.isLess(cbv.getStatus(), CBVorgang.STATUS_ANSWERED)) {
                throw new StoreException("Die TAL-Bestellung kann im aktuellen Status nicht abgeschlossen werden!");
            }
            else if (NumberTools.equal(cbv.getStatus(), CBVorgang.STATUS_CLOSED)) {
                throw new StoreException("Die TAL-Bestellung ist bereits abgeschlossen!");
            }

            if (cbv.getReturnOk() == null) {
                throw new StoreException("Es ist keine Rueckmeldung (pos./neg.) eingetragen.\n"
                        + "Vorgang kann deshalb nicht abgeschlossen werden.");
            }

            if (cbv.getCbId() != null && !cbv.isRexMk()) {
                // Zur REX-MK gibt es keine Carrierbestellung
                writeDataOntoCarrierbestellung(sessionId, cbv);
            }

            cbv.close();
            carrierElTalService.saveCBVorgang(cbv);

            return cbv;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void writeDataOntoCarrierbestellung(Long sessionId, CBVorgang cbv) throws FindException, StoreException {
        Carrierbestellung cb = carrierService.findCB(cbv.getCbId());
        if (cb == null) {
            throw new StoreException("Die zugehoerige Carrierbestellung konnte nicht ermittelt werden!");
        }

        if (cbv.isKuendigung() || cbv.isStorno()) { // check first: Storno is check by another field than type
            // Kuendigung abschliessen
            cb.setKuendBestaetigungCarrier(getOrNullOnNegCbv(cbv.getReturnRealDate(), cbv));
        }
        else if (NumberTools.isIn(cbv.getTyp(), new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_NUTZUNGSAENDERUNG,
                CBVorgang.TYP_ANBIETERWECHSEL, CBVorgang.TYP_PORTWECHSEL })) {

            // Die Folgenden Felder bei negativer Rueckmeldung nicht zuruecksetzen
            if (BooleanTools.nullToFalse(cbv.getReturnOk())
                    // Bei neuen Leitungen Vorgabedatum setzen falls nicht gesetzt und nichts zurueckgemeldet wurde
                    && NumberTools.isIn(cbv.getTyp(), new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_ANBIETERWECHSEL })
                    && (cb.getVorgabedatum() == null) && (cb.getVtrNr() == null)) {
                cb.setVorgabedatum(cbv.getVorgabeMnet());
            }

            cb.setLbz(getOrNullOnNegCbv(cbv.getReturnLBZ(), cbv));
            cb.setVtrNr(getOrNullOnNegCbv(cbv.getReturnVTRNR(), cbv));
            cb.setLl(getOrNullOnNegCbv(cbv.getReturnLL(), cbv));
            cb.setAqs(getOrNullOnNegCbv(cbv.getReturnAQS(), cbv));
            cb.setZurueckAm(getOrNullOnNegCbv(cbv.getAnsweredAt(), cbv));
            cb.setBereitstellungAm(getOrNullOnNegCbv(cbv.getReturnRealDate(), cbv));
            cb.setKundeVorOrt(getOrNullOnNegCbv(cbv.getReturnKundeVorOrt(), cbv));

            if (StringUtils.isNotBlank(cbv.getReturnMaxBruttoBitrate())) {
                cb.setMaxBruttoBitrate(getOrNullOnNegCbv(cbv.getReturnMaxBruttoBitrate(), cbv));
            }
        }
        else {
            throw new StoreException("Vorgang noch nicht implementiert!");
        }

        carrierService.saveCB(cb);
        // Da cb.setLl(cbv.getReturnLL()), Leitungslänge auf Geo ID 2 Tech Location Mapping schreiben
        carrierService.saveCBDistance2GeoId2TechLocations(cb, sessionId);
    }

    /**
     * Liefert null zurueck, wenn der CBVorgang negativ zurueckgemeldet wurde, sonst den Wert value.
     */
    private <T> T getOrNullOnNegCbv(T value, CBVorgang cbv) {
        if (BooleanTools.nullToFalse(cbv.getReturnOk())) {
            return value;
        }
        return null;
    }

    @Override
    public List<ProduktDtag> getProduktDtag(ProduktDtag example) throws FindException {
        try {
            return ((ByExampleDAO) getDAO()).queryByExample(example, ProduktDtag.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Reference> findPossibleGeschaeftsfaelle(Long carrierId, boolean forKvz) throws FindException {
        throw new FindException("findPossibleGeschaeftsfaelle not longer supported on ESAA!");
    }

    @Override
    public CBVorgang doStorno(Long id, AKUser user) throws StoreException, ValidationException {
        throw new StoreException("doStorno not longer supported on ESAA!");
    }

}
