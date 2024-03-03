/**
 *
 */
package de.augustakom.hurrican.service.cc.impl.command.voip;

import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;

/**
 * Abstrakte Klasse, die als Basisklasse für die Zuordnung von Rufnummern oder Blöcken zu Endgeräte Ports dient
 */
public abstract class AbstractAssignVoIPDNs2EGPorts extends AbstractServiceCommand {

    // Property-Keys
    /**
     * Value fuer die prepare-Methode, um die Auftrag VoIP DN Views zu uebergeben.
     */
    public static final String AUFTRAG_VOIP_DN_VIEWS = "auftrag.voip.dn.views";
    /**
     * Value fuer die prepare-Methode, um den Anzahl der konfigurierten Ports zu uebergeben.
     */
    public static final String PORT_COUNT = "port.count";

    protected Collection<AuftragVoipDNView> auftragVoipDNViews;
    protected Integer portCount;

    /**
     * Ueberprueft, ob alle benoetigten Daten uebergeben wurden.
     */
    @SuppressWarnings("unchecked")
    protected void checkValues() throws HurricanServiceCommandException {
        auftragVoipDNViews = (getPreparedValue(AUFTRAG_VOIP_DN_VIEWS) instanceof Collection<?>)
                ? (Collection<AuftragVoipDNView>) getPreparedValue(AUFTRAG_VOIP_DN_VIEWS) : null;
        if (CollectionTools.isEmpty(auftragVoipDNViews)) {
            throw new HurricanServiceCommandException("Die Liste der VoIP Rufnummern ist leer!");
        }
        portCount = (getPreparedValue(PORT_COUNT) instanceof Integer)
                ? (Integer) getPreparedValue(PORT_COUNT) : null;
        if (portCount == null) {
            throw new HurricanServiceCommandException("Anzahl konfigurierter Ports fehlt!");
        }
        for (final AuftragVoipDNView auftragVoipDNView : auftragVoipDNViews) {
            if (!auftragVoipDNView.getSelectedPorts().isEmpty()) {
                throw new HurricanServiceCommandException("Es wurden bereits Portzuordnungen konfiguriert!");
            }
        }
    }

    /**
     * Injected by Test
     */
    void setAuftragVoipDNViews(Collection<AuftragVoipDNView> auftragVoipDNViews) {
        this.auftragVoipDNViews = auftragVoipDNViews;
    }

    /**
     * Injected by Test
     */
    void setPortCount(Integer portCount) {
        this.portCount = portCount;
    }
}
