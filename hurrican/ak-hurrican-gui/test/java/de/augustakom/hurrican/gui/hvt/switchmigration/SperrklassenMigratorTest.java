/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2011 11:10:12
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Testklasse fuer {@link SperrklassenMigrator}.
 *
 *
 * @since Release 10
 */
@Test(groups = { BaseTest.UNIT })
public class SperrklassenMigratorTest extends BaseTest {

    private SperrklassenMigrator cut;

    @BeforeMethod
    private void reset() {
        cut = new SperrklassenMigrator();
    }

    private Date getNow() {
        return Calendar.getInstance().getTime();
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = IllegalArgumentException.class)
    public void create_NullSet() {
        final HWSwitchType source = null;
        final HWSwitchType destination = null;
        cut = SperrklassenMigrator.create(source, destination, getNow());
    }

    @Test(groups = BaseTest.UNIT)
    public void create_ProperlySet() {
        final HWSwitchType source = HWSwitchType.IMS;
        final HWSwitchType destination = HWSwitchType.IMS;
        cut = SperrklassenMigrator.create(source, destination, getNow());
        assertEquals(cut.getSourceHwSwitchType(), source);
        assertEquals(cut.getDestinationHwSwitchType(), destination);
    }

    @DataProvider(name = "dataProviderIsLeistung2DNSperrklasseLeistung")
    protected Object[][] dataProviderIsLeistung2DNSperrklasseLeistung() {
        // @formatter:off
        return new Object[][] {
                {   leistungWithLeistung4dnAndParameterId(46L, 6L),       true },
                {   leistungWithLeistung4dnAndParameterId(46L, null),    true },
                {   leistungWithLeistung4dnAndParameterId(46L, 1L),      false },
                {   leistungWithLeistung4dnAndParameterId(null, null), false },
                {   leistungWithLeistung4dnAndParameterId(-1L, -1L),     false },
                {   leistungWithLeistung4dnAndParameterId(0L, 0L),       false },
                {   leistungWithLeistung4dnAndParameterId(6L, 46L),      false },
        };
        // @formatter:on
    }

    private Leistung2DN leistungWithLeistung4dnAndParameterId(Long leistung4dn, Long parameterId) {
        Leistung2DN result = new Leistung2DN();
        result.setLeistung4DnId(leistung4dn);
        result.setParameterId(parameterId);
        return result;
    }

    private Leistung2DN sperrklasseWithRealisierungAndKuendigung(Date realisierung, Date kuendigung) {
        Leistung2DN result = new Leistung2DN();
        result.setScvRealisierung(realisierung);
        result.setScvKuendigung(kuendigung);
        return result;
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "dataProviderIsLeistung2DNSperrklasseLeistung")
    public void isLeistung2DNSperrklasseLeistung(Leistung2DN leistung2dn, boolean expectedResult) {
        assertEquals(cut.isLeistung2DNSperrklasseLeistung(leistung2dn), expectedResult);
    }

    @DataProvider(name = "dataProviderIsSperrklassenLeistungCurrent")
    protected Object[][] dataProviderIsSperrklassenLeistungCurrent() {
        // @formatter:off
        return new Object[][] {
                { sperrklasseWithRealisierungAndKuendigung(null,      null),      OCTOBER,   false},
                { sperrklasseWithRealisierungAndKuendigung(null,      SEPTEMBER), OCTOBER,   false},
                { sperrklasseWithRealisierungAndKuendigung(null,      OCTOBER),   OCTOBER,   false},
                { sperrklasseWithRealisierungAndKuendigung(null,      NOVEMBER),  OCTOBER,   false},
                { sperrklasseWithRealisierungAndKuendigung(NOVEMBER,  null),      OCTOBER,   false},
                { sperrklasseWithRealisierungAndKuendigung(OCTOBER,   SEPTEMBER), NOVEMBER,  false},
                { sperrklasseWithRealisierungAndKuendigung(OCTOBER,   OCTOBER),   NOVEMBER,  false},
                { sperrklasseWithRealisierungAndKuendigung(OCTOBER,   NOVEMBER),  NOVEMBER,  false},
                { sperrklasseWithRealisierungAndKuendigung(SEPTEMBER, OCTOBER),   NOVEMBER,  false},
                { sperrklasseWithRealisierungAndKuendigung(SEPTEMBER, null),      SEPTEMBER, true},
                { sperrklasseWithRealisierungAndKuendigung(SEPTEMBER, OCTOBER),   SEPTEMBER, true},
                { sperrklasseWithRealisierungAndKuendigung(SEPTEMBER, NOVEMBER),  OCTOBER,   true},
                { sperrklasseWithRealisierungAndKuendigung(SEPTEMBER, null),      OCTOBER,   true},
        };
        // @formatter:on
    }

