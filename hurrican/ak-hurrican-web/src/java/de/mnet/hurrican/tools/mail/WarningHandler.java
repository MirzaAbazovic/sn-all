/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2013 16:45:55
 */
package de.mnet.hurrican.tools.mail;

import org.springframework.mail.SimpleMailMessage;

import de.augustakom.common.tools.messages.AKWarnings;

/**
 * Interface um Warnungen zu versenden
 */
public interface WarningHandler {

    void handleWarning(SimpleMailMessage errorMailMessage, AKWarnings warnings, String message);
}


