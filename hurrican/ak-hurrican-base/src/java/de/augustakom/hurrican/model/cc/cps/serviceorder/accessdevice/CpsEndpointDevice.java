/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by guiber on 02.05.2014.
 */
@XStreamAlias("ENDPOINT_DEVICE")
public class CpsEndpointDevice extends AbstractCpsDevice {

    @XStreamAlias("TECH_ID")
    private String techId;

    @XStreamAlias("SERIAL_NO")
    private String serialNo;

    @XStreamAlias("LOCATION_TYPE")
    private String locationType;

    @XStreamAlias("KVZ_NR")
    private String kvzNr;

    @XStreamAlias("CARD_TYPE")
    private String cardType;

    @XStreamAlias("DOWNSTREAM")
    private String downstream;

    @XStreamAlias("UPSTREAM")
    private String upstream;

    @XStreamAlias("PORT_NAME")
    private String portName;

    @XStreamAlias("TARGET_MARGIN_DOWN")
    private String targetMarginDown;

    @XStreamAlias("TARGET_MARGIN_UP")
    private String targetMarginUp;

    @XStreamAlias("FASTPATH")
    private String fastpath;

    @XStreamAlias("INTERLEAVING_DELAY")
    private String interleavingDelay;

    @XStreamAlias("IMPULSE_NOISE_PROTECTION")
    private String impulseNoiseProtection;

    @XStreamAlias("TRANSFER_MODE_OVERRIDE")
    private String transferModeOverride;

    @XStreamAlias("WIRES")
    private String wires;

    @XStreamAlias("LINE_TYPE")
    private String lineType;

    @XStreamAlias("IP_ADDRESS")
    private String ipAddress;

    @XStreamAlias("IP_MASK")
    private String ipMask;

    /**
     * TDD-Profil/Parameter (pro DPU)
     */
    @XStreamAlias("GFASTProfId")
    private String profileTdd;

    /**
     * Upstream Data Rate Profil/Parameter(pro Anschluss)
     */
    @XStreamAlias("DataRateUp")
    private String profileUpstream;

    /**
     * Downstream Data Rate Parameter(pro Anschluss)
     */
    @XStreamAlias("DataRateDown")
    private String profileDownstream;

    /**
     * UPBO-Parameter(pro Anschluss)
     */
    @XStreamAlias("PowerBackOffUp")
    private String profileUpbo;

    /**
     * DPBO-Parameter(pro Anschluss)
     */
    @XStreamAlias("PowerBackOffDown")
    private String profileDpbo;

    /**
     * RFI-Parameter(pro Anschluss)
     */
    @XStreamAlias("RadioFrequencyInterference")
    private String profileRfi;

    /**
     * Noise Margin Parameter(pro Anschluss)
     */
    @XStreamAlias("NoiseMargin")
    private String profileNoiseMargin;

    /**
     * INP-Delay-Parameter(pro Anschluss)
     */
    @XStreamAlias("ImpulseNoiseProtectionDelay")
    private String profileInpDelay;

    /**
     * Virtual Noise Parameter(pro Anschluss)
     */
    @XStreamAlias("VirtualNoise")
    private String profileVirtualNoise;

    /**
     * Line Spectrum Parameter(pro Anschluss)
     */
    @XStreamAlias("Spectrum")
    private String profileLineSpectrum;

    /**
     * INM-Parameter(pro Anschluss)
     */
    @XStreamAlias("ImpulseNoiseMonitoring")
    private String profileInm;

    /**
     * SOS-Parameter(pro Anschluss)
     */
    @XStreamAlias("Sos")
    private String profileSos;

    /**
     * 'Traffic Table Upstream'-Parameter (pro Anschluss)
     */
    @XStreamAlias("TrafficTableUp")
    private String profileTrafficTableUpstream;

    /**
     * 'Traffic Table Downstream' (pro Anschluss)
     */
    @XStreamAlias("TrafficTableDown")
    private String profileTrafficTableDownstream;

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getKvzNr() {
        return kvzNr;
    }

