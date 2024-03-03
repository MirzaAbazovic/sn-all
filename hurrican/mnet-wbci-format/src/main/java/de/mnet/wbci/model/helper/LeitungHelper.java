/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2014
 */
package de.mnet.wbci.model.helper;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.Leitung;

/**
 *
 */
public class LeitungHelper {
    /**
     * Extract the {@link Leitung#vertragsnummer}
     *
     * @param leitungen Set of {@link Leitung}en
     * @return a sorted set of WITA-Vertragsnummern as Strings
     */
    public static SortedSet<String> getWitaVertragsNrs(Set<Leitung> leitungen) {
        SortedSet<String> witaVertragsNr = new TreeSet<>();
        if (leitungen != null) {
            for (Leitung leitung : leitungen) {
                if (StringUtils.isNotEmpty(leitung.getVertragsnummer())) {
                    witaVertragsNr.add(leitung.getVertragsnummer());
                }
            }
        }
        return witaVertragsNr;
    }
}
