/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2011 10:25:29
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType.CcType.*;
import static de.augustakom.hurrican.service.cc.impl.crossconnect.CcTechLeistungStrategyTyp.*;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;

/**
 *
 */
public class CcTechLeistungStrategy {

    private static List<Pair<CcTechLeistungStrategyTyp, List<CcStrategyType.CcType>>> strategies
            = new ArrayList<Pair<CcTechLeistungStrategyTyp, List<CcStrategyType.CcType>>>();

    static {
        // @formatter:off
        // create(voip, businessCPE, IpV6)
        add(create(false, false, false), EnumSet.of(HSI, CPE));
        add(create(true,  false, false), EnumSet.of(HSI, IAD, VOIP));
        add(create(false, true,  false), EnumSet.of(HSI, IAD));
        add(create(true,  true,  false), EnumSet.of(HSI, IAD, VOIP));

        add(create(false, false, true ), EnumSet.of(HSI, IAD));
        add(create(true,  false, true ), EnumSet.of(HSI, IAD, VOIP));
        add(create(false, true,  true ), EnumSet.of(HSI, IAD));
        add(create(true,  true, true  ), EnumSet.of(HSI, IAD, VOIP));
        // @formatter:off
    }

    private static void add(CcTechLeistungStrategyTyp techLeistungStrategyTyp, Set<CcStrategyType.CcType> ccTypes) {
        List<CcStrategyType.CcType> ccTypeList = new ArrayList<CcStrategyType.CcType>();
        if (CollectionTools.isNotEmpty(ccTypes)) {
            for (CcStrategyType.CcType ccType : ccTypes) {
                ccTypeList.add(ccType);
            }
        }
        strategies.add(Pair.create(techLeistungStrategyTyp, ccTypeList));
    }

    /**
     * Sucht abhängig von den technischen Leistungen nach der Liste der CC Typen, die berechnet werden sollen.
     *
     * @return Klon der CC Typen Liste bzw. leere Liste
     */
    public static List<CcStrategyType.CcType> get(CcTechLeistungStrategyTyp techLeistungStrategyTyp) {
        List<CcStrategyType.CcType> strategyTypes = new ArrayList<CcStrategyType.CcType>();

        for (Pair<CcTechLeistungStrategyTyp, List<CcStrategyType.CcType>> rule : strategies) {
            if (rule.getFirst().isMatch(techLeistungStrategyTyp)) {
                strategyTypes.addAll(rule.getSecond());
                return strategyTypes;
            }
        }
        List<CcStrategyType.CcType> emptyResult = new ArrayList<CcStrategyType.CcType>();
        return emptyResult;
    }
}


