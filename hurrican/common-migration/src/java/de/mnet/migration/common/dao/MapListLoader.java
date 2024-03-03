/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2010 18:01:41
 */

package de.mnet.migration.common.dao;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.util.CollectionUtil;


/**
 *
 */
public class MapListLoader<KEY, VALUE extends KeyAbleSource<KEY>> implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(MapListLoader.class);
    protected DataLoader<VALUE> dataLoader;
    protected Map<KEY, List<VALUE>> existingValuesMap = new HashMap<KEY, List<VALUE>>();

    @Override
    public List<TransformationResult> call() throws Exception {
        for (VALUE existingValue : dataLoader.getSourceData()) {
            KEY key = existingValue.getKey();
            CollectionUtil.addToMapList(existingValuesMap, key, existingValue);
        }
        LOGGER.info(existingValuesMap.size() + " lists for sourceObject = " + dataLoader.getSourceObjectName() + " in map");
        return Collections.emptyList();
    }

    public List<VALUE> getValue(KEY key) {
        if (!existingValuesMap.containsKey(key)) {
            return Collections.emptyList();
        }
        return existingValuesMap.get(key);
    }

    public boolean containsKey(KEY key) {
        return existingValuesMap.containsKey(key);
    }

    @Required
    public void setDataLoader(DataLoader<VALUE> dataLoader) {
        this.dataLoader = dataLoader;
    }

}

