/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.02.14
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.tools.lang.DateTools.*;
import static org.apache.commons.collections.CollectionUtils.*;

import java.time.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.VorabstimmungIdsByBillingOrderNoCriteria;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.helper.WbciRequestStatusHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOHelper;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.helper.LeitungHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaWbciServiceFacade;

/**
 *
 */
public class WitaWbciServiceFacadeImpl implements WitaWbciServiceFacade {

    @Autowired
    private WbciCommonService wbciCommonService;

    @Autowired
    private WbciDecisionService wbciDecisionService;

    @Autowired
    private WitaTalOrderService witaTalOrderService;

    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public WbciGeschaeftsfall getWbciGeschaeftsfall(String vorabstimmungsId) {
        return wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId);
    }

    protected WbciGeschaeftsfall getCheckedWbciGeschaeftsfall(String vorabstimmungsId) {
        WbciGeschaeftsfall wbciGeschaeftsfall = getWbciGeschaeftsfall(vorabstimmungsId);
        if (wbciGeschaeftsfall != null) {
            return wbciGeschaeftsfall;
        }
        throw new WbciServiceException(String.format(NO_VALID_VORABSTIMMUNG_FOUND, vorabstimmungsId));
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Map<WitaTaskVariables, Object> getAutomaticAnswerForAkmPv(AnkuendigungsMeldungPv akmPv) {
        Map<WitaTaskVariables, Object> akmPvVariables = new HashMap<>();
        final String vorabstimmungsId = akmPv.getVorabstimmungsId();
        final List<VorabstimmungsAnfrage> vorabstimmungsAnfrageList =
                wbciCommonService.findWbciRequestByType(vorabstimmungsId, VorabstimmungsAnfrage.class);
        if (CollectionUtils.isEmpty(vorabstimmungsAnfrageList)) {
            akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.SONSTIGES);
            akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTTEXT,
                    String.format(NO_VALID_VORABSTIMMUNG_FOUND, vorabstimmungsId));
        }
        else {
            final VorabstimmungsAnfrage vorabstimmungsAnfrage = vorabstimmungsAnfrageList.get(0);
            final LocalDate wechseltermin = vorabstimmungsAnfrage.getWbciGeschaeftsfall().getWechseltermin();
            final UebernahmeRessourceMeldung akmtr =
                    wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class);
            if (akmtr != null && akmtr.isUebernahme()
                    && WbciRequestStatus.AKM_TR_EMPFANGEN == vorabstimmungsAnfrage.getRequestStatus()) {
                akmPvVariables.putAll(checkWechseltermin(akmPv, wechseltermin));
            }
            else {
                akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.SONSTIGES);
                akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTTEXT, String.format(NO_AKM_TR, vorabstimmungsId));
            }
        }
        return akmPvVariables;
    }

    private Map<WitaTaskVariables, Object> checkWechseltermin(AnkuendigungsMeldungPv akmPv, LocalDate wechseltermin) {
        Map<WitaTaskVariables, Object> akmPvVariables = new HashMap<>();
        final AufnehmenderProvider aufnehmenderProvider = akmPv.getAufnehmenderProvider();
        if (wechseltermin.isEqual(aufnehmenderProvider.getUebernahmeDatumGeplant())) {
            akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.OK);
        }
        else {
            akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTCODE, RuemPvAntwortCode.TERMIN_UNGUELTIG);
            akmPvVariables.put(WitaTaskVariables.RUEM_PV_ANTWORTTEXT, AKM_PV_TERMIN_ABWEICHEND);
        }
        return akmPvVariables;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Set<String> findNonCompletedVorabstimmungen(WbciRequestStatus vaStatus, Long taifunOrderId,
            boolean considerActiveTVsOrStornos) {
        final WbciGeschaeftsfallStatus[] notCompletedGfStati = { WbciGeschaeftsfallStatus.ACTIVE,
                WbciGeschaeftsfallStatus.PASSIVE };

        final VorabstimmungIdsByBillingOrderNoCriteria vaCriteria = new VorabstimmungIdsByBillingOrderNoCriteria(
                taifunOrderId, VorabstimmungsAnfrage.class)
                .addMatchingGeschaeftsfallStatus(notCompletedGfStati);
        final VorabstimmungIdsByBillingOrderNoCriteria stornoCriteria = new VorabstimmungIdsByBillingOrderNoCriteria(
                taifunOrderId, StornoAnfrage.class)
                .addMatchingGeschaeftsfallStatus(notCompletedGfStati)
                .addMatchingRequestStatus(WbciRequestStatus.STORNO_EMPFANGEN, WbciRequestStatus.STORNO_VERSENDET,
                        WbciRequestStatus.STORNO_VORGEHALTEN);
        final VorabstimmungIdsByBillingOrderNoCriteria tvCriteria = new VorabstimmungIdsByBillingOrderNoCriteria(
                taifunOrderId, TerminverschiebungsAnfrage.class)
                .addMatchingGeschaeftsfallStatus(notCompletedGfStati)
                .addMatchingRequestStatus(WbciRequestStatus.TV_EMPFANGEN, WbciRequestStatus.TV_VERSENDET,
                        WbciRequestStatus.TV_VORGEHALTEN);

        Set<String> vorabstimmungIds = wbciCommonService.findVorabstimmungIdsByBillingOrderNoOrig(vaCriteria);
        if (considerActiveTVsOrStornos) {
            vorabstimmungIds.removeAll(wbciCommonService.findVorabstimmungIdsByBillingOrderNoOrig(stornoCriteria));
            vorabstimmungIds.removeAll(wbciCommonService.findVorabstimmungIdsByBillingOrderNoOrig(tvCriteria));
        }

        return vorabstimmungIds;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public LocalDateTime getWechselterminForVaId(String wbciVorabstimmungsId) {
        if (getCheckedWbciGeschaeftsfall(wbciVorabstimmungsId).getWechseltermin() == null) {
            throw new WbciServiceException(String.format(NO_WECHSELTERMIN, wbciVorabstimmungsId));
        }
        return getCheckedWbciGeschaeftsfall(wbciVorabstimmungsId).getWechseltermin().atStartOfDay();
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Collection<RufnummerPortierungSelection> updateRufnummernSelectionForVaId(
            Collection<RufnummerPortierungSelection> rufnummerPortierungSelections, String wbciVorabstimmungsId) {

        List<Rufnummer> rufnummern = new ArrayList<>();
        for (RufnummerPortierungSelection rnr : rufnummerPortierungSelections) {
            rufnummern.add(rnr.getRufnummer());
        }

        // Update and disable the Rufnummern-Selection
        return DecisionVOHelper
                .updateRufnummerPortierungSelection(rufnummerPortierungSelections,
                        wbciDecisionService.evaluateRufnummernDecisionData(
                                getRuemVaPortierung(wbciVorabstimmungsId),
                                rufnummern,
                                false),
                        wbciVorabstimmungsId
                );
    }

    /**
     * @return the Rufnummerportierung of the RUEM-VA or throws an {@link WbciServiceException}.
     */
    protected Rufnummernportierung getRuemVaPortierung(String wbciVaId) {
        RueckmeldungVorabstimmung ruemVa = wbciCommonService.findLastForVaId(wbciVaId,
                RueckmeldungVorabstimmung.class);
        if (ruemVa != null && ruemVa.getRufnummernportierung() != null) {
            return ruemVa.getRufnummernportierung();
        }
        throw new WbciServiceException(String.format(NO_RUEM_VA, wbciVaId));
    }

    @Override
    public void checkVorabstimmungValidForWitaVorgang(GeschaeftsfallTyp witaGfTyp, String wbciVaId)
            throws WbciValidationException {
        WbciGeschaeftsfall wbciGeschaeftsfall = getCheckedWbciGeschaeftsfall(wbciVaId);
        if (wbciGeschaeftsfall.getBillingOrderNoOrig() == null) {
            throw new WbciValidationException(String.format(NO_TAIFUN_NO, wbciVaId));
        }
        Set<String> result = findNonCompletedVorabstimmungen(
                WbciRequestStatusHelper.getActiveWbciRequestStatus(witaGfTyp),
                wbciGeschaeftsfall.getBillingOrderNoOrig(),
                true);
        if (result == null || !result.contains(wbciVaId)) {
            throw new WbciValidationException(String.format(ACTIVE_TV_OR_STORNO, wbciVaId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkAndReturnNextWitaVertragsnummern(GeschaeftsfallTyp witaGfTyp, String wbciVorabstimmungsId,
            Long cbVogangId, Long auftragsKlammerId)
            throws WbciValidationException {

        if (!isGeschaeftsfallVBLorPV(witaGfTyp)) {
            // keine VBL oder PV, d.h. keine Vertragsnummer aus der WBCI Vorabstimmung notwendig
            return null;
        }
        else {
            SortedSet<String> witaVertragsNr = getSortedWitaVertragsNr(witaGfTyp, wbciVorabstimmungsId);
            SortedSet<Long> witaCbVorgangIds = getSortedWitaCbIDs(cbVogangId, auftragsKlammerId);

            if (witaVertragsNr.size() == witaCbVorgangIds.size()) {
                if (isWita7() && isDTAGAbgebenderProvider(wbciVorabstimmungsId)) {
                    // Sonderfall WITA 7: falls abgebender Provider die DTAG, darf die Vertragsnummer nicht uebermittelt werden!
                    // (siehe hierzu auch WITA AuftragsMeldestruktur_Order-SST-V700.xls (Anmerkung und Kommentarfunktion zu
                    // 'vertragsnummer' und 'bestandssuche')
                    return null;
                }
                else {
                    // Ab der WITA 10 entfaellt die Bestandssuche bei VBL und PV.
                    // D.h. wir muessen jetzt immer die Vertragsnummer aus der Vorabstimmung schicken
                    //Normalfall: VertragsNr wird anhand einer sortierten map zurückgeliefert!
                    return getMappedVertragsNr(witaVertragsNr.iterator(), witaCbVorgangIds.iterator()).get(cbVogangId);
                }
            }
            else {
                throw new WbciValidationException(String.format(COUNT_OF_LEITUNGEN_IN_AKMTR, wbciVorabstimmungsId,
                        witaVertragsNr.size(), witaCbVorgangIds.size()));
            }
        }
    }

    private boolean isWita7() {
        return witaConfigService.getDefaultWitaVersion().equals(WitaCdmVersion.V1);
    }


    private boolean isDTAGAbgebenderProvider(String wbciVorabstimmungsId) {
        return CarrierCode.DTAG.equals(getCheckedWbciGeschaeftsfall(wbciVorabstimmungsId).getAbgebenderEKP());
    }


    private boolean isGeschaeftsfallVBLorPV(GeschaeftsfallTyp witaGfTyp) {
        return GeschaeftsfallTyp.VERBUNDLEISTUNG.equals(witaGfTyp) || GeschaeftsfallTyp.PROVIDERWECHSEL.equals(witaGfTyp);
    }

    private Map<Long, String> getMappedVertragsNr(Iterator<String> witaVertragsNrIterator,
            Iterator<Long> cbVorgangIDsIterator) {
        Map<Long, String> mappedVertragsNr = new HashMap<>();
        while (cbVorgangIDsIterator.hasNext() && witaVertragsNrIterator.hasNext()) {
            mappedVertragsNr.put(cbVorgangIDsIterator.next(), witaVertragsNrIterator.next());
        }
        return mappedVertragsNr;
    }

    protected SortedSet<Long> getSortedWitaCbIDs(Long cbVogangId, Long auftragsKlammerId) {
        if (auftragsKlammerId != null) {
            return witaTalOrderService.findWitaCBVorgaengIDs4Klammer(auftragsKlammerId);
        }
        return new TreeSet<>(Arrays.asList(cbVogangId));
    }

    protected SortedSet<String> getSortedWitaVertragsNr(GeschaeftsfallTyp witaGfTyp, String wbciVorabstimmungsId)
            throws WbciValidationException {
        UebernahmeRessourceMeldung akmTr = getCheckedAkmTr(wbciVorabstimmungsId);
        if (Boolean.TRUE.equals(akmTr.isUebernahme())) {
            Set<Leitung> leitungen = akmTr.getLeitungen();
            if (CollectionUtils.isEmpty(leitungen)) {
                throw new WbciValidationException(String.format(NO_LEITUNGEN_IN_AKMTR, wbciVorabstimmungsId));
            }
            return LeitungHelper.getWitaVertragsNrs(leitungen);
        }
        throw new WbciValidationException(String.format(UEBERNAHME_RESSOURCE_REQUIRED, wbciVorabstimmungsId,
                witaGfTyp.getDisplayName()));
    }

    @Override
    public void checkDateForMatchingWithVorabstimmung(Date date, String wbciVaId) throws WbciValidationException {
        LocalDate wechselterminVa = getWechselterminForVaId(wbciVaId).toLocalDate();
        if (wechselterminVa.compareTo(DateConverterUtils.asLocalDate(date)) != 0) {
            throw new WbciValidationException(
                    String.format(DATE_IS_NOT_MATCHING,
                            formatDate(date, PATTERN_DAY_MONTH_YEAR),
                            formatDate(Date.from(wechselterminVa.atStartOfDay(ZoneId.systemDefault()).toInstant()), PATTERN_DAY_MONTH_YEAR),
                            wbciVaId)
            );
        }
    }

    @Override
    public void checkDateIsEqualOrAfterWbciVa(Date date, String wbciVaId) throws WbciValidationException {
        LocalDate wechselterminVa = getWechselterminForVaId(wbciVaId).toLocalDate();
        if (wechselterminVa.compareTo(DateConverterUtils.asLocalDate(date)) > 0) {
            throw new WbciValidationException(
                    String.format(DATE_IS_NOT_EQUAL_OR_AFTER,
                            formatDate(date, PATTERN_DAY_MONTH_YEAR),
                            formatDate(Date.from(wechselterminVa.atStartOfDay(ZoneId.systemDefault()).toInstant()), PATTERN_DAY_MONTH_YEAR),
                            wbciVaId)
            );
        }
    }

    @Override
    public void checkRufnummernForMatchingWithVorabstimmung(Collection<Rufnummer> rufnummern, String wbciVaId) {
        if (isNotEmpty(rufnummern)) {
            List<DecisionVO> decisionVOs = wbciDecisionService.evaluateRufnummernDecisionData(
                    getRuemVaPortierung(wbciVaId),
                    new ArrayList<>(rufnummern),
                    false);

            for (DecisionVO decision : decisionVOs) {
                if (!DecisionResult.OK.equals(decision.getFinalResult())
                        || !MeldungsCode.ZWA.equals(decision.getFinalMeldungsCode())) {
                    throw new WbciValidationException(String.format(RUFNUMMERN_NOT_MATCH, decision.getControlValue()));
                }
            }
        }
    }

    @Override
    public UebernahmeRessourceMeldung getLastAkmTr(String wbciVorabstimmungsId) {
        return wbciCommonService.findLastForVaId(wbciVorabstimmungsId, UebernahmeRessourceMeldung.class);
    }

    @Override
    public RueckmeldungVorabstimmung getRuemVa(String wbciVorabstimmungsId) {
        return wbciCommonService.findLastForVaId(wbciVorabstimmungsId, RueckmeldungVorabstimmung.class);
    }

    protected UebernahmeRessourceMeldung getCheckedAkmTr(String wbciVaId) {
        UebernahmeRessourceMeldung akmTr = getLastAkmTr(wbciVaId);
        if (akmTr == null) {
            throw new WbciServiceException(String.format(NO_AKM_TR, wbciVaId));
        }
        return akmTr;
    }
}
