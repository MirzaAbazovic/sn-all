/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragInternBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.matchers.VarargMatcher;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderDescription4InterneArbeitCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private ProduktService produktService;
    @Mock
    private BAService baService;
    @Mock
    private AuftragInternService auftragInternService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private AKUserService userService;
    @Mock
    private ReferenceService referenceService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderDescription4InterneArbeitCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderDescription4InterneArbeitCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws Exception {
        when(auftragInternService.findByAuftragId(anyLong())).thenReturn(
                new AuftragInternBuilder().withDescription("description interne Arbeit").withWorkingTypeRefId(9L).setPersist(false).build());

        doReturn(new AuftragDatenBuilder().withBemerkungen("auftragdaten-bemerkungen")
                .setPersist(false).build()).when(testling).getAuftragDaten();

        AuftragTechnik auftragTechnik = new AuftragTechnikBuilder().withProjectResponsibleUserId(99L).withProjectLeadUserId(100L).build();
        Produkt produkt = new ProduktBuilder().withAnschlussart("super-product by M-net").setPersist(false).build();
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(produkt);

        VerlaufAbteilung vaAM = new VerlaufAbteilungBuilder().withBemerkung("AM-Bemerkung").setPersist(false).build();
        VerlaufAbteilung vaDispo = new VerlaufAbteilungBuilder().withBemerkung("Dispo-Bemerkung").setPersist(false).build();
        VerlaufAbteilung vaNP = new VerlaufAbteilungBuilder().withBemerkung(" ").setPersist(false).build();
        when(baService.findVerlaufAbteilungen(anyLong(), argThat(new ReturnTrueVarargMatcher()))).thenReturn(Arrays.asList(vaAM, vaDispo, vaNP));

        when(ccAuftragService.findAuftragTechnikByAuftragId(anyLong())).thenReturn(auftragTechnik);
        when(userService.findById(99L)).thenReturn(new AKUser(99L, "responsible", "Projektleiter", "Der", null, null, null, null, null, true, null));
        when(userService.findById(100L)).thenReturn(new AKUser(100L, "lead", "Hauptprojektleiter", "Der", null, null, null, null, null, true, null));

        Reference workingTypeReference = new Reference();
        workingTypeReference.setStrValue("Schlafen");
        when(referenceService.findReference(9L)).thenReturn(workingTypeReference);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getDescription());
        WorkforceOrder.Description description = workforceOrder.getDescription();
        Assert.assertEquals(description.getSummary(), produkt.getAnschlussart());
        Assert.assertTrue(description.getDetails().startsWith("description interne Arbeit"));
        Assert.assertTrue(description.getDetails().contains("Arbeit: Schlafen"));
        Assert.assertTrue(description.getDetails().contains("Projektleiter: Der Projektleiter"));
        Assert.assertTrue(description.getDetails().contains("Hauptprojektverantwortlicher: Der Hauptprojektleiter"));
        Assert.assertTrue(description.getDetails().contains("auftragdaten-bemerkungen"));
        Assert.assertTrue(description.getDetails().contains("AM-Bemerkung"));
        Assert.assertTrue(description.getDetails().endsWith("Dispo-Bemerkung"));
        Assert.assertTrue(description.getDetails().contains("-----------"));
    }

    @Test
    public void testExecuteDefaultData() throws Exception {
        when(auftragInternService.findByAuftragId(anyLong())).thenReturn(
                new AuftragInternBuilder().setPersist(false).build());

        doReturn(new AuftragDatenBuilder().setPersist(false).build()).when(testling).getAuftragDaten();

        Produkt produkt = new ProduktBuilder().withAnschlussart("super-product by M-net").setPersist(false).build();
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(produkt);

        VerlaufAbteilung vaAM = new VerlaufAbteilungBuilder().setPersist(false).build();
        VerlaufAbteilung vaDispo = new VerlaufAbteilungBuilder().setPersist(false).build();
        VerlaufAbteilung vaNP = new VerlaufAbteilungBuilder().withBemerkung(" ").setPersist(false).build();
        when(baService.findVerlaufAbteilungen(anyLong(), argThat(new ReturnTrueVarargMatcher()))).thenReturn(Arrays.asList(vaAM, vaDispo, vaNP));

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getDescription());
        WorkforceOrder.Description description = workforceOrder.getDescription();
        Assert.assertEquals(description.getSummary(), produkt.getAnschlussart());
        Assert.assertTrue(description.getDetails().startsWith("Arbeit: Unbekannt"));
        Assert.assertTrue(description.getDetails().contains("Projektleiter: Unbekannt"));
        Assert.assertTrue(description.getDetails().contains("Hauptprojektverantwortlicher: Unbekannt"));
    }

    class ReturnTrueVarargMatcher extends ArgumentMatcher<Long[]> implements VarargMatcher {
        private static final long serialVersionUID = 4212220952443635679L;

        @Override
        public boolean matches(Object varargArgument) {
            return true;
        }
    }
}
