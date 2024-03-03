/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 07:38:34
 */
package de.mnet.common.service.impl;

import static com.google.common.collect.ImmutableList.*;
import static com.google.common.collect.Iterables.*;
import static de.augustakom.common.BaseTest.*;
import static de.augustakom.common.tools.matcher.RegularExpressionFinder.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.IOArchiveProperties;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.IoArchiveBuilder;
import de.mnet.wita.model.SendAllowed;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = UNIT)
public class HistoryServiceImplTest extends BaseTest {

    @InjectMocks
    private HistoryServiceImpl underTest;

    @Mock
    private WitaConfigService witaConfigService;
    @Mock
    private MwfEntityDao mwfEntityDao;
    @Mock
    private WitaMessageHistoryCreator witaMessageHistoryCreator;

    @BeforeMethod
    public void setUp() {
        underTest = new HistoryServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] anlagenTypen() {
        Object[][] result = new Object[Anlagentyp.values().length][];
        int count = 0;
        for (Anlagentyp anlagentyp : Anlagentyp.values()) {
            result[count] = new Object[] { anlagentyp };
            count++;
        }
        return result;
    }

    @Test(dataProvider = "anlagenTypen")
    public void anlagenTypShouldBeMappedToArchiveType(Anlagentyp anlagentyp) {
        assertNotNull(underTest.getArchiveDocumentType(anlagentyp));
    }

    public void testCreateIoArchiveMultiMap() {
        IoArchive archive1 = new IoArchiveBuilder().withWitaExtOrderNo("1").build();
        IoArchive archive2 = new IoArchiveBuilder().withWitaExtOrderNo("1")
                .withRequestMeldungstyp(MeldungsType.ABM.getValue()).build();
        IoArchive archive3 = new IoArchiveBuilder().withWitaExtOrderNo("")
                .withRequestMeldungstyp(MeldungsType.AKM_PV.getValue()).build();
        IoArchive archive4 = new IoArchiveBuilder().withWitaExtOrderNo("")
                .withRequestMeldungstyp(MeldungsType.RUEM_PV.getValue()).build();
        IoArchive archive5 = new IoArchiveBuilder().withWitaExtOrderNo("")
                .withRequestMeldungstyp(MeldungsType.AKM_PV.getValue()).build();
        IoArchive archive6 = new IoArchiveBuilder().withWitaExtOrderNo("")
                .withRequestMeldungstyp(MeldungsType.RUEM_PV.getValue()).build();
        IoArchive archive7 = new IoArchiveBuilder().withWitaExtOrderNo("")
                .withRequestMeldungstyp(MeldungsType.ABM_PV.getValue()).build();

        ListMultimap<String, IoArchive> result = underTest.createIoArchiveMultiMap(Lists.newArrayList(archive1,
                archive2, archive3, archive4, archive5, archive6, archive7));
        assertNotNull(result, "MultiMap wurde nicht aufgebaut!");
        assertEquals(result.keySet().size(), 3, "KeySize der MultiMap ist nicht wie erwartet!");

        assertTrue(result.keySet().contains("1"));
        assertEquals(result.get("1").size(), 2);
        assertTrue(result.keySet().contains("AKM-PV_1"));
        assertEquals(result.get("AKM-PV_1").size(), 2);
        assertTrue(result.keySet().contains("AKM-PV_2"));
        assertEquals(result.get("AKM-PV_2").size(), 3);
    }

    public void testCreateNotSentIoArchiveEntriyForAlreadySentEntry() throws Exception {
        String extOrderNo = "12401";

        Set<IoArchive> ioArchives = ImmutableSet.of(new IoArchiveBuilder().withWitaExtOrderNo(extOrderNo).build());
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(LocalDateTime.now()).buildValid();
        when(mwfEntityDao.findAllRequests(extOrderNo, MnetWitaRequest.class)).thenReturn(
                ImmutableList.<MnetWitaRequest>of(auftrag));

        assertTrue(underTest.createNotSentIoArchiveEntries(ioArchives, extOrderNo).isEmpty());
    }

    public void testCreateNotSentIoArchiveEntriesForTv() throws Exception {
        String extOrderNo = "1234556";
        String minOnHold = "5";

        Set<IoArchive> ioArchives = ImmutableSet.of((new IoArchiveBuilder()).withWitaExtOrderNo(extOrderNo).build());
        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(LocalDateTime.now()).buildValid();
        TerminVerschiebung tv = (new TerminVerschiebungBuilder(BEREITSTELLUNG)).withExterneAuftragsnummer(extOrderNo)
                .withSentAt(null).buildValid();
        when(mwfEntityDao.findAllRequests(extOrderNo, MnetWitaRequest.class)).thenReturn(of(auftrag, tv));

        when(witaConfigService.checkSendAllowed(tv)).thenReturn(SendAllowed.REQUEST_VORGEHALTEN);
        when(witaConfigService.getMinutesWhileRequestIsOnHold()).thenReturn(minOnHold);

        Set<IoArchive> notSentEntries = underTest.createNotSentIoArchiveEntries(ioArchives, extOrderNo);
        assertThat(notSentEntries, hasSize(1));
        assertThat(getOnlyElement(notSentEntries).getIoSource(), equalTo(IOArchiveProperties.IOSource.WITA));
        assertThat(getOnlyElement(notSentEntries).getRequestMeldungstyp(), equalTo(MeldungsType.TV.getLongName()));
        assertThat(getOnlyElement(notSentEntries).getRequestXml(), findsPattern("Grund:\n"
                + SendAllowed.REQUEST_VORGEHALTEN.getText(minOnHold)));
    }

