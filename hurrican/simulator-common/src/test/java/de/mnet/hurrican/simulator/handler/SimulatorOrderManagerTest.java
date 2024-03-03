package de.mnet.hurrican.simulator.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:simulator-test-context.xml" })
public class SimulatorOrderManagerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SimulatorOrderManager orderManager;

    @BeforeMethod
    public void resetOrders() {
        orderManager.orderIds.clear();
    }

    @Test
    public void testKnowsOrderId() throws Exception {
        // tests the FIFO Queue implementation:

        // adds unknown externalOrderIds into the FIFO Queue
        for (int i = 0; i < orderManager.getCacheSize(); i++) {
            Assert.assertFalse(orderManager.knowsOrderId(String.valueOf(i)));
        }

        // verifies that all IDs are now known
        for (int i = 0; i < orderManager.getCacheSize(); i++) {
            Assert.assertTrue(orderManager.knowsOrderId(String.valueOf(i)));
        }

        // verifies that when the next unknown ID is added, that the first ID ("0") inserted into the queue has been removed
        Assert.assertFalse(orderManager.knowsOrderId("unknownId"));
        Assert.assertFalse(orderManager.knowsOrderId("0"));
    }

    @Test
    public void testMissingOrderId() throws Exception {
        // adds missing externalOrderIds into the FIFO Queue
        for (int i = 0; i < orderManager.getCacheSize(); i++) {
            Assert.assertFalse(orderManager.knowsOrderId(String.valueOf(i)));
            Assert.assertTrue(orderManager.knowsOrderId(""));
            Assert.assertTrue(orderManager.knowsOrderId(null));
        }

        // verifies that all IDs are now known
        for (int i = 0; i < orderManager.getCacheSize(); i++) {
            Assert.assertTrue(orderManager.knowsOrderId(String.valueOf(i)));
            Assert.assertTrue(orderManager.knowsOrderId(""));
            Assert.assertTrue(orderManager.knowsOrderId(null));
        }

        // verifies that when the next unknown ID is added, that the first ID ("0") inserted into the queue has been removed
        Assert.assertTrue(orderManager.knowsOrderId(""));
        Assert.assertTrue(orderManager.knowsOrderId(null));
    }

}
