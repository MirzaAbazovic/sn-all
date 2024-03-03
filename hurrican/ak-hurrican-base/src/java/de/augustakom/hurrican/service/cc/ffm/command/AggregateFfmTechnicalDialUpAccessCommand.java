/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DialUpAccessBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.cc.AccountService;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>dialUpAccess</li>
 *     <ul>
 *         <li>accountId</li>
 *         <li>password</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalDialUpAccessCommand")
@Scope("prototype")
public class AggregateFfmTechnicalDialUpAccessCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalDialUpAccessCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.AccountService")
    private AccountService accountService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();
            final Long auftragId = getAuftragDaten().getAuftragId();
            final List<IntAccount> accounts = accountService.findIntAccounts4Auftrag(auftragId);
            final String realm = accountService.getAccountRealm(auftragId);
            if (CollectionUtils.isNotEmpty(accounts)) {
                for (IntAccount account : accounts) {
                    getWorkforceOrder().getDescription().getTechParams().getDialUpAccess().add(
                            new DialUpAccessBuilder()
                                    .withAccountId(account.getAccount())
                                    .withPassword(account.getPasswort())
                                    .withRealm(realm)
                                    .build());
                }
            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams DialUpAccess Data: " + e.getMessage(), this.getClass());
        }
    }

}
