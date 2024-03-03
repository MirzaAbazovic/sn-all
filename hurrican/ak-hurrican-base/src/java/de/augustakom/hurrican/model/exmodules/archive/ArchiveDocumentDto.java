/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2011 12:10:07
 */
package de.augustakom.hurrican.model.exmodules.archive;

import java.io.*;
import com.google.common.base.Preconditions;

import de.augustakom.common.tools.file.FileTools;

/**
 * Klasse stellt eine Abbildung eines Dokuments dar, das per AIL aus ScanView ermittelt wurde.
 */
public class ArchiveDocumentDto implements Serializable {

    private static final long serialVersionUID = 6248726241856528941L;

    private String key;
    private String label;
    private String mimeType;
    private String fileExtension;
    private byte[] stream;
    private String vertragsNr;
    private ArchiveDocumentType documentType;

    private Boolean selected;

    /**
     * Erzeugt aus dem byte[] ein File und legt es unter den angegebenem Verzeichnis und Namen ab.
     *
     * @param directory    Ziel-Verzeichnis fuer die Datei
     * @param fileName     gewuenschter Dateiname
     * @param deleteOnExit Flag gibt an, ob die Datei beim Beenden der VM automatisch geloescht werden soll ({@code
     *                     true} ) oder nicht ({@code false}).
     * @return das generierte File
     * @throws IOException wenn beim Erzeugen des Files ein Fehler auftritt
     */
    public File convertToFile(String directory, String fileName, boolean deleteOnExit) throws IOException {
        File createdFile = FileTools.createFile(getStream(), directory, fileName);
        Preconditions.checkNotNull(createdFile, "Die Datei konnte nicht erzeugt werden!");
        if (deleteOnExit) {
            createdFile.deleteOnExit();
        }
        return createdFile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public byte[] getStream() {
        return stream;
    }

    public void setStream(byte[] stream) {
        this.stream = stream;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getVertragsNr() {
        return vertragsNr;
    }

    public void setVertragsNr(String vertragsNr) {
        this.vertragsNr = vertragsNr;
    }

    public ArchiveDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(ArchiveDocumentType documentType) {
        this.documentType = documentType;
    }
}
