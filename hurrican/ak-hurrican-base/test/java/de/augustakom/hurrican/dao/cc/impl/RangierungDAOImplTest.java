package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWSwitchService;

@Test(groups = BaseTest.SERVICE)
public class RangierungDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    RangierungDAO rangierungDAO;

    @Test
    public void testFreigabeRangierung() throws Exception {
        Rangierung rangierung = createTestRangierung();
        assertEquals(rangierungDAO.freigabeRangierung(rangierung.getId()), 1);
    }

    @Test
    public void testFindRangierungWithEquipmentViews() throws Exception {
        String hwEQN1 = "1-03-04-01";
        String dslamBez1 = "DSL0001-ABC";
        String hwEQN2 = "1-03-04-02";
        String dslamBez2 = "DSL0002-ABC";

        Rangierung rangierung1 = createTestRangierung(hwEQN1, dslamBez1);
        Rangierung rangierung2 = createTestRangierung(hwEQN2, dslamBez2);
        List<RangierungWithEquipmentView> views = rangierungDAO.findRangierungWithEquipmentViews(new HashSet<>(Arrays.asList(rangierung1.getId(), rangierung2.getId())));
        assertEquals(views.size(), 2);
        for (RangierungWithEquipmentView view :views) {
            assertNotNull(view.getAuftragId());
            assertNotNull(view.getEndstelleId());
            assertNotNull(view.getBgTyp());
            assertTrue(view.getHwEqn().equals(hwEQN1) || view.getHwEqn().equals(hwEQN2));
        }
    }

    private Rangierung createTestRangierung() throws FindException {
        return createTestRangierung("1-03-04-01", "DSL0001-ABC");
    }

    private Rangierung createTestRangierung(String hwEQN, String dslamBez) throws FindException {
        HWBaugruppeBuilder hwBaugruppeBuilder =
                getBuilder(HWBaugruppeBuilder.class)
                        .withRackBuilder(
                                getBuilder(HWDslamBuilder.class)
                                        .withGeraeteBez(dslamBez)
                        );
        EquipmentBuilder equipmentBuilder =
                getBuilder(EquipmentBuilder.class)
                        .withHwEQN(hwEQN)
                        .withBaugruppeBuilder(hwBaugruppeBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        Rangierung rangierung =
                getBuilder(RangierungBuilder.class)
                        .withEndstelleBuilder(endstelleBuilder)
                        .withEqInBuilder(equipmentBuilder)
                        .build();
        AuftragDatenBuilder auftragDatenBuilder =
                getBuilder(AuftragDatenBuilder.class)
                        .withAuftragBuilder(getBuilder(AuftragBuilder.class));
        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder)
                .build();
        return rangierung;
    }

}
