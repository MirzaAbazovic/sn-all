/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.03.2012 10:24:43
 */
package de.augustakom.hurrican.service.wholesale.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.AuftragStatus.*;
import static de.augustakom.hurrican.model.wholesale.WholesaleContactPerson.*;
import static de.augustakom.hurrican.model.wholesale.WholesaleProductName.*;
import static de.mnet.common.tools.DateConverterUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;
import de.augustakom.hurrican.model.cc.AuftragAktionBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.fttx.EqVlanBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleContactPerson;
import de.augustakom.hurrican.model.wholesale.WholesaleEkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleProduct;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ExterneAuftragsLeistungen;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.wholesale.LineIdNotFoundException;
import de.augustakom.hurrican.service.wholesale.ModifyPortReservationDateToEarlierDateException;
import de.augustakom.hurrican.service.wholesale.ProductGroupNotSupportedException;
import de.augustakom.hurrican.service.wholesale.WholesaleServiceException;
import de.augustakom.hurrican.service.wholesale.WholesaleTechnicalException;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = UNIT)
public class WholesaleServiceImplTest extends BaseTest {

    private static final String LINE_ID = "DEU.MNET.4354356";
    private final LocalDate today = LocalDate.now();

    @InjectMocks
    @Spy
    private WholesaleServiceImpl cut;

    @Mock
    private AvailabilityService availabilityServiceMock;
    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private HVTService hvtServiceMock;
    @Mock
    private CCAuftragService auftragServiceMock;
    @Mock
    private RangierungsService rangierungsServiceMock;
    @Mock
    private CCLeistungsService leistungsService;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private EkpFrameContractService ekpFrameContractService;
    @Mock
    private VlanService vlanService;
    @Mock
    private BAService baServiceMock;
    @Mock
    private AKUserService akUserService;
    @Mock
    private PhysikService physikService;
    @Mock
    private HardwareDAO hardwareDAO;
    @Mock
    private AnsprechpartnerService ansprechpartnerService;

    @BeforeMethod
    public void setUp() {
        cut = new WholesaleServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    private WholesaleReservePortRequest buildWholesaleReservePortRequest(WholesaleProduct wholesaleProduct) {
        final WholesaleEkpFrameContract ekpFrameContract = new WholesaleEkpFrameContract();
        ekpFrameContract.setEkpId("QSC");
        ekpFrameContract.setEkpId("QSC-001");
        final WholesaleReservePortRequest request = new WholesaleReservePortRequest();
        request.setProduct(wholesaleProduct);
        request.setDesiredExecutionDate(LocalDate.now().plusDays(1));
        request.setEkpFrameContract(ekpFrameContract);
        request.setGeoId(RandomTools.createLong());
        request.setLageTaeOnt("WerWeissWo");
        request.setZeitFensterAnfang(LocalTime.MIN);
        request.setZeitFensterAnfang(LocalTime.MAX);
        return request;
    }

    private WholesaleContactPerson createContactPersonWithRole(final String role) {
        final WholesaleContactPerson wholesaleContactPerson = new WholesaleContactPerson();
        wholesaleContactPerson.setEmailAddress(RandomTools.createString());
        wholesaleContactPerson.setFaxNumber(RandomTools.createString());
        wholesaleContactPerson.setFirstName(RandomTools.createString());
        wholesaleContactPerson.setLastName(RandomTools.createString());
        wholesaleContactPerson.setMobilePhoneNumber(RandomTools.createString());
        wholesaleContactPerson.setRole(role);
        wholesaleContactPerson.setPhoneNumber(RandomTools.createString());
        return wholesaleContactPerson;
    }

    private WholesaleProduct createProduct(final WholesaleProductName productName) {
        WholesaleProduct product = new WholesaleProduct();
        product.setName(productName);
        return product;
    }

    private LocalDate hurricanEndDate() {
        return DateTools.getHurricanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private WholesaleReleasePortRequest buildWholesaleReleasePortRequest(String lineId) {
        WholesaleReleasePortRequest request = new WholesaleReleasePortRequest();
        request.setSessionId(1L);
        request.setLineId(lineId);
        request.setReleaseDate(LocalDate.now().plusDays(10));
        return request;
    }

    @Test
    public void testReservePortFtthProduct() throws Exception {
        final WholesaleReservePortRequest request = buildWholesaleReservePortRequest(createProduct(FTTH_50));

        final EkpFrameContract ekpFrameContract = new EkpFrameContractBuilder().withRandomId().build();
        final GeoId geoId = new GeoIdBuilder().withId(request.getGeoId()).build();
        final Endstelle endstelleB = endstelleB();
        final Auftrag auftrag = new AuftragBuilder().withRandomId().build();

        mockCreateAuftrag(auftrag);

        mockFindGeoId(request, geoId);

        mockFindPossibleGeoIds2TechLocations(geoId);

        mockFindEkpFrameContract(request, ekpFrameContract);

        mockAssignEkpFrameContract2Auftrag(request, ekpFrameContract);

        mockCreateEndstellen(endstelleB);

        mockFindVBZById();

        mockFindEkp4Auftrag(request, ekpFrameContract, auftrag);

        final WholesaleReservePortResponse result = cut.reservePort(request);

        assertThat(result.getHurricanAuftragId(), equalTo(auftrag.getAuftragId().toString()));
        assertThat(result.getA10nsp(), isEmptyOrNullString());
        assertThat(result.getA10nspPort(), isEmptyOrNullString());
        assertThat(result.getSvlanEkp(), isEmptyOrNullString());
        assertTrue(result.isManuellePortzuweisung());
        verify(auftragServiceMock).saveAuftragWholesale(any(AuftragWholesale.class));
    }

    @Test
    public void reservePort_returnsA10NspAndSvlanData() throws Exception {
        final WholesaleReservePortRequest request = buildWholesaleReservePortRequest(createProduct(FTTB_50));
        final EkpFrameContract ekpFrameContract = new EkpFrameContractBuilder().withRandomId().build();
        final GeoId geoId = new GeoIdBuilder().withId(request.getGeoId()).build();
        final Endstelle endstelleB = endstelleB();
        final Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        final Rangierung rangierung = new RangierungBuilder().withRandomId().build();
        final HWOlt olt = new HWOltBuilder().withRandomId().build();
        final A10NspPort a10NspPort = a10Nsp(olt);
        final HWRack rack = new HWDpoBuilder()
                .withRandomId()
                .build();
        final EqVlan eqVlan = eqVlan();
        final AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftrag.getAuftragId())
                .withProdId(Produkt.PROD_ID_WHOLESALE_FTTX)
                .withBemerkungen("Description beforeUpdate, ")
                .build();

        mockCreateAuftrag(auftrag);
        mockFindGeoId(request, geoId);
        mockFindPossibleGeoIds2TechLocations(geoId);
        mockFindEkpFrameContract(request, ekpFrameContract);
        mockAssignEkpFrameContract2Auftrag(request, ekpFrameContract);
        mockCreateEndstellen(endstelleB);
        mockAssignRangierung2ES(endstelleB, rangierung);
        mockFindVBZById();
        mockFindEkp4Auftrag(request, ekpFrameContract, auftrag);
        mockAssignEqVlans(request, ekpFrameContract, auftrag, eqVlan);
        mockFindRack4EqIn(rangierung, rack);
        mockFindA10Nsp(a10NspPort);
        mockFindOltForRack(rack);
        mockFindEqVlans4Auftrag(request, auftrag, eqVlan);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId())).thenReturn(auftragDaten);

        final WholesaleReservePortResponse result = cut.reservePort(request);

