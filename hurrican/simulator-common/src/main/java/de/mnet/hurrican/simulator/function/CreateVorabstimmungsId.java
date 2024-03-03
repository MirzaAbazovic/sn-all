/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2015
 */
package de.mnet.hurrican.simulator.function;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.functions.Function;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.util.CollectionUtils;

/**
 * Citrus function creates new preAgreement id with required syntax for a carrier. <br><br> Moegliche Parameter: <ul>
 * <li>Index 0: Carrier-Code, z.B. DEU.DTAG (=Default)</li> <li>Index 1: V|T|S  -  zur Definition des Request-Typs in
 * der Vorabstimmungs-Id</li> </ul> (Default-Wert fuer den Request-Typ: V)
 *
 *
 */
public class CreateVorabstimmungsId implements Function {

    // VorabstimmungsId-Prefix, das von Atlas benötigt wird, um die Nachrichten an das richtige
    // System routen zu können (siehe WBAUF-01-46)
    private static final String VA_ROUTING_PREFIX_HURRICAN = "H";

    @Override
    public String execute(List<String> parameterList, TestContext context) {
        String carrierCode = "DEU.DTAG"; // default carrier
        if (!parameterList.isEmpty()) {
            carrierCode = parameterList.get(0);
        }

        String requestTyp = (!CollectionUtils.isEmpty(parameterList) && parameterList.size() > 1) ? parameterList.get(1) : "V";

        String idCode = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        return String.format("%s.%s%s%s", carrierCode, requestTyp, VA_ROUTING_PREFIX_HURRICAN, idCode);
    }

}
