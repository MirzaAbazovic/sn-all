/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2007 08:48:48
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;


/**
 * Liefert im moment nur die bereits bekannte Kunde__NO zurück
 *
 *
 */
public class SVGetKundeNoCommand extends AbstractArchivCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        List result = new ArrayList();
        result.add(getPreparedValue(KUNDE__NO).toString());
        return result;
    }
}


