/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;

import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;

/**
 * Finds and triggers the sending the the Bereitstellung Auftrag. <br /> The HVT_KFT use case creates two
 * CBVorgange. The first Vorgang, the Kuendigung, is sent to the facade. The second Vorgang, the Bereitstellung, is
 * scheduled to be sent later on (when the ABM arrives for the kuendigung). Because the scheduler, that would
 * normally trigger the sending of the Bereitstellung, does not run in acceptance testing, the sending has to be
 * triggered manually.
 *
 *
 */
public class TriggerHvtKvzBereitstellungTestAction extends AbstractWitaTestAction {

    private final MwfEntityService mwfEntityService;
    private final WitaSendMessageService witaSendMessageService;

    public TriggerHvtKvzBereitstellungTestAction(MwfEntityService mwfEntityService, WitaSendMessageService witaSendMessageService) {
        super("triggerHvtKvzBereitstellung");

        this.mwfEntityService = mwfEntityService;
        this.witaSendMessageService = witaSendMessageService;
    }

    @Override
    public void doExecute(TestContext testContext) {
        Long vorgangId = Long.valueOf(testContext.getVariable(VariableNames.CB_VORGANG_ID));

        Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(vorgangId);
        auftrag.setEarliestSendDate(null);
        witaSendMessageService.sendAndProcessMessage(auftrag);
    }
}
