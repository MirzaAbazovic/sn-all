/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.02.2008 09:06:49
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
 * Command-Klasse, um die QoS-Daten aus einem Auftrag zu loeschen.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CancelQoSCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CancelQoSCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(CancelQoSCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            Date gueltigBis = (getAktivBis() != null) ? getAktivBis() : new Date();

            QoSService qosService = getCCService(QoSService.class);
            List<AuftragQoS> existing = qosService.findQoS4Auftrag(getAuftragId(), true);
            if (CollectionTools.isNotEmpty(existing)) {
                for (AuftragQoS qos : existing) {
                    qos.setGueltigBis(gueltigBis);
                    qosService.saveAuftragQoS(qos, getSessionId());
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
    }

}


