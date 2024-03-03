/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 15:28:18
 */
package de.mnet.migration.common.util.csv;

import java.lang.annotation.*;

import de.mnet.migration.common.util.ColumnName;

/**
 * Ueberschreibt Feldnamen und {@link ColumnName}
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnIndex {

    /**
     * index starting with 0
     */
    int value();
}
