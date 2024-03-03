/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2009 11:10:11
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse prueft, ob dem Deluxe-Auftrag eine passende Physik zugeordnet ist.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckPhysik4DeluxeCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckPhysik4DeluxeCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckPhysik4DeluxeCommand.class);

    @Override
    public Object execute() throws Exception {
        try {
            // Pruefe, ob
            // - die Rangierung einen EQ-In Stift besitzt.
            // - der Rangierung eine aktive Baugruppe zugeordnet ist.
            RangierungsService rs = getCCService(RangierungsService.class);
            HWService hws = getCCService(HWService.class);
            Rangierung[] rangs = rs.findRangierungen(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
            if ((rangs == null) || (rangs[0] == null)) {
                throw new FindException("Dem Auftrag ist keine Rangierung zugeordnet.");
            }
            Equipment eq = rs.findEquipment(rangs[0].getEqInId());
            if (eq == null) {
                throw new FindException("Der Rangierung ist kein EQ-In Port zugewiesen.");
            }
            HWBaugruppe bg = hws.findBaugruppe(eq.getHwBaugruppenId());
            if (bg == null) {
                throw new FindException("Die Rangierung besitzt keine Baugruppe.");
            }
            HWRack rack = hws.findRackById(bg.getRackId());
            if ((rack == null) || !BooleanTools.nullToFalse(bg.getEingebaut())
                    || DateTools.isDateAfter(rack.getGueltigVon(), new Date())) {
                throw new FindException("Baugruppe der Rangierung ist noch nicht aktiv.");
            }

        }
        catch (FindException e) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der Rufnummernanzahl ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


