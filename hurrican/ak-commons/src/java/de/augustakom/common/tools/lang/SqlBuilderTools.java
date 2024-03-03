/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2012 12:09:12
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Tool-Klasse fuer das Erstellen von Sql Zeichenketten.
 */
public class SqlBuilderTools {

    private static final Logger LOGGER = Logger.getLogger(SqlBuilderTools.class);

    private static final String PARAM_WILDCARD = "?";

    private static void addIn(Appendable sql, int size) {
        try {
            sql.append(" IN ( ");
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(PARAM_WILDCARD);
            }
            sql.append(" ) ");
        }
        catch (IOException e) {
            LOGGER.error(e);
            // nothing to do (StringBuilder or StringBuilder catch the exception)
        }
    }

    public static void addIn(StringBuilder sql, Collection<?> inParams) {
        addIn(sql, inParams.size());
    }

}
