/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2014
 */
package de.mnet.hurrican.acceptance.resource;

import static de.augustakom.hurrican.model.cc.cps.CPSTransaction.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.behavior.SendCommandStiftData_Behavior;
import de.mnet.hurrican.acceptance.behavior.SendCpsServiceResponse_Behavior;
import de.mnet.hurrican.acceptance.behavior.SendUpdateResource_Behavior;
import de.mnet.hurrican.acceptance.behavior.SendUpdateResource_Port_Behavior;
import de.mnet.hurrican.acceptance.behavior.SendUpdateResource_SerialNr_Behavior;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class ResourceInventory_UpdateDpoResource_Test extends AbstractResourceInventoryTestBuilder {

    private final String hwRackType = HWRack.RACK_TYPE_DPO;

    /**
     * Simulates the command application by sending DPO and DPO-Port without stift and leiste update resource requests
     * to hurrican. After each update resource request a check is made to ensure that the resources was created
     * correctly in hurrican.
     */
    @CitrusTest
    public void UpdateDpoResource_01_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_01);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();
        String chassisBezeichnung = "CH-12";
        setVariable(VariableNames.CHASSIS_BEZEICHNUNG, chassisBezeichnung);
        String chassisPosition = "1";
        setVariable(VariableNames.CHASSIS_POSITION, chassisPosition);

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));
        hurrican().verifyDpoCreated(chassisBezeichnung, chassisPosition);

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_Port");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyDpoPortCreated();
    }

    /**
     * Simulates the command application by sending two DPO update resource requests to hurrican. The first request
     * contains no serial number. The second request contains a serial number which results in a CPS request being sent
     * to CPS.
     */
    @CitrusTest
    @Test
    public void UpdateDpoResource_02_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_02);
        final String serialNumber = RandomStringUtils.randomAlphanumeric(11).toUpperCase();

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));
        hurrican().verifyDpoSerialNumber(null);
        hurrican().verifyDpoCreated(null, null);

        String chassisBezeichnung = "CH-12";
        setVariable(VariableNames.CHASSIS_BEZEICHNUNG, chassisBezeichnung);
        String chassisPosition = "1";
        setVariable(VariableNames.CHASSIS_POSITION, chassisPosition);
        applyBehavior(new SendUpdateResource_SerialNr_Behavior(hwRackType, serialNumber));
        hurrican().verifyDpoCreated(chassisBezeichnung, chassisPosition);

        applyBehavior(new SendCpsServiceResponse_Behavior());

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_Chassis");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");
        hurrican().verifyDpoCreated("CH-32", "3");
    }

    /**
     * Simulates the command application by sending three DPO update resource requests to hurrican. The first request
     * creates a DPO Resource. The second request sets the installation status to "GELOESCHT". The third request
     * modifies the "modellnummer" characteristic resulting in a new DPO resource being created in hurrican.
     */
    @CitrusTest
    @Test
    public void UpdateDpoResource_03_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_03);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_2");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyDpoChild()
                .withGueltigBisBeforeOrEqual(new Date());

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_3");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyDpoCreated();
        hurrican().verifyDpoChild()
                .withGueltigBisBeforeOrEqual(DateTools.getHurricanEndDate());
    }

    /**
     * 1. Simulates the command application by sending a DPO update resource request to hurrican
     * 2. Verifies that the DPO is correctly created in hurrican
     * 3. Simulates the command application by sending a commandStiftDataRequest to hurrican
     * 4. Verifies that for every stift wihtin the commandStiftDataRequest an equipment has been created in hurrican
     * 5. Simulates the command application by sending a DPO-Port update resource request to hurrican
     * 6. Verifies that the created Equipment in step 4 has been updated correctly with the hwEqn. This should be the
     *    case only for the first equipments since only for it a DpoPort update request has been sent from command.
     */
    @CitrusTest
    public void UpdateDpoResource_04_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_04);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        String hwEqn = "1-1";
        String leiste = "3";
        String stift1 = "01";
        String stift2 = "02";

        applyBehavior(new SendCommandStiftData_Behavior(hwEqn, leiste, stift1, stift2));

        applyBehavior(new SendUpdateResource_Port_Behavior(hwEqn, leiste, stift1));

        hurrican().verifyEquipment(null, leiste, stift2);
    }

    /**
     * 1. Simulates the command application by sending a DPO update resource request to hurrican
     * 2. Verifies that the DPO is correctly created in hurrican
     * 3. Simulates the command application by sending a DPO-Port update resource request with leiste and stift to
     *    hurrican. This should return a soap fault since no equipment with the provided leiste and stift has been
     *    created before that by command.
     */
    @CitrusTest
    public void UpdateDpoResource_05_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_05);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_Port");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse_Fault");
    }

    /**
     * Test verifies automatic create subscriber CPS transaction after CREATE_DEVICE is finished. HWDpo must have
     * a port that is already linked to a Hurrican order. Then create subscriber CPS transaction is done automatically after
     * create device CPS transaction.
     *
     *  1. Simulates the command application by sending a DPO update resource request to hurrican
     *  2. Verifies that the DPO is correctly created in hurrican
     *  3. Simulates the command application by sending a commandStiftDataRequest to hurrican
     *  4. Verifies that for every stift within the commandStiftDataRequest an equipment has been created in hurrican
     *  5. Simulates the command application by sending a DPO-Port update resource request to hurrican
     *  6. Verifies that the created Equipment in step 4 has been updated correctly with the hwEqn. This should be the
     *     case only for the first equipments since only for it a DpoPort update request has been sent from command.
     *  7. Update on DPO with serial number which triggers the create device CPS transaction.
     *  8. Adds DPO port as rangierung equipment to a Hurrican Auftrag.
     *  9. Verifies that DPO {@link de.augustakom.hurrican.model.cc.hardware.HWDpo#freigabe} is defined
     * 10. Finish create device CPS transaction and verify automatic create subscriber CPS transaction
     */
    @CitrusTest
    public void UpdateDpoResource_06_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateDpoResource_06);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTB_H, hwRackType);
        hurrican().createDpoBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        String hwEqn = "1-1";
        String leiste = "3";
        String stift1 = "01";
        String stift2 = "02";

        applyBehavior(new SendCommandStiftData_Behavior(hwEqn, leiste, stift1, stift2));

        applyBehavior(new SendUpdateResource_Port_Behavior(hwEqn, leiste, stift1));
        hurrican().verifyEquipment(null, leiste, stift2);

        hurrican().verifyDpoSerialNumber(null);
        final String serialNumber = RandomStringUtils.randomAlphanumeric(11).toUpperCase();
        applyBehavior(new SendUpdateResource_SerialNr_Behavior(hwRackType, serialNumber)
                            .withCpsServiceRequestTemplate("cpsServiceRequest_CreateDevice"));

        hurrican().createTaifunAuftragMaxiGlasfaserDsl(1);
        hurrican().createHurricanFttxTelefonieAuftrag();
        hurrican().assignPortToAuftrag(leiste, stift1);

        cps().sendCpsAsyncServiceResponse("cpsServiceResponse_CreateDevice");

        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PREPARING, true);

        variables().add("cpsCreateDeviceTxId", "${" + VariableNames.CPS_TX_ID + "}");

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.TX_STATE_IN_PROVISIONING, true);
        hurrican().verifyCpsTx(SERVICE_ORDER_TYPE_CREATE_DEVICE, CPSTransaction.TX_STATE_SUCCESS, false);

        hurrican().verifyDpoFreigabe(Date.from(ZonedDateTime.now().toInstant()));

        variables().add(VariableNames.CPS_TX_ID, "${cpsCreateDeviceTxId}");

        resourceInventory().receiveUpdateResourceRequest("updateResourceRequest_Freigabe");
        resourceInventory().sendUpdateResourceResponse("updateResourceResponse");
        cps().receiveCpsAsyncServiceResponseAck("cpsServiceResponseAck");

    }

}
