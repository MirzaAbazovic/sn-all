/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Ermittelt den abgebenden Provider für einen Providerwechsel.
 */
public class AbgebenderProviderAggregator extends AbstractWitaDataAggregator<Carrier> {

    @Override
    public Carrier aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        if (cbVorgang.getVorabstimmungsId() != null) {
            WbciGeschaeftsfall wbciGeschaeftsfall = witaWbciServiceFacade.getWbciGeschaeftsfall(cbVorgang.getVorabstimmungsId());
            if (wbciGeschaeftsfall == null) {
                throw new WitaDataAggregationException(
                        String.format("WBCI Geschäftsfall zu '%s' konnte nicht ermittelt werden!", cbVorgang.getVorabstimmungsId()));
            }

            return (CarrierCode.DTAG.equals(wbciGeschaeftsfall.getAbgebenderEKP())) ? Carrier.DTAG : Carrier.OTHER;
        }

        Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
        if (vorabstimmung.getCarrier() == null) {
            return null;
        }
        else if (vorabstimmung.isCarrierDtag()) {
            return Carrier.DTAG;
        }
        return Carrier.OTHER;
    }

}
