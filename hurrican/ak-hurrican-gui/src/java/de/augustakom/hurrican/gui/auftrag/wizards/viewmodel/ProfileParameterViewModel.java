/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2016
 */
package de.augustakom.hurrican.gui.auftrag.wizards.viewmodel;

import java.util.*;

import de.augustakom.hurrican.model.cc.ProfileParameter;

public class ProfileParameterViewModel {

    private ProfileParameter profileParameter;

    public ProfileParameterViewModel(ProfileParameter profileParameter) {
        this.profileParameter = profileParameter;
    }

    public String getName() {
        return profileParameter.getParameterName();
    }

    public void setName(String parameterName) {
        profileParameter.setParameterName(parameterName);
    }

    public String getValue() {
        return profileParameter.getParameterValue();
    }

    public void setValue(String parameterValue) {
        profileParameter.setParameterValue(parameterValue);
    }

    public Boolean isDefault() {
        return profileParameter.getDefault();
    }

    public void setDefault(Boolean aDefault) {
        profileParameter.setDefault(aDefault);
    }

    public static Map<String, List<String>> groupProfileDefaultsByName(List<ProfileParameterViewModel> profileDefaults) {
        Map<String, List<String>> defaultNameValues = new HashMap<>();
        profileDefaults.forEach(def -> {
            if (defaultNameValues.containsKey(def.getName()))
                defaultNameValues.get(def.getName()).add(def.getValue());
            else {
                List<String> values = new ArrayList<>();
                values.add(def.getValue());
                defaultNameValues.put(def.getName(), values);
            }
        });
        return defaultNameValues;
    }
}