/**
 *
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
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * Command-Klasse, um einen evtl. vorhandenen Einwahlaccount auf einem Auftrag zu sperren.<br> Command bezieht sich auf
 * die technischen Leistungen '5000 kbit/s'/'UPSTREAM' und 'VOIP_PMX'/'VOIP'.<br> Es wird geprüft ob beides UPSTREAM als
 * auch PMX auf dem Auftrag gebucht sind. Sollte dies der Fall sein, so ist die Bandbreite niedrig und das
 * Einwahlaccount wird gesperrt (falls noch nicht geschehen). Sollte allerdings die UPSTREAM oder PMX Leistung
 * gekuendigt werden, so darf das Einwahlaccount wieder freigegeben werden.
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.DisableEinwahlaccountCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DisableEinwahlaccountCommand extends AbstractEinwahlCommand {

    private static final Logger LOGGER = Logger.getLogger(DisableEinwahlaccountCommand.class);

    @Override
    public Object execute() throws Exception {
        init();
        disableAccount(checkLeistungen());
        return null;
    }

    void disableAccount(boolean disableAccount) throws HurricanServiceCommandException {
        try {
            List<IntAccount> intAccounts =
                    getAccountService().findIntAccounts4Auftrag(getAuftragId(), IntAccount.LINR_EINWAHLACCOUNT);
            if (CollectionTools.isNotEmpty(intAccounts)) {
                IntAccount account = intAccounts.get(intAccounts.size() - 1);
                if (!BooleanTools.nullToFalse(account.getGesperrt()) && disableAccount) {
                    account.setGesperrt(Boolean.TRUE);
                    getAccountService().saveIntAccount(account, true);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim Sperren des Einwahlaccounts!", e);
        }
    }
}
