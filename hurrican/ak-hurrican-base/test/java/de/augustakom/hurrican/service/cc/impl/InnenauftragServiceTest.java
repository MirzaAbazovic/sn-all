/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2007 07:44:24
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKBereich;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel1Builder;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel3Builder;
import de.augustakom.hurrican.model.cc.innenauftrag.IaLevel5Builder;
import de.augustakom.hurrican.model.shared.view.InnenauftragQuery;
import de.augustakom.hurrican.model.shared.view.InnenauftragView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.InnenauftragService;


/**
 * JUnit TestCase fuer <code>InnenauftragService</code>.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class InnenauftragServiceTest extends AbstractHurricanBaseServiceTest {

    private InnenauftragService cut;

    @BeforeMethod
    public void setup() {
        cut = getCCService(InnenauftragService.class);
    }

    public void testFindIAViews() throws FindException {
        InnenauftragService ias = getCCService(InnenauftragService.class);

        InnenauftragQuery query = new InnenauftragQuery();
        query.setIaNummer("AKBG00001*");
        List<InnenauftragView> result = ias.findIAViews(query);

        assertNotEmpty(result, "IA-Views wurden nicht ermittelt!");
    }


    public void testFindIaLevels() throws FindException {
        getBuilder(IaLevel1Builder.class)
                .build();
        assertNotEmpty(cut.findIaLevels());
    }


    public void testSaveIaLevel() throws StoreException, FindException {
        IaLevel1 toStore = new IaLevel1Builder()
                .addIaLevel3(
                        new IaLevel3Builder()
                                .addIaLevel5(
                                        new IaLevel5Builder()
                                                .setPersist(false)
                                                .build()
                                )
                                .setPersist(false)
                                .build()
                )
                .setPersist(false).build();
        cut.saveIaLevel(toStore);

        List<IaLevel1> iaLevels = cut.findIaLevels();
        Optional<IaLevel1> result =
                iaLevels
                        .stream()
                        .filter(
                                ia -> toStore.getName().equals(ia.getName()) && toStore.getSapId().equals(ia.getSapId())
                        )
                        .findFirst();
        assertTrue(result.isPresent());
    }

    public void testFindIaLevelsForUser()   {
        final IaLevel1 toFindSameBereich = getBuilder(IaLevel1Builder.class).withBereichName("bspBereich").withLockMode(false).build();
        final IaLevel1 toFindNotLocked = getBuilder(IaLevel1Builder.class).withLockMode(false).build();
        final IaLevel1 notToFindLockedDifferentBereich = getBuilder(IaLevel1Builder.class).withLockMode(true).build();

        final AKBereich bereich = new AKBereich(toFindSameBereich.getBereichName(), 815L);
        final AKUser user = new AKUser();
        user.setBereich(bereich);

        final List<IaLevel1> result = cut.findIaLevelsForUser(user);
        assertTrue(result.contains(toFindNotLocked));
        assertTrue(result.contains(toFindSameBereich));
        assertFalse(result.contains(notToFindLockedDifferentBereich));
    }
}


