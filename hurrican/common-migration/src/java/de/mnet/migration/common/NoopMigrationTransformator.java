/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2010 15:25:14
 */
package de.mnet.migration.common;

import de.mnet.migration.common.result.TransformationResult;


/**
 *
 */
public class NoopMigrationTransformator extends MigrationTransformator<Void> {

    /**
     * @see de.mnet.migration.common.MigrationTransformator#transform(java.lang.Object)
     */
    @Override
    public TransformationResult transform(Void row) {
        throw new UnsupportedOperationException("Dummy-Transformator must not be called");
    }

}
