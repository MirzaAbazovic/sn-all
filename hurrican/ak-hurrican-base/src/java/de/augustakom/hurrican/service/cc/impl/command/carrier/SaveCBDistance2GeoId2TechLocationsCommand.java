/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2011 16:49:42
 */

package de.augustakom.hurrican.service.cc.impl.command.carrier;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Command-Klasse, um eventuell aus Abschnitten aufsummierte Leitungslänge auf den Kombinationen aus GeoId/HVT Standort
 * zu speichern.
 */
public class SaveCBDistance2GeoId2TechLocationsCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(SaveCBDistance2GeoId2TechLocationsCommand.class);

    // Property-Keys
    /**
     * Key fuer die prepare-Methode, um dem Command die Carrierbestellung zu uebergeben.
     */
    public static final String KEY_CARRIER_ORDER = "carrier.order";
    /**
     * Key fuer die prepare-Methode, um dem Command die Session ID zu uebergeben.
     */
    public static final String KEY_SESSION_ID = "session.id";

    // Properties
    private Carrierbestellung carrierbestellung;
    private Long sessionId;

    // Resources
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            carrierbestellung = getPreparedValue(KEY_CARRIER_ORDER, Carrierbestellung.class, false,
                    "Command Parameter 'Carrierbestellung' muss angegeben sein!");
            sessionId = getPreparedValue(KEY_SESSION_ID, Long.class, false,
                    "Command Parameter 'Session ID' muss angegeben sein!");

            Integer distance = carrierbestellung.calcLlSum();
            List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellung);
            if ((distance == null) || CollectionTools.isEmpty(endstellen)) {
                return null;
            }
            Long longDistance = Long.valueOf(distance);
            writeDistance2Endstellen(longDistance, endstellen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Speichern der Leitungslänge aus Carrierbestellung schlug fehl!", e);
        }
        return null;
    }

    /**
     * Schreibt die Leitungslänge auf alle Endstellen, die eine brauchbare Kombination aus Geo ID/HVT Standort halten.
     */
    void writeDistance2Endstellen(Long longDistance, List<Endstelle> endstellen) throws StoreException {
        Map<Pair<Long, Long>, Object> alreadyProcessed = new HashMap<>();
        for (Endstelle endstelle : endstellen) {
            if ((endstelle.getGeoId() != null) && (endstelle.getHvtIdStandort() != null)) {
                Pair<Long, Long> key = Pair.create(endstelle.getGeoId(), endstelle.getHvtIdStandort());
                if (!alreadyProcessed.containsKey(key)) {
                    alreadyProcessed.put(key, null);
                    GeoId2TechLocation geoId2TechLocation = null;
                    try {
                        geoId2TechLocation = availabilityService.findGeoId2TechLocation(endstelle.getGeoId(),
                                endstelle.getHvtIdStandort());
                    }
                    catch (FindException e) {
                        // Ignore Exception, stay silent
                        LOGGER.error(e.getMessage(), e);
                    }

                    if (geoId2TechLocation != null
                            && NumberTools.notEqual(longDistance, geoId2TechLocation.getTalLength())) {
                        geoId2TechLocation.setTalLength(longDistance);
                        geoId2TechLocation.setTalLengthTrusted(Boolean.TRUE);
                        availabilityService.saveGeoId2TechLocation(geoId2TechLocation, sessionId);
                    }
                }
            }
        }
    }

    /**
     * Injected
     */
    public void setAvailabilityService(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

}
