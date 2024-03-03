/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 25.07.2014 
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;

import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.service.WbciGeschaeftsfallService;

/**
 * Prevents all RUEM-VAs that are capable of being processed automatically, from being automatically processed.
 */
public class ResetAutomatedAkmTrProcessingTestAction extends AbstractResetAutomatedVaProcessingTestAction {

    public ResetAutomatedAkmTrProcessingTestAction(WbciDao wbciDao, WbciGeschaeftsfallService wbciGeschaeftsfallService) {
        super("resetAutomatedAkmTrProcessing", wbciDao, wbciGeschaeftsfallService);
    }

    @Override
    protected Set<String> getVaIdsToPreventForAutomaticProcessing() {
        List<UebernahmeRessourceMeldung> akmTrs = wbciDao.findAutomatableAkmTRsForWitaProcesing(Technologie.getWitaOrderRelevantTechnologies());
        Set<String> vaIds = new HashSet<>();
        if (akmTrs != null) {
            for (UebernahmeRessourceMeldung akmTr : akmTrs) {
                vaIds.add(akmTr.getVorabstimmungsId());
            }
        }
        return vaIds;
    }

}
