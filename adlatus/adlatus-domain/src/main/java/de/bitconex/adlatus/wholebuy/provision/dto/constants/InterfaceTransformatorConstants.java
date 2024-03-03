package de.bitconex.adlatus.wholebuy.provision.dto.constants;

import lombok.Getter;

@Getter
public enum InterfaceTransformatorConstants {
    CUSTOMER_NUMBER("customerNumber"),
    INTERFACE_TYPE("interfaceType"),
    INTERFACE_VERSION("interfaceVersion"),
    PROVISION("Bereitstellung"),
    TYPE("type");

    private final String value;

    InterfaceTransformatorConstants(String value) {
        this.value = value;
    }
}
