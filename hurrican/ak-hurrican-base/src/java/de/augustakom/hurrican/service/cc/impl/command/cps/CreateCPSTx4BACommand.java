/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2009 11:44:16
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.Portierungsart;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungCPSPredicate;


/**
 * Command-Klasse, um CPS-Transactions fuer Provisionierungsauftraege zu erstellen. <br> <br> Die ermittelten
 * Provisionierungsauftraege (mit RealDate = 'respectedRealDate') werden nach folgenden Kriterien gefiltert: <br> <ul>
 * <li>Verlauf ist nicht als manuelle Provisionierung markiert. <li>fuer den Verlauf existiert keine aktive bzw.
 * abgeschlossene CPS-Transaction <li>Verlauf ist im Status 'bei Technik' resp. 'Kuendigung bei Technik' </ul> <br>
 * Weitere Kriterien sind ueber <code>CPSService#isCPSProvisioningAllowed</code> definiert. <br><br> Statt der
 * Ermittlung der Provisionierungsauftraege ueber das Realisierungsdatum kann dem Command auch ein einzelner
 * Provisionierungsauftrag uebergeben werden.
 *
 *
 */
public class CreateCPSTx4BACommand extends AbstractCPSCommand {

    /**
     * Konstante, um das zu beruecksichtigende Realisierungsdatum zu uebergeben.
     */
    public static final String KEY_RESPECTED_REAL_DATE = "respected.real.date";
    /**
     * Konstante, um dem Command einen speziellen Verlauf zu uebergeben.
     */
    public static final String KEY_ASSIGNED_PROVISIONING_ORDER = "assigned.provisioning.order";
    private static final Logger LOGGER = Logger.getLogger(CreateCPSTx4BACommand.class);
    private BAService baService;
    private BAConfigService baConfigService;
    private CCAuftragService ccAuftragService;
    private PhysikService physikService;
    private RufnummerService rufnummerService;

    private Date respectedRealDate = null;
    private Verlauf assignedProvOrder = null;

    private Boolean isAutoCreation = Boolean.TRUE;

    /* Map, um Provisionierungsauftraege zu buendeln. Key=Verlauf-ID, Value=StackModel mit Stack-Seq und ID */
    private Map<Long, StackModel> provOrder2StackMap = null;
    /* Map mit den Taifun-Auftragsnummern, die fuer die Provisionierung 'ignoriert' werden sollen. */
    private Map<Long, AuftragDaten> taifunOrdersToIgnore = null;

