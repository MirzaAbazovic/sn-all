/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.message.MeldungsType;

/**
 *
 */
public class WaitForWitaNotificationTestAction extends AbstractWitaWorkflowTestAction {

    private final MeldungsType meldungType;

    public WaitForWitaNotificationTestAction(MeldungsType meldungType) {
        super("waitFor" + meldungType.name());

        this.meldungType = meldungType;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        try {
            switch (meldungType) {
                case QEB:
                    workflow.waitForQEB();
                    break;
                case ABM:
                    workflow.waitForABM();
                    break;
                case ERLM:
                    workflow.waitForERLM();
                    break;
                case ENTM:
                    workflow.waitForENTM();
                    break;
                case ABBM:
                    workflow.waitForABBM();
                    break;
                case TAM:
                    workflow.waitForTAM();
                    break;
                case AKM_PV:
                    workflow.waitForAkmPv();
                    break;
                case ABM_PV:
                    workflow.waitForAbmPv();
                    break;
                case ERLM_PV:
                    workflow.waitForErlmPv();
                    break;
                case ENTM_PV:
                    workflow.waitForEntmPv();
                    break;
                case VZM:
                    workflow.waitForVZM();
                    break;
                case VZM_PV:
                    workflow.waitForVZMPV();
                    break;
                default:
                    throw new CitrusRuntimeException(String.format("Unsupported type of WITA Meldung - can not wait for this type '%s'", meldungType.name()));
            }
        }
        catch (Exception e) {
            throw new CitrusRuntimeException(String.format("Failed to wait for notification '%s'", meldungType.name()), e);
        }
    }
}
