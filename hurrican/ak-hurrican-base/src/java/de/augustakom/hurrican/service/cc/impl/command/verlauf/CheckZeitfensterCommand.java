/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2009 12:05:54
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragKombi;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Command prueft die Zuordnung eines Realisierungszeitfensters zum Auftrag.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckZeitfensterCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckZeitfensterCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckZeitfensterCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Override
    public Object execute() throws Exception {
        try {
            if (isFttcKvz()) {
                // Sonderfall: bei FTTC (KVZ) sind die Zeitfenster nicht notwendig!
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
            }

            // Ermittle Taifun-Auftrag
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(getAuftragId());
            if (auftragDaten == null || auftragDaten.getAuftragNoOrig() == null) {
                throw new FindException("Der Hurrican-Auftrag ist keinem Taifun-Auftrag zugeordnet.");
            }

            BAuftrag billingAuftrag = billingAuftragService.findAuftrag(auftragDaten.getAuftragNoOrig());
            BAuftragKombi auftragKombi = billingAuftrag == null ? null : billingAuftragService.findAuftragKombiByAuftragNo(billingAuftrag.getAuftragNo());
            if (auftragKombi == null) {
                throw new FindException(String.format("Taifun-Auftrag %s kann nicht ermittelt werden.", auftragDaten.getAuftragNoOrig()));
            }

            if (auftragKombi.getTimeSlotNo() == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        String.format("Dem Taifun Auftrag %s ist kein Realisierungszeitfenster zugeordnet!", auftragDaten.getAuftragNoOrig()), getClass());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Bei der Ueberpruefung des Realisierungszeitfensters ist ein Fehler aufgetreten: %s", e.getMessage()), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }


    boolean isFttcKvz() throws FindException {
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        if (endstelleB != null && endstelleB.getHvtIdStandort() != null) {
            HVTStandort hvtStandort = hvtService.findHVTStandort(endstelleB.getHvtIdStandort());
            return hvtStandort != null && hvtStandort.isFttc();
        }

        return false;
    }

}


