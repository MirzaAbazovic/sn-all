/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2012 15:37:45
 */
package de.augustakom.hurrican.service.cc.impl;

import static java.lang.String.*;

import java.lang.String;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.ProfileAuftrag;
import de.augustakom.hurrican.model.cc.ProfileAuftragValue;
import de.augustakom.hurrican.model.cc.ProfileParameterMapper;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSPBITData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVlanData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsEndpointDevice;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsItem;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsLayer2Config;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsNetworkDevice;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CPSGetDataService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProfileService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.command.cps.CpsFacade;
import de.augustakom.hurrican.service.cc.impl.command.cps.PbitHelper;
import de.augustakom.hurrican.service.cc.utils.ProfileAuftragValueUtil;

/**
 * Implementierung von CPSGetDataService
 */
@CcTxRequired
public class CPSGetDataServiceImpl extends DefaultCCService implements CPSGetDataService {

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    private DSLAMService dslamService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProfileService")
    private ProfileService profileService;
    @Autowired
    private VlanService vlanService;
    @Autowired
    private PbitHelper pbitHelper;
    @Autowired
    private CpsFacade cpsFacade;
    @Autowired
    private ReferenceService referenceService;

    final Function<EqVlan, CPSVlanData> TO_CPS_VLAN_DATA = new Function<EqVlan, CPSVlanData>() {
        @Override
        public CPSVlanData apply(EqVlan eqVlan) {
            CPSVlanData vlanData = new CPSVlanData();
            vlanData.setCvlan(eqVlan.getCvlan());
            vlanData.setService(eqVlan.getCvlanTyp().toString());
            vlanData.setSvlan(eqVlan.getSvlanMdu());
            vlanData.setSvlanBackbone(eqVlan.getSvlanOlt());
            vlanData.setType(eqVlan.getCvlanTyp().getType().toString());
            return vlanData;
        }
    };

    @Override
    public boolean isFttbAuftrag(final Long auftragId) throws FindException {
        final HWRack rack = findRackForFttb_h(auftragId);
        return (rack instanceof HWMdu || rack instanceof HWDpo);
    }

    @Override
    public boolean isFtthAuftrag(Long auftragId) throws FindException {
        final HWRack rack = findRackForFttb_h(auftragId);
        return (rack instanceof HWOnt);
    }

    @Override
    public CPSFTTBData getFttbData4Wholesale(final Long auftragId, final String portId, Date when) throws FindException {
        HWRack rack = findRackForFttb_h(auftragId);
        if (!(rack instanceof HWMdu)) {
            return null;
        }

        // FTTB per OLT/GSLAM
        HWMdu mdu = (HWMdu) rack;
        HWRack oltOrGslam = hwService.findRackById(mdu.getOltRackId());
        if (oltOrGslam == null) {
            throw new FindException("Die OLT der MDU konnte nicht gefunden werden!");
        }

        Endstelle endstelleB = cpsFacade.findEndstelleB(auftragId);
        Rangierung rang = findRangierung(endstelleB);
        Equipment eqIn = findEquipment(rang);
        HVTTechnik manufacturer = hvtService.findHVTTechnik(mdu.getHwProducer());
        HVTGruppe hvtGruppe = findHvtGruppe(endstelleB);
        List<EqVlan> vlans = vlanService.findEqVlans(eqIn.getId(), when);

        CPSFTTBData fttbData = createFTTBData(portId, mdu, oltOrGslam, manufacturer, eqIn, hvtGruppe.getOrtsteil());

        DSLAMProfile dslamProfile = dslamService.findDslamProfile4AuftragOrCalculateDefault(auftragId, when);
        if (dslamProfile != null) {
            fttbData.setDownstream(dslamProfile.getBandwidth().getDownstreamAsString());
            fttbData.setUpstream(dslamProfile.getBandwidth().getUpstreamAsString());
        }

        fttbData.setBucht(eqIn.getRangBucht());
        fttbData.setLeiste(eqIn.getRangLeiste1());
        fttbData.setStift(eqIn.getRangStift1());

        fttbData.setVlans(Lists.transform(vlans, TO_CPS_VLAN_DATA));

        pbitHelper.addVoipPbitIfNecessary(fttbData, dslamProfile, auftragId, when);
        return fttbData;
    }

