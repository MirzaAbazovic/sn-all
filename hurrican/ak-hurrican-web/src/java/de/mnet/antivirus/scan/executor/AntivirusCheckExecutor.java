/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 13:50:19
 */
package de.mnet.antivirus.scan.executor;

import java.io.*;

import de.mnet.antivirus.scan.AntivirusScanService.AntivirusScanStatus;

/**
 * Start Antivirus-Scan mit
 */
public interface AntivirusCheckExecutor {

    /**
     * Triggert Antivirus-Scan
     *
     * @param fileToCheck wird geprueft
     * @throws RuntimeException on Error calling Antivirus-Scanner
     */
    public AntivirusScanStatus scanFile(File fileToCheck);
}


