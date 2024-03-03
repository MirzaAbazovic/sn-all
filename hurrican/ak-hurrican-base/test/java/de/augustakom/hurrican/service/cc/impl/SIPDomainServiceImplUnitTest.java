/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 16:23:02
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.impl.SIPDomainDAOImpl;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.EGType2SIPDomainBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomainBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Unit Tests zur Validierung der {@code VoIPServiceImpl}
 */
@Test(groups = { BaseTest.UNIT })
public class SIPDomainServiceImplUnitTest extends BaseTest {

    private static final String CONST_SWITCH_KENNUNG = "AUG01";
    private static final String CONST_SIP_DOMAIN = "maxi.m-call.de";

    @Mock
    private SIPDomainDAOImpl sipDomainDAO;
    @Mock
    private EndgeraeteService endgeraeteServiceMock;
    @Mock
    private CCAuftragService auftragServiceMock;
    @Mock
    private ReferenceService referenceServiceMock;
    @InjectMocks
    @Spy
    private SIPDomainServiceImpl cut;

    private HWSwitch hwSwitch;
    private Reference sipDomainRef;
    private AuftragDaten auftragDaten;

    @BeforeMethod
    public void setUp(Method method) throws FindException, StoreException {
        MockitoAnnotations.initMocks(this);

        hwSwitch = new HWSwitchBuilder()
                .setPersist(false)
                .withName(CONST_SWITCH_KENNUNG)
                .build();

        sipDomainRef = new ReferenceBuilder()
                .setPersist(false)
                .withRandomId()
                .withStrValue(CONST_SIP_DOMAIN)
                .build();

        auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(1L);
        auftragDaten.setProdId(513L); // Maxi Glasfaser-DSL Doppel-Flat
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragWithoutSwitch() throws FindException {
        when(auftragServiceMock.getSwitchKennung4Auftrag(any(Long.class))).thenReturn(null);
        Reference refSIPDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());
        assertNull(refSIPDomain, "SIP Domäne muss null sein!");
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragWithAuftragDatenIsNull() throws FindException {
        when(auftragServiceMock.getSwitchKennung4Auftrag(any(Long.class))).thenReturn(hwSwitch);
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(null);

        Reference refSIPDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());

        assertNull(refSIPDomain, "SIP Domäne muss null sein!");
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragWithEmptyDomainList() throws FindException {
        when(auftragServiceMock.getSwitchKennung4Auftrag(any(Long.class))).thenReturn(hwSwitch);
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        when(sipDomainDAO.querySIPDomain4Produkt(any(Produkt2SIPDomain.class))).thenReturn(Lists.newArrayList());
        when(sipDomainDAO.querySIPDomain4Eg(any(EGType2SIPDomain.class))).thenReturn(Lists.newArrayList());

        Reference resultSipDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());

        assertNull(resultSipDomain, "SIP Domäne muss null sein!");
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragOneSIPDomain() throws FindException {
        List<Produkt2SIPDomain> sipDomains = new ArrayList<>();
        Produkt2SIPDomain produkt2SIPDomain = new Produkt2SIPDomain();
        produkt2SIPDomain.setDefaultDomain(true);
        produkt2SIPDomain.setSipDomainRef(sipDomainRef);
        sipDomains.add(produkt2SIPDomain);

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setHwSwitch(hwSwitch);

        when(auftragServiceMock.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(auftragTechnik);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragDaten.getAuftragId())).thenReturn(Collections.emptyList());
        when(sipDomainDAO.querySIPDomain4Produkt(any(Produkt2SIPDomain.class))).thenReturn(sipDomains);

        Reference resultSipDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());

