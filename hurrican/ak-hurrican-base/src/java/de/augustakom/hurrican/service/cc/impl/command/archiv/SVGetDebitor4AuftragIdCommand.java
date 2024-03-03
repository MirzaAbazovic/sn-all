/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2007 11:59:55
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;
import org.apache.log4j.Logger;


/**
 * ernittelt die R_INFO__NO zu einem Auftrag
 *
 *
 */
public class SVGetDebitor4AuftragIdCommand extends AbstractArchivCommand {

    private static final Logger LOGGER = Logger.getLogger(SVGetDebitor4AuftragIdCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public List execute() throws Exception {
        try {
            return getDebitor4Auftrag((Long) getPreparedValue(ORDER__NO));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }
}


