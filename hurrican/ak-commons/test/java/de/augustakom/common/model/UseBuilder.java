/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 14:52:51
 */
package de.augustakom.common.model;

import java.lang.annotation.*;


/**
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseBuilder {
    Class<? extends EntityBuilder<?, ?>> value();
}
