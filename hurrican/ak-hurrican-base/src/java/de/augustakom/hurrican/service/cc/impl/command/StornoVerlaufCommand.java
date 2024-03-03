/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2004 11:14:54
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.Auftrag2TechLeistungDAO;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungPredicate;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;
import de.mnet.esb.cdm.resource.workforceservice.v1.DeleteOrder;

/**
 * Command-Klasse, um einen Verlauf zu stornieren.
 *
 *
 */
public class StornoVerlaufCommand extends AbstractVerlaufCommand {

    private static final Logger LOGGER = Logger.getLogger(StornoVerlaufCommand.class);

    /**
     * Key fuer die prepare-Methode, um die Verlaufs-ID zu uebermitteln.
     */
    public static final String VERLAUF_ID = "verlauf.id";
    /**
     * Key fuer die prepare-Methode, um das SendMail-Flag zu uebermitteln.
     */
    public static final String SEND_MAIL = "send.mail";
    /**
     * Key fuer die prepare-Methode, um den Benutzer zu uebermitteln.
     */
    public static final String AK_USER = "ak.user";
    /**
     * Key fuer die prepare-Methode, um die Session-ID zu uebermitteln.
     */
    public static final String SESSION_ID = "session.id";
    /**
     * Key fuer die prepare-Methode, um ein Flag fuer die Stornierung der Verlauf_Abteilung zu uebermitteln.
     */
    public static final String VA_STORNO = "va.storno";

    // Parameter
    private Long verlaufId = null;
    private Boolean sendMail = null;
    private Boolean vaStorno = null;
    private AKUser user = null;
    private Long sessionId = null;

    // DAOs
    private VerlaufAbteilungDAO verlaufAbteilungDAO = null;
    private Auftrag2TechLeistungDAO auftrag2TechLeistungDAO = null;

    // Modelle
    private Verlauf verlauf = null;
    private boolean isKuendigung = false;
    private AuftragDaten auftragDaten = null;
    private VerbindungsBezeichnung verbindungsBezeichnung = null;

    // Services
    BAService baService;
    CCAuftragService ccAuftragService;
    CPSService cpsService;
    ExtServiceProviderService extServiceProviderService;
    NiederlassungService niederlassungService;
    RegistryService registryService;
    FFMService ffmService;

    // Sonstiges
    private ResourceReader resourceReader = new ResourceReader(
            "de.augustakom.hurrican.service.cc.resources.BAVerlauf");
    private AKWarnings warnings = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    @CcTxRequired
    public Object execute() throws Exception {
        init();
        checkValues();
        loadData();
        checkStatus();
        storno();

        return warnings;
    }

    private void init() throws ServiceNotFoundException {
        baService = getCCService(BAService.class);
        cpsService = getCCService(CPSService.class);
        ccAuftragService = getCCService(CCAuftragService.class);
        extServiceProviderService = getCCService(ExtServiceProviderService.class);
        niederlassungService = getCCService(NiederlassungService.class);
        registryService = getCCService(RegistryService.class);
        ffmService = getCCService(FFMService.class);
    }

