/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 16:10:22
 */
package de.mnet.wita.activiti;


/**
 * Helper class for the activiti business key handling.
 */
public final class BusinessKeyUtils {

    public static final String KUEDT_SUFFIX = "-kuedt";

    private BusinessKeyUtils() {
        // should not be instantiated
    }

    public static String getKueDtBusinesskey(String businessKey) {
        return businessKey + KUEDT_SUFFIX;
    }
}


