/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2014
 */
package de.mnet.wbci.acceptance.common.utils;

import java.util.*;
import com.google.common.collect.Collections2;

import de.mnet.wbci.model.CarrierCode;

/**
 *
 */
public class TestUtils {
    /**
     * diese Carrier benoetigen keine WBCI Schnittstelle und muessen aus den Tests ausgenommen werden, da es fuer sie
     * keinen Eintrag in T_WITA_CONFIG gibt
     */
    private static final List<CarrierCode> CARRIER_WITHOUT_WBCI = Arrays.asList(CarrierCode.MNET, CarrierCode.DEU_QSC);

    public static Object[][] convertCarrierCodesToTestParameterArray() {
        Collection<CarrierCode> carrierCodes = Arrays.asList(CarrierCode.values());
        carrierCodes = Collections2.filter(carrierCodes, input -> (!CARRIER_WITHOUT_WBCI.contains(input)));
        final Object[][] parameters = new Object[carrierCodes.size()][];
        int counter = 0;
        for (CarrierCode carrierCode : carrierCodes) {
            parameters[counter++] = new Object[] { carrierCode };
        }
        return parameters;
    }

}
