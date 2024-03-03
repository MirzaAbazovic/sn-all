/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 23.06.2010 13:58:14
  */

package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.DNLeistungDAO;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Leistung2DNBuilder;
import de.augustakom.hurrican.model.cc.Leistung2ParameterBuilder;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CCRufnummernServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private CCRufnummernServiceImpl sut;

    @Mock
    private LeistungService leistungService;
    @Mock
    private RufnummerService rufnummerService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private DNLeistungDAO dao;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] dataForMissingDnLeistungen() throws Exception {
        Leistung l1 = new Leistung();
        l1.setExternLeistungNo(23L);
        Leistung l2 = new Leistung();
        l2.setExternLeistungNo(42L);

        List<Leistung> taifunLeistungen1 = new ArrayList<Leistung>();
        taifunLeistungen1.add(l1);
        List<Leistung> taifunLeistungen2 = new ArrayList<Leistung>();
        taifunLeistungen2.add(l1);
        taifunLeistungen2.add(l2);

        Leistung2DN l2dn = new Leistung2DN();
        l2dn.setLeistung4DnId(5L);
        l2dn.setLbId(10L);
        List<Leistung2DN> leistungen4Auftrag = new ArrayList<Leistung2DN>();
        leistungen4Auftrag.add(l2dn);

        Leistung4Dn l4dn1 = new Leistung4Dn();
        l4dn1.setId(5L);
        Leistung4Dn l4dn2 = new Leistung4Dn();
        l4dn2.setId(6L);

        Map<Long, Leistung4Dn> leistung4Dn = new HashMap<Long, Leistung4Dn>();
        leistung4Dn.put(23L, l4dn1);
        leistung4Dn.put(42L, l4dn2);

        List<Leistung4Dn> missing1 = new ArrayList<Leistung4Dn>();
        List<Leistung4Dn> missing2 = new ArrayList<Leistung4Dn>();
        missing2.add(l4dn2);

        AuftragDaten auftrag1 = new AuftragDaten();
        auftrag1.setAuftragId(Long.valueOf(23));
        auftrag1.setAuftragNoOrig(23L);

        AuftragDaten auftrag2 = new AuftragDaten();
        auftrag2.setAuftragId(Long.valueOf(42));
        auftrag2.setAuftragNoOrig(42L);

        return new Object[][] {
                // 1 Leistung in Taifun + korrespondierende Leistung in Hurrican -> nichts fehlt
                new Object[] { auftrag1.getAuftragId(), auftrag1.getAuftragNoOrig(), auftrag1, taifunLeistungen1,
                        leistungen4Auftrag, leistung4Dn, missing1 },
                // 2 Leistungen in Taifun aber nur 1 Leistung davon in Hurrican -> eine Leistung fehlt
                new Object[] { auftrag2.getAuftragId(), auftrag2.getAuftragNoOrig(), auftrag2, taifunLeistungen2,
                        leistungen4Auftrag, leistung4Dn, missing2 }, };
    }

    @Test(dataProvider = "dataForMissingDnLeistungen")
    public void testFindMissingDnLeistungen(Long auftragId, Long auftragNoOrig, AuftragDaten auftragDaten,
            List<Leistung> leistungen, List<Leistung2DN> leistungen4Auftrag, Map<Long, Leistung4Dn> leistung4Dn,
            List<Leistung4Dn> missing) throws Exception {

        when(leistungService.findLeistungen4Auftrag(auftragNoOrig)).thenReturn(leistungen);
        when(auftragService.findAuftragDatenByAuftragId(auftragId)).thenReturn(auftragDaten);
        doReturn(leistungen4Auftrag).when(sut).findDNLeistungen4Auftrag(auftragId);
        for (Map.Entry<Long, Leistung4Dn> entry : leistung4Dn.entrySet()) {
            doReturn(entry.getValue()).when(sut).findDNLeistungByExternLeistungNo(entry.getKey());
        }

        List<Leistung4Dn> result = sut.findMissingDnLeistungen(auftragId);
        assertNotNull(result);
        assertEquals(result, missing);
    }

    public void testFindDNLeistungen4Auftrag() throws Exception {

        AuftragDaten auftragDaten = (new AuftragDatenBuilder()).withAuftragId(23L)
                .withAuftragNoOrig(23L).setPersist(false).build();
        when(auftragService.findAuftragDatenByAuftragId(23L)).thenReturn(auftragDaten);

        Leistungsbuendel lb = (new LeistungsbuendelBuilder()).withId(1817L).setPersist(false).build();
        doReturn(lb).when(sut).findLeistungsbuendel4Auftrag(23L);

        List<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add((new RufnummerBuilder()).withDnNo(1122L).setPersist(false).build());
        when(rufnummerService.findByParam(anyShort(), any(Object[].class))).thenReturn(rufnummern);

        List<Leistung2DN> l2dn = Collections.emptyList();
        when(dao.queryByExample(anyObject(), eq(Leistung2DN.class))).thenReturn(l2dn);

        sut.findDNLeistungen4Auftrag(23L);

        class IsCorrectExampleMatcher extends ArgumentMatcher<Leistung2DN> {
            private final Long dnNo;
            private final Long lbId;

            private IsCorrectExampleMatcher(Long dnNo, Long lbId) {
                this.dnNo = dnNo;
                this.lbId = lbId;
            }

            @Override
            public boolean matches(Object object) {
                if (object instanceof Leistung2DN) {
                    Leistung2DN leistung2dn = (Leistung2DN) object;
                    return dnNo.equals(leistung2dn.getDnNo()) && lbId.equals(leistung2dn.getLbId());
                }
                return false;
            }
        }
        verify(dao, times(1)).queryByExample(argThat(new IsCorrectExampleMatcher(1122L, 1817L)), eq(Leistung2DN.class));
    }

    public void testKuendigeLeistung4Rufnummern() throws Exception {
        final Long randomId = RandomTools.createLong();
        final Date expectedKuendigungDate = new Date();
        final String expectedUsername = "asdf";
        final List<Rufnummer> rufnummern = ImmutableList.of(createRufnummer(), createRufnummer());
        final List<Leistung2DN> leistungen2DNs = ImmutableList.of(createLeistung2Dn());

        when(dao.findAktiveLeistung2DnByRufnummern(eq(randomId), anyListOf(Long.class))).thenReturn(leistungen2DNs);
        sut.kuendigeLeistung4Rufnummern(randomId, expectedKuendigungDate, expectedUsername, rufnummern);

        assertThat(leistungen2DNs.get(0).getScvKuendigung(), equalTo(expectedKuendigungDate));
        assertThat(leistungen2DNs.get(0).getScvUserKuendigung(), equalTo(expectedUsername));

        verify(dao).findAktiveLeistung2DnByRufnummern(eq(randomId), anyListOf(Long.class));
        verify(dao).store(leistungen2DNs.get(0));
    }

    @DataProvider(name = "dataProviderFindDNLeistungenWithParameter")
    public Object[][] dataProviderFindDNLeistungenWithParameter() throws Exception {
        Leistung2Parameter leistung2Parameter = new Leistung2ParameterBuilder().withLeistungId(RandomTools.createLong()).build();
        final List<Leistung2Parameter> leistungenWithParameter = new LinkedList<Leistung2Parameter>();
        leistungenWithParameter.add(leistung2Parameter);

        final List<Leistung2Parameter> leistungenWithParameterEmpty = new LinkedList<Leistung2Parameter>();

        return new Object[][] {
                { leistungenWithParameter, 1 },
                { leistungenWithParameterEmpty, 0 },
                { null, 0 }
        };
    }

    @Test(dataProvider = "dataProviderFindDNLeistungenWithParameter")
    public void testFindDNLeistungenWithParameter(List<Leistung2Parameter> leistungenWithParameter, int resultSize) throws Exception {
        final List<Leistung4Dn> leistungen4DNs = ImmutableList.of(createLeistung4Dn());

        when(dao.findLeistung2Parameter(any(Long.class))).thenReturn(leistungenWithParameter);
        List<Leistung4Dn> result = sut.findDNLeistungenWithParameter(leistungen4DNs);
        assertEquals(result.size(), resultSize);

    }

    @DataProvider(name = "dataProviderFindDNLeistungenWithoutParameter")
    public Object[][] dataProviderFindDNLeistungenWithoutParameter() throws Exception {
        Leistung2Parameter leistung2Parameter = new Leistung2ParameterBuilder().withLeistungId(RandomTools.createLong()).build();
        final List<Leistung2Parameter> leistungenWithoutParameter = new LinkedList<Leistung2Parameter>();
        leistungenWithoutParameter.add(leistung2Parameter);

        final List<Leistung2Parameter> leistungenWithoutParameterEmpty = new LinkedList<Leistung2Parameter>();

        return new Object[][] {
                { leistungenWithoutParameter, 0 },
                { leistungenWithoutParameterEmpty, 1 },
                { null, 1 }
        };
    }

    @Test(dataProvider = "dataProviderFindDNLeistungenWithoutParameter")
    public void testFindDNLeistungenWithoutParameter(List<Leistung2Parameter> leistungenWithoutParameter, int resultSize) throws Exception {
        final List<Leistung4Dn> leistungen4DNs = ImmutableList.of(createLeistung4Dn());

        when(dao.findLeistung2Parameter(any(Long.class))).thenReturn(leistungenWithoutParameter);
        List<Leistung4Dn> result = sut.findDNLeistungenWithoutParameter(leistungen4DNs);
        assertEquals(result.size(), resultSize);

    }

    private Rufnummer createRufnummer() {
        return new RufnummerBuilder().withRandomDnNo().build();
    }

    private Leistung2DN createLeistung2Dn() {
        return new Leistung2DNBuilder().withRandomId().build();
    }

    private Leistung4Dn createLeistung4Dn() {
        return new Leistung4DnBuilder().withRandomId().build();
    }
} // end
