/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2010 07:57:42
 */
package de.mnet.hurrican.scheduler.job.cps;

import java.util.*;

/**
 * Erstellt aus den heutigen Bauauftraegen CPS-Transaktionen, sofern der Bauauftrag noch keine CPS-Tx besitzt bzw. der
 * BA noch nicht abgeschlossen ist.
 *
 *
 */
public class CPSCreateCPSTx4BATodayJob extends CPSCreateCPSTx4BAJob {

    @Override
    protected Date getExecutionDay() {
        return new Date();
    }

}
