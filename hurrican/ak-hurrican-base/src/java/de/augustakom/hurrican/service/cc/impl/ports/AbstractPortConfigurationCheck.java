/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 14:41:01
 */
package de.augustakom.hurrican.service.cc.impl.ports;

import java.util.*;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Abstrakte Klasse, um verschiedene Checks vor der Port-Generierung durchzufuehren.
 *
 *
 */
public abstract class AbstractPortConfigurationCheck {

    protected HWService hwService;

    /**
     * Fuehrt einen oder mehrere Checks durch.
     *
     * @param portGeneratorDetails
     * @param portsToGenerate
     * @return ServiceCommandResult das definiert, ob der Check OK oder fehlerhaft ist (niemals {@code null}).
     */
    public abstract ServiceCommandResult executeCheck(PortGeneratorDetails portGeneratorDetails, List<PortGeneratorImport> portsToGenerate);

    protected HWBaugruppenTyp getHwBaugruppenTyp(Long hwBaugruppeId) throws FindException {
        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(hwBaugruppeId);
        if (hwBaugruppe == null) {
            throw new FindException("Baugruppe konnte nicht ermittelt werden!");
        }
        if (hwBaugruppe.getHwBaugruppenTyp() == null) {
            throw new FindException("Baugruppentyp konnte nicht ermittelt werden!");
        }
        return hwBaugruppe.getHwBaugruppenTyp();
    }

    /**
     * Injected
     */
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }
}


