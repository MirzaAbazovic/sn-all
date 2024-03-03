/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2006 09:01:06
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Command-Klasse, um einen Einwahlaccount auf einem Auftrag anzulegen (falls nicht schon vorhanden). <br> Command
 * bezieht sich auf die technische Leistung 'Einwahlaccount' (EXT_LEISTUNG__NO=10007).
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CreateEinwahlaccountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateEinwahlaccountCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateEinwahlaccountCommand.class);

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        createOrUnlockAccount();
        return null;
    }

    /*
     * Ueberprueft, ob dem Auftrag ein Einwahlaccount zugeordnet ist.
     * Ist dies nicht der Fall, wird ein neuer Einwahlaccount angelegt.
     * Besitzt der Auftrag einen gesperrten Einwahlaccount, wird dieser entsperrt.
     */
    private void createOrUnlockAccount() throws HurricanServiceCommandException {
        try {
            AccountService accs = getCCService(AccountService.class);
            List<IntAccount> intAccounts =
                    accs.findIntAccounts4Auftrag(getAuftragId(), IntAccount.LINR_EINWAHLACCOUNT);

            if (CollectionTools.isEmpty(intAccounts)) {
                AuftragTechnik at = getAuftragTechnikTx(getAuftragId());

                // Einwahlaccount erstellen
                CCLeistungsService ccls = getCCService(CCLeistungsService.class);
                TechLeistung blk = ccls.findTechLeistungByExtLstNo(ExterneLeistung.EINWAHLACCOUNT.leistungNo);
                accs.createIntAccount(at, blk.getParameter(), IntAccount.LINR_EINWAHLACCOUNT);
            }
            else {
                // falls Account gesperrt, wieder entsperren (letzen Account verwenden)
                IntAccount acc = intAccounts.get(intAccounts.size() - 1);
                if (BooleanTools.nullToFalse(acc.getGesperrt())) {
                    acc.setGesperrt(Boolean.FALSE);
                    accs.saveIntAccount(acc, false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim Anlegen des Einwahlaccounts fuer die technische Leistung " + getTechLeistungName(), e);
        }
    }

}


