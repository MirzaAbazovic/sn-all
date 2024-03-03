/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2012 11:49:51
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxSupports;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusServiceHelper;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;
import de.mnet.common.tools.DateConverterUtils;


/**
 * implements @{code CCAuftragStatusService}
 */
@CcTxRequired
public class CCAuftragStatusServiceImpl extends DefaultCCService implements CCAuftragStatusService {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragStatusServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Resource(name = "auftragDatenDAO")
    private AuftragDatenDAO auftragDatenDAO;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCRufnummernService")
    private CCRufnummernService rufnummernService;

    @Resource(name = "de.augustakom.hurrican.service.cc.InnenauftragService")
    private InnenauftragService innenauftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragStatusServiceHelper")
    private CCAuftragStatusServiceHelper statusServiceHelper;

    @Inject
    private CPSService cpsService;

    /*
     *  Implementierung analog zur bisherigen Implementierung in der GUI: Die Auftrag-Kuendigung wird in eine neue
     *  Transaction ausgefuehrt, damit die Kuendigung auch bei einem Fehler im vorgelagerten Schritt ausgefuehrt wird.
     */
    @Override
    @Nonnull
    public AKWarnings kuendigeAuftragUndPhysik(
            @Nonnull final Long auftragId, @Nonnull final Date kuendigungsDatum, @Nonnull Long sessionId) {

        try{
            final AKWarnings warnings = new AKWarnings();
            try {
                List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
                if (endstellen != null) {
                    for (Endstelle es : endstellen) {
                        if (es.getRangierId() != null) {
                            kuendigeCarrierBestellung(auftragId, warnings, es);
                            rangierungsService.rangierungFreigabebereit(es, kuendigungsDatum);
                        }
                    }
                }
                checkVpn(auftragId, warnings);
                return warnings;
            }
            catch (StoreException | FindException e) {
                throw new RuntimeException(e);
            }
            finally {
                    statusServiceHelper.kuendigeAuftragReqNew(auftragId, kuendigungsDatum, getAKUserBySessionIdSilent(sessionId));
            }
        }
        catch (StoreException e) {
            throw new RuntimeException(e); // sonar mag kein throw statement im finally block
        }
    }

    void kuendigeCarrierBestellung(Long auftragId, AKWarnings warnings, Endstelle es) throws FindException {
        List<Carrierbestellung> cbs = carrierService.findCBs4Endstelle(es.getId());
        if ((cbs == null) || (cbs.isEmpty())) {
            // Warnung nur dann, wenn HVT-Standort Carrier != M-net   (--> Carrier#isMNetCarrier)
            Carrier carrier = carrierService.findCarrier4HVT(es.getHvtIdStandort());
            if (carrier == null || !Carrier.isMNetCarrier(carrier.getId())) {
                final String msg = "Bitte erfassen Sie die CuDa-Kündigung für den Hurrican-Auftrag %d selbst." +
                        "Zu der Endstelle mit der ID %d konnte nämlich keine CuDa-Bestellungen gefunden werden.";
                warnings.addAKWarningNotNull(null, String.format(msg, auftragId, es.getId()));
            }
        }
        else if (cbs.size() > 1) {
            // Info, dass CuDa-Kuendigung selber erfasst werden muss.
            final String msg = "Bitte erfassen Sie die CuDa-Kündigung für den Hurrican-Auftrag %d selbst." +
                    "Zu der Endstelle mit der ID %d wurden mehrere CuDa-Bestellungen gefunden.";
            warnings.addAKWarningNotNull(null, String.format(msg, auftragId, es.getId()));
        }
        else {
            try {
                carrierService.cbKuendigenNewTx(cbs.get(0).getId());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                final String msg =
                        "Die CuDA konnte nicht gekündigt " +
                                "werden! Bitte erfassen Sie die CuDA-Kündigung selbst.\n\nGrund: "
                                + e.getMessage();
                warnings.addAKWarning(null, msg);
            }
        }
    }


