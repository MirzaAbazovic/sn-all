/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 12:53:26
 */
package de.mnet.wita.aggregator;

import org.apache.commons.lang.StringUtils;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Leitungsbezeichnung für einen Providerwechsel aus der Vorabstimmung zu ermitteln.
 */
public class LeitungsbezeichnungPvAggregator extends AbstractWitaDataAggregator<LeitungsBezeichnung> {

    @Override
    public LeitungsBezeichnung aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        if (StringUtils.isNotBlank(cbVorgang.getVorabstimmungsId())) {
            // falls WBCI Vorabstimmung angegeben, muss keine Leitungsbezeichnung gesetzt werden!
            return null;
        }

        Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
        if ((vorabstimmung == null) || (StringUtils.isBlank(vorabstimmung.getProviderLbz()))) {
            return null;
        }
        return new LeitungsBezeichnung(vorabstimmung.getProviderLbz(), witaDataService.loadHVTStandortOnkz4Auftrag(cbVorgang.getAuftragId(),vorabstimmung.getEndstelleTyp()));
    }
}
