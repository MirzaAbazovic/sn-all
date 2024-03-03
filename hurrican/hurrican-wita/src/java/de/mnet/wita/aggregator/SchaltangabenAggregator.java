/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 14:15:08
 */
package de.mnet.wita.aggregator;

import static de.mnet.wita.model.KollokationsTyp.*;

import java.util.*;

import de.augustakom.hurrican.model.cc.Equipment;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Schaltangaben zu ermitteln.
 */
public class SchaltangabenAggregator extends AbstractWitaDataAggregator<Schaltangaben> {

    @Override
    public Schaltangaben aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        List<Equipment> dtagEquipments = witaDataService.loadDtagEquipments(cbVorgang);

        if (hasEquipmentOfTyp(dtagEquipments, HVT) && hasEquipmentOfTyp(dtagEquipments, FTTC_KVZ)) {
            throw new WitaDataAggregationException(
                    "Eine gemischte Bestellung HVt / KVz ist nicht zulaessig!");
        }

        for (Equipment equipment : dtagEquipments) {
            if (!equipment.isPortForHvtTal() && !equipment.isPortForKvzTal()) {
                throw new WitaDataAggregationException(
                        "Es können nur HVt- oder KVz-TALs über WITA bestellt werden. Andere TALs bitte per Fax bestellen.");
            }
        }
        return witaDataService.createSchaltangaben(dtagEquipments);
    }


    boolean hasEquipmentOfTyp(List<Equipment> equipments, KollokationsTyp kollokationsTyp) {
        for (Equipment eq : equipments) {
            if (HVT.equals(kollokationsTyp) && eq.isPortForHvtTal()) {
                return true;
            }
            else if (FTTC_KVZ.equals(kollokationsTyp) && eq.isPortForKvzTal()) {
                return true;
            }
        }

        return false;
    }

}
