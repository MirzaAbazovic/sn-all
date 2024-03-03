/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2014
 */
package de.mnet.wbci.model;

import java.util.*;

import de.augustakom.hurrican.service.elektra.builder.DialingNumberTypeBuilder;
import de.mnet.elektra.services.DialingNumberType;

/**
 *
 */
public class DialingNumberTypeHelper {

    /**
     * Builds a new DialingNumberType from the provided RufnummerportierungVO
     *
     * @param vo
     * @return
     */
    public static DialingNumberType buildFrom(RufnummernportierungVO vo) {
        String areaDialingCode =
                !vo.getOnkz().startsWith("0") ? String.format("0%s", vo.getOnkz()) : vo.getOnkz();
        return new DialingNumberTypeBuilder()
                    .withAreaDialingCode(areaDialingCode)
                    .withDialingNumber(vo.getDnBase())
                    .withCentral(vo.getDirectDial())
                    .withRangeFrom(stringToInteger(vo.getBlockFrom()))
                    .withRangeTo(stringToInteger(vo.getBlockTo()))
                    .build();
    }

    public static List<DialingNumberType> lookupDialNumbers(Rufnummernportierung rufnummernportierung) {
        List<DialingNumberType> dialNumbers = new ArrayList<>();
        if (RufnummernportierungTyp.ANLAGE.equals(rufnummernportierung.getTyp())) {
            RufnummernportierungAnlage rufnummernAnlage = (RufnummernportierungAnlage) rufnummernportierung;
            for (Rufnummernblock rufnummernblock : rufnummernAnlage.getRufnummernbloecke()) {
                dialNumbers.add(
                        new DialingNumberTypeBuilder()
                                .withAreaDialingCode(rufnummernAnlage.getOnkzWithLeadingZero())
                                .withDialingNumber(rufnummernAnlage.getDurchwahlnummer())
                                .withCentral(rufnummernAnlage.getAbfragestelle())
                                .withRangeFrom(stringToInteger(rufnummernblock.getRnrBlockVon()))
                                .withRangeTo(stringToInteger(rufnummernblock.getRnrBlockBis()))
                                .build()
                );
            }
        }
        else {
            RufnummernportierungEinzeln rufnummernEinzeln = (RufnummernportierungEinzeln) rufnummernportierung;
            for (RufnummerOnkz rufnummernOnkz : rufnummernEinzeln.getRufnummernOnkz()) {
                dialNumbers.add(
                        new DialingNumberTypeBuilder()
                                .withAreaDialingCode(rufnummernOnkz.getOnkzWithLeadingZero())
                                .withDialingNumber(rufnummernOnkz.getRufnummer())
                                .build()
                );
            }
        }
        return dialNumbers;
    }

    /**
     * Converts the supplied string to an integer, returning null is the supplied string is null.
     * @param str the string to map
     * @return the mapped Integer or null
     */
    private static Integer stringToInteger(String str) {
        if (str != null) {
            return Integer.parseInt(str);
        }
        return null;
    }

}
