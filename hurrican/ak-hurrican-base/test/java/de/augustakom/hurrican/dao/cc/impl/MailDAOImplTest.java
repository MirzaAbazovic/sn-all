/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2014
 */
package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.MailDAO;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.MailBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 *
 */
@Test(groups = BaseTest.SERVICE)
public class MailDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    MailDAO mailDAO;

    @Test
    public void testMailWithLongText() {
        final String textLong = generateString(4001);
        Mail mail = mailDAO.store(new MailBuilder()
                .withId(null)
                .withTextLong(textLong)
                .build());
        mailDAO.flushSession();
        assertNotNull(mail.getId());
        final Mail mailDAOById = mailDAO.findById(mail.getId(), Mail.class);
        assertEquals(mailDAOById.getTextLong(), textLong);
    }

    private static String generateString(int length) {
        StringBuilder outputBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            outputBuilder.append("a");
        }
        return outputBuilder.toString();
    }

}
