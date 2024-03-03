/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Ermittelt die zu verwendende Vertragsnummer für einen Providerwechsel aus der Vorabstimmung.
 */
public class VertragsNummerPvAggregator extends AbstractWitaDataAggregator<String> {

    @Override
    public String aggregate(WitaCBVorgang cbVorgang) throws WbciValidationException {
        if (cbVorgang.getVorabstimmungsId() != null) {
            //Ermittlung der Vertragsnummer anhand der AKM-TR und unter berücksichtigung von Klammerungen
            return witaWbciServiceFacade.checkAndReturnNextWitaVertragsnummern(
                    cbVorgang.getWitaGeschaeftsfallTyp(),
                    cbVorgang.getVorabstimmungsId(),
                    cbVorgang.getCbId(),
                    cbVorgang.getAuftragsKlammer());
        }

        Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
        if ((vorabstimmung == null) || (StringUtils.isBlank(vorabstimmung.getProviderVtrNr()))) {
            return null;
        }
        return Strings.padStart(vorabstimmung.getProviderVtrNr(), 10, '0');
    }

}
