/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Ermittelt die zu verwendende Vertragsnummer fuer den Geschaeftsfall. <br>
 * <p/>
 * Sofern die aktuelle Carrierbestellung einer Vertragsnummer besitzt, wird diese auf den Geschaeftsfall geschrieben.
 * <br> Ansonsten wird geprueft, ob die Carrierbestellung einen Auftrag fuer eine LAE/LMAE referenziert. Falls dies der
 * Fall ist, wird die letzte Carrierbestellung des referenzierten Auftrags ermittelt und die Vertragsnummer von dieser
 * Carrierbestellung in den Geschaeftsfall uebernommen.
 */
public class VertragsNummerAggregator extends AbstractWitaDataAggregator<String> {

    @Override
    public String aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Carrierbestellung carrierbestellung = witaDataService.loadCarrierbestellung(cbVorgang);

        if (StringUtils.isNotBlank(carrierbestellung.getVtrNr())) {
            return Strings.padStart(carrierbestellung.getVtrNr(), 10, '0');
        }
        else if (carrierbestellung.getAuftragId4TalNA() != null) {
            Carrierbestellung referencingCb = witaDataService.getReferencingCarrierbestellung(cbVorgang,
                    carrierbestellung);
            if ((referencingCb != null) && StringUtils.isNotBlank(referencingCb.getVtrNr())) {
                return Strings.padStart(referencingCb.getVtrNr(), 10, '0');
            }
        }

        return null;
    }

}
