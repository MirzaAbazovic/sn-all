/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2016
 */
package de.augustakom.hurrican.service.cc.utils;

import de.augustakom.hurrican.model.cc.ProfileAuftragValue;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;

import javax.annotation.CheckForNull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by marteleu on 13.12.2016.
 */
public class ProfileAuftragValueUtil {

    /** Hilfsmethode um die Werte eines AuftragProfils mit den CpsNamen zu mappen
     * @param profileValues
     * @param parameterMapperList
     * @param cpsName
     * @return
     */
    @CheckForNull
    public static String getValueForCpsName(@NotNull Set<ProfileAuftragValue> profileValues,
                                            @NotNull List<ProfileParameterMapper> parameterMapperList,
                                            @NotNull CpsName cpsName) {
        final String parameterName = getParameterNameFromCpsName(parameterMapperList, cpsName);
        if (parameterName != null) {
            return getValue(profileValues, parameterName);
        }
        return null;
    }

    /**
     * Sucht den Wert eines Parameter-Namens in einer ProfileAuftragValue-Liste
     *
     * @param profileValues
     * @param parameterName
     * @return Wert zu dem gesuchten Parameter-Namen
     */
    @CheckForNull
    private static String getValue(@NotNull Set<ProfileAuftragValue> profileValues, @NotNull String parameterName) {
        Optional<ProfileAuftragValue> result =
                profileValues.stream().filter(param -> param.getParameterName().equals(parameterName)).findFirst();
        if (result.isPresent()) {
            return result.get().getParameterValue();
        }
        return null;
    }

    @CheckForNull
    private static String getParameterNameFromCpsName(@NotNull List<ProfileParameterMapper> parameterMapperList,
                                                     @NotNull CpsName cpsName) {
        Optional<ProfileParameterMapper> result = parameterMapperList.stream().filter(param -> param.getParameterCpsName().equals(cpsName.getCpsName())).findFirst();
        if (result.isPresent()) {
            return  result.get().getParameterName();
        }
        return null;
    }

    public enum CpsName {
        UPSTREAM("DataRateUp"),
        DOWNSTREAM("DataRateDown"),
        UPBO("PowerBackOffUp"),
        DPBO("PowerBackOffDown"),
        RFI("RadioFrequencyInterference"),
        NOISE_MARGIN("NoiseMargin"),
        INP_DELAY("ImpulseNoiseProtectionDelay"),
        VIRTUAL_NOISE("VirtualNoise"),
        LINE_SPECTRUM("Spectrum"),
        INM("ImpulseNoiseMonitoring"),
        SOS("Sos"),
        TRAFFIC_TABLE_UPSTREAM("TrafficTableUp"),
        TRAFFIC_TABLE_DOWNSTREAM("TrafficTableDown");

        private String cpsName;

        CpsName(String pCpsName) {
            this.cpsName = pCpsName;
        }

        public String getCpsName() {
            return this.cpsName;
        }
    }
}
