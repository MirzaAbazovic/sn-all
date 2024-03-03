package de.augustakom.hurrican.service.cc.impl.equipment;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCardBuilder;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2PortBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortViewBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class HwBaugruppenChangeCardHelperTest {

    private HwBaugruppenChangeCardHelper cut;
    @Mock
    private EQCrossConnectionService eqCrossConnectionService;
    @Mock
    private HWService hwService;
    @Mock
    private HWBaugruppenChangeService hwBaugruppenChangeService;
    @Mock
    private ProduktService produktService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private DSLAMService dslamService;
    @Mock
    private PhysikService physikService;

    @Mock
    Map<Long, Rangierung> aktualisierteRangierungen;

    private Long sessionId = 1L;

    private HWBaugruppenChange hwBgChange;
    private HWBaugruppeBuilder hwBgSrcBuilder;
    private HWBaugruppeBuilder hwBgDestBuilder;
    private Equipment eqOld1;
    private Equipment eqOld2;
    private Equipment eqNew1;
    private Equipment eqNew2;

    @BeforeMethod
    public void setUp() throws FindException {
        MockitoAnnotations.initMocks(this);
        buildTestData();

        cut = new HwBaugruppenChangeCardHelper(eqCrossConnectionService, hwService, hwBaugruppenChangeService,
                produktService, rangierungsService, physikService, hwBgChange, sessionId, dslamService, "userW");
    }

    private void buildTestData() throws FindException {
        hwBgSrcBuilder = new HWBaugruppeBuilder()
                .withRandomId().withEingebaut(Boolean.TRUE).setPersist(false);

        hwBgDestBuilder = new HWBaugruppeBuilder()
                .withRandomId().withEingebaut(Boolean.FALSE).setPersist(false);

        HVTStandortBuilder hvtStdBuilder = new HVTStandortBuilder().setPersist(false);

        eqOld1 = new EquipmentBuilder()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();
        eqOld2 = new EquipmentBuilder()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        eqNew1 = new EquipmentBuilder()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();
        eqNew2 = new EquipmentBuilder()
                .withHvtStandortBuilder(hvtStdBuilder).withBaugruppeBuilder(hwBgSrcBuilder).withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        HWBaugruppenChangePort2Port p2p1 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld1).withEquipmentNew(eqNew1).setPersist(false).build();
        HWBaugruppenChangePort2Port p2p2 = new HWBaugruppenChangePort2PortBuilder()
                .withEquipmentOld(eqOld2).withEquipmentNew(eqNew2).setPersist(false).build();

        hwBgChange = new HWBaugruppenChangeBuilder()
                .withChangeTypeReference(new ReferenceBuilder().withId(HWBaugruppenChange.ChangeType.CHANGE_CARD.refId()).setPersist(false).build())
                .withHWBaugruppenChangeCard(new HWBaugruppenChangeCardBuilder()
                        .withHWBaugruppeNew(hwBgDestBuilder.build())
                        .withHwBaugruppenSource(hwBgSrcBuilder.build())
                        .setPersist(false).build())
                .withHWBaugruppenChangePort2Port(p2p1)
                .withHWBaugruppenChangePort2Port(p2p2)
                .setPersist(false)
                .build();

        List<HWBaugruppenChangePort2PortView> portMappings = Arrays.asList(new HWBaugruppenChangePort2PortViewBuilder()
                .setPersist(false).withAuftragId(1L).build());
        when(hwBaugruppenChangeService.findPort2PortViews(hwBgChange)).thenReturn(portMappings);
    }


    public void testSwitchPortsInRangierung() throws StoreException, FindException {
        EquipmentBuilder eqInOldBuilder = new EquipmentBuilder()
                .setPersist(false)
                .withRandomId()
                .withHwEQN("1-1-1-1");

        EquipmentBuilder eqInNewBuilder = new EquipmentBuilder()
                .setPersist(false)
                .withId(eqInOldBuilder.build().getId() + 1)
                .withHwEQN("1-1-2-1");

        Rangierung rangierung = new RangierungBuilder()
                .setPersist(false)
                .withEqInBuilder(eqInOldBuilder)
                .build();

        cut.switchPortsInRangierung(rangierung, eqInOldBuilder.build(), eqInNewBuilder.build(), false);
        assertEquals(rangierung.getEqInId(), eqInNewBuilder.get().getId());
    }


    public void testDeclarePortsAndCardsAsRemoved() throws StoreException, ValidationException {
        //        cut.setHwBgChange(hwBgChange);
        cut.declarePortsAsRemoved();

        assertEquals(eqOld1.getStatus(), EqStatus.abgebaut);
        assertEquals(eqOld2.getStatus(), EqStatus.abgebaut);
        assertEquals(eqNew1.getStatus(), EqStatus.WEPLA);
        assertEquals(eqNew2.getStatus(), EqStatus.WEPLA);
    }

    public void testDeclareCardsAsRemoved() throws StoreException, ValidationException {
        cut.declareCardsAsRemoved(null);
        assertEquals(hwBgSrcBuilder.get().getEingebaut(), Boolean.FALSE);
    }

    @DataProvider
    Object[][] testDeclareCardsAsRemoved_WithAllDslPortsAreAbgebautPredicate_DataProvider() {
        return new Object[][] {
                { EqStatus.frei, EqStatus.abgebaut, Boolean.TRUE },
                { EqStatus.WEPLA, EqStatus.abgebaut, Boolean.TRUE },
                { EqStatus.abgebaut, EqStatus.abgebaut, Boolean.FALSE },
        };
    }

    @Test(dataProvider = "testDeclareCardsAsRemoved_WithAllDslPortsAreAbgebautPredicate_DataProvider")
    public void testDeclareCardsAsRemoved_WithAllDslPortsAreAbgebautPredicate(EqStatus eq1Status, EqStatus eq2Status, Boolean expectedResult) throws Exception {
        final Equipment eq1 = new EquipmentBuilder().withRandomId().withStatus(eq1Status).build();
        final Equipment eq2 = new EquipmentBuilder().withRandomId().withStatus(eq2Status).build();
        when(rangierungsService.findEquipments4HWBaugruppe(anyLong())).thenReturn(ImmutableList.of(eq1, eq2));
        cut.declareCardsAsRemoved(new HWBaugruppenChangePortConcentrationExecuter.AllPortsAreAbgebautPredicate(rangierungsService));
        assertEquals(hwBgSrcBuilder.get().getEingebaut(), expectedResult);
    }

    public void testDeclareNewPortsAndCardAsBuiltIn() throws StoreException, ValidationException {
        cut.declareNewPortsAndCardAsBuiltIn();

        assertEquals(eqNew1.getStatus(), EqStatus.rang);
        assertEquals(eqNew2.getStatus(), EqStatus.rang);
        assertEquals(hwBgDestBuilder.get().getEingebaut(), Boolean.TRUE);
    }

    public void testModifyEquipmentState() throws StoreException {
        Equipment equipment = new EquipmentBuilder()
                .withStatus(EqStatus.WEPLA)
                .setPersist(false).build();

        cut.modifyEquipmentState(equipment, EqStatus.abgebaut);
        assertEquals(equipment.getStatus(), EqStatus.abgebaut);
    }

    public void testModifyEquipmentStateWithNullValue() throws StoreException {
        cut.modifyEquipmentState(null, EqStatus.abgebaut);
    }


    public void testModifyHwBaugruppenState() throws StoreException, ValidationException {
        HWBaugruppe hwBaugruppe = new HWBaugruppeBuilder()
                .withEingebaut(Boolean.FALSE)
                .setPersist(false).build();

        cut.modifyHwBaugruppenState(hwBaugruppe, Boolean.TRUE);

        assertTrue(hwBaugruppe.getEingebaut());
    }

    @DataProvider
    public Object[][] changeDslamProfilesDataProvider() {
        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String futureDate1 = LocalDate.now().plusMonths(1).plusDays(3).atStartOfDay(ZoneId.systemDefault()).format(pattern);
        String futureDate2 = LocalDate.now().plusMonths(4).plusDays(3).atStartOfDay(ZoneId.systemDefault()).format(pattern);
        String futureDate3 = LocalDate.now().plusMonths(8).plusDays(3).atStartOfDay(ZoneId.systemDefault()).format(pattern);

        return new Object[][] {
                { new Pair[] { new Pair<>("2012-09-27", "2200-01-01") },
                        new String[] { "EXECUTIONDATE" },
                        new Pair[] { new Pair<>("EXECUTIONDATE", "2200-01-01") } },
                { new Pair[] { new Pair<>(futureDate1, "2200-01-01") },
                        new String[] { "EXECUTIONDATE" },
                        new Pair[] { new Pair<>(futureDate1, "2200-01-01") } },
                { new Pair[] { new Pair<>("2012-09-27", "2014-08-01"),
                        new Pair<>("2014-08-01", "2200-01-01") },
                        new String[] { "2014-08-01", "EXECUTIONDATE" },
                        new Pair[] { new Pair<>("EXECUTIONDATE", "2200-01-01") } },
                { new Pair[] { new Pair<>(futureDate1, futureDate2), new Pair<>(futureDate2, "2200-01-01") },
                        new String[] { "EXECUTIONDATE", "EXECUTIONDATE" },
                        new Pair[] { new Pair<>(futureDate1, futureDate2), new Pair<>(futureDate2, "2200-01-01") } },
                { new Pair[] { new Pair<>(futureDate1, futureDate2), new Pair<>(futureDate2, futureDate3),
                        new Pair<>(futureDate3, "2200-01-01") },
                        new String[] { "EXECUTIONDATE", "EXECUTIONDATE", "EXECUTIONDATE" },
                        new Pair[] { new Pair<>(futureDate1, futureDate2), new Pair<>(futureDate2, futureDate3),
                                new Pair<>(futureDate3, "2200-01-01") } },
        };
    }

    @Test(dataProvider = "changeDslamProfilesDataProvider")
    public void testChangeDslamProfiles(Pair<String, String>[] currentProfileDates,
            String[] expectedCurrentProfileGueltigBis, Pair<String, String>[] expectedNewProfileDates)
            throws Exception {
        when(rangierungsService.findEquipment(anyLong())).thenReturn(new Equipment());
        when(aktualisierteRangierungen.get(anyLong())).thenReturn
                (new RangierungBuilder().withEqOutBuilder(new EquipmentBuilder().setPersist(false)).setPersist(false).build());
        HWBaugruppenTyp hwBaugruppenTyp = new HWBaugruppenTypBuilder().withId(1L).setPersist(false).build();
        when(hwService.findBaugruppe(anyLong()))
                .thenReturn(new HWBaugruppeBuilder().withHwBaugruppenTyp(hwBaugruppenTyp)
                        .setPersist(false).build());

        Date execDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = ArrayListMultimap.create();
        dslamProfileMapping.put(Pair.create(1L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(100L).withBaugruppenTypId(hwBaugruppenTyp.getId())
                        .setPersist(false).build());
        // Profile mit Baugruppentyp-ID als neue Zuordnung  erwartet
        dslamProfileMapping.put(Pair.create(2L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(200L).withBaugruppenTypId(hwBaugruppenTyp.getId()).setPersist(false)
                        .build());
        dslamProfileMapping.put(Pair.create(2L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(201L).withBaugruppenTypId(hwBaugruppenTyp.getId() + 1)
                        .setPersist(false).build());
        dslamProfileMapping.put(Pair.create(2L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(202L).setPersist(false).build());
        // Profil ohne Baugruppentyp-ID als neue Zuordnung erwartet
        dslamProfileMapping.put(Pair.create(3L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(300L).setPersist(false).build());
        dslamProfileMapping.put(Pair.create(3L, (Uebertragungsverfahren) null),
                new DSLAMProfileBuilder().withId(301L).withBaugruppenTypId(hwBaugruppenTyp.getId() + 1)
                        .setPersist(false).build());

        List<Auftrag2DSLAMProfile> currentProfiles = new ArrayList<>();
        for (int i = 0; i < currentProfileDates.length; i++) {
            Pair<String, String> currentProfileDate = currentProfileDates[i];
            Auftrag2DSLAMProfile current = new Auftrag2DSLAMProfile();
            current.setGueltigVon(toDate(currentProfileDate.getFirst(), execDate));
            current.setGueltigBis(toDate(currentProfileDate.getSecond(), execDate));
            current.setDslamProfileId((long) (i + 1));
            currentProfiles.add(current);
        }
        when(dslamService.findAuftrag2DSLAMProfiles(anyLong())).thenReturn(currentProfiles);

        final List<Auftrag2DSLAMProfile> newProfiles = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Auftrag2DSLAMProfile toStore = (Auftrag2DSLAMProfile) invocationOnMock.getArguments()[0];
                if (toStore.getDslamProfileId() >= 100) {
                    newProfiles.add(toStore);
                }
                return null;
            }
        }).when(dslamService).saveAuftrag2DSLAMProfile(any(Auftrag2DSLAMProfile.class));

        cut.loadPortMappingViews();
        cut.changeDslamProfiles(execDate, dslamProfileMapping, aktualisierteRangierungen);

        for (int i = 0; i < currentProfiles.size(); i++) {
            Auftrag2DSLAMProfile currentProfile = currentProfiles.get(i);
            assertEquals(currentProfile.getGueltigBis(), toDate(expectedCurrentProfileGueltigBis[i], execDate));
        }
        assertEquals(expectedNewProfileDates.length, newProfiles.size());
        for (int i = 0; i < newProfiles.size(); i++) {
            Auftrag2DSLAMProfile newProfile = newProfiles.get(i);
            assertEquals(newProfile.getGueltigVon(), toDate(expectedNewProfileDates[i].getFirst(), execDate));
            assertEquals(newProfile.getGueltigBis(), toDate(expectedNewProfileDates[i].getSecond(), execDate));
            assertEquals(newProfile.getDslamProfileId() % 100, 0);
        }
    }


    private Date toDate(String isoDateString, Date executionDate) {
        if ("EXECUTIONDATE".equals(isoDateString)) {
            return executionDate;
        }
        return Date.from(LocalDate.parse(isoDateString).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void testFindBestDslamProfileMatchWithNoMatch() {
        final Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = createDslamProfiles(false);
        assertNull(cut.findBestDslamProfileMatch(new HWBaugruppenTypBuilder().withId(100L).build(), 100L, null,
                dslamProfileMapping));
    }

    public void testFindBestDslamProfileMatchWithDefault() {
        final Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = createDslamProfiles(true);
        DSLAMProfile result = cut.findBestDslamProfileMatch(new HWBaugruppenTypBuilder().withId(100L).build(), 1L, null,
                dslamProfileMapping);
        assertNull(result.getBaugruppenTypId());
    }

    public void testFindBestDslamProfileMatchWithMatch() {
        final Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = createDslamProfiles(true);
        DSLAMProfile result = cut.findBestDslamProfileMatch(new HWBaugruppenTypBuilder().withId(2L).build(), 1L, null,
                dslamProfileMapping);
        assertEquals(result.getBaugruppenTypId(), new Long(2));
    }

    public void testFindBestDslamProfileMatchWithUetvMatch() {
        final Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = createDslamProfiles(true);
        DSLAMProfile result = cut.findBestDslamProfileMatch(new HWBaugruppenTypBuilder().withId(3L).build(), 2L,
                Uebertragungsverfahren.H04, dslamProfileMapping);
        assertEquals(result.getBaugruppenTypId(), new Long(3));
    }

    private Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> createDslamProfiles(boolean addDefault) {
        List<DSLAMProfile> profiles = new ArrayList<>();
        DSLAMProfileBuilder dslamProfileBuilder = new DSLAMProfileBuilder();
        if (addDefault) {
            profiles.add(dslamProfileBuilder.withBaugruppenTypId(null).build());
        }
        profiles.add(dslamProfileBuilder.withBaugruppenTypId(1L).build());
        profiles.add(dslamProfileBuilder.withBaugruppenTypId(2L).build());
        profiles.add(dslamProfileBuilder.withBaugruppenTypId(3L).build());

        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> profileMap = ArrayListMultimap.create();
        profileMap.putAll(Pair.create(1L, (Uebertragungsverfahren) null), profiles);

        profileMap.put(Pair.create(2L, Uebertragungsverfahren.H04), dslamProfileBuilder.withBaugruppenTypId(3L).build());
        return profileMap;
    }
}
