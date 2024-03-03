/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2010 10:19:18
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.model.cc.AuftragMVSBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSEnterpriseBuilder;
import de.augustakom.hurrican.model.cc.AuftragMVSSiteBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.MVSService;
import de.augustakom.hurrican.service.cc.impl.command.tal.AbstractTALCommand;

/**
 * Test class for both mvs check commands CheckMVSEnterpriseCommand and CheckMVSSiteCommand.
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class CheckMVSCommandsTest extends AbstractHurricanBaseServiceTest {

    private Auftrag auftrag;

    @SuppressWarnings("unused")
    @BeforeMethod(groups = BaseTest.SERVICE, dependsOnMethods = "beginTransactions")
    private void prepareTest() throws StoreException {
        auftrag = getBuilder(AuftragBuilder.class).withAuftragTechnikBuilder(
                getBuilder(AuftragTechnikBuilder.class).withEndstelleBuilder(
                        getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B))
        ).build();
    }

    @DataProvider
    public Object[][] provider() {
        return new Object[][] { { CheckMVSEnterpriseCommand.class, AuftragMVSEnterpriseBuilder.class },
                { CheckMVSSiteCommand.class, AuftragMVSSiteBuilder.class }
        };
    }

    @Test(dataProvider = "provider")
    public void testSuccess(Class<AbstractCheckMVSCommand> commandClass, Class<AuftragMVSBuilder<?, ?>> builderClass)
            throws Exception {
        AuftragMVS auftragMVS = getBuilder(builderClass).build();
        auftragMVS.setAuftragId(auftrag.getAuftragId());
        getCCService(MVSService.class).saveAuftragMvs(auftragMVS);

        assertCommandExecutionYields(commandClass, ServiceCommandResult.CHECK_STATUS_OK);
    }

    @Test(dataProvider = "provider")
    public void testMissingMvsData(Class<AbstractCheckMVSCommand> commandClass,
            Class<AuftragMVSBuilder<?, ?>> ignored) throws Exception {
        assertCommandExecutionYields(commandClass, ServiceCommandResult.CHECK_STATUS_INVALID);
    }

    private void assertCommandExecutionYields(Class<AbstractCheckMVSCommand> commandClass, int status)
            throws Exception {
        AbstractCheckMVSCommand command = ((AbstractCheckMVSCommand) getBean(
                commandClass.getName()));
        command.prepare(AbstractTALCommand.KEY_AUFTRAG_ID, auftrag.getAuftragId());

        ServiceCommandResult result = (ServiceCommandResult) command.execute();
        assertEquals(result.getCheckStatus(), status);
    }

}
