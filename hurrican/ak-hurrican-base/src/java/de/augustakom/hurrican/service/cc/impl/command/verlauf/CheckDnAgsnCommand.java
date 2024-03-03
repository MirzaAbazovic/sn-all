package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * CheckDnAgsnCommand
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDnAgsnCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDnAgsnCommand extends AbstractVerlaufCheckCommand {

    @Override
    public Object execute() throws Exception {
        try {
            if (BooleanTools.nullToFalse(getProdukt().needsDn())) {
                CCAuftragService auftragService = getCCService(CCAuftragService.class);
                auftragService.checkAgsn4Auftrag(getAuftragId());
            }
        }
        catch (FindException e) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }
}
