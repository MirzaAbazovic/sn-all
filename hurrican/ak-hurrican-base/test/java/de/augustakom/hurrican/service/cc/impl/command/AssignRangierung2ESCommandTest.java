/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2013 09:28:26
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Unit-Test fuer {@link AssignRangierung2ESCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class AssignRangierung2ESCommandTest extends BaseTest {

    @Spy
    private AssignRangierung2ESCommand sut = new AssignRangierung2ESCommand();

    @Mock
    private PhysikService physikServiceMock;

    @Mock
    private RangierungsService rangierungsServiceMock;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        sut.setPhysikService(physikServiceMock);
        sut.setRangierungsService(rangierungsServiceMock);
    }

    @DataProvider(name = "findRangierungen4CombiProduktOnlyDP")
    public Object[][] findRangierungen4CombiProduktOnlyDP() {
        Rangierung rangierung = new Rangierung();
        rangierung.setId(Long.valueOf(1L));
        Rangierung rangierungAdd = new Rangierung();
        rangierungAdd.setId(Long.valueOf(2L));

        return new Object[][] {
                // @formatter:off
                // Rangierung, RangierungAdd, exceptionExpected
                { null,       null,          true },
                { rangierung, null,          true },
                { null,       rangierungAdd, true },
                { rangierung, rangierungAdd, false },
                // @formatter:on
        };
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "findRangierungen4CombiProduktOnlyDP")
    public void testFindRangierungen4CombiProduktOnly(Rangierung rangierung, Rangierung rangierungAdd,
            boolean exceptionExpected) {
        Pair<Rangierung, Rangierungsmatrix>[] mockedResult = new Pair[0];
        Pair<Rangierung, Rangierung> result;
        if (rangierung != null && rangierungAdd != null) {
            mockedResult = (Pair<Rangierung, Rangierungsmatrix>[]) Array.newInstance(Pair.class, 2);
            mockedResult[0] = new Pair<>(rangierung, null);
            mockedResult[1] = new Pair<>(rangierungAdd, null);
        }
        else if (rangierung != null) {
            mockedResult = (Pair<Rangierung, Rangierungsmatrix>[]) Array.newInstance(Pair.class, 1);
            mockedResult[0] = new Pair<>(rangierung, null);
        }

        try {
            doReturn(mockedResult).when(sut).findRangierungen4CombiProdukt(null);
            result = sut.findRangierungen4CombiProduktOnly();
        }
        catch (FindException e) {
            if (!exceptionExpected) {
                fail("No Exception Expected!");
            }
            return;
        }
        if (exceptionExpected) {
            fail("Exception Expected!");
        }

        assertEquals(result.getFirst(), rangierung);
        assertEquals(result.getSecond(), rangierungAdd);
    }

    @DataProvider(name = "findRangierungen4ESOrCombiProduktDP")
    public Object[][] findRangierungen4ESOrCombiProduktDP() {
        Rangierung r1 = new Rangierung();
        r1.setId(Long.valueOf(1L));
        Rangierung rA2 = new Rangierung();
        rA2.setId(Long.valueOf(2L));
        Rangierung r3 = new Rangierung();
        r3.setId(Long.valueOf(3L));
        Rangierungsmatrix rM1 = new Rangierungsmatrix();
        rM1.setPriority(1);
        Rangierungsmatrix rM2 = new Rangierungsmatrix();
        rM2.setPriority(2);
        Rangierungsmatrix rMNull = new Rangierungsmatrix();

        return new Object[][] {
                // @formatter:off
                // combiRangierungen, simpleRangierung, expectedResult
                { new Pair[0],            null,               null },
                { combi(r1, rMNull, rA2), null,               res(r1, rA2) },
                { new Pair[0],            simple(r3, rMNull), res(r3, null) },
                { combi(r1, rMNull, rA2), simple(r3, rMNull), res(r3, null) },
                { combi(r1, rM1, rA2),    simple(r3, rM1),    res(r3, null) },
                { combi(r1, rM1, rA2),    simple(r3, rM2),    res(r3, null) },
                { combi(r1, rM2, rA2),    simple(r3, rM1),    res(r1, rA2) },
                { combi(r1, rM1, rA2),    simple(r3, rMNull), res(r1, rA2) },
                { combi(r1, rMNull, rA2), simple(r3, rM1),    res(r3, null) },
                // @formatter:on
        };
    }

    private Pair<Rangierung, Rangierungsmatrix>[] combi(Rangierung rang1, Rangierungsmatrix rangMatrix1,
            Rangierung rang2) {
        @SuppressWarnings("unchecked")
        Pair<Rangierung, Rangierungsmatrix>[] combi = (Pair<Rangierung, Rangierungsmatrix>[]) Array.newInstance(
                Pair.class, 2);
        combi[0] = new Pair<>(rang1, rangMatrix1);
        combi[1] = new Pair<>(rang2, null);
        return combi;
    }

    private Pair<Rangierung, Rangierungsmatrix> simple(Rangierung rang1, Rangierungsmatrix rangMatrix1) {
        return new Pair<>(rang1, rangMatrix1);
    }

    private Pair<Rangierung, Rangierung> res(Rangierung rang1, Rangierung rang2) {
        return new Pair<>(rang1, rang2);
    }

    @Test(dataProvider = "findRangierungen4ESOrCombiProduktDP")
    public void testFindRangierungen4ESOrCombiProdukt(Pair<Rangierung, Rangierungsmatrix>[] combiRangierungen,
            Pair<Rangierung, Rangierungsmatrix> simpleRangierung, Pair<Rangierung, Rangierung> expectedResult)
            throws FindException {
        doReturn(combiRangierungen).when(sut).findRangierungen4CombiProdukt(
                any(Boolean.class));
        doReturn(simpleRangierung).when(sut).findRangierung4ES(any(Boolean.class));

        Pair<Rangierung, Rangierung> result = sut.findRangierungen4ESOrCombiProdukt();
        if (expectedResult == null) {
            assertTrue(result == null);
            return;
        }
        assertEquals(result.getFirst(), expectedResult.getFirst());
        assertEquals(result.getSecond(), expectedResult.getSecond());
    }

    @DataProvider(name = "testFindRangierung4MatrixDP")
    public Object[][] testFindRangierung4MatrixDP() {
        Rangierungsmatrix rangMatrix = new Rangierungsmatrix();
        Rangierung expectedRangierung = new Rangierung();

        return new Object[][] {
                // @formatter:off
                // rangMatrix, p2pt, filterP2PT, expectedRangierung
                { null,       null,             null,   null },
                { rangMatrix, null,             null,   null },
                { rangMatrix, p2pt(null, null), null,   null },
                { rangMatrix, p2pt(1L, null),   null,   expectedRangierung },
                { rangMatrix, p2pt(1L, null),   false,  null },
                { rangMatrix, p2pt(1L, 2L),     true,   null },
                { rangMatrix, p2pt(1L, null),   true,   expectedRangierung },
                { rangMatrix, p2pt(1L, 2L),     false,  expectedRangierung },
                // @formatter:on
        };
    }

    private Produkt2PhysikTyp p2pt(Long physikTypId, Long physikTypAddId) {
        Produkt2PhysikTyp p2pt = new Produkt2PhysikTyp();
        p2pt.setPhysikTypId(physikTypId);
        p2pt.setPhysikTypAdditionalId(physikTypAddId);
        return p2pt;
    }

    @Test(dataProvider = "testFindRangierung4MatrixDP")
    public void testFindRangierung4Matrix(Rangierungsmatrix rangMatrix, Produkt2PhysikTyp p2pt, Boolean simpleOnly,
            Rangierung expectedRangierung) throws FindException {
        List<Rangierungsmatrix> matrizen = (rangMatrix != null) ? Collections.singletonList(rangMatrix) : null;
        RangierungsmatrixQuery matrixQuery = new RangierungsmatrixQuery();
        when(rangierungsServiceMock.findMatrix(matrixQuery)).thenReturn(matrizen);
        when(physikServiceMock.findP2PT(any(Long.class))).thenReturn(p2pt);
        //@formatter:off
        when(rangierungsServiceMock.findFreieRangierung(any(RangierungQuery.class), any(Endstelle.class),
                        eq(false), any(Long.class), any(Integer.class),
                        any(Uebertragungsverfahren.class), any(Bandwidth.class))).thenReturn(expectedRangierung);
        //@formatter:on
        Pair<Rangierung, Rangierungsmatrix> result = sut.findRangierung4Matrix(matrixQuery, null, false, null, false,
                simpleOnly);
        if (expectedRangierung == null) {
            assertTrue(result == null);
            return;
        }
        assertEquals(expectedRangierung, result.getFirst());
    }


    @Test
    public void testGetHvtIdStandort() throws FindException {
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();
        endstelle.setHvtIdStandort(99L);

        sut.setEndstelle(endstelle);

        assertEquals(sut.getHvtIdStandort(), endstelle.getHvtIdStandort());
    }

}
