/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 10:50:08
 */
package de.mnet.wita.aggregator;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.model.WitaCBVorgang;

public class AktionsCodeAenderungAggregator extends AbstractWitaDataAggregator<AktionsCode> {

    @Override
    public AktionsCode aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        return AktionsCode.AENDERUNG;
    }

}
