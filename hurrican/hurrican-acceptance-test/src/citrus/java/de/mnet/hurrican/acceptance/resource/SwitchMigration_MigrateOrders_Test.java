/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2015
 */
package de.mnet.hurrican.acceptance.resource;

import static de.augustakom.hurrican.model.cc.cps.CPSTransaction.*;

import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.ActionTimeoutException;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.TaifunVariables;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class SwitchMigration_MigrateOrders_Test extends AbstractResourceInventoryTestBuilder {

    public static final String MUC_06 = "MUC06";
    public static final String MUC_07 = "MUC07";

    /**
     * Test prepares single order that is added to switch migration run. Test verifies that automated CPS modify
     * subscriber is sent during migration.
     *
     * @throws FindException
     */
    @CitrusTest
    public void SwitchMigration_MigrateOrders_Test_01() throws FindException {
        simulatorUseCase(SimulatorUseCase.SwitchMigration_MigrateOrders_01);

        VerlaufTestBuilder.CreatedData auftragData = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);

        cps().sendProvisionRequest("PROVISION_REQUEST").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE");

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true);

        hurrican().verifyHwSwitch(auftragData.auftrag.getAuftragId(), MUC_07);

        hurrican().migrateOrdersToSwitch(Collections.singletonList(createSwitchMigrationView(auftragData)));

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");

        hurrican().verifyHwSwitch(auftragData.auftrag.getAuftragId(), MUC_06);
        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true);

        hurrican().verifyMigrateOrderWarnings(0);
    }

    /**
     * Test prepares three orders that are added to a migration run. First order is successful with automated CPS modify
     * subscriber. Second order is marked as manual provisioning so CPS modify subscriber should not be send (but a
     * warning is written to XLS). Third order has no active CPS provisioning yet so CPS modify subscriber should also
     * not be sent (but a warning is written to XLS).
     *
     * @throws FindException
     */
    @CitrusTest
    public void SwitchMigration_MigrateOrders_Test_02() throws FindException {
        simulatorUseCase(SimulatorUseCase.SwitchMigration_MigrateOrders_02);

        VerlaufTestBuilder.CreatedData auftragData1 = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);
        VerlaufTestBuilder.CreatedData auftragData2 = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);
        VerlaufTestBuilder.CreatedData auftragData3 = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);

        variable(VariableNames.AUFTRAG_ID + "1", auftragData1.auftrag.getAuftragId());
        variable(VariableNames.AUFTRAG_ID + "2", auftragData2.auftrag.getAuftragId());
        variable(VariableNames.AUFTRAG_ID + "3", auftragData3.auftrag.getAuftragId());

        variable(TaifunVariables.ORDER_NO + ".1", auftragData1.auftragDaten.getAuftragNoOrig());
        variable(TaifunVariables.ORDER_NO + ".2", auftragData2.auftragDaten.getAuftragNoOrig());
        variable(TaifunVariables.ORDER_NO + ".3", auftragData3.auftragDaten.getAuftragNoOrig());

        cps().sendProvisionRequest("PROVISION_REQUEST_1").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber_1");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE_1");

        cps().sendProvisionRequest("PROVISION_REQUEST_2").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber_2");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE_2");

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData1.auftrag.getAuftragId());

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData2.auftrag.getAuftragId());

        hurrican().verifyHwSwitch(auftragData1.auftrag.getAuftragId(), MUC_07);
        hurrican().verifyHwSwitch(auftragData2.auftrag.getAuftragId(), MUC_07);
        hurrican().verifyHwSwitch(auftragData3.auftrag.getAuftragId(), MUC_07);

        hurrican().changeAuftragTechnik(auftragData2.auftrag.getAuftragId(), at -> at.setPreventCPSProvisioning(true));

        List<SwitchMigrationView> switchMigrationViews = new ArrayList<>();
        switchMigrationViews.add(createSwitchMigrationView(auftragData1));
        switchMigrationViews.add(createSwitchMigrationView(auftragData2));
        switchMigrationViews.add(createSwitchMigrationView(auftragData3));

        hurrican().migrateOrdersToSwitch(switchMigrationViews);

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber_1");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");

        assertException(
                cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber_2")
                        .timeout(3000L)
        ).exception(ActionTimeoutException.class);

        assertException(
                cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber_3")
                        .timeout(3000L)
        ).exception(ActionTimeoutException.class);

        hurrican().verifyHwSwitch(auftragData1.auftrag.getAuftragId(), MUC_06);
        hurrican().verifyHwSwitch(auftragData2.auftrag.getAuftragId(), MUC_06);

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData1.auftrag.getAuftragId());

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData2.auftrag.getAuftragId());

        hurrican().verifyMigrateOrderWarnings(2);
    }

    /**
     * Test prepares three orders that are added to a migration run. First order is responded by CPS with 500 error
     * code. Second order is successful.
     *
     * @throws FindException
     */
    @CitrusTest
    public void SwitchMigration_MigrateOrders_Test_03() throws FindException {
        simulatorUseCase(SimulatorUseCase.SwitchMigration_MigrateOrders_03);

        VerlaufTestBuilder.CreatedData auftragData1 = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);
        VerlaufTestBuilder.CreatedData auftragData2 = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);

        variable(VariableNames.AUFTRAG_ID + "1", auftragData1.auftrag.getAuftragId());
        variable(VariableNames.AUFTRAG_ID + "2", auftragData2.auftrag.getAuftragId());

        variable(TaifunVariables.ORDER_NO + ".1", auftragData1.auftragDaten.getAuftragNoOrig());
        variable(TaifunVariables.ORDER_NO + ".2", auftragData2.auftragDaten.getAuftragNoOrig());

        cps().sendProvisionRequest("PROVISION_REQUEST_1").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber_1");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE_1");

        cps().sendProvisionRequest("PROVISION_REQUEST_2").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber_2");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE_2");

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData1.auftrag.getAuftragId());

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData2.auftrag.getAuftragId());

        hurrican().verifyHwSwitch(auftragData1.auftrag.getAuftragId(), MUC_07);
        hurrican().verifyHwSwitch(auftragData2.auftrag.getAuftragId(), MUC_07);

        List<SwitchMigrationView> switchMigrationViews = new ArrayList<>();
        switchMigrationViews.add(createSwitchMigrationView(auftragData1));
        switchMigrationViews.add(createSwitchMigrationView(auftragData2));

        hurrican().migrateOrdersToSwitch(switchMigrationViews);

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber_1");
        cps().sendCpsAsyncServiceRequestHttpError(HttpStatus.INTERNAL_SERVER_ERROR);

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_ModifySubscriber_2");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");

        hurrican().verifyHwSwitch(auftragData1.auftrag.getAuftragId(), MUC_06);
        hurrican().verifyHwSwitch(auftragData2.auftrag.getAuftragId(), MUC_06);

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData1.auftrag.getAuftragId());

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true)
                .withAuftragId(auftragData2.auftrag.getAuftragId());

        hurrican().verifyMigrateOrderWarnings(1);
    }

    private SwitchMigrationView createSwitchMigrationView(VerlaufTestBuilder.CreatedData createdData) {
        SwitchMigrationView switchMigrationView = new SwitchMigrationView();
        switchMigrationView.setAuftragId(createdData.auftrag.getAuftragId());
        switchMigrationView.setProdId(createdData.auftragDaten.getProdId());
        return switchMigrationView;
    }
}
