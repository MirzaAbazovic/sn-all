/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 17:17:53
 */
package de.mnet.wita.aggregator;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Projekt;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregiert die Daten fuer ein Projekt (Projektkenner und Projektkopplung)
 */
public class ProjektAggregator extends AbstractWitaDataAggregator<Projekt> {

    @Override
    public Projekt aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        String projektKenner = cbVorgang.getProjektKenner();
        String kopplungsKenner = cbVorgang.getKopplungsKenner();

        /**
         * Sonderfall fuer die DTAG: Sollte bei einem abgebenden Anbieterwechsel der Ziel-Carrier keine Wita
         * implementiert haben, wird ein definierter Code in den Projektkenner geschrieben, den die DTAG (miss)braucht.
         * <br/><br/>
         * Falls eine Vorabstimmungs-Id angegeben ist wird davon ausgegangen, dass der abgebende Carrier auch
         * die WITA-Schnittstelle verwendet und der Projektkennter "PV TNBab" nicht benoetigt wird!
         */
        if (cbVorgang.isAnbieterwechsel() && StringUtils.isBlank(projektKenner) && StringUtils.isBlank(cbVorgang.getVorabstimmungsId())) {
            Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
            Preconditions.checkNotNull(vorabstimmung,
                    "Bei dem aktuellen Geschäftsfall muss die Vorabstimmung angegeben sein!");
            if (!vorabstimmung.getCarrier().getHasWitaInterface()) {
                projektKenner = WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA;
            }
        }

        if (StringUtils.isBlank(projektKenner)) {
            if (StringUtils.isNotBlank(kopplungsKenner)) {
                throw new WitaDataAggregationException(
                        "Ein Kopplungskenner kann nur in Verbindung mit einem Projektkenner verwendet werden"
                                + ". Bitte setzen Sie zusätzlich einen mit DTAG abgestimmten, gültigen Projektkenner."
                );
            }
            return null;
        }
        return new Projekt(projektKenner, kopplungsKenner);
    }
}
