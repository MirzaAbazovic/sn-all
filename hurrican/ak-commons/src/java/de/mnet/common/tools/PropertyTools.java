/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 11:26:14
 */
package de.mnet.common.tools;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Hilfsklasse, die die Apache Common PropertyUtils kapselt.
 */
public class PropertyTools {

    public static Object getNestedPropertyIgnoreNestedNulls(Object item, String nestedProperty) {
        return getNestedProperty(item, nestedProperty, true);
    }

    public static Object getNestedProperty(Object item, String nestedProperty, boolean ignoreNestedNullException) {
        try {
            return PropertyUtils.getNestedProperty(item, nestedProperty);
        }
        catch (NestedNullException e) {
            if (ignoreNestedNullException) {
                return null;
            }
            else {
                throw new RuntimeException(String.format("Access to nested property <%s> failed with: %s", nestedProperty, e.getMessage()), e);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Access to nested property <%s> failed with: %s", nestedProperty, e.getMessage()), e);
        }
    }

}


