/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2004 10:48:16
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * Test, um die Mail-API des Spring-Frameworks zu testen.
 *
 *
 */
@Test(groups = { "unit" })
public class MailSenderTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(MailSenderTest.class);

    @Test(enabled = false)
    public void testSendMail() {
        try {
            JavaMailSenderImpl ms = new JavaMailSenderImpl();
            ms.setHost("10.1.2.133");

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("AK-Scheduler");
            msg.setTo(new String[] { "GlinkJo@m-net.de" });
            msg.setSubject("Mail-Test");
            msg.setText("Just a simple Mail...\nnext line...");

            ms.send(msg);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            fail(e.getMessage());
        }
    }

}


