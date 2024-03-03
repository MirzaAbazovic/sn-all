/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Command-Klasse für die automatische Zuordnung der VoIP Rufnummern (AuftragVoIPDN) zu EG Ports
 */
@CcTxRequired
public class AssignVoIPDNs2EGPortsCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AssignVoIPDNs2EGPortsCommand.class);

    // Property-Keys
    /**
     * Value fuer die prepare-Methode, um die techn. Auftrag ID zu uebergeben.
     */
    public static final String AUFTRAG_ID = "auftrag.id";
    /**
     * Value fuer die prepare-Methode, um die Auftrag VoIP DN Views zu uebergeben.
     */
    public static final String AUFTRAG_VOIP_DN_VIEWS = "auftrag.voip.dn.views";

    // Resources
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.VoIPService")
    private VoIPService voipService;
    @Autowired
    private ServiceLocator serviceLocator;

    // Vars
    private Collection<AuftragVoipDNView> auftragVoipDNViews;
    private Long auftragId;
    private int portCount = 0;

    @Override
    public Object execute() throws Exception {
        checkValues();
        if (!CollectionTools.isEmpty(auftragVoipDNViews)) {
            checkPortCount();
            if (checkDNBlocks()) {
                assignBlocks();
            }
            else {
                assignDNs();
            }
        }
        return null;
    }

    private void assignBlocks() throws Exception {
        IServiceCommand cmd = serviceLocator.getCmdBean(AssignBlocks2EGPortsCommand.class);
        cmd.prepare(AssignBlocks2EGPortsCommand.AUFTRAG_VOIP_DN_VIEWS, auftragVoipDNViews);
        cmd.prepare(AssignBlocks2EGPortsCommand.PORT_COUNT, portCount);
        cmd.execute();
    }

    private void assignDNs() throws Exception {
        IServiceCommand cmd = serviceLocator.getCmdBean(AssignDNs2EGPortsCommand.class);
        cmd.prepare(AssignDNs2EGPortsCommand.AUFTRAG_VOIP_DN_VIEWS, auftragVoipDNViews);
        cmd.prepare(AssignDNs2EGPortsCommand.PORT_COUNT, portCount);
        cmd.execute();
    }

    private boolean checkDNBlocks() throws HurricanServiceCommandException {
        try {
            return voipService.checkDNBlocks(auftragVoipDNViews);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Prüfung auf Rufnummern bzw. Blocks ist fehlgeschlagen!", e);
        }
    }

    void checkPortCount() throws HurricanServiceCommandException {
        Integer maxDefaultPortCount;
        try {
            Integer count = leistungsService.getCountEndgeraetPort(auftragId, new Date());
            maxDefaultPortCount = endgeraeteService.getMaxDefaultEndgeraetPorts();
            portCount = count;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Die Ermittlung der Endgeräteports ist fehlgeschlagen!", e);
        }
        if (portCount <= 0) {
            throw new HurricanServiceCommandException("Es sind keine Endgeräteports auf dem Auftrag konfiguriert!");
        }
        if ((maxDefaultPortCount == null) || NumberTools.isGreater(portCount, maxDefaultPortCount)) {
            throw new HurricanServiceCommandException(String.format(
                    "Es sind auf dem Auftrag mehr Ports konfiguriert (%s) "
                            + "als erlaubt (%s)!", portCount, maxDefaultPortCount
            ));
        }
    }

    /**
     * Ueberprueft, ob alle benoetigten Daten uebergeben wurden.
     */
    @SuppressWarnings("unchecked")
    private void checkValues() throws HurricanServiceCommandException {
        auftragId = (getPreparedValue(AUFTRAG_ID) instanceof Long)
                ? (Long) getPreparedValue(AUFTRAG_ID) : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Technische Auftrags ID fehlt!");
        }

        auftragVoipDNViews = (getPreparedValue(AUFTRAG_VOIP_DN_VIEWS) instanceof List<?>)
                ? (List<AuftragVoipDNView>) getPreparedValue(AUFTRAG_VOIP_DN_VIEWS) : null;
    }

    /**
     * Injected
     */
    public void setLeistungsService(CCLeistungsService leistungsService) {
        this.leistungsService = leistungsService;
    }

    /**
     * Injected
     */
    public void setEndgeraeteService(EndgeraeteService endgeraeteService) {
        this.endgeraeteService = endgeraeteService;
    }

    /**
     * Injected
     */
    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    /**
     * Injected
     */
    public void setVoipService(VoIPService voipService) {
        this.voipService = voipService;
    }

    /**
     * Test Injected
     */
    public void setAuftragVoipDNViews(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        this.auftragVoipDNViews = auftragVoipDNViews;
    }
}
