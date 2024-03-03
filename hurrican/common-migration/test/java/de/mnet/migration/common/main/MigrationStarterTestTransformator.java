package de.mnet.migration.common.main;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.TransformationResult;

public class MigrationStarterTestTransformator extends MigrationTransformator<Void> {
    private static final Logger LOGGER = Logger.getLogger(MigrationStarterTestTransformator.class);

    @Autowired
    private MigrationStarterTestAdditionalData additionalData;

    @Override
    public TransformationResult transform(Void dioV) {
        LOGGER.info("transform() - truth: " + additionalData.getTruth());
        return ok(source("sourceValue"), target("destinationValue"), "infoText");
    }
}
