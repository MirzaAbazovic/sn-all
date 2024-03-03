/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2011 08:41:43
 */
package de.mnet.wita.service.impl;

import static com.google.common.collect.Lists.*;
import static de.augustakom.common.tools.lang.DateTools.*;
import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;
import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;
import static org.apache.commons.lang.StringUtils.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.exception.WbciBaseException;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.aggregator.AnsprechpartnerAmAggregator;
import de.mnet.wita.aggregator.MontageleistungAggregator;
import de.mnet.wita.aggregator.StandortKollokationAggregator;
import de.mnet.wita.aggregator.execution.WitaDataAggregationConfig;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.bpm.converter.MwfCbVorgangConverterService;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.AkmPvUserTask.AkmPvStatus;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.UserTask.UserTaskStatus;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgang.AbmState;
import de.mnet.wita.model.validators.RufnummerPortierungCheck;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.TalAenderungstypService;
import de.mnet.wita.service.TalAnbieterwechseltypService;
import de.mnet.wita.service.TalDetermineGeschaeftsfallService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;
import de.mnet.wita.service.WitaVorabstimmungService;

@CcTxRequired
public class WitaTalOrderServiceImpl implements WitaTalOrderService {

    public static final ImmutableMultimap<Long, GeschaeftsfallTyp> cbTyp2Geschaeftsfall = ImmutableMultimap
            .<Long, GeschaeftsfallTyp>builder()
            .put(CBVorgang.TYP_NEU, GeschaeftsfallTyp.BEREITSTELLUNG)
            .put(CBVorgang.TYP_KUENDIGUNG, GeschaeftsfallTyp.KUENDIGUNG_KUNDE)
            .putAll(CBVorgang.TYP_ANBIETERWECHSEL, GeschaeftsfallTyp.PROVIDERWECHSEL,
                    GeschaeftsfallTyp.VERBUNDLEISTUNG,
                    GeschaeftsfallTyp.BEREITSTELLUNG)
            .putAll(CBVorgang.TYP_PORTWECHSEL, GeschaeftsfallTyp.LEISTUNGS_AENDERUNG,
                    GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, GeschaeftsfallTyp.PORTWECHSEL).build();

    private static final Logger LOGGER = Logger.getLogger(WitaTalOrderServiceImpl.class);

    @Autowired
    CBVorgangDAO cbVorgangDao;
    @Autowired
    WitaCBVorgangDao witaCbVorgangDao;
    @Autowired
    TalOrderWorkflowService workflowService;
    @Autowired
    CommonWorkflowService commonWorkflowService;
    @Autowired
    AbgebendPvWorkflowService abgebendPvWorkflowService;
    @Autowired
    TalAenderungstypService talAenderungstypService;
    @Autowired
    TalAnbieterwechseltypService talAnbieterwechseltypService;
    @Autowired
    WitaVorabstimmungService witaVorabstimmungService;
    @Autowired
    WitaUsertaskService witaUsertaskService;
    @Autowired
    WitaDataService witaDataService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    CarrierElTALService carrierElTalService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    ReferenceService referenceService;
    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    AKUserService userService;
    @Autowired
    DateTimeCalculationService dateTimeCalculationService;
    @Autowired
    MwfEntityService mwfEntityService;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    RufnummerService rufnummerService;
    @Resource(name = "de.mnet.wita.service.impl.RufnummerPortierungService")
    RufnummerPortierungService rufnummerPortierungService;
    @Autowired
    AnsprechpartnerAmAggregator ansprechpartnerAmAggregator;
    @Autowired
    StandortKollokationAggregator standortKollokationAggregator;
    @Autowired
    MontageleistungAggregator montageleistungAggregator;
    @Autowired
    TaskDao taskDao;
    @Autowired
    WitaCheckConditionService witaCheckConditionService;
    @Autowired
    private MwfCbVorgangConverterService mwfCbVorgangConverterService;
    @Autowired
    private FeatureService featureService;

