/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2007 16:15:22
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.Account;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Command-Klasse, um die Account-Daten eines Auftrags zu pruefen, bevor ein Bauauftrag erstellt wird. <br><br> Folgende
 * Punkte werden geprueft: <br> - pruefen, ob Account vorhanden ist <br> - pruefen, ob Rufnummer in Account gesetzt ist
 * (falls Produkt entsprechend konfiguriert) <br>
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAccountsCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAccountsCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckAccountsCommand.class);

    private BillingAuftragService billingAuftragService;

    @PostConstruct
    public void init() throws ServiceNotFoundException {
        setBillingAuftragService(getBillingService(BillingAuftragService.class));
    }

    /**
     * Gibt an, ob ein Account auf jeden Fall notwendig ist.
     *
     * @return
     */
    protected boolean isAccountNecessary() {
        return true;
    }

    /**
     * liefert die AccountId fuer die gegebene AuftragId. Falls ein Account notwendig ist, aber keine Account gefunden
     * wurde, wird eine {@link FindException} geworfen.
     *
     * @param auftragId        Die Id des Auftrags fuer den der Account gesucht wird.
     * @param accountNecessary Ist ein Account notwendig?
     * @return Die AccountId.
     * @throws FindException
     */
    protected Long getAccountId(Long auftragId, boolean accountNecessary) throws FindException {
        AuftragTechnik auftragTechnik = getAuftragTechnikTx(auftragId);
        Long accountId = auftragTechnik.getIntAccountId();
        if (accountNecessary && (accountId == null)) {
            throw new FindException("Dem Auftrag ist kein Account zugeordnet. " +
                    "Bauauftrag wurde nicht erstellt!");
        }
        return accountId;
    }

    /**
     * liefert einen {@link IntAccount} fuer die gegegebene accountId.
     *
     * @param accountId
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    protected IntAccount getAccount(Long accountId) throws FindException, ServiceNotFoundException {
        AccountService service = getCCService(AccountService.class);
        IntAccount account = service.findIntAccountById(accountId);
        return account;
    }

    protected ServiceCommandResult invalidResult(String message) {
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                message, getClass());
    }

    protected ServiceCommandResult okResult() {
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    protected boolean checkMustAccountBeLocked(Long auftragId) throws FindException, ServiceNotFoundException {
        CCLeistungsService service = getCCService(CCLeistungsService.class);
        boolean result = service.checkMustAccountBeLocked(auftragId);
        return result;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            final Long accountId = getAccountId(getAuftragId(), isAccountNecessary());
            final IntAccount account = getAccount(accountId);

            ServiceCommandResult result = okResult();
            if (account == null) {
                if (isAccountNecessary()) {
                    result = invalidResult("Dem Auftrag ist kein Account zugeordnet.");
                }
            }
            else if (account.isEinwahlaccountGesperrt()) {
                if (isAccountNecessary() && !checkMustAccountBeLocked(getAuftragId())) {
                    result = invalidResult("Der Account, der dem Auftrag zugeordnet ist, ist gesperrt!");
                }
            }
            else {
                result = checkAndUpdateTaifunAccount(account);
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getCause(), e);
            return invalidResult("Bei der Ueberpruefung der Account-Daten ist ein nicht erwarteter Fehler aufgetreten: " +
                    e.getMessage());
        }
    }

    /* Ueberprueft und erstellt ggf. den Account in Taifun. */
    ServiceCommandResult checkAndUpdateTaifunAccount(IntAccount account) throws StoreException, FindException {
        // pruefen, ob im Billing-System der gleiche Account eingetragen ist
        BAuftrag billingAuftrag = getBillingAuftrag();
        if (billingAuftrag != null) {
            List<Account> billingAccounts = billingAuftragService.findAccounts4Auftrag(billingAuftrag.getAuftragNo());
            filterBillingAccounts(billingAccounts);

            if (CollectionTools.isEmpty(billingAccounts)) {
                // Insert
                billingAuftragService.updateAccount(billingAuftrag.getAuftragNo(), account);
            }
            else {
                boolean accountOK = false;

                // Pruefe Accounts
                for (Account acc : billingAccounts) {
                    if (StringUtils.equals(StringUtils.trimToNull(acc.getAccountId()), account.getAccount())) {
                        accountOK = true;
                        if (!StringUtils.equals(acc.getPassword(), account.getPasswort())) {
                            // Update
                            billingAuftragService.updateAccount(billingAuftrag.getAuftragNo(), account);
                        }
                    }
                }
                if (!accountOK) {
                    return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            "In Taifun ist ein anderer Account eingetragen als im " +
                                    "Hurrican-System - bitte korrigieren.", getClass()
                    );
                }
            }
        }
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /*
     * Filtert aus der angegebenen Liste der Taifun-Accounts alle
     * Accounts heraus, die ein '_' im Namen besitzen.
     */
    void filterBillingAccounts(List<Account> accounts) {
        CollectionUtils.filter(accounts, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                Account account = (Account) obj;
                if (account.getAccountId().contains("_")) {
                    return false;
                }
                return true;
            }
        });
    }


    /**
     * @param billingAuftragService The billingAuftragService to set.
     */
    public void setBillingAuftragService(BillingAuftragService billingAuftragService) {
        this.billingAuftragService = billingAuftragService;
    }

} // end
