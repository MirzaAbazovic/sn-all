/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2011 10:16:21
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
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Auftragsmanagement;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.validators.ansprechpartner.TelefonnummerValid.TelefonnummerValidator;

/**
 * TestNG Klasse fuer den {@link AnsprechpartnerAmAggregator}
 */
@Test(groups = BaseTest.UNIT)
public class AnsprechpartnerAmAggregatorTest extends BaseTest {

    private AnsprechpartnerAmAggregator cut;
    private AKUserService userServiceMock;
    private WitaCBVorgang cbVorgang;

    @BeforeMethod
    public void setUp() {
        cut = new AnsprechpartnerAmAggregator();
        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();

        userServiceMock = mock(AKUserService.class);
        cut.userService = userServiceMock;
    }

    public void aggregate() throws AKAuthenticationException {
        AKUser user = new AKUserBuilder().withPhone("089/000000-0").setPersist(false).build();
        when(userServiceMock.findById(any(Long.class))).thenReturn(user);

        Auftragsmanagement result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Auftragsmanagement Objekt nicht erstellt!");
        assertEquals(result.getNachname(), user.getName());
        assertEquals(result.getVorname(), user.getFirstName());
        assertEquals(result.getEmail(), user.getEmail());

        assertTrue(TelefonnummerValidator.isMatchingDtagPhoneFormat(result.getTelefonnummer()));
        assertEquals(result.getTelefonnummer(), "089 0000000");
    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateFailDueToPhoneNumber() throws AKAuthenticationException {
        AKUser user = new AKUserBuilder().withPhone("0 89 00 00 00").setPersist(false).build();
        when(userServiceMock.findById(any(Long.class))).thenReturn(user);
        cut.aggregate(cbVorgang);
    }

    public void checkEmailCanBeNull() throws AKAuthenticationException {
        AKUser user = new AKUserBuilder().setPersist(false).build();
        user.setEmail(null);
        when(userServiceMock.findById(any(Long.class))).thenReturn(user);

        Auftragsmanagement result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Auftragsmanagement Objekt nicht erstellt!");
        assertEquals(result.getEmail(), user.getEmail());
    }

}
