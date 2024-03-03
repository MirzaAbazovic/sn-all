/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.KuendigungCheck;
import de.augustakom.hurrican.model.cc.KuendigungFrist;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.helper.LeitungHelper;
import de.mnet.wbci.model.helper.TechnischeRessourceHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciKuendigungsService;
import de.mnet.wbci.service.WbciValidationService;

@CcTxRequired
@ObjectsAreNonnullByDefault
public class WbciKuendigungsServiceImpl implements WbciKuendigungsService {

    private static final Logger LOGGER = Logger.getLogger(WbciKuendigungsServiceImpl.class);

    /**
     * Pair mit Angabe der Leistungs-IDs fuer Surf&Fon 18: <br/>
     * <pre>
     *   First = alte Leistung
     *   Second = neue Leistung
     * </pre>
     */
    static final Pair<Long, Long> SURF_AND_FON_18 = Pair.create(72781L, 75320L);

    /**
     * Pair mit Angabe der Leistungs-IDs fuer Surf&Fon 50: <br/>
     * <pre>
     *   First = alte Leistung
     *   Second = neue Leistung
     * </pre>
     */
    static final Pair<Long, Long> SURF_AND_FON_50 = Pair.create(72782L, 75339L);

    /**
     * Pair mit Angabe der Leistungs-IDs fuer Surf&Fon 100: <br/>
     * <pre>
     *   First = alte Leistung
     *   Second = neue Leistung
     * </pre>
     */
    static final Pair<Long, Long> SURF_AND_FON_100 = Pair.create(72783L, 75340L);

    private static final List<Pair<Long, Long>> SURF_AND_FON_SERVICE_PAIRS =
            Arrays.asList(SURF_AND_FON_18, SURF_AND_FON_50, SURF_AND_FON_100);

    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.billing.RufnummerService")
    private RufnummerService rufnummerService;

    @Autowired
    private WbciDao wbciDao;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciValidationService wbciValidationService;

