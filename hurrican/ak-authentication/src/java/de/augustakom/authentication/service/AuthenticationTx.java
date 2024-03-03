package de.augustakom.authentication.service;

import java.lang.annotation.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction annotation to be used when working with the HURRICAN.AUTHENTICATION schema. The default propagation
 * level (REQUIRED) is configured by default.
 * Created by maherma on 29.01.2015.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional(propagation = Propagation.REQUIRED, value = "authentication.transactionManager")
public @interface AuthenticationTx {
}
