/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2011 11:18:56
 */
package de.mnet.wita.aggregator;

import de.mnet.common.tools.PropertyTools;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.common.LeitungsBezeichnung;

/**
 * Aggregator-Klasse, um den {@link ProduktBezeichner} fuer eine Kuendigung zu ermitteln. <br> Die Ermittlung erfolgt
 * hier auf Basis der Leitungsbezeichnung der zu kuendigenden Leitung.
 */
public class ProduktBezeichnerKueKdAggregator {

    public ProduktBezeichner aggregate(Auftrag auftrag) throws WitaDataAggregationException {
        String leitungsSchluesselZahl = (String) PropertyTools.getNestedPropertyIgnoreNestedNulls(auftrag, LeitungsBezeichnung.LEITUNGS_SCHLUESSEL_ZAHL_PROPERTY_PATH);
        ProduktBezeichner produktBezeichner = ProduktBezeichner.getByLeitungsSchluesselZahl(leitungsSchluesselZahl);
        return produktBezeichner;
    }
}
