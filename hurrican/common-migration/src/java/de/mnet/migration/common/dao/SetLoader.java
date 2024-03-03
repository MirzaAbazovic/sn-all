/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 16:02:00
 */

package de.mnet.migration.common.dao;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.result.TransformationResult;


/**
 * Lädt die Daten aus einem DataLoader in ein Set.
 */
public class SetLoader<TYPE> implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(SetLoader.class);
    private DataLoader<TYPE> dataLoader;
    private Set<TYPE> valueSet = new HashSet<TYPE>();

    @Override
    public List<TransformationResult> call() throws Exception {
        List<TransformationResult> transformationResults = new ArrayList<TransformationResult>();
        for (TYPE currentValue : dataLoader.getSourceData()) {
            valueSet.add(currentValue);
        }
        LOGGER.info(valueSet.size() + " entries for sourceObject = " + dataLoader.getSourceObjectName() + " in set");
        return transformationResults;
    }

    public boolean contains(TYPE key) {
        return valueSet.contains(key);
    }

    @Required
    public void setDataLoader(DataLoader<TYPE> dataLoader) {
        this.dataLoader = dataLoader;
    }
}
