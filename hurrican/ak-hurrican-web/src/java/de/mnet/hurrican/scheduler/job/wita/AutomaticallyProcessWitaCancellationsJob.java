/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2013 13:19:03
 */
package de.mnet.hurrican.scheduler.job.wita;

import org.quartz.JobExecutionContext;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Scheduler-Job, der alle positiv bestätigten WITA-Kuendigungen mit gesetztem Automatismus-Flag ermittelt und
 * abschliesst.
 */
public class AutomaticallyProcessWitaCancellationsJob extends AbstractProcessWitaResponseJob {

    @Override
    protected Long[] getCbVorgangTypes() {
        return new Long[] { CBVorgang.TYP_KUENDIGUNG };
    }

    @Override
    protected void closeWitaAutomatically(WitaCBVorgang witaCbVorgang, JobExecutionContext context) throws StoreException, ValidationException {
        elektraFacadeService.terminateOrder(witaCbVorgang);
        witaTalOrderService.closeCBVorgang(witaCbVorgang.getId(), HurricanScheduler.getSessionId());
    }

}


