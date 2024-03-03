/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2016
 */
package de.mnet.hurrican.scheduler.job.cps;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.hurrican.scheduler.HurricanScheduler;

/**
 * Die Mail-Konfiguration mit Initialisierung der
 *
 */
public class CPSJobMailConfig {
    private String mailTo;
    private String mailSubject;

    public void initialize(ResourceReader resourceReader, RegistryService rs) throws Exception{
        mailTo = rs.getStringValue(RegistryService.REGID_CPS_MAIL_4_JOBS);
        mailSubject = StringTools.formatString(resourceReader.getValue("mail.subject"),
                new Object[] { HurricanScheduler.getApplicationMode() });
        if (StringUtils.isBlank(mailTo)) {
            throw new Exception("No eMail address defined!");
        }
    }

    public String getMailTo() {
        return mailTo;
    }

    public String getMailSubject() {
        return mailSubject;
    }
}

