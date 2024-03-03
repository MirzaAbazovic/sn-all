/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2016
 */
package de.augustakom.hurrican.service.cc.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.ProfileDAO;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.model.cc.*;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.common.tools.DateConverterUtils;
import org.apache.log4j.Logger;

import javax.annotation.CheckForNull;
import javax.inject.Inject;
import javax.validation.constraints.Size;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@CcTxRequired
@ObjectsAreNonnullByDefault
public class ProfileServiceImpl extends DefaultCCService implements ProfileService {
    private static final Logger LOGGER = Logger.getLogger(ProfileService.class);

    private ProfileDAO profileDAO;
    private TechLeistungDAO techLeistungDAO;

    public ProfileServiceImpl() {
        super();
    }

    @Inject
    public ProfileServiceImpl(ProfileDAO profileDAO, TechLeistungDAO techLeistungDAO) {
        super();
        this.profileDAO = profileDAO;
        this.techLeistungDAO = techLeistungDAO;
    }

    public Map<String, DSLAMProfileChangeReason> findAllChangeReasons() {
        return profileDAO.findAll(DSLAMProfileChangeReason.class).stream()
                .collect(Collectors.toMap(DSLAMProfileChangeReason::getName,
                        Function.identity()));
    }

    public List<ProfileAuftrag> findProfileAuftrags(Long auftragId) {
        return profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragId);
    }

    @CheckForNull
    public ProfileAuftrag findNewestProfileAuftrag(Long auftragId) {
        return findProfileAuftrags(auftragId).stream().max(Comparator.comparing(ProfileAuftrag::getGueltigBis)).orElse(null);
    }

    @Override
    @CheckForNull
    public ProfileAuftrag findProfileAuftragForDate(Long auftragId, Date datumGueltig) {
        List<ProfileAuftrag> alleProfilesZuAuftragsId = findProfileAuftrags(auftragId);
        List<ProfileAuftrag> duplicateCheck = new ArrayList<>();
        for (ProfileAuftrag pa : alleProfilesZuAuftragsId) {
            if (pa.getGueltigVon().compareTo(datumGueltig) <= 0 && pa.getGueltigBis().compareTo(datumGueltig) >= 0) {
                duplicateCheck.add(pa);
                if (duplicateCheck.size() > 1) {
                    throw new IllegalStateException("Mehrere gueltige AuftragProfile zum Datum "
                            + DateFormat.getDateTimeInstance().format(datumGueltig) + " gefunden.");
                }
            }
        }

        return duplicateCheck.isEmpty() ? null : duplicateCheck.get(0);
    }

    public Map<String, List<ProfileParameter>> findProfileParametersGroupByName(Long hvtStandortId, HWBaugruppe baugruppe) {
        return findAllPossibleProfileParameters(hvtStandortId, baugruppe).stream()
                .collect(Collectors.groupingBy(ProfileParameter::getParameterName));
    }

    private List<ProfileParameter> findAllPossibleProfileParameters(Long hvtStandortId, HWBaugruppe baugruppe) {
        final List<ProfileParameter> profileParameters = profileDAO.findProfileParameters(baugruppe.getHwBaugruppenTyp().getId());
        final String lineSpectrumFromStandort = findLineSpectrumFromStandort(hvtStandortId);
        final HWRack rack = getHWRack(baugruppe.getRackId());
        List<ProfileParameter> temp = modifyProfileDefaults(profileParameters,
                HVTStandort.HVT_STANDORT_LINE_SPECTRUM, lineSpectrumFromStandort);
        return temp;
    }

    private List<ProfileParameter> findDefaultProfileParameters(Long hvtStandortId, HWBaugruppe baugruppe) {
        final List<ProfileParameter> profileParameters = profileDAO.findProfileParameterDefaults(baugruppe.getHwBaugruppenTyp().getId());
        final String lineSpectrumFromStandort = findLineSpectrumFromStandort(hvtStandortId);
        final HWRack rack = getHWRack(baugruppe.getRackId());
        if (lineSpectrumFromStandort != null) {
            filterValue(profileParameters, HVTStandort.HVT_STANDORT_LINE_SPECTRUM, lineSpectrumFromStandort);
        }
        return profileParameters;
    }

    private void filterValue(List<ProfileParameter> profileParameters, String filter, String value) {
        final Optional<ProfileParameter> profileParameter = findProfileParameterByParamName(profileParameters, filter);
        profileParameter.ifPresent(pp -> {
            profileParameters.remove(pp);
            profileParameters.add(new ProfileParameter(pp.getBaugruppenTyp(), pp.getParameterName(), value,
                    TRUE));
        });
    }

    private List<ProfileParameter> modifyProfileDefaults(List<ProfileParameter> values, String paramName, String paramValue) {
        if (!Strings.isNullOrEmpty(paramName) && !Strings.isNullOrEmpty(paramValue)) {
            final List<ProfileParameter> changedValues = Lists.newArrayList(values);
            final Optional<ProfileParameter> valOpt = findProfileParameterByParamName(values, paramName);
            final ProfileParameter overwriteValue = findOverwriteValue(valOpt, paramName, paramValue);
            if (!overwriteValueExists(changedValues, overwriteValue))
                changedValues.add(overwriteValue);
            return changedValues;
        }
        return values;
    }

    private boolean overwriteValueExists(List<ProfileParameter> changedValues, ProfileParameter overwriteValue) {
        return changedValues.stream()
                .filter(val -> val.getParameterName().equals(overwriteValue.getParameterName()))
                .anyMatch(val -> val.getParameterValue().equals(overwriteValue.getParameterValue()));
    }

    private Optional<ProfileParameter> findProfileParameterByParamName(List<ProfileParameter> values, String paramName) {
        return values.stream().filter(p -> paramName.equals(p.getParameterName())).findAny();
    }

    private ProfileParameter findOverwriteValue(Optional<ProfileParameter> valOpt, String paramName, String paramValue) {
        final ProfileParameter changedVal;
        if (valOpt.isPresent()) {
            ProfileParameter value = valOpt.get();
            changedVal = overwriteProfileDefaultValue(value, paramValue);
        }
        else {
            changedVal = new ProfileParameter(null, paramName, paramValue, false);
        }
        return changedVal;
    }

    private ProfileParameter overwriteProfileDefaultValue(ProfileParameter value, String paramValue) {
        ProfileParameter changedVal = value.copy();
        changedVal.setParameterValue(paramValue);
        return changedVal;
    }

    public List<ProfileParameterMapper> findParameterMappers(@Size(min = 1) Set<ProfileAuftragValue> parameterNames) {
        List<ProfileParameterMapper> mappers = parameterNames.stream()
                .map(profileAuftragValue -> findParameterMapper(profileAuftragValue.getParameterName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        mappers.sort(Comparator.comparing(ProfileParameterMapper::getSortOrder));
        return mappers;
    }

    private ProfileParameterMapper findParameterMapper(@Size(min = 1) String parameterName) {
        return profileDAO.findParameterMapper(parameterName);
    }

    public List<String> findLineSpectrumValues() {
        return profileDAO.findParameterValuesByName(HVTStandort.HVT_STANDORT_LINE_SPECTRUM);
    }

    public String findLineSpectrumDefaultValue() {
        return profileDAO.findDefaultParameterValueByName(HVTStandort.HVT_STANDORT_LINE_SPECTRUM);
    }

    private String findLineSpectrumFromStandort(Long hvtStandortId) {
        final HVTStandort hvtStandortById = findHVTStandortById(hvtStandortId);
        return hvtStandortById.getGfastStartfrequenz();
    }

    public Either<String, ProfileAuftrag> persistProfileAuftrag(ProfileAuftrag profileAuftrag) {
        if (DateTools.isDateBefore(profileAuftrag.getGueltigVon(), new Date())) {
            final String validationMsg = String.format(
                    "Der 'gültig von' - Wert des Profil für Auftrag %s darf nicht in der Vergangenheit liegen (aktuell %s)",
                    profileAuftrag.getAuftragId(), DateConverterUtils.asLocalDate(profileAuftrag.getGueltigVon()).toString());
            return Either.left(validationMsg);
        }

        final List<ProfileAuftrag> previousProfiles = findProfileAuftrags(profileAuftrag.getAuftragId());
        previousProfiles
                .stream()
                .filter(pa -> DateTools.isDateAfter(pa.getGueltigBis(), profileAuftrag.getGueltigVon()))
                .forEach(pa -> pa.setGueltigBis(profileAuftrag.getGueltigVon()));

        return Either.right(profileDAO.store(profileAuftrag));
    }

    public ProfileAuftrag createNewProfile(Long auftragId, Long sessionId) {
        final Equipment equipmentByAuftragId = profileDAO.findEquipmentsByAuftragId(auftragId);
        if (equipmentByAuftragId != null) {
            final ProfileAuftrag pa = newProfileAuftrag(auftragId, equipmentByAuftragId, sessionId);
            pa.getProfileAuftragValues().addAll(newProfileAuftragValues(equipmentByAuftragId, auftragId));
            return pa;
        }
        else {
            throw new IllegalArgumentException(String.format("Equipment für tech. Auftrag [%d] nicht gefunden", auftragId));
        }
    }

    private ProfileAuftrag newProfileAuftrag(Long auftragId, Equipment equipment, Long sessionId) {
        final ProfileAuftrag pa = new ProfileAuftrag();
        pa.setAuftragId(auftragId);
        pa.setEquipmentId(equipment.getId());
        pa.setBemerkung("");
        pa.setGueltigVon(DateConverterUtils.asDate(LocalDate.now()));
        pa.setGueltigBis(DateTools.getHurricanEndDate());
        pa.setUserW(getLoginNameSilent(sessionId));
        return pa;
    }

    private List<ProfileAuftragValue> newProfileAuftragValues(Equipment equipment, Long auftragId) {
        if (equipment.getHwBaugruppenId() != null) {
            HWBaugruppe baugruppe = findBaugruppeByEquipment(equipment);
            final List<ProfileParameter> profileParameters = findDefaultProfileParameters(equipment.getHvtIdStandort(),
                    baugruppe);
            final List<ProfileParameter> withoutDefault = findParametersWithoutDefault(baugruppe.getHwBaugruppenTyp().getId(),
                    auftragId);
            profileParameters.addAll(withoutDefault);

            return profileParameters.stream()
                    .map(pd -> new ProfileAuftragValue(pd.getParameterName(), pd.getParameterValue()))
                    .collect(Collectors.toList());
        }
        else {
            LOGGER.warn(String.format("Create new profile auftrag values for equipment [%d] without HW Baugruppe",
                    equipment.getId()));
            return Collections.emptyList();
        }
    }

    private List<ProfileParameter> findParametersWithoutDefault(Long baugruppenTypId, Long auftragId) {
        List<ProfileParameter> allValuesWithoutDefault = profileDAO.findParametersWithoutDefault(baugruppenTypId);
        final TechLeistung downstreamTechLeistung = getFromTechLeistung(auftragId, TechLeistung.TYP_DOWNSTREAM);
        final TechLeistung upstreamTechLeistung = getFromTechLeistung(auftragId, TechLeistung.TYP_UPSTREAM);
        if (Strings.isNullOrEmpty(downstreamTechLeistung.getStrValue()) || Strings.isNullOrEmpty(upstreamTechLeistung.getStrValue()))
            throw new RuntimeException("Down-/Up-Stream Werte fuer Auftrags-ID " +
                    auftragId + " in technischen Leistungen nicht gefunden.");
        Optional<ProfileParameter> upStream = findUpstream(allValuesWithoutDefault, upstreamTechLeistung.getStrValue());
        Optional<ProfileParameter> downStream = findDownstream(allValuesWithoutDefault, downstreamTechLeistung.getStrValue());
        if (!upStream.isPresent() || !downStream.isPresent())
            throw new RuntimeException("Down-/Up-Stream Werte aus technischen Leistungen stimmen fuer Auftrags-ID " +
                    auftragId + " nicht mit Parameterwerten überein");
        return Arrays.asList(upStream.get(), downStream.get());
    }

    private TechLeistung getFromTechLeistung(Long auftragId, String techLeistungsTyp) {
        List<TechLeistung> downstreams = techLeistungDAO.findTechLeistungen4Auftrag(auftragId, techLeistungsTyp, true);
        if (downstreams.isEmpty())
            return new TechLeistung();
        return downstreams.get(0);
    }

    private Optional<ProfileParameter> findUpstream(List<ProfileParameter> parameters, String upstreamValue) {
        return findItemsContaining(parameters, ProfileParameter.TRAFFIC_TABLE_UPSTREAM, upstreamValue);
    }

    private Optional<ProfileParameter> findDownstream(List<ProfileParameter> parameters, String downstreamValue) {
        return findItemsContaining(parameters, ProfileParameter.TRAFFIC_TABLE_DOWNSTREAM, downstreamValue);
    }

    private Optional<ProfileParameter> findItemsContaining(List<ProfileParameter> parameters, String contains, String value) {
        final String postfix = "000";
        return parameters.stream()
                .filter(p -> p.getParameterName().contains(contains))
                .filter(p -> p.getParameterValue().concat(postfix).equals(value))
                .findFirst();
    }

    private HWRack getHWRack(Long rackId) {
        return profileDAO.findById(rackId, HWRack.class);
    }

    private HWBaugruppe findBaugruppeByEquipment(Equipment equipment) {
        return profileDAO.findById(equipment.getHwBaugruppenId(), HWBaugruppe.class);
    }

    private HVTStandort findHVTStandortById(Long hvtStandortId) {
        return profileDAO.findById(hvtStandortId, HVTStandort.class);
    }

    public DSLAMProfileChangeReason getChangeReasonById(Long changeReasonId) {
        return profileDAO.findById(changeReasonId, DSLAMProfileChangeReason.class);
    }

}
