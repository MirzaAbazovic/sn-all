package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * CheckDnPortierungskennungCommand prueft, ob fuer allen angegebenen Rufnummern eine Portierungskennung vorhanden ist.
 * Dieses Command prueft nicht, ob der Auftrag / das Produkt eine Rufnummer benoetigt - diese Pruefung muss, falls
 * benoetigt, durch andere Commands erfolgen.
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDnPortierungskennungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDnPortierungskennungCommand extends AbstractVerlaufCheckCommand {

    @Override
    public Object execute() throws Exception {
        try {
            final List<Rufnummer> rufnummern = getRufnummern();

            // Portierungskennungen pruefen
            for (Rufnummer rn : rufnummern) {
                if (StringUtils.isBlank(rn.getActCarrierPortKennung()) || rn.isMobile()) {
                    throw new FindException(
                            String.format(
                                    "Die Portierungskennung fuer %s ist nicht vorhanden oder es handelt sich um eine Mobilfunk-Nummer.",
                                    rn));
                }
            }
        }
        catch (FindException e) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }
}
