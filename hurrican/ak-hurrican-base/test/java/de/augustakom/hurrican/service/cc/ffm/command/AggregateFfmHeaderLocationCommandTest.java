/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderLocationCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private CCKundenService ccKundenService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private KundenService billingKundenService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderLocationCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderLocationCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).getEndstelleB(anyBoolean());

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());
    }


    @DataProvider(name = "executeDP")
    public Object[][] executeDP() {
        return new Object[][] {
                { true },
                { false },
        };
    }


    @Test(dataProvider = "executeDP")
    public void testExecute(boolean withEndstelleB) throws Exception {
        AddressModel toCheck = null;

        if (withEndstelleB) {
            CCAddressBuilder addressBuilder = new CCAddressBuilder()
                    .withRandomId()
                    .withHausnummerZusatz("a").setPersist(false);
            toCheck = addressBuilder.build();

            doReturn(new EndstelleBuilder().withAddressBuilder(addressBuilder).setPersist(false).build())
                .when(testling).getEndstelleB(anyBoolean());
            when(ccKundenService.findCCAddress(addressBuilder.get().getId())).thenReturn(addressBuilder.get());
        }
        else {
            doReturn(null).when(testling).getEndstelleB(anyBoolean());

            Adresse billingAddress = new AdresseBuilder().withNummer("22").withHausnummerZusatz("b").build();
            toCheck = billingAddress;

            Auftrag auftrag = new AuftragBuilder().setPersist(false).build();
            when(auftragService.findAuftragById(anyLong())).thenReturn(auftrag);
            when(billingKundenService.getAdresse4Kunde(auftrag.getKundeNo())).thenReturn(billingAddress);
        }

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getLocation());
        Assert.assertEquals(workforceOrder.getLocation().getCountry(), toCheck.getLandId());
        Assert.assertEquals(workforceOrder.getLocation().getCity(), toCheck.getCombinedOrtOrtsteil());
        Assert.assertEquals(workforceOrder.getLocation().getZipCode(), toCheck.getPlz());
        Assert.assertEquals(workforceOrder.getLocation().getStreet(), toCheck.getStrasse());
        Assert.assertEquals(workforceOrder.getLocation().getFloor(), toCheck.getStrasseAdd());
        Assert.assertEquals(workforceOrder.getLocation().getHouseNumber(),
                toCheck.getNummer() + " " + toCheck.getHausnummerZusatz());
    }

}
