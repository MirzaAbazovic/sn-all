package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.testng.Assert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;

/**
 * AbstractCPSCommandTest
 */
@Test(groups = BaseTest.UNIT)
public class AbstractCPSCommandTest extends BaseTest {

    class ClassUnderTest extends AbstractCPSCommand {

        @Override
        public Object execute() throws Exception {
            return null;
        }
    }

    private ClassUnderTest cut;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        cut = new ClassUnderTest();
    }

    @DataProvider
    public Object[][] dataProviderCheckSuspendSeq() {
        // @formatter:off
       return new Object[][] {
                { CPSTransaction.SERVICE_ORDER_TYPE_UPDATE_MDU,    true  },
                { CPSTransaction.SERVICE_ORDER_TYPE_INIT_MDU,      false },
                { CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE, true  },
                { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckSuspendSeq")
    public void testCheckSuspendSeq(Long serviceOrderType, boolean expected) {
        CPSTransaction cpsTransaction = new CPSTransaction();
        cpsTransaction.setServiceOrderType(serviceOrderType);
        assertEquals(cut.checkSuspendSeq(cpsTransaction), expected);
    }

}
