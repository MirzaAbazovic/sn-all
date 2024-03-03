/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 15:28:18
 */
package de.mnet.migration.common.util.excel;

import java.lang.annotation.*;


/**
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
    Class<? extends CellMapper> value();
}