    /**
     * called by Spring
     *
     * @throws ServiceNotFoundException
     */
    public void init() throws ServiceNotFoundException {
        setRufnummerService(getBillingService(RufnummerService.class));
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            initCommand();

            List<Verlauf> provOrders = null;
            if (respectedRealDate != null) {
                LOGGER.info("Find provisioning orders for real date " + respectedRealDate);

                // Provisionierungsauftraege mit RealDate=respectedRealDate ermitteln
                provOrders = baService.findActVerlaeufe(respectedRealDate, Boolean.FALSE);

                LOGGER.info("count of provisioning orders for real date: " + ((provOrders != null) ? provOrders.size() : 0));
            }
            else if (assignedProvOrder != null) {
                // uebergebenen Provisionierungsauftrag verwenden
                if (DateTools.isDateBefore(assignedProvOrder.getRealisierungstermin(), new Date())) {
                    throw new HurricanServiceCommandException(
                            "Real-Date of provisioning order is not valid!");
                }
                else {
                    provOrders = new ArrayList<Verlauf>();
                    provOrders.add(assignedProvOrder);
                    isAutoCreation = Boolean.FALSE;
                }
            }

            if (CollectionTools.isEmpty(provOrders)) {
                throw new HurricanServiceCommandException("Es wurden keine " +
                        "Provisionierungsauftraege gefunden, fuer die eine CPS-Transaction erstellt werden konnte.");
            }

            // Provisioning-Orders filtern, sortieren und buendeln
            List<Verlauf> filteredProvOrders = filterProvisioningOrders(provOrders);
            sortAndBundleProvisioningOrders(filteredProvOrders);

            if (CollectionTools.isNotEmpty(filteredProvOrders)) {
                // CPS-Transaction pro ProvisioningOrder erstellen
                List<CPSTransaction> cpsTransactions = createCPSTransactions(filteredProvOrders);
                return cpsTransactions;
            }
            else {
                LOGGER.info("WARNINGS: " + getWarnings().getWarningsAsText());
                throw new HurricanServiceCommandException(
                        "Auf Grund der Filter-Regeln ist für keinen der Bauaufträge eine CPS-Provisionierung möglich.");
            }
        }
        catch (HurricanServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);
        }
    }

    /* Initialisiert das Command. */
    private void initCommand() throws Exception {
        checkValues();
        provOrder2StackMap = new HashMap<Long, StackModel>();
        taifunOrdersToIgnore = new HashMap<Long, AuftragDaten>();
    }

    /*
     * Erstellt fuer jeden Provisionierungsauftrag aus <code>provOrders</code>
     * eine CPS-Transaction.
     * @param provOrders Liste mit den Provisionierungsauftraegen, die an den CPS
     * uebergeben werden sollen.
     * @return Liste mit den generierten CPS-Transactions
     */
    private List<CPSTransaction> createCPSTransactions(List<Verlauf> provOrders) throws HurricanServiceCommandException {
        try {
            List<CPSTransaction> result = new ArrayList<CPSTransaction>();
            for (Verlauf v : provOrders) {
                CPSTransaction cpsTx = null;
                try {
                    // BA-Anlass ermitteln (enthaelt den CPS ServiceOrder-Type)
                    BAVerlaufAnlass anlass = baConfigService.findBAVerlaufAnlass(v.getAnlass());

                    // SO_STACK_ID und SO_STACK_SEQ setzen
                    StackModel stackMdl4ProvOrder = provOrder2StackMap.get(v.getId());
                    Long stackId = (stackMdl4ProvOrder != null) ? stackMdl4ProvOrder.getStackId() : null;
                    Long stackSeq = (stackMdl4ProvOrder != null) ? stackMdl4ProvOrder.getStackSeq() : null;

                    CPSExecutionDetails execDetails =
                            defineExecTimeAndPrio(v.getAuftragId(), v.getRealisierungstermin());

                    // CPS-Transaction anlegen
                    CPSTransactionResult txResult = cpsService.createCPSTransaction(
                            new CreateCPSTransactionParameter(v.getAuftragId(), v.getId(), anlass.getCpsServiceOrderType(), CPSTransaction.TX_SOURCE_HURRICAN_VERLAUF, execDetails.getServiceOrderPrio(),
                                    execDetails.getExecDateTime(), stackId, stackSeq, null, null, isAutoCreation, false, getSessionId())
                    );

                    if (CollectionTools.isNotEmpty(txResult.getCpsTransactions())) {
                        cpsTx = txResult.getCpsTransactions().get(0);
                    }
                    else {
                        String warnings = (txResult.getWarnings() != null)
                                ? txResult.getWarnings().getWarningsAsText() : null;
                        throw new HurricanServiceCommandException(warnings);
                    }

                    // Verlaufsstatus aendern
                    changeVAState(cpsTx);

                    result.add(cpsTx);
                }
                catch (Exception e) {
                    if (cpsTx != null) {
                        createCPSTxLog(cpsTx, e.getMessage(), true);
                    }
                    addWarning(v.getAuftragId(), "Error creating CPS-Tx: " + e.getMessage());
                }
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Unexpected error during creation of CPS Transactions: " + e.getMessage(), e);
        }
    }

    /*
     * Ermittelt die Abteilungsdatensaetze der techn. Abteilungen (ausser FieldService u. extern)
     * zu den Verlaeufen, die der CPS-Tx zugeordnet sind und setzt deren Status auf 'CPS-Provisionierung'.
     * @throws HurricanServiceCommandException
     */
    private void changeVAState(CPSTransaction cpsTx) throws HurricanServiceCommandException {
        try {
            List<Long> verlaufIDs = cpsService.findVerlaufIDs4CPSTransaction(cpsTx);
            for (Long verlaufId : verlaufIDs) {
                List<VerlaufAbteilung> verlaufAbteilungen = baService.findVerlaufAbteilungen(verlaufId);
                if (CollectionTools.isNotEmpty(verlaufAbteilungen)) {
                    CollectionUtils.filter(verlaufAbteilungen, new VerlaufAbteilungCPSPredicate());

                    for (VerlaufAbteilung verlaufAbteilung : verlaufAbteilungen) {
                        verlaufAbteilung.setVerlaufStatusId(VerlaufStatus.STATUS_CPS_BEARBEITUNG);
                        verlaufAbteilung.setBearbeiter("CPS");
                        baService.saveVerlaufAbteilung(verlaufAbteilung);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error while changing the state of the provisioning order: " + e.getMessage(), e);
        }
    }

    /*
     * Filtert die Provisionierungsauftraege nach den o.g. Kriterien.
     * @param provOrders zu filternde Liste
     * @return Liste mit den Provisionierungsauftraegen, fuer die
     * eine CPS-Transaction erstellt werden soll.
     */
    private List<Verlauf> filterProvisioningOrders(List<Verlauf> provOrders) throws HurricanServiceCommandException {
        if (CollectionTools.isEmpty(provOrders)) { return null; }

        try {
            Map<Long, Verlauf> addedTaifunOrders = new HashMap<Long, Verlauf>();
            List<Verlauf> filteredProvOrders = new ArrayList<Verlauf>();
            for (Verlauf verlauf : provOrders) {
                // manuelle Provisionierung auf Verlauf pruefen
                if (!BooleanTools.nullToFalse(verlauf.getPreventCPSProvisioning())) {
                    if (verlauf.isVerteilt()) {
                        checkProvisioningAllowed(filteredProvOrders, verlauf, addedTaifunOrders);
                    }
                    else {
                        addWarning(verlauf.getAuftragId(), "ProvisioningOrder state is not valid.");
                    }
                }
                else {
                    addWarning(verlauf.getAuftragId(), "ProvisioningOrder is marked as manual");
                }
            }

            return filteredProvOrders;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error during filtering provisioning orders: " + e.getMessage(), e);
        }
    }

    /* Prueft, ob fuer den Verlauf eine CPS-Tx erstellt werden darf. */
    private void checkProvisioningAllowed(List<Verlauf> filteredProvOrders, Verlauf verlauf, Map<Long, Verlauf> addedTaifunOrders)
            throws FindException, HurricanServiceCommandException {
        CPSProvisioningAllowed allowed = cpsService.isCPSProvisioningAllowed(
                verlauf.getAuftragId(),
                LazyInitMode.noInitialLoad,
                isAutoCreation,
                false,
                true);

        if (allowed.isProvisioningAllowed()) {
            // aktive/abgeschlossene CPS-Tx pruefen
            List<CPSTransaction> activeOrFinishedCPSTx = new ArrayList<CPSTransaction>();
            List<CPSTransaction> cpsTx4Verlauf = cpsService.findCPSTransactions4Verlauf(verlauf.getId());
            CollectionTools.addAllIgnoreNull(activeOrFinishedCPSTx, cpsTx4Verlauf);

            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(verlauf.getAuftragId());
            if (auftragDaten != null) {
                List<CPSTransaction> activeCPSTx4TaifunOrder = cpsService
                        .findActiveCPSTransactions(auftragDaten.getAuftragNoOrig(), null, null);
                CollectionTools.addAllIgnoreNull(activeOrFinishedCPSTx, activeCPSTx4TaifunOrder);

                if (CollectionTools.isEmpty(activeCPSTx4TaifunOrder)) {
                    // pruefen, ob eine erfolgreich abgeschlossene CPS-Tx fuer den
                    // Billing-Auftrag zum vorgesehenen Execution-Termin vorhanden ist - unabhaengig vom Bauauftrag!
                    List<CPSTransaction> successfulCpsTx = cpsService.findSuccessfulCPSTransaction4TechOrder(auftragDaten.getAuftragId());
                    if (CollectionTools.isNotEmpty(successfulCpsTx)) {
                        for (CPSTransaction successful : successfulCpsTx) {
                            if (DateTools.isDateEqual(successful.getEstimatedExecTime(),
                                    verlauf.getRealisierungstermin())) {
                                activeOrFinishedCPSTx.add(successful);
                            }
                        }
                    }
                }
            }

            if (CollectionTools.isNotEmpty(activeOrFinishedCPSTx)) {
                boolean actOrFinish = false;
                for (CPSTransaction tx : activeOrFinishedCPSTx) {
                    if (tx.isActive() || tx.isFinished()) {
                        actOrFinish = true;
                        break;
                    }
                }

                if (!actOrFinish) {
                    // Auftrag darf grundsaetzlich provisioniert werden
                    addProvisioningOrder(filteredProvOrders, verlauf, addedTaifunOrders);
                }
                else {
                    addWarning(this, String.format("Order has active CPS provisioning: %s", verlauf.getAuftragId()));
                }
            }
            else {
                // Auftrag darf grundsaetzlich provisioniert werden
                addProvisioningOrder(filteredProvOrders, verlauf, addedTaifunOrders);
            }
        }
        else {
            addWarning(this, String.format("Order (%s) not allowed for CPS provisioning. Reason: %s",
                    verlauf.getAuftragId(), allowed.getNoCPSProvisioningReason()));
        }
    }

    /*
     * Bei Auftraegen fuer TK-Anlagen prueft die Methode, ob fuer
     * den Billing-Auftrag bereits eine CPS-Tx in dieser Session vorgesehen ist.
     * Ist dies der Fall, wird der Provisionierungsauftrag nicht beruecksichtigt.
     * <br>
     * Ansonsten wird der Verlauf der Liste <code>filteredProvOrders</code> hinzugefuegt.
     * Ausserdem wird noch geprueft, ob auf Grund von Physik-Uebernahmen weitere
     * Verlaeufe / Auftraege hinzugefuegt werden muessen. <br>
     * <br>
     * @param filteredProvOrders
     * @param provOrderToAdd
     * @throws HurricanServiceCommandException
     *
     */
    private void addProvisioningOrder(
            List<Verlauf> filteredProvOrders,
            Verlauf provOrderToAdd,
            Map<Long, Verlauf> addedTaifunOrders) throws HurricanServiceCommandException {
        try {
            boolean hasTK = ccLeistungsService.hasTechLeistungWithExtLeistungNo(ExterneLeistung.ISDN_TYP_TK.leistungNo,
                    provOrderToAdd.getAuftragId(), false);

            if (hasTK) {
                // Pruefung, ob fuer Taifun-Auftrag schon eine Provisionierung vorhanden nur,
                // wenn es sich um einen TK-Auftrag handelt
                AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragId(provOrderToAdd.getAuftragId());

                if (!addedTaifunOrders.containsKey(auftragDaten.getAuftragNoOrig()) &&
                        !taifunOrdersToIgnore.containsKey(auftragDaten.getAuftragNoOrig())) {
                    filteredProvOrders.add(provOrderToAdd);
                    addVerlauf4PhysikUebernahme(filteredProvOrders, provOrderToAdd);

                    addedTaifunOrders.put(auftragDaten.getAuftragNoOrig(), provOrderToAdd);
                }
                else {
                    // pruefen, ob die Provisionierungsanlaesse zu dem Taifun-Auftrag unterschiedlich sind
                    Verlauf assignedProvOrder = addedTaifunOrders.get(auftragDaten.getAuftragNoOrig());
                    if (assignedProvOrder == null) {
                        addWarning(provOrderToAdd.getAuftragId(),
                                "Please check CPS-Tx for Taifun order " + auftragDaten.getAuftragNoOrig());
                    }
                    else if (NumberTools.notEqual(assignedProvOrder.getAnlass(), provOrderToAdd.getAnlass())) {
                        // unterschiedliche Anlaesse (z.B. Kuendigung <> Neuschaltung) fuehrt dazu, dass
                        // der gesamte Taifun-Auftrag NICHT provisioniert werden darf (manuelle Provisionierung!)
                        addWarning(provOrderToAdd.getAuftragId(),
                                "Different provisioning types found for provisioning of Taifun order " + auftragDaten.getAuftragNoOrig() +
                                        "! Manual provisioning!"
                        );

                        // Taifun-Order aus addedTaifunOrders entfernen und 'merken'
                        addedTaifunOrders.remove(auftragDaten.getAuftragNoOrig());
                        taifunOrdersToIgnore.put(auftragDaten.getAuftragNoOrig(), auftragDaten);
                    }
                    else {
                        addWarning(provOrderToAdd.getAuftragId(),
                                "CPS-Tx for billing order already added - combined provisioning!");
                    }
                }
            }
            else {
                filteredProvOrders.add(provOrderToAdd);
                addVerlauf4PhysikUebernahme(filteredProvOrders, provOrderToAdd);
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(
                    "Error adding provisioning (ID: " + provOrderToAdd.getId() + ") order to filtered list: " + e.getMessage(), e);
        }
    }

    /*
     * Ueberprueft, ob fuer den Verlauf <code>parentVerlauf</code> eine Physik-Uebernahme
     * besteht und der Ursprungs-Auftrag auf 'gekuendigt' gesetzt ist (ohne dass ein
     * Provisionierungsauftrag an die Technik ging). <br>
     * In diesem Fall wird der Ursprungsauftrag (bzw. der auf 'abgeschlossen' gesetzte
     * Bauauftrag) der Liste <code>filteredProvOrders</code> hinzugefuegt.
     * <br><br>
     * Dieses Vorgehen ist z.B. fuer Produktwechsel eines Kunden wichtig.
     * @param filteredProvOrders
     * @param parentVerlauf
     */
    private void addVerlauf4PhysikUebernahme(List<Verlauf> filteredProvOrders, Verlauf parentVerlauf)
            throws HurricanServiceCommandException {
        try {
            List<PhysikUebernahme> physikUebernahmen =
                    physikService.findPhysikUebernahmen4Verlauf(parentVerlauf.getId());
            if (CollectionTools.isNotEmpty(physikUebernahmen)) {
                for (PhysikUebernahme physikUebernahme : physikUebernahmen) {
                    if (physikUebernahme.isKriteriumNeu2Alt()) {
                        // PhysikUebernahme vom Typ ALT_NEU ermitteln
                        PhysikUebernahme puAlt2Neu = physikService.findPhysikUebernahme(
                                physikUebernahme.getVorgang(), PhysikUebernahme.KRITERIUM_ALT_NEU);
                        // PhysikUebernahme ALT_NEU nur beruecksichtigen, wenn kein Verlauf zugeordnet
                        // ist. In diesem Fall ist der Verlauf fuer den Auftrag automatisch abgeschlossen worden.)
                        if ((puAlt2Neu != null) && (puAlt2Neu.getVerlaufId() == null)) {
                            // aktuellsten Verlauf fuer den Ursprungsauftrag ermitteln
                            List<Verlauf> verlaeufe = baService.findVerlaeufe4Auftrag(puAlt2Neu.getAuftragIdA());
                            if (CollectionTools.isNotEmpty(verlaeufe)) {
                                Verlauf verlaufToCheck = verlaeufe.get(0);
                                if (verlaufToCheck.isKuendigung() && !BooleanTools.nullToFalse(verlaufToCheck.getAkt())) {
                                    filteredProvOrders.add(verlaufToCheck);

                                    // mehrere Physikuebernahmen i.d.R. nicht ueber mehrere Taifun-Auftraege.
                                    // --> nur einen Verlauf zuordnen (Kombination bei TK-Anlagen erfolgt automatisch)
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error determining according provisioning order (for e.g. product change): " + e.getMessage(), e);
        }
    }

    /*
     * Sortiert die Provisionierungsauftraege nach folgenden Kriterien: <br>
     * <ul>
     *   <li>Provisionierungsanlass (Kuendigungen zuerst, dann alle anderen)
     *   <li>TODO Auftraege des gleichen Kunden direkt hintereinander
     * </ul>
     * <br>
     * Im Anschluss an die Sortierung nach Anlass erfolgt noch eine Ermittlung, welche
     * Provisionierungsauftraege 'zusammen gehoeren', also zu einem Kunden gehoeren. Hierfuer
     * werden temp. Stack-Sequences generiert und dem Verlauf in einer Map zugeordnet.
     * @param provisioningOrders die zu sortierenden Provisionierungsauftraege
     * @throws ServiceCommandException
     */
    private void sortAndBundleProvisioningOrders(List<Verlauf> provisioningOrders) throws ServiceCommandException {
        try {
            // temp. Map, um zur Verlaufs-ID die Kundennummer zu speichern
            Map<Long, Long> verlauf2CustomerMap = new HashMap<Long, Long>();

            // Map definiert, ob ein Kunde mehrere Provisionierungsauftraege hat (ist der
            // Fall, wenn Value=TRUE ist)
            Map<Long, Boolean> customerMultipleMap = new HashMap<Long, Boolean>();

            // zu jedem Provisionierungsauftrag den Kunden laden und in den Maps speichern
            for (Verlauf v : provisioningOrders) {
                Auftrag auftrag = ccAuftragService.findAuftragById(v.getAuftragId());
                verlauf2CustomerMap.put(v.getId(), auftrag.getKundeNo());

                Boolean multiple = customerMultipleMap.containsKey(auftrag.getKundeNo()) ? Boolean.TRUE : null;
                customerMultipleMap.put(auftrag.getKundeNo(), multiple);
            }

            sortProvisioningOrders(provisioningOrders);

            // temp. Map, um zu einer Kundennummer eine temp. Stack-Sequence zu erzeugen
            Map<Long, StackModel> customer2SeqMap = new HashMap<Long, StackModel>();

            // bei aufeinander folgenden ProvisioningOrders fuer einen Kunden eine Reihenfolge generieren
            LOGGER.info("sortierte ProvisioningOrders: (AuftragID - VerlaufID - RealDate - BAAnlassID)");
            for (Verlauf v : provisioningOrders) {
                LOGGER.info(v.getAuftragId() + " - " + v.getId() + " - " + v.getRealisierungstermin() + " - " + v.getAnlass());

                Long custNo = verlauf2CustomerMap.get(v.getId());
                Boolean customerMultiple = customerMultipleMap.get(custNo);
                if (BooleanTools.nullToFalse(customerMultiple)) {
                    // Kunde hat mehrere Provisionierungsauftraege
                    //   --> Stack-Sequence erzeugen/ermitteln und dem Prov.Auftrag zuordnen
                    StackModel savedStackMdl = customer2SeqMap.get(custNo);

                    // StackSequence aus Customer-Zuordnung ermitteln oder neue Sequence erzeugen
                    Long stackSeq = (savedStackMdl != null) ? savedStackMdl.getStackSeq() : null;
                    if (stackSeq == null) {
                        stackSeq = createNewStackSequence();
                    }

                    // StackId aus Customer-Zuordnung ermitteln u. erhoehen oder auf 1 setzen
                    Long stackId = (savedStackMdl != null) ? Long.valueOf(savedStackMdl.getStackId() + 1) : Long.valueOf(1);

                    // neues StackModel erzeugen und dem Verlauf sowie dem Kunden zuordnen
                    StackModel stackMdl = new StackModel();
                    stackMdl.setStackSeq(stackSeq);
                    stackMdl.setStackId(stackId);

                    customer2SeqMap.put(custNo, stackMdl);
                    provOrder2StackMap.put(v.getId(), stackMdl);
                    LOGGER.info(" +++ +++ +++ +++  Stack Sequence / ID: " + stackSeq + " / " + stackId);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Error during sorting and bundling the provisioning orders: " + e.getMessage(), e);
        }
    }

    void sortProvisioningOrders(List<Verlauf> provisioningOrders) {
        // ProvisioningOrders nach Anlass sortieren (erst Kuendigung, dann alle anderen)
        Collections.sort(provisioningOrders, new Comparator<Verlauf>() {
            @Override
            public int compare(Verlauf o1, Verlauf o2) {
                if ((o1.isKuendigung() && o2.isKuendigung()) ||
                        (!o1.isKuendigung() && !o2.isKuendigung())) {
                    return 0;
                }
                if (o1.isKuendigung()) {
                    return -1;
                }

                return 1;
            }
        });
    }

    /*
     * Setzt die geplante Ausfuehrungszeit sowie die Prioritaet
     * fuer die CPS-Transaction in einem temporaeren Modell. <br>
     * Die Ausfuehrungszeit sowie die Prioritaet leitet sich aus den Rufnummern wie folgt ab: <br>
     * <ul>
     *   <li>Portierung STANDARD --> 7 Uhr / Standard-Prio
     *   <li>Portierung EXPORT   --> 7 Uhr / hohe Prio
     *   <li>Portierung SONDER   --> 6 Uhr / hohe Prio
     *   <li>keine Portierung    --> 6 Uhr / Standard-Prio  (wenn keine oder M-net Rufnummer)
     * </ul>
     * <br>
     * Es werden nur Rufnummern beruecksichtigt, deren
     * <br>
     * Tritt waehrend der Ermittlung der Ausfuehrungszeit/Prio ein Fehler auf, wird
     * als 7 Uhr / Standard-Prio als Default gesetzt. (Dies geschieht ebenfalls, wenn
     * der Auftrag keine Rufnummern besitzt bzw. keine Portierung zum geplanten
     * Ausfuehrungsdatum ansteht.)
     * <br>
     * @param cpsTx die CPS-Transaction, deren Ausfuehrungszeit und Prio definiert
     * werden soll
     * @return temp. Modell mit der Ausfuehrungszeit sowie der Prio.
     */
    private CPSExecutionDetails defineExecTimeAndPrio(Long auftragId, Date execDate) {
        int execHour = -1;
        Long prio = null;
        CPSExecutionDetails details = new CPSExecutionDetails();
        try {
            AuftragDaten auftragDaten = ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId);

            // TODO: evtl. muss hier der eingetragene Installationstermin beruecksichtigt werden (MaxiDeluxe!)

            if ((auftragDaten != null) && (auftragDaten.getAuftragNoOrig() != null)) {
                // 1. Rufnummern ermitteln (alle Rufnummern mit HistLast=1)
                List<Rufnummer> dns = getLastDNs4Order(auftragDaten.getAuftragNoOrig(), execDate);

                if (CollectionTools.isNotEmpty(dns)) {
                    // 2. Prio und Zeit ueber Portierungs-Modus definieren
                    Long portMode = dns.get(0).getPortingTimeFrame();
                    execHour = CPSTransaction.EXEC_HOUR_DEFAULT;

                    if (NumberTools.equal(portMode, Portierungsart.PORTIERUNG_EXPORT)) {
                        prio = CPSTransaction.SERVICE_ORDER_PRIO_HIGH;
                    }
                    else if (NumberTools.equal(portMode, Portierungsart.PORTIERUNG_SONDER)) {
                        prio = CPSTransaction.SERVICE_ORDER_PRIO_HIGH;
                    }
                    else {
                        prio = CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            addWarning(auftragId, "Error determining Exec-Time and Prio - set to default!");
        }
        finally {
            Long serviceOrderPrio = (prio != null) ? prio : CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT;

            execHour = (execHour >= 0) ? execHour : CPSTransaction.EXEC_HOUR_DEFAULT;
            Date estimatedExecTime = DateTools.changeDate(execDate, Calendar.HOUR_OF_DAY, execHour);

            details.setServiceOrderPrio(serviceOrderPrio);
            details.setExecDateTime(estimatedExecTime);
        }
        return details;
    }

    /*
     * Ermittelt alle Rufnummern zu einem bestimmten Auftrag. <br>
     * Von diesen Rufnummern wiederum wird jeweils der Datensatz mit HistLast=1
     * ermittelt.
     * @param orderNoOrig Auftragsnummer, zu der die Rufnummern ermittelt werden sollen
     * @param execDate zu beruecksichtigendes RealDate der Rufnummern (nur aus
     * den Rufnummern mit HistLast=1)
     * @return Liste mit allen Rufnummern zu dem Auftrag (dabei jeweils die Rufnummer
     * mit HistLast=1)
     */
    private List<Rufnummer> getLastDNs4Order(Long orderNoOrig, Date execDate) {
        try {
            List<Rufnummer> dns = rufnummerService.findRNs4Auftrag(orderNoOrig);  // alle Rufnummern des Auftrags ermitteln

            if (CollectionTools.isNotEmpty(dns)) {
                List<Rufnummer> lastDNs = new ArrayList<Rufnummer>();
                for (Rufnummer dn : dns) {
                    lastDNs.add(rufnummerService.findLastRN(dn.getDnNoOrig()));  // relevante DNs mit HistLast=1 ermitteln
                }

                // nach Rufnummern mit Real-Date = execDate filtern
                final Date execDateToCheck = execDate;
                CollectionUtils.filter(lastDNs, new Predicate() {
                    @Override
                    public boolean evaluate(Object obj) {
                        Rufnummer dn = (Rufnummer) obj;
                        return DateTools.isDateEqual(execDateToCheck, dn.getRealDate());
                    }
                });

                return lastDNs;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * Erzeugt eine neue ServiceOrder-Stack Sequence ueber die Datenbank.
     * @return eine neue StackSequence
     */
    private Long createNewStackSequence() {
        return getCpsTransactionDAO().getNextCPSStackSequence();
    }

    /* Ueberprueft die uebergebenen Daten. */
    private void checkValues() throws ServiceCommandException {
        respectedRealDate = (Date) getPreparedValue(KEY_RESPECTED_REAL_DATE);
        assignedProvOrder = (Verlauf) getPreparedValue(KEY_ASSIGNED_PROVISIONING_ORDER);

        if ((respectedRealDate == null) && (assignedProvOrder == null)) {
            throw new HurricanServiceCommandException("Es ist kein zu beruecksichtigendes Realisierungsdatum " +
                    "bzw. kein zu verwendender Provisionierungsauftrag definiert!");
        }
    }

    /**
     * Injected
     */
    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    /**
     * Injected
     */
    public void setBaConfigService(BAConfigService baConfigService) {
        this.baConfigService = baConfigService;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * @param rufnummerService The rufnummerService to set.
     */
    public void setRufnummerService(RufnummerService rufnummerService) {
        this.rufnummerService = rufnummerService;
    }

    /**
     * Temporaeres Modell, um eine ServiceOrder-Prio sowie die Ausfuehrungszeit (Datum+Zeit) zu definieren.
     */
    static class CPSExecutionDetails {
        private Long serviceOrderPrio = null;
        private Date execDateTime = null;

        /**
         * @return the serviceOrderPrio
         */
        public Long getServiceOrderPrio() {
            return serviceOrderPrio;
        }

        /**
         * @param serviceOrderPrio the serviceOrderPrio to set
         */
        public void setServiceOrderPrio(Long serviceOrderPrio) {
            this.serviceOrderPrio = serviceOrderPrio;
        }

        /**
         * @return the execDateTime
         */
        public Date getExecDateTime() {
            return execDateTime;
        }

        /**
         * @param execDateTime the execDateTime to set
         */
        public void setExecDateTime(Date execDateTime) {
            this.execDateTime = execDateTime;
        }
    }

    /**
     * Temporaere Modell-Klasse, um eine Kombination aus Stack-Seq und Stack-ID fuer die Buendelung von CPS-Transactions
     * zu definieren.
     */
    static class StackModel {
        private Long stackSeq = null;
        private Long stackId = null;

        /**
         * @return the stackSeq
         */
        public Long getStackSeq() {
            return stackSeq;
        }

        /**
         * @param stackSeq the stackSeq to set
         */
        public void setStackSeq(Long stackSeq) {
            this.stackSeq = stackSeq;
        }

        /**
         * @return the stackId
         */
        public Long getStackId() {
            return stackId;
        }

        /**
         * @param stackId the stackId to set
         */
        public void setStackId(Long stackId) {
            this.stackId = stackId;
        }
    }

}
