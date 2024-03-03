/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 13:36:57
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.impl.RufnummerPortierungService;

public class RufnummernPortierungAggregator extends AbstractWitaDataAggregator<RufnummernPortierung> {

    @Resource(name = "de.mnet.wita.service.impl.RufnummerPortierungService")
    RufnummerPortierungService rufnummerPortierungService;

    @Override
    public RufnummernPortierung aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Collection<Rufnummer> rufnummern = witaDataService.loadRufnummern(cbVorgang);
        return rufnummerPortierungService.transformToRufnummerPortierung(rufnummern, true);
    }

}
