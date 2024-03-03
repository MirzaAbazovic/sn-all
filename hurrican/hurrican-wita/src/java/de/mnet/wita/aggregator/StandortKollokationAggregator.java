/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:53:38
 */
package de.mnet.wita.aggregator;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Standortdaten des Kollokationsraums zu ermitteln (WITA: Standort B).
 */
public class StandortKollokationAggregator extends AbstractWitaDataAggregator<StandortKollokation> {

    private static final Logger LOG = Logger.getLogger(StandortKollokationAggregator.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    HVTService hvtService;

    /**
     * Ermittle fuer den Vorgang die Kollokationsadresse fuer den Standort. Ist dieser Standort ein Kvz, so wird die
     * KvzAddresse anstatt die HVT-Adresse verwendet.
     *
     * @see de.mnet.wita.aggregator.AbstractWitaDataAggregator#aggregate(de.mnet.wita.model.WitaCBVorgang)
     */
    @Override
    public StandortKollokation aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        HVTStandort hvtStandort = loadHvtStandort(cbVorgang);
        if (hvtStandort == null) {
            throw new WitaDataAggregationException("Der technische Standort konnte nicht ermittelt werden!");
        }

        try {
            HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(hvtStandort.getId());
            if (hvtGruppe == null) {
                throw new WitaDataAggregationException(
                        "Die Adress-Daten des technischen Standorts konnten nicht ermittelt werden!");
            }

            if (hvtStandort.isFttc()) {
                List<Equipment> dTagEquipments = witaDataService.loadDtagEquipmentsWithNoRollback(cbVorgang);
                if (dTagEquipments.isEmpty()) {
                    throw new WitaDataAggregationException(
                            String.format("Das Equipment fuer den Vorgang '%s' konnte nicht ermittelt werden!",
                                    cbVorgang.toString())
                    );
                }
                Equipment eq1 = dTagEquipments.get(0);
                String kvzNummer = eq1.getKvzNummer();
                KvzAdresse kvzAdresse = hvtService.findKvzAdresse(hvtStandort.getId(), kvzNummer);
                if (kvzAdresse == null) {
                    throw new WitaDataAggregationException(
                            "Die Adress-Daten des technischen Standorts (KVZ Adresse) konnten nicht ermittelt werden!");
                }
                return createKvzStandort(hvtGruppe, hvtStandort, kvzAdresse);
            }
            else {
                return createHvtStandort(hvtGruppe, hvtStandort);
            }
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException(
                    "Fehler bei der Ermittlung der Kollokationsdaten: " + e.getMessage(), e);
        }
    }

    /**
     * Uebertraegt die Kollokations-Informationen aus den Angaben der technischen Standorte in das {@link Standort}
     * Objekt fuer Kvz-Adressen.
     */
    StandortKollokation createKvzStandort(HVTGruppe hvtGruppe, HVTStandort hvtStandort, KvzAdresse kvzAdresse) {
        StandortKollokation standort = new StandortKollokation();
        standort.setStrassenname(kvzAdresse.getStrasse()
                + (kvzAdresse.getHausNr() == null ? "" : " " + kvzAdresse.getHausNr()));
        standort.setHausnummer("0");
        standort.setPostleitzahl(kvzAdresse.getPlz());
        standort.setOrtsname(kvzAdresse.getOrt());
        standort.setOnkz(hvtGruppe.getOnkzWithoutLeadingNulls());
        standort.setAsb(String.format("%s", hvtStandort.getDTAGAsb()));

        return standort;
    }

    /**
     * Uebertraegt die Kollokations-Informationen aus den Angaben der technischen Standorte in das {@link Standort}
     * Objekt fuer HVT-Adressen.
     */
    StandortKollokation createHvtStandort(HVTGruppe hvtGruppe, HVTStandort hvtStandort) {
        StandortKollokation standort = new StandortKollokation();
        standort.setStrassenname(hvtGruppe.getStrasse()
                + (hvtGruppe.getHausNr() == null ? "" : " " + hvtGruppe.getHausNr()));
        standort.setHausnummer("0");
        standort.setPostleitzahl(hvtGruppe.getPlz());
        standort.setOrtsname(hvtGruppe.getOrt());
        standort.setOrtsteil(hvtGruppe.getOrtZusatz());
        standort.setOnkz(hvtGruppe.getOnkzWithoutLeadingNulls());
        standort.setAsb(String.format("%s", hvtStandort.getAsb()));

        return standort;
    }

    /**
     * Ermittelt den technischen Standort, der der Endstelle des aktuellen {@link CBVorgang}s zugeordnet ist.
     *
     * @return zugeordneter technischer Standort
     */
    HVTStandort loadHvtStandort(CBVorgang cbVorgang) {
        List<Endstelle> endstellen = witaDataService.loadEndstellen(cbVorgang);
        if (endstellen.isEmpty()) {
            throw new WitaDataAggregationException(String.format("Es konnten keine Endstellen ermittelt werden. CBVorgangs-Id: %s", cbVorgang.getId()));
        }
        Endstelle endstelle = endstellen.get(0); // same Hvt on both 4-Draht Endstellen
        if (endstelle.getHvtIdStandort() == null) {
            throw new WitaDataAggregationException(String.format("Der Endstelle ist kein technischer Standort zugeordnet! Endstellen-Id: %s", endstelle.getId()));
        }
        try {
            return hvtService.findHVTStandort(endstelle.getHvtIdStandort());
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WitaDataAggregationException("Bei der Ermittlung des HVT-Standorts ist ein Fehler aufgetreten: "
                    + e.getMessage(), e);
        }
    }

}
