/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 14:25:54
 */
package de.mnet.wita.aggregator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Ansprechpartner;
import de.mnet.wita.message.auftrag.AnsprechpartnerRolle;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid.TelefonnummerValidator;

/**
 * TestNG Klasse fuer den {@link AnsprechpartnerTechnikAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class AnsprechpartnerTechnikAggregatorTest extends BaseTest {

    private AnsprechpartnerTechnikAggregator cut;
    private AKUserService userServiceMock;
    private CCAuftragService auftragServiceMock;
    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        cut = new AnsprechpartnerTechnikAggregator();
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();

        userServiceMock = mock(AKUserService.class);
        cut.userService = userServiceMock;

        auftragServiceMock = mock(CCAuftragService.class);
        cut.ccAuftragService = auftragServiceMock;
    }

    public void aggregate() throws AKAuthenticationException, FindException {
        AKUser user = new AKUserBuilder().withRandomId().withPhone("089/0000-000").setPersist(false).build();
        when(userServiceMock.findById(any(Long.class))).thenReturn(user);

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder().withProjectLeadUserId(user.getId()).setPersist(false).build();
        when(auftragServiceMock.findAuftragTechnikByAuftragIdTx(any(Long.class))).thenReturn(auftragTechnik);

        Ansprechpartner result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Ansprechpartner Objekt nicht erstellt!");
        assertEquals(result.getNachname(), user.getName());
        assertEquals(result.getVorname(), user.getFirstName());
        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.getRolle(), AnsprechpartnerRolle.TECHNIK);

        assertTrue(TelefonnummerValidator.isMatchingDtagPhoneFormat(result.getTelefonnummer()));
        assertEquals(result.getTelefonnummer(), "089 0000000");
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateFailDueToPhoneNumber() throws AKAuthenticationException {
        AKUser user = new AKUserBuilder().withPhone("0 89 00 00 00").setPersist(false).build();
        when(userServiceMock.findById(any(Long.class))).thenReturn(user);
        cut.aggregate(cbVorgang);
    }
}


