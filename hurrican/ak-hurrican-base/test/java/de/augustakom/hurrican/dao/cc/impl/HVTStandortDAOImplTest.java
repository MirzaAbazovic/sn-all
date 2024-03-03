package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.HVTStandortDAO;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class HVTStandortDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    HVTStandortDAO hvtStandortDAO;

    @Test
    public void testFindHVTStandort() throws Exception {
        String onkz = "myonkz";

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOnkz(onkz);

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder);

        flushAndClear();

        HVTGruppe hvtGruppe = hvtGruppeBuilder.get();
        HVTStandort standort = standortBuilder.get();

        HVTStandort match = hvtStandortDAO.findHVTStandort(hvtGruppe.getOnkz(), standort.getAsb());
        Assert.assertEquals(match.getHvtIdStandort(), standort.getId());
        Assert.assertEquals(match.getHvtGruppeId(), hvtGruppe.getId());
        Assert.assertEquals(match.getAsb(), standort.getAsb());
        Assert.assertEquals(match.getGueltigVon(), standort.getGueltigVon());
        Assert.assertEquals(match.getGueltigBis(), standort.getGueltigBis());

    }

    @Test
    public void testFindHVTStandorteAndGruppen() throws Exception {
        String onkz = "myonkz";

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOnkz(onkz)
                .withOrt("myort");

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder);

        flushAndClear();

        HVTGruppe hvtGruppe = hvtGruppeBuilder.get();
        HVTStandort standort = standortBuilder.get();

        Pair<List<HVTStandort>, List<HVTGruppe>> result = hvtStandortDAO.findHVTStandorteAndGruppen(hvtGruppe.getOnkz(), standort.getAsb(),
                hvtGruppe.getOrtsteil(), hvtGruppe.getOrt(), standort.getStandortTypRefId(), standort.getClusterId());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFirst());
        Assert.assertNotNull(result.getSecond());
        Assert.assertEquals(result.getFirst().size(), 1);
        Assert.assertEquals(result.getSecond().size(), 1);

        result = hvtStandortDAO.findHVTStandorteAndGruppen("no_onkz", 12345,
                "notexit", "nocity", 1L, "no_cluster");

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFirst());
        Assert.assertNotNull(result.getSecond());
        Assert.assertEquals(result.getFirst().size(), 0);
        Assert.assertEquals(result.getSecond().size(), 0);
    }
}
