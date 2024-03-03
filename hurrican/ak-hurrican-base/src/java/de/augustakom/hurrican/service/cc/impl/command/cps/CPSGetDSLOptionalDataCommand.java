/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2009 07:17:08
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;


/**
 *
 * @see CPSGetDSLDataCommand Die Ermittlung der DSL-Daten ist fuer dieses Command jedoch optional.
 */
public class CPSGetDSLOptionalDataCommand extends CPSGetDSLDataCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSDataCommand#isNecessary()
     */
    @Override
    protected boolean isNecessary() {
        return false;
    }

}


