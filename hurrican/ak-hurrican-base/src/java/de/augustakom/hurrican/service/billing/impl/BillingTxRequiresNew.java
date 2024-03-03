/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2015
 */
package de.augustakom.hurrican.service.billing.impl;

import java.lang.annotation.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction annotation to be used when working with the BILLING schema. The REQUIRES_NEW propagation
 * level is configured. The transaction will be rolled back when any exception is thrown (not
 * just runtime exceptions).
 * Created by maherma on 13.02.2015.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional(
        propagation = Propagation.REQUIRES_NEW,
        value = "billing.hibernateTxManager",
        rollbackFor = {Exception.class}
)
public @interface BillingTxRequiresNew {
}
