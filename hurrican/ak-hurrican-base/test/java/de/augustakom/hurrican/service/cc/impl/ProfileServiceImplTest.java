package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.ProfileService;

@Test(groups = BaseTest.SERVICE)
public class ProfileServiceImplTest  extends AbstractHurricanBaseServiceTest {

    @Autowired
    private ProfileService profileService;

    public void findProfileAuftrags_ProfilesAvailable_ReturnsAuftragsList() {
        final DSLAMProfileChangeReason changeReason =
                profileService.getChangeReasonById(DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT);

        final long auftragID = 1L;
        final ProfileAuftrag profileAuftrag = createProfileAuftrag(changeReason, auftragID);

        profileService.persistProfileAuftrag(profileAuftrag);

        List<ProfileAuftrag> result = profileService.findProfileAuftrags(auftragID);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0) != null);
        assertTrue(result.get(0).getAuftragId() == auftragID);
    }

    private ProfileAuftrag createProfileAuftrag(DSLAMProfileChangeReason changeReason, long auftragID) {
        final ProfileAuftrag profileAuftrag = new ProfileAuftrag();
        profileAuftrag.setAuftragId(auftragID);
        profileAuftrag.setEquipmentId(1L);
        profileAuftrag.setGueltigBis(new Date());
        profileAuftrag.setGueltigVon(new Date());
        profileAuftrag.setUserW("TestUser");
        profileAuftrag.setChangeReason(changeReason);
        return profileAuftrag;
    }

}