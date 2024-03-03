/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2014
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import java.util.regex.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import de.augustakom.hurrican.dao.cc.MailDAO;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 *
 */
public class AssertEscalationEmailEntryAction extends AbstractWbciTestAction {

    public static final String SUBJECT_REG_EXP = "(.*), Applikationsmodus .*\\)$";
    public static final int SUBJECT_REG_EXP_GROUP = 1;
    private final MailDAO mailDAO;
    private final String from;
    private final String to;
    private final String subject;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertEscalationEmailEntryAction.class);

    public AssertEscalationEmailEntryAction(MailDAO mailDAO, String from, String to, String subject) {
        super("assertEscalationEmailEntryAction");
        this.mailDAO = mailDAO;
        this.from = from;
        this.to = to;
        this.subject = subject;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            List<Mail> pendingMails = mailDAO.findAllPendingMails();
            Assert.assertTrue(CollectionUtils.isNotEmpty(pendingMails));
            Matcher matcher = Pattern.compile(SUBJECT_REG_EXP).matcher(subject);
            boolean found = false;
            if (matcher.find()) {
                for (Mail mail : pendingMails) {
                    LOGGER.info(String.format("Found pending mail from '%s' to '%s' with subject '%s'", mail.getFrom(), mail.getTo(), mail.getSubject()));

                    Matcher mailMatcher = Pattern.compile(SUBJECT_REG_EXP).matcher(mail.getSubject());
                    if (from.equals(mail.getFrom())
                            && to.equals(mail.getTo())
                            && mailMatcher.find()
                            && matcher.group(SUBJECT_REG_EXP_GROUP).equals(mailMatcher.group(SUBJECT_REG_EXP_GROUP))) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                throw new CitrusRuntimeException(String.format("The escalation email sent from '%s' to '%s' with " +
                        "subject '%s' couldn't be found in the table 'T_MAIL'!", from, to, subject));
            }
        }
        catch (FindException e) {
            throw new CitrusRuntimeException(e);
        }
    }

}
