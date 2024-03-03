/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2008 14:48:12
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BeanTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Function2;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.CCAuftragViewDAO;
import de.augustakom.hurrican.dao.cc.EndstelleDAO;
import de.augustakom.hurrican.dao.cc.EndstelleLtgDatenDAO;
import de.augustakom.hurrican.dao.cc.EndstelleViewDAO;
import de.augustakom.hurrican.dao.cc.HVTStandortDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.impl.command.CopyAPAddressCommand;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.augustakom.hurrican.validation.cc.EndstelleLtgDatenValidator;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von <code>EndstellenService</code>.
 *
 *
 */
@CcTxRequired
public class EndstellenServiceImpl extends DefaultCCService implements EndstellenService {

    private static final Logger LOGGER = Logger.getLogger(EndstellenServiceImpl.class);

    @Autowired
    private EndstelleViewDAO endstelleViewDAO;
    @Autowired
    private EndstelleDAO endstelleDAO;
    @Autowired
    private EndstelleLtgDatenDAO endstelleLtgDAO;
    @Autowired
    private CCAuftragViewDAO auftragViewDAO;
    @Autowired
    private HVTStandortDAO hvtStandortDAO;
    @Autowired
    private HardwareDAO hardwareDAO;
    @Autowired
    private ServiceLocator serviceLocator;
    @Autowired
    private EndstelleLtgDatenValidator endstelleLtgDatenValidator;
    @Autowired
    private ProduktService produktService;

    @Override
    public void store(Object toStore) throws StoreException {
        saveEndstelle((Endstelle) toStore);
    }

