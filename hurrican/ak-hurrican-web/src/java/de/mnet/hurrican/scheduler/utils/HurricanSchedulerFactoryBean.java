/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.03.14 15:00
 */
package de.mnet.hurrican.scheduler.utils;

import java.util.*;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Erweiterung der SchedulerFactoryBean der man Trigger und Jobs auch als Liste statt nur als Array mitgeben kann. Ist
 * noetig um ein redudantes Anlegen der SchedulerFactoryBean fuer Prod als auch Dev zu vermeiden. Auf diese Weise kann
 * man die Liste der Trigger oder auch Jobs im jeweiligen Dev- bzw. Prod-Context definieren, waehrend die
 * SchedulerFactoryBean nur noch im Common-Context definiert sein muss. (Zum Hintergrund: Spring kann nur Listen als
 * Bean instanziieren, fuer Arrays ist dies nicht moeglich)
 */
public class HurricanSchedulerFactoryBean extends SchedulerFactoryBean {

    public void setTriggers(List<Trigger> triggers) {
        super.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
    }

    public void setJobDetails(List<JobDetail> jobDetails) {
        super.setJobDetails(jobDetails.toArray(new JobDetail[jobDetails.size()]));
    }
}
