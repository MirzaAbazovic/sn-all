/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 14:26:37
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.AuftragInternService;


/**
 * Command prueft, ob bei internen Arbeitsauftraegen mindestens folgende Daten definiert sind: <ul> <li>Service-Raum
 * (HVT) <li>Arbeitstyp <li>Buendel-Nummer (bei Zusatz-Auftrag) </ul>
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAuftragInternCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAuftragInternCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAuftragInternCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            AuftragInternService ais = getCCService(AuftragInternService.class);
            AuftragIntern ai = ais.findByAuftragId(getAuftragId());

            if (ai == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Der interne Arbeitsauftrag ist nicht definiert!", getClass());
            }
            else if (ai.getHvtStandortId() == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Der interne Arbeitsauftrag ist keinem Service-Raum zugeordnet!", getClass());
            }
            else if (ai.getWorkingTypeRefId() == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Fuer den internen Arbeitsauftrag ist kein Arbeitstyp definiert!", getClass());
            }
            else {
                // pruefen, ob bei Zusatz-Auftrag eine Buendel-Nr definiert ist
                Produkt prod = getProdukt();
                if ((prod != null) && !BooleanTools.nullToFalse(prod.getIsParent())) {
                    // Zusatz-Auftrag benoetigt Buendel-Nummer
                    AuftragDaten ad = getAuftragDatenTx(getAuftragId());
                    if (ad.getBuendelNr() == null) {
                        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                                "Fuer den Zusatz-Auftrag ist kein Buendel definiert!", getClass());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Ueberpruefung der internen Auftraege ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

}