    public CpsAccessDevice getFttbData(Long auftragId, String portName, Date when) {
        final Equipment oltChildPort;
        final HWBaugruppe hwBaugruppe;
        final HWOltChild endpointRack;
        final HWRack networkRack; // OLT oder GSLAM -> nur Rack
        final DSLAMProfile dslamProfile;
        final ProfileAuftrag auftragProfile;
        final boolean isProfileDSLAM;
        try {
            final Endstelle endstelleB = cpsFacade.getEndstelleBWithStandortId(auftragId);
            oltChildPort = cpsFacade.findFttxPort(endstelleB);
            hwBaugruppe = cpsFacade.findBaugruppeByPort(oltChildPort);
            endpointRack = findFttxRack(oltChildPort, HWOltChild.class);
            networkRack = hwService.findRackById(endpointRack.getOltRackId());
            isProfileDSLAM = !BooleanTools.nullToFalse(hwBaugruppe.getHwBaugruppenTyp().isProfileAssignable());
            if (isProfileDSLAM) {
                auftragProfile = null;
                dslamProfile = dslamService.findDslamProfile4AuftragOrCalculateDefault(auftragId, when);
            } else {
                dslamProfile = null;
                auftragProfile = profileService.findProfileAuftragForDate(auftragId, when);
            }
        } catch (FindException e) {
            throw new RuntimeException(e);
        }

        final CpsAccessDevice accessDevice = new CpsAccessDevice();

        final CpsNetworkDevice networkDevice = getNetworkDevice(endpointRack.getGponPort(), networkRack);
        final CpsEndpointDevice endpointDevice = getEndpointDevice(endpointRack);

        setPortData4EndpointDevice(endpointDevice, oltChildPort, hwBaugruppe, portName);
        if (isProfileDSLAM) {
            setDSLAMProfileData4FttbEndpointDevice(endpointDevice, dslamProfile);
        } else {
            setAuftragProfileData4FttbEndpointDevice(endpointDevice, auftragProfile);
        }
        if (endpointRack instanceof HWMdu) {
            setIpAddressData4FttbEndpointDevice(endpointDevice, (HWMdu) endpointRack);
        }

        final CpsLayer2Config layerTwoConfig = getLayer2Config(auftragId, when, dslamProfile, oltChildPort);
        CpsItem cpsItem = getCpsItem(networkDevice, endpointDevice, layerTwoConfig);

        accessDevice.setItems(ImmutableList.of(cpsItem));

        return accessDevice;
    }

    protected final HWRack findRackForFttb_h(final Long auftragId) throws FindException {
        final Endstelle endstelleB = cpsFacade.findEndstelleB(auftragId);
        final Rangierung rangierung = findRangierung(endstelleB);
        final Equipment eqIn = findEquipment(rangierung);
        final HWBaugruppe baugruppe = hwService.findBaugruppe(eqIn.getHwBaugruppenId());
        return hwService.findRackById(baugruppe.getRackId());
    }

    protected final Equipment findEquipment(Rangierung rangierung) throws FindException {
        Equipment eqIn = rangierungsService.findEquipment(rangierung.getEqInId());
        if (eqIn == null) {
            throw new FindException("EQ-IN Equipment konnte nicht geladen werden!");
        }
        if (eqIn.getHwBaugruppenId() == null) {
            throw new FindException("EQ-IN Equipment ist keiner Baugruppe zugeordnet!");
        }
        return eqIn;
    }

    protected final HVTGruppe findHvtGruppe(Endstelle esB) throws FindException {
        HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(esB.getHvtIdStandort());
        if ((hvtGruppe == null) || StringUtils.isBlank(hvtGruppe.getOrtsteil())) {
            throw new FindException("Standort nicht ermittelbar!");
        }
        return hvtGruppe;
    }

    protected final Rangierung findRangierung(Endstelle esB) throws FindException {
        Rangierung rang = rangierungsService.findRangierung(esB.getRangierId());
        if ((rang == null) || (rang.getEqInId() == null)) {
            throw new FindException("Rangierung nicht gefunden oder FTTX-Equipment nicht definiert!");
        }
        return rang;
    }

