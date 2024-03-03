/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2009 17:40:44
 */
package de.augustakom.hurrican.service.cc.mock;

import static org.mockito.Mockito.*;

import org.springframework.mail.javamail.JavaMailSender;

import de.augustakom.common.tools.mail.HurricanMailSender;


/**
 *
 */
public class CCMockFactory {

    private static volatile JavaMailSender mailSender = null;

    public static JavaMailSender getMailSender() {
        if (mailSender == null) {
            synchronized (CCMockFactory.class) {
                if (mailSender == null) {
                    mailSender = mock(HurricanMailSender.class);
                }
            }
        }
        return mailSender;
    }

}
