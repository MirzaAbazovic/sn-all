/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Sets;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all STR-AUFH ERLMs that are capable of being processed automatically, from being automatically processed.
 * M-net = donating carrier
 */
public class ResetAutomatedStrAufhErlmDonatingProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedStrAufhErlmDonatingProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedStrAufhErlmDonatingTestAction", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<ErledigtmeldungStornoAuf> erlms = wbciDao.findAutomateableStrAufhErlmsDonatingProcessing();
        if (CollectionUtils.isEmpty(erlms)) {
            return null;
        }
        Set<String> result = Sets.newHashSet();
        for (ErledigtmeldungStornoAuf erlm : erlms) {
            result.add(erlm.getVorabstimmungsId());
        }
        return result;
    }

}
