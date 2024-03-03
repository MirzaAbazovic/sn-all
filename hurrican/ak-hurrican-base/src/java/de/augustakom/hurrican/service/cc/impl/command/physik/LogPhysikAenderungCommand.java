/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 15:47:17
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um eine Physik-Aenderung zu protokollieren.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.LogPhysikAenderungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LogPhysikAenderungCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(LogPhysikAenderungCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object executeAfterFlush() throws Exception {
        logPhysikAenderung();
        return null;
    }

    /*
     * Fuehrt die Protokollierung der Physik-Aenderung durch.
     */
    private void logPhysikAenderung() throws StoreException {
        try {
            RangierungsService rs = (RangierungsService) getCCService(RangierungsService.class);
            rs.logPhysikUebernahme(getAuftragIdSrc(), getAuftragIdDest(), getStrategy());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die Physik-Aenderung konnte nicht protokolliert werden!\nGrund: " +
                    e.getMessage(), e);
        }
    }

}


