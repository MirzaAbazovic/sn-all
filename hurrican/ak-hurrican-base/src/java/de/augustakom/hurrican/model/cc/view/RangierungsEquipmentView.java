/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2008 15:49:20
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Joiner;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 * View-Klasse fuer die manuelle Physikzuordnung.
 *
 *
 */

public class RangierungsEquipmentView extends AbstractCCModel {

    private Long rangierId = null;
    private Long rangierIdAdd = null;
    private String physikTyp = null;
    private String physikTypAdd = null;
    private Boolean freigabe = null;
    private String hwEqnOut = null;
    private String hwBgTypEqOut = null;
    private String hwEqnIn = null;
    private String hwBgTypEqIn = null;
    private Bandwidth hwEqInMaxBandwidth = null;
    private String rackEqIn = null;
    private String bemerkungEqIn = null;
    private String hwEqnInAdd = null;
    private String rackEqInAdd = null;
    private String dtagPorts = null;
    private Uebertragungsverfahren uetv = null;
    private Long esId = null;
    private Date freigabeAb = null;
    private String bemerkung = null;
    private String kvzNummerEqOut = null;
    private String raum;

    /**
     * Default-Const.
     */
    public RangierungsEquipmentView() {
    }

    /**
     * Konstruktor mit Angabe der Objekte, aus denen die View aufgebaut werden kann.
     */
    public RangierungsEquipmentView(
            Rangierung rangierung,
            Equipment eqOut,
            HWBaugruppe bgEqOut,
            HWRack rackEqOut,
            Equipment eqIn,
            HWBaugruppe bgEqIn,
            HWRack rackEqIn,
            @CheckForNull HVTRaum raumEqIn,
            PhysikTyp physikTyp,
            Rangierung rangierungAdd,
            Equipment eqOutAdd,
            HWBaugruppe bgEqOutAdd,
            HWRack rackEqOutAdd,
            Equipment eqInAdd,
            HWBaugruppe bgEqInAdd,
            HWRack rackEqInAdd,
            PhysikTyp physikTypAdd) {
        this.setBemerkung(rangierung.getBemerkung());
        this.setEsId(rangierung.getEsId());
        this.setFreigabe(rangierung.getFreigegebenBoolean());
        this.setFreigabeAb(rangierung.getFreigabeAb());
        this.setRangierId(rangierung.getId());
        this.setHwEqnOut((eqOut != null) ? eqOut.getHwEQN() : "");
        this.setHwBgTypEqOut(((bgEqOut != null) && (bgEqOut.getHwBaugruppenTyp() != null)) ? bgEqOut.getHwBaugruppenTyp().getName() : "");
        this.setHwEqnIn((eqIn != null) ? eqIn.getHwEQN() : "");
        this.setHwBgTypEqIn(((bgEqIn != null) && (bgEqIn.getHwBaugruppenTyp() != null)) ? bgEqIn.getHwBaugruppenTyp().getName() : "");
        this.setHwEqInMaxBandwidth(((bgEqIn != null) && (bgEqIn.getHwBaugruppenTyp() != null)) ? bgEqIn.getHwBaugruppenTyp().getMaxBandwidth() : null);
        this.setRackEqIn(createRackString(rackEqIn, bgEqIn));
        this.setBemerkungEqIn((bgEqIn != null) ? bgEqIn.getBemerkung() : "");
        this.setDtagPorts((eqOut != null) ? eqOut.getVerteilerLeisteStift() : "");
        this.setUetv((eqOut != null) ? eqOut.getUetv() : null);
        this.setPhysikTyp((physikTyp != null) ? physikTyp.getName() : "");
        this.setRangierIdAdd((rangierungAdd != null) ? rangierungAdd.getId() : null);
        this.setHwEqnInAdd((eqInAdd != null) ? eqInAdd.getHwEQN() : "");
        this.setRackEqInAdd(createRackString(rackEqInAdd, bgEqInAdd));
        this.setPhysikTypAdd((physikTypAdd != null) ? physikTypAdd.getName() : "");
        this.setKvzNummerEqOut((eqOut != null) ? eqOut.getKvzNummer() : "");
        if (raumEqIn != null) {
            this.raum = raumEqIn.getRaum();
        }
    }

