package de.mnet.wita.acceptance;

import static de.mnet.wita.WitaSimulatorTestUser.*;
import static org.testng.Assert.assertEquals;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.common.role.WorkflowTestRole;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.SendWitaBereitstellungTestAction;

/**
 * Test, um die neue Zeitfenster-Logik (ab 24.02.2014) zu ueberpruefen.
 */
@Test(groups = BaseTest.ACCEPTANCE_INTEGRATION)
public class WitaZeitfenster_AccTest extends AbstractWitaAcceptanceTest {
    private static final Logger LOGGER = Logger.getLogger(WitaZeitfenster_AccTest.class);
    @CitrusTest(name="testZfLogicForBereitstellung")
    public void testZfLogicForBereitstellung() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_ZF_LOGIC_SINCE_FEB_2014_NEU, WitaCdmVersion.V1);

        workflow().select(workflow().get()).sendBereitstellung(TAL_ZF_LOGIC_SINCE_FEB_2014_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();
    }

    @CitrusTest(name="testZfLogicForKuendigungKunde")
    public void testZfLogicForKuendigungKunde() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, WitaCdmVersion.V1);
        WorkflowTestRole testRole = workflow().select(workflow().get());
        testRole.sendBereitstellung(TAL_KUENDIGUNG_KUE_KD_01_NEUBESTELLUNG);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");
        atlas().sendNotification("QEB");
        workflow().waitForQEB();
        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        atlas().sendNotification("ABM");
        workflow().waitForABM();
        atlas().sendNotification("ERLM");
        workflow().waitForERLM();
        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> {wf.closeCbVorgang();
        });

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        useCase(WitaAcceptanceUseCase.TAL_ZF_LOGIC_SINCE_FEB_2014_KUEKD, WitaCdmVersion.V1);
        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 14).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withUserName(TAL_ZF_LOGIC_SINCE_FEB_2014_KUEKD);
        workflow().select(workflow2).send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();
    }

}
