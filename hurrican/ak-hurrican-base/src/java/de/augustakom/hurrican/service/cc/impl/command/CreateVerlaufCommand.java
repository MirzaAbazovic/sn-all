/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2004 12:09:11
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.verlauf.AbstractVerlaufCheckCommand;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Service-Command, um einen Bauauftrags-Verlauf zu erstellen. <br> In diesem Command sind generelle Checks
 * implementiert, die bei jedem Bauauftrag durchgefuehrt werden muessen. <br> Checks, die vom Produkt- bzw. der
 * Verlaufsart (Realisierung, Kuendigung) abhaengen, sind ueber weitere ServiceCommands realisiert, die von dieser
 * Klasse in einer Chain gebuendelt werden.
 *
 *
 */
public class CreateVerlaufCommand extends AbstractVerlaufCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateVerlaufCommand.class);

    /**
     * Key-Value fuer die prepare-Methode, um die Auftrags-ID zu uebergeben.
     */
    public static final String AUFTRAG_ID = "auftrag.id";
    /**
     * Key-Value fuer die prepare-Methode, um den Realisierungstermin zu uebergeben.
     */
    public static final String REALISIERUNGSTERMIN = "real.termin";
    /**
     * Key-Value fuer die prepare-Methode, um den Verlaufs-Anlass zu uebergeben.
     */
    public static final String ANLASS_ID = "anlass.id";
    /**
     * Key-Value fuer die prepare-Methode, um den Installationstyp zu uebergeben.
     */
    public static final String INSTALL_TYPE_ID = "install.type.id";
    /**
     * Key-Value fuer die prepare-Methode, um die Session-ID zu uebergeben.
     */
    public static final String SESSION_ID = "session.id";
    /**
     * Key-Value fuer die prepare-Methode, um zu definieren, ob der BA an die zentrale Dispo geschickt werden soll.
     */
    public static final String ZENTRALE_DISPO = "verlauf.4.zentrale.dispo";
    /**
     * Key-Value fuer die prepare-Methode, um ein Objekt mit den SubAuftragsIds zu uebergeben.
     */
    public static final String AUFTRAG_SUBORDERS = "auftrag.suborders";

    private static final String MSG_RESOURCE = "de.augustakom.hurrican.service.cc.resources.BAVerlauf";

    // Parameter fuer die Command-Klasse
    private Long auftragId = null;
    private Date realTermin = null;
    private Long anlassId = null;
    private Long installTypeId = null;
    private Long sessionId = null;
    private Boolean createVerlauf4ZentraleDispo = null;
    private Set<Long> subAuftragsIds = null;

    // Modelle
    private AuftragDaten auftragDaten = null;
    private AuftragTechnik auftragTechnik = null;
    private AKUser user = null;
    private Produkt produkt = null;
    private BAuftrag billingAuftrag = null;
    private Verlauf verlauf = null;
    private boolean aenderungsauftrag = false;
    private boolean isKuendigung;

    // Command-Results
    private Long portierungsart = null;
    private Boolean shortTermRealDate = null;

    // Services
    private BAService baService;
    private ProduktService produktService;
    private BAConfigService baConfigService;
    private CCAuftragService auftragService;
    private CCLeistungsService ccLeistungsService;
    private ChainService chainService;

    @Autowired
    private ServiceLocator serviceLocator;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        init();
        checkValues();
        checkActiveVerlauf();

        List<Pair<Long, Produkt>> orderId2ProductList = loadAuftragDaten();

        // Verlaufs-Header generieren
        createVerlaufHeader();

        // Synchronisiert die Leistungen mit Billing oder - im Falle einer Kuendigung - synchronisiert nicht mit
        // Billing, sondern verknuepft die aktuellen Leistungen der Auftraege mit diesem Kuendigungs BA.
        synchAndAssignTechLeistungen();

        // Daten werden bei Kuendigung nicht geprueft!
        executeCheckCommands(orderId2ProductList);

        createVerlaufForAbteilungen();

        changeTaifunAuftragStatus(verlauf, sessionId);
        BAService bas = getCCService(BAService.class);
        if (bas.isAutomaticallyDispatchable(verlauf)) {
            bas.dispoBAVerteilenAuto(verlauf.getId(), sessionId);
        }

        return verlauf;
    }

    private void init() throws ServiceNotFoundException {
        baService = getCCService(BAService.class);
        produktService = getCCService(ProduktService.class);
        baConfigService = getCCService(BAConfigService.class);
        auftragService = getCCService(CCAuftragService.class);
        ccLeistungsService = getCCService(CCLeistungsService.class);
        chainService = getCCService(ChainService.class);
    }

    /**
     * Start-Methode, um die {@link VerlaufAbteilung} Datesaetze anzulegen.
     */
    private void createVerlaufForAbteilungen() throws StoreException {
        if (BooleanTools.nullToFalse(createVerlauf4ZentraleDispo)) {
            createVerlaufAbteilungen4ZentraleDispo();
        }
        else {
            createVerlaufAbteilungen();
        }
    }

    /* Erstellt die Abteilungsdatensaetze fuer den Bauauftrag fuer die Abteilung 'zentrale Dispo' */
    private void createVerlaufAbteilungen4ZentraleDispo() throws StoreException {
        try {
            baService.createVerlaufAbteilungForZentraleDispo(verlauf.getId(), sessionId);
            changeOrderStates(true);

            verlauf.setVerlaufStatusId(VerlaufStatus.BEI_ZENTRALER_DISPO);
            baService.saveVerlauf(verlauf);
        }
        catch (Exception e) {
            throw new StoreException("Fehler bei der Erstellung des Bauauftrags fuer Zentrale Dispo: " + e.getMessage(), e);
        }
    }

    /* Erstellt die Abteilungsdatensaetze fuer den Bauauftrag an Hand der Konfiguration Produkt/Anlass. */
    private void createVerlaufAbteilungen() throws StoreException {
        try {
            boolean anDispoOrNP = true;
            List<Long> abtIds = null;

            // pruefen, welche Abteilungen fuer Verlauf konfiguriert sind
            // Dispo/NP erhaelt Verlauf immer mit folgender Ausnahme: es ist nur eine Abteilung
            // fuer den Verlauf konfiguriert - dann an Dispo/NP vorbei und Verlauf-Status gleich auf 4300.
            // Ansonsten erhaelt nur Dispo/NP (und AM) den Verlauf. Dispo/NP verteilt anschliessend
            // an die anderen Abteilungen!

            BAVerlaufConfig verlConf = baConfigService.findBAVerlaufConfig(anlassId, produkt.getId(), aenderungsauftrag);
            if (verlConf == null) {
                LOGGER.warn("Konfiguration fuer Anlass wurde nicht gefunden. BA-Anlass: " + anlassId);
            }

            // Ueber Produkt ermitteln, welche Abteilung fuer die Verteilung
            // verantwortlich ist (Dispo, NP oder keine)
            Long abtId4Verteilung = produkt.getVerteilungDurch();

            List<Long> configuredAbtIds = (verlConf != null)
                    ? baConfigService.findAbteilungen4BAVerlaufConfig(verlConf.getAbtConfigId()) : null;
            if (CollectionTools.hasExpectedSize(configuredAbtIds, 1) && (abtId4Verteilung == null)) {
                abtIds = configuredAbtIds;
            }
            else {
                abtIds = new ArrayList<>();
                abtIds.add((abtId4Verteilung != null) ? abtId4Verteilung : Abteilung.DISPO);
            }

            // ChangeRequest von M. Winkler:
            //   - pruefen, ob Auftrag einem VPN zugeordnet
            //   - wenn VPN zugeordnet, dann muss Verteilung durch NP, nicht durch DISPO erfolgen
            vpnCheck(auftragTechnik, abtIds);

            // Verlauf an AM und DISPO/NP (oder an Dispo vorbei) erstellen
            anDispoOrNP = abtIds.contains(Abteilung.DISPO) || abtIds.contains(Abteilung.NP);

            abtIds.add(Abteilung.AM);
            baService.baErstellen(verlauf.getId(), abtIds, verlauf.getRealisierungstermin(), sessionId);

            changeOrderStates(anDispoOrNP);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Erstellen des Verlaufs ist ein Fehler aufgetreten:\n" + e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Anlage des Bauauftrags ist ein nicht erwarteter Fehler aufgetreten.", e);
        }
    }

    /* Aendert den Status der vom Bauauftrag betroffenen Auftraege. */
    private void changeOrderStates(boolean anDispoOrNP) throws ServiceNotFoundException, FindException, StoreException {
        // StatusId (vom Auftrag) abhaengig davon, ob an Dispo oder an Dispo vorbei
        Long adStatusId = null;
        if (anDispoOrNP) {
            sendShortTermNotification();

            long actStatus = (auftragDaten.getStatusId() != null) ? auftragDaten.getStatusId() : -1;
            adStatusId = (actStatus >= AuftragStatus.KUENDIGUNG)
                    ? AuftragStatus.KUENDIGUNG_TECHN_REAL :
                    (actStatus >= AuftragStatus.IN_BETRIEB)
                            ? AuftragStatus.AENDERUNG_IM_UMLAUF : AuftragStatus.TECHNISCHE_REALISIERUNG;
        }
        else {
            long actStatus = (auftragDaten.getStatusId() != null) ? auftragDaten.getStatusId() : -1;
            adStatusId = (actStatus >= AuftragStatus.KUENDIGUNG)
                    ? AuftragStatus.KUENDIGUNG_TECHN_REAL :
                    (actStatus >= AuftragStatus.IN_BETRIEB)
                            ? AuftragStatus.AENDERUNG_IM_UMLAUF : AuftragStatus.TECHNISCHE_REALISIERUNG;

            verlauf.setVerlaufStatusId((actStatus >= AuftragStatus.KUENDIGUNG)
                    ? VerlaufStatus.KUENDIGUNG_BEI_TECHNIK : VerlaufStatus.BEI_TECHNIK);
            getVerlaufDAO().store(verlauf);
        }

        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDatenToChange = auftragService.findAuftragDatenByAuftragIdTx(orderId);
            auftragDatenToChange.setStatusId(adStatusId);
            auftragService.saveAuftragDaten(auftragDatenToChange, false);
        }
    }


    /**
     * Gleicht die technischen Leistungen des Auftrags ab und ordnet sie dem Verlauf zu, sofern notwendig. Bei
     * Kuendigung werden nur die bestehenden Leistungen zum Auftrag betrachtet. Andernfalls werden die technischen
     * Leistungen des Auftrages/der Auftraege mit Billing abgeglichen.
     */
    private void synchAndAssignTechLeistungen() throws ServiceNotFoundException, StoreException,
            FindException, HurricanServiceCommandException {
        boolean freigabeProcessed = false;
        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDaten4Verlauf = auftragService.findAuftragDatenByAuftragIdTx(orderId);
            Produkt produkt4Verlauf = produktService.findProdukt(auftragDaten4Verlauf.getProdId());

            if (!isKuendigung && !BooleanTools.nullToFalse(produkt4Verlauf.getAuftragserstellung())) {
                // Auftrags-Leistungen abgleichen und dem Verlauf zuordnen
                addWarnings(ccLeistungsService.synchTechLeistungen4Auftrag(auftragDaten4Verlauf.getAuftragId(),
                        auftragDaten4Verlauf.getAuftragNoOrig(), auftragDaten4Verlauf.getProdId(), verlauf.getId(),
                        true, sessionId));
            }
            else if (isKuendigung) {
                // techn. Leistungen dem Kuendigungsverlauf zuordnen
                List<Auftrag2TechLeistung> techLeistungen =
                        ccLeistungsService.findAuftrag2TechLeistungen(auftragDaten4Verlauf.getAuftragId(), null, true);
                if (CollectionTools.isNotEmpty(techLeistungen)) {
                    for (Auftrag2TechLeistung a2tl : techLeistungen) {
                        a2tl.setAktivBis(verlauf.getRealisierungstermin());
                        a2tl.setVerlaufIdKuend(verlauf.getId());
                    }
                }

                if (!freigabeProcessed) {
                    freigabeProcessed = true;
                    // Freigabedatum der Rangierung auf Kuendigungsdatum+Delay setzen
                    manipulateFreigabedatum(verlauf, DateTools.changeDate(verlauf.getRealisierungstermin(),
                            Calendar.DATE, RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE));
                }
            }
        }

    }

    /**
     * Erzeugt den Verlaufs-Header Datensatz.
     */
    private void createVerlaufHeader() throws StoreException {
        try {
            verlauf = new Verlauf();
            verlauf.setAuftragId(auftragId);
            verlauf.setAkt(Boolean.TRUE);
            verlauf.setStatusIdAlt(auftragDaten.getStatusId());
            verlauf.setRealisierungstermin(realTermin);
            verlauf.setProjektierung(Boolean.FALSE);
            verlauf.setAnlass((anlassId != null) ? anlassId : auftragTechnik.getAuftragsart());
            verlauf.setPortierungsartId(portierungsart);
            verlauf.setInstallationRefId(installTypeId);
            verlauf.setSubAuftragsIds(subAuftragsIds);

            long actStatus = (auftragDaten.getStatusId() != null) ? auftragDaten.getStatusId() : -1;
            verlauf.setVerlaufStatusId((actStatus >= AuftragStatus.KUENDIGUNG)
                    ? VerlaufStatus.KUENDIGUNG_BEI_DISPO : VerlaufStatus.BEI_DISPO);

            getVerlaufDAO().store(verlauf);
            joinPhysikuebernahme();
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Anlegen des Bauauftrags ist ein nicht " +
                    "erwarteter Fehler aufgetreten! Bitte prüfen.", e);
        }
    }

    /**
     * Ueberprueft, ob der Auftrag sich in einer Physikuebernahme befindet. Ist dies der Fall, wird die Physikuebernahme
     * mit dem Verlauf verbunden.
     */
    private void joinPhysikuebernahme() throws StoreException {
        try {
            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            if (CollectionTools.isNotEmpty(orderIds)) {
                for (Long orderId : orderIds) {
                    PhysikUebernahme pu = getPhysikUebernahmeDAO().findLast4AuftragA(orderId);
                    if ((pu != null) && (pu.getVerlaufId() == null)) {
                        pu.setVerlaufId(verlauf.getId());
                        getPhysikUebernahmeDAO().store(pu);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Der Bauauftrag konnte mit der Physikuebernahme nicht verknuepft werden. " +
                    "Bauauftrag wurde nicht erstellt!", e);
        }
    }

    /**
     * Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden.
     */
    void checkValues() throws FindException {
        auftragId = getPreparedValue(AUFTRAG_ID, Long.class, false, "Es wurde kein Auftrag angegeben, für " +
                "den der Verlauf erstellt werden soll. Bauauftrag wird nicht erstellt!");
        Object date = getPreparedValue(REALISIERUNGSTERMIN);
        realTermin = (date instanceof Date) ? (Date) date : null;
        if ((realTermin == null) || DateTools.isDateBefore(realTermin, new Date())) {
            throw new FindException("Der Realisierungstermin für den Auftrag ist " +
                    "ungültig! Bauauftrag wird nicht erstellt!");
        }

        sessionId = getPreparedValue(SESSION_ID, Long.class, false,
                "Die Session-ID des Benutzers wurde nicht korrekt übermittelt!");
        anlassId = getPreparedValue(ANLASS_ID, Long.class, false, "Es wurde kein Bauauftrags-Anlass angegeben!");
        isKuendigung = NumberTools.equal(anlassId, BAVerlaufAnlass.KUENDIGUNG);

        installTypeId = getPreparedValue(INSTALL_TYPE_ID, Long.class, true, null);
        createVerlauf4ZentraleDispo = getPreparedValue(ZENTRALE_DISPO, Boolean.class, true, null);

        Object value = getPreparedValue(AUFTRAG_SUBORDERS);
        @SuppressWarnings("unchecked")
        Set<Long> subAuftragsIds = (Set<Long>) ((value instanceof Set<?>) ? value : null);
        this.subAuftragsIds = subAuftragsIds;
    }

    /**
     * Laedt benoetigte Produkt-Daten fuer alle Unterauftraege und fuehrt Plausichecks durch.
     *
     * @throws ServiceNotFoundException
     * @throws FindException
     */
    private List<Pair<Long, Produkt>> loadAuftragDaten4SubOrders() throws ServiceNotFoundException, FindException {
        List<Pair<Long, Produkt>> orderId2ProductList = new LinkedList<>();
        if (CollectionTools.isNotEmpty(subAuftragsIds)) {
            for (Long subOrderId : subAuftragsIds) {
                // Auftrag laden
                AuftragDaten subAuftragDaten = auftragService.findAuftragDatenByAuftragIdTx(subOrderId);
                AuftragTechnik subAuftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(subOrderId);
                checkAuftrag(subAuftragDaten, subAuftragTechnik, subOrderId, false);

                // Billing-Auftrag mit Hauptauftrag vergleichen
                // Da der Billing-Auftrag im Hauptauftrag schon geprueft ist, wird hier nur die Billing ID auf Gleichheit gecheckt.
                if ((subAuftragDaten.getAuftragNoOrig() == null)
                        || !this.auftragDaten.getAuftragNoOrig().equals(subAuftragDaten.getAuftragNoOrig())) {
                    throw new FindException(String.format("Der Billing-Auftrag für Unterauftrag %s ist nicht "
                            + "gesetzt oder weicht von Hauptauftrag ab. Bauauftrag wurde nicht erstellt!", subOrderId));
                }

                // Auftragsart pruefen
                checkAuftragsart(subAuftragTechnik, subOrderId, false);

                // Produkt laden/pruefen
                Produkt subProdukt = produktService.findProdukt(subAuftragDaten.getProdId());
                checkProdukt(subProdukt, subOrderId, false);

                orderId2ProductList.add(new Pair<>(subOrderId, subProdukt));
            }
        }
        return orderId2ProductList;
    }

    /**
     * Laedt benoetigte Auftrags- und Produkt-Daten und prueft, ob fuer den Auftrag ueberhaupt ein Verlauf erstellt
     * werden darf.
     */
    private List<Pair<Long, Produkt>> loadAuftragDaten() throws StoreException, FindException {
        try {
            AKUserService uss = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            user = uss.findUserBySessionId(sessionId);
            if (user == null) {
                throw new FindException(String.format("Für Hauptauftrag %s konnte der Benutzer nicht ermittelt "
                        + "werden. Bauauftrag wurde nicht erstellt!", auftragId));
            }

            // Auftrag laden
            auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
            auftragTechnik = auftragService.findAuftragTechnikByAuftragIdTx(auftragId);
            checkAuftrag(auftragDaten, auftragTechnik, auftragId, true);

            if (auftragDaten.getAuftragNoOrig() != null) {
                BillingAuftragService bAS = getBillingService(BillingAuftragService.class);
                billingAuftrag = bAS.findAuftrag(auftragDaten.getAuftragNoOrig());
                if (billingAuftrag == null) {
                    billingAuftrag = bAS.findAuftragStornoByAuftragNoOrig(auftragDaten.getAuftragNoOrig());
                    if (billingAuftrag == null) {
                        throw new FindException(String.format("Der Billing-Auftrag für Hauptauftrag %s konnte "
                                + "nicht ermittelt werden. Bauauftrag wurde nicht erstellt!", auftragId));
                    }
                }
            }

            // Auftragsart pruefen
            checkAuftragsart(auftragTechnik, auftragId, true);

            // Produkt laden/pruefen
            produkt = produktService.findProdukt(auftragDaten.getProdId());
            checkProdukt(produkt, auftragId, true);

            BAVerlaufAnlass baAnlass = baConfigService.findBAVerlaufAnlass(anlassId);
            aenderungsauftrag = ((baAnlass != null) && !BooleanTools.nullToFalse(baAnlass.getAuftragsart())) ? true : false;

            return loadAuftragDaten4SubOrders();
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(String.format("Bei der Ermittlung der Auftragsdaten für Hauptauftrag %s ist "
                    + "ein Fehler aufgetreten. Bauauftrag wurde nicht erstellt!", auftragId), e);
        }
    }

    void checkAuftrag(AuftragDaten auftragDaten, AuftragTechnik auftragTechnik, Long orderId, boolean mainOrder) throws FindException {
        if ((auftragDaten == null) || (auftragTechnik == null)) {
            throw new FindException(String.format("Die Auftragsdaten für %s %s konnten nicht ermittelt werden. "
                    + "Bauauftrag wurde nicht erstellt!", getAuftragbezeichnung(mainOrder), orderId));
        }
    }

    void checkAuftragsart(AuftragTechnik auftragTechnik, Long orderId, boolean mainOrder) throws FindException {
        if (auftragTechnik.getAuftragsart() == null) {
            throw new FindException(String.format("Für %s %s ist die Auftragsart nicht definiert. "
                    + "Bauauftrag wurde nicht erstellt!", getAuftragbezeichnung(mainOrder), orderId));
        }
    }

    void checkProdukt(Produkt produkt, Long orderId, boolean mainOrder) throws FindException {
        if ((produkt == null) || !BooleanTools.nullToFalse(produkt.getElVerlauf())) {
            throw new FindException(String.format("Das Produkt für %s %s konnte nicht ermittelt werden order ist "
                    + "nicht für den el. Verlauf konfiguriert. Bauauftrag wurde nicht erstellt!",
                    getAuftragbezeichnung(mainOrder), orderId));
        }
    }

    private String getAuftragbezeichnung(boolean mainOrder) {
        return mainOrder ? "Hauptauftrag " : "Unterauftrag ";
    }

    /**
     * Ueberprueft, ob fuer den Auftrag bereits ein aktiver Verlauf existiert.
     */
    private void checkActiveVerlauf() throws FindException {
        try {
            // Hauptauftrag pruefen
            if (getVerlaufDAO().hasActiveVerlauf(auftragId)) {
                throw new FindException("Für den Auftrag existiert bereits ein aktiver Verlauf. " +
                        "Bauauftrag wurde nicht erstellt!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Überprüfung des Auftrag-Status ist ein nicht erwarteter Fehler " +
                    "aufgetreten. Bauauftrag wurde nicht erstellt.", e);
        }
    }

    /**
     * Ermittelt die notwendigen ServiceCommandChains fuer jeden Auftrag (Hauptauftrag und Unterauftraege) und fuehrt
     * diese aus.
     *
     * @param kuendigung Flag, ob Kuendigungs-Commands ausgefuehrt werden sollen.
     * @param subOrder  Flag gibt an, ob Sub-Orders vorhanden sind
     * @param auftragId
     * @param produkt
     * @throws FindException
     * @throws ServiceCommandException
     * @throws ServiceNotFoundException
     */
    private AKWarnings executeCheckCommands4Auftrag(boolean kuendigung, boolean subOrder, Long auftragId, Produkt produkt)
            throws FindException, ServiceCommandException, ServiceNotFoundException {
        AKWarnings warnings = new AKWarnings();
        if ((auftragId == null) || (produkt == null)) {
            return warnings;
        }

        Long chainId = (kuendigung) ? produkt.getVerlaufCancelChainId() : produkt.getVerlaufChainId();
        if (chainId != null) {
            List<ServiceCommand> cmds = chainService.findServiceCommands4Reference(chainId, ServiceChain.class, null);
            if (CollectionTools.isNotEmpty(cmds)) {
                AKServiceCommandChain chain = new AKServiceCommandChain();
                for (ServiceCommand cmd : cmds) {
                    IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                    if (serviceCmd == null) {
                        throw new FindException(
                                String.format("Für Auftrag %s und das definierte ServiceCommand %s konnte kein "
                                        + "Objekt geladen werden. Bauauftrag wurde nicht erstellt!", auftragId, cmd.getName()));
                    }

                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_SESSION_ID, sessionId);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_AUFTRAG_ID, auftragId);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_ANLASS_ID, anlassId);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_PRODUKT, produkt);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_BILLING_AUFTRAG, billingAuftrag);
                    serviceCmd.prepare(AbstractVerlaufCheckCommand.KEY_REAL_DATE, realTermin);

                    chain.addCommand(serviceCmd);
                }

                chain.executeChain(true);

                // Result-Parameter der Commands abfragen - nur fuer Hauptauftrag
                if (!subOrder) {
                    portierungsart = (Long) chain.getCommandResult(
                            AbstractVerlaufCheckCommand.RESULT_KEY_PORTIERUNGSART);
                    shortTermRealDate = (Boolean) chain.getCommandResult(
                            AbstractVerlaufCheckCommand.RESULT_KEY_IN_SHORT_TERM);
                }

                warnings.addAKWarnings(chain.getWarnings());
            }
        }
        else {
            LOGGER.warn(String.format("No ServiceChain defined for order %s and product %s. Order is not checked!",
                    auftragId, produkt.getProdId()));
        }
        return warnings;
    }

    /**
     * Ermittelt die notwendige ServiceCommandChain und fuehrt diese aus.
     */
    private void executeCheckCommands(List<Pair<Long, Produkt>> subOrders) throws FindException {
        try {
            AKWarnings warnings = new AKWarnings();

            // Hauptauftrag
            warnings.addAKWarnings(executeCheckCommands4Auftrag(isKuendigung, false, auftragId, produkt));

            // Unterauftraege
            if (CollectionTools.isNotEmpty(subOrders)) {
                for (Pair<Long, Produkt> subOrder : subOrders) {
                    warnings.addAKWarnings(executeCheckCommands4Auftrag(isKuendigung, true, subOrder.getFirst(), subOrder.getSecond()));
                }
            }
            setWarnings(warnings);
        }
        catch (ServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bauauftrag wurde nicht erstellt, da die Auftragspruefung einen Fehler entdeckt hat:\n" +
                    e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Unbekannter Fehler bei der Auftragspruefung! Bauauftrag wurde nicht erstellt.", e);
        }
    }

    /**
     * Prueft, ob der Realisierungstermin des Bauauftrags kurzfristig ist. Wenn ja, wird eine EMail an DISPO
     * verschickt.
     */
    private void sendShortTermNotification() {
        try {
            if (BooleanTools.nullToFalse(shortTermRealDate) && (auftragTechnik.getVpnId() != null)) {
                //Email-Adresse anhand der Niederlassung ermitteln
                NiederlassungService ns = getCCService(NiederlassungService.class);
                Niederlassung nl = ns.findNiederlassung(auftragTechnik.getNiederlassungId());
                if ((nl != null) && StringUtils.isNotEmpty(nl.getDispoTeampostfach())) {
                    String[] emails = StringUtils.split(nl.getDispoTeampostfach(), HurricanConstants.EMAIL_SEPARATOR);
                    if ((emails != null) && (emails.length > 0)) {
                        ResourceReader rr = new ResourceReader(MSG_RESOURCE);

                        String from = rr.getValue("email.from");
                        String subject = rr.getValue("realisierung.in.short.term.subject");
                        String message = rr.getValue("realisierung.in.short.term.msg",
                                new Object[] { "" + verlauf.getAuftragId() });

                        // Mail senden
                        SimpleMailMessage mailMsg = new SimpleMailMessage();
                        mailMsg.setFrom(from);
                        mailMsg.setTo(emails);
                        mailMsg.setSubject(subject);
                        mailMsg.setText(message);
                        getMailSender().send(mailMsg);
                    }
                }
                else {
                    throw new HurricanServiceCommandException("Konnte Email-Adressen nicht ermitteln.");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    public void setBaConfigService(BAConfigService baConfigService) {
        this.baConfigService = baConfigService;
    }

    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    public void setChainService(ChainService chainService) {
        this.chainService = chainService;
    }

    // Fuer Tests
    boolean getIsKuendigung() {
        return isKuendigung;
    }

}

