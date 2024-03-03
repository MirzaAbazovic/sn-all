/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:13:46
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.cc.AuftragMVS;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.MVSService;

/**
 * Command-Klasse prueft, ob MVS Site Auftrag vorhanden ist. Muss hier nicht validiert werden, da dies beim Speichern
 * passiert.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckMVSSiteCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckMVSSiteCommand extends AbstractCheckMVSCommand {

    @Override
    protected AuftragMVS findAuftragMVS(MVSService mvsService) throws FindException {
        return mvsService.findMvsSite4Auftrag(getAuftragId(), false);
    }


}
