/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:08:19
 */
package de.augustakom.hurrican.service.cc.impl;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DefaultDeleteDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AvailabilityDAO;
import de.augustakom.hurrican.dao.cc.GeoIdDAO;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationView;
import de.augustakom.hurrican.model.cc.GeoIdCarrierAddress;
import de.augustakom.hurrican.model.cc.GeoIdClarification;
import de.augustakom.hurrican.model.cc.GeoIdLocation;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.query.GeoIdClarificationQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdQuery;
import de.augustakom.hurrican.model.cc.query.GeoIdSearchQuery;
import de.augustakom.hurrican.model.cc.vento.availability.VentoAvailabilityInformationType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoConnectionType;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.augustakom.hurrican.model.cc.vento.availability.VentoTechnologyType;
import de.augustakom.hurrican.model.cc.view.GeoIdClarificationView;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.AvailabilityServiceHelper;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.impl.command.geoid.CommonImportGeoIdFromExcelCommand;
import de.augustakom.hurrican.service.cc.impl.command.geoid.ImportGeoIdKVZExcelCommand;
import de.augustakom.hurrican.service.cc.impl.command.geoid.ImportGeoIdVDSLExcelCommand;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Service-Implementierung von {@link AvailabilityService}
 */
@CcTxRequired
public class AvailabilityServiceImpl extends DefaultCCService implements AvailabilityService {

