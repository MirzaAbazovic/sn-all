/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.13 16:50
 */
package de.augustakom.hurrican.service.location;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdCarrierAddress;
import de.augustakom.hurrican.model.cc.GeoIdCity;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdCountry;
import de.augustakom.hurrican.model.cc.GeoIdDistrict;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.GeoIdStreetSection;
import de.augustakom.hurrican.model.cc.GeoIdZipCode;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.GewofagService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.location.v1.City;
import de.mnet.esb.cdm.resource.location.v1.Country;
import de.mnet.esb.cdm.resource.location.v1.District;
import de.mnet.esb.cdm.resource.location.v1.Location;
import de.mnet.esb.cdm.resource.location.v1.StreetSection;
import de.mnet.esb.cdm.resource.location.v1.ZipCode;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyReplaceLocation;
import de.mnet.esb.cdm.resource.locationnotificationservice.v1.NotifyUpdateLocation;


@Component
@CcTxRequired
public class LocationNotificationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationNotificationHelper.class);
    @Resource(name = "de.augustakom.hurrican.dao.cc.GeoIdDAO")
    protected GeoIdDAO geoIdDao;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityServiceHelper")
    AvailabilityServiceHelper availabilityServiceHelper;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HvtUmzugService")
    private HvtUmzugService hvtUmzugService;
    @Autowired
    private ServiceLocator serviceLocator;
    private GewofagService gewofagService;


    @CcTxRequiresNew
    public Pair<GeoIdLocation, AKWarnings> updateLocation(final NotifyUpdateLocation in, final boolean createIfNotExist, Long sessionId) throws FindException, StoreException {
        if (in.getBuilding() != null) {
            return updateBuilding(in.getBuilding(), createIfNotExist, sessionId);
        }
        else if (in.getStreetSection() != null) {
            return new Pair<>(updateStreetSection(in.getStreetSection(), createIfNotExist), null);
        }
        else if (in.getZipCode() != null) {
            return new Pair<>(updateZipCode(in.getZipCode(), createIfNotExist), null);
        }
        else if (in.getCity() != null) {
            return new Pair<>(updateCity(in.getCity(), createIfNotExist), null);
        }
        else if (in.getCountry() != null) {
            return new Pair<>(updateCountry(in.getCountry(), createIfNotExist), null);
        }
        else if (in.getDistrict() != null) {
            return new Pair<>(updateDistrict(in.getDistrict(), createIfNotExist), null);
        }
        return null;
    }

    private Pair<GeoIdLocation, AKWarnings> updateBuilding(Building building, boolean createIfNotExist, Long sessionId) throws StoreException, FindException {
        availabilityServiceHelper.deleteGeoIdClarification4GeoId(building.getId(), ImmutableList.of(
                GeoIdClarification.Status.OPEN, GeoIdClarification.Status.IN_PROGRESS,
                GeoIdClarification.Status.SUSPENDED));

        final boolean buildingExists = geoIdDao.findLocation(GeoId.class, building.getId()) != null;
        if (!buildingExists && !createIfNotExist) {
            return new Pair<>(null, null);
        }

        final GeoId entity;
        try {
            entity = updateLocation(GeoId.class, building, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("building notification is obsolete, skipping update", e);
            return new Pair<>(e.getEntity(), null);
        }
        entity.setHouseNum(building.getHouseNumber());
        entity.setHouseNumExtension(building.getHouseNumberExtension());
        entity.setAgsn(building.getAgsn());
        if (building.getTAL() != null) {
            entity.setOnkz(building.getTAL().getOnkz());
            entity.setAsb(building.getTAL().getAsb());
            entity.setKvz(building.getTAL().getKvz());
        }
        else {
            entity.setOnkz(null);
            entity.setAsb(null);
            entity.setKvz(null);
        }

        if (building.getInfas() != null) {
            Building.Infas infas = building.getInfas();
            entity.setDistance(infas.getDistance());
        }
        else {
            entity.setDistance(null);
        }

        // update carrier addresses
        Set<String> newCarrierNames = Sets.newHashSet();
        Set<String> oldCarrierNames = entity.getCarrierAddresses().keySet();
        for (Building.CarrierAddress ca : building.getCarrierAddress()) {
            newCarrierNames.add(ca.getCarrier());
            GeoIdCarrierAddress entityCa = entity.getCarrierAddresses().get(ca.getCarrier());
            if (entityCa == null) {
                // add missing
                entityCa = new GeoIdCarrierAddress();
                entity.getCarrierAddresses().put(ca.getCarrier(), entityCa);
            }
            entityCa.setHouseNum(ca.getHouseNumber());
            entityCa.setHouseNumExtension(ca.getHouseNumberExtension());
            entityCa.setStreet(ca.getStreet());
            entityCa.setDistrict(ca.getDistrict());
            entityCa.setZipCode(ca.getZipCode());
            entityCa.setCity(ca.getCity());
        }
        Set<String> diffCarrierNames = Sets.difference(oldCarrierNames, newCarrierNames);
        for (String carrierName : diffCarrierNames) {
            // remove superfluous
            entity.getCarrierAddresses().remove(carrierName);
        }

        entity.setStreetSection(updateStreetSection(building.getStreet(), true));
        geoIdDao.save(entity);

        // Technische Standorte anlegen oder aktualisieren und ggf. Klaerfaelle erzeugen
        AKWarnings warnings = new AKWarnings();
        if (!buildingExists) {
            processMissingGeoId(entity, sessionId, warnings);
        }
        else {
            processExistingGeoId(entity, sessionId, warnings);
        }

        return new Pair<>(entity, warnings);
    }

    private GeoIdStreetSection updateStreetSection(StreetSection street, boolean createIfNotExist) {
        final GeoIdStreetSection entity;
        try {
            entity = updateLocation(GeoIdStreetSection.class, street, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("street notification is obsolete, skipping update", e);
            return (GeoIdStreetSection) e.getEntity();
        }
        entity.setName(street.getName());
        entity.setDistrict(updateDistrict(street.getDistrict(), true));
        entity.setZipCode(updateZipCode(street.getZipCode(), true));
        geoIdDao.save(entity);
        return entity;
    }

    private GeoIdDistrict updateDistrict(District district, boolean createIfNotExist) {
        if (district == null) {
            return null;
        }
        final GeoIdDistrict entity;
        try {
            entity = updateLocation(GeoIdDistrict.class, district, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("district notification is obsolete, skipping update", e);
            return (GeoIdDistrict) e.getEntity();
        }
        entity.setName(district.getName());
        geoIdDao.save(entity);
        return entity;
    }

    private GeoIdZipCode updateZipCode(ZipCode zipCode, boolean createIfNotExist) {
        final GeoIdZipCode entity;
        try {
            entity = updateLocation(GeoIdZipCode.class, zipCode, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("zipCode notification is obsolete, skipping update", e);
            return (GeoIdZipCode) e.getEntity();
        }
        entity.setZipCode(zipCode.getZipCode());
        entity.setCity(updateCity(zipCode.getCity(), true));
        geoIdDao.save(entity);
        return entity;
    }

    private GeoIdCity updateCity(City city, boolean createIfNotExist) {
        final GeoIdCity entity;
        try {
            entity = updateLocation(GeoIdCity.class, city, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("city notification is obsolete, skipping update", e);
            return (GeoIdCity) e.getEntity();
        }
        entity.setName(city.getName());
        entity.setCountry(updateCountry(city.getCountry(), true));
        geoIdDao.save(entity);
        return entity;
    }

    private GeoIdCountry updateCountry(Country country, boolean createIfNotExist) {
        final GeoIdCountry entity;
        try {
            entity = updateLocation(GeoIdCountry.class, country, createIfNotExist);
            if (entity == null) {
                return null;
            }
        }
        catch (ObsoleteNotificationException e) {
            LOGGER.warn("country notification is obsolete, skipping update", e);
            return (GeoIdCountry) e.getEntity();
        }
        entity.setName(country.getName());
        geoIdDao.save(entity);
        return entity;
    }

    private <T extends GeoIdLocation> T updateLocation(Class<T> clazz, Location location, boolean createIfNotExist)
            throws ObsoleteNotificationException {
        T entity = geoIdDao.findLocation(clazz, location.getId());
        if (entity == null) {
            if (createIfNotExist) {
                try {
                    entity = clazz.getConstructor().newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException("Unable to create instance of " + clazz.getName(), e);
                }
                entity.setId(location.getId());
            }
            else {
                return null;
            }
        }
        else {
            if (entity.getModified().after(location.getModified())) {
                throw new ObsoleteNotificationException(entity, location);
            }
        }
        entity.setModified(location.getModified());
        entity.setServiceable(location.isServiceable());
        entity.setTechnicalId(location.getTechnicalId());
        return entity;
    }

    @CcTxRequiresNew
    public Pair<GeoIdLocation, GeoIdLocation> replaceLocation(final NotifyReplaceLocation in, final boolean createIfNotExist, Long sessionId) throws FindException, StoreException {
        if (in.getNewBuilding() != null) {
            GeoId oldBuilding = geoIdDao.findLocation(GeoId.class, in.getOldBuilding().getId());
            if (oldBuilding == null && !createIfNotExist) {
                return null;
            }

            Pair<GeoIdLocation, AKWarnings> updateResult = updateBuilding(in.getNewBuilding(), true, sessionId);
            GeoId newBuilding = (GeoId) updateResult.getFirst();
            if (oldBuilding != null) {
                replace(oldBuilding, newBuilding);
                processGeoIdReplacement(newBuilding, oldBuilding);
                return new Pair<>(oldBuilding, newBuilding);
            }
        }
        else if (in.getNewStreetSection() != null) {
            GeoIdLocation oldLocation = geoIdDao.findLocation(GeoIdStreetSection.class, in.getOldStreetSection().getId());
            if (oldLocation == null && !createIfNotExist) {
                return null;
            }

            GeoIdLocation newLocation = updateStreetSection(in.getNewStreetSection(), true);
            if (oldLocation != null) {
                return replace(oldLocation, newLocation);
            }
        }
        else if (in.getNewZipCode() != null) {
            GeoIdLocation oldLocation = geoIdDao.findLocation(GeoIdZipCode.class, in.getOldZipCode().getId());
            if (oldLocation == null && !createIfNotExist) {
                return null;
            }

            GeoIdLocation newLocation = updateZipCode(in.getNewZipCode(), true);
            if (oldLocation != null) {
                return replace(oldLocation, newLocation);
            }
        }
        else if (in.getNewCity() != null) {
            GeoIdLocation oldLocation = geoIdDao.findLocation(GeoIdCity.class, in.getOldCity().getId());
            if (oldLocation == null && !createIfNotExist) {
                return null;
            }

            GeoIdLocation newLocation = updateCity(in.getNewCity(), true);
            if (oldLocation != null) {
                return replace(oldLocation, newLocation);
            }
        }
        else if (in.getNewCountry() != null) {
            GeoIdLocation oldLocation = geoIdDao.findLocation(GeoIdCountry.class, in.getOldCountry().getId());
            if (oldLocation == null && !createIfNotExist) {
                return null;
            }

            GeoIdLocation newLocation = updateCountry(in.getNewCountry(), true);
            if (oldLocation != null) {
                return replace(oldLocation, newLocation);
            }
        }
        else if (in.getNewDistrict() != null) {
            GeoIdLocation oldLocation = geoIdDao.findLocation(GeoIdDistrict.class, in.getOldDistrict().getId());
            if (oldLocation == null && !createIfNotExist) {
                return null;
            }

            GeoIdLocation newLocation = updateDistrict(in.getNewDistrict(), true);
            if (oldLocation != null) {
                return replace(oldLocation, newLocation);
            }
        }
        return null;
    }

    private Pair<GeoIdLocation, GeoIdLocation> replace(GeoIdLocation oldLocation, GeoIdLocation newLocation) {
        oldLocation.setReplacedById(newLocation.getId());
        geoIdDao.save(oldLocation);
        return new Pair<>(oldLocation, newLocation);
    }


    /**
     * Verarbeitet eine Location, die noch nicht im Hurrican Cache abgelegt ist. Gefangene Exceptions werden in
     * Klärungsliste eingetragen.
     *
     * @throws StoreException
     * @throws FindException
     */
    void processMissingGeoId(GeoId building, Long sessionId, AKWarnings warnings) throws FindException,
            StoreException {
        HVTStandort hvtStandort = findHVTStandortByLocation(building,
                HVTStandort.HVT_STANDORT_TYP_HVT, sessionId, warnings, GeoIdClarification.Type.INSERT_GEO_ID);
        if (hvtStandort != null) {
            checkAndWriteTechLocationMapping(building, hvtStandort, null, sessionId);
        }

        HVTStandort kvzStandort = findHVTStandortByLocation(building,
                HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ, sessionId, warnings, GeoIdClarification.Type.INSERT_GEO_ID);
        if (kvzStandort != null) {
            checkAndWriteTechLocationMapping(building, kvzStandort, null, sessionId);
        }
    }

    /**
     * Verarbeitet eine Location, die bereits im Hurrican Cache abgelegt sind. Gefangene Exceptions werden in
     * Klärungsliste eingetragen.
     *
     * @throws FindException
     * @throws StoreException
     */
    public void processExistingGeoId(GeoId building, Long sessionId, AKWarnings warnings)
            throws FindException, StoreException {
        if (BooleanTools.nullToFalse(building.getNoDTAGTAL())) {
            // Wenn keine DTAG Versorgung -> kein HVT, kein KVZ
            return;
        }
        List<GeoId2TechLocationView> tlViews = availabilityServiceHelper.findGeoId2TechLocationViews(building.getId());
        processTechLocationChange(building, HVTStandort.HVT_STANDORT_TYP_HVT, tlViews, sessionId, warnings);
        processTechLocationChange(building, HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ, tlViews, sessionId, warnings);
    }

    void processGeoIdReplacement(GeoId newGeoId, GeoId oldGeoId)
            throws FindException, StoreException {
        List<GeoId2TechLocation> replacedByGeoIdMappings = availabilityServiceHelper.findGeoId2TechLocations(newGeoId
                .getId());
        List<GeoId2TechLocation> geoIdMappings = availabilityServiceHelper.findGeoId2TechLocations(oldGeoId.getId());

        // Endstellen auf neue Id umziehen
        endstellenService.replaceGeoIdsOfEnstellen(oldGeoId.getId(), newGeoId.getId());

        // Wohungen auf neue Id umziehen
        getGewofagService().replaceGeoIdsOfGewofag(oldGeoId, newGeoId);
        // Klärfälle zur alten GeoId löschen
        availabilityServiceHelper.deleteGeoIdClarification4GeoId(oldGeoId.getId(), null);

        // Technische Standorte übernehmen oder zusammenführen
        LOGGER.info(geoIdMappings.size() + " Techlocations für alte GeoId " + oldGeoId.getId() + " gefunden");
        LOGGER.info(replacedByGeoIdMappings.size() + " Techlocations für neue GeoId " + newGeoId.getId() + " gefunden");
        for (GeoId2TechLocation techLocationSource : geoIdMappings) {
            GeoId2TechLocation techLocationDest = null;
            for (GeoId2TechLocation techLocation : replacedByGeoIdMappings) {
                if (NumberTools.equal(techLocationSource.getHvtIdStandort(), techLocation.getHvtIdStandort())) {
                    techLocationDest = techLocation;
                    break;
                }
            }
            if (techLocationDest != null) {
                // Zusammenführung
                LOGGER.info(String.format(
                        "Führe Daten des technischen Standortes %s von Geo ID %s nach %s zusammen.",
                        techLocationSource.getHvtIdStandort(), techLocationSource.getGeoId(), techLocationDest
                                .getGeoId()
                ));
                techLocationSource.merge(techLocationDest, "VENTO_MERGE");
                availabilityServiceHelper.store(techLocationDest);
                availabilityServiceHelper.deleteById(techLocationSource.getId(), GeoId2TechLocation.class);
            }
            else {
                // Übernahme
                LOGGER.info(String.format("Technischer Standort %s von Geo ID %s nach %s übernommen.",
                        techLocationSource.getHvtIdStandort(), oldGeoId.getId(), newGeoId.getId()));
                techLocationSource.setGeoId(newGeoId.getId());
                availabilityServiceHelper.store(techLocationSource);
            }
        }
    }

    private GewofagService getGewofagService() {
        if (gewofagService == null) {
            gewofagService = serviceLocator.getBean(GewofagService.class.getName(), GewofagService.class);
        }
        return gewofagService;
    }

    private void processTechLocationChange(GeoId building, Long standortTypRefId,
            List<GeoId2TechLocationView> techLocationViews, Long sessionId, AKWarnings warnings)
            throws FindException, StoreException {
        HVTStandort hvtStandort = findHVTStandortByLocation(building, standortTypRefId,
                sessionId, warnings, GeoIdClarification.Type.UPDATE_GEO_ID);

        GeoId2TechLocationView techLocationView = null;
        for (GeoId2TechLocationView techLocView : techLocationViews) {
            // nimm den ersten Datensatz ODER den fuer den oben gefundenen Standort
            if (techLocView.getStandortTypRefId().equals(standortTypRefId)
                    && (techLocationView == null
                    || hvtStandort != null && techLocView.getHvtIdStandort().equals(hvtStandort.getHvtIdStandort()))) {
                    techLocationView = techLocView;
            }
        }
        if (hvtStandort == null) {
            if (techLocationView != null) {
                if (hasActiveOrder(building)) {
                    createClarification(
                            sessionId,
                            warnings,
                            building.getId(),
                            GeoIdClarification.Type.ONKZ_ASB,
                            String.format("Abweichende Hurrican ONKZ/ASB/KVZ (%s/ %s/ %s) und " +
                                            "Vento ONKZ/ASB/KVZ (%s/ %s/ %s) mit mind. einem aktiven Auftrag",
                                    techLocationView.getOnkz(), techLocationView.getAsb(),
                                    techLocationView.getKvzNumber(),
                                    building.getOnkz(), building.getAsb(), building.getKvz()
                            )
                    );
                }
                else {
                    for (GeoId2TechLocationView techLocView : techLocationViews) {
                        if (techLocView.getStandortTypRefId().equals(standortTypRefId)) {
                            availabilityServiceHelper.deleteById(techLocView.getId(), GeoId2TechLocation.class);
                        }
                    }
                }
            }
            return;
        }

        GeoId2TechLocation techLocation = null;
        if (techLocationView != null) {
            // KVZ Nummer fuer HVT Standorte ignorieren
            boolean isKvzNrEqual = HVTStandort.HVT_STANDORT_TYP_HVT.equals(standortTypRefId) || StringUtils
                    .equals(building.getKvz(), techLocationView.getKvzNumber());
            if (hvtStandort.getHvtIdStandort().equals(techLocationView.getHvtIdStandort()) && isKvzNrEqual) {
                // Vergleich auf StandortId/kvzNummer, weil hvtStandort über ONKZ/ASB/KVZ der Location ermittelt wurden
                return;
            }
            if (hasActiveOrder(building)) {
                createClarification(sessionId, warnings, building.getId(), GeoIdClarification.Type.ONKZ_ASB,
                        String.format("Abweichende Hurrican ONKZ/ASB/KVZ (%s/ %s/ %s) und " +
                                        "Vento ONKZ/ASB/KVZ (%s/ %s/ %s) mit mind. einem aktiven Auftrag",
                                techLocationView.getOnkz(), techLocationView.getAsb(), techLocationView.getKvzNumber(),
                                building.getOnkz(), building.getAsb(), building.getKvz()
                        )
                );
                return;
            }
            // kein aktiver Auftrag, HVT umschreiben
            techLocation = availabilityServiceHelper.findById(techLocationView.getId(), GeoId2TechLocation.class);
        }
        checkAndWriteTechLocationMapping(building, hvtStandort, techLocation, sessionId);
    }

    /**
     * Ueberprueft und erstellt oder modifiziert die Zuordnung der GeoID zum HVT.
     */
    private void checkAndWriteTechLocationMapping(GeoId building, HVTStandort hvtStandort,
            @Nullable GeoId2TechLocation geoId2TechLocation, Long sessionId)
            throws StoreException {
        if (geoId2TechLocation == null) {
            geoId2TechLocation = new GeoId2TechLocation();
        }
        geoId2TechLocation.setGeoId(building.getId());
        geoId2TechLocation.setHvtIdStandort(hvtStandort.getHvtIdStandort());
        geoId2TechLocation.setKvzNumber(
                HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(hvtStandort.getStandortTypRefId()) ?
                        building.getKvz() : null
        );
        // nicht ueberschreiben, wenn die TAL-Laenge "gesichert" ueber die WITA gesetzt wurde
        if (HVTStandort.HVT_STANDORT_TYP_HVT.equals(hvtStandort.getStandortTypRefId())
                && !BooleanTools.nullToFalse(geoId2TechLocation.getTalLengthTrusted())) {
            geoId2TechLocation.setTalLength(NumberUtils.toLong(building.getDistance()));
        }
        geoId2TechLocation.setTalLengthTrusted(Boolean.FALSE);
        geoId2TechLocation.setMaxBandwidthAdsl(null);
        geoId2TechLocation.setMaxBandwidthSdsl(null);
        geoId2TechLocation.setMaxBandwidthVdsl(null);
        availabilityServiceHelper.saveGeoId2TechLocation(geoId2TechLocation, sessionId);
    }

    /**
     * Ermittelt zu einer Location den {@code HVTStandort}.
     */
    private HVTStandort findHVTStandortByLocation(GeoId building, Long standortTypRefId,
            Long sessionId, AKWarnings warnings, GeoIdClarification.Type type) throws FindException {
        if (StringUtils.isBlank(building.getAsb()) || StringUtils.isBlank(building.getOnkz())) {
            return null;
        }
        if (HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(standortTypRefId)
                && StringUtils.isBlank(building.getKvz())) {
            return null;
        }

        List<HVTStandort> standorte = hvtService.findHVTStandort4DtagAsb(building.getOnkz(), HVTStandort
                .getDTAGAsb(building.getAsb()), standortTypRefId);
        standorte = filterHvtStandorteIfNotActive(standorte);
        standorte = filterHvtStandorteIfUmzug(standorte, building.getKvz());
        HVTStandort hvtStandort = null;
        if (HVTStandort.HVT_STANDORT_TYP_HVT.equals(standortTypRefId)) {
            if (standorte.size() > 1) {
                createClarification(sessionId, warnings, building.getId(), type, String.format(
                        "Für ONKZ/ASB (%s/ %s) konnte kein eindeutiger HVT Standort ermittelt werden!",
                        building.getOnkz(), building.getAsb()));
                return null;
            }
            hvtStandort = Iterables.getFirst(standorte, null);
        }
        else if (HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(standortTypRefId)) {
            for (HVTStandort standort : standorte) {
                if (hvtService.findKvzAdresse(standort.getId(), building.getKvz()) != null) {
                    hvtStandort = standort;
                    break;
                }
            }
        }
        return hvtStandort;
    }

    /**
     * Diese Methode filtert alle Standorte heraus, die nicht in Betrieb oder nicht (mehr) aktiv sind.
     */
    List<HVTStandort> filterHvtStandorteIfNotActive(List<HVTStandort> standorte) {
        if (standorte != null && !standorte.isEmpty()) {
            return standorte.stream()
                    .filter(h -> h.isActive(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                            && HVTStandort.GESICHERT_IN_BETRIEB.equals(h.getGesicherteRealisierung()))
                    .collect(Collectors.toList());
        }
        return standorte;
    }

    /**
     * Diese Methode filtert bei zweideutigen Standorten, den Standort heraus, der gerade in einem Umzug steck oder
     * bereits umgezogen ist.
     */
    List<HVTStandort> filterHvtStandorteIfUmzug(List<HVTStandort> standorte, String kvzNr ) {
        if (standorte != null && standorte.size() > 1) {
            Set<Long> umzugIds = hvtUmzugService.findAffectedStandorte4Umzug();
            if (umzugIds != null && !umzugIds.isEmpty()) {
                for (HVTStandort standort : standorte) {
                    if (umzugIds.contains(standort.getId())) {
                        if (hvtUmzugService.findKvz4UmzugWithStatusUmgezogen(standort, kvzNr).isEmpty()) {
                            return standorte.stream()
                                    .filter(h -> umzugIds.contains(h.getId()))
                                    .collect(Collectors.toList());
                        }
                    }
                }
                return standorte.stream()
                        .filter(h -> !umzugIds.contains(h.getId()))
                        .collect(Collectors.toList());
            }
        }
        return standorte;
    }

    /**
     * Prüft, ob es zu einer Geo ID mindestens einen aktiven Auftrag gibt.
     */
    boolean hasActiveOrder(GeoId geoId) throws FindException {
        List<Endstelle> endstellen = endstellenService.findEndstellenByGeoId(geoId.getId());
        if (CollectionTools.isEmpty(endstellen)) {
            return false;
        }
        for (Endstelle endstelle : endstellen) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
            if (auftragDaten != null && auftragDaten.isAuftragActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Erstellt einen neuen Eintrag fuer die Klaerungsliste fuer die angegebene GeoID. <br>
     *
     * @param sessionId Session Id des Users
     * @param warnings  Methode fuegt eventuell Warnungen hinzu
     * @param geoId     die ID zu der der Klaerfall erzeugt werden soll. Wenn <code>null</code> kann wegen der
     *                  referenziellen Integritaet kein Eintrag in die DB erfolgen, stattdessen wird eine AKWarning
     *                  erzeugt
     * @param type      der Klaerfalltyp
     * @param info      die Klaerfallinfo
     */
    void createClarification(Long sessionId, AKWarnings warnings, Long geoId, GeoIdClarification.Type type, String info) {
        if (geoId == null) {
            // kann keine Clarification eintragen, da GeoId nicht gespeichert ist
            warnings.addAKWarning(this,
                    String.format("Klärelisteneintrag zu nicht gespeicherter GeoId type=%s, info=%s", type, info));
            return;
        }
        try {
            availabilityServiceHelper.createGeoIdClarification(sessionId, geoId, type, info);
        }
        catch (Exception e) {
            LOGGER.error("Nicht erwarteter Fehler bei Erstellung der Klärliste", e);
            if (warnings != null) {
                warnings.addAKWarning(this, "Nicht erwarteter Fehler bei Erstellung der Klärliste: " + e.getMessage());
            }
        }
    }

    private static class ObsoleteNotificationException extends Exception {
        private final GeoIdLocation entity;
        private final Location location;

        public ObsoleteNotificationException(GeoIdLocation entity, Location location) {
            super("Notification for " + entity.getClass().getName() + "(id=" + entity.getId()
                    + ") is obsolete: notification.modified=" + location.getModified() + "< entity.modified="
                    + entity.getModified());
            this.entity = entity;
            this.location = location;
        }

        private GeoIdLocation getEntity() {
            return entity;
        }
    }
}
