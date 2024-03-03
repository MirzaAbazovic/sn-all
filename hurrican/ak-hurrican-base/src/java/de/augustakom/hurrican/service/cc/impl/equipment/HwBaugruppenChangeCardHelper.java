/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014 15:43
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
public class HwBaugruppenChangeCardHelper {

    private static final Logger LOGGER = Logger.getLogger(HwBaugruppenChangeCardHelper.class);

    private final EQCrossConnectionService eqCrossConnectionService;
    private final HWService hwService;
    private final HWBaugruppenChangeService hwBaugruppenChangeService;
    private final ProduktService produktService;
    private final RangierungsService rangierungsService;
    private final HWBaugruppenChange hwBgChange;
    private final PhysikService physikService;
    private final Long sessionId;
    private final DSLAMService dslamService;
    private final String userW;

    private List<HWBaugruppenChangePort2PortView> portMappingViews;

    HwBaugruppenChangeCardHelper(EQCrossConnectionService eqCrossConnectionService, HWService hwService,
            HWBaugruppenChangeService hwBaugruppenChangeService, ProduktService produktService,
            RangierungsService rangierungsService, PhysikService physikService,
            HWBaugruppenChange hwBgChange, Long sessionId,
            DSLAMService dslamService, String userW) {
        this.eqCrossConnectionService = eqCrossConnectionService;
        this.hwService = hwService;
        this.hwBaugruppenChangeService = hwBaugruppenChangeService;
        this.produktService = produktService;
        this.rangierungsService = rangierungsService;
        this.physikService = physikService;
        this.hwBgChange = hwBgChange;
        this.sessionId = sessionId;
        this.dslamService = dslamService;
        this.userW = userW;
    }

    AKWarnings changeDslamProfiles(Date executionDate,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping,
            Map<Long, Rangierung> aktualisierteRangierungen
    ) throws FindException, StoreException {
        final AKWarnings warnings = new AKWarnings();
        for (HWBaugruppenChangePort2PortView port2Port : this.getPortMappingViews()) {
            if (port2Port.getAuftragId() == null) {
                continue;
            }
            for (Auftrag2DSLAMProfile a2dProfile : dslamService.findAuftrag2DSLAMProfiles(port2Port.getAuftragId())) {
                if (a2dProfile.getGueltigBis().before(executionDate)) {
                    continue; // nicht mehr gueltig
                }

                Equipment eq = rangierungsService.findEquipment(port2Port.getEquipmentIdNew());
                HWBaugruppenTyp hwBaugruppenTyp = hwService.findBaugruppe(eq.getHwBaugruppenId()).getHwBaugruppenTyp();

                final Rangierung rangierung = aktualisierteRangierungen.get(eq.getId());
                Equipment eqUevt = rangierungsService.findEquipment(rangierung.getEqOutId());

                DSLAMProfile newDslamProfile = findBestDslamProfileMatch(hwBaugruppenTyp, a2dProfile.getDslamProfileId(),
                        eqUevt.getUetv(), dslamProfileMapping);

                if (newDslamProfile != null) {
                    switchDslamProfile(executionDate, a2dProfile, newDslamProfile);
                }
                else {
                    warnings.addAKWarning(this, String.format("Für Auftrag %s konnte kein neues DSLAM-Profil " +
                            "ermittelt werden.", a2dProfile.getAuftragId()));
                }
            }
        }
        return warnings;
    }

