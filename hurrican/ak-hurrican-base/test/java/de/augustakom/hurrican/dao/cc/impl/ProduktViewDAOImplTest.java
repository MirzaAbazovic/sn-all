package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.ProduktViewDAO;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class ProduktViewDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    ProduktViewDAO produktViewDAO;

    @Test
    public void testFindProduktGruppen4Hurrican() throws Exception {
        List<ProduktGruppe> hurricanPGs = produktViewDAO.findProduktGruppen4Hurrican();
        if (CollectionUtils.isNotEmpty(hurricanPGs)) {
            for (ProduktGruppe pg : hurricanPGs) {
                Assert.assertNotNull(pg.getId());
            }
        }
        else {
            Assert.assertNull(hurricanPGs);
        }
    }
}