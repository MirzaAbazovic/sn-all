package de.augustakom.hurrican.dao.cc.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;

@Test(groups = BaseTest.SERVICE)
public class VerlaufAbteilungDAOImplTest extends AbstractVerlaufBaseDaoTest {

    @Test
    public void testUpdateStatus() throws Exception {
        Long initialStatus = VerlaufStatus.BEI_DISPO;
        Long updatedStatus = VerlaufStatus.BEI_TECHNIK;

        Verlauf verlauf = createTestVerlauf();
        VerlaufAbteilung verlaufAbteilung = createTestVerlaufAbteilung(verlauf.getId(), initialStatus, Abteilung.DISPO);
        verlaufAbteilungDAO.flushSession();

        verlaufAbteilung = verlaufAbteilungDAO.findById(verlaufAbteilung.getId(), VerlaufAbteilung.class);
        Assert.assertNotNull(verlaufAbteilung);
        Assert.assertEquals(verlaufAbteilung.getVerlaufStatusId(), initialStatus);

        int updatedRecords = verlaufAbteilungDAO.updateStatus(verlauf.getId(), updatedStatus);
        Assert.assertEquals(updatedRecords, 1);
        verlaufAbteilungDAO.flushSession();

        verlaufAbteilungDAO.refresh(verlaufAbteilung);
        Assert.assertEquals(verlaufAbteilung.getVerlaufStatusId(), updatedStatus);
    }

    @Test
    public void testDeleteVerlaufAbteilung() throws Exception {
        Verlauf verlauf = createTestVerlauf();
        createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.BEI_DISPO, Abteilung.DISPO);
        createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.BEI_DISPO, Abteilung.EXTERN);

        flushAndClear();

        int deletedVAs;
        deletedVAs = verlaufAbteilungDAO.deleteVerlaufAbteilung(verlauf.getId(), true);
        Assert.assertEquals(deletedVAs, 1); // only verlaufAbteilung with Abteilung.EXTERN should be deleted

        flushAndClear();

        deletedVAs = verlaufAbteilungDAO.deleteVerlaufAbteilung(verlauf.getId(), false);
        Assert.assertEquals(deletedVAs, 1); // also the verlaufAbteilung with Abteilung.DISPO should be deleted

        flushAndClear();

        Assert.assertEquals(verlaufAbteilungDAO.findVerlaufAbteilungen(verlauf.getId()).size(), 0);
    }


}