    /**
     * Storniert den Verlauf.
     */
    private void storno() throws StoreException {
        try {

            if (getCCService(InnenauftragService.class).hasOpenBudget(auftragDaten.getAuftragId())) {
                throw new IllegalStateException(
                        "Der Auftrag besitzt noch ein offenes Budget. Budget muss erst geschlossen werden.");
            }

            cancelCPSTx();
            deleteFfmOrder();

            // Bemerkung mit Hinweis, wann und von wem der Verlauf storniert wurde.
            StringBuilder stornoText = new StringBuilder(resourceReader.getValue("verlauf.storno",
                    new Object[] { DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME), user.getName() }));
            if (StringUtils.isNotBlank(verlauf.getBemerkung())) {
                stornoText.append(SystemUtils.LINE_SEPARATOR);
                stornoText.append(verlauf.getBemerkung());
            }
            verlauf.setBemerkung(stornoText.toString());

            // Verlaufs-Status auf 'storniert'
            long auftragStatus = (verlauf.getStatusIdAlt() != null) ? verlauf.getStatusIdAlt().longValue() : -1;
            verlauf.setVerlaufStatusId((auftragStatus < AuftragStatus.KUENDIGUNG.longValue())
                    ? VerlaufStatus.VERLAUF_STORNIERT : VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT);
            verlauf.setAkt(Boolean.FALSE);
            getVerlaufDAO().store(verlauf);

            // Storniere Verlauf-Abteilung
            if (BooleanTools.nullToFalse(vaStorno)) {
                List<VerlaufAbteilung> vas = baService.findVerlaufAbteilungen(verlauf.getId());
                if (CollectionTools.isNotEmpty(vas)) {
                    Date now = new Date();
                    for (VerlaufAbteilung va : vas) {
                        if ((va != null) && (NumberTools.equal(va.getAbteilungId(), Abteilung.DISPO)
                                || NumberTools.equal(va.getAbteilungId(), Abteilung.NP))) {
                            va.setDatumErledigt(now);
                            va.setAusgetragenAm(now);
                            va.setAusgetragenVon(user.getName());
                            va.setVerlaufStatusId(VerlaufStatus.VERLAUF_STORNIERT);
                            getVerlaufAbteilungDAO().store(va);
                        }
                        else if ((va != null) && NumberTools.equal(va.getAbteilungId(), Abteilung.EXTERN)) {
                            stornoExtern(va);
                        }
                    }
                }
            }

            removeJoin2AuftragTechLeistungen();
            removeJoin2Physikuebernahme();
            resetDNLeistungen();

            if (NumberTools.equal(verlauf.getAnlass(), BAVerlaufAnlass.KUENDIGUNG)) {
                // Rangierungsfreigabe auf 01.01.2200 setzen, um Rangierung nicht aus Versehen
                // freizugeben, obwohl Auftrag noch aktiv
                manipulateFreigabedatum(verlauf, DateTools.getHurricanEndDate());
            }

            changeOrderStates();
            sendInfoMailIntern(baService);

            // Statusmeldung an Taifun senden
            changeTaifunAuftragStatus(verlauf, sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Stornieren des Verlaufs ist ein Fehler aufgetreten!", e);
        }
    }

    /**
     * Setzt den Status der betroffenen Auftraege auf den Ursprungs-Status zurueck, den sie VOR dem Bauauftrag hatten.
     */
    void changeOrderStates() throws StoreException, FindException {
        Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
        for (Long orderId : orderIds) {
            // Auftrags-Status zurueck setzen
            AuftragDaten auftragDatenToChange = ccAuftragService.findAuftragDatenByAuftragIdTx(orderId);
            Long statusIdAlt = verlauf.getStatusIdAlt();
            auftragDatenToChange.setStatusId(statusIdAlt);
            ccAuftragService.saveAuftragDaten(auftragDatenToChange, false);
        }
    }

