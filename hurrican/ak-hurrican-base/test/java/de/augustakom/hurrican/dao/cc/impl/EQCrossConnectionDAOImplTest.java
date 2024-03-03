package de.augustakom.hurrican.dao.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.EQCrossConnectionDAO;
import de.augustakom.hurrican.model.cc.BrasPoolBuilder;
import de.augustakom.hurrican.model.cc.EQCrossConnectionBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class EQCrossConnectionDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    EQCrossConnectionDAO cut;

    public void testFindUsedBrasVcs() throws Exception {
        final BrasPoolBuilder brasPoolBuilder = getBuilder(BrasPoolBuilder.class)
                .withName("test")
                .withVcMax(10)
                .withVcMin(1)
                .withVp(1234);

        getBuilder(EQCrossConnectionBuilder.class)
                .withBrasOuter(1234)
                .withBrasInner(1)
                .withBrasPoolBuilder(brasPoolBuilder)
                .withValidFrom(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withValidTo(DateTools.getHurricanEndDate())
                .build();

        final List<Integer> result = cut.findUsedBrasVcs(brasPoolBuilder.get());

        assertThat(result, hasSize(1));
        assertTrue(result.contains(1), "result muss Vc mit Wert 1 enthalten da dies auf einer Crossconnection verwendet wird");
    }
}