    public void setKvzNr(String kvzNr) {
        this.kvzNr = kvzNr;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getDownstream() {
        return downstream;
    }

    public void setDownstream(String downstream) {
        this.downstream = downstream;
    }

    public String getUpstream() {
        return upstream;
    }

    public void setUpstream(String upstream) {
        this.upstream = upstream;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getTargetMarginDown() {
        return targetMarginDown;
    }

    public void setTargetMarginDown(String targetMarginDown) {
        this.targetMarginDown = targetMarginDown;
    }

    public String getTargetMarginUp() {
        return targetMarginUp;
    }

    public void setTargetMarginUp(String targetMarginUp) {
        this.targetMarginUp = targetMarginUp;
    }

    public String getFastpath() {
        return fastpath;
    }

    public void setFastpath(String fastpath) {
        this.fastpath = fastpath;
    }

    public String getInterleavingDelay() {
        return interleavingDelay;
    }

    public void setInterleavingDelay(String interleavingDelay) {
        this.interleavingDelay = interleavingDelay;
    }

    public String getImpulseNoiseProtection() {
        return impulseNoiseProtection;
    }

    public void setImpulseNoiseProtection(String impulseNoiseProtection) {
        this.impulseNoiseProtection = impulseNoiseProtection;
    }

    public String getTransferModeOverride() {
        return transferModeOverride;
    }

    public void setTransferModeOverride(String transferModeOverride) {
        this.transferModeOverride = transferModeOverride;
    }

    public String getWires() {
        return wires;
    }

    public void setWires(String wires) {
        this.wires = wires;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpMask() {
        return ipMask;
    }

    public void setIpMask(String ipMask) {
        this.ipMask = ipMask;
    }

    public String getProfileTdd() {
        return profileTdd;
    }

    public void setProfileTdd(String profileTdd) {
        this.profileTdd = profileTdd;
    }


    public String getProfileUpstream() {
        return profileUpstream;
    }

    public void setProfileUpstream(String profileUpstream) {
        this.profileUpstream = profileUpstream;
    }

    public String getProfileDownstream() {
        return profileDownstream;
    }

    public void setProfileDownstream(String profileDownstream) {
        this.profileDownstream = profileDownstream;
    }

    public String getProfileUpbo() {
        return profileUpbo;
    }

    public void setProfileUpbo(String profileUpbo) {
        this.profileUpbo = profileUpbo;
    }

    public String getProfileDpbo() {
        return profileDpbo;
    }

    public void setProfileDpbo(String profileDpbo) {
        this.profileDpbo = profileDpbo;
    }

    public String getProfileRfi() {
        return profileRfi;
    }

    public void setProfileRfi(String profileRfi) {
        this.profileRfi = profileRfi;
    }

    public String getProfileNoiseMargin() {
        return profileNoiseMargin;
    }

    public void setProfileNoiseMargin(String profileNoiseMargin) {
        this.profileNoiseMargin = profileNoiseMargin;
    }

    public String getProfileInpDelay() {
        return profileInpDelay;
    }

    public void setProfileInpDelay(String profileInpDelay) {
        this.profileInpDelay = profileInpDelay;
    }

    public String getProfileVirtualNoise() {
        return profileVirtualNoise;
    }

    public void setProfileVirtualNoise(String profileVirtualNoise) {
        this.profileVirtualNoise = profileVirtualNoise;
    }

    public String getProfileLineSpectrum() {
        return profileLineSpectrum;
    }

    public void setProfileLineSpectrum(String profileLineSpectrum) {
        this.profileLineSpectrum = profileLineSpectrum;
    }

    public String getProfileInm() {
        return profileInm;
    }

    public void setProfileInm(String profileInm) {
        this.profileInm = profileInm;
    }

    public String getProfileSos() {
        return profileSos;
    }

    public void setProfileSos(String profileSos) {
        this.profileSos = profileSos;
    }

    public String getProfileTrafficTableUpstream() {
        return profileTrafficTableUpstream;
    }

    public void setProfileTrafficTableUpstream(String profileTrafficTableUpstream) {
        this.profileTrafficTableUpstream = profileTrafficTableUpstream;
    }

    public String getProfileTrafficTableDownstream() {
        return profileTrafficTableDownstream;
    }

    public void setProfileTrafficTableDownstream(String profileTrafficTableDownstream) {
        this.profileTrafficTableDownstream = profileTrafficTableDownstream;
    }
}
