/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * Command-Klasse für die automatische Zuordnung der VoIP Blöcke (AuftragVoIPDN) zu EG Ports
 */
@CcTxRequired
public class AssignBlocks2EGPortsCommand extends AbstractAssignVoIPDNs2EGPorts {

    private static final Logger LOGGER = Logger.getLogger(AssignBlocks2EGPortsCommand.class);

    @Override
    public Object execute() throws Exception {
        checkValues();
        assignBlocks();
        return null;
    }

    private void assignBlocks() throws HurricanServiceCommandException {
        try {
            for (AuftragVoipDNView voipDNView : auftragVoipDNViews) {
                final SelectedPortsView selectedPortsView =
                        SelectedPortsView.createNewSelectedPorts(new boolean[portCount], new Date(),
                                DateTools.getHurricanEndDate());
                for (int portIndex = 0; portIndex < portCount; portIndex++) {
                    selectedPortsView.selectOnIndex(portIndex);
                }
                voipDNView.addSelectedPort(selectedPortsView);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Zuordnung der Hauptrufnummern zu den Endgeräteports ist "
                    + "fehlgeschlagen!", e);
        }
    }
}
