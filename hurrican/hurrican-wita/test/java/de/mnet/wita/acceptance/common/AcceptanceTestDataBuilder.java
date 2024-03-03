/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2011 11:09:14
 */
package de.mnet.wita.acceptance.common;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.factory.TaifunDataFactory;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.EndstelleConnectBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ConnectService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.service.WitaVorabstimmungService;

public class AcceptanceTestDataBuilder implements ApplicationContextAware {

    public static Long CARRIER_WITA_SIMULATOR = 999999L; // über modify-hurrican4devel.sql angelegt
    private final static String BESTANDSSUCHE_ONKZ = "04301";
    private final static String BESTANDSSUCHE_DN = "9301842";
    private final String rangVerteiler = "02K1";
    private final String kvzNummer = "A013";
    private final String kvzDoppelader = "0011";
    private final Set<Pair<ArchiveDocumentDto, String>> archiveDocuments = Sets.newHashSet();
    @Autowired
    private AcceptanceTestWorkflowService acceptanceTestWorkflowService;
    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private ConnectService connectService;
    @Autowired
    private CCKundenService kundenService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private RufnummerService rufnummerService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private AKUserService userService;
    @Autowired
    private AnsprechpartnerService ansprechpartnerService;
    @Autowired
    private WitaVorabstimmungService witaVorabstimmungService;
    @Resource(name = "de.mnet.wita.acceptance.common.KftStandortDataBuilder")
    private StandortDataBuilder<?> standortDataBuilder;
    private Long sessionId;
    private String userPhone = "089/0000-000";
    private String userName = "Test User";
    private String rangLeiste1 = "01";
    private String rangStift1 = "01";
    private boolean isVierDraht = false;
    private String rangLeiste1ForRang2 = "02";
    private String rangStift1ForRang2 = "02";
    private Date carrierbestellungRealDate;
    private Date carrierbestellungKuendigungAn;
    private String carrierbestellungLbz;
    private String carrierbestellungVtrNr;
    private Long carrierbestellungAuftragId4TalNa;
    private CarrierbestellungBuilder referencingCbBuilder;
    private Uebertragungsverfahren referencingCbUetv;
    private RangSchnittstelle rangierungSchnittstelle = RangSchnittstelle.H;
    private RangSchnittstelle referencingCbRangSchnittstelle;
    private String rangSsType = "2H";
    private boolean useKvz = false;
    private Uebertragungsverfahren uetv = Uebertragungsverfahren.H04;
    private Vorabstimmung vorabstimmung;
    private VorabstimmungAbgebend vorabstimmungAbgebend;
    private GeneratedTaifunData taifunData;
    private LocalDateTime vorgabeMnet = null;
    private Long hurricanProduktId = Produkt.PROD_ID_MAXI_DSL_UND_ISDN;
    private boolean createAnschlussinhaberAddress = false;
    private boolean anschlussinhaberFirma = false;
    private boolean createVormieter = false;
    private boolean createProjectLead = false;
    private String addressType = CCAddress.ADDRESS_FORMAT_RESIDENTIAL;
    private String projektKenner;
    private String vorabstimmungsId;
    private String cbVorgangMontagehinweis = null;
    private boolean cbVorgangAnbieterwechselTKG46;
    private String terminReservierungsId = null;
    private String eqOutCarrier = Carrier.CARRIER_DTAG;
    private EndstelleConnect endstelleConnect = null;
    private boolean cancelHurricanOrdersWithSameTaifunId = false;
    private ApplicationContext applicationContext;

    public AcceptanceTestDataBuilder() {
        super();
    }

