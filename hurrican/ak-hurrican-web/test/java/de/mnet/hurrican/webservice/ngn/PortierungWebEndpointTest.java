package de.mnet.hurrican.webservice.ngn;

import static de.mnet.hurrican.ngn.portierungservice.PortierungStatusEnumWeb.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.mnet.hurrican.ngn.portierungservice.BillingOrderNumberWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungFault;
import de.mnet.hurrican.ngn.portierungservice.PortierungRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.PortierungStatusEnumWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationRequestWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationResponseWeb;
import de.mnet.hurrican.ngn.portierungservice.ValidationStatusEnumWeb;
import de.mnet.hurrican.webservice.ngn.endpoint.PortierungWebEndpoint;
import de.mnet.hurrican.webservice.ngn.service.PortierungService;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungHelperService;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungServiceImpl;
import de.mnet.hurrican.webservice.ngn.service.impl.PortierungskennungMigrationService;
import de.mnet.hurrican.webservice.ngn.service.impl.dn.DNLeistungMigration;

/**
 * Unit test for {@link PortierungWebEndpoint}
 *
 */
@Test(groups = BaseTest.UNIT)
public class PortierungWebEndpointTest {

    private PortierungWebEndpoint portierungWebEndpoint;

    @Mock
    PortierungHelperService portierungHelperService;

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        portierungWebEndpoint = createPortierungServiceEndpoint(() -> true);
    }

    @Test
    public void testValidatePortierungskennung_Success() throws Exception {
        Set<String> portierungsKennung = new HashSet<>(Collections.singletonList("D052"));
        when(portierungHelperService.hasActiveAuftraege(anyObject())).thenReturn(Boolean.TRUE);
        when(portierungHelperService.findPortierungsKennungBilling(anyLong())).thenReturn(portierungsKennung);
        when(portierungHelperService.findActiveWbciGeschaeftsfaelle(anyLong())).thenReturn(Collections.emptyList());
        when(portierungHelperService.findNonClosedWitaCbVorgaenge(anyObject())).thenReturn(Collections.emptyList());

        final ValidationRequestWeb validationRequest = mock(ValidationRequestWeb.class, Answers.RETURNS_DEEP_STUBS.get());
        final ValidationResponseWeb validationResponse = portierungWebEndpoint.validatePortierungskennung(validationRequest);

        assertNotNull(validationResponse);
        assertEquals(validationResponse.getValidationStatus().getValidationStatusEnum(), ValidationStatusEnumWeb.MIGRATION_POSSIBLE);
    }

    @Test(expectedExceptions = PortierungFault.class)
    public void testValidatePortierungskennung_FeatureFlag() throws Exception {
        portierungWebEndpoint = createPortierungServiceEndpoint(() -> false);
        portierungWebEndpoint.validatePortierungskennung(mock(ValidationRequestWeb.class));
        fail();
    }

    @Test(expectedExceptions = PortierungFault.class)
    public void testMigratePortierungskennung_FeatureFlag() throws Exception {
        portierungWebEndpoint = createPortierungServiceEndpoint(() -> false);
        portierungWebEndpoint.migratePortierungskennung(mock(PortierungRequestWeb.class));
        fail();
    }

    @Test
    public void testMigratePortierungskennung_Error() throws Exception {
        Set<String> portierungsKennung = new HashSet<>(Arrays.asList("D052","D043"));
        BillingOrderNumberWeb orderNumber = new BillingOrderNumberWeb();
        orderNumber.setBillingOrderNumber(123L);
        List<BillingOrderNumberWeb> billingOrderNumbers = new ArrayList<>(Collections.singletonList(orderNumber));
        when(portierungHelperService.findAuftragDaten(anyLong())).thenReturn(Collections.emptyList());
        when(portierungHelperService.hasInactiveAuftraege(anyObject())).thenReturn(Boolean.FALSE);
        when(portierungHelperService.findPortierungsKennungBilling(anyLong())).thenReturn(portierungsKennung);

        final PortierungRequestWeb portierungRequest = mock(PortierungRequestWeb.class);
        when(portierungRequest.getBillingOrderNumbers()).thenReturn(billingOrderNumbers);
        final PortierungResponseWeb portierungResponse = portierungWebEndpoint.migratePortierungskennung(portierungRequest);

        List<PortierungStatusEnumWeb> status = portierungResponse.getPortierungStatusEntries()
                .stream()
                .filter(portierungStatus -> portierungStatus.getPortierungStatus().getPortierungStatusEnum() == ERROR)
                .map(portierungStatus -> portierungStatus.getPortierungStatus().getPortierungStatusEnum())
                .collect(Collectors.toList());

       assertNotNull(portierungResponse);
       assertTrue(!status.isEmpty());
    }

    private PortierungWebEndpoint createPortierungServiceEndpoint(final BooleanSupplier featureFlagSupplier) {
        FeatureService featureService = Mockito.mock(FeatureService.class);
        PortierungskennungMigrationService migrationService = Mockito.mock(PortierungskennungMigrationService.class);
        final DNLeistungMigration dnLeistungMigration = Mockito.mock(DNLeistungMigration.class);
        PortierungService portierungService =
                new PortierungServiceImpl(featureService, portierungHelperService, migrationService, dnLeistungMigration);
        return new PortierungWebEndpoint(portierungService) {
            @Override
            protected boolean isFeatureFlagActive() {
                return featureFlagSupplier.getAsBoolean();
            }
        };

    }
}
