package de.augustakom.hurrican.dao.cc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.PortStatisticDAO;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SLOW)
public class PortStatisticDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    PortStatisticDAO portStatisticDAO;

    public void testGeneratePortUsageStatistics() throws Exception {
        portStatisticDAO.generatePortUsageStatistics();
    }

    public void testRetrievePortStatistics() throws Exception {
        portStatisticDAO.retrievePortStatistics();
    }
}