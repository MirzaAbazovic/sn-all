/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2005 08:17:00
 */
package de.augustakom.common.tools.collections;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Hilfsklasse fuer das Arbeiten mit Collections.
 */
public class CollectionTools {

    /**
     * Kopiert alle Objekte der Collection <code>toCopy</code> in die Collection <code>destCollection</code>. <br> Im
     * Gegensatz zu <code>Collection.addAll(..)</code> werden evtl. NullPointer-Exceptions unterdrueckt bzw. der Code
     * nicht ausgefuehrt wenn die Ziel- oder Quell-Collection <code>null</code> ist.
     */
    public static <T, S extends T> void addAllIgnoreNull(Collection<T> destCollection, Collection<S> toCopy) {
        if ((destCollection != null) && (toCopy != null)) {
            destCollection.addAll(toCopy);
        }
    }

    /**
     * Ueberprueft, ob die Collection <code>coll</code> <code>null</code> oder 'leer' ist.
     *
     * @param coll zu pruefende Collection
     * @return true wenn die Collection <code>null</code> oder 'leer' ist.
     */
    public static <T> boolean isEmpty(Collection<T> coll) {
        return (coll == null) || coll.isEmpty();
    }

    /**
     * @see CollectionTools#isEmpty(Collection) - Negation!
     */
    public static <T> boolean isNotEmpty(Collection<T> coll) {
        return !isEmpty(coll);
    }

    /**
     * Fuegt der Collection <code>coll</code> den String <code>toAdd</code> hinzu, sofern dieser nicht leer (null oder
     * Leerstring) ist.
     *
     * @param coll  Collection, der der String hinzugefuegt werden soll
     * @param toAdd String, der hinzugefuegt werden soll.
     */
    public static void addIfNotBlank(Collection<String> coll, String toAdd) {
        if ((coll != null) && StringUtils.isNotBlank(toAdd)) {
            coll.add(toAdd);
        }
    }

    /**
     * Fuegt der Collection <code>coll</code> das Object <code>toAdd</code> hinzu, sofern dieses nicht <code>null</code>
     * ist.
     *
     * @param coll  Collection, der das Objekt hinzugefuegt werden soll
     * @param toAdd Objekt, das hinzugefuegt werden soll.
     */
    public static <T, S extends T> void addIfNotNull(Collection<T> coll, S toAdd) {
        if ((coll != null) && (toAdd != null)) {
            coll.add(toAdd);
        }
    }

    /**
     * Ueberprueft, ob die angegebene Collection die erwartete Groesse besitzt.
     *
     * @param col          zu pruefende Collection
     * @param expectedSize erwartete Groesse der Collection.
     * @return true, wenn die Collection die Groesse 'expectedSize' besitzt - sonst false.
     */
    public static <T> boolean hasExpectedSize(Collection<T> col, int expectedSize) {
        return (col != null) && (col.size() == expectedSize);
    }

    /**
     * @param coll  Collection, aus der ein Objekt an einem best. Index ermittelt werden soll
     * @param index index to get
     * @return das Objekt am angegebenen Index oder <code>null</code>, wenn der Index nicht existiert.
     * @see org.apache.commons.collections.CollectionUtils#get(Object, int) Evtl. auftretende Exceptions (z.B.
     * IndexOutOfBoundsException) werden in dieser Methode jedoch unterdrueckt!
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFromCollection(Collection<T> coll, int index) {
        try {
            return (T) CollectionUtils.get(coll, index);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Erstellt eine neue Collection mit umgekehrter Reihenfolge wie <code>toReverse</code>.
     *
     * @return Collection mit umgekehrter Reihenfolge
     */
    public static <T> Collection<T> reverse(Collection<T> toReverse) {
        if (toReverse != null) {
            Object[] ordered = toReverse.toArray();
            CollectionUtils.reverseArray(ordered);

            List<T> retVal = new ArrayList<>();
            CollectionUtils.addAll(retVal, ordered);
            return retVal;
        }
        return null;
    }

    /**
     * Erstellt eine Map aus der Collection, wobei keyMapper verwendet wird um für die Values Keys zu ermitteln.
     */
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<V, K> keyMapper) {
        final Map<K, V> result = new HashMap<>(collection.size());
        for (final V value : collection) {
            result.put(keyMapper.apply(value), value);
        }
        return result;
    }

    public static <T> Collection<T> select(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate::test).collect(Collectors.toList());
    }

    public static <T> String formatCommaSeparated(Collection<T> carrierRefIds) {
        StringBuilder sb = new StringBuilder();
        if (carrierRefIds != null) {
            Iterator<T> it = carrierRefIds.iterator();
            while (it.hasNext()) {
                sb.append(it.next().toString());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Gruppiert Elemente einer Liste in Liste von Listen anhand des BiPredicate p.
     * Die Elemente der Eingangsliste werden paarweise mit p geprüft und falls das
     * Prädikat true liefert, werden nachfolgende Elemente in eine neue Liste
     * gruppiert.
     */
    public static <T> List<List<T>> chunkBy2(List<T> list, BiPredicate<T, T> p) {
        List<List<T>> result = new ArrayList<>();
        List<T> chunk = new ArrayList<>();
        Optional<T> prev = Optional.empty();

        for (T elem : list) {
            if (prev.isPresent() && p.test(prev.get(), elem)) {
                if (!chunk.isEmpty()) {
                    result.add(chunk);
                }
                chunk = new ArrayList<>();
            }
            chunk.add(elem);
            prev = Optional.of(elem);
        }
        if (!chunk.isEmpty()) {
            result.add(chunk);
        }
        return result;
    }
}
