/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.2014
 */
package de.augustakom.hurrican.service.cc.ffm.impl;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import javax.xml.ws.soap.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.mail.HtmlMailConstants;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.ffm.FfmDAO;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DeleteOrderBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.UpdateOrderBuilder;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Mail;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterial;
import de.augustakom.hurrican.model.cc.ffm.FfmOrderState;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.MailService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.ffm.command.AbstractFfmCommand;
import de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderIdsCommand;
import de.augustakom.hurrican.service.cc.ffm.command.FfmAggregationConfig;
import de.mnet.common.service.locator.ServiceLocator;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.CreateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.UpdateOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceService;

/**
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.ffm.FFMService")
public class FFMServiceImpl implements FFMService {

    private static final Logger LOGGER = Logger.getLogger(FFMServiceImpl.class);

    private static final String MATERIAL_MAIL_TABLE_DATA = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";
    private static final String MATERIAL_MAIL_HEADER = HtmlMailConstants.HTML_HEADER +
            "Hallo M-net Kollegen,<br/><p/>" +
            "zu Taifun-Auftragsnr %s haben wir folgende Materialr&uuml;ckmeldung(en) von FFM erhalten:<br/><p/>" +
            "<table border=\"1\" rules=\"all\" cellpadding=\"5\"><tr><th>Material-Id</th><th>Anzahl</th><th>Kurzbeschreibung</th></tr>";
    private static final String MATERIAL_MAIL_FOOTER = "</table><p/>Viele Gr&uuml;sse<br/>M-net Admin" + HtmlMailConstants.HTML_FOOTER;

    @Autowired
    private WorkforceService workforceService;

    @Autowired
    private FfmDAO ffmDAO;

    @Autowired
    private ServiceLocator serviceLocator;

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAConfigService")
    private BAConfigService baConfigService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.MailService")
    private MailService mailService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.billing.OEService")
    private OEService oeService;

    @Override
    public String createAndSendOrder(Verlauf bauauftrag) {
        try {
            WorkforceOrder workforceOrder = createWorkforceOrder4Bauauftrag(bauauftrag);
            CreateOrder createOrder = new CreateOrder();
            createOrder.setOrder(workforceOrder);

            LOGGER.info("Sending new FFM workforce order: " + createOrder);
            workforceService.createOrder(createOrder);

            return workforceOrder.getId();
        }
        catch (Exception e) {
            // SOAPFaultException ist nicht serialisierbar!!!
            throw new FFMServiceException("Fehler bei der Uebergabe des Bauauftrags an das FFM-System: " + e.getMessage(),
                    (e instanceof SOAPFaultException) ? null : e);
        }
    }

    @Override
    public void updateAndSendOrder(@Nonnull Long verlaufId) {
        try {
            updateAndSendOrder(baService.findVerlauf(verlaufId));
        }
        catch (FindException e) {
            throw new FFMServiceException("Fehler beim Update der FFM WorkforceOrder: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAndSendOrder(Verlauf bauauftrag) {
        try {
            if (hasActiveFfmRecord(bauauftrag, true)) {
                WorkforceOrder workforceOrder = createWorkforceOrder4Bauauftrag(bauauftrag);

                UpdateOrder updateOrder = new UpdateOrderBuilder()
                        .withWorkforceOrder(workforceOrder)
                        .withId(bauauftrag.getWorkforceOrderId())
                        .build();

                LOGGER.info("Sending new FFM update workforce order: " + updateOrder);
                workforceService.updateOrder(updateOrder);
            }
            else {
                throw new FFMServiceException(String.format(
                        "Der angegebene Bauaftrag '%s' besitzt keine aktive FFM WorkforceOrder!",
                        bauauftrag.getAuftragId()));
            }
        }
        catch (Exception e) {
            // SOAPFaultException ist nicht serialisierbar!!!
            throw new FFMServiceException("Fehler beim Update der FFM WorkforceOrder: " + e.getMessage(),
                    (e instanceof SOAPFaultException) ? null : e);
        }
    }

    @Override
    public boolean hasActiveFfmRecord(Verlauf bauauftrag, boolean checkWorkforceOrder) {
        if (bauauftrag == null || !BooleanTools.nullToFalse(bauauftrag.getAkt())
                || (checkWorkforceOrder && bauauftrag.getWorkforceOrderId() == null)) {
            return false;
        }

        try {
            VerlaufAbteilung vaFfm = baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM);
            return vaFfm != null && !vaFfm.isErledigt();
        }
        catch (Exception e) {
            throw new FFMServiceException("Fehler bei der Ueberpruefung, ob FFM Bauauftrag noch aktiv ist: " + e.getMessage(), e);
        }
    }

    protected WorkforceOrder createWorkforceOrder4Bauauftrag(Verlauf bauauftrag) throws FindException, ServiceCommandException {
        HVTStandort hvtStandort = findHvtStandort(bauauftrag.getAuftragId());
        AuftragDaten auftragDaten = findAuftragDaten(bauauftrag.getAuftragId());
        FfmProductMapping productMapping = findFfmProductMapping(bauauftrag.getAuftragId(), bauauftrag.getAnlass(),
                (hvtStandort != null ? hvtStandort.getStandortTypRefId() : null), auftragDaten.getProdId());
        LOGGER.info(String.format("Loaded FfmProductMapping for Bauauftrag with Id %s: %s",
                bauauftrag.getId(), productMapping));
        LocalDateTime referenceDate = Optional.ofNullable(DateConverterUtils
                .asLocalDateTime(bauauftrag.getRealisierungstermin()))
                .orElse(LocalDateTime.now());

        WorkforceOrder workforceOrder = aggregateAndBuildWorkforceOrder(bauauftrag.getAuftragId(),
                referenceDate, Optional.of(bauauftrag), getTechLeistungen4Ba(bauauftrag.getId()),
                productMapping, hvtStandort, auftragDaten);
        return workforceOrder;
    }

    private List<TechLeistung> getTechLeistungen(Long auftragId) {
        try {
            return ccLeistungsService.findTechLeistungen4Auftrag(auftragId, null, true);
        }
        catch (FindException e) {
            throw new FFMServiceException(String.format(
                    "Fehler bei der Ermittlung der aktuellen technischen Leistungen zum Auftrag: %s", auftragId), e);
        }
    }

    private List<TechLeistung> getTechLeistungen4Ba(Long bauauftragId) {
        try {
            return ccLeistungsService.findTechLeistungen4Verlauf(bauauftragId, true);
        }
        catch (FindException e) {
            throw new FFMServiceException(String.format(
                    "Fehler bei der Ermittlung der technischen Leistungen zum Bauauftrag: %s", bauauftragId), e);
        }
    }

    @Override
    @Nullable
    public WorkforceOrder createOrder(Verlauf bauauftrag) {
        try {
            if (hasActiveFfmRecord(bauauftrag, false)) {
                return createWorkforceOrder4Bauauftrag(bauauftrag);
            }
        }
        catch (Exception e) {
            throw new FFMServiceException(String.format("Fehler bei Erstellung der FFM WorkforceOrder: %s",
                    e.getMessage()), e);
        }
        return null;
    }

    @Override
    public WorkforceOrder createOrder(Long auftragId, Long baVerlaufAnlassId) {
        try {
            HVTStandort hvtStandort = findHvtStandort(auftragId);
            AuftragDaten auftragDaten = findAuftragDaten(auftragId);
            FfmProductMapping productMapping = findFfmProductMapping(auftragId, baVerlaufAnlassId,
                    (hvtStandort != null ? hvtStandort.getStandortTypRefId() : null), auftragDaten.getProdId());
            WorkforceOrder workforceOrder = aggregateAndBuildWorkforceOrder(auftragId, getReferenceDate(auftragDaten),
                    Optional.empty(), getTechLeistungen(auftragId), productMapping, hvtStandort, auftragDaten);
            return workforceOrder;
        }
        catch (Exception e) {
            throw new FFMServiceException("Fehler beim Erstellen der FFM-Order", e);
        }
    }

    private LocalDateTime getReferenceDate(AuftragDaten auftragDaten) {
        LocalDateTime referenceDate = LocalDateTime.now();
        if (auftragDaten.isWholesaleAuftrag()) {
            referenceDate = DateConverterUtils.asLocalDateTime(auftragDaten.getVorgabeSCV());
        }
        return referenceDate;
    }

    /**
     * Determines the {@link FfmProductMapping} for the assigned Bauauftrag.
     *
     * @param auftragId           techn. Auftrag ID
     * @param baVerlaufAnlassId   Verlauf Anlass
     * @param hvtStandortTypRefId Endstelle_B Standort Typ Referenz Id
     * @param prodId              Produkt des techn. Auftrags
     */
    protected FfmProductMapping findFfmProductMapping(Long auftragId, Long baVerlaufAnlassId, Long hvtStandortTypRefId, Long prodId) {
        try {
            BAVerlaufAnlass anlass = baConfigService.findBAVerlaufAnlass(baVerlaufAnlassId);
            Optional<FfmTyp> ffmTyp = (anlass != null && anlass.getFfmTyp() != null)
                    ? Optional.of(anlass.getFfmTyp()) : Optional.empty();

            if (!ffmTyp.isPresent()) {
                final String msg = String.format("Der Bauauftrags-Anlass mit der ID %s ist nicht fuer eine Uebergabe an FFM konfiguriert.",
                        baVerlaufAnlassId);
                throw new FFMServiceException(msg);
            }

            FfmProductMapping example = new FfmProductMapping();
            example.setProduktId(prodId);
            example.setBaFfmTyp(ffmTyp.get());
            example.setStandortTypRefId(hvtStandortTypRefId);

            List<FfmProductMapping> fullMatchResult = ffmDAO.queryByExample(example, FfmProductMapping.class);
            if (CollectionTools.hasExpectedSize(fullMatchResult, 1)) {
                return fullMatchResult.get(0);
            }
            else {
                FfmProductMapping exampleWithBaTyp = new FfmProductMapping();
                exampleWithBaTyp.setBaFfmTyp(ffmTyp.get());
                List<FfmProductMapping> mappings = ffmDAO.queryByExample(exampleWithBaTyp, FfmProductMapping.class);

                // BestMatch auf Collection, die noch ueber die ProduktId eingeschraenkt ist
                FfmProductMapping bestMatch = filterBestMatch(
                        mappings.stream()
                                .filter(pm -> prodId.equals(pm.getProduktId()))
                                .collect(Collectors.toList()),
                        example, true);

                if (bestMatch == null) {
                    // BestMatch auf Collection, die lediglich den FfmTyp beruecksichtigt hat
                    bestMatch = filterBestMatch(mappings, example, false);
                }

                if (bestMatch != null) {
                    LOGGER.info(String.format("Found FfmProductMapping %s for example %s", bestMatch, example));
                    return bestMatch;
                }
            }
            throw new FFMServiceException(String.format(
                    "Es konnte kein FFM ProductMapping zu folgenden Parametern ermittelt werden: %s", example));
        }
        catch (FFMServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FFMServiceException(String.format(
                    "Fehler bei der Ermittlung des FFM-Mappings: %s", e), e);
        }
    }

    /**
     * Finds the Endstelle_B location for current order id.
     *
     * @param auftragId
     * @return
     */
    protected HVTStandort findHvtStandort(Long auftragId) {
        try {
            Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            HVTStandort hvtStandort = (endstelleB != null && endstelleB.getHvtIdStandort() != null)
                    ? hvtService.findHVTStandort(endstelleB.getHvtIdStandort()) : null;

            return hvtStandort;
        }
        catch (FindException e) {
            throw new FFMServiceException(String.format(
                    "Fehler bei der Ermittlung des HVTStandorts: %s", e), e);
        }
    }

    /**
     * Finds auftragDaten for given order id.
     *
     * @param auftragId
     * @return
     */
    protected AuftragDaten findAuftragDaten(Long auftragId) {
        try {
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(auftragId);

            if (auftragDaten == null) {
                throw new FFMServiceException("Hurrican-Auftrag zum angegebenen Bauauftrag konnte nicht ermittelt werden!");
            }

            return auftragDaten;
        }
        catch (FFMServiceException e) {
            throw e;
        }
        catch (FindException e) {
            throw new FFMServiceException(String.format("Fehler bei der Ermittlung der AuftragDaten: %s", e), e);
        }
    }

    /**
     * Ermittelt aus allen angegebenen {@link FfmProductMapping}s den zum {@code example} besten passenden Kandidaten
     * und gibt diesen zurueck.
     *
     * @param mappings Liste aller moeglichen {@link FfmProductMapping}s
     * @param example  Example-Objekt, zu dem das beste Match ermittelt werden soll
     * @return das Best-Match {@link FfmProductMapping} oder {@code null} wenn kein Match gefunden wurde
     */
    FfmProductMapping filterBestMatch(List<FfmProductMapping> mappings, FfmProductMapping example, boolean returnNullOnDuplicates) {
        FfmProductMapping best = null;
        for (FfmProductMapping candidate : mappings) {
            if (example.isEqualToOrPartOf(candidate)) {
                if (best == null) {
                    best = candidate;
                }
                else {
                    if (best.isSubsetOf(candidate) == candidate.isSubsetOf(best)) {
                        if (returnNullOnDuplicates) {
                            return null;
                        }

                        throw new IllegalStateException(String.format(
                                "Fehler in FFM ProductMapping: zwei identische Konfigurationen gefunden %s und %s und ",
                                candidate, best));
                    }

                    if (candidate.isSubsetOf(best)) {
                        best = candidate;
                    }
                }
            }
        }
        return best;
    }


    /**
     * Determine, build and execute the aggregator chain for the assigned parameter.
     */
    protected WorkforceOrder aggregateAndBuildWorkforceOrder(Long auftragId, LocalDateTime referenceDate,
            Optional<Verlauf> bauauftrag, List<TechLeistung> techLeistungen, FfmProductMapping productMapping,
            HVTStandort hvtStandort, AuftragDaten auftragDaten) throws FindException, ServiceCommandException {
        WorkforceOrder workforceOrder = new WorkforceOrder();
        Collection<Class<? extends AbstractFfmCommand>> aggregatorClasses =
                FfmAggregationConfig.getFfmAggregationConfig(productMapping.getAggregationStrategy());

        AKServiceCommandChain chain = new AKServiceCommandChain();
        for (Class<? extends AbstractFfmCommand> aggregatorClass : aggregatorClasses) {
            // Commands konfigurieren und der Chain zuordnen
            AbstractFfmCommand aggregateCmd = (AbstractFfmCommand) serviceLocator.getCmdBean(aggregatorClass);
            if (aggregateCmd == null) {
                throw new FindException("Could not load FFM AggregatorCommand " + aggregatorClass.getName());
            }

            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_FFM_PRODUCT_MAPPING, productMapping);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_HVT_STANDORT, hvtStandort);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_AUFTRAG_DATEN, auftragDaten);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_TECH_LEISTUNGEN, techLeistungen);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_WORKFORCE_ORDER, workforceOrder);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_AUFTRAG_ID, auftragId);
            aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_REFERENCE_DATE, referenceDate);
            if (bauauftrag.isPresent()) {
                aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_VERLAUF, bauauftrag.get());
                aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_VERLAUF_ANLASS,
                        baConfigService.findBAVerlaufAnlass(bauauftrag.get().getAnlass()));
                if (CollectionTools.isNotEmpty(bauauftrag.get().getSubAuftragsIds())) {
                    aggregateCmd.prepare(AggregateFfmHeaderIdsCommand.KEY_SUB_ORDERS, bauauftrag.get().getSubAuftragsIds());
                }
            }

            chain.addCommand(aggregateCmd);
        }

        executeAggregatorChain(chain);

        return workforceOrder;
    }

    /**
     * executs the assigned aggregator chain
     */
    protected void executeAggregatorChain(AKServiceCommandChain chain) throws ServiceCommandException {
        if (chain.hasCommands()) {
            // ServiceChain ausfuehren (Result-Status wird ueber Chain geprueft)
            chain.executeChain(true);
        }
    }

    @Override
    @CcTxRequiresNew
    public void notifyUpdateOrder(NotifyUpdateOrder in, Long sessionId) {
        final FfmOrderState state = FfmOrderState.from(in.getStateInfo().getState());
        final String orderId = in.getOrderId();

        if (state == null) {
            LOGGER.debug(String.format("Ignoring FFM notification '%s' for order '%s'", in.getStateInfo().getState(), orderId));
            return;
        }

        Verlauf bauauftrag = baService.findVerlaufByWorkforceOrder(in.getOrderId());
        if (bauauftrag == null) {
            LOGGER.info(String.format("Ignoring FFM notification '%s' for order '%s' - no valid Bauauftrag have been found",
                    state, orderId));
        }
        else {
            LOGGER.info(String.format("Processing FFM notification '%s' for order '%s'", state, orderId));
            processValidNotification(in, bauauftrag, sessionId);
        }
    }

    /**
     * Process a valid notification message to the {@link VerlaufAbteilung} table and update her the state.
     *
     * @param in         incoming {@link NotifyUpdateOrder}
     * @param bauauftrag the corresponding Bauauftrag
     * @param sessionId  current seesionId
     */
    private void processValidNotification(NotifyUpdateOrder in, Verlauf bauauftrag, Long sessionId) {
        final FfmOrderState state = FfmOrderState.from(in.getStateInfo().getState());
        final String orderId = in.getOrderId();
        VerlaufAbteilung verlaufAbteilungFfm;

        try {
            verlaufAbteilungFfm = baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM);
            if (verlaufAbteilungFfm == null) {
                throw new FFMServiceException(String.format("FFM Bauauftrag Abteilung not found for workforce order %s", orderId));
            }
        }
        catch (FindException e) {
            throw new FFMServiceException(String.format("Internal error while processing FFM notification '%s' for order '%s'", state, orderId), e);
        }

        try {
            updateBearbeiter(in, verlaufAbteilungFfm);

            if (FfmOrderState.CUST.equals(state) || FfmOrderState.TNFE.equals(state) || FfmOrderState.DONE.equals(state)) {
                boolean notPossible = !FfmOrderState.DONE.equals(state);

                // zusaetzliche VerlaufAbteilung-Datensaetze von FieldService ermitteln und ebenfalls abschliessen
                List<VerlaufAbteilung> vasToClose = baService.findVerlaufAbteilungen(bauauftrag.getId(),
                        new Long[] { Abteilung.FIELD_SERVICE });
                vasToClose.add(verlaufAbteilungFfm);

                for (VerlaufAbteilung vaToClose : vasToClose) {
                    if (vaToClose.getDatumErledigt() == null) {
                        String bearbeiter =
                                (in.getResource() != null && StringUtils.isNotBlank(in.getResource().getId()))
                                        ? in.getResource().getId() : HurricanConstants.UNKNOWN;
                        Date realDate = (in.getActual() != null && in.getActual().getEndTime() != null) ? DateConverterUtils.asDate(in.getActual().getEndTime()) : DateConverterUtils.asDate(LocalDate.now());
                        baService.finishVerlauf4Abteilung(
                                vaToClose,
                                bearbeiter,                              // Bearbeiter
                                null,                                    // Bemerkung
                                realDate,                                // RealDate
                                sessionId,                               // SessionId
                                null,                                    // Zusatzaufwand
                                notPossible,
                                getNotPossibleReasonRef(state));
                    }
                }
            }
            else if (FfmOrderState.ON_SITE.equals(state)) {
                verlaufAbteilungFfm.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                baService.saveVerlaufAbteilung(verlaufAbteilungFfm);
            }

            LOGGER.info(String.format("Processing FFM notification '%s' for order '%s' successfully done", state, orderId));
        }
        catch (Exception e) {
            throw new FFMServiceException(
                    String.format("Failed to close FFM-Bauauftrag after FFM notification '%s' for order '%s'", state, orderId), e);
        }
    }

    /**
     * Aktualisiert den Bearbeiter auf dem FFM Abteilungsverlauf, sofern dieser noch nicht abgeschlossen ist.
     * Aktualisiert wird unabhaengig vom FFM Status in der FFM Nachricht ({@code in.getStateInfo().getState()}).
     */
    void updateBearbeiter(NotifyUpdateOrder in, VerlaufAbteilung verlaufAbteilungFfm) throws StoreException {
        if (verlaufAbteilungFfm != null && verlaufAbteilungFfm.getDatumErledigt() == null
                && in != null && in.getResource() != null) {
            verlaufAbteilungFfm.setBearbeiter(in.getResource().getId());
            baService.saveVerlaufAbteilung(verlaufAbteilungFfm);
        }
    }

    /**
     * Gets reason reference id for order state. If no reason reference is found in the first place try to set default
     * reason. If default reason does not work either return null so no reson reference is set.
     *
     * @param state
     * @return
     */
    protected Long getNotPossibleReasonRef(FfmOrderState state) {
        Reference reason = null;
        try {
            switch (state) {
                case DONE:
                    return null;
                case TNFE:
                    reason = referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_TNFE);
                    break;
                case CUST:
                    reason = referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_CUST);
                    break;
                default:
                    break;
            }
        }
        catch (FindException e) {
            LOGGER.warn(String.format("Failed to get not possible reason reference for state '%s'", state.name()), e);
        }

        try {
            if (reason == null) {
                reason = referenceService.findReference(VerlaufAbteilung.NOT_POSSIBLE_REF_ID_OTHER);
            }
        }
        catch (FindException e) {
            LOGGER.warn("Failed to get default not possible reason reference", e);
        }

        return reason != null ? reason.getId() : null;
    }

    @Override
    public void deleteOrder(Verlauf bauauftrag) {
        if (bauauftrag == null || StringUtils.isBlank(bauauftrag.getWorkforceOrderId())) {
            return;
        }

        try {
            LOGGER.info(String.format("Delete FFM workforce order %s", bauauftrag.getWorkforceOrderId()));
            DeleteOrder deleteOrder = new DeleteOrderBuilder().withId(bauauftrag.getWorkforceOrderId()).build();

            bauauftrag.setWorkforceOrderId(null);
            baService.saveVerlauf(bauauftrag);

            workforceService.deleteOrder(deleteOrder);
        }
        catch (Exception e) {
            // SOAPFaultException ist nicht serialisierbar!!!
            throw new FFMServiceException(
                    String.format("Failed to delete FFM workforce order '%s'", bauauftrag.getWorkforceOrderId()),
                    (e instanceof SOAPFaultException) ? null : e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void textFeedback(String workforceOrderId, String textInfo, LocalDateTime occured) {
        try {
            if (StringUtils.isBlank(textInfo)) {
                LOGGER.info(String.format("Ignoring empty FFM text feedback for order '%s'", workforceOrderId));
                return;
            }

            Verlauf bauauftrag = baService.findVerlaufByWorkforceOrder(workforceOrderId);
            if (bauauftrag == null) {
                LOGGER.warn(String.format("Received FFM text feedback to unknown order '%s'", workforceOrderId));
                LOGGER.debug(String.format("Ignoring FFM text feedback '%s' for order '%s'", textInfo, workforceOrderId));
            }
            else {
                LOGGER.info(String.format("Processing FFM text feedback for order '%s'", workforceOrderId));

                VerlaufAbteilung vaFfm = baService.findVerlaufAbteilung(bauauftrag.getId(), Abteilung.FFM);
                StringBuilder bemerkung = new StringBuilder();
                if (StringUtils.isNotBlank(vaFfm.getBemerkung())) {
                    bemerkung.append(vaFfm.getBemerkung());
                    bemerkung.append(SystemUtils.LINE_SEPARATOR);
                }
                bemerkung.append("(");
                bemerkung.append(occured.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DATE_TIME_LONG)));
                bemerkung.append("): ");
                bemerkung.append(textInfo);

                vaFfm.setBemerkung(StringUtils.right(bemerkung.toString(), VerlaufAbteilung.MAX_CHARS_BEMERKUNG));

                baService.saveVerlaufAbteilung(vaFfm);
            }
        }
        catch (Exception e) {
            throw new FFMServiceException(
                    String.format("Failed to add text feedback to FFM-Bauauftrag for order '%s'", workforceOrderId), e);
        }
    }

    @Override
    @CcTxRequiresNew
    public void materialFeedback(String workforceOrderId, @NotNull NotifyOrderFeedback.Material material) {
        try {
            Verlauf baAuftrag = baService.findVerlaufByWorkforceOrder(workforceOrderId);
            if (baAuftrag == null) {
                LOGGER.warn(String.format("Received FFM material feedback to unknown order '%s'", workforceOrderId));
                LOGGER.debug(String.format("Ignoring FFM material feedback for order '%s'", workforceOrderId));
            }
            else {
                LOGGER.info(String.format("Processing FFM material feedback for order '%s'", workforceOrderId));

                FfmFeedbackMaterial feedbackMaterial = new FfmFeedbackMaterial();
                feedbackMaterial.setProcessed(Boolean.FALSE);
                feedbackMaterial.setWorkforceOrderId(workforceOrderId);
                feedbackMaterial.setMaterialId(material.getId());
                feedbackMaterial.setDescription(material.getDescription());
                feedbackMaterial.setSerialNumber(material.getSerialNumber());
                feedbackMaterial.setSummary(material.getSummary());
                feedbackMaterial.setQuantity(material.getQuantity());

                ffmDAO.store(feedbackMaterial);
                ffmDAO.flushSessionLoud();

                LOGGER.info(String.format("Saved FFM material feedback '%s' for order '%s'", material.getId(), workforceOrderId));
            }
        }
        catch (Exception e) {
            throw new FFMServiceException(
                    String.format("Failed to add material feedback to FFM-Bauauftrag for order '%s'", workforceOrderId), e);
        }
    }

    @Override
    public void markFfmFeedbackMaterialAsProcessed(@NotNull FfmFeedbackMaterial ffmFeedbackMaterial) {
        try {
            ffmFeedbackMaterial.setProcessed(Boolean.TRUE);
            ffmDAO.store(ffmFeedbackMaterial);
            ffmDAO.flushSessionLoud();
        }
        catch (Exception e) {
            throw new FFMServiceException(
                    String.format("Failed to mark the material feedback as being processed! Message: %s", e.getMessage()), e);
        }
    }

    @Override
    public void createMailsForFfmFeedbacks() {
        try {
            FfmFeedbackMaterial notProcessedExample = new FfmFeedbackMaterial();
            notProcessedExample.setProcessed(Boolean.FALSE);

            List<FfmFeedbackMaterial> notProcessed = ffmDAO.queryByExample(notProcessedExample, FfmFeedbackMaterial.class);
            Map<String, List<FfmFeedbackMaterial>> notProcessedByWorkforceOrderId = convertNotProcessedToMap(notProcessed);

            if (notProcessedByWorkforceOrderId != null) {
                for (String workforceOrderId : notProcessedByWorkforceOrderId.keySet()) {
                    Verlauf verlauf = baService.findVerlaufByWorkforceOrder(workforceOrderId);
                    if (verlauf != null) {
                        List<FfmFeedbackMaterial> feedbackMaterials = notProcessedByWorkforceOrderId.get(workforceOrderId);

                        Mail mail = createMailForFfmFeedbacks(verlauf, feedbackMaterials);
                        if (mail != null) {
                            mailService.sendMailFromHurricanServer(mail);
                        }

                        // Feedbacks auch dann als 'prozessiert' markieren, wenn keine Mail generiert wurde
                        // (ist z.B. der Fall, wenn der zugehoerige Auftrag keine Taifun-Referenz besitzt)
                        for (FfmFeedbackMaterial feedback : feedbackMaterials) {
                            markFfmFeedbackMaterialAsProcessed(feedback);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new FFMServiceException(String.format(
                    "Error creating mails for FFM order feedbacks: %s", e.getMessage()), e);
        }
    }

    protected Mail createMailForFfmFeedbacks(Verlauf verlauf, List<FfmFeedbackMaterial> feedbackMaterials) throws FindException {
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(verlauf.getAuftragId());
        if (auftragDaten != null && auftragDaten.getAuftragNoOrig() != null) {
            Auftrag auftrag = ccAuftragService.findAuftragById(verlauf.getAuftragId());

            String produkt = oeService.findProduktName4Auftrag(auftragDaten.getAuftragNoOrig());
            Kunde kunde = kundenService.findKunde(auftrag.getKundeNo());

            Mail mail = new Mail();
            mail.setAuftragId(auftragDaten.getAuftragId());
            mail.setVerlaufId(verlauf.getId());
            mail.setSubject(String.format("Servicebericht (Taifun Auftrag: %s, Kunde: %s, Produkt: %s)",
                    auftragDaten.getAuftragNoOrig(), kunde.getNameVorname(), produkt));
            mail.setFrom(registryService.getStringValue(RegistryService.REGID_MAIL_FROM_HURRICAN));
            mail.setTo(registryService.getStringValue(RegistryService.REGID_FFM_MATERIAL_FEEDBACK_TO));
            mail.setIsTextLongHTML(Boolean.TRUE);

            StringBuilder mailText = new StringBuilder(MATERIAL_MAIL_HEADER);
            for (FfmFeedbackMaterial feedbackMaterial : feedbackMaterials) {
                mailText.append(String.format(MATERIAL_MAIL_TABLE_DATA,
                        feedbackMaterial.getMaterialId(), feedbackMaterial.getQuantity(), feedbackMaterial.getSummary()));
            }
            mailText.append(MATERIAL_MAIL_FOOTER);

            mail.setTextLong(String.format(mailText.toString(), auftragDaten.getAuftragNoOrig()));
            return mail;
        }

        return null;
    }

    private Map<String, List<FfmFeedbackMaterial>> convertNotProcessedToMap(List<FfmFeedbackMaterial> notProcessed) {
        Map<String, List<FfmFeedbackMaterial>> notProcessedByWorkforceOrderId = new HashMap<>();
        for (FfmFeedbackMaterial feedbackMaterial : notProcessed) {
            String orderId = feedbackMaterial.getWorkforceOrderId();
            if (notProcessedByWorkforceOrderId.containsKey(orderId)) {
                notProcessedByWorkforceOrderId.get(orderId).add(feedbackMaterial);
            }
            else {
                List<FfmFeedbackMaterial> feedbacks = new ArrayList<>();
                feedbacks.add(feedbackMaterial);

                notProcessedByWorkforceOrderId.put(orderId, feedbacks);
            }
        }
        return notProcessedByWorkforceOrderId;
    }

    @Override
    public List<FfmQualification> getFfmQualifications(List<TechLeistung> techLeistungen) {
        List<FfmQualification> qualifications = new ArrayList<>();
        for (TechLeistung techLeistung : techLeistungen) {
            List<FfmQualificationMapping> qualificationMappings =
                    ffmDAO.findQualificationsByLeistung(techLeistung.getId());
            qualificationMappings.forEach(qm -> qualifications.add(qm.getFfmQualification()));
        }

        return qualifications;
    }

    @Override
    public List<FfmQualification> getFfmQualifications(AuftragDaten auftragDaten) {
        Set<FfmQualification> qualifications = new HashSet<>();
        if (auftragDaten.getProdId() != null) {
            List<FfmQualificationMapping> qualificationMappings =
                    ffmDAO.findQualificationsByProduct(auftragDaten.getProdId());
            qualificationMappings.forEach(qm -> qualifications.add(qm.getFfmQualification()));
        }

        try {
            // VPN Qualifications ermitteln, sofern Auftrag einem VPN zugeordnet ist!
            AuftragTechnik auftragTechnik = ccAuftragService.findAuftragTechnikByAuftragId(auftragDaten.getAuftragId());
            if (auftragTechnik != null && auftragTechnik.getVpnId() != null) {
                List<FfmQualificationMapping> vpnQualificationMappings = ffmDAO.findQualifications4Vpn();
                vpnQualificationMappings.forEach(qm -> qualifications.add(qm.getFfmQualification()));
            }
        }
        catch (FindException e) {
            throw new FFMServiceException("Fehler bei der Ermittlung / Pruefung auf VPN Skills für den Auftrag!", e);
        }

        return Lists.newArrayList(qualifications);
    }

    @Override
    public List<FfmQualification> getFfmQualifications(HVTStandort hvtStandort) {
        List<FfmQualification> qualifications = new ArrayList<>();
        if (hvtStandort.getStandortTypRefId() != null) {
            List<FfmQualificationMapping> qualificationMappings =
                    ffmDAO.findQualificationsByStandortRef(hvtStandort.getStandortTypRefId());
            qualificationMappings.forEach(qm -> qualifications.add(qm.getFfmQualification()));
        }

        return qualifications;
    }
}
