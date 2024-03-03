/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2009 12:58:03
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.LockDetail;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.LockService;


/**
 * Command-Klasse, um Referenzierungen auf eine CPS-Transaction zu entfernen. <br> Dies wird fuer Stornos einer CPS-Tx
 * sowie (vorsichtshalber) im Error-Fall von 'createCPSTx' benoetigt.
 *
 *
 */
public class DisjoinCPSTxCommand extends AbstractCPSCommand {

    private static final Logger LOGGER = Logger.getLogger(DisjoinCPSTxCommand.class);

    private CCRufnummernService ccRufnummernService;
    private LockService lockService;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            disjoinDNServices();
            disjoinLocks();

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error disjoining CPS-Transaction (ID: " + getCPSTxId() + "): " + e.getMessage(), e);
        }
    }

    /*
     * Entfernt die CPS-Tx Referenzierung von Rufnummernleistungen.
     */
    private void disjoinDNServices() throws ServiceCommandException {
        try {
            // Rufnummernleistungen ermitteln, die durch die CPS-Tx provisioniert wurden
            List<Leistung2DN> dnServices = ccRufnummernService.findLeistung2DN4CPSTx(getCPSTxId());
            if (CollectionTools.isNotEmpty(dnServices)) {
                for (Leistung2DN toDisjoin : dnServices) {
                    if (NumberTools.equal(toDisjoin.getCpsTxIdCreation(), getCPSTxId())) {
                        toDisjoin.setEwsdRealisierung(null);
                        toDisjoin.setEwsdUserRealisierung(null);
                        toDisjoin.setCpsTxIdCreation(null);
                    }

                    if (NumberTools.equal(toDisjoin.getCpsTxIdCancel(), getCPSTxId())) {
                        toDisjoin.setEwsdKuendigung(null);
                        toDisjoin.setEwsdUserKuendigung(null);
                        toDisjoin.setCpsTxIdCancel(null);
                    }

                    ccRufnummernService.saveLeistung2DN(toDisjoin);

                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Disjoined DN service " + toDisjoin.getId() + " from CPS transaction " + getCPSTxId());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error disjoining DN services: " + e.getMessage(), e);
        }
    }

    /*
     * Entfernt die CPS-Tx Referenzierungen von Sperr-Eintraegen.
     */
    private void disjoinLocks() throws ServiceCommandException {
        try {
            LockDetail example = new LockDetail();
            example.setCpsTxId(getCPSTxId());

            List<LockDetail> lockDetails4CPSTx = lockService.findLockDetailsByExample(example);
            if (CollectionTools.isNotEmpty(lockDetails4CPSTx)) {
                for (LockDetail lockDetailToDisjoin : lockDetails4CPSTx) {
                    lockDetailToDisjoin.setCpsTxId(null);
                    lockDetailToDisjoin.setExecutedAt(null);
                    lockDetailToDisjoin.setExecutedFrom(null);

                    lockService.saveLockDetail(lockDetailToDisjoin);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error disjoining locks: " + e.getMessage(), e);
        }
    }


    /**
     * Injected
     */
    public void setCcRufnummernService(CCRufnummernService ccRufnummernService) {
        this.ccRufnummernService = ccRufnummernService;
    }


    /**
     * Injected
     */
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

}


