package de.bitconex.adlatus.wholebuy.provision.resolver;

import de.bitconex.adlatus.wholebuy.provision.dto.AgreementDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.ProductDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.TelecomInterfaceDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.agreement.AgreementClientService;
import de.bitconex.adlatus.wholebuy.provision.service.agreement.AgreementResolver;
import de.bitconex.tmf.agreement.model.Characteristic;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgreementResolverTest {

    public static final String INTERFACE_VERSION = "15";
    public static final String INTERFACE_TYPE = "WITA";
    public static final String CUSTOMER_NUMBER = "12345";
    public static final String PRODUCT = "l2bsa 250";
    public static final String ID = "id";
    @Mock
    private AgreementClientService agreementClientService;

    private AgreementResolver agreementResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agreementResolver = new AgreementResolver(agreementClientService);
    }

    @Test
    void testResolveContract() {
        ResourceOrder mockOrder = new ResourceOrder();

        when(agreementClientService.getAllAgreements()).thenReturn(getAgreements());


        AgreementDTO result = agreementResolver.resolveContract(mockOrder);

        assertThat(result.getAgreementNumber()).isEqualTo(ID);
        assertThat(result.getCustomerNumber()).isEqualTo(CUSTOMER_NUMBER);

    }

    @Test
    void testResolveProduct() {
        ResourceOrder mockOrder = mock(ResourceOrder.class);
        when(agreementClientService.getAllAgreements()).thenReturn(getAgreements());

        ProductDTO result = agreementResolver.resolveProduct(mockOrder);

        assertThat(result.getProductId()).isEqualTo(PRODUCT);
    }

    @Test
    void testResolveInterface() {
        ResourceOrder mockOrder = mock(ResourceOrder.class);
        when(agreementClientService.getAllAgreements()).thenReturn(getAgreements());

        TelecomInterfaceDTO result = agreementResolver.resolveInterface(mockOrder);

        assertThat(result.getType().getName()).isEqualTo(INTERFACE_TYPE);
        assertThat(result.getType().getVersion()).isEqualTo(INTERFACE_VERSION);
        assertThat(result.getMajorVersion()).isEqualTo(INTERFACE_VERSION);
        assertThat(result.getMinorVersion()).isEqualTo("10"); // this is currently hard coded
    }

    @Test
    void testResolveProductType() {
        //Arrange
        String ID = "agreementId";

        Characteristic characteristic = Characteristic.builder()
                .name("type")
                .value("XDSL")
                .build();

        de.bitconex.tmf.agreement.model.Agreement agreement = de.bitconex.tmf.agreement.model.Agreement.builder()
                .id(ID)
                .characteristic(List.of(characteristic))
                .agreementPeriod(de.bitconex.tmf.agreement.model.TimePeriod.builder()
                        .endDateTime(OffsetDateTime.now().plusDays(2))
                        .build())
                .build();

        when(agreementClientService.getAllAgreements()).thenReturn(List.of(agreement));

        //call
        String result = agreementResolver.resolveProductType(new ResourceOrder());

        //assert
        assertThat(result).isEqualTo("ProduktXDSLType");
    }

    @Test
    void testResolveOrderType() {
        //Arrange
        String ID = "agreementId1234";

        Characteristic characteristic = Characteristic.builder()
                .name("type")
                .value("XDSL")
                .build();

        de.bitconex.tmf.agreement.model.Agreement agreement = de.bitconex.tmf.agreement.model.Agreement.builder()
                .id(ID)
                .characteristic(List.of(characteristic))
                .agreementPeriod(de.bitconex.tmf.agreement.model.TimePeriod.builder()
                        .endDateTime(OffsetDateTime.now().plusDays(2))
                        .build())
                .build();

        ResourceOrderItem resourceOrderItem = ResourceOrderItem.builder()
                .action("add")
                .build();

        ResourceOrder resourceOrder = ResourceOrder.builder()
                .orderItem(List.of(resourceOrderItem))
                .build();

        when(agreementClientService.getAllAgreements()).thenReturn(List.of(agreement));

        //call
        String result = agreementResolver.resolveOrderType(resourceOrder);

        //assert
        assertThat(result).isEqualTo("XDSLBereitstellungType");
    }

    @Test
    void testResolveBusinessCase() {
        //Arrange
        ResourceOrderItem resourceOrderItem = ResourceOrderItem.builder()
                .action("add")
                .build();

        ResourceOrder resourceOrder = ResourceOrder.builder()
                .orderItem(List.of(resourceOrderItem))
                .build();

        //call
        String result = agreementResolver.resolveBusinessCase(resourceOrder);

        //assert
        assertThat(result).isEqualTo("BereitstellungType");
    }

    private List<de.bitconex.tmf.agreement.model.Agreement> getAgreements() {
        return List.of(de.bitconex.tmf.agreement.model.Agreement.builder()
                        .id(ID)
                        .agreementPeriod(de.bitconex.tmf.agreement.model.TimePeriod.builder()
                                .endDateTime(OffsetDateTime.now().plusDays(2))
                                .build())
                        .characteristic(List.of(de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("customerNumber")
                                        .value(CUSTOMER_NUMBER)
                                        .build(),
                                de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("interfaceType")
                                        .value(INTERFACE_TYPE)
                                        .build(),
                                de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("interfaceVersion")
                                        .value(INTERFACE_VERSION)
                                        .build()))
                        .agreementItem(List.of(de.bitconex.tmf.agreement.model.AgreementItem.builder()
                                .productOffering(List.of(de.bitconex.tmf.agreement.model.ProductOfferingRef.builder()
                                        .name(PRODUCT)
                                        .build()))
                                .build()))
                        .build(),
                de.bitconex.tmf.agreement.model.Agreement.builder()
                        .id("IGNORED")
                        .agreementPeriod(de.bitconex.tmf.agreement.model.TimePeriod.builder()
                                .endDateTime(OffsetDateTime.now().minusDays(1))
                                .build())
                        .characteristic(List.of(de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("customerNumber")
                                        .value("customerNumber")
                                        .build(),
                                de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("interfaceType")
                                        .value("interfaceType")
                                        .build(),
                                de.bitconex.tmf.agreement.model.Characteristic.builder()
                                        .name("interfaceVersion")
                                        .value("interfaceVersion")
                                        .build()))
                        .agreementItem(List.of(de.bitconex.tmf.agreement.model.AgreementItem.builder()
                                .productOffering(List.of(de.bitconex.tmf.agreement.model.ProductOfferingRef.builder()
                                        .name("productOffering")
                                        .build()))
                                .build()))
                        .build());
    }
}