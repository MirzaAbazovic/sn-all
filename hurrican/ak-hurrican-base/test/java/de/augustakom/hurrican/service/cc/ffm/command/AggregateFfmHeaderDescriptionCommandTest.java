/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.matchers.VarargMatcher;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderDescriptionCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private ProduktService produktService;
    @Mock
    private BAService baService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderDescriptionCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderDescriptionCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).getBauauftrag();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());
    }


    @Test
    public void testExecute() throws Exception {
        doReturn(new AuftragDatenBuilder().withBemerkungen("auftragdaten-bemerkungen")
                .setPersist(false).build()).when(testling).getAuftragDaten();

        Produkt produkt = new ProduktBuilder().withAnschlussart("super-product by M-net").setPersist(false).build();
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(produkt);

        VerlaufAbteilung vaAM = new VerlaufAbteilungBuilder().withBemerkung("AM-Bemerkung").setPersist(false).build();
        VerlaufAbteilung vaDispo = new VerlaufAbteilungBuilder().withBemerkung("Dispo-Bemerkung").setPersist(false).build();
        VerlaufAbteilung vaNP = new VerlaufAbteilungBuilder().withBemerkung(" ").setPersist(false).build();
        when(baService.findVerlaufAbteilungen(anyLong(), argThat(new ReturnTrueVarargMatcher()))).thenReturn(Arrays.asList(vaAM, vaDispo, vaNP));

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getDescription());
        WorkforceOrder.Description description = workforceOrder.getDescription();
        Assert.assertEquals(description.getSummary(), produkt.getAnschlussart());
        Assert.assertTrue(description.getDetails().startsWith("auftragdaten-bemerkungen"));
        Assert.assertTrue(description.getDetails().contains("AM-Bemerkung"));
        Assert.assertTrue(description.getDetails().endsWith("Dispo-Bemerkung"));
        Assert.assertTrue(description.getDetails().contains("-----------"));
    }


    class ReturnTrueVarargMatcher extends ArgumentMatcher<Long[]> implements VarargMatcher {
        private static final long serialVersionUID = 538926113395737133L;

        @Override
        public boolean matches(Object varargArgument) {
            return true;
        }
    }
}