    private String createRackString(HWRack rackEqIn, HWBaugruppe bgEqIn) {
        StringBuilder builder = new StringBuilder();
        if ((rackEqIn != null) && (bgEqIn != null)) {
            String mgmBezeichung = rackEqIn.getManagementBez();
            if ((mgmBezeichung != null) && (rackEqIn.getAnlagenBez() != null)) {
                mgmBezeichung = "(" + mgmBezeichung + ")";
            }
            String anlagenUndMgmBezeichnung = Joiner.on(" ").skipNulls().join(
                    rackEqIn.getAnlagenBez(),
                    mgmBezeichung);
            if (StringUtils.isEmpty(anlagenUndMgmBezeichnung)) {
                anlagenUndMgmBezeichnung = null;
            }
            Joiner.on(" - ").skipNulls().appendTo(builder,
                    anlagenUndMgmBezeichnung,
                    rackEqIn.getGeraeteBez(),
                    bgEqIn.getHwBaugruppenTyp().getName()
            );
        }
        return builder.toString();
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Long getEsId() {
        return esId;
    }

    public void setEsId(Long esId) {
        this.esId = esId;
    }

    public Boolean getFreigabe() {
        return freigabe;
    }

    public void setFreigabe(Boolean freigabe) {
        this.freigabe = freigabe;
    }

    public String getHwEqnIn() {
        return hwEqnIn;
    }

    public void setHwEqnIn(String hwEqnIn) {
        this.hwEqnIn = hwEqnIn;
    }

    public String getHwEqnOut() {
        return hwEqnOut;
    }

    public void setHwEqnOut(String hwEqnOut) {
        this.hwEqnOut = hwEqnOut;
    }

    public String getHwBgTypEqIn() {
        return hwBgTypEqIn;
    }

    public void setHwBgTypEqIn(String hwBgTypEqIn) {
        this.hwBgTypEqIn = hwBgTypEqIn;
    }

    public String getHwEqnInAdd() {
        return hwEqnInAdd;
    }

    public void setHwEqnInAdd(String hwEqnInAdd) {
        this.hwEqnInAdd = hwEqnInAdd;
    }

    public String getRackEqInAdd() {
        return rackEqInAdd;
    }

    public void setRackEqInAdd(String rackEqInAdd) {
        this.rackEqInAdd = rackEqInAdd;
    }

    public String getPhysikTypAdd() {
        return physikTypAdd;
    }

    public void setPhysikTypAdd(String physikTypAdd) {
        this.physikTypAdd = physikTypAdd;
    }

    public String getHwBgTypEqOut() {
        return hwBgTypEqOut;
    }

    public void setHwBgTypEqOut(String hwBgTypEqOut) {
        this.hwBgTypEqOut = hwBgTypEqOut;
    }

    public String getPhysikTyp() {
        return physikTyp;
    }

    public void setPhysikTyp(String physikTyp) {
        this.physikTyp = physikTyp;
    }

    public Long getRangierId() {
        return rangierId;
    }

    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    public Long getRangierIdAdd() {
        return rangierIdAdd;
    }

    public void setRangierIdAdd(Long rangierIdAdd) {
        this.rangierIdAdd = rangierIdAdd;
    }

    public String getRackEqIn() {
        return rackEqIn;
    }

    public void setRackEqIn(String rackEqIn) {
        this.rackEqIn = rackEqIn;
    }

    public String getDtagPorts() {
        return dtagPorts;
    }

    public void setDtagPorts(String dtagPorts) {
        this.dtagPorts = dtagPorts;
    }

    public Uebertragungsverfahren getUetv() {
        return uetv;
    }

    public void setUetv(Uebertragungsverfahren uetv) {
        this.uetv = uetv;
    }

    public Date getFreigabeAb() {
        return freigabeAb;
    }

    public void setFreigabeAb(Date freigabeAb) {
        this.freigabeAb = freigabeAb;
    }

    public String getBemerkungEqIn() {
        return bemerkungEqIn;
    }

    public void setBemerkungEqIn(String bemerkungEqIn) {
        this.bemerkungEqIn = bemerkungEqIn;
    }

    public Bandwidth getHwEqInMaxBandwidth() {
        return hwEqInMaxBandwidth;
    }

    public void setHwEqInMaxBandwidth(Bandwidth hwEqInMaxBandwidth) {
        this.hwEqInMaxBandwidth = hwEqInMaxBandwidth;
    }

    public String getKvzNummerEqOut() {
        return kvzNummerEqOut;
    }

    public void setKvzNummerEqOut(String kvzNummerEqOut) {
        this.kvzNummerEqOut = kvzNummerEqOut;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(final String raum) {
        this.raum = raum;
    }
}


