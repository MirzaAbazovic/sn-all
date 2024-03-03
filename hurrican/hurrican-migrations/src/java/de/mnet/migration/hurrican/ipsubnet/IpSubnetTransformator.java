package de.mnet.migration.hurrican.ipsubnet;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

/**
 * Transformator, um aus den Subnetz-Masken einer IP die zugehoerige Prefix-Schreibweise zu generieren.
 */
public class IpSubnetTransformator extends HurricanTransformator<IpSubnet> {

    private static final Logger LOGGER = Logger.getLogger(IpSubnetTransformator.class);

    public static class MigMessages extends HurricanMessages {
        Messages.Message WARNING_DIFFERENT_PREFIX = new Messages.Message(
                TransformationStatus.WARNING, 0x01L,
                "Prefix definition is different! current IP: %s, Subnetmask: %s, calculated prefix: %s, Taifun Id: %s");
        Messages.Message ERROR_CALCULATING_PREFIX = new Messages.Message(
                TransformationStatus.ERROR, 0x02L,
                "Could not calculate prefix for netmask %s");
        Messages.Message ERROR_UNEXPECTED = new Messages.Message(
                TransformationStatus.ERROR, 0x04L,
                "unexpected error: %s");
    }

    static MigMessages messages = new MigMessages();

    private static HurricanMigrationStarter migrationStarter;

    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;

    private Long sessionId;

    public static void main(String[] args) {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/ipsubnet/ipsubnet-migration.xml").finish();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    /**
     * Ablauf: - Prefix zur Netmask ermitteln - falls IP ein Prefix besitzt --> vergleichen - Typen: T_EG_IP -->
     * IPV4_with_prefixlength, T_EG_ROUTING --> IPV4_prefix
     */
    @Override
    public TransformationResult transform(IpSubnet row) {
        messages.prepare(String.format("Convert subnet %s to prefix for IP address: %d(ID), %s(Address)",
                (row.subnetMaskEgIp != null) ? row.subnetMaskEgIp : row.subnetMaskRouting, row.id, row.ipAddress));
        LOGGER.info(String.format(".... transform IP Address ID=%s ", row.id));

        calculatePrefix(row);

        TargetIdList updatedIds = new TargetIdList(new SourceTargetId("IP-Address Target ID", row.id));
        return messages.evaluate(updatedIds);
    }

    private void calculatePrefix(IpSubnet row) {
        try {
            boolean ipV4Prefix = StringUtils.isNotBlank(row.subnetMaskRouting);
            String subnetMask = (ipV4Prefix) ? row.subnetMaskRouting : row.subnetMaskEgIp;
            int prefix = getPrefixForNetmask(subnetMask);

            if (prefix >= 0) {
                IPAddress address = ipAddressService.findIpAddress(row.id);

                if (!prefixMatchesIfExist(row.ipAddress, prefix)) {
                    messages.WARNING_DIFFERENT_PREFIX.add(row.ipAddress, subnetMask, prefix, address.getBillingOrderNo());
                }

                AddressTypeEnum addressType = (ipV4Prefix) ? AddressTypeEnum.IPV4_prefix : AddressTypeEnum.IPV4_with_prefixlength;
                address.setIpType(addressType);
                address.setAddress(modifyIpAddress(row.ipAddress, prefix));
                ipAddressService.saveIPAddress(address, sessionId);

                messages.INFO.add(String.format("IP-Address changed to %s, type %s", address.getAddress(), address.getIpType().name()));
            }
            else {
                messages.ERROR_CALCULATING_PREFIX.add(subnetMask);
            }
        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
        }
    }


    boolean prefixMatchesIfExist(String ip, int prefix) {
        if (StringUtils.contains(ip, "/")) {
            String prefixFromIp = StringUtils.substringAfterLast(ip, "/");
            return StringUtils.equals(prefixFromIp, String.format("%s", prefix));
        }
        else {
            return true;
        }
    }


    String modifyIpAddress(String ip, int prefix) {
        if (!StringUtils.contains(ip, "/")) {
            return String.format("%s/%s", ip, prefix);
        }
        return String.format("%s/%s", StringUtils.substringBefore(ip, "/"), prefix);
    }


    private int getPrefixForNetmask(String netmask) {
        return IPToolsV4.instance().getPrefixLength4Netmask(netmask);
    }


}
