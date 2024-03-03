/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 09:59:02
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Command prueft, ob fuer den Auftrag eine ASB-Kennung ermittelt werden kann. Dies ist dann der Fall, wenn dem Auftrag
 * (bzw. der Endstelle B) ein technischer Standort mit entsprechender Information zugeordnet ist.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAsbDefinitionCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAsbDefinitionCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAsbDefinitionCommand.class);

    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private HVTService hvtService;

    private Endstelle endstelleB;

    @Override
    public Object execute() throws Exception {
        try {
            loadRequiredData();
            checkAsbKennung();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der ASB-Kennung ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * Prueft, ob die Endstelle B des Auftrags einen technischen Standort mit ASB-Kennung besitzt.
     *
     * @throws FindException
     */
    void checkAsbKennung() throws FindException {
        try {
            if ((endstelleB == null) || (endstelleB.getHvtIdStandort() == null)) {
                throw new FindException("Endstelle B ist keinem technischen Standort zugeordnet!");
            }

            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelleB.getHvtIdStandort());
            if (hvtStandort == null) {
                throw new FindException("Der zugeordnete Standort konnte nicht ermittelt werden!");
            }
            else if (hvtStandort.getAsb() == null) {
                throw new FindException("Der zugeordnete Standort besitzt keine ASB Kennzeichnung!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Prüfung der ASB Kennzeichnung: " + e.getMessage(), e);
        }
    }

    @Override
    protected void loadRequiredData() throws FindException {
        try {
            setEndstelleB(endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B));
            if (endstelleB == null) {
                throw new FindException("Endstelle B des Auftrags konnte nicht ermittelt werden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Daten zur HVT-Ueberpruefung: " + e.getMessage(), e);
        }
    }

    public void setEndstelleB(Endstelle endstelleB) {
        this.endstelleB = endstelleB;
    }

    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }
}


