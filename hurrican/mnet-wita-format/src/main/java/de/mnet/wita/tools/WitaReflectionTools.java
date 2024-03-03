/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2012 17:02:16
 */
package de.mnet.wita.tools;

import static org.apache.commons.beanutils.PropertyUtils.*;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

/**
 * Klasse fuer ReflectionTools, die im MnetWitaFormat und in Hurrican-Wita benutzt wird
 */
public class WitaReflectionTools {

    public static class FieldWithPath<T> {

        public FieldWithPath(T field, String path) {
            this.field = field;
            this.path = path;
        }

        public T field;
        public String path;

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((field == null) ? 0 : field.hashCode());
            return result;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            FieldWithPath<?> other = (FieldWithPath<?>) obj;
            if (field == null) {
                if (other.field != null) {
                    return false;
                }
            }
            else if (!field.equals(other.field)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Sucht rekursiv alle Felder vom einer bestimmten Klasse (filterClass), wobei die Tiefensuche nur weitergeht, wenn
     * das Feld vom Typ descendClass oder eine Collection ist.
     * <p/>
     * Wenn das root-Objekt vom Typ filterClass ist, so ist es auch im Resultat
     *
     * @param root         Das Objekt, von dem die Suche beginnt
     * @param descendClass
     * @param filterClass
     * @return Die Menge der Inhalte der Felder mit der Pfadangabe
     */
    public static <S, T> Set<FieldWithPath<S>> filterFields(T root, Class<T> descendClass, Class<S> filterClass)
            throws IllegalArgumentException,
            IllegalAccessException,
            InvocationTargetException {
        return filterFields(root, descendClass, filterClass, "", Sets.<T>newHashSet());
    }

    private static <T, S> Set<FieldWithPath<S>> filterFields(T root, Class<T> descendClass, Class<S> filterClass,
            String fieldPath,
            Set<T> visitedEntities)
            throws IllegalArgumentException,
            IllegalAccessException,
            InvocationTargetException {
        Set<FieldWithPath<S>> methods = Sets.newHashSet();

        if (visitedEntities.contains(root)) {
            return methods;
        }

        if (filterClass.isAssignableFrom(root.getClass())) {
            @SuppressWarnings("unchecked")
            S rootS = (S) root;
            methods.add(new FieldWithPath<S>(rootS, fieldPath));
        }
        visitedEntities.add(root);
        if (StringUtils.isNotBlank(fieldPath)) {
            fieldPath += ".";
        }
        for (PropertyDescriptor propertyDescriptor : getPropertyDescriptors(root)) {
            Method getter = propertyDescriptor.getReadMethod();
            String newAttributePath = fieldPath + propertyDescriptor.getName();
            if (getter == null) {
                throw new RuntimeException("No getter found for attribute path " + newAttributePath);
            }

            Object invoke = getter.invoke(root);
            if (invoke != null) {
                if (propertyDescriptor.getPropertyType().isAssignableFrom(filterClass)) {
                    @SuppressWarnings("unchecked")
                    S invokeS = (S) invoke;
                    methods.add(new FieldWithPath<S>(invokeS, newAttributePath));
                }
                if (descendClass.isAssignableFrom(invoke.getClass())) {
                    @SuppressWarnings("unchecked")
                    T invokeT = (T) invoke;
                    methods.addAll(filterFields(invokeT, descendClass, filterClass, newAttributePath, visitedEntities));
                }
                else if (invoke instanceof Collection) {
                    Collection<?> coll = (Collection<?>) invoke;
                    int idx = 0;
                    for (Object object : coll) {
                        if (object != null && descendClass.isAssignableFrom(object.getClass())) {
                            @SuppressWarnings("unchecked")
                            T objectT = (T) object;
                            methods.addAll(filterFields(objectT, descendClass, filterClass, newAttributePath + "["
                                    + idx
                                    + "]", visitedEntities));
                        }
                        idx++;
                    }
                }
            }
        }
        return methods;
    }

}