    public void testCreateNotSentIoArchiveEntriesForMultipleExtOrderNos() {
        int countOfDaysBeforeSent = 30;
        LocalDate kwt = (LocalDate.now()).plusDays(100);
        LocalDateTime earliestSendDate = LocalDateTime.now().plusMinutes(1);

        Auftrag auftrag1 = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1231")
                .withSentAt(LocalDateTime.now()).buildValid();
        Auftrag auftrag2 = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1231")
                .withSentAt(LocalDateTime.now()).buildValid();
        when(mwfEntityDao.findAllRequests("1231", MnetWitaRequest.class)).thenReturn(
                ImmutableList.<MnetWitaRequest>of(auftrag1, auftrag2));

        when(mwfEntityDao.findAllRequests("1232", MnetWitaRequest.class)).thenReturn(
                ImmutableList.<MnetWitaRequest>of());

        Auftrag auftrag3 = new AuftragBuilder(PORTWECHSEL).withExterneAuftragsnummer("1233").withSentAt(LocalDateTime.now())
                .buildValid();
        Storno storno3 = new StornoBuilder(PORTWECHSEL).withExterneAuftragsnummer("1233").buildValid();
        when(mwfEntityDao.findAllRequests("1233", MnetWitaRequest.class)).thenReturn(of(auftrag3, storno3));

        Auftrag auftrag4 = new AuftragBuilder(new GeschaeftsfallBuilder(VERBUNDLEISTUNG).withKundenwunschtermin(kwt))
                .withExterneAuftragsnummer("1234").buildValid();
        when(mwfEntityDao.findAllRequests("1234", MnetWitaRequest.class)).thenReturn(
                ImmutableList.<MnetWitaRequest>of(auftrag4));

        Auftrag auftrag5 = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1235")
                .withSentAt(LocalDateTime.now()).buildValid();
        TerminVerschiebung tv5 = new TerminVerschiebungBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1235")
                .withRequestWurdeStorniert(true).buildValid();
        when(mwfEntityDao.findAllRequests("1235", MnetWitaRequest.class)).thenReturn(of(auftrag5, tv5));

        Auftrag auftrag6 = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1236")
                .withEarliestSendDate(earliestSendDate).buildValid();
        when(mwfEntityDao.findAllRequests("1236", MnetWitaRequest.class)).thenReturn(
                ImmutableList.<MnetWitaRequest>of(auftrag6));

        when(witaConfigService.checkSendAllowed(storno3)).thenReturn(SendAllowed.SENDE_LIMIT);
        when(witaConfigService.checkSendAllowed(auftrag4)).thenReturn(SendAllowed.KWT_IN_ZUKUNFT);
        when(witaConfigService.checkSendAllowed(auftrag6)).thenReturn(SendAllowed.EARLIEST_SEND_DATE_IN_FUTURE);
        when(witaConfigService.getCountOfDaysBeforeSent(any(GeschaeftsfallTyp.class)))
                .thenReturn(countOfDaysBeforeSent);

        // @formatter:off
       Set<IoArchive> ioArchives = ImmutableSet.of(
               (new IoArchiveBuilder()).withWitaExtOrderNo("1231").build(),
               (new IoArchiveBuilder()).withWitaExtOrderNo("1231").build(),
               (new IoArchiveBuilder()).withWitaExtOrderNo("1233").withRequestGeschaeftsfall(PORTWECHSEL).build(),
               (new IoArchiveBuilder()).withWitaExtOrderNo("1235").build(),
               (new IoArchiveBuilder()).withWitaExtOrderNo("1236").build()
           );
       // @formatter:on

        List<IoArchive> results = Lists.newArrayList();
        for (String extOrderNo : ImmutableSet.of("1231", "1232", "1233", "1234", "1235", "1236")) {
            results.addAll(underTest.createNotSentIoArchiveEntries(ioArchives, extOrderNo));
        }
        assertThat(results, hasSize(3));
        assertThat(get(results, 0).getRequestXml(),
                findsPattern("Grund:\n" + SendAllowed.SENDE_LIMIT.getText(PORTWECHSEL)));
        assertThat(
                get(results, 1).getRequestXml(),
                findsPattern("Grund:\n"
                        + SendAllowed.KWT_IN_ZUKUNFT.getText(kwt.format(DateTimeFormatter.ofPattern("dd\\.MM\\.yyyy")), countOfDaysBeforeSent)
                        .replace("(", "\\(").replace(")", "\\)"))
        );
        assertThat(
                get(results, 2).getRequestXml(),
                findsPattern("Grund:\n"
                        + SendAllowed.EARLIEST_SEND_DATE_IN_FUTURE.getText(earliestSendDate.format(DateTimeFormatter.ofPattern("dd\\.MM\\.yyyy")))
                        .replace("(", "\\(").replace(")", "\\)"))
        );
    }

    public void testCreateEmptySentIoArchiveEntries() {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1234556").buildValid();
        when(mwfEntityDao.queryByExample(any(Auftrag.class), eq(Auftrag.class))).thenReturn(ImmutableList.of(auftrag));

        Set<IoArchive> notSentIoArchiveEntries = underTest.createNotSentIoArchiveEntries(Sets.<IoArchive>newHashSet(),
                "1234556");
        assertThat(notSentIoArchiveEntries, hasSize(0));
    }

}
