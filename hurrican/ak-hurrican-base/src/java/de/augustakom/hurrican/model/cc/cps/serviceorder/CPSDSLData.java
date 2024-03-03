/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 10:39:09
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


/**
 * Modell-Klasse fuer die Abbildung von DSL-Daten zur CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("DSL")
public class CPSDSLData extends AbstractCPSServiceOrderDataModel implements PbitAware {

    public static final String PORT_ID_SEPARATOR = "-";

    public static final int PORT_ID_MAX_LENGTH = 32;

    /**
     * Transfer-Methode fuer ADSL 1
     */
    public static final String TRANSFER_METHOD_ADSL = "ADSL";
    /**
     * Transfer-Methode fuer ADSL2+
     */
    public static final String TRANSFER_METHOD_ADSL2P = "ADSL2+";

    @XStreamAlias("PROTECTED")
    private String protectedFlag = null;
    @XStreamAlias("PORT_ID")
    private String portId = null;
    @XStreamAlias("KVZ_NR")
    private String kvzNummer = null;
    @XStreamAlias("DSLAM_NAME")
    private String dslamName = null;
    @XStreamAlias("DSLAM_MANUFACTURER")
    private String dslamManufacturer = null;
    @XStreamAlias("DSLAM_TYPE")
    private String dslamType = null;
    @XStreamAlias("DSLAM_PORT")
    private String dslamPort = null;
    @XStreamAlias("DSLAM_PORT_TYPE")
    private String dslamPortType = null;
    @XStreamAlias("CARD_TYPE")
    private String cardType = null;
    @XStreamAlias("PHY_TYPE")
    private String physicType = null;
    @XStreamAlias("WIRES")
    private String wires = null;
    @XStreamAlias("TRANSFER_METHOD")
    private String transferMethod = null;
    @XStreamAlias("DOWNSTREAM")
    private String downstream = null;
    @XStreamAlias("UPSTREAM")
    private String upstream = null;
    @XStreamAlias("DS_NETTO")
    private String downstreamNetto = null;
    @XStreamAlias("US_NETTO")
    private String upstreamNetto = null;
    @XStreamAlias("FASTPATH")
    private String fastpath = null;
    @XStreamAlias("TM_DOWN")
    private String tmDown = null;
    @XStreamAlias("TM_UP")
    private String tmUp = null;
    @XStreamAlias("L2PS_MODE")
    private String l2PowerSafeMode = null;
    @XStreamAlias("FORCE_ADSL1")
    private String forceADSL1 = null;
    @XStreamAlias("CROSS_CONNECTIONS")
    private List<CPSDSLCrossConnectionData> crossConnections = null;
    @XStreamAlias("PBIT")
    private List<CPSPBITData> pbits = Lists.newArrayList();

    @Override
    public void addPbit(final @Nonnull CPSPBITData pbit) {
        pbits.add(pbit);
    }

    @Nonnull
    public List<CPSPBITData> getPbits() {
        return pbits;
    }

    @CheckForNull
    @XStreamAlias("SLAVE_PORTS")
    public SlavePorts fttcBondingSlavePorts;

    public static final class SlavePorts {
        public SlavePorts(List<String> fttcBondingSlavePorts) {
            this.fttcBondingSlavePorts = fttcBondingSlavePorts;
        }

        @XStreamImplicit(itemFieldName = "SLAVE_PORT")
        final List<String> fttcBondingSlavePorts;

        @Override
        public int hashCode() {
            return Objects.hash(fttcBondingSlavePorts);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {return true;}
            if (obj == null || getClass() != obj.getClass()) {return false;}
            final SlavePorts other = (SlavePorts) obj;
            return Objects.equals(this.fttcBondingSlavePorts, other.fttcBondingSlavePorts);
        }
    }

    /**
     * Fuegt dem DSL-Part eine weitere CrossConnection hinzu.
     *
     * @param toAdd
     */
    public void addCrossConnection(CPSDSLCrossConnectionData toAdd) {
        if (crossConnections == null) {
            crossConnections = new ArrayList<CPSDSLCrossConnectionData>();
        }
        crossConnections.add(toAdd);
    }

    public String getProtectedFlag() {
        return protectedFlag;
    }

    public void setProtectedFlag(String protectedFlag) {
        this.protectedFlag = protectedFlag;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    public String getDslamName() {
        return dslamName;
    }

    public void setDslamName(String dslamName) {
        this.dslamName = dslamName;
    }

    public String getDslamManufacturer() {
        return dslamManufacturer;
    }

    public void setDslamManufacturer(String dslamManufacturer) {
        this.dslamManufacturer = dslamManufacturer;
    }

    public String getDslamPort() {
        return dslamPort;
    }

    public void setDslamPort(String dslamPort) {
        this.dslamPort = dslamPort;
    }

    public String getDslamPortType() {
        return dslamPortType;
    }

    public void setDslamPortType(String dslamPortType) {
        this.dslamPortType = dslamPortType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPhysicType() {
        return physicType;
    }

    public void setPhysicType(String physicType) {
        this.physicType = physicType;
    }

    public String getWires() {
        return wires;
    }

    public void setWires(String wires) {
        this.wires = wires;
    }

    public String getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(String transferMethod) {
        this.transferMethod = transferMethod;
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

    public String getDownstreamNetto() {
        return downstreamNetto;
    }

    public void setDownstreamNetto(String downstreamNetto) {
        this.downstreamNetto = downstreamNetto;
    }

    public String getUpstreamNetto() {
        return upstreamNetto;
    }

    public void setUpstreamNetto(String upstreamNetto) {
        this.upstreamNetto = upstreamNetto;
    }

    public String getFastpath() {
        return fastpath;
    }

    public void setFastpath(String fastpath) {
        this.fastpath = fastpath;
    }

    public String getTmDown() {
        return tmDown;
    }

    public void setTmDown(String tmDown) {
        this.tmDown = tmDown;
    }

    public String getTmUp() {
        return tmUp;
    }

    public void setTmUp(String tmUp) {
        this.tmUp = tmUp;
    }

    public String getL2PowerSafeMode() {
        return l2PowerSafeMode;
    }

    public void setL2PowerSafeMode(String l2PowerSafeMode) {
        this.l2PowerSafeMode = l2PowerSafeMode;
    }

    public String getForceADSL1() {
        return forceADSL1;
    }

    public void setForceADSL1(String forceADSL1) {
        this.forceADSL1 = forceADSL1;
    }

    public List<CPSDSLCrossConnectionData> getCrossConnections() {
        return crossConnections;
    }

    public void setCrossConnections(List<CPSDSLCrossConnectionData> crossConnections) {
        this.crossConnections = crossConnections;
    }

    public String getDslamType() {
        return dslamType;
    }

    public void setDslamType(String dlamType) {
        this.dslamType = dlamType;
    }

}


