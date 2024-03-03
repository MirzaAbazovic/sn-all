package de.mnet.common.errorhandling;

/**
 * List of known error codes expressed to the Atlas ESB error service.
 *
 *
 */
public enum ErrorCode {
    HUR_DEFAULT("HUR_TECH_1000", "Internal Hurrican Error"),
    WBCI_DEFAULT("WBCI_TECH_1000", "Internal WBCI processing error"),
    WBCI_DUPLICATE_VA_ID("WBCI_TECH_1001", "Duplicate Va-Id"),
    WBCI_UNKNOWN_VA("WBCI_TECH_1002", "Unknown Va-Id"),
    WBCI_SCHEMA_VALIDATION("WBCI_TECH_1003", "Invalid WBCI message content - schema validation failed"),
    WITA_DEFAULT("WITA_TECH_1000", "Internal WITA processing error"),
    WITA_UNKNOWN_ORDER("WITA_TECH_1001", "Unknown WITA order"),
    WITA_MESSAGE_OUT_OF_ORDER("WITA_TECH_1002", "WITA message out of order error"),
    WITA_SCHEMA_VALIDATION("WITA_TECH_1003", "Invalid WITA message content - schema validation failed");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
