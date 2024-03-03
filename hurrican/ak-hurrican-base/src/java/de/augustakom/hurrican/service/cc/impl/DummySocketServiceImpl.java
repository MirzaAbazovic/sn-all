/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2016
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.cc.DummySocketService;

/**
 * Dummy Implementierung eines Services, der fuer Tests der Socket-Kommunikation verwendet werden kann Created by
 * zolleiswo on 02.03.2016.
 */
public class DummySocketServiceImpl extends DefaultCCService implements DummySocketService {
    private static final Logger LOGGER = Logger.getLogger(DummySocketServiceImpl.class);
    private String EOL = System.getProperty("line.separator");

    @Override
    public String doSomething(final long waitTimeInMillis) {
        StringBuilder sb = new StringBuilder();

        LocalDateTime start = LocalDateTime.now();
        String message = "dummy socket service called @" + start + " with sleep time " + waitTimeInMillis + " ms";
        logMessage(message, sb);

        try {
            long waitTimeSlice = Math.min(waitTimeInMillis, 10 * 1000);
            message = "   slice = " + waitTimeSlice + " ms";
            logMessage(message, sb);
            while (true) {
                message = "    sleeping now for " + waitTimeSlice + " ms ...";
                logMessage(message, sb);
                Thread.sleep(waitTimeSlice);
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(start, now);
                message = "   sleep lasts now for " + duration.toString();
                logMessage(message, sb);

                if (duration.toMillis() >= waitTimeInMillis) {
                    break;
                }
            }

            message = "dummy socket server finished";
            logMessage(message, sb);
        }
        catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            sb.append("ERROR!: ").append(e.getMessage()).append(EOL);
        }
        finally {
            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            message = "duration = " + duration.toMillis() + " ms";
            logMessage(message, sb);
            if (duration.toMillis() >= 60000) {
                message = "duration = " + duration.toMinutes() + " min";
                logMessage(message, sb);
            }
        }

        return sb.toString();
    }

    private void logMessage(String message, StringBuilder sb) {
        LOGGER.info(message);
        sb.append(message).append(EOL);
    }
}

