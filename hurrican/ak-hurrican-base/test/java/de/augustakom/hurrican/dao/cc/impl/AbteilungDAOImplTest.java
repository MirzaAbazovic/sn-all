package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import java.util.stream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.AbteilungDAO;
import de.augustakom.hurrican.dao.cc.NiederlassungDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class AbteilungDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    AbteilungDAO abteilungDAO;

    @Autowired
    NiederlassungDAO niederlassungDAO;

    @Test
    public void testFindNL4Abteilung() throws Exception {
        List<Niederlassung> niederlassungList = abteilungDAO.findNL4Abteilung(Niederlassung.ID_MUENCHEN);
        List<Long> niederlassungIds= niederlassungList
                .stream()
                .map(abteilung -> abteilung.getId())
                .collect(Collectors.toList());
        assertTrue(niederlassungIds.contains(Niederlassung.ID_AUGSBURG));
        assertTrue(niederlassungIds.contains(Niederlassung.ID_MUENCHEN));
        assertTrue(niederlassungIds.contains(Niederlassung.ID_NUERNBERG));
        assertTrue(niederlassungIds.contains(Niederlassung.ID_MAIN_KINZIG));
        assertTrue(niederlassungIds.contains(Niederlassung.ID_ZENTRAL));
        assertFalse(niederlassungIds.contains(Niederlassung.ID_KEMPTEN));
        assertFalse(niederlassungIds.contains(Niederlassung.ID_LANDSHUT));
    }

}
