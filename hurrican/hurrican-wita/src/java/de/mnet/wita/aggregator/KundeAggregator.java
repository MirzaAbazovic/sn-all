/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 15:03:37
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator, um die (M-net) Kundeninformationen fuer den WITA-Vorgang zu ermitteln.
 */
public class KundeAggregator extends AbstractWitaDataAggregator<Pair<Kunde, Kunde>> {

    @Override
    public Pair<Kunde, Kunde> aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        CarrierKennung carrierKennung = (cbVorgang.isRexMk())
                ? witaDataService.loadCarrierKennungForRexMk(cbVorgang)
                : witaDataService.loadCarrierKennung(cbVorgang);

        Kunde kunde = new Kunde();
        kunde.setKundennummer(carrierKennung.getKundenNr());
        kunde.setLeistungsnummer(Strings.padStart(carrierKennung.getWitaLeistungsNr(), 10, '0'));
        Kunde besteller = null;

        // Falls nicht fuer Mnet-Muenchen bestellt wird -> im Namen von Mnet-Muenchen bestellen
        // ACHTUNG: wird diese Logik geaendert bitte auch die View 'V_HURRICAN_DTAG_CB' anpassen
        if (!StringUtils.equals(CarrierKennung.DTAG_KUNDEN_NR_MNET, carrierKennung.getKundenNr())) {
            besteller = createBestellerMuc();
        }
        return new Pair<Kunde, Kunde>(kunde, besteller);
    }

    private Kunde createBestellerMuc() {
        try {
            CarrierKennung carrierKennungMuc = carrierService.findCarrierKennung(CarrierKennung.ID_MNET_MUENCHEN);
            Kunde besteller = new Kunde();
            besteller.setKundennummer(carrierKennungMuc.getKundenNr());
            besteller.setLeistungsnummer(Strings.padStart(carrierKennungMuc.getWitaLeistungsNr(), 10, '0'));
            return besteller;
        }
        catch (FindException e) {
            throw new WitaDataAggregationException("Carrier MUC wurde nicht gefunden!", e);
        }
    }

}
