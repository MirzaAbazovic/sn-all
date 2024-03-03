/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 17:44:14
 */
package de.mnet.hurrican.scheduler.dao;

import java.util.*;

import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.hurrican.scheduler.model.JobExecution;

/**
 * DAO-Interface fuer Objekte des Typs <code>JobExecution</code>.
 *
 *, gilgan
 */
public interface JobExecutionDAO extends StoreDAO {

    /**
     * Loescht alle Job-Executions, deren Start-Date vor <code>maxStartDate</code> liegt.
     *
     * @return Anzahl der geloeschten Datensaetze.
     */
    public int deleteOldJobs(Date maxStartDate);

    /**
     * Liefert die letzte erfolgreiche JobExecution anhand des Job-Namens.
     */
    public JobExecution findLastSuccessfullExecutionByJobName(String jobName);
}
