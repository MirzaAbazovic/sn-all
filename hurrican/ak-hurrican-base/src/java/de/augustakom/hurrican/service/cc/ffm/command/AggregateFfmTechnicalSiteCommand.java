/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.util.*;
import java.util.function.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.CrossingBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DeviceBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.LocationBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.SiteBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.WiringDataBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'OrderTechnicalParams' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert:
 * <br/>
 * <ul>
 *     <li>site</li>
 *     <ul>
 *         <li>type</li>
 *         <li>cluster</li>
 *         <li>location</li>
 *         <li>device</li>
 *         <li>wiringData</li>
 *         <li>...</li>
 *     </ul>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmTechnicalSiteCommand")
@Scope("prototype")
public class AggregateFfmTechnicalSiteCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmTechnicalSiteCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    private DSLAMService dslamService;

    @Override
    public Object execute() throws Exception {
        try {
            checkThatWorkforceOrderHasTechnicalParams();

            getAllAuftragIds()
                    .stream()
                    .sorted() // sort fuer stabilen Acc-Test notwendig! kein Business-Nutzen!
                    .forEach(this::aggregateSiteForOrder);

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM TechnicalOrderParams Site Data: " + e.getMessage(), this.getClass());
        }
    }


    void aggregateSiteForOrder(final Long auftragId) {
        try {
            List<Endstelle> endstellen = endstellenService.findEndstellen4AuftragBasedOnProductConfig(auftragId);
            if (CollectionUtils.isNotEmpty(endstellen)) {
                for (Endstelle endstelle : endstellen) {
                    getWorkforceOrder().getDescription().getTechParams().getSite().add(buildSite(endstelle, auftragId));
                }
            }
        }
        catch (Exception e) {
            throw new FFMServiceException(String.format("Error collecting Site Data for order %s", auftragId), e);
        }
    }


    OrderTechnicalParams.Site buildSite(final Endstelle endstelle, final Long auftragId) throws FindException {
        SiteBuilder siteBuilder = new SiteBuilder();

        HVTStandort hvtStandort = (endstelle.getHvtIdStandort() != null)
                ? hvtService.findHVTStandort(endstelle.getHvtIdStandort()) : null;
        HVTGruppe hvtGruppe = (hvtStandort != null)
                ? hvtService.findHVTGruppeById(hvtStandort.getHvtGruppeId()) : null;

        defineHvtInformation(siteBuilder, endstelle, hvtGruppe, hvtStandort);
        defineUpDownstream(siteBuilder, auftragId);
        defineCarrierbestellungData(siteBuilder, endstelle, hvtStandort);

        DSLAMProfile dslamProfile = dslamService.findDSLAMProfile4AuftragNoEx(auftragId, new Date(), true);
        if (dslamProfile != null) {
            siteBuilder.withLastValidDSLAMProfile(dslamProfile.getName());
        }

        siteBuilder
                .withLocation(createLocation(endstelle))
                .withDevice(createDevice(endstelle))
                .withWiringData(createWiringData(endstelle, auftragId))
                .withSwitch(ccAuftragService.getSwitchKennung4Auftrag(auftragId));

        return siteBuilder.build();
    }

    /**
     * Ermittelt div. HVT-Informationen und traegt sie in das {@link OrderTechnicalParams.Site} Objekt ein.
     */
    void defineHvtInformation(SiteBuilder siteBuilder, Endstelle endstelle, HVTGruppe hvtGruppe, HVTStandort hvtStandort) {
        siteBuilder
                .withType(String.format("Endstelle_%s", endstelle.getEndstelleTyp()))
                .withCluster((hvtStandort != null) ? hvtStandort.getClusterId() : null)
                .withHvtGruppe(hvtGruppe);
    }

    /**
     * Ermittelt div. Up/Downstream Werte des Auftrags und traegt sie in das {@link OrderTechnicalParams.Site} Objekt
     * ein.
     */
    void defineUpDownstream(SiteBuilder sb, Long auftragId) throws FindException {
        TechLeistung downstream = ccLeistungsService.findTechLeistung4Auftrag(
                auftragId, TechLeistung.TYP_DOWNSTREAM, DateConverterUtils.asDate(getReferenceDate()));
        if (downstream != null) {
            sb.withDownstream(downstream.getLongValue());
        }

        TechLeistung upstream = ccLeistungsService.findTechLeistung4Auftrag(
                auftragId, TechLeistung.TYP_UPSTREAM, DateConverterUtils.asDate(getReferenceDate()));
        if (upstream != null) {
            sb.withUpstream(upstream.getLongValue());
        }
    }


    /**
     * Ermittelt die aktuellste Carrierbestellung der Endstelle B des Auftrags und traegt die TAL-Daten (z.B. VBZ) in
     * das {@link OrderTechnicalParams.Site} Objekt ein.
     *
     * @param sb
     * @param endstelle
     * @param hvtStandort
     * @throws FindException
     */
    void defineCarrierbestellungData(SiteBuilder sb, Endstelle endstelle, HVTStandort hvtStandort) throws FindException {
        Carrierbestellung carrierbestellung = carrierService.findLastCB4Endstelle(endstelle.getId());
        if (carrierbestellung != null) {
            sb.withLbz(carrierbestellung.getLbz())
                    .withVtrNr(carrierbestellung.getVtrNr())
                    .withLl(carrierbestellung.getLl())
                    .withAqs(carrierbestellung.getAqs())
                    .withCustomerOnSite(carrierbestellung.getKundeVorOrt());

            if (carrierbestellung.getBereitstellungAm() != null
                    && hvtStandort != null
                    && (HVTStandort.HVT_STANDORT_TYP_HVT.equals(hvtStandort.getStandortTypRefId())
                    || HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ.equals(hvtStandort.getStandortTypRefId()))) {
                sb.withTalProvisioningDate(DateTools.formatDate(
                        carrierbestellung.getBereitstellungAm(), DateTools.PATTERN_DAY_MONTH_YEAR));
            }
        }
    }


    /**
     * Ermittelt die Adress-Daten der Endstelle B und generiert daraus ein {@link OrderTechnicalParams.Site.Location}
     * Objekt
     */
    OrderTechnicalParams.Site.Location createLocation(Endstelle endstelle) throws FindException {
        if (endstelle.getAddressId() != null) {
            CCAddress address = kundenService.findCCAddress(endstelle.getAddressId());
            if (address != null) {
                return new LocationBuilder()
                        .withTAE1(address.getStrasseAdd())
                        .withStreet(address.getStrasse())
                        .withHouseNumber(address.getCombinedHausnummer())
                        .withCity(address.getCombinedOrtOrtsteil())
                        .withZipCode(address.getPlzTrimmed()).build();
            }
        }
        return null;
    }

    // @formatter:off
    /**
     * Ermittelt den EQ-In Port der Endstelle B und davon: <br/>
     * <ul>
     *     <li>Hersteller des Racks</li>
     *     <li>Seriennummer, sofern es sich bei dem Rack um eine MDU handelt</li>
     * </ul>
     * @return
     */
    // @formatter:on
    OrderTechnicalParams.Site.Device createDevice(Endstelle endstelle) throws FindException {
        WiringDataStore wiringDataStore = loadHardwareData4Equipment(endstelle.getRangierId(), true, "EQ-In");
        if (wiringDataStore != null && wiringDataStore.hwRack != null) {
            HVTTechnik hvtTechnik = hvtService.findHVTTechnik(wiringDataStore.hwRack.getHwProducer());
            if (hvtTechnik != null) {
                DeviceBuilder db = new DeviceBuilder().withManufacturer(hvtTechnik.getHersteller());
                if (wiringDataStore.hwRack instanceof HWOltChild) {
                    db.withSerialNumber(((HWOltChild) wiringDataStore.hwRack).getSerialNo());
                }
                return db.build();
            }
        }
        return null;
    }


    private String getPanelPin1FromEquipment(Equipment eq) {
        if (eq != null && (eq.getRangLeiste1() != null || eq.getRangStift1() != null)) {
            return StringTools.join(new String[] { eq.getRangBucht(), eq.getRangLeiste1(),
                        eq.getRangStift1() }, " ", true);
        }
        return null;
    }

    private String getPanelPin2FromEquipment(Equipment eq) {
        if (eq != null && (eq.getRangLeiste2() != null || eq.getRangStift2() != null)) {
            return StringTools.join(new String[] { eq.getRangBucht(), eq.getRangLeiste2(),
                        eq.getRangStift2() }, " ", true);
        }
        return null;
    }


    List<OrderTechnicalParams.Site.WiringData> createWiringData(final Endstelle endstelle, final Long auftragId) throws FindException {
        List<OrderTechnicalParams.Site.WiringData> ffmWiringData = new ArrayList<>();

        List<WiringDataStore> wiringDataStores = loadHardwareData(endstelle);
        for (WiringDataStore wiringDataStore : wiringDataStores) {
            if (wiringDataStore.rangierung != null && wiringDataStore.equipment != null) {
                String panelPin1 = getPanelPin1FromEquipment(wiringDataStore.equipment);
                String panelPin2 = getPanelPin2FromEquipment(wiringDataStore.equipment);
                WiringDataBuilder wb = new WiringDataBuilder()
                        .withType(wiringDataStore.rangierungTyp)
                        .withHWEQN(wiringDataStore.equipment.getHwEQN())
                        .withDistributor(wiringDataStore.equipment.getRangVerteiler())
                        .withLayer2Protocol((wiringDataStore.equipment.getSchicht2Protokoll() != null)
                                ? wiringDataStore.equipment.getSchicht2Protokoll().name() : null)
                        .withPanelPin1(panelPin1)
                        .withPanelPin2(panelPin2);

                if (wiringDataStore.rangierung.getPhysikTypId() != null) {
                    PhysikTyp physikTyp = physikService.findPhysikTyp(wiringDataStore.rangierung.getPhysikTypId());
                    wb.withPhysicType((physikTyp != null) ? physikTyp.getName() : null);
                }

                if (wiringDataStore.hwRack != null) {
                    addHwRackSpecificData(wiringDataStore, wb);
                }

                wb.withCrossing(createCrossingData(wiringDataStore, auftragId).map(CrossingBuilder::build).orElse(null));

                ffmWiringData.add(wb.build());
            }
        }
        return ffmWiringData;
    }


    private void addHwRackSpecificData(WiringDataStore wiringDataStore, WiringDataBuilder wb) {

        wb.withDeviceName(wiringDataStore.hwRack.getGeraeteBez())
            .withModuleType(wiringDataStore.bgTyp.getName())
            .withManagementDescription(wiringDataStore.hwRack.getManagementBez());

        switch(wiringDataStore.hwRack.getClass().getSimpleName()){
            case "HWOnt":
                  wb.withONTID(((HWOnt) wiringDataStore.hwRack).getSerialNo());
                break;
            case "HWDpo":
                  HWDpo hwDpo = (HWDpo) wiringDataStore.hwRack;
                  wb.withChassisIdentifier(hwDpo.getChassisIdentifier())
                  .withChassisSlot(hwDpo.getChassisSlot());
                break;
            default:
                break;
        }
    }

    Optional<CrossingBuilder> createCrossingData(final WiringDataStore wiringDataStore, final Long auftragId) throws FindException   {
        if (wiringDataStore.rangierung == null)  {
            return Optional.empty();
        }

        if (getBauauftrag().isPresent()) {
            final Optional<PhysikUebernahme> physikUebernahme =
                    Optional.ofNullable(physikService.findPhysikUebernahme4Verlauf(
                            auftragId, getBauauftrag().get().getId()));
            physikUebernahme.filter(
                    pu -> PhysikaenderungsTyp.STRATEGY_DSL_KREUZUNG.equals(pu.getAenderungstyp()));

            if (physikUebernahme.isPresent()) {
                final Optional<Rangierung> rangierungForKreuzung =
                        Optional.ofNullable(rangierungsService.findKreuzung(wiringDataStore.rangierung.getId(),
                                !wiringDataStore.eqIn));

                final Function<Rangierung, Optional<PhysikTyp>> getPhysikTyp = r ->
                        Optional.ofNullable(r.getPhysikTypId()).map(id -> {
                                try {
                                    return physikService.findPhysikTyp(id);
                                }
                                catch (FindException e) {
                                    throw new RuntimeException(e);
                                }}
                );

                return rangierungForKreuzung.flatMap( (Rangierung r) ->
                        Optional.ofNullable(r.getEquipmentIn()).<CrossingBuilder>map( (Equipment eqIn) ->
                          new CrossingBuilder()
                            .withHwEqn(eqIn.getHwEQN())
                            .withDistributor(eqIn.getRangVerteiler())
                            .withPhysikType(getPhysikTyp.apply(r).map(PhysikTyp::getName).orElse(null))
                            .withPanelPin1(getPanelPin1FromEquipment(eqIn))
                            .withPanelPin2(getPanelPin2FromEquipment(eqIn)))
                        );
            }
        }

        return Optional.empty();
    }

    @NotNull
    List<WiringDataStore> loadHardwareData(Endstelle endstelle) throws FindException {
        List<WiringDataStore> wiringDataStores = new ArrayList<>();
        if (endstelle != null) {
            if (endstelle.getRangierId() != null) {
                wiringDataStores.add(loadHardwareData4Equipment(endstelle.getRangierId(), true, "EQ-In"));
                wiringDataStores.add(loadHardwareData4Equipment(endstelle.getRangierId(), false, "EQ-Out"));
            }

            if (endstelle.getRangierIdAdditional() != null) {
                wiringDataStores.add(loadHardwareData4Equipment(endstelle.getRangierIdAdditional(), true, "EQ-In2"));
                wiringDataStores.add(loadHardwareData4Equipment(endstelle.getRangierIdAdditional(), false, "EQ-Out2"));
            }
        }

        return wiringDataStores;
    }


    WiringDataStore loadHardwareData4Equipment(Long rangierId, boolean eqIn, String rangierungTyp) throws FindException {
        WiringDataStore wiringDataStore = new WiringDataStore();
        wiringDataStore.rangierungTyp = rangierungTyp;
        wiringDataStore.eqIn = eqIn;
        if (rangierId != null) {
            wiringDataStore.rangierung = rangierungsService.findRangierung(rangierId);
            Long equipmentId = eqIn
                    ? wiringDataStore.rangierung.getEqInId()
                    : wiringDataStore.rangierung.getEqOutId();

            if (equipmentId != null) {
                wiringDataStore.equipment = rangierungsService.findEquipment(equipmentId);

                HWBaugruppe hwBg = (wiringDataStore.equipment.getHwBaugruppenId() != null)
                        ? hwService.findBaugruppe(wiringDataStore.equipment.getHwBaugruppenId()) : null;
                wiringDataStore.bgTyp = (hwBg != null) ? hwBg.getHwBaugruppenTyp() : null;

                wiringDataStore.hwRack = (wiringDataStore.equipment.getHwBaugruppenId() != null)
                        ? hwService.findRackForBaugruppe(wiringDataStore.equipment.getHwBaugruppenId()) : null;
            }
        }
        return wiringDataStore;
    }


    /**
     * Hilfsklasse, um sich notwendige Daten fuer die Aggregation von {@linke de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams.Site.WiringData}
     * Objekten zu 'merken'.
     */
    public static class WiringDataStore {

        Rangierung rangierung = null;
        Equipment equipment = null;
        HWBaugruppenTyp bgTyp = null;
        HWRack hwRack = null;
        boolean eqIn;
        String rangierungTyp;
    }
}
