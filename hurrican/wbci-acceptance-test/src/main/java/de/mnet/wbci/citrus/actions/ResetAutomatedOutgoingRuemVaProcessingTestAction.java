/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all outgoing RUEM-VAs that are capable of being processed automatically, from being automatically processed.
 */
public class ResetAutomatedOutgoingRuemVaProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedOutgoingRuemVaProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedOutgoingRuemVaProcessing", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<WbciGeschaeftsfall> wbciGfs = wbciDao.findAutomateableOutgoingRuemVaForKuendigung();
        if (CollectionUtils.isEmpty(wbciGfs)) {
            return null;
        }
        Set<String> result = Sets.newHashSet();
        for (WbciGeschaeftsfall gf : wbciGfs) {
            result.add(gf.getVorabstimmungsId());
        }
        return result;
    }

}
