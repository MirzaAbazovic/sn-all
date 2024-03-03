/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2016
 */
package de.augustakom.hurrican.service.cc.impl;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.hurrican.dao.cc.ProfileDAO;
import de.augustakom.hurrican.dao.cc.TechLeistungDAO;
import de.augustakom.hurrican.model.cc.*;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.*;

import static de.mnet.common.tools.DateConverterUtils.asDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = BaseTest.UNIT)
public class ProfileServiceImplUnitTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private ProfileDAO profileDAO;
    @Mock
    private TechLeistungDAO techLeistungDAO;


    @BeforeMethod
    public void configureMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private ProfileAuftrag generateProfileAuftrag(long timeInMillis) {
        ProfileAuftrag auftrag = new ProfileAuftrag();
        auftrag.setAuftragId(1L);
        auftrag.setGueltigBis(new Date(timeInMillis));
        return auftrag;
    }

    private ProfileAuftrag generateProfileAuftrag(Long auftragId, Date gueltigVon, Date gueltigBis) {
        ProfileAuftrag auftrag = new ProfileAuftrag();
        auftrag.setAuftragId(auftragId);
        auftrag.setGueltigVon(gueltigVon);
        auftrag.setGueltigBis(gueltigBis);
        return auftrag;
    }


    public void findNewestProfileAuftrag_AuftragsFound_ReturnNewest() {
        final long auftragID = 1L;
        final long newerTime = System.currentTimeMillis();
        final ProfileAuftrag auftragNewer = generateProfileAuftrag(newerTime);
        final ProfileAuftrag auftragOlder = generateProfileAuftrag(0);

        Mockito.when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragID)).
                thenReturn(Arrays.asList(auftragOlder, auftragNewer));

        ProfileAuftrag result = profileService.findNewestProfileAuftrag(auftragID);

        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getGueltigBis().getTime() == newerTime);
    }

    public void findNewestProfileAuftrag_AuftragsNotFound_ReturnNotPresent() {
        final long auftragID = 1L;
        Mockito.when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragID)).
                thenReturn(Collections.emptyList());

        ProfileAuftrag result = profileService.findNewestProfileAuftrag(auftragID);

        Assert.assertFalse(result != null);
    }

    public void findProfileAuftragForDate_keinProfilGefunden() {
        final long auftragID = 1L;
        final Date dateGueltig = new Date();
        Mockito.when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragID)).
                thenReturn(Collections.emptyList());
        ProfileAuftrag result = profileService.findProfileAuftragForDate(auftragID, dateGueltig);
        Assert.assertTrue(result == null);
    }

    public void findProfileAuftragForDate_genauEinProfilGefunden() {
        final long auftragID = 1L;
        Calendar inputDate = new GregorianCalendar();
        inputDate.set(2016, 10, 12);
        // Ein Satz soll in Vergangenheit des inputDates liegen
        Calendar dateFromPast = new GregorianCalendar();
        dateFromPast.set(2014, 10, 12);
        Calendar dateToPast = new GregorianCalendar();
        dateToPast.set(2014, 11, 30);
        final ProfileAuftrag auftragPast = generateProfileAuftrag(auftragID, dateFromPast.getTime(), dateToPast.getTime());
        // Ein Satz soll in Gegewart des inputDates liegen
        Calendar dateFromNow = (GregorianCalendar) inputDate.clone();
        Calendar dateToNow = new GregorianCalendar();
        dateToNow.set(2017, 1, 15);
        final ProfileAuftrag auftragNow = generateProfileAuftrag(auftragID, dateFromNow.getTime(), dateToNow.getTime());
        // Ein Satz soll in Zukunft des inputDates liegen
        Calendar dateFromFuture = new GregorianCalendar();
        dateFromFuture.set(2017, 1, 16);
        Calendar dateToFuture = new GregorianCalendar();
        dateToFuture.set(2022, 11, 31);
        final ProfileAuftrag auftragFuture = generateProfileAuftrag(auftragID, dateFromFuture.getTime(), dateToFuture.getTime());

        Mockito.when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragID)).
                thenReturn(Arrays.asList(auftragPast, auftragNow, auftragFuture));

        ProfileAuftrag result = profileService.findProfileAuftragForDate(auftragID, inputDate.getTime());
        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getGueltigBis().equals(dateToNow.getTime()));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void findProfileAuftragForDate_mehrereProfileGefunden_ExceptionTest() {
        final long auftragID = 1L;
        Calendar inputDate = new GregorianCalendar();
        inputDate.set(2016, 11, 22);
        // Zwei  Saetze sollen zu dem inputDate-Datum passen
        Calendar dateFromNow1 = new GregorianCalendar();
        dateFromNow1.set(2014, 10, 12);
        Calendar dateToNow1 = new GregorianCalendar();
        dateToNow1.set(2016, 11, 30);
        final ProfileAuftrag auftragNow1 = generateProfileAuftrag(auftragID, dateFromNow1.getTime(), dateToNow1.getTime());
        Calendar dateFromNow2 = new GregorianCalendar();
        dateFromNow2.set(2016, 11, 20);
        Calendar dateToNow2 = new GregorianCalendar();
        dateToNow2.set(2017, 1, 15);
        final ProfileAuftrag auftragNow2 = generateProfileAuftrag(auftragID, dateFromNow2.getTime(), dateToNow2.getTime());
        // Ein Satz soll in Zukunft des inputDates liegen
        Calendar dateFromFuture = new GregorianCalendar();
        dateFromFuture.set(2017, 1, 16);
        Calendar dateToFuture = new GregorianCalendar();
        dateToFuture.set(2022, 11, 31);
        final ProfileAuftrag auftragFuture = generateProfileAuftrag(auftragID, dateFromFuture.getTime(), dateToFuture.getTime());

        Mockito.when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", auftragID)).
                thenReturn(Arrays.asList(auftragNow1, auftragNow2, auftragFuture));

        ProfileAuftrag result = profileService.findProfileAuftragForDate(auftragID, inputDate.getTime());
    }

    public void findProfileDefaults_ValueFoundOnStandort_OverwriteProfileDefaults() {
        final String paramValue = "5", paramName = HVTStandort.HVT_STANDORT_LINE_SPECTRUM;
        final String standortValue = "3";
        final HWBaugruppe baugruppe = generateHWBaugruppe(1L);
        Mockito.when(profileDAO.findProfileParameters(Mockito.anyLong())).
                thenReturn(Collections.singletonList(generateProfileDefault(paramName, paramValue)));
        Mockito.when(profileDAO.findById(Mockito.anyLong(), Mockito.eq(HVTStandort.class))).
                thenReturn(generateHVTStandort(1L, standortValue));

        Map<String, List<ProfileParameter>> result = profileService.findProfileParametersGroupByName(1L, baugruppe);

        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.containsKey(paramName));
        Assert.assertTrue(result.get(paramName).size() == 2);
        Assert.assertTrue(result.get(paramName).get(1).getParameterValue().equals(standortValue));
    }

    public void findProfileDefaults_ValueNotFoundOnStandort_NoOverwrite() {
        final String paramValue = "5", paramName = HVTStandort.HVT_STANDORT_LINE_SPECTRUM;
        final HWBaugruppe baugruppe = generateHWBaugruppe(1L);
        Mockito.when(profileDAO.findProfileParameters(Mockito.anyLong())).
                thenReturn(Collections.singletonList(generateProfileDefault(paramName, paramValue)));
        Mockito.when(profileDAO.findById(Mockito.anyLong(), Mockito.eq(HVTStandort.class))).
                thenReturn(new HVTStandort());

        Map<String, List<ProfileParameter>> result = profileService.findProfileParametersGroupByName(1L, baugruppe);

        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.containsKey(paramName));
        Assert.assertTrue(result.get(paramName).get(0).getParameterValue().equals(paramValue));
    }

    public void findParameterMappers_MappersFound_ReturnsSortedList() {
        final String paramName1 = "name1", paramName2 = "name2", paramName3 = "name3";
        final ProfileParameterMapper mapper1 = generateMapper(paramName1, 1);
        final ProfileParameterMapper mapper2 = generateMapper(paramName2, 2);
        final ProfileParameterMapper mapper3 = generateMapper(paramName3, 3);

        Mockito.when(profileDAO.findParameterMapper(paramName1)).thenReturn(mapper1);
        Mockito.when(profileDAO.findParameterMapper(paramName2)).thenReturn(mapper2);
        Mockito.when(profileDAO.findParameterMapper(paramName3)).thenReturn(mapper3);

        final ProfileAuftragValue value1 = new ProfileAuftragValue(paramName1, "");
        final ProfileAuftragValue value2 = new ProfileAuftragValue(paramName2, "");
        final ProfileAuftragValue value3 = new ProfileAuftragValue(paramName3, "");

        List<ProfileParameterMapper> result = profileService.
                findParameterMappers(Sets.newHashSet(value3, value1, value2));

        Assert.assertTrue(result.size() == 3);
        Assert.assertTrue(result.get(0).equals(mapper1));
        Assert.assertTrue(result.get(1).equals(mapper2));
        Assert.assertTrue(result.get(2).equals(mapper3));
    }

    private ProfileParameterMapper generateMapper(String name, int sortOrder) {
        return new ProfileParameterMapper(name, null, null, null, sortOrder);
    }

    private HWBaugruppe generateHWBaugruppe(long baugruppenTypId) {
        HWBaugruppe baugruppe = new HWBaugruppe();
        HWBaugruppenTyp typ = new HWBaugruppenTyp();
        typ.setId(baugruppenTypId);
        baugruppe.setHwBaugruppenTyp(typ);
        return baugruppe;
    }

    private ProfileParameter generateProfileDefault(String paramName, String paramValue) {
        return new ProfileParameter(new HWBaugruppenTyp(), paramName, paramValue, true);
    }

    private HVTStandort generateHVTStandort(Long id, String standortValue) {
        HVTStandort standort = new HVTStandort();
        standort.setId(id);
        standort.setGfastStartfrequenz(standortValue);
        return standort;
    }

    public void createNewProfile_EquipmentIdAvailable_ReturnsNewProfileDefaultsEmpty() throws FindException {
        final long auftragId = 122227352511L;
        final Long hvtStandortId = 1L;

        final HVTStandort hvtStandort = generateHVTStandort(1l, "10");
        final Equipment equipment = generateEquipment(hvtStandortId);

        when(profileDAO.findEquipmentsByAuftragId(auftragId)).thenReturn(equipment);
        when(profileDAO.findById(hvtStandortId, HVTStandort.class)).thenReturn(hvtStandort);

        final ProfileAuftrag result = profileService.createNewProfile(auftragId, 1L);

        assertNotNull(result);
        assertNotNull(result.getProfileAuftragValues());
        assertTrue(result.getProfileAuftragValues().size() == 0);
    }

    private Equipment generateEquipment(Long hvtStandortId) {
        Equipment equipment = new Equipment();
        equipment.setId(100L);
        equipment.setHvtIdStandort(hvtStandortId);
        return equipment;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNewProfile_EquipmentIdNotFound_ReturnsIllegalArgumentException() throws FindException {
        final long auftragId = 122227352511L;
        Mockito.when(profileDAO.findEquipmentsByAuftragId(auftragId)).thenReturn(null);

        profileService.createNewProfile(auftragId, 1L);
    }

    private TechLeistung generateTechLeistung(String value, String typ) {
        TechLeistung techLeistung = new TechLeistung();
        techLeistung.setStrValue(value);
        techLeistung.setTyp(typ);
        return techLeistung;
    }

    @Test
    public void persistProfileAuftrag() {
        final ProfileAuftrag profileAuftrag = new ProfileAuftrag();
        profileAuftrag.setGueltigVon(new Date());
        final Either<String, ProfileAuftrag> result = profileService.persistProfileAuftrag(profileAuftrag);
        assertThat(result.isRight(), equalTo(true));
    }

    @Test
    public void persistProfileAuftrag_FehlermeldungWennGueltigVonInDerVergangenheit() {
        final ProfileAuftrag profileAuftrag = new ProfileAuftrag();
        profileAuftrag.setGueltigVon(asDate(LocalDate.now().minusDays(1).atStartOfDay()));
        final Either<String, ProfileAuftrag> result = profileService.persistProfileAuftrag(profileAuftrag);
        assertThat(result.isLeft(), equalTo(true));
    }

    @Test
    public void persistProfileAuftrag_gueltigBisVorherigerProfileWirdAngepasst() {
        final ProfileAuftrag newProfileAuftrag = new ProfileAuftrag();
        newProfileAuftrag.setAuftragId(815L);
        newProfileAuftrag.setGueltigVon(asDate(LocalDate.now().plusDays(2).atStartOfDay()));

        final ProfileAuftrag profileAuftragToChange = new ProfileAuftrag();
        profileAuftragToChange.setGueltigBis(asDate(LocalDate.now().plusDays(3).atStartOfDay()));

        final Date beforeNewStarts = new Date();
        final ProfileAuftrag profileAuftragNotToChange = new ProfileAuftrag();
        profileAuftragNotToChange.setGueltigBis(beforeNewStarts);

        when(profileDAO.findByProperty(ProfileAuftrag.class, "auftragId", newProfileAuftrag.getAuftragId()))
                .thenReturn(ImmutableList.of(profileAuftragToChange, profileAuftragNotToChange));

        profileService.persistProfileAuftrag(newProfileAuftrag);

        assertThat(profileAuftragToChange.getGueltigBis(), equalTo(newProfileAuftrag.getGueltigVon()));
        assertThat(profileAuftragNotToChange.getGueltigBis(), equalTo(beforeNewStarts));
    }
}
