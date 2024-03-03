package de.mnet.hurrican.webservice.customerorder.services;

import static de.mnet.hurrican.webservice.customerorder.services.PublicOrderStatus.StatusValue.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Service-Implementierung von {@link OrderStatusService}.
 *
 */
@CcTxRequired
@Service
public class OrderStatusServiceImpl implements OrderStatusService {
    private static final Logger LOGGER = Logger.getLogger(OrderStatusServiceImpl.class);

    @Autowired
    private CCAuftragService ccAuftragService;
    @Autowired
    private WbciCommonService wbciCommonService;
    @Autowired
    private WbciDao wbciDao;
    @Autowired
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Autowired
    private BAService baService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private CPSService cpsService;

    private final Comparator<PublicOrderStatus> statusPrioComparator = (s1, s2) ->
            (int) (s1.getStatusValue().getStatusPrio() - s2.getStatusValue().getStatusPrio());

    // sortiert  WITA meldungen nach VersandZeitstempel
    private final Comparator<Meldung<?>> meldungsVersandZeitstempel =
            (d1, d2) -> d1.getVersandZeitstempel().compareTo(d2.getVersandZeitstempel());
    private final Comparator<AuftragsBestaetigungsMeldung> abmVerbindlicherLieferterminComparator =
            (d1, d2) -> d1.getVerbindlicherLiefertermin().compareTo(d2.getVerbindlicherLiefertermin());
    private final Comparator<MnetWitaRequest> witaRequestComparator =
            (r1, r2) -> r1.getMwfCreationDate().compareTo(r2.getMwfCreationDate());

    @Override
    public PublicOrderStatus getPublicOrderStatus(String customerOrderId) {
        final Long orderNoOrig = validateCustomerOrderId(customerOrderId);
        if(orderNoOrig == null) {
            // nothing to do with invalid customer order id
            return PublicOrderStatus.create(UNDEFINIERT);
        }

        final List<AuftragDaten> allCustomerAuftraege = getAuftragDaten4OrderNoOrig(orderNoOrig);
        if (isNoStatusPossible(allCustomerAuftraege)) {
            return PublicOrderStatus.create(UNDEFINIERT);
        }

        final List<PublicOrderStatus> allStatuses = new ArrayList<>();
        final Optional<AuftragDaten> singleActiveAuftrag = allCustomerAuftraege.stream()
                .filter(AuftragDaten::isAuftragActive)
                .findFirst();

        final List<Verlauf> allActVerlauf4Auftrag = getAllActVerlauf4Auftrag(singleActiveAuftrag);
        if (needToEvaluate(TERMIN_HINWEIS, allStatuses)) {
            final Optional<Date> realisierungsTermin = isTerminHinweis(allActVerlauf4Auftrag);
            if (realisierungsTermin.isPresent()) {
                allStatuses.add(PublicOrderStatus.create(TERMIN_HINWEIS)
                        .addDetail("Realisierungstermin", realisierungsTermin.get().toString()));
            }
        }

        if (needToEvaluate(IN_BETRIEB, allStatuses)
                || needToEvaluate(SCHALTTERMIN_NEGATIV, allStatuses)
                || needToEvaluate(SCHALTTERMIN_NEU, allStatuses)
                || needToEvaluate(LEITUNGSBESTELLUNG, allStatuses)
                || needToEvaluate(LEITUNGSBESTELLUNG_POSITIV, allStatuses)
                || needToEvaluate(LEITUNGSBESTELLUNG_NEGATIV, allStatuses)) {

            final List<PublicOrderStatus> tmpStatuses = retrieveLeitungsbestellungStatuses(singleActiveAuftrag, allActVerlauf4Auftrag);
            allStatuses.addAll(tmpStatuses);
        }

        if (needToEvaluate(ANBIETERWECHSEL, allStatuses)
                || needToEvaluate(ANBIETERWECHSEL_POSITIV, allStatuses)
                || needToEvaluate(ANBIETERWECHSEL_NEGATIV, allStatuses) ) {
            final Optional<PublicOrderStatus> anbiterWechselStatus = retrieveAnbieterwechselStatus(orderNoOrig);
            if (anbiterWechselStatus.isPresent()) {
                allStatuses.add(anbiterWechselStatus.get());
            }
        }
        if (needToEvaluate(START_BEARBEITUNG, allStatuses) && isStartBearbeitungStatus(allCustomerAuftraege)) {
            allStatuses.add(PublicOrderStatus.create(START_BEARBEITUNG));
        }

        return bestStatus(allStatuses);
    }

