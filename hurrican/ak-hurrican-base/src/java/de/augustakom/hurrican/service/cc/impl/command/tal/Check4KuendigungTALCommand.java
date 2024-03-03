/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 08:14:19
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.cc.CarrierService;


/**
 * Command-Klasse, um die Daten einer Carrierbestellung fuer den Vorgang 'Kuendigung' zu pruefen. <br> Folgende
 * Pruefungen uebernimmt die Command-Klasse: <br> <ul> <li>Datum der urspruenglichen Rueckmeldung muss gesetzt sein
 * <li>LBZ muss gesetzt sein (bei Carrier DTAG) <li>Kuendigungsbestaetigung darf nicht gefuellt sein </ul> <br>
 * Zusaetzlich ueberschreibt das Command das Datum der Kuendigungsuebermittlung in der Carrierbestellung.
 *
 *
 */
public class Check4KuendigungTALCommand extends AbstractTALCommand {

    private static final Logger LOGGER = Logger.getLogger(Check4KuendigungTALCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            Carrierbestellung cb = getCarrierbestellung();
            if (cb.getZurueckAm() == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Carrierbestellung besitzt kein Rueckmeldedatum von der Bestellung!", getClass());
            }

            if (NumberTools.equal(cb.getCarrier(), Carrier.ID_DTAG) && StringUtils.isBlank(cb.getLbz())) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Carrierbestellung besitzt keine LBZ - Kuendigung nicht moeglich!", getClass());
            }

            if (cb.getKuendBestaetigungCarrier() != null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die Carrierbestellung besitzt bereits eine Kuendigungsbestaetigung!", getClass());
            }

            // Kuendigungsdatum setzen
            cb.setKuendigungAnCarrier(new Date());
            CarrierService cs = (CarrierService) getCCService(CarrierService.class);
            cs.saveCB(cb);

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung der Kuendigungsdaten: " + e.getMessage(), getClass());
        }
    }

}


