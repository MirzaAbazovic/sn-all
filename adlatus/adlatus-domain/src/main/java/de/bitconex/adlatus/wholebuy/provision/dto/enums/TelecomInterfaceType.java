package de.bitconex.adlatus.wholebuy.provision.dto.enums;

public enum TelecomInterfaceType { //todo: move me
    WITA14("WITA", "14", "wita-create-order.ftlh"),
    WITA15("WITA", "15", "wita-create-order.ftlh"),
    // TODO should be updated once we actually start working with SPRI interface
    SPRI("SPRI", "1", "spri-create-order.ftlh");

    private final String templateName;
    private final String name;
    private String version;

    TelecomInterfaceType(String name, String version, String templateName) {
        this.name = name;
        this.version = version;
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public static TelecomInterfaceType fromNameAndVersion(String interfaceName, String version) {
        for (TelecomInterfaceType type : TelecomInterfaceType.values()) {
            if (type.getName().equals(interfaceName) && type.getVersion().equals(version)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum found with interface name: " + interfaceName + " and version: " + version);
    }
}
