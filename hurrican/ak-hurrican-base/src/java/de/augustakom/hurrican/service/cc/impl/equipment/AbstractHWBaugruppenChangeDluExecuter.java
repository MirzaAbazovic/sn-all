/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2010 08:35:28
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Abstrakte Klasse fuer die DLU-Executer.
 */
public abstract class AbstractHWBaugruppenChangeDluExecuter implements HWBaugruppenChangeExecuter {

    /**
     * Ermittelt alle Equipments der angegebenen DLU. Es werden dabei nur Baugruppen beruecksichtigt, die als
     * 'eingebaut' markiert sind.
     *
     * @param hwBgChangeDlu
     * @param hwService
     * @param rangierungsService
     * @return
     * @throws FindException
     */
    List<Equipment> readEquipments4Dlu(HWBaugruppenChangeDlu hwBgChangeDlu,
            HWService hwService, RangierungsService rangierungsService) throws FindException {
        if ((hwBgChangeDlu == null) || (hwService == null) || (rangierungsService == null)) {
            throw new IllegalArgumentException("Parameters not valid to load equipments!");
        }

        List<HWBaugruppe> baugruppen4Dlu = hwService.findBaugruppen4Rack(hwBgChangeDlu.getDluRackOld().getId());
        if (CollectionTools.isNotEmpty(baugruppen4Dlu)) {
            List<Equipment> dluEquipments = new ArrayList<Equipment>();
            for (HWBaugruppe hwBg : baugruppen4Dlu) {
                if (BooleanTools.nullToFalse(hwBg.getEingebaut())) {
                    List<Equipment> eqs = rangierungsService.findEquipments4HWBaugruppe(hwBg.getId());
                    dluEquipments.addAll(eqs);
                }
            }
            return dluEquipments;
        }
        return null;
    }

}


