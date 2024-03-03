/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.dao.cc.EndstelleViewDAO;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ProduktService;

@Test(groups = BaseTest.UNIT)
public class EndstellenServiceImplTest extends BaseTest {

    @InjectMocks
    private EndstellenServiceImpl testling;

    @Mock
    private ProduktService produktService;
    @Mock
    private EndstelleViewDAO endstelleViewDAO;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @DataProvider
    public Object[][] findEndstellenDP() {
        return new Object[][] {
                { createProdukt(Produkt.ES_TYP_KEINE_ENDSTELLEN), 0 },
                { createProdukt(Produkt.ES_TYP_NUR_B), 1 },
                { createProdukt(Produkt.ES_TYP_A_UND_B), 2 }
        };
    }

    @Test(dataProvider = "findEndstellenDP")
    public void testFindEndstellen4AuftragBasedOnProductConfig(Produkt produkt, int expectedResultCount) throws ServiceNotFoundException, FindException {
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(produkt);

        when(endstelleViewDAO.findEndstellen4Auftrag(anyLong()))
                .thenReturn(
                        Arrays.asList(
                                createEndstelle(Endstelle.ENDSTELLEN_TYP_B),
                                createEndstelle(Endstelle.ENDSTELLEN_TYP_A)
                        )
                );

        List<Endstelle> result = testling.findEndstellen4AuftragBasedOnProductConfig(1L);
        assertEquals(result.size(), expectedResultCount);
    }

    private Endstelle createEndstelle(String endstelleTyp) {
        return new EndstelleBuilder()
                .withEndstelleTyp(endstelleTyp)
                .setPersist(false)
                .build();
    }

    private Produkt createProdukt(Integer endstellenTyp) {
        return new ProduktBuilder()
                .withEndstellenTyp(endstellenTyp)
                .setPersist(false)
                .build();
    }

}
