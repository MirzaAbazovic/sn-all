/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 17:10:26
 */
package de.mnet.hurrican.scheduler.utils;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

/**
 * Hilfsklasse fuer die Arbeit mit JobExecutionContext-Modellen.
 *
 *
 */
public class JobExecutionContextHelper {

    /**
     * Ermittelt aus der JobDataMap (context.getJobDetail().getJobDataMap()) das Objekt, das unter dem Namen
     * <code>name</code> gespeichert ist.
     *
     * @return Objekt, das dem Namen in der JobDataMap zugeordnet ist.
     */
    public static Object getJobDataMapObject(JobExecutionContext context, String name) {
        if (context != null) {
            JobDetail jd = context.getJobDetail();
            if (jd != null) {
                JobDataMap jdm = jd.getJobDataMap();
                return (jdm != null) ? jdm.get(name) : null;
            }
        }
        return null;
    }

    /**
     * Speichert das Object <code>object</code> in der JobDataMap (erreichbar ueber
     * context.getJobDetail().getJobDataMap()). <br> Als Key fuer das Objekt wird <code>name</code> verwendet.
     */
    public static void setJobDataMapObject(JobExecutionContext context, String name, Object object) {
        if ((context != null) && StringUtils.isNotBlank(name)) {
            JobDetail jd = context.getJobDetail();
            if (jd != null) {
                JobDataMap jdm = jd.getJobDataMap();
                if (jdm != null) {
                    jdm.put(name, object);
                }
            }
        }
    }

    /**
     * Ermittelt aus der JobDataMap des Triggers(!) (context.getJobDetail().getJobDataMap()) das Objekt, das unter dem
     * Namen <code>name</code> gespeichert ist.
     *
     * @return Objekt, das dem Namen in der JobDataMap des Triggers(!) zugeordnet ist.
     */
    public static Object getJobDataMapObjectFromTrigger(JobExecutionContext context, String name) {
        if (context != null) {
            Trigger trigger = context.getTrigger();
            if (trigger != null) {
                JobDataMap jdm = trigger.getJobDataMap();
                return (jdm != null) ? jdm.get(name) : null;
            }
        }
        return null;
    }
}
