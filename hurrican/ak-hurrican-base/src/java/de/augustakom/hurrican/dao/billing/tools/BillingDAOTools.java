/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.09.2006 08:04:59
 */
package de.augustakom.hurrican.dao.billing.tools;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.HurricanConstants;


/**
 * Hilfs-Klasse fuer Hibernate DAO-Implementierungen des Billing-Bereichs.
 *
 *
 */
public class BillingDAOTools {

    private static String schemaName = null;

    /**
     * Gibt den System-Property Wert mit dem definierten Datenbankschema fuer das Billing-System zurueck. <br> An den
     * Wert wird zusaetzlich ein Punkt (.) angehaengt. Dies ist notwendig, da bei JDBC-Abfragen der Schema-Name vor den
     * Tabellennamen gestellt werden muss, z.B. MNETPROD.KUNDE (= SCHEMA.TABLE). <br> Sollte fuer die DB kein Schema
     * hinterlegt sein, wird ein Leerstring zurueck geliefert. Dadurch ist es moeglich, die Queries sowohl auf Oracle
     * als auch auf MySQL durchzufuehren.
     *
     * @return definierter Schema-Name mit zusaetzlichem Punkt.
     *
     */
    public static String getSchemaName() {
        if (schemaName == null) {
            schemaName = System.getProperty(HurricanConstants.SYSTEM_PROPERTY_BILLING_SCHEMA);
            if (StringUtils.isNotBlank(schemaName)) {
                schemaName += ".";
            }
            else {
                schemaName = "";
            }
        }
        return schemaName;
    }

}


