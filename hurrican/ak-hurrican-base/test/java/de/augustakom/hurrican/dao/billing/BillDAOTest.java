package de.augustakom.hurrican.dao.billing;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.view.BillRunView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class BillDAOTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private BillDAO billDAO;

    @Test
    public void testFindBillRunViews() throws Exception {
        List<BillRunView> billRunViews = billDAO.findBillRunViews();
        assertNotEmpty(billRunViews);
        assertTrue(billRunViews.get(0).getRunNo() > billRunViews.get(1).getRunNo());
    }
}