/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.2006 12:02:43
 */
package de.augustakom.common.tools.logging;

import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.MapTools;


/**
 * Hilfsklasse, um ein Modell zu debuggen.
 */
public class DebugModelHelper {

    /**
     * Gibt alle Parameter des Modells <code>model</code> auf dem Logger aus.
     */
    public static void debugModel(Object model, Logger logger) {
        if (model != null && logger != null && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + model.getClass().getName());

            try {
                Map description = PropertyUtils.describe(model);
                if (description != null) {
                    Collection keys = MapTools.getKeys(description);
                    if (CollectionTools.isNotEmpty(keys)) {
                        for (Object key : keys) {
                            logger.debug("  " + key + ": " + description.get(key));
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

}


