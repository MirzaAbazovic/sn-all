package de.mnet.hurrican.webservice.ngn;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.webservice.ngn.model.PortierungRequest;
import de.mnet.hurrican.webservice.ngn.model.PortierungResponse;
import de.mnet.hurrican.webservice.ngn.model.PortierungStatusEnum;
import de.mnet.hurrican.webservice.ngn.model.PortierungWarnings;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungHelperService;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungServiceImpl;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungskennungMigrationService;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;

/**
 * UT for {@link PortierungServiceImpl}
 */
@Test(groups = { BaseTest.UNIT })
public class PortierungServiceImplTest {

    @Mock
    private FeatureService featureService;
    @Mock
    private PortierungHelperService portierungHelperService;
    @Mock
    private PortierungskennungMigrationService portierungskennungMigrationService;

    @InjectMocks
    @Spy
    private PortierungServiceImpl cut;

    @BeforeMethod
    public void setUp() {
        cut = new PortierungServiceImpl();
        initMocks(this);
    }

    @Test
    public void testIsFeatureFlagActive() throws Exception {
        when(featureService.isFeatureOnline(Feature.FeatureName.NGN_PORTIERING_WEB_SERVICE)).thenReturn(true);
        assertTrue(cut.isFeatureFlagActive());
    }

    @Test
    public void testMigratePortierungskennungWithInactiveAuftraege() throws Exception {
        Long BillingOrderID = 222L;
        AuftragDaten adActive = new AuftragDatenBuilder().withAuftragId(123L).withAuftragNoOrig(222L).withStatusId(6000L).build();
        AuftragDaten adInActive = new AuftragDatenBuilder().withAuftragId(122L).withAuftragNoOrig(222L).withStatusId(9800L).build();
        PortierungRequest portierungRequest = new PortierungRequest(Collections.singletonList(BillingOrderID));

        List<AuftragDaten> listAuftragDaten = Arrays.asList(adActive,adInActive);
        when(portierungHelperService.findAuftragDaten(anyLong())).thenReturn(listAuftragDaten);
        when((portierungHelperService.hasInactiveAuftraege(anyList()))).thenReturn(true);
        when(portierungHelperService.findPortierungsKennungBilling(anyLong())).thenReturn(Sets.newHashSet("D235"));
        when(portierungHelperService.findCompleteWbciGeschaeftsfaelle(anyLong())).thenReturn(Collections.EMPTY_LIST);
        when(portierungskennungMigrationService.findAuftraegeForMigration(anyLong())).thenReturn(listAuftragDaten);
        doNothing().when(portierungskennungMigrationService).executeWitaGfMigration(anyList());
        PortierungResponse response = cut.migratePortierungskennung(portierungRequest);

        String expectedWarning = "- Inaktive technische Aufträge vorhanden: [123-222, 122-222]";
        getPortierungsWarningsAndEvaluate(response,expectedWarning);
    }

    @Test
    public void testMigratePortierungskennungWithWbciGeschaeftsfaelleWarnings() throws Exception {
        Long BillingOrderID = 222L;
        AuftragDaten adActive = new AuftragDatenBuilder().withAuftragId(123L).withAuftragNoOrig(222L).withStatusId(6000L).build();
        PortierungRequest portierungRequest = new PortierungRequest(Collections.singletonList(BillingOrderID));
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnBuilder()
                                                    .withAuftragId(123L)
                                                    .withStatus(WbciGeschaeftsfallStatus.ACTIVE)
                                                    .withVorabstimmungsId("DEU.MNET.X123456789")
                                                    .build();

        List<AuftragDaten> listAuftragDaten = Collections.singletonList(adActive);
        
        when(portierungHelperService.findAuftragDaten(anyLong())).thenReturn(listAuftragDaten);
        when((portierungHelperService.hasInactiveAuftraege(anyList()))).thenReturn(false);
        when(portierungHelperService.findPortierungsKennungBilling(anyLong())).thenReturn(Sets.newHashSet("D235"));
        when(portierungHelperService.findCompleteWbciGeschaeftsfaelle(anyLong())).thenReturn(Collections.singletonList(wbciGeschaeftsfall));
        when(portierungHelperService.findWitaCbVorgaenge(wbciGeschaeftsfall)).thenReturn(Collections.EMPTY_LIST);
        when(portierungHelperService.findPortierungsKennungVa(wbciGeschaeftsfall)).thenReturn("D052");

        when(portierungskennungMigrationService.findAuftraegeForMigration(anyLong())).thenReturn(listAuftragDaten);
        doNothing().when(portierungskennungMigrationService).executeWitaGfMigration(anyList());
        PortierungResponse response = cut.migratePortierungskennung(portierungRequest);
        String expectedWarning = "- zur abgeschlossenen VorabstimmungId: [DEU.MNET.X123456789], fehlt die WITA TAL Bestellung und PK weicht zur VA ab -> Neue Vorabstimmung nötig";
        getPortierungsWarningsAndEvaluate(response,expectedWarning);
    }

    private void getPortierungsWarningsAndEvaluate(PortierungResponse response, String expectedWarning) {
        Set<Long> orderNumbers = response.getResponses().keySet();
        assertThat(orderNumbers.size()).isEqualTo(1);
        for (Long orderNumber : orderNumbers) {
            assertTrue(response.getResponses().get(orderNumber).getPortierungStatusEnum().equals(PortierungStatusEnum.SUCCESSFUL));
            PortierungWarnings portierungWarnings = response.getResponses().get(orderNumber).getPortierungWarnings().get();
            assertTrue(!portierungWarnings.isEmpty());
            assertEquals(portierungWarnings.getWarnings().get(0).getMessage().trim(), expectedWarning);
        }
    }
}
