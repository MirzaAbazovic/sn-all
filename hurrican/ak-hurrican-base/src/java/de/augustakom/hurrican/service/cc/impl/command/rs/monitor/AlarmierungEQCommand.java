/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2009 12:14:44
 */
package de.augustakom.hurrican.service.cc.impl.command.rs.monitor;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.MonitorException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.tools.predicate.RSMEQCountPredicate;


/**
 * Prueft die Schwellwerte des Portmonitors und erzeugt eventl. eine Alarmierung
 *
 *
 */
public class AlarmierungEQCommand extends AbstractRSMAlamierungsCommand {

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            checkMonitorRun(RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);
            String error = eqMonitorAlarmierung();

            // Versende Alarmierung per Email
            RegistryService rs = (RegistryService) getCCService(RegistryService.class);
            String email = rs.getStringValue(RegistryService.REGID_RESSOURCEN_MONITOR_ALARM_EQ);
            sendMail(error, email, "Alarmierung ÜVT-Monitor");
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e.getMessage(), e);
        }
        return null;
    }

    /*
     * Prueft die Schwellwerte des EQ-Monitors und erzeugt eine Alarmierungsemail
     */
    private String eqMonitorAlarmierung() throws MonitorException {
        try {
            MonitorService ms = (MonitorService) getCCService(MonitorService.class);
            HVTService hvts = (HVTService) getCCService(HVTService.class);
            NiederlassungService ns = (NiederlassungService) getCCService(NiederlassungService.class);

            StringBuilder email = new StringBuilder();

            // Lade Monitor-Daten
            List<UevtCuDAView> cudaViews = ms.findUevtCuDAViews();
            if (CollectionTools.isEmpty(cudaViews)) {
                return null;
            }

            // Ermittle HVTs anhand der Niederlassung
            List<Niederlassung> niederlassungen = ns.findNiederlassungen();
            if (CollectionTools.isEmpty(niederlassungen)) {
                return null;
            }

            for (Niederlassung niederlassung : niederlassungen) {
                List<HVTGruppe> hvtGruppen = hvts.findHVTGruppenForNiederlassung(niederlassung.getId());
                if (CollectionTools.isEmpty(hvtGruppen)) {
                    continue;
                }

                StringBuilder error = new StringBuilder();
                StringBuilder warnings = new StringBuilder();

                // Durchlaufe alle HVTs
                for (HVTGruppe hvtGruppe : hvtGruppen) {
                    List<HVTStandort> hvtStandorte = hvts.findHVTStandorte4Gruppe(hvtGruppe.getId(), Boolean.TRUE);
                    for (HVTStandort hvtStandort : hvtStandorte) {
                        List<RSMonitorConfig> configs = ms.findMonitorConfig4HvtType(hvtStandort.getId(), RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);
                        if (CollectionTools.isEmpty(configs)) {
                            continue;
                        }
                        for (RSMonitorConfig config : configs) {
                            // Nur falls Alarmierung aktiv
                            if (config != null && BooleanTools.nullToFalse(config.getAlarmierung())) {
                                // UevtCudaView ermitteln
                                RSMEQCountPredicate predicate = new RSMEQCountPredicate();
                                predicate.setPredicateValues(config.getEqUEVT(), config.getHvtIdStandort(), config.getEqRangSchnittstelle());
                                UevtCuDAView count = (UevtCuDAView) CollectionUtils.find(cudaViews, predicate);

                                if (count != null && config.getMinCount() != null) {
                                    // Pruefe Schwellwert
                                    int check = count.checkSchwellwert(config.getMinCount());

                                    // Frei Stifte < Schwellwert
                                    if (check == RSMonitorConfig.SCHWELLWERT_UNTERSCHRITTEN) {
                                        error.append(getErrorMsg4EQ(hvtStandort, hvtGruppe, count, config));
                                    }
                                    // Freie Stifte ist knapp am Schwellwert
                                    else if (check == RSMonitorConfig.SCHWELLWERT_FAST_UNTERSCHRITTEN) {
                                        warnings.append(getErrorMsg4EQ(hvtStandort, hvtGruppe, count, config));
                                    }
                                }
                            }
                        }
                    }
                }
                // Fehlermeldung erstellen
                if (error.length() > 0 || warnings.length() > 0) {
                    email.append("Niederlassung ").append(niederlassung.getName()).append(": \n\n");
                    if (error.length() > 0) {
                        email.append("Schwellwerte unterschritten:\n");
                        email.append(error);
                        email.append("\n");
                    }
                    if (warnings.length() > 0) {
                        email.append("Schwellwerte fast erreicht:\n");
                        email.append(warnings);
                        email.append("\n");
                    }
                }
            }
            return (email.length() > 0) ? email.toString() : null;
        }
        catch (Exception e) {
            throw new MonitorException(e.getMessage(), e);
        }
    }

    /*
     * Erzeugt eine Fehlermeldung fuer die EQ-Monitor-Alarmierung
     */
    private String getErrorMsg4EQ(HVTStandort hvtStandort, HVTGruppe hvtGruppe, UevtCuDAView count, RSMonitorConfig config) {
        StringBuilder msg = new StringBuilder();
        msg.append(hvtGruppe.getOrtsteil()).append(" (ONKZ ").append(hvtGruppe.getOnkz()).append("/ASB ").append(hvtStandort.getAsb()).append(") ");
        msg.append("UEVT ").append(config.getEqUEVT());
        msg.append(" Physik ").append(config.getEqRangSchnittstelle());
        msg.append(" -  Freie Stifte: ").append(count.getCudaFrei());
        msg.append(" (Schwellwert ").append(config.getMinCount()).append(")\n");

        return msg.toString();
    }
}


