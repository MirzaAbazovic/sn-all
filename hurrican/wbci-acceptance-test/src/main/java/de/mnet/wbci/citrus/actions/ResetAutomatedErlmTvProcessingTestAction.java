/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 20.08.2014
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all ERLM-TVs that are capable of being processed automatically, from being automatically processed.
 */
public class ResetAutomatedErlmTvProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedErlmTvProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedErlmTvProcessing", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<ErledigtmeldungTerminverschiebung> erlmTvs = wbciDao.findAutomateableTvErlmsForWitaProcessing(null);

        Set<String> vaIds = new HashSet<>();
        if (erlmTvs != null) {
            for (ErledigtmeldungTerminverschiebung erlmTv : erlmTvs) {
                vaIds.add(erlmTv.getVorabstimmungsId());
            }
        }
        return vaIds;
    }

}
