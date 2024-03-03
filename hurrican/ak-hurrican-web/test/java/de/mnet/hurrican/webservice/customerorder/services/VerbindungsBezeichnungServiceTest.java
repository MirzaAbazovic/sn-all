package de.mnet.hurrican.webservice.customerorder.services;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.hurrican.base.AbstractHurricanWebServiceTest;

/**
 * UT for {@link VerbindungsBezeichnungService}
 */
@Test(groups = SERVICE)
public class VerbindungsBezeichnungServiceTest extends AbstractHurricanWebServiceTest {

    private PhysikService physikService;
    private CCAuftragService ccAuftragService;
    private VerbindungsBezeichnungService verbindungsBezeichnungService;

    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        physikService = getCCService(PhysikService.class);
        ccAuftragService = getCCService(CCAuftragService.class);
        verbindungsBezeichnungService = new VerbindungsBezeichnungService(ccAuftragService, physikService);
    }

    @Test
    public void testFindVerbindungsBezeichnungByAuftragId() throws Exception {
        final Long taifunOrderNo = 1236720912L;
        final AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .setPersist(false)
                .withAuftragNoOrig(taifunOrderNo);

        final AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .setPersist(false)
                .withAuftragDatenBuilder(auftragDatenBuilder);

        final VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("TEST-VBZ");

        final AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .setPersist(false)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder);

        final Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .setPersist(false)
                .build();

        final AuftragDaten auftragDaten = auftragDatenBuilder.get();
        final AuftragTechnik auftragTechnik = auftragTechnikBuilder.get();

        final Auftrag createdAuftrag = ccAuftragService.createAuftrag(auftrag.getKundeNo(), auftragDaten, auftragTechnik,
                getSessionId(), null);

        flushAndClear();

        final List<VerbindungsBezeichnung> foundVbz = verbindungsBezeichnungService.getVerbindungsBezeichnung(taifunOrderNo);
        assertNotNull(foundVbz);

        final VerbindungsBezeichnung foundVerbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(
                auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVerbindungsBezeichnung, vbzBuilder.get());

        final Auftrag foundAuftrag = ccAuftragService.findAuftragById(createdAuftrag.getAuftragId());
        assertNotNull(foundAuftrag);
    }

}
