package de.augustakom.hurrican.dao.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class CCAuftragDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private CCAuftragDAOImpl ccAuftragDAO;

    @Test
    public void testFindAuftragDatenByLineIdAndStatus() throws Exception {
        final VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withVbz(RandomTools.createString());
        final AuftragDaten adToFind1 = createAuftragDatenWithStatusId(vbzBuilder, AuftragStatus.TECHNISCHE_REALISIERUNG);
        final AuftragDaten adToFind2 = createAuftragDatenWithStatusId(vbzBuilder, AuftragStatus.IN_BETRIEB);
        createAuftragDatenWithStatusId(vbzBuilder, AuftragStatus.ABSAGE);

        final List<AuftragDaten> result =
                ccAuftragDAO.findAuftragDatenByLineIdAndStatus(vbzBuilder.get().getVbz(),
                        adToFind1.getAuftragStatusId(), adToFind2.getAuftragStatusId());

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(adToFind1, adToFind2));
    }

    private AuftragDaten createAuftragDatenWithStatusId(VerbindungsBezeichnungBuilder vbzBuilder, Long auftragStatusId) {
        final AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(auftragStatusId);
        getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class).withVerbindungsBezeichnungBuilder(vbzBuilder))
                .build();

        return auftragDatenBuilder.get();
    }
}