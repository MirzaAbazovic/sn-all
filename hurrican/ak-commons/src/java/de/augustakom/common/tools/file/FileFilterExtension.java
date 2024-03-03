/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2005 10:12:38
 */
package de.augustakom.common.tools.file;


import java.io.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang.StringUtils;


/**
 * FileFilter fuer einen FileChooser oder die listFiles-Methode der File-Klasse, um nach Dateien mit einer best. Endung
 * zu suchen.
 *
 *
 */
public class FileFilterExtension extends FileFilter implements java.io.FileFilter {

    public static final String[] FILE_EXTENSION_EXCEL = new String[] { ".xls" };

    public static final String[] FILE_EXTENSION_EXCEL_ALL = new String[] { ".xls", ".xlsx" };

    private String[] extension;

    /**
     * Konstruktor mit Angabe der Extension, nach der gefiltert werden soll.
     *
     * @param extension Angabe der Extension (z.B. '.xls')
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Extension wird nicht veraendert!")
    public FileFilterExtension(String[] extension) {
        this.extension = extension;
    }

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String filename = f.getName();
        return StringUtils.endsWithAny(filename, extension);
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        StringBuilder str = new StringBuilder();
        for (String ext : extension) {
            if (str.length() > 0) {
                str.append(", ");
            }
            if (ext.startsWith(".")) {
                str.append("*");
            }
            str.append(ext);

        }
        return str.toString();
    }

}


