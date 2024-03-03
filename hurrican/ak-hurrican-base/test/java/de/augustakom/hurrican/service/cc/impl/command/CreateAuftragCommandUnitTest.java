/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.14 15:15
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 *
 */
public class CreateAuftragCommandUnitTest {

    @Mock
    CCAuftragDAO auftragDAO;
    @Mock
    AuftragTechnikDAO auftragTechnikDAO;
    @Mock
    AuftragDatenDAO auftragDatenDAO;
    @Mock
    CCLeistungsService ccLeistungsService;
    @Mock
    BillingAuftragService billingAuftragService;

    @InjectMocks
    CreateAuftragCommand cut;

    @BeforeMethod
    void setUp() {
        cut = new CreateAuftragCommand();
        initMocks(this);
    }

    @DataProvider
    public Object[][] voipLeistungProvider() {
        return new Object[][] {
                { Boolean.TRUE },
                { Boolean.FALSE }
        };
    }

    @Test(dataProvider = "voipLeistungProvider")
    public void testCreateAuftrag(Boolean hasVoip) throws Exception {
        final AuftragDaten auftragDaten = new AuftragDatenBuilder().build();
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        final HWSwitch hwSwitch = new HWSwitchBuilder().withName("MUC06").build();
        ReflectionTestUtils.setField(cut, "auftragDaten", auftragDaten);
        ReflectionTestUtils.setField(cut, "auftragTechnik", auftragTechnik);
        ReflectionTestUtils.setField(cut, "ccProdukt", new ProduktBuilder().withHwSwitch(hwSwitch).withSmsVersand(Boolean.TRUE).build());
        Mockito.when(ccLeistungsService.hasVoipLeistungFromNowOn(Mockito.anyList())).thenReturn(hasVoip);

        cut.createAuftrag();
        assertThat(auftragDaten.isAutoSmsAndMailVersand(), equalTo(true));
        assertThat(auftragTechnik.getHwSwitch(), equalTo(hasVoip ? hwSwitch : null));
    }
}
