/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2012 10:58:54
 */
package de.augustakom.hurrican.service.billing.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Sets;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.dao.billing.AdresseDAO;
import de.augustakom.hurrican.dao.billing.AuftragDAO;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.BAuftragPosBuilder;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungViewBuilder;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RufnummerService;

/**
 * Testklasse fuer BillingAuftragServiceImpl
 */
@Test(groups = { BaseTest.UNIT })
public class BillingAuftragServiceImplTest extends BaseTest {

    @Spy
    private BillingAuftragServiceImpl sut;

    @Mock
    private AuftragDAO auftragDaoMock;
    @Mock
    private AdresseDAO adresseDAO;
    @Mock
    private RufnummerService rufnummerService;
    @Mock
    private KundenService kundenService;

    @BeforeMethod
    void setUp() {
        sut = new BillingAuftragServiceImpl();
        initMocks(this);
        sut.setDAO(auftragDaoMock);
        sut.adresseDAO = adresseDAO;
    }

    @DataProvider(name = "findAuftragLeistungViewsByExtProdNoAndExtMiscNOsDP")
    public Object[][] findAuftragLeistungViewsByExtProdNoAndExtMiscNOsDP() {
        final Long extProdNo = 1L;
        final Long extMiscNo = 1L;
        List<BAuftragLeistungView> lstngen = new ArrayList<BAuftragLeistungView>(2);

        BAuftragLeistungView lstWithExtProdAndWithMisc = createBAuftragLeistungView(extProdNo, extMiscNo, null);
        BAuftragLeistungView lstWithExtProdAndWithoutMisc = createBAuftragLeistungView(extProdNo, null, null);
        BAuftragLeistungView lstWithoutExtProdAndWithMisc = createBAuftragLeistungView(null, extMiscNo, null);
        BAuftragLeistungView lstWithDifferentNos = createBAuftragLeistungView(extProdNo - 1, extMiscNo - 1, null);

        Collections.addAll(lstngen,
                lstWithExtProdAndWithMisc,
                lstWithExtProdAndWithoutMisc,
                lstWithoutExtProdAndWithMisc,
                lstWithDifferentNos);

        return new Object[][] {
                {
                        extProdNo,
                        extMiscNo,
                        lstngen,
                        new BAuftragLeistungView[] { lstWithExtProdAndWithMisc, lstWithExtProdAndWithoutMisc,
                                lstWithoutExtProdAndWithMisc } },
                { null, null, lstngen, null },
                { extProdNo, null, lstngen,
                        new BAuftragLeistungView[] { lstWithExtProdAndWithMisc, lstWithExtProdAndWithoutMisc } },
                { null, extMiscNo, lstngen,
                        new BAuftragLeistungView[] { lstWithExtProdAndWithMisc, lstWithoutExtProdAndWithMisc } },
        };
    }

    @Test(dataProvider = "findAuftragLeistungViewsByExtProdNoAndExtMiscNOsDP")
    public void findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(Long extProdNo, Long extMiscNo,
            List<BAuftragLeistungView> leistungenUnfiltered, BAuftragLeistungView[] expectedResult)
            throws FindException {
        final Long auftragNoOrig = Long.MAX_VALUE;

        List<BAuftragLeistungView> foundServices = new ArrayList<BAuftragLeistungView>();
        foundServices.addAll(leistungenUnfiltered);

        doReturn(foundServices).when(sut).findAuftragLeistungViews4Auftrag(auftragNoOrig, false, false);
        List<BAuftragLeistungView> result = sut.findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(auftragNoOrig,
                extProdNo,
                (extMiscNo != null) ? Arrays.asList(extMiscNo) : null);

        if (expectedResult != null) {
            assertThat(result, containsInAnyOrder(expectedResult));
        }
        else {
            assertEmpty(result);
        }
    }

