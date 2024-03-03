/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.14
 */
package de.mnet.wbci.acceptance.donating.behavior;

import java.time.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.HurricanConstants;
import de.mnet.wbci.acceptance.AbstractTestBehavior;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.builder.AutomationTaskBuilder;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Test-Behavior, um die automatische AKM-TR Prozessierung (AKM-TR eingehend) zu triggern und die
 * notwendigen Aktionen zu pruefen.
 * <pre>
 *     AtlasESB     Hurrican (Receiving Carrier)       WITA
 *     AKM-TR   ->
 *                  WITA cancellation             ->   check if CBVorgang is created - no communication!
 * </pre>
 */
public class TriggerIncomingAkmTrProcessing_TestBehavior extends AbstractTestBehavior {

    private boolean expectedWitaKuendigungSuccess;
    private String updatePortKennungTnbRequest;
    private String updatePortKennungTnbResponse;

    public TriggerIncomingAkmTrProcessing_TestBehavior(final boolean expectedWitaKuendigungSuccess) {
        this.expectedWitaKuendigungSuccess = expectedWitaKuendigungSuccess;
    }

    /**
     *
     * @param updatePortKennungTnbRequest
     * @param updatePortKennungTnbResponse
     * @return
     */
    public TriggerIncomingAkmTrProcessing_TestBehavior withUpdatePortKennungTnb(String updatePortKennungTnbRequest, String updatePortKennungTnbResponse) {
       this.updatePortKennungTnbRequest = updatePortKennungTnbRequest;
       this.updatePortKennungTnbResponse = updatePortKennungTnbResponse;
        return this;
    }

    @Override
    public void apply() {
        hurrican().triggerAutomatedIncomingAkmTrProcessing();

        if(isUpdatePortKennungTnb()) {
            elektra().receiveElektraServiceRequest(updatePortKennungTnbRequest);
            elektra().sendElektraServiceResponse(updatePortKennungTnbResponse);
        }

        if (expectedWitaKuendigungSuccess) {
            hurrican().assertWitaCbVorgangCreated(WitaCBVorgang.TYP_KUENDIGUNG);
        }

        AutomationTask.AutomationStatus expectedStatus = (expectedWitaKuendigungSuccess)
                ? AutomationTask.AutomationStatus.COMPLETED
                : AutomationTask.AutomationStatus.ERROR;
        LocalDateTime completedAt = (expectedWitaKuendigungSuccess) ? LocalDateTime.now() : null;

        List<AutomationTask> expectedAutomationTasks = new ArrayList<>();

        expectedAutomationTasks.add(new AutomationTaskBuilder()
                .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                .withName(AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN)
                .withUserName(HurricanConstants.SYSTEM_USER)
                .withCompletedAt(LocalDateTime.now())
                .build());
        expectedAutomationTasks.add(new AutomationTaskBuilder()
                .withStatus(expectedStatus)
                .withName(AutomationTask.TaskName.WITA_SEND_KUENDIGUNG)
                .withUserName(HurricanConstants.SYSTEM_USER)
                .withCompletedAt(completedAt)
                .build());

        if(isUpdatePortKennungTnb()) {
            expectedAutomationTasks.add(new AutomationTaskBuilder()
                    .withStatus(AutomationTask.AutomationStatus.COMPLETED)
                    .withName(AutomationTask.TaskName.TAIFUN_NACH_AKMTR_AKTUALISIEREN)
                    .withUserName(HurricanConstants.SYSTEM_USER)
                    .withCompletedAt(LocalDateTime.now())
                    .build());
        }

        hurrican().assertAutomationTasks(expectedAutomationTasks);

        hurrican().assertGfStatus((expectedWitaKuendigungSuccess)
                ? WbciGeschaeftsfallStatus.PASSIVE : WbciGeschaeftsfallStatus.ACTIVE);
    }

    private boolean isUpdatePortKennungTnb() {
        return StringUtils.isNotBlank(updatePortKennungTnbRequest);
    }

}
