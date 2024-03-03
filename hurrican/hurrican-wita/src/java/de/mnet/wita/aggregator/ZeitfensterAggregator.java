/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 08:54:33
 */
package de.mnet.wita.aggregator;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um ein Objekt des Typs {@link Kundenwunschtermin} mit einem Zeitfenster zu generieren. Das
 * Zeitfenster wird dabei aus dem CBVorgang gelesen. Falls dort kein Zeitfenster angegeben ist, wird ein
 * Default-Zeitfenster (SLOT_2) zurueck gegeben.
 */
public class ZeitfensterAggregator extends AbstractWitaDataAggregator<Zeitfenster> {

    @Override
    public Zeitfenster aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        return getZeitfenster(cbVorgang);
    }

    Zeitfenster getZeitfenster(WitaCBVorgang cbVorgang) {
        Zeitfenster result = null;

        // definiertes Zeitfenster von CBVorgang verwenden
        if (cbVorgang.getRealisierungsZeitfenster() != null) {
            result = cbVorgang.getRealisierungsZeitfenster();
        }

        // falls nicht angegeben, Default-Zeitfenster verwenden!
        if (result == null) {
            result = Zeitfenster.getDefaultZeitfenster(cbVorgang.getWitaGeschaeftsfallTyp());
        }

        return result;
    }

}
