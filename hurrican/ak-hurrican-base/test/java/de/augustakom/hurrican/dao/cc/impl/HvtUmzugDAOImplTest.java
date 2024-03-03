package de.augustakom.hurrican.dao.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIn.isIn;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.HvtUmzugDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HvtUmzugBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class HvtUmzugDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    HvtUmzugDAO hvtUmzugDAO;

    @Test
    public void testFindAuftraegeAndEsTypForHvtUmzug() throws Exception {
        Pair<HvtUmzug, Auftrag> data = createTestData();

        List<Pair<Long, String>> result = hvtUmzugDAO.findAuftraegeAndEsTypForHvtUmzug(data.getFirst().getId());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);

        Assert.assertEquals(result.get(0).getFirst(), data.getSecond().getAuftragId());
        Assert.assertEquals(result.get(0).getSecond(), Endstelle.ENDSTELLEN_TYP_B);
    }


    private Pair<HvtUmzug, Auftrag> createTestData() {
        String kvzNr = "A999";
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdBuilder(getBuilder(ProduktBuilder.class));
        auftragDatenBuilder.build();

        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(getBuilder(HVTGruppeBuilder.class));

        AuftragTechnik2EndstelleBuilder at2esBuilder = getBuilder(AuftragTechnik2EndstelleBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAuftragTechnik2EndstelleBuilder(at2esBuilder);
        auftragTechnikBuilder.build();

        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class)
                .withKvz(kvzNr);
        GeoId2TechLocationBuilder geoId2TechLocationBuilder = getBuilder(GeoId2TechLocationBuilder.class)
                .withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(hvtStandortBuilder);
        geoId2TechLocationBuilder.build();

        EndstelleBuilder endstelle = getBuilder(EndstelleBuilder.class)
                .withEndstelleGruppeBuilder(at2esBuilder)
                .withGeoIdBuilder(geoIdBuilder)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqOutBuilder(getBuilder(EquipmentBuilder.class)
                                .withDtagValues()));
        endstelle.build();

        HvtUmzugBuilder hvtUmzugBuilder = getBuilder(HvtUmzugBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder)
                .withKvzNr(kvzNr);
        return Pair.create(hvtUmzugBuilder.build(), auftragBuilder.get());
    }

    public void testFindHvtUmzuegeWithStatus() throws Exception {
        final HvtUmzug hvtUmzugToFind = getBuilder(HvtUmzugBuilder.class)
                .withStatus(HvtUmzugStatus.BEENDET)
                .build();
        final HvtUmzug hvtUmzugNotToFind = getBuilder(HvtUmzugBuilder.class)
                .withStatus(HvtUmzugStatus.OFFEN)
                .build();

        final List<HvtUmzug> result = hvtUmzugDAO.findHvtUmzuegeWithStatus(hvtUmzugToFind.getStatus());

        assertThat(hvtUmzugToFind, isIn(result));
        assertThat(hvtUmzugNotToFind, not(isIn(result)));
    }

}
