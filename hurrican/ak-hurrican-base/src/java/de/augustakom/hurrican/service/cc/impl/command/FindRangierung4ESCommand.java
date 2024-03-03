/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.2015
 */
package de.augustakom.hurrican.service.cc.impl.command;

import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

// @formatter:off
/**
 * Command-Klasse, um fuer eine Endstelle eine neue Rangierung zu finden. <br/>
 * Im Gegensatz zu {@link de.augustakom.hurrican.service.cc.impl.command.AssignRangierung2ESCommand} wird in diesem
 * Command keine Zuordnung der Rangierung zum Auftrag bzw. zur Endstelle durchgefuehrt, sondern lediglich die
 * Rangierung(en) ermittelt und zurueck gegeben.
 */
// @formatter:on
@CcTxRequired
public class FindRangierung4ESCommand extends AssignRangierung2ESCommand {

    private static final Logger LOGGER = Logger.getLogger(FindRangierung4ESCommand.class);

    /**
     * Key zur Uebergabe der HVT-Id an das Command
     */
    public static final String HVT_ID_STANDORT = "hvt.id.standort";


    /**
     * Als Ergebnis wird ein {@link de.augustakom.common.tools.lang.Pair} zurueck gegeben: <br/>
     * <ul>
     *     <li>First: die erste Rangierung</li>
     *     <li>Second: wenn vorhanden, die zweite (zusaetzliche) Rangierung</li>
     * </ul>
     * @return
     * @throws FindException
     * @throws StoreException
     * @throws HurricanServiceCommandException
     */
    @Override
    public Object execute() throws FindException, StoreException, HurricanServiceCommandException {
        super.execute();
        return Pair.create(getRangierung4ES(), getRangierung4ESAdd());
    }

    @Override
    void bundleRangierungAndEndstelle() throws StoreException {
        LOGGER.info("Bundling of Rangierung / Endstelle not supported in FindRangierung4ESCommand!");
    }

    @Override
    protected Long getHvtIdStandort() throws FindException {
        return getPreparedValue(HVT_ID_STANDORT, Long.class, false, "HVT-Id wurde nicht angegeben!");
    }

    protected boolean checkKvzSperre() {
        return false;
    }

    protected void checkExistingRangierung(Endstelle endstelle) throws FindException {
        // nothing to do here
    }

}
