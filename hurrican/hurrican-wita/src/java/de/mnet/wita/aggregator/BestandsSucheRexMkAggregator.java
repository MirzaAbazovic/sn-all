/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

public class BestandsSucheRexMkAggregator extends AbstractWitaDataAggregator<BestandsSuche> {

    @Override
    public BestandsSuche aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Vorabstimmung cbPv = witaDataService.loadVorabstimmung(cbVorgang);

        BestandsSuche bestandsSuche;
        if (isEinzelanschluss(cbPv)) {
            Preconditions.checkNotNull(cbPv.getBestandssucheOnkz(),
                    "Onkz muss für Einzelanschluss Bestandssuche gesetzt sein!");
            Preconditions.checkNotNull(cbPv.getBestandssucheDn(),
                    "Rufnummer muss für Einzelanschluss Bestandssuche gesetzt sein!");
            bestandsSuche = new BestandsSuche(cbPv.getBestandssucheOnkzWithoutLeadingZeros(), cbPv.getBestandssucheDn(), null, null, null);
        }
        else {
            Preconditions.checkNotNull(cbPv.getBestandssucheOnkzWithoutLeadingZeros(),
                    "Onkz muss für Anlagenanschluss Bestandssuche gesetzt sein!");
            Preconditions.checkNotNull(cbPv.getBestandssucheDn(),
                    "Durchwahl muss für Einzelanschluss Bestandssuche gesetzt sein!");
            Preconditions.checkNotNull(cbPv.getBestandssucheDirectDial(),
                    "Abfragestelle muss für Einzelanschluss Bestandssuche gesetzt sein!");

            bestandsSuche = new BestandsSuche(null, null, cbPv.getBestandssucheOnkzWithoutLeadingZeros(), cbPv.getBestandssucheDn(), cbPv.getBestandssucheDirectDial());
        }
        return bestandsSuche;
    }

    private boolean isEinzelanschluss(Vorabstimmung vorabstimmung) {
        return StringUtils.isEmpty(vorabstimmung.getBestandssucheDirectDial());
    }

}
