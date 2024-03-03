/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2007 09:35:09
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Command-Klasse ermittelt eine freie Rangierung und ordnet diese einer Endstelle zu. <br> Die freien Rangierungen
 * werden ueber folgende Parameter ermittelt: <br> - HVT  <br> - EQ_OUT.UETV <br> - EQ_OUT.RANG_SS_TYPE <br> <br><br>
 * Die ermittelte freie Rangierung erhaelt nach der Zuordnung zur Endstelle noch einen bestimmten Physiktyp, der ueber
 * die Carrier-Equipment Details bestimmt ist.
 *
 *
 */
@CcTxRequired
public class AssignRangierungByCarrierDetailsCommand extends AbstractAssignRangierungCommand {

    private static final Logger LOGGER = Logger.getLogger(AssignRangierungByCarrierDetailsCommand.class);

    /**
     * Key fuer die prepare-Methode, um die CarrierEquipment-Details fuer die Rangierungsermittlung zu uebergeben.
     */
    public static final String CARRIER_EQUIPMENT_DETAILS = "ce.details";

    private CarrierEquipmentDetails ceDetails = null;
    private Endstelle endstelle = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        Long endstelleId;
        try {
            endstelleId = (Long) getPreparedValue(ENDSTELLE_ID);
            LOGGER.debug("Endstelle, fuer die eine Rangierung gesucht wird: " + endstelleId);

            this.ceDetails = (CarrierEquipmentDetails) getPreparedValue(CARRIER_EQUIPMENT_DETAILS);
            if (ceDetails == null) {
                throw new HurricanServiceCommandException(
                        "Es sind keine Equipment-Details definiert. Rangierungen koennen nicht ermittelt werden!");
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(HurricanServiceCommandException.INVALID_PARAMETER_4_COMMAND);
        }

        endstelle = loadEndstelle(endstelleId);

        Rangierung rangierung = findRangierung();
        if (rangierung != null) {
            // Rangierung der Endstelle zuordnen und Physiktyp auf Rangierung setzen
            endstelle.setRangierId(rangierung.getId());
            getEndstelleDAO().store(endstelle);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);

            rangierung.setEsId(endstelleId);
            rangierung.setFreigabeAb(null);
            rangierung.setPhysikTypId(ceDetails.getPhysiktypId());
            rangierung.setBemerkung("Rangierungsermittlung u. -zuordnung durch Hurrican");
            getRangierungDAO().store(rangierung);

            return rangierung;
        }
        else {
            throw new HurricanServiceCommandException(
                    "Es konnte keine freie Rangierung fuer <" + ceDetails.getName() + "> ermittelt werden.");
        }
    }

    /* Ermittelt eine freie Physik ueber die CarrierEquipment-Details. */
    private Rangierung findRangierung() throws FindException {
        try {
            List<Rangierung> freeRangs = getRangierungDAO().findFrei(endstelle.getHvtIdStandort(), ceDetails);
            if (CollectionTools.isNotEmpty(freeRangs)) {
                // erste freie Rangierung wird verwendet
                return freeRangs.get(0);
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung von freien Rangierungen: " + e.getMessage(), e);
        }
    }

}
