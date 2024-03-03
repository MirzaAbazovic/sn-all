package de.mnet.migration.hurrican.wita10;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;

public class Wita10BestandsucheUndoMigrationTransformer extends HurricanTransformator<Wita10BestandsucheMigrationData> {

    private static final Logger LOGGER = Logger.getLogger(Wita10BestandsucheUndoMigrationTransformer.class);
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    private final CBVorgangDAO cbVorgangDAO;

    @Autowired
    public Wita10BestandsucheUndoMigrationTransformer(CBVorgangDAO cbVorgangDAO) {
        this.cbVorgangDAO = cbVorgangDAO;
    }

    public static void main(String[] args) {
        List<String> migrationContexts = new ArrayList<>();
        migrationContexts.add("de/mnet/migration/hurrican/wita10/wita_10_undo_Migration.xml");
        new HurricanMigrationStarter()
                .startMigration(args, migrationContexts, false)
                .finish();

        System.exit(0);
    }

    @Override
    public TransformationResult transform(Wita10BestandsucheMigrationData migrationData) {
        Geschaeftsfall geschaeftsfall = cbVorgangDAO.findById(migrationData.geschaeftsfallID, Geschaeftsfall.class);
        String vertragsNummer = geschaeftsfall.getVertragsNummer();
        if (vertragsNummer != null) {
            LOGGER.info("started migration of " + migrationData.externeAuftragsnummer);
            geschaeftsfall.setVertragsNummer(null);
            cbVorgangDAO.store(geschaeftsfall);

            String msg = String.format("wita_request id = '%d', ext_auftrags_nummer = '%s', cb_vorgang = '%d' vertragsnummer = 'null'",
                    migrationData.requestID, migrationData.externeAuftragsnummer, migrationData.cbVorgangID);
            LOGGER.info(msg);

            return ok(sources(
                    id("wita_request_id", migrationData.requestID),
                    id("ext_auftrags_nr", migrationData.externeAuftragsnummer)),
                    target(id("vertragsnummer", null)),
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
