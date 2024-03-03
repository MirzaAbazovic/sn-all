/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

/**
 * Basisklasse zur Sperrung oder Freigabe eines Einwahlaccounts.
 */
public abstract class AbstractEinwahlCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractEinwahlCommand.class);

    private CCLeistungsService ccLeistungsService;
    private AccountService accountService;

    protected void init() throws HurricanServiceCommandException {
        try {
            setCcLeistungsService(getCCService(CCLeistungsService.class));
            setAccountService(getCCService(AccountService.class));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Initialisierung des Commands 'AbstractLeistungCommand' fehlgeschlagen!", e);
        }
    }

    protected boolean checkLeistungen() throws HurricanServiceCommandException {
        try {
            return getCcLeistungsService().checkMustAccountBeLocked(getAuftragId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Prüfung ob das Einwahlaccount gesperrt oder freigegeben werden soll!", e);
        }
    }

    public CCLeistungsService getCcLeistungsService() {
        return this.ccLeistungsService;
    }

    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    public AccountService getAccountService() {
        return this.accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
