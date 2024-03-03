/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.converter;

import static de.mnet.wbci.helper.RufnummerHelper.*;

import de.augustakom.hurrican.model.billing.Rufnummer;

/**
 * Hilfsklasse fuer Operationen mit Rufnummern im Taifun-Format.
 */
public class RufnummerConverter {

    /**
     * Erstellt aus den angegebenen Billing Rufnummer eine String-Repraesentation im Format {@link
     * de.mnet.wbci.helper.RufnummerHelper#DN_FORMAT_SINGLE} oder {@link de.mnet.wbci.helper.RufnummerHelper#DN_FORMAT_BLOCK}.
     *
     * @param rufnummer
     * @return
     */
    public static String convertBillingDn(Rufnummer rufnummer) {
        if (rufnummer.isBlock()) {
            return String.format(DN_FORMAT_BLOCK, rufnummer.getOnKz(), rufnummer.getDnBase(), rufnummer.getDirectDial(),
                    rufnummer.getRangeFrom(), rufnummer.getRangeTo());
        }
        else {
            return String.format(DN_FORMAT_SINGLE, rufnummer.getOnKz(), rufnummer.getDnBase());
        }
    }

}