    @DataProvider(name = "hasUnchargedServiceElementsWithExtMiscNoDP")
    public Object[][] hasUnchargedServiceElementsWithExtMiscNoDP() {
        BAuftragLeistungView charged = createBAuftragLeistungView(1L, Leistung.EXT_MISC_NO_MONTAGE_MNET, new Date());
        BAuftragLeistungView uncharged = createBAuftragLeistungView(1L, Leistung.EXT_MISC_NO_MONTAGE_MNET, null);

        // @formatter:off
        return new Object[][] {
                { Arrays.asList(charged)            , false },
                { Arrays.asList(uncharged)          , true },
                { Arrays.asList(charged, uncharged) , true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "hasUnchargedServiceElementsWithExtMiscNoDP")
    @SuppressWarnings("unchecked")
    public void hasUnchargedServiceElementsWithExtMiscNo(List<BAuftragLeistungView> views, boolean expectedResult)
            throws FindException {
        BAuftrag billingOrder = new BAuftrag();
        billingOrder.setOeNoOrig(Long.valueOf(10));

        doReturn(billingOrder).when(sut).findAuftrag(any(Long.class));
        doReturn(views).when(sut).findAuftragLeistungViewsByExtProdNoAndExtMiscNOs(any(Long.class), any(Long.class),
                any(List.class));
        boolean result = sut
                .hasUnchargedServiceElementsWithExtMiscNo(Long.MAX_VALUE, Leistung.EXT_MISC_NO_MONTAGE_MNET);
        assertEquals(result, expectedResult);
    }

    private BAuftragLeistungView createBAuftragLeistungView(Long extProdNo, Long extMiscNo, Date chargedUntil) {
        return new BAuftragLeistungViewBuilder()
                .withExternProduktNo(extProdNo)
                .withExternMiscNo(extMiscNo)
                .withAuftragPosChargedUntil(chargedUntil)
                .setPersist(false)
                .build();
    }

    @Test(expectedExceptions = FindException.class)
    public void findAuftragIdsByGeoIdExceptionThrown() throws FindException {
        Long geoId = 1L;
        when(auftragDaoMock.findAuftragIdsByGeoId(geoId, true)).thenThrow(RuntimeException.class);
        sut.findAuftragIdsByGeoId(geoId, true);
    }

    @Test(expectedExceptions = FindException.class)
    public void findAuftragIdsByGeoIdNull() throws FindException {
        sut.findAuftragIdsByGeoId(null, true);
    }

    @Test
    public void findAuftragIdsByGeoId() throws FindException {
        Long geoId = 1L;
        List<Long> expectedAuftragIds = Arrays.asList(1L, 2L, 3L);
        when(auftragDaoMock.findAuftragIdsByGeoId(geoId, true)).thenReturn(expectedAuftragIds);
        final Set<Long> auftragIdsByGeoId = sut.findAuftragIdsByGeoId(geoId, true);
        verify(auftragDaoMock).findAuftragIdsByGeoId(geoId, true);
        assertNotNull(auftragIdsByGeoId);
        assertEquals(auftragIdsByGeoId.size(), expectedAuftragIds.size());
    }


    @DataProvider(name = "getBasicOrderInformationDP")
    public Object[][] getBasicOrderInformationDP() {
        Rufnummer mainDn = new RufnummerBuilder()
                .withOnKz("0821").withDnBase("123456")
                .withMainNumber(true)
                .build();
        Rufnummer noMainDn = new RufnummerBuilder()
                .withOnKz("0821").withDnBase("123457")
                .withMainNumber(false)
                .build();
        Rufnummer mainDnNonBillable = new RufnummerBuilder()
                .withOnKz("0821").withDnBase("123456")
                .withMainNumber(true)
                .withNonBillable(true)
                .build();

        return new Object[][] {
                { "0821/123456", Arrays.asList(mainDn, noMainDn) },
                { "0821/123456", Arrays.asList(mainDn) },
                { null, Arrays.asList(mainDnNonBillable, noMainDn) },
        };
    }


    @Test(dataProvider = "getBasicOrderInformationDP")
    public void getBasicOrderInformation(String expectedDn, List<Rufnummer> rufnummern) throws FindException, ServiceNotFoundException {
        AdresseBuilder adresseBuilder = new AdresseBuilder().withRandomAdresseNo();
        BAuftrag auftrag = new BAuftragBuilder()
                .withAuftragNoOrig(Long.valueOf(99))
                .withKundeNo(Long.valueOf(123))
                .withApAddressBuilder(adresseBuilder)
                .withKuendigungsdatum(new Date())
                .build();

        doReturn(auftrag).when(sut).findAuftrag(auftrag.getAuftragNoOrig());
        doReturn(rufnummerService).when(sut).getBillingService(RufnummerService.class);
        when(rufnummerService.findAllRNs4Auftrag(auftrag.getAuftragNoOrig())).thenReturn(rufnummern);
        when(adresseDAO.findById(adresseBuilder.get().getAdresseNo(), Adresse.class)).thenReturn(adresseBuilder.get());

        BAuftragVO result = sut.getBasicOrderInformation(auftrag.getAuftragNoOrig());
        assertNotNull(result);
        assertEquals(result.getAuftragNoOrig(), auftrag.getAuftragNoOrig());
        assertEquals(result.getKundeNo(), auftrag.getKundeNo());
        assertEquals(result.getKuendigungsdatum(), auftrag.getKuendigungsdatum());
        assertEquals(result.getMainDn(), expectedDn);
        assertEquals(result.getCustomerName(), adresseBuilder.get().getCombinedNameData());
        assertEquals(result.getStreet(), adresseBuilder.get().getCombinedStreetData());
        assertEquals(result.getCity(), adresseBuilder.get().getCombinedOrtOrtsteil());
    }

    @Test
    public void getBasicOrderInformationShouldReturnNull() throws FindException {
        doReturn(null).when(sut).findAuftrag(any(Long.class));
        assertNull(sut.getBasicOrderInformation(Long.valueOf(1)));
    }


    @Test
    public void findProduktLeistungen4Auftrag() throws FindException {
        BAuftragLeistungView productView = new BAuftragLeistungViewBuilder().withExternProduktNo(Long.valueOf(99)).build();
        BAuftragLeistungView miscView = new BAuftragLeistungViewBuilder().withExternProduktNo(null).withExternMiscNo(Long.valueOf(1)).build();

        Long orderNoOrig = Long.valueOf(123456);
        doReturn(Arrays.asList(productView, miscView)).when(sut).findAuftragLeistungViews4Auftrag(orderNoOrig, false, true);

        List<BAuftragLeistungView> result = sut.findProduktLeistungen4Auftrag(orderNoOrig);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), productView);
    }