        assertNotNull(resultSipDomain, "SIP Domäne darf nicht null sein!");
        assertTrue(sipDomainRef == resultSipDomain, "SIP Domäne weicht vom Erwartungswert ab!");
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragFromEg() throws FindException {
        List<EGType2SIPDomain> sipDomains = new ArrayList<>();
        EGType2SIPDomain egType2SIPDomain = new EGType2SIPDomain();
        egType2SIPDomain.setSipDomainRef(sipDomainRef);
        sipDomains.add(egType2SIPDomain);

        List<EGConfig> egConfigs = new ArrayList<>();
        egConfigs.add(new EGConfig());

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setHwSwitch(hwSwitch);

        when(auftragServiceMock.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(auftragTechnik);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragDaten.getAuftragId())).thenReturn(egConfigs);
        when(sipDomainDAO.querySIPDomain4Produkt(any(Produkt2SIPDomain.class))).thenReturn(Collections.emptyList());
        when(sipDomainDAO.querySIPDomain4Eg(any(EGType2SIPDomain.class))).thenReturn(sipDomains);

        Reference resultSipDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());

        assertNotNull(resultSipDomain, "SIP Domäne darf nicht null sein!");
        assertTrue(sipDomainRef == resultSipDomain, "SIP Domäne weicht vom Erwartungswert ab!");
    }

    @Test
    public void testFindDefaultSIPDomain4AuftragMoreSIPDomains() throws FindException {
        List<Produkt2SIPDomain> sipDomains = new ArrayList<>();
        Produkt2SIPDomain produkt2SIPDomain = new Produkt2SIPDomain();
        produkt2SIPDomain.setSipDomainRef(sipDomainRef);
        sipDomains.add(produkt2SIPDomain);

        Produkt2SIPDomain produkt2SIPDomainTwo = new Produkt2SIPDomain();
        Reference sipDomainTwo = new Reference();
        sipDomainTwo.setId(2L);
        sipDomainTwo.setStrValue("mag.sie.de");
        produkt2SIPDomainTwo.setSipDomainRef(sipDomainTwo);
        produkt2SIPDomainTwo.setDefaultDomain(true);
        sipDomains.add(produkt2SIPDomainTwo);

        AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setHwSwitch(hwSwitch);

        when(auftragServiceMock.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(auftragTechnik);
        when(auftragServiceMock.findAuftragDatenByAuftragIdTx(any(Long.class))).thenReturn(auftragDaten);
        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragDaten.getAuftragId())).thenReturn(Collections.emptyList());
        final List<Produkt2SIPDomain> defaultDomains = sipDomains.stream().filter(s -> Boolean.TRUE.equals(s.getDefaultDomain())).collect(Collectors.toList());
        when(sipDomainDAO.querySIPDomain4Produkt(any(Produkt2SIPDomain.class))).thenReturn(defaultDomains);

        Reference resultSipDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());
        assertNotNull(resultSipDomain, "SIP Domäne darf nicht null sein!");
        assertEquals(resultSipDomain, sipDomainTwo, "SIP Domäne weicht vom Erwartungswert ab!");
        final List<Reference> produkt2SIPDomains = cut.findPossibleSIPDomains4Auftrag(auftragDaten.getAuftragId(), true);
        assertEquals(produkt2SIPDomains.get(0), sipDomainTwo, "SIP Domäne weicht vom Erwartungswert ab!");

        //now check with EGs
        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragDaten.getAuftragId())).thenReturn(Collections.singletonList(new EGConfig()));
        Reference sipDomainEG = new Reference();
        sipDomainEG.setId(3L);
        sipDomainEG.setStrValue("eg.domain.de");

        EGType2SIPDomain egtyp2SipDomain = new EGType2SIPDomain();
        egtyp2SipDomain.setSipDomainRef(sipDomainEG);
        when(sipDomainDAO.querySIPDomain4Eg(any(EGType2SIPDomain.class))).thenReturn(Collections.singletonList(egtyp2SipDomain));

        resultSipDomain = cut.findDefaultSIPDomain4Auftrag(auftragDaten.getAuftragId());
        assertNotNull(resultSipDomain, "SIP Domäne darf nicht null sein!");
        assertEquals(resultSipDomain, sipDomainEG, "SIP Domäne weicht vom Erwartungswert ab!");

        final List<Reference> allPossibleDomain = cut.findPossibleSIPDomains4Auftrag(auftragDaten.getAuftragId(), false);
        assertEquals(allPossibleDomain.get(0), sipDomainEG, "SIP Domäne weicht vom Erwartungswert ab!");
        assertEquals(allPossibleDomain.get(1), sipDomainTwo, "SIP Domäne weicht vom Erwartungswert ab!");
    }

    @Test
    public void testFindPossibleSIPDomainsWithMultipleDomains4Eg() throws FindException {
        List<EGType2SIPDomain> egTypes2SipDomain = new ArrayList<>();
        egTypes2SipDomain.add(new EGType2SIPDomainBuilder()
                .withHwSwitch(hwSwitch)
                .withSIPDomainRef(sipDomainRef)
                .build());
        egTypes2SipDomain.add(new EGType2SIPDomainBuilder()
                .withHwSwitch(hwSwitch)
                .withSIPDomainRef(sipDomainRef)
                .build());
        doReturn(egTypes2SipDomain).when(cut).findSipDomains4EgTyp(null, hwSwitch);
        doReturn(new ArrayList<Produkt2SIPDomain>()).when(cut).findSIPDomains4Produkt(1L, hwSwitch, null, null);

        List<Reference> result = cut.findPossibleSIPDomains(1L, 2L, hwSwitch, false);

        assertNotNull(result);
        assertTrue(result.size() == 1, "Zwei EG Typen, aber nur eine SIP Domäne!");
    }

    @DataProvider
    public Object[][] dataProviderMigrateSIPDomain() {
        Reference currentSIPDomain = new Reference();
        currentSIPDomain.setStrValue("current.sip.m-net.de");
        Reference defaultSIPDomain = new Reference();
        defaultSIPDomain.setStrValue("default.sip.m-net.de");
        Produkt2SIPDomain currentProdukt2SIPDomain = new Produkt2SIPDomain();
        currentProdukt2SIPDomain.setSipDomainRef(currentSIPDomain);
        List<Produkt2SIPDomain> noSwitchP2SD = new ArrayList<>();
        noSwitchP2SD.add(currentProdukt2SIPDomain);
        List<Produkt2SIPDomain> currentP2SD = new ArrayList<>();
        currentP2SD.add(currentProdukt2SIPDomain);
        Produkt2SIPDomain defaultProdukt2SIPDomain = new Produkt2SIPDomain();
        defaultProdukt2SIPDomain.setSipDomainRef(defaultSIPDomain);
        List<Produkt2SIPDomain> defaultP2SD = new ArrayList<>();
        defaultP2SD.add(defaultProdukt2SIPDomain);

        return new Object[][] {
                //DN SIP Domain,    result 2),    result 3),   result 4),   expected result
                { null, null, null, null, null },             // Fall 1 (siehe Javadoc@migrateSIPDomain())
                { currentSIPDomain, noSwitchP2SD, null, null, currentSIPDomain }, // Fall 2 (siehe Javadoc@migrateSIPDomain())
                { currentSIPDomain, null, currentP2SD, null, currentSIPDomain }, // Fall 3 (siehe Javadoc@migrateSIPDomain())
                { currentSIPDomain, null, currentP2SD, defaultP2SD, currentSIPDomain }, // wie Fall 3 (siehe Javadoc@migrateSIPDomain())
                { currentSIPDomain, null, null, defaultP2SD, defaultSIPDomain }, // Fall 4.1 (siehe Javadoc@migrateSIPDomain())
                { currentSIPDomain, null, null, null, null }              // Fall 4.2 (siehe Javadoc@migrateSIPDomain())
        };
    }

    @Test(dataProvider = "dataProviderMigrateSIPDomain")
    public void testMigrateSIPDomain(Reference currentSIPDomain, List<Produkt2SIPDomain> noSwitchP2SD,
            List<Produkt2SIPDomain> currentP2SD, List<Produkt2SIPDomain> defaultP2SD, Reference expectedSIPDomain)
            throws FindException {
        HWSwitch destSwitch = new HWSwitch();
        destSwitch.setName("MUC06");
        when(sipDomainDAO.querySIPDomain4Produkt(argThat(new P2SIPDomainExampleMatcher(currentSIPDomain, null, null))))
                .thenReturn(noSwitchP2SD);
        when(sipDomainDAO.querySIPDomain4Produkt(argThat(new P2SIPDomainExampleMatcher(currentSIPDomain, destSwitch, null))))
                .thenReturn(currentP2SD);
        when(sipDomainDAO.querySIPDomain4Produkt(argThat(new P2SIPDomainExampleMatcher(null, destSwitch, Boolean.TRUE))))
                .thenReturn(defaultP2SD);

        Reference migratedSIPDomain = cut.migrateSIPDomain(Long.valueOf(1), currentSIPDomain, destSwitch);
        assertTrue(migratedSIPDomain == expectedSIPDomain, "Migrierte SIP Domäne weicht vom Erwartungswert ab!");
    }

    class P2SIPDomainExampleMatcher extends ArgumentMatcher<Produkt2SIPDomain> {
        private final Reference sipDomain;
        private final Boolean defaultDomain;
        private final HWSwitch hwSwitch;

        private P2SIPDomainExampleMatcher(Reference sipDomain, HWSwitch hwSwitch, Boolean defaultDomain) {
            this.sipDomain = sipDomain;
            this.defaultDomain = defaultDomain;
            this.hwSwitch = hwSwitch;
        }

        @Override
        public boolean matches(Object object) {
            if (object instanceof Produkt2SIPDomain) {
                Produkt2SIPDomain p2sipDomain = (Produkt2SIPDomain) object;
                if ((sipDomain == p2sipDomain.getSipDomainRef())
                        && (defaultDomain == p2sipDomain.getDefaultDomain())
                        && (hwSwitch == p2sipDomain.getHwSwitch())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void testFindPossibleSIPDomainsWithDefaultsAsHigherPrio() throws Exception {
        final Produkt2SIPDomain produkt2SIPDomainDefault = new Produkt2SIPDomainBuilder()
                .withRandomId()
                .withDefaultDomain(Boolean.TRUE)
                .withSIPDomainRef(new ReferenceBuilder().withRandomId().build())
                .build();
        final Produkt2SIPDomain produkt2SIPDomainNotDefault = new Produkt2SIPDomainBuilder()
                .withRandomId()
                .withDefaultDomain(Boolean.FALSE)
                .withSIPDomainRef(new ReferenceBuilder().withRandomId().build())
                .build();

        when(endgeraeteServiceMock.findEgConfigs4Auftrag(anyLong())).thenReturn(Collections.emptyList());
        when(sipDomainDAO.querySIPDomain4Produkt(any(Produkt2SIPDomain.class)))
                .thenReturn(ImmutableList.of(produkt2SIPDomainNotDefault, produkt2SIPDomainDefault));

        final List<Reference> result = cut.findPossibleSIPDomains(815L, 815L, new HWSwitch(), true);

        assertThat(result,
                Matchers.contains(produkt2SIPDomainDefault.getSipDomainRef(), produkt2SIPDomainNotDefault.getSipDomainRef()));

    }

    @Test(expectedExceptions = FindException.class)
    public void testFindSipDomain4EgsWithTwoSipDomains() throws FindException {
        Long auftragId = 1L;
        EGTypeBuilder egTypeBuilder1 = new EGTypeBuilder().withHersteller("A").withModell("a");
        EGTypeBuilder egTypeBuilder2 = new EGTypeBuilder().withHersteller("A").withModell("b");
        EGConfig egConfig1 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder1).build();
        EGConfig egConfig2 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder2).build();
        List<EGConfig> egConfigs = Arrays.asList(egConfig1, egConfig2);
        HWSwitch hwSwitch = new HWSwitchBuilder().build();

        Reference sipDomainRef1 = new ReferenceBuilder().withStrValue("sip.m-call.de").build();
        Reference sipDomainRef2 = new ReferenceBuilder().withStrValue("sip2.m-call.de").build();

        EGType2SIPDomain egType2SIPDomain1 = new EGType2SIPDomainBuilder().withSIPDomainRef(sipDomainRef1).build();
        List<EGType2SIPDomain> egType2SipDomains1 = new ArrayList<>();
        egType2SipDomains1.add(egType2SIPDomain1);

        EGType2SIPDomain egType2SIPDomain2 = new EGType2SIPDomainBuilder().withSIPDomainRef(sipDomainRef2).build();
        List<EGType2SIPDomain> egType2SipDomains2 = new ArrayList<>();
        egType2SipDomains2.add(egType2SIPDomain2);

        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragId)).thenReturn(egConfigs);
        doReturn(egType2SipDomains1).when(cut).findSipDomains4EgTyp(egTypeBuilder1.get(), hwSwitch);
        doReturn(egType2SipDomains2).when(cut).findSipDomains4EgTyp(egTypeBuilder2.get(), hwSwitch);

        cut.findSipDomain4Egs(auftragId, hwSwitch);
    }

    @Test
    public void testFindSipDomain4EgsWithOneSipDomain() throws FindException {
        Long auftragId = 1L;
        EGTypeBuilder egTypeBuilder1 = new EGTypeBuilder().withHersteller("A").withModell("a");
        EGTypeBuilder egTypeBuilder2 = new EGTypeBuilder().withHersteller("A").withModell("b");
        EGConfig egConfig1 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder1).build();
        EGConfig egConfig2 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder2).build();
        List<EGConfig> egConfigs = Arrays.asList(egConfig1, egConfig2);
        HWSwitch hwSwitch = new HWSwitchBuilder().build();

        Reference sipDomainRef1 = new ReferenceBuilder().withStrValue("sip.m-call.de").build();

        EGType2SIPDomain egType2SIPDomain1 = new EGType2SIPDomainBuilder().withSIPDomainRef(sipDomainRef1).build();
        List<EGType2SIPDomain> egType2SipDomains1 = new ArrayList<>();
        egType2SipDomains1.add(egType2SIPDomain1);

        EGType2SIPDomain egType2SIPDomain2 = new EGType2SIPDomainBuilder().withSIPDomainRef(sipDomainRef1).build();
        List<EGType2SIPDomain> egType2SipDomains2 = new ArrayList<>();
        egType2SipDomains2.add(egType2SIPDomain2);

        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragId)).thenReturn(egConfigs);
        doReturn(egType2SipDomains1).when(cut).findSipDomains4EgTyp(egTypeBuilder1.get(), hwSwitch);
        doReturn(egType2SipDomains2).when(cut).findSipDomains4EgTyp(egTypeBuilder2.get(), hwSwitch);

        Optional<Reference> result = cut.findSipDomain4Egs(auftragId, hwSwitch);
        assertTrue(result.isPresent());
        assertTrue(sipDomainRef1.getStrValue().equals(result.get().getStrValue()));
    }

    @Test
    public void testFindSipDomain4EgsWithNoSipDomains() throws FindException {
        Long auftragId = 1L;
        EGTypeBuilder egTypeBuilder1 = new EGTypeBuilder().withHersteller("A").withModell("a");
        EGTypeBuilder egTypeBuilder2 = new EGTypeBuilder().withHersteller("A").withModell("b");
        EGConfig egConfig1 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder1).build();
        EGConfig egConfig2 = new EGConfigBuilder().withEGTypeBuilder(egTypeBuilder2).build();
        List<EGConfig> egConfigs = Arrays.asList(egConfig1, egConfig2);
        HWSwitch hwSwitch = new HWSwitchBuilder().build();

        when(endgeraeteServiceMock.findEgConfigs4Auftrag(auftragId)).thenReturn(egConfigs);
        doReturn(new ArrayList<>()).when(cut).findSipDomains4EgTyp(egTypeBuilder1.get(), hwSwitch);
        doReturn(new ArrayList<>()).when(cut).findSipDomains4EgTyp(egTypeBuilder2.get(), hwSwitch);

        Optional<Reference> result = cut.findSipDomain4Egs(auftragId, hwSwitch);
        assertTrue(!result.isPresent());
    }
}
