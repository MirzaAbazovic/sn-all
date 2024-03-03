/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 16:48:44
 */
package de.mnet.migration.common.dao;

import java.util.*;
import org.apache.log4j.Logger;

import de.mnet.migration.common.util.CollectionUtil;


/**
 * Dao, das per DataLoader Daten liest und Werte anhand eines Schlüssels gruppiert<br/> Das Aufteilen in Bloecke
 * geschieht ueber {@link List#subList(int, int)}, so dass dafuer kein signifikanter Overhead produziert wird.<br/>
 * <b>Thread-Safe.</b>
 */
public class MapDataLoaderMigrationDao<K, T extends KeyAbleSource<K>> extends ListBackedMigrationDao<K> {
    private static final Logger LOGGER = Logger.getLogger(MapDataLoaderMigrationDao.class);

    protected volatile Map<K, List<T>> keyToList = new HashMap<K, List<T>>();
    private DataLoader<T> dataLoader;

    protected List<K> convertList2Map(List<T> list) {
        for (T element : list) {
            CollectionUtil.addToMapList(keyToList, element.getKey(), element);
        }
        return new ArrayList<K>(keyToList.keySet());
    }

    /**
     * @see de.mnet.migration.common.dao.ListBackedMigrationDao#getSourceData()
     */
    @Override
    protected List<K> getSourceData() {
        List<T> list = dataLoader.getSourceData();
        List<K> keyList = convertList2Map(list);
        LOGGER.info("" + keyList.size() + " keys in " + dataLoader.getSourceObjectName() + " map");
        return keyList;
    }

    public List<T> getValue(K key) {
        return keyToList.get(key);
    }

    public void setDataLoader(DataLoader<T> dataLoader) {
        this.dataLoader = dataLoader;
    }

}
