/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 17:40:32
 */
package de.mnet.wita.activiti;

/**
 * Interface fuer Entitaeten, die einen Activiti-Workflow ausloesen koennen.
 */
public interface CanOpenActivitiWorkflow {

    /**
     * Activiti Business Key
     */
    String getBusinessKey();
}
