package de.mnet.migration.hellomigration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.main.MigrationStarter;
import de.mnet.migration.common.result.TransformationResult;

public class HelloMigration extends MigrationTransformator<Void> {

    private static final Logger LOGGER = Logger.getLogger(HelloMigration.class);

    public static void main(String[] args) {
        new MigrationStarter().startMigration(args, "de/mnet/migration/hellomigration/helloMigration.xml").finish();
    }

    @Autowired
    private HelloAdditionalData helloAdditionalData;

    @Override
    public TransformationResult transform(Void dioV) {
        LOGGER.info("transform() - started");
        LOGGER.info("Truth: " + helloAdditionalData.getTruth());
        return ok(source("sourceValue"), target("destinationValue"), "infoText");
    }
}
