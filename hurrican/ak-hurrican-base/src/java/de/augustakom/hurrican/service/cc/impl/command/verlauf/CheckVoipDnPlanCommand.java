/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.11.13
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Command prüft, ob Blockrufnummern mindestens ein gültiger Rufnummernplan zugeordnet ist.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckVoipDnPlanCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckVoipDnPlanCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckVoipDnPlanCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voipService;

    @Override
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    public Object execute() throws Exception {
        try {
            for (final AuftragVoipDNView dnView : voipService.findVoIPDNView(getAuftragId())) {
                if (!dnView.isBlock() || hasAtLeastOneValidDnPlan(dnView)) {
                    continue;
                }

                final String formattedDate = DateTools.formatDate(getRealDate(), DateTools.PATTERN_DATE_TIME);
                final String msg = String.format(
                        "Zu der Blockrufnummer %s ist kein Rufnummernplan, der zum %s gültig ist, zugeordnet",
                        dnView.getFormattedRufnummer(), formattedDate);

                return ServiceCommandResult.createCmdResult(
                        ServiceCommandResult.CHECK_STATUS_INVALID, msg, getClass());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Bei der Überprüfung der Rufnummernpläne ist ein Fehler aufgetreten: %s",
                            e.getMessage()), getClass()
            );
        }
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    private boolean hasAtLeastOneValidDnPlan(final AuftragVoipDNView dnView) {
        final Date realDate = getRealDate();
        return realDate == null || dnView.getMostCurrentDnPlanView(realDate).isPresent();
    }
}
