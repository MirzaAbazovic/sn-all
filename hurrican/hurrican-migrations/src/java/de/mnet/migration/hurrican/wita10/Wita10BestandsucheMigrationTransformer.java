package de.mnet.migration.hurrican.wita10;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.aggregator.VertragsNummerPvAggregator;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

public class Wita10BestandsucheMigrationTransformer extends HurricanTransformator<Wita10BestandsucheMigrationData> {

    private static final Logger LOGGER = Logger.getLogger(Wita10BestandsucheMigrationTransformer.class);
    private final VertragsNummerPvAggregator vertragsNummerPvAggregator;
    private final WitaConfigService witaConfigService;

    private final CBVorgangDAO cbVorgangDAO;

    @Autowired
    public Wita10BestandsucheMigrationTransformer(WitaConfigService witaConfigService,
            VertragsNummerPvAggregator vertragsNummerPvAggregator,
            CBVorgangDAO cbVorgangDAO) {
        this.witaConfigService = witaConfigService;
        this.vertragsNummerPvAggregator = vertragsNummerPvAggregator;
        this.cbVorgangDAO = cbVorgangDAO;
    }

    public static void main(String[] args) {
        // -Dspring.profiles.active=wita-wbci-atlas
        List<String> migrationContexts = new ArrayList<>();
        migrationContexts.add("de/mnet/migration/hurrican/wita10/wita_10_Migration.xml");
        new HurricanMigrationStarter()
                .startMigration(args, migrationContexts, false)
                .finish();

        System.exit(0);
    }

    @Override
    public TransformationResult transform(Wita10BestandsucheMigrationData migrationData) {
        final WitaCdmVersion witaVersion = witaConfigService.getDefaultWitaVersion();
        if (!witaVersion.isGreaterOrEqualThan(WitaCdmVersion.V2))
        {
            String msg = "CDM Version " + witaVersion.getName() + " ist nicht V2 oder groesser";
            LOGGER.info(msg);
            return skipped(sources(
                    id("wita_request_id", migrationData.requestID),
                    id("ext_auftrags_nr", migrationData.externeAuftragsnummer)),
                    msg,
                    CLASS_DEFAULT,
                    "keine Vertragsnummer");

        }


        LOGGER.info("cbvorgang = " + migrationData.cbVorgangID);
        WitaCBVorgang cbVorgang = cbVorgangDAO.findById(migrationData.cbVorgangID, WitaCBVorgang.class);
        String vertragsnummer = vertragsNummerPvAggregator.aggregate(cbVorgang);

        if (vertragsnummer != null) {
            Geschaeftsfall geschaeftsfall = cbVorgangDAO.findById(migrationData.geschaeftsfallID, Geschaeftsfall.class);
            geschaeftsfall.setVertragsNummer(vertragsnummer);
            cbVorgangDAO.store(geschaeftsfall);

            String msg = String.format("wita_request id = '%d', ext_auftrags_nummer = '%s', cb_vorgang = '%d' vertragsnummer = '%s'",
                    migrationData.requestID, migrationData.externeAuftragsnummer, migrationData.cbVorgangID, vertragsnummer);
            LOGGER.info(msg);

            return ok(sources(
                    id("wita_request_id", migrationData.requestID),
                    id("ext_auftrags_nr", migrationData.externeAuftragsnummer)),
                    target(id("vertragsnummer", vertragsnummer)),
                    msg);
        }
        else {
            String msg = String.format("skipped wita_request id = '%d', ext_auftrags_nummer = '%s', cb_vorgang = '%d' --> keine Vertragsnummer gefunden",
                    migrationData.requestID, migrationData.externeAuftragsnummer, migrationData.cbVorgangID);
            LOGGER.info(msg);
            return skipped(sources(
                    id("wita_request_id", migrationData.requestID),
                    id("ext_auftrags_nr", migrationData.externeAuftragsnummer)),
                    msg,
                    CLASS_DEFAULT,
                    "keine Vertragsnummer");
        }
    }
}
