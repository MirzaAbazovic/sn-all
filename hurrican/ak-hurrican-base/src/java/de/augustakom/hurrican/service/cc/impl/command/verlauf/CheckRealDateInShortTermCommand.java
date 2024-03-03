/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 10:35:11
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;


/**
 * Command-Klasse prueft, ob der Realisierungstermin kurzfristig ist (heute oder morgen). <br> Ist dies der Fall, wird
 * das Chain-Result 'RESULT_KEY_IN_SHORT_TERM auf Boolean.TRUE gesetzt.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckRealDateInShortTermCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckRealDateInShortTermCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckRealDateInShortTermCommand.class);

    private MailSender mailSender = null;

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            if (DateTools.isTodayOrNextWorkDay(getRealDate())) {
                getServiceCommandChain().addCommandResult(RESULT_KEY_IN_SHORT_TERM, Boolean.TRUE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }


    /**
     * @return Returns the mailSender.
     */
    public MailSender getMailSender() {
        return mailSender;
    }

    /**
     * @param mailSender The mailSender to set.
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

}


