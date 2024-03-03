/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2015
 */
package de.mnet.migration.hurrican.accessaddress;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

/**
 * Transformator generiert fuer die Endstelle, die in {@link de.mnet.migration.hurrican.accessaddress.AccessAddress}
 * angegeben ist eine neue Adresse basierend auf der entsprechenden AP-Adresse in der Taifun-DB.
 */
public class AccessAddressTransformator extends HurricanTransformator<AccessAddress> {

    private static final Logger LOGGER = Logger.getLogger(AccessAddressTransformator.class);

    public static class MigMessages extends HurricanMessages {
        Messages.Message SUCCESS = new Messages.Message(
                TransformationStatus.OK, 0x01L,
                "new address created and assigned: %s");
        Messages.Message NO_TAIFUN_ADDRESS = new Messages.Message(
                TransformationStatus.BAD_DATA, 0x02L,
                "No access point address found in Taifun: %s");
        Messages.Message ERROR_UNEXPECTED = new Messages.Message(
                TransformationStatus.ERROR, 0x04L,
                "unexpected error: %s");
    }

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;

    static MigMessages messages = new MigMessages();

    private static HurricanMigrationStarter migrationStarter;

    private Long sessionId;

    private static List<MigrationResult> results = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/accessaddress/access-address-migration.xml").finish();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }


    @Override
    public TransformationResult transform(AccessAddress row) {
        String message = String.format("Create new Address for ENDSTELLE Id %s (Hurrican AuftragId %s)",
                row.endstelleId, row.auftragId);
        messages.prepare(message);
        LOGGER.info(message);

        createNewAccessAddress(row);

        TargetIdList updatedIds = new TargetIdList(new SourceTargetId("Endstelle ID", row.endstelleId));
        return messages.evaluate(updatedIds);
    }


    void createNewAccessAddress(AccessAddress row) {
        try {
            Endstelle endstelle = endstellenService.findEndstelle(row.endstelleId);
            if (endstelle == null) {
                messages.BAD_DATA.add(String.format("Endstelle not found for Id %s (Auftrag Id %s)",
                        row.endstelleId, row.auftragId));
            }
            else {
                Adresse addressToCopy = billingAuftragService.findAnschlussAdresse4Auftrag(row.auftragNoOrig, row.esTyp);
                if (addressToCopy != null) {
                    CCAddress newAccessAddress = kundenService.getCCAddress4BillingAddress(addressToCopy);
                    newAccessAddress.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
                    kundenService.saveCCAddress(newAccessAddress);

                    endstelle.setAddressId(newAccessAddress.getId());
                    endstellenService.saveEndstelle(endstelle);

                    String infoMessage = String.format("Auftrag %s / Endstelle-Id %s - New address Id: %s",
                            row.auftragId, row.endstelleId, newAccessAddress.getId());
                    messages.SUCCESS.add(infoMessage);
                    LOGGER.info(infoMessage);
                }
                else {
                    messages.NO_TAIFUN_ADDRESS.add(
                            String.format("Billing order %s (ES-Typ %s)", row.auftragNoOrig, row.esTyp));
                }
            }
        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
        }
    }


}
