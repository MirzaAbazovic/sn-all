/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2005 13:58:16
 */
package de.augustakom.hurrican;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.common.AKDefaultConstants;
import de.augustakom.common.tools.lang.DesEncrypter;

/**
 * Hilfsklasse, um Datenbank-Informationen in die System-Properties zu schreiben.
 *
 *
 */
public class HurricanSystemPropertyWriter {

    private static final Logger LOGGER = Logger.getLogger(HurricanSystemPropertyWriter.class);

    /**
     * Liest die Datenbank-Informationen aus der List und der Map aus und speichert sie als System-Properties.
     *
     * @param accounts  Liste mit den Accounts zu den Datenbanken
     * @param databases Map mit den verfuegbaren Datenbanken.
     * @throws Exception
     */
    public static void readDBInfosIntoSystem(List<AKAccount> accounts, Map<Long, AKDb> databases) throws Exception {
        if ((accounts == null) || (accounts.isEmpty()) || (databases == null) || (databases.isEmpty())) {
            throw new Exception("Der User ist nicht korrekt konfiguriert (keine Datenbanken zugeordnet!).");
        }

        String prefix = HurricanConstants.HURRICAN_NAME.toLowerCase() + ".";
        for (int i = 0; i < accounts.size(); i++) {
            AKAccount account = accounts.get(i);

            AKDb db = databases.get(account.getDbId());
            if (db != null) {
                setSystemPropertiesForDb(account, db, prefix);
            }
            else {
                LOGGER.debug("DB-Definition fuer Account " + account.getName() + " nicht gefunden!");
                throw new Exception("Die DB-Definitionen konnten nicht ermittelt werden. \n" +
                        "Bitte wenden Sie sich an IT-Services.");
            }
        }

        // Set dummy values for Databases to make the ApplicationContext to start up
        AKAccount dummyAccount = new AKAccount();
        dummyAccount.setAccountUser("dummy.user");
        dummyAccount.setAccountPassword(DesEncrypter.getInstance().encrypt("dummy.password"));
        dummyAccount.setMaxActive(0);
        dummyAccount.setMaxIdle(0);
        for (AKDb db : databases.values()) {
            String tmp = prefix + db.getName();
            if (System.getProperty(tmp + AKDefaultConstants.JDBC_USER_SUFFIX) == null) {
                LOGGER.info("No account for database " + db.getName());
                LOGGER.info("Using dummy account " + db.getName());
                setSystemPropertiesForDb(dummyAccount, db, prefix);

            }

        }
    }

    public static void setSystemPropertiesForDb(AKAccount account, AKDb db, String prefix) {
        String tmp = prefix + db.getName();

        String pwd = account.getAccountPassword();
        try {
            pwd = DesEncrypter.getInstance().decrypt(pwd);
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        String driverProperty = tmp + AKDefaultConstants.JDBC_DRIVER_SUFFIX;
        String urlProperty = tmp + AKDefaultConstants.JDBC_URL_SUFFIX;
        String userProperty = tmp + AKDefaultConstants.JDBC_USER_SUFFIX;
        String pwProperty = tmp + AKDefaultConstants.JDBC_PASSWORD_SUFFIX;
        String maxActiveProperty = tmp + AKDefaultConstants.JDBC_MAX_ACTIVE_SUFFIX;
        String maxIdleProperty = tmp + AKDefaultConstants.JDBC_MAX_IDLE_SUFFIX;
        String schemaProperty = tmp + AKDefaultConstants.JDBC_SCHEMA_SUFFIX;
        String hibDialectProperty = tmp + AKDefaultConstants.HIBERNATE_DIALECT_SUFFIX;

        System.setProperty(driverProperty, db.getDriver());
        System.setProperty(urlProperty, db.getUrl());
        System.setProperty(hibDialectProperty, db.getHibernateDialect());
        if (StringUtils.isNotBlank(db.getSchema())) {
            System.setProperty(schemaProperty, db.getSchema());
        }

        System.setProperty(userProperty, account.getAccountUser());
        System.setProperty(pwProperty, pwd);
        System.setProperty(maxActiveProperty, "" + account.getMaxActive());
        System.setProperty(maxIdleProperty, "" + account.getMaxIdle());
    }

}
