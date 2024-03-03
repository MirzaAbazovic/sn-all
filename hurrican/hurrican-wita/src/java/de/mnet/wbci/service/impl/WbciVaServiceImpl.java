/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.13
 */
package de.mnet.wbci.service.impl;

import static org.apache.commons.collections.CollectionUtils.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.*;
import javax.validation.constraints.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.mnet.common.tools.NormalizePattern;
import de.mnet.common.tools.PhoneticCheck;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.OrderMatchVO;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.ProcessingError;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungAware;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.RufnummernportierungTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciLocationService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciSchedulerService;
import de.mnet.wbci.service.WbciVaService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

/**
 * Base class for encapsulating the business logic required to create a new Vorabstimmung (incoming or outgoing).
 *
 *
 * @since 10.07.13
 */
@CcTxRequired
public abstract class WbciVaServiceImpl<GF extends WbciGeschaeftsfall> implements WbciVaService<GF> {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(WbciVaServiceImpl.class);
    @Autowired
    protected WbciDao wbciDao;
    @Autowired
    protected WbciCommonService wbciCommonService;
    @Autowired
    protected WbciDeadlineService wbciDeadlineService;
    @Autowired
    protected WbciSchedulerService wbciSchedulerService;
    @Autowired
    protected WbciLocationService wbciLocationService;
    @Autowired
    protected BillingAuftragService billingAuftragService;
    @Autowired
    protected RufnummerService rufnummerService;
    @Autowired
    protected WbciValidationService validationService;
    @Autowired
    protected WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Autowired
    private ConstraintViolationHelper constraintViolationHelper;
    @Autowired
    private WbciMeldungService wbciMeldungService;

