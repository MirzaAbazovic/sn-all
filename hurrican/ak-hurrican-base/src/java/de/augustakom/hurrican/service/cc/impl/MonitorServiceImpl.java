/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2008 16:43:31
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import java.util.concurrent.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BeanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.MonitorDAO;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RsmPortUsage;
import de.augustakom.hurrican.model.cc.RsmRangCount;
import de.augustakom.hurrican.model.cc.query.EquipmentQuery;
import de.augustakom.hurrican.model.cc.query.ResourcenMonitorQuery;
import de.augustakom.hurrican.model.cc.view.HVTBelegungView;
import de.augustakom.hurrican.model.cc.view.RsmRangCountView;
import de.augustakom.hurrican.model.cc.view.UevtCuDAView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.MonitorException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.rs.monitor.AlarmierungEQCommand;
import de.augustakom.hurrican.service.cc.impl.command.rs.monitor.AlarmierungRangCommand;
import de.augustakom.hurrican.validation.cc.MonitorConfigValidator;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Service-Implementierung fuer den Ressourcenmonitor
 *
 *
 */
@CcTxRequiredReadOnly
public class MonitorServiceImpl extends DefaultCCService implements MonitorService {

    private static final Logger LOGGER = Logger.getLogger(MonitorServiceImpl.class);