    protected final CPSFTTBData createFTTBData(String portId, HWMdu mdu, HWRack oltOrGslam, HVTTechnik manufacturer,
                                               Equipment eqIn, String standort) throws FindException {
        CPSFTTBData fttb = new CPSFTTBData();
        fttb.setManufacturer(manufacturer.getCpsName());
        fttb.setMduGeraeteBezeichnung(mdu.getGeraeteBez());
        fttb.setMduStandort(standort);
        fttb.setMduTyp(mdu.getMduType());
        fttb.setSerialNo(mdu.getSerialNo());
        fttb.setOltRackId(mdu.getOltRackId());

        String gpon = buildGponPort(mdu);
        fttb.setGponPort(StringUtils.trimToNull(gpon));

        fttb.setOltGeraeteBezeichnung(StringUtils.trimToNull(oltOrGslam.getGeraeteBez()));
        fttb.setBaugruppenPort(StringUtils.trimToNull(eqIn.getHwEQN())); // evtl. Ermittlung des MDU-Ports ueber BG-Typ
        // und RegExp
        fttb.setPortTyp(StringUtils.trimToNull(eqIn.getHwSchnittstelle()));
        fttb.setPortId(StringUtils.trimToNull(portId));

        return fttb;
    }

    protected final String buildGponPort(HWMdu mdu) {
        List<String> gponElements = new ArrayList<String>();
        gponElements.add(mdu.getOltFrame());
        gponElements.add(mdu.getOltSubrack());
        gponElements.add(mdu.getOltSlot());
        gponElements.add(mdu.getOltGPONPort());
        gponElements.add(mdu.getOltGPONId());
        String gpon = StringTools.join(gponElements.toArray(new String[gponElements.size()]), "-", true);
        return gpon;
    }

    @Override
    public Pair<HVTGruppe, HVTStandort> findHvtGruppeAndStandort(Long auftragId, String esTyp) throws FindException {
        if (!((esTyp.equals(Endstelle.ENDSTELLEN_TYP_A) || (esTyp.equals(Endstelle.ENDSTELLEN_TYP_B))))) {
            throw new FindException(FindException.INSUFFICIENT_FIND_PARAMETER);
        }

        Endstelle esB = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);

        if ((esB == null) || (esB.getHvtIdStandort() == null)) {
            throw new FindException(
                    "Endstelle B oder technischen Standort auf Endstelle B nicht gefunden!");
        }

        HVTStandort hvtStd = hvtService.findHVTStandort(esB.getHvtIdStandort());
        HVTGruppe hvtGruppe = (hvtStd != null) ? hvtService.findHVTGruppeById(hvtStd.getHvtGruppeId()) : null;
        if ((hvtStd == null) || (hvtGruppe == null)) {
            throw new FindException("HVTStandort bzw. Gruppe für Endstelle B nicht gefunden!");
        }

