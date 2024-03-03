/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2010 13:54:32
 */
package de.mnet.migration.common.util;

import java.util.*;
import com.google.common.base.Function;

public class CollectionUtil {

    public static <T> List<T> list(T... elements) {
        List<T> result = new ArrayList<T>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

    public static <T> List<T> subList(List<T> list, int fromIndex, int toIndex) {
        int toIndex1 = toIndex;
        int fromIndex1 = fromIndex;
        if (fromIndex1 > list.size()) {
            fromIndex1 = list.size();
        }
        if (toIndex1 > list.size()) {
            toIndex1 = list.size();
        }
        return list.subList(fromIndex1, toIndex1);
    }

    /**
     * Appends to a list that's stored in a map (or creates the list if it does not yet exist in the map for the given
     * key).
     *
     * @param map    Map that consists of Lists for the keys
     * @param addKey Key for the list to append to
     * @param value  The value to append
     */
    public static <K, V> void addToMapList(Map<K, List<V>> map, K addKey, V value) {
        List<V> oldList = map.get(addKey);
        if (oldList == null) {
            oldList = new ArrayList<V>();
            map.put(addKey, oldList);
        }
        oldList.add(value);
    }

    public static <K, V> void addToMapMap(Map<K, Map<V, V>> map, K addKey, V value) {
        Map<V, V> oldMap = map.get(addKey);
        if (oldMap == null) {
            oldMap = new HashMap<V, V>();
            map.put(addKey, oldMap);
        }
        oldMap.put(value, value);
    }

    public static <K, V> void addToMapSet(Map<K, Set<V>> map, K addKey, V value) {
        Set<V> oldMap = map.get(addKey);
        if (oldMap == null) {
            oldMap = new HashSet<V>();
            map.put(addKey, oldMap);
        }
        oldMap.add(value);
    }

    public static <K, V> Set<K> groupAsSetBy(Collection<V> collection, Function<V, K> groupByFunc) {
        Set<K> result = new HashSet<K>();
        for (V value : collection) {
            K group = groupByFunc.apply(value);
            result.add(group);
        }
        return result;
    }

    public static <K, V> Map<K, List<V>> groupBy(List<V> list, Function<V, K> groupByFunc) {
        Map<K, List<V>> result = new HashMap<K, List<V>>();
        for (V value : list) {
            K group = groupByFunc.apply(value);
            addToMapList(result, group, value);
        }
        return result;
    }
}
