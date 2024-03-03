/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.07.2011 09:14:17
 */
package de.mnet.wita.aggregator;

import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Daten fuer {@link Auftragsposition} fuer eine LMAE zu sammeln.
 */
public class AuftragspositionLmaeAggregator extends AbstractWitaDataAggregator<Auftragsposition> {

    @Autowired
    AuftragspositionAggregator auftragspositionAggregator;

    @Autowired
    SchaltangabenAggregator schaltangabenAggregator;

    @Autowired
    ProduktBezeichnerAggregator produktBezeichnerAggregator;

    @Override
    public Auftragsposition aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Auftragsposition auftragsposition = auftragspositionAggregator.aggregate(cbVorgang);
        auftragsposition.setPosition(generateSubAuftragsposition(auftragsposition, cbVorgang));
        return auftragsposition;
    }

    /**
     * Ermittelt die Unter-{@link Auftragsposition} fuer die AEN-LMAE.
     */
    private Auftragsposition generateSubAuftragsposition(Auftragsposition mainAuftragsposition, WitaCBVorgang cbVorgang) {
        GeschaeftsfallProdukt subGeschaeftsfallProdukt = new GeschaeftsfallProdukt();
        subGeschaeftsfallProdukt.setStandortKollokation(mainAuftragsposition.getGeschaeftsfallProdukt().getStandortKollokation());
        subGeschaeftsfallProdukt.setSchaltangaben(schaltangabenAggregator.aggregate(cbVorgang));

        Auftragsposition subAuftragposition = new Auftragsposition();
        subAuftragposition.setAktionsCode(AktionsCode.AENDERUNG);
        subAuftragposition.setProdukt(mainAuftragsposition.getProdukt());
        subAuftragposition.setGeschaeftsfallProdukt(subGeschaeftsfallProdukt);

        return subAuftragposition;
    }
}