        return Pair.create(hvtGruppe, hvtStd);
    }

    @Override
    public CpsAccessDevice getFtthData(final long auftragId, final Date when, final boolean deviceNecessary,
                                       final String portName) {

        final Equipment ontPort;
        final HWBaugruppe ontBaugruppe;
        final HWOnt endpointRack;
        final HWRack networkRack;
        final DSLAMProfile profile;

        try {
            final Endstelle endstelleB = cpsFacade.getEndstelleBWithStandortId(auftragId);
            ontPort = cpsFacade.findFttxPort(endstelleB);
            ontBaugruppe = cpsFacade.findBaugruppeByPort(ontPort);
            endpointRack = findFttxRack(ontPort, HWOnt.class);
            networkRack = hwService.findRackById(endpointRack.getOltRackId());
            profile = dslamService.findDslamProfile4AuftragOrCalculateDefault(auftragId, when);
        } catch (FindException e) {
            throw new RuntimeException(e);
        }

        final CpsAccessDevice accessDevice = new CpsAccessDevice();

        if (endpointRack.getSerialNo() == null) {
            throw new RuntimeException(String.format("dem Rack %s ist keine Seriennr. zugewiesen", endpointRack.getGeraeteBez()));
        }

        final CpsNetworkDevice networkDevice = getNetworkDevice(endpointRack.getGponPort(), networkRack);
        final CpsEndpointDevice endpointDevice = getEndpointDevice(endpointRack);

        setPortData4EndpointDevice(endpointDevice, ontPort, ontBaugruppe, portName);
        setProfileData4FtthEndpointDevice(endpointDevice, deviceNecessary, ontBaugruppe, profile);
        final CpsLayer2Config layerTwoConfig = getLayer2Config(auftragId, when, profile, ontPort);
        CpsItem cpsItem = getCpsItem(networkDevice, endpointDevice, layerTwoConfig);

        accessDevice.setItems(ImmutableList.of(cpsItem));

        return accessDevice;
    }

    private <T extends HWRack> T findFttxRack(Equipment ontPort, Class<T> clazz) throws FindException {
        final HWRack hwRack = cpsFacade.findRackByPort(ontPort);
        if (!(clazz.isInstance(hwRack))) {
            throw new RuntimeException(format("Rack %s muss vom Typ %s sein, ist aber vom Typ %s", hwRack.getId(),
                    clazz.getName(), hwRack.getRackTyp()));
        }
        return (T) hwRack;
    }

    private CpsItem getCpsItem(final CpsNetworkDevice networkDevice, final CpsEndpointDevice endpointDevice,
                               final CpsLayer2Config layerTwoConfig) {
        final CpsItem cpsItem = new CpsItem();
        cpsItem.setNetworkDevice(networkDevice);
        cpsItem.setEndpointDevice(endpointDevice);
        cpsItem.setLayer2Config(layerTwoConfig);
        return cpsItem;
    }

    private CpsLayer2Config getLayer2Config(final long auftragId, final Date execDate, final DSLAMProfile profile, final Equipment ontPort) {
        List<CPSPBITData> pbits = getCpsPbitDatas(auftragId, execDate, profile);
        final List<EqVlan> vlans = vlanService.findEqVlans(ontPort.getId(), execDate);

        final CpsLayer2Config layerTwoConfig = new CpsLayer2Config();
        layerTwoConfig.setPbits(pbits);
        layerTwoConfig.setVlans(Lists.transform(vlans, TO_CPS_VLAN_DATA));
        return layerTwoConfig;
    }

    private List<CPSPBITData> getCpsPbitDatas(long auftragId, Date execDate, DSLAMProfile profile) {
        final CPSPBITData voipPbit;
        try {
            voipPbit = pbitHelper.getVoipPbitIfNecessary(profile, auftragId, execDate);
        } catch (FindException e) {
            throw new RuntimeException(e);
        }
        List<CPSPBITData> pbits = Lists.newArrayListWithCapacity(1);
        if (voipPbit != null) {
            pbits.add(voipPbit);
        }
        return pbits;
    }

    @Override
    public CpsNetworkDevice getNetworkDevice(final String oltGponPort, final HWRack networkRack) {
        final HVTTechnik manufacturer = findManufacturer(networkRack);
        final CpsNetworkDevice networkDevice = new CpsNetworkDevice();
        String deviceType = determineNetworkDeviceType(networkRack);
        networkDevice.setType(deviceType);
        networkDevice.setManufacturer(manufacturer.getCpsName());
        networkDevice.setName(networkRack.getGeraeteBez());
        networkDevice.setPort(oltGponPort);
        networkDevice.setPortType("GPON");
        return networkDevice;
    }

    private HVTTechnik findManufacturer(HWRack networkRack) {
        final HVTTechnik manufacturer;
        try {
            manufacturer = hvtService.findHVTTechnik(networkRack.getHwProducer());
        } catch (FindException e) {
            throw new RuntimeException(e);
        }
        return manufacturer;
    }

    private String determineNetworkDeviceType(HWRack networkRack) {
        String deviceType;
        switch (networkRack.getRackTyp()) {
            case HWRack.RACK_TYPE_DSLAM:
                deviceType = "GSLAM";
                break;
            case HWRack.RACK_TYPE_OLT:
                deviceType = "OLT";
                break;
            default:
                throw new IllegalStateException(format("Rack vom Typ %s wird nicht an einem Fttb/h-Standort erwartet", networkRack.getRackTyp()));
        }
        return deviceType;
    }

    @Override
    public CpsEndpointDevice getEndpointDevice(HWOltChild endpointRack) {
        final HVTGruppe endpointDeviceStandortGruppe;
        final String standortTyp;
        try {
            endpointDeviceStandortGruppe = findHvtGruppeForEndpointRack(endpointRack);
            standortTyp = getEndpointDeviceStandortTyp(endpointRack);
        } catch (FindException e) {
            throw new RuntimeException(e);
        }

        final CpsEndpointDevice endpointDevice = new CpsEndpointDevice();
        endpointDevice.setType(determineEndpointDeviceType(endpointRack));
        endpointDevice.setHardwareModel(determineHardwareModel(endpointRack));
        endpointDevice.setManufacturer(findManufacturer(endpointRack).getCpsName());
        endpointDevice.setTechId(endpointDeviceStandortGruppe.getOrtsteil());
        endpointDevice.setName(endpointRack.getGeraeteBez());
        endpointDevice.setSerialNo(endpointRack.getSerialNo());
        endpointDevice.setLocationType(standortTyp);
        return endpointDevice;
    }


    private String determineHardwareModel(HWRack endpointRack) {
        switch (endpointRack.getRackTyp()) {
            case HWRack.RACK_TYPE_MDU:
                return ((HWMdu) endpointRack).getMduType();
            case HWRack.RACK_TYPE_ONT:
                return ((HWOnt) endpointRack).getOntType();
            case HWRack.RACK_TYPE_DPO:
                return ((HWDpo) endpointRack).getDpoType();
            case HWRack.RACK_TYPE_DPU:
                return ((HWDpu) endpointRack).getDpuType();
            default:
                break;
        }
        throw new IllegalStateException(format("Rack vom Typ %s wird nicht an einem Ftth-Standort erwartet", endpointRack.getRackTyp()));
    }

    private String determineEndpointDeviceType(HWRack endpointRack) {
        String deviceType;
        switch (endpointRack.getRackTyp()) {
            case HWRack.RACK_TYPE_MDU:
                deviceType = "MDU";
                break;
            case HWRack.RACK_TYPE_ONT:
                deviceType = "ONT";
                break;
            case HWRack.RACK_TYPE_DPO:
                deviceType = "DPO";
                break;
            case HWRack.RACK_TYPE_DPU:
                deviceType = "DPU";
                break;
            default:
                throw new IllegalStateException(format("Rack vom Typ %s wird nicht an einem Ftth-Standort erwartet", endpointRack.getRackTyp()));
        }
        return deviceType;
    }

    /**
     * Ergaenzt ein endpoint device um Port spezifische Daten.
     */
    private void setPortData4EndpointDevice(CpsEndpointDevice endpointDevice, Equipment port, HWBaugruppe baugruppe,
                                            String portName) {
        endpointDevice.setPort(port.getHwEQN());
        endpointDevice.setPortName(portName);
        if (baugruppe != null && baugruppe.getHwBaugruppenTyp() != null) {
            endpointDevice.setPortType(baugruppe.getHwBaugruppenTyp().getHwSchnittstelleName());
        }
    }

    private void setIpAddressData4FttbEndpointDevice(CpsEndpointDevice endpointDevice, HWMdu mduRack) {
        endpointDevice.setIpAddress(mduRack.getIpAddress());
    }

    /**
     * Ergaenzt ein endpoint device um DSLAM Profil spezifische Daten (fuer FTTH).
     */
    private void setProfileData4FtthEndpointDevice(CpsEndpointDevice endpointDevice, boolean deviceNecessary,
                                                   HWBaugruppe baugruppe, @CheckForNull DSLAMProfile profile) {
        if (profile != null) {
            if (deviceNecessary || baugruppe.getHwBaugruppenTyp().isFtthEthernetPort()) {
                setDownAndUpstream4EndpointDevice(endpointDevice, profile);
            }
            setDownAndUpMargin4EndpointDevice(endpointDevice, profile);
        }

    }

    /**
     * Ergaenzt ein endpoint device um DSLAM Profil spezifische Daten (fuer FTTB).
     */
    private void setDSLAMProfileData4FttbEndpointDevice(CpsEndpointDevice endpointDevice,
                                                        @CheckForNull DSLAMProfile profile) {
        if (profile != null) {
            setDownAndUpstream4EndpointDevice(endpointDevice, profile);
            setDownAndUpMargin4EndpointDevice(endpointDevice, profile);
        }

    }

    /**
     * Ergaenzt ein endpoint device um Auftrag-Profil spezifische Daten (fuer FTTB mit zugeordneten AuftragProfil).
     */
    private void setAuftragProfileData4FttbEndpointDevice(CpsEndpointDevice endpointDevice,
                                                          @CheckForNull ProfileAuftrag profile) {
        if (profile != null) {
            final Set<ProfileAuftragValue> valueSet = profile.getProfileAuftragValues();
            final List<ProfileParameterMapper> parameterMapperList = profileService.findParameterMappers(valueSet);
            endpointDevice.setProfileUpstream(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.UPSTREAM));
            endpointDevice.setProfileDownstream(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.DOWNSTREAM));
            endpointDevice.setProfileUpbo(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.UPBO));
            endpointDevice.setProfileDpbo(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.DPBO));
            endpointDevice.setProfileRfi(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.RFI));
            endpointDevice.setProfileNoiseMargin(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.NOISE_MARGIN));
            endpointDevice.setProfileInpDelay(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.INP_DELAY));
            endpointDevice.setProfileVirtualNoise(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.VIRTUAL_NOISE));
            endpointDevice.setProfileLineSpectrum(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.LINE_SPECTRUM));
            endpointDevice.setProfileInm(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.INM));
            endpointDevice.setProfileSos(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.SOS));
            endpointDevice.setProfileTrafficTableUpstream(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.TRAFFIC_TABLE_UPSTREAM));
            endpointDevice.setProfileTrafficTableDownstream(
                    ProfileAuftragValueUtil.getValueForCpsName(valueSet, parameterMapperList, ProfileAuftragValueUtil.CpsName.TRAFFIC_TABLE_DOWNSTREAM));
        }
    }

    private void setDownAndUpstream4EndpointDevice(CpsEndpointDevice endpointDevice, DSLAMProfile profile) {
        endpointDevice.setDownstream(profile.getBandwidth().getDownstreamAsString());
        endpointDevice.setUpstream(profile.getBandwidth().getUpstreamAsString());
    }

    private void setDownAndUpMargin4EndpointDevice(CpsEndpointDevice endpointDevice, DSLAMProfile profile) {
        if (profile.getTmDown() != null) {
            endpointDevice.setTargetMarginDown(profile.getTmDown().toString());
        }
        if (profile.getTmUp() != null) {
            endpointDevice.setTargetMarginUp(profile.getTmUp().toString());
        }
    }

    private String getEndpointDeviceStandortTyp(HWRack endpointRack) throws FindException {
        String standortTyp;
        final HVTStandort standort = findHvtStandortWithStandorTyp(endpointRack);
        standortTyp = referenceService.findReference(standort.getStandortTypRefId()).getStrValue();
        return standortTyp;
    }

    private HVTStandort findHvtStandortWithStandorTyp(HWRack endpointRack) throws FindException {
        final HVTStandort standort = hvtService.findHVTStandort(endpointRack.getHvtIdStandort());
        if (standort.getStandortTypRefId() == null) {
            throw new RuntimeException(format("Der Typ des Standorts mit Id %s ist nicht gesetzt", standort.getHvtIdStandort()));
        }
        return standort;
    }

    private HVTGruppe findHvtGruppeForEndpointRack(HWRack endpointRack) throws FindException {
        HVTGruppe endpointDeviceStandortGruppe;
        endpointDeviceStandortGruppe = hvtService.findHVTGruppe4Standort(endpointRack.getHvtIdStandort());
        if (endpointDeviceStandortGruppe == null) {
            throw new RuntimeException(format("Der Standort mit Id %s befindet sich in keiner Standort-Gruppe", endpointRack.getHvtIdStandort()));
        }
        return endpointDeviceStandortGruppe;
    }
}


