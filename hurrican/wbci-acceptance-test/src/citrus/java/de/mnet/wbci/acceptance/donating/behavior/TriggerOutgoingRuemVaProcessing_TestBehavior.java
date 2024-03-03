/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.14
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.time.*;
import java.util.*;

import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;

/**
 * Test-Behavior, um die automatische RUEM-VA Prozessierung (RUEM_VA ausgehend) zu triggern und die
 * notwendigen Aktionen zu pruefen.
 * <pre>
 *     AtlasESB     Hurrican (Receiving Carrier)      Elektra
 *              <-  RUEMVA
 *                  cancelOrder                   ->
 * </pre>
 */
public class TriggerOutgoingRuemVaProcessing_TestBehavior extends AbstractTestBehavior {

    @Override
    public void apply() {
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_VERSENDET);

        // triggger auto-processing
        hurrican().triggerAutomatedOutgoingRuemVaProcessing();

        elektra().receiveElektraServiceRequest("CancelOrderRequest");
        elektra().sendElektraServiceResponse("CancelOrderResponse");

        elektra().receiveElektraServiceRequest("GenerateAndPrint_CancelOrder_Request");
        elektra().sendElektraServiceResponse("GenerateAndPrintResponse");

        hurrican().assertAutomationTasks(Arrays.asList(
                new AutomationTaskBuilder()
                        .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                        .withName(AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN)
                        .withUserName(HurricanConstants.SYSTEM_USER)
                        .withCompletedAt(LocalDateTime.now())
                        .build()
        ));

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        // pruefen, dass HUR Auftrag gekuendigt und Kuendigungs-BA erstellt ist
        hurrican().assertAuftragDatenStatus(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        hurrican().verifyBauauftragExist(BAVerlaufAnlass.KUENDIGUNG, VerlaufStatus.KUENDIGUNG_BEI_DISPO);
    }

}
