package de.mnet.migration.hurrican.base;

import de.mnet.migration.base.MigrationIntegrationTest;


/**
 * Basisklasse von der alle Integration Tests ableiten. Konfiguriert das Logging-Framework.
 * <p/>
 * <b>Achtung:</b>Es wird empfohlen die @BeforeMethod Annotation anstatt der @BeforeClass Annotation zu verwenden, falls
 * man Instanzen aus dem Spring Context benoetigt. Dadurch wird sichergestellt, dass der Spring Context auch wirklich
 * initialisiert wurde
 */
public class HurricanMigrationIntegrationTest extends MigrationIntegrationTest {

}


