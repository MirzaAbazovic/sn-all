package de.augustakom.hurrican.dao.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.HWSwitchDAO;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.HWDluBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDlu;
import de.augustakom.hurrican.model.cc.hardware.HWDluView;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class HardwareDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    HardwareDAO hardwareDAO;

    @Autowired
    HWSwitchDAO hwSwitchDAO;

    @Test
    public void testFindEWSDBaugruppen() throws Exception {
        HVTTechnikBuilder technikBuilder1 = getBuilder(HVTTechnikBuilder.class).withId(HVTTechnik.SIEMENS);

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class);

        HWDluBuilder hwDluBuilder = getBuilder(HWDluBuilder.class)
                .withHvtStandortBuilder(standortBuilder)
                .withHwProducerBuilder(technikBuilder1);

        HWBaugruppenTypBuilder abBuilder = getBuilder(HWBaugruppenTypBuilder.class)
                .setAbValues()
                .withHvtTechnikBuilder(technikBuilder1);

        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(hwDluBuilder)
                .withBaugruppenTypBuilder(abBuilder);

        HWBaugruppe hwBaugruppe = hwBaugruppeBuilder.get();
        HWBaugruppenTyp hwBaugruppenTyp = abBuilder.get();
        HWDlu hwDlu = hwDluBuilder.get();
        HVTStandort hvtStandort = standortBuilder.get();

        flushAndClear();

        List<HWDluView> dluViews = hardwareDAO.findEWSDBaugruppen(hvtStandort.getId(), false);

        Assert.assertEquals(dluViews.size(), 1);
        HWDluView hwDluView = dluViews.get(0);
        Assert.assertEquals(hwDluView.getBaugruppenId(), hwBaugruppe.getId());
        Assert.assertEquals(hwDluView.getBgTyp(), hwBaugruppenTyp.getName());
        Assert.assertEquals(hwDluView.getDluNumber(), hwDlu.getDluNumber());
        Assert.assertEquals(hwDluView.getModNumber(), hwBaugruppe.getModNumber());
        Assert.assertEquals(hwDluView.getHvtIdStandort(), hvtStandort.getId());
    }

    @Test
    public void testCreateSwitchMigrationViews() throws Exception {
        HWSwitch hwSwitch = hwSwitchDAO.findSwitchByName(HWSwitch.SWITCH_MUC06);
        Date inbetriebnahme = new Date();
        SwitchMigrationSearchCriteria criteria = new SwitchMigrationSearchCriteria();
        criteria.setInbetriebnahmeVon(inbetriebnahme);
        criteria.setInbetriebnahmeBis(inbetriebnahme);
        criteria.setHwSwitch(hwSwitch);
        List<SwitchMigrationView> views = hardwareDAO.createSwitchMigrationViews(criteria);
        int numberOfExitingViews = views.size();

        String ortsteil = "some loc";

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil(ortsteil);

        HVTStandortBuilder standortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder);

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withHvtStandortBuilder(standortBuilder);

        AuftragDatenBuilder auftragDatenBuilder =
                getBuilder(AuftragDatenBuilder.class)
                        .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                        .withInbetriebnahme(inbetriebnahme);

        getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder)
                .withHwSwitch(hwSwitch)
                .build();

        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        views = hardwareDAO.createSwitchMigrationViews(criteria);
        Assert.assertEquals(views.size(), 1 + numberOfExitingViews);

        for (SwitchMigrationView view : views) {
            if (view.getAuftragId().equals(auftragDaten.getAuftragId())) {
                Assert.assertEquals(view.getAuftragId(), auftragDaten.getAuftragId());
                Assert.assertEquals(view.getAuftragStatusId(), auftragDaten.getStatusId());
                Assert.assertEquals(view.getProdId(), auftragDaten.getProdId());
                Assert.assertEquals(view.getTechLocation(), ortsteil);
                Assert.assertEquals(view.getBillingAuftragId(), auftragDaten.getAuftragNoOrig());
                Assert.assertEquals(view.getTechLocation(), ortsteil);
                Assert.assertEquals(view.getSwitchKennung(), HWSwitch.SWITCH_MUC06);

                return;
            }
        }

        Assert.fail("Missing switch migration view for auftragId: " + auftragDaten.getAuftragId());
    }

    @Test
    public void testFindHwOltForRack() throws Exception {
        final HardwareHierarchy hardwareHierarchy = createHardwareHierarchy();
        final HWOlt result = hardwareDAO.findHwOltForRack(hardwareHierarchy.dpo);
        assertThat(result, equalTo(hardwareHierarchy.olt));
    }

    @Test
    public void testFindHwRack4EqInofRangierung() throws Exception {
        final HardwareHierarchy hardwareHierarchy = createHardwareHierarchy();
        final HWRack result = hardwareDAO.findRack4EqInOfRangierung(hardwareHierarchy.rangierung.getId());
        assertThat(result, equalTo(hardwareHierarchy.dpo));
    }

    private HardwareHierarchy createHardwareHierarchy()  {
        final HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class);
        final HWDpoBuilder dpoBuilder = getBuilder(HWDpoBuilder.class)
                .withHWRackOltBuilder(oltBuilder);
        final HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(dpoBuilder);
        final EquipmentBuilder eqInBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder);
        final Rangierung rangierung = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(eqInBuilder)
                .build();

        return new HardwareHierarchy(oltBuilder.get(), dpoBuilder.get(), rangierung);
    }

    private static final class HardwareHierarchy    {
        final HWOlt olt;
        final HWDpo dpo;
        final Rangierung rangierung;

        public HardwareHierarchy(HWOlt olt, HWDpo dpo, Rangierung rangierung) {
            this.olt = olt;
            this.dpo = dpo;
            this.rangierung = rangierung;
        }
    }
}
