/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.02.2008 09:01:30
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragQoS;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.QoSService;


/**
 * Command-Klasse, um zu einem Auftrag die Default-Werte fuer Quality-of-Service anzulegen.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CreateQoSCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateQoSCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateQoSCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            QoSService qosService = getCCService(QoSService.class);
            List<AuftragQoS> result = qosService.addDefaultQoS2Auftrag(getAuftragId(), getAktivVon(), getSessionId());
            if (CollectionTools.isEmpty(result)) {
                throw new Exception("Es wurden keine Default-Werte fuer QoS ermittelt und dem Auftrag zugeordnet!");
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Quality-of-Service Daten konnten auf dem Auftrag nicht angelegt werden!\n" +
                            "Die QoS-Daten muessen selbst erfasst werden! Fehlermeldung:\n" + e.getMessage(), e
            );
        }
    }

}


