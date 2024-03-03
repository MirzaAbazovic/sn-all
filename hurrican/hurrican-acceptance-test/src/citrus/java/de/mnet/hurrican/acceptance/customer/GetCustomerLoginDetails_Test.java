
package de.mnet.hurrican.acceptance.customer;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Tests Zugangsdaten Erfassung {@link de.mnet.hurrican.webservice.customerorder.CustomerOrderServiceProvider#getCustomerLoginDetails(String)}
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GetCustomerLoginDetails_Test extends AbstractCustomerTestBuilder {

    @CitrusTest
    @Test
    public void getCustomerLoginDetails_Test_OK() {
        simulatorUseCase(SimulatorUseCase.GetCustomerLoginDetails_01);

        variable("customerOrderId", "2695389");
        atlas().sendOrderDetailsCustomerOrder("getCustomerLoginDetails");

        atlas().receiveGetOrderDetailsCustomerOrderResponse("getCustomerLoginDetailsResponse");
    }

    @CitrusTest
    @Test
    public void getCustomerLoginDetails_Test_OK_VOIP() {
        simulatorUseCase(SimulatorUseCase.GetCustomerLoginDetails_01);

        variable("customerOrderId", "2774195");   // mit voip
        atlas().sendOrderDetailsCustomerOrder("getCustomerLoginDetails");

        atlas().receiveGetOrderDetailsCustomerOrderResponse("getCustomerLoginDetailsResponse_Voip");
    }

    @CitrusTest
    @Test
    public void getCustomerLoginDetails_Test_Product_not_provided() {
        simulatorUseCase(SimulatorUseCase.GetCustomerLoginDetails_01);

        variable("customerOrderId", "2007761");
        atlas().sendOrderDetailsCustomerOrder("getCustomerLoginDetails");

        variable("faultstring", "@ignore@");
        variable("errorCode", "HUR-007");
        variable("errorMessage", "@ignore@");

        atlas().receiveGetOrderDetailsCustomerOrderResponse("getCustomerLoginDetailsResponse_Fault");
    }

    @CitrusTest
    @Test
    public void getCustomerLoginDetails_Test_Order_Not_Found() {
        simulatorUseCase(SimulatorUseCase.GetCustomerLoginDetails_01);

        variable("customerOrderId", "1123456789");
        atlas().sendOrderDetailsCustomerOrder("getCustomerLoginDetails");

        variable("faultstring", "@ignore@");
        variable("errorCode", "HUR-001");
        variable("errorMessage", "@ignore@");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getCustomerLoginDetailsResponse_Fault");
    }

    @CitrusTest
    @Test
    public void getCustomerLoginDetails_Test_Ims_Switch() {
        simulatorUseCase(SimulatorUseCase.GetCustomerLoginDetails_01);

        variable("customerOrderId", "2703838");
        atlas().sendOrderDetailsCustomerOrder("getCustomerLoginDetails");

        variable("faultstring", "@ignore@");
        variable("errorCode", "HUR-004");
        variable("errorMessage", "@ignore@");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getCustomerLoginDetailsResponse_Fault");
    }
}
