/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2015
 */
package de.mnet.wita.acceptance.common.role;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaTestAction;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.citrus.actions.CreateCbVorgangWithKlammerung;
import de.mnet.wita.citrus.actions.CreateHvtKvzCbVorgangTestAction;
import de.mnet.wita.citrus.actions.CreateMasterChildCbVorgangTestAction;
import de.mnet.wita.citrus.actions.TriggerHvtKvzBereitstellungTestAction;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

/**
 *
 */
public class HurricanTestRole extends AbstractTestRole {

    @Autowired
    private WitaTalOrderService talOrderService;

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    @Autowired
    private MwfEntityService mwfEntityService;

    @Autowired
    private CarrierService carrierService;

    @Autowired
    protected WitaSendMessageService witaSendMessageService;

    @Autowired
    private AKLoginContext akLoginContext;

    @Autowired
    @Qualifier("de.augustakom.hurrican.dao.cc.CBVorgangDAO")
    protected CBVorgangDAO cbVorgangDAO;

    public String getNextVertragsnummer() {
        return cbVorgangDAO.getNextCarrierRefNr();
    }

    /**
     * Create new master child CbVorgaenge with Klammerung.
     */
    public CreateCbVorgangWithKlammerung createCbVorgangWithKlammerung(Long cbVorgangTyp,CreatedData... createdDatas) throws Exception {
        CreateCbVorgangWithKlammerung action = new CreateCbVorgangWithKlammerung(cbVorgangTyp, talOrderService, createdDatas);
        testBuilder.action(action);
        return action;
    }

    public CreateMasterChildCbVorgangTestAction createMasterChildCbVorgang(AcceptanceTestWorkflow workflowMaster, CreatedData createdDataMaster,
            AcceptanceTestWorkflow workflowChild, CreatedData createdDataChild) throws Exception {
        CreateMasterChildCbVorgangTestAction action = new CreateMasterChildCbVorgangTestAction(CBVorgang.TYP_NEU, workflowMaster, createdDataMaster, workflowChild, createdDataChild, talOrderService);
        testBuilder.action(action);

        return action;
    }

    /**
     * Create new CbVorgang and Kuendigung CbVorgang for HvtKvz Umzug.
     */
    public CreateHvtKvzCbVorgangTestAction createHvtKvzCBVorgaenge(AcceptanceTestWorkflow workflowNeu, CreatedData createdDataNeu,
            AcceptanceTestWorkflow workflowKue, CreatedData createdDataKue) throws Exception {
        CreateHvtKvzCbVorgangTestAction action = new CreateHvtKvzCbVorgangTestAction(workflowNeu, createdDataNeu, workflowKue, createdDataKue, talOrderService);
        testBuilder.action(action);

        return action;
    }

    /**
     * Find and trigger Hvt Bereitstellung Auftrag. Bereitstellung CbVorgangId has to be saved as test variable {@link
     * de.mnet.wita.citrus.VariableNames#CB_VORGANG_ID} before.
     */
    public void findAndTriggerHvtKvzBereitstellungAuftrag() {
        testBuilder.action(new TriggerHvtKvzBereitstellungTestAction(mwfEntityService, witaSendMessageService));
    }

    /**
     * Assert Klaerfall for Wita CBVorgang and check reason with message regular expression match. CbVorgangId has to be
     * saved as test variable {@link de.mnet.wita.citrus.VariableNames#CB_VORGANG_ID} before.
     */
    public void assertKlaerfall(String klaerfallRegexp) {
        testBuilder.action(new AbstractWitaTestAction("assertKlaerfallOnCBVorgang") {
            @Override
            public void doExecute(TestContext testContext) {
                Long vorgangId = Long.valueOf(testContext.getVariable(VariableNames.CB_VORGANG_ID));
                WitaCBVorgang bereitstellung = cbVorgangDAO.findById(vorgangId, WitaCBVorgang.class);
                assertTrue(bereitstellung.getKlaerfallBemerkung().matches(klaerfallRegexp),
                        String.format("Got: '%s', Tried to match with: '%s'", bereitstellung.getKlaerfallBemerkung(), klaerfallRegexp));
            }
        });
    }

    /**
     * Checks Carrierbestellung fields for KueKd.
     */
    public void assertCbFieldsForKuendigung(CreatedData createdData, boolean kuendigungAnCarrierIsNull,
            boolean kuendBestaetigungCarrierIsNull) {
        testBuilder.action(new AbstractWitaTestAction("assertCbFieldsForKuendigung") {
            @Override
            public void doExecute(TestContext testContext) {
                try {
                    Carrierbestellung cb = carrierService.findCB(createdData.carrierbestellung.getId());
                    assertThat(cb.getKuendigungAnCarrier(), kuendigungAnCarrierIsNull ? nullValue() : notNullValue());
                    assertThat(cb.getKuendBestaetigungCarrier(), kuendBestaetigungCarrierIsNull ? nullValue() : notNullValue());
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException("Failed to find CarrierBestellung", e);
                }
            }
        });
    }

    public void assertAkmPvUserTaskIsAbbmOnAbm() {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertAkmPvUserTaskIsAbbmOnAbm") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
                try {
                    AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTaskByContractId(workflow.getCbVorgang().getReturnVTRNR());
                    assertNotNull(userTask);
                    assertTrue(userTask.getAbbmOnAbm());
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException("Failed to load cbVorgang", e);
                }
            }
        });
    }

    public void assertAkmPvUserTaskStatus(AkmPvUserTask.AkmPvStatus expectedStatus, UserTask.UserTaskStatus userTaskStatus, boolean expectTv) {
        testBuilder.action(new AbstractWitaWorkflowTestAction(String.format("assertAkmPvUserTaskStatus(%s)", expectedStatus)) {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
                try {
                    AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTaskByContractId(workflow.getCbVorgang().getReturnVTRNR());
                    assertNotNull(userTask);
                    assertEquals(userTask.getAkmPvStatus(), expectedStatus);
                    assertEquals(userTask.getStatus(), userTaskStatus);
                    assertNull(userTask.getBearbeiter());
                    assertEquals(BooleanTools.nullToFalse(userTask.getTerminverschiebung()), expectTv);
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException("Failed to load cbVorgang", e);
                }
            }
        });
    }

    public void assertMwfEntityCountSearchByVtrNr(final Class mwfEntityClass, final int expectedResultCount) {
        testBuilder.action(new AbstractWitaWorkflowTestAction("assertAkmPvUserTaskIsAbbmOnAbm") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
                try {
                    List<AnkuendigungsMeldungPv> result = mwfEntityService.findMwfEntitiesByProperty(mwfEntityClass,
                            Meldung.VERTRAGS_NUMMER_FIELD, workflow.getCbVorgang().getReturnVTRNR());
                    assertNotNull(result);
                    assertEquals(result.size(), expectedResultCount);
                }
                catch (FindException e) {
                    throw new CitrusRuntimeException("Failed to load cbVorgang", e);
                }
            }
        });
    }

    /**
     * Closes cbVorgang by its id. CbVorgang is selected by the current workflow.
     */
    public void closeCBVorgang() {
        testBuilder.action(new AbstractWitaWorkflowTestAction("closeCbVorgang") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                try {
                    talOrderService.closeCBVorgang(workflow.getCbVorgangId(), akLoginContext.getUserSession().getSessionId());
                }
                catch (Exception e) {
                    throw new CitrusRuntimeException("Failed to close cbVorgang", e);
                }
            }
        });
    }

    public String getNextCarrierRefNr() {
        return cbVorgangDAO.getNextCarrierRefNr();
    }

}
