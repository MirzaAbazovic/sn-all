/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2009 17:55:11
 */

package de.augustakom.hurrican.service.reporting.impl.command;

import java.io.*;
import java.util.*;
import javax.crypto.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Command, um Engeraet-Daten bereitzustellen.
 *
 *
 */
public class GetEndgeraeteDatenCommand extends AbstractReportCommand {
    private static final Logger LOGGER = Logger.getLogger(GetEndgeraeteDatenCommand.class);

    public static final String ETAGE = "etage";
    public static final String RAUM = "raum";
    public static final String WAN_IPV4 = "ipV4";
    public static final String NETMASKV4 = "netmaskV4";
    public static final String WAN_IPV6 = "ipV6";
    public static final String NETMASKV6 = "netmaskV6";
    public static final String NAT_ACTIVE = "nat";
    public static final String DHCP_ACTIVE = "dhcp";
    public static final String ENDGERAET_PASSWORD = "firewallpw";
    public static final String FIREWALL_ACTIVE = "firewallenabled";

    public static final String PORTFORWARDINGS = "portforwardings";
    public static final String PORTFORWARDING_LAN_IP = "lanip";
    public static final String PORTFORWARDING_WAN_IP = "wanip";
    public static final String PORTFORWARDING_SOURCE_PORT = "sourceport";
    public static final String PORTFORWARDING_DEST_PORT = "destport";

    private EndgeraeteService endgeraeteService;

    private Long auftragId;
    private Map<String, Object> map;

    @Override
    public Object execute() throws Exception {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            initServices();
            map = new HashMap<String, Object>();
            readCpeData();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        endgeraeteService = getCCService(EndgeraeteService.class);
    }

    private void readCpeData() throws FindException, IllegalStateException, IllegalBlockSizeException, BadPaddingException, IOException {
        List<Map<String, Object>> cpes = new ArrayList<Map<String, Object>>();
        List<EG2Auftrag> eg2Auftraege = null;
        try {
            eg2Auftraege = endgeraeteService.findEGs4Auftrag(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e);
        }
        if (eg2Auftraege != null) {
            for (EG2Auftrag eg2Auftrag : eg2Auftraege) {
                EGConfig egConfig = null;
                try {
                    egConfig = endgeraeteService.findEGConfig(eg2Auftrag.getId());
                }
                catch (Exception e) {
                    LOGGER.error(e);
                }
                Map<String, Object> cpe = new HashMap<String, Object>();

                cpe.put(ETAGE, eg2Auftrag.getEtage());
                cpe.put(RAUM, eg2Auftrag.getRaum());

                Set<EndgeraetIp> wanIps = eg2Auftrag.getWanEndgeraetIps();
                IPAddress ipRefV4 = getWanIp(wanIps, true);
                cpe.put(WAN_IPV4, (ipRefV4 != null) ? ipRefV4.getAbsoluteAddress() : null);
                cpe.put(NETMASKV4, (ipRefV4 != null) ? ipRefV4.getNetmask() : null);
                IPAddress ipRefV6 = getWanIp(wanIps, false);
                cpe.put(WAN_IPV6, (ipRefV6 != null) ? ipRefV6.getAbsoluteAddress() : null);
                cpe.put(NETMASKV6, (ipRefV6 != null) ? ipRefV6.getNetmask() : null);

                if (egConfig != null) {
                    if (egConfig.getNatActive() == null) {
                        cpe.put(NAT_ACTIVE, null);
                    }
                    else {
                        cpe.put(NAT_ACTIVE, egConfig.getNatActive().booleanValue() ? "Aktiv" : "Inaktiv");
                    }

                    if (egConfig.getDhcpActive() == null) {
                        cpe.put(DHCP_ACTIVE, null);
                    }
                    else {
                        cpe.put(DHCP_ACTIVE, egConfig.getDhcpActive().booleanValue() ? "Aktiv" : "Inaktiv");
                    }

                    if (egConfig.getFirewallActive() == null) {
                        cpe.put(FIREWALL_ACTIVE, null);
                    }
                    else {
                        cpe.put(FIREWALL_ACTIVE, egConfig.getFirewallActive().booleanValue() ? "Aktiv" : "Inaktiv");
                    }

                    cpe.put(ENDGERAET_PASSWORD, egConfig.getEgPassword());

                    Set<PortForwarding> portForwardings = egConfig.getPortForwardings();
                    List<Map<String, Object>> pfs = new ArrayList<Map<String, Object>>();
                    for (PortForwarding portForwarding : portForwardings) {
                        Map<String, Object> pf = new HashMap<String, Object>();
                        pf.put(PORTFORWARDING_WAN_IP, (portForwarding.getSourceIpAddressRef() != null) ?
                                portForwarding.getSourceIpAddressRef().getAddress() : null);
                        pf.put(PORTFORWARDING_LAN_IP, (portForwarding.getDestIpAddressRef() != null) ?
                                portForwarding.getDestIpAddressRef().getAddress() : null);
                        pf.put(PORTFORWARDING_SOURCE_PORT, portForwarding.getSourcePort());
                        pf.put(PORTFORWARDING_DEST_PORT, portForwarding.getDestPort());
                        pfs.add(pf);
                    }
                    cpe.put(PORTFORWARDINGS, pfs);
                }
                else {
                    cpe.put(NAT_ACTIVE, null);
                    cpe.put(DHCP_ACTIVE, null);
                    cpe.put(ENDGERAET_PASSWORD, null);
                    cpe.put(FIREWALL_ACTIVE, null);
                    cpe.put(PORTFORWARDINGS, null);
                }

                cpes.add(cpe);
            }
        }
        map.put(getPrefix(), cpes);
    }

    private IPAddress getWanIp(Set<EndgeraetIp> wanIps, boolean ipV4) {
        if (wanIps.isEmpty()) {
            return null;
        }
        for (EndgeraetIp wanIp : wanIps) {
            IPAddress ipRef = wanIp.getIpAddressRef();
            if (ipRef != null) {
                if (ipV4 && ipRef.isIPV4()) {
                    return ipRef;
                }
                else if (!ipV4 && ipRef.isIPV6()) {
                    return ipRef;
                }
            }
        }
        return null;
    }

    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(AUFTRAG_ID);
        auftragId = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (auftragId == null) {
            throw new HurricanServiceCommandException("Auftrag-ID wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    @Override
    public String getPrefix() {
        return ENDGERAETE;
    }

    @Override
    public String getPropertyFile() {
        return null;
    }

}
