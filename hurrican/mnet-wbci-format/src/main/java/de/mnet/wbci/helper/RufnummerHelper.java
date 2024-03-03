/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.14
 */
package de.mnet.wbci.helper;

import java.util.*;

import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;

/**
 * Hilfsklasse fuer Rufnummern-Operationen im WBCI Kontext.
 *
 *
 */
public class RufnummerHelper {

    private static final String DN_SEPARATOR = "/";
    public static final String DN_FORMAT_SINGLE = "%s" + DN_SEPARATOR + "%s";
    public static final String DN_FORMAT_BLOCK = "%s" + DN_SEPARATOR + "%s (%s) - %s bis %s";

    /**
     * Erstellt aus allen enthaltenen Einzelrufnummern einen String in der Form onkz/dn. Die Onkz erhaelt dabei immer
     * eine fuehrende '0'. Die Rufnummern werden dabei in natuerlicher Reihenfolge sortiert.
     *
     * @return
     */
    public static Set<String> convertWbciEinzelrufnummer(RufnummernportierungEinzeln rnEinzel) {
        List<String> result = new ArrayList<>();
        for (RufnummerOnkz rufnummerOnkz : rnEinzel.getRufnummernOnkz()) {
            result.add(String.format(DN_FORMAT_SINGLE, rufnummerOnkz.getOnkzWithLeadingZero(), rufnummerOnkz.getRufnummer()));
        }
        return new TreeSet<>(result);
    }

    /**
     * Erstellt aus allen enthaltenen Rufnummernbloecken einen String in der Form: onkz/durchwahlnummer (abfragestelle)
     * - (blockVon bis blockBis). Die Onkz erhaelt dabei immer eine fuehrende '0'. Die Rufnummern werden dabei in
     * natuerlicher Reihenfolge sortiert.
     *
     * @return
     */
    public static Set<String> convertWbciRufnummerAnlage(RufnummernportierungAnlage anlage) {
        List<String> result = new ArrayList<>();
        for (Rufnummernblock block : anlage.getRufnummernbloecke()) {
            result.add(String.format(DN_FORMAT_BLOCK,
                    anlage.getOnkzWithLeadingZero(), anlage.getDurchwahlnummer(), anlage.getAbfragestelle(),
                    block.getRnrBlockVon(), block.getRnrBlockBis()));
        }
        return new TreeSet<>(result);
    }

}
