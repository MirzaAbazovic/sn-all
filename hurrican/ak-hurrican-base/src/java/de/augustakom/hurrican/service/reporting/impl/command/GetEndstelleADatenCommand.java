/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2007 15:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Command-Klasse, um die Daten zur Endstelle A eines Auftrags zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetEndstelleADatenCommand extends AbstractEndstelleDatenCommand {

    private static final Logger LOGGER = Logger.getLogger(GetEndstelleADatenCommand.class);

    private Long auftragId = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();
            readEndstellenDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * Ermittelt die Endstellen-Daten zu einem best. Auftrag
     */
    private void readEndstellenDaten() throws HurricanServiceCommandException {
        try {
            EndstellenService esSrv = getCCService(EndstellenService.class);
            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
            if (endstellen != null) {
                for (Endstelle es : endstellen) {
                    if (es.isEndstelleA()) {
                        map.putAll(readEndstelleDaten(es, auftragId));
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("AuftragId wurde dem Command-Objekt nicht uebergeben!");
        }
    }

}


