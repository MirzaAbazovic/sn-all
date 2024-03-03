/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.03.2010
 */
package de.mnet.hurrican.tools.mail;

import org.springframework.mail.SimpleMailMessage;

public interface ErrorHandler {

    void handleError(SimpleMailMessage errorMailMessage, Throwable ex, String message);

}
