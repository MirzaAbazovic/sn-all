/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2016
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.hamcrest.core.StringContains;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class CheckGFastModemCommandTest {
    private static final long G2_PHYSIC_TYP_ID = 810L;
    private static final long G2_MODEM_ID = 666L;

    @Mock
    private EndgeraeteService endgeraeteService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private EndstellenService endstellenService;

    private CheckGFastModemCommand testling;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        testling = new CheckGFastModemCommand() {

            private Produkt produkt = new Produkt();

            @Override
            protected Long getAuftragId() {
                return 1L;
            }

            @Override
            public Produkt getProdukt() {
                return produkt;
            }

            @Override
            public <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
                if (type.equals(EndgeraeteService.class))
                    return (T) endgeraeteService;
                if (type.equals(RangierungsService.class))
                    return (T) rangierungsService;
                if (type.equals(EndstellenService.class))
                    return (T) endstellenService;
                return null;
            }
        };
        setProduktId(1L);
        Endstelle endstelle = new Endstelle();
        endstelle.setRangierId(1L);
        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelle);
    }

    @Test
    public void execute_PhysikIsGFastProductIsFTTXNoModem_ReturnsNoWarning() throws Exception {
        setProduktId(Produkt.PROD_ID_FTTX_DSL_FON);
        setEndgeraeteServiceReturns();
        setRangierungsServiceReturns(createRangierung(G2_PHYSIC_TYP_ID));

        testling.execute();

        assertEquals(testling.getWarnings(), null);
    }

    @Test
    public void execute_PhysikIsGFastProductIsNotFTTXNoModem_ReturnsWarning() throws Exception {
        setEndgeraeteServiceReturns();
        setRangierungsServiceReturns(createRangierung(G2_PHYSIC_TYP_ID));

        testling.execute();
        String result = testling.getWarnings().getWarningsAsText();

        assertThat(result, StringContains.containsString("kein Modem"));
    }

    @Test
    public void execute_PhysikIsGFastProductIsNotFTTXModemSelected_ReturnsNoWarning() throws Exception {
        setEndgeraeteServiceReturns(createGFastModem());
        setRangierungsServiceReturns(createRangierung(G2_PHYSIC_TYP_ID));

        testling.execute();

        assertEquals(testling.getWarnings(), null);
    }

    @Test
    public void execute_PhysikIsNotGFastNoModem_ReturnsNoWarning() throws Exception {
        setEndgeraeteServiceReturns();
        setRangierungsServiceReturns(createRangierung(1L));

        testling.execute();

        assertEquals(testling.getWarnings(), null);
    }

    @Test
    public void execute_PhysikIsNotGFastModemSelected_ReturnsNoWarning() throws Exception {
        setEndgeraeteServiceReturns(createGFastModem());
        setRangierungsServiceReturns(createRangierung(1L));

        testling.execute();

        assertEquals(testling.getWarnings(), null);
    }

    private void setEndgeraeteServiceReturns(EG2AuftragView... e2as) throws Exception {
        when(endgeraeteService.findEG2AuftragViews(anyLong())).thenReturn(Arrays.asList(e2as));
    }

    private void setRangierungsServiceReturns(Rangierung rangierung) throws FindException {
        when(rangierungsService.findRangierung(anyLong())).thenReturn(rangierung);
    }

    private EG2AuftragView createGFastModem() {
        EG2AuftragView e2a = new EG2AuftragView();
        e2a.setEgId(G2_MODEM_ID);
        return e2a;
    }

    private Rangierung createRangierung(long physikTypID) {
        Rangierung rangierung = new Rangierung();
        rangierung.setPhysikTypId(physikTypID);
        return rangierung;
    }

    private void setProduktId(long id) {
        testling.getProdukt().setId(id);
    }

}