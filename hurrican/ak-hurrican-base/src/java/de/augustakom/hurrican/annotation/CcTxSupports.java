/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.02.2015 15:10:56
 */
package de.augustakom.hurrican.annotation;

import java.lang.annotation.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Annotation fuer transactionale Services fuer die CC-Datasource
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@Transactional(
        rollbackFor = { Exception.class },
        value = "cc.hibernateTxManager",
        propagation = Propagation.SUPPORTS
)
public @interface CcTxSupports {
    // annotation for transactional CC services
}