    @Override
    public @Nonnull AKWarnings cancelHurricanOrdersAndCreateBA(Long orderNoOrig, @Nonnull Date kuendigungsDatum,
            @Nonnull AKUser user, @Nonnull Long sessionId) {
        AKWarnings overallWarnings = new AKWarnings();

        try {
            List<AuftragDaten> auftragDatenList = ccAuftragService.findAuftragDaten4OrderNoOrig(orderNoOrig);

            for (AuftragDaten auftragDaten : auftragDatenList) {
                if (auftragDaten.isInBetriebOrAenderung()) {
                    AKWarnings warnings = kuendigeAuftragUndPhysik(auftragDaten.getAuftragId(), kuendigungsDatum, sessionId);
                    if (warnings.isNotEmpty()) {
                        overallWarnings.addAKWarnings(warnings);
                    }

                    Pair<Verlauf, AKWarnings> baResult = baService.createVerlauf(new CreateVerlaufParameter(
                            auftragDaten.getAuftragId(),
                            kuendigungsDatum,
                            BAVerlaufAnlass.KUENDIGUNG,
                            null,
                            false,
                            sessionId,
                            null));

                    if (baResult.getFirst() == null) {
                        overallWarnings.addAKWarning(this,
                                String.format("Es wurde kein Kündigungs-Bauauftrag für techn. Auftrag %s erstellt!",
                                        auftragDaten.getAuftragId()));
                    }
                }
                else if (auftragDaten.isInRealisierung()) {
                    overallWarnings.addAKWarning(this,
                            String.format("Auftrag %s ist noch nicht in Betrieb; Kuendigung nicht moeglich!",
                                    auftragDaten.getAuftragId()));
                }
                else if (auftragDaten.isInKuendigung()) {
                    AKWarnings warnings = modifyCancelDate(auftragDaten, kuendigungsDatum, user);
                    if (warnings.isNotEmpty()) {
                        overallWarnings.addAKWarnings(warnings);
                    }
                } else {
                    overallWarnings.addAKWarning(this,
                            String.format("Auftrag %s ist in einem unerwarteten Bearbeitungszustand; Kuendigung nicht moeglich!",
                                    auftragDaten.getAuftragId()));
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return overallWarnings;
    }

    @Nonnull
    AKWarnings modifyCancelDate(AuftragDaten auftragDaten, Date cancelDate, AKUser user) {
        AKWarnings warnings = new AKWarnings();
        try {
            if (!DateTools.isDateEqual(auftragDaten.getKuendigung(), cancelDate)) {
                auftragDaten.setKuendigung(cancelDate);
                auftragDatenDAO.store(auftragDaten);
            }

            // BA pruefen und ggf. verschieben
            Verlauf verlauf = baService.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
            if (verlauf != null && verlauf.isKuendigung()
                    && !DateTools.isDateEqual(verlauf.getRealisierungstermin(), cancelDate)) {

                if (DateTools.isDateBeforeOrEqual(verlauf.getRealisierungstermin(), DateConverterUtils.asDate(LocalDateTime.now().plusDays(1)))) {
                    warnings.addAKWarning(this,
                            String.format("Bauauftrags-Termin zu Auftrag %s kann nicht angepasst werden, da das " +
                                    "bisherige Datum zu nahe liegt!", auftragDaten.getAuftragId()));
                }
                else {
                    // pruefen, ob eine Technik-Abteilung den BA schon 'angefasst' hat
                    // falls ja --> Datum nicht anpassen und Warning schreiben!
                    List<VerlaufAbteilung> verlaufAbteilungen = baService.findVerlaufAbteilungen(verlauf.getId());
                    Collection<VerlaufAbteilung> techAbteilungen =
                            CollectionUtils.select(verlaufAbteilungen, new VerlaufAbteilungTechPredicate());

                    boolean verlaufInBearbeitung = false;
                    for (VerlaufAbteilung va : techAbteilungen) {
                        if (!va.getVerlaufStatusId().equals(VerlaufStatus.STATUS_IM_UMLAUF)) {
                            verlaufInBearbeitung = true;
                            break;
                        }
                    }

                    if (verlaufInBearbeitung) {
                        warnings.addAKWarning(this,
                                String.format("Das Kuendigungsdatum des Auftrags %s kann nicht angepasst werden, da der " +
                                        "Bauauftrag von der Technik bereits in Bearbeitung ist.", auftragDaten.getAuftragId()));
                    }
                    else {
                        baService.changeRealDate(verlauf.getId(), cancelDate, user);
                    }
                }
            }
        }
        catch (Exception e) {
            warnings.addAKWarning(this,
                    String.format("Fehler bei der Anpassung des Kuendigungsdatums fuer Auftrag %s: %s",
                            auftragDaten.getAuftragId(), ExceptionUtils.getFullStackTrace(e)));
        }

        return warnings;
    }


    @Override
    @Nonnull
    @CcTxSupports
    public AKWarnings checkVpn(Long auftragId, @Nonnull AKWarnings warnings) {
        try {
            final VPNService vpns = getCCService(VPNService.class);
            final VPNKonfiguration vpnKonf = vpns.findVPNKonfiguration4Auftrag(auftragId);
            if (vpnKonf != null) {
                warnings.addAKWarning(this, "Der Auftrag besitzt eine VPN-Konfiguration. " +
                        "Bitte ordnen Sie die phys. Leitung der VPN-Konfiguration einem anderen Auftrag zu oder entfernen Sie sie zum Kündigungstermin.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return warnings;
    }

    @Override
    public void kuendigeAuftrag(Long auftragId, Date kuendigungsDatum, Long sessionId) throws StoreException {
        statusServiceHelper.kuendigeAuftragReq(auftragId, kuendigungsDatum, getAKUserBySessionIdSilent(sessionId));
    }

    @Override
    public void checkAuftragAbsagen(Long auftragId) throws FindException {
        if (auftragId == null) {
            throw new IllegalArgumentException(
                    "Es wurde keine Auftrags-ID angegeben! Auftrag kann nicht auf Absage gesetzt werden.");
        }

        // Auftragsstatus pruefen
        AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (auftragDaten.getStatusId() > AuftragStatus.ABSAGE) {
            throw new IllegalStateException(
                    "Der Auftrag ist bereits in Realisierung oder in Betrieb! Absage nicht mehr möglich");
        }

        if (hatNichtgekuendigteCarrierbestellung(auftragId)) {
            throw new IllegalStateException(
                    "Bitte TAL Kündigung veranlassen und das Feld \"Kündigung am:\" der Carrierbestellung befüllen.");
        }

        if (innenauftragService.hasOpenBudget(auftragId)) {
            throw new IllegalStateException(
                    "Der Auftrag besitzt noch ein offenes Budget. Budget muss erst geschlossen werden.");
        }
    }

    private boolean hatNichtgekuendigteCarrierbestellung(final Long auftragId) throws FindException {
        boolean result = false;
        final Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if (esB != null) {
            final Carrierbestellung cb = carrierService.findLastCB4Endstelle(esB.getId());
            if (cb != null) {
                result = cb.getKuendBestaetigungCarrier() == null;
            }
        }
        return result;
    }

    @Override
    public void performAuftragAbsagen(Long auftragId, Long sessionId) throws StoreException, FindException {
        checkAuftragAbsagen(auftragId);
        try {
            AKUser user = getAKUserBySessionIdSilent(sessionId);

            final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
            if (auftragDaten.isAuftragAborted()) {
                throw new StoreException(String.format("Der techn. Auftrag mit Id %s ist bereits abgesagt",
                        auftragDaten.getAuftragId()));
            }

            List<Verlauf> verlaeufe = baService.findVerlaeufe4Auftrag(auftragId);
            if (verlaeufe != null) {
                for (Verlauf v : verlaeufe) {
                    if ((v.getAkt() != null) && v.getAkt()) {
                        if (v.hasSubOrders()) {
                            // Bei Verlaufbündeln den Verlauf für die 'auftragId' extrahieren
                            Set<Long> toSplit = new HashSet<>();
                            toSplit.add(auftragId);
                            List<Verlauf> splitVerlaeufe = baService.splitVerlauf(v, toSplit, sessionId);
                            if (CollectionTools.isEmpty(splitVerlaeufe)) {
                                throw new StoreException("Splitten der Verlaufbündelung ist fehlgeschlagen!");
                            }
                            if (!NumberTools.equal(v.getAuftragId(), auftragId)) {
                                // Die 'auftragId' ist ein Unterauftrag, Verlauf setzen
                                v = splitVerlaeufe.get(0);
                            }
                        }
                        v.setAkt(Boolean.FALSE);
                        v.setAnlass(BAVerlaufAnlass.ABSAGE);
                        baService.saveVerlauf(v);
                    }
                }
            }

            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragId);
            if ((endstellen != null) && (!endstellen.isEmpty())) {
                for (Endstelle e : endstellen) {
                    rangierungsService.rangierungFreigabebereit(e, new Date());
                }
            }

            if (auftragDaten.getAuftragNoOrig() != null) {
                // Rufnummernleistungen auf 'gekuendigt' setzen
                List<Leistung2DN> dnLeistungen = rufnummernService.findDNLeistungen4Auftrag(auftragId);
                if (dnLeistungen != null) {
                    for (Leistung2DN l2dn : dnLeistungen) {
                        if (l2dn.getScvKuendigung() == null) {
                            l2dn.setScvKuendigung(l2dn.getScvRealisierung());
                            l2dn.setScvUserKuendigung((user != null) ? user.getName() : "unbekannt");
                            rufnummernService.saveLeistung2DN(l2dn);
                        }
                    }
                }

            }

            sendAbsageToCps(sessionId, determineCpsTxInfosForAuftragAbsage(auftragId));

            // Auftrag-Status auf Absage setzen
            auftragDaten.setStatusId(AuftragStatus.ABSAGE);
            auftragDatenDAO.store(auftragDaten);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            String id = auftragId.toString();
            throw new StoreException(StoreException.UNABLE_TO_CANCEL_CC_AUFTRAG, new Object[] { id }, e);
        }
    }

    @Override
    public CpsTxInfosForAuftragAbsage determineCpsTxInfosForAuftragAbsage(Long auftragId) throws FindException {
        final List<CPSTransaction> transactions = getSortedCreateSubCpsTransactions(auftragId);
        final boolean hasCreateSubTx = !transactions.isEmpty();

        if (!hasCreateSubTx) {
            return new CpsTxInfosForAuftragAbsage(CpsTxInfosForAuftragAbsage.CpsTxType.NONE, new Date(), null);
        }

        final AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);
        final List<AuftragDaten> auftragDatenList =
                ccAuftragService.findAuftragDaten4OrderNoOrigTx(auftragDaten.getAuftragNoOrig());
        final boolean multipleActiveOrders = auftragDatenList.size() > 1;

        final Date execDate = getExecDateForAuftragAbsageCpsTx(transactions.get(0));
        if (multipleActiveOrders) {
            return new CpsTxInfosForAuftragAbsage(CpsTxInfosForAuftragAbsage.CpsTxType.MODIFY_SUB, execDate, transactions.get(0).getAuftragId());
        }
        else {
            return new CpsTxInfosForAuftragAbsage(CpsTxInfosForAuftragAbsage.CpsTxType.CANCEL_SUB, execDate, transactions.get(0).getAuftragId());
        }
    }

    private List<CPSTransaction> getSortedCreateSubCpsTransactions(Long auftragId) throws FindException {
        final List<CPSTransaction> transactions = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragId);
        CollectionUtils.filter(transactions, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                CPSTransaction tx = (CPSTransaction) o;
                return CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB.equals(tx.getServiceOrderType());
            }
        });
        Collections.sort(transactions, new Comparator<CPSTransaction>() {
            @Override
            public int compare(CPSTransaction o1, CPSTransaction o2) {
                return o2.getEstimatedExecTime().compareTo(o1.getEstimatedExecTime());
            }
        });
        return transactions;
    }

    private Date getExecDateForAuftragAbsageCpsTx(CPSTransaction oldCreateSubTx) {
        final Date now = new Date();
        final Date execDate = oldCreateSubTx.getEstimatedExecTime();
        return (execDate != null && execDate.after(now)) ? execDate : now;
    }

    final void sendAbsageToCps(final long sessionId,
            final CpsTxInfosForAuftragAbsage cpsTxInfosForAuftragAbsage) throws Exception {
        switch (cpsTxInfosForAuftragAbsage.getCpsTxType()) {
            case NONE:
                break;
            case MODIFY_SUB:
            case CANCEL_SUB:
                handleCancel(sessionId, cpsTxInfosForAuftragAbsage);
                break;
            default:
                break;
        }
    }

    private void handleCancel(long sessionId, CpsTxInfosForAuftragAbsage cpsTxInfosForAuftragAbsage) throws StoreException {
        final CreateCPSTransactionParameter cpsTransactionParameter =
                new CreateCPSTransactionParameter(
                        cpsTxInfosForAuftragAbsage.getAuftragId(),
                        null,
                        cpsTxInfosForAuftragAbsage.getCpsTxType().serviceOrderType,
                        CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                        CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                        cpsTxInfosForAuftragAbsage.getExecutionDateForAbsage(),
                        null, null, null, null, false, false,
                        sessionId
                );
        final CPSTransactionResult txResult = cpsService.createCPSTransaction(cpsTransactionParameter);
        if (txResult.getWarnings() != null && txResult.getWarnings().isNotEmpty()) {
            throw new RuntimeException(
                    String.format("Absage nicht möglich, da ein Fehler bei der CPS-Provisionierung aufgetreten ist:%n%s",
                            txResult.getWarnings().getMessagesAsText())
            );
        }
        if (txResult.getCpsTransactions() != null) {
            for (final CPSTransaction cpsTransaction : txResult.getCpsTransactions()) {
                cpsService.sendCpsTx2CPSAsyncWithoutNewTx(cpsTransaction, sessionId);
            }
        }
    }

    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

}