    private static final Date OCTOBER = new GregorianCalendar(2011, Calendar.OCTOBER, 3).getTime();
    private static final Date SEPTEMBER = new GregorianCalendar(2011, Calendar.SEPTEMBER, 3).getTime();
    private static final Date NOVEMBER = new GregorianCalendar(2011, Calendar.NOVEMBER, 3).getTime();


    @Test(groups = BaseTest.UNIT, dataProvider = "dataProviderIsSperrklassenLeistungCurrent")
    public void isSperrklassenLeistungCurrent(Leistung2DN leistung2dn, Date plannedExecution, boolean expectedResult) {
        assertEquals(cut.isSperrklassenLeistungCurrent(leistung2dn, plannedExecution), expectedResult);
    }

    @Test(groups = BaseTest.UNIT)
    public void checkMigratable_DataEmpty() throws FindException, StoreException, ServiceNotFoundException {
        assertNotNull(cut.checkMigratable(Collections.<SwitchMigrationView>emptyList()));
    }

    @Test(groups = BaseTest.UNIT)
    public void checkMigratable_OneOrderNoSperrklassenLeistungen() throws FindException, ServiceNotFoundException,
            StoreException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(true).when(spy).isMigrationNeeded();
        final SwitchMigrationView viewWithNoLeistungen = new SwitchMigrationView();
        viewWithNoLeistungen.setAuftragId(1000L);
        doReturn(Collections.emptyList()).when(spy).findAllSperrklassenLeistungen(viewWithNoLeistungen.getAuftragId());
        SperrklassenMigrator result = spy.checkMigratable(Arrays.asList(viewWithNoLeistungen));
        Assert.assertTrue(result.getLeistungenToMigrate().isEmpty());
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = IllegalStateException.class)
    public void checkMigratable_SperrklasseFoundButNoNumberSet() throws FindException, ServiceNotFoundException,
            StoreException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(true).when(spy).isMigrationNeeded();

        final SwitchMigrationView view = new SwitchMigrationView();
        view.setAuftragId(1000L);
        List<SwitchMigrationView> switchMigrationViews = Arrays.asList(view);

        Leistung2DN leistungWithSperrklasse = new Leistung2DN();
        List<Leistung2DN> leistung2dnList = Arrays.asList(leistungWithSperrklasse);

        doReturn(leistung2dnList).when(spy).findAllSperrklassenLeistungen(view.getAuftragId());
        doThrow(new IllegalStateException()).when(spy).getSperrklasseNumber(leistungWithSperrklasse);
        spy.checkMigratable(switchMigrationViews);
    }

    @Test(groups = BaseTest.UNIT)
    public void checkMigratable_TwoOrdersOneSperrklasseFoundWithDifferentNumbers() throws FindException, ServiceNotFoundException,
            StoreException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(true).when(spy).isMigrationNeeded();

        final SwitchMigrationView viewWithNoLeistungen = new SwitchMigrationView();
        viewWithNoLeistungen.setAuftragId(1000L);
        final SwitchMigrationView viewWithLeistung = new SwitchMigrationView();
        viewWithLeistung.setAuftragId(2000L);
        List<SwitchMigrationView> switchMigrationViews = Arrays.asList(viewWithLeistung, viewWithNoLeistungen);

        Leistung2DN leistungWithSperrklasse = new Leistung2DN();
        final String oldLeistungParameter = "1000";
        leistungWithSperrklasse.setLeistungParameter(oldLeistungParameter);
        final Integer newLeistungParameter = 2000;
        List<Leistung2DN> leistung2dnList = Arrays.asList(leistungWithSperrklasse);

        doReturn(leistung2dnList).when(spy).findAllSperrklassenLeistungen(viewWithLeistung.getAuftragId());
        doReturn(Collections.emptyList()).when(spy).findAllSperrklassenLeistungen(viewWithNoLeistungen.getAuftragId());

        doReturn(Integer.valueOf(oldLeistungParameter)).when(spy).getSperrklasseNumber(Mockito.eq(leistungWithSperrklasse));
        doReturn(Integer.valueOf(newLeistungParameter)).when(spy).findNewSperrklasseNumberForOldNumber(
                Mockito.eq(Integer.valueOf(oldLeistungParameter)));

        SperrklassenMigrator result = spy.checkMigratable(switchMigrationViews);
        List<Leistung2DN> listOfLeistungenToMigrate = result.getLeistungenToMigrate();
        assertFalse(listOfLeistungenToMigrate.isEmpty());
        assertEquals(listOfLeistungenToMigrate.size(), 1);
        final String leistungParameterResult = listOfLeistungenToMigrate.get(0).getLeistungParameter();
        assertEquals(Integer.valueOf(leistungParameterResult), newLeistungParameter);
    }

    @Test(groups = BaseTest.UNIT)
    public void checkMigratable_SperrklasseFoundWithEqualNumbers() throws FindException, ServiceNotFoundException,
            StoreException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(true).when(spy).isMigrationNeeded();

        final SwitchMigrationView viewWithLeistung = new SwitchMigrationView();
        viewWithLeistung.setAuftragId(2000L);
        List<SwitchMigrationView> switchMigrationViews = Arrays.asList(viewWithLeistung);

        Leistung2DN leistungWithSperrklasse = new Leistung2DN();
        final String oldLeistungParameter = "1000";
        leistungWithSperrklasse.setLeistungParameter(oldLeistungParameter);
        final Integer newLeistungParameter = 1000;
        List<Leistung2DN> leistung2dnList = Arrays.asList(leistungWithSperrklasse);

        doReturn(leistung2dnList).when(spy).findAllSperrklassenLeistungen(viewWithLeistung.getAuftragId());
        doReturn(Integer.valueOf(oldLeistungParameter)).when(spy).getSperrklasseNumber(Mockito.eq(leistungWithSperrklasse));
        doReturn(newLeistungParameter).when(spy).findNewSperrklasseNumberForOldNumber(
                Mockito.eq(Integer.valueOf(oldLeistungParameter)));

        SperrklassenMigrator result = spy.checkMigratable(switchMigrationViews);
        List<Leistung2DN> listOfLeistungenToMigrate = result.getLeistungenToMigrate();
        assertTrue(listOfLeistungenToMigrate.isEmpty());
    }

    @Test(groups = BaseTest.UNIT)
    public void checkMigratable_NoMigrationNeeded() throws FindException, StoreException, ServiceNotFoundException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(false).when(spy).isMigrationNeeded();
        final List<SwitchMigrationView> dummyMigrationData = Arrays.asList(new SwitchMigrationView(),
                new SwitchMigrationView());
        SperrklassenMigrator result = spy.checkMigratable(dummyMigrationData);
        Assert.assertTrue(result.getLeistungenToMigrate().isEmpty());
    }

    @Test(groups = BaseTest.UNIT)
    public void migrate_NoMigrationData() throws ServiceNotFoundException, StoreException {
        SperrklassenMigrator spy = spy(cut);
        List<Leistung2DN> leistung2dnList = Collections.emptyList();

        CCRufnummernService rufnummernService = mock(CCRufnummernService.class);
        doReturn(leistung2dnList).when(spy).getLeistungenToMigrate();
        doReturn(rufnummernService).when(spy).getRufnummernService();
        spy.migrate();
    }

    @Test(groups = BaseTest.UNIT)
    public void migrate_ProperlySaveThreeLeistung2Dn() throws ServiceNotFoundException, StoreException {
        SperrklassenMigrator spy = spy(cut);
        Leistung2DN first = new Leistung2DN();
        Leistung2DN second = new Leistung2DN();
        Leistung2DN third = new Leistung2DN();
        List<Leistung2DN> leistung2dnList = Arrays.asList(first, second, third);

        CCRufnummernService rufnummernService = mock(CCRufnummernService.class);
        doReturn(leistung2dnList).when(spy).getLeistungenToMigrate();
        doReturn(rufnummernService).when(spy).getRufnummernService();
        spy.migrate();
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = StoreException.class)
    public void migrate_StoreExceptionOnSave() throws ServiceNotFoundException, StoreException {
        SperrklassenMigrator spy = spy(cut);
        Leistung2DN errorLeistung2dn = new Leistung2DN();
        Leistung2DN anotherLeistung2dn = new Leistung2DN();
        Leistung2DN additionalLeistung2dn = new Leistung2DN();
        List<Leistung2DN> leistung2dnList = Arrays.asList(errorLeistung2dn, anotherLeistung2dn, additionalLeistung2dn);

        CCRufnummernService rufnummernService = mock(CCRufnummernService.class);
        doThrow(new StoreException()).when(rufnummernService).saveLeistung2DN(errorLeistung2dn);
        doReturn(leistung2dnList).when(spy).getLeistungenToMigrate();
        doReturn(rufnummernService).when(spy).getRufnummernService();
        spy.migrate();
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = IllegalStateException.class)
    public void findSperrklasseNumberForLeistung_NoLeistungParameterInLeistung() throws FindException, ServiceNotFoundException {
        SperrklassenMigrator spy = spy(cut);
        Leistung2DN leistung2dn = new Leistung2DN();
        leistung2dn.setLeistungParameter(null);
        spy.getSperrklasseNumber(leistung2dn);
    }

    @Test(groups = BaseTest.UNIT)
    public void findSperrklasseNumberForLeistung_ProperSperrklasseNumber() throws FindException, ServiceNotFoundException {
        SperrklassenMigrator spy = spy(cut);
        Sperrklasse sperrklasse = new Sperrklasse();
        doReturn(sperrklasse).when(spy).findSperrklasse(Mockito.any(Integer.class));

        final Integer newLeistungParameter = Integer.valueOf(30);
        doReturn(newLeistungParameter).when(spy).getSperrklasseNumberForDestinationSwitch(sperrklasse);

        Integer result = spy.findNewSperrklasseNumberForOldNumber(1234);
        assertNotNull(result);
        assertEquals(result, newLeistungParameter);
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = IllegalStateException.class)
    public void findSperrklasseNumberForLeistung_NoSperrklasseForLeistung() throws FindException, ServiceNotFoundException {
        SperrklassenMigrator spy = spy(cut);
        doReturn(null).when(spy).findSperrklasse(Mockito.any(Integer.class));
        spy.findNewSperrklasseNumberForOldNumber(1234);
    }

    @Test(groups = BaseTest.UNIT, expectedExceptions = IllegalStateException.class)
    public void findSperrklasseNumberForLeistung_NoSperrklasseInDestinationSwitch() throws FindException, ServiceNotFoundException {
        SperrklassenMigrator spy = spy(cut);
        Sperrklasse sperrklasse = new Sperrklasse();
        doReturn(sperrklasse).when(spy).findSperrklasse(Mockito.any(Integer.class));
        doReturn(null).when(spy).getSperrklasseNumberForDestinationSwitch(sperrklasse);
        spy.findNewSperrklasseNumberForOldNumber(1234);
    }

    @Test(groups = BaseTest.UNIT)
    public void findAllSperrklassenLeistungen_Empty() throws ServiceNotFoundException, FindException {
        SperrklassenMigrator spy = spy(cut);
        List<Leistung2DN> emptyList = Collections.emptyList();
        final Long auftragId = 4L;
        doReturn(emptyList).when(spy).findAlleLeistungen(auftragId);
        List<Leistung2DN> result = spy.findAllSperrklassenLeistungen(auftragId);
        assertNotNull(result);
        assertEmpty(result, "Liste sollte leer sein!");
    }

    @Test(groups = BaseTest.UNIT)
    public void findAllSperrklassenLeistungen_NoSperrklassenJustOther() throws ServiceNotFoundException, FindException {
        SperrklassenMigrator spy = spy(cut);
        Leistung2DN first = new Leistung2DN();
        Leistung2DN second = new Leistung2DN();
        List<Leistung2DN> list = Arrays.asList(first, second);
        final Long auftragId = 4L;
        doReturn(list).when(spy).findAlleLeistungen(auftragId);
        doReturn(getNow()).when(spy).getPlannedExecution();
        doReturn(false).when(spy).isLeistung2DNSperrklasseLeistung(Mockito.any(Leistung2DN.class));
        doReturn(false).when(spy).isSperrklassenLeistungCurrent(Mockito.any(Leistung2DN.class), Mockito.any(Date.class));
        List<Leistung2DN> result = spy.findAllSperrklassenLeistungen(auftragId);
        assertNotNull(result);
        assertEmpty(result, "Liste sollte leer sein!");
    }

    @Test(groups = BaseTest.UNIT)
    public void findAllSperrklassenLeistungen_AllSperrklassenFoundButOneCurrent() throws ServiceNotFoundException, FindException {
        SperrklassenMigrator spy = spy(cut);
        CCRufnummernService rufnummernService = mock(CCRufnummernService.class);
        doReturn(rufnummernService).when(spy).getRufnummernService();
        Leistung2DN current = new Leistung2DN();
        current.setDnNo(1234L);
        Leistung2DN inactiveFirst = new Leistung2DN();
        Leistung2DN inactiveSecond = new Leistung2DN();
        List<Leistung2DN> list = Arrays.asList(inactiveFirst, current, inactiveSecond);
        final Long auftragId = 4L;
        doReturn(list).when(rufnummernService).findDNLeistungen4Auftrag(auftragId);
        doReturn(true).when(spy).isLeistung2DNSperrklasseLeistung(Mockito.any(Leistung2DN.class));
        doReturn(true).when(spy).isSperrklassenLeistungCurrent(Mockito.eq(current), Mockito.any(Date.class));
        List<Leistung2DN> result = spy.findAllSperrklassenLeistungen(auftragId);
        assertNotNull(result);
        assertNotEmpty(result, "Liste sollte nicht leer sein!");
        assertEquals(result.size(), 1);
    }

} // end

