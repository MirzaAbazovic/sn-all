/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.13
 */
package de.mnet.wbci.model.helper;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.model.TechnischeRessource;

/**
 *
 */
public final class TechnischeRessourceHelper {

    private TechnischeRessourceHelper() {
    }

    /**
     * Converts a Set of {@link TechnischeRessource} to Hashmap with following key pattern: <ul> <li>{@link
     * TechnischeRessource#vertragsnummer} is set => Key = WITA-Vertragsnummer</li> <li>{@link
     * TechnischeRessource#lineId} is set => Key = WBCI-Line-Id</li> <li>else => the entry won't be considered</li>
     * </ul>
     *
     * @param technischeRessourcen Set of {@link TechnischeRessource}
     * @return a Map with valid Entries
     */
    public static Map<String, TechnischeRessource> convertToMap(Set<TechnischeRessource> technischeRessourcen) {
        Map<String, TechnischeRessource> map = new HashMap<>();
        for (TechnischeRessource tr : technischeRessourcen) {
            if (tr.getVertragsnummer() != null) {
                map.put(tr.getVertragsnummer(), tr);
            }
            else if (tr.getLineId() != null) {
                map.put(tr.getLineId(), tr);
            }
            else if (tr.getIdentifizierer() != null) {
                map.put(tr.getIdentifizierer(), tr);
            }
        }
        return map;
    }

    /**
     * @return a filtered Map, which only contains the entries matching to the keys.
     */
    public static Map<String, TechnischeRessource> filterKeys(final Map<String, TechnischeRessource> technischeRessourceMap, final List<String> keys) {
        return Maps.filterKeys(technischeRessourceMap, new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return keys.contains(input);
            }
        });
    }

    /**
     * Extract the {@link TechnischeRessource#vertragsnummer}
     *
     * @param technischeRessources Set of {@link TechnischeRessource}s
     * @return a sorted set of WITA-Vertragsnummern as Strings
     */
    public static SortedSet<String> getWitaVertragsNrs(Set<TechnischeRessource> technischeRessources) {
        SortedSet<String> witaVertragsNr = new TreeSet<>();
        if (technischeRessources != null) {
            for (TechnischeRessource technischeRessource : technischeRessources) {
                if (StringUtils.isNotEmpty(technischeRessource.getVertragsnummer())) {
                    witaVertragsNr.add(technischeRessource.getVertragsnummer());
                }
            }
        }
        return witaVertragsNr;
    }
}
