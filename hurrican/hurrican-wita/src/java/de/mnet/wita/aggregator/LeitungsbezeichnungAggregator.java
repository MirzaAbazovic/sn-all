/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2011 12:53:26
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Leitungsbezeichnung einer aktiven TAL zu ermitteln.
 */
public class LeitungsbezeichnungAggregator extends AbstractWitaDataAggregator<LeitungsBezeichnung> {

    private static final Logger LOG = Logger.getLogger(LeitungsbezeichnungAggregator.class);

    @Override
    public LeitungsBezeichnung aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            Carrierbestellung carrierbestellung = carrierService.findCB(cbVorgang.getCbId());
            Preconditions.checkNotNull(carrierbestellung, "Carrierbestellung konnte nicht ermittelt werden!");
            if (StringUtils.isBlank(carrierbestellung.getLbz())) {
                return null;
            }

            String esTyp = loadEndstelleTyp(carrierbestellung, cbVorgang);
            return new LeitungsBezeichnung(carrierbestellung.getLbz(),
                    witaDataService.loadHVTStandortOnkz4Auftrag(cbVorgang.getAuftragId(),esTyp));
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Fehler bei der Ermittlung der LeitungsBezeichnung: "
                    + e.getMessage(), e);
        }
    }
}
