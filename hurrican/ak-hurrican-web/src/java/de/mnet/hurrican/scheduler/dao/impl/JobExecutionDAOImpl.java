/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2005 08:02:58
 */
package de.mnet.hurrican.scheduler.dao.impl;

import java.util.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.mnet.hurrican.scheduler.dao.JobExecutionDAO;
import de.mnet.hurrican.scheduler.model.JobExecution;

/**
 * Hibernate DAO-Implementierung von <code>JobExecutionDAO</code>.
 *
 *
 */
public class JobExecutionDAOImpl extends Hibernate4DAOImpl implements JobExecutionDAO {

    @Autowired
    @Qualifier("scheduler.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public int deleteOldJobs(Date maxStartDate) {
        return sessionFactory.getCurrentSession().createSQLQuery("delete from JOB_EXECUTION where START_TIME < ?")
            .setDate(0, maxStartDate).executeUpdate();
    }

    @Override
    public JobExecution findLastSuccessfullExecutionByJobName(String jobName) {
        StringBuilder sql = new StringBuilder("select ex.* ");
        sql.append(" from  job_execution ex left join job_error er on ex.id = er.job_id ");
        sql.append(" where error_level is null ");
        sql.append(" and ex.JOB_NAME = ? ");
        sql.append(" and ex.END_TIME is not null ");
        sql.append(" order by ex.START_TIME desc");

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
        sqlQuery.setString(0, jobName);
        List<Object[]> result = sqlQuery.list();
        if ((result != null) && (!result.isEmpty())) {
            Object[] values = result.get(0);
            JobExecution job = new JobExecution();
            job.setId(ObjectTools.getLongSilent(values, 0));
            job.setJobName(ObjectTools.getStringSilent(values, 1));
            job.setJobClass(ObjectTools.getStringSilent(values, 2));
            job.setStartTime(ObjectTools.getDateSilent(values, 3));
            job.setEndTime(ObjectTools.getDateSilent(values, 4));
            job.setNextTime(ObjectTools.getDateSilent(values, 5));
            return job;
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
