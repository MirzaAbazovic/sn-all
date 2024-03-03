/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 14:15:08
 */
package de.mnet.wita.aggregator;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Schaltangaben;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Aggregator-Klasse, um die Schaltangaben der Endstelle zur referenzierten Carrierbestellung zu ermitteln.
 */
public class ReferencingSchaltangabenAggregator extends AbstractWitaDataAggregator<Schaltangaben> {

    @Override
    public Schaltangaben aggregate(final WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        Schaltangaben schaltangaben;
        if (cbVorgang.getPreviousUebertragungsVerfahren() != null) {
            final Collection<Equipment> dtagEquipment = loadDtagEquipmentOfActualCbVorgang(cbVorgang);
            schaltangaben = witaDataService.createSchaltangaben(dtagEquipment, cbVorgang.getPreviousUebertragungsVerfahren());
        }
        else {
            final Collection<Equipment> dtagEquipment = loadDtagEquipmentOfReferencingCbVorgang(cbVorgang);
            schaltangaben = witaDataService.createSchaltangaben(dtagEquipment);
        }
        return schaltangaben;
    }

    /**
     * Ermittelt das {@link Equipment} aus der alten (referenzierten) Carrierbestellung
     *
     * @return never {@code null}
     * @throws WitaDataAggregationException if no {@link Endstelle} or {@link Equipment} found
     */
    Collection<Equipment> loadDtagEquipmentOfReferencingCbVorgang(final CBVorgang cbVorgang) {
        try {
            final Carrierbestellung actualCb = witaDataService.loadCarrierbestellung(cbVorgang);
            final Carrierbestellung referencingCb = witaDataService.getReferencingCarrierbestellung(cbVorgang, actualCb);

            return loadDtagEquipment(referencingCb);
        }
        catch (final FindException e) {
            throw new WitaDataAggregationException(e);
        }
    }

    /**
     * Ermittelt das {@link Equipment} aus der eigenen Carrierbestellung - benoetigt fuer DTAG Portwechsel.
     *
     * @return never {@code null}
     * @throws WitaDataAggregationException if no {@link Endstelle} or {@link Equipment} found
     */
    Collection<Equipment> loadDtagEquipmentOfActualCbVorgang(final CBVorgang cbVorgang) {
        try {
            final Carrierbestellung actualCb = witaDataService.loadCarrierbestellung(cbVorgang);
            return loadDtagEquipment(actualCb);
        }
        catch (final FindException e) {
            throw new WitaDataAggregationException(e);
        }
    }

    private Collection<Equipment> loadDtagEquipment(final Carrierbestellung referencingCb) throws FindException,
            WitaDataAggregationException {
        final List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(referencingCb);
        if (CollectionUtils.isEmpty(endstellen)) {
            throw new WitaDataAggregationException("Keine Endstellen für alte Carrierbestellung gefunden.");
        }

        final Map<Long, Equipment> dtagPorts = new HashMap<Long, Equipment>();
        for (final Endstelle endstelle : endstellen) {
            final Equipment dtagPort = rangierungsService.findEquipment4Endstelle(endstelle, false, true);
            if ((dtagPort != null) && !dtagPorts.containsKey(dtagPort.getId())) {
                dtagPorts.put(dtagPort.getId(), dtagPort);
            }
        }
        if (CollectionUtils.isEmpty(dtagPorts.values())) {
            throw new WitaDataAggregationException("Keinen Dtag-Ports für alte Carrierbestellung gefunden.");
        }
        return dtagPorts.values();
    }
}
