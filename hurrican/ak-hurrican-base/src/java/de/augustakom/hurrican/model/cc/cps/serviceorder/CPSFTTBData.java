/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2009 16:30:35
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import de.augustakom.hurrican.model.shared.iface.ISerialNoAwareModel;


/**
 * Modell-Klasse zur Abbildung von FTTB-Daten.
 *
 *
 */
@XStreamAlias("FTTB")
public class CPSFTTBData extends AbstractCPSServiceOrderDataModel implements PbitAware, ISerialNoAwareModel {

    private static final long serialVersionUID = -6117880548145465711L;

    @XStreamAlias("MANUFACTURER")
    private String manufacturer;
    @XStreamAlias("FRAME")
    private String frame;
    @XStreamAlias("SHELF")
    private String shelf;
    @XStreamAlias("GPON_PORT")
    private String gponPort;
    @XStreamAlias("GPON_ID")
    private String gponId;
    @XStreamAlias("PORTID")
    private String portId;
    @XStreamAlias("VLANS")
    private List<CPSVlanData> vlans;
    @XStreamAlias("OLT_GERAETE_BEZEICHNUNG")
    private String oltGeraeteBezeichnung;
    @XStreamAlias("DOWNSTREAM")
    private String downstream;
    @XStreamAlias("UPSTREAM")
    private String upstream;
    @XStreamAlias("PBIT")
    private List<CPSPBITData> pbits = Lists.newArrayList();
    @XStreamAlias("MDU_TYP")
    private String mduTyp;
    @XStreamAlias("MDU_GERAETE_BEZEICHNUNG")
    private String mduGeraeteBezeichnung;
    @XStreamAlias("MDU_SERIAL_NO")
    private String serialNo;
    @XStreamAlias("MDU_STANDORT")
    private String mduStandort;
    @XStreamAlias("BG_PORT")
    private String baugruppenPort;
    @XStreamAlias("PORT_TYPE")
    private String portTyp;

    @XStreamOmitField
    private String bucht;
    @XStreamOmitField
    private String leiste;
    @XStreamOmitField
    private String stift;
    // temporary field; just to get the Id of the assigned OLT of the MDU
    @XStreamOmitField
    private Long oltRackId;

    @Override
    public void addPbit(final @Nonnull CPSPBITData pbit) {
        pbits.add(pbit);
    }

    @Nonnull
    public List<CPSPBITData> getPbits() {
        return pbits;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getGponPort() {
        return gponPort;
    }

    public void setGponPort(String gponPort) {
        this.gponPort = gponPort;
    }

    public String getGponId() {
        return gponId;
    }

    public void setGponId(String gponId) {
        this.gponId = gponId;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public List<CPSVlanData> getVlansNotNull() {
        if (vlans == null) {
            return ImmutableList.of();
        }
        else {
            return vlans;
        }
    }

    @CheckForNull
    public List<CPSVlanData> getVlans() {
        return vlans;
    }

    public void setVlans(List<CPSVlanData> vlans) {
        // null setzen verhindert die Generierung eines leere "VLANS"-XML tag, falls die Liste leer ist
        if (vlans.isEmpty()) {
            this.vlans = null;
        }
        else {
            this.vlans = Lists.newArrayList(vlans);
        }
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

    public String getOltGeraeteBezeichnung() {
        return oltGeraeteBezeichnung;
    }

    public void setOltGeraeteBezeichnung(String oltName) {
        this.oltGeraeteBezeichnung = oltName;
    }

    public String getBucht() {
        return bucht;
    }

    public void setBucht(String bucht) {
        this.bucht = bucht;
    }

    public String getLeiste() {
        return leiste;
    }

    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

    public String getStift() {
        return stift;
    }

    public void setStift(String stift) {
        this.stift = stift;
    }

    public String getMduTyp() {
        return mduTyp;
    }

    public void setMduTyp(String mduTyp) {
        this.mduTyp = mduTyp;
    }

    public String getMduGeraeteBezeichnung() {
        return mduGeraeteBezeichnung;
    }

    public void setMduGeraeteBezeichnung(String mduGeraeteBezeichnung) {
        this.mduGeraeteBezeichnung = mduGeraeteBezeichnung;
    }

    @Override
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMduStandort() {
        return mduStandort;
    }

    public void setMduStandort(String mduStandort) {
        this.mduStandort = mduStandort;
    }

    public String getBaugruppenPort() {
        return baugruppenPort;
    }

    public void setBaugruppenPort(String baugruppenPort) {
        this.baugruppenPort = baugruppenPort;
    }

    public String getPortTyp() {
        return portTyp;
    }

    public void setPortTyp(String portTyp) {
        this.portTyp = portTyp;
    }

    public Long getOltRackId() {
        return oltRackId;
    }

    public void setOltRackId(Long oltRackId) {
        this.oltRackId = oltRackId;
    }

}


