/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.14
 */
package de.mnet.wbci.model.helper;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Standort;

/**
 *
 */
public class StandortHelper {

    /**
     * Checks if the {@link Standort} attributes are set to the corresponding ADA-{@link
     * de.mnet.wbci.model.MeldungsCode}. For example: - ADAORT => Only the attribute 'Ort' should be set!
     *
     * @return true or false
     */
    public static boolean isStandortValidForMeldungscode(Standort standort, MeldungsCode meldungsCode) {
        switch (meldungsCode) {
            case ADAORT:
                return isStandortValidADAORT(standort);
            case ADAPLZ:
                return isStandortValidADAPLZ(standort);
            case ADASTR:
                return isStandortValidADASTR(standort);
            case ADAHSNR:
                return isStandortValidADAHSNR(standort);
            default:
                return true;
        }
    }

    private static boolean isStandortValidADAHSNR(Standort standort) {
        return standort != null
                && isEmpty(standort.getPostleitzahl())
                && isEmpty(standort.getOrt())
                && standort.getStrasse() != null
                && isHausnummerSet(standort);
    }

    private static boolean isStandortValidADASTR(Standort standort) {
        return standort != null
                && isEmpty(standort.getPostleitzahl())
                && isEmpty(standort.getOrt())
                && isStrassennameSet(standort);
    }

    private static boolean isStandortValidADAPLZ(Standort standort) {
        return standort != null
                && isNotEmpty(standort.getPostleitzahl())
                && isEmpty(standort.getOrt())
                && isStrasseEmpty(standort);
    }

    private static boolean isStandortValidADAORT(Standort standort) {
        return standort != null
                && isNotEmpty(standort.getOrt())
                && isEmpty(standort.getPostleitzahl())
                && isStrasseEmpty(standort);
    }

    private static boolean isStrasseEmpty(Standort standort) {
        return standort.getStrasse() == null;
    }

    private static boolean isHausnummerSet(Standort standort) {
        return standort.getStrasse() != null
                && isEmpty(standort.getStrasse().getStrassenname())
                && isNotEmpty(standort.getStrasse().getHausnummer())
                && isEmpty(standort.getStrasse().getHausnummernZusatz());
    }

    private static boolean isStrassennameSet(Standort standort) {
        return standort.getStrasse() != null
                && isNotEmpty(standort.getStrasse().getStrassenname())
                && isEmpty(standort.getStrasse().getHausnummer())
                && isEmpty(standort.getStrasse().getHausnummernZusatz());
    }

}
