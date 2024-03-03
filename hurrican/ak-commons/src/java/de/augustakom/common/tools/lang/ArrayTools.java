/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2005 14:02:26
 */
package de.augustakom.common.tools.lang;

import java.lang.reflect.*;


/**
 * Tool-Klasse fuer das Arbeiten mit Arrays.
 */
public class ArrayTools {

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p> <p/> <p>The new array
     * contains the same elements of the input array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p> <p/> <p>If the input array is <code>null</code>, a new
     * one element array is returned whose component type is the same as the element.</p>
     * <p/>
     * <pre>
     * ArrayUtils.add(null, null)      = [null]
     * ArrayUtils.add(null, "a")       = ["a"]
     * ArrayUtils.add(["a"], null)     = ["a", null]
     * ArrayUtils.add(["a"], "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param array   the array to "add" the element to, may be <code>null</code>
     * @param element the object to add
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static Object[] add(Object[] array, Object element) {
        Class<?> type = (array != null ? array.getClass() : (element != null ? element.getClass() : Object.class));
        Object[] newArray = (Object[]) copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.</p> <p/> <p>The new array
     * contains the same elements of the input array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.</p> <p/> <p>If the input array is <code>null</code>, a new
     * one element array is returned whose component type is the same as the element.</p>
     * <p/>
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   the array to copy and add the element to, may be <code>null</code>
     * @param element the object to add at the last index of the new array
     * @return A new array containing the existing elements plus the new element
     * @since 2.1
     */
    public static int[] add(int[] array, int element) {
        int[] newArray = (int[]) copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * Returns a copy of the given array of size 1 greater than the argument. The last value of the array is left to the
     * default value.
     *
     * @param array                 The array to copy, must not be <code>null</code>.
     * @param newArrayComponentType If <code>array</code> is <code>null</code>, create a size 1 array of this type.
     * @return A new copy of the array of size 1 greater than the input.
     */
    private static Object copyArrayGrow1(Object array, Class<? extends Object> newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        else {
            return Array.newInstance(newArrayComponentType, 1);
        }
    }

    /**
     * Ueberprueft, ob das Array <code>arr</code> <code>null</code> oder 'leer' ist.
     *
     * @param arr zu pruefendes Array
     * @return true wenn das Array <code>null</code> oder 'leer' ist.
     */
    public static boolean isEmpty(Object[] arr) {
        return (arr == null) || (arr.length == 0);
    }

    /**
     * @see #isEmpty(Object[]) - Negation!
     */
    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }
}


