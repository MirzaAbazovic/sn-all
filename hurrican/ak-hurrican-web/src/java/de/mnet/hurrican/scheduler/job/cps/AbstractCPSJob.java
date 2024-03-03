/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2009 13:06:02
 */
package de.mnet.hurrican.scheduler.job.cps;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.cc.CPSService;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;

/**
 * Abstrakte Basis-Klasse fuer alle CPS-Jobs.
 */
public abstract class AbstractCPSJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(AbstractCPSJob.class);
    private static final String DEFAULT_WARNING = "Error sending transaction (ID: {0}) to CPS: {1}";

    /**
     * Sendet die angegebenen CPS-Transactions an den CPS. <br> Alle evtl. auftretenden Fehler oder Warnungen waehrend
     * dem Send-Vorgang werden wie folgt behandelt: <br> <ul> <li>Eintrag in <code>warnings</code> Objekt </ul>
     *
     * @param toSend     Liste mit den CPS-Transactions, die an den CPS uebertragen werden sollen
     * @param warnings   Objekt, in das auftretende Fehler u. Warnungen eingetragen werden sollen
     * @param cpsService Instanz eines CPS-Services
     * @return Array mit zwei int-Werte: <br> Index 0: Anzahl erfolgreich uebertragener CPS-Transactions <br> Index 1:
     * Anzahl fehlerhafter Uebertragungen
     */
    protected int[] sendCPSTransactions(List<CPSTransaction> toSend, AKWarnings warnings, CPSService cpsService) {
        int success = 0;
        int error = 0;
        if (CollectionTools.isNotEmpty(toSend)) {
            for (CPSTransaction cpsTx : toSend) {
                if (cpsTx == null) {
                    continue;
                }

                try {
                    LOGGER.info("Calling CPSService.sendCPSTx2CPS for Tx ID " + cpsTx.getId());

                    cpsService.sendCPSTx2CPS(cpsTx, HurricanScheduler.getSessionId());
                    success++;

                    LOGGER.info("DONE Calling CPSService.sendCPSTx2CPS for Tx ID " + cpsTx.getId());
                }
                catch (Exception e) {
                    error++;
                    LOGGER.error(e.getMessage(), e);
                    warnings.addAKWarning(this, StringTools.formatString(
                            DEFAULT_WARNING, new Object[] { "" + cpsTx.getId(), e.getMessage() }));
                }
            }
        }
        return new int[] { success, error };
    }

}
