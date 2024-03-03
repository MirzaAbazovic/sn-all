
package de.mnet.hurrican.acceptance.customer;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.AccountService;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Tests Kundenauftrag-DialIn-Einzelverbindungsnachweise
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class CustomerDataUsageDetails_Test extends AbstractCustomerTestBuilder {

    @Autowired
    private AccountService accountService;

    @CitrusTest
    @Test
    public void getDataUsageDetailsStatus_Test() {
        simulatorUseCase(SimulatorUseCase.CustomerDataUsageDetails_01);

        variable("customerOrderId", "2695389");
        atlas().sendOrderDetailsCustomerOrder("getDataUsageDetailsStatus");

        variable("radiusAccountId", "@ignore@");
        variable("status", "@ignore@");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getDataUsageDetailsStatusResponse");
    }

    @CitrusTest
    @Test
    public void getDataUsageDetailsStatus_Test_Invalid_Order_Id() {
        simulatorUseCase(SimulatorUseCase.CustomerDataUsageDetails_01);

        variable("customerOrderId", "123456778789987");
        atlas().sendOrderDetailsCustomerOrder("getDataUsageDetailsStatus");

        variable("faultstring", "@ignore@");
        variable("errorCode", "HUR-001");
        variable("errorMessage", "@ignore@");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getDataUsageDetailsStatusResponse_Fault");
    }

    @CitrusTest
    @Test(enabled = false)
    public void changeDataUsageDetailsStatus_Test() throws Exception {
        // purge all JMS Queues
        atlas().purgeAllJmsQueues();
        hurrican().cleanUpTestData();

        simulatorUseCase(SimulatorUseCase.CustomerDataUsageDetails_01);

        final VerlaufTestBuilder.CreatedData data = hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);
        accountService.createAccount4Auftrag(data.auftrag.getAuftragId(), null);

        final List<IntAccount> intAccounts4Auftrag = accountService.findIntAccounts4Auftrag(data.auftragDaten.getAuftragId());
        assertNotNull(intAccounts4Auftrag);
        assertFalse(intAccounts4Auftrag.isEmpty());
        final IntAccount account = intAccounts4Auftrag.get(0);

        variable("radiusAccountId", account.getAccount());
        atlas().sendOrderDetailsCustomerOrder("activateDataUsageDetails").fork(true);

        // CPS
        cps().receiveCpsSyncRequest("cpsServiceRequest");
        variable("cpsTransactionId", 1222113);
        cps().sendCpsSyncResponse("cpsServiceResponse");

        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceResponseAck");

        // purge all JMS Queues
        atlas().purgeAllJmsQueues();
        hurrican().cleanUpTestData();
    }

}
