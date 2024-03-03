package de.bitconex.adlatus.wholebuy.provision.dto.enums;

public enum XmlExtractorEnum {
    MELDUNGSTYP_TYPE("//meldungstyp//@type"),
    ZEITSTEMPEL("//zeitstempel"),
    INTERFACE_VERSION("//majorRelease"),
    ORDER_TYPE("NEU Bereitstellung");
    private final String expression;

    XmlExtractorEnum(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return this.expression;
    }
}
