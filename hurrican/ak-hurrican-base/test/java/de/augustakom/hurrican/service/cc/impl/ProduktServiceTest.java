/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 11:10:08
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationTypeBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.ProduktMappingBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Test-NG Klasse fuer {@link ProduktService}
 */
@Test(groups = { BaseTest.SERVICE })
public class ProduktServiceTest extends AbstractHurricanBaseServiceTest {

    private final Long produktGruppeIdMax = Long.valueOf(1000000);
    private final Long produktIdMax = Long.valueOf(1000000);

    private Produkt2TechLocationTypeBuilder builder1;
    private Produkt2TechLocationTypeBuilder builder2;

    public void testSaveProd2TechLocTypes4Produkt() throws StoreException, FindException {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class).withProduktGruppeBuilder(getBuilder(ProduktGruppeBuilder.class));
        ProduktService prodService = getProduktService();

        // First Pass
        builder1 = getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withPriority(Integer.valueOf(1));
        builder1.withRandomId().setPersist(true).build();

        builder1.withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_KVZ)
                .withPriority(Integer.valueOf(2));
        builder1.withRandomId().setPersist(true).build();

        // Second Pass - eine Ref Id wie im First Pass, eine Ref Id entfällt, eine Ref Id kommt dazu
        builder2 = getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_HVT);
        Produkt2TechLocationType secondPass1 = builder2.setPersist(false).build();

        builder2 = getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTH);
        Produkt2TechLocationType secondPass2 = builder2.setPersist(false).build();

        // Save Second Pass - überschreibt First Pass
        List<Produkt2TechLocationType> secondPassMapping = new ArrayList<Produkt2TechLocationType>();
        Collections.addAll(secondPassMapping, secondPass1, secondPass2);
        prodService.saveProdukt2TechLocationTypes(prodBuilder.getId(), secondPassMapping, -1L);

        // Check
        List<Produkt2TechLocationType> result = prodService.findProdukt2TechLocationTypes(builder1.get().getProduktId());
        assertNotEmpty(result, "Configuration not loaded!");
        assertEquals(result.size(), 2, "Result size differ from expected size!");
        assertEquals(result.get(0).getId(), secondPass1.getId(), "PK differs!");
        assertEquals(result.get(0).getTechLocationTypeRefId(), secondPass1.getTechLocationTypeRefId(), "TechLocationTypeRefId differs!");
        assertEquals(result.get(0).getPriority(), Integer.valueOf(1), "Prio not correctly defined");
        assertEquals(result.get(1).getId(), secondPass2.getId(), "PK differs!");
        assertEquals(result.get(1).getTechLocationTypeRefId(), secondPass2.getTechLocationTypeRefId(), "TechLocationTypeRefId differs!");
        assertEquals(result.get(1).getPriority(), Integer.valueOf(2), "Prio not correctly defined");
    }

    public void testFindProdukt2TechLocationTypes() throws FindException {
        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class).withProduktGruppeBuilder(getBuilder(ProduktGruppeBuilder.class));
        builder1 = getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withPriority(Integer.valueOf(1));
        Produkt2TechLocationType mapping1 = builder1.withRandomId().setPersist(true).build();

        builder2 = getBuilder(Produkt2TechLocationTypeBuilder.class)
                .withProduktBuilder(prodBuilder)
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_KVZ)
                .withPriority(Integer.valueOf(2));
        Produkt2TechLocationType mapping2 = builder2.withRandomId().setPersist(true).build();

        List<Produkt2TechLocationType> result = getProduktService().findProdukt2TechLocationTypes(builder1.get().getProduktId());
        assertNotEmpty(result, "Configuration not loaded!");
        assertEquals(result.size(), 2, "Result size unexpected!");
        assertEquals(result.get(0).getId(), mapping1.getId());
        assertEquals(result.get(1).getId(), mapping2.getId());
    }


    /**
     * Test stellt sicher, dass nicht priorisierte ProduktMappings weiter hinten einsortiert sind (NULLS last).
     */
    public void testFindProduktIdsSortedByPrio() {
        // @formatter:off
        ProduktMapping pm1 = getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(-99))
            .withProdId(Long.valueOf(420))
            .withPriority(null)
            .build();
        ProduktMapping pm2 = getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(-98))
            .withProdId(Long.valueOf(512))
            .withPriority(Long.valueOf(1))
            .build();
        ProduktMapping pm3 = getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(-98))
            .withProdId(Long.valueOf(513))
            .withPriority(Long.valueOf(2))
            .build();
        // @formatter:on

        List<Long> mappingGroups = new ArrayList<Long>();
        mappingGroups.add(pm1.getMappingGroup());
        mappingGroups.add(pm2.getMappingGroup());
        mappingGroups.add(pm3.getMappingGroup());

        List<Long> produktIds = getProduktService().findProduktIdsSortedByPrio(mappingGroups);
        assertNotEmpty(produktIds);
        assertEquals(produktIds.get(0), Long.valueOf(512));
        assertEquals(produktIds.get(1), Long.valueOf(513));
        assertEquals(produktIds.get(2), Long.valueOf(420));
    }

    public void testFindExtProdNos() throws FindException {
        // @formatter:off
        Produkt produkt1 = getBuilder(ProduktBuilder.class).withRandomId().build();
        Produkt produkt2 = getBuilder(ProduktBuilder.class).withRandomId().build();

        getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(1000000))
            .withExtProdNo(Long.valueOf(10))
            .withProdId(produkt1.getId())
            .withMappingPartType(ProduktMapping.MAPPING_PART_TYPE_PHONE_DSL)
            .build();
        getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(2000000))
            .withExtProdNo(Long.valueOf(20))
            .withProdId(produkt2.getId())
            .withMappingPartType(ProduktMapping.MAPPING_PART_TYPE_DSL)
            .build();
        getBuilder(ProduktMappingBuilder.class)
            .withMappingGroup(Long.valueOf(2000000))
            .withExtProdNo(Long.valueOf(21))
            .withProdId(produkt2.getId())
            .withMappingPartType(ProduktMapping.MAPPING_PART_TYPE_PHONE)
            .build();
        // @formatter:on

        List<Long> result = getProduktService().findExtProdNos(produkt2.getId(), null);
        assertNotEmpty(result);
        assertTrue(CollectionUtils.isEqualCollection(result, Arrays.asList(Long.valueOf(20), Long.valueOf(21))));
    }


    @DataProvider(name = "dataProviderFindProdukte4PGs")
    public Object[][] dataProviderFindEQByLeiste() {
        // @formatter:off
        return new Object[][] {
            { null },
            { new Long[] {} },
            { new Long[] {produktGruppeIdMax} },
            { new Long[] {produktGruppeIdMax-1} },
            { new Long[] {produktGruppeIdMax-2} },
            { new Long[] {produktGruppeIdMax-1,produktGruppeIdMax-2} },
            { new Long[] {produktGruppeIdMax,produktGruppeIdMax-1,produktGruppeIdMax-2} },
        };
        // @formatter:on
    }

    private ProduktGruppe[] createProduktgruppen() {
        ProduktGruppe[] produktGruppen = new ProduktGruppe[3];
        for (int i = 0; i < 3; i++) {
            produktGruppen[i] = getBuilder(ProduktGruppeBuilder.class).withId(Long.valueOf(produktGruppeIdMax - i))
                    .build();
        }
        return produktGruppen;
    }

    private Produkt[] createProdukte(ProduktGruppe[] produktGruppen) {
        Produkt[] produkte = new Produkt[3];
        Long produktGruppeId1 = produktGruppen[1].getId();
        Long produktGruppeId2 = produktGruppen[2].getId();
        produkte[0] = getBuilder(ProduktBuilder.class).withId(produktIdMax).withProduktGruppeId(produktGruppeId1)
                .build();
        produkte[1] = getBuilder(ProduktBuilder.class).withId(produktIdMax - 1).withProduktGruppeId(produktGruppeId2)
                .build();
        produkte[2] = getBuilder(ProduktBuilder.class).withId(produktIdMax - 2).withProduktGruppeId(produktGruppeId2)
                .build();
        return produkte;
    }

    @Test(dataProvider = "dataProviderFindProdukte4PGs")
    public void testFindProdukte4PGs(final Long[] produktGruppenIds) throws FindException {
        ProduktGruppe[] produktGruppen = createProduktgruppen();
        Produkt[] produkte = createProdukte(produktGruppen);

        List<Produkt> expectedProdukte = ImmutableList.of();
        if (produktGruppenIds != null) {
            final ImmutableSet<Long> setProduktGruppenIds = ImmutableSet.copyOf(produktGruppenIds);
            expectedProdukte = ImmutableList.copyOf(Iterables.filter(Arrays.asList(produkte), new Predicate<Produkt>() {
                @Override
                public boolean apply(Produkt input) {
                    if (setProduktGruppenIds.contains(input.getProduktGruppeId())) {
                        return true;
                    }
                    return false;
                }
            }));
        }

        List<Produkt> result = getProduktService().findProdukte4PGs(produktGruppenIds);
        assertNotNull(result);
        assertTrue(CollectionUtils.isEqualCollection(result, expectedProdukte));
    }

    private ProduktService getProduktService() {
        return getCCService(ProduktService.class);
    }

}