    @Resource(name = "monitorDAO")
    private MonitorDAO monitorDAO = null;
    @Resource(name = "equipmentDAO")
    private EquipmentDAO equipmentDAO = null;
    @Resource(name = "de.augustakom.hurrican.validation.cc.MonitorConfigValidator")
    private MonitorConfigValidator monitorConfigValidator = null;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.MonitorCalculationService")
    private MonitorCalculationServiceImpl monitorCalculationService;
    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public RSMonitorRun findByMonitorType(Long type) throws FindException {
        if (type == null) {
            return null;
        }
        try {
            RSMonitorRun example = new RSMonitorRun();
            example.setMonitorType(type);
            List<RSMonitorRun> list = monitorDAO.queryByExample(example, RSMonitorRun.class, null, new String[] { "startedAt" });
            return CollectionTools.isNotEmpty(list) ? list.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequired
    public void saveRsMonitorRun(RSMonitorRun run) throws StoreException {
        if (run == null) {
            return;
        }
        try {
            monitorDAO.store(run);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequired
    public void monitorAlarmierung(Long type) throws MonitorException {
        try {
            if (type == null) {
                throw new MonitorException("Kein Monitor-Typ übergeben!");
            }

            //Pruefe, ob Monitor korrekt abgeschlossen wurde
            RSMonitorRun run = findByMonitorType(type);

            if (run == null) {
                throw new MonitorException("Kein Monitor mit Typ " + type + " vorhanden");
            }
            else if (NumberTools.equal(run.getState(), RSMonitorRun.RS_REF_STATE_RUNNING)) {
                throw new MonitorException("Monitor läuft gerade, keine Alarmierung möglich");
            }
            else if (NumberTools.equal(run.getState(), RSMonitorRun.RS_REF_STATE_ERROR)) {
                throw new MonitorException("Monitor fehlerhaft beendet, keine Alarmierung möglich");
            }

            // Ausfuehrung Monitor-Alarmierung
            if (NumberTools.equal(RSMonitorRun.RS_REF_TYPE_RANG_MONITOR, type)) {
                IServiceCommand cmd = serviceLocator.getCmdBean(AlarmierungRangCommand.class);
                cmd.execute();
            }
            else if (NumberTools.equal(RSMonitorRun.RS_REF_TYPE_EQ_MONITOR, type)) {
                IServiceCommand cmd = serviceLocator.getCmdBean(AlarmierungEQCommand.class);
                cmd.execute();
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new MonitorException(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequired
    public RSMonitorRun startMonitor(Long type, Long sessionId) throws MonitorException {
        if (type == null || sessionId == null) {
            return null;
        }
        try {
            RSMonitorRun run = findByMonitorType(type);
            if (run == null) {
                run = new RSMonitorRun();
                run.setMonitorType(type);
            }

            // Falls Monitor gerade laeuft, erzeuge Exception
            if (NumberTools.equal(run.getState(), RSMonitorRun.RS_REF_STATE_RUNNING)) {
                throw new MonitorException(MonitorException.RS_MONITOR_RUNNING_FAILURE);
            }

            // Monitor kann gestartet werden
            AKUser user = userService.findUserBySessionId(sessionId);

            run.setStartedAt(new Date());
            run.setState(RSMonitorRun.RS_REF_STATE_RUNNING);
            run.setFinishedAt(null);
            run.setRunExecutedBy(user != null ? user.getLoginName() : null);
            saveRsMonitorRun(run);

            runMonitor(type, run);

            // Monitor-Lauf ohne Fehler beendet
            run.setState(RSMonitorRun.RS_REF_STATE_FINISHED);
            run.setFinishedAt(new Date());
            saveRsMonitorRun(run);
            return run;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new MonitorException(e.getMessage(), e);
        }
    }

    private void runMonitor(Long type, RSMonitorRun run) throws StoreException, MonitorException {
        try {
            // Ausfuehrung Port-Monitor
            if (NumberTools.equal(type, RSMonitorRun.RS_REF_TYPE_EQ_MONITOR)) {
                detectUevtBelegung();
                monitorCalculationService.createUevtCuDAViews();
            }
            // Ausfuehrung Rangierungs-Monitor
            else if (NumberTools.equal(type, RSMonitorRun.RS_REF_TYPE_RANG_MONITOR)) {
                writeRsmRangCount();
            }
            else {
                run.setState(RSMonitorRun.RS_REF_STATE_ERROR);
                saveRsMonitorRun(run);
            }
        }
        catch (Exception e) {
            run.setState(RSMonitorRun.RS_REF_STATE_ERROR);
            saveRsMonitorRun(run);
            throw new MonitorException(e.getMessage(), e);
        }
    }

    @Override
    public List<RSMonitorConfig> findMonitorConfig4HvtType(Long hvtStandortId, Long monitorType) throws FindException {
        if (monitorType == null) {
            return null;
        }
        try {
            RSMonitorConfig example = new RSMonitorConfig();
            example.setMonitorType(monitorType);
            if (hvtStandortId != null) {
                example.setHvtIdStandort(hvtStandortId);
            }
            List<RSMonitorConfig> list = monitorDAO.queryByExample(example, RSMonitorConfig.class);
            return CollectionTools.isNotEmpty(list) ? list : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequired
    public void saveRsMonitorConfig(RSMonitorConfig toSave, Long sessionId) throws StoreException {
        if (toSave == null) {
            return;
        }
        if (sessionId == null) {
            throw new StoreException(StoreException.INVALID_SESSION_ID);
        }
        try {
            // Pruefung der Konfiguration
            ValidationException ve = new ValidationException(toSave, "RSMonitorConfig");
            monitorConfigValidator.validate(toSave, ve);
            if (ve.hasErrors()) {
                throw ve;
            }

            // User ermitteln
            AKUser user = userService.findUserBySessionId(sessionId);
            if (user == null) {
                throw new StoreException(StoreException.INVALID_SESSION_ID);
            }

            // Pruefung, ob fuer die Parameter bereits ein Datensatz existiert
            // (Nur bei neuen Objekten, ID == null)
            if (toSave.getId() == null) {
                RSMonitorConfig example = new RSMonitorConfig();
                BeanTools.copyProperties(example, toSave,
                        new String[] { "hvtIdStandort", "kvzNummer", "monitorType", "physiktyp", "physiktypAdd", "eqRangSchnittstelle", "eqUEVT" });
                List<RSMonitorConfig> list = monitorDAO.queryByExample(example, RSMonitorConfig.class);
                if (list != null && list.size() > 1) {
                    throw new StoreException("Konfiguration ist bereits mehrfach vorhanden.");
                }
                // Ueberschreiben bestehende Konfiguration
                else if (list != null && list.size() == 1) {
                    RSMonitorConfig newConfig = list.get(0);
                    newConfig.setMinCount(toSave.getMinCount());
                    newConfig.setAlarmierung(toSave.getAlarmierung());
                    newConfig.setUserw(user.getLoginName());
                    newConfig.setDatew(new Date());
                    monitorDAO.store(newConfig);
                    return;
                }
            }
            toSave.setUserw(user.getLoginName());
            toSave.setDatew(new Date());
            monitorDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    @CcTxRequired
    public void deleteRsMonitorConfig(Long id) throws StoreException {
        if (id == null) {
            return;
        }
        try {
            monitorDAO.deleteById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public List<RsmRangCountView> findAllRsmRangCount() throws FindException {
        try {
            return monitorDAO.findAllRsmRangCount();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RsmRangCountView> findRsmRangCount(ResourcenMonitorQuery query)
            throws FindException {
        try {
            return monitorDAO.findRsmRangCount(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Loescht alle RsmRangCount-Datensaetze
     */
    void deleteRsmRangCount() {
        monitorDAO.deleteRsmRangCount();
    }


    /**
     * {@link Callable} Implementierung, um fuer einen bestimmten HVT die {@link RsmRangCount} Objekte zu erzeugen.
     */
    private class RsmRangCountTask implements Callable<Void> {
        private final HVTStandort hvtStandort;
        private final List<Object[]> physikTypCombinations;

        public RsmRangCountTask(HVTStandort hvtStandort, List<Object[]> physikTypCombinations) {
            this.hvtStandort = hvtStandort;
            this.physikTypCombinations = physikTypCombinations;
        }

        @Override
        public Void call() throws Exception {
            monitorCalculationService.createRsmRangCounts4Hvt(physikTypCombinations, hvtStandort);
            return null;
        }
    }


    @Override
    @CcTxRequired
    public void writeRsmRangCount() throws FindException {
        try {
            deleteRsmRangCount();

            List<Object[]> physikTypCombinations = physikService.findPhysiktypKombinationen();

            // Ermittle nur HVTs der Typen HVT, KVZ und FTTB
            List<HVTStandort> hvtStandorte = hvtService.findHVTStandortByTyp(HVTStandort.HVT_STANDORT_TYP_HVT);
            hvtStandorte.addAll(hvtService.findHVTStandortByTyp(HVTStandort.HVT_STANDORT_TYP_KVZ));
            hvtStandorte.addAll(hvtService.findHVTStandortByTyp(HVTStandort.HVT_STANDORT_TYP_FTTB));
            hvtStandorte.addAll(hvtService.findHVTStandortByTyp(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ));

            // Ermittlung der RsmRangCounts mit Hilfe von Threads, um die Performance zu optimieren.
            // (Pro Thread werden die RsmRangCount Objekte fuer einen techn. Standort generiert)
            List<Future<Void>> computedElements = new ArrayList<Future<Void>>();
            ExecutorService executor = Executors.newFixedThreadPool(5);

            for (HVTStandort hvtStd : hvtStandorte) {
                RsmRangCountTask rsmRangCountTask = new RsmRangCountTask(hvtStd, physikTypCombinations);
                computedElements.add(executor.submit(rsmRangCountTask));
            }

            int num = 0;
            final int hvtSize = hvtStandorte.size();
            for (Future<Void> future : computedElements) {
                future.get();
                num++;
                LOGGER.info(String.format("Finished analysing HVT #%s of %s", num, hvtSize));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequired
    public void detectUevtBelegung() throws StoreException {
        try {
            monitorDAO.deleteHVTBelegung();

            // Belegte CuDA's ermitteln
            // 2-Draht (niederbit-ratig)
            List<HVTBelegungView> belegt2DrahtN = equipmentDAO.find2DrahtBelegt(false);
            monitorDAO.store(belegt2DrahtN);
            // 2-Draht (hochbit-ratig)
            List<HVTBelegungView> belegt2DrahtH = equipmentDAO.find2DrahtBelegt(true);
            monitorDAO.store(belegt2DrahtH);
            // 4-Draht (=hochbit-ratig)
            List<HVTBelegungView> belegt4Draht = equipmentDAO.find4DrahtBelegt();
            monitorDAO.store(belegt4Draht);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die HVT- bzw. UEVT-Belegungen konnten nicht ermittelt werden!", e);
        }
    }


    @Override
    public List<UevtCuDAView> findUevtCuDAViews() throws FindException {
        try {
            return monitorDAO.findUevtCuDAViews();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<UevtCuDAView> findUevtCuDAViews(ResourcenMonitorQuery query) throws FindException {
        try {
            return monitorDAO.findUevtCuDAViews(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<UevtCuDAView> findViewsGroupedByRangSSType(String uevt, String cudaPhysik, Long hvtIdStandort) throws FindException {
        if (StringUtils.isBlank(uevt) || StringUtils.isBlank(cudaPhysik) || hvtIdStandort == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            return monitorDAO.findViewsGroupedByRangSSType(uevt, cudaPhysik, hvtIdStandort);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<UevtCuDAView> findViews4UevtBelegung(String uevt, Long hvtIdStandort) throws FindException {
        if (hvtIdStandort == null || StringUtils.isBlank(uevt)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            List<HVTBelegungView> belegungViews = monitorDAO.findHVTBelegungGrouped(uevt, hvtIdStandort);
            if (belegungViews != null) {
                List<UevtCuDAView> result = new ArrayList<UevtCuDAView>();
                for (HVTBelegungView hbv : belegungViews) {
                    UevtCuDAView ucView = new UevtCuDAView(hbv);

                    // Anzahl rangierter, freier und vorbereiter EQs abfragen
                    EquipmentQuery query = new EquipmentQuery();
                    query.setRangVerteiler(hbv.getUevt());
                    query.setHvtIdStandort(hbv.getHvtIdStandort());
                    query.setRangLeiste1(hbv.getRangLeiste1());

                    ucView.setAnzahlCuDAs(equipmentDAO.getEquipmentCount(query));

                    query.setRangSSType(hbv.getRangSSType());
                    query.setRangSchnittstelle(RangSchnittstelle.valueOf(hbv.getCudaPhysik()));
                    query.setStatus(EqStatus.frei);
                    ucView.setCudaFreigegeben(equipmentDAO.getEquipmentCount(query));

                    query.setStatus(EqStatus.rang);
                    ucView.setCudaRangiert(equipmentDAO.getEquipmentCount(query));

                    query.setStatus(EqStatus.vorb);
                    ucView.setCudaVorbereitet(equipmentDAO.getEquipmentCount(query));

                    result.add(ucView);
                }
                return result;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<RsmPortUsage> findRsmPortUsage(Long hvtIdStandort, String kvzNummer, Long physikTypId, Long physikTypIdAdd) throws FindException {
        try {
            return monitorDAO.findPortUsages(hvtIdStandort, kvzNummer, physikTypId, physikTypIdAdd, RsmPortUsage.PORT_USAGE_MONTH_COUNT);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    /**
     * Ermittelt von den angegebenen Rangierungen jeweils die KVZ-Nummer und erstellt pro KVZ-Nummer eine eigene/neue
     * Liste. <br> Die erstellen Listen werden in einer Map zurueck gegeben. Als Map-Key wird die KVZ-Nummer verwendet.
     */
    Map<String, List<Rangierung>> splitRangierungenByKvzNummer(List<Rangierung> activeRangierungen) {
        Map<String, List<Rangierung>> splittedMap = new HashMap<String, List<Rangierung>>();
        for (Rangierung rangierung : activeRangierungen) {
            if (rangierung.getEqOutId() != null) {
                Equipment carrierEquipment = equipmentDAO.findById(rangierung.getEqOutId(), Equipment.class);
                String kvzNummer = carrierEquipment.getKvzNummer() != null ? carrierEquipment.getKvzNummer() : "";

                List<Rangierung> rangierungen4Kvz = splittedMap.get(kvzNummer);
                if (rangierungen4Kvz == null) {
                    rangierungen4Kvz = new ArrayList<>();
                    splittedMap.put(kvzNummer, rangierungen4Kvz);
                }
                rangierungen4Kvz.add(rangierung);
            }
        }
        return splittedMap;
    }


    /**
     * Injected
     */
    public void setMonitorDAO(MonitorDAO monitorDAO) {
        this.monitorDAO = monitorDAO;
    }

    /**
     * Injected
     */
    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

    /**
     * Injected
     */
    public void setMonitorConfigValidator(MonitorConfigValidator monitorConfigValidator) {
        this.monitorConfigValidator = monitorConfigValidator;
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
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

}


