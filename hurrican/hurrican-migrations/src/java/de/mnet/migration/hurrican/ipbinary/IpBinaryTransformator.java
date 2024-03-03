package de.mnet.migration.hurrican.ipbinary;

import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.net.IPAddressConverter;
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
 * Transformator, um eine IP-Adresse zu laden und gleich wieder zu speichern. Durch den Speicher-Vorgang wird die
 * Binaer-Darstellung der IP-Adresse generiert und mit abgespeichert.
 */
public class IpBinaryTransformator extends HurricanTransformator<IpBinary> {

    private static final Logger LOGGER = Logger.getLogger(IpBinaryTransformator.class);

    public static class MigMessages extends HurricanMessages {
        Messages.Message ERROR_UPDATING_IP = new Messages.Message(
                TransformationStatus.ERROR, 0x01L,
                "Error updating T_IP_ADDRESS with ID: %s; Error: %s");
    }

    static MigMessages messages = new MigMessages();

    private static HurricanMigrationStarter migrationStarter;

    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    private IPAddressService ipAddressService;

    private Long sessionId;

    public static void main(String[] args) {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/ipbinary/ipbinary-migration.xml").finish();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    @Override
    public TransformationResult transform(IpBinary row) {
        messages.prepare(String.format("IP-Address: %s", row.ipAddress));
        LOGGER.info(String.format(".... transform IP Address ID=%s, Address=%s, current binary=%s ", row.id,
                row.ipAddress, row.binaryRepresentation));

        createAndSaveBinary(row);

        TargetIdList updatedIds = new TargetIdList(new SourceTargetId("IP-Address Target ID", row.id));
        return messages.evaluate(updatedIds);
    }

    private void createAndSaveBinary(IpBinary row) {
        try {
            IPAddress address = ipAddressService.findIpAddress(row.id);
            if (address != null) {
                String binary = IPAddressConverter.parseIPAddress(
                        address.getAbsoluteAddress(),
                        !address.isPrefixAddress());
                address.setBinaryRepresentation(binary);

                ipAddressService.saveIPAddress(address, sessionId);
                if (StringUtils.isEmpty(address.getBinaryRepresentation())) {
                    messages.BAD_DATA.add("Binary representation not created!");
                }
                else {
                    messages.INFO.add(String.format("binary=%s", address.getBinaryRepresentation()));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            messages.ERROR_UPDATING_IP.add(row.id, e.getMessage());
        }
    }

}
