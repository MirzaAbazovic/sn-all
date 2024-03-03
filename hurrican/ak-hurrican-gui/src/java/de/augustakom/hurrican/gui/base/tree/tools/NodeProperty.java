/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2009 15:32:29
 */
package de.augustakom.hurrican.gui.base.tree.tools;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BeanTools;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;


/**
 * Immutable class
 *
 *
 */
public final class NodeProperty<T> {
    private static final Logger LOGGER = Logger.getLogger(NodeProperty.class);

    private final Class<T> propertyType;
    private final String propertyPath;
    private final String name;
    private final Comparator<?> comparator;


    /**
     * Private Constructor - use factory methods
     */
    private NodeProperty(Class<T> propertyType, String propertyPath, String name, Comparator<?> comparator) {
        this.propertyType = propertyType;
        this.propertyPath = propertyPath;
        this.name = name;
        this.comparator = comparator;
    }

    /**
     * Factory method which can be used if the Property implements Comparable
     *
     * @return A new node property
     */
    public static <X extends Comparable<?>> NodeProperty<X> create(Class<X> propertyType,
            String propertyPath, String name) {
        return new NodeProperty<X>(propertyType, propertyPath, name, null);
    }

    /**
     * Factory method for non-Comparable properties which need a Comparator
     *
     * @return A new node property
     */
    public static <X> NodeProperty<X> create(Class<X> propertyType, String propertyPath,
            String name, Comparator<X> comparator) {
        return new NodeProperty<X>(propertyType, propertyPath, name, comparator);
    }


    /**
     * Try to get the nested property from the given node
     *
     * @return The property, or {@code null} if anything goes wrong
     */
    public T getFrom(DynamicTreeNode node) {
        try {
            @SuppressWarnings("unchecked")
            T property = (T) BeanTools.getNestedProperty(node, propertyPath);
            return property;
        }
        catch (Exception e) {
            LOGGER.warn("getFrom() - exception while trying to get field " + this.toString() + " from " + node.getClass().getSimpleName(), e);
            return null;
        }
    }


    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public String getName() {
        return name;
    }

    /**
     * Kann {@code null} sein.
     */
    public Comparator<?> getComparator() {
        return comparator;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NodeProperty [");
        if (propertyPath != null) {
            builder.append("propertyPath=");
            builder.append(propertyPath);
            builder.append(", ");
        }
        if (propertyType != null) {
            builder.append("propertyType=");
            builder.append(propertyType);
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

}
