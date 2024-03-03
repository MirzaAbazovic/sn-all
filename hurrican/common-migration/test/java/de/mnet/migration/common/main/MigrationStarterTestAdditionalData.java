/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2010 17:36:48
 */
package de.mnet.migration.common.main;

import static java.util.Collections.*;

import java.util.*;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.result.TransformationResult;


/**
 *
 */
public class MigrationStarterTestAdditionalData implements MigrationAdditionalData {

    private Integer truth;

    @Override
    public List<TransformationResult> call() throws Exception {
        truth = 42;
        return emptyList();
    }

    public Integer getTruth() {
        return truth;
    }
}