    protected PublicOrderStatus bestStatus(List<PublicOrderStatus> allStatuses) {
        return allStatuses.stream().max(statusPrioComparator).orElse(PublicOrderStatus.create(UNDEFINIERT));
    }

    protected boolean needToEvaluate(PublicOrderStatus.StatusValue statusToEvaluate, List<PublicOrderStatus> alreadyDefinedStatuses) {
        if (CollectionUtils.isNotEmpty(alreadyDefinedStatuses)) {
            final PublicOrderStatus bestStatus = bestStatus(alreadyDefinedStatuses);
            return statusPrioComparator.compare(PublicOrderStatus.create(statusToEvaluate), bestStatus) > 0;
        } else {
            return true; // still no statuses evaluated
        }

    }

    private Long validateCustomerOrderId(String customerOrderId) {
        try {
            if (StringUtils.isNotEmpty(customerOrderId)) {
                return Long.valueOf(customerOrderId);
            }
        }
        catch (NumberFormatException e) {
            // nothing here
        }
        return null;
    }

    private List<AuftragDaten> getAuftragDaten4OrderNoOrig(Long orderNoOrig) {
        try {
            final List<AuftragDaten> auftragDaten4OrderNoOrig = ccAuftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);
            return auftragDaten4OrderNoOrig != null ? auftragDaten4OrderNoOrig : Collections.EMPTY_LIST;
        }
        catch (FindException e) {
            LOGGER.error(String.format("Error by finding auftrag by customer order [%d]", orderNoOrig));
            return Collections.EMPTY_LIST;
        }
    }

    private boolean isNoStatusPossible(List<AuftragDaten> allCustomerAuftraege) {
        final long activeAuftragCount = allCustomerAuftraege.stream().filter(AuftragDaten::isAuftragActive).count();
        return activeAuftragCount != 1;  // not possible if several auftraege are found
    }

    /**
     * Hurrican 'sieht' den Taifun Auftrag
     */
    private boolean isStartBearbeitungStatus(List<AuftragDaten> allCustomerAuftraege) {
        final long activeAuftragCount = allCustomerAuftraege.stream().filter(AuftragDaten::isAuftragActive).count();
        return activeAuftragCount == 1;  // Hurrican 'sieht' den Taifun Auftrag
    }

    /**
     * common = mindestens eine noch offene (GF Status != COMPLETE) Vorabstimmung
     * positive = Existiert auf der aktuellen VA ein AKM-TR (IN oder OUT)?
     * negative = Existiert auf der aktuellen VA ein Storno Request (IN oder OUT) oder existiert eine ABBM (IN oder OUT)?
     */
    private Optional<PublicOrderStatus> retrieveAnbieterwechselStatus(Long orderNoOrig) {
        final Predicate<WbciRequestStatus> positivStati = p
                -> p == WbciRequestStatus.AKM_TR_EMPFANGEN
                || p == WbciRequestStatus.AKM_TR_VERSENDET;

        final Predicate<WbciRequestStatus> negativStati = p
                -> p == WbciRequestStatus.ABBM_EMPFANGEN
                || p == WbciRequestStatus.ABBM_VERSENDET
                || p == WbciRequestStatus.STORNO_EMPFANGEN
                || p == WbciRequestStatus.STORNO_VERSENDET;

        final List<WbciGeschaeftsfall> gfs = wbciCommonService.findAllGfByTaifunId(orderNoOrig).stream()
                .sorted((gf1, gf2) -> gf1.getId().compareTo(gf2.getId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(gfs)) {
            boolean areAllGfNegative = true;
            for (WbciGeschaeftsfall gf : gfs) {
                final List<WbciRequest> wbciRequests = wbciDao.findWbciRequestByType(gf.getVorabstimmungsId(), WbciRequest.class);
                final boolean isPositivStati = wbciRequests.stream().map(WbciRequest::getRequestStatus).anyMatch(positivStati);
                if (isPositivStati) {
                    // got it. any positive status is enough for us
                    final PublicOrderStatus positiv = PublicOrderStatus.create(ANBIETERWECHSEL_POSITIV);
                    final LocalDate wechseltermin = gf.getWechseltermin() != null ? gf.getWechseltermin() : gf.getKundenwunschtermin();
                    if (wechseltermin != null) {
                        positiv.addDetail("Wechseltermin", wechseltermin.toString());
                    }
                    return Optional.of(positiv);
                }
                final boolean isNegativStati = wbciRequests.stream().map(WbciRequest::getRequestStatus).anyMatch(negativStati);
                if (!isNegativStati) {
                    areAllGfNegative = false; // there is at lease one GF that is not negative
                }

            }
            if (areAllGfNegative) {
                // all GF are negative
                return Optional.of(PublicOrderStatus.create(ANBIETERWECHSEL_NEGATIV));
            } else {
                // there is at least one GS that is not negative,
                // but also we do not have any positive one = we do not know status yet, that is a common case
                return Optional.of(PublicOrderStatus.create(ANBIETERWECHSEL));
            }
        }

        return Optional.empty(); // does not found any active GF
    }

    /**
     * Existiert ein aktiver WITA Geschäftsfall
     *
     * Positive = Existiert auf dem aktuellen GF eine WITA ABM (IN) nach allen WITA ABBM
     * Negative = Existiert auf dem aktuellen GF eine WITA ABBM nach eventuellem ABM?
     * Schalttermin negative = Existiert auf dem aktuellen GF eine WITA TAM oder TV?
     */
    private List<PublicOrderStatus> retrieveLeitungsbestellungStatuses(Optional<AuftragDaten> singleActiveAuftrag, List<Verlauf> allActVerlauf4Auftrag) {
        final List<PublicOrderStatus> result = new ArrayList<>();
        if (singleActiveAuftrag.isPresent()) {

            final Comparator<WitaCBVorgang> byDateDesc = (cb1, cb2) -> cb2.getSubmittedAt().compareTo(cb1.getSubmittedAt()); // next > prev
            final Comparator<WitaCBVorgang> byDateThenIdDesc = byDateDesc.thenComparing((cb1, cb2) -> cb2.getId().compareTo(cb1.getId()));

            final Optional<WitaCBVorgang> newestFilteredCbVorgaeng = wbciWitaServiceFacade.findWitaCbVorgaengeByAuftrag(singleActiveAuftrag.get().getAuftragId())
                    .stream()
                    .filter(cb -> cb.isNeuschaltung() || cb.isAnbieterwechsel() || cb.isRexMk())
                    .sorted(byDateThenIdDesc)
                    .findFirst();

            if (newestFilteredCbVorgaeng.isPresent()) {
                final String externeAuftragsnummer = newestFilteredCbVorgaeng.get().getBusinessKey();
                final List<Meldung<?>> allMeldungen = mwfEntityDao.findAllMeldungen(externeAuftragsnummer);
                final List<MnetWitaRequest> allRequests = mwfEntityDao.findAllRequests(externeAuftragsnummer);

                // SCHALTTERMIN_NEGATIV
                final boolean hasBauftragRealisirungBeforeEqualToday = allActVerlauf4Auftrag.stream()
                        .filter(v -> !v.isStorno() && v.getRealisierungstermin() != null)
                        .map(Verlauf::getRealisierungstermin)
                        .filter(realisierungstermin -> DateConverterUtils.asLocalDate(realisierungstermin).isBefore(LocalDate.now().plusDays(1))) // <= heute
                        .findAny()
                        .isPresent();
                final Optional<Meldung<?>> newestTamErlmEntm = allMeldungen
                        .stream()
                        .filter(m -> m.getMeldungsTyp() == MeldungsType.TAM
                                || m.getMeldungsTyp() == MeldungsType.ENTM
                                || isErlmOrErlmPv(m.getMeldungsTyp())
                        )
                        .max(meldungsVersandZeitstempel);
                final Optional<MnetWitaRequest> newestTv = allRequests
                        .stream()
                        .filter(req -> req.getMeldungsTyp() == MeldungsType.TV)
                        .max(witaRequestComparator);

                // found latest TAM | TV instead of ERM | ENT
                final boolean isTamOrTv = newestTamErlmEntm
                        .map(Meldung::getMeldungsTyp)
                        .map(MeldungsType.TAM::equals)
                        .orElse(false) || newestTv.isPresent();
                if (hasBauftragRealisirungBeforeEqualToday && !isInBetrieb(singleActiveAuftrag) && isTamOrTv) {
                    result.add(PublicOrderStatus.create(SCHALTTERMIN_NEGATIV));
                }

                // LEITUNGSBESTELLUNG_POSITIV
                final Optional<Meldung<?>> newestAbmAbbm = allMeldungen
                        .stream()
                        .filter(m -> m.getMeldungsTyp() == MeldungsType.ABM
                                || m.getMeldungsTyp() == MeldungsType.ABBM)
                        .max(meldungsVersandZeitstempel);

                if (newestAbmAbbm.isPresent() && MeldungsType.ABM.equals(newestAbmAbbm.get().getMeldungsTyp())) {

                    final AuftragsBestaetigungsMeldung abbm = (AuftragsBestaetigungsMeldung) newestAbmAbbm.get();
                    final PublicOrderStatus positivStatus = PublicOrderStatus.create(LEITUNGSBESTELLUNG_POSITIV)
                            .addDetail("Verbindlicher Liefertermin", abbm.getVerbindlicherLiefertermin().toString());
                    result.add(positivStatus);

                }
                else if (newestAbmAbbm.isPresent() && MeldungsType.ABBM.equals(newestAbmAbbm.get().getMeldungsTyp())) {
                    // LEITUNGSBESTELLUNG_NEGATIV
                    result.add(PublicOrderStatus.create(LEITUNGSBESTELLUNG_NEGATIV));

                }
                else if (newestFilteredCbVorgaeng.get().getStatus() >= CBVorgang.STATUS_SUBMITTED &&
                        newestFilteredCbVorgaeng.get().getStatus() <= CBVorgang.STATUS_ANSWERED) {

                    // LEITUNGSBESTELLUNG
                    result.add(PublicOrderStatus.create(LEITUNGSBESTELLUNG));
                }

                // SCHALTTERMIN_NEU
                final List<MeldungsType> meldungsTypesTvAbmErlm = allMeldungen
                        .stream()
                        .map(m -> m.getMeldungsTyp())
                        .filter(typ -> MeldungsType.TV.equals(typ)
                                || MeldungsType.ABM.equals(typ)
                                || isErlmOrErlmPv(typ))
                        .collect(Collectors.toList());

                if (newestTv.isPresent()
                        && meldungsTypesTvAbmErlm.contains(MeldungsType.ABM)
                        && !meldungsTypesTvAbmErlm.contains(MeldungsType.ERLM)
                        && !meldungsTypesTvAbmErlm.contains(MeldungsType.ERLM_PV)
                        && isInBetrieb(singleActiveAuftrag) ) {

                    final Optional<AuftragsBestaetigungsMeldung> maxAbmLiefertermin = allMeldungen.stream()
                            .filter(m -> MeldungsType.ABM.equals(m.getMeldungsTyp()))  // ABM only
                            .map(m -> (AuftragsBestaetigungsMeldung) m)
                            .filter(abm -> abm.getVerbindlicherLiefertermin() != null)
                            .max(abmVerbindlicherLieferterminComparator);

                    final PublicOrderStatus schaltterminNeuStatus = PublicOrderStatus.create(SCHALTTERMIN_NEU);
                    if (maxAbmLiefertermin.isPresent()) {
                        schaltterminNeuStatus.addDetail("Realisierungstermin", maxAbmLiefertermin.get().getVerbindlicherLiefertermin().toString());
                    }
                    result.add(schaltterminNeuStatus);
                }

                // IN_BETRIEB
                final Optional<Meldung<?>> letzteMeldungErlmEntm = allMeldungen.stream()
                        .max(meldungsVersandZeitstempel)
                        .filter(m -> isErlmOrErlmPv(m.getMeldungsTyp())
                                || m.getMeldungsTyp() == MeldungsType.ENTM
                                || m.getMeldungsTyp() == MeldungsType.ENTM_PV
                        );
                if (isInBetrieb(singleActiveAuftrag) && letzteMeldungErlmEntm.isPresent()) {
                    result.add(PublicOrderStatus.create(IN_BETRIEB));
                }
            }
            else { // keine WITA-Bestellung
                // IN_BETRIEB
                if (isInBetrieb(singleActiveAuftrag)) {
                    result.add(PublicOrderStatus.create(IN_BETRIEB));
                }
            }
        }
        return result;
    }

    /**
     * Aktuellster nicht storniert BA mit Realisierungstermin
     */
    private Optional<Date> isTerminHinweis(List<Verlauf> allActVerlauf4Auftrag) {
        return allActVerlauf4Auftrag.stream()
                .filter(v -> !v.isStorno() && v.getRealisierungstermin() != null) // Verlauf mit einem Realisierungstermin und wurde NICHT storniert
                .map(Verlauf::getRealisierungstermin)
                .max(Date::compareTo);
    }

    private List<Verlauf> getAllActVerlauf4Auftrag(Optional<AuftragDaten> singleActiveAuftrag) {
        if (singleActiveAuftrag.isPresent()) {
            return getAllActVerlauf4Auftrag(singleActiveAuftrag.get());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private List<Verlauf> getAllActVerlauf4Auftrag(AuftragDaten auftragDaten) {
        try {
            final List<Verlauf> allActVerlauf4Auftrag = baService.findAllActVerlauf4Auftrag(auftragDaten.getAuftragId());
            return allActVerlauf4Auftrag != null ? allActVerlauf4Auftrag : ListUtils.EMPTY_LIST;
        }
        catch (FindException e) {
            LOGGER.error(String.format("Error by finding all active BA by auftrag Id [%d]", auftragDaten.getAuftragId()));
            return Collections.EMPTY_LIST;
        }
    }

    protected boolean isToday(Date date) {
        final LocalDate localDate = DateConverterUtils.asLocalDate(date);
        return LocalDate.now().isEqual(localDate);
    }

    protected boolean isInFuture(Date date) {
        if (date == null) {
            return false;
        }
        final LocalDate localDate = DateConverterUtils.asLocalDate(date);
        return localDate.isAfter(LocalDate.now());
    }

    private boolean isCreateSubscriberSucceded(Long auftrId) {
        final CCAuftragModel ccAuftragModel = new CCAuftragModel() {
            final Long auftragId = auftrId;
            @Override
            public void setAuftragId(Long auftragId) {
                throw new UnsupportedOperationException("Cannot change auftragId of this model!");
            }
            @Override
            public Long getAuftragId() {
                return auftragId;
            }
        };

        try {
            final List<CPSTransactionExt> cpsTransactionList = cpsService.findCPSTransactionsForTechOrder(ccAuftragModel);
            if (cpsTransactionList != null) {
                final Optional<CPSTransactionExt> createSubsOption = cpsTransactionList.stream()
                        .filter(trx -> trx.isCreateSubscriber())
                        .findAny();
                return createSubsOption.isPresent(); // has create subs in the history
            }
        }
        catch (FindException e) {
            LOGGER.error(String.format("Error by finding CPS transaction for tech order by auftrag Id [%d]", auftrId));
        }
        return false;
    }

    /**
     * Auftrag schon in Betrieb oder in Aenderung. In Aenderung heisst nicht, dass der Auftrag still gelegt ist.
     * Auftraege, welche in Kuendigung sind, bleiben genau genommen weiterhin in Betrieb, bis sie tatsaechlich
     * gekuendigt sind. Problematisch sind die Faelle, wo der alte Auftrag in Kuendigung und der neue in
     * Realisierung ist (Anschlussuebernahmen, Produktwechsel des selben Kunden). Hier interessiert potentiell
     * der neue Auftrag und sollte somit den in Kuendigung befindlichen 'schlagen'.
     */
    private boolean isInBetriebOrAenderung(Optional<AuftragDaten> singleActiveAuftrag) {
        if (singleActiveAuftrag.isPresent()) {
            return singleActiveAuftrag.get().isInBetriebOrAenderung();
        }
        return false;
    }

    private boolean isInBetrieb(Optional<AuftragDaten> singleActiveAuftrag) {
        if (singleActiveAuftrag.isPresent()) {
            return singleActiveAuftrag.get().isInBetrieb();
        }
        return false;
    }

    private boolean isErlmOrErlmPv(MeldungsType type) {
        return MeldungsType.ERLM.equals(type) || MeldungsType.ERLM_PV.equals(type);
    }

}
