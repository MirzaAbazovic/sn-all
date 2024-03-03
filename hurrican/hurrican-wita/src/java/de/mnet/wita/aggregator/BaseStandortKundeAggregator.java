/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2011 10:57:52
 */
package de.mnet.wita.aggregator;

import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.aggregator.utils.AggregationAddressHelper;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um das Objekt fuer den Kundenstandort (WITA: Standort A) zu erzeugen. Konkrete Subklassen muessen
 * via der Methode findAdresseStandort festlegen, wie die Standortadresse ermittelt wird.
 */
public abstract class BaseStandortKundeAggregator extends AbstractWitaDataAggregator<StandortKunde> {

    private static final Logger LOGGER = Logger.getLogger(BaseStandortKundeAggregator.class);

    @Override
    public StandortKunde aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        try {
            AddressModel adresseStandort = findAdresseStandort(cbVorgang);
            if (adresseStandort == null) {
                throw new WitaDataAggregationException("Die Standortadresse des Kunden konnte nicht ermittelt werden!");
            }
            return createStandort(adresseStandort, cbVorgang);
        }
        catch (WitaDataAggregationException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Nicht erwarteter Fehler bei der Ermittlung der Standortadresse des Kunden: " + e.getMessage(), e);
        }
    }

    protected abstract AddressModel findAdresseStandort(WitaCBVorgang cbVorgang) throws FindException;

    /**
     * Ermittelt aus dem angegebenen {@link AddressModel} die Angabe fuer die Lage der TAE.
     *
     * @param cbVorgang Der CBVorgang, zu dem die Lage der TAE gefunden werden soll. Wird in Subklassen verwendet.
     */
    protected String getLageTae(AddressModel addressModel, WitaCBVorgang cbVorgang) throws FindException {
        return addressModel.getStrasseAdd();
    }

    StandortKunde createStandort(AddressModel addressModel, WitaCBVorgang cbVorgang) throws FindException {
        StandortKunde standort = new StandortKunde();
        standort.setKundenname(AggregationAddressHelper.getKundenname(addressModel));
        standort.setStrassenname(addressModel.getStrasse());
        standort.setHausnummer(addressModel.getNummer());
        standort.setHausnummernZusatz(addressModel.getHausnummerZusatz());
        standort.setPostleitzahl(addressModel.getPlzTrimmed());
        standort.setOrtsname(addressModel.getOrt());
        standort.setOrtsteil(addressModel.getOrtsteil());
        standort.setLand(AggregationAddressHelper.getLandId(addressModel));
        standort.setLageTAEDose(getLageTae(addressModel, cbVorgang));

        if (addressModel instanceof CCAddress) {
            // nur fuer WITA KFT notwendig!
            CCAddress address = (CCAddress) addressModel;
            standort.setGebaeudeteilName(address.getGebaeudeteilName());
            standort.setGebaeudeteilZusatz(address.getGebaeudeteilZusatz());
        }
        return standort;
    }

}


