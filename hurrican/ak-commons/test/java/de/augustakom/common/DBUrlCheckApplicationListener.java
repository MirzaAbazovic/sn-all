/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.14
 */
package de.augustakom.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Used during unit/service/acceptance testing for detecting when tests are configured to use the production database.
 * This is a safety net that verifies that the JDBC Url being used does <b>not</b> contain the word <b>PROD</b> (case
 * insensitive). <br /> To add new jdbc urls to be checked just add a new field to the class with a {@link
 * org.springframework.beans.factory.annotation.Value} annotation containing the property name that stores the url and
 * add this field to the {@code checkDbUrls} array. <br /> Make sure to add ':NOT_SET' after the property name as this
 * ensures the default value 'NOT_SET' will be used when the property does not exist (not all properties are set in all
 * test executions). Otherwise an IllegalArgumentException is thrown. <br /> It might not be immediately obvious but
 * this check also verifies the JDBC URLs set in the AUTHENTICATION.DB table. The urls stored here are also injected as
 * Properties into the spring context and as a result are check here as well.
 */
public class DBUrlCheckApplicationListener implements ApplicationListener {

    @Value("${hurrican.cc.jdbc.url:NOT_SET}")
    private String ccDbUrl;

    @Value("${hurrican.monline.jdbc.url:NOT_SET}")
    private String monlineDbUrl;

    @Value("${hurrican.tal.jdbc.url:NOT_SET}")
    private String talDbUrl;

    @Value("${hurrican.billing.jdbc.url:NOT_SET}")
    private String billingDbUrl;

    @Value("${hurrican.reporting.jdbc.url:NOT_SET}")
    private String reportingDbUrl;

    @Value("${hurrican.scheduler.jdbc.url:NOT_SET}")
    private String schedulerDbUrl;

    @Value("${authentication.jdbc.url:NOT_SET}")
    private String authenticationDbUrl;

    @Value("${db.hurrican.jdbc.url:NOT_SET}")
    private String dbUrl;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            final String[] checkDbUrls = new String[] {
                    ccDbUrl,
                    monlineDbUrl,
                    talDbUrl,
                    billingDbUrl,
                    reportingDbUrl,
                    schedulerDbUrl,
                    authenticationDbUrl,
                    dbUrl,
            };
            final String checkUrlForString = "prod";

            for (String checkDbUrl : checkDbUrls) {
                if (checkDbUrl != null && checkDbUrl.toLowerCase().indexOf(checkUrlForString) > -1) {
                    String errMsg = "Detected String '%s' in DB Url '%s'! " +
                            "%nStopping test execution and exiting the VM. " +
                            "%nCheck ENV properties and Database configuration (Authentication.DB Table) to make sure " +
                            "that the PROD database is NOT being used.";
                    System.err.println(String.format(errMsg, checkUrlForString, checkDbUrl));
                    System.exit(-1);
                }
            }
        }
    }
}
