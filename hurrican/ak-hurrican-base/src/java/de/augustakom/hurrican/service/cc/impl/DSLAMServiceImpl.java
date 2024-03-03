/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 17:04:13
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.DSLAMProfileDAO;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.DSLAMProfileMapping;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.FindNextHigherDSLAMProfile4DSL18000Command;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Implementierung von <code>DSLAMService</code>
 *
 *
 */
@CcTxRequired
public class DSLAMServiceImpl extends DefaultCCService implements DSLAMService {

    private static final Logger LOGGER = Logger.getLogger(DSLAMServiceImpl.class);

    @Resource(name = "dslamProfileDAO")
    private DSLAMProfileDAO dslamProfileDAO;

    // @Named ist notwendig, solange für die Zielservices noch zwei Instanzen (Target + Transaktionsproxy) existieren
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;
    @Resource(name = "de.mnet.common.service.locator.ServiceLocator")
    private ServiceLocator serviceLocator;

    @Override
    public List<DSLAMProfile> findDSLAMProfiles() throws FindException {
        try {
            return dslamProfileDAO.findAll(DSLAMProfile.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<DSLAMProfile> findDSLAMProfiles(String name) throws FindException {
        if (StringUtils.isBlank(name)) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            DSLAMProfile ex = new DSLAMProfile();
            ex.setName(name);

            return dslamProfileDAO.queryByExample(ex, DSLAMProfile.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<DSLAMProfile> findDSLAMProfiles4Produkt(Long prodId) throws FindException {
        try {
            return dslamProfileDAO.findDSLAMProfiles4Produkt(prodId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<DSLAMProfile> findDSLAMProfiles4Auftrag(Long auftragId) {
        return dslamProfileDAO.findDSLAMProfiles4Auftrag(auftragId);
    }

    @Override
    public DSLAMProfile calculateDefaultDSLAMProfile(Long auftragId) throws FindException {
        if (auftragId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        try {
            Produkt produkt = produktService.findProdukt4Auftrag(auftragId);

            DSLAMProfile profile2Assign = null;
            List<DSLAMProfile> profiles = (produkt != null) ? findDSLAMProfiles4Produkt(produkt.getProdId()) : Collections.emptyList();
            if (!profiles.isEmpty()) {
                Rangierung[] rangs = rangierungsService.findRangierungen(auftragId, Endstelle.ENDSTELLEN_TYP_B);
                // Hier das Uetv mit Blank vorbelegen. Sollte kein Uetv ermittelt werden koennen (z.B. keine Rangierung
                // vorhanden - Direktanschluss)
                // wird spaeter automatisch das hoechstwertige - H13 - angenommen.
                Uebertragungsverfahren uetv = null;
                HWBaugruppe hwBaugruppe = null;
                if ((rangs != null) && (rangs.length >= 1)) {
                    Equipment eqOut = rangierungsService.findEquipment(rangs[0].getEqOutId());
                    uetv = (eqOut != null) ? eqOut.getUetv() : null;

                    Equipment eqIn = rangierungsService.findEquipment(rangs[0].getEqInId());
                    hwBaugruppe = (eqIn != null) ? hwService.findBaugruppe(eqIn.getHwBaugruppenId()) : null;
                }

                Long downstreamTechLs = null;
                Long upstreamTechLs = null;
                Long fastpathTechLs = null;

                // IDs der zugeordneten techn. Leistungen ermitteln (die fuer das Profil relevant sind)
                List<TechLeistung> techLeistungen = leistungsService.findTechLeistungen4Auftrag(auftragId, null, true);
                if (CollectionTools.isNotEmpty(techLeistungen)) {
                    downstreamTechLs = TechLeistung.getTechLsId4Typ(techLeistungen, TechLeistung.TYP_DOWNSTREAM);
                    upstreamTechLs = TechLeistung.getTechLsId4Typ(techLeistungen, TechLeistung.TYP_UPSTREAM);

                    if ((downstreamTechLs != null) && (hwBaugruppe != null)
                            && (hwBaugruppe.getHwBaugruppenTyp().getMaxBandwidth() != null)) {
                        // falls Downstream vom Auftrag groesser als die max. Bandwidth der Baugruppe, so wird als DownstreamTechLs die
                        // technische Leistung zur max. Baugruppenbandbreite ermittelt (und die dazugehoerige max. Upstream Leistung).
                        // Dies ist z.B. dann notwendig, wenn ein 18.000 ADSL Auftrag mit einem ADSL1 Port realisiert wird (und
                        // somit natuerlich auch nur ein ADSL1 Profil zugeordnet werden darf. Dies kann allerdings nur aus einer
                        // Anschlussuebernahme resultieren!)
                        // (Stichwort: ADSL1 Ports bei Surf&Fon 18.000)
                        Integer maxBandwidthOfBgTyp = hwBaugruppe.getHwBaugruppenTyp().getMaxBandwidth().getDownstream();
                        TechLeistung techLsDownstream = leistungsService.findTechLeistung(downstreamTechLs);
                        if (techLsDownstream.getIntegerValue() > maxBandwidthOfBgTyp) {
                            downstreamTechLs = leistungsService.findTechLeistung(TechLeistung.TYP_DOWNSTREAM, Long.valueOf(maxBandwidthOfBgTyp)).getId();
                            int upstreamValue = DSLAMProfile.getMaxUpstreamForDownstream(maxBandwidthOfBgTyp);
                            upstreamTechLs = leistungsService.findTechLeistung(TechLeistung.TYP_UPSTREAM, Long.valueOf(upstreamValue)).getId();
                        }
                    }

                    for (TechLeistung tl : techLeistungen) {
                        if (Long.valueOf(ExterneLeistung.FASTPATH.leistungNo).equals(tl.getExternLeistungNo())) {
                            fastpathTechLs = tl.getId();
                            break;
                        }
                    }
                }

                profile2Assign = getDslamProfiles(DSLAMProfile.filterProfiles(profiles, downstreamTechLs,
                        upstreamTechLs, fastpathTechLs, uetv), hwBaugruppe);
            }
            else {
                // keine Exception werfen, damit bei Produkten ohne Profil-Zuordnung kein Fehler auftritt!
                LOGGER.info(String.format("Es konnte für den techn. Auftrag: %d kein DSLAM-Profil fuer das Produkt mit der ProdId: %d ermittelt werden!",
                        auftragId, produkt.getProdId()));
            }
            return profile2Assign;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Unexpected error while calculating DSLAM profile: " + e.getMessage(), e);
        }
    }

    private DSLAMProfile getDslamProfiles(List<DSLAMProfile> dslamProfiles, HWBaugruppe hwBaugruppe) throws FindException {
        DSLAMProfile profile2Assign;
        try {
            // DSLAM-Profile filtern
            if (CollectionTools.isNotEmpty(dslamProfiles)) {
                profile2Assign = filterProfilesByHwBaugruppe(dslamProfiles, hwBaugruppe);
            }
            else {
                throw new FindException("Kein DSLAM-Profil mit entsprechenden Parametern verfuegbar!");
            }
        }
        catch (IllegalArgumentException e) {
            throw new FindException("DSLAM-Profil konnte nicht ermittelt "
                    + "werden, da nicht alle Parameter definiert wurden!\n" + e.getMessage(), e);
        }
        return profile2Assign;
    }

    /**
     * Filtert die angegebenen DSLAM-Profile nach der Baugruppen-ID. Falls keine Uebereinstimmung der Profile mit der
     * angegebenen Baugruppen-ID ermittelt wird, wird entweder ein Profil ohne Baugruppenzuordnung (falls vorhanden)
     * oder das erste Profil aus der Liste zurueck gegeben. (Damit ist sichergestellt, dass auch bei unvollstaendiger
     * Produkt- bzw. Profil-Konfiguration ein DSLAM-Profil dem Auftrag zugeordnet wird.)
     *
     * @param profiles    Liste der zu durchsuchenden DSLAM-Profile
     * @param hwBaugruppe Angabe der Baugruppe ueber die der Baugruppentyp des gesuchten DSLAM-Profils definiert ist.
     * @return DSLAM-Profil, das zu der Baugruppe konfiguriert ist. <br> Falls kein DSLAM-Profil zur Baugruppe ermittelt
     * werden konnte, wird erstes DSLAM-Profil OHNE BG-Zuordnung zurueck gegeben. Ist auch kein DSLAM-Profil ohne
     * Baugruppenzuordnung vorhanden, wird einfach das erste Profil aus der Liste zurueck gegeben.
     */
    private DSLAMProfile filterProfilesByHwBaugruppe(List<DSLAMProfile> profiles, HWBaugruppe hwBaugruppe) {
        DSLAMProfile profileWithoutBg = null;
        if ((profiles.size() > 1) && (hwBaugruppe != null) && (hwBaugruppe.getHwBaugruppenTyp() != null)) {
            for (DSLAMProfile profile : profiles) {
                if ((profileWithoutBg == null) && (profile.getBaugruppenTypId() == null)) {
                    profileWithoutBg = profile;
                }

                if (NumberTools.equal(profile.getBaugruppenTypId(), hwBaugruppe.getHwBaugruppenTyp().getId())) {
                    return profile;
                }
            }
        }
        return (profileWithoutBg != null) ? profileWithoutBg : profiles.get(0);
    }

    @Override
    public DSLAMProfile assignDSLAMProfile(Long auftragId, Date gueltigVon, @Nullable AuftragAktion auftragAktion, Long sessionId) throws FindException,
            StoreException {
        if ((auftragId == null) || (gueltigVon == null)) {
            throw new StoreException(
                    StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            DSLAMProfile profile2Assign = calculateDefaultDSLAMProfile(auftragId);
            if (profile2Assign != null) {
                String userw = getLoginNameSilent(sessionId);

                Auftrag2DSLAMProfile result = changeDSLAMProfile(auftragId, profile2Assign.getId(), gueltigVon, userw,
                        DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT, null, auftragAktion);

                if (result == null) {
                    String msg = "Das ermittelte Profil ({0}) wurde dem Auftrag nicht zugeordnet!";
                    throw new StoreException(StringTools.formatString(msg, new Object[] { profile2Assign.getName() },
                            null));
                }

                return profile2Assign;
            }
            return null;
        }
        catch (FindException | StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Auftrag2DSLAMProfile changeDSLAMProfile(Long auftragId, Long newProfileId, Date gueltigVon,
            String user, Long changeReasonId, String changeReason) throws StoreException {
        return changeDSLAMProfile(auftragId, newProfileId, gueltigVon, user, changeReasonId, changeReason, null);
    }

    private Auftrag2DSLAMProfile changeDSLAMProfile(Long auftragId, Long newProfileId, Date gueltigVon,
            String user, Long changeReasonId, String changeReason, AuftragAktion auftragAktion)
            throws StoreException {
        if ((auftragId == null) || (gueltigVon == null) || (newProfileId == null) || StringUtils.isBlank(user)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            boolean doProfileChange = true;
            Auftrag2DSLAMProfile lastProfile = null;
            Long auftragAktionsId = (auftragAktion != null) ? auftragAktion.getId() : null;

            List<Auftrag2DSLAMProfile> a2dps = findAuftrag2DSLAMProfiles(auftragId);
            if (CollectionTools.isNotEmpty(a2dps)) {
                // letztes noch aktives Profil ermitteln
                lastProfile = a2dps.get(a2dps.size() - 1);
                if (DateTools.isDateEqual(lastProfile.getGueltigBis(), DateTools.getHurricanEndDate())
                        && NumberTools.equal(lastProfile.getDslamProfileId(), newProfileId)
                        && NumberTools.notEqual(changeReasonId, DSLAMProfileChangeReason.CHANGE_REASON_ID_DAILYFILE)) {
                    // letztes Profil ist identisch mit neuem --> keine Neu-Zuordnung notwendig!
                    doProfileChange = false;

                    if (DateTools.isDateBefore(gueltigVon, lastProfile.getGueltigVon())) {
                        // falls Profil frueher gueltig sein soll, wird das Datum geaendert
                        lastProfile.setGueltigVon(gueltigVon);
                        lastProfile.setUserW(user);

                        dslamProfileDAO.store(lastProfile);
                    }
                }

                if (doProfileChange) {
                    // bereits zugeordnete Profile werden beendet, falls laenger gueltig als 'gueltigVon'
                    for (Auftrag2DSLAMProfile a2dp : a2dps) {
                        if (DateTools.isDateAfter(a2dp.getGueltigBis(), gueltigVon)) {
                            a2dp.setGueltigBis(gueltigVon);
                            a2dp.setAuftragAktionsIdRemove(auftragAktionsId);
                            dslamProfileDAO.store(a2dp);
                        }
                    }
                }
            }

            if (doProfileChange) {
                // neues Profil eintragen
                Auftrag2DSLAMProfile newProfile = new Auftrag2DSLAMProfile();
                newProfile.setAuftragId(auftragId);
                newProfile.setAuftragAktionsIdAdd(auftragAktionsId);
                newProfile.setDslamProfileId(newProfileId);
                newProfile.setGueltigVon(gueltigVon);
                newProfile.setGueltigBis(DateTools.getHurricanEndDate());
                newProfile.setUserW(user);
                newProfile.setChangeReasonId(changeReasonId);
                newProfile.setBemerkung(changeReason);
                dslamProfileDAO.store(newProfile);

                return newProfile;
            }
            else {
                LOGGER.info("Profile hat sich nicht veraendert - nicht neu zuordnen!");
                return lastProfile;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("DSLAM-Profil konnte dem Auftrag nicht zugeordnet werden!\n" + e.getMessage(), e);
        }
    }

    @Override
    public void cancelAuftrag2DslamProfile(Long auftragId, AuftragAktion auftragAktion)
            throws StoreException {
        try {
            List<Auftrag2DSLAMProfile> assignedProfiles = findAuftrag2DSLAMProfiles(auftragId);

            for (Auftrag2DSLAMProfile a2dp : assignedProfiles) {
                if (auftragAktion.isAuftragAktionAddFor(a2dp)) {
                    dslamProfileDAO.deleteAuftrag2DSLAMProfileById(a2dp.getId());
                }
                else if (auftragAktion.isAuftragAktionRemoveFor(a2dp)) {
                    a2dp.setGueltigBis(DateTools.getHurricanEndDate());
                    a2dp.setAuftragAktionsIdRemove(null);
                    dslamProfileDAO.store(a2dp);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    @Nonnull
    public List<Auftrag2DSLAMProfile> findAuftrag2DSLAMProfiles(Long auftragId) throws FindException {
        try {
            Auftrag2DSLAMProfile example = new Auftrag2DSLAMProfile();
            example.setAuftragId(auftragId);

            return dslamProfileDAO.queryByExample(example, Auftrag2DSLAMProfile.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public DSLAMProfile findDSLAMProfile4Auftrag(Long auftragId, Date validDate, boolean getLastIfNull)
            throws FindException {
        try {
            DSLAMProfile profile = findDSLAMProfile4AuftragNoEx(auftragId, validDate, getLastIfNull);
            if (profile == null) {
                throw new FindException("Dem Auftrag ist kein DSLAM-Profil zugeordnet oder " +
                        "es konnte kein aktives Profil gefunden werden.");
            }
            return profile;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public DSLAMProfile findDSLAMProfile4AuftragNoEx(Long auftragId, Date validDate, boolean getLastIfNull)
            throws FindException {
        if (auftragId == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }

        List<Auftrag2DSLAMProfile> assignedProfiles = findAuftrag2DSLAMProfiles(auftragId);
        if (CollectionTools.isEmpty(assignedProfiles)) {
            return null;
        }

        Auftrag2DSLAMProfile activeProfile = null;
        for (Auftrag2DSLAMProfile assigned : assignedProfiles) {
            if (DateTools.isDateBetween(validDate, assigned.getGueltigVon(), assigned.getGueltigBis())) {
                activeProfile = assigned;
                break;
            }
        }

        if ((activeProfile == null) && getLastIfNull) {
            // falls kein Profil zum Datum gefunden wurde, das zuletzt zugeordnete Profil verwenden
            activeProfile = assignedProfiles.get(assignedProfiles.size() - 1);
        }
        if (activeProfile == null) {
            return null;
        }
        return dslamProfileDAO.findById(activeProfile.getDslamProfileId(), DSLAMProfile.class);
    }

    @Override
    public void saveAuftrag2DSLAMProfile(Auftrag2DSLAMProfile toStore) throws StoreException {
        try {
            dslamProfileDAO.store(toStore);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @Nonnull
    public List<Auftrag2DSLAMProfile> modifyDslamProfiles4Auftrag(Long auftragId, LocalDate originalDate,
            LocalDate modifiedDate, AuftragAktion auftragAktion) throws StoreException {
        try {
            List<Auftrag2DSLAMProfile> assignedProfiles = findAuftrag2DSLAMProfiles(auftragId);

            List<Auftrag2DSLAMProfile> modifiedProfiles = new ArrayList<>();
            for (Auftrag2DSLAMProfile assignedProfile : assignedProfiles) {
                boolean isModified = false;
                if (auftragAktion != null) {  // nur DSLAM-Profile mit verlinkter AuftragAktion beruecksichtigen
                    if (auftragAktion.isAuftragAktionAddFor(assignedProfile)) {
                        assignedProfile.setGueltigVon(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        isModified = true;
                    }
                    else if (auftragAktion.isAuftragAktionRemoveFor(assignedProfile)) {
                        assignedProfile.setGueltigBis(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        isModified = true;
                    }
                }
                else {
                    if (DateTools.isDateEqual(assignedProfile.getGueltigVon(), Date.from(originalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                        if (DateTools.isDateEqual(assignedProfile.getGueltigBis(), Date.from(originalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                            assignedProfile.setGueltigBis(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        }
                        else {
                            assignedProfile.setGueltigVon(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        }
                        isModified = true;
                    }
                    else if (DateTools.isDateEqual(assignedProfile.getGueltigBis(), Date.from(originalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                        assignedProfile.setGueltigBis(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        isModified = true;
                    }
                }

                if (isModified) {
                    saveAuftrag2DSLAMProfile(assignedProfile);
                    modifiedProfiles.add(assignedProfile);
                }
            }

            return modifiedProfiles;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<DSLAMProfile> findValidDSLAMProfiles4Auftrag(Long auftragId) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        Rangierung rangierung = rangierungsService.findRangierungWithEQ(endstelle.getRangierId());
        if ((rangierung != null) && (rangierung.getEquipmentIn() != null)) {
            HWBaugruppe baugruppe = hwService.findBaugruppe(rangierung.getEquipmentIn().getHwBaugruppenId());
            if (baugruppe != null) {
                return dslamProfileDAO.findDSLAMProfiles4BaugruppenTyp(baugruppe.getHwBaugruppenTyp());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<DSLAMProfile> findByExample(DSLAMProfile fromParams) {
        return dslamProfileDAO.queryByExample(fromParams, DSLAMProfile.class);
    }

    @Override
    public List<DSLAMProfile> findWithParams(DSLAMProfile fromParams) {
        return dslamProfileDAO.findByParams(fromParams);
    }

    @Override
    public DSLAMProfile findNextHigherDSLAMProfile4DSL18000Auftrag(Long ccAuftragId, Integer bitrateUp,
            Integer bitrateDown) throws FindException {
        if ((ccAuftragId == null) || (bitrateUp == null) || (bitrateDown == null)) {
            throw new IllegalArgumentException(
                    "Die Parameter Auftrag-ID, Bitrate Up/Down dürfen nicht null sein!");
        }

        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(FindNextHigherDSLAMProfile4DSL18000Command.class);
            cmd.prepare(FindNextHigherDSLAMProfile4DSL18000Command.CCAUFTRAG_ID, ccAuftragId);
            cmd.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_DOWN, bitrateDown);
            cmd.prepare(FindNextHigherDSLAMProfile4DSL18000Command.MAX_ATTAINABLE_BITRATE_UP, bitrateUp);
            return (DSLAMProfile) cmd.execute();
        }
        catch (FindException fe) {
            throw fe;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<DSLAMProfile> findDSLAMProfiles(Long baugruppenTyp, final Boolean fastpath,
            Collection<String> uetvsAllowed) throws FindException {
        try {
            List<DSLAMProfile> profiles = dslamProfileDAO.findDSLAMProfiles(baugruppenTyp, fastpath, uetvsAllowed);
            if (profiles != null) {
                // nochmalige Filterung der DSLAM Profiles auf Parameter Fastpath, da dieser beim Hibernate Query
                // auf der Produktiv-DB nicht richtig ausgewertet wurde (Fehler konnte bisher nicht identifiziert
                // werden!).
                // (Auf den Test- bzw. Devel-DBs wurde der Fastpath-Parameter im Hibernate Query korrekt ausgewertet...)
                CollectionUtils.filter(profiles, toEvaluate -> {
                    DSLAMProfile profile = (DSLAMProfile) toEvaluate;
                    return profile.getFastpath().equals(fastpath);
                });
            }

            return profiles;
        }
        catch (DataAccessException e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public DSLAMProfile findDslamProfile4AuftragOrCalculateDefault(final Long auftragId, final Date when)
            throws FindException {
        try {
            return tryFindOrCalculateDslamProfile(auftragId, when);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    private DSLAMProfile tryFindOrCalculateDslamProfile(Long auftragId, Date when) throws FindException {
        DSLAMProfile profile;
        try {
            profile = findDSLAMProfile4Auftrag(auftragId, when, true);
        }
        catch (FindException e) {
            // Exception is thrown if order has no profile
            // loading default profile for order...
            profile = calculateDefaultDSLAMProfile(auftragId);
        }
        return profile;
    }

    @Override
    public void deleteAuftrag2DslamProfile(Long id) throws DeleteException {
        try {
            dslamProfileDAO.deleteAuftrag2DSLAMProfileById(id);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public DSLAMProfile saveDSLAMProfile(DSLAMProfile dslamProfile) {
        return dslamProfileDAO.store(dslamProfile);
    }

    @Override
    public List<DSLAMProfileMapping> findDSLAMProfileMappings(String oldDslamProfileName) throws FindException {
        try {
            return dslamProfileDAO.findByProperty(DSLAMProfileMapping.class, "profilNameAlt", oldDslamProfileName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> findDSLAMProfileMappingCandidates(Long oldDslamProfileId) throws FindException {
        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> profileCandidates = ArrayListMultimap.create();
        DSLAMProfile oldProfile = dslamProfileDAO.findById(oldDslamProfileId, DSLAMProfile.class);
        List<DSLAMProfileMapping> mappings = findDSLAMProfileMappings(oldProfile.getName());
        for (DSLAMProfileMapping mapping : mappings) {
            List<DSLAMProfile> dslamProfiles = findDSLAMProfiles(mapping.getProfilNameNeu());
            profileCandidates.putAll(Pair.create(oldProfile.getId(), mapping.getUetv()), dslamProfiles);
        }

        return profileCandidates;
    }

    @Override
    public DSLAMProfile findNewDSLAMProfileMatch(Long hwBaugruppenTypId, Long oldDslamProfileId, Uebertragungsverfahren uetv) throws FindException {
        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> profileCandidates = findDSLAMProfileMappingCandidates(oldDslamProfileId);
        DSLAMProfile newDslamProfile = null;

        Collection<DSLAMProfile> newDslamProfiles = profileCandidates.get(Pair.create(oldDslamProfileId, uetv));

        if (newDslamProfiles.isEmpty()) {
            newDslamProfiles = profileCandidates.get(Pair.create(oldDslamProfileId, (Uebertragungsverfahren) null));
        }

        for (DSLAMProfile dp : newDslamProfiles) {
            if (hwBaugruppenTypId != null && hwBaugruppenTypId.equals(dp.getBaugruppenTypId())) {
                newDslamProfile = dp;
            }
            // falls es kein Baugruppentyp spezifisches Profil gibt => das allgemeine ohne Baugruppentyp Bezug verwenden
            if (dp.getBaugruppenTypId() == null && newDslamProfile == null) {
                newDslamProfile = dp;
            }
        }
        return newDslamProfile;
    }
}
