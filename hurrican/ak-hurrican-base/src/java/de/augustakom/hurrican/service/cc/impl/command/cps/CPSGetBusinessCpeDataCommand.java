/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2010 08:31:04
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSBusinessCpeData;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;

/**
 * Command-Klasse, um die Konfiguration der Business CPEs zu ermitteln und in die CPS-Struktur zu uebertragen. <br>
 * Es wird davon ausgegangen, dass nur eins bis zwei (ueber CPS provisionierbare) Endgeraete auf dem Auftrag vorhanden
 * sind. Sollten es mehr oder weniger Endgeraete geben, wird ein Invalid-Result zurueck geliefert.
 */
public class CPSGetBusinessCpeDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetBusinessCpeDataCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            if (businessCpeNecessary() || !ccLeistungsService.deviceNecessary(getCPSTransaction().getAuftragId(),
                    getCPSTransaction().getEstimatedExecTime())) {
                // Business CPE Provisionierung:
                // - businessCpeNecessary() -> Glasfaser SDSL
                // - deviceNecessary() -> Premium Glasfaser-DSL (IAD (Fritz Box) oder Business CPE)

                final List<CPSBusinessCpeData> cpeDataList = new ArrayList<>();
                final List<Pair<EGConfig, EG2Auftrag>> egConfigs = findEgConfigurations();
                for (Pair<EGConfig, EG2Auftrag> egConfig : egConfigs) {
                    final Long ccAuftragsId = egConfig.getSecond().getAuftragId();
                    final List<TechLeistung> voipLeistungen =
                            leistungsService.findTechLeistungen4Auftrag(ccAuftragsId, TechLeistung.TYP_VOIP, true);

                    final CPSBusinessCpeData cpeData = new CPSBusinessCpeData();
                    cpeData.transferEgConfigData(egConfig.getFirst(), egConfig.getSecond(), voipLeistungen);
                    cpeDataList.add(cpeData);
                }
                if (!cpeDataList.isEmpty()) {
                    getServiceOrderData().setCustomerPremisesEquipments(cpeDataList);
                }
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult
                    .createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                            String.format("Fehler bei der Ermittlung der Business CPE Daten: %s", e.getMessage()),
                            this.getClass());
        }
    }

    /**
     * Ermittelt CPS-provisionierbare Endgeraete des Auftrags und laedt deren Endgeraete-Konfiguration. <br>
     * Falls mehr als zwei CPS-provisionierbare Endgeraete gefunden werden, wird eine Exception generiert.
     *
     * @throws HurricanServiceCommandException
     */
    List<Pair<EGConfig, EG2Auftrag>> findEgConfigurations() throws HurricanServiceCommandException {
        try {
            List<EG2AuftragView> egs2Auftrag = endgeraeteService
                    .findEG2AuftragViews(getCPSTransaction().getAuftragId());
            CollectionUtils.filter(egs2Auftrag, object -> {
                EG2AuftragView eg2Auftrag = (EG2AuftragView) object;
                if (BooleanTools.nullToFalse(eg2Auftrag.getCpsProvisioning())
                        && !BooleanTools.nullToFalse(eg2Auftrag.getDeactivated())
                        && BooleanTools.nullToFalse(eg2Auftrag.getHasConfiguration())) {
                    return true;
                }
                return false;
            });

            checkEgCount(egs2Auftrag);

            final List<Pair<EGConfig, EG2Auftrag>> configs = new ArrayList<>();
            for (EG2AuftragView eg2AuftragView : egs2Auftrag) {
                final Long eg2AuftragId = eg2AuftragView.getEg2AuftragId();
                final EGConfig egConfig = endgeraeteService.findEGConfig(eg2AuftragId);
                final EG2Auftrag eg2Auftrag = endgeraeteService.findEG2AuftragById(eg2AuftragId);

                if ((egConfig == null) || (eg2Auftrag == null)) {
                    throw new HurricanServiceCommandException("CPE Konfiguration konnte nicht ermittelt werden!");
                }
                configs.add(Pair.create(egConfig, eg2Auftrag));
            }
            return configs;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format(
                    "Fehler bei der Ermittlung der CPE Konfiguration: %s", e.getMessage()), e);
        }
    }

    private void checkEgCount(List<EG2AuftragView> egs2Auftrag) throws HurricanServiceCommandException {
        if (egs2Auftrag.isEmpty() || egs2Auftrag.size() > 2) {
            throw new HurricanServiceCommandException(String.format(
                    "Anzahl der zur CPS Provisionierung freigegebenen Business CPEs ist %d. " +
                            "Erwartet werden ein bis zwei Geräte!",
                    egs2Auftrag.size()));
        }
    }

    /**
     * Prueft technische Leistung, ob ein Business CPE provisioniert werden muss oder nicht.
     */
    boolean businessCpeNecessary() throws FindException {
        Long auftragId = getCPSTransaction().getAuftragId();
        Date execDate = getCPSTransaction().getEstimatedExecTime();
        List<TechLeistung> leistungen = leistungsService.findTechLeistungen4Auftrag(auftragId,
                TechLeistung.TYP_CROSS_CONNECTION, execDate);
        return leistungen.stream()
                .filter(l -> TechLeistung.ID_BUSINESS_CPE.equals(l.getId()))
                .findFirst().isPresent();
    }

    /**
     * Injected
     */
    public void setEndgeraeteService(EndgeraeteService endgeraeteService) {
        this.endgeraeteService = endgeraeteService;
    }

    public void setLeistungsService(CCLeistungsService leistungsService) {
        this.leistungsService = leistungsService;
    }
}
