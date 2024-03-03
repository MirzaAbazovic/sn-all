/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2014
 */
package de.mnet.wita.aggregator;

import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um das Objekt fuer den Kundenstandort (WITA: Standort A) zu erzeugen. <br> <br/> Im Gegensatz zum
 * herkoemmlichen {@link de.mnet.wita.aggregator.StandortKundeAggregator} wird nicht die auf der Endstelle hinterlegte
 * Adresse angezogen, sondern die AI-Adresse der aktuellen Carrierbestellung. <br/> Dies ist mit WITA v7 notwendig
 * geworden, da in dieser Version das XML-Element 'StandortAnschlussinhaber' nicht mehr verfuegbar ist; somit koennen
 * abweichende Anschlussinhaber nicht mehr parallel zum 'Standort A' angegeben werden. Die DTAG weist die Bestellungen
 * aber ab, wenn wir im 'Standort A' eine andere Kundeninformation mit schicken, wie sie in ihren Systemen hinterlegt
 * hat. <br/> Bei diesen Bestellungen ist es dann auch zwingend notwendig, dass ein Montagehinweis mit der korrekten
 * Kundenadresse bzw. den korrekten Kundennamen angegeben wird, damit der Service-Techniker vor Ort den Kunden auch
 * 'finden' kann. Dies ist jedoch durch den UseCase VBL (im Zuge von TKG $46) schon gegeben und wird nicht gesondert
 * geprueft.
 */
public class StandortKundeVblAggregator extends BaseStandortKundeAggregator {

    @Override
    protected AddressModel findAdresseStandort(WitaCBVorgang cbVorgang) throws FindException {
        Endstelle endstelle = loadEndstelle(cbVorgang);
        CCAddress anschlussinhaber = loadAnschlussinhaberAdresse(cbVorgang);
        AddressModel adresseStandort = anschlussinhaber;
        if (anschlussinhaber != null && endstelle != null) {
            adresseStandort = loadDtagNameFailSave(endstelle.getGeoId(), anschlussinhaber);
        }
        return adresseStandort;
    }

}
