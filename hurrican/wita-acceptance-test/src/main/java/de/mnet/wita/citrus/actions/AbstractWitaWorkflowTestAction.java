/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2015
 */
package de.mnet.wita.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;

/**
 *
 */
public abstract class AbstractWitaWorkflowTestAction extends AbstractWitaTestAction {

    private AcceptanceTestWorkflow workflow;

    /**
     * Constructor setting the action name field.
     *
     * @param actionName
     */
    public AbstractWitaWorkflowTestAction(String actionName) {
        super(actionName);
    }

    @Override
    public final void doExecute(TestContext testContext) {
        workflow = (AcceptanceTestWorkflow) testContext.getVariables().get(VariableNames.TEST_WORKFLOW);
        try {
            doExecute(workflow, testContext);
            exportCreatedDataToVariables(testContext);
        }
        catch (Exception e) {
            throw new CitrusRuntimeException("Failed to perform workflow action", e);
        }
    }

    /**
     * Test action execution method with workflow instance provided.
     *
     * @param workflow
     * @param testContext
     */
    protected abstract void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception;

    protected void exportCreatedDataToVariables(TestContext context) throws FindException {
        getCreatedData(context).ifPresent(createdData -> {
            CCAddress customerAddress = createdData.address;
            context.setVariable(VariableNames.ENDKUNDE_NACHNAME, customerAddress.getName());
            if (customerAddress.getVorname() != null) {
                context.setVariable(VariableNames.ENDKUNDE_VORNAME, customerAddress.getVorname());
            }
            context.setVariable(VariableNames.STANDORT_STRASSE, customerAddress.getStrasse());
            context.setVariable(VariableNames.STANDORT_HNR, customerAddress.getNummer());
            String hausnummerZusatz = customerAddress.getHausnummerZusatz() == null ? "" : customerAddress.getHausnummerZusatz();
            context.setVariable(VariableNames.STANDORT_HNR_ZUSATZ, hausnummerZusatz);
            context.setVariable(VariableNames.STANDORT_PLZ, customerAddress.getPlz());
            context.setVariable(VariableNames.STANDORT_ORT, customerAddress.getOrt());
            context.setVariable(VariableNames.STANDORT_ZUSATZ, customerAddress.getOrtsteil());
            String ortsteil = customerAddress.getOrtsteil() == null ? "" : customerAddress.getOrtsteil();
            context.setVariable(VariableNames.STANDORT_ORTSTEIL, ortsteil);

            exportRufnummernToVariables(context, createdData.rufnummern);
        });

    }

    protected Optional<CreatedData> getCreatedData(TestContext context) {
        Object createdDataObject = context.getVariables().getOrDefault(VariableNames.TEST_DATA, null);
        return Optional.ofNullable(createdDataObject instanceof CreatedData ? (CreatedData) createdDataObject : null);
    }

    protected void exportRufnummernToVariables(TestContext context, Collection<Rufnummer> rufnummern) {
        int count = 0;
        for (Rufnummer dn : rufnummern) {
            context.setVariable(String.format(VariableNames.RUFNUMMER_ONKZ, count), dn.getOnKzWithoutLeadingZeros());
            context.setVariable(String.format(VariableNames.RUFNUMMER_DNBASE, count), dn.getDnBase());
            if (dn.isBlock()) {
                context.setVariable(String.format(VariableNames.RUFNUMMER_DIRECT_DIAL, count), dn.getDirectDial());
                context.setVariable(String.format(VariableNames.RUFNUMMER_RANGE_FROM, count), dn.getRangeFrom());
                context.setVariable(String.format(VariableNames.RUFNUMMER_RANGE_TO, count), dn.getRangeTo());
            }
            count++;
        }
    }

    /**
     * Lambda expression ready callback for workflow actions in test action boundaries.
     */
    public interface WorkflowCallback {
        void doWithWorkflow(AcceptanceTestWorkflow workflow) throws Exception;
    }
}
