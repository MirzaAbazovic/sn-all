/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 11:58:06
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.HibernateSessionHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EndstelleDAO;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.GewofagDao;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GewofagWohnung;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.service.locator.ServiceLocator;


/**
 *
 *
 */
@CcTxRequired
public class GewofagServiceImpl extends DefaultCCService implements GewofagService {
    private static final Logger LOGGER = Logger.getLogger(GewofagServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.GewofagDao")
    private GewofagDao gewofagDao;
    @Resource(name = "endstelleDAO")
    private EndstelleDAO endstelleDAO;
    @Resource(name = "equipmentDAO")
    private EquipmentDAO equipmentDAO;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Autowired
    private ServiceLocator serviceLocator;


    /**
     * @see de.augustakom.hurrican.service.cc.GewofagService#findGewofagWohnung(java.lang.Long)
     */
    @Override
    public GewofagWohnung findGewofagWohnung(Long gewoWhgId) throws FindException {
        try {
            return gewofagDao.findById(gewoWhgId, GewofagWohnung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * @see de.augustakom.hurrican.service.cc.GewofagService#findGewofagWohnung(java.lang.Long)
     */
    @Override
    public GewofagWohnung findGewofagWohnung(Equipment equipment) throws FindException {
        try {
            return gewofagDao.findGewofagWohnung(equipment);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<GewofagWohnung> findGewofagWohnungenByGeoId(GeoId geoId) throws FindException {
        try {
            return gewofagDao.findGewofagWohnungenByGeoId(geoId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * Vorgehen: 1. existierende Rangierung ermitteln 2. pruefen, ob das das ist, was der Kunde moechte. falls ja =>
     * done 3. alte rangierung historisieren 4. neue rangierung aufbauen
     */
    @Override
    public void wohnungsZuordnung(Endstelle endstelle, GewofagWohnung gewofagWohnung, Long sessionId)
            throws StoreException {
        List<Rangierung> existingRangierungen = performSanityChecks(endstelle, gewofagWohnung);
        try {
            String userName = getLoginNameSilent(sessionId);

            AuftragDaten auftragDaten = findAuftragDaten(endstelle);
            List<Produkt2PhysikTyp> produkt2PhysikTypen = physikService
                    .findP2PTs4Produkt(auftragDaten.getProdId(), null);

            if (!existingRangierungen.isEmpty()) {
                if (findMatchingProdukt2PhysikTyp(existingRangierungen, produkt2PhysikTypen)) {
                    // Die existierende Rangierung kann genutzt werden!
                    for (Rangierung rangierung : existingRangierungen) {
                        rangierungsService.loadEquipments(rangierung);
                        rangierung.setEsId(endstelle.getId());
                        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
                        rangierung.setUserW(userName);
                        rangierung.setFreigabeAb(null);
                        rangierung.setFreigegeben(Freigegeben.freigegeben);
                    }
                    saveRangierungen(existingRangierungen, endstelle);
                    return;
                }
                else {
                    // alte Rangierung muss aufgebrochen werden
                    rangierungsService.breakAndDeactivateRangierung(existingRangierungen, false, sessionId);
                    HibernateSessionHelper.clearAndFlushSession(serviceLocator);
                }
            }

            // neue Rangierung fuer den Auftrag anlegen
            List<Rangierung> rangierungen = createRangierungen(existingRangierungen, endstelle, gewofagWohnung, produkt2PhysikTypen, userName);
            saveRangierungen(rangierungen, endstelle);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * Ueberprueft, ob die Zuordnung ueberhaupt erfolgen darf. Falls nicht, wird eine Exception geworfen. Falls valide,
     * wird eine Liste der derzeit fuer die Wohnung aktiven Rangierungen zurueck geliefert.
     */
    private List<Rangierung> performSanityChecks(Endstelle endstelle, GewofagWohnung gewofagWohnung) throws StoreException {
        if (endstelle.getRangierId() != null) {
            throw new StoreException("Endstelle besitzt schon eine Rangierung!");
        }
        List<Rangierung> existingRangierungen;
        try {
            existingRangierungen = findExistingRangierungen(gewofagWohnung);
            if ((!existingRangierungen.isEmpty()) && (existingRangierungen.get(0).getEsId() != null) &&
                    (existingRangierungen.get(0).getEsId() > Rangierung.RANGIERUNG_NOT_ACTIVE)) {
                throw new StoreException("Wohnungs-Port ist noch aktiv");
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
        return existingRangierungen;
    }


    /**
     * Erstellt Rangierungen
     * <p/>
     * Gewofag: Nur ISDN only, Analog only, ISDN/Analog+DSL moeglich, kein DSL only
     *
     * @return Die erstellten Rangierungen
     */
    @SuppressWarnings("null") // Nur wenn alle Equipments != null wird useEq2 gesetzt
    private List<Rangierung> createRangierungen(List<Rangierung> existingRangierungen, Endstelle endstelle,
            GewofagWohnung gewofagWohnung, List<Produkt2PhysikTyp> produkt2PhysikTypen, String username) throws FindException, StoreException {
        boolean kombiRangierung = false;
        Equipment eqIn1 = null;
        Equipment eqOut2 = null;
        Equipment eqIn2 = null;
        Long physikTypId = null;
        Long physikTypAdditionalId = null;
        Date now = new Date();

        for (Produkt2PhysikTyp p2pt : produkt2PhysikTypen) {
            // AB/UK0 only
            if (p2pt.getPhysikTypAdditionalId() == null) {
                List<Equipment> possibleEquipments = equipmentDAO.findEquipmentsForPhysiktyp(endstelle.getHvtIdStandort(),
                        p2pt.getPhysikTypId());
                eqIn1 = findFreeInEquipment(possibleEquipments);
                if (eqIn1 != null) {
                    physikTypId = p2pt.getPhysikTypId();
                    break;
                }
            }
            // AB/UK0 + ADSL
            else {
                List<Equipment> possibleEquipments1 = equipmentDAO.findEquipmentsForPhysiktyp(endstelle.getHvtIdStandort(),
                        p2pt.getPhysikTypId());
                List<Equipment> possibleEquipments2 = equipmentDAO.findEquipmentsForPhysiktyp(endstelle.getHvtIdStandort(),
                        p2pt.getPhysikTypAdditionalId());
                eqIn1 = findFreeInEquipment(possibleEquipments1);
                eqOut2 = equipmentDAO.findCorrespondingEquipment(eqIn1);
                eqIn2 = findFreeInEquipment(possibleEquipments2);
                if ((eqIn1 != null) && (eqIn2 != null) && (eqOut2 != null)) {
                    physikTypId = p2pt.getPhysikTypId();
                    physikTypAdditionalId = p2pt.getPhysikTypAdditionalId();
                    kombiRangierung = true;
                    break;
                }
            }
        }

        if (eqIn1 == null) {
            throw new FindException("Kein freier Port gefunden!");
        }

        Rangierung previousRangierung = null;
        if (!existingRangierungen.isEmpty()) {
            for (Rangierung existingRangierung : existingRangierungen) {
                if ((existingRangierung.getLeitungLfdNr() == null) ||
                        (existingRangierung.getLeitungLfdNr().compareTo(1) == 0)) {
                    previousRangierung = existingRangierung;
                }
            }
        }

        List<Rangierung> result = new ArrayList<>();
        Rangierung rangierung = new Rangierung();
        rangierung.setEsId(endstelle.getId());
        rangierung.setHvtIdStandort(endstelle.getHvtIdStandort());
        rangierung.setEqOutId(gewofagWohnung.getEquipment().getId());
        rangierung.setEquipmentOut(gewofagWohnung.getEquipment());
        rangierung.setEqInId(eqIn1.getId());
        rangierung.setEquipmentIn(eqIn1);
        rangierung.setFreigegeben(Freigegeben.freigegeben);
        rangierung.setGueltigVon(now);
        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
        rangierung.setUserW(username);
        rangierung.setPhysikTypId(physikTypId);
        rangierung.setHistoryFrom(previousRangierung != null ? previousRangierung.getId() : null);
        rangierung.setHistoryCount((previousRangierung != null) && (previousRangierung.getHistoryCount() != null)
                ? previousRangierung.getHistoryCount() + 1 : 1);
        rangierungsService.saveRangierung(rangierung, false);
        result.add(rangierung);
        if (kombiRangierung) {
            Integer leitungGesamtId = rangierungsService.findNextLtgGesId4Rangierung();
            rangierung.setLeitungGesamtId(leitungGesamtId);
            rangierung.setLeitungLfdNr(1);

            Rangierung rangierung2 = new Rangierung();
            rangierung2.setEsId(endstelle.getId());
            rangierung2.setHvtIdStandort(endstelle.getHvtIdStandort());
            rangierung2.setEqOutId(eqOut2.getId());
            rangierung2.setEquipmentOut(eqOut2);
            rangierung2.setEqInId(eqIn2.getId());
            rangierung2.setEquipmentIn(eqIn2);
            rangierung2.setFreigegeben(Freigegeben.freigegeben);
            rangierung2.setGueltigVon(now);
            rangierung2.setGueltigBis(DateTools.getHurricanEndDate());
            rangierung2.setUserW(username);
            rangierung2.setPhysikTypId(physikTypAdditionalId);
            rangierung2.setLeitungGesamtId(leitungGesamtId);
            rangierung2.setLeitungLfdNr(2);
            rangierungsService.saveRangierung(rangierung2, false);
            result.add(rangierung2);
        }
        return result;
    }


    /**
     * Speichert die gegebenen Rangierungen und setzt und speichert entsprechende Werte in den Equipments und der
     * Endstelle
     */
    private void saveRangierungen(List<Rangierung> rangierungen, Endstelle endstelle) throws StoreException {
        for (Rangierung rangierung : rangierungen) {
            if ((rangierung.getLeitungLfdNr() == null) || Integer.valueOf(1).equals(rangierung.getLeitungLfdNr())) {
                endstelle.setRangierId(rangierung.getId());
            }
            else if (Integer.valueOf(2).equals(rangierung.getLeitungLfdNr())) {
                endstelle.setRangierIdAdditional(rangierung.getId());
            }
            else {
                throw new StoreException("LeitungLfdNr = " + rangierung.getLeitungLfdNr() + ", but logic can only handle null, 1, 2");
            }
            rangierung.getEquipmentIn().setStatus(EqStatus.rang);
            equipmentDAO.store(rangierung.getEquipmentIn());
            rangierung.getEquipmentOut().setStatus(EqStatus.rang);
            equipmentDAO.store(rangierung.getEquipmentOut());
        }
        endstelleDAO.store(endstelle);
    }


    /**
     * Untersucht die als 'frei' markierten Equipments, ob ihre Rangierungen tatsaechlich schon abgebaut sind. Der erste
     * wirklich freie Port wird zurueckgeliefert.
     */
    private Equipment findFreeInEquipment(List<Equipment> possibleEquipments) throws FindException {
        for (Equipment equipment : possibleEquipments) {
            // Muss != -IN sein fuer IN ports (da AB/UK0 und -OUT gesucht wird)
            if ((equipment.getHwSchnittstelle() == null) || !equipment.getHwSchnittstelle().endsWith("-IN")) {
                Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId(), true);
                if (rangierung == null) {
                    return equipment;
                }
            }
        }
        return null;
    }


    /**
     * Findet existierende Rangierungen fuer die gegebene GewofagWohnung
     */
    private List<Rangierung> findExistingRangierungen(GewofagWohnung gewofagWohnung) throws FindException {
        List<Rangierung> existingRangierungen = new ArrayList<>();
        Rangierung existingRangierung = rangierungsService.findRangierung4Equipment(gewofagWohnung.getEquipment().getId(), false);
        if ((existingRangierung != null) && (existingRangierung.getLeitungGesamtId() != null)) {
            existingRangierungen = rangierungsService.findByLtgGesId(existingRangierung.getLeitungGesamtId());
        }
        else if (existingRangierung != null) {
            existingRangierungen.add(existingRangierung);
        }
        return existingRangierungen;
    }


    /**
     * Findet AuftragDaten zu einer Endstelle
     *
     * @throws FindException falls der Auftrag gekuendigt ist
     */
    private AuftragDaten findAuftragDaten(Endstelle endstelle) throws FindException, StoreException {
        AuftragTechnik auftragTechnik = auftragService.findAuftragTechnik4ESGruppe(endstelle.getEndstelleGruppeId());
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragTechnik.getAuftragId());
        if (AuftragStatus.KUENDIGUNG.compareTo(auftragDaten.getStatusId()) <= 0) {
            throw new FindException(
                    "Der Endstelle kann keine Physik zugeordnet werden, da der zugehörige Auftrag bereits gekündigt ist!");
        }
        return auftragDaten;
    }


    /**
     * Falls das Produkt mit den existierenden Rangierungen realisiert werden kann, wird der entsprechende
     * Produkt2PhysikTyp zurueck geliefert.
     */
    private boolean findMatchingProdukt2PhysikTyp(List<Rangierung> existingRangierungen,
            List<Produkt2PhysikTyp> produkt2PhysikTypen) throws FindException {
        for (Produkt2PhysikTyp produkt2PhysikTyp : produkt2PhysikTypen) {
            if ((produkt2PhysikTyp.getPhysikTypAdditionalId() == null) && (existingRangierungen.size() == 1)) {
                if (produkt2PhysikTyp.getPhysikTypId().equals(existingRangierungen.get(0).getPhysikTypId())) {
                    return true;
                }
            }
            else if ((produkt2PhysikTyp.getPhysikTypAdditionalId() != null) && (existingRangierungen.size() == 2)) {
                return produkt2PhysikTyp.getPhysikTypId().equals(existingRangierungen.get(0).getPhysikTypId())
                        && produkt2PhysikTyp.getPhysikTypAdditionalId().equals(
                        existingRangierungen.get(1).getPhysikTypId());
            }
        }
        return false;
    }


    @Override
    public void replaceGeoIdsOfGewofag(GeoId geoId, GeoId replacedByGeoId) throws FindException {
        List<GewofagWohnung> gewofagWohnungen = findGewofagWohnungenByGeoId(geoId);
        if (CollectionTools.isNotEmpty(gewofagWohnungen)) {
            for (GewofagWohnung gewofagWohnung : gewofagWohnungen) {
                gewofagWohnung.setGeoId(replacedByGeoId);
                saveGewofagWohnung(gewofagWohnung);
                LOGGER.info("GEOID: " + replacedByGeoId + " auf der Wohnung(" + gewofagWohnung.getId() + ") aktualisiert");
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.GewofagService#saveGewofagWohnung(de.augustakom.hurrican.model.cc.GewofagWohnung)
     */
    @Override
    public void saveGewofagWohnung(GewofagWohnung gewofagWohnung) {
        gewofagDao.store(gewofagWohnung);
    }


    /**
     * Injected
     */
    public void setGewofagDao(GewofagDao gewofagDao) {
        this.gewofagDao = gewofagDao;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Injected
     */
    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

    /**
     * Injected
     */
    public void setEndstelleDAO(EndstelleDAO endstelleDAO) {
        this.endstelleDAO = endstelleDAO;
    }
}
