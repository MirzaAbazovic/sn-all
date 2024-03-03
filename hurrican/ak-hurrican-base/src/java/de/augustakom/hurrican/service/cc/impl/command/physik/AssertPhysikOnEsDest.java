/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 16:27:58
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Abstrakte Command-Klasse, um zu pruefen, ob auf einer bestimmten Endstelle eines Auftrags eine Physik zugeordnet
 * ist.
 *
 *
 */
public abstract class AssertPhysikOnEsDest extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(AssertPhysikOnEsDest.class);

    /**
     * Ueberprueft, ob auf der Endstelle Typ <code>esTyp</code> des Auftrags mit der ID <code>auftragId</code> eine
     * Physik zugeordnet ist.
     *
     * @param auftragId
     * @param esTyp
     * @return
     */
    protected boolean hasPhysik(Long auftragId, String esTyp) throws FindException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            Endstelle es = esSrv.findEndstelle4Auftrag(auftragId, esTyp);

            if (es != null) {
                return es.hasRangierung();
            }
            else {
                addWarning(this, "Es konnte nicht geprueft werden, ob der Endstelle " + esTyp +
                        " des Auftrags " + auftragId + " eine Physik zugeordnet ist!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ueberpruefung der bestehenden Physik:\n" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Erzeugt eine FindException. <br> Der Message-Text ist abhaengig vom Endstellen-Typ und ob eine Physik erwartet
     * (physikNeeded=true) wird oder nicht.
     *
     * @param esTyp
     * @param physikNeeded
     * @return
     */
    protected FindException generateException(String esTyp, boolean physikNeeded) {
        String msg = null;
        if (physikNeeded) {
            msg = "Der Endstelle " + esTyp + " des Ziel-Auftrags ist noch keine Physik zugeordnet!\n" +
                    "Dies ist fuer die Physik-Aenderung jedoch notwendig.";
        }
        else {
            msg = "Der Endstelle " + esTyp + " des Ziel-Auftrags ist bereits eine Physik zugeordnet!\n" +
                    "Die Physik-Aenderung kann deshalb nicht durchgefuehrt werden.";
        }
        return new FindException(msg);
    }
}


