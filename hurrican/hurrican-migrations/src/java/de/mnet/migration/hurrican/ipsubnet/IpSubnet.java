package de.mnet.migration.hurrican.ipsubnet;

import de.mnet.migration.common.util.ColumnName;


/**
 * Modell fuer die Basis-Informationen der zu migrierenden Subnetz-Maske.
 */
public class IpSubnet {

    @ColumnName("ID")
    public Long id;
    @ColumnName("IP_ADDRESS")
    public String ipAddress;
    @ColumnName("ADDRESS_TYPE")
    public String addressType;
    @ColumnName("EG_IP_SUBNET_MASK")
    public String subnetMaskEgIp;
    @ColumnName("ROUTING_NETMASK")
    public String subnetMaskRouting;

}


