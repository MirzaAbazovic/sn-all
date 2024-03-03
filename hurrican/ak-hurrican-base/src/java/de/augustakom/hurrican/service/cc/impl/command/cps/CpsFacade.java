/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.2017
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static java.lang.String.*;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Sammelklasse für Methoden, die in verschiedenen CpsCommands eingesetzt werden
 * Created by marteleu on 03.02.2017.
 */
public class CpsFacade {

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    protected EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    protected RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    public Endstelle getEndstelleBWithStandortId(long auftragId) throws FindException {
        final Endstelle endstelleB = findEndstelleB(auftragId);
        if (endstelleB.getHvtIdStandort() == null) {
            throw new RuntimeException("Dem Auftrag %s ist kein HVT-Standort zugewiesen");
        }
        return endstelleB;
    }

    public Endstelle findEndstelleB(final Long auftragId) throws FindException {
        Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId,
                Endstelle.ENDSTELLEN_TYP_B);
        if (esB.getRangierId() == null) {
            throw new FindException("Endstelle B besitzt keine Rangierung!");
        }
        return esB;
    }

    public Equipment findFttxPort(Endstelle endstelleB) throws FindException {
        Equipment fttxPort;
        final Rangierung fttxRangierung = findRangierung(endstelleB);
        fttxPort = findEquipment(fttxRangierung);
        return fttxPort;
    }

    protected final Rangierung findRangierung(Endstelle esB) throws FindException {
        Rangierung rang = rangierungsService.findRangierung(esB.getRangierId());
        if ((rang == null) || (rang.getEqInId() == null)) {
            throw new FindException("Rangierung nicht gefunden oder FTTX-Equipment nicht definiert!");
        }
        return rang;
    }

    protected final Equipment findEquipment(Rangierung rangierung) throws FindException {
        Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
        if (eqIn == null) {
            throw new FindException("EQ-IN Equipment konnte nicht geladen werden!");
        }
        if (eqIn.getHwBaugruppenId() == null) {
            throw new FindException("EQ-IN Equipment ist keiner Baugruppe zugeordnet!");
        }
        return eqIn;
    }

    public HWBaugruppe findBaugruppeByPort(Equipment port) throws FindException {
        if (port.getHwBaugruppenId() == null) {
            throw new RuntimeException(format("Dem Port mit Id %s ist keine Baugruppe zugewiesen", port.getId()));
        }
        return hwService.findBaugruppe(port.getHwBaugruppenId());
    }

    public HWRack findRackByPort(Equipment port) throws FindException {
        HWBaugruppe bg = findBaugruppeByPort(port);
        if (bg != null) {
            return hwService.findRackForBaugruppe(bg.getId());
        }
        return null;
    }
}