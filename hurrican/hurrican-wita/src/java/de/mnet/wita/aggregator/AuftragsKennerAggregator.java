/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 13:39:32
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.AuftragsKenner;
import de.mnet.wita.model.WitaCBVorgang;

public class AuftragsKennerAggregator extends AbstractWitaDataAggregator<AuftragsKenner> {

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    CarrierElTALService carrierElTALService;

    @Override
    public AuftragsKenner aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        if (cbVorgang.getAuftragsKlammer() == null) {
            return null;
        }
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setAuftragsKlammer(cbVorgang.getAuftragsKlammer());
        try {
            List<WitaCBVorgang> cbVorgaenge = carrierElTALService.findCBVorgaengeByExample(example);
            return new AuftragsKenner(cbVorgang.getAuftragsKlammer(), cbVorgaenge.size());
        }
        catch (FindException e) {
            throw new WitaDataAggregationException("Problem beim suchen der CBVorgaenge zu der Auftragsklammer"
                    + cbVorgang.getAuftragsKlammer(), e);
        }

    }

}


