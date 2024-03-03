/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:35:48
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.augustakom.hurrican.model.cc.AuftragConnectBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESEinstellung;
import de.augustakom.hurrican.model.cc.EndstelleConnect.ESSchnittstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnectBuilder;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ConnectService;

@Test(groups = { "service" })
public class ConnectServiceTest extends AbstractHurricanBaseServiceTest {

    private ConnectService connectService;

    @BeforeMethod(groups = { "service" }, dependsOnMethods = { "beginTransactions" })
    public void initConnectService() {
        connectService = getCCService(ConnectService.class);
    }

    public void testSaveAuftragHousingWithBuilder() throws StoreException {
        AuftragConnect toSave = getBuilder(AuftragConnectBuilder.class).build();
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragConnect not saved.");
    }

    public void testSaveAuftragHousingWithService() throws StoreException {
        AuftragConnect toSave = getBuilder(AuftragConnectBuilder.class).setPersist(false).build();
        toSave.setAuftragId(Long.valueOf(123123));
        connectService.saveAuftragConnect(toSave);
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragConnect not saved.");
    }

    @Test(expectedExceptions = FindException.class,
            expectedExceptionsMessageRegExp = "Mehr als einen Connect-Auftrag zu diesem Auftrag gefunden.")
    public void testFindAuftragConnectByAuftragException() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragConnect auftragConnect1 = getBuilder(AuftragConnectBuilder.class)
                .withAuftragBuilder(auftragBuilder).build();
        AuftragConnect auftragConnect2 = getBuilder(AuftragConnectBuilder.class)
                .withAuftragBuilder(auftragBuilder).build();
        flushAndClear();

        assertEquals(auftragConnect1.getAuftragId(), auftragConnect2.getAuftragId(), "auftragId's must be equal for test");

        CCAuftragModel model = new AuftragDaten();
        model.setAuftragId(auftragConnect1.getAuftragId());
        connectService.findAuftragConnectByAuftrag(model);
    }

    public void testFindAuftragConnectByAuftrag() throws FindException {
        AuftragConnect auftragConnect = getBuilder(AuftragConnectBuilder.class).withProduktcode(
                "service-test").withProjektleiter("Hans Hummel").build();
        flushAndClear();

        CCAuftragModel model = new AuftragDaten();
        model.setAuftragId(auftragConnect.getAuftragId());
        AuftragConnect result = connectService.findAuftragConnectByAuftrag(model);

        assertNotNull(result, "result expected to be not null");
        assertEquals(result.getId(), auftragConnect.getId(), "id incorrect");
        assertEquals(result.getAuftragId(), auftragConnect.getAuftragId(), "auftragId incorrect");
        assertEquals(result.getProduktcode(), "service-test", "produktcode incorrect");
        assertEquals(result.getProjektleiter(), "Hans Hummel", "projektleiter incorrect");
    }

    public void testSaveEndstelleConnect() throws StoreException {
        EndstelleConnect toSave = getBuilder(EndstelleConnectBuilder.class).build();
        flushAndClear();
        assertNotNull(toSave.getId(), "EnstelleConnect not saved.");
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class,
            expectedExceptionsMessageRegExp = ".*ConstraintViolationException.*")
    public void testSaveEndstelleConnectUniqueEndstelle() throws StoreException, FindException {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        EndstelleConnect toSave1 = getBuilder(EndstelleConnectBuilder.class).withEndstelleBuilder(endstelleBuilder)
                .withSchnittstelle(ESSchnittstelle._1000BASET).setPersist(false).build();
        EndstelleConnect toSave2 = getBuilder(EndstelleConnectBuilder.class).withEndstelleBuilder(endstelleBuilder)
                .withSchnittstelle(ESSchnittstelle._10BASET).setPersist(false).build();

        connectService.saveEndstelleConnect(toSave1);
        connectService.saveEndstelleConnect(toSave2); // should fail due to unique index

        flushAndClear();
    }

    public void testSaveEndstelleConnectWithService() throws StoreException, FindException {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        EndstelleConnect toSave = getBuilder(EndstelleConnectBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withSchnittstelle(ESSchnittstelle._1000BASET).setPersist(false).build();
        connectService.saveEndstelleConnect(toSave);
        flushAndClear();
        assertNotNull(toSave.getId(), "EnstelleConnect not saved.");

        EndstelleConnect result = connectService.findEndstelleConnectByEndstelle(endstelleBuilder.get());
        assertNotNull(result, "no result");
        assertEquals(result.getEndstelleId(), endstelleBuilder.get().getId(), "endstelleId incorrect");
        assertEquals(result.getSchnittstelle(), ESSchnittstelle._1000BASET, "schnittstelle incorrect");
    }

    public void testFindEndstelleConnectByEndstelle() throws FindException {
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        EndstelleConnect endstelleConnect = getBuilder(EndstelleConnectBuilder.class)
                .withEndstelleBuilder(endstelleBuilder).withGebaude("Tower")
                .withRaum("E.98").withEinstellung(ESEinstellung.FULLDUPLEX).build();
        flushAndClear();

        EndstelleConnect result = connectService.findEndstelleConnectByEndstelle(endstelleBuilder.get());
        assertNotNull(result, "result expected to be not null");
        assertEquals(result.getId(), endstelleConnect.getId(), "id incorrect");
        assertEquals(result.getEndstelleId(), endstelleConnect.getEndstelleId(), "endstelleId incorrect");
        assertEquals(result.getGebaeude(), "Tower", "gebauede incorrect");
        assertEquals(result.getRaum(), "E.98", "raum incorrect");
        assertEquals(result.getEinstellung(), ESEinstellung.FULLDUPLEX, "einstellung incorrect");
    }
}
