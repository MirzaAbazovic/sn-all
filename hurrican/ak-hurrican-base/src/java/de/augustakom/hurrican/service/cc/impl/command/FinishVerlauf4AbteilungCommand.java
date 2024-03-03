/**
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH All rights reserved. -------------------------------------------------------
 * File created: 14.07.2006 09:27:48
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;

// @formatter:off
/**
 * Command-Implementierung, um einen Abteilungs-Verlauf zu erledigen. <br/> <br/>
 * Funktion: <br/>
 * Haben alle technischen(!) Abteilungen den Verlauf zum vorgegebenen Termin abgeschlossen, wird der ganze Verlauf auf
 * erledigt und der Auftrag auf 'in Betrieb' bzw. 'Auftrag gekuendigt' gesetzt - ohne Bauauftrags-Ruecklaeufer fuer
 * Dispo/AM. Sollte einer der nachfolgenden Faelle auftreten, wird ein BA-Ruecklaeufer erstellt: <br/>
 * <ul>
 *     <li>Produkt ist als 'Ruecklaeufer' konfiguriert
 *     <li>Verlauf ist als Ueberwachung markiert
 *     <li>Realisierungstermin min. einer Abteilung weicht vom Vorgabetermin ab
 *     <li>Auftrag ist einem VPN zugeordnet
 * </ul> <br/>
 * <br/>
 * Das Command gibt das modifizierte VerlaufAbteilungs-Objekt zurueck.
 *
 *
 */
