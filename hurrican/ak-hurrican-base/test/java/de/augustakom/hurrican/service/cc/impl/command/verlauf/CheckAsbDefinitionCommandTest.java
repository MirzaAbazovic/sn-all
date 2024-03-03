/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 10:09:33
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * TestNG Klasse fuer {@link CheckAsbDefinitionCommand}.
 */
@Test(groups = BaseTest.UNIT)
public class CheckAsbDefinitionCommandTest extends BaseTest {

    private CheckAsbDefinitionCommand cut;
    private HVTService hvtServiceMock;
    private HVTStandortBuilder hvtStandortBuilder;

    @BeforeMethod
    public void setUp() {
        cut = new CheckAsbDefinitionCommand();

        hvtServiceMock = mock(HVTService.class);
        cut.setHvtService(hvtServiceMock);

        hvtStandortBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withAsb(Integer.valueOf(1))
                .withHvtGruppeBuilder(new HVTGruppeBuilder().setPersist(false))
                .setPersist(false);
    }

    public void testCheckAsbKennung() throws FindException {
        Endstelle esB = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();
        cut.setEndstelleB(esB);

        when(hvtServiceMock.findHVTStandort(esB.getHvtIdStandort())).thenReturn(hvtStandortBuilder.get());

        cut.checkAsbKennung();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckAsbKennungExpectErrorBecauseNoHvt() throws FindException {
        Endstelle esB = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();
        esB.setHvtIdStandort(null);
        cut.setEndstelleB(esB);

        when(hvtServiceMock.findHVTStandort(esB.getHvtIdStandort())).thenReturn(hvtStandortBuilder.get());

        cut.checkAsbKennung();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckAsbKennungExpectErrorBecauseNoHvtStandort() throws FindException {
        Endstelle esB = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();
        cut.setEndstelleB(esB);

        when(hvtServiceMock.findHVTStandort(esB.getHvtIdStandort())).thenReturn(null);

        cut.checkAsbKennung();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckAsbKennungExpectErrorBecauseNoAsb() throws FindException {
        Endstelle esB = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder().setPersist(false))
                .withHvtStandortBuilder(hvtStandortBuilder)
                .setPersist(false).build();
        cut.setEndstelleB(esB);

        HVTStandort hvtStandort = hvtStandortBuilder.get();
        hvtStandort.setAsb(null);
        when(hvtServiceMock.findHVTStandort(esB.getHvtIdStandort())).thenReturn(hvtStandort);

        cut.checkAsbKennung();
    }

}


