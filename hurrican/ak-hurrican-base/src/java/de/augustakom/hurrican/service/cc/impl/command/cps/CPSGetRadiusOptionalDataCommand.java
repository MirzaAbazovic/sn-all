/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2009 07:20:33
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;


/**
 *
 * @see CPSGetRadiusDataCommand Die Ermittlung der Radius-Daten ist fuer dieses Command jedoch optional.
 */
public class CPSGetRadiusOptionalDataCommand extends CPSGetRadiusDataCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSDataCommand#isNecessary()
     */
    @Override
    protected boolean isNecessary() {
        return false;
    }

}