// @formatter:on
public class FinishVerlauf4AbteilungCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(FinishVerlauf4AbteilungCommand.class);

    /**
     * Key-Value fuer die prepare-Methode, um den Realisierungstermin zu uebergeben.
     */
    public static final String REALISIERUNGSTERMIN = "real.termin";
    /**
     * Key-Value fuer die prepare-Methode, um das Verlauf-Abteilungs Objekt zu uebergeben.
     */
    public static final String VERLAUF_ABTEILUNG = "verlauf.abt";
    /**
     * Key-Value fuer die prepare-Methode, um den Benutzernamen zu uebergeben.
     */
    public static final String BEARBEITER = "bearbeiter";
    /**
     * Key-Value fuer die prepare-Methode, um eine Bemerkung zu uebergeben.
     */
    public static final String BEMERKUNG = "bemerkung";
    /**
     * Key-Value fuer die prepare-Methode, um den angemeldeten Benutzer zu uebergeben.
     */
    public static final String AK_USER = "ak-user";
    /**
     * Key-Value fuer die prepare-Methode, um einen Zusatzaufwand zu uebergeben.
     */
    public static final String ZUSATZ_AUFWAND = "zusatz.aufwand";
    /**
     * Key-Value fuer die prepare-Methode, um die SessionId zu uebergeben.
     */
    public static final String SESSION_ID = "session.id";
    /**
     * Key-Value fuer die prepare-Methode, um den NOT_POSSIBLE Status zu uebergeben.
     */
    public static final String NOT_POSSIBLE = "not.possible";
    /**
     * Key-Value fuer die prepare-Methode, um die NOT_POSSIBLE_REASON zu uebergeben.
     */
    public static final String NOT_POSSIBLE_REASON = "not.possible.reason";

    private Verlauf verlauf = null;
    private VerlaufAbteilung verlaufAbteilung = null;
    private Produkt produkt = null;
    private String bearbeiter = null;
    private Date realDate = null;
    private String bemerkung = null;
    private AKUser user = null;
    private Long zusatzAufwand = null;
    private Collection<VerlaufAbteilung> techVerlaufAbts = null;
    private Long sessionId = null;
    private Boolean notPossible = null;
    private Long notPossibleReasonRefId = null;

    // Services
    private BAService baService;
    private CCAuftragService auftragService;
    private CCLeistungsService ccLeistungsService;
    private ProduktService produktService;
    private IPAddressService ipAddressService;

    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        checkValues();
        loadRequiredData();
        finishVerlauf4Abt();
        return verlaufAbteilung;
    }

    /**
     * @return Returns the verlauf.
     */
    protected Verlauf getVerlauf() {
        return verlauf;
    }

    /**
     * Erledigt den Abteilungs-Verlauf. <br> Abhaengig von verschiedenen Daten wird der gesamte Verlauf erledigt oder
     * ein Ruecklaeufer-BA erstellt.
     */
    private void finishVerlauf4Abt() throws StoreException {
        if (verlaufAbteilung.getDatumErledigt() != null) {
            throw new StoreException("Der Bauauftrag ist bereits abgeschlossen.");
        }

        try {
            verlaufAbteilung.setBearbeiter(bearbeiter);
            verlaufAbteilung.setDatumErledigt(realDate);
            if (StringUtils.isNotBlank(bemerkung)) {
                verlaufAbteilung.setBemerkung(StringUtils.left(bemerkung, VerlaufAbteilung.BEMERKUNG_MAX_LENGTH));
            }
            if (zusatzAufwand != null) {
                verlaufAbteilung.setZusatzAufwand(zusatzAufwand);
            }
            verlaufAbteilung.setAusgetragenAm(new Date());
            verlaufAbteilung.setAusgetragenVon((user == null || StringUtils.isBlank(user.getLoginName())) ? "hurrican" : user.getLoginName());
            verlaufAbteilung.setVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT);
            verlaufAbteilung.setNotPossible(notPossible);
            verlaufAbteilung.setNotPossibleReasonRefId(notPossibleReasonRefId);
            baService.saveVerlaufAbteilung(verlaufAbteilung);

            changeStati();
            changeTechLsDate();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Abschluss des Verlaufs: " + e.getMessage(), e);
        }
    }

    /**
     * Setzt die Verlaufs- und Auftrag-Stati auf die benoetigten Werte.
     */
    protected void changeStati() throws StoreException {
        try {
            if (BooleanTools.nullToFalse(notPossible)) {
                // Verlauf als 'NOT_POSSIBLE' markieren, falls Abteilung negativ abgeschlossen hat
                getVerlauf().setNotPossible(notPossible);
            }

            if (isBAFinishedFromAllTechAbt() && !needsBARuecklaeufer()) {
                doFinishFromAllTechAbt(baService, auftragService);
            }
            else {
                closeProvisioningOrder(baService);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Aendern der Verlauf-Auftrag-Stati: " + e.getMessage(), e);
        }
    }

    /**
     * Verlauf als erledigt markieren und Auftrags-Status aller beteiligten Auftraege aendern.
     */
    protected void doFinishFromAllTechAbt(BAService bas, CCAuftragService auftragService) throws StoreException, FindException, ServiceNotFoundException {
        boolean kuendigung = getVerlauf().isKuendigung();

        // Verlauf als erledigt markieren
        Long verlStatus = (kuendigung) ? VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN : VerlaufStatus.VERLAUF_ABGESCHLOSSEN;
        getVerlauf().setVerlaufStatusId(verlStatus);
        getVerlauf().setAkt(Boolean.FALSE);
        bas.saveVerlauf(verlauf);

        // Verlaeufe der nicht techn. Abteilungen als 'erledigt' markieren
        Collection<VerlaufAbteilung> nonTechVAs = getNonTechAbtVerlaeufe();
        if (CollectionTools.isNotEmpty(nonTechVAs)) {
            for (VerlaufAbteilung va : nonTechVAs) {
                if (va.getAusgetragenAm() == null) {
                    va.setAusgetragenAm(new Date());
                    va.setDatumErledigt(realDate);
                    va.setAusgetragenVon(VerlaufStatus.AUSGETRAGEN_VON_SYSTEM);
                    va.setVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT_SYSTEM);
                    bas.saveVerlaufAbteilung(va);
                }
            }
        }

        changeStateOfOrders(auftragService, kuendigung);
        // Aktive Sperren nur bei "Auftrag in Kuendigung" auf Erledigt setzen,
        // damit diese nicht mehr in der Sperrverwaltung aufgelistet werden. Ist
        // der Auftrag in einem anderen Status, darf der Abschluss des
        // Bauauftrags die Sperre nicht loeschen.
        if (kuendigung) {
            finishActiveLocks4AllOrders();
        }
    }

    /**
     * Aendert den Auftragsstatus der betroffenen Auftraege.
     */
    private void changeStateOfOrders(CCAuftragService auftragService, boolean kuendigung) throws FindException, StoreException, ServiceNotFoundException {
        Set<Long> orderIds = getVerlauf().getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(orderId);
            // Auftrag auf 'in Betrieb' bzw. auf 'gekuendigt' setzen
            auftragDaten.setStatusId((kuendigung) ? AuftragStatus.AUFTRAG_GEKUENDIGT : AuftragStatus.IN_BETRIEB);
            if (!kuendigung && (auftragDaten.getInbetriebnahme() == null)) {
                auftragDaten.setInbetriebnahme(getVerlauf().getRealisierungstermin());
            }
            auftragService.saveAuftragDaten(auftragDaten, false);

            // StatusMeldung an Taifun
            BillingWorkflowService wfService = getBillingService(BillingWorkflowService.class);
            wfService.changeOrderState4Verlauf(getVerlauf().getId(), sessionId);

            /*
            * If Wholesale order notify Wholesale.
            */
            if (auftragDaten.isWholesaleAuftrag()) {
                baService.notifyWholesale(auftragDaten.getWholesaleAuftragsId(), getVerlauf().getId());
            }
        }
    }

    /**
     * Setzt alle aktiven Locks aller dem Verlauf zugeordneten Auftraege in den Status 'finished'
     */
    private void finishActiveLocks4AllOrders() throws ServiceNotFoundException, StoreException {
        Set<Long> orderIds = getVerlauf().getAllOrderIdsOfVerlauf();
        LockService lockService = getCCService(LockService.class);
        for (Long orderId : orderIds) {
            lockService.finishActiveLocks(orderId);
        }
    }

    /**
     * Verlaufs-Datensatz abschliessen; Auftrags-Stati werden nicht geaendert.
     */
    private void closeProvisioningOrder(BAService bas) throws StoreException {
        // nur den Abteilungs-Verlauf abschliessen
        Long status = null;
        if (NumberTools.isIn(getVerlauf().getVerlaufStatusId(),
                new Number[] { VerlaufStatus.KUENDIGUNG_RL_AM, VerlaufStatus.RUECKLAEUFER_AM })) {
            status = getVerlauf().getVerlaufStatusId();  // falls BA an Dispo vorbei
        }
        else {
            status = (getVerlauf().getVerlaufStatusId().longValue() >= VerlaufStatus.KUENDIGUNG_BEI_DISPO.longValue())
                    ? VerlaufStatus.KUENDIGUNG_RL_DISPO : VerlaufStatus.RUECKLAEUFER_DISPO;
        }

        getVerlauf().setVerlaufStatusId(status);
        bas.saveVerlauf(verlauf);
    }

    /**
     * Ermittelt die technischen Leistungen zum Auftrag, die fuer die aktuelle Abteilung relevant sind. <br> Das
     * angegebene Realisierungsdatum wird als aktiv-von bzw. aktiv-bis Datum der Leistungszuordnungen verwendet. <br>
     * Exceptions werden lediglich abgefangen und protokolliert!
     */
    private void changeTechLsDate() {
        try {
            List<Auftrag2TechLeistung> a2tls = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(getVerlauf().getId());
            if (CollectionTools.isNotEmpty(a2tls)) {
                for (Auftrag2TechLeistung a2tl : a2tls) {
                    TechLeistung techLs = ccLeistungsService.findTechLeistung(a2tl.getTechLeistungId());
                    if ((techLs != null) && techLs.containsAbteilung(verlaufAbteilung.getAbteilungId())) {
                        if ((a2tl.getAktivBis() != null) &&
                                !DateTools.isDateEqual(a2tl.getAktivBis(), realDate)) {
                            a2tl.setAktivBis(realDate);
                            ccLeistungsService.saveAuftrag2TechLeistung(a2tl);
                        }
                        else if ((a2tl.getAktivBis() == null) && !DateTools.isDateEqual(a2tl.getAktivVon(), realDate)) {
                            a2tl.setAktivVon(realDate);
                            ccLeistungsService.saveAuftrag2TechLeistung(a2tl);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            // do nothing
        }
    }

    /**
     * Ueberprueft, ob der Bauauftrag bereits von allen technischen Abteilungen erledigt wurde. <br> Sollte der Wert
     * nicht sicher ermittelt werden koenne, wird vorsichtshalber 'false' zurueck geliefert.
     */
    protected boolean isBAFinishedFromAllTechAbt() {
        try {
            Collection<VerlaufAbteilung> techAbts = getTechAbtVerlaeufe();
            if (CollectionTools.isNotEmpty(techAbts)) {
                for (VerlaufAbteilung va : techAbts) {
                    if (va.getDatumErledigt() == null) {
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Ueberprueft, ob fuer den aktuellen Fall ein Bauauftrags-Ruecklaeufer benoetigt wird. <br> Sollte keine sichere
     * Ueberpruefung stattfinden koennen, wird vorsichtshalber 'true' zurueck geliefert.
     */
    protected boolean needsBARuecklaeufer() {
        try {
            AuftragTechnik at = auftragService.findAuftragTechnikByAuftragIdTx(getVerlauf().getAuftragId());
            Collection<VerlaufAbteilung> techAbts = getTechAbtVerlaeufe();
            return new NeedsBaRuecklaeuferResult(produkt, at, verlauf, techAbts).needsBaRuecklaeufer();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return true;
        }
    }

    public static class NeedsBaRuecklaeuferResult {
        private boolean produktIstAlsRueckLaeuferMarkiertOderProzessWirdBeobachtet;
        private boolean vpnAuftrag;
        private boolean nichtZumVorgegebenenTerminAbgeschlossen;
        private boolean bemerkungVonFieldServiceOderExtern;
        private boolean verlaufNotPossible;
        private boolean keineTechAbtVerlaeufe;

        public NeedsBaRuecklaeuferResult(final Produkt produkt, final AuftragTechnik at, final Verlauf verlauf, final Collection<VerlaufAbteilung> techAbteilungVerlaeufe) {
            if (BooleanTools.nullToFalse(produkt.getBaRuecklaeufer()) || BooleanTools.nullToFalse(verlauf.getObserveProcess())) {
                // Produkt ist als Ruecklaeufer konfiguriert oder Verlauf ist als 'Beobachtung' markiert
                produktIstAlsRueckLaeuferMarkiertOderProzessWirdBeobachtet = true;
            }
            else {
                // prueft, ob der Auftrag einem VPN zugeordnet ist
                if (at.isVPNAuftrag()) {
                    vpnAuftrag = true;
                }

                // Pruefe Abteilungs-Verlaeufe
                if (CollectionTools.isNotEmpty(techAbteilungVerlaeufe)) {
                    Date demandDate = verlauf.getRealisierungstermin();
                    for (VerlaufAbteilung va : techAbteilungVerlaeufe) {
                        // prueft, ob alle techn. Abteilungen den BA zum vorgegebenen Termin abgeschlossen haben
                        if (!DateTools.isDateEqual(va.getDatumErledigt(), demandDate)) {
                            nichtZumVorgegebenenTerminAbgeschlossen = true;
                        }
                        // Prueft, ob ein externer Partner oder FieldService eine Bemerkung zurueckgemeldet hat.
                        // (bei FFM nicht gewuenscht; lt. Matthias Seeberger, 12.09.2014)
                        if (NumberTools.isIn(va.getAbteilungId(), new Number[] { Abteilung.EXTERN, Abteilung.FIELD_SERVICE })
                                && StringUtils.isNotBlank(va.getBemerkung())) {
                            bemerkungVonFieldServiceOderExtern = true;
                        }
                        // Prueft, ob eine Abteilung den Verlauf als 'not_possible' markiert hat
                        if (BooleanTools.nullToFalse(va.getNotPossible())) {
                            verlaufNotPossible = true;
                        }
                    }
                }
                else {
                    keineTechAbtVerlaeufe = true;
                }
            }
        }

        public final boolean needsBaRuecklaeufer() {
            return produktIstAlsRueckLaeuferMarkiertOderProzessWirdBeobachtet
                    || vpnAuftrag
                    || nichtZumVorgegebenenTerminAbgeschlossen
                    || bemerkungVonFieldServiceOderExtern
                    || verlaufNotPossible
                    || keineTechAbtVerlaeufe;
        }

        public final boolean needsBaRuecklaeuferWegenBemerkung() {
            return needsBaRuecklaeufer()
                    && !produktIstAlsRueckLaeuferMarkiertOderProzessWirdBeobachtet
                    && !vpnAuftrag
                    && !nichtZumVorgegebenenTerminAbgeschlossen
                    && !verlaufNotPossible
                    && !keineTechAbtVerlaeufe
                    && bemerkungVonFieldServiceOderExtern;
        }
    }

    /**
     * Ermittelt alle VerlaufAbteilung-Datensaetze zu technischen Abteilungen.
     */
    private Collection<VerlaufAbteilung> getTechAbtVerlaeufe() {
        return techVerlaufAbts;
    }


    /**
     * Ermittelt alle VerlaufAbteilung-Datensaetze von nicht-technischen Abteilungen (Dispo/AM).
     */
    private Collection<VerlaufAbteilung> getNonTechAbtVerlaeufe() {
        try {
            List<VerlaufAbteilung> verlAbts = baService.findVerlaufAbteilungen(getVerlauf().getId());

            Collection<VerlaufAbteilung> nonTechVAs = CollectionUtils.subtract(verlAbts, getTechAbtVerlaeufe());
            return nonTechVAs;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Laedt die zusaetzlich benoetigten Daten.
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            verlauf = baService.findVerlauf(verlaufAbteilung.getVerlaufId());
            if (verlauf == null) {
                throw new FindException("Verlaufs-Datensatz konnte nicht gefunden werden!");
            }
            else if (BooleanTools.nullToFalse(getVerlauf().getProjektierung())) {
                throw new FindException("Es sind nur Bauauftraege, keine Projektierungen fuer " +
                        "diese Funktion zulaessig!");
            }

            //reattach to session in order to avoid duplicated object problems in session caused by baService.findVerlaufAbteilungen
            verlaufAbteilung = baService.findVerlaufAbteilung(verlaufAbteilung.getId());

            produkt = produktService.findProdukt4Auftrag(getVerlauf().getAuftragId());
            if (produkt == null) {
                throw new FindException("Das zugehoerige Produkt konnte nicht ermittelt werden!");
            }

            techVerlaufAbts = baService.findVerlaufAbteilungen(verlauf.getId(),
                    VerlaufAbteilungTechPredicate.TECH_ABTEILUNGEN.toArray(new Long[VerlaufAbteilungTechPredicate.TECH_ABTEILUNGEN.size()]));
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler beim Laden von notwendigen Daten zum Abschluss des Verlaufs!", e);
        }
    }

    /**
     * Ueberprueft die Parameter, die dem Command uebergeben wurden.
     */
    private void checkValues() throws FindException {
        verlaufAbteilung = getPreparedValue(VERLAUF_ABTEILUNG, VerlaufAbteilung.class, false,
                "Es wurde kein Verlauf angegeben, der erledigt werden soll!");

        bearbeiter = getPreparedValue(BEARBEITER, String.class, false,
                "Bearbeiter, der den Bauauftrag erledigt hat wurde nicht angegeben.");

        bemerkung = getPreparedValue(BEMERKUNG, String.class, true, null);

        zusatzAufwand = getPreparedValue(ZUSATZ_AUFWAND, Long.class, true, null);

        notPossible = getPreparedValue(NOT_POSSIBLE, Boolean.class, true, null);
        notPossibleReasonRefId = getPreparedValue(NOT_POSSIBLE_REASON, Long.class, true, null);

        realDate = getPreparedValue(REALISIERUNGSTERMIN, Date.class, true, null);
        if (!BooleanTools.nullToFalse(notPossible) && (realDate == null)) {
            throw new FindException("Es wurde kein Realisierungsdatum angegeben!");
        }

        user = getPreparedValue(AK_USER, AKUser.class, true,
                "Der angemeldete Benutzer wurde nicht korrekt uebermittelt!");

        sessionId = getPreparedValue(SESSION_ID, Long.class, true, null);
    }

    public void setBaService(BAService baService) {
        this.baService = baService;
    }

    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    public void setCcLeistungsService(CCLeistungsService ccLeistungsService) {
        this.ccLeistungsService = ccLeistungsService;
    }

    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    public void setIpAddressService(IPAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

} // end
