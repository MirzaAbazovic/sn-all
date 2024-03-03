/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 12:57:08
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.aggregator.execution.WitaDataAggregationExecuter.*;

import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Produkt;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Daten fuer {@link Auftragsposition} zu sammeln.
 */
public class AuftragspositionAggregator extends AbstractWitaDataAggregator<Auftragsposition> {

    @Autowired
    GeschaeftsfallProduktAggregator geschaeftsfallProduktAggregator;
    @Autowired
    AktionsCodeAenderungAggregator aktionsCodeAenderungAggregator;


    @Override
    public Auftragsposition aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Auftragsposition auftragsposition = new Auftragsposition();
        auftragsposition.setProdukt(Produkt.TAL); // aktuell nur TAL-Produkte relevant!
        auftragsposition.setGeschaeftsfallProdukt(geschaeftsfallProduktAggregator.aggregate(cbVorgang));

        // AktionsCode abhaengig von der Konfiguration setzen
        executeAggregator(cbVorgang, aktionsCodeAenderungAggregator, auftragsposition);

        return auftragsposition;
    }
}
