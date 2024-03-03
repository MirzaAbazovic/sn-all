/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all RUEM-VAs that are capable of being processed automatically, from being automatically processed.
 */
public class ResetAutomatedRuemVaProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedRuemVaProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedRuemVaProcessing", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<String> vaIDs = wbciDao.findPreagreementsWithAutomatableRuemVa();
        if (CollectionUtils.isEmpty(vaIDs)) {
            return null;
        }
        Set<String> result = Sets.newHashSet();
        result.addAll(vaIDs);
        return result;
    }

}
