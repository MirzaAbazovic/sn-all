/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2009 11:13:35
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.utils;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSDNData;


/**
 * Hilfsklasse, um die Angabe von Phone-Numbers zu modifizieren.
 *
 *
 */
public class CPSPhoneNumberModificator {

    /**
     * Ueberprueft, ob in der Liste <code>phoneNumbers</code> bereits eine Rufnummer mit gleicher ONKZ / DN_BASE
     * enthalten ist, wie in <code>phoneNumberToModify</code> angegeben ist. Falls dies der Fall ist, wird die
     * DIRECT_DIAL von <code>phoneNumberToModify</code> auf die DIRECT_DIAL der passenden Rufnummer verwendet. <br> Dies
     * ist notwendig, da der CPS bei gesplitteten Rufnummernbloecken immer die gleiche DIRECT_DIAL erwartet; in Taifun
     * ist als DIRECT_DIAL jedoch der Beginn des Blocks eingetragen. <br> Beispiel: <br> TAIFUN_DIRECT_DIAL |
     * TAIFUN_RANGR_FROM | TAIFUN_RANGE_TO | CPS_DIRECT_DIAL |  <br> 0                  | 00                | 49 | 0 |
     * <br> 5                  | 50                | 59              | 0               |  <br> 6 | 60                |
     * 99              | 0               |  <br> <br>
     *
     * @param phoneNumbers
     * @param phoneNumberToModify
     */
    public static void modifyDirectDial(List<AbstractCPSDNData> phoneNumbers, AbstractCPSDNData phoneNumberToModify) {
        if (CollectionTools.isNotEmpty(phoneNumbers)
                && (phoneNumberToModify != null)
                && StringUtils.isNotBlank(phoneNumberToModify.getDirectDial())) {
            for (AbstractCPSDNData dn : phoneNumbers) {
                boolean matchFound = false;
                if (StringUtils.equals(dn.getLac(), phoneNumberToModify.getLac()) &&
                        StringUtils.equals(dn.getDn(), phoneNumberToModify.getDn())) {
                    phoneNumberToModify.setDirectDial(dn.getDirectDial());
                    matchFound = true;
                }

                if (matchFound) {
                    break;
                }
            }
        }
    }

}


