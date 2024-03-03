/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2009 18:48:43
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWSubrackBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;


/**
 * TestNG Klasse fuer {@link CheckAndDefineCrossConnectionsCommand}
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class CheckAndDefineCrossConnectionsCommandTest extends AbstractHurricanBaseServiceTest {

    private EQCrossConnectionService eqCrossConnectionService;


    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
        eqCrossConnectionService = getCCService(EQCrossConnectionService.class);
    }


    /**
     * Test fuer {@link CheckAndDefineCrossConnectionsCommand#getDSLAMPort(de.augustakom.hurrican.model.cc.Endstelle)}
     */
    public void testGetDSLAMPort() throws FindException {
        EquipmentBuilder eqInBuilderDSLAM = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(getBuilder(HWBaugruppeBuilder.class)
                        .withSubrackBuilder(getBuilder(HWSubrackBuilder.class)));

        Endstelle endstelleDSLAM = getBuilder(EndstelleBuilder.class)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqInBuilder(eqInBuilderDSLAM))
                .build();

        CheckAndDefineCrossConnectionsCommand command = (CheckAndDefineCrossConnectionsCommand)
                getBean(CheckAndDefineCrossConnectionsCommand.class.getName());

        Equipment eqDSLAM = command.getDSLAMPort(endstelleDSLAM);
        Assert.assertNotNull(eqDSLAM, "DSLAM port not found!");
    }


    /**
     * Test fuer {@link CheckAndDefineCrossConnectionsCommand#getDSLAMPort(de.augustakom.hurrican.model.cc.Endstelle)}
     * Auch fuer Huawei Hardware (keine Subracks) muessen die CCs berechnet werden.
     */
    public void testGetDSLAMPortNoResultBecauseNoSubrack() throws FindException {
        EquipmentBuilder eqInBuilderDSLAM = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(getBuilder(HWBaugruppeBuilder.class)
                        .withSubrackBuilder(null)
                        .withRackBuilder(getBuilder(HWDslamBuilder.class)));

        Endstelle endstelleNoDSLAM = getBuilder(EndstelleBuilder.class)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqInBuilder(eqInBuilderDSLAM))
                .build();

        CheckAndDefineCrossConnectionsCommand command = (CheckAndDefineCrossConnectionsCommand)
                getBean(CheckAndDefineCrossConnectionsCommand.class.getName());

        Equipment eqNoDSLAM = command.getDSLAMPort(endstelleNoDSLAM);
        Assert.assertNotNull(eqNoDSLAM);
    }


    /**
     * Test fuer {@link CheckAndDefineCrossConnectionsCommand#getDSLAMPort(de.augustakom.hurrican.model.cc.Endstelle)}
     */
    public void testGetDSLAMPortNoResult() throws FindException {
        Endstelle endstelleNoDSLAM = getBuilder(EndstelleBuilder.class)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqInBuilder(getBuilder(EquipmentBuilder.class)
                                .withBaugruppeBuilder(null)))
                .build();

        CheckAndDefineCrossConnectionsCommand command = (CheckAndDefineCrossConnectionsCommand)
                getBean(CheckAndDefineCrossConnectionsCommand.class.getName());

        Equipment eqNoDSLAM = command.getDSLAMPort(endstelleNoDSLAM);
        Assert.assertNull(eqNoDSLAM, "DSLAM port found but not expected!");
    }


    /**
     * Test fuer {@link CheckAndDefineCrossConnectionsCommand#defineDefaultCrossConnection4Port(Equipment)}
     */
    public void testDefineDefaultCrossConnection4Port() throws ServiceCommandException, FindException {
        Integer ltInner = Integer.valueOf(999);
        EquipmentBuilder eqBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(getBuilder(HWBaugruppeBuilder.class)
                        .withSubrackBuilder(getBuilder(HWSubrackBuilder.class)));
        eqBuilder.getHwBaugruppenBuilder().getRackBuilder().getHwProducerBuilder().toAlcatel();

        getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(eqBuilder)
                .withLtInner(ltInner)
                .withReferenceBuilder(getBuilder(ReferenceBuilder.class))
                .build();

        CheckAndDefineCrossConnectionsCommand command = (CheckAndDefineCrossConnectionsCommand)
                getBean(CheckAndDefineCrossConnectionsCommand.class.getName());
        command.prepare(AbstractVerlaufCheckCommand.KEY_REAL_DATE, new Date());

        command.defineDefaultCrossConnection4Port(eqBuilder.get());

        // aktuelle CrossConnections laden / pruefen
        List<EQCrossConnection> result = eqCrossConnectionService.findEQCrossConnections(
                eqBuilder.get().getId(), new Date());
        assertNotEmpty(result, "No CrossConnections found for date but!");
        for (EQCrossConnection cc : result) {
            Assert.assertTrue(NumberTools.notEqual(cc.getLtInner(), ltInner), "LT-Inner of cross-connection is not as expected!");
            Assert.assertTrue(DateTools.isDateEqual(cc.getGueltigVon(), new Date()), "ValidFrom of cross-connection is not 'now'");
            Assert.assertTrue(DateTools.isDateEqual(cc.getGueltigBis(), DateTools.getHurricanEndDate()), "ValidTo of cross-connection is not 'end-date'");
        }

        // CrossConnection History laden / pruefen
        Date yesterday = DateTools.changeDate(new Date(), Calendar.DATE, -1);
        List<EQCrossConnection> oldCrossConn = eqCrossConnectionService.findEQCrossConnections(
                eqBuilder.get().getId(), yesterday);
        assertNotEmpty(oldCrossConn, "No cross connection history found!");
        Assert.assertEquals(oldCrossConn.size(), 1, "count of cross-connection history is not as expected!");
        EQCrossConnection ccHistory = oldCrossConn.get(0);
        Assert.assertTrue(NumberTools.equal(ccHistory.getLtInner(), ltInner), "LT-Inner of cross-connection history is not as expected!");
        Assert.assertTrue(DateTools.isDateEqual(ccHistory.getGueltigBis(), yesterday), "ValidTo of cross-connection is not 'yesterday'");
    }


    /**
     * Test fuer {@link CheckAndDefineCrossConnectionsCommand#execute()}
     */
    public void testExecuteWithManualFlag() throws Exception {
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class);
        HWBaugruppeBuilder baugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withSubrackBuilder(getBuilder(HWSubrackBuilder.class));
        baugruppeBuilder.getRackBuilder().getHwProducerBuilder().toAlcatel();

        RangierungBuilder rangierungABuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class)
                        .withBaugruppeBuilder(baugruppeBuilder)
                        .withManualConfiguration(Boolean.FALSE));
        RangierungBuilder rangierungBBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(getBuilder(EquipmentBuilder.class)
                        .withBaugruppeBuilder(baugruppeBuilder)
                        .withManualConfiguration(Boolean.TRUE));

        EQCrossConnection ccA = getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(rangierungABuilder.getEqInBuilder())
                .build();
        EQCrossConnection ccB = getBuilder(EQCrossConnectionBuilder.class)
                .withEquipmentBuilder(rangierungBBuilder.getEqInBuilder())
                .build();

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);
        endstelleBuilder
                .withRangierungBuilder(rangierungABuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A)
                .build();
        endstelleBuilder
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBBuilder)
                .build();

        CheckAndDefineCrossConnectionsCommand command = (CheckAndDefineCrossConnectionsCommand)
                getBean(CheckAndDefineCrossConnectionsCommand.class.getName());

        Date now = new Date();

        command.prepare(AbstractVerlaufCheckCommand.KEY_AUFTRAG_ID, auftragTechnikBuilder.getAuftragBuilder().get().getId());
        command.prepare(AbstractVerlaufCheckCommand.KEY_REAL_DATE, now);
        ServiceCommandResult result = (ServiceCommandResult) command.execute();

        assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);

        List<EQCrossConnection> ccs;

        ccs = eqCrossConnectionService.findEQCrossConnections(rangierungABuilder.getEqInBuilder().get().getId(), now);
        assertFalse(ccs.contains(ccA));
        ccs = eqCrossConnectionService.findEQCrossConnections(rangierungBBuilder.getEqInBuilder().get().getId(), now);
        assertTrue(ccs.contains(ccB));
    }

}


