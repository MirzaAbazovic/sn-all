package de.mnet.migration.hurrican.ipbinary;

import de.mnet.migration.common.util.ColumnName;


/**
 * Modell fuer die Basis-Informationen der zu migrierenden IP-Adresse.
 */
public class IpBinary {

    @ColumnName("ID")
    public Long id;
    @ColumnName("IP_ADDRESS")
    public String ipAddress;
    @ColumnName("BINARY_REPRESENTATION")
    public String binaryRepresentation;

}


