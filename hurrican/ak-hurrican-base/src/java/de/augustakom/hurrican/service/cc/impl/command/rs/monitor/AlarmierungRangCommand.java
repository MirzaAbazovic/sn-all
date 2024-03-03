/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2009 12:18:44
 */
package de.augustakom.hurrican.service.cc.impl.command.rs.monitor;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.MonitorException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.tools.predicate.RSMRangCountPredicate;


/**
 * Prueft die Schwellwerte des Rangierungsmonitors und sendet ggf. eine Alarmierung per Mail.
 */
public class AlarmierungRangCommand extends AbstractRSMAlamierungsCommand {

    @Resource(name = "de.augustakom.hurrican.service.cc.MonitorService")
    private MonitorService monitorService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlassungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    @Override
    public Object execute() throws Exception {
        try {
            checkMonitorRun(RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
            String error = rangMonitorAlarmierung();

            // Versende Alarmierung per Email
            String email = registryService.getStringValue(RegistryService.REGID_RESSOURCEN_MONITOR_ALARM_RANG);
            sendMail(error, email, "Alarmierung Rangierungsmonitor");
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Prueft die Schwellwerte des Rangierungsmonitors und erzeugt den Text fuer eine Alarm eMail.
     */
    private String rangMonitorAlarmierung() throws MonitorException {
        try {
            StringBuilder email = new StringBuilder();
            List<PhysikTyp> typen = physikService.findPhysikTypen();
            Map<Long, String> physikTypen = CollectionMapConverter.convert2Map(typen, "getId", "getName");

            // Lade Monitor-Daten
            List<? extends RsmRangCount> exisitungRsmRangCounts = monitorService.findAllRsmRangCount();
            if (CollectionTools.isEmpty(exisitungRsmRangCounts)) {
                return null;
            }

            // Ermittle HVTs anhand der Niederlassung
            List<Niederlassung> niederlassungen = niederlassungsService.findNiederlassungen();
            if (CollectionTools.isEmpty(niederlassungen)) {
                return null;
            }

            for (Niederlassung niederlassung : niederlassungen) {
                List<HVTGruppe> hvtGruppen = hvtService.findHVTGruppenForNiederlassung(niederlassung.getId());
                if (CollectionTools.isEmpty(hvtGruppen)) {
                    continue;
                }

                StringBuilder freePortThresholdReached = new StringBuilder();
                StringBuilder freePortThresholdNearlyReached = new StringBuilder();
                StringBuilder portUsageThresholdReached = new StringBuilder();
                StringBuilder portUsageThresholdNearlyReached = new StringBuilder();

                // Durchlaufe alle HVTs
                for (HVTGruppe hvtGruppe : hvtGruppen) {
                    List<HVTStandort> hvtStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), Boolean.TRUE);
                    for (HVTStandort hvtStandort : hvtStandorte) {
                        List<RSMonitorConfig> monitorConfigs =
                                monitorService.findMonitorConfig4HvtType(hvtStandort.getId(), RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
                        if (CollectionTools.isEmpty(monitorConfigs)) {
                            continue;
                        }

                        for (RSMonitorConfig monitorConfig : monitorConfigs) {
                            evaluateRsMonitorConfig(monitorConfig,
                                    hvtGruppe, hvtStandort, physikTypen,
                                    exisitungRsmRangCounts,
                                    freePortThresholdReached, freePortThresholdNearlyReached,
                                    portUsageThresholdReached, portUsageThresholdNearlyReached);
                        }
                    }
                }

                // Fehlermeldung erstellen
                if ((freePortThresholdReached.length() > 0) || (freePortThresholdNearlyReached.length() > 0)) {
                    email.append("Niederlassung " + niederlassung.getName() + ": \n\n");
                    if (freePortThresholdReached.length() > 0) {
                        email.append("Schwellwerte unterschritten:\n");
                        email.append(freePortThresholdReached);
                        email.append("\n");
                    }
                    if (freePortThresholdNearlyReached.length() > 0) {
                        email.append("Schwellwerte fast erreicht:\n");
                        email.append(freePortThresholdNearlyReached);
                        email.append("\n");
                    }
                    email.append("#########################\n\n");
                }
            }
            return (email.length() > 0) ? email.toString() : null;
        }
        catch (Exception e) {
            throw new MonitorException(e.getMessage(), e);
        }
    }

    /**
     * Interpretiert die aktuellen Werte gegenueber der Monitor-Konfiguration und fuegt ggf. eine Alarm-Meldung hinzu.
     */
    void evaluateRsMonitorConfig(RSMonitorConfig monitorConfig,
            HVTGruppe hvtGruppe, HVTStandort hvtStandort, Map<Long, String> physikTypen,
            List<? extends RsmRangCount> exisitungRsmRangCounts, StringBuilder freePortThresholdReached,
            StringBuilder freePortThresholdNearlyReached, StringBuilder portUsageThresholdReached,
            StringBuilder portUsageThresholdNearlyReached) {
        if ((monitorConfig != null) && BooleanTools.nullToFalse(monitorConfig.getAlarmierung())) {
            // RangCount ermitteln
            RSMRangCountPredicate predicate = new RSMRangCountPredicate();
            predicate.setPredicateValues(
                    monitorConfig.getHvtIdStandort(),
                    monitorConfig.getKvzNummer(),
                    monitorConfig.getPhysiktyp(),
                    monitorConfig.getPhysiktypAdd());
            RsmRangCount rsmRangCount = (RsmRangCount) CollectionUtils.find(exisitungRsmRangCounts, predicate);

            if ((rsmRangCount != null)) {
                if (NumberTools.isGreater(monitorConfig.getMinCount(), Integer.valueOf(0))) {
                    // Schwellwerte fuer Anzahl freier Ports pruefen
                    int freePortThreshold = rsmRangCount.checkFreePortThreshold(monitorConfig.getMinCount());
                    if (freePortThreshold == RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN) {
                        freePortThresholdReached.append(
                                getMessage4FreePorts(hvtStandort, hvtGruppe, rsmRangCount, monitorConfig, physikTypen));
                    }
                    else if (freePortThreshold == RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN) {
                        freePortThresholdNearlyReached.append(
                                getMessage4FreePorts(hvtStandort, hvtGruppe, rsmRangCount, monitorConfig, physikTypen));
                    }
                }

                if (NumberTools.isGreater(monitorConfig.getDayCount(), Integer.valueOf(0))) {
                    // Schwellwerte fuer Reichweite pruefen
                    int portReachThreshold = rsmRangCount.checkPortUsageThreshold(monitorConfig.getDayCount());
                    if (portReachThreshold == RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN) {
                        portUsageThresholdReached.append(
                                getMessage4PortReach(hvtStandort, hvtGruppe, rsmRangCount, monitorConfig, physikTypen));
                    }
                    else if (portReachThreshold == RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN) {
                        portUsageThresholdNearlyReached.append(
                                getMessage4PortReach(hvtStandort, hvtGruppe, rsmRangCount, monitorConfig, physikTypen));
                    }
                }
            }
        }
    }


    /* Erzeugt eine Message fuer die Unterschreitung der freien Ports */
    private String getMessage4FreePorts(HVTStandort hvtStandort, HVTGruppe hvtGruppe, RsmRangCount count,
            RSMonitorConfig config, Map<Long, String> physikTypen) {
        return buildMesssage("Freie Stifte: ", hvtStandort, hvtGruppe, count.getKvzNummer(), config, count.getPortCountFree(), config.getMinCount(), physikTypen);
    }

    /* Erzeugt eine Message fuer die Unterschreitung der Port-Reichweite */
    private String getMessage4PortReach(HVTStandort hvtStandort, HVTGruppe hvtGruppe, RsmRangCount count,
            RSMonitorConfig config, Map<Long, String> physikTypen) {
        return buildMesssage("Reichweite: ", hvtStandort, hvtGruppe, count.getKvzNummer(), config, count.getPortReach(), config.getDayCount(), physikTypen);
    }

    private String buildMesssage(String type, HVTStandort hvtStandort, HVTGruppe hvtGruppe, String kvzNummer,
            RSMonitorConfig config, Integer count, Integer threshold, Map<Long, String> physikTypen) {
        StringBuilder msg = new StringBuilder();
        if (StringUtils.isNotBlank(kvzNummer)) {
            msg.append(String.format("%s (ONKZ %s/KVZ %s), ", hvtGruppe.getOrtsteil(), hvtGruppe.getOnkz(), kvzNummer));
        }
        else {
            msg.append(String.format("%s (ONKZ %s/ASB %s), ", hvtGruppe.getOrtsteil(), hvtGruppe.getOnkz(), hvtStandort.getAsb()));
        }

        msg.append(physikTypen.get(config.getPhysiktyp()));
        if (config.getPhysiktypAdd() != null) {
            msg.append("/").append(physikTypen.get(config.getPhysiktypAdd()));
        }
        msg.append(", ");
        msg.append(type);
        msg.append(String.format("%s", count));
        msg.append(", (Schwellwert ");
        msg.append(String.format("%s", threshold));
        msg.append(")\n");

        return msg.toString();
    }

    /**
     * Injected
     */
    public void setMonitorService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Injected
     */
    public void setNiederlassungsService(NiederlassungService niederlassungsService) {
        this.niederlassungsService = niederlassungsService;
    }

}


