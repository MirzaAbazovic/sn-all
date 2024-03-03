/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2011 11:42:15
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.mnet.wita.message.common.Anlagentyp;

@Test(groups = UNIT)
public class CbVorgangDataUnitTest extends BaseTest {

    public void buildCbVorgangAssertOne() {
        // @formatter:off
        CbVorgangData cbv = new CbVorgangData()
            .addAuftragId(Long.valueOf(1))
            .withUser(new AKUser());
        // @formatter:on
        List<CBVorgang> result = cbv.buildCbVorgaenge();
        assertThat(result, hasSize(1));
    }

    public void buildCbVorgangAssertTwo() {
        // @formatter:off
        CbVorgangData cbv = new CbVorgangData()
            .addAuftragId(Long.valueOf(1),Long.valueOf(99))
            .addAuftragId(Long.valueOf(2))
            .withUser(new AKUser());
        // @formatter:on
        List<CBVorgang> result = cbv.buildCbVorgaenge();
        assertThat(result, hasSize(2));
        assertEquals(cbv.getAuftragId4PortChange(1L), Long.valueOf(99));
        assertNull(cbv.getAuftragId4PortChange(2L));
    }

    public void buildCbVorgangAssertWita() {
        // @formatter:off
        CbVorgangData cbv = new CbVorgangData()
            .addAuftragId(Long.valueOf(1))
            .withCarrierId(Carrier.ID_DTAG)
            .withUser(new AKUser());
        // @formatter:on
        List<CBVorgang> result = cbv.buildCbVorgaenge();
        assertThat(Iterables.getOnlyElement(result), instanceOf(WitaCBVorgang.class));
    }

    public void duplicateAuftragIdsNotPossible() {
        List<Long> longList = Lists.newArrayList();
        longList.add(Long.valueOf(1));
        longList.add(Long.valueOf(2));

        CbVorgangData cbv = new CbVorgangData()
                .addAuftragId(Long.valueOf(1))
                .addAuftragId(Long.valueOf(2))
                .addAuftragId(Long.valueOf(1))
                .addAuftragIds(longList)
                .addAuftragIds(Long.valueOf(1), Long.valueOf(2))
                .withUser(new AKUser());
        assertEquals(cbv.getAuftragIds().size(), 2);
    }

    public void verifyArchiveDocsArePlacedToRightCbVorgang() {
        Map<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocs = Maps.newHashMap();
        Long auftragId1 = Long.valueOf(1);
        Long auftragId2 = Long.valueOf(2);

        ArchiveDocumentDto archiveDoc1 = new ArchiveDocumentDto();
        ArchiveDocumentDto archiveDoc2 = new ArchiveDocumentDto();
        ArchiveDocumentDto archiveDoc3 = new ArchiveDocumentDto();

        Set<Pair<ArchiveDocumentDto, String>> archive2auftrag1 = Sets.newHashSet();
        archive2auftrag1.add(Pair.create(archiveDoc1, Anlagentyp.KUENDIGUNGSSCHREIBEN.value));
        archive2auftrag1.add(Pair.create(archiveDoc2, Anlagentyp.KUENDIGUNGSSCHREIBEN.value));
        Set<Pair<ArchiveDocumentDto, String>> archive2auftrag2 = Sets.newHashSet();
        archive2auftrag2.add(Pair.create(archiveDoc3, Anlagentyp.KUENDIGUNGSSCHREIBEN.value));
        archiveDocs.put(auftragId1, archive2auftrag1);
        archiveDocs.put(auftragId2, archive2auftrag2);

        CbVorgangData cbv = new CbVorgangData()
                .addAuftragId(auftragId1)
                .addAuftragId(auftragId2)
                .withArchiveDocuments(archiveDocs)
                .withCarrierId(Carrier.ID_DTAG)
                .withUser(new AKUser());
        assertEquals(cbv.getAuftragIds().size(), 2);
        List<CBVorgang> result = cbv.buildCbVorgaenge();
        WitaCBVorgang first = (WitaCBVorgang) result.get(0);
        WitaCBVorgang second = (WitaCBVorgang) result.get(1);
        assertEquals(first.getAnlagen().size(), 2);
        assertEquals(second.getAnlagen().size(), 1);
    }

    public void onlyFirstOfKlammerGetsRufnummerPortierung() {
        Set<Long> rufnummerIds = Sets.newHashSet(Long.valueOf(213), Long.valueOf(123));

        // @formatter:off
        CbVorgangData cbv = new CbVorgangData()
            .addAuftragId(Long.valueOf(1))
            .addAuftragId(Long.valueOf(2))
            .withCarrierId(Carrier.ID_DTAG)
            .withRufnummerIds(rufnummerIds)
            .withUser(new AKUser());
        // @formatter:on

        List<CBVorgang> result = cbv.buildCbVorgaenge();
        WitaCBVorgang first = (WitaCBVorgang) result.get(0);
        WitaCBVorgang second = (WitaCBVorgang) result.get(1);

        assertThat(result, hasSize(2));
        assertEquals(first.getAuftragId(), Long.valueOf(1));
        assertEquals(second.getAuftragId(), Long.valueOf(2));
        assertTrue(CollectionTools.isNotEmpty(first.getRufnummerIds()));
        assertTrue(CollectionTools.isEmpty(second.getRufnummerIds()));
    }

}


