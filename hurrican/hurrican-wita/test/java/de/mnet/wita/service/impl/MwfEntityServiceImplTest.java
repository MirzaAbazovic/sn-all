/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2011 13:16:37
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

@Test(groups = BaseTest.UNIT)
public class MwfEntityServiceImplTest extends BaseTest {

    @InjectMocks
    private MwfEntityServiceImpl cut;

    @Mock
    private MwfEntityDao mwfEntityDao;

    private Auftrag auftrag;
    private QualifizierteEingangsBestaetigung qeb;
    private AuftragsBestaetigungsMeldung abm;
    private TerminAnforderungsMeldung tam;
    private TerminVerschiebung tv;
    private TerminAnforderungsMeldung secondTam;
    private List<Meldung<?>> meldungen;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void setUp() {
        cut = new MwfEntityServiceImpl();
        MockitoAnnotations.initMocks(this);

        LocalDateTime baseDate = LocalDateTime.of(2011, 12, 1, 10, 1, 2, 0);
        auftrag = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(baseDate).buildValid();
        qeb = new QualifizierteEingangsBestaetigungBuilder().withVersandZeitstempel(baseDate.plusMinutes(5)).build();
        abm = new AuftragsBestaetigungsMeldungBuilder().withVersandZeitstempel(baseDate.plusMinutes(10)).build();
        tam = new TerminAnforderungsMeldungBuilder().withVersandZeitstempel(baseDate.plusMinutes(15)).build();
        tv = new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).withSentAt(baseDate.plusMinutes(20))
                .buildValid();
        secondTam = new TerminAnforderungsMeldungBuilder().withVersandZeitstempel(baseDate.plusMinutes(25)).build();

        meldungen = new ArrayList<>();
        Collections.addAll(meldungen, qeb, abm, tam, secondTam);
    }

    public void combineAndSortRequestsAndMessages() {
        List<MwfEntity> result = cut.combineAndSortRequestsAndMessages(Arrays.asList(auftrag, tv), meldungen);
        assertNotEmpty(result);
        assertEquals(result.size(), 6);
        assertEquals(result.get(0), secondTam);
    }

    public void combineAndSortRequestsAndMessagesAssertNotSentTvAtFirstPosition() {
        tv.setSentAt(null);

        List<MwfEntity> result = cut.combineAndSortRequestsAndMessages(Arrays.asList(auftrag, tv), meldungen);
        assertNotEmpty(result);
        assertEquals(result.size(), 6);
        assertEquals(result.get(0), tv);
    }

    public void testFindVertragsnummerFor() {
        String externeAuftragsnummer = "123";
        String vertragsnummer = "456";

        List<Meldung<?>> meldungen = getThreeMeldungenWith(vertragsnummer, vertragsnummer, null);
        when(mwfEntityDao.findAllMeldungen(externeAuftragsnummer)).thenReturn(meldungen);

        String result = cut.findVertragsnummerFor(externeAuftragsnummer);

        assertThat(result, equalTo(vertragsnummer));
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testFindVertragsnummerForNoSuchElementException() {
        String externeAuftragsnummer = "123";
        List<Meldung<?>> meldungen = getThreeMeldungenWith("", null, null);
        when(mwfEntityDao.findAllMeldungen(externeAuftragsnummer)).thenReturn(meldungen);

        cut.findVertragsnummerFor(externeAuftragsnummer);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindVertragsnummerForIllegalArgumentException() {
        String externeAuftragsnummer = "123";

        List<Meldung<?>> meldungen = getThreeMeldungenWith("1", "2", null);
        when(mwfEntityDao.findAllMeldungen(externeAuftragsnummer)).thenReturn(meldungen);

        cut.findVertragsnummerFor(externeAuftragsnummer);
    }

    public void testCheckMeldungNotReceived() {
        assertFalse(cut.checkMeldungReceived(null, AbbruchMeldung.class));
    }

    public void testCheckMeldungNotReceived2() {
        assertFalse(cut.checkMeldungReceived("123", QualifizierteEingangsBestaetigung.class));
    }

    @DataProvider
    public Object[][] dataProviderCheckMeldungReceived() {
        Auftrag neu = (new AuftragBuilder(BEREITSTELLUNG)).buildAuftragWithSchaltungKupfer();
        Auftrag lae = (new AuftragBuilder(LEISTUNGS_AENDERUNG)).buildValid();
        Auftrag pv = (new AuftragBuilder(PROVIDERWECHSEL)).buildValid();

        // @formatter:off
        return new Object[][] {
                { neu,  QualifizierteEingangsBestaetigung.class },
                { neu,  AuftragsBestaetigungsMeldung.class },
                { lae,  AbbruchMeldung.class },
                { pv,   VerzoegerungsMeldung.class },
                { lae,  ErledigtMeldung.class },
            };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckMeldungReceived")
    public <T extends Meldung<V>, V extends MeldungsPosition> void testCheckMeldungReceived(Auftrag auftrag,
            Class<T> clazz) throws InstantiationException, IllegalAccessException {
        ArgumentCaptor<T> meldungCaptor = ArgumentCaptor.forClass(clazz);
        when(mwfEntityDao.queryByExample(meldungCaptor.capture(), eq(clazz))).thenReturn(
                singletonList(clazz.newInstance()));

        assertTrue(cut.checkMeldungReceived(auftrag.getExterneAuftragsnummer(), clazz));
        assertEquals(meldungCaptor.getValue().getExterneAuftragsnummer(), auftrag.getExterneAuftragsnummer());
    }

    private List<Meldung<?>> getThreeMeldungenWith(String vtrnr1, String vtrnr2, String vtrnr3) {
        List<Meldung<?>> meldungen = Lists.newArrayList();
        qeb.setVertragsNummer(vtrnr1);
        meldungen.add(qeb);
        abm.setVertragsNummer(vtrnr2);
        meldungen.add(abm);
        tam.setVertragsNummer(vtrnr3);
        meldungen.add(tam);
        return meldungen;
    }
}
