/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 10:22:30
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.PhysikTypBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSSdslData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;


/**
 * TestNG UnitTest fuer {@link CPSGetSdslDataCommand}
 */
@Test(groups = BaseTest.SERVICE)
public class CPSGetSdslDataCommandTest extends AbstractHurricanBaseServiceTest {

    private static final String PORT_ID = "001-04";
    private static final String MANUFACTURER = "HUAWEI";
    private static final String VBZ = "DV123456-2009";

    private AuftragBuilder auftragBuilder;
    private HWRack hwRack;

    public void testExecute() throws Exception {
        buildTestData(Schicht2Protokoll.EFM.name(), true);

        @SuppressWarnings("deprecation")
        CPSGetSdslDataCommand sdslDataCommand = (CPSGetSdslDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetSdslDataCommand");

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(Long.valueOf(123))
                .withAuftragBuilder(auftragBuilder)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        sdslDataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        sdslDataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        Object result = sdslDataCommand.execute();

        assertTrue((result instanceof ServiceCommandResult), "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK, "ServiceCommand not successful!");

        assertNotNull(serviceOrderData.getSdsl(), "No SDSL data defined!");

        CPSSdslData sdslData = serviceOrderData.getSdsl();
        assertEquals(sdslData.getDslamManufacturer(), MANUFACTURER);
        assertEquals(sdslData.getDslamName(), hwRack.getGeraeteBez());
        assertEquals(sdslData.getDslamPortType(), Schicht2Protokoll.EFM.name());
        assertNotEmpty(sdslData.getDslamPorts(), "keine SDSL Ports definiert!");
        assertEquals(sdslData.getDslamPorts().get(0).getDslamPort(), PORT_ID);
    }

    public void testExecuteWithoutPorts() throws Exception {
        buildTestData(Schicht2Protokoll.EFM.name(), false);

        @SuppressWarnings("deprecation")
        CPSGetSdslDataCommand sdslDataCommand = (CPSGetSdslDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetSdslDataCommand");

        CPSTransaction cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withOrderNoOrig(Long.valueOf(123))
                .withAuftragBuilder(auftragBuilder)
                .withServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB)
                .withEstimatedExecTime(DateTools.changeDate(new Date(), Calendar.DATE, 1))
                .build();

        CPSServiceOrderData serviceOrderData = new CPSServiceOrderData();
        sdslDataCommand.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, serviceOrderData);
        sdslDataCommand.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        Object result = sdslDataCommand.execute();

        assertTrue((result instanceof ServiceCommandResult), "Result is not of type ServiceCommandResult");
        ServiceCommandResult cmdResult = (ServiceCommandResult) result;
        assertEquals(cmdResult.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK, "ServiceCommand not successful!");

        assertNull(serviceOrderData.getSdsl(), "SDSL data defined but not expected!");
    }


    private void buildTestData(String dslamPortType, boolean buildPorts) {
        auftragBuilder = getBuilder(AuftragBuilder.class).withRandomId();
        EquipmentBuilder eqInBuilder = createEqInBuilder(dslamPortType);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        if (buildPorts) {
            RangierungBuilder rangierungBuilder = buildRangierung(eqInBuilder);
            endstelleBBuilder.withRangierungBuilder(rangierungBuilder);
        }
        else {
            endstelleBBuilder.withoutRangierung();
        }

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz(VBZ))
                .withEndstelleBuilder(endstelleBBuilder)
                .build();
    }

    private EquipmentBuilder createEqInBuilder(String dslamPortType) {
        HWBaugruppeBuilder hwBaugruppeBuilder = createHwBaugruppeBuilder();
        hwBaugruppeBuilder.build();
        return getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withSchicht2Protokoll(Schicht2Protokoll.valueOf(dslamPortType))
                .withHwEQN("U03-3-" + PORT_ID);
    }

    private HWBaugruppeBuilder createHwBaugruppeBuilder() {
        HWBaugruppenTypBuilder bgTypBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .withHwTypeName("XDSL_AGB")
                .withName("SHEB");

        HWDslamBuilder hwDslamBuilder = getBuilder(HWDslamBuilder.class)
                .withHwProducerBuilder(
                        getBuilder(HVTTechnikBuilder.class)
                                .withHersteller(MANUFACTURER)
                                .withCpsName(MANUFACTURER)
                );
        hwRack = hwDslamBuilder.build();

        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withBaugruppenTypBuilder(bgTypBuilder)
                .withRackBuilder(hwDslamBuilder);

        return hwBaugruppeBuilder;
    }

    private RangierungBuilder buildRangierung(EquipmentBuilder eqInBuilder) {
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .withPhysikTypBuilder(getBuilder(PhysikTypBuilder.class))
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class));
        return rangierungBuilder;
    }

}


