/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;

import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciVaService;

/**
 *
 */
public class DetermineIsSearchBuildingExpected<GF extends WbciGeschaeftsfall> extends AbstractWbciTestAction {

    private WbciVaService<GF> wbciVaService;
    private final WbciGeschaeftsfall expectedWbciGeschaeftsfallForSearchBuilding;

    public DetermineIsSearchBuildingExpected(WbciVaService<GF> wbciVaService, WbciGeschaeftsfall expectedWbciGeschaeftsfallForSearchBuilding) {
        super("determineIsSearchByStandortExpected");
        this.wbciVaService = wbciVaService;
        this.expectedWbciGeschaeftsfallForSearchBuilding = expectedWbciGeschaeftsfallForSearchBuilding;
    }

    @Override
    public void doExecute(TestContext context) {
        Set<Long> orderNoOrigsByDNs = wbciVaService.getOrderNoOrigsByDNs(expectedWbciGeschaeftsfallForSearchBuilding);
        /**
         * Search for standort-maches when:
         * <ul>
         *     <li>GF is VA-KUE-MRN and VA-KUE-ORN</li>
         *     <li>and no unique match is already found</li>
         * </ul>
         */
        boolean searchByStandort = !GeschaeftsfallTyp.VA_RRNP.equals(expectedWbciGeschaeftsfallForSearchBuilding.getTyp()) && orderNoOrigsByDNs.size() != 1;
        context.setVariable(VariableNames.IS_SEARCH_BUILDING_EXPECTED, searchByStandort);

    }
}
