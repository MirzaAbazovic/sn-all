package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.PhysikTypDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class PhysikTypDAOImplTest  extends AbstractHurricanBaseServiceTest {

    @Autowired
    PhysikTypDAO physikTypDAO;

    @Test
    public void testFindPhysiktypen4Auftrag() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class))
                .withAuftragBuilder(auftragBuilder);

        EndstelleBuilder endstelleABuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A);

        EndstelleBuilder endstelleBBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);

        endstelleABuilder.build();
        endstelleBBuilder.build();

        Long physikTypAId = endstelleABuilder.getRangierungBuilder().get().getPhysikTypId();
        Long physikTypBId = endstelleBBuilder.getRangierungBuilder().get().getPhysikTypId();
        Long auftragId = auftragBuilder.get().getAuftragId();

        flushAndClear();

        List<Long> physiktypen4Auftrag = physikTypDAO.findPhysiktypen4Auftrag(auftragId);
        Assert.assertFalse(CollectionUtils.isEmpty(physiktypen4Auftrag));
        Assert.assertTrue(physiktypen4Auftrag.contains(physikTypAId));
        Assert.assertTrue(physiktypen4Auftrag.contains(physikTypBId));
    }
}