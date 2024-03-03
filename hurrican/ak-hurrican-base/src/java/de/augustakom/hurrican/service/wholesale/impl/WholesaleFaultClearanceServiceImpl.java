/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:00:02
 */
package de.augustakom.hurrican.service.wholesale.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.model.wholesale.WholesaleAssignedVdslProfile;
import de.augustakom.hurrican.model.wholesale.WholesaleChangePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeVdslProfileRequest;
import de.augustakom.hurrican.model.wholesale.WholesalePort;
import de.augustakom.hurrican.model.wholesale.WholesaleVdslProfile;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode;
import de.augustakom.hurrican.service.wholesale.WholesaleFaultClearanceService;
import de.augustakom.hurrican.service.wholesale.WholesaleServiceException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 * Implementierung von {@link WholesaleFaultClearanceService}.
 */
@CcTxRequired
@ObjectsAreNonnullByDefault
public class WholesaleFaultClearanceServiceImpl extends AbstractWholesaleService implements
        WholesaleFaultClearanceService {

    private static final Logger LOG = Logger.getLogger(WholesaleFaultClearanceServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Resource(name = "de.augustakom.hurrican.service.cc.QueryCCService")
    private QueryCCService queryService;

    @Override
    public List<WholesaleChangeReason> getChangeReasons(RequestedChangeReasonType changeReasonType) {
        try {
            List<WholesaleChangeReason> result = new ArrayList<WholesaleChangeReason>();
            if (RequestedChangeReasonType.CHANGE_PORT.equals(changeReasonType)) {
                List<Reference> references = referenceService.findReferencesByType(
                        Reference.REF_TYPE_PORT_CHANGE_REASON_WS, Boolean.TRUE);
                for (Reference ref : references) {
                    WholesaleChangeReason changeReason = new WholesaleChangeReason();
                    changeReason.setChangeReasonId(ref.getId());
                    changeReason.setDescription(ref.getStrValue());
                    result.add(changeReason);
                }
            }
            else {
                DSLAMProfileChangeReason example = new DSLAMProfileChangeReason();
                example.setWholesale(Boolean.TRUE);
                List<DSLAMProfileChangeReason> dslamProfileChangeReasons = queryService.findByExample(example,
                        DSLAMProfileChangeReason.class);
                for (DSLAMProfileChangeReason dslamProfileChangeReason : dslamProfileChangeReasons) {
                    WholesaleChangeReason changeReason = new WholesaleChangeReason();
                    changeReason.setChangeReasonId(dslamProfileChangeReason.getId());
                    changeReason.setDescription(dslamProfileChangeReason.getName());
                    result.add(changeReason);
                }
            }

            return result;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_READING_CHANGE_REASONS, String.format(
                    "Error reading possible change reasons: %s", e.getMessage()), null, e);
        }
    }

    @Override
    public List<WholesalePort> getAvailablePorts(String lineId) {
        try {
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(lineId, LocalDate.now());
            Endstelle endstelle = findEndstelle(order.getFirst());

            RangierungQuery query = new RangierungQuery();
            query.setHvtStandortId(endstelle.getHvtIdStandort());
            query.setIncludeFreigabebereit(Boolean.TRUE);

            List<Rangierung> rangierungen = new ArrayList<Rangierung>();
            List<Produkt2PhysikTyp> p2pts = physikService.findP2PTs4Produkt(order.getSecond().getProdId(), null);
            if (CollectionTools.isNotEmpty(p2pts)) {
                for (Produkt2PhysikTyp p2pt : p2pts) {
                    query.setPhysikTypId(p2pt.getPhysikTypId());
                    List<Rangierung> resultForP2PT = rangierungsService.findFreieRangierungen(query, true, null, null);
                    rangierungen.addAll(resultForP2PT);
                }
            }

            List<RangierungsEquipmentView> rangierungsEquipmentViews = rangierungsService
                    .createRangierungsEquipmentView(rangierungen, null);

            List<WholesalePort> result = new ArrayList<WholesalePort>();
            for (RangierungsEquipmentView view : rangierungsEquipmentViews) {
                result.add(WholesalePort.createWholesalePort(view));
            }

            return result;
        }
        catch (WholesaleException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_READING_AVAILABLE_PORTS, String.format(
                    "Error reading available ports: %s", e.getMessage()), lineId, e);
        }
    }

    @Override
    public void changePort(WholesaleChangePortRequest changePortRequest) {
        try {
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(
                    changePortRequest.getLineId(), LocalDate.now());

            // Bemerkung: Aktuell erfolgt keine Prüfung bzw. Abgleich mit einem für die Zukunft anstehende
            // modifyPort. Je nach Konstellation könnten sich unterschiedl. Folgen ergeben (z.B. das cancelModify
            // nicht mehr sauber durchläuft).
            // Evtl. muss für bei den Wholsale Tests auftretende Fälle eine Sonderbehandlung eingebaut werden

            Endstelle endstelle = findEndstelle(order.getFirst());
            Rangierung rangierungToChange = rangierungsService.findRangierung(endstelle.getRangierId());
            Rangierung rangierungToUse = rangierungsService.findRangierung(changePortRequest.getNewPortId());

            checkIfChangePortIsPossible(changePortRequest.getNewPortId(), endstelle, rangierungToUse);

            // Port-Wechsel und Neuberechnung der VLANs durchfuehren
            rangierungsService.changeEqOut(
                    referenceService.findReference(changePortRequest.getChangeReasonId()),
                    endstelle,
                    rangierungToChange,
                    null, // rangierungAddToChange
                    rangierungToUse,
                    null, // rangierungAddToUse
                    null); // sessionId
        }
        catch (WholesaleException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_CHANGING_PORT, String.format(
                    "Error changing port: %s", e.getMessage()), null, e);
        }
    }

    void checkIfChangePortIsPossible(Long newPortId, Endstelle endstelle,
            Rangierung rangierungToUse) {
        if (rangierungToUse == null) {
            throw new WholesaleServiceException(
                    WholesaleFehlerCode.ERROR_CHANGING_PORT,
                    String.format(
                            "Port-Change not possible because defined port does not exist! (Port Id %s)",
                            newPortId), null, null
            );
        }
        else if (!DateTools.isHurricanEndDate(rangierungToUse.getGueltigBis())) {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_CHANGING_PORT, String.format(
                    "Port-Change not possible because defined port (Rangierung) is historized! (Port Id %s)",
                    newPortId), null, null);
        }
        else if (rangierungToUse.isAssignedToOrder()) {
            throw new WholesaleServiceException(
                    WholesaleFehlerCode.ERROR_CHANGING_PORT,
                    String.format(
                            "Port-Change not possible because port is not defined or selected port is already assigned to another order! (Port Id %s)",
                            newPortId), null, null
            );
        }

        if (NumberTools.notEqual(endstelle.getHvtIdStandort(), rangierungToUse.getHvtIdStandort())) {
            throw new WholesaleServiceException(
                    WholesaleFehlerCode.ERROR_CHANGING_PORT,
                    "The selected port is at another location than the order! Port change not possible.",
                    null, null);
        }
    }

    @Override
    public List<WholesaleAssignedVdslProfile> getAssignedVdslProfiles(String lineId) {
        try {
            LocalDate validDate = LocalDate.now();
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(lineId, validDate);
            List<Auftrag2DSLAMProfile> auftrag2Profiles = dslamService.findAuftrag2DSLAMProfiles(order.getFirst()
                    .getAuftragId());
            Map<Long, String> profileNameMap = CollectionMapConverter.convert2Map(
                    dslamService.findDSLAMProfiles4Auftrag(order.getFirst().getAuftragId()), "getId", "getName");
            Map<Long, String> reasonMap = CollectionMapConverter.convert2Map(
                    getChangeReasons(RequestedChangeReasonType.CHANGE_VDSL_PROFILE), "getChangeReasonId",
                    "getDescription");

            List<WholesaleAssignedVdslProfile> assignedProfiles = new ArrayList<WholesaleAssignedVdslProfile>();
            for (Auftrag2DSLAMProfile auftrag2dslamProfile : auftrag2Profiles) {
                WholesaleAssignedVdslProfile aProfile = new WholesaleAssignedVdslProfile();
                aProfile.setAssignedBy(auftrag2dslamProfile.getUserW());
                aProfile.setChangeReason(reasonMap.get(auftrag2dslamProfile.getChangeReasonId()));
                aProfile.setComment(auftrag2dslamProfile.getBemerkung());
                aProfile.setProfileName(profileNameMap.get(auftrag2dslamProfile.getDslamProfileId()));
                aProfile.setValidFrom(Instant.ofEpochMilli(auftrag2dslamProfile.getGueltigVon().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
                aProfile.setValidTo(Instant.ofEpochMilli(auftrag2dslamProfile.getGueltigBis().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
                assignedProfiles.add(aProfile);
            }

            return assignedProfiles;
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_READING_VDSL_PROFILES, String.format(
                    "Error reading vdsl Profiles: %s", e.getMessage()), lineId, e);
        }
    }

    @Override
    public List<WholesaleVdslProfile> getPossibleVdslProfiles(String lineId) {
        try {
            LocalDate validDate = LocalDate.now();
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(lineId, validDate);

            final TechLeistung downStreamTechnLeistung = ccLeistungsService.findTechLeistung4Auftrag(order.getFirst()
                    .getAuftragId(), TechLeistung.TYP_DOWNSTREAM, Date.from(validDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            final TechLeistung upStreamTechnLeistung = ccLeistungsService.findTechLeistung4Auftrag(order.getFirst()
                    .getAuftragId(), TechLeistung.TYP_UPSTREAM, Date.from(validDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            List<DSLAMProfile> profiles = dslamService.findValidDSLAMProfiles4Auftrag(order.getFirst().getAuftragId());
            Iterable<DSLAMProfile> filteredProfiles = Iterables.filter(profiles, new Predicate<DSLAMProfile>() {
                @Override
                public boolean apply(DSLAMProfile input) {
                    return NumberTools.isLessOrEqual(Long.valueOf(input.getBandwidth().getDownstream()),
                            downStreamTechnLeistung.getLongValue())
                            && NumberTools.isLessOrEqual(Long.valueOf(input.getBandwidth().getUpstream()),
                            upStreamTechnLeistung.getLongValue());
                }
            });
            List<WholesaleVdslProfile> vdslProfiles = new ArrayList<WholesaleVdslProfile>();
            for (DSLAMProfile dslamProfile : filteredProfiles) {
                vdslProfiles.add(new WholesaleVdslProfile(dslamProfile));
            }
            return vdslProfiles;
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_READING_VDSL_PROFILES, String.format(
                    "Error reading vdsl Profiles: %s", e.getMessage()), lineId, e);
        }
    }

    @Override
    public void changeVdslProfile(WholesaleChangeVdslProfileRequest request) {
        try {
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(request.getLineId(),
                    request.getValidFrom());

            if (request.getValidFrom().isBefore(LocalDate.now().atStartOfDay().toLocalDate())) {
                throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_CHANGING_VDSL_PROFILES, String.format(
                        "Error changing vdsl Profile, validFrom must not be in the past."), request.getLineId(), null);
            }


            boolean toChangeProfileIsPossible = false;
            for (WholesaleVdslProfile possibleProfile : getPossibleVdslProfiles(request.getLineId())) {
                if (possibleProfile.getId().equals(request.getNewProfileId())) {
                    toChangeProfileIsPossible = true;
                    break;
                }
            }
            if (!toChangeProfileIsPossible) {
                throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_CHANGING_VDSL_PROFILES, String.format(
                        "Error changing vdsl Profile, Profile not supported"), request.getLineId(), null);
            }

            dslamService.changeDSLAMProfile(order.getFirst().getAuftragId(), request.getNewProfileId(),
                    Date.from(request.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    request.getUsername(), request.getChangeReasonId(), request.getComment()
            );
        }
        catch (StoreException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_CHANGING_VDSL_PROFILES, String.format(
                    "Error changing vdsl Profile: %s", e.getMessage()), request.getLineId(), e);
        }
    }
}
