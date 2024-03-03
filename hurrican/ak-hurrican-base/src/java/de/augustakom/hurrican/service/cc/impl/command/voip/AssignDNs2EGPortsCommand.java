/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.SelectedPortsView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;

/**
 * Command-Klasse für die automatische Zuordnung der VoIP Rufnummern (AuftragVoIPDN) zu EG Ports
 */
@CcTxRequired
public class AssignDNs2EGPortsCommand extends AbstractAssignVoIPDNs2EGPorts {

    private static final Logger LOGGER = Logger.getLogger(AssignDNs2EGPortsCommand.class);

    @Override
    public Object execute() throws Exception {
        checkValues();
        checkHauptrufnummern();
        int[] dnCounter = assignHauptrufnummern();
        assignRest(dnCounter);
        return null;
    }

    void assignRest(int[] dnCounter) throws HurricanServiceCommandException {
        try {
            int portNumber = 1;
            for (AuftragVoipDNView voipDNView : auftragVoipDNViews) {
                if (!BooleanTools.nullToFalse(voipDNView.getMainNumber())) {
                    if (NumberTools
                            .isGreaterOrEqual(dnCounter[portNumber - 1], EndgeraetPort.getMaxDNsPerDefaultPort())) {
                        if (NumberTools.isGreater(portNumber, portCount)) {
                            break;
                        }
                        portNumber++;
                    }
                    final SelectedPortsView portSelection = SelectedPortsView.createNewSelectedPorts(
                            new boolean[portCount], new Date(), DateTools.getHurricanEndDate());
                    voipDNView.addSelectedPort(portSelection);
                    portSelection.selectOnIndex(portNumber - 1);
                    dnCounter[portNumber - 1]++;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Zuordnung der Hauptrufnummern zu den Endgeräteports ist "
                    + "fehlgeschlagen!", e);
        }
    }

    int[] assignHauptrufnummern() throws HurricanServiceCommandException {
        try {
            int portNumber = 1;
            int[] dnCounter = new int[portCount];
            for (AuftragVoipDNView voipDNView : auftragVoipDNViews) {
                if (BooleanTools.nullToFalse(voipDNView.getMainNumber())) {
                    final SelectedPortsView selectedPortsView =
                            SelectedPortsView.createNewSelectedPorts(new boolean[portCount], new Date(),
                                    DateTools.getHurricanEndDate());
                    voipDNView.addSelectedPort(selectedPortsView);
                    selectedPortsView.selectOnIndex(portNumber - 1);
                    dnCounter[portNumber - 1] = 1;
                    portNumber++;
                }
            }
            return dnCounter;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Zuordnung der Hauptrufnummern zu den Endgeräteports ist "
                    + "fehlgeschlagen!", e);
        }
    }

    void checkHauptrufnummern() throws HurricanServiceCommandException {
        int hauptrufnummern = 0;
        for (AuftragVoipDNView voipDNView : auftragVoipDNViews) {
            if (BooleanTools.nullToFalse(voipDNView.getMainNumber())) {
                hauptrufnummern++;
            }
        }
        if (NumberTools.notEqual(portCount, Integer.valueOf(hauptrufnummern))) {
            throw new HurricanServiceCommandException(String.format(
                    "Es sind unterschiedlich viele Hauptrufnummern (%s)"
                            + " zu Ports (%s) konfiguriert!", hauptrufnummern, portCount
            ));
        }
    }
}
