/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2005 14:04:35
 */
package de.augustakom.hurrican.service.cc.impl.command.physik;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Funktion wie Super-Klasse {@link MoveEinwahlAccountCommand}.<br> Allerdings wird vor der Account-Uebernahme der
 * Benutzer 'gefragt' (ueber das Interface IServiceCallback), ob der Account wirklich uebernommen werden soll. <br> <br>
 * WICHTIG: die Frage, ob der Account uebernommen werden soll wird nur dann gestellt, wenn die beteiligten Auftraege dem
 * gleichen(!) Kunden zugeordnet sind.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.physik.AskAndMoveEinwahlAccountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AskAndMoveEinwahlAccountCommand extends MoveEinwahlAccountCommand {

    private static final Logger LOGGER = Logger.getLogger(AskAndMoveEinwahlAccountCommand.class);

    private AuftragTechnik atSrc = null;
    private IServiceCallback serviceCallback = null;

    @Override
    public Object executeAfterFlush() throws Exception {
        loadRequiredData();

        if ((atSrc.getIntAccountId() != null) && isSameCustomer() && askMoveAccount()) {
            return super.executeAfterFlush();
        }

        return null;
    }

    /*
     * Nachfrage ueber ein ServiceCallback, ob der Einwahlaccount uebernommen werden soll.
     */
    private boolean askMoveAccount() {
        try {
            AccountService accs = getCCService(AccountService.class);
            IntAccount account = accs.findIntAccountById(atSrc.getIntAccountId());
            if ((account != null) && account.isEinwahlaccount()) {
                Map<String, IntAccount> params = new HashMap<String, IntAccount>();
                params.put(RangierungsService.CALLBACK_PARAM_ACCOUNT, account);

                Object result = serviceCallback.doServiceCallback(this,
                        RangierungsService.CALLBACK_ASK_4_ACCOUNT_UEBERNAHME, params);
                if ((result instanceof Boolean) && ((Boolean) result).booleanValue()) {
                    // Einwahlaccount uebernehmen
                    return true;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Bei der Nachfrage fuer die Uebernahme des Einwahlaccounts ist ein Fehler aufgetreten." +
                    "\nEinwahlaccount wurde nicht uebernommen.\nFehler: " + e.getMessage());
        }

        return false;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        atSrc = getAuftragTechnikTx(getAuftragIdSrc());

        Object tmp = getPreparedValue(KEY_SERVICE_CALLBACK);
        if (tmp instanceof IServiceCallback) {
            serviceCallback = (IServiceCallback) tmp;
        }

        if (serviceCallback == null) {
            addWarning(this, "Es konnte kein ein IServiceCallback-Objekt ermittelt werden, ueber " +
                    "das der Benutzer nach der Account-Uebernahme gefragt werden kann.");
        }
    }

}


