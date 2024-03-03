/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2014
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;
import static de.mnet.wbci.model.AutomationTask.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.mnet.wbci.exception.WbciAutomationValidationException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.helper.LeitungHelper;
import de.mnet.wbci.model.helper.TechnischeRessourceHelper;
import de.mnet.wbci.service.WbciAutomationService;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciKuendigungsService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
@CcTxRequired
public class WbciAutomationServiceImpl extends AbstractWbciCommonAutomationService implements WbciAutomationService {

    private static final Logger LOGGER = Logger.getLogger(WbciAutomationServiceImpl.class);
    static final String KEINE_WITA_KUENDIGUNGEN_NOTWENDIG = "Keine WITA Kuendigung(en) notwendig!";
    private static final String NO_TAIFUN_ORDER_ASSIGNED = "Der Vorabstimmung '%s' wurde keinen Taifun-Auftrag zugeordnet";
    private static final String HURRICAN_ORDER_SIZE_NOT_EQUAL_TO_ONE = "Für die Vorabstimmung '%s' " +
            "ist eine automatische Erzeugung des WITA-Vorgangs ist nicht möglich, " +
            "da die Anzahl der zugewiesenen Hurrican-Aufträge ungleich 1 ist - Hurrican-Auftrag-IDs: %s";
    private static final String WITA_VERTR_NR_SIZE_NOT_EQUAL_TO_ONE = "Für die Vorabstimmung '%s' " +
            "ist eine automatische Erzeugung des WITA-Vorgangs ist nicht möglich, " +
            "da die Anzahl der zu übernehmenden Leitung in der %s ungleich 1 ist - WITA-Vertragsnummern: %s";
    private static final String NO_ACTIVE_WITA_INSTANCE =
            "Konnte keinen (oder keinen eindeutigen) aktiven WITA Vorgang zur Vorabstimmungs-Id '%s' ermitteln";
    private static final String WITA_ALREADY_CANCELLED =
            "Zu dem WITA Vorgang '%s' wurde bereits ein STORNO ausgelöst. Eine TV ist nicht mehr möglich!";
    @Autowired
    private WbciElektraService wbciElektraService;

    @Autowired
    private WbciMeldungService wbciMeldungService;

    @Autowired
    private WbciWitaServiceFacade wbciWitaServiceFacade;

    @Autowired
    private WbciCommonService wbciCommonService;

    @Autowired
    private WbciKuendigungsService wbciKuendigungsService;