    @PostConstruct
    public void init() throws StoreException {
        vorgabeMnet = DateCalculationHelper.addWorkingDays(LocalDate.now(), 14).atStartOfDay();
        withTaifunData(getTaifunDataFactory().surfAndFonWithDns(0));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public TaifunDataFactory getTaifunDataFactory() {
        return applicationContext.getBean(TaifunDataFactory.class);
    }

    public AcceptanceTestDataBuilder withUserName(WitaSimulatorTestUser userName) {
        this.userName = userName.getName();
        return this;
    }

    public AcceptanceTestDataBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Tells the builder to cancel existing hurrican orders with the same Taifun order Id as the current builder.
     */
    public AcceptanceTestDataBuilder withCancelHurricanOrdersWithSameTaifunId() {
        this.cancelHurricanOrdersWithSameTaifunId = true;
        return this;
    }

    public AcceptanceTestDataBuilder withCreateAnschlussinhaberAddress(boolean anschlussinhaberFirma) {
        this.createAnschlussinhaberAddress = true;
        this.anschlussinhaberFirma = anschlussinhaberFirma;
        return this;
    }

    public AcceptanceTestDataBuilder withVorabstimmungEinzelanschluss(ProduktGruppe produktGruppe, Long carrierId)
            throws FindException {
        vorabstimmung = new Vorabstimmung();
        vorabstimmung.setProduktGruppe(produktGruppe);
        vorabstimmung.setProviderVtrNr(acceptanceTestWorkflowService.getNextVertragsnummer());
        vorabstimmung.setProviderLbz("96X/82100/82100/0000011");
        vorabstimmung.setCarrier(carrierService.findCarrier(carrierId));
        if (!CollectionUtils.isEmpty(taifunData.getDialNumbers())) {
            Rufnummer rufnummer = taifunData.getDialNumbers().get(0);
            vorabstimmung.setBestandssucheOnkz(rufnummer.getOnKz());
            vorabstimmung.setBestandssucheDn(rufnummer.getDnBase());
        } else {
            vorabstimmung.setBestandssucheOnkz(BESTANDSSUCHE_ONKZ);
            vorabstimmung.setBestandssucheDn(BESTANDSSUCHE_DN);
        }
        return this;
    }

    public AcceptanceTestDataBuilder withVorabstimmungAnlagenanschluss(ProduktGruppe produktGruppe, Long carrierId)
            throws FindException {
        vorabstimmung = new Vorabstimmung();
        vorabstimmung.setProduktGruppe(produktGruppe);
        vorabstimmung.setProviderVtrNr(acceptanceTestWorkflowService.getNextVertragsnummer());
        vorabstimmung.setProviderLbz("96X/82100/82100/0000022");
        vorabstimmung.setCarrier(carrierService.findCarrier(carrierId));
        vorabstimmung.setBestandssucheOnkz("04401");
        vorabstimmung.setBestandssucheDn("9602");
        vorabstimmung.setBestandssucheDirectDial("0");
        return this;
    }

    public AcceptanceTestDataBuilder withVorabstimmungAbgebend(boolean positive, Long carrierId, LocalDate kwt)
            throws FindException {
        Carrier carrier = carrierService.findCarrier(carrierId);
        Preconditions.checkNotNull(carrier);

        vorabstimmungAbgebend = new VorabstimmungAbgebend();
        vorabstimmungAbgebend.setRueckmeldung(positive);
        vorabstimmungAbgebend.setCarrier(carrier);
        vorabstimmungAbgebend.setAbgestimmterProdiverwechsel(kwt);
        return this;
    }

    public AcceptanceTestDataBuilder withProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
        return this;
    }

