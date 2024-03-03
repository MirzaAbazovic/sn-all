package de.mnet.migration.hurrican.iproute;

import de.mnet.migration.common.util.ColumnName;


/**
 * Modell fuer die Basis-Informationen der zu migrierenden IP-Routen.
 */
public class IpRoute {

    @ColumnName("ID")
    public Long id;
    @ColumnName("IP_ADDRESS_ID")
    public Long ipAddressId;
    @ColumnName("IP_ADDRESS")
    public String ipAddress;
    @ColumnName("ADDRESS_TYPE")
    public String addressType;
    @ColumnName("PREFIX_LENGTH")
    public Integer prefixLength;

}