    private static final Logger LOGGER = Logger.getLogger(AvailabilityServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityServiceHelper")
    protected AvailabilityServiceHelper availabilityServiceHelper;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Resource(name = "defaultDeleteDAO")
    private Hibernate4DefaultDeleteDAO defaultDeleteDAO;

    @Autowired
    private GeoIdDAO geoIdDao;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ServiceLocator serviceLocator;

    private AvailabilityDAO getAvailabilityDao() {
        return (AvailabilityDAO) getDAO();
    }

    @Override
    public List<GeoId> findGeoIdsByQuery(GeoIdQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return getAvailabilityDao().findByQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<GeoId> findGeoIdsBySearchQuery(GeoIdSearchQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        if ((StringUtils.isNotBlank(query.getOnkz()) && (query.getAsb() == null))
                || ((query.getAsb() != null) && StringUtils.isBlank(query.getOnkz()))) {
            throw new FindException(
                    "Angegebene Such-Parameter sind nicht gueltig. ONKZ / ASB muessen entweder beide gesetzt oder beide leer sein!");
        }

        try {
            return getAvailabilityDao().findBySearchQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public GeoId saveGeoId(GeoId toSave) throws StoreException {
        try {
            return geoIdDao.save(toSave);
        }
        catch (Exception e) {
            throw new StoreException("save failed", e);
        }
    }

    @Override
    public void deleteGeoId2TechLocationView(GeoId2TechLocationView toDelete) throws DeleteException {
        if (toDelete == null) {
            return;
        }
        try {
            GeoId2TechLocation toDeleteLocation = findGeoId2TechLocation(toDelete.getGeoId(),
                    toDelete.getHvtIdStandort());
            if (toDeleteLocation == null) {
                throw new DeleteException(String.format("Der Standort mit der Geo ID %s und"
                                + " der HVT Standort ID %s konnte nicht ermittelt werden!",
                        toDelete.getGeoId(), toDelete.getHvtIdStandort()
                ));
            }
            defaultDeleteDAO.delete(toDeleteLocation);
        }
        catch (DeleteException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public VentoGetAvailabilityInformationResponse getAvailabilityInformation(
            VentoGetAvailabilityInformationRequest request) throws FindException {
        VentoGetAvailabilityInformationResponse response = new VentoGetAvailabilityInformationResponse();
        response.getAvailabilityInformationTypes().addAll(getAvailabilityInformationTypes(request.getGeoId()));
        return response;
    }

    List<VentoAvailabilityInformationType> getAvailabilityInformationTypes(Long geoId) throws FindException {
        try {
            List<VentoAvailabilityInformationType> availabilityInformationTypes = new ArrayList<>();

            if (findGeoId(geoId) == null) {
                return availabilityInformationTypes;
            }

            List<GeoId2TechLocation> geoId2TechLocations = findGeoId2TechLocations(geoId);
            if (CollectionTools.isNotEmpty(geoId2TechLocations)) {
                for (GeoId2TechLocation geoId2TechLocation : geoId2TechLocations) {
                    // technical params
                    Long hvtStandortId = geoId2TechLocation.getHvtIdStandort();
                    HVTStandort hvtStandort = getHvtService().findHVTStandort(hvtStandortId);
                    List<HVTStandortTechType> techTypes = getHvtService().findTechTypes4HVTStandort(hvtStandortId);
                    availabilityInformationTypes.addAll(createAvailabilityInfoTypes(geoId2TechLocation, hvtStandort,
                            techTypes));

                    // wenn VDSL in der Liste nicht vorhanden, dann Geo2Techlocation überprüfen und ggf. VDSL über HVT
                    // für GeoID hinzufügen
                    if (!isVdslAvailable(techTypes) && (geoId2TechLocation.getVdslAnHvtAvailableSince() != null)) {
                        HVTStandortTechType vdslTechType = new HVTStandortTechType();

                        vdslTechType.setAvailableFrom(geoId2TechLocation.getVdslAnHvtAvailableSince().getTime());
                        vdslTechType.setAvailableTo(DateTools.getHurricanEndDate());

                        Long distance = geoId2TechLocation.getTalLength();
                        Boolean distanceApproved = Boolean.FALSE;
                        if ((distance != null) && BooleanTools.nullToFalse(geoId2TechLocation.getTalLengthTrusted())) {
                            distanceApproved = Boolean.TRUE;
                        }
                        Long maxBandwidth = getBandwidthForTechnology(VentoTechnologyType.VDSL2, geoId2TechLocation);

                        VentoAvailabilityInformationType ventoAvailabilityInformationType = new VentoAvailabilityInformationType();
                        ventoAvailabilityInformationType.setTechnology(VentoTechnologyType.VDSL2);
                        ventoAvailabilityInformationType.setConnection(getConnectionTypeForHVTStandort(hvtStandort));
                        ventoAvailabilityInformationType
                                .setDistanceInMeters(distance != null ? distance.intValue() : 0);
                        ventoAvailabilityInformationType.setDistanceApproved(distanceApproved);
                        ventoAvailabilityInformationType
                                .setMaxDownstreamBandwidthInKB(maxBandwidth != null ? maxBandwidth.intValue() : 0);
                        ventoAvailabilityInformationType.setStart(DateConverterUtils.asLocalDate(vdslTechType.getAvailableFrom()));
                        ventoAvailabilityInformationType.setTermination(DateConverterUtils.asLocalDate(vdslTechType.getAvailableTo()));
                        availabilityInformationTypes.add(ventoAvailabilityInformationType);
                    }
                }
            }

            return availabilityInformationTypes;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e);
        }
    }

    private boolean isVdslAvailable(List<HVTStandortTechType> techTypes) {
        if (CollectionTools.isNotEmpty(techTypes)) {
            for (HVTStandortTechType hvtStandortTechType : techTypes) {
                if (hvtStandortTechType.getTechnologyTypeName().equals("VDSL")) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<VentoAvailabilityInformationType> createAvailabilityInfoTypes(GeoId2TechLocation geoId2TechLocation,
            HVTStandort hvtStandort, List<HVTStandortTechType> techTypes) {
        List<VentoAvailabilityInformationType> availabilityInformationTypes = new ArrayList<>();
        if (CollectionTools.isNotEmpty(techTypes)) {
            availabilityInformationTypes.addAll(techTypes.stream().map(hvtStandortTechType ->
                    createAvailabilityInfoType(geoId2TechLocation, hvtStandort, hvtStandortTechType))
                    .collect(Collectors.toList()));
        }
        return availabilityInformationTypes;
    }

    private VentoAvailabilityInformationType createAvailabilityInfoType(GeoId2TechLocation geoId2TechLocation,
            HVTStandort hvtStandort,
            HVTStandortTechType hvtStandortTechType) {
        VentoAvailabilityInformationType ventoAvailabilityInformationType = new VentoAvailabilityInformationType();

        VentoTechnologyType technologyType = getTechnologyTypeForTechType(hvtStandortTechType);
        Long distance = geoId2TechLocation.getTalLength();
        Boolean distanceApproved = Boolean.FALSE;
        if ((distance != null) && BooleanTools.nullToFalse(geoId2TechLocation.getTalLengthTrusted())) {
            distanceApproved = Boolean.TRUE;
        }
        Long maxBandwidth = getBandwidthForTechnology(technologyType, geoId2TechLocation);

        ventoAvailabilityInformationType.setTechnology(technologyType);
        ventoAvailabilityInformationType.setConnection(getConnectionTypeForHVTStandort(hvtStandort));
        ventoAvailabilityInformationType.setDistanceInMeters(distance != null ? distance.intValue() : 0);
        ventoAvailabilityInformationType.setDistanceApproved(distanceApproved);
        ventoAvailabilityInformationType.setMaxDownstreamBandwidthInKB(maxBandwidth != null ? maxBandwidth.intValue()
                : 0);
        ventoAvailabilityInformationType.setStart(Optional.ofNullable(hvtStandortTechType.getAvailableFrom()).orElse(new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        ventoAvailabilityInformationType.setTermination(Optional.ofNullable(hvtStandortTechType.getAvailableTo()).orElse(new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return ventoAvailabilityInformationType;
    }

    Long getBandwidthForTechnology(VentoTechnologyType technologyType, GeoId2TechLocation geoId2TechLocation) {
        if ((technologyType == VentoTechnologyType.ADSL) || (technologyType == VentoTechnologyType.ADSL_POTS)
                || (technologyType == VentoTechnologyType.ADSL_ISDN)
                || (technologyType == VentoTechnologyType.ADSL2PLUS)
                || (technologyType == VentoTechnologyType.ADSL2PLUS_POTS)
                || (technologyType == VentoTechnologyType.ADSL2PLUS_ISDN)) {
            return geoId2TechLocation.getMaxBandwidthAdsl();
        }
        else if (technologyType == VentoTechnologyType.VDSL2) {
            return geoId2TechLocation.getMaxBandwidthVdsl();
        }
        else if ((technologyType == VentoTechnologyType.SDSL) || (technologyType == VentoTechnologyType.SHDSL)) {
            return geoId2TechLocation.getMaxBandwidthSdsl();
        }
        return null;
    }

    VentoTechnologyType getTechnologyTypeForTechType(HVTStandortTechType techType) {
        String techTypeName = techType.getTechnologyTypeName();
        return VentoTechnologyType.fromValue(techTypeName);
    }

    VentoConnectionType getConnectionTypeForHVTStandort(HVTStandort hvtStandort) {
        if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTB)) {
            return VentoConnectionType.FTTB;
        }
        else if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTB_H)) {
            return VentoConnectionType.FTTB_H;
        }
        else if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTH)) {
            return VentoConnectionType.FTTH;
        }
        else if (hvtStandort.isFttc()) {
            return VentoConnectionType.FTTC;
        }
        else if (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_GEWOFAG)) {
            return VentoConnectionType.GEWOFAG;
        }
        else {
            // TODO Unterscheidung, ob direkt KVZ
            return VentoConnectionType.TAL_HVT;
        }
    }

    @Override
    public List<GeoIdClarification> findGeoIdClarificationsByStatus(List<GeoIdClarification.Status> states,
            List<Long> geoIds)
            throws FindException {
        if (CollectionTools.isEmpty(states)) {
            return Collections.emptyList();
        }
        try {
            return getAvailabilityDao().findGeoIdClarificationsByStatusId(states.stream()
                    .map(GeoIdClarification.Status::getRefId).collect(Collectors.toList()), geoIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByQuery(GeoIdClarificationQuery query)
            throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            if (((query.getFrom() == null) && (query.getTo() != null))
                    || ((query.getFrom() != null) && (query.getTo() == null))) {
                throw new FindException(
                        "Angegebene Such-Parameter sind nicht gueltig. Datum 'Von'/'Bis' muessen entweder beide "
                                + "gesetzt oder beide leer sein!"
                );
            }
            //noinspection ConstantConditions
            if ((query.getFrom() != null) && (query.getTo() != null)) {
                // From: Auf Mitternacht abschneiden
                Date from = DateUtils.truncate(query.getFrom(), Calendar.DAY_OF_MONTH);
                query.setFrom(from);
                // To: Auf Mitternacht des nächsten Tages abschneiden
                Date to = DateTools.changeDate(DateUtils.truncate(query.getTo(), Calendar.DAY_OF_MONTH),
                        Calendar.DAY_OF_MONTH, 1);
                query.setTo(to);
            }

            return getAvailabilityDao().findGeoIdClarificationViewsByQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public GeoIdClarification findGeoIdClarificationById(Long id) throws FindException {
        if (id == null) {
            return null;
        }
        try {
            AvailabilityDAO dao = (AvailabilityDAO) getDAO();
            return dao.findById(id, GeoIdClarification.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveGeoIdClarification(GeoIdClarification toSave, Long sessionId) throws StoreException {
        availabilityServiceHelper.saveGeoIdClarification(toSave, sessionId);
    }

    @Override
    public List<GeoIdClarificationView> findGeoIdClarificationViewsByStatus(List<GeoIdClarification.Status> states)
            throws FindException {
        if (CollectionTools.isEmpty(states)) {
            return Collections.emptyList();
        }
        try {
            return getAvailabilityDao().findGeoIdClarificationViewsByStatusId(states.stream()
                    .map(GeoIdClarification.Status::getRefId).collect(Collectors.toList()));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<GeoId2TechLocation> findPossibleGeoId2TechLocations(GeoId geoId, Long prodId) throws FindException {
        if ((geoId == null) || (prodId == null)) {
            return Collections.emptyList();
        }

        try {
            return getAvailabilityDao().findPossibleGeoId2TechLocations(geoId, prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<String, Object> importGeoIdsAnKVZ(String filename, Long sessionId) throws StoreException,
            FindException {
        return importViaCommand(filename, sessionId, ImportGeoIdKVZExcelCommand.class.getName());
    }

    @Override
    public Map<String, Object> importGeoIdsForVDSLAnHVT(String filename, Long sessionId) throws StoreException,
            FindException {
        return importViaCommand(filename, sessionId, ImportGeoIdVDSLExcelCommand.class.getName());
    }

    private Map<String, Object> importViaCommand(String filename, Long sessionId, String commandName)
            throws StoreException, FindException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(commandName);
            cmd.prepare(CommonImportGeoIdFromExcelCommand.KEY_IMPORT_FILENAME, filename);
            cmd.prepare(CommonImportGeoIdFromExcelCommand.KEY_SESSION_ID, sessionId);

            Object result = cmd.execute();
            if (result instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> retVal = (Map<String, Object>) result;
                return retVal;
            }
            return null;
        }
        catch (FindException | StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Der Strassenimport Service mit VDSL am HVT schlug fehl!", e);
        }
    }

    @Override
    public GeoId findGeoId(Long geoId) {
        return findOrCreateGeoId(geoId, null);
    }

    @Override
    public GeoId findOrCreateGeoId(Long geoId, Long sessionId) {
        return availabilityServiceHelper.findOrCreateGeoId(geoId, sessionId);
    }

    @Override
    public List<GeoId> mapLocationDataToGeoIds(String street, String houseNum, String houseNumExt, String zipCode,
            String city, String district) throws StoreException {
        return availabilityServiceHelper.findExact(street, houseNum, houseNumExt, zipCode, city, district);
    }

    @Override
    public AddressModel getDtagAddressForCb(Long geoIdNo, AddressModel apAddress) throws FindException {
        if (geoIdNo == null) {
            return apAddress;
        }
        else if (apAddress == null) {
            return null;
        }

        AddressModel dtagAddress;
        try {
            dtagAddress = (AddressModel) BeanUtils.cloneBean(apAddress);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        GeoId geoId = availabilityServiceHelper.findGeoId(geoIdNo);
        if ((geoId != null) && (geoId.getCarrierAddresses().get(GeoIdCarrierAddress.CARRIER_DTAG) != null)) {
            GeoIdCarrierAddress carrierAddress = geoId.getCarrierAddresses().get(GeoIdCarrierAddress.CARRIER_DTAG);
            dtagAddress.setOrt(carrierAddress.getCity());
            dtagAddress.setStrasse(carrierAddress.getStreet());
            dtagAddress.setNummer(carrierAddress.getHouseNum());
            dtagAddress.setHausnummerZusatz(carrierAddress.getHouseNumExtension());
            dtagAddress.setOrtsteil(carrierAddress.getDistrict());
            dtagAddress.setPlz(carrierAddress.getZipCode());
        }
        else if (geoId != null) {
            // wenn keine spezielle DTAG Adresse, dann aus GeoId lesen (wie früher bei TransformationService auch)
            dtagAddress.setOrt(geoId.getCity());
            dtagAddress.setStrasse(geoId.getStreet());
            dtagAddress.setNummer(geoId.getHouseNum());
            dtagAddress.setHausnummerZusatz(geoId.getHouseNumExtension());
            dtagAddress.setOrtsteil(geoId.getDistrict());
            dtagAddress.setPlz(geoId.getZipCode());

        }
        return dtagAddress;
    }

    @Override
    public Pair<Integer, String> findAsbAndOnKzForGeoId(Long geoId) throws FindException, ServiceNotFoundException {
        GeoId geoIdEntity = availabilityServiceHelper.findGeoId(geoId);
        //noinspection ConstantConditions
        return new Pair<>(Integer.valueOf(geoIdEntity.getAsb()), geoIdEntity.getOnkz());
    }

    @Override
    public String findStandortKuerzelForGeoId(Long geoId) throws FindException, ServiceNotFoundException {
        if (geoId == null) {
            return null;
        }
        GeoId geoIdEntity = availabilityServiceHelper.findGeoId(geoId);
        if (geoIdEntity == null) {
            throw new FindException("GeoId nicht gefunden");
        }
        return geoIdEntity.getTechnicalId();
    }

    @Override
    public <T extends GeoIdLocation> T save(final T location) {
        return geoIdDao.save(location);
    }

    @Override
    public GeoId2TechLocation saveGeoId2TechLocation(GeoId2TechLocation toSave, Long sessionId)
            throws StoreException {
        return availabilityServiceHelper.saveGeoId2TechLocation(toSave, sessionId);
    }

    @Override
    public List<GeoId2TechLocation> findGeoId2TechLocations(Long geoId) throws FindException {
        return availabilityServiceHelper.findGeoId2TechLocations(geoId);
    }

    @Override
    public List<GeoId2TechLocationView> findGeoId2TechLocationViews(Long geoId) throws FindException {
        return availabilityServiceHelper.findGeoId2TechLocationViews(geoId);
    }

    @Override
    public GeoId2TechLocation findGeoId2TechLocation(Long geoId, Long hvtIdStandort) throws FindException {
        return availabilityServiceHelper.findGeoId2TechLocation(geoId, hvtIdStandort);
    }


    @Override
    public void moveKvzLocationsToHvt(@Nonnull final KvzSperre kvzSperre, @Nonnull final Long currentHvtIdStandort,
            @Nonnull final Long futureHvtIdStandort, @Nonnull final Long sessionId)
            throws StoreException {

        try {
            GeoIdSearchQuery searchQuery = new GeoIdSearchQuery();
            searchQuery.setOnkz(kvzSperre.getOnkz());
            searchQuery.setAsb(kvzSperre.getAsb());
            searchQuery.setKvz(kvzSperre.getKvzNummer());

            List<GeoId> geoIds4Kvz = findGeoIdsBySearchQuery(searchQuery);
            for (GeoId geoId : geoIds4Kvz) {
                GeoId2TechLocation geoId2TechLocation = findGeoId2TechLocation(geoId.getId(), currentHvtIdStandort);
                if (geoId2TechLocation != null) {
                    if (findGeoId2TechLocation(geoId.getId(), futureHvtIdStandort) == null ){
                        geoId2TechLocation.setHvtIdStandort(futureHvtIdStandort);
                        saveGeoId2TechLocation(geoId2TechLocation, sessionId);
                    }
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(String.format(
                    "Fehler beim Umschreiben der GeoIDs auf den neuen Standort: %s", e.getMessage()), e);
        }
    }


    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    public HVTService getHvtService() {
        return this.hvtService;
    }

    /**
     * Injected
     */
    public Hibernate4DefaultDeleteDAO getDefaultDeleteDAO() {
        return defaultDeleteDAO;
    }

    public void setDefaultDeleteDAO(Hibernate4DefaultDeleteDAO defaultDeleteDAO) {
        this.defaultDeleteDAO = defaultDeleteDAO;
    }
}
