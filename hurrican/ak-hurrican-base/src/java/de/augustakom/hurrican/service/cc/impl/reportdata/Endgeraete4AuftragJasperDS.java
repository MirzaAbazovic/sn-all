/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2006 16:20:00
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 * Jasper-DataSource, um die Endgeraete zu einem Auftrag zu ermitteln.
 *
 *
 */
public class Endgeraete4AuftragJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(Endgeraete4AuftragJasperDS.class);

    private Long auftragId = null;
    private Iterator<EGReportView> dataIterator = null;
    private EGReportView currentData = null;
    private EGConfig currentEGConfig = null;
    private EG2Auftrag currentEg2Auftrag = null;

    private CCAuftragService auftragService;
    private EndgeraeteService endgeraeteService;
    private DeviceService deviceService;

    /**
     * Konstruktor mit Angabe der Auftrags-ID zu dem die Endgeraete geladen werden sollen.
     *
     * @param auftragId
     * @throws JRException
     */
    public Endgeraete4AuftragJasperDS(Long auftragId) throws AKReportException {
        super();
        this.auftragId = auftragId;
        init();
    }

    @Override
    protected void init() throws AKReportException {
        try {
            List<EGReportView> data = new ArrayList<>();

            auftragService = getCCService(CCAuftragService.class);
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);

            // Endgeraete aus Hurrican laden
            endgeraeteService = getCCService(EndgeraeteService.class);
            List<EG2AuftragView> views = endgeraeteService.findEG2AuftragViews(auftragId);
            if (CollectionTools.isNotEmpty(views)) {
                for (EG2AuftragView view : views) {
                    EGReportView repView = new EGReportView();
                    repView.setEg2AuftragId(view.getEg2AuftragId());
                    repView.setEgName(view.getEgName());
                    repView.setSerialNumber(view.getSeriennummer());
                    repView.setMontageart(view.getMontageart());
                    data.add(repView);
                }
            }

            // Endgeraete aus Taifun ermitteln und mit den EGs aus Hurrican kombinieren
            if (auftragDaten.getAuftragNoOrig() != null) {
                deviceService = getBillingService(DeviceService.class);
                List<Device> devices = deviceService.findDevices4Auftrag(auftragDaten.getAuftragNoOrig(), null, null);
                if (CollectionTools.isNotEmpty(devices)) {
                    for (Device dev : devices) {
                        EGReportView repView = new EGReportView();
                        repView.setEgName(dev.getDevType());
                        repView.setSerialNumber(dev.getSerialNumber());
                        repView.setMacAddress(dev.getMacAddress());
                        data.add(repView);
                    }
                }
            }

            dataIterator = data.iterator();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Error loading Devices: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                try {
                    this.currentData = this.dataIterator.next();
                    loadDetails();
                }
                catch (FindException e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new JRException("Error loading device config: " + e.getMessage(), e);
                }
            }
        }
        return hasNext;
    }

    private void loadDetails() throws FindException {
        currentEGConfig = null;
        currentEg2Auftrag = null;
        if (currentData.getEg2AuftragId() != null) {
            // Aus findEGConfig -> findEGConfigNewTx, da Subreports mit concurrent findEGConfigs zu Deadlocks
            // in der DB gefuehrt haben. BA Verteilung triggert BA Report, triggert update/select auf
            // T_EG_CONFIG.
            currentEGConfig = endgeraeteService.findEGConfig(currentData.getEg2AuftragId());
            currentEg2Auftrag = endgeraeteService.findEG2AuftragById(currentData.getEg2AuftragId());
        }
    }

    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            if ("EG_NAME".equals(field)) {
                return currentData.getEgName();
            }
            else if ("MONTAGEART".equals(field)) {
                return currentData.getMontageart();
            }
            else if ("MAC_ADRESSE".equals(field)) {
                return currentData.getMacAddress();
            }
            else if ("SERIENNUMMER".equals(field)) {
                return currentData.getSerialNumber();
            }
            else if ("HERSTELLER".equals(field)) {
                return ((currentEGConfig != null) && (currentEGConfig.getEgType() != null)) ?
                        currentEGConfig.getEgType().getHersteller() : null;
            }
            else if ("MODELL".equals(field)) {
                return ((currentEGConfig != null) && (currentEGConfig.getEgType() != null)) ?
                        currentEGConfig.getEgType().getModell() : null;
            }
            else if ("ZUGANGSDATEN".equals(field)) {
                if (currentEGConfig != null) {
                    return StringTools.join(new String[] { currentEGConfig.getEgUser(), currentEGConfig.getEgPassword() }, " / ", true);
                }
                return null;
            }
            else if ("WAN_IP_FEST".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getWanIpFest() : null;
            }
            else if ("HAS_NAT".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getNatActive() : null;
            }
            else if ("HAS_DHCP".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getDhcpActive() : null;
            }
            else if ("HAS_DNS".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getDnsServerActive() : null;
            }
            else if ("QOS_ACTIVE".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getQosActive() : null;
            }
            else if ("WAN_VP".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getWanVp() : null;
            }
            else if ("WAN_VC".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getWanVc() : null;
            }
            else if ("DHCP_POOL_FROM".equals(field)) {
                return ((currentEGConfig != null) && (currentEGConfig.getDhcpPoolFromRef() != null)) ? currentEGConfig
                        .getDhcpPoolFromRef().getAddress() : null;
            }
            else if ("DHCP_POOL_TO".equals(field)) {
                return ((currentEGConfig != null) && (currentEGConfig.getDhcpPoolToRef() != null)) ? currentEGConfig
                        .getDhcpPoolToRef().getAddress() : null;
            }
            else if ("EG_CONFIG".equals(field)) {
                return currentEGConfig;
            }
            else if ("EG_2_AUFTRAG".equals(field)) {
                return currentEg2Auftrag;
            }
            else if ("BEMERKUNG".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getBemerkung() : null;
            }
            else if ("SCHICHT2PROTOKOLL".equals(field)) {
                return ((currentEGConfig != null) && (currentEGConfig.getSchicht2Protokoll() != null)) ?
                        currentEGConfig.getSchicht2Protokoll().toString() : null;
            }
            else if ("SOFTWARESTAND".equals(field)) {
                return (currentEGConfig != null) ? currentEGConfig.getSoftwarestand() : null;
            }
            else if ("MTU".equals(field))   {
                return (currentEGConfig != null) ? currentEGConfig.getMtu() : null;
            }
        }
        return null;
    }

    /* Temp. Modell-Klasse fuer die Report-Daten. */
    private static class EGReportView {
        private Long eg2AuftragId = null;
        private String egName = null;
        private String montageart = null;
        private String serialNumber = null;
        private String macAddress = null;

        public String getEgName() {
            return egName;
        }

        public void setEgName(String egName) {
            this.egName = egName;
        }

        public String getMontageart() {
            return montageart;
        }

        public void setMontageart(String montageart) {
            this.montageart = montageart;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public Long getEg2AuftragId() {
            return eg2AuftragId;
        }

        public void setEg2AuftragId(Long eg2AuftragId) {
            this.eg2AuftragId = eg2AuftragId;
        }
    }
}