    @Override
    public KuendigungsCheckVO doKuendigungsCheck(Long auftragNoOrig, LocalDateTime cancellationIncome) {
        BAuftrag billingAuftrag = getCheckedBillingAuftrag(auftragNoOrig);

        KuendigungsCheckVO kuendigungsCheckVO = new KuendigungsCheckVO();
        kuendigungsCheckVO.setMindestVertragslaufzeitTaifun(Instant.ofEpochMilli(billingAuftrag.getVertragsendedatum().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        kuendigungsCheckVO.setUeberlassungsleistungen(getRelevantOrderPositions(auftragNoOrig));
        kuendigungsCheckVO.setMindestVertragslaufzeitCalculated(
                calculateMindestvertragslaufzeit(billingAuftrag, kuendigungsCheckVO.getUeberlassungsleistungen()));
        evaluateCancelCheckForOrder(billingAuftrag, cancellationIncome, kuendigungsCheckVO);

        return kuendigungsCheckVO;
    }

    @Override
    public @NotNull BAuftrag getCheckedBillingAuftrag(Long billingAuftragNo) {
        try {
            BAuftrag billingAuftrag = billingAuftragService.findAuftrag(billingAuftragNo);
            if (billingAuftrag == null) {
                throw new WbciServiceException(
                        String.format("Zur Auftragsnummer %s konnte kein Billing-Auftrag ermittelt werden",
                                billingAuftragNo)
                );
            }
            return billingAuftrag;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WbciServiceException(
                    "Bei der Ermittlung bzw. Berechnung des Kuendigungsdatums ist ein Fehler aufgetreten: "
                            + e.getMessage(), e
            );
        }
    }

    @Override
    public void evaluateCancelCheckForOrder(BAuftrag billingOrder, LocalDateTime cancellationIncome, KuendigungsCheckVO kuendigungsCheckVO) {
        try {
            if (billingOrder.getKuendigungsdatum() != null) {
                // Sonderfall: Auftrag ist bereits gekuendigt  -->  Kuendigungsdatum vom Auftrag verwenden und nicht berechnen!
                kuendigungsCheckVO.setCalculatedEarliestCancelDate(Instant.ofEpochMilli(billingOrder.getKuendigungsdatum().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
                kuendigungsCheckVO.setKuendigungsstatus(getKuendigungstatusForCanceledOrders(
                        DateConverterUtils.asLocalDateTime(billingOrder.getKuendigungsdatum()), cancellationIncome));
            }
            else {
                KuendigungCheck kuendigungCheck = wbciDao.findKuendigungCheckForOeNoOrig(billingOrder.getOeNoOrig());
                KuendigungFrist kuendigungFrist = (kuendigungCheck != null)
                        ? kuendigungCheck.getRelevantKuendigungFrist(billingOrder) : new KuendigungFrist();
                kuendigungsCheckVO.setKuendigungsfrist(kuendigungFrist);

                if (isPmxOrTk(billingOrder)) {
                    // Sonderfall: bei DN-Block > 100: Info-Anzeige, dass Kuendigungsdatum von Vertrieb erfragt wird
                    kuendigungsCheckVO.setKuendigungsstatus(KuendigungsCheckVO.Kuendigungsstatus.MANUELL_PMX_OR_TK);
                }

                // Sonderfall wenn Vertrieb eingeschalten werden muss
                if (kuendigungCheck == null || BooleanTools.nullToFalse(kuendigungCheck.getDurchVertrieb())) {
                    kuendigungsCheckVO.setKuendigungsstatus(KuendigungsCheckVO.Kuendigungsstatus.MANUELL_CONTACT_SALES);
                }
                else {
                    kuendigungsCheckVO.setVertragsverlaengerung(
                            String.format("%s Monate", kuendigungFrist != null ? kuendigungFrist.getAutoVerlaengerung() : ""));

                    calculateEarliestCancelDate(kuendigungsCheckVO, cancellationIncome, kuendigungCheck,
                            kuendigungFrist);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WbciServiceException("Fehler bei der Berechnung des Kuendigungsdatums: " + e.getMessage(), e);
        }
    }

    @Override
    public LocalDateTime getTaifunKuendigungstermin(Long billingOrderNo) {
        try {
            Date date = getCheckedBillingAuftrag(billingOrderNo).getKuendigungsdatum();
            return date != null ? Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        }
        catch (WbciServiceException e) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.systemDefault());
        }
    }

    @Override
    public SortedSet<String> getCancellableWitaVertragsnummern(String vorabstimmungsId) {
        SortedSet<String> cancellableVertragsNos = new TreeSet<>();
        UebernahmeRessourceMeldung akmTr = wbciCommonService.findLastForVaId(vorabstimmungsId, UebernahmeRessourceMeldung.class);
        if (akmTr != null) {
            boolean tvOrStornoActive = wbciValidationService.isTvOrStornoActive(vorabstimmungsId);
            if (!tvOrStornoActive) {
                RueckmeldungVorabstimmung ruemVA = wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
                cancellableVertragsNos = TechnischeRessourceHelper.getWitaVertragsNrs(ruemVA.getTechnischeRessourcen());

                if (Boolean.TRUE.equals(akmTr.isUebernahme())) {
                    if (akmTr.getLeitungen() == null || akmTr.getLeitungen().size() == 0) {
                        // Sonderfall: keine Leitungen in AKM-TR angegeben --> alle Leitungen aus RUEM-VA werden vom
                        // Partner-Carrier uebernommen; somit ist keine Kuendigung von WITA VtrNrs notwendig
                        cancellableVertragsNos.clear();
                    }
                    else {
                        // remove all Vertragsnrs, which will be ported
                        SortedSet<String> portableVertragsNos = LeitungHelper.getWitaVertragsNrs(akmTr.getLeitungen());
                        cancellableVertragsNos.removeAll(portableVertragsNos);
                    }
                }
            }
        }
        return cancellableVertragsNos;
    }

    /**
     * Determines the {@link KuendigungsCheckVO.Kuendigungsstatus} in the case the line(s) are already cancelled by the
     * customer.
     *
     * @param lineCancellationDate Date when the line has been cancelled
     * @param requestIncome        Date when the requested for an Anbieterwechsel or Rufnummernportierung comes in.
     * @return
     */
    protected KuendigungsCheckVO.Kuendigungsstatus getKuendigungstatusForCanceledOrders(LocalDateTime lineCancellationDate,
            LocalDateTime requestIncome) {

        int daysToLinesCancelation = getDifferenceInDays(LocalDate.now(),
                lineCancellationDate.toLocalDate(), DateCalculationHelper.DateCalculationMode.ALL);
        int daysBetweenLineCancelationAndRequestIncome = getDifferenceInDays(
                requestIncome.toLocalDate(), lineCancellationDate.toLocalDate(),
                DateCalculationHelper.DateCalculationMode.ALL);
        boolean lineActive = daysToLinesCancelation >= 0;

        // on active lines use workindays differenz
        if (lineActive) {
            daysBetweenLineCancelationAndRequestIncome = getDifferenceInDays(
                    requestIncome.toLocalDate(), lineCancellationDate.toLocalDate(),
                    DateCalculationHelper.DateCalculationMode.WORKINGDAYS);
        }

        // Anschluss gekuendigt ? noch nicht abgeschaltet (Vorlauffrist eingehalten)
        if (lineActive && daysBetweenLineCancelationAndRequestIncome <= ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_ERROR) {
            return KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_NICHT_OK;
        }
        // Anschluss gekuendigt ? noch nicht abgeschaltet (Vorlauffrist unterschritten fuer ABW (weniger 6 AT))
        else if (lineActive
                && daysBetweenLineCancelationAndRequestIncome <= ALLOWED_WORKING_DAYS_BEFORE_WECHSELTERMIN_WARNING) {
            return KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_WARNING;
        }
        // Anschluss gekuendigt ? noch nicht abgeschaltet (Vorlauffrist unterschritten fuer ABW (6-10 AT))
        else if (lineActive) {
            return KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_AKTIV_ABW_OK;
        }
        // Anschluss gekuendigt & abgeschaltet (innerhalb Karenzzeit 90 Tage)
        else if ((daysBetweenLineCancelationAndRequestIncome * -1) <= ALLOWED_DAYS_AFTER_CANCELLATION) {
            return KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_INAKTIV_PORTIERUNG_OK;
        }
        // Anschluss gekuendigt & abgeschaltet (ausserhalb Karenzzeit 90 Tage)
        return KuendigungsCheckVO.Kuendigungsstatus.GEKUENDIGT_INAKTIV_PORTIERUNG_NICHT_OK;
    }

    /**
     * Berechnet das fruehest moegliche Kuendigungsdatum fuer den Billing-Auftrag auf Basis des
     * Kuendigungs-Eingangsdatums sowie der angegebenen Kuendigungs-Konfiguration.
     *
     * @param kuendigungsCheckVO
     * @param cancellationIncome
     * @param kuendigungCheck
     * @param fristToUse
     */
    void calculateEarliestCancelDate(KuendigungsCheckVO kuendigungsCheckVO, LocalDateTime cancellationIncome,
            KuendigungCheck kuendigungCheck, KuendigungFrist fristToUse) {
        if (cancellationIncome == null) {
            cancellationIncome = LocalDateTime.now();
        }

        LocalDateTime earliestCancelDate;
        if (!BooleanTools.nullToFalse(fristToUse.getMitMvlz()) || !fristToUse.hasAutoVerlaengerung()) {
            earliestCancelDate = calculateEarliestCancelDateWithoutCancellationPeriod(cancellationIncome, fristToUse);
        }
        else {
            earliestCancelDate = calculateEarliestCancelDateWithCancellationPeriod(
                    cancellationIncome, kuendigungsCheckVO.getMindestVertragslaufzeitCalculated(), fristToUse);
        }

        if (earliestCancelDate != null) {
            while (!DateCalculationHelper.isWorkingDay(earliestCancelDate.toLocalDate())) {
                earliestCancelDate = earliestCancelDate.plusDays(1);
            }
        }

        kuendigungsCheckVO.setCalculatedEarliestCancelDate(earliestCancelDate);
    }


    LocalDateTime calculateEarliestCancelDateWithoutCancellationPeriod(LocalDateTime cancellationIncome, KuendigungFrist fristToUse) {
        LocalDateTime baseDate;
        switch (fristToUse.getFristAuf()) {
            case EINGANGSDATUM:
                // Basisdatum fuer Kuendigungsberechnung ist das Eingangsdatum der Kuendigung
                baseDate = cancellationIncome;
                break;
            case MONATSENDE:
                // Basisdatum fuer Kuendigungsberechnung ist der Monatsletzte vom Kuendigungseingangsdatum
                baseDate = cancellationIncome.with(TemporalAdjusters.lastDayOfMonth());
                break;
            default:
                throw new WbciServiceException(
                        String.format("Die angegebene KuendigungsFrist-Konfiguration (ID der Kuendigungsfrist: %s) ist nicht plausibel.",
                                fristToUse.getId())
                );
        }

        return changeDate(baseDate, true, fristToUse.getFristInWochen().intValue());
    }


    LocalDateTime calculateEarliestCancelDateWithCancellationPeriod(
            LocalDateTime cancellationIncome, LocalDateTime vertragsende, KuendigungFrist fristToUse) {
        if (vertragsende == null) {
            throw new WbciServiceException("Fuer den Auftrag konnte kein Vertragsende ermittelt / berechnet werden. " +
                    "Berechnung des fruehesten Kuendigungstermins ist deshalb nicht moeglich!");
        }

        LocalDateTime mvlz;
        LocalDateTime baseDate = cancellationIncome;  //.dayOfMonth().withMaximumValue(); // Monatsletzter vom Kuendigungseingangsdatum
        switch (fristToUse.getFristAuf()) {
            case ENDE_MVLZ:
                mvlz = fristToUse.calculateNextMvlz(cancellationIncome, vertragsende);
                break;
            default:
                throw new WbciServiceException(
                        String.format("Die angegebene KuendigungsFrist-Konfiguration (ID der Kuendigungsfrist: %s) ist nicht plausibel.",
                                fristToUse.getId())
                );
        }

        while (fristToUse.hasAutoVerlaengerung() && baseDate.isAfter(changeDate(mvlz, false, fristToUse.getFristInWochen().intValue()))) {
            mvlz = mvlz.plusMonths(fristToUse.getAutoVerlaengerung().intValue());
        }
        
        return mvlz;
    }


    /**
     * Ermittelt alle fuer die Berechnung eines Kuendigungsdatums 'relevanten' Auftragspositionen zu dem angegbenen
     * Taifun-Auftrag. <br/> Es werden dabei die Auftragspositionen ermittelt, die eine 'extProdNo' konfiguriert haben
     * und somit fuer das Hurrican-Produktmapping in Frage kommen. Abhaengig davon, ob die gefundenen Positionen mit
     * oder ohne Kuendigungsdatum sind, werden folgende Positionen zurueck gegeben:
     * <pre>
     *   - alle Positionen sind ohne Kuendigungsdatum
     *   -- >> es werden alle Positionen zurueck gegeben
     *   - Positionen haben teilweise ein Kuendigungsdatum, teilweise keines
     *   -- >> es werden nur die Positionen ohne Kuendigungsdatum zurueck gegeben
     *   - alle Positionen sind mit Kuendigungsdatum
     *   -- >> es werden alle Positionen zurueck gegeben, die das spaeteste Kuendigungsdatum besitzen
     * </pre>
     *
     * @param auftragNoOrig
     * @return
     */
    List<BAuftragLeistungView> getRelevantOrderPositions(Long auftragNoOrig) {
        try {
            List<BAuftragLeistungView> views = billingAuftragService.findProduktLeistungen4Auftrag(auftragNoOrig);
            List<BAuftragLeistungView> activePositions = Lists.newArrayList(Collections2.filter(views, BAuftragLeistungView.filterNotCancelled()));
            List<BAuftragLeistungView> cancelledPositions = Lists.newArrayList(Collections2.filter(views, BAuftragLeistungView.filterCancelled()));

            // Dirty-Hack fuer Surf&Fon Sonderfall durchfuehren...
            List<BAuftragLeistungView> surfAndFonDirtyResult = doDirtyHackSurfAndFonFilter(activePositions, cancelledPositions);
            if (CollectionUtils.isNotEmpty(surfAndFonDirtyResult)) {
                return surfAndFonDirtyResult;
            }

            if (!cancelledPositions.isEmpty() && activePositions.isEmpty()) {
                Date latestCancelDate = getLatestCancelDate(cancelledPositions);
                return Lists.newArrayList(Collections2.filter(cancelledPositions, BAuftragLeistungView.filterCancelled(latestCancelDate)));
            }
            else {
                return activePositions;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WbciServiceException("Fehler bei der Ermittlung der relevanten Auftragspositionen: " + e.getMessage(), e);
        }
    }


    /**
     * Super-dirty-hack fuer Surf&Fon Produkte bzw. spezielle Leistungen bei den Surf&Fon Produkten: <br/> Eine
     * bestimmte Zeit lang wurde auf den Auftraegen statt einer Standard-Ueberlassungsleistung + Gutschrift eine
     * Ueberlassungsleistung mit verringertem Preis eingebucht und per Skript nach 6 Monaten beendet und ab diesem
     * Zeitpunkt eine neue Auftragsposition mit einer anderen Leistungsnummer eingebucht. <br/> Fuer die Berechnung der
     * Mindestvertragslaufzeit ist jedoch die urspruengliche Leistung und deren Beginn-Datum relevant... <br/> <br/>
     * Damit dies in dem Kuendigungs-Check auch richtig dargestellt wird, werden die beiden Listen nach bestimmten
     * Leistungen durchsucht und geprueft, ob die neue Position genau 6 Monate nach dem Beginn der alten Position aktiv
     * wurde. Ist dies der Fall, dann ist nur die alte Position/Leistung fuer die Berechnung der MVLZ relevant. Falls
     * jedoch mehr als eine Position auf dem Auftrag beendet ist, wird keine Filterung vorgenommen und die
     * urspruengliche Logik ist relevant.
     *
     * @param activePositions
     * @param cancelledPositions
     * @return
     */
    private List<BAuftragLeistungView> doDirtyHackSurfAndFonFilter(@NotNull List<BAuftragLeistungView> activePositions,
            @NotNull List<BAuftragLeistungView> cancelledPositions) {
        if (CollectionUtils.isNotEmpty(cancelledPositions) && cancelledPositions.size() > 1) {
            return Collections.emptyList();
        }

        for (Pair<Long, Long> servicePair : SURF_AND_FON_SERVICE_PAIRS) {
            if (isSurfAndFonHackPresent(activePositions, cancelledPositions, servicePair)) {
                return cancelledPositions;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Fuehrt die eigentliche Pruefung durch, ob der Surf&Fon Hack fuer eine bestimmte Leistungs-Kombination vorhanden
     * ist.
     */
    boolean isSurfAndFonHackPresent(@NotNull List<BAuftragLeistungView> activePositions,
            @NotNull List<BAuftragLeistungView> cancelledPositions,
            @NotNull Pair<Long, Long> surfAndFonServices) {

        List<BAuftragLeistungView> cancelled = Lists.newArrayList(
                Collections2.filter(cancelledPositions, BAuftragLeistungView.filterByServiceNoOrig(surfAndFonServices.getFirst())));
        if (cancelled.size() == 1) {
            List<BAuftragLeistungView> active = Lists.newArrayList(
                    Collections2.filter(activePositions, BAuftragLeistungView.filterByServiceNoOrig(surfAndFonServices.getSecond())));

            if (active.size() == 1) {
                LocalDateTime cancelledValidFrom = Instant.ofEpochMilli(cancelled.get(0).getAuftragPosGueltigVon().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                boolean cancelledOnLastDayOfMonth = cancelledValidFrom.with(TemporalAdjusters.lastDayOfMonth()).equals(cancelledValidFrom);

                LocalDateTime activeValidFrom = Instant.ofEpochMilli(active.get(0).getAuftragPosGueltigVon().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                boolean activeOnLastDayOfMonth = activeValidFrom.with(TemporalAdjusters.lastDayOfMonth()).equals(activeValidFrom);

                int monthsBetween = (int) ChronoUnit.MONTHS.between(cancelledValidFrom.toLocalDate(), activeValidFrom.toLocalDate());
                if (((cancelledOnLastDayOfMonth && activeOnLastDayOfMonth) || (cancelledValidFrom.getDayOfMonth() == activeValidFrom.getDayOfMonth())) 
                        && monthsBetween == 6) {
                    return true;
                }
            }
        }

        return false;
    }


    private Date getLatestCancelDate(List<BAuftragLeistungView> views) {
        Date latestCancelDate = null;
        for (BAuftragLeistungView view : views) {
            if (view.getAuftragPosGueltigBis() != null
                    && DateTools.isDateBefore(view.getAuftragPosGueltigBis(), DateTools.getBillingEndDate())
                    && (latestCancelDate == null || DateTools.isDateAfter(view.getAuftragPosGueltigBis(), latestCancelDate))) {
                latestCancelDate = view.getAuftragPosGueltigBis();
            }
        }
        return latestCancelDate;
    }


    /**
     * Ueberprueft, ob es sich bei dem angegebenen Auftrag um einen TK bzw. PMX Auftrag handelt. Dies ist dann der Fall,
     * wenn der Auftrag min. eine Blockrufnummer mit Block-Groesse > 100 besitzt.
     *
     * @param billingOrder der zu ueberpruefende Auftrag
     * @return true wenn der Auftrag min. eine Rufnummer mit Block-Groesse > 100 besitzt
     */
    boolean isPmxOrTk(BAuftrag billingOrder) {
        try {
            List<Rufnummer> rufnummern = rufnummerService.findRNs4Auftrag(billingOrder.getAuftragNoOrig());

            if (CollectionTools.isNotEmpty(rufnummern)) {
                for (Rufnummer dn : rufnummern) {
                    if (dn.isBlock() && dn.getDnSize() != null && dn.getDnSize().intValue() > 100) {
                        return true;
                    }
                }
            }

            return false;
        }
        catch (FindException e) {
            LOGGER.error("Fehler bei der Ueberpruefung, ob es sich um einen TK/PMX Auftrag handelt!", e);
            return false;
        }
    }


    /**
     * Berechnet die Mindestvertragslaufzeit fuer den Auftrag. <br/> Dabei wird das neueste Datum der Positionen
     * ermittelt und dazu die MVLZ in Monaten des Billing-Auftrags addiert. <br/> Sollte auf dem Auftrag keine
     * Vertragslaufzeit angegeben sein, wird kein Datum berechnet!
     *
     * @param billingOrder der Billing-Auftrag mit ggf. hinterlegter Vertragslaufzeit
     * @param positions    die relevanten Auftragspositionen (Ueberlassungsleistungen)
     * @return berechnetes Datum fuer die Mindestvertragslaufzeit
     */
    @Nullable
    LocalDateTime calculateMindestvertragslaufzeit(BAuftrag billingOrder, List<BAuftragLeistungView> positions) {
        if (billingOrder.getVertragsLaufzeit() == null) {
            return null;
        }

        Date latest = null;
        for (BAuftragLeistungView view : positions) {
            if (latest == null || DateTools.isDateAfter(view.getAuftragPosGueltigVon(), latest)) {
                latest = view.getAuftragPosGueltigVon();
            }
        }

        return (latest != null)
                ? Instant.ofEpochMilli(latest.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().plusMonths(billingOrder.getVertragsLaufzeit().intValue()).minusDays(1)
                : null;
    }

    /**
     * Aendert das angegebene Datum
     *
     * @param base  zu aenderndes Datum
     * @param add   Flag, ob ein Wert addiert (true) oder subtrahiert (false) werden soll
     * @param weeks Anzahl der Wochen, die addiert/subtrahiert werden soll. Falls die Anzahl in Wochen durch 4 teilbar
     *              ist, erfolgt die Addition/Subtraktion in Monaten statt in Wochen!
     * @return das geaenderte Datum
     */
    protected LocalDateTime changeDate(LocalDateTime base, boolean add, int weeks) {
        boolean inMonth = (weeks % 4 == 0);

        LocalDateTime changed;
        if (inMonth) {
            int month = weeks / 4;
            if (add) {
                changed = base.plusMonths(month);
            }
            else {
                changed = base.minusMonths(month);
            }
        }
        else {
            if (add) {
                changed = base.plusWeeks(weeks);
            }
            else {
                changed = base.minusWeeks(weeks);
            }
        }
        return changed;
    }

}