    @Test
    public void testFindEndstelleAnsprechpartner() throws Exception {
        BAuftrag auftrag = new BAuftragBuilder()
                .withAuftragNoOrig(Long.valueOf(99))
                .withKundeNo(Long.valueOf(123))
                .withBearbeiterKundeEmail("bearbEmail")
                .withBearbeiterKundeFax("9876")
                .withBearbeiterKundeName("bearbName")
                .withBearbeiterKundeRN("1234")
                .build();
        Kunde kunde = new KundeBuilder().withName("kName").withKundeNo(123L).withEmail("kundeMail").withRnFax("4567")
                .withName("kundeName").withVorname("hans").withHauptRufnummer("234567").withRnMobile("0176")
                .withKundeTyp(Kunde.CUSTOMER_TYPE_BUSINESS).build();

        doReturn(auftrag).when(sut).findAuftrag(auftrag.getAuftragNoOrig());
        doReturn(kunde).when(kundenService).findKunde(kunde.getKundeNo());
        doReturn(kundenService).when(sut).getKundenService();
        // geschäftlich
        EndstelleAnsprechpartnerView endstelleAnsprechpartner = sut.findEndstelleAnsprechpartner(auftrag
                .getAuftragNoOrig());
        assertNotNull(endstelleAnsprechpartner);
        assertEquals(endstelleAnsprechpartner.getEmail(), auftrag.getBearbeiterKundeEmail());
        assertEquals(endstelleAnsprechpartner.getFax(), auftrag.getBearbeiterKundeFax());
        assertEquals(endstelleAnsprechpartner.getName(), auftrag.getBearbeiterKundeName());
        assertEquals(endstelleAnsprechpartner.getTelefon(), auftrag.getBearbeiterKundeRN());

        // privat
        kunde.setKundenTyp("privat");
        endstelleAnsprechpartner = sut.findEndstelleAnsprechpartner(auftrag.getAuftragNoOrig());
        assertNotNull(endstelleAnsprechpartner);
        assertEquals(endstelleAnsprechpartner.getEmail(), kunde.getEmail());
        assertEquals(endstelleAnsprechpartner.getFax(), kunde.getRnFax());
        assertEquals(endstelleAnsprechpartner.getName(), kunde.getName());
        assertEquals(endstelleAnsprechpartner.getVorname(), kunde.getVorname());
        assertEquals(endstelleAnsprechpartner.getTelefon(), kunde.getHauptRufnummer());
        assertEquals(endstelleAnsprechpartner.getMobil(), kunde.getRnMobile());
    }


    public void testFindAuftragNoOrigsWithExtLeistungNos() throws FindException {
        BAuftragPos pos1 = new BAuftragPosBuilder().withAuftragNoOrig(1L).setPersist(false).build();
        BAuftragPos pos2 = new BAuftragPosBuilder().withAuftragNoOrig(1L).setPersist(false).build();
        BAuftragPos pos3 = new BAuftragPosBuilder().withAuftragNoOrig(2L).setPersist(false).build();

        when(auftragDaoMock.findAuftragPos4Auftrag(eq(null), anyListOf(Long.class), eq(false), any(Date.class),
                any(Date.class))).thenReturn(Arrays.asList(pos1));
        when(auftragDaoMock.findAuftragPos4Auftrag(eq(null), anyListOf(Long.class), eq(true), any(Date.class),
                any(Date.class))).thenReturn(Arrays.asList(pos2, pos3));


        Set<Long> result = sut.findAuftragNoOrigsWithExtLeistungNos(Sets.newHashSet(1L), LocalDate.now(), 7);
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
    }


}
