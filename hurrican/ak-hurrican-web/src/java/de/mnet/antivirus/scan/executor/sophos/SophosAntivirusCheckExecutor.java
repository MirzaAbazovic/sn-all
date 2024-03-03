/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.2011 13:49:04
 */
package de.mnet.antivirus.scan.executor.sophos;

import java.io.*;
import org.apache.log4j.Logger;

import de.mnet.antivirus.scan.AntivirusScanService.AntivirusScanStatus;
import de.mnet.antivirus.scan.executor.AntivirusCheckExecutor;

/**
 * Implementiert Antiviruscheck fuer Sophos
 */
public class SophosAntivirusCheckExecutor implements AntivirusCheckExecutor {

    private static final Logger LOGGER = Logger.getLogger(SophosAntivirusCheckExecutor.class);

    private static final String SOPHOS_COMMAND = "savscan";

    // 0 Wenn keine Fehler aufgetreten sind und keine Viren/Spyware entdeckt wurden.
    private static final int SOPHOS_EVERYTHING_OK = 0;
    // 1 Wenn die Ausführung unterbrochen wurde.
    private static final int SOPHOS_CANCELED = 1;
    // 2 Wenn ein Fehler entdeckt wurde, der die weitere Ausführung verhindert.
    private static final int SOPHOS_ERROR = 2;
    // 3 Wenn Viren/Spyware oder Virenfragmente entdeckt wurden.
    private static final int SOPHOS_VIRUS_FOUND = 3;

    @Override
    public AntivirusScanStatus scanFile(File fileToCheck) {
        for (int i = 0; i < 3; i++) {
            try {
                Process sophosProcess = Runtime.getRuntime().exec(SOPHOS_COMMAND + " " + fileToCheck);
                sophosProcess.waitFor();
                int shophosReturnCode = sophosProcess.exitValue();
                switch (shophosReturnCode) {
                    case SOPHOS_EVERYTHING_OK:
                        return AntivirusScanStatus.FILE_OK;
                    case SOPHOS_CANCELED:
                    case SOPHOS_ERROR:
                        break;
                    case SOPHOS_VIRUS_FOUND:
                        return AntivirusScanStatus.FILE_INFECTED;
                    default:
                        return AntivirusScanStatus.FILE_CAUSES_ERROR;
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.warn("Got interrupt while scanning for viruses", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Fehler beim Virenscannen", e);
            }
        }
        return AntivirusScanStatus.FILE_CAUSES_ERROR;
    }

}


