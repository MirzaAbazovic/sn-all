/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:35:48
 */

package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingBuilder;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.AuftragHousingKeyBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingBuildingBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingFloorBuilder;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderBuilder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.model.cc.housing.TransponderGroupBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.model.cc.view.CCAuftragHousingView;
import de.augustakom.hurrican.model.shared.view.AuftragHousingQuery;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HousingService;


@Test(groups = { BaseTest.SERVICE })
public class HousingServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private HousingService housingService;

    public void testSaveAuftragHousingWithBuilder() throws StoreException {
        AuftragHousing toSave = getBuilder(AuftragHousingBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .build();
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragHousing not saved.");
    }

    public void testSaveAuftragHousingKeyWithBuilder() throws StoreException {
        AuftragHousingKey toSave2 = getBuilder(AuftragHousingKeyBuilder.class)
                .withAuftragHousingBuilder(getBuilder(AuftragHousingBuilder.class))
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .build();
        flushAndClear();
        assertNotNull(toSave2.getId(), "AuftragHousingKey not saved.");
    }

    public void testSaveAuftragHousingWithService() throws StoreException {
        AuftragHousing toSave = getBuilder(AuftragHousingBuilder.class).setPersist(false).build();
        toSave.setAuftragId(Long.valueOf(123123));
        housingService.saveAuftragHousing(toSave);
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragHousing not saved.");
    }

    public void testFindHousingBuildings() throws FindException {
        CCAddress address = getBuilder(CCAddressBuilder.class).build();
        HousingFloor floor = getBuilder(HousingFloorBuilder.class).build();
        getBuilder(HousingBuildingBuilder.class)
                .withCCAddress(address)
                .withFloor(floor)
                .build();

        List<HousingBuilding> buildings = housingService.findHousingBuildings();
        assertNotEmpty(buildings, "No buildings found!");
        assertNotNull(buildings.get(0).getAddress(), "No address found for building!");
        assertNotEmpty(buildings.get(0).getFloors(), "No floors found for building!");
    }

    public void testSaveAuftragHousingKey() throws StoreException {
        AuftragHousingKey toSave = getBuilder(AuftragHousingKeyBuilder.class).setPersist(false)
                .withAuftragHousingBuilder(getBuilder(AuftragHousingBuilder.class)).build();
        housingService.saveAuftragHousingKey(toSave);
        flushAndClear();
        assertNotNull(toSave.getId(), "AuftragHousingKey not saved.");
    }

    public void testFindHousingKeys() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragHousingKey toFind = getBuilder(AuftragHousingKeyBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .build();
        Long auftragId = toFind.getAuftragId();

        TransponderGroup transponderGroup = getBuilder(TransponderGroupBuilder.class)
                .addTransponder(getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(123)).build())
                .build();
        getBuilder(AuftragHousingKeyBuilder.class)
                .withTransponderBuilder(null)
                .withAuftragBuilder(auftragBuilder)
                .withTransponderGroup(transponderGroup)
                .build();

        List<AuftragHousingKeyView> keys = housingService.findHousingKeys(auftragId);
        assertNotEmpty(keys, "No housing keys found for order!");
        assertEquals(keys.size(), 2);
        assertNotNull(keys.get(0).getTransponderId(), "TransponderID for housing key missing!");
        assertNotNull(keys.get(1).getTransponderId(), "TransponderID for housing key missing!");
    }

    public void testFindHousingsByQuery() throws FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragHousingBuilder auftragHousingBuilder = getBuilder(AuftragHousingBuilder.class)
                .withAuftragBuilder(auftragBuilder);
        AuftragHousingKey toFind = getBuilder(AuftragHousingKeyBuilder.class)
                .withAuftragHousingBuilder(auftragHousingBuilder)
                .withAuftragBuilder(auftragBuilder)
                .build();

        AuftragHousingQuery housingQuery = new AuftragHousingQuery();
        housingQuery.setTransponderNr(toFind.getTransponder().getTransponderId());
        assertNotNull(housingQuery.getTransponderNr());

        List<CCAuftragHousingView> orders = housingService.findHousingsByQuery(housingQuery);
        assertNotEmpty(orders, "No housing keys found with given query parameters!");
        assertNotNull(orders.get(0).getAuftragId(), "Oder ID for housing key missing!");
    }

    public void testSaveTransponderGroup() throws StoreException {
        Transponder transponder1 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(1)).build();
        Transponder transponder2 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(2)).build();

        TransponderGroup group = getBuilder(TransponderGroupBuilder.class)
                .setPersist(false)
                .addTransponder(transponder1)
                .addTransponder(transponder2)
                .build();

        housingService.saveTransponderGroup(group);
        assertNotNull(group.getId());
        assertNotEmpty(group.getTransponders());
        assertEquals(group.getTransponders().size(), 2);
        assertNotNull(group.getTransponders().iterator().next().getId());
    }

    public void testModifyTransponderGroup() throws StoreException, FindException {
        Transponder transponder1 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(1)).build();
        Transponder transponder2 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(2)).build();
        TransponderGroup group = getBuilder(TransponderGroupBuilder.class)
                .withKundeNo(Long.valueOf(123))
                .addTransponder(transponder1)
                .addTransponder(transponder2)
                .build();

        group.getTransponders().remove(transponder1);
        housingService.saveTransponderGroup(group);

        List<TransponderGroup> reloadedTGs = housingService.findTransponderGroups(group.getKundeNo());
        assertNotNull(reloadedTGs);
        assertNotEmpty(reloadedTGs.get(0).getTransponders());
        assertEquals(reloadedTGs.get(0).getTransponders().size(), 1);
    }


    public void testFindTransponderGroups() throws StoreException, FindException {
        Transponder transponder1 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(1)).build();
        Transponder transponder2 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(2)).build();

        TransponderGroup group = getBuilder(TransponderGroupBuilder.class)
                .withKundeNo(Long.valueOf(123))
                .addTransponder(transponder1)
                .addTransponder(transponder2)
                .build();

        List<TransponderGroup> result = housingService.findTransponderGroups(group.getKundeNo());
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTransponders().size(), 2);
    }


    public void testDeleteTransponderGroups() throws StoreException, FindException, DeleteException {
        Long kundeNo = Long.valueOf(123);
        Transponder transponder1 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(1)).build();
        Transponder transponder2 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(2)).build();

        TransponderGroup group = getBuilder(TransponderGroupBuilder.class)
                .withKundeNo(kundeNo)
                .addTransponder(transponder1)
                .addTransponder(transponder2)
                .build();

        housingService.deleteTransponderGroup(group);
        assertEmpty(housingService.findTransponderGroups(kundeNo), "Transponder-Group found but not expected!");
    }


    @Test(expectedExceptions = DeleteException.class)
    public void testDeleteTransponderGroupsExpectToFail() throws StoreException, FindException, DeleteException {
        Long kundeNo = Long.valueOf(123);
        Transponder transponder1 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(1)).build();
        Transponder transponder2 = getBuilder(TransponderBuilder.class).setPersist(false).withTransponderId(Long.valueOf(2)).build();

        TransponderGroup group = getBuilder(TransponderGroupBuilder.class)
                .withKundeNo(kundeNo)
                .addTransponder(transponder1)
                .addTransponder(transponder2)
                .build();

        // Transponder-Gruppe einem Auftrag zuordnen, damit Delete einen Fehler verursacht!
        getBuilder(AuftragHousingKeyBuilder.class)
                .withTransponderBuilder(null)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withTransponderGroup(group)
                .build();

        housingService.deleteTransponderGroup(group);
    }

}
