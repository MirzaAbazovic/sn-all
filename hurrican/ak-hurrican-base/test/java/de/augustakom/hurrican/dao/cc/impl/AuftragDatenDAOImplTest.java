package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class AuftragDatenDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private AuftragDatenDAOImpl auftragDatenDAO;

    @Test
    public void testFindAuftragDatenByBaugruppe() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("#VBZ#"))
                .withHwSwitch(getHwSwitch())
                .withAuftragBuilder(auftragBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelle(Endstelle.ENDSTELLEN_TYP_B)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);
        HVTTechnikBuilder hvtTechnikBuilder = getBuilder(HVTTechnikBuilder.class)
                .withCpsName("ALCATEL_LUCENT")
                .withHersteller("Alcatel");
        HWOltBuilder oltBuilder = getBuilder(HWOltBuilder.class)
                .withHwProducerBuilder(hvtTechnikBuilder)
                .withGeraeteBez("OLT-400003");
        HWOntBuilder ontBuilder = getBuilder(HWOntBuilder.class)
                .withHWRackOltBuilder(oltBuilder)
                .withSerialNo("A-1-B2-C3-0815")
                .withGeraeteBez("ONT-404453");
        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class)
                .withRackBuilder(ontBuilder);
        EquipmentBuilder ontEquipmentBuilder = getBuilder(EquipmentBuilder.class)
                .withBaugruppeBuilder(hwBaugruppeBuilder)
                .withRangBucht("01.-001");
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withEqInBuilder(ontEquipmentBuilder);

        assert hwBaugruppeBuilder.getId() == null;
        rangierungBuilder.build();

        flushAndClear();

        List<AuftragDaten> auftragDatenByBaugruppe = auftragDatenDAO.findAuftragDatenByBaugruppe(hwBaugruppeBuilder.get().getId());
        assertEquals(auftragDatenByBaugruppe.size(), 1);
        assertEquals(auftragDatenByBaugruppe.get(0).getAuftragId(), auftragBuilder.get().getAuftragId());

        final AuftragTechnik auftragTechnik = auftragDatenDAO.findById(auftragTechnikBuilder.get().getId(), AuftragTechnik.class);
        assertNotNull(auftragTechnik.getHwSwitch());
        assertNotNull(auftragTechnik.getHwSwitch().getName());
    }

    public void testFindAuftragDatenByAuftragNoOrigAndBuendelNo() {
        Long auftragNoOrig = 123L;
        Integer buendelNo = 123;
        getBuilder(AuftragDatenBuilder.class).withBuendelNr(buendelNo).withAuftragNoOrig(auftragNoOrig).build();

        List<AuftragDaten> current = auftragDatenDAO.findAuftragDatenByAuftragNoOrigAndBuendelNo(auftragNoOrig, buendelNo);
        assertNotNull(current);
        assertEquals(current.size(), 1);
    }

    private HWSwitch getHwSwitch() {
        final List<HWSwitch> hwSwitches = auftragDatenDAO.findAll(HWSwitch.class);
        assertNotEmpty(hwSwitches);
        assertTrue(hwSwitches.size() >= 1);
        return hwSwitches.iterator().next();
    }
}