    @CheckForNull
    @Override
    public List<Endstelle> createEndstellen(AuftragTechnik auftragTechnik, Integer esTyp, Long sessionId)
            throws StoreException {
        if (Produkt.ES_TYP_KEINE_ENDSTELLEN.equals(esTyp)) {
            return null;
        }

        if (auftragTechnik == null) {
            throw new StoreException(StoreException.UNABLE_TO_CREATE_ENDSTELLEN);
        }

        try {
            CCAuftragService ccAS = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = ccAS.findAuftragDatenByAuftragId(auftragTechnik.getAuftragId());
            Long billingOrderNo = auftragDaten != null ? auftragDaten.getAuftragNoOrig() : null;

            Long esGruppeId = null;
            boolean makeHistoryAT = false;
            if (auftragTechnik.getAuftragTechnik2EndstelleId() != null) {
                esGruppeId = auftragTechnik.getAuftragTechnik2EndstelleId();
                makeHistoryAT = true;
            }
            else {
                esGruppeId = endstelleDAO.createNewMappingId();
            }

            List<Endstelle> result = new ArrayList<Endstelle>();
            // es werden weiterhin beide Endstellen angelegt!!!
            Endstelle esA = new Endstelle();
            esA.setEndstelleTyp("A");
            esA.setEndstelleGruppeId(esGruppeId);
            findAndAssignGeoIdToEndstelle(esA, billingOrderNo, sessionId);

            Endstelle esB = new Endstelle();
            esB.setEndstelleTyp("B");
            esB.setEndstelleGruppeId(esGruppeId);
            findAndAssignGeoIdToEndstelle(esB, billingOrderNo, sessionId);

            // Bug-ID 11411
            getAndSetKundenName(auftragTechnik, ccAS, esB);

            saveEndstelle(esA);
            result.add(esA);
            saveEndstelle(esB);
            result.add(esB);

            // Leitungsdaten fuer Endstelle B anlegen
            createLtgDaten4Endstelle(esB, auftragTechnik.getAuftragId());

            // ES-Gruppe-ID der AuftragTechnik zuweisen und AT speichern
            auftragTechnik.setAuftragTechnik2EndstelleId(esGruppeId);
            ccAS.saveAuftragTechnik(auftragTechnik, makeHistoryAT);

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_ENDSTELLEN, e);
        }
    }

    private void getAndSetKundenName(AuftragTechnik auftragTechnik, CCAuftragService ccAS, Endstelle esB) {
        // Bug-ID 11411
        try {
            Auftrag auftrag = ccAS.findAuftragById(auftragTechnik.getAuftragId());
            if (auftrag != null) {
                KundenService ks = getBillingService(KundenService.class);
                Kunde kunde = ks.findKunde(auftrag.getKundeNo());
                esB.setName(kunde != null ? kunde.getName() : null);
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt zu dem aktuellen Auftrag die Standort-Adresse aus dem Billing-System und uebernimmt die GeoID auf die
     * Endstelle. <br>
     *
     * @param endstelle
     */
    private void findAndAssignGeoIdToEndstelle(Endstelle endstelle, Long billingOrderNo, Long sessionId) {
        if ((billingOrderNo == null) || (endstelle == null)) {
            return;
        }

        try {
            AvailabilityService availabilityService = getCCService(AvailabilityService.class);
            BillingAuftragService billingAuftragService = getBillingService(BillingAuftragService.class);
            Adresse address = billingAuftragService.findAnschlussAdresse4Auftrag(billingOrderNo,
                    endstelle.getEndstelleTyp());

            if ((address != null) && (address.getGeoId() != null)) {
                GeoId geoId = availabilityService.findOrCreateGeoId(address.getGeoId(), sessionId);
                if (geoId != null) {
                    endstelle.setGeoId(geoId.getId());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // Exceptions werden nicht weiter gereicht - GeoID kann auch manuell ausgewaehlt werden!
        }
    }

    /* Legt Leitungs-Daten fuer eine best. Endstelle an. */
    private void createLtgDaten4Endstelle(Endstelle endstelle, Long auftragId) {
        try {
            ProduktService ps = getCCService(ProduktService.class);
            Produkt produkt = ps.findProdukt4Auftrag(auftragId);

            EndstelleLtgDaten ltgB = new EndstelleLtgDaten();
            ltgB.setEndstelleId(endstelle.getId());
            ltgB.setGueltigVon(new Date());
            ltgB.setGueltigBis(DateTools.getHurricanEndDate());

            List<Schnittstelle> schnittstellen = null;
            if (produkt != null) {
                ltgB.setLeitungsartId(produkt.getLeitungsart());
                schnittstellen = ps.findSchnittstellen4Produkt(produkt.getId());
            }

            if ((schnittstellen != null) && (schnittstellen.size() == 1)) {
                ltgB.setSchnittstelleId(schnittstellen.get(0).getId());
            }
            else {
                // TODO bei mehr als einer Schnittstelle sollte User die Auswahl treffen
                ltgB.setSchnittstelleId(Schnittstelle.SCHNITTSTELLE_SONSTIGE);
            }

            // Ermittle Leitungsart anhand der technischen Leistung (Downstream)
            CCLeistungsService lsService = getCCService(CCLeistungsService.class);
            List<TechLeistung> list = lsService
                    .findTechLeistungen4Auftrag(auftragId, TechLeistung.TYP_DOWNSTREAM, true);
            if (CollectionTools.isNotEmpty(list) && (list.size() == 1)) {
                PhysikService physSrv = getCCService(PhysikService.class);
                Leitungsart la = physSrv.findLeitungsartByName(list.get(0).getName());
                if (la != null) {
                    ltgB.setLeitungsartId(la.getId());
                }
            }

            saveESLtgDaten(ltgB, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Endstelle saveEndstelle(Endstelle toSave) throws StoreException {
        if (toSave == null) {
            return null;
        }
        try {
            mapAnschlussart(toSave);
            toSave.debugModel(LOGGER);
            toSave.setLastChange(new Date());
            return endstelleDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Mappt die Anschlussart der Endstelle, wenn die Anschlussart = FttX, der Standorttyp = FTTB_H,  und die Endstelle
     * mit einem Port und einem Rack (FTTB oder FTTH) versehen ist.
     */
    void mapAnschlussart(Endstelle endstelle) {
        HVTStandort hvtStandort = (endstelle.getHvtIdStandort() != null)
                ? hvtStandortDAO.findById(endstelle.getHvtIdStandort(), HVTStandort.class)
                : null;

        if (hvtStandort != null && hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTB_H)) {
            String rackTyp = hardwareDAO.findRackType4EqIn(endstelle.getRangierId());
            if (rackTyp != null) {
                switch (rackTyp) {
                    case HWRack.RACK_TYPE_MDU:
                    case HWRack.RACK_TYPE_DPO:
                    case HWRack.RACK_TYPE_DPU:
                        endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_FTTB);
                        break;
                    case HWRack.RACK_TYPE_ONT:
                        endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_FTTH);
                        break;
                    default:
                        endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_FTTX);
                }
            }
            else {
                endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_FTTX);
            }
        }
    }

    @Override
    public void copyEndstelle(Endstelle src, Endstelle dest, List<String> copyProperties) throws StoreException {
        if ((src == null) || (dest == null)) {
            throw new StoreException("Die Ursprungs- oder Ziel-Endstelle ist nicht angegeben!");
        }
        if (CollectionUtils.isEmpty(copyProperties)) {
            throw new StoreException("Es sind keine Felder angegeben, die kopiert werden sollen!");
        }

        try {
            BeanTools.copyProperties(dest, src, copyProperties.toArray(new String[copyProperties.size()]));

            ProduktService ps = getCCService(ProduktService.class);
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByEndstelleTx(dest.getId());
            Produkt produkt = ps.findProdukt(ad.getProdId());

            // Produktkonfig pruefen und (Hurrican) AP-Adresse nur kopieren,
            // wenn Erfassung in Hurrican zulaessig!
            if (BooleanTools.nullToFalse(produkt.getCreateAPAddress()) && (src.getAddressId() != null)) {
                // Kundennummer der neuen Endstelle ermitteln
                Auftrag auftragDest = as.findAuftragById(ad.getAuftragId());

                // Adresse kopieren und neue Adresse der Dest-ES zuordnen
                CCKundenService ks = getCCService(CCKundenService.class);
                CCAddress adrSrc = ks.findCCAddress(src.getAddressId());

                CCAddress adrDest = new CCAddress();
                PropertyUtils.copyProperties(adrDest, adrSrc);
                adrDest.setId(null);
                adrDest.setKundeNo(auftragDest != null ? auftragDest.getKundeNo() : null);
                ks.saveCCAddress(adrDest);

                dest.setAddressId(adrDest.getId());
            }

            saveEndstelle(dest);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Endstelle findEndstelle(Long esId) throws FindException {
        if (esId == null) {
            return null;
        }
        try {
            return endstelleDAO.findById(esId, Endstelle.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Endstelle findEndstelle(Long esGruppe, String esTyp) throws FindException {
        if ((esGruppe == null)
                || (!StringUtils.equals(esTyp, Endstelle.ENDSTELLEN_TYP_A) && !StringUtils.equals(esTyp,
                Endstelle.ENDSTELLEN_TYP_B))) {
            return null;
        }

        try {
            Endstelle example = new Endstelle();
            example.setEndstelleGruppeId(esGruppe);
            example.setEndstelleTyp(esTyp);
            List<Endstelle> result = endstelleDAO.queryByExample(example, Endstelle.class);
            return (result != null) && (result.size() == 1) ? (Endstelle) result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Endstelle> findEndstellen4Auftrag(Long auftragId) throws FindException {
        try {
            return endstelleViewDAO.findEndstellen4Auftrag(auftragId);
        }
        catch (HurricanConcurrencyException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Endstelle> findEndstellen4AuftragBasedOnProductConfig(Long auftragId) throws FindException {
        try {
            Produkt produkt = produktService.findProdukt4Auftrag(auftragId);

            List<Endstelle> endstellen = findEndstellen4Auftrag(auftragId);

            List<Endstelle> result = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(endstellen)) {
                for (Endstelle endstelle : endstellen) {
                    if (Endstelle.ENDSTELLEN_TYP_A.equals(endstelle.getEndstelleTyp())
                            && Produkt.ES_TYP_A_UND_B.equals(produkt.getEndstellenTyp())) {
                        result.add(endstelle);
                    }
                    else if (Endstelle.ENDSTELLEN_TYP_B.equals(endstelle.getEndstelleTyp())
                            && !Produkt.ES_TYP_KEINE_ENDSTELLEN.equals(produkt.getEndstellenTyp())) {
                        result.add(endstelle);
                    }
                }
            }

            return result;
        }
        catch (HurricanConcurrencyException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Endstelle> findEndstellen4AuftragWithoutExplicitFlush(Long auftragId) throws FindException {
        try {
            return (List<Endstelle>) endstelleViewDAO.findEndstellen4AuftragWithoutFlush(auftragId);
        }
        catch (HurricanConcurrencyException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Endstelle findEndstelle4Auftrag(final Long auftragId, final String esTyp) throws FindException {
        return findEndstelle4Auftrag(auftragId, esTyp, new Function2<List<Endstelle>, Long, String>() {
            @Override
            public List<Endstelle> apply(final Long aLong, final String s) throws Exception {
                return findEndstellen4Auftrag(auftragId);
            }
        });
    }

    @Override
    @CcTxRequiresNew
    public Endstelle findEndstelle4AuftragNewTx(Long auftragId, String esTyp) throws FindException {
        return findEndstelle4Auftrag(auftragId, esTyp);
    }

    @Override
    public Endstelle findEndstelle4AuftragWithoutExplicitFlush(final Long auftragId, final String esTyp)
            throws FindException {
        return findEndstelle4Auftrag(auftragId, esTyp, new Function2<List<Endstelle>, Long, String>() {
            @Override
            public List<Endstelle> apply(final Long aLong, final String s) throws Exception {
                return findEndstellen4AuftragWithoutExplicitFlush(auftragId);
            }
        });
    }

    private Endstelle findEndstelle4Auftrag(Long auftragId, String esTyp,
            Function2<List<Endstelle>, Long, String> findEndstelle) throws FindException {
        if ((auftragId == null) || StringUtils.isBlank(esTyp)) {
            return null;
        }
        try {
            List<Endstelle> endstellen = findEndstelle.apply(auftragId, esTyp);
            if (endstellen != null) {
                for (Endstelle es : endstellen) {
                    if (StringUtils.equals(esTyp, es.getEndstelleTyp())) {
                        return es;
                    }
                }
            }

            return null;
        }
        catch (HurricanConcurrencyException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Endstelle> findEndstellenWithRangierId(Long rangierId) throws FindException {
        if (rangierId == null) {
            return new ArrayList<Endstelle>();
        }
        try {
            Endstelle example = new Endstelle();
            example.setRangierId(rangierId);

            List<Endstelle> result = endstelleDAO.queryByExample(example, Endstelle.class);
            if (CollectionTools.isEmpty(result)) {
                example.setRangierId(null);
                example.setRangierIdAdditional(rangierId);
                result = endstelleDAO.queryByExample(example, Endstelle.class);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Endstelle> findEndstellenByGeoId(Long geoId) throws FindException {
        if (geoId == null) {
            return null;
        }
        try {
            Endstelle example = new Endstelle();
            example.setGeoId(geoId);
            List<Endstelle> result = endstelleDAO.queryByExample(example, Endstelle.class);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void replaceGeoIdsOfEnstellen(Long geoId, Long replacedByGeoId) throws StoreException, FindException {
        List<Endstelle> endstellen = findEndstellenByGeoId(geoId);
        if (CollectionTools.isNotEmpty(endstellen)) {
            for (Endstelle endstelle : endstellen) {
                endstelle.setGeoId(replacedByGeoId);
                saveEndstelle(endstelle);
                LOGGER.info("GEOID: " + replacedByGeoId + " auf der Endstelle(" + endstelle.getId() + ") aktualisiert");
            }
        }
    }

    @Override
    public List<Endstelle> findEndstellen4Carrierbestellung(Carrierbestellung carrierbestellung) throws FindException {
        if ((carrierbestellung == null) || (carrierbestellung.getCb2EsId() == null)) {
            return null;
        }
        try {
            Endstelle example = new Endstelle();
            example.setCb2EsId(carrierbestellung.getCb2EsId());
            List<Endstelle> result = endstelleDAO.queryByExample(example, Endstelle.class);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Endstelle findEndstelle4CarrierbestellungAndAuftrag(Long cbId, Long auftragId) throws FindException {
        CarrierService carrierService = serviceLocator.getBean(CarrierService.class.getName(), CarrierService.class);
        Carrierbestellung carrierbestellung = carrierService.findCB(cbId);
        List<Endstelle> endstellen = findEndstellen4Auftrag(auftragId);
        for (Endstelle endstelle : endstellen) {
            if (NumberTools.equal(endstelle.getCb2EsId(), carrierbestellung.getCb2EsId())) {
                return endstelle;
            }
        }
        throw new FindException("Keinen Endstelle gefunden");
    }

    @Override
    public List<AuftragEndstelleView> findEndstellen4Anschlussuebernahme(Long endstelleId) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            // Daten der angegebenen Endstelle laden
            AuftragEndstelleQuery query = new AuftragEndstelleQuery();
            query.setEndstelleId(endstelleId);

            List<AuftragEndstelleView> currentES = getCCService(CCAuftragService.class)
                    .findAuftragEndstelleViews(query);
            if ((currentES != null) && (currentES.size() == 1)) {
                AuftragEndstelleView view = currentES.get(0);
                if ((view.getProduktId() == null) || (view.getEndstelleGeoId() == null)) {
                    throw new FindException("Für die Endstelle " + endstelleId
                            + " konnten keine Daten ermittelt werden.");
                }

                // moegliche Physiktypen fuer die Endstelle bzw. das Produkt ermitteln
                PhysikService ps = getCCService(PhysikService.class);
                List<Produkt2PhysikTyp> p2pts = ps.findP2PTs4Produkt(view.getProduktId(), null);
                if ((p2pts == null) || (p2pts.isEmpty())) {
                    throw new FindException("Für die Endstelle " + endstelleId
                            + " konnten keine Physik-Typen ermittelt werden.");
                }

                List<Long> physiken = new ArrayList<Long>();
                for (Produkt2PhysikTyp p2pt : p2pts) {
                    physiken.add(p2pt.getPhysikTypId());
                }

                List<AuftragEndstelleView> result = auftragViewDAO.findAESViews4Uebernahme(
                        view.getEndstelleGeoId(), physiken);
                KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                if (ks != null) {
                    ks.loadKundendaten4AuftragViews(result);
                }

                return result;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragEndstelleView> findEndstellen4Bandbreite(Long endstelleId) throws FindException {
        return findAESViews(endstelleId, false);
    }

    /*
     * Sucht nach Auftrags- und Endstellendaten fuer einen best. Kunden, die sich innerhalb (pgExclusive=false) oder
     * ausserhalb (pgExclusive=true) einer ProduktGruppe befinden. Die Kunden-No wird ueber die Endstellen-ID ermittelt.
     */
    private List<AuftragEndstelleView> findAESViews(Long endstelleId, boolean pgExclusive) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            // Kunde und Produktgruppe der Endstelle ermitteln
            AuftragEndstelleQuery query = new AuftragEndstelleQuery();
            query.setEndstelleId(endstelleId);

            List<AuftragEndstelleView> currentES = getCCService(CCAuftragService.class)
                    .findAuftragEndstelleViews(query);
            if ((currentES != null) && (currentES.size() == 1)) {
                AuftragEndstelleView view = currentES.get(0);

                ProduktService ps = getCCService(ProduktService.class);
                ProduktGruppe pg = ps.findPG4Auftrag(view.getAuftragId());
                if (pg != null) {
                    List<AuftragEndstelleView> result = auftragViewDAO.findAESViews4KundeAndPG(view.getKundeNo(),
                            pg.getId(), pgExclusive);

                    KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                    if (ks != null) {
                        ks.loadKundendaten4AuftragViews(result);
                    }

                    return result;
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragEndstelleView> findEndstellen4Wandel(Long endstelleId, Boolean analog2isdn) throws FindException {
        if (endstelleId == null) {
            return null;
        }
        try {
            // Kunde und Produktgruppe der Endstelle ermitteln
            AuftragEndstelleQuery query = new AuftragEndstelleQuery();
            query.setEndstelleId(endstelleId);

            List<AuftragEndstelleView> currentES = getCCService(CCAuftragService.class)
                    .findAuftragEndstelleViews(query);
            if ((currentES != null) && (currentES.size() == 1)) {
                AuftragEndstelleView view = currentES.get(0);

                List<AuftragEndstelleView> result = auftragViewDAO.findAESViews4Wandel(view.getEndstelleGeoId(),
                        view.getKundeNo(), analog2isdn);

                KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
                if (ks != null) {
                    ks.loadKundendaten4AuftragViews(result);
                }

                return result;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragEndstelleView> findEndstellen4TalPortAenderung(Long geoId, String esTyp) throws FindException {
        if ((geoId == null) || (esTyp == null)) {
            return null;
        }
        try {
            List<AuftragEndstelleView> result = auftragViewDAO.findAESViews4TalPortAenderung(geoId, esTyp);
            KundenService ks = getBillingService(KundenService.class.getName(), KundenService.class);
            if (ks != null) {
                ks.loadKundendaten4AuftragViews(result);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public EndstelleLtgDaten findESLtgDaten4ES(Long esId) throws FindException {
        return findESLtgDaten4ESTx(esId);
    }

    @Override
    public EndstelleLtgDaten findESLtgDaten4ESTx(Long esId) throws FindException {
        if (esId == null) {
            return null;
        }
        try {
            return endstelleLtgDAO.findByEsId(esId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public EndstelleLtgDaten saveESLtgDaten(EndstelleLtgDaten toSave, boolean makeHistory) throws StoreException,
            ValidationException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        ValidationException ve = new ValidationException(toSave, "EndstelleLtgDaten");
        endstelleLtgDatenValidator.validate(toSave, ve);
        if (ve.hasErrors()) {
            throw ve;
        }

        try {
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                EndstelleLtgDaten newLtg = endstelleLtgDAO.update4History(toSave, toSave.getId(), now);

                return newLtg;
            }
            else {
                // pruefen, ob bereits EsLtg-Daten vorhanden sind. Wenn ja, diese beenden.
                EndstelleLtgDaten existing = findESLtgDaten4ES(toSave.getEndstelleId());
                if (existing != null) {
                    if (!existing.equals(toSave)) {
                        // alten Datensatz beenden
                        Date now = new Date();
                        existing.setGueltigBis(now);
                        endstelleLtgDAO.store(existing);
                        // neuen Datensatz einfuegen
                        HistoryHelper.checkHistoryDates(toSave);
                        endstelleLtgDAO.store(toSave);
                    }
                    else {
                        // Datensatz wird aktualisiert, benutze 'existing', da dieses bereits an
                        // der Session haengt
                        HistoryHelper.checkHistoryDates(existing);
                        PropertyUtils.copyProperties(existing, toSave);
                        endstelleLtgDAO.store(existing);
                    }
                }
                else { // neuer Datensatz
                    HistoryHelper.checkHistoryDates(toSave);
                    endstelleLtgDAO.store(toSave);
                }

                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public EndstelleAnsprechpartner findESAnsp4ES(Long endstellenId) throws FindException {
        return findESAnsp4ESTx(endstellenId);
    }

    @Override
    public EndstelleAnsprechpartner findESAnsp4ESTx(Long endstellenId) throws FindException {
        if (endstellenId == null) {
            return null;
        }
        try {
            Endstelle endstelle = findEndstelle(endstellenId);
            if (endstelle != null) {
                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstellenId);

                if (auftragDaten != null) {
                    Ansprechpartner.Typ ansprechpartnerTyp = endstelle.isEndstelleB() ? ENDSTELLE_B : ENDSTELLE_A;
                    Ansprechpartner ansprechpartner = getCCService(AnsprechpartnerService.class)
                            .findPreferredAnsprechpartner(ansprechpartnerTyp, auftragDaten.getAuftragId());
                    if (ansprechpartner != null) {
                        EndstelleAnsprechpartner endstelleAnsprechpartner = new EndstelleAnsprechpartner();
                        endstelleAnsprechpartner.setAnsprechpartner(ansprechpartner.getDisplayText());
                        return endstelleAnsprechpartner;
                    }
                }
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void copyAPAddress(AuftragDaten auftragDaten, Long sessionId) throws FindException, StoreException {
        if (auftragDaten == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(CopyAPAddressCommand.class);
            cmd.prepare(CopyAPAddressCommand.KEY_AUFTRAG_DATEN, auftragDaten);
            cmd.prepare(CopyAPAddressCommand.KEY_SESSION_ID, sessionId);
            cmd.execute();
        }
        catch (ServiceCommandException e) {
            throw new StoreException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Boolean hasAPAddressChanged(AuftragDaten ad) throws FindException {
        if ((ad == null) || (ad.getAuftragNoOrig() == null)) {
            return Boolean.FALSE;
        }
        try {
            ProduktService prodService = getCCService(ProduktService.class);
            Produkt produkt = prodService.findProdukt(ad.getProdId());
            if (produkt == null) {
                throw new FindException("Produkt konnte nicht ermittelt werden!");
            }

            if (BooleanTools.nullToFalse(produkt.getCreateAPAddress())) {
                return Boolean.FALSE;
            }
            if (NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_A_UND_B)) {
                return hasAPAddressChanged(ad, Endstelle.ENDSTELLEN_TYP_B)
                        || hasAPAddressChanged(ad, Endstelle.ENDSTELLEN_TYP_A);
            }
            if (NumberTools.equal(produkt.getEndstellenTyp(), Produkt.ES_TYP_NUR_B)) {
                return hasAPAddressChanged(ad, Endstelle.ENDSTELLEN_TYP_B);
            }
            return Boolean.FALSE;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Funktion ueberprueft eine Endstellen-Adresse des Auftrags
     */
    @Override
    public Boolean hasAPAddressChanged(AuftragDaten auftragDaten, String esTyp) throws FindException {
        if (auftragDaten == null) {
            return Boolean.FALSE;
        }
        try {
            // Endstelle laden
            Endstelle es = findEndstelle4Auftrag(auftragDaten.getAuftragId(), esTyp);
            if (es == null) {
                throw new FindException("Endstelle " + esTyp + " zum Auftrag " + auftragDaten.getAuftragId().toString()
                        + " konnte nicht ermittelt werden!");
            }

            // Taifun-Auftrag ermitteln
            BillingAuftragService bas = getBillingService(BillingAuftragService.class);
            Adresse billingAddress = bas.findAnschlussAdresse4Auftrag(auftragDaten.getAuftragNoOrig(), esTyp);
            if (billingAddress != null) {
                // Endstellendaten pruefen
                if (!StringUtils.equals(es.getName(), billingAddress.getCombinedNameData())) {
                    return Boolean.TRUE;
                }
                // GeoId pruefen
                if ((billingAddress.getGeoId() != null) && (es.getGeoId() != null)
                        && NumberTools.notEqual(billingAddress.getGeoId(), es.getGeoId())) {
                    return Boolean.TRUE;
                }
                else if ((billingAddress.getGeoId() != null) && (es.getGeoId() == null)) {
                    return Boolean.TRUE;
                }
                else if (NumberTools.equal(billingAddress.getGeoId(), es.getGeoId())
                        && (StringUtils.isBlank(es.getOrt()) || !StringUtils.equals(es.getOrt(),
                        billingAddress.getCombinedOrtOrtsteil()))) {
                    // falls GeoID in Hurrican/Taifun identisch, aber in Hurrican kein Ort angegeben - Abgleich
                    // durchfuehren
                    return Boolean.TRUE;
                }
                else if ((billingAddress.getGeoId() == null) && (es.getGeoId() == null)) {
                    if (!StringUtils.equals(es.getOrt(), billingAddress.getCombinedOrtOrtsteil())
                            || !StringUtils.equals(es.getPlz(), billingAddress.getPlz())) {
                        return Boolean.TRUE;
                    }
                }
                else if (!es.getEndstelle().equals(billingAddress.getCombinedStreetData())) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AddressModel findAnschlussadresse4Auftrag(Long auftragId, String esTyp) throws FindException {
        if ((auftragId == null) || (esTyp == null)) {
            return null;
        }

        try {
            CCAuftragService cas = getCCService(CCAuftragService.class);
            AuftragDaten ad = cas.findAuftragDatenByAuftragIdTx(auftragId);
            if (ad != null) {
                ProduktService ps = getCCService(ProduktService.class);
                Produkt prod = ps.findProdukt(ad.getProdId());

                // Entscheide, ob Endstellenadresse aus Hurrican oder
                // Anschlussadresse aus Taifun verwendet werden soll.
                if ((prod != null) && !BooleanTools.nullToFalse(prod.getCreateAPAddress())) {
                    BillingAuftragService bas = getBillingService(BillingAuftragService.class);
                    return bas.findAnschlussAdresse4Auftrag(ad.getAuftragNoOrig(), esTyp);
                }

                Endstelle es = findEndstelle4Auftrag(auftragId, esTyp);
                if (es != null) {
                    CCKundenService cks = getCCService(CCKundenService.class);
                    CCAddress address = cks.findCCAddress(es.getAddressId());
                    return address;
                }

                throw new FindException("Fehler beim Ermitteln der Endstelle.");
            }

            throw new FindException("Auftragsdaten konnten nicht ermittelt werden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Standortadresse: " + e.getMessage(), e);
        }
    }

    @Override
    public void transferBuendel2Endstellen(List<Endstelle> endstellen, Integer buendelNr, String buendelNrHerkunft)
            throws FindException {
        if (CollectionTools.isEmpty(endstellen) || (buendelNr == null) || (buendelNrHerkunft == null)) {
            return;
        }
        try {
            CCAuftragService ccAS = getCCService(CCAuftragService.class);
            AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);

            AuftragDaten auftragDatenDest = ccAS.findAuftragDatenByEndstelle(endstellen.get(0).getId());

            // 1. Auftraege zur Buendel-Nr suchen
            List<AuftragDaten> auftragsDaten = ccAS.findAuftragDaten4Buendel(buendelNr, buendelNrHerkunft);
            if (CollectionTools.isEmpty(auftragsDaten)) {
                return;
            }

            boolean esFound = false;
            boolean esAnspFound = false;
            for (AuftragDaten ad : auftragsDaten) {
                // 2. Endstellen des Auftrags ermitteln
                List<Endstelle> es4AD = findEndstellen4Auftrag(ad.getAuftragId());
                if (es4AD != null) {
                    for (Endstelle esRef : es4AD) {
                        handleNewEndstellen(endstellen, ansprechpartnerService, auftragDatenDest, esAnspFound, ad, esRef);
                    }

                    esFound = true;
                }

                if (esFound) {
                    break;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Beim Kopieren der Endstellen-Daten von dem zugehörigen "
                    + "Bündelauftrag ist ein Fehler aufgetreten. Bitte geben Sie die Endstellen-Daten selbst ein.", e);
        }
    }

    private void handleNewEndstellen(List<Endstelle> endstellen, AnsprechpartnerService ansprechpartnerService,
            AuftragDaten auftragDatenDest, boolean esAnspFound, AuftragDaten ad, Endstelle esRef) throws StoreException {
        for (Endstelle esNew : endstellen) {
            if (StringUtils.equals(esRef.getEndstelleTyp(), esNew.getEndstelleTyp())) {
                // 3. Endstellendaten kopieren
                copyEndstelle(esRef, esNew, Endstelle.COPY_SMALL);

                try {
                    if (!esAnspFound) {
                        // 4. Ansprechpartner der Endstelle kopieren
                        Ansprechpartner.Typ ansprechpartnerTyp = esNew.isEndstelleB() ? Ansprechpartner.Typ.ENDSTELLE_B
                                : Ansprechpartner.Typ.ENDSTELLE_A;
                        ansprechpartnerService.copyAnsprechpartner(ansprechpartnerTyp,
                                ad.getAuftragId(), auftragDatenDest.getAuftragId());
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

}
