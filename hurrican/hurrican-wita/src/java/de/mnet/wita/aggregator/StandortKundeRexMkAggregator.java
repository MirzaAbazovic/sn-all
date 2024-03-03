/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2011 10:55:38
 */
package de.mnet.wita.aggregator;

import javax.annotation.*;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um das Objekt fuer den Kundenstandort (WITA: Standort A) zu erzeugen. <br> Sofern ein {@link
 * Vorabstimmung}s-Objekt inkl. Adresse definiert ist, wird diese Adresse als Kunden-Standort verwendet. <br>
 * Andernfalls wird die Standort-Adresse der Endstelle B des aktuellen Auftrags verwendet. <br><br> Dieses Vorgehen ist
 * bei REX-MK notwendig, damit auch eine Alt-Adresse des Kunden angegeben werden kann. (Wird verwendet, wenn Kunde
 * umzieht und seine Rufnummer mit nehmen will.)
 */
public class StandortKundeRexMkAggregator extends BaseStandortKundeAggregator {

    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    BillingAuftragService billingAuftragService;

    @Override
    protected AddressModel findAdresseStandort(WitaCBVorgang cbVorgang) throws FindException {
        Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
        if ((vorabstimmung != null) && (vorabstimmung.getPreviousLocationAddress() != null)) {
            return vorabstimmung.getPreviousLocationAddress();
        }
        AuftragDaten ad = ccAuftragService.findAuftragDatenByAuftragIdTx(cbVorgang.getAuftragId());
        Adresse anschlussAdresse = billingAuftragService.findAnschlussAdresse4Auftrag(ad.getAuftragNoOrig(),
                Endstelle.ENDSTELLEN_TYP_B);
        AddressModel adresseStandort = anschlussAdresse;
        if (anschlussAdresse != null) {
            adresseStandort = loadDtagNameFailSave(anschlussAdresse.getGeoId(), anschlussAdresse);
        }
        return adresseStandort;
    }

}
