/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.2010 08:36:04
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;


import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Ableitung von {@code CheckAccountsCommand}. In diesem Fall ist es nicht zwingend notwendig, dass der Auftrag einen
 * Account besitzt.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAccountsOptionalCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckAccountsOptionalCommand extends CheckAccountsCommand {

    @Override
    protected boolean isAccountNecessary() {
        return false;
    }

}


