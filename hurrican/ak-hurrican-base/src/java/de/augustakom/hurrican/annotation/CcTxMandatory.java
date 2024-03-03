package de.augustakom.hurrican.annotation;

import java.lang.annotation.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Annotation fuer transactionale Services fuer die CC-Datasource, mit MANDATORY Propogation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@Transactional(
        rollbackFor = { Exception.class },
        value = "cc.hibernateTxManager",
        propagation = Propagation.MANDATORY
)
public @interface CcTxMandatory {
    // annotation for transactional CC services
}


