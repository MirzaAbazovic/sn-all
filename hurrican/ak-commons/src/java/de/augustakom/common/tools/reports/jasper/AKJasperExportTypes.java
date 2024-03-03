/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 07:52:01
 */
package de.augustakom.common.tools.reports.jasper;


/**
 * Interface zur Definition von Keys fuer die unterschiedlichen Export-Typen von Jasper.
 *
 *
 */
public interface AKJasperExportTypes {

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>Print</code>.
     */
    public static final int EXPORT_TYPE_PRINT = 0;

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>PDF</code>.
     */
    public static final int EXPORT_TYPE_PDF = 1;

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>HTML</code>.
     */
    public static final int EXPORT_TYPE_HTML = 2;

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>Excel</code>.
     */
    public static final int EXPORT_TYPE_EXCEL = 3;

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>CSV</code>.
     */
    public static final int EXPORT_TYPE_CSV = 4;

    /**
     * Konstante zur Kennzeichnung des Report-Typs <code>XML</code>.
     */
    public static final int EXPORT_TYPE_XML = 5;

}


