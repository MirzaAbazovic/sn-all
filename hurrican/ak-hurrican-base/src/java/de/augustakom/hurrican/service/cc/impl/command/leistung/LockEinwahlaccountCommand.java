/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2006 09:06:00
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Command-Klasse, um einen evtl. vorhandenen Einwahlaccount auf einem Auftrag zu sperren. <br> Command bezieht sich auf
 * die technische Leistung 'Einwahlaccount' (EXT_LEISTUNG__NO=10007).
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.LockEinwahlaccountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LockEinwahlaccountCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(LockEinwahlaccountCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        lockAccount();
        return null;
    }

    /*
     * Sperrt einen evtl. vorhandenen Einwahlaccount. <br>
     * Sollte das Produkt jedoch mit Einwahlaccount konfiguriert sein,
     * wird die Sperre nicht durchgefuehrt!
     */
    private void lockAccount() throws HurricanServiceCommandException {
        try {
            ProduktService ps = getCCService(ProduktService.class);
            Produkt produkt = ps.findProdukt4Auftrag(getAuftragId());

            if (NumberTools.notEqual(produkt.getLiNr(), IntAccount.LINR_EINWAHLACCOUNT)) {
                AccountService accs = getCCService(AccountService.class);
                List<IntAccount> intAccounts =
                        accs.findIntAccounts4Auftrag(getAuftragId(), IntAccount.LINR_EINWAHLACCOUNT);
                if (CollectionTools.isNotEmpty(intAccounts)) {
                    IntAccount acc = intAccounts.get(intAccounts.size() - 1);
                    if (!BooleanTools.nullToFalse(acc.getGesperrt())) {
                        acc.setGesperrt(Boolean.TRUE);
                        accs.saveIntAccount(acc, true);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim Sperren des Einwahlaccounts wg. Wegfall der Leistung " + getTechLeistungName(), e);
        }
    }
}

