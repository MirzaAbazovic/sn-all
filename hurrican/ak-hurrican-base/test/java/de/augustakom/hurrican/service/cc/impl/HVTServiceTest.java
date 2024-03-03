/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 13:30:56
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.HVTStandortTechTypeBuilder;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.KvzAdresseBuilder;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;
import de.augustakom.hurrican.model.cc.UEVTBuilder;
import de.augustakom.hurrican.model.cc.query.HVTQuery;
import de.augustakom.hurrican.model.cc.view.HVTGruppeStdView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * TestNG fuer eine Implementierung von <code>HVTService</code>.
 */
@Test(groups = { BaseTest.SERVICE })
public class HVTServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(HVTServiceTest.class);

    @Test(enabled = true)
    public void testFindUEVTByHvtUevtAndKvz() throws FindException {
        UEVT uevt = getBuilder(UEVTBuilder.class)
                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class))
                .withUevt("0101")
                .build();

        UEVT result = getHVTService().findUEVT(uevt.getHvtIdStandort(), uevt.getUevt());
        assertNotNull(result, "UEVT wurde nicht gefunden!");
    }


    @Test(enabled = false)
    public void testFindHVTViews() throws FindException {
        HVTQuery query = new HVTQuery();
        //query.setOnkz("0821");
        query.setOrt("Augsburg");

        HVTService service = getHVTService();
        List<HVTGruppeStdView> result = service.findHVTViews(query);
        assertNotEmpty(result, "Keine HVT-Views gefunden!");

        for (HVTGruppeStdView view : result) {
            LOGGER.debug(view.getName());
        }
    }

    @Test(enabled = false)
    public void testFindHVTGruppeById() throws FindException {
        HVTService service = getHVTService();
        Long id = 1L;
        HVTGruppe gruppe = service.findHVTGruppeById(id);
        assertNotNull(gruppe, "HVTGruppe mit ID " + id + " nicht gefunden!");
        gruppe.debugModel(LOGGER);
    }

    @Test(enabled = false)
    public void testFindHVTStandortByOnkzAndASB() throws FindException {
        HVTService service = getHVTService();
        HVTStandort result = service.findHVTStandort("08331", 2);

        assertNotNull(result, "HVT-Standort konnte nicht ermittelt werden!");
        result.debugModel(LOGGER);
    }

    @Test(enabled = false)
    public void testFindHVTStandorte4Gruppe() throws FindException {
        HVTService service = getHVTService();
        Long id = 1L;
        List<HVTStandort> result = service.findHVTStandorte4Gruppe(id, false);
        assertNotEmpty(result, "Keine HVT-Standorte fuer HVT-Gruppe " + id + " gefunden!");
        LOGGER.debug("Anzahl HVT-Standorte fuer HVT-Gruppe " + id + ": " + result.size());
        for (HVTStandort aResult : result) {
            if (aResult != null) {
                aResult.debugModel(LOGGER);
            }
        }

        Long noID = 999999L;
        List<HVTStandort> shouldBeEmpty = service.findHVTStandorte4Gruppe(noID, false);
        assertEmpty(shouldBeEmpty, "HVT-Standorte wurden fuer eine Gruppe gefunden, die nicht existiert!");
    }

    @Test(enabled = false)
    public void testFindHVTStandorte() throws FindException {
        HVTService service = getHVTService();
        HVTStandort example = new HVTStandort();
        example.setVirtuell(Boolean.TRUE);
        List<HVTStandort> result = service.findHVTStandorte(example);
        assertNotEmpty(result, "Keine virtuellen HVT-Standorte gefunden!!!");
    }

    @Test(enabled = false)
    public void testFindUEVTs4HVTStandort() throws FindException {
        HVTService service = getHVTService();
        Long hvtId = 1L;
        List<UEVT> result = service.findUEVTs4HVTStandort(hvtId);
        assertNotEmpty(result, "Es wurden keine UEVTs gefunden!");
    }

    @Test(enabled = false)
    public void testFindUEVTZiele() throws FindException {
        HVTService service = getHVTService();
        Long uevtId = 1L;
        List<UEVT2Ziel> result = service.findUEVTZiele(uevtId);
        assertNotEmpty(result, "Es wurden keine UEVT-Ziele fuer den UEVT " + uevtId + " gefunden!");
    }

    public void testSaveUEVT() throws StoreException {
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        HVTService service = getHVTService();
        UEVT uevt = new UEVT();
        uevt.setHvtIdStandort(hvtStandort.getId());
        uevt.setUevt("0000");

        service.saveUEVT(uevt, null);
    }

    public void testSaveTechType() throws StoreException, FindException {
        Reference technologyTypeRef = getBuilder(ReferenceBuilder.class)
                .withType("TECHNOLOGY_TYPE")
                .build();
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        HVTStandortTechType hvtStandortTechType = getBuilder(HVTStandortTechTypeBuilder.class)
                .withHvtIdStandort(hvtStandort.getHvtIdStandort())
                .withTechnologyTypeReference(technologyTypeRef)
                .setPersist(false)
                .build();

        HVTService service = getHVTService();
        service.saveTechType(hvtStandortTechType, -1L);

        // Test insert erfolgreich?
        assertNull(hvtStandortTechType.getAvailableFrom(), "Datum Verfügbarkeit bereits gueltig!");
        assertNotNull(hvtStandortTechType.getId(), "Insert Statement ist fehlgeschlagen!");

        hvtStandortTechType.setAvailableFrom(new Date());
        service.saveTechType(hvtStandortTechType, -1L);
        List<HVTStandortTechType> result = service.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());

        // Test update erfolgreich?
        assertFalse(CollectionTools.isEmpty(result), "Update Statement ist fehlgeschlagen!");
        assertTrue(result.size() == 1, "Die Anzahl der Technologietypen in der Liste weicht vom erwarteten Wert ab!");
        assertNotNull(result.get(0).getAvailableFrom(), "Datum Verfügbarkeit immer noch 'null'!");
    }

    public void testDeleteTechType() throws DeleteException, FindException {
        Reference technologyTypeRef = getBuilder(ReferenceBuilder.class)
                .withType("TECHNOLOGY_TYPE")
                .build();
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        HVTStandortTechType hvtStandortTechType = getBuilder(HVTStandortTechTypeBuilder.class)
                .withHvtIdStandort(hvtStandort.getHvtIdStandort())
                .withTechnologyTypeReference(technologyTypeRef)
                .build();

        HVTService service = getHVTService();
        service.deleteTechType(hvtStandortTechType);
        List<HVTStandortTechType> result = service.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());

        assertTrue(CollectionTools.isEmpty(result), "Delete Statement ist fehlgeschlagen!");
    }

    public void testFindTechTypes4HVTStandort() throws DeleteException, FindException {
        Reference techTypeRefOne = getBuilder(ReferenceBuilder.class)
                .withType("TECHNOLOGY_TYPE")
                .build();
        Reference techTypeRefTwo = getBuilder(ReferenceBuilder.class)
                .withType("TECHNOLOGY_TYPE")
                .build();

        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        getBuilder(HVTStandortTechTypeBuilder.class)
                .withHvtIdStandort(hvtStandort.getHvtIdStandort())
                .withTechnologyTypeReference(techTypeRefOne)
                .build();
        getBuilder(HVTStandortTechTypeBuilder.class)
                .withHvtIdStandort(hvtStandort.getHvtIdStandort())
                .withTechnologyTypeReference(techTypeRefTwo)
                .build();

        HVTService service = getHVTService();
        List<HVTStandortTechType> result = service.findTechTypes4HVTStandort(hvtStandort.getHvtIdStandort());

        assertFalse(CollectionTools.isEmpty(result), "Die Technologietypen sind nicht wie erwartet in der Liste!");
        assertTrue(result.size() == 2, "Die Anzahl der Technologietypen in der Liste weicht vom erwarteten Wert ab!");
    }

    public void testFindHVTGruppeByBezeichnung() throws FindException {
        HVTGruppe result = getHVTService().findHVTGruppeByBezeichnung("xyz");
        assertNull(result, "Query with not existing name should return NULL, not an object!");
    }

    public void testFindKvzAdresse() throws FindException {
        KvzAdresse kvzAdresse = getBuilder(KvzAdresseBuilder.class).build();

        KvzAdresse kvzAdresseFound = getHVTService().findKvzAdresse(kvzAdresse.getHvtStandortId(),
                kvzAdresse.getKvzNummer());

        assertEquals(kvzAdresseFound.getId(), kvzAdresse.getId());
        assertEquals(kvzAdresseFound.getHvtStandortId(), kvzAdresse.getHvtStandortId());
        assertEquals(kvzAdresseFound.getKvzNummer(), kvzAdresse.getKvzNummer());
    }

    @Test(expectedExceptions = { StoreException.class })
    public void testSaveKvzAdresseNoKvzFttcStandortTyp() throws StoreException {
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .build();
        KvzAdresse kvzAdresse = new KvzAdresseBuilder().build();
        kvzAdresse.setHvtStandortId(hvtStandort.getHvtIdStandort());
        getHVTService().saveKvzAdresse(kvzAdresse);
    }

    @Test
    public void testCreateAndFindKvzSperre() throws Exception {
        final String kvzNummer = "KVZXX";
        final String comment = "my test kvz_sperre";
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        HVTService service = getHVTService();

        service.createKvzSperre(hvtStandort.getId(), kvzNummer, comment);

        final GeoId geoId = getBuilder(GeoIdBuilder.class).withKvz(kvzNummer).build();
        final KvzSperre kvzSperre = service.findKvzSperre(hvtStandort.getId(), geoId.getId());
        assertNotNull(kvzSperre);
        assertEquals(kvzSperre.getKvzNummer(), kvzNummer);
        assertEquals(kvzSperre.getAsb(), hvtStandort.getDTAGAsb());
        assertEquals(kvzSperre.getOnkz(), service.findHVTGruppeById(hvtStandort.getHvtGruppeId()).getOnkz());

        final Long kvzSperreId = kvzSperre.getId();
        service.deleteKvzSperre(kvzSperreId);
        assertNull(service.findKvzSperre(hvtStandort.getId(), geoId.getId()));
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void testCreateNotValidKvzSperre() throws Exception {
        final String toLongKvzNumber = "KVZXXXXXXXXXXXXXXXXXXX";
        HVTStandort hvtStandort = getBuilder(HVTStandortBuilder.class).build();
        HVTService service = getHVTService();
        service.createKvzSperre(hvtStandort.getId(), toLongKvzNumber, null);
        flushAndClear();
    }

    /* Gibt einen HVT-Service zurueck. */
    private HVTService getHVTService() {
        return getCCService(HVTService.class);
    }

}
