/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2012 13:54:55
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 * Command ermittelt das naechst hoehere DSLAM-Profil fuer einen Auftrag anhand der uebergebenen max. attainable. Ist
 * dem bitrate.
 */
@CcTxRequired
public class FindNextHigherDSLAMProfile4DSL18000Command extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(FindNextHigherDSLAMProfile4DSL18000Command.class);

    public static final String CCAUFTRAG_ID = "auftrag.id";
    public static final String MAX_ATTAINABLE_BITRATE_UP = "bitrate.up";
    public static final String MAX_ATTAINABLE_BITRATE_DOWN = "bitrate.down";


    private Long ccAuftragId;
    Integer maxAttainableBitrateUp;
    Integer maxAttainableBitrateDown;

    private List<String> uetvs;
    private List<DSLAMProfile> profiles;
    DSLAMProfile actProfile;

    @Override
    public Object execute() throws Exception {
        initPreparedValues();
        validateIsDSL18000Auftrag(ccAuftragId);

        try {
            uetvs = findUETVs();
            profiles = findProfiles();
            return filterForBestFittingProfile();
        }
        catch (FindException fe) {
            throw fe;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Prueft ob dem Auftrag eine 18000er Downstream-Leistung zugeordnet ist
     *
     * @param ccAuftragId
     * @throws FindException            wenn dem Auftrag keine Downstream-Leistung 18000 zugeordnet ist
     * @throws ServiceNotFoundException
     */
    void validateIsDSL18000Auftrag(Long ccAuftragId) throws FindException, ServiceNotFoundException {
        TechLeistung ls = getCCService(CCLeistungsService.class).findTechLeistung4Auftrag(ccAuftragId,
                TechLeistung.TYP_DOWNSTREAM, false);
        if ((ls == null) || (ls.getLongValue() == null) || (ls.getLongValue() != 18000L)) {
            throw new FindException("Der Auftrag ist kein DSL18000 Auftrag!");
        }
    }

    /**
     * Ermittelt anhand des UETVs des aktuellen DSLAM-Profils die zulaessigen UETVs fuer moegliche kuenftige Profile
     * Liefert nur sinnvolle Werte fuer DSL18000 - Auftraege
     *
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    List<String> findUETVs() throws FindException, ServiceNotFoundException {
        DSLAMProfile actProf = getActualProfile();
        return DSLAMProfile.getMatchingUETV(actProf.isADSL1Profile(), actProf.getUetv());
    }

    /**
     * Ermittelt aus allen moeglichen DSLAM-Profilen, dass Profil dessen Downstream am naehesten ueber der angegeben
     * Bitrate liegt und den hoechsten Upstream Wert besitzt. <br> (Die Ermittlung des Profils mit dem hoechsten
     * Upstream ist ueber die Sortierung der DSLAM-Profile sichergestellt!)
     *
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     */
    DSLAMProfile filterForBestFittingProfile() throws FindException, ServiceNotFoundException {
        sortProfiles();
        DSLAMProfile newProfile4Auftrag = null;
        long nearestDown = Long.MAX_VALUE;
        for (DSLAMProfile profile : profiles) {
            final long down = profile.getBandwidth().getDownstream();

            if ((nearestDown >= down) && (down >= maxAttainableBitrateDown)) {
                nearestDown = down;
                newProfile4Auftrag = profile;
            }
        }
        if (newProfile4Auftrag == null) {
            newProfile4Auftrag = getActualProfile();
        }
        return newProfile4Auftrag;
    }

    /**
     * Sortiert die ermittelten DSLAM-Profile aufsteigend nach Down- und Upstream. <br> Beispiel: Input : 3552/256,
     * 1152/256, 1152/128 Output: 1152/128, 1152/256, 3552/256
     */
    void sortProfiles() {
        if (profiles != null) {
            Collections.sort(profiles, DSLAMProfile.DSLAMPROFILE_COMPARATOR);
        }
    }

    /**
     * Ermittelt anhand BaugruppenTypId, moeglichen UETVs und Fastpath moegliche DSLAM-Profile
     *
     * @return
     * @throws FindException
     * @throws ServiceNotFoundException
     * @throws ServiceCommandException
     */
    List<DSLAMProfile> findProfiles() throws FindException, ServiceNotFoundException, ServiceCommandException {
        Long baugruppenTypId = actProfile.getBaugruppenTypId();

        if (baugruppenTypId == null) {
            baugruppenTypId = findBaugruppenTypId();
        }

        List<DSLAMProfile> profiles = getCCService(DSLAMService.class).findDSLAMProfiles(baugruppenTypId,
                getActualProfile().getFastpath(), uetvs);
        if (CollectionUtils.isEmpty(profiles)) {
            throw new FindException("Es konnten keine passenden DSLAM-Profile ermittelt werden!");
        }
        return profiles;
    }

    Long findBaugruppenTypId() throws ServiceNotFoundException, ServiceCommandException {
        RangierungsService rangierungsService = getCCService(RangierungsService.class);
        HWService hwService = getCCService(HWService.class);
        try {
            Rangierung[] rangierungen = rangierungsService.findRangierungenTx(ccAuftragId, Endstelle.ENDSTELLEN_TYP_B);
            Equipment equipment = ((rangierungen != null) && (rangierungen.length > 0)) ?
                    rangierungsService.findEquipment(rangierungen[0].getEqInId()) : null;
            HWBaugruppe hwBaugruppe = (((equipment != null) && (equipment.getHwBaugruppenId() != null)) ?
                    hwService.findBaugruppe(equipment.getHwBaugruppenId()) : null);

            if (hwBaugruppe == null) {
                throw new HurricanServiceCommandException(String.format("CPS Query Attainable Bitrate abgebrochen, da zum technischen "
                        + "Auftrag %d keine oder nicht alle Daten ermittelt werden konnten!", ccAuftragId));
            }

            return hwBaugruppe.getHwBaugruppenTyp().getId();
        }
        catch (ServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format("Es konnte kein passendes DSLAM-Profil ermittelt werden, da zum technischen "
                    + "Auftrag %d folgender Fehler gefangen wurde: %s", ccAuftragId, e.getMessage()), e);
        }
    }

    /**
     * ueberfuehrt das command in einen gueltigen initial-zustand, und ueberprueft ob alle notwendige prepared values
     * gesetzt wurden
     */
    void initPreparedValues() {
        ccAuftragId = (Long) getPreparedValue(CCAUFTRAG_ID);
        maxAttainableBitrateUp = (Integer) getPreparedValue(MAX_ATTAINABLE_BITRATE_UP);
        maxAttainableBitrateDown = (Integer) getPreparedValue(MAX_ATTAINABLE_BITRATE_DOWN);
        if ((ccAuftragId == null) || (maxAttainableBitrateUp == null) || (maxAttainableBitrateDown == null)) {
            throw new RuntimeException(
                    String.format(
                            "FindNextHigherDSLAMProfileCommand wurde nicht vollständig initialisiert! (CCAUFTRAG_ID=%d, "
                                    +
                                    "MAX_ATTAINABLE_BITRATE_UP=%d, MAX_ATTAINABLE_BITRATE_DOWN=%d",
                            ccAuftragId, maxAttainableBitrateUp, maxAttainableBitrateDown
                    )
            );
        }
    }

    private DSLAMProfile getActualProfile() throws FindException, ServiceNotFoundException {
        if (actProfile == null) {
            DSLAMService dslamService = getCCService(DSLAMService.class);
            actProfile = dslamService.findDSLAMProfile4Auftrag(ccAuftragId, new Date(), false);
            if (actProfile == null) {
                throw new FindException(String.format("Dem techn. Auftrag mit Id %d ist aktuell kein DSLAM-Profil zugeordnet!",
                        ccAuftragId));
            }
        }
        return actProfile;
    }
}
