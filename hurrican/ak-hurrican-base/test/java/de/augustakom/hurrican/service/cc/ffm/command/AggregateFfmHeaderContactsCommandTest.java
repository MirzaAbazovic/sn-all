/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.mnet.esb.cdm.resource.workforceservice.v1.ContactPerson;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderContactsCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private AnsprechpartnerService ansprechpartnerService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private CCKundenService ccKundenService;
    @Mock
    private AKUserService userService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderContactsCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderContactsCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).getAuftragDaten();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertFalse(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void testExecute() throws Exception {
        Ansprechpartner ansprechpartnerEndstelleB = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.ENDSTELLE_B)
                .withAddressBuilder(new CCAddressBuilder().setPersist(false))
                .setPersist(false).build();
        Ansprechpartner ansprechpartnerHotline = new AnsprechpartnerBuilder()
                .withType(Ansprechpartner.Typ.HOTLINE_SERVICE)
                .withAddressBuilder(new CCAddressBuilder().setPersist(false))
                .setPersist(false).build();

        CCAddressBuilder endstelleAddressBuilder = new CCAddressBuilder()
                .withRandomId()
                .withHausnummerZusatz("a").setPersist(false);
        doReturn(new EndstelleBuilder().withAddressBuilder(endstelleAddressBuilder).setPersist(false).build())
                .when(testling).getEndstelleB(anyBoolean());
        when(ccKundenService.findCCAddress(endstelleAddressBuilder.get().getId())).thenReturn(endstelleAddressBuilder.get());

        doReturn(new AuftragDatenBuilder().setPersist(false).build()).when(testling).getAuftragDaten();
        when(ansprechpartnerService.findAnsprechpartner(isNull(Ansprechpartner.Typ.class), anyLong())).thenReturn(
                Arrays.asList(ansprechpartnerEndstelleB, ansprechpartnerHotline));

        when(ccAuftragService.findAuftragTechnikByAuftragId(anyLong())).thenReturn(mock(AuftragTechnik.class));
        when(userService.findById(anyLong())).thenReturn(new AKUser(123L, "loginname", "Mustermann", "Max", "max@mustermann.net",
                "0176222222222", null, null, 123L, true, null));

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getContactPerson());
        Assert.assertEquals(workforceOrder.getContactPerson().size(), 5); //  3 + HAUPTPROJEKTVERANTWORTLICHE + PROJEKTLEITER

        for (ContactPerson contact : workforceOrder.getContactPerson()) {
            if (contact.getRole().equals(Ansprechpartner.Typ.HOTLINE_SERVICE.name())) {
                assertContactRoleMatches(contact, ansprechpartnerHotline.getAddress());
            }
            else if (contact.getRole().equals(AggregateFfmHeaderContactsCommand.ENDKUNDE_MONTAGELEISTUNG)) {
                assertContactRoleMatches(contact, ansprechpartnerEndstelleB.getAddress());
            }
            else if (contact.getRole().equals(AggregateFfmHeaderContactsCommand.KUNDE)) {
                assertContactRoleMatches(contact, endstelleAddressBuilder.get());
            }
        }

        Assert.assertTrue(workforceOrder.getContactPerson().stream()
                .filter(cp -> AggregateFfmHeaderContactsCommand.HAUPTPROJEKTVERANTWORTLICHE.equals(cp.getRole()))
                .findAny()
                .isPresent());
        Assert.assertTrue(workforceOrder.getContactPerson().stream()
                .filter(cp -> AggregateFfmHeaderContactsCommand.PROJEKTLEITER.equals(cp.getRole()))
                .findAny()
                .isPresent());
    }

    private void assertContactRoleMatches(ContactPerson contact, CCAddress address) {
        Assert.assertEquals(contact.getLastName(), address.getName());
        Assert.assertEquals(contact.getFirstName(), address.getVorname());
        Assert.assertNotNull(contact.getCommunication());
        Assert.assertEquals(contact.getCommunication().getEmail().get(0), address.getEmail());
        Assert.assertEquals(contact.getCommunication().getPhone().get(0), address.getTelefon());
        Assert.assertEquals(contact.getCommunication().getMobile().get(0), address.getHandy());
        Assert.assertEquals(contact.getCommunication().getFax().get(0), address.getFax());
    }

}
