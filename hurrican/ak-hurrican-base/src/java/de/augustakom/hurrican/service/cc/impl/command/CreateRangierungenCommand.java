/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.10.2007 09:16:51
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import java.util.regex.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.Lager;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.innenauftrag.IABudget;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterial;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahme;
import de.augustakom.hurrican.model.cc.innenauftrag.IAMaterialEntnahmeArtikel;
import de.augustakom.hurrican.model.cc.innenauftrag.RangierungsMaterial;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Command-Klasse, um zu einem Rangierungs-Auftrag (RA) die Rangierungen aus Equipment-Datensaetzen zu generieren. <br>
 * Der Ablauf ist wie folgt: <br> - Status vom RA pruefen (darf noch nicht bearbeitet sein) <br> - ueber den RA pruefen,
 * welche Equipments notwendig sind  <br> - pruefen, ob alle notwendigen Equipments definiert sind  <br> - pruefen, ob
 * die Anzahl der Rangierungen i.O. ist (8er Gruppen) <br> - falls Carrier-Equipments definiert, pruefen, ob RangSSType
 * u. UETV definiert sind  <br> - Rangierungen erstellen und Status der Equipments aendern (evtl. auch RangSSType u.
 * UETV aendern)  <br> - Budget generieren, falls Baugruppe noch nicht eingebaut ist  <br> - RA auf bearbeitet setzen
 * <br> <br><br> Die nachfolgende Matrix stellt dar, an welcher Position die einzelnen Equipments verwendet werden.
 * (Dies ist immer abhaengig davon, welche Equipment-Listen uebergeben wurden.) <br> | Equipment-Liste:  |  Carrier  |
 * EWSD    |  DSLAM-IN  |  DSLAM-OUT  |  Produkt (Bsp.)  | <br> |------------------------------------------------------------------------------------------|
 * <br> |                   | R1.EQ_OUT |  R2.EQ_IN  |  R2.EQ_OUT |  R1.EQ_IN   |  ADSL+Phone      | <br>
 * |------------------------------------------------------------------------------------------| <br> | | R1.EQ_OUT |
 * R1.EQ_IN  |    xxx     |    xxx      |  S0 od. a/b      | <br> |------------------------------------------------------------------------------------------|
 * <br> |                   | R1.EQ_OUT |    xxx     |    xxx     |  R1.EQ-IN   |  ADSL only       | <br>
 * |------------------------------------------------------------------------------------------| <br> | | R1.EQ_OUT | xxx
 *     |    xxx     |  R1.EQ-IN   |  SDSL            | <br> |------------------------------------------------------------------------------------------|
 * <br> <br>
 *
 *
 */
