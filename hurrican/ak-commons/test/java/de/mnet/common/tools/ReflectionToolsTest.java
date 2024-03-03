/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 11:34:24
 */
package de.mnet.common.tools;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class ReflectionToolsTest extends BaseTest {

    private static class GenericClass<T> {
    }

    private static class ConcreteSubclass extends GenericClass<Integer> {
    }

    private static class DeepSubclass extends ConcreteSubclass {
    }

    private static class GenericClassWith2Parameters<S, T> {
    }

    private static class SubclassWithOneConcreteParameter<T> extends GenericClassWith2Parameters<T, String> {
    }

    private static class SubclassWithTwoConcreteParameter extends SubclassWithOneConcreteParameter<Integer> {
    }

    public void getTypeArgumentForDirectSubclass() {
        assertEquals(ReflectionTools.getTypeArgument(GenericClass.class, ConcreteSubclass.class), Integer.class);
    }

    public void getTypeArgumentForDeepSubclass() {
        assertEquals(ReflectionTools.getTypeArgument(GenericClass.class, DeepSubclass.class), Integer.class);
    }

    public void getGenericTypeArgumentsForDirectSubclass() {
        assertEquals(ReflectionTools.getTypeArguments(GenericClass.class, ConcreteSubclass.class),
                ImmutableList.of(Integer.class));
    }

    public void getGenericTypeArgumentsForDeepSubclass() {
        assertEquals(ReflectionTools.getTypeArguments(GenericClass.class, DeepSubclass.class),
                ImmutableList.of(Integer.class));
    }

    public void getGenericTypeArgumentsFor2Parameters() {
        List<Class<?>> genericParametersOfSuperclass = ReflectionTools.getTypeArguments(
                GenericClassWith2Parameters.class,
                SubclassWithTwoConcreteParameter.class);
        assertEquals(genericParametersOfSuperclass, ImmutableList.of(Integer.class, String.class));
    }


}


