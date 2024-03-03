/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.14
 */
package de.mnet.wbci.model.builder;

import org.apache.commons.lang.RandomStringUtils;

import de.mnet.wbci.model.CarrierCode;

/**
 * Test helper for generating random va, aenderung and storno ids
 */
public class IdGenerator {

    public static String generateVaId(CarrierCode carrierCode) {
        return generateId(carrierCode, 'V');
    }

    public static String generateTvId(CarrierCode carrierCode) {
        return generateId(carrierCode, 'T');
    }

    public static String generateStornoId(CarrierCode carrierCode) {
        return generateId(carrierCode, 'S');
    }

    private static String generateId(CarrierCode carrierCode, char requestType) {
        String carrierCodeStr = carrierCode.getITUCarrierCode();
        String hurricanIdentifier = "";
        if (CarrierCode.MNET.equals(carrierCode)) {
            hurricanIdentifier = "H";
        }
        String idCode = String.format("%s%s%s", requestType, hurricanIdentifier, RandomStringUtils.randomAlphanumeric(9)).toUpperCase().substring(0, 10);
        return String.format("%s.%s", carrierCodeStr, idCode);
    }
}
