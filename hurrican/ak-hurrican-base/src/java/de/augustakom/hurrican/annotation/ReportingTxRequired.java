package de.augustakom.hurrican.annotation;

import java.lang.annotation.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Annotation fuer transactionale Services fuer die Reporting-Datasource
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@Transactional(rollbackFor = { Exception.class }, value = "reporting.hibernateTxManager")
public @interface ReportingTxRequired {
    // annotation for transactional Reporting services
}