    @Override
    public WitaCBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            Long sessionId) throws StoreException {
        throw new StoreException("Methode createCBVorgang(Integer, Integer, ...) not supported on WitaTalOrderService!");
    }

    @Override
    public WitaCBVorgang createCBVorgang(Long cbId, Long auftragId, Set<CBVorgangSubOrder> subOrders4Klammer,
            Long carrierId, Date vorgabe, Long typ, Long usecaseId, Boolean vierDraht, String montagehinweis,
            AKUser user) throws StoreException {
        throw new StoreException("Methode createCBVorgang(Integer, Integer, ...) not supported on WitaTalOrderService!");
    }

    @Override
    public CBVorgang createCBVorgang(Long cbId, Long auftragId, Long carrierId, Date vorgabe, Long typ,
            String montagehinweis, AKUser user) throws StoreException {
        CbVorgangData cbvData = new CbVorgangData().withCbId(cbId).addAuftragId(auftragId).withCarrierId(carrierId)
                .withVorgabe(vorgabe).withCbVorgangTyp(typ).withMontagehinweis(montagehinweis).withUser(user);
        cbvData.withRealisierungsZeitfenster(Zeitfenster.SLOT_2);
        return createCBVorgang(cbvData).get(0);
    }

    @Override
    public List<CBVorgang> createCBVorgang(CbVorgangData cbVorgangData) throws StoreException {
        Preconditions.checkNotNull(cbVorgangData);

        try {
            List<CBVorgang> cbVorgaenge = createCBVorgangWithCarrierbestellung(cbVorgangData);

            startWitaWorkflows(cbVorgaenge);
            return cbVorgaenge;
        }
        catch (WitaDataAggregationException e) {
            LOGGER.info(e.getMessage(), e);
            throw new StoreException(
                    "Der WITA-Vorgang konnte nicht ausgelöst werden, da bei der Datenaggregierung ein oder mehrere Fehler aufgetreten sind:\n"
                            + e.getMessage(), e
            );
        }
        catch (WbciBaseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Der WITA-Vorgang konnte nicht ausgelöst werden, da es Probleme mit der ausgewählten WBCI-Vorabstimmung gab:", e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Der WITA-Vorgang konnte nicht ausgelöst werden!\nGrund: " + e.getMessage());
        }
    }

    @Override
    public List<CBVorgang> createHvtKvzCBVorgaenge(CbVorgangData cbVorgangDataKvz) throws StoreException { // called from GUI
        Preconditions.checkNotNull(cbVorgangDataKvz);
        Preconditions.checkArgument(CBVorgang.TYP_HVT_KVZ.equals(cbVorgangDataKvz.getCbVorgangTyp()),
                "Es wird ein CBVorgang vom Typ HVT_KVZ erwartet.");

        try {
            // es wird geprüft, ob für den referenzierten HVt-Auftrag bereits eine aktive WITA-Kuendigung existiert.
            // Falls dies der Fall ist, wird nur eine Neubestellung für den KVz-Auftrag erzeugz. Die Neubestellung
            // sollte gleich beim nächsten Scheduler-Lauf ausgelöst werden.
            // Falls keine aktive WITA-Kuendigung existiert wird sowohl eine Kündigung als auch eine Neubestellung
            // angelegt. Die Neubestellung wird aber erst rausgeschickt, sobald die Kündigung vom DTAG bestätigt wird.
            WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
            example.setAuftragId(cbVorgangDataKvz.getAuftragId4HvtToKvz());
            Collection<WitaCBVorgang> witaCbVorgaenge = carrierElTalService.findCBVorgaengeByExample(example);
            witaCbVorgaenge = Collections2.filter(witaCbVorgaenge, new Predicate<WitaCBVorgang>() {
                @Override
                public boolean apply(@Nullable WitaCBVorgang input) {
                    return input.isInStatusAnswered();
                }
            });
            if (!CollectionUtils.isEmpty(witaCbVorgaenge)) {
                final Long hvtCbVorgangId = witaCbVorgaenge.iterator().next().getId();
                return createKvzCBVorgang(cbVorgangDataKvz, hvtCbVorgangId);
            }
            else {
                return createHvtAndKvzCBVorgang(cbVorgangDataKvz);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die WITA-Vorgaenge konnten nicht ausgelöst werden!\nGrund: " + e.getMessage(), e);
        }
    }

    protected List<CBVorgang> createKvzCBVorgang(CbVorgangData cbVorgangDataKvz, Long cbVorgangRefId) throws StoreException {
        try {
            cbVorgangDataKvz.withCbVorgangTyp(CBVorgang.TYP_NEU);
            cbVorgangDataKvz.withCbVorgangHvtRef(cbVorgangRefId);
            List<CBVorgang> cbVorgangNeuKvz = createCBVorgangWithCarrierbestellung(cbVorgangDataKvz);
            startWitaWorkflows(cbVorgangNeuKvz);
            return cbVorgangNeuKvz;
        }
        catch (WitaDataAggregationException e) {
            LOGGER.info(e.getMessage(), e);
            throw new StoreException(
                    "Der WITA-Vorgang konnte nicht ausgelöst werden, da bei der Datenaggregierung ein oder mehrere " +
                            "Fehler aufgetreten sind:\n" + e.getMessage(), e
            );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Der WITA-Vorgang konnte nicht ausgelöst werden!\nGrund: " + e.getMessage(), e);
        }
    }

    protected List<CBVorgang> createHvtAndKvzCBVorgang(CbVorgangData cbVorgangDataKvz) throws StoreException {
        Preconditions.checkNotNull(cbVorgangDataKvz.getAuftragId4HvtToKvz(),
                "Die AuftragsId des zu kündigenden HVt-Auftrags muss gesetzt sein.");

        try {
            final Long auftragIdHvt = cbVorgangDataKvz.getAuftragId4HvtToKvz();
            Endstelle endstelle4Auftrag = endstellenService.findEndstelle4Auftrag(auftragIdHvt, Endstelle.ENDSTELLEN_TYP_B);
            final Carrierbestellung cbHvt = carrierService.findLastCB4Endstelle(endstelle4Auftrag.getId());
            CbVorgangData cbVorgangDataHvt = new CbVorgangData()
                    .withCbId(cbHvt.getId())
                    .addAuftragId(auftragIdHvt)
                    .withCarrierId(cbVorgangDataKvz.getCarrierId())
                    .withVorgabe(cbVorgangDataKvz.getVorgabe())
                    .withCbVorgangTyp(CBVorgang.TYP_KUENDIGUNG)
                    .withSubOrders(null, Boolean.FALSE)
                    .withMontagehinweis(cbVorgangDataKvz.getMontagehinweis())
                    .withAnbieterwechselTkg46(Boolean.FALSE)
                    .withAutomation(cbVorgangDataKvz.getAutomation())
                    .withUser(cbVorgangDataKvz.getUser())
                    .withRealisierungsZeitfenster(Zeitfenster.SLOT_2)   // bei Kuendigungen ist nur SLOT_2 zulaessig!
                    .withArchiveDocuments(cbVorgangDataKvz.getArchiveDocuments())
                    .withProjektKenner(cbVorgangDataKvz.getProjektKenner())
                    .withKopplungsKenner(cbVorgangDataKvz.getKopplungsKenner())
                    .withRufnummerIds(cbVorgangDataKvz.getRufnummerIds());

            List<CBVorgang> cbVorgangKueKdHvt = createCBVorgangWithCarrierbestellung(cbVorgangDataHvt);
            startWitaWorkflows(cbVorgangKueKdHvt);

            cbVorgangDataKvz.withCbVorgangTyp(CBVorgang.TYP_NEU);
            cbVorgangDataKvz.withCbVorgangHvtRef(cbVorgangKueKdHvt.get(0).getId());
            List<CBVorgang> cbVorgangNeuKvz = createCBVorgangWithCarrierbestellung(cbVorgangDataKvz);
            startWitaWorkflows(cbVorgangNeuKvz);

            final List<CBVorgang> cbVorgaenge = new ArrayList<>();
            cbVorgaenge.addAll(cbVorgangKueKdHvt);
            cbVorgaenge.addAll(cbVorgangNeuKvz);
            return cbVorgaenge;
        }
        catch (WitaDataAggregationException e) {
            LOGGER.info(e.getMessage(), e);
            throw new StoreException(
                    "Die WITA-Vorgaenge konnten nicht ausgelöst werden, da bei der Datenaggregierung ein oder mehrere " +
                            "Fehler aufgetreten sind:\n" + e.getMessage(), e
            );
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die WITA-Vorgaenge konnten nicht ausgelöst werden!\nGrund: " + e.getMessage(), e);
        }
    }

    @Override
    public WitaCBVorgang closeCBVorgang(Long id, Long sessionId) throws StoreException, ValidationException {
        if (id == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            WitaCBVorgang witaCbVorgang = findCBVorgang(id);
            if (NumberTools.isLess(witaCbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED)) {
                throw new StoreException("Die TAL-Bestellung kann im aktuellen Status nicht abgeschlossen werden!");
            }
            else if (NumberTools.equal(witaCbVorgang.getStatus(), CBVorgang.STATUS_CLOSED)) {
                throw new StoreException("Die TAL-Bestellung ist bereits abgeschlossen!");
            }

            if (witaCbVorgang.getReturnOk() == null) {
                throw new StoreException("Es ist keine Rueckmeldung (pos./neg.) eingetragen.\n"
                        + "Vorgang kann deshalb nicht abgeschlossen werden.");
            }

            if (witaCbVorgang.getCbId() != null && !witaCbVorgang.isRexMk()) {
                // Zur REX-MK gibt es keine Carrierbestellung
                writeDataOntoCarrierbestellung(sessionId, witaCbVorgang);
            }

            witaCbVorgang.close();
            carrierElTalService.saveCBVorgang(witaCbVorgang);

            return witaCbVorgang;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    protected List<CBVorgang> createCBVorgangWithCarrierbestellung(CbVorgangData cbVorgangData) throws FindException,
            StoreException, ValidationException, ServiceCommandException {
        List<CBVorgang> cbVorgaenge = cbVorgangData.buildCbVorgaenge();

        Carrierbestellung carrierbestellungBase = carrierService.findCB(cbVorgangData.getCbId());

        validateKvzSperre(carrierbestellungBase);

        String esTyp = getEsTyp4Carrierbestellung(cbVorgangData, carrierbestellungBase);

        Long auftragsKlammer = CollectionTools.hasExpectedSize(cbVorgaenge, 1) ? null : cbVorgangDao
                .getNextAuftragsKlammer();

        List<CBVorgang> createdCbVorgaenge = new ArrayList<>();
        for (CBVorgang cbVorgang : cbVorgaenge) {
            Carrierbestellung carrierbestellung = findOrCreateCarrierbestellung(cbVorgang, esTyp,
                    carrierbestellungBase, cbVorgangData.getAuftragId4PortChange(cbVorgang.getAuftragId()));
            cbVorgang.setCbId(carrierbestellung.getId());

            link4DrahtCarrierbestellungen(carrierbestellungBase, esTyp, cbVorgang);

            String vbzValue = getVbz(esTyp, cbVorgang);
            cbVorgang.setBezeichnungMnet(vbzValue);

            if (cbVorgang instanceof WitaCBVorgang) {
                defineWitaSpecificData(auftragsKlammer, (WitaCBVorgang) cbVorgang, esTyp);
            }
            carrierElTalService.saveCBVorgang(cbVorgang);
            if (carrierbestellung == carrierbestellungBase) {
                createdCbVorgaenge.add(0, cbVorgang);
            }
            else {
                createdCbVorgaenge.add(cbVorgang);
            }
        }
        checkAllVorgaengeHaveSameWitaGeschaeftsfallTyp(createdCbVorgaenge);
        witaCheckConditionService.checkConditionsForWbciPreagreement(createdCbVorgaenge);
        return createdCbVorgaenge;
    }

    private void validateKvzSperre(Carrierbestellung carrierbestellungBase) throws FindException, ValidationException {
        List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellungBase);
        for (Endstelle endstelle : endstellen) {
            hvtService.validateKvzSperre(endstelle);
        }
    }

    /**
     * Stellt sicher, dass alle erstellten WITA-Vorgaenge den gleichen WitaGeschaeftsfallTyp haben.
     */
    void checkAllVorgaengeHaveSameWitaGeschaeftsfallTyp(List<CBVorgang> createdCbVorgaenge) {
        GeschaeftsfallTyp typToCompare = null;
        for (CBVorgang cbv : createdCbVorgaenge) {
            if (cbv instanceof WitaCBVorgang) {
                WitaCBVorgang witaCbVorgang = (WitaCBVorgang) cbv;
                GeschaeftsfallTyp typ = witaCbVorgang.getWitaGeschaeftsfallTyp();
                if ((typToCompare != null) && (typToCompare != typ)) {
                    throw new WitaBaseException(
                            "Es wurden unterschiedliche Geschaeftsfalltypen für die verschiedenen Aufträge ermittelt. Eine Klammerung ist somit nicht möglich.");
                }
                typToCompare = typ;
            }
        }
    }

    /**
     * Bei 4-Draht (96X) ist es notwendig, dass die Endstellen auf die identische Carrierbestellung verweisen (nur ein
     * WITA-Vorgang mit zwei Ports). <br> Diese Methode ermittelt die Endstelle des Sub-Auftrags und verlinkt sie auf
     * die angegebene {@link Carrierbestellung} ({@code carrierbestellungBase}).
     *
     * @param carrierbestellungBase
     * @param esTyp
     * @param cbVorgang
     * @throws FindException
     * @throws StoreException
     */
    private void link4DrahtCarrierbestellungen(Carrierbestellung carrierbestellungBase, String esTyp,
            CBVorgang cbVorgang) throws FindException, StoreException {
        // bei 4-Draht muss die Endstelle des Sub-Auftrags auf die Basis-Carrierbestellung verlinkt werden!
        if (BooleanTools.nullToFalse(cbVorgang.getVierDraht()) && (cbVorgang.getSubOrders() != null)) {
            for (CBVorgangSubOrder subOrder : cbVorgang.getSubOrders()) {
                Endstelle endstelle4Draht = endstellenService.findEndstelle4Auftrag(subOrder.getAuftragId(), esTyp);
                Preconditions.checkNotNull(endstelle4Draht, "Endstelle des 4-Draht Auftrags nicht gefunden!");

                if ((endstelle4Draht.getCb2EsId() != null)
                        && NumberTools.notEqual(endstelle4Draht.getCb2EsId(), carrierbestellungBase.getCb2EsId())) {

                    // @formatter:off
                    // Sonderbehandlung fuer alte MUC Auftraege (siehe WITA-615):
                    // - 96X wurden mit zwei Carrierbestellungen migriert (KuP Migration), die identische LBZ/VtrNr hatten
                    // - bei gleicher LBZ wird keine Exception mehr geworfen, damit alte 96X noch gekuendigt werden koennen
                    // - Carrierbestellung von 4-Draht Endstelle wird durch 'endstelle4Draht.setCb2EsId(..)' hinfaellig
                    //   und haengt nur noch in der Luft (werden durch eigene Migration noch bereinigt!)
                    List<Carrierbestellung> carrierbestellungen4Draht = carrierService
                            .findCBs4Endstelle(endstelle4Draht.getId());
                    if (CollectionTools.isNotEmpty(carrierbestellungen4Draht)
                            && StringUtils.isNotBlank(carrierbestellungen4Draht.get(0).getLbz())
                            && !StringUtils.equals(carrierbestellungen4Draht.get(0).getLbz(), carrierbestellungBase.getLbz())) {
                        throw new WitaBaseException(
                            "Die Endstelle des 4-Draht Auftrags besitzt bereits eine andere Carrierbestellung!\n"
                                    + "Die 4-Draht Bestellung kann dadurch nicht ausgeloest werden.");
                    }
                    // @formatter:on
                }

                endstelle4Draht.setCb2EsId(carrierbestellungBase.getCb2EsId());
                endstellenService.saveEndstelle(endstelle4Draht);
            }
        }
    }

    private void defineWitaSpecificData(Long auftragsKlammer, WitaCBVorgang cbVorgang, String esTyp) throws FindException,
            ServiceCommandException, StoreException {
        cbVorgang.setAuftragsKlammer(auftragsKlammer);

        CarrierKennung carrierKennung = findCarrierKennung(cbVorgang.getAuftragId(), esTyp);
        cbVorgang.setCarrierKennungAbs(carrierKennung.getElTalAbsenderId());

        // Checks ueber Command-Chain durchfuehren
        carrierElTalService.executeCheckCommands4CBV(cbVorgang.getCbId(), cbVorgang.getAuftragId(), null,
                cbVorgang.getTyp(), null, carrierKennung);

        createAndAssignCarrierRefNr(cbVorgang);

        GeschaeftsfallTyp geschaeftsFall = determineGeschaeftsfallTyp(cbVorgang.getTyp(), cbVorgang.getCbId(),
                cbVorgang);
        cbVorgang.setWitaGeschaeftsfallTyp(geschaeftsFall);
        cbVorgang.setLetztesGesendetesAenderungsKennzeichen(AenderungsKennzeichen.STANDARD);
    }

    void startWitaWorkflows(List<CBVorgang> cbVorgaenge) {
        for (CBVorgang cbVorgang : cbVorgaenge) {
            if (cbVorgang instanceof WitaCBVorgang) {
                workflowService.newProcessInstance((WitaCBVorgang) cbVorgang);
            }
        }
    }

    @VisibleForTesting
    CarrierKennung findCarrierKennung(Long auftragId, String esTyp) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
        Long hvtIdStandort = endstelle.getHvtIdStandort();
        CarrierKennung carrierKennung = carrierService.findCarrierKennung4Hvt(hvtIdStandort);
        if (carrierKennung == null) {
            throw new FindException("Dem HVT-Standort mit der Id=" + hvtIdStandort + " ist keine Carrierkennung zugeordnet");
        }
        return carrierKennung;
    }

    @VisibleForTesting
    String getVbz(String esTyp, CBVorgang cbVorgang) throws FindException, StoreException {
        String vbzValue = physikService.getVbzValue4TAL(cbVorgang.getAuftragId(), esTyp);
        if (vbzValue == null) {
            throw new StoreException("VerbindungsBezeichnung des Auftrags konnte nicht ermittelt werden!");
        }
        return vbzValue;
    }

    private String getEsTyp4Carrierbestellung(CbVorgangData cbVorgangData, Carrierbestellung carrierbestellungBase)
            throws FindException {
        List<Endstelle> endstellen = endstellenService.findEndstellen4Carrierbestellung(carrierbestellungBase);
        for (Endstelle endstelle : endstellen) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
            if ((auftragDaten != null) && cbVorgangData.getAuftragIds().contains(auftragDaten.getAuftragId())) {
                return endstelle.getEndstelleTyp();
            }
        }
        throw new FindException("Konnte EndstellenTyp für die Carrierbestellung mit der id "
                + carrierbestellungBase.getId() + " nicht ermitteln!");
    }

    void createAndAssignCarrierRefNr(CBVorgang cbVorgang) {
        if ((cbVorgang != null) && (cbVorgang.getCarrierRefNr() == null)) {
            String carrierRefNr = cbVorgangDao.getNextCarrierRefNr();
            cbVorgang.setCarrierRefNr(carrierRefNr);
        }
    }

    private Carrierbestellung findOrCreateCarrierbestellung(CBVorgang cbVorgang, String esTyp,
            Carrierbestellung carrierbestellungBase, Long auftragId4PortChange) throws FindException,
            StoreException, ValidationException {

        Endstelle endstelleSubOrder = endstellenService.findEndstelle4Auftrag(cbVorgang.getAuftragId(), esTyp);
        Carrierbestellung carrierbestellung = null;

        if (endstelleSubOrder.getCb2EsId() != null) {
            List<Carrierbestellung> existingCBs = carrierService.findCBs4EndstelleTx(endstelleSubOrder.getId());
            carrierbestellung = getCarrierbestellungToUse(cbVorgang, carrierbestellungBase, existingCBs);
        }

        if (carrierbestellung == null) {
            carrierbestellung = new Carrierbestellung();
            carrierbestellung.setCarrier(carrierbestellungBase.getCarrier());
            carrierbestellung.setAuftragId4TalNA(auftragId4PortChange);
            carrierService.saveCB(carrierbestellung, endstelleSubOrder);
        }

        if (NumberTools.equal(carrierbestellung.getId(), carrierbestellungBase.getId())) {
            if (NumberTools.notEqual(carrierbestellungBase.getAuftragId4TalNA(), auftragId4PortChange)) {
                carrierbestellungBase.setAuftragId4TalNA(auftragId4PortChange);
                carrierService.saveCB(carrierbestellungBase);
            }
            return carrierbestellungBase;
        }

        carrierbestellung.setCarrier(carrierbestellungBase.getCarrier());
        carrierbestellung.setBestelltAm(carrierbestellungBase.getBestelltAm());
        carrierbestellung.setBereitstellungAm(carrierbestellungBase.getBereitstellungAm());
        carrierbestellung.setVorgabedatum(carrierbestellungBase.getVorgabedatum());
        carrierbestellung.setZurueckAm(carrierbestellungBase.getZurueckAm());
        carrierbestellung.setKundeVorOrt(carrierbestellungBase.getKundeVorOrt());
        carrierService.saveCB(carrierbestellung);
        return carrierbestellung;
    }

    /**
     * Wenn in der Liste der existingCBs sich eine fuer den CBVorgang benutztbare Carrierbestellung enthalten ist, so
     * wird diese zurueckgeliefert, ansonsten null.
     */
    private Carrierbestellung getCarrierbestellungToUse(CBVorgang cbVorgang, Carrierbestellung carrierbestellungBase,
            List<Carrierbestellung> existingCBs) {
        Carrierbestellung carrierbestellung = null;
        for (Carrierbestellung existingCB : existingCBs) {
            // CBVorgang auf dieser Carrierbestellung ausgeloest -> benutze sie!
            if (NumberTools.equal(existingCB.getId(), carrierbestellungBase.getId())) {
                return carrierbestellungBase;
            }
        }
        if (CollectionTools.isNotEmpty(existingCBs)) {
            if (cbVorgang.isKuendigung()) {
                carrierbestellung = existingCBs.get(0);
            }
            else {
                Carrierbestellung lastCB = existingCBs.get(0);
                if (StringUtils.isBlank(lastCB.getVtrNr())) {
                    carrierbestellung = lastCB;
                }
            }
        }
        return carrierbestellung;
    }

    /**
     * Berechnet den Geschaeftsfall aus dem Typ und weiteren übergebenen Daten. Wenn es nur einen möglichen
     * Geschäftsfall gibt, so wird dieser angezogen.
     */
    GeschaeftsfallTyp determineGeschaeftsfallTyp(Long cbTyp, Long cbId, WitaCBVorgang cbVorgang) {
        if (!cbTyp2Geschaeftsfall.containsKey(cbTyp)) {
            throw new WitaBaseException(String.format("Geschäftsfalltyp %d nicht unterstützt!", cbTyp));
        }

        GeschaeftsfallTyp determinedGeschaeftsfallTyp = null;
        ImmutableCollection<GeschaeftsfallTyp> moeglicheGeschaeftsfaelle = cbTyp2Geschaeftsfall.get(cbTyp);
        if (moeglicheGeschaeftsfaelle.size() == 1) {
            determinedGeschaeftsfallTyp = Iterables.getOnlyElement(moeglicheGeschaeftsfaelle);
        }
        else {
            if (CBVorgang.TYP_PORTWECHSEL.equals(cbTyp)) {
                determinedGeschaeftsfallTyp = determineGeschaeftsfall(talAenderungstypService, cbId, cbVorgang);
            }
            else if (CBVorgang.TYP_ANBIETERWECHSEL.equals(cbTyp)) {
                determinedGeschaeftsfallTyp = determineGeschaeftsfall(talAnbieterwechseltypService, cbId, cbVorgang);
            }
        }

        if (determinedGeschaeftsfallTyp == null) {
            throw new WitaBaseException(String.format(
                    "Konnte Geschaeftsfall nicht aus CbVorgang %d auswählen. Mögliche Geschäftsfälle: %s", cbTyp,
                    moeglicheGeschaeftsfaelle.toString()));
        }
        if (!moeglicheGeschaeftsfaelle.contains(determinedGeschaeftsfallTyp)) {
            throw new WitaBaseException(String.format(
                    "Geschaeftsfall %s nicht erlaubt für CbVorgang %d. Mögliche Geschäftsfälle: %s",
                    determinedGeschaeftsfallTyp, cbTyp, moeglicheGeschaeftsfaelle));
        }
        return determinedGeschaeftsfallTyp;
    }

    private GeschaeftsfallTyp determineGeschaeftsfall(TalDetermineGeschaeftsfallService determineGeschaeftsfallService,
            Long cbId, WitaCBVorgang cbVorgang) {
        try {
            Carrierbestellung carrierbestellung = carrierService.findCB(cbId);
            return determineGeschaeftsfallService.determineGeschaeftsfall(
                    carrierbestellung, cbVorgang);
        }
        catch (FindException e) {
            return null;
        }
    }


    @Override
    public void writeDataOntoCarrierbestellung(Long sessionId, WitaCBVorgang cbv) throws FindException, StoreException {
        Carrierbestellung cb = carrierService.findCB(cbv.getCbId());
        if (cb == null) {
            throw new StoreException("Die zugehoerige Carrierbestellung konnte nicht ermittelt werden!");
        }

        if (cbv.isKuendigung()) {
            if (cbv.isStorno()) { // wenn Kündigung erfolgreich storniert beide Felder unten zurücksetzen
                if (BooleanTools.nullToFalse(cbv.getReturnOk())) {
                    cb.setKuendBestaetigungCarrier(null);
                    cb.setKuendigungAnCarrier(null);
                }
            }
            else {
                // Kuendigung abschliessen
                cb.setKuendBestaetigungCarrier(getOrNullOnNegCbv(cbv.getReturnRealDate(), cbv));
            }
        }
        else if (NumberTools.isIn(cbv.getTyp(), new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_NUTZUNGSAENDERUNG,
                CBVorgang.TYP_ANBIETERWECHSEL, CBVorgang.TYP_PORTWECHSEL })) {

            // Die Folgenden Felder bei negativer Rueckmeldung nicht zuruecksetzen
            if (BooleanTools.nullToFalse(cbv.getReturnOk())) {
                // Bei neuen Leitungen Vorgabedatum setzen falls noch nicht gesetzt und noch nichts zurueckgemeldet wurde
                if (NumberTools.isIn(cbv.getTyp(), new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_ANBIETERWECHSEL })
                        && (cb.getVorgabedatum() == null) && (cb.getVtrNr() == null)) {
                    cb.setVorgabedatum(cbv.getVorgabeMnet());
                }

                writeCancelDateOnReferencedCarrierbestellung(cbv, cb);
                writeRealDateOnOrder(cbv);
            }

            cb.setLbz(getOrNullOnNegCbv(cbv.getReturnLBZ(), cbv));
            cb.setVtrNr(getOrNullOnNegCbv(cbv.getReturnVTRNR(), cbv));
            cb.setLl(getOrNullOnNegCbv(cbv.getReturnLL(), cbv));
            cb.setAqs(getOrNullOnNegCbv(cbv.getReturnAQS(), cbv));
            cb.setZurueckAm(getOrNullOnNegCbv(cbv.getAnsweredAt(), cbv));
            cb.setBereitstellungAm(getOrNullOnNegCbv(cbv.getReturnRealDate(), cbv));
            cb.setKundeVorOrt(getOrNullOnNegCbv(cbv.getReturnKundeVorOrt(), cbv));
            cb.setTalRealisierungsZeitfenster(getOrNullOnNegCbv(cbv.getTalRealisierungsZeitfenster(), cbv));

            if (StringUtils.isNotBlank(cbv.getReturnMaxBruttoBitrate())) {
                cb.setMaxBruttoBitrate(getOrNullOnNegCbv(cbv.getReturnMaxBruttoBitrate(), cbv));
            }

            if (CollectionTools.isNotEmpty(cbv.getSubOrders())) {
                writeCarrierbestellung4SubOrders(cbv, cb, sessionId);
            }
        }
        else {
            throw new StoreException("Vorgang noch nicht implementiert!");
        }

        carrierService.saveCB(cb);
        // Da cb.setLl(cbv.getReturnLL()), Leitungslänge auf Geo ID 2 Tech Location Mapping schreiben
        carrierService.saveCBDistance2GeoId2TechLocations(cb, sessionId);
    }


    /**
     * Bei Bestellungen vom Typ 'Aenderung' wird die Carrierbestellung des referenzierten Auftrags ermittelt. Auf dessen
     * Carrierbestellung wird dann das Realisierungsdatum als Kuendigungs-Datum eingetragen.
     */
    void writeCancelDateOnReferencedCarrierbestellung(WitaCBVorgang cbv, Carrierbestellung actualCarrierbestellung)
            throws StoreException {
        try {
            if (!cbv.isAenderung() || (actualCarrierbestellung.getAuftragId4TalNA() == null)) {
                return;
            }

            Carrierbestellung referencedCarrierbestellung = witaDataService.getReferencingCarrierbestellung(cbv, actualCarrierbestellung);
            if (referencedCarrierbestellung == null) {
                throw new StoreException("Die Carrierbestellung des Alt-Auftrags, auf die sich die Aenderung bezieht konnte nicht ermittelt werden!");
            }

            referencedCarrierbestellung.setKuendBestaetigungCarrier(cbv.getReturnRealDate());
            carrierService.saveCB(referencedCarrierbestellung);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Rueckmeldedatum konnte nicht als Kuendigungsdatum auf die referenzierte Carrierbestellung geschrieben werden!", e);
        }
    }


    /*
     * Schreibt das Realisierungsdatum als 'Vorgabe AM' Datum auf den aktuellen Auftrag (der
     * durch den {@code WitaCBVorgang} referenziert ist.
     */
    void writeRealDateOnOrder(WitaCBVorgang cbv) {
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(cbv.getAuftragId());
            auftragDaten.setVorgabeSCV(cbv.getReturnRealDate());
            auftragService.saveAuftragDaten(auftragDaten, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    /**
     * Liefert null zurueck, wenn der CBVorgang negativ zurueckgemeldet wurde, sonst den Wert value.
     */
    private <T> T getOrNullOnNegCbv(T value, WitaCBVorgang cbv) {
        if (BooleanTools.nullToFalse(cbv.getReturnOk())) {
            return value;
        }
        return null;
    }

    /**
     * Erstellt fuer die Sub-Auftraege der Auftragsklammerung entsprechende TAL-Bestellungen und traegt die
     * Rueckmeldungen dort ein.
     */
    private void writeCarrierbestellung4SubOrders(CBVorgang cbVorgang, Carrierbestellung carrierbestellungBase,
            Long sessionId) throws StoreException {
        try {
            String esTyp = null;
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(cbVorgang.getAuftragId());
            for (Endstelle endstelle : endstellen) {
                if (NumberTools.equal(endstelle.getCb2EsId(), carrierbestellungBase.getCb2EsId())) {
                    esTyp = endstelle.getEndstelleTyp();
                    break;
                }
            }

            for (CBVorgangSubOrder cbvSubOrder : cbVorgang.getSubOrders()) {
                Endstelle endstelleSubOrder = endstellenService
                        .findEndstelle4Auftrag(cbvSubOrder.getAuftragId(), esTyp);
                Carrierbestellung carrierbestellung = null;
                if (endstelleSubOrder.getCb2EsId() != null) {
                    List<Carrierbestellung> existingCBs = carrierService.findCBs4Endstelle(endstelleSubOrder.getId());
                    if (CollectionTools.isNotEmpty(existingCBs)) {
                        Carrierbestellung lastCB = existingCBs.get(0);
                        if (StringUtils.isBlank(lastCB.getVtrNr())) {
                            carrierbestellung = lastCB;
                        }
                    }
                }

                // Falls Carrierbestellung identisch, keine Übernahme der Daten notwendig.
                if ((carrierbestellung != null)
                        && NumberTools.equal(carrierbestellung.getId(), carrierbestellungBase.getId())) {
                    continue;
                }

                if (carrierbestellung == null) {
                    carrierbestellung = new Carrierbestellung();
                    carrierbestellung.setCarrier(carrierbestellungBase.getCarrier());
                    carrierService.saveCB(carrierbestellung, endstelleSubOrder);
                }

                carrierbestellung.setCarrier(carrierbestellungBase.getCarrier());
                carrierbestellung.setBestelltAm(carrierbestellungBase.getBestelltAm());
                carrierbestellung.setBereitstellungAm(carrierbestellungBase.getBereitstellungAm());
                carrierbestellung.setVorgabedatum(carrierbestellungBase.getVorgabedatum());
                carrierbestellung.setZurueckAm(carrierbestellungBase.getZurueckAm());
                carrierbestellung.setKundeVorOrt(carrierbestellungBase.getKundeVorOrt());
                carrierbestellung.setTalRealisierungsZeitfenster(carrierbestellungBase.getTalRealisierungsZeitfenster());
                carrierbestellung.setVtrNr(cbvSubOrder.getReturnVTRNR());
                carrierbestellung.setLbz(cbvSubOrder.getReturnLBZ());
                carrierbestellung.setAqs(cbvSubOrder.getReturnAQS());
                carrierbestellung.setLl(cbvSubOrder.getReturnLL());
                carrierService.saveCB(carrierbestellung);
                // Da cb.setLl(carrierbestellung.getReturnLL()), Leitungslänge auf Geo ID 2 Tech Location Mapping
                // schreiben
                carrierService.saveCBDistance2GeoId2TechLocations(carrierbestellung, sessionId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Zurueckschreiben der TAL-Daten der Sub-Auftraege! " + e.getMessage());
        }
    }

    @Override
    public boolean isPossibleKlammer4GF(Long cbVorgangTyp, Carrierbestellung cb) {
        // Bei internen Bestellungen -> keine DTAG-Klammerung moeglich
        return !((cb != null) && Carrier.isMNetCarrier(cb.getCarrier()))
                && NumberTools.isIn(cbVorgangTyp, new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_ANBIETERWECHSEL });
    }

    @Override
    public boolean isPossible4Draht4GF(Long cbVorgangTyp, Carrierbestellung cb) {
        // Bei internen Bestellungen -> kein 4Draht möglich
        return !((cb != null) && Carrier.isMNetCarrier(cb.getCarrier()))
                && NumberTools.isIn(cbVorgangTyp, new Number[] { CBVorgang.TYP_NEU, CBVorgang.TYP_KUENDIGUNG });
    }

    @Override
    public Date getVorgabeDatumTv60(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId) {
        return Date.from(dateTimeCalculationService.getVorgabeDatumTv60(LocalDateTime.now(), geschaeftsfallTyp, vorabstimmungsId).atZone(ZoneId.systemDefault()).toInstant());
    }
    @Override
    public Date getVorgabeDatumTv30(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId) {
        return Date.from(dateTimeCalculationService.getVorgabeDatumTv30(LocalDateTime.now(), geschaeftsfallTyp, vorabstimmungsId).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public List<RufnummerPortierungSelection> getRufnummerPortierungList(Long auftragNoOrig, Date vorgabeMnet) {
        try {
            return rufnummerService.findDNs4TalOrder(auftragNoOrig, vorgabeMnet);
        }
        catch (FindException e) {
            throw new WitaBaseException("Es konnten keine Rufnummern für die angegebenen "
                    + "Auftragsnummern und Vorgabedatum gefunden werden.", e);
        }
    }

    @Override
    public WitaCBVorgang findCBVorgang(Long id) {
        String errorMsg = "CB-Vorgang konnte nicht gefunden werden.";
        WitaCBVorgang cbVorgang;
        try {
            cbVorgang = (WitaCBVorgang) carrierElTalService.findCBVorgang(id);
        }
        catch (FindException e) {
            throw new WitaBaseException(errorMsg, e);
        }
        catch (ClassCastException e) {
            throw new WitaBaseException("Der elektronische CB-Vorgang wurde nicht über die WITA ausgelöst.", e);
        }
        if (cbVorgang == null) {
            throw new WitaBaseException(errorMsg);
        }
        return cbVorgang;
    }

    @Override
    public WitaCBVorgang doTerminverschiebung(Long cbVorgangId, LocalDate neuerTermin, AKUser user,
            boolean completeUsertasks, String montagehinweis, TamBearbeitungsStatus tamBearbeitungsStatus) throws StoreException, ValidationException {
        Preconditions.checkNotNull(cbVorgangId);
        Preconditions.checkNotNull(neuerTermin);
        WitaCBVorgang witaCbVorgang = findCBVorgang(cbVorgangId);
        if (witaCbVorgang == null) {
            throw new StoreException(String.format("Der CB-Vorgang mit der ID %s konnte nicht ermittelt werden!",
                    cbVorgangId));
        }
        checkNeuesVorgabedatumNotSameDay(witaCbVorgang, neuerTermin);

        TamUserTask tamUserTask = witaCbVorgang.getTamUserTask();
        if (completeUsertasks && (tamUserTask != null) && (tamUserTask.getStatus() == UserTaskStatus.OFFEN)) {
            witaUsertaskService.closeUserTask(tamUserTask, user);
        }
        else {
            witaUsertaskService.checkUserTaskNotClaimedByOtherUser(tamUserTask, user);
            if (tamUserTask != null) {
                tamUserTask.setTamBearbeitungsStatus(tamBearbeitungsStatus);
                tamUserTask.setTv60Sent(true);
                tamUserTask.setMahnTam(false);
                taskDao.store(tamUserTask);
            }
        }
        if (montagehinweis != null) {
            witaCbVorgang.setMontagehinweis(montagehinweis);
            // this is used for TV in setMontageleistungForRequest
        }
        carrierElTalService.saveCBVorgang(witaCbVorgang);

        MnetWitaRequest unsentRequest = mwfEntityService.findUnsentRequest(cbVorgangId);
        if (unsentRequest != null) {
            return updateExistingWitaRequestAndCbVorgang(unsentRequest, witaCbVorgang, neuerTermin, user);
        }

        TerminVerschiebung tv = checkAndCreateTv(witaCbVorgang, neuerTermin);
        mwfCbVorgangConverterService.writeTerminVerschiebung(witaCbVorgang, tv.getTermin(), user);

        // do not check Workflow state due to only state in which TV is not possible is waitForENTMMessage
        workflowService.sendTvOrStornoRequest(tv);
        return witaCbVorgang;
    }

    TerminVerschiebung checkAndCreateTv(WitaCBVorgang cbVorgang, LocalDate neuerTermin) {
        Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId());
        witaCheckConditionService.checkConditionsForTv(cbVorgang, auftrag, neuerTermin);

        TerminVerschiebung tv = new TerminVerschiebung(auftrag, neuerTermin);
        setMontageleistungForRequest(cbVorgang, tv);
        mwfEntityService.store(tv);
        return tv;
    }

    private void setMontageleistungForRequest(WitaCBVorgang cbVorgang, MnetWitaRequest request)
            throws WitaDataAggregationException {
        // Die Montageleistung muss neu aggregiert werden, da der Montagehinweis geaendert worden sein kann
        if (WitaDataAggregationConfig.isAggregatorConfiguredForGeschaeftsfall(request.getGeschaeftsfall().getClass(),
                MontageleistungAggregator.class)) {
            Montageleistung montageleistung = montageleistungAggregator.aggregate(cbVorgang);

            // Alte Montageleistung wird ueberschrieben
            request.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().setMontageleistung(montageleistung);
        }
    }

    public void checkNeuesVorgabedatumNotSameDay(WitaCBVorgang witaCbVorgang, LocalDate neuerTermin)
            throws StoreException {
        if (witaCbVorgang.hasReturnRealDate()) {
            if (neuerTermin.compareTo(Instant.ofEpochMilli(witaCbVorgang.getReturnRealDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate()) == 0) {
                throw new StoreException(
                        "Eine Terminverschiebung auf das aktuelle Realisierungsdatum des CB-Vorgangs kann nicht durchgeführt werden. Bitte wählen Sie ein anderes Datum.");
            }
        }
        else {
            if (neuerTermin.compareTo(Instant.ofEpochMilli(witaCbVorgang.getVorgabeMnet().getTime()).atZone(ZoneId.systemDefault()).toLocalDate()) == 0) {
                throw new StoreException(
                        "Eine Terminverschiebung auf das aktuelle Vorgabedatum des CB-Vorgangs kann nicht durchgeführt werden. Bitte wählen Sie ein anderes Datum.");
            }
        }
    }

    /**
     * Achtung: Montageleistung wird bisher nicht neu aggregiert
     */
    private WitaCBVorgang updateExistingWitaRequestAndCbVorgang(MnetWitaRequest request, WitaCBVorgang witaCbVorgang,
            LocalDate neuerTermin, AKUser user) throws StoreException {
        if (request.isStorno()) {
            throw new StoreException("Terminverschiebung nicht möglich"
                    + ", da zu diesem Vorgang noch ein nicht an die Telekom gesendeter Storno exisitiert.");
        }
        Geschaeftsfall geschaeftsfall = request.getGeschaeftsfall();
        if (!dateTimeCalculationService.isTerminverschiebungValid(neuerTermin, geschaeftsfall.getKundenwunschtermin()
                        .getZeitfenster(), false, witaCbVorgang.getWitaGeschaeftsfallTyp(), witaCbVorgang.getVorabstimmungsId(),
                witaCbVorgang.isHvtToKvz()
        )) {
            throw new StoreException("Der neue Termin hat nicht die nötige Mindestvorlaufzeit von "
                    + WitaConstants.MINDESTVORLAUFZEIT
                    + " Tagen oder hat ein anderes Zeitfenster als das bestellte Zeitfenster!");
        }
        if (request.isAuftrag()) {
            geschaeftsfall.getKundenwunschtermin().setDatum(neuerTermin);
            if (geschaeftsfall.getGfAnsprechpartner() != null) {
                geschaeftsfall.getGfAnsprechpartner().setAuftragsmanagement(
                        ansprechpartnerAmAggregator.aggregate(witaCbVorgang));
            }
            else {
                throw new StoreException(
                        "Fehlender GeschaeftsfallAnsprechpartner in dem noch nicht gesendetem Auftrag.");
            }
        }
        else if (request.isTv()) {
            ((TerminVerschiebung) request).setTermin(neuerTermin);
        }
        setMontageleistungForRequest(witaCbVorgang, request);
        mwfEntityService.store(request);

        witaCbVorgang.setVorgabeMnet(Date.from(neuerTermin.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        witaCbVorgang.setBearbeiter(user);
        witaCbVorgang.setRequestOnUnsentRequest(true);
        carrierElTalService.saveCBVorgang(witaCbVorgang);

        return witaCbVorgang;
    }

    @Override
    public Collection<WitaCBVorgang> doTerminverschiebung(Set<Long> cbVorgangIds, LocalDate neuerTermin,
            AKUser user, boolean completeUsertasks, String montagehinweis, TamBearbeitungsStatus tamBearbeitungsStatus) throws StoreException, ValidationException {
        List<WitaCBVorgang> terminverschiebungCbVorgaenge = newArrayList();
        for (Long cbVorgangId : cbVorgangIds) {
            terminverschiebungCbVorgaenge.add(doTerminverschiebung(cbVorgangId, neuerTermin, user, completeUsertasks,
                    montagehinweis, tamBearbeitungsStatus));
        }
        return terminverschiebungCbVorgaenge;
    }

    @Override
    public WitaCBVorgang sendErlmk(Long cbVorgangId, AKUser user) throws StoreException {
        Preconditions.checkNotNull(cbVorgangId);
        WitaCBVorgang witaCbVorgang = findCBVorgang(cbVorgangId);
        if (witaCbVorgang.getTamUserTask() != null) {
            witaUsertaskService.closeUserTask(witaCbVorgang.getTamUserTask(), user);
        }
        witaCbVorgang.setBearbeiter(user);
        carrierElTalService.saveCBVorgang(witaCbVorgang);
        return workflowService.sendErlmk(witaCbVorgang);
    }

    @Override
    public Collection<WitaCBVorgang> sendErlmks(Set<Long> cbVorgaenge, AKUser user) throws StoreException {
        List<WitaCBVorgang> erlmkCbVorgaenge = newArrayList();
        for (Long cbVorgangId : cbVorgaenge) {
            erlmkCbVorgaenge.add(sendErlmk(cbVorgangId, user));
        }
        return erlmkCbVorgaenge;
    }

    @Override
    public void sendNegativeRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, AKUser user) {
        checkRuemPvParameters(businessKey, antwortCode, antwortText);
        Preconditions.checkArgument(!antwortCode.zustimmung, "Der RuemPvAntwortCode ist kein negativer Antwort-Code!");

        sendRuemPv(businessKey, antwortCode, antwortText, user);
    }

    @Override
    public void sendPositiveRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, AKUser user) {
        checkRuemPvParameters(businessKey, antwortCode, antwortText);
        Preconditions.checkArgument(antwortCode.zustimmung, "Der RuemPvAntwortCode ist kein positiver Antwort-Code!");

        sendRuemPv(businessKey, antwortCode, antwortText, user);
    }

    private void sendRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText, AKUser user) {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(businessKey);
        witaUsertaskService.checkUserTaskNotClaimedByOtherUser(userTask, user);
        abgebendPvWorkflowService.sendRuemPv(businessKey, antwortCode, antwortText);

    }

    /**
     * Ueberprueft, ob die Basis-Parameter fuer eine RUEM-PV Meldung korrekt definiert sind.
     */
    private void checkRuemPvParameters(String businessKey, RuemPvAntwortCode antwortCode, String antwortText) {
        Preconditions.checkNotNull(businessKey, "BusinessKey des Workflows muss angegeben werden!");
        Preconditions.checkNotNull(antwortCode, "Der RuemPvAntwortCode muss angegeben werden!");
        if (antwortCode.antwortTextRequired) {
            Preconditions.checkNotNull(antwortText,
                    "Bei dem angegebenen RuemPvAntwortCode muss auch ein Antwort-Text angegeben werden!");
        }
    }

    @Override
    public List<Reference> findPossibleGeschaeftsfaelle(Long carrierId, boolean forKvz) throws FindException {
        List<Reference> refTypes = referenceService.findReferencesByType(Reference.REF_TYPE_TAL_BESTELLUNG_TYP, true);
        final ImmutableList<Long> possibleTypen;
        if (Carrier.isMNetCarrier(carrierId)) {
            possibleTypen = CBVorgang.ESAA_AND_INTERN_TYPEN;
        }
        else {
            possibleTypen = forKvz ? CBVorgang.WITA_KVZ_TYPEN : CBVorgang.WITA_TYPEN;
        }

        return ImmutableList.copyOf(Collections2.filter(refTypes, new Predicate<Reference>() {
            @Override
            public boolean apply(Reference input) {
                return possibleTypen.contains(input.getId());
            }
        }));
    }

    @Override
    public Collection<WitaCBVorgang> doStorno(Set<Long> cbVorgangIds, AKUser user) throws StoreException,
            ValidationException, FindException {
        List<WitaCBVorgang> stornoCbVorgaenge = newArrayList();
        for (Long id : cbVorgangIds) {
            stornoCbVorgaenge.add(doStorno(id, user));
        }
        return stornoCbVorgaenge;
    }

    @Override
    public WitaCBVorgang doStorno(Long cbVorgangId, AKUser user) throws StoreException, ValidationException {
        Preconditions.checkNotNull(cbVorgangId);
        WitaCBVorgang witaCbVorgang = findCBVorgang(cbVorgangId);

        MnetWitaRequest request = mwfEntityService.findUnsentRequest(witaCbVorgang.getId());
        if (request != null) {
            return undoRequestIfNotSendYet(user, witaCbVorgang, request);
        }

        Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(witaCbVorgang.getId());
        witaCheckConditionService.checkConditionsForStorno(witaCbVorgang, auftrag);

        if ((witaCbVorgang.getTamUserTask() != null)
                && (witaCbVorgang.getTamUserTask().getStatus() == UserTaskStatus.OFFEN)) {
            witaUsertaskService.closeUserTask(witaCbVorgang.getTamUserTask(), user);
        }
        mwfCbVorgangConverterService.writeStorno(witaCbVorgang, user);

        Storno storno = new Storno(auftrag);
        mwfEntityService.store(storno);

        workflowService.sendTvOrStornoRequest(storno);
        return witaCbVorgang;
    }

    private WitaCBVorgang undoRequestIfNotSendYet(AKUser user, WitaCBVorgang witaCbVorgang,
            MnetWitaRequest mnetWitaRequest) throws StoreException {

        mnetWitaRequest.setRequestWurdeStorniert(true);
        mwfEntityService.store(mnetWitaRequest);

        if (mnetWitaRequest.isAuftrag()) {
            witaCbVorgang.setAenderungsKennzeichen(AenderungsKennzeichen.STORNO);
            witaCbVorgang.setLetztesGesendetesAenderungsKennzeichen(AenderungsKennzeichen.STORNO);
            witaCbVorgang.answer(true);
            witaCbVorgang.close();
            ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(witaCbVorgang.getBusinessKey());
            commonWorkflowService.deleteProcessInstance(pi);
        }
        else {
            if (witaCbVorgang.getLetztesGesendetesAenderungsKennzeichen() != null) {
                witaCbVorgang.setAenderungsKennzeichen(witaCbVorgang.getLetztesGesendetesAenderungsKennzeichen());
                witaCbVorgang.setLetztesGesendetesAenderungsKennzeichen(null);
            }
            else {
                witaCbVorgang.setAenderungsKennzeichen(STANDARD);
            }
            if (witaCbVorgang.getPreviousVorgabeMnet() != null) {
                witaCbVorgang.setVorgabeMnet(witaCbVorgang.getPreviousVorgabeMnet());
                witaCbVorgang.setPreviousVorgabeMnet(null);
            }

            if (witaCbVorgang.getStatusLast() != null) {
                // if status greate or equal to 'answered' write some additional data
                if (NumberTools.isGreaterOrEqual(witaCbVorgang.getStatusLast(), CBVorgang.STATUS_ANSWERED)) {
                    witaCbVorgang.setAbmState(AbmState.FIRST_ABM);
                    witaCbVorgang.setAnsweredAt(new Date());
                    witaCbVorgang.setReturnOk(true); // must be ok otherwise ABBM would have close Workflow
                }
                witaCbVorgang.setStatus(witaCbVorgang.getStatusLast());
                witaCbVorgang.setStatusLast(null);
            }
            if (witaCbVorgang.getTamUserTask() != null) {
                witaUsertaskService.resetUserTask(witaCbVorgang.getTamUserTask());
            }
        }
        witaCbVorgang.setBearbeiter(user);
        witaCbVorgang.setRequestOnUnsentRequest(true);
        carrierElTalService.saveCBVorgang(witaCbVorgang);
        return witaCbVorgang;
    }

    @Override
    public List<WitaCBVorgang> getWitaCBVorgaengeForTVOrStorno(Long cbId, Long auftragId) {
        if ((cbId == null) && (auftragId == null)) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(Iterables.filter(witaCbVorgangDao.findCbVorgaengeByAuftragOrCBId(auftragId, cbId),
                WitaCBVorgang.class));
    }

    @Override
    public void saveUser(AKUser user) throws AKAuthenticationException {
        userService.save(user);
    }

    @Override
    public AKUser findUserBySessionId(Long sessionId) throws AKAuthenticationException {
        return userService.findUserBySessionId(sessionId);
    }

    @Override
    public boolean isMinWorkingDaysInFuture(Date toCheck, GeschaeftsfallTyp geschaeftsfallTyp,
            String vorabstimmungsId, boolean isHvtToKvz) {
        return dateTimeCalculationService.isKundenwunschTerminValid(LocalDateTime.now(), Instant.ofEpochMilli(toCheck.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(), false, false,
                geschaeftsfallTyp, vorabstimmungsId, isHvtToKvz);
    }

    @Override
    public boolean isDayInZeitfenster(Date toCheck, Kundenwunschtermin.Zeitfenster zeitfenster) {
        return dateTimeCalculationService.isKwtDayInZeitfenster(toCheck, zeitfenster);
    }

    @Override
    public WitaCBVorgang changeUebertragungsverfahren(Carrierbestellung cb, Equipment dtagEquipment, Date kwtVorgabe,
            Uebertragungsverfahren uebertragungsverfahrenNeu, AKUser user) throws FindException, StoreException,
            ValidationException, ServiceCommandException {

        // alte Carrierbestellung auf gekuendigt setzen und spaeter neue anlegen
        cb.setKuendBestaetigungCarrier(kwtVorgabe);
        cb.setKuendigungAnCarrier(kwtVorgabe);
        carrierService.saveCB(cb);

        Carrierbestellung carrierbestellung = new Carrierbestellung();
        carrierbestellung.setCarrier(cb.getCarrier());
        carrierbestellung.setBestelltAm(new Date());
        carrierbestellung.setVorgabedatum(kwtVorgabe);

        Endstelle endstelle = findEndstelleForEquipment(dtagEquipment);
        AuftragDaten auftrag = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
        carrierService.saveCB(carrierbestellung, endstelle);

        if ((auftrag != null) && cb.getCarrier().equals(Carrier.ID_DTAG)) {
            // @formatter:off
            CbVorgangData cbData = new CbVorgangData()
                    .addAuftragId(auftrag.getAuftragId(), auftrag.getAuftragId())
                    .withCbId(carrierbestellung.getId())
                    .withVorgabe(kwtVorgabe).withUser(user)
                    .withCarrierId(carrierbestellung.getCarrier())
                    .withCbVorgangTyp(CBVorgang.TYP_PORTWECHSEL)
                    .withPreviousUetv(dtagEquipment.getUetv());
            // @formatter:on

            // neuen Port am Equipment speichern
            Equipment storedDtagEquipment = rangierungsService.findEquipment(dtagEquipment.getId());
            storedDtagEquipment.setUetv(uebertragungsverfahrenNeu);
            rangierungsService.saveEquipment(storedDtagEquipment);

            // Erstellen der CB-Vorgangs und auslosen der Workflows

            return (WitaCBVorgang) Iterables.getOnlyElement(this.createCBVorgang(cbData));
        }
        throw new ValidationException(cb.getCarrier(),
                "Portänderung kann nur bei DTAG TAL über die WITA durchgeführt werden!");

    }

    private Endstelle findEndstelleForEquipment(Equipment dtagEquipment) throws FindException {
        Rangierung rangierung = rangierungsService.findRangierung4Equipment(dtagEquipment.getId());
        return endstellenService.findEndstelle(rangierung.getEsId());
    }

    @Override
    public RufnummerPortierungCheck checkRufnummerPortierungAbgebend(AbgebendeLeitungenUserTask abgebendeLeitungUserTask) {
        RufnummerPortierungCheck check = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_ABGEBEND);

        // Pruefen ob die Portierungschecks ueberhaupt Sinn machen
        if (abgebendeLeitungUserTask instanceof KueDtUserTask) {
            return check;
        }
        AkmPvStatus akmPvStatus = ((AkmPvUserTask) abgebendeLeitungUserTask).getAkmPvStatus();
        if (ABBM_PV_EMPFANGEN.equals(akmPvStatus)) {
            return check;
        }
        if (abgebendeLeitungUserTask.getAuftragIds().size() != 1) {
            check.auftragIdNichtEindeutig = true;
            return check;
        }

        // Relevante Daten laden
        AuftragDaten auftragDaten;
        try {
            Long auftragId = Iterables.getOnlyElement(abgebendeLeitungUserTask.getAuftragIds());
            auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
            if ((auftragDaten == null) || (auftragDaten.getAuftragNoOrig() == null)) {
                throw new FindException("Keine AuftragDaten oder keinen Billing-Auftrag zu der auftragId=" + auftragId
                        + " gefunden.");
            }
        }
        catch (FindException e) {
            check.fehlerBeimLadenDesAuftrags = true;
            return check;
        }

        List<Rufnummer> rufnummernAbgehend;
        try {
            rufnummernAbgehend = rufnummerService.findDnsAbgehend(auftragDaten.getAuftragNoOrig());
        }
        catch (FindException e) {
            check.fehlerBeimLadenDerRufnummern = true;
            return check;
        }

        AnkuendigungsMeldungPv akmPv = mwfEntityService.getLastAkmPv(abgebendeLeitungUserTask.getVertragsNummer());
        AuftragsBestaetigungsMeldungPv abmPv = mwfEntityService.getLastAbmPv(abgebendeLeitungUserTask
                .getVertragsNummer());

        RufnummernPortierung portierungAndererCarrier = null;
        LocalDate uebernahmeDatum = null;
        if (akmPv != null) {
            portierungAndererCarrier = akmPv.getRufnummernPortierung();
            uebernahmeDatum = akmPv.getAufnehmenderProvider().getUebernahmeDatumGeplant();
        }
        if (abmPv != null) {
            portierungAndererCarrier = abmPv.getRufnummernPortierung();
            uebernahmeDatum = abmPv.getAufnehmenderProvider().getUebernahmeDatumVerbindlich();
        }

        try {
            // Checks durchfuehren, falls Daten vorhanden
            if (uebernahmeDatum != null) {
                check.checkPortierungsDatum(rufnummernAbgehend, uebernahmeDatum);
            }
            RufnummernPortierung portierungMnet = rufnummerPortierungService.transformToRufnummerPortierung(
                    rufnummernAbgehend, false);
            check.checkPortierungenEqual(portierungMnet, portierungAndererCarrier);
        }
        catch (Exception e) {
            check.fehlerBeimLadenDerTaifunPortierung = true;
        }
        return check;
    }

    @Override
    public RufnummerPortierungCheck checkRufnummerPortierungAufnehmend(Long witaCBVorgangId) {
        RufnummerPortierungCheck check = new RufnummerPortierungCheck(RufnummerPortierungCheck.IS_AUFNEHMEND);
        WitaCBVorgang witaCbVorgang = findCBVorgang(witaCBVorgangId);

        // Pruefen ob die Portierungschecks ueberhaupt Sinn machen
        if (NumberTools.equal(witaCbVorgang.getStatus(), CBVorgang.STATUS_CLOSED)) {
            throw new WitaBaseException("Die TAL-Bestellung ist bereits abgeschlossen!");
        }
        if (!Boolean.TRUE.equals(witaCbVorgang.getReturnOk())) {
            return check;
        }
        // Nach Ruecksprache mit Sven Quitschau Check nur bei Anbieterwechsel & RexMk durchfuehren
        if (!witaCbVorgang.isAnbieterwechsel() && !witaCbVorgang.isRexMk()) {
            return check;
        }

        AuftragsBestaetigungsMeldung abm = mwfEntityService.getLastAbm(witaCbVorgang.getCarrierRefNr());
        if (abm == null) {
            return check;
        }

        // Relevante Daten laden und Checks durchfuehren
        RufnummernPortierung portierungMnet = loadBestellteRufnummerPortierung(witaCBVorgangId);
        RufnummernPortierung portierungAndererCarrier = abm.getRufnummernPortierung();
        check.checkPortierungenEqual(portierungMnet, portierungAndererCarrier);
        return check;
    }

    @Override
    @Transactional(
            value = "cc.hibernateTxManager",
            noRollbackFor = de.mnet.wita.exceptions.WitaDataAggregationException.class,
            propagation = Propagation.REQUIRED
    )
    public void modifyStandortKollokation(MnetWitaRequest mnetWitaRequest) {
        if ((mnetWitaRequest == null)
                || (mnetWitaRequest.getGeschaeftsfall() == null)
                || (mnetWitaRequest.getGeschaeftsfall().getAuftragsPosition() == null)
                || (mnetWitaRequest.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt() == null)
                || (mnetWitaRequest.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKollokation() == null)) {
            return;
        }
        StandortKollokation standortKollokationMWF = mnetWitaRequest.getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getStandortKollokation();
        StandortKollokation standortKollokationAktuell = findStandortKollokation4CBVorgang(mnetWitaRequest.getCbVorgangId());
        if (!standortKollokationMWF.equalsKollokation(standortKollokationAktuell)) {
            standortKollokationMWF.copyKollokation(standortKollokationAktuell);
            mwfEntityService.store(standortKollokationMWF);
        }
    }

    /**
     * @param witaCbVorgangId {@link CBVorgang#ID}
     * @return the actual {@link StandortKollokation} for the assigned CBVorgang-Id.
     */
    protected StandortKollokation findStandortKollokation4CBVorgang(Long witaCbVorgangId) {
        WitaCBVorgang witaCbVorgang = findCBVorgang(witaCbVorgangId);
        return standortKollokationAggregator.aggregate(witaCbVorgang);
    }

    List<Rufnummer> loadRufnummernKommend(WitaCBVorgang witaCbVorgang) throws FindException {
        if (witaCbVorgang.getAuftragId() == null) {
            return newArrayList();
        }
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(witaCbVorgang.getAuftragId());
        if (auftragDaten == null) {
            return newArrayList();
        }
        return rufnummerService.findDnsKommend(auftragDaten.getAuftragNoOrig());
    }

    RufnummernPortierung loadBestellteRufnummerPortierung(Long witaCBVorgangId) {
        MnetWitaRequest example = new MnetWitaRequest();
        example.setMwfCreationDate(null);
        example.setCbVorgangId(witaCBVorgangId);
        List<MnetWitaRequest> mnetWitaRequests = mwfEntityService.findMwfEntitiesByExample(example);
        MnetWitaRequest mnetWitaRequest = mnetWitaRequests.get(0);
        return mnetWitaRequest.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getRufnummernPortierung();
    }

    @Override
    public List<WitaCBVorgang> findCBVorgaenge4Klammer(Long klammer, Long auftragId) {
        List<WitaCBVorgang> witaCbVorgaenge = new LinkedList<>();
        if (klammer != null) {
            witaCbVorgaenge = witaCbVorgangDao.findByProperty(WitaCBVorgang.class, "auftragsKlammer", klammer);
            if (auftragId != null) {
                // Den gegebenen Vorgang ausschliessen
                Iterator<WitaCBVorgang> it = witaCbVorgaenge.iterator();
                while (it.hasNext()) {
                    WitaCBVorgang witaCbVorgang = it.next();
                    if (auftragId.equals(witaCbVorgang.getAuftragId())) {
                        it.remove();
                    }
                }
            }
        }
        return witaCbVorgaenge;
    }

    /**
     * {@inheritDoc  *
     */
    @Override
    public SortedSet<Long> findWitaCBVorgaengIDs4Klammer(Long klammerId) {
        return new TreeSet<>(witaCbVorgangDao.findWitaCBVorgangIDsForKlammerId(klammerId));
    }

    @Override
    public boolean checkAutoClosingAllowed(AuftragDaten auftragDaten, Long carrierId, Long cbVorgangTyp) {
        FeatureName feature = FeatureName.WITA_ORDER_AUTOMATION;
        if (CBVorgang.TYP_KUENDIGUNG.equals(cbVorgangTyp)) {
            feature = FeatureName.WITA_CANCELLATION_AUTOMATION;
        }
        else if (CBVorgang.TYP_HVT_KVZ.equals(cbVorgangTyp)) {
            feature = FeatureName.HVT_KVZ_RQW_AUTOMATION;
            //HVT_KVZ-Typ wird für diese Prüfung genau so wie NEU behandelt
            cbVorgangTyp = CBVorgang.TYP_NEU;
        }
        return featureService.isFeatureOnline(feature)
                && auftragService.isAutomationPossible(auftragDaten, cbVorgangTyp)
                && CBVorgang.WITA_TYPEN.contains(cbVorgangTyp)
                && Carrier.ID_DTAG.equals(carrierId);
    }

    @Override
    public boolean isAutomationAllowed(CBVorgang cbVorgang) throws FindException {
        boolean isPossible = false;
        if (cbVorgang instanceof WitaCBVorgang) {
            WitaCBVorgang witaCBVorgang = (WitaCBVorgang) cbVorgang;

            isPossible =
                    DateTools.isDateAfter(witaCBVorgang.getReturnRealDate(), new Date())
                            && DateCalculationHelper.isWorkingDay(Instant.ofEpochMilli(witaCBVorgang.getReturnRealDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate())
                            && witaCBVorgang.isStandardPositiv()
                            && (witaCBVorgang.getAuftragsKlammer() == null)
                            && featureService.isFeatureOnline(FeatureName.ORDER_AUTOMATION)
                            && checkHvtKvzAndAutomationFeature(witaCBVorgang)
                            && checkAutomationPossible(witaCBVorgang)
                            && !Boolean.TRUE.equals(witaCBVorgang.isKlaerfall())
            ;
        }
        return isPossible;
    }

    /**
     * Stellt sicher, dass wenn es sich bei dem angegebenen CbVorgang um ein HVT_KVZ-Auftrag handelt, das
     * Automatisierung-Feature aktiviert ist. Wenn das nicht der Fall ist, wird {@code false} zurückgeliefert. Wenn der
     * CbVorgang keinen HVT_KVZ-Auftrag ist, wird immer {@code true} zurückgeliefert.
     */
    protected boolean checkHvtKvzAndAutomationFeature(WitaCBVorgang cbVorgang) {
        if (CBVorgang.TYP_NEU.equals(cbVorgang.getTyp()) && cbVorgang.isHvtToKvz()) {
            return isHvtKvzRqwAutomationOnline();
        }
        else if (CBVorgang.TYP_KUENDIGUNG.equals(cbVorgang.getTyp())) {
            final WitaCBVorgang witaCBVorgangByRefId = findWitaCBVorgangByRefId(cbVorgang.getId());
            if (witaCBVorgangByRefId != null) {
                return isHvtKvzRqwAutomationOnline();
            }
        }
        return true;
    }

    private boolean isHvtKvzRqwAutomationOnline() {
        return featureService.isFeatureOnline(FeatureName.HVT_KVZ_RQW_AUTOMATION);
    }

    /**
     * Ermittelt ob die Automatisierung von dem Produkt möglich ist.
     */
    private boolean checkAutomationPossible(WitaCBVorgang cbVorgang) throws FindException {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId());
        return auftragService.isAutomationPossible(auftragDaten, cbVorgang.getTyp());
    }

    @Override
    public List<WitaCBVorgang> findWitaCBVorgaengeForAutomation(Long... orderType) throws FindException {
        if (orderType.length <= 0) {
            throw new FindException("Method findWitaCBVorgaengeForAutomation(Long... orderType) should not be called with no parameters!");
        }
        return witaCbVorgangDao.findWitaCBVorgaengeForAutomation(orderType);
    }

    @Override
    public List<WitaCBVorgang> findCBVorgaengeByVorabstimmungsId(String vorabstimmungsId) throws FindException {
        if (vorabstimmungsId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return witaCbVorgangDao.findByProperty(WitaCBVorgang.class, WitaCBVorgang.VORABSTIMMUNGSID, vorabstimmungsId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void markWitaCBVorgangAsKlaerfall(Long id, String bemerkung) throws StoreException {
        Preconditions.checkNotNull(id);

        WitaCBVorgang witaCbVorgang = findCBVorgang(id);
        witaCbVorgang.setKlaerfall(true);
        addKlaerfallBemerkung(witaCbVorgang, bemerkung);
        carrierElTalService.saveCBVorgang(witaCbVorgang);
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void addKlaerfallBemerkung(Long id, String bemerkung) throws StoreException {
        Preconditions.checkNotNull(id);

        WitaCBVorgang witaCbVorgang = findCBVorgang(id);
        addKlaerfallBemerkung(witaCbVorgang, bemerkung);
        carrierElTalService.saveCBVorgang(witaCbVorgang);
    }

    private void addKlaerfallBemerkung(WitaCBVorgang witaCbVorgang, String bemerkung) throws StoreException {
        String klaerfallBemerkungFormat = "%s (%s)";

        StringBuilder sb = new StringBuilder();
        if (isNotEmpty(witaCbVorgang.getKlaerfallBemerkung())) {
            sb.append(witaCbVorgang.getKlaerfallBemerkung()).append("\n");
        }
        sb.append(String.format(klaerfallBemerkungFormat, bemerkung,
                DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME)));
        witaCbVorgang.setKlaerfallBemerkung(sb.toString());
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public String getKlaerfallBemerkungen(Set<Long> cbIDs) {
        try {
            if (CollectionUtils.isNotEmpty(cbIDs)) {
                Map<Long, String> map = new HashMap<>();
                for (CBVorgang cbVorgang : carrierElTalService
                        .findCBVorgaenge4CB(cbIDs.toArray(new Long[cbIDs.size()]))) {
                    if (cbVorgang.isKlaerfall() && isNotEmpty(cbVorgang.getKlaerfallBemerkung())) {
                        map.put(cbVorgang.getCbId(), cbVorgang.getKlaerfallBemerkung());
                    }
                }

                if (map.size() == 1) {
                    return map.values().iterator().next();
                }
                else if (map.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<Long, String> entry : map.entrySet()) {
                        sb.append("\n- Carrierbestellung ").append(entry.getKey()).append(": ")
                                .append(entry.getValue());
                    }
                    return sb.toString();
                }
            }
            return null;
        }
        catch (FindException e) {
            throw new WitaBaseException(e);
        }
    }

    @Override
    public boolean checkAndAdaptHvtToKvzBereitstellung(Long cbVorgangRefId, LocalDateTime earliesSendDate, Date kuendigungsDate) throws StoreException {
        WitaCBVorgang bereitstellung = witaCbVorgangDao.findWitaCBVorgangByRefId(cbVorgangRefId);
        if (bereitstellung != null) {
            Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(bereitstellung.getId());
            if (auftrag.getSentAt() == null) {
                // the neubestellung has not been sent yet
                auftrag.setEarliestSendDate(Date.from(earliesSendDate.atZone(ZoneId.systemDefault()).toInstant()));
                if (!isCancellationDateAsRequested(bereitstellung, kuendigungsDate)) {
                    // The Bereitstellung and Kuendigung have to be executed on the same day.
                    // The original VorgabeMnet Date is not overwritten with the new date, since this date
                    // has to match the date stored in Taifun. Later on when the ABM for the Neubestellung is
                    // received the user will manually adapt the date in Taifun.
                    LOGGER.info(String.format("Overwriting the Neubestellung KWT '%s' with the confirmed kuendigungsdatum '%s'", bereitstellung.getVorgabeMnet(), kuendigungsDate));
                    auftrag.getGeschaeftsfall().getKundenwunschtermin().setDatum(DateConverterUtils.asLocalDate(kuendigungsDate));
                    addKlaerfallBemerkung(bereitstellung.getId(), String.format("Neubestellung wird zum bestätigten Kündigungstermin '%tY-%tm-%td' ausgelöst", kuendigungsDate, kuendigungsDate, kuendigungsDate));
                }
                LOGGER.debug(String.format("HVT_KVZ Bereitstellungsauftrag '%s' adapted. Scheduled send date = '%s'", auftrag.getId(), earliesSendDate));
                return true;
            }
            else if (!isCancellationDateAsRequested(bereitstellung, kuendigungsDate)) {
                // the neubestellung has already been sent and both dates are different -> mark the neubestellung for
                // clarification and DON'T send a TV for it
                final String DELIVERY_DATE_DIFFERS_FROM_REQUESTED_DATE =
                        "Der bestätigte Liefertermin '%s' für die Neubestellung weicht vom Kundenwunschtermin '%s' ab.";

                markWitaCBVorgangAsKlaerfall(bereitstellung.getId(),
                        String.format(DELIVERY_DATE_DIFFERS_FROM_REQUESTED_DATE,
                                formatDate(kuendigungsDate, PATTERN_DAY_MONTH_YEAR),
                                formatDate(bereitstellung.getVorgabeMnet(), PATTERN_DAY_MONTH_YEAR))
                );
                return true;
            }
        }

        return false;
    }

    private boolean isCancellationDateAsRequested(WitaCBVorgang bereitstellung, Date kuendigungsDate) {
        return bereitstellung.getVorgabeMnet().equals(kuendigungsDate);
    }

    @Override
    public boolean checkHvtKueAndCancelKvzBereitstellung(Long cbVorgangKueId) throws StoreException {
        WitaCBVorgang bereitstellung = witaCbVorgangDao.findWitaCBVorgangByRefId(cbVorgangKueId);
        if (bereitstellung != null) {
            markWitaCBVorgangAsKlaerfall(cbVorgangKueId, "Die Kündigung im HVt-nach-KVz wurde mit einer ABBM beantwortet.");
            try {
                //todo we need some kind of System-User here since the storno is processed automatically. Imho it is
                // not correct to use the last user here, but at the moment this is the only possible workaround since
                // the user is a mandatory field within the CbVorgang
                AKUser user = userService.findById(bereitstellung.getUserId());
                doStorno(bereitstellung.getId(), user);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new StoreException("Während der Stornierung der CBVorgang-Bereitstellung '%s' ist ein " +
                        "unerwarteter Fehler aufgetreten!\nGrund: " + e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    @Override
    public WitaCBVorgang findWitaCBVorgangByRefId(Long cbVorgangRefId) {
        return witaCbVorgangDao.findWitaCBVorgangByRefId(cbVorgangRefId);
    }

}
