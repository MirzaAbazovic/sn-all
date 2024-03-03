/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 16:02:00
 */

package de.mnet.migration.common.dao;

import static de.mnet.migration.common.MigrationTransformator.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.mnet.migration.common.MigrationAdditionalData;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * Konvertiert beliebige Daten, die mit einem DataLoader geladen wurden in eine Map. Der Key fuer die Map muss von der
 * Entity mittels der Methode KeyAbleSource.getKey() implementiert werden,.
 */
public class MapLoader<KEY, VALUE extends KeyAbleSource<KEY>> implements MigrationAdditionalData {

    private static final Logger LOGGER = Logger.getLogger(MapLoader.class);
    private DataLoader<VALUE> dataLoader;
    protected Map<KEY, VALUE> existingValuesMap = new HashMap<KEY, VALUE>();

    @Override
    public List<TransformationResult> call() throws Exception {
        List<TransformationResult> transformationResults = new ArrayList<TransformationResult>();
        for (VALUE currentValue : dataLoader.getSourceData()) {
            addToMap(transformationResults, currentValue);
        }
        LOGGER.info(existingValuesMap.size() + " entries for sourceObject = " + dataLoader.getSourceObjectName() + " in map");
        return transformationResults;
    }

    protected void addToMap(List<TransformationResult> transformationResults, VALUE currentValue) {
        KEY contractKey = currentValue.getKey();
        if (existingValuesMap.containsKey(contractKey)) {
            handleDuplicate(transformationResults, contractKey, currentValue);
        }
        else {
            existingValuesMap.put(contractKey, currentValue);
        }
    }

    protected void handleDuplicate(List<TransformationResult> transformationResults, KEY contractKey, VALUE existingValue) {
        VALUE foundValue = existingValuesMap.get(contractKey);
        String infoText = "Für Key " + contractKey + " im " + dataLoader.getSourceObjectName() + " Loader " +
                "gibt es bereits einen Eintrag: '" + foundValue + "'. Aktueller Eintrag: '" + existingValue + "'";
        LOGGER.error(infoText);
        transformationResults.add(
                new TransformationResult(
                        TransformationStatus.BAD_DATA,
                        sources(id("LOADER_NAME", dataLoader.getSourceObjectName() + "Data")).merge(
                                existingValue.getKeySources()).merge(
                                foundValue.getValueSources()).merge(
                                existingValue.getValueSources()),
                        null,
                        infoText,
                        MigrationTransformator.CLASS_DEFAULT,
                        "DATA_LOADER",
                        null
                )
        );
    }

    public VALUE getValue(KEY key) {
        return existingValuesMap.get(key);
    }

    public boolean containsKey(KEY key) {
        return existingValuesMap.containsKey(key);
    }

    public int size() {
        return existingValuesMap.size();
    }

    public boolean isEmpty() {
        return existingValuesMap.isEmpty();
    }

    @Required
    public void setDataLoader(DataLoader<VALUE> dataLoader) {
        this.dataLoader = dataLoader;
    }
}
