/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2007 10:21:16
 */
package de.augustakom.common.tools.lang;

import java.lang.reflect.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;


/**
 * Hilfsklasse, um Beans zu manipulieren (enthaelt Methode, die die Apache BeanUtils nicht bieten).
 *
 *
 */
public class BeanTools {

    private static final Logger LOGGER = Logger.getLogger(BeanTools.class);

    /**
     * Kopiert die Properties von <code>src</code> nach <code>dest</code>. <br> Es werden dabei nur die Properties
     * beruecksichtigt, die in dem Array <code>copyProperties</code> angegeben sind.
     *
     * @param dest           Ziel-Bean.
     * @param src            Ursprungs-Bean.
     * @param copyProperties Property-Namen, die kopiert werden sollen.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NullPointerException
     * @throws NoSuchMethodException
     *
     */
    public static void copyProperties(Object dest, Object src, String[] copyProperties) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (copyProperties == null) {
            throw new NullPointerException("no properties to copy defined!");
        }

        for (String property : copyProperties) {
            Object value = PropertyUtils.getProperty(src, property);
            PropertyUtils.setProperty(dest, property, value);
        }
    }


    /**
     * Example:
     * <p/>
     * <code> class NewSuper { private Mario mario = new Mario(); }
     * <p/>
     * class Mario { private String wii = "wii"; }
     * <p/>
     * NewSuper newSuper = new NewSuper(); String wii = BeanTools.getNestedProperty(newSuper, "mario.wii"); </code>
     *
     * @return The property, or {@code null} if an error occurred
     */
    public static Object getNestedProperty(Object object, String propertyPath) {
        String[] pathParts = propertyPath.split("\\.");
        for (int i = 0; (i < pathParts.length) && (object != null); ++i) {
            object = getProperty(object, pathParts[i]);
        }
        return object;
    }


    /**
     * Returns the property from the given object. Searches through super classes.
     *
     * @return The property, or {@code null} if an error occurred.
     */
    public static Object getProperty(Object object, String property) {
        try {
            Field field;
            Class<?> clazz = object.getClass();
            while (clazz != null) {
                field = getField(property, clazz);
                if (field != null) {
                    if (!Modifier.isPublic(field.getModifiers()) ||
                            !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
                        field.setAccessible(true);
                    }
                    return field.get(object);
                }
                clazz = clazz.getSuperclass();
            }
            LOGGER.warn("getProperty() - Property " + property + " from bean of class " + object.getClass().getName() + " not found!");
            return null;
        }
        catch (Exception e) {
            LOGGER.warn("getProperty() - Error trying to get property " + property + " from bean of class " + object.getClass().getName(), e);
            return null;
        }
    }

    private static Field getField(String property, Class<?> clazz) {
        Field field;
        try {
            field = clazz.getDeclaredField(property);
        }
        catch (NoSuchFieldException e) {
            field = null;
        }
        return field;
    }
}


