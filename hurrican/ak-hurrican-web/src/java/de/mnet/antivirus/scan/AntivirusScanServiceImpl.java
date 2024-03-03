/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 09:11:52
 */
package de.mnet.antivirus.scan;

import java.io.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.mnet.antivirus.scan.executor.AntivirusCheckExecutor;

public class AntivirusScanServiceImpl implements AntivirusScanService {

    @Autowired
    private AntivirusCheckExecutor antivirusChecker;

    private String scanFilePath;
    private String fileName;

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification = "Ob die Datei geloescht wird oder nicht ist egal")
    public AntivirusScanStatus scanFileStream(byte[] stream) {
        try {
            File fileToCheck;
            fileToCheck = writeFileToHdd(stream);

            AntivirusScanStatus status = antivirusChecker.scanFile(fileToCheck);

            if (fileToCheck != null) {
                fileToCheck.delete();
            }

            return status;
        }
        catch (IOException e) {
            throw new RuntimeException("Fehler beim Virenscannen", e);
        }
    }

    private File writeFileToHdd(byte[] stream) throws IOException {
        checkFileAndDir();
        File fileToCheck = new File(scanFilePath + fileName);
        OutputStream out = new FileOutputStream(fileToCheck);
        try {
            out.write(stream);
        }
        finally {
            out.close();
        }
        return fileToCheck;
    }

    private void checkFileAndDir() {
        if (fileName == null) {
            fileName = UUID.randomUUID().toString();
        }
        File path = new File(scanFilePath);
        if (!path.exists()) {
            boolean dirCreated = path.mkdirs();
            if (!dirCreated) {
                throw new RuntimeException("Verzeichnis " + scanFilePath + " konnte nicht erstellt werden");
            }
        }
    }

    // fuer Testzwecke
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setAntivirusChecker(AntivirusCheckExecutor antivirusChecker) {
        this.antivirusChecker = antivirusChecker;
    }

    @Value("${antivirus.scan.dir}")
    public void setScanFilePath(String filePath) {
        this.scanFilePath = filePath;
    }
}


