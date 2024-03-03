/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.10.2011 13:47:33
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.SperrklasseDAO;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse.SperrklassenTypEnum;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Testklasse fuer {@link SperrklasseServiceImpl}.
 *
 *
 * @since Release 10
 */
@Test(groups = BaseTest.UNIT)
public class SperrklasseServiceImplTest extends BaseTest {

    private SperrklasseDAO dao;
    private SperrklasseServiceImpl sut;

    @BeforeMethod
    public void setup() {
        sut = Mockito.spy(new SperrklasseServiceImpl());
        dao = Mockito.mock(SperrklasseDAO.class);
        sut.setDAO(dao);
    }

    /**
     * Testmethode fuer {@link CCRufnummernServiceImpl#findPossibleSperrtypen(Sperrklasse, HWSwitchType)}. Es werden
     * keine {@link Sperrklasse}n gefunden.
     */
    @Test(groups = BaseTest.UNIT)
    public void findPossibleSperrtypen_NoSperrklassenFoundEmptyList() {
        final Sperrklasse example = new Sperrklasse();
        when(dao.queryByExample(example, Sperrklasse.class)).thenReturn(Collections.<Sperrklasse>emptyList());
        List<Long> result = sut.findPossibleSperrtypen(example, HWSwitchType.EWSD);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(groups = BaseTest.UNIT)
    public void findPossibleSperrtypen_SomeEWSDFoundNoIMS() {
        Sperrklasse ersteSperrklasse = new Sperrklasse();
        ersteSperrklasse.setSperrklasse(60);
        ersteSperrklasse.setSperrklasseIms(null);
        ersteSperrklasse.setAbgehend(true);
        ersteSperrklasse.setMabez(true);

        Sperrklasse zweiteSperrklasse = new Sperrklasse();
        zweiteSperrklasse.setSperrklasse(2);
        zweiteSperrklasse.setSperrklasseIms(null);
        zweiteSperrklasse.setInternational(true);

        List<Sperrklasse> sperrklassen = Arrays.asList(ersteSperrklasse, zweiteSperrklasse);

        final Sperrklasse example = new Sperrklasse();
        final int amountOfSperrtypen = 11;
        final int expectedAmountOfSperrtypen = sperrklassen.size() * amountOfSperrtypen;
        when(dao.queryByExample(example, Sperrklasse.class)).thenReturn(sperrklassen);
        doReturn(createLongList(amountOfSperrtypen)).when(sut).getSperrklassenTypen(Mockito.any(Sperrklasse.class));
        List<Long> result = sut.findPossibleSperrtypen(example, HWSwitchType.EWSD);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), expectedAmountOfSperrtypen);
    }

    private List<Long> createLongList(int size) {
        List<Long> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(0L);
        }
        return result;
    }

    @DataProvider
    private Object[][] imsOrNspDP() {
        return new Object[][] {
                { HWSwitchType.IMS },
                { HWSwitchType.NSP }
        };
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "imsOrNspDP")
    public void findPossibleSperrtypen_SomeIMSNoEWSD(HWSwitchType hwSwitchType) {
        Sperrklasse ersteSperrklasse = new Sperrklasse();
        ersteSperrklasse.setSperrklasse(null);
        ersteSperrklasse.setSperrklasseIms(23);
        ersteSperrklasse.setAbgehend(true);

        Sperrklasse zweiteSperrklasse = new Sperrklasse();
        zweiteSperrklasse.setSperrklasse(null);
        zweiteSperrklasse.setSperrklasseIms(45);
        zweiteSperrklasse.setInternational(true);

        List<Sperrklasse> sperrklassen = Arrays.asList(ersteSperrklasse, zweiteSperrklasse);

        final Sperrklasse example = new Sperrklasse();
        final int amountOfSperrtypen = 11;
        final int expectedAmountOfSperrtypen = sperrklassen.size() * amountOfSperrtypen;
        when(dao.queryByExample(example, Sperrklasse.class)).thenReturn(sperrklassen);
        doReturn(createLongList(amountOfSperrtypen)).when(sut).getSperrklassenTypen(Mockito.any(Sperrklasse.class));

        List<Long> result = sut.findPossibleSperrtypen(example, hwSwitchType);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), expectedAmountOfSperrtypen);
    }

    /**
     * Testmethode fuer {@link CCRufnummernServiceImpl#findPossibleSperrtypen(Sperrklasse, HWSwitchType)}. Fuer den
     * {@link HWSwitchType#EWSD} sollen die noetigen Sperrklassentypen geladen werden. Die Datenbank liefert <ol>
     * <li>Eine Sperrklasse fuer alle Switchtypen</li> <li>Eine Sperrklasse nur IMS</li> <li>Eine Sperrklasse nicht
     * IMS</li> </ol> Das Ergebnis sollte eine Liste an Integer liefern. Diese Liste enhealt pro Sperrklasse zwoelf
     * Eintraege, die entweder 0 oder 1 oder null sind. Das haengt davon ab welcher Wert in der Datenbank dafuer
     * gespeichert wurde. Fuer diesen Fall sind es also zwei Sperrklassen, da Fall 2 hier keine verwertbare Sperrklasse
     * liefert.
     */
    @Test(groups = BaseTest.UNIT)
    public void findPossibleSperrtypen_ForEWSDSwitchSomeIMSAndSomeEWSD() {
        Sperrklasse ersteSperrklasse = new Sperrklasse();
        ersteSperrklasse.setSperrklasse(60);
        ersteSperrklasse.setSperrklasseIms(60);
        ersteSperrklasse.setAbgehend(true);
        ersteSperrklasse.setMabez(true);

        Sperrklasse zweiteSperrklasse = new Sperrklasse();
        zweiteSperrklasse.setSperrklasse(null);
        zweiteSperrklasse.setSperrklasseIms(30);
        zweiteSperrklasse.setInternational(true);

        Sperrklasse dritteSperrklasse = new Sperrklasse();
        dritteSperrklasse.setSperrklasse(12);
        dritteSperrklasse.setSperrklasseIms(null);
        dritteSperrklasse.setMobil(true);

        List<Sperrklasse> sperrklassen = Arrays.asList(ersteSperrklasse, zweiteSperrklasse, dritteSperrklasse);

        final Sperrklasse example = new Sperrklasse();
        final int amountOfSperrtypen = 11;
        when(dao.queryByExample(example, Sperrklasse.class)).thenReturn(sperrklassen);
        doReturn(createLongList(amountOfSperrtypen)).when(sut).getSperrklassenTypen(Mockito.any(Sperrklasse.class));

        List<Long> result = sut.findPossibleSperrtypen(example, HWSwitchType.EWSD);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), amountOfSperrtypen * 2);
    }

    /**
     * Testmethode fuer {@link CCRufnummernServiceImpl#findPossibleSperrtypen(Sperrklasse, HWSwitchType)}. Fuer den
     * {@link HWSwitchType#EWSD} sollen die noetigen Sperrklassentypen geladen werden. Die Datenbank liefert <ol>
     * <li>Eine Sperrklasse fuer alle Switchtypen</li> <li>Eine Sperrklasse nur IMS</li> <li>Eine Sperrklasse nicht
     * IMS</li> </ol> Das Ergebnis sollte eine Liste an Integer liefern. Diese Liste enhealt pro Sperrklasse zwoelf
     * Eintraege, die entweder 0 oder 1 oder null sind. Das haengt davon ab welcher Wert in der Datenbank dafuer
     * gespeichert wurde. Fuer diesen Fall sind es also zwei Sperrklassen, da Fall 2 hier keine verwertbare Sperrklasse
     * liefert.
     * @param hwSwitchType
     */
    @Test(groups = BaseTest.UNIT, dataProvider = "imsOrNspDP")
    public void findPossibleSperrtypen_SomeIMSAndSomeEWSD(HWSwitchType hwSwitchType) {
        Sperrklasse ersteSperrklasse = new Sperrklasse();
        ersteSperrklasse.setSperrklasse(60);
        ersteSperrklasse.setSperrklasseIms(60);
        ersteSperrklasse.setAbgehend(true);
        ersteSperrklasse.setMabez(true);

        Sperrklasse zweiteSperrklasse = new Sperrklasse();
        zweiteSperrklasse.setSperrklasse(null);
        zweiteSperrklasse.setSperrklasseIms(30);
        zweiteSperrklasse.setInternational(true);

        Sperrklasse dritteSperrklasse = new Sperrklasse();
        dritteSperrklasse.setSperrklasse(12);
        dritteSperrklasse.setSperrklasseIms(null);
        dritteSperrklasse.setMobil(true);

        List<Sperrklasse> sperrklassen = Arrays.asList(ersteSperrklasse, zweiteSperrklasse, dritteSperrklasse);

        final Sperrklasse example = new Sperrklasse();
        final int amountOfSperrtypen = 11;
        when(dao.queryByExample(example, Sperrklasse.class)).thenReturn(sperrklassen);
        doReturn(createLongList(amountOfSperrtypen)).when(sut).getSperrklassenTypen(Mockito.any(Sperrklasse.class));

        List<Long> result = sut.findPossibleSperrtypen(example, hwSwitchType);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), amountOfSperrtypen * 2);
    }

    @Test(groups = BaseTest.UNIT)
    public void getSperrklassenTypen_AllNull() {
        Sperrklasse sperrklasse = new Sperrklasse();
        List<Long> result = sut.getSperrklassenTypen(sperrklasse);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (Long longValue : result) {
            Assert.assertNull(longValue);
        }
    }

    @Test(groups = BaseTest.UNIT)
    public void getSperrklassenTypen_AllEnabled() {
        List<Long> expected = new ArrayList<>();
        for (SperrklassenTypEnum sperrklassenTyp : SperrklassenTypEnum.values()) {
            expected.add(sperrklassenTyp.getId());
        }
        Sperrklasse sperrklasse = new Sperrklasse();
        sperrklasse.setAbgehend(true);
        sperrklasse.setAuskunftsdienste(true);
        sperrklasse.setInnovativeDienste(true);
        sperrklasse.setInternational(true);
        sperrklasse.setMabez(true);
        sperrklasse.setMobil(true);
        sperrklasse.setNational(true);
        sperrklasse.setOffline(true);
        sperrklasse.setPrd(true);
        sperrklasse.setPremiumServicesInt(true);
        sperrklasse.setVpn(true);
        List<Long> result = sut.getSperrklassenTypen(sperrklasse);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (Long longValue : result) {
            Assert.assertNotNull(longValue);
        }
        Assert.assertEquals(result, expected);
    }

    @Test(groups = BaseTest.UNIT)
    public void getSperrklassenTypen_AllDisabled() {
        Sperrklasse sperrklasse = new Sperrklasse();
        sperrklasse.setAbgehend(false);
        sperrklasse.setAuskunftsdienste(false);
        sperrklasse.setInnovativeDienste(false);
        sperrklasse.setInternational(false);
        sperrklasse.setMabez(false);
        sperrklasse.setMobil(false);
        sperrklasse.setNational(false);
        sperrklasse.setOffline(false);
        sperrklasse.setPrd(false);
        sperrklasse.setPremiumServicesInt(false);
        sperrklasse.setVpn(false);
        List<Long> result = sut.getSperrklassenTypen(sperrklasse);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        for (Long longValue : result) {
            Assert.assertNull(longValue);
        }
    }

    @Test(groups = BaseTest.UNIT, dataProvider = "imsOrNspDP")
    public void testFindSperrklasseByHwSwitchType(HWSwitchType hwSwitchType) {
        Sperrklasse sk1 = new Sperrklasse();
        sk1.setId(1L);
        sk1.setSperrklasseIms(4);
        Sperrklasse sk2 = new Sperrklasse();
        sk2.setId(2L);
        when(dao.queryByExample(any(Sperrklasse.class), any(Class.class))).thenReturn(Arrays.asList(sk2, sk1));
        List<Sperrklasse> sperrklasses = sut.findSperrklasseByHwSwitchType(hwSwitchType);
        Assert.assertEquals(sperrklasses.size(), 1);
        Assert.assertEquals(sperrklasses.get(0), sk1);
    }
}
