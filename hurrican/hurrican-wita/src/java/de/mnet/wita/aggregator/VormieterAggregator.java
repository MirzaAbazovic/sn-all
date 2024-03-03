/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2011 15:06:46
 */
package de.mnet.wita.aggregator;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.model.WitaCBVorgang;

public class VormieterAggregator extends AbstractWitaDataAggregator<Vormieter> {

    @Override
    public Vormieter aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        return createVormieter(cbVorgang);
    }

    Vormieter createVormieter(WitaCBVorgang cbVorgang) {
        Carrierbestellung carrierbestellung = witaDataService.loadCarrierbestellung(cbVorgang);

        CarrierbestellungVormieter cbVormieter = carrierbestellung.getCarrierbestellungVormieter();
        if ((cbVormieter == null) || cbVormieter.isEmpty()) {
            return null;
        }

        Vormieter result = new Vormieter(cbVormieter.getVorname(), cbVormieter.getNachname(), cbVormieter.getOnkz(),
                cbVormieter.getRufnummer(), cbVormieter.getUfaNummer());
        return result;
    }
}