    void switchDslamProfile(Date executionDate, Auftrag2DSLAMProfile a2dProfile, DSLAMProfile newDslamProfile)
            throws StoreException {
        Auftrag2DSLAMProfile newProfile = new Auftrag2DSLAMProfile();
        newProfile.setAuftragId(a2dProfile.getAuftragId());
        newProfile.setBemerkung("Baugruppenschwenk");
        newProfile.setChangeReasonId(DSLAMProfileChangeReason.CHANGE_REASON_ID_MIGRATION);
        newProfile.setDslamProfileId(newDslamProfile.getId());
        newProfile.setUserW(userW);
        // default Gueltigkeit
        newProfile.setGueltigVon(executionDate);
        newProfile.setGueltigBis(DateTools.getHurricanEndDate());
        if (a2dProfile.getGueltigVon().after(executionDate)) {
            newProfile.setGueltigVon(a2dProfile.getGueltigVon());
        }
        if (!a2dProfile.getGueltigBis().equals(DateTools.getHurricanEndDate())) {
            newProfile.setGueltigBis(a2dProfile.getGueltigBis());
        }
        a2dProfile.setGueltigBis(executionDate);
        dslamService.saveAuftrag2DSLAMProfile(a2dProfile);
        dslamService.saveAuftrag2DSLAMProfile(newProfile);
    }

    DSLAMProfile findBestDslamProfileMatch(HWBaugruppenTyp hwBaugruppenTyp, Long currentDslamProfileId, Uebertragungsverfahren uetv,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping) {
        DSLAMProfile newDslamProfile = null;
        Collection<DSLAMProfile> newDslamProfiles = dslamProfileMapping.get(Pair.create(currentDslamProfileId, uetv));
        if (newDslamProfiles.isEmpty()) {
            newDslamProfiles = dslamProfileMapping.get(Pair.create(currentDslamProfileId, (Uebertragungsverfahren) null));
        }

        for (DSLAMProfile dp : newDslamProfiles) {
            if (hwBaugruppenTyp.getId().equals(dp.getBaugruppenTypId())) {
                newDslamProfile = dp;
            }
            // falls es kein Baugruppentyp spezifisches Profil gibt => das allgemeine ohne Baugruppentyp Bezug verwenden
            if (dp.getBaugruppenTypId() == null && newDslamProfile == null) {
                newDslamProfile = dp;
            }
        }
        return newDslamProfile;
    }

