/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 14:29:34
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Endstelle;

/**
 * DAO-Interface fuer DB-Operationen ueber mehrere Tabellen, die hauptsaechlich mit der Endstelle zu tun haben.
 *
 *
 */
public interface EndstelleViewDAO {

    /**
     * Sucht nach allen Endstellen, die einem best. Auftrag zugeordnet sind. Die Reihenfolge der Endstellen ist immer
     * erst B, dann A !!!
     *
     * @param auftragId ID des Auftrags.
     * @return Liste mit Objekten des Typs <code>Endstelle</code>
     */
    public List<Endstelle> findEndstellen4Auftrag(Long auftragId);

    /**
     * wie findEndstellen4Auftrag, nur ohne expliziten Aufruf von flush
     */
    List<?> findEndstellen4AuftragWithoutFlush(Long auftragId);

}
