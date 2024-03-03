/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2011 09:42:38
 */
package de.mnet.migration.common.dao;

import java.util.*;

/**
 * Ein DataLoader der für Tests verwendet werden kann, um bestimmte Daten an den Transformator zu geben. Nimmt eine
 * Collection vom Typ T und gibt diese dann bei Aufruf von getSourceData zurück.
 *
 * @param <T> Der Typ des DataLoaders
 *
 */
public class TestDataLoader<T> implements DataLoader<T> {
    public static <S> TestDataLoader<S> of(Collection<S> testData) {
        return new TestDataLoader<S>(testData);
    }

    public static <S> TestDataLoader<S> of(S... testData) {
        return new TestDataLoader<S>(Arrays.asList(testData));
    }

    private List<T> testData = new ArrayList<T>();

    public TestDataLoader(Collection<T> testData) {
        this.testData.addAll(testData);
    }

    @Override
    public List<T> getSourceData() {
        return Collections.unmodifiableList(testData);
    }

    @Override
    public String getSourceObjectName() {
        return "Test Data";
    }

}
