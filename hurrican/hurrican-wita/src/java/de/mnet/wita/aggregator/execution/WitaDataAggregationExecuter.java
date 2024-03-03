/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 14:55:25
 */
package de.mnet.wita.aggregator.execution;

import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.StringTools;
import de.mnet.wita.aggregator.AbstractWitaDataAggregator;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Hilfsklasse fuer die Aggregation der WITA-Daten. Die Hilfsklasse wertet an Hand einer Konfiguration aus, ob ein
 * angegebener Aggregator fuer den Geschaeftsfall ueberhaupt benoetigt wird und fuehrt diesen ggf. aus. Dies ist
 * abhaengig vom UseCase, der ueber den CBVorgang bestimmt ist.
 */
public class WitaDataAggregationExecuter {

    private static final Logger LOG = Logger.getLogger(WitaDataAggregationExecuter.class);

    /**
     * Methode ueberprueft, ob die angegebene Aggregator-Klasse {@code aggregatorClass} in der aktuellen Konfiguration
     * ausgefuehrt werden darf und fuehrt sie ggf. aus. <br> Das Ergebnis wird in die Methode {@code destinationMethod}
     * des Ziel-Objekts {@code destinationObject} eingetragen. Dabei muss sichergestellt sein, dass die {@code
     * destinationMethod} ein Setter mit Angabe eines einzelnen Parameters (Ableitung von {@link MwfEntity} ist.
     *
     * @param cbVorgang         Von dem CBVorgang wird der WitaGeschaeftsfallTyp verwendet und er wird auch an den
     *                          Aggregator übergeben
     * @param aggregator        Aggregator-Objekt, dass abhaengig von der Konfiguration ausgefuehrt werden soll
     * @param destinationObject
     */
    public static <M, A extends AbstractWitaDataAggregator<M>> boolean executeAggregator(
            WitaCBVorgang cbVorgang, A aggregator, MwfEntity destinationObject) {

        boolean executed = false;
        if (executeAggregator(cbVorgang.getWitaGeschaeftsfallTyp(), aggregator)) {
            executed = true;
            M aggregatedMwfEntity = aggregator.aggregate(cbVorgang);

            Method setMethod = findCorrespondingSetterMethod(aggregator, destinationObject);
            try {
                setMethod.invoke(destinationObject, aggregatedMwfEntity);
            }
            catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new WitaDataAggregationException("Fehler beim Aufruf der setter-Methode: " + e.getMessage(), e);
            }
        }
        return executed;
    }

    /**
     * Für Aggregatoren, die einen Typen zurückliefern, der in einer Klasse mehrmals verwendet wird. z.B. String,
     * Integer
     */
    public static <M, A extends AbstractWitaDataAggregator<M>> void executeAggregator(
            WitaCBVorgang cbVorgang, A aggregator, MwfEntity destinationObject, Method setter) {

        if (executeAggregator(cbVorgang.getWitaGeschaeftsfallTyp(), aggregator)) {
            M aggregatedMwfEntity = aggregator.aggregate(cbVorgang);

            try {
                setter.invoke(destinationObject, aggregatedMwfEntity);
            }
            catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new WitaDataAggregationException("Fehler beim Aufruf der setter-Methode: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Ermittelt die (setter) Methode des Ziel-Objekts {@code destinationObject}, an die das Ergebnis des Aggregators
     * {@code aggregator} uebergeben werden soll.
     *
     * @param <M>               Typ der vom Aggregator zu erstellenden MWFEntity
     * @param <A>               Typ des Aggregators
     * @param aggregator        Aggregator-Objekt
     * @param destinationObject Ziel-Objekt, in das das vom Aggregator erstellte MWFEntity-Objekt eingetragen werden
     *                          soll
     * @return die aufzurufende Methode des Ziel-Objekts, um das vom Aggregator erstelltw MWFEntity-Objekt zu
     * uebergeben.
     */
    static <M, A extends AbstractWitaDataAggregator<M>> Method findCorrespondingSetterMethod(
            A aggregator, MwfEntity destinationObject) {

        Class<M> aggregationType = aggregator.getAggregationType();

        Method[] declaredMethods = destinationObject.getClass().getDeclaredMethods();
        Method correspondingMethod = searchForCorrespondingSetterMethod(aggregationType, declaredMethods);
        if (correspondingMethod == null) {
            Method[] allMethods = destinationObject.getClass().getMethods();
            correspondingMethod = searchForCorrespondingSetterMethod(aggregationType, allMethods);
        }

        if (correspondingMethod != null) {
            return correspondingMethod;
        }

        throw new WitaDataAggregationException(StringTools.formatString(
                "Destination method not found to set object of type {0} on class {1}",
                new Object[] { aggregationType.getName(), destinationObject.getClass().getName() }));
    }

    private static <M> Method searchForCorrespondingSetterMethod(Class<M> aggregationType, Method[] methods) {
        for (Method method : methods) {
            if (method.getName().startsWith("set") && (method.getParameterTypes() != null)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if ((paramTypes.length == 1) && (paramTypes[0] == aggregationType)) {
                    return method;
                }
            }
        }
        return null;
    }


    /**
     * Ermittelt die Konfiguration fuer den angegebenen UseCase und prueft, ob der durch den Aggregator {@code
     * aggregator} definierte Typ konfiguriert ist.
     *
     * @param geschaeftsfallTyp
     * @param aggregator
     * @return {@code true} wenn der Aggregator {@code aggregator} fuer den angegebenen UseCase konfiguriert ist.
     */
    static <M, A extends AbstractWitaDataAggregator<M>> boolean executeAggregator(GeschaeftsfallTyp geschaeftsfallTyp,
            A aggregator) {
        Collection<Class<?>> configuredAggregators = WitaDataAggregationConfig.getWitaDataAggregationConfig(
                geschaeftsfallTyp.getClazz());
        if (configuredAggregators != null) {
            for (Class<?> configuredAggregator : configuredAggregators) {
                if (configuredAggregator.isInstance(aggregator)) {
                    return true;
                }
            }
        }

        return false;
    }

}
