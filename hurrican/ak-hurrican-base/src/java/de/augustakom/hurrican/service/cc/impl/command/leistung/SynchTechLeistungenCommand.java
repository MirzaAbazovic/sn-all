/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2006 13:23:17
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Command-Klasse, um die Leistungen eines Auftrags zwischen dem Billing-System und Hurrican abzugleichen. <br> Ablauf
 * fuer die Synchronisation: <ul> <li>aktive Positionen/Leistungen vom Billing-System ermitteln <li>hinzuzufuegende
 * Leistungen ermitteln <li>zu entfernende Leistungen ermitteln <li>Commands fuer die Zugangs- und Abgangs-Leistungen
 * aufrufen (falls angegeben) </ul> Sollte die Menge fuer eine Leistung >1 sein, wird pro Anzahl ein eigener Datensatz
 * erzeugt. Dies wurde so implementiert, damit spaeter evtl. Daten an die einzelnen Positionen angehaengt werden
 * koennen.
 *
 *
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.SynchTechLeistungenCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SynchTechLeistungenCommand extends AbstractLeistungCommand {

    private static final Logger LOGGER = Logger.getLogger(SynchTechLeistungenCommand.class);

    public static final String KEY_PROD_ID = "prod.id";
    public static final String KEY_LEISTUNG_DIFF = "leistung.diff";
    public static final String KEY_VERLAUF_ID = "verlauf.id";
    public static final String KEY_EXECUTE_LS_COMMANDS = "execute.commands";
    public static final String KEY_REAL_DATE = "real.date";
    public static final String KEY_AUFTRAG_AKTION = "auftrag.aktion";

    private CCLeistungsService ccls = null;
    private ProfileService profileService = null;

    private Long auftragId = null;
    private List<LeistungsDiffView> diff;
    private Long prodId = null;
    private Long verlaufId = null;
    private boolean executeLsCommands = false;
    private Date realisierungsTermin;
    private AuftragAktion auftragAktion = null;

    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private RangierungsService rangierungsService;

    @Autowired
    private HWService hwService;

    @Autowired
    private EndstellenService endstellenService;

    @Override
    public Object execute() throws Exception {
        try {
            ccls = getCCService(CCLeistungsService.class);
            profileService = getCCService(ProfileService.class);
            checkValues();
            synchTechLeistungen4Auftrag();

            if (verlaufId != null) {
                // Leistungen dem Verlauf zuordnen
                assignVerlaufIds2Leistungen(verlaufId, auftragId);
            }

            boolean isProfileAssignable = isProfileAssignable();

            if(isProfileAssignable && auftragId != null){
                synchProfile(auftragId);
            } else {
                if (realisierungsTermin != null && !isProfileAssignable) {
                    synchDSLAMProfile(realisierungsTermin);
                }
            }

        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Leistungen konnten auf Grund eines Fehlers " +
                    "nicht abgeglichen werden!\nFehler: " + e.getMessage(), e);
        }
        return null;
    }

    private boolean isProfileAssignable() throws FindException {
        Endstelle endstelle4Auftrag = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if(endstelle4Auftrag == null){
            return false;
        }

        Equipment equipment4Endstelle = rangierungsService.findEquipment4Endstelle(endstelle4Auftrag, false, false);
        Long bgId = (equipment4Endstelle != null ? equipment4Endstelle.getHwBaugruppenId() : null);
        final HWBaugruppe hwBaugruppe;
        if(bgId == null) {
            return false;
        }

        hwBaugruppe = hwService.findBaugruppe(bgId);
        return (hwBaugruppe != null) && BooleanTools.nullToFalse(hwBaugruppe.getHwBaugruppenTyp().isProfileAssignable());
    }

    /*
     * Synchronisiert die Auftrags-Leistungen.
     */
    final void synchTechLeistungen4Auftrag() throws StoreException {
        try {
            syncTechLeistungen(diff);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Abgleich der Leistungen des Auftrags ist ein Fehler " +
                    "aufgetreten:\n" + e.getMessage(), e);
        }
    }

    private void syncTechLeistungen(List<LeistungsDiffView> diffs) throws FindException, StoreException,
            HurricanServiceCommandException {
        if (CollectionTools.isNotEmpty(diffs)) {
            List<Auftrag2TechLeistung> toAdd = new ArrayList<>();
            List<Auftrag2TechLeistung> toRemove = new ArrayList<>();
            Long auftragAktionsId = (auftragAktion != null) ? auftragAktion.getId() : null;
            for (LeistungsDiffView diff : diffs) {
                diff.debugModel(LOGGER);

                if (diff.isZugang()) {
                    // Leistungs-Zugaenge
                    // pro Anzahl eine eigene Zuordnung
                    for (long i = 0; i < diff.getQuantity(); i++) {
                        Auftrag2TechLeistung atl = new Auftrag2TechLeistung();
                        atl.setAuftragId(diff.getAuftragId());
                        atl.setTechLeistungId(diff.getTechLeistungId());
                        atl.setQuantity(1L);
                        atl.setAktivVon(diff.getAktivVon());
                        atl.setAuftragAktionsIdAdd(auftragAktionsId);

                        if (null != diff.getAktivBis()) {
                            atl.setAktivBis(diff.getAktivBis());
                        }

                        toAdd.add(atl);
                    }
                }
                else {
                    // Leistungs-Abgaenge
                    List<Auftrag2TechLeistung> atls = ccls.findAuftrag2TechLeistungen(diff.getAuftragId(),
                            diff.getTechLeistungId(), true);
                    if (CollectionTools.isNotEmpty(atls)) {
                        // pro Anzahl eine eigene Zuordnung
                        int diffCount = (int) diff.getQuantity().longValue();
                        if (diffCount <= atls.size()) {
                            for (int i = 0; i < diffCount; i++) {
                                Auftrag2TechLeistung a2tl = atls.get(i);
                                Date aktivBis = (diff.getAktivBis() != null) ? diff.getAktivBis() : new Date();
                                a2tl.setAktivBis(aktivBis);
                                a2tl.setAuftragAktionsIdRemove(auftragAktionsId);
                                toRemove.add(a2tl);
                            }
                        }
                    }
                }
            }

            AKWarnings warnings = null;
            for (Auftrag2TechLeistung add : toAdd) {
                LOGGER.info(String.format("Synch techn. Leistungen fuer techn. Auftrag %s - add: %s",
                        add.getAuftragId(), add.getTechLeistungId()));
                ccls.saveAuftrag2TechLeistung(add);
                AKWarnings addWarnings = executeTechLeistungCommand(add.getTechLeistungId(),
                        ServiceCommand.COMMAND_TYPE_LS_ZUGANG, add.getAuftragId(), add.getAktivVon(),
                        add.getAktivBis(), getSessionId(), null);

                if ((addWarnings != null) && addWarnings.isNotEmpty()) {
                    if (warnings == null) {
                        warnings = new AKWarnings();
                    }
                    warnings.addAKWarnings(addWarnings);
                }
            }

            for (Auftrag2TechLeistung rem : toRemove) {
                LOGGER.info(String.format("Synch techn. Leistungen fuer techn. Auftrag %s - rem: %s",
                        rem.getAuftragId(), rem.getTechLeistungId()));
                ccls.saveAuftrag2TechLeistung(rem);
                AKWarnings remWarnings = executeTechLeistungCommand(rem.getTechLeistungId(),
                        ServiceCommand.COMMAND_TYPE_LS_KUENDIGUNG, rem.getAuftragId(), rem.getAktivVon(),
                        rem.getAktivBis(), getSessionId(), null);

                if ((remWarnings != null) && remWarnings.isNotEmpty()) {
                    if (warnings == null) {
                        warnings = new AKWarnings();
                    }
                    warnings.addAKWarnings(remWarnings);
                }
            }

            setWarnings(warnings);
        }
    }

    /*
     * Ordnet den Leistungen, die dem Auftrag 'auftragId' zugeordnet sind, die
     * angegebene Verlaufs-ID zu.
     * @param verlaufId
     * @param auftragId
     * @return Liste mit den IDs der technischen Leistungen, die von dem Verlauf
     * betroffen sind.
     * @throws StoreException
     */
    private List<Long> assignVerlaufIds2Leistungen(Long verlaufId, Long auftragId) throws StoreException {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf verlauf = bas.findVerlauf(verlaufId);

            List<Long> techLsIds4Verlauf = new ArrayList<>();

            // FIXME hier sollte noch ueberprueft werden, ob der zugeordnete Verlauf storniert
            //       wurde. Ist dies der Fall, muss der neue Verlauf zugeordnet werden.
            //       (Ein stornierter Verlauf kann einer Leistung noch zugeordnet sein, falls die Stornierung
            //       manuell in der DB durchgefuehrt wurde.)

            List<Auftrag2TechLeistung> techLsExist = ccls.findAuftrag2TechLeistungen(auftragId, null, false);
            if (CollectionTools.isNotEmpty(techLsExist)) {
                for (Auftrag2TechLeistung atl : techLsExist) {
                    if ((atl.getAktivBis() != null) && (atl.getAktivVon() != null) && DateTools.isDateEqual(atl.getAktivVon(), atl.getAktivBis())) {
                        // Bei einmaligen Leistungen ist verlaufIdKuend und verlaufIdReal identisch
                        atl.setVerlaufIdKuend(verlaufId);
                        atl.setVerlaufIdReal(verlaufId);
                        atl.setAktivVon(verlauf.getRealisierungstermin());
                        atl.setAktivBis(verlauf.getRealisierungstermin());

                        techLsIds4Verlauf.add(atl.getTechLeistungId());
                    }
                    else if ((atl.getAktivBis() == null) && (atl.getVerlaufIdReal() == null)) {
                        atl.setVerlaufIdReal(verlaufId);
                        atl.setAktivVon(verlauf.getRealisierungstermin());
                        ccls.saveAuftrag2TechLeistung(atl);

                        techLsIds4Verlauf.add(atl.getTechLeistungId());
                    }
                    else if ((atl.getAktivBis() != null) && (atl.getVerlaufIdKuend() == null)) {
                        atl.setVerlaufIdKuend(verlaufId);
                        atl.setAktivBis(verlauf.getRealisierungstermin());
                        ccls.saveAuftrag2TechLeistung(atl);

                        techLsIds4Verlauf.add(atl.getTechLeistungId());
                    }
                }
            }
            return techLsIds4Verlauf;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Bei der Zuordnung der Verlaufs-ID zu den Leistungen " +
                    "ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /*
     * Versucht das DSLAM-Profil des Auftrags mit den Leistungen abzugleichen. <br>
     * (Evtl. koennten hier zuerst die vom Verlauf betroffenen Leistungen ermittelt
     * werden. Dann koennte der Profil-Abgleich nur dann erfolgen, wenn auch
     * Profil-relevante Leistungen geaendert wurden.)
     *
     */
    private void synchDSLAMProfile(Date changeDate) throws StoreException {
        try {
            // pruefen, ob zu dem Auftrag/Produkt ein DSLAM-Profil ermittelt werden muss
            // und nur dann das Profil ermitteln lassen.
            DSLAMService dslamService = getCCService(DSLAMService.class);
            List<DSLAMProfile> profiles = dslamService.findDSLAMProfiles4Produkt(prodId);
            if (CollectionTools.isNotEmpty(profiles)) {
                DSLAMProfile assigned = dslamService.assignDSLAMProfile(auftragId, changeDate, auftragAktion, getSessionId());
                if (assigned == null) {
                    // Exception, falls kein Profil zugeordnet wurde
                    throw new StoreException("DSLAM-Profil konnte nicht ermittelt werden!");
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "DSLAM-Profil konnte nicht zugeordnet werden: " + e.getMessage());
        }
    }

    final void synchProfile(long auftragId) {
        try {
            ProfileAuftrag currentAuftrag = profileService.findNewestProfileAuftrag(auftragId);
            if (currentAuftrag == null) {
                ProfileAuftrag auftrag = profileService.createNewProfile(auftragId, getSessionId());
                DSLAMProfileChangeReason changeReason = profileService.
                        getChangeReasonById(DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT);
                auftrag.setChangeReason(changeReason);
                profileService.persistProfileAuftrag(auftrag);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addWarning(this, "Profil konnte nicht zugeordnet werden: " + e.getMessage());
        }
    }

    /*
     * Ermittelt die zur techn. Leistung konfigurierten Commands und fuehrt diese aus.
     * @param techLeistungId ID der technischen Leistung
     * @param commandTyp Typ der auszufuehrenden Commands ('null' fuer alle)
     * @param ccAuftragId Auftrags-ID
     * @param aktivVon Datum, ab dem die techn. Leistung aktiv sein soll
     * @param aktivBis Datum, bis zu dem die techn. Leistung aktiv sein soll
     * @param sessionId Session-ID des Users
     * @param serviceCallback das ServiceCallback-Objekt fuer User-Benachrichtigungen
     * @throws HurricanServiceCommandException
     *
     */
    private AKWarnings executeTechLeistungCommand(Long techLeistungId, String commandTyp, Long ccAuftragId,
            Date aktivVon, Date aktivBis, Long sessionId, IServiceCallback serviceCallback)
            throws HurricanServiceCommandException {
        if (!executeLsCommands) {
            return null;
        }

        try {
            ChainService cs = getCCService(ChainService.class);
            List<ServiceCommand> commands =
                    cs.findServiceCommands4Reference(techLeistungId, TechLeistung.class, commandTyp);
            if (CollectionTools.isNotEmpty(commands)) {
                AKServiceCommandChain chain = createAkServiceCommandChain();
                for (ServiceCommand cmd : commands) {
                    IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                    if (serviceCmd == null) {
                        throw new HurricanServiceCommandException("Fuer das definierte ServiceCommand " + cmd.getName() +
                                " konnte kein Objekt geladen werden!\nLeistungsabgleich konnte nicht durchgefuehrt werden!");
                    }

                    serviceCmd.prepare(AbstractLeistungCommand.KEY_AUFTRAG_ID, ccAuftragId);
                    serviceCmd.prepare(AbstractLeistungCommand.KEY_SESSION_ID, sessionId);
                    serviceCmd.prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_ID, techLeistungId);
                    serviceCmd.prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_AKTIV_VON, aktivVon);
                    serviceCmd.prepare(AbstractLeistungCommand.KEY_TECH_LEISTUNG_AKTIV_BIS, aktivBis);
                    serviceCmd.prepare(AbstractLeistungCommand.KEY_SERVICE_CALLBACK, serviceCallback);
                    chain.addCommand(serviceCmd);
                }

                chain.executeChain();
                if (chain.getWarnings() != null) {
                    LOGGER.warn(chain.getWarnings().getWarningsAsText());
                }
                return chain.getWarnings();
            }
            return null;
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Bei der Ausfuehrung der Commands zum Leistungsabgleich " +
                    "ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    AKServiceCommandChain createAkServiceCommandChain() {
        return new AKServiceCommandChain();
    }

    /* Ueberprueft die Parameter, die dem Command uebergeben wurden. */
    @SuppressWarnings("unchecked")
    final void checkValues() throws FindException {
        auftragId = getPreparedValue(KEY_AUFTRAG_ID, Long.class, false,
                "Es wurde kein Auftrag fuer den Leistungsabgleich angegeben!");

        diff = getPreparedValue(KEY_LEISTUNG_DIFF, List.class, false, "Leistungsdiff wurde nicht definiert!");

        prodId = getPreparedValue(KEY_PROD_ID, Long.class, false, "Produkt-ID wurde nicht definiert!");

        Object verlId = getPreparedValue(KEY_VERLAUF_ID);
        verlaufId = (verlId instanceof Long) ? (Long) verlId : null;

        Object execute = getPreparedValue(KEY_EXECUTE_LS_COMMANDS);
        executeLsCommands = (execute instanceof Boolean) && (Boolean) execute;

        Object realTermin = getPreparedValue(KEY_REAL_DATE);
        realisierungsTermin = (realTermin instanceof Date) ? (Date) realTermin : null;

        auftragAktion = getPreparedValue(KEY_AUFTRAG_AKTION, AuftragAktion.class, true, null);
    }
}


