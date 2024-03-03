package de.mnet.migration.hurrican.iproute;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
 * Transformator, um die Prefix-Length Angaben von T_IP_ROUTE auf T_IP_ADDRESS zu migrieren.
 */
public class IpRouteTransformator extends HurricanTransformator<IpRoute> {

    private static final Logger LOGGER = Logger.getLogger(IpRouteTransformator.class);

    public static class MigMessages extends HurricanMessages {
        Messages.Message ERROR_UNEXPECTED = new Messages.Message(
                TransformationStatus.ERROR, 0x01L,
                "unexpected error: %s");
    }

    static MigMessages messages = new MigMessages();

    private static HurricanMigrationStarter migrationStarter;

    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;

    private Long sessionId;

    public static void main(String[] args) {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/iproute/iproute-migration.xml").finish();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    @Override
    public TransformationResult transform(IpRoute row) {
        messages.prepare(String.format(
                "Convert prefix length %d of IP Route %d(ID) to IP address: %d(ID), %s(Address), %s(Type)",
                row.prefixLength, row.id, row.ipAddressId, row.ipAddress, row.addressType));
        LOGGER.info(String.format(".... transform IP Route ID=%s ", row.id));

        migratePrefix(row);

        TargetIdList updatedIds = new TargetIdList(new SourceTargetId("IP-Address Target ID", row.id));
        return messages.evaluate(updatedIds);
    }

    private void migratePrefix(IpRoute row) {
        try {
            IPAddress address = ipAddressService.findIpAddress(row.ipAddressId);
            address.setIpType(AddressTypeEnum.IPV4_prefix);
            address.setAddress(modifyIpAddress(row.ipAddress, row.prefixLength));
            ipAddressService.saveIPAddress(address, sessionId);

            messages.INFO.add(String.format("IP-Address changed to %s, type %s", address.getAddress(), address.getIpType().name()));
        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
        }
    }


    String modifyIpAddress(String ip, int prefix) {
        if (!StringUtils.contains(ip, "/")) {
            return String.format("%s/%s", ip, prefix);
        }
        return String.format("%s/%s", StringUtils.substringBefore(ip, "/"), prefix);
    }

}
