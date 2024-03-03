/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 13:39:44
 */
package de.mnet.migration.common;

import java.util.*;
import java.util.concurrent.*;

import de.mnet.migration.common.result.TransformationResult;


/**
 * Marker interface for Spring injection (Controller will fetch all beans of this type and run them). Returns a list of
 * TransformationResults which will be logged by the Controller via the DatabaseLogger. This list must not be null!
 *
 *
 */
public interface MigrationAdditionalData extends Callable<List<TransformationResult>> {
    // no methods
}
