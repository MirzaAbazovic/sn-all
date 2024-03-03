/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.service.exceptions.helper;

import java.net.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.ComponentBuilder;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 *
 */
public class AtlasErrorMessageHelper {
    public static final String[] FILTER_OUT_JMS_HEADERS = new String[] { "JMS(.*)", "breadcrumbId" };
    private static final Logger LOGGER = LoggerFactory.getLogger(AtlasErrorMessageHelper.class);

    /**
     * Searches through the list of {@code strings}, looking for a string matching the supplied {@code value}.
     *
     * @param strings the list to search through. Can be a regexp or null
     * @param value   the value to search for
     * @return true if value found in string list
     */
    public static boolean isValueInList(String[] strings, String value) {
        if (strings != null && value != null) {
            for (String string : strings) {
                if (value.matches(string)) {
                    //                if(value.equals(string)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Extracts the CDM Service Name from the Soap Action. The CDM SoapActon typically has the following format:
     * "/ServiceName/OperationName"
     *
     * @param soapAction
     * @return
     */
    public static String extractCdmServiceNameFromSoapAction(String soapAction) {
        return getTokenAtIndex(soapAction, "/", 0);
    }

    /**
     * Extracts the CDM Operation Name from the Soap Action. The CDM SoapActon typically has the following format:
     * "/ServiceName/OperationName"
     *
     * @param soapAction
     * @return
     */
    public static String extractCdmOperationNameFromSoapAction(String soapAction) {
        return getTokenAtIndex(soapAction, "/", 1);
    }

    private static String getTokenAtIndex(String str, String delim, int index) {
        str = StringUtils.replace(str, "\"", "");
        if (str == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(str, delim);
        if (index > st.countTokens() - 1) {
            return null;
        }

        for (int i = 0; st.hasMoreTokens(); i++) {
            String token = st.nextToken();
            if (i == index) {
                return token;
            }
        }
        return null;
    }

    public static String getServerHostname() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            LOGGER.error("Error extracting hostname", e);
        }
        return "unknown";
    }

    public static HandleError.Component buildComponent(String soapAction, String processName) {
        return new ComponentBuilder()
                .withHost(getServerHostname())
                .withName(AtlasEsbConstants.COMPONENT_NAME)
                .withProcessName(processName)
                .withOperation(extractCdmOperationNameFromSoapAction(soapAction))
                .withService(extractCdmServiceNameFromSoapAction(soapAction))
                .withProcessId(Thread.currentThread().getName())
                .build();
    }
}
