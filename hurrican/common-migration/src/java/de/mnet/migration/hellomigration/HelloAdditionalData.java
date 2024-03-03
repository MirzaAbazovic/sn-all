/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2010 16:57:35
 */

package de.mnet.migration.hellomigration;

import java.util.*;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.result.TransformationResult;


/**
 *
 */
public class HelloAdditionalData implements MigrationAdditionalData {

    private Integer truth = 0;

    @Override
    public List<TransformationResult> call() throws Exception {
        truth = 42;
        return Collections.emptyList();
    }

    public Integer getTruth() {
        return truth;
    }
}
