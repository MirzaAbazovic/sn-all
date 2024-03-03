/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;

/**
 * <pre>
 *     Hurrican (Receiving Carrier)             Elektra        WITA
 *     check: Hurrican order re-activated
 *     WITA cancellation                                  ->   check if CBVorgang is cancelled  -  no communication!
 *     Taifun undoCancellation            ->    verify undoCancelOrder
 *
 * </pre>
 */
public class TriggerStrAufhErlmDonatingProcessing_TestBehavior extends AbstractTestBehavior {

    private AutomationTask.AutomationStatus automationStatus = AutomationTask.AutomationStatus.COMPLETED;
    
    public TriggerStrAufhErlmDonatingProcessing_TestBehavior withAutomationStatus(
            AutomationTask.AutomationStatus automationStatus) {
        
        this.automationStatus = automationStatus;
        return this;
    }
    
    @Override
    public void apply() {
        // triggger auto-processing
        hurrican().triggerAutomatedStrAufhErlmDonatingProcessing();

        LocalDateTime automationTaskCompleted = null;
        if (automationStatus == AutomationTask.AutomationStatus.COMPLETED) {
            automationTaskCompleted = LocalDateTime.now();
            
            // pruefen, dass HUR Auftrag 'in Betrieb'
            hurrican().assertAuftragDatenStatus(AuftragStatus.IN_BETRIEB);
    
            // check WITA storno 
            hurrican().assertWitaCbVorgangCancelled();

            elektra().receiveElektraServiceRequest("UndoPlannedOrderCancellationRequest");
            elektra().sendElektraServiceResponse("UndoPlannedOrderCancellationResponse");

            elektra().receiveElektraServiceRequest("GenerateAndPrint_UndoCancelOrder_Request");
            elektra().sendElektraServiceResponse("GenerateAndPrintResponse");
        }
        else {
            hurrican().assertAuftragDatenStatus(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        }

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(automationStatus)
                        .withName(AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(automationTaskCompleted)
                        .build()
        ), false);

    }

}
