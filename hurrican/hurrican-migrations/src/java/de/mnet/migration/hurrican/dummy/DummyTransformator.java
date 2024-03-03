package de.mnet.migration.hurrican.dummy;

import javax.annotation.*;

import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;

public class DummyTransformator extends HurricanTransformator<Dummy> {

    static HurricanMessages messages = new HurricanMessages();

    private static HurricanMigrationStarter migrationStarter;

    @SuppressWarnings("unused")
    private Long sessionId;

    public static void main(String[] args) {
        migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args,
                "de/mnet/migration/hurrican/dummy/hurrican-dummy-migration.xml").finish();
    }

    @PostConstruct
    public void init() {
        // null check is necessary for MigrationStartupContextsTests because it is not started using #main method
        if (migrationStarter != null) {
            sessionId = migrationStarter.sessionId;
        }
    }

    @Override
    public TransformationResult transform(Dummy row) {
        System.out.println(".... transform dummy " + row.id);

        TargetIdList createdIds = new TargetIdList(new SourceTargetId("dummy", String.format("%s", row.id)));
        return messages.evaluate(createdIds);
    }

}