        assertThat(result.getA10nsp(), equalTo(a10NspPort.getA10Nsp().getName()));
        assertThat(result.getA10nspPort(), equalTo(a10NspPort.getVbz().getVbz()));
        assertThat(result.getSvlanEkp(), equalTo(String.valueOf(eqVlan.getSvlanEkp())));
        assertThat(auftragDaten.getBemerkungen(), allOf(containsString(a10NspPort.getA10Nsp().getName()),
                containsString(a10NspPort.getVbz().getVbz())));
        verify(auftragServiceMock).saveAuftragDaten(eq(auftragDaten), eq(false));
    }

    private void mockFindOltForRack(HWRack rack) {
        when(hardwareDAO.findHwOltForRack(rack))
                .thenReturn(new HWOltBuilder().withRandomId().build());
    }

    private void mockFindRack4EqIn(Rangierung rangierung, HWRack rack) {
        when(hardwareDAO.findRack4EqInOfRangierung(rangierung.getId()))
                .thenReturn(rack);
    }

    private void mockFindEkp4Auftrag(WholesaleReservePortRequest request, EkpFrameContract ekpFrameContract, Auftrag auftrag) throws FindException {
        when(ekpFrameContractService.findEkp4AuftragOrDefaultMnet(
                auftrag.getAuftragId(),
                request.getDesiredExecutionDate(),
                false)
        ).thenReturn(ekpFrameContract);
    }

    private void mockAssignRangierung2ES(Endstelle endstelleB, Rangierung rangierung) throws FindException, StoreException {
        when(rangierungsServiceMock.assignRangierung2ES(endstelleB.getId(), null))
                .thenReturn(rangierung);
    }

    private void mockAssignEkpFrameContract2Auftrag(WholesaleReservePortRequest request, EkpFrameContract ekpFrameContract) {
        when(ekpFrameContractService.assignEkpFrameContract2Auftrag(
                eq(ekpFrameContract),
                any(Auftrag.class),
                eq(request.getDesiredExecutionDate()),
                isNull(AuftragAktion.class))
        ).thenReturn(new Auftrag2EkpFrameContract());
    }

    private void mockFindPossibleGeoIds2TechLocations(GeoId geoId) throws FindException {
        when(availabilityServiceMock.findPossibleGeoId2TechLocations(eq(geoId), anyLong()))
                .thenReturn(Collections.singletonList(new GeoId2TechLocationBuilder().build()));
    }

    private void mockCreateAuftrag(Auftrag auftrag) throws StoreException {
        when(auftragServiceMock.createAuftrag(
                isNull(Long.class),
                any(AuftragDaten.class),
                any(AuftragTechnik.class),
                isNull(Long.class),
                isNull(IServiceCallback.class))
        ).thenReturn(auftrag);
    }

    private A10NspPort a10Nsp(HWOlt olt) {
        return new A10NspPortBuilder()
                .withRandomId()
                .withOlts(Collections.singleton(olt))
                .withA10NspBuilder(
                        new A10NspBuilder()
                                .withRandomId()
                                .withName(RandomTools.createString())
                )
                .withVbzBuilder(new VerbindungsBezeichnungBuilder().withVbz(RandomTools.createString()))
                .build();
    }

    @Test
    public void reservePort_AnsprechpartnerUndAdresseWerdenAngelegt() throws Exception {
        final WholesaleReservePortRequest request = buildWholesaleReservePortRequest(createProduct(FTTB_50));
        request.setContactPersons(ImmutableList.of(createContactPersonWithRole(ROLE_STANDORTA)));
        request.setLageTaeOnt(RandomTools.createString());
        final GeoId geoId = new GeoIdBuilder().withId(request.getGeoId()).build();
        final Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        final EkpFrameContract ekpFrameContract = new EkpFrameContractBuilder().withRandomId().build();

        mockReservePortTechnicalData(request, geoId, auftrag, ekpFrameContract);

        cut.reservePort(request);

        final ArgumentCaptor<Ansprechpartner> ansprechpartnerCaptor = ArgumentCaptor.forClass(Ansprechpartner.class);
        verify(ansprechpartnerService).saveAnsprechpartner(ansprechpartnerCaptor.capture());
        final Ansprechpartner ansprechpartnerHur = ansprechpartnerCaptor.getValue();
        final WholesaleContactPerson ansprechpartnerWhox = Iterables.getOnlyElement(request.getContactPersons());

        assertThat(ansprechpartnerHur.getAuftragId(), equalTo(auftrag.getAuftragId()));
        assertThat(ansprechpartnerHur.getTypeRefId(), equalTo(Ansprechpartner.Typ.ENDSTELLE_B.refId()));
        assertThat(ansprechpartnerHur.getAddress().getEmail(), equalTo(ansprechpartnerWhox.getEmailAddress()));
        assertThat(ansprechpartnerHur.getAddress().getFax(), equalTo(ansprechpartnerWhox.getFaxNumber()));
        assertThat(ansprechpartnerHur.getAddress().getHandy(), equalTo(ansprechpartnerWhox.getMobilePhoneNumber()));
        assertThat(ansprechpartnerHur.getAddress().getVorname(), equalTo(ansprechpartnerWhox.getFirstName()));
        assertThat(ansprechpartnerHur.getAddress().getName(), equalTo(ansprechpartnerWhox.getLastName()));
        assertThat(ansprechpartnerHur.getAddress().getAddressType(), equalTo(CCAddress.ADDRESS_TYPE_WHOLESALE_FFM_DATA));
        assertThat(ansprechpartnerHur.getAddress().getPlz(), equalTo(geoId.getZipCode()));
        assertThat(ansprechpartnerHur.getAddress().getOrt(), equalTo(geoId.getCity()));
        assertThat(ansprechpartnerHur.getAddress().getStrasse(), equalTo(geoId.getStreet()));
        assertThat(ansprechpartnerHur.getAddress().getStrasseAdd(), equalTo(request.getLageTaeOnt()));
        assertThat(ansprechpartnerHur.getAddress().getHausnummerZusatz(), equalTo(geoId.getHouseNumExtension()));
        assertThat(ansprechpartnerHur.getAddress().getNummer(), equalTo(geoId.getHouseNum()));
        assertThat(ansprechpartnerHur.getAddress().getOrtsteil(), equalTo(geoId.getDistrict()));
    }

    private void mockReservePortTechnicalData(WholesaleReservePortRequest request, GeoId geoId, Auftrag auftrag, EkpFrameContract ekpFrameContract) throws StoreException, FindException {
        final Endstelle endstelleB = endstelleB();
        final Rangierung rangierung = new RangierungBuilder().withRandomId().build();
        final HWOlt olt = new HWOltBuilder().withRandomId().build();
        final A10NspPort a10NspPort = a10Nsp(olt);
        final HWRack rack = new HWDpoBuilder().withRandomId().build();
        final EqVlan eqVlan = eqVlan();

        mockCreateAuftrag(auftrag);
        mockFindGeoId(request, geoId);
        mockFindPossibleGeoIds2TechLocations(geoId);
        mockFindEkpFrameContract(request, ekpFrameContract);
        mockAssignEkpFrameContract2Auftrag(request, ekpFrameContract);
        mockCreateEndstellen(endstelleB);
        mockAssignRangierung2ES(endstelleB, rangierung);
        mockFindVBZById();
        mockFindEkp4Auftrag(request, ekpFrameContract, auftrag);
        mockAssignEqVlans(request, ekpFrameContract, auftrag, eqVlan);
        mockFindRack4EqIn(rangierung, rack);
        mockFindA10Nsp(a10NspPort);
        mockFindOltForRack(rack);

        mockFindEqVlans4Auftrag(request, auftrag, eqVlan);
    }

    @Test
    public void reservePort_EkpWirdZugeordnet() throws Exception {
        final WholesaleReservePortRequest request = buildWholesaleReservePortRequest(createProduct(FTTB_50));
        request.setContactPersons(ImmutableList.of(createContactPersonWithRole(ROLE_STANDORTA)));
        request.setLageTaeOnt(RandomTools.createString());
        final EkpFrameContract ekpFrameContract = new EkpFrameContractBuilder().withRandomId().build();
        final Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        final GeoId geoId = new GeoIdBuilder().withId(request.getGeoId()).build();
        
        mockReservePortTechnicalData(request, geoId, auftrag, ekpFrameContract);

        cut.reservePort(request);

        verify(ekpFrameContractService).assignEkpFrameContract2Auftrag(eq(ekpFrameContract), eq(auftrag), eq(LocalDate.now()), isNull(AuftragAktion.class));

    }


    private void mockFindEqVlans4Auftrag(WholesaleReservePortRequest request, Auftrag auftrag, EqVlan eqVlan) throws FindException {
        when(vlanService.findEqVlans4Auftrag(auftrag.getAuftragId(), request.getDesiredExecutionDate()))
                .thenReturn(Collections.singletonList(eqVlan));
    }

    private void mockFindA10Nsp(A10NspPort a10NspPort) {
        when(ekpFrameContractService.findA10NspPort(any(EkpFrameContract.class), anyLong()))
                .thenReturn(a10NspPort);
    }

    private void mockAssignEqVlans(WholesaleReservePortRequest request, EkpFrameContract ekpFrameContract, Auftrag auftrag, EqVlan eqVlan) throws FindException, StoreException {
        when(vlanService.assignEqVlans(
                eq(ekpFrameContract),
                eq(auftrag.getAuftragId()),
                anyLong(),
                eq(request.getDesiredExecutionDate()),
                isNull(AuftragAktion.class)))
                .thenReturn(Collections.singletonList(eqVlan));
    }

    private void mockFindVBZById() throws FindException {
        when(physikService.findVerbindungsBezeichnungById(anyLong()))
                .thenReturn(new VerbindungsBezeichnungBuilder()
                        .withRandomId()
                        .withVbz(RandomTools.createString())
                        .build());
    }

    private void mockCreateEndstellen(Endstelle endstelleB) throws StoreException {
        when(endstellenServiceMock.createEndstellen(
                any(AuftragTechnik.class),
                eq(Produkt.ES_TYP_NUR_B),
                isNull(Long.class))
        ).thenReturn(Collections.singletonList(endstelleB));
    }

    private void mockFindEkpFrameContract(WholesaleReservePortRequest request, EkpFrameContract ekpFrameContract) {
        when(ekpFrameContractService.findEkpFrameContract(
                request.getEkpFrameContract().getEkpId(),
                request.getEkpFrameContract().getEkpFrameContractId())
        ).thenReturn(ekpFrameContract);
    }

    private void mockFindGeoId(WholesaleReservePortRequest request, GeoId geoId) throws FindException {
        when(availabilityServiceMock.findGeoId(request.getGeoId()))
                .thenReturn(geoId);
    }

    private EqVlan eqVlan() {
        return new EqVlanBuilder()
                .withRandomId()
                .withSvlanEkp(RandomTools.createInteger())
                .build();
    }

    private Endstelle endstelleB() {
        return new EndstelleBuilder()
                .withRandomId()
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .build();
    }


    @Test
    public void getHurricanProduktId() {
        WholesaleReservePortRequest request = buildWholesaleReservePortRequest(createProduct(FTTB_50));

        Long result = cut.getHurricanProduktId(request);
        assertEquals(result, FTTB_50.hurricanProduktId);
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void assignTechLocationExpectExceptionBecauseOfUnknownGeoId() throws FindException {
        when(availabilityServiceMock.findGeoId(any(Long.class))).thenReturn(null);
        cut.assignTechLocation(buildWholesaleReservePortRequest(createProduct(FTTB_50)), new Endstelle(), Produkt.PROD_ID_WHOLESALE_FTTX);
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void assignTechLocationExpectExceptionBecauseOfFindTechLocations() throws FindException {
        GeoId geoId = new GeoIdBuilder().setPersist(false).build();
        when(availabilityServiceMock.findGeoId(any(Long.class))).thenReturn(geoId);
        when(availabilityServiceMock.findPossibleGeoId2TechLocations(any(GeoId.class), any(Long.class))).thenThrow(
                new FindException("Can't find it!"));

        cut.assignTechLocation(buildWholesaleReservePortRequest(createProduct(FTTB_50)), new Endstelle(), Produkt.PROD_ID_WHOLESALE_FTTX);
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void assignTechLocationExpectExceptionBecauseOfEmptyTechLocations() throws FindException {
        GeoId geoId = new GeoIdBuilder().setPersist(false).build();
        when(availabilityServiceMock.findGeoId(any(Long.class))).thenReturn(geoId);
        when(availabilityServiceMock.findPossibleGeoId2TechLocations(any(GeoId.class), any(Long.class))).thenReturn(
                null);

        cut.assignTechLocation(buildWholesaleReservePortRequest(createProduct(FTTB_50)), new Endstelle(), Produkt.PROD_ID_WHOLESALE_FTTX);
    }

    @Test
    public void assignTechLocation() throws FindException, StoreException {
        GeoIdBuilder geoIdBuilder = new GeoIdBuilder().withId(1L).setPersist(false);
        GeoId geoId = geoIdBuilder.build();
        HVTStandortBuilder fttbStdBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).withRandomId().setPersist(false);
        HVTStandortBuilder ftthStdBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTH).withRandomId().setPersist(false);

        GeoId2TechLocation geoId2TechLocation1 = new GeoId2TechLocationBuilder().withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(fttbStdBuilder).build();
        GeoId2TechLocation geoId2TechLocation2 = new GeoId2TechLocationBuilder().withGeoIdBuilder(geoIdBuilder)
                .withHvtStandortBuilder(ftthStdBuilder).build();

        when(availabilityServiceMock.findGeoId(any(Long.class))).thenReturn(geoId);
        when(availabilityServiceMock.findPossibleGeoId2TechLocations(any(GeoId.class), any(Long.class))).thenReturn(
                Arrays.asList(geoId2TechLocation1, geoId2TechLocation2));

        Endstelle endstelle = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).withRandomId().build();

        cut.assignTechLocation(buildWholesaleReservePortRequest(createProduct(FTTB_50)), endstelle, Produkt.PROD_ID_WHOLESALE_FTTX);

        assertEquals(endstelle.getHvtIdStandort(), fttbStdBuilder.get().getHvtIdStandort());
        assertEquals(endstelle.getGeoId(), geoId.getId());
        verify(endstellenServiceMock).saveEndstelle(endstelle);
        verify(hvtServiceMock).findAnschlussart4HVTStandort(endstelle.getHvtIdStandort());
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void assignRangierungExpectExceptionIfResultIsNull() throws FindException, StoreException {
        when(rangierungsServiceMock.assignRangierung2ES(any(Long.class), any(IServiceCallback.class))).thenReturn(
                null);
        cut.assignRangierung(new Endstelle());
    }

    @Test
    public void assignRangierungShouldThrowWholesaleTechExceptionOnStoreException() throws FindException, StoreException {
        try {
            when(rangierungsServiceMock.assignRangierung2ES(any(Long.class), any(IServiceCallback.class)))
                    .thenThrow(new StoreException());
            cut.assignRangierung(new Endstelle());

            fail();
        }
        catch (WholesaleTechnicalException e) {
            //Hier gibt es nichts zu tun
        }
        catch (Exception e) {
            fail("Exception not of expected type!");
        }
    }

    @DataProvider(name = "modifyPortProductGroupNotSupportedDataProvider")
    public Object[][] modifyPortProductGroupNotSupportedDataProvider() {
        //@formatter:off
        return new Object[][] {
                { WholesaleProductName.FTTH_100},
                { WholesaleProductName.FTTH_300},
        };
        //@formatter:on
    }

    @Test(expectedExceptions = { ProductGroupNotSupportedException.class }, dataProvider = "modifyPortProductGroupNotSupportedDataProvider")
    public void modifyPortProductGroupNotSupported(final WholesaleProductName wholesaleProductName) {
        WholesaleProduct wholesaleProdukt = new WholesaleProduct();
        wholesaleProdukt.setName(wholesaleProductName);
        WholesaleModifyPortRequest request = new WholesaleModifyPortRequest();
        request.setProduct(wholesaleProdukt);
        cut.modifyPort(request);
    }

    @DataProvider
    public Object[][] modifyPortDataProvider() {
        return new Object[][] {
                { LocalDate.now().plusDays(1) },
                { LocalDate.now() }
        };
    }

    @Test(dataProvider = "modifyPortDataProvider")
    public void modifyPort(final LocalDate desiredExecutionDate) throws Exception {
        final String lineId = "0815";
        AuftragBuilder auftragBuilder = new AuftragBuilder().withId(Long.MAX_VALUE);
        Auftrag auftrag = auftragBuilder.build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragBuilder(auftragBuilder).withProdId(Produkt.PROD_ID_WHOLESALE_FTTX).build();
        EkpFrameContractBuilder ekpFrameContractBuilder = new EkpFrameContractBuilder().withRandomId();
        Auftrag2EkpFrameContract auftrag2EkpFrame = new Auftrag2EkpFrameContractBuilder().withAuftragBuilder(
                auftragBuilder).withEkpFrameContractBuilder(ekpFrameContractBuilder).build();
        WholesaleProduct wholesaleProduct = new WholesaleProduct();
        wholesaleProduct.setName(FTTB_50);
        List<LeistungsDiffView> leistungsDiffViews = Collections.emptyList();
        WholesaleModifyPortRequest modifyPortRequest = createWhsModifyPortRequest(lineId, auftrag2EkpFrame,
                wholesaleProduct, desiredExecutionDate);
        List<EqVlan> eqVlans = Collections.singletonList(new EqVlanBuilder().build());

        when(auftragServiceMock.findActiveOrderByLineId(eq(lineId), eq(today))).thenReturn(auftrag);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId())).thenReturn(auftragDaten);
        //@formatter:off
        when(leistungsService.findLeistungsDiffs(eq(auftrag.getId()), isNull(Long.class),
                eq(wholesaleProduct.getName().hurricanProduktId), any(ExterneAuftragsLeistungen.class), eq(Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))))
                    .thenReturn(leistungsDiffViews);
        when(leistungsService.synchTechLeistung4Auftrag(eq(auftrag.getId()), eq(wholesaleProduct.getName().hurricanProduktId),
                eq(Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant())), eq(true),
                isNull(Long.class), eq(leistungsDiffViews), any(AuftragAktion.class)))
                    .thenReturn(null);
        when(ekpFrameContractService.findEkpFrameContract(eq(modifyPortRequest.getEkpId()), eq(modifyPortRequest.getEkpContractId())))
            .thenReturn(auftrag2EkpFrame.getEkpFrameContract());
        when(ekpFrameContractService.assignEkpFrameContract2Auftrag(eq(auftrag2EkpFrame.getEkpFrameContract()), eq(auftrag), eq(desiredExecutionDate), any(AuftragAktion.class)))
            .thenReturn(auftrag2EkpFrame);
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(auftrag.getId()), eq(desiredExecutionDate))).thenReturn(auftrag2EkpFrame);
        when(ekpFrameContractService.findEkp4AuftragOrDefaultMnet(eq(auftrag.getId()), eq(desiredExecutionDate), eq(false))).thenReturn(ekpFrameContractBuilder.get());
        when(vlanService.assignEqVlans(eq(ekpFrameContractBuilder.get()), eq(auftrag.getId()), eq(auftragDaten.getProdId()), eq(desiredExecutionDate), any(AuftragAktion.class))).thenReturn(eqVlans);
        //@formatter:on

        WholesaleModifyPortResponse response = cut.modifyPort(modifyPortRequest);

        assertEquals(response.getExecutionDate(), desiredExecutionDate);
        assertEquals(response.getLineId(), lineId);

        verify(auftragServiceMock).findActiveOrderByLineId(eq(lineId), eq(today));
        verify(leistungsService).findLeistungsDiffs(eq(auftrag.getId()), isNull(Long.class),
                eq(wholesaleProduct.getName().hurricanProduktId), any(ExterneAuftragsLeistungen.class), eq(Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        //@formatter:off
        verify(leistungsService).synchTechLeistung4Auftrag(eq(auftrag.getId()), eq(wholesaleProduct.getName().hurricanProduktId),
                eq(Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant())), eq(true),
                isNull(Long.class),
                eq(leistungsDiffViews),
                any(AuftragAktion.class));
        //@formatter:on
        verify(ekpFrameContractService).findEkpFrameContract(eq(modifyPortRequest.getEkpId()),
                eq(modifyPortRequest.getEkpContractId()));
        verify(ekpFrameContractService).assignEkpFrameContract2Auftrag(eq(auftrag2EkpFrame.getEkpFrameContract()),
                eq(auftrag), eq(desiredExecutionDate), any(AuftragAktion.class));
        verify(ekpFrameContractService).findAuftrag2EkpFrameContract(eq(auftrag.getId()), eq(desiredExecutionDate));
        verify(cut).logAndCheckAktion(eq(auftrag.getAuftragId()), isNull(Long.class), eq(AktionType.MODIFY_PORT),
                eq(desiredExecutionDate));
    }

    private WholesaleModifyPortRequest createWhsModifyPortRequest(final String lineId,
            Auftrag2EkpFrameContract auftrag2EkpFrame, WholesaleProduct wholesaleProduct, LocalDate desiredExecutionDate) {
        WholesaleModifyPortRequest modifyPortRequest = new WholesaleModifyPortRequest();
        modifyPortRequest.setDesiredExecutionDate(desiredExecutionDate);
        modifyPortRequest.setLineId(lineId);
        modifyPortRequest.setProduct(wholesaleProduct);
        modifyPortRequest.setEkpId(auftrag2EkpFrame.getEkpFrameContract().getEkpId());
        modifyPortRequest.setEkpContractId((auftrag2EkpFrame.getEkpFrameContract().getFrameContractId()));
        return modifyPortRequest;
    }

    public void modifyAuftrag2EkpFrameContractIfNecessary() {
        LocalDate desiredExecDate = LocalDate.now().plusDays(2);
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        Auftrag auftrag = auftragBuilder.build();
        EkpFrameContractBuilder actualEkpFrameContractBuilder = new EkpFrameContractBuilder().withRandomId();
        EkpFrameContract actualEkpFrameContract = actualEkpFrameContractBuilder.build();
        Auftrag2EkpFrameContract actualAuftrag2EkpContract = new Auftrag2EkpFrameContractBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(actualEkpFrameContractBuilder)
                .build();
        EkpFrameContractBuilder newEkpFrameContractBuilder = new EkpFrameContractBuilder().withRandomId();
        EkpFrameContract newEkpFrameContract = newEkpFrameContractBuilder.build();
        Auftrag2EkpFrameContract newAuftrag2EkpContract = new Auftrag2EkpFrameContractBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withEkpFrameContractBuilder(newEkpFrameContractBuilder)
                .build();

        when(ekpFrameContractService.findEkpFrameContract(eq(newEkpFrameContract.getEkpId()),
                eq(newEkpFrameContract.getFrameContractId())))
                .thenReturn(newEkpFrameContract);
        when(ekpFrameContractService.findEkpFrameContract(eq(actualEkpFrameContract.getEkpId()),
                eq(actualEkpFrameContract.getFrameContractId())))
                .thenReturn(actualEkpFrameContract);
        when(ekpFrameContractService.assignEkpFrameContract2Auftrag(eq(newEkpFrameContract),
                eq(auftrag), eq(desiredExecDate), any(AuftragAktion.class)))
                .thenReturn(newAuftrag2EkpContract);
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(auftrag.getId()), eq(desiredExecDate)))
                .thenReturn(actualAuftrag2EkpContract);

        cut.modifyAuftrag2EkpFrameContractIfNecessary(LINE_ID, newEkpFrameContract.getFrameContractId(),
                newEkpFrameContract.getEkpId(), desiredExecDate, auftrag, null);

        verify(ekpFrameContractService).findEkpFrameContract(eq(newEkpFrameContract.getEkpId()),
                eq(newEkpFrameContract.getFrameContractId()));
        verify(ekpFrameContractService).findAuftrag2EkpFrameContract(eq(auftrag.getId()), eq(desiredExecDate));
        verify(ekpFrameContractService).assignEkpFrameContract2Auftrag(eq(newEkpFrameContract),
                eq(auftrag), eq(desiredExecDate), any(AuftragAktion.class));
    }

    @Test(expectedExceptions = LineIdNotFoundException.class)
    public void invalidLineIdYieldsException() throws FindException {
        when(auftragServiceMock.findActiveOrderByLineId(eq(LINE_ID), eq(today))).thenReturn(null);
        cut.findActiveOrderByLineIdAndCheckWholesaleProduct(LINE_ID, today);
    }


    @Test(expectedExceptions = FindException.class)
    public void releasePortImmediatelyEsNotFound() throws FindException, StoreException {
        when(auftragServiceMock.findAuftragById(anyLong())).thenReturn(new AuftragBuilder().withRandomId().build());
        when(endstellenServiceMock.findEndstelle4Auftrag(any(Long.class), any(String.class))).thenReturn(null);
        cut.rangierungFreigeben(new AuftragDatenBuilder().build(), LocalDate.now());
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void modifyPortReservationDateWithoutModifyPortActionYieldsException() throws FindException, StoreException {
        Pair<Auftrag, AuftragDaten> order =
                buildDefaultAuftrag(getDefaultAuftragBuilder(DateConverterUtils.asDate(LocalDate.now().minusDays(5))));
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), anyVararg());
        try {
            WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(LocalDate.now().plusDays(10));
            cut.modifyPortReservationDate(request);
        }
        finally {
            verify(cut, times(0)).logAndCheckAktion(any(Long.class), any(Long.class), any(AktionType.class),
                    any(LocalDate.class));
            verify(cut, times(0))
                    .modifyReservationDates(any(Long.class), any(LocalDate.class), any(LocalDate.class), any(AuftragAktion.class));
        }
    }

    @DataProvider(name = "modifyPortReservationDateWithModifyPortActionDataProvider")
    Object[][] modifyPortReservationDateWithModifyPortActionDataProvider() {
        return new Object[][] {
                { new AuftragBuilder().withRandomId(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(10) },
                { null, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(10) },
                { new AuftragBuilder().withRandomId(), LocalDate.now(), LocalDate.now(),
                        LocalDate.now().plusDays(10) },
                { null, LocalDate.now(), LocalDate.now(),
                        LocalDate.now().plusDays(10) },
        };
    }

    @Test(dataProvider = "modifyPortReservationDateWithModifyPortActionDataProvider")
    public void modifyPortReservationDateWithModifyPortAction(AuftragBuilder prevAuftragBuilder,
            LocalDate reservePortDate, LocalDate exisitingModifyPortDate, LocalDate modifiedDate) throws FindException,
            StoreException {
        final Long prevAuftragId = (prevAuftragBuilder == null) ? null : prevAuftragBuilder.get().getAuftragId();
        Pair<AuftragBuilder, AuftragDatenBuilder> builders =
                getDefaultAuftragBuilder(DateConverterUtils.asDate(reservePortDate));

        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(builders);
        Auftrag auftrag = order.getFirst();

        Auftrag2EkpFrameContract auftrag2EkpContractBeforeModify = createAuftrag2EkpFrameContract(reservePortDate,
                exisitingModifyPortDate);
        Auftrag2EkpFrameContract auftrag2EkpContractAfterModify = createAuftrag2EkpFrameContract(
                exisitingModifyPortDate, hurricanEndDate());

        AuftragAktion action = new AuftragAktionBuilder()
                .withAuftragBuilder(builders.getFirst())
                .withPreviousAuftragBuilder(prevAuftragBuilder)
                .withDesiredExecutionDate(exisitingModifyPortDate).setPersist(false).build();

        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), anyVararg());
        when(auftragServiceMock.getActiveAktion(auftrag.getAuftragId(), AktionType.MODIFY_PORT)).thenReturn(action);

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(auftrag.getAuftragId()), eq(exisitingModifyPortDate.minusDays(1))))
                .thenReturn(auftrag2EkpContractBeforeModify);

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(auftrag.getAuftragId()),
                eq(exisitingModifyPortDate))).thenReturn(auftrag2EkpContractAfterModify);

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(prevAuftragId),
                eq(exisitingModifyPortDate.minusDays(1))))
                .thenReturn(auftrag2EkpContractBeforeModify);

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(auftrag.getAuftragId()), eq(reservePortDate),
                eq(exisitingModifyPortDate)))
                .thenReturn(Lists.newArrayList(auftrag2EkpContractBeforeModify));

        if (exisitingModifyPortDate.isEqual(reservePortDate)) {
            when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(prevAuftragId),
                    eq(exisitingModifyPortDate)))
                    .thenReturn(auftrag2EkpContractBeforeModify);
        }
        else {
            when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(prevAuftragId),
                    eq(exisitingModifyPortDate))).thenReturn(auftrag2EkpContractAfterModify);
        }

        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(eq(prevAuftragId))).thenReturn(order.getSecond());

        doNothing().when(cut).modifyPortReservationDates4PreviousOrder(eq(exisitingModifyPortDate),
                eq(modifiedDate),
                eq(prevAuftragId),
                any(AuftragAktion.class));

        try {
            WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);
            cut.modifyPortReservationDate(request);
        }
        finally {
            assertEquals(auftrag2EkpContractAfterModify.getAssignedTo(), DateConverterUtils.asLocalDate(DateTools.getHurricanEndDate()));
            assertEquals(auftrag2EkpContractBeforeModify.getAssignedTo(), modifiedDate);
            assertEquals(auftrag2EkpContractAfterModify.getAssignedFrom(), modifiedDate);

            verify(auftragServiceMock).getActiveAktion(auftrag.getAuftragId(), AktionType.MODIFY_PORT);
            verify(cut).logAndCheckAktion(auftrag.getAuftragId(), prevAuftragId, AktionType.MODIFY_PORT, modifiedDate);

            if (exisitingModifyPortDate.isEqual(reservePortDate)) {
                verify(ekpFrameContractService).findAuftrag2EkpFrameContract(eq(auftrag.getAuftragId()),
                        eq(exisitingModifyPortDate));
            }

            // @formatter:off
            verify(cut).modifyReservationDates(auftrag.getAuftragId(), exisitingModifyPortDate, modifiedDate, action);
            verify(leistungsService).modifyAuftrag2TechLeistungen(auftrag.getAuftragId(), exisitingModifyPortDate, modifiedDate, action);
            verify(dslamService).modifyDslamProfiles4Auftrag(auftrag.getAuftragId(), exisitingModifyPortDate, modifiedDate, action);
            verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(auftrag2EkpContractAfterModify);
            verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(auftrag2EkpContractBeforeModify);
            // @formatter:on

            if (prevAuftragBuilder != null) {
                assertEquals(modifiedDate, order.getSecond().getInbetriebnahme().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                verify(cut).modifyPortReservationDates4PreviousOrder(eq(exisitingModifyPortDate), eq(modifiedDate),
                        eq(prevAuftragId), any(AuftragAktion.class));
                verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(false));
            }
            else {
                verify(cut, never()).modifyPortReservationDates4PreviousOrder(any(LocalDate.class),
                        any(LocalDate.class),
                        any(Long.class),
                        any(AuftragAktion.class));
            }
        }
    }

    private Auftrag2EkpFrameContract createAuftrag2EkpFrameContract(LocalDate assignedFrom, LocalDate assignedTo) {
        return new Auftrag2EkpFrameContractBuilder()
                .withRandomId()
                .withAssignedFrom(assignedFrom)
                .withAssignedTo(assignedTo)
                .build();
    }

    public void logAndCheckAktion() throws Exception {
        ArgumentCaptor<AuftragAktion> auftragAktionArgumentCaptor = ArgumentCaptor.forClass(AuftragAktion.class);
        Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        final Long prevAuftragId = RandomTools.createLong();
        final AktionType aktionTypeModify = AktionType.MODIFY_PORT;
        final LocalDate desiredExecutionDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(RandomTools.createLong(Instant.MAX.getEpochSecond())), ZoneId.systemDefault()).toLocalDate();

        cut.logAndCheckAktion(auftrag.getAuftragId(), prevAuftragId, aktionTypeModify, desiredExecutionDate);

        verify(auftragServiceMock).saveAuftragAktion(auftragAktionArgumentCaptor.capture());
        AuftragAktion auftragAktionSaved = auftragAktionArgumentCaptor.getValue();
        assertThat(auftragAktionSaved.getAction(), equalTo(aktionTypeModify));
        assertThat(auftragAktionSaved.getAuftragId(), equalTo(auftrag.getAuftragId()));
        assertThat(auftragAktionSaved.getDesiredExecutionDate(), equalTo(desiredExecutionDate));
        assertThat(auftragAktionSaved.getPreviousAuftragId(), equalTo(prevAuftragId));
    }

    public void modifyPortReservationDateWithInvalidDate() throws Exception {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        order.getSecond().setAuftragStatusId(TECHNISCHE_REALISIERUNG);
        LocalDate expectedOriginalDate = DateConverterUtils.asLocalDate(order.getSecond().getVorgabeSCV());
        LocalDate modifiedDate = LocalDate.now().plusDays(1);
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder().build();
        final Verlauf verlauf = new VerlaufBuilder().withRandomId().build();
        final AKUser akUser = new AKUser();

        final BAService.TerminverschiebungException tvException =
                new BAService.TerminverschiebungException(RandomTools.createString());

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(order.getFirst().getAuftragId()),
                eq(expectedOriginalDate))).thenReturn(auftrag2EkpFrameContract);
        when(baServiceMock.findActVerlauf4Auftrag(order.getSecond().getAuftragId(), false)).thenReturn(verlauf);
        when(akUserService.findUserBySessionId(anyLong())).thenReturn(akUser);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());

        doThrow(tvException).when(baServiceMock).changeRealDate(verlauf.getId(), asDate(modifiedDate), akUser);

        WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);

        try {
            cut.modifyPortReservationDate(request);
        }
        catch (ModifyPortReservationDateToEarlierDateException e) {
            assertThat(e.getFehlerBeschreibung(), equalTo(tvException.getMessage()));
            return;
        }

        fail();
    }

    public void modifyPortReservationDateWithAuftragNotYetInBetrieb() throws Exception {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        order.getSecond().setAuftragStatusId(TECHNISCHE_REALISIERUNG);
        LocalDate expectedOriginalDate = DateConverterUtils.asLocalDate(order.getSecond().getVorgabeSCV());
        LocalDate modifiedDate = LocalDate.now().plusDays(1);
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder().build();
        final Verlauf verlauf = new VerlaufBuilder().withRandomId().build();
        final AKUser akUser = new AKUser();

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(order.getFirst().getAuftragId()),
                eq(expectedOriginalDate))).thenReturn(auftrag2EkpFrameContract);
        when(baServiceMock.findActVerlauf4Auftrag(order.getSecond().getAuftragId(), false)).thenReturn(verlauf);
        when(akUserService.findUserBySessionId(anyLong())).thenReturn(akUser);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());

        WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);
        cut.modifyPortReservationDate(request);

        assertEquals(auftrag2EkpFrameContract.getAssignedFrom(), modifiedDate);

        verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(Boolean.FALSE));
        verify(leistungsService).modifyAuftrag2TechLeistungen(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verify(dslamService).modifyDslamProfiles4Auftrag(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(eq(auftrag2EkpFrameContract));
        final ArgumentCaptor<Date> realDateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(baServiceMock).changeRealDate(eq(verlauf.getId()), realDateCaptor.capture(), eq(akUser));
        assertTrue(DateTools.isDateEqual(realDateCaptor.getValue(), DateConverterUtils.asDate(modifiedDate)));
    }

    public void modifyPortReservationDateForErfassungFttH() throws Exception {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        order.getSecond().setAuftragStatusId(ERFASSUNG);
        LocalDate expectedOriginalDate = DateConverterUtils.asLocalDate(order.getSecond().getVorgabeSCV());
        LocalDate modifiedDate = LocalDate.now().plusDays(1);
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder().build();
        final Verlauf verlauf = new VerlaufBuilder().withRandomId().build();
        final AKUser akUser = new AKUser();

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(order.getFirst().getAuftragId()),
                eq(expectedOriginalDate))).thenReturn(auftrag2EkpFrameContract);
        when(baServiceMock.findActVerlauf4Auftrag(order.getSecond().getAuftragId(), false)).thenReturn(verlauf);
        when(akUserService.findUserBySessionId(anyLong())).thenReturn(akUser);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());
        when(endstellenServiceMock.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(new Endstelle());
        final HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(11011L);
        when(hvtServiceMock.findHVTStandort(anyLong())).thenReturn(hvtStandort);

        WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);
        cut.modifyPortReservationDate(request);

        assertEquals(auftrag2EkpFrameContract.getAssignedFrom(), modifiedDate);

        verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(Boolean.FALSE));
        verify(leistungsService).modifyAuftrag2TechLeistungen(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verify(dslamService).modifyDslamProfiles4Auftrag(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verifyNoMoreInteractions(vlanService);
        verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(eq(auftrag2EkpFrameContract));
        final ArgumentCaptor<Date> realDateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(baServiceMock).changeRealDate(eq(verlauf.getId()), realDateCaptor.capture(), eq(akUser));
        assertTrue(DateTools.isDateEqual(realDateCaptor.getValue(), DateConverterUtils.asDate(modifiedDate)));
    }

    public void modifyPortReservationDateForErfassungFttBVerlaufAvailable() throws Exception {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        order.getSecond().setAuftragStatusId(ERFASSUNG);
        LocalDate expectedOriginalDate = DateConverterUtils.asLocalDate(order.getSecond().getVorgabeSCV());
        LocalDate modifiedDate = LocalDate.now().plusDays(1);
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder().build();
        final Verlauf verlauf = new VerlaufBuilder().withRandomId().build();
        final AKUser akUser = new AKUser();

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(order.getFirst().getAuftragId()),
                eq(expectedOriginalDate))).thenReturn(auftrag2EkpFrameContract);
        when(baServiceMock.findActVerlauf4Auftrag(order.getSecond().getAuftragId(), false)).thenReturn(verlauf);
        when(akUserService.findUserBySessionId(anyLong())).thenReturn(akUser);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());
        when(endstellenServiceMock.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(new Endstelle());
        final HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(11002L);
        when(hvtServiceMock.findHVTStandort(anyLong())).thenReturn(hvtStandort);

        WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);
        cut.modifyPortReservationDate(request);

        assertEquals(auftrag2EkpFrameContract.getAssignedFrom(), modifiedDate);

        verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(Boolean.FALSE));
        verify(leistungsService).modifyAuftrag2TechLeistungen(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verify(dslamService).modifyDslamProfiles4Auftrag(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verifyNoMoreInteractions(vlanService);
        verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(eq(auftrag2EkpFrameContract));
        final ArgumentCaptor<Date> realDateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(baServiceMock).changeRealDate(eq(verlauf.getId()), realDateCaptor.capture(), eq(akUser));
        assertTrue(DateTools.isDateEqual(realDateCaptor.getValue(), DateConverterUtils.asDate(modifiedDate)));
    }

    public void modifyPortReservationDateForErfassungFttBVerlaufNotAvailable() throws Exception {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().plusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        order.getSecond().setAuftragStatusId(ERFASSUNG);
        LocalDate expectedOriginalDate = DateConverterUtils.asLocalDate(order.getSecond().getVorgabeSCV());
        LocalDate modifiedDate = LocalDate.now().plusDays(1);
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder().build();
        final AKUser akUser = new AKUser();

        when(ekpFrameContractService.findAuftrag2EkpFrameContract(eq(order.getFirst().getAuftragId()),
                eq(expectedOriginalDate))).thenReturn(auftrag2EkpFrameContract);
        when(akUserService.findUserBySessionId(anyLong())).thenReturn(akUser);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());
        when(endstellenServiceMock.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(new Endstelle());
        final HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(11002L);
        when(hvtServiceMock.findHVTStandort(anyLong())).thenReturn(hvtStandort);

        WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);
        cut.modifyPortReservationDate(request);

        assertEquals(auftrag2EkpFrameContract.getAssignedFrom(), modifiedDate);

        verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(Boolean.FALSE));
        verify(leistungsService).modifyAuftrag2TechLeistungen(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verify(dslamService).modifyDslamProfiles4Auftrag(eq(order.getFirst().getAuftragId()), eq(expectedOriginalDate),
                eq(request.getDesiredExecutionDate()), any(AuftragAktion.class));
        verifyNoMoreInteractions(vlanService);
        verify(ekpFrameContractService).saveAuftrag2EkpFrameContract(eq(auftrag2EkpFrameContract));
        verify(baServiceMock).createVerlauf(any());
    }

    public void modifyPortReservationDateForKuendigung() throws Exception {
        final Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(new Date()));
        order.getSecond().setAuftragStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        final LocalDate modifiedDate = LocalDate.now().plusDays(2);
        order.getSecond().setKuendigung(DateConverterUtils.asDate(modifiedDate.minusDays(1)));
        final Verlauf verlauf = new VerlaufBuilder().withRandomId().build();
        final AKUser akUser = new AKUser();
        akUser.setId(RandomTools.createLong());
        final Auftrag2TechLeistung auftrag2TechLeistung = new Auftrag2TechLeistungBuilder()
                .withRandomId()
                .build();
        final WholesaleModifyPortReservationDateRequest request = createWhsModifyPortReservationDateRequest(modifiedDate);

        when(baServiceMock.findActVerlauf4Auftrag(order.getSecond().getAuftragId(), false)).thenReturn(verlauf);
        doReturn(order.getSecond()).when(auftragServiceMock).findAuftragDatenByLineIdAndStatus(eq(LINE_ID), Matchers.<Long>anyVararg());
        when(leistungsService.findAuftrag2TechLeistungen(order.getSecond().getAuftragId(),
                DateConverterUtils.asLocalDate(order.getSecond().getKuendigung()).minusDays(1L))).thenReturn(Collections.singletonList(auftrag2TechLeistung));
        when(akUserService.findUserBySessionId(request.getSessionId())).thenReturn(akUser);

        cut.modifyPortReservationDate(request);

        verify(auftragServiceMock).saveAuftragDaten(eq(order.getSecond()), eq(Boolean.FALSE));
        verify(leistungsService).saveAuftrag2TechLeistung(auftrag2TechLeistung);
        final ArgumentCaptor<Date> realDateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(baServiceMock).changeRealDate(eq(verlauf.getId()), realDateCaptor.capture(), eq(akUser));

        assertTrue(DateTools.isDateEqual(realDateCaptor.getValue(), DateConverterUtils.asDate(modifiedDate)));
        assertThat(auftrag2TechLeistung.getAktivBis(), equalTo(DateConverterUtils.asDate(modifiedDate)));
    }

    private WholesaleModifyPortReservationDateRequest createWhsModifyPortReservationDateRequest(LocalDate executionDate) {
        WholesaleModifyPortReservationDateRequest request = new WholesaleModifyPortReservationDateRequest();
        request.setLineId(LINE_ID);
        request.setDesiredExecutionDate(executionDate);
        return request;
    }

    public void modifyPortReservationDates4PreviousOrderInKuendigung() throws Exception {
        Long previousAuftragId = 1234L;
        AuftragAktion auftragAktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();
        Rangierung rangierung = new RangierungBuilder().withRandomId().build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomId().withKuendigung(new Date(0L)).build();
        LocalDate originalDate = LocalDate.now().plusDays(2);
        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        Date expectedRangierungFreigabeAb = Date.from(modifiedDate.plusDays(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(rangierungsServiceMock.findRangierungenTx(eq(previousAuftragId),
                eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(new Rangierung[] { rangierung });
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(eq(previousAuftragId))).thenReturn(auftragDaten);

        cut.modifyPortReservationDates4PreviousOrder(originalDate, modifiedDate, previousAuftragId, auftragAktion);

        assertTrue(DateTools.isDateEqual(rangierung.getFreigabeAb(), expectedRangierungFreigabeAb));
        assertTrue(DateTools.isDateEqual(auftragDaten.getKuendigung(), Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));

        verify(rangierungsServiceMock).findRangierungenTx(eq(previousAuftragId),
                eq(Endstelle.ENDSTELLEN_TYP_B));
        verify(rangierungsServiceMock).saveRangierung(eq(rangierung), eq(false));
        verify(auftragServiceMock).findAuftragDatenByAuftragIdTx(eq(previousAuftragId));
        verify(auftragServiceMock).saveAuftragDaten(eq(auftragDaten), eq(false));
        verify(cut).modifyReservationDates(eq(previousAuftragId), eq(originalDate), eq(modifiedDate), any(AuftragAktion.class));

    }

    @DataProvider
    public Object[][] modifyPortReservationDates4PreviousOrderInStornoDataProvider() {
        return new Object[][] {
                { LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), LocalDate.now().plusDays(2) },
                { LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now() },
                { LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), LocalDate.now().plusDays(2) },
        };
    }

    @Test(dataProvider = "modifyPortReservationDates4PreviousOrderInStornoDataProvider")
    public void modifyPortReservationDates4PreviousOrderInStorno(LocalDate originalDate, LocalDate modifiedDate,
            LocalDate inbetriebnahme) throws Exception {
        Long previousAuftragId = 1234L;
        AuftragAktion auftragAktion = new AuftragAktionBuilder().withRandomId().setPersist(false).build();
        Rangierung rangierung = new RangierungBuilder().withRandomId().build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomId().withStatusId(AuftragStatus.STORNO)
                .withInbetriebnahme(Date.from(inbetriebnahme.atStartOfDay(ZoneId.systemDefault()).toInstant())).build();

        Date expectedRangierungFreigabeAb = Date.from(modifiedDate.plusDays(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(rangierungsServiceMock.findRangierungenTx(eq(previousAuftragId),
                eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(new Rangierung[] { rangierung });
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(eq(previousAuftragId))).thenReturn(auftragDaten);

        cut.modifyPortReservationDates4PreviousOrder(originalDate, modifiedDate, previousAuftragId, auftragAktion);

        assertTrue(DateTools.isDateEqual(rangierung.getFreigabeAb(), expectedRangierungFreigabeAb));
        assertTrue(DateTools.isDateEqual(auftragDaten.getKuendigung(), Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));

        verify(rangierungsServiceMock).findRangierungenTx(eq(previousAuftragId),
                eq(Endstelle.ENDSTELLEN_TYP_B));
        verify(rangierungsServiceMock).saveRangierung(eq(rangierung), eq(false));
        verify(auftragServiceMock).findAuftragDatenByAuftragIdTx(eq(previousAuftragId));
        verify(auftragServiceMock).saveAuftragDaten(eq(auftragDaten), eq(false));

        final boolean modifyReservationDatesExpected = originalDate.isBefore(inbetriebnahme);

        verify(cut, times((modifyReservationDatesExpected) ? 1 : 0)).modifyReservationDates(eq(previousAuftragId),
                eq(originalDate), eq(modifiedDate), any(AuftragAktion.class));
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void cancelModifyPortWithoutPendingModifyPortYieldsException() throws FindException {
        Pair<Auftrag, AuftragDaten> order = buildDefaultAuftrag(getDefaultAuftragBuilder(Date.from(LocalDate.now().minusDays(5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant())));
        doReturn(order).when(cut).findActiveOrderByLineIdAndCheckWholesaleProduct(LINE_ID, today);

        when(auftragServiceMock.getActiveAktion(order.getFirst().getAuftragId(), AktionType.MODIFY_PORT)).thenReturn(null);

        WholesaleCancelModifyPortRequest request = new WholesaleCancelModifyPortRequest();
        request.setLineId(LINE_ID);
        cut.cancelModifyPort(request);
    }

    private Pair<AuftragBuilder, AuftragDatenBuilder> getDefaultAuftragBuilder(Date vorgabeAM) {
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withVorgabeSCV(vorgabeAM)
                .withProdId(600L)
                .setPersist(false);
        return Pair.create(auftragBuilder, auftragDatenBuilder);
    }

    private Pair<Auftrag, AuftragDaten> buildDefaultAuftrag(Pair<AuftragBuilder, AuftragDatenBuilder> builders) {
        return Pair.create(builders.getFirst().build(), builders.getSecond().build());
    }

    public void cancelModifyPortAction() throws StoreException {
        final Long auftragId = RandomTools.createLong();
        AuftragAktion modifyPortAction = new AuftragAktionBuilder().withCancelled(false).build();
        AuftragAktion cancelAction = cut.cancelModifyPortAction(modifyPortAction, LINE_ID, auftragId);
        verify(auftragServiceMock, times(1)).cancelAuftragAktion(eq(modifyPortAction));
        verify(auftragServiceMock, times(1)).saveAuftragAktion(eq(cancelAction));
        assertEquals(cancelAction.getAction(), AktionType.CANCEL_MODIFY_PORT);
        assertEquals(cancelAction.getDesiredExecutionDate(), LocalDate.now());
        assertEquals(cancelAction.getAuftragId(), auftragId);
    }


    @Test(expectedExceptions = WholesaleServiceException.class)
    public void findPbitsWithoutEkpFrameContractYieldsException() {
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(any(Long.class), any(LocalDate.class))).thenReturn(
                null);
        cut.findPbits(new Auftrag(), LocalDate.now());
    }

    @DataProvider
    Object[][] rangierungFreigebenZuSofortOderAuftragInStornoDataProvider() {
        return new Object[][] {
                { AuftragStatus.STORNO, LocalDate.now() },
                { AuftragStatus.STORNO, LocalDate.now().plusDays(1) },
                { AuftragStatus.STORNO, LocalDate.now().minusDays(1) },
                { AuftragStatus.KUENDIGUNG, LocalDate.now() },
        };
    }

    @Test(dataProvider = "rangierungFreigebenZuSofortOderAuftragInStornoDataProvider")
    public void rangierungFreigebenZuSofortOderAuftragInStorno(Long statusId, LocalDate when) throws Exception {
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId();
        Rangierung rangierung = rangierungBuilder.build();
        Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withStatusId(statusId).build();
        Endstelle endstelle = new EndstelleBuilder().withRandomId().withRangierungBuilder(rangierungBuilder).build();

        when(auftragServiceMock.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(rangierungsServiceMock.findRangierung(eq(rangierung.getId()))).thenReturn(rangierung);
        when(endstellenServiceMock.findEndstelle4Auftrag(auftrag.getAuftragId(),
                Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);

        cut.rangierungFreigeben(auftragDaten, when);

        assertEquals(rangierung.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE);
        assertEquals(rangierung.getFreigabeAb().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), when.plusDays(1));
        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void releasePort_with_Auftragdaten_NULL() throws FindException, StoreException {
        WholesaleReleasePortRequest testRequest = buildWholesaleReleasePortRequest(LINE_ID);

        when(auftragServiceMock.findAuftragDatenByLineIdAndStatus(
                testRequest.getLineId(),
                IN_BETRIEB,
                TECHNISCHE_REALISIERUNG))
                .thenReturn(null);

        cut.releasePort(testRequest);
    }

    @Test(expectedExceptions = WholesaleServiceException.class)
    public void releasePort_with_Verlauf_NULL() throws FindException, StoreException {
        WholesaleReleasePortRequest testRequest = buildWholesaleReleasePortRequest(LINE_ID);
        AuftragDaten testAuftragDaten = new AuftragDatenBuilder().withRandomAuftragId().build();

        when(auftragServiceMock.findAuftragDatenByLineIdAndStatus(
                testRequest.getLineId(),
                IN_BETRIEB,
                TECHNISCHE_REALISIERUNG))
                .thenReturn(testAuftragDaten);
        when(baServiceMock.findActVerlauf4Auftrag(testAuftragDaten.getAuftragId(), false)).thenReturn(null);

        cut.releasePort(testRequest);
    }

    @Test
    public void releasePort_WithAuftragStatus_TECHNISCHE_REALISIERUNG() throws FindException, StoreException {
        WholesaleReleasePortRequest testRequest = buildWholesaleReleasePortRequest(LINE_ID);
        Auftrag testAuftrag = new AuftragBuilder().withRandomId().build();
        AuftragDaten testAuftragDaten = new AuftragDatenBuilder().withRandomAuftragId().build();
        testAuftragDaten.setAuftragStatusId(TECHNISCHE_REALISIERUNG);
        Verlauf testVerlauf = new VerlaufBuilder().withAuftragId(testAuftragDaten.getAuftragId()).build();
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId();
        Rangierung testRangierung = rangierungBuilder.build();
        Endstelle testEndstelle = new EndstelleBuilder().withRandomId().withRangierungBuilder(rangierungBuilder).build();

        when(auftragServiceMock.findAuftragById(testAuftragDaten.getAuftragId())).thenReturn(testAuftrag);
        when(auftragServiceMock.findAuftragDatenByLineIdAndStatus(testRequest.getLineId(), IN_BETRIEB, TECHNISCHE_REALISIERUNG)).thenReturn(testAuftragDaten);
        when(baServiceMock.findActVerlauf4Auftrag(testAuftragDaten.getAuftragId(), false)).thenReturn(testVerlauf);
        when(rangierungsServiceMock.findRangierung(eq(testRangierung.getId()))).thenReturn(testRangierung);
        when(endstellenServiceMock.findEndstelle4Auftrag(testAuftrag.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(testEndstelle);

        cut.executeReleasePort4Storno(testAuftragDaten, testRequest);

        verify(baServiceMock).verlaufStornieren(testVerlauf.getId(), false, testRequest.getSessionId());
        verify(cut).rangierungFreigeben(testAuftragDaten, LocalDate.now());
        verify(auftragServiceMock).saveAuftragDaten(testAuftragDaten, false);

        assertEquals(testAuftragDaten.getAuftragStatusId(), AuftragStatus.ABSAGE);

    }

    @Test
    public void releasePort_WithAuftragStatus_IN_BETRIEB() throws FindException, StoreException {
        WholesaleReleasePortRequest testRequest = buildWholesaleReleasePortRequest(LINE_ID);
        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten testAuftragDaten = new AuftragDatenBuilder()
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(IN_BETRIEB)
                .build();
        Verlauf testVerlauf = new VerlaufBuilder().withAuftragId(testAuftragDaten.getAuftragId()).build();
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId();
        Rangierung testRangierung = rangierungBuilder.build();
        Endstelle testEndstelle = new EndstelleBuilder().withRandomId().withRangierungBuilder(rangierungBuilder).build();
        LocalDate kuendigungsDatum = testRequest.getReleaseDate();
        Auftrag2TechLeistung auftrag2TechLeistung = new Auftrag2TechLeistungBuilder()
                .withRandomId()
                .build();

        when(auftragServiceMock.findAuftragDatenByLineIdAndStatus(testRequest.getLineId(), IN_BETRIEB, TECHNISCHE_REALISIERUNG)).thenReturn(testAuftragDaten);
        when(auftragServiceMock.findAuftragById(testAuftragDaten.getAuftragId())).thenReturn(auftragBuilder.get());
        when(baServiceMock.findActVerlauf4Auftrag(testAuftragDaten.getAuftragId(), false)).thenReturn(testVerlauf);
        when(rangierungsServiceMock.findRangierung(eq(testRangierung.getId()))).thenReturn(testRangierung);
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragBuilder.get().getAuftragId(), Endstelle.ENDSTELLEN_TYP_B)).thenReturn(testEndstelle);
        when(leistungsService.findAuftrag2TechLeistungen(testAuftragDaten.getAuftragId(), kuendigungsDatum)).thenReturn(Collections.singletonList(auftrag2TechLeistung));

        cut.executeReleasePort4Kuendigung(testAuftragDaten, testRequest);

        final ArgumentCaptor<CreateVerlaufParameter> createVerlaufParamArgCaptor =
                ArgumentCaptor.forClass(CreateVerlaufParameter.class);
        verify(baServiceMock).createVerlauf(createVerlaufParamArgCaptor.capture());
        verify(cut).rangierungFreigeben(testAuftragDaten, kuendigungsDatum);
        verify(leistungsService).saveAuftrag2TechLeistung(auftrag2TechLeistung);

        assertThat(createVerlaufParamArgCaptor.getValue().anlass, equalTo(BAVerlaufAnlass.KUENDIGUNG));
        assertThat(createVerlaufParamArgCaptor.getValue().auftragId, equalTo(auftragBuilder.get().getAuftragId()));
        assertThat(DateConverterUtils.asLocalDate(createVerlaufParamArgCaptor.getValue().getRealisierungsTermin()), equalTo(kuendigungsDatum));
        assertThat(auftrag2TechLeistung.getAktivBis(), equalTo(DateConverterUtils.asDate(kuendigungsDatum)));
        assertThat(testAuftragDaten.getAuftragStatusId(), equalTo(AuftragStatus.KUENDIGUNG));
        assertThat(testAuftragDaten.getKuendigung(), equalTo(DateConverterUtils.asDate(testRequest.getReleaseDate())));
    }


    public void rangierungFreigebenInZukunftUndKeinStorno() throws Exception {
        RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId();
        Rangierung rangierung = rangierungBuilder.build();
        Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG).build();
        Endstelle endstelle = new EndstelleBuilder().withRandomId().withRangierungBuilder(rangierungBuilder).build();
        LocalDate when = LocalDate.now().plusDays(1);

        when(auftragServiceMock.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
        when(rangierungsServiceMock.findRangierung(eq(rangierung.getId()))).thenReturn(rangierung);
        when(endstellenServiceMock.findEndstelle4Auftrag(auftrag.getAuftragId(),
                Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);

        cut.rangierungFreigeben(auftragDaten, when);

        assertEquals(rangierung.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE);
        assertEquals(rangierung.getFreigabeAb().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                when.plusDays(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE));
        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
    }

    @Test(expectedExceptions = WholesaleTechnicalException.class)
    public void calculateAndSaveEqVlanThrowsWholesaleTechExceptionOnConcurrencyException() throws FindException, StoreException {
        Auftrag auftrag = new AuftragBuilder().withRandomId().build();

        when(ekpFrameContractService.findEkp4AuftragOrDefaultMnet(any(Long.class), any(LocalDate.class), any(Boolean.class))).thenReturn(new EkpFrameContract());
        when(vlanService.assignEqVlans(any(EkpFrameContract.class), any(Long.class), any(Long.class),
                any(LocalDate.class), any(AuftragAktion.class))).thenThrow(new HurricanConcurrencyException());
        cut.calculateAndSaveEqVlan(LINE_ID, auftrag, 10L, LocalDate.now(), null);
    }

    @Test
    public void testReleasePort4AuftragStatusErfassung() throws Exception {
        final String lineId = "DEU.MNET.000111";
        final String orderId = "HER_1234";
        final WholesaleReleasePortRequest request = new WholesaleReleasePortRequest();
        request.setLineId(lineId);
        request.setOrderId(orderId);

        final RangierungBuilder rangierungBuilder = new RangierungBuilder().withRandomId();
        final Rangierung rangierung = rangierungBuilder.build();
        final Auftrag auftrag = new AuftragBuilder().withRandomId().build();
        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).build();
        final Endstelle endstelle = new EndstelleBuilder().withRandomId().withRangierungBuilder(rangierungBuilder).build();

        when(auftragServiceMock.findAuftragDatenByLineIdAndStatus(lineId, IN_BETRIEB, TECHNISCHE_REALISIERUNG, ERFASSUNG))
                .thenReturn(auftragDaten);
        when(auftragServiceMock.findAuftragById(anyLong())).thenReturn(auftrag);
        when(endstellenServiceMock.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(endstelle);
        when(rangierungsServiceMock.findRangierung(anyLong())).thenReturn(rangierung);

        cut.releasePort(request);

        verify(rangierungsServiceMock).saveRangierung(rangierung, false);
        assertThat(rangierung.getEsId(), equalTo(Rangierung.RANGIERUNG_NOT_ACTIVE));
    }

}