@CcTxRequired
public class CreateRangierungenCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateRangierungenCommand.class);

    private RangierungDAO rangierungDAO = null;

    public static final String KEY_SESSION_ID = "user.session.id";
    public static final String KEY_RANGIERUNGSAUFTRAG_ID = "ra.id";
    public static final String KEY_EQ_IDS_EWSD = "eq.ids.ewsd";
    public static final String KEY_EQ_IDS_CARRIER = "eq.ids.carrier";
    public static final String KEY_EQ_IDS_DSLAM_IN = "eq.ids.dslam.in";
    public static final String KEY_EQ_IDS_DSLAM_OUT = "eq.ids.dslam.out";
    public static final String KEY_RANG_SS_TYPE = "rang.ss.type";
    public static final String KEY_UETV = "uetv";
    public static final String KEY_IA_NUMBER = "ia.number";

    private Long sessionId = null;
    private AKUser user = null;
    private Long raId = null;
    private RangierungsAuftrag rangierungsAuftrag = null;
    private List<Long> eqIdsEWSD = null;
    private List<Long> eqIdsCarrier = null;
    private List<Long> eqIdsDSLAMIn = null;
    private List<Long> eqIdsDSLAMOut = null;
    private String rangSSType = null;
    private Uebertragungsverfahren uetv = null;
    private String iaNumber = null;

    private List<Equipment> usedEquipments = null;
    private AKWarnings warnings = null;

    private int rangierungsCount = 0;

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            usedEquipments = null;
            warnings = null;
            rangierungsCount = 0;

            warnings = new AKWarnings();
            loadRequiredData();

            // Checks durchfuehren
            checkRangierungsAuftrag();
            checkDefinedEquipments();

            // Rangierungern erzeugen
            createRangierungen();
            checkRangierungsCount();

            setBGBuiltIn();

            // Status vom Rangierungs-Auftrag aendern
            changeRAStatus();

            AKMessages messages = new AKMessages();
            messages.addAKMessages(warnings);
            return messages;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler beim Aufbau der Rangierungen:\n" + e.getMessage(), e);
        }
    }

    /*
     * Erstellt aus den uebergebenen Equipments die notwendigen Rangierungen.
     */
    private void createRangierungen() throws ServiceCommandException {
        try {
            usedEquipments = new ArrayList<>();
            RangierungsService rangSrv = getCCService(RangierungsService.class);
            if (CollectionTools.isNotEmpty(eqIdsDSLAMIn)) {
                // 2 Rangierungen notwendig
                for (int i = 0; i < eqIdsCarrier.size(); i++) {   // Carrier-Stifte sind immer notwendig
                    // naechsten Wert von LTG_GES_ID ermitteln
                    int ltgGesId = getRangierungDAO().getNextLtgGesId();

                    Equipment eqOut = getEquipment(eqIdsCarrier.get(i));
                    Equipment eqIn = getEquipment(eqIdsDSLAMOut.get(i));
                    Equipment eqOutAdd = getEquipment(eqIdsDSLAMIn.get(i));
                    Equipment eqInAdd = getEquipment(eqIdsEWSD.get(i));
                    CollectionUtils.addAll(usedEquipments, new Equipment[] { eqOut, eqIn, eqOutAdd, eqInAdd });

                    if ((eqOut == null) || (eqIn == null) || (eqOutAdd == null) || (eqInAdd == null)) {
                        throw new HurricanServiceCommandException(
                                "Es konnten nicht alle notwendigen Equipment-Objekte geladen werden!");
                    }

                    Rangierung rangParent = createDefaultRangierung(rangierungsAuftrag.getPhysiktypParent());
                    rangParent.setEqInId(eqIn.getId());
                    rangParent.setEqOutId(eqOut.getId());
                    rangParent.setLeitungGesamtId(ltgGesId);
                    rangParent.setLeitungLfdNr(1);
                    rangSrv.saveRangierung(rangParent, false);

                    Rangierung rangChild = createDefaultRangierung(rangierungsAuftrag.getPhysiktypChild());
                    rangChild.setEqInId(eqInAdd.getId());
                    rangChild.setEqOutId(eqOutAdd.getId());
                    rangChild.setLeitungGesamtId(ltgGesId);
                    rangChild.setLeitungLfdNr(2);
                    rangSrv.saveRangierung(rangChild, false);

                    modifyEquipment(rangSrv, eqOut, EqStatus.vorb, rangSSType, uetv);
                    modifyEquipment(rangSrv, eqIn, EqStatus.vorb, null, null);
                    modifyEquipment(rangSrv, eqOutAdd, EqStatus.vorb, null, null);
                    modifyEquipment(rangSrv, eqInAdd, EqStatus.vorb, null, null);

                    rangierungsCount++;
                }
            }
            else {
                if (!Pattern.matches(Equipment.RANG_SS_FTTX_REG_EXP, rangSSType)) {
                    // 1 Rangierung notwendig
                    for (int i = 0; i < eqIdsCarrier.size(); i++) {
                        Equipment eqOut = getEquipment(eqIdsCarrier.get(i));
                        if (eqOut == null) {
                            throw new HurricanServiceCommandException(
                                    "Das Equipment-Objekt fuer die Out-Seite konnte nicht geladen werden!");
                        }
                        usedEquipments.add(eqOut);

                        Long eqInId = null;
                        if (CollectionTools.isNotEmpty(eqIdsDSLAMOut)) {
                            eqInId = eqIdsDSLAMOut.get(i);
                        }
                        else if (CollectionTools.isNotEmpty(eqIdsEWSD)) {
                            eqInId = eqIdsEWSD.get(i);
                        }

                        Equipment eqIn = (eqInId != null) ? getEquipment(eqInId) : null;
                        if ((eqInId != null) && (eqIn == null)) {
                            throw new HurricanServiceCommandException(
                                    "Das Equipment-Objekt fuer die In-Seite konnte geladen werden!");
                        }

                        Rangierung rangierung = createDefaultRangierung(rangierungsAuftrag.getPhysiktypParent());
                        rangierung.setEqOutId(eqOut.getId());
                        if (eqIn != null) {
                            rangierung.setEqInId(eqIn.getId());
                            usedEquipments.add(eqIn);
                        }
                        rangSrv.saveRangierung(rangierung, false);

                        modifyEquipment(rangSrv, eqOut, EqStatus.vorb, rangSSType, uetv);
                        modifyEquipment(rangSrv, eqIn, EqStatus.vorb, null, null);

                        rangierungsCount++;
                    }
                }
                else {
                    // FTTX --> 1 Rangierung - nur EQ-IN Port
                    for (int i = 0; i < eqIdsDSLAMOut.size(); i++) {
                        // bei FTTX werden nur die EQ-OUTs geliefert - entsprechen aber EQ_IN
                        Equipment eqIn = getEquipment(eqIdsDSLAMOut.get(i));
                        if (eqIn == null) {
                            throw new HurricanServiceCommandException(
                                    "Das Equipment-Objekt fuer die IN-Seite konnte nicht geladen werden!");
                        }
                        usedEquipments.add(eqIn);

                        Rangierung rangierung = createDefaultRangierung(rangierungsAuftrag.getPhysiktypParent());
                        rangierung.setEqInId(eqIn.getId());
                        rangSrv.saveRangierung(rangierung, false);

                        modifyEquipment(rangSrv, eqIn, EqStatus.vorb, rangSSType, uetv);
                        rangierungsCount++;
                    }
                }
            }
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Fehler beim Aufbau der Rangierungen: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft die Anzahl der erstellten Rangierungen mit der Vorgabe. */
    private void checkRangierungsCount() throws ServiceCommandException {
        if (rangierungsAuftrag.getAnzahlPorts() != rangierungsCount) {
            throw new HurricanServiceCommandException(
                    "Die Anzahl der generierten Rangierungen stimmte nicht mit der Vorgabe ueberein.\n" +
                            "Anzahl erwartet: " + rangierungsAuftrag.getAnzahlPorts() +
                            "\nAnzahl rangiert: " + rangierungsCount +
                            "\nRollback wurde veranlasst."
            );
        }
    }

    /* Setzt den Status vom Rangierungs-Auftrag auf 'definiert'. */
    private void changeRAStatus() throws ServiceCommandException {
        try {
            rangierungsAuftrag.setDefiniertAm(new Date());
            rangierungsAuftrag.setDefiniertVon(user.getNameAndFirstName());

            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            ras.saveRangierungsAuftrag(rangierungsAuftrag);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Der Status vom Rangierungs-Auftrag konnte nicht geaendert werden: " + e.getMessage(), e);
        }
    }

    /*
     * Aendert einige Parameter des angegebenen Equipments. <br>
     * Die Parameter 'rangSSType' und 'uetv' sind optional.
     */
    private void modifyEquipment(RangierungsService rs, Equipment eqToModify, EqStatus status, String rangSSType, Uebertragungsverfahren uetv) throws Exception {
        if (eqToModify != null) {
            eqToModify.setStatus(status);
            eqToModify.setGueltigVon(new Date());
            eqToModify.setGueltigBis(DateTools.getHurricanEndDate());
            if (StringUtils.isNotBlank(rangSSType)) {
                eqToModify.setRangSSType(rangSSType);
            }
            if (uetv != null) {
                eqToModify.setUetv(uetv);
            }

            rs.saveEquipment(eqToModify);
        }
    }

    /* Ermittelt das Equipment-Objekt mit der angegebenen ID */
    private Equipment getEquipment(Long eqId) throws Exception {
        RangierungsService rs = getCCService(RangierungsService.class);
        return rs.findEquipment(eqId);
    }

    /* Erstellt ein Default Rangierungs-Objekt mit dem angegebenen Physiktyp. */
    private Rangierung createDefaultRangierung(Long physiktyp) {
        Rangierung rangierung = new Rangierung();
        rangierung.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
        rangierung.setPhysikTypId(physiktyp);
        rangierung.setFreigegeben(Freigegeben.in_Aufbau);
        rangierung.setRangierungsAuftragId(rangierungsAuftrag.getId());
        rangierung.setGueltigVon(new Date());
        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
        rangierung.setUserW(user.getLoginName());

        return rangierung;
    }

    /*
     * Ueberprueft von allen verbauten Equipments, ob die Baugruppe eingebaut ist.
     * Ist dies nicht der Fall, wird die Baugruppe auf 'eingebaut' gesetzt und
     * ein entsprechendes Budget dazu erstellt.
     */
    private void setBGBuiltIn() throws ServiceCommandException {
        try {
            if (usedEquipments != null) {
                Map<Long, Object> bgIds = new HashMap<>();
                for (Equipment usedEq : usedEquipments) {
                    // Baugruppen-IDs der Equipments ermitteln
                    if (usedEq.getHwBaugruppenId() != null) {
                        bgIds.put(usedEq.getHwBaugruppenId(), null);
                    }
                }

                HWService hws = getCCService(HWService.class);
                Map<Long, HWBaugruppe> bgsToInstall = new HashMap<>();
                Iterator<Long> bgIdIterator = bgIds.keySet().iterator();
                while (bgIdIterator.hasNext()) {
                    // Baugruppe ermitteln und pruefen, ob sie schon eingebaut ist.
                    // Falls nicht, die Baugruppe merken und anschliessend ein Budget erstellen
                    Long bgId = bgIdIterator.next();
                    HWBaugruppe hwBG = hws.findBaugruppe(bgId);
                    if (!BooleanTools.nullToFalse(hwBG.getEingebaut())) {
                        bgsToInstall.put(hwBG.getId(), hwBG);
                    }
                }

                if (!bgsToInstall.isEmpty()) {
                    RangierungsService rs = getCCService(RangierungsService.class);
                    List<HWBaugruppe> hwBGToInstall = new ArrayList<>();

                    // zu den einzubauenden Baugruppen die Equipments ermitteln und
                    // diese auf 'eingebaut' setzen
                    Iterator<Long> bgIt = bgsToInstall.keySet().iterator();
                    while (bgIt.hasNext()) {
                        Long bgId = bgIt.next();
                        HWBaugruppe hwBaugruppe = bgsToInstall.get(bgId);
                        hwBGToInstall.add(hwBaugruppe);

                        List<Equipment> eqs = rs.findEquipments4HWBaugruppe(bgId);
                        if (CollectionTools.isEmpty(eqs)) {
                            throw new HurricanServiceCommandException(
                                    "Es konnten keine Equipments zur Baugruppe " + bgId + " ermittelt werden!");
                        }

                        // Baugruppe als 'eingebaut' markieren
                        hwBaugruppe.setEingebaut(Boolean.TRUE);
                        hws.saveHWBaugruppe(hwBaugruppe);
                    }

                    // Budget fuer die Baugruppen erstellen
                    createBudget(hwBGToInstall);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Die Baugruppe(n) konnte(n) nicht auf <eingebaut> gesetzt werden: " + e.getMessage(), e);
        }
    }

    /**
     * Erstellt zu dem aktuellen Rangierungs-Auftrag einen Innenauftrag inkl. Budget und Materialliste. <br> Die
     * Materialliste wird an Hand der einzubauenden Baugruppen ermittelt.
     *
     * @param hwBaugruppen Liste mit den Baugruppen, fuer deren Einbau ein Budget erstellt werden soll
     */
    private void createBudget(List<HWBaugruppe> hwBaugruppen) throws ServiceCommandException {
        try {
            if (CollectionTools.isNotEmpty(hwBaugruppen)) {
                InnenauftragService ias = getCCService(InnenauftragService.class);
                HWService hwService = getCCService(HWService.class);

                // alle notwendigen Materialien ermitteln
                List<IAMaterialEntnahmeArtikel> necessaryArticles = new ArrayList<>();
                float budget = 0f;
                for (HWBaugruppe hwBG : hwBaugruppen) {
                    HWRack rack = hwService.findRackById(hwBG.getRackId());

                    RangierungsMaterial example = new RangierungsMaterial();
                    example.setGueltigBis(DateTools.getHurricanEndDate());
                    example.setHwBgTypName(hwBG.getHwBaugruppenTyp().getName());

                    List<RangierungsMaterial> rms =
                            getRangierungDAO().queryByExample(example, RangierungsMaterial.class);
                    if (CollectionTools.isNotEmpty(rms)) {
                        for (RangierungsMaterial rm : rms) {
                            IAMaterial material = ias.findMaterial(rm.getMaterialNr());
                            if (material != null) {
                                // Materialentnahme-Artikel inkl. Anlagenbezeichnung (von Rack) aufbauen
                                IAMaterialEntnahmeArtikel entArticle = new IAMaterialEntnahmeArtikel();
                                entArticle.setAnzahl((float) 1);
                                entArticle.setArtikel(material.getArtikel());
                                entArticle.setMaterialNr(material.getMaterialNr());
                                entArticle.setEinzelpreis(material.getEinzelpreis());
                                entArticle.setAnlagenBez((rack != null) ? rack.getAnlagenBez() : null);

                                necessaryArticles.add(entArticle);

                                if (material.getEinzelpreis() != null) {
                                    budget += material.getEinzelpreis();
                                }
                            }
                            else {
                                warnings.addAKWarning(this, "Material <" + rm.getMaterialNr() +
                                        "> konnte nicht ermittelt werden. Materialliste bitte kontrollieren.");
                            }
                        }
                    }
                }

                // Budget wie folgt aendern: Budget auf 100 aufrunden + 1000 (Vorgabe von Netzplanung)
                int budgetRounded = NumberTools.roundToNextHundred(budget);
                budgetRounded += 1000;

                // HVT zum Rangierungs-Auftrag ermitteln
                HVTService hvts = getCCService(HVTService.class);
                HVTGruppe hvtGruppe = hvts.findHVTGruppe4Standort(rangierungsAuftrag.getHvtStandortId());
                if (hvtGruppe == null) {
                    throw new HurricanServiceCommandException("Die HVT-Gruppe konnte nicht ermittelt werden!");
                }

                if (CollectionTools.isNotEmpty(necessaryArticles)) {
                    if (StringUtils.isBlank(iaNumber)) {
                        throw new HurricanServiceCommandException(
                                "Es ist keine Innenauftragsnummer definiert! (Notwendig wg. Baugruppen-Einbau)");
                    }

                    IA ia = ias.findIA4RangierungsAuftrag(rangierungsAuftrag.getId());
                    if (ia == null) {
                        ia = new IA();
                        ia.setRangierungsAuftragId(rangierungsAuftrag.getId());
                        ia.setIaNummer(iaNumber);
                        ia.setProjectLeadId(user.getId());
                        ias.saveIA(ia);
                    }

                    IABudget iaBudget = new IABudget();
                    iaBudget.setIaId(ia.getId());
                    iaBudget.setCreatedAt(new Date());
                    iaBudget.setBudget((float) budgetRounded);
                    iaBudget.setBudgetUserId(user.getId());
                    iaBudget.setProjektleiter(user.getNameAndFirstName());
                    ias.saveBudget(iaBudget, null);

                    IAMaterialEntnahme matEnt = new IAMaterialEntnahme();
                    matEnt.setIaBudgetId(iaBudget.getId());
                    matEnt.setLagerId(Lager.LAGER_ID_AUGSBURG);
                    matEnt.setEntnahmetyp(IAMaterialEntnahme.TYP_RESERVATION);
                    matEnt.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
                    ias.saveMaterialEntnahme(matEnt, sessionId);

                    for (IAMaterialEntnahmeArtikel entArticle : necessaryArticles) {
                        entArticle.setMaterialEntnahmeId(matEnt.getId());
                        ias.saveMaterialEntnahmeArtikel(entArticle);
                    }
                }
                else {
                    warnings.addAKWarning(this,
                            "Budget konnte nicht erstellt werden, da keine Materialien ermittelt werden konnten!\n" +
                                    "Bitte erstellen Sie das Budget manuell."
                    );
                }
            }
        }
        catch (HurricanServiceCommandException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            warnings.addAKWarning(this,
                    "Es ist ein nicht erwarteter Fehler bei der Budget-Erstellung aufgetreten.\n" +
                            "Bitte erstellen Sie das Budget manuell."
            );
        }
    }

    /* Ueberprueft den Rangierungs-Auftrag auf Gueltigkeit */
    private void checkRangierungsAuftrag() throws ServiceCommandException {
        try {
            if (rangierungsAuftrag == null) {
                throw new HurricanServiceCommandException("Rangierungs-Auftrag konnte nicht ermittelt werden!");
            }

            if (rangierungsAuftrag.getDefiniertAm() != null) {
                throw new HurricanServiceCommandException("Der Rangierungs-Auftrag wurde bereits bearbeitet!");
            }

            // pruefen, ob zu RA schon Rangierungen existieren
            Rangierung example = new Rangierung();
            example.setRangierungsAuftragId(rangierungsAuftrag.getId());
            List<Rangierung> found = getRangierungDAO().queryByExample(example, Rangierung.class);
            if (CollectionTools.isNotEmpty(found)) {
                throw new HurricanServiceCommandException(
                        "Zu dem Rangierungs-Auftrag wurden bereits Rangierungen angelegt!");
            }
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Ueberpruefung des Rangierungs-Auftrags: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft, ob die Equipment-Daten korrekt definiert wurden. */
    private void checkDefinedEquipments() throws ServiceCommandException {
        try {
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            boolean[] validated = ras.validateNeededEquipments(rangierungsAuftrag);

            boolean useEWSD = validated[0];
            boolean useDSLAM = validated[1];
            boolean useCarrier = validated[2];

            int countEWSD = -1;
            int countDSLAMIn = -1;
            int countDSLAMOut = -1;
            int countCarrier = -1;
            List<Integer> eqCount = new ArrayList<>();

            if (useEWSD) {
                if (CollectionTools.isEmpty(eqIdsEWSD)) {
                    throw new HurricanServiceCommandException(
                            "Es wurden keine Equipments fuer die EWSD-Seite definiert!");
                }
                else {
                    countEWSD = eqIdsEWSD.size();
                    eqCount.add(countEWSD);
                }

                if (StringUtils.isBlank(rangSSType) || (uetv == null)) {
                    throw new HurricanServiceCommandException(
                            "Auf der Carrier-Seite muessen die Werte fuer Rang-SS-Type und UETV definiert werden!");
                }
            }

            if (useCarrier) {
                if (CollectionTools.isEmpty(eqIdsCarrier)) {
                    throw new HurricanServiceCommandException(
                            "Es wurden keine Equipments fuer die Carrier-Seite definiert!");
                }
                else {
                    countCarrier = eqIdsCarrier.size();
                    eqCount.add(countCarrier);
                }
            }

            if (useDSLAM) {
                if (CollectionTools.isEmpty(eqIdsDSLAMIn) && CollectionTools.isEmpty(eqIdsDSLAMOut)) {
                    throw new HurricanServiceCommandException(
                            "Es wurden keine Equipments fuer die DSLAM-Seite definiert!");
                }
                else {
                    if (eqIdsDSLAMIn != null) {
                        countDSLAMIn = eqIdsDSLAMIn.size();
                        eqCount.add(countDSLAMIn);
                    }
                    if (eqIdsDSLAMOut != null) {
                        countDSLAMOut = eqIdsDSLAMOut.size();
                        eqCount.add(countDSLAMOut);
                    }
                }
            }

            // pruefen, ob ueberall die gleiche Anzahl Equipments definiert sind
            int size = -1;
            for (Integer eqc : eqCount) {
                if (size == -1) {
                    size = eqc;
                }
                else if (size != eqc) {
                    throw new HurricanServiceCommandException(
                            "Die Anzahl der definierten Equipments stimmt nicht ueberein!");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Ueberpruefung der Equipments: " + e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        try {
            sessionId = getPreparedValue(
                    KEY_SESSION_ID, Long.class, false, "Session-ID ist nicht definiert.");
            AKUserService us = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            user = us.findUserBySessionId(sessionId);

            raId = getPreparedValue(
                    KEY_RANGIERUNGSAUFTRAG_ID, Long.class, false, "ID vom Rangierungs-Auftrag ist nicht definiert!");
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            rangierungsAuftrag = ras.findRA(raId);

            eqIdsEWSD = getPreparedValue(KEY_EQ_IDS_EWSD, List.class, true, "EWSD-Equipments ungueltig!");
            eqIdsCarrier = getPreparedValue(KEY_EQ_IDS_CARRIER, List.class, true, "Carrier-Equipments ungueltig!");
            eqIdsDSLAMIn = getPreparedValue(KEY_EQ_IDS_DSLAM_IN, List.class, true, "DSLAM-In-Equipments ungueltig!");
            eqIdsDSLAMOut = getPreparedValue(KEY_EQ_IDS_DSLAM_OUT, List.class, true, "EWSD-Out-Equipments ungueltig!");

            rangSSType = getPreparedValue(KEY_RANG_SS_TYPE, String.class, true, "Rang-SS-Type ungueltig!");
            uetv = getPreparedValue(KEY_UETV, Uebertragungsverfahren.class, true, "UETV ungueltig!");
            iaNumber = getPreparedValue(KEY_IA_NUMBER, String.class, true, null);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ueberpruefung der notwendigen Daten: " + e.getMessage(), e);
        }
    }

    /**
     * @return Returns the rangierungDAO.
     */
    public RangierungDAO getRangierungDAO() {
        return rangierungDAO;
    }

    /**
     * @param rangierungDAO The rangierungDAO to set.
     */
    public void setRangierungDAO(RangierungDAO rangierungDAO) {
        this.rangierungDAO = rangierungDAO;
    }

}


