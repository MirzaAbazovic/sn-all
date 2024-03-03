/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2009 09:56:19
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import java.util.stream.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.SdslNdraht;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLCrossConnectionData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDSLData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Command-Klasse, um DSL-Daten fuer einen Auftrag zu ermitteln. <br> Die ermittelten Daten werden von dem Command in
 * einem XML-Element in der vom CPS erwarteten Struktur aufbereitet.
 *
 *
 */
public class CPSGetDSLDataCommand extends AbstractCPSDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetDSLDataCommand.class);

    private RegularExpressionService regularExpressionService;
    private EndstellenService endstellenServie;
    private RangierungsService rangierungsService;
    private HWService hwService;
    private HVTService hvtService;
    private EQCrossConnectionService crossConnectionService;
    private ReferenceService referenceService;
    private PbitHelper pbitHelper;

    // ID des relevanten techn. Auftrags fuer die Radius-Daten
    // (z.B. bei TK-Anlagen nicht unbedingt CPSTransaction.auftragId)
    private Long techOrderId = null;

    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            techOrderId = findTechOrderId4XDSL(isNecessary());

            // techOrderId kann an dieser Stelle nur null sein wenn getNecessary() false liefert, sonst haette findTechOrderId4XDSL() eine Exception geworfen.
            if (!isNecessary() && (techOrderId == null)) {
                return ServiceCommandResult.createCmdResult(
                        ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
            }
            else {
                Endstelle esB = endstellenServie.findEndstelle4Auftrag(techOrderId, Endstelle.ENDSTELLEN_TYP_B);
                HVTStandort hvtStandort = hvtService.findHVTStandort(esB.getHvtIdStandort());
                if (hvtStandort == null) {
                    throw new HurricanServiceCommandException("Standort fuer Endstelle B konnte nicht ermittelt werden!");
                }

                if (hvtStandort.isFtthOrFttb()) {
                    // bei FTTX Standorten werden keine DSL-Daten ermittelt!
                    return ServiceCommandResult.createCmdResult(
                            ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
                }

                CPSDSLData dsl = new CPSDSLData();
                dsl.setPortId(definePortId(techOrderId));
                dsl.setWires("2");  // FIXME  CPSDSLData.wires - muss dynamisch ermittelt werden!

                if (esB.getRangierId() == null) {
                    throw new HurricanServiceCommandException("Endstelle B besitzt keine Rangierung!");
                }

                Rangierung dslRang = findRangierungByEndstelle(esB);

                // DTAG u. DSLAM-Port ermitteln
                Equipment dtagEQ = rangierungsService.findEquipment(dslRang.getEqOutId());
                Equipment dslEQ = rangierungsService.findEquipment(dslRang.getEqInId());
                if (dslEQ == null) {
                    throw new HurricanServiceCommandException("DSLAM Equipment not found for order!");
                }

                // HW-Struktur ermitteln
                HWBaugruppe baugruppe = hwService.findBaugruppe(dslEQ.getHwBaugruppenId());
                HWRack rack = hwService.findRackById(baugruppe.getRackId());
                HVTTechnik manufacturer = hvtService.findHVTTechnik(rack.getHwProducer());

                // DSLAM Typ definieren (entspricht dem Standort-Typ)
                if (hvtStandort.getStandortTypRefId() != null) {
                    Reference hvtStandortType = referenceService.findReference(hvtStandort.getStandortTypRefId());
                    dsl.setDslamType(hvtStandortType.getStrValue());
                }

                // DSLAM-Port abhaengig vom Baugruppentyp ermitteln
                dsl.setDslamPort(getDSLAMPort(dslEQ, baugruppe.getHwBaugruppenTyp()));

                if (PhysikTyp.PHYSIKTYP_FTTC_VDSL.equals(dslRang.getPhysikTypId())) {
                    // Bei VDSL ist Schicht-2-Protokoll immer EFM
                    dsl.setDslamPortType(Schicht2Protokoll.EFM.name());
                    if (SdslNdraht.OPTIONAL_BONDING.equals(getProdukt().getSdslNdraht())) {
                        // FTTC-Bonding fuer Glasfaser-SDSL
                        final List<String> slavePorts = getFttcBondingSlavePorts();
                        dsl.fttcBondingSlavePorts = slavePorts.isEmpty() ? null : new CPSDSLData.SlavePorts(slavePorts);
                        dsl.setWires(String.valueOf((1 + slavePorts.size()) * 2));
                    }
                }
                else {
                    dsl.setDslamPortType((dslEQ.getSchicht2Protokoll() != null)
                            ? dslEQ.getSchicht2Protokoll().name() : Schicht2Protokoll.ATM.name());
                }

                // Uebertragungsverfahren definierten - Default: H13
                // (Default z.B. bei eigener Technik notwendig)
                dsl.setPhysicType(((dtagEQ != null) && (dtagEQ.getUetv() != null))
                        ? dtagEQ.getUetv().name() : Uebertragungsverfahren.H13.name());

                // Hardware-Daten setzen
                dsl.setDslamName(rack.getGeraeteBez());
                dsl.setCardType(baugruppe.getHwBaugruppenTyp().getName());
                dsl.setDslamManufacturer(manufacturer.getCpsName());
                if (dtagEQ != null) {
                    dsl.setKvzNummer(dtagEQ.getKvzNummer());
                }

                // Down/Upstream, Fastpath, Flatrate
                DSLAMProfile profile = getDSLAMProfile(techOrderId);
                if (profile == null) {
                    throw new HurricanServiceCommandException("DSLAM-Profile not found for order!");
                }
                dsl.setDownstream(profile.getBandwidth().getDownstreamAsString());
                dsl.setUpstream(profile.getBandwidth().getUpstreamAsString());
                if (profile.getBandwidthNetto() != null) {
                    dsl.setDownstreamNetto(profile.getBandwidthNetto().getDownstreamAsString());
                    dsl.setUpstreamNetto(profile.getBandwidthNetto().getUpstreamAsString());
                }
                dsl.setFastpath(BooleanTools.getBooleanAsString(profile.getFastpath()));
                dsl.setTmDown(NumberTools.convertToString(profile.getTmDown(), null));
                dsl.setTmUp(NumberTools.convertToString(profile.getTmUp(), null));
                dsl.setL2PowerSafeMode(BooleanTools.getBooleanAsString(profile.getL2PowersafeEnabled()));
                dsl.setForceADSL1(BooleanTools.getBooleanAsString(profile.getForceADSL1()));

                PhysikTyp pt = physikService.findPhysikTyp(dslRang.getPhysikTypId());
                dsl.setTransferMethod(pt.getCpsTransferMethod());
                checkAndModifyADSLProfile(profile, pt.getCpsTransferMethod(), dsl);

                // CrossConnections laden
                addCrossConnections(dslEQ, dsl);

                pbitHelper.addVoipPbitIfNecessary(dsl, profile, getAuftragDaten().getAuftragId(),
                        getCPSTransaction().getEstimatedExecTime());

                // DSL-Daten an ServiceOrder-Data uebergeben
                getServiceOrderData().setDsl(dsl);

                return ServiceCommandResult.createCmdResult(
                        ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
            }
        }
        catch (NullPointerException e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "NPE while loading DSL-Data: " + e.getMessage(), this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading DSL-Data: " + e.getMessage(), this.getClass());
        }
    }

    final List<String> getFttcBondingSlavePorts() throws FindException, ServiceCommandException {
        return getOrderIDs4CPSTx()
                .stream()
                .filter(id -> NumberTools.notEqual(id, getAuftragDaten().getAuftragId()))
                .map(id -> findEqInWithBgTypForAuftrag(id).map(pair -> getDSLAMPort(pair.getFirst(), pair.getSecond())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Rangierung findRangierungByEndstelle(Endstelle esB) throws FindException, HurricanServiceCommandException {
        Rangierung dslRang = rangierungsService.findRangierungTx(esB.getRangierId());
        if ((dslRang == null) || (dslRang.getEqInId() == null)) {
            throw new HurricanServiceCommandException(
                    "Rangierung not found or DSL-Equipment not defined!");
        }
        return dslRang;
    }

    private Optional<Pair<Equipment, HWBaugruppenTyp>> findEqInWithBgTypForAuftrag(final long auftragId) {
        try {
            final Endstelle esB = endstellenServie.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            final Rangierung rangierung = findRangierungByEndstelle(esB);
            return Optional.ofNullable(rangierungsService.findEquipment(rangierung.getEqInId()))
                    .flatMap(e -> findHwBaugruppenTyp(e.getHwBaugruppenId()).map(t -> Pair.create(e, t)));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<HWBaugruppenTyp> findHwBaugruppenTyp(final Long bgId)  {
        if(bgId == null)    {
            return Optional.empty();
        }
        try {
            final HWBaugruppenTyp typ = hwService.findBaugruppe(bgId).getHwBaugruppenTyp();
            return (typ != null && typ.getBondingCapable()) ? Optional.of(typ) : Optional.empty();
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Prueft und korrigiert ggf. die ADSL Transfer-Methode.
     * Folgende Regelung:
     *  - Transfer-Methode von Port = ADSL2+
     *      - DSLAM-Profil = ADSL 1  -->> Transfer-Methode von dslData wird auf 'ADSL' gesetzt
     * @param usedDslamProfile
     * @param transferMethodOfPort
     */
    private void checkAndModifyADSLProfile(DSLAMProfile usedDslamProfile, String transferMethodOfPort, CPSDSLData dslData) {
        if (StringUtils.equalsIgnoreCase(transferMethodOfPort, CPSDSLData.TRANSFER_METHOD_ADSL2P)
                && usedDslamProfile.isADSL1Profile()) {
            dslData.setTransferMethod(CPSDSLData.TRANSFER_METHOD_ADSL);
        }
    }

    /**
     * Ermittelt abhaengig vom Baugruppen-Typ die notwendige DSLAM-Port Information. Ueber den BG-Typ wird eine
     * bestimmte Regular-Expression ermittelt. Der mit der RegExp matchende Part von HW_EQN wird durch einen Leerstring
     * ersetzt. Der restliche String ist die zu verwendende DSLAM Port-Information.
     *
     * @param eq    Equipment Datensatz vom DSLAM-Port
     * @param bgTyp Baugruppen-Typ
     * @return DSLAM-Port Information abhaengig vom Baugruppentyp
     */
    private String getDSLAMPort(Equipment eq, HWBaugruppenTyp bgTyp) {
        final String portInfoWithLeadingZeros = regularExpressionService.match(bgTyp.getHwTypeName(),
                HWBaugruppenTyp.class, CfgRegularExpression.Info.CPS_DSLAM_PORT, eq.getHwEQN());
        return removeLeadingZerosFromParts(portInfoWithLeadingZeros);
    }

    private String removeLeadingZerosFromParts(final String portInfoWithLeadingZeros) {
        final String[] parts = portInfoWithLeadingZeros.split("-");
        return Lists.newArrayList(parts).stream()
                .reduce("", (a, b) -> a + ((a.isEmpty()) ? "" : "-") + removeLeadingZeros(b));
    }

    private String removeLeadingZeros(final String part) {
        return (part.startsWith("0") && part.length() > 1) ? removeLeadingZeros(part.substring(1)) : part;
    }

    /*
     * Ermittelt die CrossConnections zu dem Port und fuegt
     * diese dem DSL-Objekt hinzu.
     * @param dslEquipment
     */
    private void addCrossConnections(Equipment dslEquipment, CPSDSLData dslData) throws HurricanServiceCommandException {
        try {
            List<EQCrossConnection> crossConnectionsOnPort =
                    crossConnectionService.findEQCrossConnections(dslEquipment.getId(), getCPSTransaction().getEstimatedExecTime());
            if (CollectionTools.isNotEmpty(crossConnectionsOnPort)) {
                for (EQCrossConnection eqCrossConnection : crossConnectionsOnPort) {
                    Reference crossConnType = referenceService.findReference(eqCrossConnection.getCrossConnectionTypeRefId());
                    if (crossConnType == null) {
                        throw new FindException(
                                "CrossConnection type could not be loaded! Reference ID: " + eqCrossConnection.getCrossConnectionTypeRefId());
                    }

                    CPSDSLCrossConnectionData crossConnection = new CPSDSLCrossConnectionData();
                    crossConnection.setCcType(crossConnType.getStrValue());
                    crossConnection.setNtInner(eqCrossConnection.getNtInner());
                    crossConnection.setNtOuter(eqCrossConnection.getNtOuter());
                    crossConnection.setLtInner(eqCrossConnection.getLtInner());
                    crossConnection.setLtOuter(eqCrossConnection.getLtOuter());
                    crossConnection.setBrasInner(eqCrossConnection.getBrasInner());
                    crossConnection.setBrasOuter(eqCrossConnection.getBrasOuter());

                    dslData.addCrossConnection(crossConnection);
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException("Error loading the CrossConnections of the DSLAM port: " + e.getMessage());
        }
    }

    /**
     * Injected
     */
    public void setRegularExpressionService(RegularExpressionService regularExpressionService) {
        this.regularExpressionService = regularExpressionService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenServie = endstellenService;
    }

    /**
     * Injected
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected
     */
    @Override
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    /**
     * Injected
     */
    public void setCrossConnectionService(EQCrossConnectionService crossConnectionService) {
        this.crossConnectionService = crossConnectionService;
    }

    /**
     * Injected
     */
    @Override
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    public void setPbitHelper(final PbitHelper pbitHelper) {
        this.pbitHelper = pbitHelper;
    }
}


