/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.03.2005 12:52:02
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.dao.cc.VrrpPriority;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Jasper-DataSource, um die IP-Adressen zu einem Auftrags zu ermitteln.
 */
public class AKInetIPJasperDS extends AbstractCCJasperDS {

    private static final String IP = "IP";
    private static final String ART = "ART";
    private static final String MNET_ENDGERAET = "M-net Endgeraet ";
    private static final String IP_ADRESSE = "IP-Adresse";
    private static final String IP_NETZ = "IP-Netze/Prefix (DHCPv6-PD)";
    private static final String NICHT_FEST_KONFIGURIERT = "(nicht fest konfiguriert)";
    private static final String FEST_KONFIGURIERT = "(fest konfiguriert)";

    private static final Logger LOGGER = Logger.getLogger(AKInetIPJasperDS.class);

    private Long auftragId = null;
    private Iterator<IPModel> dataIterator = null;
    private IPModel currentIP = null;

    /**
     * Konstruktor mit Angabe der ID des Auftrags, dessen IP-Adressen ermittelt werden sollen.
     *
     * @param auftragId
     */
    public AKInetIPJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            boolean loadV4IPs = true;
            boolean loadV4Nets = true;
            dataIterator = null;

            List<IPModel> ipModels = new ArrayList<>();

            EndgeraeteService egs = getCCService(EndgeraeteService.class);
            List<EG2Auftrag> egs2a = egs.findEGs4Auftrag(auftragId);
            if (CollectionTools.isNotEmpty(egs2a)) {
                boolean natActive = false;
                boolean hasEGConfig = false;
                for (EG2Auftrag eg2a : egs2a) {
                    final EGConfig egConfig = egs.findEGConfig(eg2a.getId());
                    if (egConfig != null) {
                        hasEGConfig = true;
                        natActive = BooleanTools.nullToFalse(egConfig.getNatActive());
                        for (EndgeraetIp endgeraetIp : eg2a.getEndgeraetIps()) {
                            if (endgeraetIp.getIpAddressRef() != null) {
                                final String ipAddress = endgeraetIp.getIpAddressRef().getEgDisplayAddress()
                                        + wanIpSuffix(endgeraetIp, egConfig.getWanIpFest())
                                        + vrrpPrioritySuffix(endgeraetIp, egConfig.getVrrpPriority());

                                ipModels.add(new IPModel(MNET_ENDGERAET + endgeraetIp.getAddressTypeFormated(), ipAddress));
                            }
                        }
                    }
                }

                loadV4IPs = (hasEGConfig) ? natActive : true;
                loadV4Nets = ipModels.isEmpty();
            }

            initAssignedIPs(loadV4IPs, loadV4Nets, ipModels);

            this.dataIterator = ipModels.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Die IP-Adressen des Auftrags konnten nicht ermittelt werden!", e);
        }
    }

    private void initAssignedIPs(boolean loadV4IPs, boolean loadV4Nets, List<IPModel> ipModels)
            throws ServiceNotFoundException, FindException {
        IPAddressService ipAddressService = getCCService(IPAddressService.class);
        List<IPAddress> ips = ipAddressService.findAssignedIPs4TechnicalOrder(auftragId);
        if (CollectionTools.isNotEmpty(ips)) {
            for (IPAddress ipAddress : ips) {
                if (ipAddress.isIPV4() && loadV4IPs) {
                    if (loadV4Nets || !BooleanTools.nullToFalse(ipAddress.isPrefixAddress())) {
                        addIPAddress(ipModels, ipAddress);
                    }
                }
                else if (ipAddress.isIPV6()) {
                    addIPAddress(ipModels, ipAddress);
                }
            }
        }
    }

    private void addIPAddress(List<IPModel> ipModels, IPAddress ipAddress) {
        if (BooleanTools.nullToFalse(ipAddress.isPrefixAddress())) {
            ipModels.add(new IPModel(IP_NETZ, ipAddress.getAbsoluteAddress()));
        }
        if (!BooleanTools.nullToFalse(ipAddress.isPrefixAddress())) {
            ipModels.add(new IPModel(IP_ADRESSE, ipAddress.getAbsoluteAddress()));
        }
    }

    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentIP = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (currentIP != null) {
            if (ART.equals(jrField.getName())) {
                return currentIP.getArt();
            }
            else if (IP.equals(jrField.getName())) {
                return currentIP.getIp();
            }
        }
        return null;
    }

    private String wanIpSuffix(EndgeraetIp ip, boolean wanIpFest) {
        if (ip.isWanIp()) {
            return " " + (wanIpFest ? FEST_KONFIGURIERT : NICHT_FEST_KONFIGURIERT);
        }
        return "";
    }

    private String vrrpPrioritySuffix(EndgeraetIp ip, VrrpPriority vrrpPriority) {
        return vrrpPriority != null && ip.isLanVRRPIp() ? " (" + vrrpPriority.getDisplayText() + ")" : "";
    }

    /*
     * Hilfsmodell fuer die Sammlung von IP-Daten.
     */
    private static class IPModel {
        private String art = null;
        private String ip = null;

        public IPModel(String art, String ip) {
            super();
            this.art = art;
            this.ip = ip;
        }

        public String getArt() {
            return this.art;
        }

        public String getIp() {
            return this.ip;
        }

    }
}
