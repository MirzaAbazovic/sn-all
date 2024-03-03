/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2012 14:57:01
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.time.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Berechnet fuer den Default-EKP (ist "MNET" mit Contract "MNET-001") die benötigten VLANs und speichert diese.
 * <p/>
 * Das Command darf fuer einen Auftrag mehrmals angezogen werden. Sollten die berechneten von den tatsaechlich
 * existierenden VLANs abweichen, so werden die alten historisiert abgelegt und die Neuen persistiert.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.AssignVlanCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AssignVlanCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(AssignVlanCommand.class);

    @Autowired
    VlanService vlanService;
    @Autowired
    EkpFrameContractService ekpFrameContractService;

    @Override
    public Object execute() throws Exception {
        try {
            LocalDate when = (getRealDate() != null) ? DateConverterUtils.asLocalDate(getRealDate()) : LocalDate.now();
            Long auftragId = getAuftragId();
            Long productId = getProdukt().getId();

            EkpFrameContract ekp = ekpFrameContractService.findEkp4AuftragOrDefaultMnet(auftragId, when, true);
            if (ekp == null) {
                throw new StoreException(
                        "VLANs koennen nicht berechnet werden, da fuer den Auftrag kein EKP frame-contract vorhanden ist!");
            }

            vlanService.assignEqVlans(ekp, auftragId, productId, when, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Beim Anlegen der VLANs ist ein Fehler aufgetreten: " + e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }
}