    /**
     * {@inheritDoc} *
     */
    @Override
    public VorabstimmungsAnfrage<GF> createWbciVorgang(GF wbciGeschaeftsfall) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("Creating new WBCI Vorgang: %s", wbciGeschaeftsfall.toString()));
        }

        if (hasLinkedGf(wbciGeschaeftsfall)) {
            /**
             * throws an {@link de.mnet.wbci.exception.WbciValidationException} if there are some errors
             */
            validationService.assertLinkedVaHasNoFaultyAutomationTasks(wbciGeschaeftsfall.getStrAenVorabstimmungsId());
            validationService.assertAbgebenderEKPMatchesLinkedAbgebenderEKP(wbciGeschaeftsfall.getAbgebenderEKP(), wbciGeschaeftsfall.getStrAenVorabstimmungsId());
        }

        wbciGeschaeftsfall.setAufnehmenderEKP(CarrierCode.MNET);
        wbciGeschaeftsfall.setAbsender(CarrierCode.MNET);
        final String vorabstimmungsId = wbciGeschaeftsfall.getVorabstimmungsId();
        if (StringUtils.isEmpty(vorabstimmungsId)) {
            wbciGeschaeftsfall.setVorabstimmungsId(wbciCommonService.getNextPreAgreementId(RequestTyp.VA));
        }
        wbciGeschaeftsfall.setStatus(WbciGeschaeftsfallStatus.ACTIVE);
        wbciGeschaeftsfall.setMnetTechnologie(wbciCommonService.getMnetTechnologie(wbciGeschaeftsfall.getAuftragId()));

        /** determine {@link KundenTyp#PK} or {@link KundenTyp#GK} if taifun order no is set **/
        if (wbciGeschaeftsfall.getBillingOrderNoOrig() != null) {
            try {
                wbciGeschaeftsfall.getEndkunde().setKundenTyp(
                        wbciCommonService.getKundenTyp(wbciGeschaeftsfall.getBillingOrderNoOrig()));
            }
            catch (FindException e) {
                throw new WbciServiceException(
                        "Ein unvorhergesehner Fehler bei der Ermittlung des Kundentyps ist aufgetreten: "
                                + e.getMessage(), e
                );
            }
        }

        final LocalDateTime creationDate = LocalDateTime.now();
        VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage = new VorabstimmungsAnfrageBuilder<GF>()
                .withVaKundenwunschtermin(wbciGeschaeftsfall.getKundenwunschtermin())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withIoType(IOType.OUT)
                .withCreationDate(creationDate)
                .withUpdatedAt(creationDate)
                .withRequestStatus(WbciRequestStatus.VA_VORGEHALTEN)
                .build();

        Set<ConstraintViolation<VorabstimmungsAnfrage<GF>>> errors = validationService.checkWbciMessageForErrors(
                vorabstimmungsAnfrage.getEKPPartner(), vorabstimmungsAnfrage);
        if (!isEmpty(errors)) {
            throw new WbciServiceException(constraintViolationHelper.generateErrorMsg(errors));
        }

        vorabstimmungsAnfrage = wbciDao.store(vorabstimmungsAnfrage);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("WBCI Vorgang created successfully: %s", vorabstimmungsAnfrage));
        }

        wbciSchedulerService.scheduleRequest(vorabstimmungsAnfrage);

        return vorabstimmungsAnfrage;
    }

    private boolean hasLinkedGf(GF wbciGeschaeftsfall) {
        return StringUtils.isNotEmpty(wbciGeschaeftsfall.getStrAenVorabstimmungsId());
    }

    @Override
    public void processIncomingVA(@NotNull MessageProcessingMetadata metadata, @NotNull VorabstimmungsAnfrage<GF> va) {
        ProcessingError processingError = checkForHighSeverityError(va);
        if (processingError == null) {
            va.setRequestStatus(WbciRequestStatus.VA_EMPFANGEN);
            va.getWbciGeschaeftsfall().setStatus(WbciGeschaeftsfallStatus.ACTIVE);

            wbciDao.store(va);
            metadata.setPostProcessMessage(true);

            // check the request to see if its valid
            validateIncomingVorabstimmung(va);

            wbciDeadlineService.updateAnswerDeadline(va);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("WBCI Vorabstimmungsanfrage received successfully: %s", va));
            }
        }
        else {
            prepareProcessingMetaData(metadata, processingError, false);
            autoProcessError(va, processingError);
        }
    }

    private ProcessingError checkForHighSeverityError(VorabstimmungsAnfrage<GF> va) {
        ProcessingError processingError = null;

        if (validationService.isDuplicateVaRequest(va)) {
            processingError = new ProcessingError(MeldungsCode.BVID, String.format(Abbruchmeldung.BEGRUENDUNG_DOPPELTE_VA, va.getVorabstimmungsId()));
        }

        if (processingError != null) {
            String logMsg = String.format("Could not process incoming VA with VA-Id:'%s' because: %s",
                    va.getVorabstimmungsId(),
                    processingError.getErrorMessage());
            LOGGER.info(logMsg);
        }

        return processingError;
    }

    private void autoProcessError(VorabstimmungsAnfrage<GF> va, ProcessingError processingError) {
        MessageProcessingMetadata responseMetadata = new MessageProcessingMetadata();
        prepareProcessingMetaData(responseMetadata, processingError, true);

        final Abbruchmeldung abbm = new AbbruchmeldungBuilder()
                .buildOutgoingForVa(va.getWbciGeschaeftsfall(),
                        processingError.getErrorMessage(),
                        processingError.getMeldungsCode());

        if (!processingError.isTechnical()) {
            wbciDao.store(abbm);
        }

        wbciMeldungService.sendErrorResponse(responseMetadata, abbm);
    }

    private void prepareProcessingMetaData(MessageProcessingMetadata metadata, ProcessingError processingError, boolean isResponse) {
        if (MeldungsCode.BVID.equals(processingError.getMeldungsCode())) {
            if (isResponse) {
                metadata.setResponseToDuplicateVaRequest(true);
            } else {
                metadata.setIncomingMessageDuplicateVaRequest(true);
            }
        }

        if (processingError.isTechnical()) {
            metadata.setPostProcessMessage(false);
        }
    }

    private void validateIncomingVorabstimmung(VorabstimmungsAnfrage vorabstimmungsAnfrage) {
        CarrierCode partnerCarrierCode = vorabstimmungsAnfrage.getEKPPartner();
        Set<ConstraintViolation<VorabstimmungsAnfrage>> errors = validationService.checkWbciMessageForErrors(partnerCarrierCode, vorabstimmungsAnfrage);

        if (CollectionUtils.isNotEmpty(errors)) {
            WbciGeschaeftsfall wbciGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();
            String errorMsg = constraintViolationHelper.generateErrorMsgForInboundMsg(errors);
            wbciGeschaeftsfallService.markGfForClarification(wbciGeschaeftsfall.getId(), errorMsg, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean autoAssignTaifunOrderToVA(@NotNull VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage)
            throws FindException {
        final String vaId = vorabstimmungsAnfrage.getVorabstimmungsId();
        final GF vaGeschaeftsfall = vorabstimmungsAnfrage.getWbciGeschaeftsfall();

        LOGGER.info(String.format("Search matching orders for incoming VA with VA-Id:'%s'", vaId));

        Collection<OrderMatchVO> orderMatches = getSuggestedOrderMatchVOs(vaGeschaeftsfall, true);
        OrderMatchVO orderToAssign =
                (CollectionTools.hasExpectedSize(orderMatches, 1))
                        ? orderMatches.iterator().next()
                        : null;

        LOGGER.info(String.format("Found %s matching orders for incoming VA with VA-Id:'%s'", orderMatches.size(), vaId));

        if (orderToAssign != null) {
            LOGGER.info(String.format("Found Taifun order to assign: '%s'", orderToAssign.getOrderNoOrig()));
            LOGGER.info(String.format("Search active WBCI VAs for Taifun order: '%s'", orderToAssign.getOrderNoOrig()));

            final List<WbciGeschaeftsfall> activeGFs = wbciCommonService.findActiveGfByTaifunId(orderToAssign.getOrderNoOrig(), true);

            LOGGER.info(String.format("Found %s active WBCI VAs for Taifun order:'%s'", activeGFs.size(), orderToAssign.getOrderNoOrig()));

            if (isEmpty(activeGFs)) {
                wbciCommonService.assignTaifunOrder(vaId, orderToAssign.getOrderNoOrig(), false);
                return true;
            }
            else if (activeGFs.size() == 1 && wbciGeschaeftsfallService.isLinkedToStrAenGeschaeftsfall(vaId, activeGFs.get(0).getId())) {
                final WbciGeschaeftsfall strAenGeschaeftsfall = activeGFs.get(0);
                if (isNewVaValidForStrAenGeschaeftsfall(vaGeschaeftsfall, strAenGeschaeftsfall)) {
                    wbciGeschaeftsfallService.assignTaifunOrderAndCloseStrAenGeschaeftsfall(
                            vaId, orderToAssign.getOrderNoOrig(), strAenGeschaeftsfall.getId(), false);
                    return true;
                }
            }
        }
        LOGGER.info(String.format("Unable to auto assign Taifun order for incoming VA with VA-Id:'%s'", vaId));

        return false;
    }

    /**
     * Checks if the incoming STR-AEN {@link WbciGeschaeftsfall} is valid for an assignment to the actual {@link WbciGeschaeftsfall}.
     *
     * @param newVaGeschaeftsfall current {@link WbciGeschaeftsfall} of the new incoming {@link VorabstimmungsAnfrage}
     * @param strAenGschaeftsfall {@link WbciGeschaeftsfall} of the {@link StornoAnfrage}
     * @return  {@code false}:
     *          <ul>
     *              <li>if strAenGschaeftsfall has the status {@link WbciGeschaeftsfallStatus#NEW_VA_EXPIRED}
     *                  <br>=> strAenGschaeftsfall need to be closed before the new VA could be accepted </br>
     *              </li>
     *              <li>if the {@link WbciGeschaeftsfall} the strAenGschaeftsfall has the typ {@link GeschaeftsfallTyp#VA_RRNP}
     *                  and the newVaGeschaeftsfall typ is {@link GeschaeftsfallTyp#VA_KUE_MRN} or {@link GeschaeftsfallTyp#VA_KUE_ORN}
     *                  <br>=> newVa will be automatically answered with an {@link Abbruchmeldung} with the reason
     *                         {@link Abbruchmeldung#BEGRUENDUNG_WECHSEL_RRNP_MRNORN}</br>
     *              </li>
     *          </ul>
     *          {@code true}: if the above criteria won't match
     */
    private boolean isNewVaValidForStrAenGeschaeftsfall(WbciGeschaeftsfall newVaGeschaeftsfall, WbciGeschaeftsfall strAenGschaeftsfall) {
        if (WbciGeschaeftsfallStatus.NEW_VA_EXPIRED.equals(strAenGschaeftsfall.getStatus())) {
            return false;
        }

        // GF-Wechsel RRNP auf MRN/ORN pruefen und sofort mit ABBM ablehnen
        if (wbciGeschaeftsfallService.isGeschaeftsfallWechselRrnpToMrnOrn(strAenGschaeftsfall, newVaGeschaeftsfall)) {
            wbciMeldungService.createAndSendWbciMeldung(
                    new AbbruchmeldungBuilder().withWbciGeschaeftsfall(strAenGschaeftsfall)
                            .withBegruendung(String.format(
                                    Abbruchmeldung.BEGRUENDUNG_WECHSEL_RRNP_MRNORN,
                                    strAenGschaeftsfall.getVorabstimmungsId()), true)
                            .build(),
                    newVaGeschaeftsfall.getVorabstimmungsId());
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public List<Pair<BAuftragVO, OrderMatchVO>> getTaifunOrderAssignmentCandidates(String vorabstimmungsId,
            boolean filterViolations) {
        final WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        Collection<OrderMatchVO> suggesOrderMatchVOs = getSuggestedOrderMatchVOs(wbciGeschaeftsfall, filterViolations);
        List<Pair<BAuftragVO, OrderMatchVO>> suggestedOrderInformations = new ArrayList<>();

        for (OrderMatchVO orderMatchVO : suggesOrderMatchVOs) {
            try {
                BAuftragVO bAuftragVO = billingAuftragService.getBasicOrderInformation(orderMatchVO.getOrderNoOrig());
                if (bAuftragVO != null) {
                    suggestedOrderInformations.add(Pair.create(bAuftragVO, orderMatchVO));
                }
            }
            catch (FindException e) {
                throw new WbciServiceException("Bei der Ermittlung des Taifun-Auftrags '"
                        + orderMatchVO.getOrderNoOrig() + "' ist ein unerwarteter Fehler aufgetreten!");
            }
        }
        return suggestedOrderInformations;
    }

    /**
     * see {@link #getTaifunOrderAssignmentCandidates(String, boolean)}
     */
    protected Collection<OrderMatchVO> getSuggestedOrderMatchVOs(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall,
            boolean filterViolations) {
        Collection<OrderMatchVO> orderMatchVOs = new ArrayList<>();

        // create matches for DN
        if (wbciGeschaeftsfall.hasRufnummerIdentification()) {
            LOGGER.info("Try to find matching orders by rufnummer identification");
            final Set<Long> orderNoOrigsByDN = getOrderNoOrigsByDNs(wbciGeschaeftsfall);
            LOGGER.debug(String.format("Found %s matching order candidates by rufnummer identification", orderNoOrigsByDN.size()));
            orderMatchVOs = OrderMatchVO.createOrderMatches(orderNoOrigsByDN, OrderMatchVO.BasicSearch.DN);
            LOGGER.info(String.format("Found %s matching order by rufnummer identification", orderMatchVOs.size()));
        }

        /**
         * Search for standort-maches when:
         * <ul>
         *     <li>GF is VA-KUE-MRN and VA-KUE-ORN</li>
         *     <li>filterViolations is FALSE</li>
         *     <li>filterViolations is TRUE but no unique match is already found</li>
         * </ul>
         */
        boolean searchByStandort = !GeschaeftsfallTyp.VA_RRNP.equals(wbciGeschaeftsfall.getTyp())
                && (!filterViolations || (filterViolations && orderMatchVOs.size() != 1));
        if (searchByStandort) {
            LOGGER.info("Try to find matching orders by geo id location");
            //create OrderMatchVOs for Standort
            final Set<Long> orderNoOrigsByStandort = getOrderNoOrigsByGeoId(wbciGeschaeftsfall);
            LOGGER.debug(String.format("Found %s matching order candidates by geo id location", orderNoOrigsByStandort.size()));
            Collection<OrderMatchVO> orderMatchVOsStandort = OrderMatchVO.createOrderMatches(orderNoOrigsByStandort, OrderMatchVO.BasicSearch.LOCATION);
            LOGGER.info(String.format("Found %s matching orders by geo id location", orderMatchVOsStandort.size()));

            // if over RN search has found something
            if (CollectionUtils.isNotEmpty(orderMatchVOs)) {
                // 1. determine location violation for current order matches
                OrderMatchVO.violates(orderMatchVOs, orderNoOrigsByStandort, OrderMatchVO.MatchViolation.LOCATION);
                // 2. determine number violation for all new matches
                OrderMatchVO.violates(orderMatchVOsStandort, OrderMatchVO.getOrderNoOrigs(orderMatchVOs),
                        OrderMatchVO.MatchViolation.DN);
            }

            //add the new found orders matches
            orderMatchVOs = OrderMatchVO.addNewOrderMatches(orderMatchVOs, orderMatchVOsStandort);
        }

        //filter out all taifun orders with only non billable DNs
        orderMatchVOs = filterBillableTaifunOrders(orderMatchVOs);

        // at least determine and set vialotion for not machting Names
        Set<Long> orderNoOrigsByName = filterAuftragIdsByName(wbciGeschaeftsfall, OrderMatchVO.getOrderNoOrigs(orderMatchVOs));
        OrderMatchVO.violates(orderMatchVOs, orderNoOrigsByName, OrderMatchVO.MatchViolation.NAME);


        if (filterViolations) {
            LOGGER.info("Filter matching orders with violations");
            // filter out all OrderMatchVOs with violations
            orderMatchVOs = Collections2.filter(orderMatchVOs, new Predicate<OrderMatchVO>() {
                @Override
                public boolean apply(@Nullable OrderMatchVO input) {
                    return input == null || CollectionUtils.isEmpty(input.getMatchViolations());
                }
            });
        }

        LOGGER.info(String.format("Found %s matching orders", orderMatchVOs.size()));
        return orderMatchVOs;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Set<Long> getOrderNoOrigsByDNs(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (wbciGeschaeftsfall instanceof RufnummernportierungAware) {
            Rufnummernportierung portierung = ((RufnummernportierungAware) wbciGeschaeftsfall).getRufnummernportierung();
            if (RufnummernportierungTyp.EINZEL == portierung.getTyp()) {
                final RufnummernportierungEinzeln rufnummernportierungEinzeln = (RufnummernportierungEinzeln) portierung;

                final Set<Long> result = new HashSet<>();
                final List<RufnummerOnkz> rufnummernOnkzs = rufnummernportierungEinzeln.getRufnummernOnkz();
                for (RufnummerOnkz rufnummernOnkz : rufnummernOnkzs) {
                    final Set<Long> auftragIdsByEinzelrufnummer;
                    try {
                        auftragIdsByEinzelrufnummer = rufnummerService
                                .findAuftragIdsByEinzelrufnummer(rufnummernOnkz.getOnkz(), rufnummernOnkz.getRufnummer());
                    }
                    catch (FindException e) {
                        throw new WbciServiceException("Fehler bei der Suche nach Einzelrufnummern aufgetreten!", e);
                    }
                    if (auftragIdsByEinzelrufnummer != null) {
                        result.addAll(auftragIdsByEinzelrufnummer);
                    }
                }
                return result;
            }
            else if (RufnummernportierungTyp.ANLAGE == portierung.getTyp()) {
                final RufnummernportierungAnlage rufnummernportierungAnlage = (RufnummernportierungAnlage) portierung;

                try {
                    return rufnummerService.findAuftragIdsByBlockrufnummer(
                            rufnummernportierungAnlage.getOnkz(), rufnummernportierungAnlage.getDurchwahlnummer());
                }
                catch (FindException e) {
                    throw new WbciServiceException("Fehler bei der Suche nach Blockrufnummern aufgetreten!", e);
                }
            }
            else {
                throw new WbciServiceException(
                        String.format("Rufnummerportierungstyp '%s' nicht unterstützt", portierung.getTyp()));
            }
        }
        else {
            if (wbciGeschaeftsfall instanceof WbciGeschaeftsfallKueOrn) {
                final RufnummerOnkz anschlussIdentifikation = ((WbciGeschaeftsfallKueOrn) wbciGeschaeftsfall)
                        .getAnschlussIdentifikation();
                try {
                    return rufnummerService.findAuftragIdsByEinzelrufnummer(anschlussIdentifikation.getOnkz(),
                            anschlussIdentifikation.getRufnummer());
                }
                catch (Exception e) {
                    throw new WbciServiceException("Fehler bei der Suche nach Einzelrufnummern aufgetreten!", e);
                }
            }
            throw new WbciServiceException(String.format("Unerwarteter Geschaeftsfalltyp '%s' gefunden. An dieser " +
                    "Stelle wird ein '%s' erwartet.", wbciGeschaeftsfall.getTyp(), GeschaeftsfallTyp.VA_KUE_ORN));
        }
    }

    /**
     * Ermittelt auf Basis der Adressdaten innerhalb von der angegebenen {@link VorabstimmungsAnfrage} die passenden
     * Taifun Auftraege. Es werden zuerst alle passende GeoIds ermittelt und daraus werden die Taifun-Auftraege
     * ermittelt.
     */
    protected Set<Long> getOrderNoOrigsByGeoId(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (wbciGeschaeftsfall instanceof WbciGeschaeftsfallKue) {
            final WbciGeschaeftsfallKue wbciGeschaeftsfallKue = (WbciGeschaeftsfallKue) wbciGeschaeftsfall;
            final Standort standort = wbciGeschaeftsfallKue.getStandort();

            List<Long> geoIds = wbciLocationService.getLocationGeoIds(standort);

            Set<Long> result = new HashSet<>();
            for (Long geoId : geoIds) {
                try {
                    // nur Auftraege ermitteln, die noch aktiv sind bzw. deren Kuendigungsdatum in der Zukunft liegt
                    result.addAll(billingAuftragService.findAuftragIdsByGeoId(geoId, true));

                    if (result.isEmpty()) {
                        // auch bereits gekuendigte Auftraege ermitteln
                        result.addAll(billingAuftragService.findAuftragIdsByGeoId(geoId, false));
                    }
                }
                catch (FindException e) {
                    throw new WbciServiceException(
                            String.format("Fehler bei der Suche nach AuftragIds für GeoId '%s'!", geoId), e);
                }
            }
            return result;
        }
        return Collections.emptySet();
    }

    /**
     * Filters out all billing orders witch have at least one billable {@link Rufnummer}. All others orders will be
     * ignored.
     *
     * @param orderMatchVOs Collection of {@link OrderMatchVO}s
     * @return a filtered collection of {@link OrderMatchVO}s
     */
    protected Collection<OrderMatchVO> filterBillableTaifunOrders(Collection<OrderMatchVO> orderMatchVOs) {
        if (CollectionUtils.isNotEmpty(orderMatchVOs)) {
            LOGGER.info("Filter matching orders with non-billable DNs");
            List<OrderMatchVO> result = new ArrayList<>();
            for (OrderMatchVO orderMatchVO : orderMatchVOs) {
                try {
                    LOGGER.debug(String.format("Search billable DN for Taifun order: '%s'", orderMatchVO.getOrderNoOrig()));
                    List<Rufnummer> allRNs4Auftrag = rufnummerService.findAllRNs4Auftrag(orderMatchVO.getOrderNoOrig());
                    if (CollectionUtils.isNotEmpty(allRNs4Auftrag)) {
                        for (Rufnummer rn : allRNs4Auftrag) {
                            //if at least one RN is marked as billable, add the billing order to the result
                            if (!BooleanTools.nullToFalse(rn.getNonBillable())) {
                                result.add(orderMatchVO);
                                LOGGER.debug(String.format("Found billable DN for Taifun order: '%s'", orderMatchVO.getOrderNoOrig()));
                                break;
                            }
                        }
                    }
                }
                catch (FindException e) {
                    throw new WbciServiceException(String.format(
                            "Fehler bei der Ermittlung der Rufnummerndaten für den Billing-Auftrag '%s'"
                            , orderMatchVO.getOrderNoOrig())
                            , e);
                }
            }

            LOGGER.info(String.format("Filtered %s non-billable Taifun orders", orderMatchVOs.size() - result.size()));
            return result;
        }
        return orderMatchVOs;
    }

    /**
     * Filtert die Liste mit den angegebenen Taifun Auftragsnummern. Es bleiben nur die Auftraege in der Liste, bei
     * denen der Anschlussinhaber mit den Namen in der {@link VorabstimmungsAnfrage} uebereinstimmen.
     * 
     * @param wbciGeschaeftsfall der wbciGeschaeftsfall
     * @param orderNoOrigs zu filternde Liste
     */
    protected Set<Long> filterAuftragIdsByName(@NotNull WbciGeschaeftsfall wbciGeschaeftsfall, Set<Long> orderNoOrigs) {
        if (orderNoOrigs == null || orderNoOrigs.isEmpty()) {
            return orderNoOrigs;
        }

        LOGGER.info("Filter matching orders by customer name");
        // phonetische Pruefung mit 'Koelner Phonetik' und nachgelagertem Levenshtein Vergleich
        // (ohne den Levenshtein Vergleich gibt es mehr Treffer)
        PhoneticCheck phoneticCheck = new PhoneticCheck(PhoneticCheck.Codec.COLOGNE, true, NormalizePattern.NAME_PATTERNS);

        Set<Long> filteredNoOrigs = Sets.newCopyOnWriteArraySet(orderNoOrigs);
        for (Long orderNoOrig : filteredNoOrigs) {
            LOGGER.debug(String.format("Perform name filter on Taifun order: '%s'", orderNoOrig));
            try {
                Adresse anschlussAdresse = billingAuftragService.findAnschlussAdresse4Auftrag(
                        orderNoOrig, Endstelle.ENDSTELLEN_TYP_B);

                boolean match = false;
                if (anschlussAdresse != null) {
                    PersonOderFirma personOderFirma = wbciGeschaeftsfall.getEndkunde();

                    // Implementation for companies
                    if (personOderFirma instanceof Firma) {
                        Firma firma = (Firma) personOderFirma;
                        String anschlussName = StringTools.join(
                                new String[] { anschlussAdresse.getName(), anschlussAdresse.getVorname() }, " ", true);
                        String wbciName = StringTools.join(
                                new String[] { firma.getFirmenname(), firma.getFirmennamenZusatz() }, " ", true);
                        match = phoneticCheck.isPhoneticEqual(anschlussName, wbciName);
                    }
                    // Implementation for persons
                    else if (personOderFirma instanceof Person) {
                        Person person = (Person) personOderFirma;
                        match = phoneticCheck.isPhoneticEqual(anschlussAdresse.getName(), person.getNachname());
                        if (match) {
                            match = phoneticCheck.isPhoneticEqual(anschlussAdresse.getVorname(), person.getVorname());
                        }
                    }
                }
                // remove number if phonetic check is false
                if (!match) {
                    filteredNoOrigs.remove(orderNoOrig);
                    LOGGER.debug(String.format("Removing non-matching Taifun order: '%s'", orderNoOrig));
                }
            }
            catch (FindException e) {
                throw new WbciServiceException(String.format(
                        "Fehler bei der phonetischen Namensuche nach AuftragIds für die Vorabstimmung '%s'!",
                        wbciGeschaeftsfall.getVorabstimmungsId()), e);
            }
        }

        LOGGER.info(String.format("Filtered %s non-matching Taifun orders", orderNoOrigs.size() - filteredNoOrigs.size()));
        return filteredNoOrigs;
    }

}
