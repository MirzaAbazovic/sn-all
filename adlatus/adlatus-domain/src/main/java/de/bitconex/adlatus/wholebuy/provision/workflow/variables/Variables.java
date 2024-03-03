package de.bitconex.adlatus.wholebuy.provision.workflow.variables;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Variable names in the state machine context
 */

public enum Variables {

    RESOURCE_ORDER("resourceOrder"),
    RESOURCE_ORDER_ID("resourceOrderId"),
    EXTERNAL_ORDER_NUMBER("externalOrderNumber", "externeAuftragsnummer"),
    CUSTOMER_NUMBER("customerNumber", "kundennummer"),
    LINE_ID("lineId", "lineID"),
    BINDING_DELIVERY_DATE("bindingDeliveryDate", "verbindlicherLiefertermin"),
    PAYMENT_DATE("paymentDate", "entgelttermin"),
    CONTACT_PERSON("contactPerson", "ansprechpartnerTelekom"),
    COMPLETION_DATE("completionDate", "erledigungstermin"),
    WITA_PRODUCT_INBOX_ID("witaProductInboxId");

    private final String variableName;
    private String witaElementName;

    Variables(String variableName, String witaElementName) {
        this.variableName = variableName;
        this.witaElementName = witaElementName;
    }

    Variables(String variableName) {
        this.variableName = variableName;
    }

    @JsonValue
    public String getVariableName() {
        return variableName;
    }

    @JsonValue
    public String getWitaElementName() {
        return witaElementName;
    }

    @Override
    public String toString() {
        return String.valueOf(variableName);
    }

    public static <E extends Enum<E>> boolean isEnumConstant(String variableName) {
        return Arrays.stream(values())
                .anyMatch(value -> value.variableName.equals(variableName));

    }

}
