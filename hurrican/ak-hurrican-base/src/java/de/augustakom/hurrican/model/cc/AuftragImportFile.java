/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2005 10:21:55
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import org.apache.commons.lang.SystemUtils;

import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell, um die importierten Auftrags-Dateien in der Datenbank zu speichern.
 *
 *
 */
public class AuftragImportFile extends AbstractCCIDModel {

    /**
     * Konstante fuer 'fileType' - definiert ein XML-File
     */
    public static final Short FILE_TYPE_XML = Short.valueOf((short) 1);
    /**
     * Konstante fuer 'fileType' - definiert ein TXT-File
     */
    public static final Short FILE_TYPE_TXT = Short.valueOf((short) 2);
    /**
     * Konstante fuer 'fileType' - definiert ein TXT-File mobile
     */
    public static final Short FILE_TYPE_MOBILE_TXT = Short.valueOf((short) 3);

    private Long auftragImportId = null;
    private String origFileName = null;
    private Short fileType = null;
    private byte[] fileStream = null;

    /**
     * Ermittelt, um welchen File-Typ es sich bei der Extension 'ext' handelt.
     *
     * @param ext
     * @return
     */
    public Short getFileType(String ext) {
        if (ext != null) {
            if (ext.equalsIgnoreCase("xml")) {
                return FILE_TYPE_XML;
            }
            else if (ext.equalsIgnoreCase("txt")) {
                return FILE_TYPE_TXT;
            }
            else if (ext.equalsIgnoreCase("mf.txt")) {
                return FILE_TYPE_MOBILE_TXT;
            }
        }
        return null;
    }

    /**
     * Uebergibt das zu speichernde File. <br> Das File wird in ein byte[] umgewandelt.
     *
     * @param file
     * @throws IOException
     */
    public void setFile(File file) throws IOException {
        setFileStream(FileTools.convertToByteArray(file));
    }

    /**
     * Erzeugt aus dem FileStream ein persistentes File.
     *
     * @param parent Pfad fuer das File
     * @param file   Name fuer das File
     * @throws IOException
     */
    public File writeFile(String parent, String file) throws IOException {
        return FileTools.createFile(getFileStream(), parent, file);
    }

    /**
     * Erzeugt aus dem FileStream ein temporaeres(!) File. <br> Das File wird im User-Home Verzeichnis unter einem
     * zufaelligen Namen abgelegt. <br> Die Datei wird ausserdem mit 'deleteOnExit' markiert.
     *
     * @return
     * @throws IOException
     */
    public File writeTmpFile() throws IOException {
        StringBuilder fileName = new StringBuilder("ai_");
        fileName.append(System.currentTimeMillis());
        if (NumberTools.equal(getFileType(), FILE_TYPE_XML)) {
            fileName.append(".xml");
        }
        else if (NumberTools.equal(getFileType(), FILE_TYPE_TXT)) {
            fileName.append(".txt");
        }
        else if (NumberTools.equal(getFileType(), FILE_TYPE_MOBILE_TXT)) {
            fileName.append(".mf.txt");
        }

        File file = writeFile(SystemUtils.USER_HOME, fileName.toString());
        if (file != null) {
            file.deleteOnExit();
        }
        return file;
    }

    /**
     * @return Returns the auftragImportId.
     */
    public Long getAuftragImportId() {
        return auftragImportId;
    }

    /**
     * @param auftragImportId The auftragImportId to set.
     */
    public void setAuftragImportId(Long auftragImportId) {
        this.auftragImportId = auftragImportId;
    }

    /**
     * @return Returns the fileType.
     */
    public Short getFileType() {
        return fileType;
    }

    /**
     * @param fileType The fileType to set.
     */
    public void setFileType(Short fileType) {
        this.fileType = fileType;
    }

    /**
     * @return Returns the fileStream.
     */
    public byte[] getFileStream() {
        return fileStream;
    }

    /**
     * @param fileStream The fileStream to set.
     */
    public void setFileStream(byte[] fileStream) {
        this.fileStream = fileStream;
    }

    /**
     * @return Returns the origFileName.
     */
    public String getOrigFileName() {
        return origFileName;
    }

    /**
     * @param origFileName The origFileName to set.
     */
    public void setOrigFileName(String origFileName) {
        this.origFileName = origFileName;
    }

}


