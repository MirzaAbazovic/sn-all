/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.WitaMessage;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer Services, die den Workflow Tasks bereitstehen.
 */
public interface WorkflowTaskService extends WitaService {

    /**
     * Speichert das Objekt {@code request} und sendet eine Nachricht an die Wita Schnittstelle. <br>
     *
     * @param request Nachricht, die gesendet werden soll
     * @throws WitaUserException um zu signalisieren, dass der Workflow in einen Error-Status versetzt werden soll.
     */
    <T extends WitaMessage> void sendToWita(T request) throws WitaUserException;

    /**
     * Versetzt den Workflow, der ueber {@code execution} definiert ist, in einen Error-Status.
     */
    void setWorkflowToError(DelegateExecution execution, String errorMessage);

    /**
     * Versetzt den Workflow, der ueber {@code execution} definiert ist, in einen Error-Status.
     */
    void setWorkflowToError(DelegateExecution execution, String errorMessage, Exception e);

    /**
     * Validiert eine eingehende Nachricht. Setzt den uebergebenen Workflow in den Fehlerstatus, falls notwendig.
     *
     * @return true, wenn die Validierung erfolgreich war.
     */
    boolean validateMwfInput(Meldung<?> meldung, DelegateExecution execution);

}
