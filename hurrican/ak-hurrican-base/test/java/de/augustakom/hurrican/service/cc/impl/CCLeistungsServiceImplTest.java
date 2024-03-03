/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2011 15:35:47
 */
package de.augustakom.hurrican.service.cc.impl;

import static com.google.common.collect.ImmutableList.*;
import static org.apache.commons.lang3.time.DateUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.Auftrag2TechLeistungDAO;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ExterneAuftragsLeistungen;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = { BaseTest.UNIT })
public class CCLeistungsServiceImplTest extends BaseTest {

    private static final Long AUFTRAG_NO_ORIG = 234234234L;

    private static final Long AUFTRAG_ID = 123123123L;

    private static final Long PRODUCT_ID = 513L;

    @InjectMocks
    @Spy
    private CCLeistungsServiceImpl sut;

    @Mock
    private TechLeistungDAO techLsDaoMock;
    @Mock
    private CCAuftragService ccAuftragServiceMock;
    @Mock
    private Auftrag2TechLeistungDAO auftrag2TechLeistungDaoMock;
    @Mock
    private BillingAuftragService billingAuftragServiceMock;

    @BeforeMethod
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] checkMustAccountBeLockedDP() {
        TechLeistung pmx = new TechLeistungBuilder().setPersist(false).withId(TechLeistung.ID_VOIP_PMX).build(), noPMX = new TechLeistungBuilder()
                .setPersist(false).withId(TechLeistung.ID_VOIP_TK).build(), upstreamGt5000 = new TechLeistungBuilder()
                .setPersist(false).withLongValue(5000L + 1L).build(), upstreamLt5000 = new TechLeistungBuilder()
                .setPersist(false).withLongValue(5000L - 1L).build(), upstreamEq5000 = new TechLeistungBuilder()
                .setPersist(true).withLongValue(5000L).build(), anyTechLs = new TechLeistung();
        List<TechLeistung> tooManyTechLsConfigured = Arrays.asList(anyTechLs, anyTechLs);

        return new Object[][] {
                new Object[] { Arrays.asList(pmx), Arrays.asList(upstreamGt5000), false, false,
                        "must be false if pmx and upstream > 5000" },
                new Object[] { Arrays.asList(pmx), Arrays.asList(upstreamLt5000), true, false,
                        "must be true if pmx and upstream < 5000" },
                new Object[] { Arrays.asList(pmx), Arrays.asList(upstreamEq5000), true, false,
                        "must be true if pmx and upstream = 5000" },
                new Object[] { Arrays.asList(noPMX), Arrays.asList(upstreamGt5000), false, false,
                        "must be false if not pmx" },
                new Object[] { Arrays.asList(noPMX), Arrays.asList(upstreamLt5000), false, false,
                        "must be false if not pmx" },
                new Object[] { Arrays.asList(noPMX), Arrays.asList(upstreamEq5000), false, false,
                        "must be false if not pmx" },
                new Object[] { null, Arrays.asList(anyTechLs), false, false,
                        "must be false if no VOIP-TechLeistung is configured for Auftrag" },
                new Object[] { Arrays.asList(anyTechLs), null, false, false,
                        "must be false if no Upstream-TechLeistung is configured for Auftrag" },
                new Object[] { null, null, false, false,
                        "must be false if no VOIP- and no Upstream-TechLeistung is configured for Auftrag" },
                new Object[] { Collections.emptyList(), Arrays.asList(anyTechLs), false, false,
                        "must be false if no VOIP-TechLeistung is configured for Auftrag" },
                new Object[] { Arrays.asList(anyTechLs), Collections.emptyList(), false, false,
                        "must be false if no Upstream-TechLeistung is configured for Auftrag" },
                new Object[] { Collections.emptyList(), Collections.emptyList(), false, false,
                        "must be false if no VOIP- and no Upstream-TechLeistung is configured for Auftrag" },
                new Object[] { tooManyTechLsConfigured, Arrays.asList(anyTechLs), null, true,
                        "FindException must be thrown if more than one TechLeistung of type VOIP is configured" },
                new Object[] { Arrays.asList(anyTechLs), tooManyTechLsConfigured, null, true,
                        "FindException must be thrown if more than one TechLeistung of type UPSTREAM is configured" } };
    }

    @Test(dataProvider = "checkMustAccountBeLockedDP")
    public void testCheckMustAccountBeLocked(final List<TechLeistung> voipTL, final List<TechLeistung> upstreamTL,
            final Boolean result, boolean findException, String errorMsg) throws Exception {
        final Long auftragsId = 123123123L;
        when(techLsDaoMock.findTechLeistungen4Auftrag(eq(auftragsId), eq(TechLeistung.TYP_VOIP), eq(true))).thenReturn(
                voipTL);
        when(techLsDaoMock.findTechLeistungen4Auftrag(eq(auftragsId), eq(TechLeistung.TYP_UPSTREAM), eq(true)))
                .thenReturn(
                        upstreamTL);
        try {
            assertEquals(sut.checkMustAccountBeLocked(auftragsId), result.booleanValue(), errorMsg);
            assertFalse(findException);
        }
        catch (FindException e) {
            assertTrue(findException);
        }
    }

    private static Produkt2TechLeistung p2tl(Long productId, TechLeistung techLeistung) {
        return p2tl(productId, techLeistung, false, null, null);
    }

    private static Produkt2TechLeistung p2tl(Long productId, TechLeistung techLeistung, boolean defaultLeistung) {
        return p2tl(productId, techLeistung, defaultLeistung, null, null);
    }

    private static Produkt2TechLeistung p2tl(Long productId, TechLeistung techLeistung, boolean defaultLeistung,
            TechLeistung dependsOn) {
        return p2tl(productId, techLeistung, defaultLeistung, dependsOn, null);
    }
    private static Produkt2TechLeistung p2tl(Long productId, TechLeistung techLeistung, boolean defaultLeistung,
            TechLeistung dependsOn, Integer priority) {
        Produkt2TechLeistung p2tl = new Produkt2TechLeistung();
        p2tl.setProdId(productId);
        p2tl.setTechLeistung(techLeistung);
        p2tl.setDefaultLeistung(defaultLeistung);
        if(dependsOn != null) {
            p2tl.setTechLeistungDependency(dependsOn.getId());
        }
        p2tl.setPriority(priority);
        return p2tl;
    }

    private static Auftrag2TechLeistung a2tl(TechLeistung techLeistung, long quantity) {
        Auftrag2TechLeistung a2tl = new Auftrag2TechLeistung();
        a2tl.setTechLeistungId(techLeistung.getId());
        a2tl.setQuantity(quantity);
        return a2tl;
    }

    private static Auftrag2TechLeistung a2tl(TechLeistung techLeistung, Date aktivVon, Date aktivBis, long quantity) {
        Auftrag2TechLeistung a2tl = a2tl(techLeistung, quantity);
        a2tl.setAktivVon(aktivVon);
        a2tl.setAktivBis(aktivBis);
        return a2tl;
    }

    private static TechLeistung tl(Long techLeistungId, String name, String typ, Long externLeistungNo, boolean autoExpire,
            boolean checkQuantity) {
        TechLeistung tl = tl(techLeistungId, name, typ, externLeistungNo, autoExpire);
        tl.setCheckQuantity(checkQuantity);
        return tl;
    }

    private static TechLeistung tl(Long techLeistungId, String name, String typ, Long externLeistungNo, boolean autoExpire) {
        TechLeistung tl = new TechLeistung();
        tl.setId(techLeistungId);
        tl.setName(name);
        tl.setTyp(typ);
        tl.setExternLeistungNo(externLeistungNo);
        // TODO was ist das?
        tl.setSnapshotRel(true);
        tl.setAutoExpire(autoExpire);
        return tl;
    }

    private static BAuftragLeistungView balv(long menge, TechLeistung techLeistung) {
        return balv(menge, techLeistung, null);
    }

    private static BAuftragLeistungView balv(long menge, TechLeistung techLeistung, Long chargeToOffsetDays) {
        LocalDate from = LocalDate.now().plusDays(1);
        BAuftrag ba = new BAuftrag();
        ba.setGueltigVon(DateConverterUtils.asDate(from));
        OE oe = new OE();
        BAuftragPos pos = new BAuftragPos();
        Leistung l = new Leistung();
        l.setExternLeistungNo(techLeistung.getExternLeistungNo());
        pos.setChargeFrom(DateConverterUtils.asDate(from));
        if(chargeToOffsetDays != null) {
            pos.setChargeTo(DateConverterUtils.asDate(from.plusDays(chargeToOffsetDays)));
        }
        BAuftragLeistungView balv = new BAuftragLeistungView(ba, oe, pos, l);
        balv.setMenge(menge);
        return balv;
    }

    private static BAuftragPos bap(long menge, Date gueltigVon, Date gueltigBis) {
        BAuftragPos pos = new BAuftragPos();
        pos.setMenge(menge);
        pos.setChargeFrom(gueltigVon);
        pos.setChargeTo(gueltigBis);
        return pos;
    }

    private static Pair<Long, Long> once(TechLeistung ls) {
        return diff(ls, 1);
    }

    private static Pair<Long, Long> diff(TechLeistung ls, long quantity) {
        return Pair.create(ls.getId(), quantity);
    }

    private static Pair<Date, Date> diffDate(Date aktivVon, Date aktivBis) {
        return Pair.create(aktivVon, aktivBis);
    }

    private static Pair<Long, Long> twice(TechLeistung ls) {
        return diff(ls, 2);
    }

    // @formatter:off
        TechLeistung down18 = tl(21L, "18000 down", "DOWNSTREAM", 10023L, false);
        TechLeistung up1 = tl(22L, "1000 up", "UPSTREAM", 10024L, false);
        TechLeistung down25 = tl(23L, "25000 down", "DOWNSTREAM", 10025L, false);
        TechLeistung down50 = tl(24L, "50000 down", "DOWNSTREAM", 10026L,false);
        TechLeistung up25 = tl(114L, "2500 up", "UPSTREAM", 10045L,false);
        TechLeistung down100 = tl(25L, "100.000 down", "DOWNSTREAM", 10027L, false);
        TechLeistung up10 = tl(28L, "10.000 up", "UPSTREAM", 10030L,false);
        TechLeistung egPort = tl(350L, "EG port", "EG_PORT", 20020L, false, true);
        TechLeistung noExtLstNo = tl(100L, "not configured", "INVALID", null, false);
        TechLeistung anyOther = tl(99L, "any other", "INVALID", 99999L, false);
        TechLeistung voIpTest = tl(492L, "100 Voip IPV6 testen", "DOWNSTREAM", 10246L, true, true);
        List<TechLeistung> tl = of(
                down18,
                up1,
                down25,
                up25,
                down100,
                up10,
                egPort,
                noExtLstNo,
                anyOther,
                voIpTest
                );
        List<Produkt2TechLeistung> p2tl_513 = of(
                p2tl(PRODUCT_ID, down18),
                p2tl(PRODUCT_ID, up1, true, down18),
                p2tl(PRODUCT_ID, down25),
                p2tl(PRODUCT_ID, up25, true, down25),
                p2tl(PRODUCT_ID, down50, false, null, 5),
                p2tl(PRODUCT_ID, down100, false, null, 10),
                p2tl(PRODUCT_ID, up10, true, down100),
                p2tl(PRODUCT_ID, voIpTest)
        );
        // @formatter:on

    @DataProvider
    public Object[][] findLeistungsDiffsWithoutTaifunDP() {
        // @formatter:off
        return new Object[][] {
                // Fehlertext, Produkt-TechLeistung,
                // Hurrican-Leistungen, Taifun-Leistungen, tl, erwartet Diff+, erwartet Diff-
                new Object[] { "Leistung zu viel im Hurrican-Auftrag", p2tl_513,
                        of(a2tl(down18, 1)), of(), tl,
                        ImmutableSet.of(), ImmutableSet.of(once(down18))},
                new Object[] { "Leistung im Hurrican-Auftrag zu wenig (direkt+default muss hinzu)", p2tl_513,
                        of(), of(balv(1, down18)), tl,
                        ImmutableSet.of(once(down18), once(up1)),  ImmutableSet.of()},
                new Object[] { "Leistung im Hurrican-Auftrag zu wenig (default muss hinzu)", p2tl_513,
                        of(a2tl(down18,1)), of(balv(1, down18)), tl,
                        ImmutableSet.of(once(up1)),  ImmutableSet.of()},
                new Object[] { "Leistungen identisch", p2tl_513,
                        of(a2tl(down18, 1), a2tl(up1, 1)), of(balv(1, down18)), tl,
                        ImmutableSet.of(), ImmutableSet.of()},
                new Object[] { "Leisungen identisch, jedoch Menge in Hurrican zu viel", p2tl_513,
                        of(a2tl(egPort, 2)), of(balv(1, egPort)), tl,
                        ImmutableSet.of(), ImmutableSet.of(once(egPort))},
                new Object[] { "Leisungen identisch, jedoch Menge in Hurrican zu wenig", p2tl_513,
                        of(a2tl(egPort, 1)), of(balv(3, egPort)), tl,
                        ImmutableSet.of(twice(egPort)), ImmutableSet.of()},
                new Object[] { "Technische Leistung im ext. Auftrag fuer Produkt ungueltig", p2tl_513,
                        of(), of(balv(1, anyOther)), tl,
                        ImmutableSet.of(), ImmutableSet.of()},
        };
        // @formatter:on
    }

    @DataProvider
    public Object[][] findLeistungsDiffsDP() {
        List<Object[]> data = Lists.newArrayList(findLeistungsDiffsWithoutTaifunDP());
        // @formatter:off
        data.add(new Object[] { "Leistung im Hurrican-Auftrag zu wenig (Prio)", p2tl_513,
                of(),
                of(balv(1, down18), balv(1, down100)), tl,
                ImmutableSet.of(once(down100), once(up10)),  ImmutableSet.of()});
        data.add(new Object[] { "Leistung im Hurrican-Auftrag zu wenig (Prio, down100 90Tage)", p2tl_513,
                of(),
                of(balv(1, down18), balv(1, down100, 90L)), tl,
                ImmutableSet.of(once(down100), once(up10)),  ImmutableSet.of()});
        data.add(new Object[] { "Leistung im Hurrican-Auftrag zu wenig (Prio, down100 expired)", p2tl_513,
                of(),
                of(balv(1, down18), balv(1, down100, -90L)), tl,
                ImmutableSet.of(once(down18), once(up1)),  ImmutableSet.of()});
        data.add(new Object[] { "Leistung im Hurrican-Auftrag zurueck von 100 auf 18 (Prio, down100 expired)", p2tl_513,
                of(a2tl(down100, 1), a2tl(up10, 1)),
                of(balv(1, down18), balv(1, down100, -90L)), tl,
                ImmutableSet.of(once(down18), once(up1)),  ImmutableSet.of(once(down100), once(up10))});
        // @formatter:on
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "findLeistungsDiffsDP")
    public void testFindLeistungsDiffs(String check, List<Produkt2TechLeistung> p2tl,
            List<Auftrag2TechLeistung> techLsExist, final List<BAuftragLeistungView> leistungViews,
            final List<TechLeistung> tl, Set<Pair<Long, Long>> expectedToAdd,
            Set<Pair<Long, Long>> expectedToDel)
            throws FindException {
        setupLeistungMocks(p2tl, techLsExist, leistungViews, tl);

        List<LeistungsDiffView> diff = sut.findLeistungsDiffs(AUFTRAG_ID, AUFTRAG_NO_ORIG, PRODUCT_ID);

        Set<Pair<Long, Long>> diffToAdd = Sets.newHashSet();
        Set<Pair<Long, Long>> diffToDel = Sets.newHashSet();
        for (LeistungsDiffView leistungsDiffView : diff) {
            if (leistungsDiffView.isZugang()) {
                diffToAdd.add(createDiff(leistungsDiffView));
            }
            else {
                diffToDel.add(createDiff(leistungsDiffView));
            }
        }
        assertThat(check + ": diffs to add should be equal", diffToAdd, equalTo(expectedToAdd));
        assertThat(check + ": diffs to del should be equal", diffToDel, equalTo(expectedToDel));
    }

    @DataProvider
    public Object[][] findLeistungsDiffsWithExpireDateInFutureDP() {
        List<Object[]> data = Lists.newArrayList();
        Date aktivVon =  DateConverterUtils.asDate(LocalDate.now().plusDays(1L));
        Date aktivBis = DateConverterUtils.asDate(LocalDate.now().plusDays(91L));

        // @formatter:off
        data.add(new Object[] { "aktivBis auf 90Tage in Zukunft gesetzt - Leistung in Hurrican anlegen)", p2tl_513,
                of(),
                of(balv(1, down100, 90L)), tl,
                ImmutableList.of(diffDate(aktivVon, null),diffDate(aktivVon, null)),new ArrayList<Pair<Date, Date>>()});
        data.add(new Object[] { "aktivBis auf 90Tage in Zukunft gesetzt - Leistung ist autoExpire. Leistung in Hurrican anlegen)", p2tl_513,
                of(),
                of(balv(1, voIpTest, 90L)), tl,
                ImmutableList.of(diffDate(aktivVon, null)), new ArrayList<Pair<Date, Date>>()});
        data.add(new Object[] { "aktivBis auf 90 Tage in Zukunft gesetzt - Leistung in Hurrican kündigen)", p2tl_513,
                of(a2tl(down100, aktivVon, null,1),a2tl(up10, aktivVon, null, 1)),
                of(balv(1, down100, 90L)), tl,
                new ArrayList<Pair<Date, Date>>(),ImmutableList.of(diffDate(aktivVon, aktivBis),diffDate(aktivVon, aktivBis))});
        data.add(new Object[] { "aktivBis auf 90 Tage in Zukunft gesetzt - Leistung ist autoExpire - Leistung in Hurrican nicht kündigen)", p2tl_513,
                of(a2tl(voIpTest, aktivVon, null,1)),
                of(balv(1, voIpTest, 90L)), tl,
                new ArrayList<Pair<Date, Date>>(),new ArrayList<Pair<Date, Date>>()});
        data.add(new Object[] { "aktivBis nicht gesetzt - keine Leistungsdifferenz)", p2tl_513,
                of(a2tl(down100, aktivVon, null,1),a2tl(up10, aktivVon, null, 1)),
                of(balv(1, down100, null)), tl,
                new ArrayList<Pair<Date, Date>>(),new ArrayList<Pair<Date, Date>>()});
        // @formatter:on
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "findLeistungsDiffsWithExpireDateInFutureDP")
    public void testFindLeistungsDiffsWithExpireDateInFuture(String check, List<Produkt2TechLeistung> p2tl,
            List<Auftrag2TechLeistung> techLsExist, final List<BAuftragLeistungView> leistungViews,
            final List<TechLeistung> tl, List<Pair<Date, Date>>expectedDiffAdd, List<Pair<Date, Date>>expectedDiffDel )
            throws FindException {
        setupLeistungMocks(p2tl, techLsExist, leistungViews, tl);
        List<Pair<Date, Date>> diffDateAdd = new ArrayList<>();
        List<Pair<Date, Date>> diffDateDel = new ArrayList<>();

        Builder<Long, BAuftragPos> builder = ImmutableMap.<Long, BAuftragPos>builder();
        com.google.common.collect.ImmutableListMultimap.Builder<Long, BAuftragPos> extLeistNo2PositionenBuilder =
                ImmutableListMultimap.<Long, BAuftragPos>builder();
        Date aktivVon = DateConverterUtils.asDate(LocalDate.now().plusDays(1L));
        Date aktivBis = leistungViews.isEmpty() ? null : leistungViews.stream().map(p -> p.getAuftragPosGueltigBis()).collect(Collectors.toList()).get(0);
        for (BAuftragLeistungView bAuftragLeistungView : leistungViews) {
            BAuftragPos bap = bap(bAuftragLeistungView.getMenge(),aktivVon, bAuftragLeistungView.getAuftragPosGueltigBis());
            builder.put(bAuftragLeistungView.getExternLeistungNo(), bap);
            extLeistNo2PositionenBuilder.put(bAuftragLeistungView.getExternLeistungNo(), bap);
        }

        List<LeistungsDiffView> diff = sut.findLeistungsDiffs(AUFTRAG_ID, AUFTRAG_NO_ORIG, PRODUCT_ID,
                new ExterneAuftragsLeistungen(builder.build(),
                        extLeistNo2PositionenBuilder.build(),
                        leistungViews,
                        leistungViews),
                aktivBis
        );


        for (LeistungsDiffView leistungsDiffView : diff) {
            if (leistungsDiffView.isZugang()) {
                diffDateAdd.add(createDiffDate(leistungsDiffView));
            }
            else {
                diffDateDel.add(createDiffDate(leistungsDiffView));
            }
        }

        assertThat(check + ": aktivVon/aktivBis should be equal", diffDateAdd, equalTo(expectedDiffAdd));
        assertThat(check + ": aktivVon/aktivBis should be equal", diffDateDel, equalTo(expectedDiffDel));
    }

    @DataProvider
    public Object[][] findLeistungsDiffsWithExpireDateInPastDP() {
        List<Object[]> data = Lists.newArrayList();

        Date aktivVon =  DateConverterUtils.asDate(LocalDate.now().minusDays(10L));
        Date aktivBis = DateConverterUtils.asDate(LocalDate.now().minusDays(0L));

        // @formatter:off
       data.add(new Object[] { "aktivBis auf 1 Tage in Vergangenheit gesetzt - Leistung in Hurrican nicht kündigen)", p2tl_513,
                of(a2tl(down100, aktivVon, aktivBis,1),a2tl(up10, aktivVon, aktivBis, 1)),
                of(balv(1, down100, -1L)), tl,
                 new ArrayList<Pair<Date, Date>>(),new ArrayList<Pair<Date, Date>>()});
        data.add(new Object[] { "aktivBis auf 1 Tage in Vergangenheit gesetzt - Leistung ist autoExpire - Leistung in Hurrican nicht kündigen)", p2tl_513,
                of(a2tl(voIpTest, aktivVon, aktivBis,1)),
                of(balv(1, voIpTest, -1L)), tl,
                new ArrayList<Pair<Date, Date>>(),new ArrayList<Pair<Date, Date>>()});
        data.add(new Object[] { "Taifun Leistung beendet, aber gültige Leistung vorhanden - keine Leistungsdifferenz)", p2tl_513,
                of(a2tl(down100, aktivVon, null,1),a2tl(up10, aktivVon, null, 1)),
                of(balv(1, down100, null),balv(1,down100,-1L)), tl,
                new ArrayList<Pair<Date, Date>>(),new ArrayList<Pair<Date, Date>>()});
        // @formatter:on
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "findLeistungsDiffsWithExpireDateInPastDP")
    public void testFindLeistungsDiffsWithExpireDateInPast(String check, List<Produkt2TechLeistung> p2tl,
            List<Auftrag2TechLeistung> techLsExist, final List<BAuftragLeistungView> leistungViews,
            final List<TechLeistung> tl, List<Pair<Date, Date>>expectedDiffAdd, List<Pair<Date, Date>>expectedDiffDel )
            throws FindException {
        setupLeistungMocks(p2tl, techLsExist, leistungViews, tl);
        List<Pair<Date, Date>> diffDateAdd = new ArrayList<>();
        List<Pair<Date, Date>> diffDateDel = new ArrayList<>();

        Map<Long, BAuftragPos> positionen = Maps.newHashMap();
        ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen = ArrayListMultimap.create();

        Date aktivVon = DateConverterUtils.asDate(LocalDate.now().minusDays(10L));
        Date aktivBis = leistungViews.isEmpty() ? null : leistungViews.stream().map(p -> p.getAuftragPosGueltigBis()).collect(Collectors.toList()).get(0);
        for (BAuftragLeistungView bAuftragLeistungView : leistungViews) {
            BAuftragPos bap = bap(bAuftragLeistungView.getMenge(),aktivVon, bAuftragLeistungView.getAuftragPosGueltigBis());
            positionen.put(bAuftragLeistungView.getExternLeistungNo(), bap);
            extLeistNo2BAuftragPositionen.put(bAuftragLeistungView.getExternLeistungNo(), bap);
        }

        List<LeistungsDiffView> diff = sut.findLeistungsDiffs(AUFTRAG_ID, AUFTRAG_NO_ORIG, PRODUCT_ID,
                new ExterneAuftragsLeistungen(positionen,
                        extLeistNo2BAuftragPositionen,
                        leistungViews,
                        leistungViews),
                aktivBis
        );


        for (LeistungsDiffView leistungsDiffView : diff) {
            if (leistungsDiffView.isZugang()) {
                diffDateAdd.add(createDiffDate(leistungsDiffView));
            }
            else {
                diffDateDel.add(createDiffDate(leistungsDiffView));
            }
        }

        assertThat(check + ": aktivVon/aktivBis should be equal", diffDateAdd, equalTo(expectedDiffAdd));
        assertThat(check + ": aktivVon/aktivBis should be equal", diffDateDel, equalTo(expectedDiffDel));
    }

    private Pair<Date, Date> createDiffDate(LeistungsDiffView diffView) {
         return Pair.create(diffView.getAktivVon(), diffView.getAktivBis());
    }


    /**
     * Test ohne Taifun (sprich Wholesale).
     */
    @Test(dataProvider = "findLeistungsDiffsWithoutTaifunDP")
    public void testFindLeistungsDiffsWithoutTaifun(String check, List<Produkt2TechLeistung> p2tl,
            List<Auftrag2TechLeistung> techLsExist, final List<BAuftragLeistungView> leistungViews,
            final List<TechLeistung> tl, Set<Pair<Long, Long>> expectedToAdd,
            Set<Pair<Long, Long>> expectedToDel) throws Exception {
        setupMocks4TechLs(techLsExist);
        setupMocks4TechLsDao(p2tl, tl);

        Builder<Long, BAuftragPos> builder = ImmutableMap.<Long, BAuftragPos>builder();
        com.google.common.collect.ImmutableListMultimap.Builder<Long, BAuftragPos> extLeistNo2PositionenBuilder =
                ImmutableListMultimap.<Long, BAuftragPos>builder();
        for (BAuftragLeistungView bAuftragLeistungView : leistungViews) {
            BAuftragPos bap = bap(bAuftragLeistungView.getMenge(), new Date(), null);
            builder.put(bAuftragLeistungView.getExternLeistungNo(), bap);
            extLeistNo2PositionenBuilder.put(bAuftragLeistungView.getExternLeistungNo(), bap);
        }

        List<LeistungsDiffView> diff = sut.findLeistungsDiffs(AUFTRAG_ID, null, PRODUCT_ID,
                new ExterneAuftragsLeistungen(builder.build(),
                        extLeistNo2PositionenBuilder.build(),
                        leistungViews,
                        leistungViews),
                null
        );

        verifyNoMoreInteractions(billingAuftragServiceMock);
        verify(ccAuftragServiceMock, never()).findAuftragDaten4OrderNoOrig(anyLong());
        Set<Pair<Long, Long>> diffToAdd = Sets.newHashSet();
        Set<Pair<Long, Long>> diffToDel = Sets.newHashSet();
        for (LeistungsDiffView leistungsDiffView : diff) {
            if (leistungsDiffView.isZugang()) {
                diffToAdd.add(createDiff(leistungsDiffView));
            }
            else {
                diffToDel.add(createDiff(leistungsDiffView));
            }
        }
        assertThat(check + ": diffs to add should be equal", diffToAdd, equalTo(expectedToAdd));
        assertThat(check + ": diffs to del should be equal", diffToDel, equalTo(expectedToDel));
    }

    private Pair<Long, Long> createDiff(LeistungsDiffView diffView) {
        return Pair.create(diffView.getTechLeistungId(), diffView.getQuantity());
    }

    private void setupLeistungMocks(final List<Produkt2TechLeistung> p2tl, List<Auftrag2TechLeistung> techLsExist,
            final List<BAuftragLeistungView> leistungViews, final List<TechLeistung> allTl) throws FindException {
        setupMocks4TechLs(techLsExist);
        setupMocks4LeistungsViews(leistungViews);
        setupMocks4TechLsDao(p2tl, allTl);
    }

    private void setupMocks4TechLsDao(final List<Produkt2TechLeistung> p2tl, final List<TechLeistung> allTl) {
        when(techLsDaoMock.findProdukt2TechLs(anyLong(), anyString(), anyBoolean())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long productId = (Long) args[0];
            Boolean defLeistung = Optional.ofNullable((Boolean) args[2]).orElse(Boolean.FALSE);
            return p2tl.stream().filter(p2t -> p2t.getProdId().equals(productId) && defLeistung.equals(p2t.getDefaultLeistung()))
                    .collect(Collectors.toList());
        });
        setupTechLsOnMocks(allTl);
        when(techLsDaoMock.findTechLeistungen(eq(true))).thenReturn(allTl);
        when(techLsDaoMock.queryByExample(anyObject(), eq(TechLeistung.class))).thenAnswer(
                invocation -> {
                    Object[] args = invocation.getArguments();
                    TechLeistung example = (TechLeistung) args[0];
                    List<TechLeistung> result = Lists.newArrayList();
                    for (TechLeistung atl : allTl) {
                        if (atl.getTyp().equals(example.getTyp())) {
                            result.add(atl);
                        }
                    }
                    return result;
                }
        );
    }

    private void setupMocks4LeistungsViews(final List<BAuftragLeistungView> leistungViews) throws FindException {
        when(billingAuftragServiceMock.findAuftragLeistungViews4Auftrag(eq(AUFTRAG_NO_ORIG), anyBoolean(), anyBoolean())).thenAnswer(
                invocationOnMock -> {
                    boolean onlyAct = (Boolean) invocationOnMock.getArguments()[1];
                    return leistungViews.stream().filter(balv -> onlyAct ? balv.isActiveAuftragpos() : true)
                            .collect(Collectors.toList());
                }
        );
        when(billingAuftragServiceMock.findAuftragPositionen(eq(AUFTRAG_NO_ORIG), eq(false), anyListOf(Long.class)))
                .thenAnswer(invocation -> ImmutableList.copyOf(Iterables.transform(leistungViews,
                                input -> bap(input.getMenge(), new Date(), null)
                        ))
                );
    }

    private void setupMocks4TechLs(List<Auftrag2TechLeistung> techLsExist) throws FindException {
        BAuftrag ba = new BAuftrag();
        ba.setGueltigVon(new Date());

        when(billingAuftragServiceMock.findAuftrag(eq(AUFTRAG_NO_ORIG))).thenReturn(ba);

        AuftragDaten ad = new AuftragDaten();
        ad.setAuftragId(AUFTRAG_ID);
        when(ccAuftragServiceMock.findAuftragDaten4OrderNoOrig(eq(AUFTRAG_NO_ORIG))).thenReturn(of(ad));
        when(ccAuftragServiceMock.findAuftragDatenByAuftragIdTx(AUFTRAG_ID)).thenReturn(ad);
        when(auftrag2TechLeistungDaoMock.findActiveA2TLGrouped(eq(AUFTRAG_ID))).thenReturn(techLsExist);
        when(auftrag2TechLeistungDaoMock.findActiveA2TLGrouped(eq(AUFTRAG_ID), anyObject())).thenReturn(techLsExist);
        when(auftrag2TechLeistungDaoMock.findAuftragTechLeistungen(eq(AUFTRAG_ID), anyLong(), anyBoolean()))
                .thenReturn(techLsExist);
    }

    private void setupTechLsOnMocks(final List<TechLeistung> tl) {
        when(techLsDaoMock.findById(anyInt(), eq(TechLeistung.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            for (TechLeistung techLeistung : tl) {
                if (techLeistung.getId().equals(args[0])) {
                    return techLeistung;
                }
            }
            return null;
        });
        when(techLsDaoMock.findTechLeistungen(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            for (TechLeistung techLeistung : tl) {
                if ((techLeistung.getExternLeistungNo() != null)
                        && techLeistung.getExternLeistungNo().equals(args[0])) {
                    return of(techLeistung);
                }
            }
            return of();
        });
    }

    @Test
    public void modifyAuftrag2TechLeistungen() throws FindException, StoreException {
        Date pastStart = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date pastEnd = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate toModify = LocalDate.now().plusDays(10);
        LocalDate modifiedDate = LocalDate.now().plusDays(20);

        Date toModifyAsDate = Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date modifiedDateAsDate = Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // @formatter:off
        Auftrag auftrag = new AuftragBuilder().withRandomId().setPersist(false).build();
        Auftrag2TechLeistung notToChangeBecauseInPast = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(pastStart).withAktivBis(pastEnd).setPersist(false).build();
        Auftrag2TechLeistung changeStart = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(toModifyAsDate).withAktivBis(null).setPersist(false).build();
        Auftrag2TechLeistung changeEnd1 = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(toModifyAsDate).withAktivBis(toModifyAsDate).setPersist(false).build();
        Auftrag2TechLeistung changeEnd2 = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(null).withAktivBis(toModifyAsDate).setPersist(false).build();
        // @formatter:on

        doReturn(Arrays.asList(notToChangeBecauseInPast, changeStart, changeEnd1, changeEnd2)).when(sut)
                .findAuftrag2TechLeistungen(
                        auftrag.getAuftragId(), null, false);

        List<Auftrag2TechLeistung> result = sut.modifyAuftrag2TechLeistungen(auftrag.getAuftragId(), toModify, modifiedDate, null);
        assertNotEmpty(result);
        assertEquals(result.size(), 3);
        for (Auftrag2TechLeistung a2tlModified : result) {
            if (NumberTools.equal(a2tlModified.getId(), changeStart.getId())) {
                assertThat(a2tlModified.getAktivVon(), equalTo(modifiedDateAsDate));
            }
            else if (NumberTools.equal(a2tlModified.getId(), changeEnd1.getId())) {
                assertThat(a2tlModified.getAktivBis(), equalTo(modifiedDateAsDate));
            }
            else if (NumberTools.equal(a2tlModified.getId(), changeEnd2.getId())) {
                assertThat(a2tlModified.getAktivBis(), equalTo(modifiedDateAsDate));
            }
        }
    }


    @Test
    public void cancelAuftrag2TechLeistungen() throws FindException, StoreException {
        Date pastStart = Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate toModify = LocalDate.now().plusDays(10);

        // @formatter:off
        Auftrag auftrag = new AuftragBuilder().withRandomId().setPersist(false).build();
        AuftragAktion aktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();
        Auftrag2TechLeistung notToChange = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(pastStart).withAktivBis(null).setPersist(false).build();
        Auftrag2TechLeistung notToChangeWithHurEndDate = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(pastStart).withAktivBis(DateTools.getHurricanEndDate()).setPersist(false).build();
        Auftrag2TechLeistung toActivate = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(pastStart).withAktivBis(
                Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant())).withAuftragAktionsIdRemove(aktion.getId()).setPersist(false).build();
        Auftrag2TechLeistung toDelete = new Auftrag2TechLeistungBuilder().withRandomId().withAktivVon(
                Date.from(toModify.atStartOfDay(ZoneId.systemDefault()).toInstant())).withAktivBis(null).withAuftragAktionsIdAdd(aktion.getId()).setPersist(false).build();
        // @formatter:on

        doReturn(Arrays.asList(notToChange, notToChangeWithHurEndDate, toActivate, toDelete)).when(sut).findAuftrag2TechLeistungen(
                auftrag.getAuftragId(), null, false);

        sut.cancelAuftrag2TechLeistungen(auftrag.getAuftragId(), aktion);

        verify(auftrag2TechLeistungDaoMock).delete(toDelete);
        verify(sut).saveAuftrag2TechLeistung(toActivate);
        assertThat(toActivate.getAktivBis(), equalTo(null));
        verify(sut, times(0)).saveAuftrag2TechLeistung(notToChange);
        verify(sut, times(0)).saveAuftrag2TechLeistung(notToChangeWithHurEndDate);
    }

    @DataProvider
    public Object[][] queryIPModeDP() {
        final Long fix_always_v4 = TechLeistung.ID_FIXED_IP_AND_ALWAYS_ON;
        final Long add_fix_v4 = TechLeistung.ID_ADDITIONAL_IP;
        final Long fix_v4 = TechLeistung.ID_FIXED_IP;
        final Long dyn_v4 = TechLeistung.ID_DYNAMIC_IP_V4;
        final Long dyn_v6 = TechLeistung.ID_DYNAMIC_IP_V6;
        final Long fix_v6 = TechLeistung.ID_DHCP_V6_PD;
        final Long other = TechLeistung.ID_BUSINESS_CPE;

        // @formatter:off
        return new Object[][] {
                new Object[] { new HashSet<Long>(),                                                IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(other)),                            IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(dyn_v4, other)),                    IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(fix_v4, other)),                    IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(fix_always_v4, other)),             IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(fix_v4, add_fix_v4, other)),        IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(fix_always_v4, add_fix_v4, other)), IpMode.IPV4 },
                new Object[] { new HashSet<>(Arrays.asList(dyn_v6, other)),                    IpMode.DS_LITE },
                new Object[] { new HashSet<>(Arrays.asList(fix_v6, other)),                    IpMode.DS_LITE },
                new Object[] { new HashSet<>(Arrays.asList(dyn_v4, dyn_v6, other)),            IpMode.DUAL_STACK },
                new Object[] { new HashSet<>(Arrays.asList(fix_v4, fix_v6, other)),            IpMode.DUAL_STACK },
                new Object[] { new HashSet<>(Arrays.asList(dyn_v4, fix_v6, other)),            IpMode.DUAL_STACK },
                new Object[] { new HashSet<>(Arrays.asList(fix_v4, dyn_v6, other)),            IpMode.DUAL_STACK },
        };
        // @formatter:on
    }

    @Test(dataProvider = "queryIPModeDP")
    public void testQueryIPMode(Set<Long> techLeistungIds, IpMode ipModeExpected) {
        doReturn(techLeistungIds).when(sut).findTechLeistungenNo4Auftrag(anyLong(), any(LocalDate.class));
        IpMode ipModeResult = sut.queryIPMode(1L, LocalDate.now());
        assertThat(ipModeExpected, equalTo(ipModeResult));
    }


    @DataProvider(name = "findTechLeistungen4VerlaufDP")
    public Object[][] findTechLeistungen4VerlaufDP() {
        Auftrag2TechLeistung zugang = new Auftrag2TechLeistungBuilder().withVerlaufIdReal(1L).build();
        Auftrag2TechLeistung zugangDifferntVerlauf = new Auftrag2TechLeistungBuilder().withVerlaufIdReal(2L).build();
        Auftrag2TechLeistung abgang = new Auftrag2TechLeistungBuilder().withVerlaufIdKuend(1L).build();
        Auftrag2TechLeistung zuUndAbgang = new Auftrag2TechLeistungBuilder().withVerlaufIdReal(1L)
                .withVerlaufIdKuend(1L).build();

        List<Auftrag2TechLeistung> a2tls = Arrays.asList(zugang, zugangDifferntVerlauf, abgang, zuUndAbgang);
        return new Object[][] {
                { a2tls, true, 2 },
                { a2tls, false, 3 },
                { null, true, 0 },
        };
    }


    @Test(dataProvider = "findTechLeistungen4VerlaufDP")
    public void findTechLeistungen4Verlauf(List<Auftrag2TechLeistung> auftrag2TechLeistungen, boolean onlyZugang, int expectedSize) throws FindException {
        doReturn(auftrag2TechLeistungen).when(sut).findAuftrag2TechLeistungen4Verlauf(anyLong());
        when(techLsDaoMock.findById(anyLong(), eq(TechLeistung.class))).thenReturn(new TechLeistung());

        List<TechLeistung> result = sut.findTechLeistungen4Verlauf(1L, onlyZugang);
        assertEquals(result.size(), expectedSize);
    }

    public void hasTechLeistungEndsInFutureWithExtLeistungNo_True() throws FindException {
        final Date twoDaysBefore = addDays(new Date(), -2);
        final Date twoDaysAfter = addDays(new Date(), 2);

        final TechLeistungBuilder techLeistungBuilder = new TechLeistungBuilder()
                .withId(1L)
                .withGueltigVon(twoDaysBefore)
                .withGueltigBis(twoDaysAfter);
        final TechLeistung techLeistung = techLeistungBuilder
                .build();

        final Auftrag2TechLeistung auftrag2TechLeistungen = new Auftrag2TechLeistungBuilder()
                .withVerlaufIdReal(1L)
                .withAktivVon(twoDaysBefore)
                .withAktivBis(twoDaysAfter)
                .withTechleistungBuilder(techLeistungBuilder)
                .build();
        doReturn(Arrays.asList(techLeistung)).when(techLsDaoMock).findTechLeistungen(anyLong());
        doReturn(Arrays.asList(auftrag2TechLeistungen)).when(auftrag2TechLeistungDaoMock).findAuftragTechLeistungen(anyLong(), anyObject(), anyObject(), anyBoolean());

        boolean result = sut.hasTechLeistungEndsInFutureWithExtLeistungNo(1L, 1L);
        assertTrue(result);
    }

    public void hasTechLeistungEndsInFutureWithExtLeistungNo_False() throws FindException {
        final Date twoDaysBefore = addDays(new Date(), -2);
        final Date yesterday = addDays(new Date(), -1);

        final TechLeistungBuilder techLeistungBuilder = new TechLeistungBuilder()
                .withId(1L)
                .withGueltigVon(twoDaysBefore)
                .withGueltigBis(yesterday);
        final TechLeistung techLeistung = techLeistungBuilder
                .build();

        doReturn(Arrays.asList(techLeistung)).when(techLsDaoMock).findTechLeistungen(anyLong());
        doReturn(Collections.EMPTY_LIST).when(auftrag2TechLeistungDaoMock).findAuftragTechLeistungen(anyLong(), anyObject(), anyObject(), anyBoolean());

        boolean result = sut.hasTechLeistungEndsInFutureWithExtLeistungNo(1L, 1L);
        assertFalse(result);
    }

    @DataProvider(name = "deviceNecessaryDataProvider")
    public Object[][] deviceNecessaryDataProvider() {
        // @formatter:off
        return new Object[][] {
                { true , false, false, false },  // VOIP_TK zugeordnet  --> kein Device notwendig
                { false, true , false, false },  // VOIP_PMX zugeordnet --> kein Device notwendig
                { false, false, true , true },  // 'weiterer Eg-Port' zugeordnet --> kein Device notwendig
                { true,  true,  false, false },  // Kombinatorik aus Leistungen --> kein Device notwendig
                { true,  false,  true, false },  // Kombinatorik aus Leistungen --> kein Device notwendig
                { false, true,  true, false },   // Kombinatorik aus Leistungen --> kein Device notwendig
                { false, false, false, true },   // keine der Leistungen VOIP_TK, VOIP_PMX bzw. 'weiterer Eg-Port' --> Device ist notwendig
        };
        // @formatter:on
    }

    @Test(dataProvider = "deviceNecessaryDataProvider")
    public void deviceNecessary(boolean voipTkActive, boolean voipPmxActive, boolean egPortAddActive,
            boolean expectedResult) throws FindException {
        Long auftragId = 1L;
        Date execDate = new Date();
        // @formatter:off
        doReturn(voipTkActive).when(sut).isTechLeistungActive(auftragId,
                TechLeistung.ExterneLeistung.VOIP_TK.leistungNo, execDate);
        doReturn(voipPmxActive).when(sut).isTechLeistungActive(auftragId,
                TechLeistung.ExterneLeistung.VOIP_PMX.leistungNo, execDate);
        doReturn(egPortAddActive).when(sut).isTechLeistungActive(auftragId,
                TechLeistung.ExterneLeistung.EGPORT_ADD.leistungNo, execDate);
        // @formatter:on
        assertEquals(sut.deviceNecessary(auftragId, execDate), expectedResult);
    }

    @DataProvider(name = "findTechLeistungen4AuftragWithDiffsDP")
    public Object[][] findTechLeistungen4AuftragWithDiffsDP() {
        TechLeistung voipLs = new TechLeistungBuilder().withId(1L).withTyp(TechLeistung.TYP_VOIP).build();
        TechLeistung downLs = new TechLeistungBuilder().withId(2L).withTyp(TechLeistung.TYP_DOWNSTREAM).build();

        LeistungsDiffView diffPast = createLeistungsDiffView(1L, Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffCurrentV = createLeistungsDiffView(1L, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffCurrentD = createLeistungsDiffView(2L, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffFuture = createLeistungsDiffView(1L, Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        // @formatter:off
        return new Object[][] {
                { Arrays.asList() , TechLeistung.TYP_VOIP, now, Arrays.asList(), null },
                { Arrays.asList(diffPast) , TechLeistung.TYP_VOIP, now, Arrays.asList(), null },
                { Arrays.asList(diffPast, diffFuture) , TechLeistung.TYP_VOIP, now, Arrays.asList(voipLs), voipLs },
                { Arrays.asList(diffPast, diffCurrentD) , TechLeistung.TYP_VOIP, now, Arrays.asList(downLs), null },
                { Arrays.asList(diffPast, diffCurrentV) , TechLeistung.TYP_VOIP, now, Arrays.asList(voipLs), voipLs },
                { Arrays.asList(diffPast, diffCurrentV, diffCurrentD) , TechLeistung.TYP_VOIP, now, Arrays.asList(voipLs, downLs), voipLs },
        };
        // @formatter:on
    }

    private LeistungsDiffView createLeistungsDiffView(Long techLeistungId, Date aktivVon) {
        LeistungsDiffView result = new LeistungsDiffView();
        result.setTechLeistungId(techLeistungId);
        result.setAktivVon(aktivVon);
        return result;
    }

    @Test(dataProvider = "findTechLeistungen4AuftragWithDiffsDP")
    public void testFindTechLeistungen4AuftragWithDiffs(List<LeistungsDiffView> diffs, String lsTyp, Date validDate,
            List<TechLeistung> techLeistungen, TechLeistung expectedTechLeistung) throws FindException {
        for (TechLeistung techLeistung : techLeistungen) {
            doReturn(techLeistung).when(sut).findTechLeistung(techLeistung.getId());
        }

        List<TechLeistung> result = sut.findTechLeistungen4Auftrag(diffs, lsTyp, validDate);
        if (expectedTechLeistung == null) {
            assertEmpty(result);
        }
        else {
            assertNotNull(result);
            assertTrue(NumberTools.equal(expectedTechLeistung.getId(), result.get(0).getId()));
        }
    }

    @DataProvider(name = "testHasVoipLeistungFromThenOnWithDiffsDP")
    public Object[][] testHasVoipLeistungFromThenOnWithDiffsDP() {
        List<TechLeistung> empty = Collections.emptyList();
        List<TechLeistung> filled = Arrays.asList(new TechLeistung());
        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // @formatter:off
        return new Object[][] {
                { empty,  now,  empty,  tomorrow, false },
                { empty,  now,  null,   null,     false },
                { filled, now,  null,   null,     true },
                { empty,  now,  filled, tomorrow, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testHasVoipLeistungFromThenOnWithDiffsDP")
    public void testHasVoipLeistungFromThenOn(List<TechLeistung> firstList, Date firstDate,
            List<TechLeistung> secondList, Date secondDate, boolean expected) throws FindException {
        doReturn(firstList).when(sut).findTechLeistungen4Auftrag(anyList(), anyString(), eq(firstDate));
        doReturn(secondDate).when(sut).getFirstFutureTechLsDate(anyList());
        doReturn(secondList).when(sut).findTechLeistungen4Auftrag(anyList(), anyString(), eq(secondDate));

        boolean result = sut.hasVoipLeistungFromThenOn(Collections.emptyList(), firstDate);
        assertTrue(result == expected);
    }

    @DataProvider(name = "testGetFirstFutureTechLsDateWithDiffsDP")
    public Object[][] testGetFirstFutureTechLsDateWithDiffsDP() {
        LeistungsDiffView diffPast = createLeistungsDiffView(null, Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffCurrent = createLeistungsDiffView(null, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffFuture = createLeistungsDiffView(null, Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        LeistungsDiffView diffFuture2 = createLeistungsDiffView(null, Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));


        // @formatter:off
        return new Object[][] {
                { Collections.emptyList(), null },
                { Arrays.asList(diffPast), null },
                { Arrays.asList(diffCurrent), null },
                { Arrays.asList(diffFuture), diffFuture.getAktivVon() },
                { Arrays.asList(diffFuture2, diffFuture), diffFuture.getAktivVon() },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testGetFirstFutureTechLsDateWithDiffsDP")
    public void testGetFirstFutureTechLsDate(List<LeistungsDiffView> diffs, Date expected) throws FindException {
        Date result = sut.getFirstFutureTechLsDate(diffs);
        if (expected == null) {
            assertNull(result);
        }
        else {
            assertTrue(DateTools.isDateEqual(result, expected));
        }
    }
}
