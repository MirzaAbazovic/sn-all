/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2016
 */
package de.augustakom.common;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Die Klasse ueberprueft die Verbindung zur Taifun-Datenbank.
 */
@Component
public class TaifunDatabaseConnectionsCheck {
    private static final Logger LOGGER = Logger.getLogger(TaifunDatabaseConnectionsCheck.class);

    @Value("${taifun.db.user:NOT_SET}")
    private String billingDbUser;

    @Value("${hurrican.billing.schema:NOT_SET}")
    private String schemaNameInAuthDb;

    private boolean validationCompleted = false;

    public void validateTaifunConnections() {
        if (validationCompleted) {
            return;
        }

        boolean validationPossible = true;
        if ("NOT_SET".equals(billingDbUser)) {
            LOGGER.info("Das Property 'taifun.db.user' ist nicht gesetzt");
            validationPossible = false;
        }
        if ("NOT_SET".equals(schemaNameInAuthDb)) {
            LOGGER.info("Das Property 'hurrican.billing.schema' ist nicht gesetzt. "
                    + "Das Property wird ueber die DB-Tabelle in der Authentication-DB gelesen");
            validationPossible = false;
        }

        try {
            if (validationPossible) {
                StringBuilder sb = new StringBuilder();
                String EOL = System.getProperty("line.separator");
                sb.append("taifun.db.user = ").append(billingDbUser).append(EOL);
                sb.append("hurrican.billing.schema = ").append(schemaNameInAuthDb);
                if (schemaNameInAuthDb.equals(billingDbUser)) {
                    String msg = String.format("TAIFUN Configuration Check: VALUES MATCH%s%s", EOL, sb.toString());
                    LOGGER.debug(msg);
                }
                else {
                    String msg = String.format("TAIFUN Configuration Check: VALUES DO NOT MATCH !!!!!%s%s", EOL, sb.toString());
                    LOGGER.warn(msg);
                }
            }
        }
        finally {
            validationCompleted = true;
        }
    }
}