    /* Externen Partner ueber Storno informieren, sofern eMail Adresse vorhanden! */
    private void stornoExtern(VerlaufAbteilung verlaufAbteilung) throws FindException, ServiceNotFoundException {
        ExtServiceProvider externPatner = extServiceProviderService
                .findById(verlaufAbteilung.getExtServiceProviderId());
        if ((externPatner != null) && StringUtils.isNotBlank(externPatner.getEmail())) {
            // Email an externen Partner senden
            extServiceProviderService.sendStornoEmail(verlaufId, sessionId);
            StringBuilder txt = new StringBuilder(resourceReader.getValue("verlauf.storno.bemerkung",
                    new Object[] { DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME) }));
            if (StringUtils.isNotBlank(verlaufAbteilung.getBemerkung())) {
                txt.append(SystemUtils.LINE_SEPARATOR);
                txt.append(verlaufAbteilung.getBemerkung());
            }
            verlaufAbteilung.setBemerkung(txt.toString());
            getVerlaufAbteilungDAO().store(verlaufAbteilung);
        }
    }

    /* EMail mit Storno-Info an interne Abteilungen senden. */
    private void sendInfoMailIntern(BAService baService) {
        try {
            if (BooleanTools.nullToFalse(sendMail)) {
                String emailDef = null;
                // Ermittle Email-Adresse von verteilender Abteilung/Niederlassung
                VerlaufAbteilung va = baService.findVAOfVerteilungsAbt(verlauf.getId());
                if ((va != null) && (va.getNiederlassungId() != null)) {
                    Niederlassung niederlassung = niederlassungService.findNiederlassung(va.getNiederlassungId());
                    emailDef = (niederlassung != null) ? niederlassung.getDispoTeampostfach() : null;
                }
                if (StringUtils.isBlank(emailDef)) {
                    // Falls EMail-Adresse leer, verwende Email-Adresse aus Registry
                    emailDef = registryService.getStringValue(RegistryService.REGID_EMAILS_DISPO);
                }

                String[] emails = StringUtils.split(emailDef, HurricanConstants.EMAIL_SEPARATOR);
                if ((emails != null) && (emails.length > 0)) {
                    StringBuilder msg = new StringBuilder("Der Auftrag ");
                    msg.append(verlauf.getAuftragId());
                    if (verbindungsBezeichnung != null) {
                        msg.append(" mit der Verbindungsbezeichnung ").append(verbindungsBezeichnung.getVbz());
                        msg.append(" und Realisierungstermin ");
                    }
                    else {
                        msg.append(" mit Realisierungstermin ");
                    }
                    msg.append(DateTools.formatDate(verlauf.getRealisierungstermin(), DateTools.PATTERN_DAY_MONTH_YEAR));
                    msg.append(" wurde veraendert oder storniert.\nBitte ueberpruefen!");

                    // Mail senden
                    SimpleMailMessage mailMsg = new SimpleMailMessage();
                    mailMsg.setFrom("Hurrican");
                    mailMsg.setTo(emails);
                    mailMsg.setSubject("Aenderung eines Bauauftrags");
                    mailMsg.setText(msg.toString());
                    getMailSender().send(mailMsg);
                }
                else {
                    String msg = "Es ist keine EMail-Adresse definiert, an die die Storno-Benachrichtigung verschickt werden soll!";
                    LOGGER.warn(msg);
                    warnings = new AKWarnings();
                    warnings.addAKWarning(this, msg);
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (warnings == null) {
                warnings = new AKWarnings();
            }
            warnings.addAKWarning(this, "Beim Mail-Versand an Dispo ist ein nicht erwarteter Fehler aufgetreten.\n" +
                    "Bitte benachrichtigen Sie die Dispo ueber den Storno-Vorgang selbst.");
        }
    }

    /**
     * Ueberprueft, ob zu dem Verlauf eine CPS-Tx existiert und versucht, diese zu stornieren. <br> Falls die
     * Stornierung der CPS-Tx nicht funktioniert, wird eine StoreException generiert.
     */
    private void cancelCPSTx() throws StoreException {
        try {
            CPSTransactionExt ex = new CPSTransactionExt();
            ex.setVerlaufId(verlaufId);
            List<CPSTransactionExt> cpsTxs = cpsService.findCPSTransaction(ex);
            if (CollectionTools.isNotEmpty(cpsTxs)) {
                for (CPSTransactionExt cpsTx : cpsTxs) {
                    if (cpsTx.isActive()) {
                        cpsService.cancelCPSTransaction(cpsTx.getId(), sessionId);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("vorhandene CPS-Tx konnte nicht storniert werden: " + e.getMessage(), e);
        }
    }

    /**
     * Evtl. vorhandene FFM Order wird ueber {@link DeleteOrder} aus dem FFM System geloescht.
     */
    private void deleteFfmOrder() {
        ffmService.deleteOrder(verlauf);
    }

    /**
     * Entfernt die Verbindung von der Verlaufs-ID zur Physikuebernahme.
     */
    private void removeJoin2Physikuebernahme() throws StoreException {
        try {
            Set<Long> orderIds = verlauf.getAllOrderIdsOfVerlauf();
            for (Long orderId : orderIds) {
                PhysikUebernahme pu = getPhysikUebernahmeDAO().findByVerlaufId(orderId, verlauf.getId());
                if (pu != null) {
                    pu.setVerlaufId(null);
                    getPhysikUebernahmeDAO().store(pu);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die Verbindung zwischen Verlauf und PhysikUebernahme konnte " +
                    "nicht entfernt werden. Verlauf wurde nicht storniert!", e);
        }
    }

    /**
     * Entfernt die Verbindung des Verlaufs zu den Auftrags-Leistungen.
     */
    private void removeJoin2AuftragTechLeistungen() throws StoreException {
        try {
            CCLeistungsService ccls = getCCService(CCLeistungsService.class);

            List<Auftrag2TechLeistung> leistungen =
                    getAuftrag2TechLeistungDAO().findAuftragTechLeistungen4Verlauf(verlaufId);
            if (CollectionTools.isNotEmpty(leistungen)) {
                for (Auftrag2TechLeistung atl : leistungen) {
                    boolean reset = false;
                    if (NumberTools.equal(atl.getVerlaufIdReal(), verlaufId)) {
                        atl.setVerlaufIdReal(null);
                        reset = true;
                    }
                    else if (NumberTools.equal(atl.getVerlaufIdKuend(), verlaufId)) {
                        reset = true;
                        atl.setVerlaufIdKuend(null);

                        TechLeistung tl = ccls.findTechLeistung(atl.getTechLeistungId());
                        if ((!tl.isTypUnique() || isKuendigung)
                                && (!DateTools.isDateEqual(atl.getAktivVon(), atl.getAktivBis()))) {
                            // Datum nur auf 2200-01-01 setzen, wenn TechLeistung ueberlappend zulaessig
                            // bzw. wenn der zu stornierende Verlauf eine Kuendigung ist
                            atl.setAktivBis(DateTools.getHurricanEndDate());
                        }
                    }

                    if (reset) {
                        getAuftrag2TechLeistungDAO().store(atl);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die Verbindung zu den Auftrags-Leistungen konnte nicht " +
                    "entfernt werden. Verlauf wurde nicht storniert!", e);
        }
    }

    /**
     * Entfernt das Realisierungsdatum von Rufnummernleistungen. Dies wird aber nur dann ausgefuehrt, wenn folgende
     * Punkte erfuellt sind: <ul> <li>Auftrag noch nicht in Betrieb <li>EWSD-Realisierungsdatum der Rufnummernleistung
     * ist nicht gefuellt (Leistung ist also noch nicht eingerichtet) <li>AM-Realisierungsdatum der Rufnummernleistung
     * ist gleich dem Realisierungsdatum des zu stornierenden Bauauftrags. </ul>
     */
    private void resetDNLeistungen() throws StoreException {
        try {
            if (NumberTools.isLess(auftragDaten.getAuftragStatusId(), AuftragStatus.IN_BETRIEB)) {
                CCRufnummernService ccRS = getCCService(CCRufnummernService.class);
                List<Leistung2DN> dnLeistungen = ccRS.findDNLeistungen4Auftrag(auftragDaten.getAuftragId());
                if (dnLeistungen != null) {
                    for (Leistung2DN l2dn : dnLeistungen) {
                        if ((l2dn.getEwsdRealisierung() == null) &&
                                DateTools.isDateEqual(l2dn.getScvRealisierung(), verlauf.getRealisierungstermin())) {
                            l2dn.setScvRealisierung(null);
                            l2dn.setScvUserRealisierung(null);
                            ccRS.saveLeistung2DN(l2dn);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Die Rufnummernleistungen konnten nicht zurueck gesetzt werden!\n" +
                    "Verlauf wurde nicht storniert!", e);
        }
    }

    /*
     * Ueberprueft, ob der Verlauf storniert werden darf. Dies ist dann der Fall, wenn der aktuelle User aus der
     * Abteilung Dispo oder Netzplanung kommt. Bei allen anderen Usern ist dies nur moeglich, wenn - nicht an
     * FieldService, FFM oder Extern verteilt wurde - keine Technik-Abteilung den BA schon in Bearbeitung hat
     */
    void checkStatus() throws FindException {
        // Dispo / NP und IT-Service koennen einen Verlauf immer stornieren
        if (Abteilung.isDispoOrNP(user.getDepartmentId()) ||
                NumberTools.equal(user.getDepartmentId(), Abteilung.ITS)) {
            return;
        }

        if (verlauf.isVerteilt()) {
            List<VerlaufAbteilung> verlaufAbteilungen = baService.findVerlaufAbteilungen(verlauf.getId());
            CollectionUtils.filter(verlaufAbteilungen, new VerlaufAbteilungTechPredicate());
            if (CollectionTools.isNotEmpty(verlaufAbteilungen)) {
                // pruefen, ob eine Abteilung den BA schon in Bearbeitung hat
                for (VerlaufAbteilung verlaufAbteilung : verlaufAbteilungen) {
                    if (StringUtils.isNotBlank(verlaufAbteilung.getBearbeiter())) {
                        throw new FindException("Der Verlauf befindet sich bereits in Bearbeitung.\n" +
                                "Storno ist nur noch ueber DISPO bzw. Netzplanung moeglich!");
                    }
                }
            }

            CollectionUtils.filter(verlaufAbteilungen, new VerlaufAbteilungPredicate(
                    Abteilung.FIELD_SERVICE, Abteilung.EXTERN, Abteilung.FFM));
            if (CollectionTools.isNotEmpty(verlaufAbteilungen)) {
                throw new FindException("Der Verlauf ist an FieldService und/oder Extern verteilt.\n" +
                        "Storno ist nur noch ueber DISPO bzw. Netzplanung moeglich!");
            }
        }
    }

    /**
     * Laedt die benoetigten Daten.
     */
    private void loadData() throws FindException {
        try {
            setVerlauf(getVerlaufDAO().findById(verlaufId, Verlauf.class));
            if (verlauf == null) {
                throw new FindException("Der Verlaufs-Datensatz konnte nicht gefunden werden!");
            }
            else if ((verlauf.getAkt() == null) || !verlauf.getAkt().booleanValue()) {
                throw new FindException("Der Verlaufs-Datensatz ist nicht aktiv. Storno-Vorgang wird abgebrochen.");
            }

            isKuendigung = verlauf.isKuendigung();

            CCAuftragService as = getCCService(CCAuftragService.class);
            auftragDaten = as.findAuftragDatenByAuftragIdTx(verlauf.getAuftragId());
            if (auftragDaten == null) {
                throw new FindException("Die Auftrags-Daten zu dem Verlauf konnten nicht ermittelt werden. " +
                        "Storno-Vorgang wird abgebrochen.");
            }

            PhysikService ps = getCCService(PhysikService.class);
            verbindungsBezeichnung = ps.findVerbindungsBezeichnungByAuftragId(verlauf.getAuftragId());

            // User ermitteln
            AKUserService userService = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            setUser(userService.findUserBySessionId(sessionId));
            if (this.user == null) {
                throw new FindException("User kann nicht ermittelt werden. Storno-Vorgang wird abgebrochen.");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Die benötigten Daten für die Stornierung konnten nicht geladen werden.", e);
        }
    }

    /**
     * Ueberprueft, ob dem Command alle benoetigten Parameter uebergeben wurden.
     */
    private void checkValues() throws FindException {
        Object id = getPreparedValue(VERLAUF_ID);
        verlaufId = (id instanceof Long) ? (Long) id : null;
        if (verlaufId == null) {
            throw new FindException("Es wurde kein Verlauf angegeben, der storniert werden soll!");
        }

        Object sm = getPreparedValue(SEND_MAIL);
        sendMail = (sm instanceof Boolean) ? (Boolean) sm : null;
        if (sendMail == null) {
            throw new FindException(
                    "Es wurde nicht angegeben, ob die Dispo über die Stornierung benachrichtigt werden soll. " +
                            "Verlauf wurde nicht storniert!"
            );
        }

        Object va = getPreparedValue(VA_STORNO);
        vaStorno = (va instanceof Boolean) ? (Boolean) va : Boolean.TRUE;

        Object tmpSession = getPreparedValue(SESSION_ID);
        sessionId = (tmpSession instanceof Long) ? (Long) tmpSession : null;
        if (sessionId == null) {
            throw new FindException("Es wurde nicht angegeben, welcher User die Storno-Aktion ausfuehrt!");
        }
    }

    /**
     * Injected
     */
    public VerlaufAbteilungDAO getVerlaufAbteilungDAO() {
        return verlaufAbteilungDAO;
    }

    /**
     * Injected
     */
    public void setVerlaufAbteilungDAO(VerlaufAbteilungDAO verlaufAbteilungDAO) {
        this.verlaufAbteilungDAO = verlaufAbteilungDAO;
    }

    /**
     * Injected
     */
    public Auftrag2TechLeistungDAO getAuftrag2TechLeistungDAO() {
        return this.auftrag2TechLeistungDAO;
    }

    /**
     * Injected
     */
    public void setAuftrag2TechLeistungDAO(Auftrag2TechLeistungDAO auftrag2TechLeistungDAO) {
        this.auftrag2TechLeistungDAO = auftrag2TechLeistungDAO;
    }

    public void setUser(AKUser user) {
        this.user = user;
    }

    public void setVerlauf(Verlauf verlauf) {
        this.verlauf = verlauf;
    }

}
