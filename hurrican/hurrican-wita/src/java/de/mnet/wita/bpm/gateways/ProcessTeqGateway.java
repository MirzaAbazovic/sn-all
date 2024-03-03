/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2012 14:07:27
 */
package de.mnet.wita.bpm.gateways;

public class ProcessTeqGateway extends AbstractGateway {

    // just uses workflowError method from super class which is required because old Workflow instances do not forward
    // "workflowError" variable to "WaitAndProcessTeq" sub Workflow
}