    public AcceptanceTestDataBuilder withVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
        return this;
    }

    public AcceptanceTestDataBuilder withCarrierbestellungRealDate(Date realDate) {
        this.carrierbestellungRealDate = realDate;
        return this;
    }

    public AcceptanceTestDataBuilder withCarrierbestellungKuendigungAn(Date kuendigungAn) {
        this.carrierbestellungKuendigungAn = kuendigungAn;
        return this;
    }

    public AcceptanceTestDataBuilder withCarrierbestellungLbz(String lbz) {
        this.carrierbestellungLbz = lbz;
        return this;
    }

    public AcceptanceTestDataBuilder withCarrierbestellungVtrNr(String vtrNr) {
        this.carrierbestellungVtrNr = vtrNr;
        return this;
    }

    public AcceptanceTestDataBuilder withCarrierbestellungAuftragId4TalNa(Long auftragId4TalNa) {
        this.carrierbestellungAuftragId4TalNa = auftragId4TalNa;
        return this;
    }

    public AcceptanceTestDataBuilder withReferencingCbBuilder(CarrierbestellungBuilder builder) {
        return withReferencingCbBuilder(builder, Uebertragungsverfahren.N01, RangSchnittstelle.N);
    }

    public AcceptanceTestDataBuilder withReferencingCbBuilder(CarrierbestellungBuilder builder,
            Uebertragungsverfahren uetv, RangSchnittstelle rangSchnittstelle) {
        this.referencingCbBuilder = builder;
        this.referencingCbUetv = uetv;
        this.referencingCbRangSchnittstelle = rangSchnittstelle;
        return this;
    }

    public AcceptanceTestDataBuilder withRangSsType(String rangSsType) {
        this.rangSsType = rangSsType;
        return this;
    }

    public AcceptanceTestDataBuilder withRangSchnittstelle(RangSchnittstelle rangSchnittstelle) {
        this.rangierungSchnittstelle = rangSchnittstelle;
        return this;
    }

    public AcceptanceTestDataBuilder withKvz() {
        this.useKvz = true;
        return this;
    }

    /**
     * EVS (= Endverschluss) fuer Rangierung 1
     */
    public AcceptanceTestDataBuilder withRangLeiste1(String rangLeiste1) {
        this.rangLeiste1 = rangLeiste1;
        return this;
    }

    /**
     * Doppelader fuer Rangierung 1
     */
    public AcceptanceTestDataBuilder withRangStift1(String rangStift1) {
        this.rangStift1 = rangStift1;
        return this;
    }

    public AcceptanceTestDataBuilder withUetv(Uebertragungsverfahren uetv) {
        this.uetv = uetv;
        return this;
    }

    public AcceptanceTestDataBuilder withCBVorgangMontagehinweis(String cbVorgangMontagehinweis) {
        this.cbVorgangMontagehinweis = cbVorgangMontagehinweis;
        return this;
    }

    public AcceptanceTestDataBuilder withCBVorgangAnbieterwechselTKG46(boolean anbieterwechselTKG46) {
        this.cbVorgangAnbieterwechselTKG46 = anbieterwechselTKG46;
        return this;
    }


    public AcceptanceTestDataBuilder withTerminReservierungsId(String terminReservierungsId) {
        this.terminReservierungsId = terminReservierungsId;
        return this;
    }

    /**
     * Fuer 4 Draht Bestellungen muss der EVS (= Endverschluss) und die Doppelader nochmal gesetzt werden
     */
    public AcceptanceTestDataBuilder withVierDraht(String rangLeiste1ForRang2, String rangStift1ForRang2) {
        assertTrue((rangLeiste1ForRang2 != null) && (rangStift1ForRang2 != null),
                "both rangLeiste1 and rangStift1 must set for 4-Draht");

        this.rangLeiste1ForRang2 = rangLeiste1ForRang2;
        this.rangStift1ForRang2 = rangStift1ForRang2;
        return withVierDraht();
    }

    /**
     * Fuer 4 Draht Bestellungen mit Default-Werte fuer RangLeiste1 und RangStift1
     */
    public AcceptanceTestDataBuilder withVierDraht() {
        this.isVierDraht = true;
        return this;
    }

    public AcceptanceTestDataBuilder withTaifunData(GeneratedTaifunData taifunData) {
        this.taifunData = taifunData;
        try {
            Long geoIdToSet = standortDataBuilder
                    .withKundeStrasse(taifunData.getAddress().getStrasse())
                    .withKundeHausNr(taifunData.getAddress().getNummer())
                    .withKundeHausNrZusatz(taifunData.getAddress().getHausnummerZusatz())
                    .withPlz(taifunData.getAddress().getPlz())
                    .withOrt(taifunData.getAddress().getOrt())
                    .withOrtsteil(taifunData.getAddress().getOrtsteil())
                    .build().getGeoId().getId();
            getTaifunDataFactory().updateAddress(this.taifunData, geoIdToSet);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error joining Taifun address with GeoId from standortDataBuilder: %s", e.getMessage()), e);
        }
        return this;
    }

    public AcceptanceTestDataBuilder withVorgabeMnet(LocalDateTime vorgabeMnet) {
        this.vorgabeMnet = vorgabeMnet;
        return this;
    }

    public LocalDateTime getVorgabeMnet() {
        return vorgabeMnet;
    }

    public AcceptanceTestDataBuilder withHurricanProduktId(Long hurricanProduktId) {
        this.hurricanProduktId = hurricanProduktId;
        return this;
    }

    public AcceptanceTestDataBuilder withCreateVormieter() {
        this.createVormieter = true;
        return this;
    }

    public AcceptanceTestDataBuilder withCreateAnsprechpartnerTechnik() {
        this.createProjectLead = true;
        return this;
    }

    public AcceptanceTestDataBuilder withStandortData(StandortDataBuilder<?> standortDataBuilder) {
        this.standortDataBuilder = standortDataBuilder;
        return this;
    }

    public AcceptanceTestDataBuilder withSessionId(Long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public AcceptanceTestDataBuilder withEqOutCarrierMnet() {
        this.eqOutCarrier = TNB.MNET.carrierNameUC;
        return this;
    }

    public AcceptanceTestDataBuilder withKundenuebergabe(String gebaeude, String etage, String raum) {
        endstelleConnect =
                new EndstelleConnectBuilder()
                        .withGebaude(gebaeude)
                        .withEtage(etage)
                        .withRaum(raum)
                        .build();
        return this;
    }

    /**
     * @return the generated {@link Auftrag} and all its created data; if 4-Draht the {@link CBVorgangSubOrder}s are
     * provided as well but the {@link Carrierbestellung} is same for both
     */
    public CreatedData build(Long sessionId) throws Exception {
        this.sessionId = sessionId;

        taifunData.setKundenname("Mustermann");
        taifunData.setKundenvorname("Max");
        taifunData = taifunData.persist();

        if (cancelHurricanOrdersWithSameTaifunId) {
            stornoHurricanOrders(taifunData.getBillingAuftrag().getAuftragNoOrig());
        }

        CreatedData createdData = createData();

        if (referencingCbBuilder != null) {
            Pair<Carrierbestellung, Auftrag> referencingData = createReferencingCarrierbestellung(createdData);
            withCarrierbestellungAuftragId4TalNa(referencingData.getSecond().getId());
        }

        createdData.carrierbestellung = createCarrierbestellung(createdData);

        if (isVierDraht) {
            String storeRangLeiste1 = rangLeiste1;
            String storeRangStift1 = rangStift1;
            try {
                withRangLeiste1(rangLeiste1ForRang2);
                withRangStift1(rangStift1ForRang2);

                CreatedData createdData2 = createData();
                createdData.cbVorgangSubOrders = createCbVorgangSubOrders(createdData2);
                // carrierbestellung is automatically assigned in createCbVorgang (anywhere)
            }
            finally {
                withRangLeiste1(storeRangLeiste1);
                withRangStift1(storeRangStift1);
            }
        }

        createdData.vorgabeMnet = vorgabeMnet;
        return createdData;
    }

    public CreatedData createData() throws Exception {
        CreatedData createdData = createAuftrag();
        createdData.endstellen = createEndstellen(createdData);
        assignRangierung(createdData);
        createdData.ansprechpartner.addAll(createEndstellenAnsprechpartner(createdData));
        createdData.user = createUser();
        createdData.montagehinweis = cbVorgangMontagehinweis;
        createdData.anbieterwechselTKG46 = cbVorgangAnbieterwechselTKG46;
        createdData.terminReservierungsId = terminReservierungsId;
        createdData.archiveDocuments = archiveDocuments;
        createdData.projektKenner = projektKenner;
        createdData.vorabstimmungsId = vorabstimmungsId;
        createdData.rufnummern = taifunData.getDialNumbers();
        return createdData;
    }

    private CreatedData createAuftrag() throws Exception {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setProdId(hurricanProduktId);
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setBearbeiter("test");
        auftragDaten.setAuftragNoOrig(taifunData.getBillingAuftrag().getAuftragNoOrig());

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setNiederlassungId(Niederlassung.ID_AUGSBURG);

        if (createProjectLead) {
            AKUser projectLead = createProjectLead();
            assertNotNull(projectLead, "Projekt-Verantwortlicher wurde nicht generiert!");
            assertNotNull(projectLead.getId(), "Projekt-Verantwortlicher besitzt keine ID!");
            auftragTechnik.setProjectLeadUserId(projectLead.getId());
        }

        Auftrag auftrag = ccAuftragService.createAuftrag(taifunData.getKunde().getKundeNo(), auftragDaten,
                auftragTechnik, sessionId, null);

        auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId());
        auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragIdTx(auftrag.getAuftragId());

        CreatedData createdData = new CreatedData();
        createdData.auftrag = auftrag;
        createdData.auftragDaten = auftragDaten;
        createdData.auftragTechnik = auftragTechnik;
        return createdData;
    }

    private List<Endstelle> createEndstellen(CreatedData createdData) throws Exception {
        assertNotNull(createdData.auftragTechnik, "require AuftragTechnik to create Endstellen.");

        createdData.endstellen = endstellenService.createEndstellen(createdData.auftragTechnik, Produkt.ES_TYP_NUR_B,
                sessionId);
        if (endstelleConnect != null) {
            endstelleConnect.setEndstelleId(createdData.endstellen.get(1).getId()); // auf EsB setzen
            connectService.saveEndstelleConnect(endstelleConnect);
        }

        return createdData.endstellen;
    }

    private List<Ansprechpartner> createEndstellenAnsprechpartner(CreatedData createdData) throws StoreException,
            ValidationException {
        List<Ansprechpartner> ansprechpartnerList = new ArrayList<>();

        for (Endstelle e : createdData.endstellen) {
            Ansprechpartner ansprechpartner = new Ansprechpartner();
            ansprechpartner.setAuftragId(createdData.auftrag.getAuftragId());
            ansprechpartner.setPreferred(Boolean.TRUE);
            ansprechpartner.setPrio(1);
            if (e.isEndstelleA()) {
                ansprechpartner.setTypeRefId(Ansprechpartner.Typ.ENDSTELLE_A.refId());
            }
            else {
                ansprechpartner.setTypeRefId(Ansprechpartner.Typ.ENDSTELLE_B.refId());
            }
            ansprechpartner.setAddress(createMontageleistungAddress(createdData));
            ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
            ansprechpartnerList.add(ansprechpartner);
        }

        return ansprechpartnerList;
    }

    private Carrierbestellung createCarrierbestellung(CreatedData createdData) throws Exception {
        assertNotNull(createdData.endstellen, "require Endstellen to create Carrierbestellung.");
        assertNotNull(createdData.endstellen.size() == 2, "require Endstelle B to create Carrierbestellung.");

        Carrierbestellung cb = new Carrierbestellung();
        final LocalDate vorgabeDatumLocalDate = DateCalculationHelper.addWorkingDays(LocalDate.now(), 10);
        final Date vorgabeDatum = DateConverterUtils.asDate(vorgabeDatumLocalDate);
        cb.setVorgabedatum(vorgabeDatum);
        cb.setBestelltAm(new Date());
        cb.setCarrier(Carrier.ID_DTAG);
        // cb.setCb2EsId(createdData.endstellen.get(1).getCb2EsId());
        if (carrierbestellungRealDate != null) {
            cb.setBereitstellungAm(carrierbestellungRealDate);
            cb.setZurueckAm(carrierbestellungRealDate);
        }
        cb.setKuendigungAnCarrier(carrierbestellungKuendigungAn);
        cb.setLbz(carrierbestellungLbz);
        cb.setVtrNr(carrierbestellungVtrNr);
        cb.setAuftragId4TalNA(carrierbestellungAuftragId4TalNa);

        if (createAnschlussinhaberAddress) {
            createAnschlussinhaber(createdData, cb);
            withCBVorgangMontagehinweis("Montagehinweis");
        }

        if (createVormieter) {
            createVormieter(cb);
        }
        if (vorabstimmung != null) {
            createVorabstimmung(vorabstimmung, createdData.endstellen.get(1), createdData.auftragDaten.getAuftragId());
        }
        if (vorabstimmungAbgebend != null) {
            vorabstimmungAbgebend.setAuftragId(createdData.auftrag.getAuftragId());
            vorabstimmungAbgebend.setEndstelleTyp(createdData.endstellen.get(1).getEndstelleTyp());
            witaVorabstimmungService.saveVorabstimmungAbgebend(vorabstimmungAbgebend);
        }

        cb = carrierService.saveCB(cb, createdData.endstellen.get(1));

        return cb;
    }

    private void createVorabstimmung(Vorabstimmung vorabstimmung, Endstelle endstelle, Long auftragId) {
        vorabstimmung.setAuftragId(auftragId);
        vorabstimmung.setEndstelleTyp(endstelle.getEndstelleTyp());
        witaVorabstimmungService.saveVorabstimmung(vorabstimmung);
    }

    private void createAnschlussinhaber(CreatedData createdData, Carrierbestellung cb) throws StoreException,
            ValidationException {
        CCAddress address = new CCAddress();
        address.setKundeNo(createdData.auftrag.getKundeNo());
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName((anschlussinhaberFirma) ? CCAddress.ADDRESS_FORMAT_BUSINESS
                : CCAddress.ADDRESS_FORMAT_RESIDENTIAL);

        address.setVorname(taifunData.getKunde().getVorname());
        address.setName(taifunData.getKunde().getName());

        address.setStrasse(createdData.address.getStrasse());
        address.setNummer(createdData.address.getNummer());
        address.setHausnummerZusatz("a");
        address.setStrasseAdd("Keller");
        address.setOrt(createdData.address.getOrt());
        address.setOrtsteil(createdData.address.getOrtsteil());
        address.setPlz(createdData.address.getPlz());
        address.setLandId("DE");
        address.setTelefon("0821/532324");
        address.setGebaeudeteilName("Gebaeudeteil");
        address.setGebaeudeteilZusatz("GebaeudeteilZusatz");
        address.setEmail("hurrican_developer@m-net.de");
        address = kundenService.saveCCAddress(address);
        cb.setAiAddressId(address.getId());
    }

    private CCAddress createMontageleistungAddress(CreatedData createdData) {
        CCAddress address = new CCAddress();
        address.setKundeNo(createdData.auftrag.getKundeNo());
        address.setAddressType(CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        address.setFormatName(CCAddress.ADDRESS_FORMAT_RESIDENTIAL);
        address.setName("Wita");
        address.setVorname("Montage");
        address.setStrasse(createdData.geoId.getStreet());
        address.setNummer(createdData.geoId.getHouseNum());
        address.setOrt(createdData.geoId.getCity());
        address.setPlz(createdData.geoId.getZipCode());
        address.setLandId("DE");
        address.setTelefon("0821/532324");
        address.setEmail("hurrican_developer@m-net.de");
        return address;
    }

    private void createVormieter(Carrierbestellung cb) {
        CarrierbestellungVormieter cbVormieter = new CarrierbestellungVormieter();
        cbVormieter.setNachname("Vormieter");
        cbVormieter.setVorname("Vormieter2");
        cbVormieter.setOnkz("4401");
        cbVormieter.setRufnummer("123456");
        cb.setCarrierbestellungVormieter(cbVormieter);
    }

    private Set<CBVorgangSubOrder> createCbVorgangSubOrders(CreatedData createdData2) {
        CBVorgangSubOrder subOrder = new CBVorgangSubOrder(createdData2.auftrag.getAuftragId(),
                createdData2.dtagPort.getDtagVerteilerLeisteStift(), true, null);
        return new HashSet<>(Collections.singletonList(subOrder));
    }

    private Pair<Carrierbestellung, Auftrag> createReferencingCarrierbestellung(CreatedData createdData)
            throws StoreException, ValidationException, FindException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN);
        auftragDaten.setStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        auftragDaten.setBearbeiter("test");
        auftragDaten.setAuftragNoOrig(null);

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setNiederlassungId(Niederlassung.ID_AUGSBURG);
        Auftrag auftrag = ccAuftragService.createAuftrag(taifunData.getKunde().getKundeNo(), auftragDaten,
                auftragTechnik, sessionId, null);

        auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId());
        auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragIdTx(auftrag.getAuftragId());

        List<Endstelle> endstellen = endstellenService.createEndstellen(auftragTechnik, Produkt.ES_TYP_NUR_B, sessionId);
        Endstelle endstelle = endstellen.get(1);
        endstelle.setHvtIdStandort(createdData.endstellen.get(1).getHvtIdStandort());

        Carrierbestellung referencingCb = referencingCbBuilder.get();
        referencingCb.setAiAddressId(createdData.address.getId());
        carrierService.saveCB(referencingCb, endstelle);

        createdData.referencingAuftragDaten = auftragDaten;
        createdData.referencingAuftragTechnik = auftragTechnik;
        createdData.referencingEndstellen = endstellen;
        createdData.referencingCarrierbestellung = referencingCb;

        endstelle = endstellenService.findEndstelle(endstelle.getId());
        createAndAssignRangierung(endstelle, referencingCbUetv, referencingCbRangSchnittstelle);
        endstelle = endstellenService.findEndstelle(endstelle.getId());

        endstellen.remove(1);
        endstellen.add(1, endstelle);

        return Pair.create(referencingCb, auftrag);
    }

    private AKUser createUser() throws Exception {
        AKUser user = new AKUserBuilder().init().withRandomLoginName().withName(userName).withPhone(userPhone)
                .withDepartmentId(AKDepartment.DEP_AM).setPersist(false).build();
        userService.save(user);
        return user;
    }

    private AKUser createProjectLead() throws Exception {
        AKUser projectLead = new AKUserBuilder().init().withRandomLoginName().withName("Project").withFirstName("Lead")
                .withPhone("0821/45003284").withDepartmentId(AKDepartment.DEP_NP).setPersist(false).build();
        userService.save(projectLead);
        return projectLead;
    }

    public AcceptanceTestDataBuilder withLageplan() {
        archiveDocuments.add(Pair.create(
                createArchiveDocument("0000001", Dateityp.JPEG, "123", ArchiveDocumentType.AUFTRAG),
                Anlagentyp.LAGEPLAN.value));
        return this;
    }

    public AcceptanceTestDataBuilder withCudaKuendigung() {
        archiveDocuments.add(Pair.create(
                createArchiveDocument("0000002", Dateityp.PDF, "123", ArchiveDocumentType.CUDA_KUENDIGUNG),
                Anlagentyp.KUENDIGUNGSSCHREIBEN.value));
        return this;
    }

    /**
     * Der Adresstyp ist eine 'Firma'. Diese Methode setzt die Attribute im Builder entsprechend
     */
    public AcceptanceTestDataBuilder withFirma() {
        withAddressType(CCAddress.ADDRESS_FORMAT_BUSINESS)
                .withHurricanProduktId(Produkt.AK_CONNECT);
        return this;
    }

    public AcceptanceTestDataBuilder withPortierungsauftrag() {
        archiveDocuments.add(Pair.create(
                createArchiveDocument("0000003", Dateityp.PDF, "123", ArchiveDocumentType.PORTIERUNGSAUFTRAG),
                Anlagentyp.PORTIERUNGSANZEIGE.value));
        return this;
    }

    public AcceptanceTestDataBuilder withKundenauftrag() {
        archiveDocuments.add(Pair.create(
                createArchiveDocument("0000004", Dateityp.PDF, "123", ArchiveDocumentType.AUFTRAG),
                Anlagentyp.KUNDENAUFTRAG.value));
        return this;
    }

    private ArchiveDocumentDto createArchiveDocument(String key, Dateityp dateityp, String vtrNr,
            ArchiveDocumentType documentType) {
        ArchiveDocumentDto archiveDoc = new ArchiveDocumentDto();
        archiveDoc.setKey(key);
        archiveDoc.setMimeType(dateityp.mimeTyp);
        archiveDoc.setFileExtension(dateityp.extension);
        archiveDoc.setVertragsNr(vtrNr);
        archiveDoc.setDocumentType(documentType);
        return archiveDoc;
    }

    /**
     * for StandortKunde address if address comes not from Taifun (use {@link #withHurricanProduktId(Long)} therefore)
     */
    public AcceptanceTestDataBuilder withAddressType(String addressType) {
        this.addressType = addressType;
        return this;
    }

    private void assignRangierung(CreatedData createdData) throws Exception {
        assertNotNull(createdData.auftrag, "require Auftrag to create Address for Endstelle");
        assertNotNull(createdData.endstellen, "require Endstellen to create Address for it");
        assertNotNull(createdData.endstellen.size() == 2, "require Endstelle B to create Address for it");

        createdData.geoId = standortDataBuilder.get().getGeoId();
        createdData.geoId2TechLocation = standortDataBuilder.get().getGeoId2TechLocation();

        CCAddress address = new CCAddress();
        address.setKundeNo(createdData.auftrag.getKundeNo());
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName(addressType);
        address.setName(taifunData.getKunde().getName());
        address.setVorname(taifunData.getKunde().getVorname());
        address.setStrasse(createdData.geoId.getStreet());
        address.setNummer(createdData.geoId.getHouseNum());
        address.setHausnummerZusatz(createdData.geoId.getHouseNumExtension());
        address.setOrt(createdData.geoId.getCity());
        address.setOrtsteil(createdData.geoId.getDistrict());
        address.setPlz(createdData.geoId.getZipCode());
        address.setLandId("DE");
        address.setTelefon("0821/532323");
        address.setStrasseAdd("Keller");
        address.setGebaeudeteilName("Gebäudeteil");
        address.setGebaeudeteilZusatz("GebäudeteilZusatz");
        address = kundenService.saveCCAddress(address);

        Endstelle endstelle = createdData.endstellen.get(1);
        endstelle.setGeoId(createdData.geoId.getId());
        endstelle.setAddressId(address.getId());
        endstelle.setHvtIdStandort(createdData.geoId2TechLocation.getHvtIdStandort());
        endstelle.setAnschlussart(Anschlussart.ANSCHLUSSART_HVT);
        endstelle = endstellenService.saveEndstelle(endstelle);

        Pair<Rangierung, Equipment> rangierungAndEquipment = createAndAssignRangierung(endstelle);
        Rangierung rangierung = rangierungAndEquipment.getFirst();
        assertNotNull(rangierung, "keine Rangierung erzeugt");
        assertNotNull(rangierung.getEsId(), "Rangierung keiner Endstelle zugeordnet");
        endstelle = endstellenService.findEndstelle(endstelle.getId());
        assertNotNull(endstelle.getRangierId(), "Endstelle keiner Rangierung zugeordnet");
        createdData.endstellen.remove(1);
        createdData.endstellen.add(1, endstelle);

        createdData.vierDraht = isVierDraht;
        createdData.address = address;
        createdData.dtagPort = rangierungAndEquipment.getSecond();
    }

    /**
     * Dummy-Rangierung mit DTAG Port anlegen und der Endstelle zuordnen
     */
    private Pair<Rangierung, Equipment> createAndAssignRangierung(Endstelle endstelle) throws StoreException, ValidationException, FindException {
        return createAndAssignRangierung(endstelle, uetv, rangierungSchnittstelle);
    }

    /**
     * Dummy-Rangierung mit DTAG Port anlegen und der Endstelle zuordnen
     */
    private Pair<Rangierung, Equipment> createAndAssignRangierung(Endstelle endstelle,
            Uebertragungsverfahren uetvToUse, RangSchnittstelle rangSt) throws StoreException, ValidationException, FindException {
        // @formatter:off
        Equipment dtagEquipment = new EquipmentBuilder()
                .withCarrier(eqOutCarrier)
                .withRangBucht(standortDataBuilder.get().getUevt())
                .withRangLeiste1(rangLeiste1)
                .withRangStift1(rangStift1)
                .withRangVerteiler(rangVerteiler)
                .withUETV(uetvToUse)
                .withRangSSType(rangSsType)
                .withKvzNummer(useKvz ? kvzNummer : null)
                .withKvzDoppelader(useKvz ? kvzDoppelader : null)
                .withRangSchnittstelle(rangSt)
                .setPersist(false).build();
        // @formatter:on

        dtagEquipment.setHvtIdStandort(endstelle.getHvtIdStandort());
        dtagEquipment.setUserW("acceptancetest");
        dtagEquipment = rangierungsService.saveEquipment(dtagEquipment);

        Rangierung rangierung = new RangierungBuilder().setPersist(false).build();
        rangierung.setHvtIdStandort(endstelle.getHvtIdStandort());
        rangierung.setEqOutId(dtagEquipment.getId());
        rangierung = rangierungsService.saveRangierung(rangierung, false);

        rangierungsService.attachRangierung2Endstelle(rangierung, null, endstelle);
        rangierung = rangierungsService.findRangierung(rangierung.getId());
        return Pair.create(rangierung, dtagEquipment);
    }

    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public GeneratedTaifunData getTaifunData() {
        return taifunData;
    }

    String getVorabstimmungsVertragsNr() {
        if (vorabstimmung != null) {
            return vorabstimmung.getProviderVtrNr();
        }
        return null;
    }

    private void stornoHurricanOrders(Long billingOrderNo) throws FindException, StoreException {
        List<AuftragDaten> toCancel = ccAuftragService.findAuftragDaten4OrderNoOrig(billingOrderNo);
        if (CollectionUtils.isNotEmpty(toCancel)) {
            for (AuftragDaten auftragDaten : toCancel) {
                auftragDaten.setStatusId(AuftragStatus.STORNO);
                
                ccAuftragService.saveAuftragDaten(auftragDaten, false);
            }
        }
    }

}
