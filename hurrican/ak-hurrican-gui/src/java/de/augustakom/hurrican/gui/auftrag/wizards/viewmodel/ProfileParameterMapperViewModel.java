/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards.viewmodel;

import java.util.*;
import java.util.function.*;

import de.augustakom.hurrican.model.cc.ProfileParameterMapper;

public class ProfileParameterMapperViewModel {
    private ProfileParameterMapper mapper;

    public ProfileParameterMapperViewModel(ProfileParameterMapper mapper) {
        this.mapper = mapper;
    }

    public String getGUIName() {
        return mapper.getParameterGuiName();
    }

    public String getGUIDescription() {
        return mapper.getParameterGuiDescription();
    }

    public String getName() {
        return mapper.getParameterName();
    }

    public static String findNameByGUIName(Collection<ProfileParameterMapperViewModel> mappers, String guiName) {
        Optional<ProfileParameterMapperViewModel> mapping = findMappingForMappingFunction(mappers, guiName,
                ProfileParameterMapperViewModel::getGUIName);
        if (mapping.isPresent())
            return mapping.get().getName();
        return null;
    }

    public static String findGUIDescriptionByName(Collection<ProfileParameterMapperViewModel> mappers, String name) {
        Optional<ProfileParameterMapperViewModel> mapping = findMappingForMappingFunction(mappers, name,
                ProfileParameterMapperViewModel::getName);
        String description;
        if (mapping.isPresent() && (description = mapping.get().getGUIDescription()) != null)
            return description;
        return null;
    }

    private static Optional<ProfileParameterMapperViewModel> findMappingForMappingFunction(
            Collection<ProfileParameterMapperViewModel> mappers, String name,
            Function<ProfileParameterMapperViewModel, String> mappingFunction) {
        return mappers.stream()
                .filter(mapping -> mappingFunction.apply(mapping) != null)
                .filter(mapping -> mappingFunction.apply(mapping).equals(name))
                .findFirst();
    }
}