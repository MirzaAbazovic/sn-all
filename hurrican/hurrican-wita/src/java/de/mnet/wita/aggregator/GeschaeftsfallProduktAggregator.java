/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:38:04
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.aggregator.execution.WitaDataAggregationExecuter.*;

import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um das {@link GeschaeftsfallProdukt} Objekt zu erzeugen
 */
public class GeschaeftsfallProduktAggregator extends AbstractWitaDataAggregator<GeschaeftsfallProdukt> {

    @Autowired
    private BestandsSucheRexMkAggregator bestandsSucheAggregator;
    @Autowired
    private BestandsSucheVblAggregator bestandsSucheVblAggregator;
    @Autowired
    private LeitungsbezeichnungAggregator leitungsbezeichnungAggregator;
    @Autowired
    private LeitungsbezeichnungPvAggregator leitungsbezeichnungPvAggregator;
    @Autowired
    private MontageleistungAggregator montageleistungAggregator;
    @Autowired
    private ReferencingLeitungsbezeichnungAggregator referencingLeitungsbezeichnungAggregator;
    @Autowired
    private ReferencingSchaltangabenAggregator referencingSchaltangabenAggregator;
    @Autowired
    private RufnummernPortierungAggregator rufnummernPortierungAggregator;
    @Autowired
    private SchaltangabenAggregator schaltangabenAggregator;
    @Autowired
    private StandortKollokationAggregator standortKollokationAggregator;
    @Autowired
    private StandortKundeAggregator standortKundeAggregator;
    @Autowired
    private StandortKundeRexMkAggregator standortKundeRexMkAggregator;
    @Autowired
    private StandortKundeVblAggregator standortKundeVblAggregator;
    @Autowired
    private VormieterAggregator vormieterAggregator;

    @Override
    public GeschaeftsfallProdukt aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        GeschaeftsfallProdukt geschaeftsfallProdukt = new GeschaeftsfallProdukt();
        geschaeftsfallProdukt.setVorabstimmungsId(cbVorgang.getVorabstimmungsId());

        executeAggregator(cbVorgang, bestandsSucheAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, bestandsSucheVblAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, leitungsbezeichnungAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, leitungsbezeichnungPvAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, montageleistungAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, referencingLeitungsbezeichnungAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, referencingSchaltangabenAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, rufnummernPortierungAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, schaltangabenAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, standortKollokationAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, standortKundeAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, standortKundeRexMkAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, standortKundeVblAggregator, geschaeftsfallProdukt);
        executeAggregator(cbVorgang, vormieterAggregator, geschaeftsfallProdukt);

        return geschaeftsfallProdukt;
    }

}
