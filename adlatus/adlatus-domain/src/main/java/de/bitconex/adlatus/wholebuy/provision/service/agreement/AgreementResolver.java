package de.bitconex.adlatus.wholebuy.provision.service.agreement;

import de.bitconex.adlatus.wholebuy.provision.dto.constants.InterfaceTransformatorConstants;
import de.bitconex.adlatus.wholebuy.provision.dto.enums.TelecomInterfaceType;
import de.bitconex.adlatus.wholebuy.provision.dto.AgreementDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.TelecomInterfaceDTO;
import de.bitconex.adlatus.wholebuy.provision.dto.ProductDTO;
import de.bitconex.adlatus.wholebuy.provision.adapter.agreement.AgreementClientService;
import de.bitconex.tmf.rom.model.ResourceOrder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.OffsetDateTime;

@Component
public class AgreementResolver {
    private final AgreementClientService agreementClientService;
    private static final String PRODUCT_TEMPLATE = "Produkt{0}Type";
    private static final String ORDER_TEMPLATE = "{0}{1}Type";

    public AgreementResolver(AgreementClientService agreementClientService) {
        this.agreementClientService = agreementClientService;
    }

    public AgreementDTO resolveContract(ResourceOrder order) {
        var agreement = resolveAgreement(order);

        return AgreementDTO.builder()
                .agreementNumber(agreement.getId())
                .customerNumber(agreement.getCharacteristic().stream().filter(c -> InterfaceTransformatorConstants.CUSTOMER_NUMBER.getValue().equals(c.getName())).findFirst().get().getValue().toString())
                .build();
    }

    public String resolveProductType(ResourceOrder order) {
        var agreement = resolveAgreement(order);
        // todo: this fails when there is no product type - NoSuchElementException - please explicitly throw it
        var productType = agreement.getCharacteristic().stream().filter(c -> InterfaceTransformatorConstants.TYPE.getValue().equals(c.getName())).findFirst().get();

        return MessageFormat.format(PRODUCT_TEMPLATE, productType.getValue().toString());
    }


    public String resolveOrderType(ResourceOrder order) {
        var agreement = resolveAgreement(order);
        var orderType = agreement.getCharacteristic().stream().filter(c -> InterfaceTransformatorConstants.TYPE.getValue().equals(c.getName())).findFirst().get();
        var orderCase = orderCase(order);

        return MessageFormat.format(ORDER_TEMPLATE, orderType.getValue().toString(), orderCase);
    }

    public String resolveBusinessCase(ResourceOrder order) {
        var orderCase = orderCase(order);

        return MessageFormat.format(ORDER_TEMPLATE, "", orderCase);
    }

    private String orderCase(ResourceOrder order) {
        //TODO: Implement concrete order case
        var actionType = order.getOrderItem().get(0).getAction();
        if (actionType.equals("add")) {
            return InterfaceTransformatorConstants.PROVISION.getValue();
        } else {
            return InterfaceTransformatorConstants.PROVISION.getValue();
        }
    }

    public ProductDTO resolveProduct(ResourceOrder order) {
        var agreement = resolveAgreement(order);
        var products = agreement.getAgreementItem();
        // TODO current resolving is just finding first product, should be more sophisticated
        var product = products.stream().findFirst().get();
        var productId = product.getProductOffering().stream().findFirst().get().getName();

        return ProductDTO.builder()
                .productId(productId)
                .build();
    }

    public TelecomInterfaceDTO resolveInterface(ResourceOrder order) {
        var agreement = resolveAgreement(order);
        var interfaceType = agreement.getCharacteristic().stream().filter(c -> InterfaceTransformatorConstants.INTERFACE_TYPE.getValue().equals(c.getName())).findFirst().get();
        var interfaceVersion = agreement.getCharacteristic().stream().filter(c -> InterfaceTransformatorConstants.INTERFACE_VERSION.getValue().equals(c.getName())).findFirst().get();

        var type = TelecomInterfaceType.fromNameAndVersion(interfaceType.getValue().toString(), interfaceVersion.getValue().toString());

        return TelecomInterfaceDTO.builder()
                .type(type)
                .majorVersion(type.getVersion())
                .minorVersion("10") // TODO should be defined somewhere (in agreement?)
                .build();
    }

    private de.bitconex.tmf.agreement.model.Agreement resolveAgreement(ResourceOrder order) {
        var activeAgreements = agreementClientService.getAllAgreements().stream()
                .filter(a -> a.getAgreementPeriod().getEndDateTime().isAfter(OffsetDateTime.now()))
                .toList();

        // TODO current resolving is just finding first active agreement, should be more sophisticated
        return activeAgreements.stream().findFirst().get();
    }

}
