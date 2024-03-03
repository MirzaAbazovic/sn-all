/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Command-Klasse, um die Zuordnung der VoIP Rufnummern zu den Endgeraeteports zu ueberpruefen. <br> Folgende Checks
 * werden durchgefuehrt: <br> <ul> <li>bei Rufnummern <ul> <li>jeder Port muss eine Hauptrufnummer haben <li>maximal
 * dürfen 10 Rufnummern pro Port zugeordnet sein <li>alle Rufnummern müssen zugeordnet sein </ul> <li>bei Blöcken <ul>
 * <li>alle Blöcke müssen zugeordnet sein </ul> </ul>
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckPortAssignmentCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckPortAssignmentCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckPortAssignmentCommand.class);

    private VoIPService voipService;
    private List<AuftragVoipDNView> auftragVoipDNViews;

    @Override
    public Object execute() throws Exception {
        try {
            init();
            loadRequiredData();
            validate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    String.format("Bei der Ueberpruefung der Zuordnung der VoIP Rufnummern/Bloecke zu Endgeraeteports ist "
                            + "ein Fehler aufgetreten: %s", e.getMessage()), getClass()
            );
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    void init() throws ServiceNotFoundException {
        setVoipService(getCCService(VoIPService.class));
    }

    @Override
    protected void loadRequiredData() throws FindException {
        setAuftragVoipDNViews(getVoipService().findVoIPDNView(getAuftragId()));
    }

    private void validate() throws HurricanServiceCommandException {
        AKWarnings validationResult = getVoipService().validatePortAssignment(auftragVoipDNViews);
        if (validationResult.isNotEmpty()) {
            addWarnings(validationResult);
        }
    }

    public List<AuftragVoipDNView> getAuftragVoipDNViews() {
        return this.auftragVoipDNViews;
    }

    public void setAuftragVoipDNViews(List<AuftragVoipDNView> auftragVoipDNViews) {
        this.auftragVoipDNViews = auftragVoipDNViews;
    }

    public VoIPService getVoipService() {
        return this.voipService;
    }

    public void setVoipService(VoIPService voipService) {
        this.voipService = voipService;
    }

}
