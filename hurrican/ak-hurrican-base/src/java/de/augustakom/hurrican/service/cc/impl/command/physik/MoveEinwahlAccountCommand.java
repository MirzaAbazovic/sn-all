/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 13:32:57
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Diese Command-Klasse ordnet dem Ziel-Auftrag den Einwahlaccount des Ursprungs-Auftrags zu. <br> Sollte der
 * Ziel-Auftrag bereits einen Einwahlaccount gehabt haben, wird dieser gesperrt. <br> Die Uebernahme des Accounts
 * erfolgt nur dann, wenn der Ursprungs- und Ziel-Auftrag dem gleichen Kunden zugeordnet sind!
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.MoveEinwahlAccountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MoveEinwahlAccountCommand extends AbstractPhysikCommand {

    private static final Logger LOGGER = Logger.getLogger(MoveEinwahlAccountCommand.class);

    @Override
    public Object executeAfterFlush() throws Exception {
        moveEinwahlAccount(getAuftragTechnikTx(getAuftragIdSrc()), getAuftragTechnikTx(getAuftragIdDest()));
        return null;
    }

    /*
     * Uebernimmt den Einwahlaccount des alten Auftrags und sperrt den
     * Einwahlaccount, der dem Ziel-Auftrag zugeordnet war.
     */
    private void moveEinwahlAccount(AuftragTechnik atSrc, AuftragTechnik atDest) throws StoreException {
        try {
            // Einwahlaccount nur umhaengen, wenn unterschiedlich!
            if (isSameCustomer() && (atSrc.getIntAccountId() != null) &&
                    !NumberTools.equal(atDest.getIntAccountId(), atSrc.getIntAccountId())) {
                AccountService as = getCCService(AccountService.class);
                IntAccount intAcc = as.findIntAccountById(atSrc.getIntAccountId());
                if ((intAcc == null) || !intAcc.isEinwahlaccount()) {
                    addWarning(this, "Auf dem Ursprungs-Auftrag konnte kein Einwahl-Account fuer die " +
                            "Uebernahme ermittelt werden.");
                    return;
                }

                if (atDest.getIntAccountId() != null) {
                    as.disableAccount(atDest.getIntAccountId());
                }

                atDest.setIntAccountId(atSrc.getIntAccountId());
                CCAuftragService ccAS = getCCService(CCAuftragService.class);
                ccAS.saveAuftragTechnik(atDest, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Übernahme des Einwal-Accounts ist ein Fehler aufgetreten. ", e);
        }
    }

    /**
     * Ueberprueft, ob der Ursprungs- und Ziel-Auftrag dem gleichen Kunden zugeordnet sind.
     */
    protected boolean isSameCustomer() throws FindException {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            Auftrag aSrc = as.findAuftragById(getAuftragIdSrc());
            Auftrag aDest = as.findAuftragById(getAuftragIdDest());
            if ((aSrc != null) && (aDest != null) && NumberTools.equal(aSrc.getKundeNo(), aDest.getKundeNo())) {
                return true;
            }

            return false;
        }
        catch (Exception e) {
            throw new FindException("Bei der Ermittlung, ob der Einwahlaccount uebernommen werden soll, ist ein " +
                    "Fehler aufgetreten:\n" + e.getMessage(), e);
        }
    }

}


