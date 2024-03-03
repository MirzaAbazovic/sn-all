/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all STR-AUFH ERLMs that are capable of being processed automatically, from being automatically processed.
 */
public class ResetAutomatedStrAufhErlmProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedStrAufhErlmProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedStrAufhErlmProcessing", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<ErledigtmeldungStornoAuf> erlms = wbciDao.findAutomateableStrAufhErlmsForWitaProcessing(null);

        Set<String> vaIds = new HashSet<>();
        if (erlms != null) {
            for (ErledigtmeldungStornoAuf erlm : erlms) {
                vaIds.add(erlm.getVorabstimmungsId());
            }
        }
        return vaIds;
    }
    
}