    private static String format(Set<String> strings) {
        StringBuilder sb = new StringBuilder();
        if (strings != null) {
            Iterator<String> it = strings.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private static void assertMnetIsReceivingCarrier(WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (!CarrierCode.MNET.equals(wbciGeschaeftsfall.getAufnehmenderEKP())) {
            throw new WbciServiceException(String.format("Invalid usage. Carrier '%s' must have '%s' role in geschaeftsfall '%s'",
                    wbciGeschaeftsfall.getAufnehmenderEKP(),
                    "receiving",
                    wbciGeschaeftsfall.getVorabstimmungsId()));
        }
    }

    @Override
    public Collection<String> processAutomatableRuemVas(AKUser user) {
        LOGGER.info("Starting automatic RUEM-VA processing");
        final Collection<String> preagreementsWithAutomatableRuemVa = wbciGeschaeftsfallService
                .findPreagreementsWithAutomatableRuemVa();
        for (String preagreementId : preagreementsWithAutomatableRuemVa) {
            RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(preagreementId, RueckmeldungVorabstimmung.class);

            ElektraResponseDto elektraResponseDto;
            try {
                elektraResponseDto = wbciElektraService.processRuemVaNewTx(preagreementId, user);
            }
            catch (Exception e) {
                handleAutomationException(ruemVa, TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN, e, user);
                continue;
            }
            if (elektraResponseDto.isSuccessfull()) {
                try {
                    wbciMeldungService.sendAutomatedAkmTr(preagreementId, user);
                }
                catch (Exception e) {
                    handleAutomationException(ruemVa, TaskName.WBCI_SEND_AKMTR, e, user);
                }
            }
        }
        LOGGER.info(String.format("Automatically processed %s RUEM-VAs", preagreementsWithAutomatableRuemVa.size()));
        return preagreementsWithAutomatableRuemVa;
    }

    @Override
    public Collection<String> processAutomatableAkmTrs(AKUser user) {
        LOGGER.info("Starting automatic AKM-TR processing");
        int processErrors = 0;
        final Collection<UebernahmeRessourceMeldung> akmTrs = wbciMeldungService
                .findAutomatableAkmTRsForWitaProcesing();
        List<String> createdWitaOrders = new ArrayList<>();

        for (UebernahmeRessourceMeldung akmTr : akmTrs) {
            String preagreementId = akmTr.getWbciGeschaeftsfall().getVorabstimmungsId();
            AkmTrAutomationTask akmTrAutomationTask = AkmTrAutomationTask.determineTask(akmTr.isUebernahme());
            try {
                switch (akmTrAutomationTask) {
                    case NEUBESTELLUNG:
                        checkOnlyOneHurricanOrderAssigned(akmTr);
                        break;
                    case ANBIETERWECHSEL:
                        checkOnylOneWitaVertNrInAkmTr(akmTr);
                        break;
                    default:
                        break;
                }

                String carrierRefId = wbciWitaServiceFacade.createWitaVorgang(akmTrAutomationTask, preagreementId, user).getCarrierRefNr();
                createdWitaOrders.add(carrierRefId);
            }
            catch (Exception e) {
                processErrors++;
                handleAutomationException(akmTr, akmTrAutomationTask.getTaskName(), e, user);
            }
        }

        if (CollectionUtils.isNotEmpty(akmTrs)) {
            LOGGER.info(String.format("Automatically processed %s AKM-TRs", createdWitaOrders.size()));
            LOGGER.info(String.format("%s AKM-TRs have not processed, because automation errors", processErrors));
        }
        else {
            LOGGER.info("No AKM-TRs found for auto processing.");
        }
        return createdWitaOrders;
    }

    @Override
    public Collection<String> processAutomatableIncomingAkmTrs(AKUser user) {
        LOGGER.info("Starting automatic incoming AKM-TR processing");
        int processErrors = 0;
        final Collection<UebernahmeRessourceMeldung> akmTrs = wbciMeldungService
                .findAutomatableIncomingAkmTRsProcesing();
        List<String> createdWitaOrders = new ArrayList<>();

        for (UebernahmeRessourceMeldung akmTr : akmTrs) {
            String preagreementId = akmTr.getWbciGeschaeftsfall().getVorabstimmungsId();
            try {
                boolean executeWitaCancellation = true;
                if (akmTr.getWbciGeschaeftsfall().getTyp() == GeschaeftsfallTyp.VA_KUE_MRN) {
                    // die PkiAuf Kennung muss für die zu portierenden Rufnummern aktualisiert werden
                    ElektraResponseDto elektraResponseDto = wbciElektraService.updatePortKennungTnbTx(preagreementId, user);
                    executeWitaCancellation = elektraResponseDto.isSuccessfull();
                }

                if (executeWitaCancellation) {
                    SortedSet<String> witaVertragsNrsForCancellation =
                            wbciKuendigungsService.getCancellableWitaVertragsnummern(preagreementId);
                    if (CollectionUtils.isEmpty(witaVertragsNrsForCancellation)) {
                        // AutomationTask=Success schreiben
                        wbciGeschaeftsfallService.createOrUpdateAutomationTaskNewTx(preagreementId, akmTr,
                                TaskName.WITA_SEND_KUENDIGUNG, COMPLETED, KEINE_WITA_KUENDIGUNGEN_NOTWENDIG, user);
                    }
                    else {
                        // WITA Kuendigungen durchfuehren, falls notwendig
                        createdWitaOrders.addAll(createWitaCancellationOrders(akmTr, witaVertragsNrsForCancellation, user));
                    }

                    // WBCI Geschaeftsfall-Status auf PASSIV setzen (wbciCommonService#closeProcessing)
                    WbciRequest lastWbciRequest = wbciCommonService.findLastWbciRequest(preagreementId);
                    if (lastWbciRequest != null) {
                        wbciCommonService.closeProcessing(lastWbciRequest);
                    }
                }
            }
            catch (Exception e) {
                processErrors++;
                handleAutomationException(akmTr, TaskName.WITA_SEND_KUENDIGUNG, e, user);
            }
        }

        if (CollectionUtils.isNotEmpty(akmTrs)) {
            LOGGER.info(String.format("Automatically processed %s incoming AKM-TRs", createdWitaOrders.size()));
            LOGGER.info(String.format("%s incoming AKM-TRs have not processed, because automation errors", processErrors));
        }
        else {
            LOGGER.info("No incoming AKM-TRs found for auto processing.");
        }
        return createdWitaOrders;
    }

    private List<String> createWitaCancellationOrders(UebernahmeRessourceMeldung akmTr,
            SortedSet<String> witaVertragsNrsForCancellation, AKUser user) {
        List<WitaCBVorgang> witaOrders = wbciWitaServiceFacade.createWitaCancellations(
                akmTr, witaVertragsNrsForCancellation, user);
        return witaOrders.stream()
                .map(CBVorgang::getCarrierRefNr)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> processAutomatableErlmTvs(AKUser user) {
        LOGGER.info("Starting automatic ERLM-TV processing");
        int processErrors = 0;
        int tvNotNecessary = 0;
        List<String> processedErlmTvs = new ArrayList<>();

        final Collection<ErledigtmeldungTerminverschiebung> erlmTvs = wbciMeldungService
                .findAutomatableTvErlmsForWitaProcessing();
        for (ErledigtmeldungTerminverschiebung erlmTv : erlmTvs) {
            String preagreementId = erlmTv.getWbciGeschaeftsfall().getVorabstimmungsId();
            try {
                WitaCBVorgang witaCBVorgang = wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(preagreementId);
                if (witaCBVorgang == null) {
                    throw new WbciAutomationValidationException(String.format(NO_ACTIVE_WITA_INSTANCE, preagreementId));
                }

                if (witaCBVorgang.isStorno()) {
                    throw new WbciAutomationValidationException(String.format(WITA_ALREADY_CANCELLED,
                            witaCBVorgang.getCarrierRefNr()));
                }

                if (!DateTools.isDateEqual(witaCBVorgang.getVorgabeMnet(), Date.from(erlmTv.getWechseltermin().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    // WITA TV ausloesen oder Termin anpassen
                    wbciWitaServiceFacade.doWitaTerminverschiebung(
                            preagreementId,
                            witaCBVorgang.getId(),
                            user,
                            TamUserTask.TamBearbeitungsStatus.TV_60_TAGE);

                    processedErlmTvs.add(preagreementId);
                }
                else {
                    tvNotNecessary++;
                    LOGGER.info(String.format(
                            "No WITA TV after WBCI ERLM-TV triggered for %s because KWT is equal. (VA-Id: %s)",
                            witaCBVorgang.getCarrierRefNr(), erlmTv.getVorabstimmungsId()));
                }
            }
            catch (Exception e) {
                processErrors++;
                handleAutomationException(erlmTv, TaskName.WITA_SEND_TV, e, user);
            }
        }

        if (CollectionUtils.isNotEmpty(erlmTvs)) {
            LOGGER.info(String.format("Automatically processed %s ERLM-TVs", processedErlmTvs.size()));
            LOGGER.info(String.format(
                    "%s ERLM-TVs have not been processed, because WITA TV is not necessary (date is equal).",
                    tvNotNecessary));
            LOGGER.info(String.format("%s ERLM-TVs have not been processed, because automation errors occured.",
                    processErrors));
        }
        else {
            LOGGER.info("No ERLM-TVs found for auto processing");
        }

        return processedErlmTvs;
    }

    @Override
    public Collection<String> processAutomatableStrAufhErlms(AKUser user) {
        LOGGER.info("Starting automatic STR-AUF-ERLM processing");
        int processErrors = 0;
        int alreadyCancelled = 0;
        List<String> processedStrErlms = new ArrayList<>();

        final Collection<ErledigtmeldungStornoAuf> strErlms = wbciMeldungService
                .findAutomatableStrAufhErlmsForWitaProcessing();
        for (ErledigtmeldungStornoAuf erlm : strErlms) {
            String preagreementId = erlm.getWbciGeschaeftsfall().getVorabstimmungsId();

            try {
                WitaCBVorgang witaCBVorgang = wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(preagreementId);
                if (witaCBVorgang == null) {
                    // Durch diesen Check kann es passieren, dass auch dann ein AutomationError erzeugt wird, wenn alle
                    // zugehoerigen WITA Vorgaenge bereits abgeschlossen sind.
                    // Dies ist aber i.O., da die WBCI Prozessierung der Master ist und ein Storno immer zuerst ueber
                    // WBCI und erst im Anschluss ueber WITA erfolgen soll.
                    throw new WbciAutomationValidationException(String.format(NO_ACTIVE_WITA_INSTANCE, preagreementId));
                }

                if (witaCBVorgang.isStorno()) {
                    alreadyCancelled++;
                }
                else {
                    wbciWitaServiceFacade.doWitaStorno(erlm, witaCBVorgang.getId(), user);
                    processedStrErlms.add(preagreementId);
                }
            }
            catch (Exception e) {
                processErrors++;
                handleAutomationException(erlm, TaskName.WITA_SEND_STORNO, e, user);
            }
        }

        if (CollectionUtils.isNotEmpty(processedStrErlms)) {
            LOGGER.info(String.format("Automatically processed %s STR-AUF ERLMs", processedStrErlms.size()));
            LOGGER.info(String.format(
                    "%s STR-AUF ERLMs have not been processed, because WITA Storno is already cancelled.", alreadyCancelled));
            LOGGER.info(String.format("%s STR-AUF ERLMs have not been processed, because automation errors occured.",
                    processErrors));
        }
        else {
            LOGGER.info("No STR-AUF ERLMs found for auto processing");
        }

        return processedStrErlms;
    }

    /**
     * Determines if only one WITA-Leitung should be transfered to M-Net.
     */
    void checkOnylOneWitaVertNrInAkmTr(@NotNull UebernahmeRessourceMeldung akmTr) {
        if (Boolean.TRUE.equals(akmTr.isUebernahme())) {
            SortedSet<String> akmTrVtrNr = LeitungHelper.getWitaVertragsNrs(akmTr.getLeitungen());
            SortedSet<String> ruemVaVtrNr = new TreeSet<>();

            String vorabstimmungsId = akmTr.getWbciGeschaeftsfall().getVorabstimmungsId();
            // no vertragsnr assigend => check RUEM-VA
            if (akmTrVtrNr.isEmpty()) {
                RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId,
                        RueckmeldungVorabstimmung.class);
                if (ruemVa != null) {
                    ruemVaVtrNr = TechnischeRessourceHelper.getWitaVertragsNrs(ruemVa.getTechnischeRessourcen());
                }
                // RUEM-VA vertragsnummer size != 1 => exception
                if (ruemVaVtrNr.size() != 1) {
                    throw new WbciAutomationValidationException(String.format(WITA_VERTR_NR_SIZE_NOT_EQUAL_TO_ONE,
                            vorabstimmungsId,
                            MeldungTyp.RUEM_VA.getShortName(),
                            format(ruemVaVtrNr)));
                }
            }
            // more than one vertragsnummer assigend => exception
            else if (akmTrVtrNr.size() > 1) {
                throw new WbciAutomationValidationException(String.format(WITA_VERTR_NR_SIZE_NOT_EQUAL_TO_ONE,
                        vorabstimmungsId,
                        MeldungTyp.AKM_TR.getShortName(),
                        format(akmTrVtrNr)));
            }
        }
    }

    /**
     * Determines if only one Hurrican order is assigned to the {@link WbciGeschaeftsfall}.
     */
    void checkOnlyOneHurricanOrderAssigned(@NotNull UebernahmeRessourceMeldung akmTr) {
        WbciGeschaeftsfall wbciGeschaeftsfall = akmTr.getWbciGeschaeftsfall();
        if (wbciGeschaeftsfall.getBillingOrderNoOrig() == null) {
            throw new WbciAutomationValidationException(String.format(NO_TAIFUN_ORDER_ASSIGNED,
                    wbciGeschaeftsfall.getVorabstimmungsId()));
        }
        Set<Long> assignedHurricanOrderNos = wbciCommonService.getWbciRelevantHurricanOrderNos(
                wbciGeschaeftsfall.getBillingOrderNoOrig(), wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs());
        if (assignedHurricanOrderNos == null || assignedHurricanOrderNos.size() != 1) {
            throw new WbciAutomationValidationException(String.format(HURRICAN_ORDER_SIZE_NOT_EQUAL_TO_ONE,
                    wbciGeschaeftsfall.getVorabstimmungsId(),
                    formatLong(assignedHurricanOrderNos)));
        }
    }

    private String formatLong(Set<Long> longs) {
        Set<String> set = new HashSet<>();
        if (longs != null) {
            set.addAll(
                    longs.stream()
                            .map(Object::toString)
                            .collect(Collectors.toList())
            );
        }
        return format(set);
    }

    @Override
    public boolean canWitaOrderBeProcessedAutomatically(String vorabstimmungsId) {
        WbciGeschaeftsfall wbciGeschaeftsfall = wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
        if (!BooleanTools.nullToFalse(wbciGeschaeftsfall.getAutomatable())) {
            return false;
        }

        assertMnetIsReceivingCarrier(wbciGeschaeftsfall);

        if (!wbciGeschaeftsfall.getAutomatable() || wbciGeschaeftsfall.getKlaerfall()) {
            return false;
        }
        UebernahmeRessourceMeldung akmTr = wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId);
        if (Boolean.TRUE.equals(akmTr.isUebernahme())) {
            SortedSet<String> akmTrVtrNr = LeitungHelper.getWitaVertragsNrs(akmTr.getLeitungen());
            if (CollectionUtils.isNotEmpty(akmTrVtrNr)) {
                return hasOneElement(akmTrVtrNr);
            }
            else {
                RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
                SortedSet<String> ruemVaVtrNr = TechnischeRessourceHelper.getWitaVertragsNrs(ruemVa.getTechnischeRessourcen());
                return hasOneElement(ruemVaVtrNr);
            }
        }
        else {
            if (wbciGeschaeftsfall.getBillingOrderNoOrig() != null) {
                Set<Long> assignedHurricanOrderNos = wbciCommonService.getWbciRelevantHurricanOrderNos(
                        wbciGeschaeftsfall.getBillingOrderNoOrig(), wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs());
                return hasOneElement(assignedHurricanOrderNos) && canMnetTechnologyBeOrderedWithWita(wbciGeschaeftsfall);
            }
            return false;
        }
    }

    private boolean canMnetTechnologyBeOrderedWithWita(WbciGeschaeftsfall wbciGeschaeftsfall) {
        Technologie mnetTechnologie = wbciGeschaeftsfall.getMnetTechnologie();
        CarrierCode ekpPartner = wbciGeschaeftsfall.getEKPPartner();
        return mnetTechnologie.isRessourcenUebernahmePossible(IOType.IN, ekpPartner);
    }

    private boolean hasOneElement(Set<?> elements) {
        return CollectionUtils.isNotEmpty(elements) && elements.size() == 1;
    }

    /**
     * Helper ENUM for the different AKM-TR automation tasks
     */
    public enum AkmTrAutomationTask {
        NEUBESTELLUNG(TaskName.WITA_SEND_NEUBESTELLUNG, CBVorgang.TYP_NEU),
        ANBIETERWECHSEL(TaskName.WITA_SEND_ANBIETERWECHSEL, CBVorgang.TYP_ANBIETERWECHSEL),
        KUENDIGUNG(TaskName.WITA_SEND_KUENDIGUNG, CBVorgang.TYP_KUENDIGUNG);

        private final TaskName taskName;
        private final Long cbVorgangTyp;

        AkmTrAutomationTask(TaskName taskName, Long cbVorgangTyp) {
            this.taskName = taskName;
            this.cbVorgangTyp = cbVorgangTyp;
        }

        /**
         * @return {@link #ANBIETERWECHSEL} if ressourcenuebernahme == true, else {@link #NEUBESTELLUNG}.
         */
        public static AkmTrAutomationTask determineTask(Boolean ressourcenuebernahme) {
            if (Boolean.TRUE.equals(ressourcenuebernahme)) {
                return ANBIETERWECHSEL;
            }
            return NEUBESTELLUNG;
        }

        public TaskName getTaskName() {
            return taskName;
        }

        public Long getCbVorgangTyp() {
            return cbVorgangTyp;
        }

    }
}
