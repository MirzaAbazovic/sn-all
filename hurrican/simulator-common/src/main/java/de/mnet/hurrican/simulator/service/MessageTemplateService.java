/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.13
 */
package de.mnet.hurrican.simulator.service;

import java.util.*;

import de.mnet.hurrican.simulator.model.MessageTemplate;

/**
 * Service interface for message template management. Service implementations do provide default message templates for
 * later manual use case triggering vie servlet user interface.
 *
 *
 */
public interface MessageTemplateService {

    List<MessageTemplate> getDefaultMessageTemplates();
}