    /**
     * Tauscht die alten gg. die neuen Ports aus.
     */
    Map<Long, Rangierung> movePorts() throws StoreException {
        Map<Long, Rangierung> umgeschriebeneRangierungen = Maps.newHashMap();
        try {
            Iterator<HWBaugruppenChangePort2Port> portMappingIterator = hwBgChange.getPort2Port().iterator();
            while (portMappingIterator.hasNext()) {
                HWBaugruppenChangePort2Port port2port = portMappingIterator.next();

                if (port2port.getEqStateOrigOld() != EqStatus.abgebaut && port2port.getEquipmentOld() != null) {
                    Rangierung rangierungToChange = rangierungsService
                            .findRangierung4Equipment(port2port.getEquipmentOld().getId());

                    if (null == rangierungToChange) {
                        // moegliche Ursache, dass keine Rangierung gefunden werden konnte:
                        // fuer die Suche in RangierugsServiceImpl.findRangierung4Equipment wird das
                        // Gueltigkeitsdatum auf DateTools.getHurricanEndDate() gesetzt. Ist die Rangierung nicht mehr
                        // gueltig, so wird sie nicht gefunden
                        throw new FindException("Rangierung für die Equipment-ID " + port2port.getEquipmentOld().getId()
                                + " nicht gefunden (Rangierung noch gültig?)");
                    }

                    if (port2port.getEquipmentNew() == null) {
                        rangierungToChange.setMatchingEqId(port2port.getEquipmentOld().getId(), null);
                        rangierungToChange.setFreigegeben(Rangierung.Freigegeben.deactivated);
                        rangierungsService.saveRangierung(rangierungToChange, false);
                    }
                    else {
                        switchPortsInRangierung(rangierungToChange, port2port.getEquipmentOld(),
                                port2port.getEquipmentNew(), true);
                        umgeschriebeneRangierungen.put(port2port.getEquipmentNew().getId(), rangierungToChange);
                    }
                }

                if (port2port.getEquipmentOldIn() != null && port2port.getEquipmentNewIn() != null) {
                    Rangierung rangierungToChange = rangierungsService
                            .findRangierung4Equipment(port2port.getEquipmentOldIn().getId());
                    switchPortsInRangierung(rangierungToChange, port2port.getEquipmentOldIn(),
                            port2port.getEquipmentNewIn(), false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Austauschen der Ports: " + e.getMessage(), e);
        }
        return umgeschriebeneRangierungen;
    }

    /**
     * Tauscht die Equipment-ID in der angegebenen Rangierung aus.
     */
    void switchPortsInRangierung(Rangierung rangierung, Equipment equipmentOld, Equipment equipmentNew,
            boolean isPrimeRangierung) throws StoreException, FindException {
        if (rangierung != null) {
            rangierung.setMatchingEqId(equipmentOld.getId(), equipmentNew.getId());

            if (hwBgChange.getPhysikTypNew() != null) {
                PhysikTyp newPhysikTyp = hwBgChange.getPhysikTypNew();
                if (isPrimeRangierung) {
                    rangierung.setPhysikTypId(newPhysikTyp.getId());
                }
                else {
                    PhysikTyp oldPhysikTyp = physikService.findPhysikTyp(rangierung.getPhysikTypId());
                    if (physikService.manufacturerChanged(oldPhysikTyp, newPhysikTyp)) {
                        // passenden Physiktyp vom neuen Hersteller ermitteln und setzen
                        PhysikTyp newChildPT = physikService.findCorrespondingPhysiktyp(
                                oldPhysikTyp, newPhysikTyp.getHvtTechnikId());
                        rangierung.setPhysikTypId(newChildPT.getId());
                    }
                }
            }

            rangierung.setFreigegeben(Rangierung.Freigegeben.freigegeben);
            rangierungsService.saveRangierung(rangierung, false);
        }
    }

    /**
     * Markiert die alten Ports und Baugruppen als 'abgebaut'.
     */
    void declarePortsAsRemoved() throws StoreException, ValidationException {
        // Ports als 'abgebaut' markieren
        Iterator<HWBaugruppenChangePort2Port> portMappingIterator = hwBgChange.getPort2Port().iterator();
        while (portMappingIterator.hasNext()) {
            HWBaugruppenChangePort2Port port2port = portMappingIterator.next();
            modifyEquipmentState(port2port.getEquipmentOld(), EqStatus.abgebaut);
            modifyEquipmentState(port2port.getEquipmentOldIn(), EqStatus.abgebaut);
        }
    }

    void declareCardsAsRemoved(@Nullable Predicate<HWBaugruppe> predicate) throws StoreException, ValidationException {
        // Baugruppen als 'abgebaut' markieren
        final Collection<HWBaugruppe> baugruppen = hwBgChange.getHWBaugruppen4ChangeCard();
        final Collection<HWBaugruppe> hwBaugruppenToRemove = (predicate == null || baugruppen == null)
                ? baugruppen
                : Collections2.filter(baugruppen, predicate);

        if (CollectionTools.isNotEmpty(hwBaugruppenToRemove)) {
            for (HWBaugruppe toRemove : hwBaugruppenToRemove) {
                modifyHwBaugruppenState(toRemove, Boolean.FALSE);
            }
        }
    }

    /**
     * Markiert die neuen Ports abhaengig von den alten Ports als 'rangiert' bzw. 'frei'
     * und die neue Baugruppe als 'eingebaut'.
     */
    void declareNewPortsAndCardAsBuiltIn() throws StoreException, ValidationException {
        // Ports als 'rangiert' markieren
        Iterator<HWBaugruppenChangePort2Port> portMappingIterator = hwBgChange.getPort2Port().iterator();
        while (portMappingIterator.hasNext()) {
            HWBaugruppenChangePort2Port port2port = portMappingIterator.next();
            modifyEquipmentState(port2port.getEquipmentNew(), port2port.getEqStateOrigOld());
            modifyEquipmentState(port2port.getEquipmentNewIn(), port2port.getEqStateOrigOld());
        }

        // neue Baugruppen als 'eingebaut' markieren
        for (final HWBaugruppenChangeCard hwBaugruppeChange : hwBgChange.getHwBgChangeCard()) {
            for (HWBaugruppe hwBaugruppeNew : hwBaugruppeChange.getHwBaugruppenNew()) {
                modifyHwBaugruppenState(hwBaugruppeNew, Boolean.TRUE);
            }
        }
    }

    /**
     * Aendert den Status des angegebenen Ports.
     */
    void modifyEquipmentState(Equipment equipmentToChangeState, EqStatus newState) throws StoreException {
        if (equipmentToChangeState != null) {
            equipmentToChangeState.setStatus(newState);
            rangierungsService.saveEquipment(equipmentToChangeState);
        }
    }

    /**
     * Setzt das Flag 'eingebaut' der Baugruppe auf {@code builtIn}
     */
    void modifyHwBaugruppenState(HWBaugruppe hwBaugruppe, Boolean builtIn) throws StoreException, ValidationException {
        hwBaugruppe.setEingebaut(builtIn);
        hwService.saveHWBaugruppe(hwBaugruppe);
    }

    /*
     * Laedt die View-Daten mit den Port-Mappings.
     * Die View-Objekte werden fuer die Berechnung der Default-CrossConnections benoetigt.
     * Auf Grund der DB-Transaktionen ist es notwendig, dass die Views VOR dem
     * Umschreiben der Ports geladen werden!
     */
    void loadPortMappingViews() throws FindException {
        portMappingViews = hwBaugruppenChangeService.findPort2PortViews(hwBgChange);
        if (CollectionTools.isEmpty(portMappingViews)) {
            throw new FindException("View mit den Port-Mappings konnte nicht ermittelt werden!");
        }
    }

    public List<HWBaugruppenChangePort2PortView> getPortMappingViews() {
        return portMappingViews;
    }

    /**
     * Berechnet fuer die neuen(!) Ports die Default-CrossConnection, sofern der Port nicht als 'manuell konfiguriert'
     * markiert ist.
     */
    void calculateCrossConnections(Date executionDate) throws StoreException {
        try {
            for (HWBaugruppenChangePort2PortView portMappingView : portMappingViews) {
                if ((portMappingView.getEquipmentIdNew() != null)
                        && (portMappingView.getAuftragId() != null)
                        && !BooleanTools.nullToFalse(portMappingView.getEqNewManualConfiguration())) {

                    Produkt produkt = produktService.findProdukt4Auftrag(portMappingView.getAuftragId());
                    if (produkt == null) {
                        throw new StoreException(
                                "Produkt fuer das Equipment (ID: {0}; HW_EQN: {1}) konnte nicht ermittelt werden!",
                                new Object[] {
                                        String.format("%d", portMappingView.getEquipmentIdNew()),
                                        portMappingView.getHwEqnNew() }
                        );
                    }

                    Equipment equipment = rangierungsService.findEquipment(portMappingView.getEquipmentIdNew());
                    eqCrossConnectionService.defineDefaultCrossConnections4Port(
                            equipment,
                            portMappingView.getAuftragId(),
                            executionDate,
                            produkt.getIsVierDraht(),
                            sessionId);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Generierung der Default CrossConnections: " + e.getMessage(), e);
        }
    }

    /* Ueberprueft die fuer die Ausfuehrung notwendigen Daten. */
    void checkData() throws StoreException {
        if (hwBgChange == null) {
            throw new StoreException("Planung zur Ausfuehrung ist nicht angegeben!");
        }
        else if (hwBgChange.getHWBaugruppen4ChangeCard().isEmpty()) {
            throw new StoreException("In der Planung ist keine Ziel-Baugruppe hinterlegt!");
        }
    }
}
