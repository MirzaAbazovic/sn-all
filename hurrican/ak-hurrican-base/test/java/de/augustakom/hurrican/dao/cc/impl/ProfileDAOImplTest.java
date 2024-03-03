package de.augustakom.hurrican.dao.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.ProfileDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileAuftragValue;
import de.augustakom.hurrican.model.cc.ProfileParameter;
import de.augustakom.hurrican.model.cc.ProfileParameterBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = BaseTest.SERVICE)
public class ProfileDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    ProfileDAO profileDAO;

    @Test
    public void findProfileParameterDefaults() {
        final HWBaugruppenTypBuilder hwBaugruppenTyp = getBuilder(HWBaugruppenTypBuilder.class);
        final ProfileParameter defaultParam = getBuilder(ProfileParameterBuilder.class)
                .withIsDefault(true)
                .withHWBaugruppenTypBuilder(hwBaugruppenTyp)
                .build();
        final ProfileParameter nonDefaultParam = getBuilder(ProfileParameterBuilder.class)
                .withIsDefault(false)
                .withHWBaugruppenTypBuilder(hwBaugruppenTyp)
                .build();

        final List<ProfileParameter> result = profileDAO.findProfileParameterDefaults(hwBaugruppenTyp.get().getId());
        assertThat(result, contains(defaultParam));
        assertThat(result, not(contains(nonDefaultParam)));
        assertThat(result, iterableWithSize(1));
    }

    @Test
    public void testFindProfileParameters_NotFound() throws Exception {
        final List<ProfileParameter> profileParameters = profileDAO.findProfileParameters(123456799L);
        Assert.assertNotNull(profileParameters);
        Assert.assertTrue(profileParameters.isEmpty());
    }

    @Test
    public void testFindProfileParameters() throws Exception {
        final HWBaugruppenTyp hwBaugruppenTyp = profileDAO.findById(20L, HWBaugruppenTyp.class);
        Assert.assertNotNull(hwBaugruppenTyp);
        final ProfileParameter profileParameter = new ProfileParameter(hwBaugruppenTyp, "param_name",
                "param_value", false);
        profileDAO.store(profileParameter);

        final List<ProfileParameter> profileParameters = profileDAO.findProfileParameters(hwBaugruppenTyp.getId());
        Assert.assertNotNull(profileParameters);
        Assert.assertFalse(profileParameters.isEmpty());
    }

    @Test
    public void testFindProfileAuftrag_WithValues() throws Exception {
        final long auftragId = 774067L;  // muss exestieren wegen FK constraint

        final ProfileAuftrag pa = new ProfileAuftrag();
        pa.setAuftragId(auftragId);
        pa.setBemerkung("bem");
        pa.setEquipmentId(123L);
        final DSLAMProfileChangeReason dslamProfileChangeReason =
                profileDAO.findById(DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT, DSLAMProfileChangeReason.class);
        pa.setChangeReason(dslamProfileChangeReason);
        pa.setGueltigVon(DateConverterUtils.asDate(LocalDate.now().minusDays(1)));
        pa.setGueltigBis(DateConverterUtils.asDate(LocalDate.now().plusDays(2)));
        pa.setUserW("utester");


        pa.getProfileAuftragValues().add(new ProfileAuftragValue("param", "value"));
        final ProfileAuftrag storedPa = profileDAO.store(pa);
        flushAndClear();

        final Optional<ProfileAuftrag> profileAuftragOpt = profileDAO.findProfileAuftrag(auftragId, Calendar.getInstance().getTime());
        Assert.assertTrue(profileAuftragOpt.isPresent());
        Assert.assertEquals(profileAuftragOpt.get().getProfileAuftragValues().size(), 1);
    }

    @Test
    public void testFindProfileAuftrag_NotFound() throws Exception {
        final Optional<ProfileAuftrag> profileAuftragOpt = profileDAO.findProfileAuftrag(123456799L, Calendar.getInstance().getTime());
        Assert.assertFalse(profileAuftragOpt.isPresent());
    }


    @Test
    public void testFindParameterMapper_NotFound() throws Exception {
        Assert.assertNull(profileDAO.findParameterMapper("not_existent_param_name"));
    }

    @Test
    public void testFindHWBaugruppenByAuftragId() throws Exception {
        final List<HWBaugruppe> hwBaugruppenByAuftragId = profileDAO.findHWBaugruppenByAuftragId(362386L);
        Assert.assertNotNull(hwBaugruppenByAuftragId);
        Assert.assertFalse(hwBaugruppenByAuftragId.isEmpty());
    }

    @Test
    public void testFindHWBaugruppenByAuftragId_NotFound() throws Exception {
        final List<HWBaugruppe> hwBaugruppenByAuftragId = profileDAO.findHWBaugruppenByAuftragId(0L);
        Assert.assertNotNull(hwBaugruppenByAuftragId);
        Assert.assertTrue(hwBaugruppenByAuftragId.isEmpty());
    }

    @Test
    public void testFindEquipmentsByAuftragId() throws Exception {
        final Equipment equipment = profileDAO.findEquipmentsByAuftragId(362386L);
        Assert.assertNotNull(equipment);
    }

}