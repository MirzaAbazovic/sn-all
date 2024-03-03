/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.2012 09:49:57
 */
package de.augustakom.hurrican.model.wholesale;

import java.util.*;

import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSFTTBData;

/**
 * Klasse um technische Auftragsdaten fuer Wholesale aggregiert zu halten
 */
public class OrderParameters {

    /**
     * Bitte beachten: Die Bezeichner muessen mit der Dyn Class {@code de.mnet.hurrican.wholesale.workflow} aus
     * HurricanWeb matchen!
     */
    public static enum WholesaleTechType {
        FTTB,
        FTTH,
        FTTC
    }

    private CPSFTTBData fttbData;
    private HVTGruppe techLocationHvtGruppe;
    private String asb;
    private WholesaleTechType techType;
    private int targetMargin;
    private String vdslProfile;
    private List<WholesalePbit> pbit = Collections.emptyList();
    private List<WholesaleVlan> vlans = Collections.emptyList();
    private String a10nsp;
    private String a10nspPort;

    public CPSFTTBData getFttbData() {
        return fttbData;
    }

    public void setFttbData(CPSFTTBData fttbData) {
        this.fttbData = fttbData;
    }

    public HVTGruppe getTechLocationHvtGruppe() {
        return techLocationHvtGruppe;
    }

    public void setTechLocationHvtGruppe(HVTGruppe hvtGruppe) {
        this.techLocationHvtGruppe = hvtGruppe;
    }

    public String getAsb() {
        return asb;
    }

    public void setAsb(String asb) {
        this.asb = asb;
    }

    public WholesaleTechType getTechType() {
        return techType;
    }

    public void setTechType(WholesaleTechType techType) {
        this.techType = techType;
    }

    public int getTargetMargin() {
        return targetMargin;
    }

    public void setTargetMargin(int targetMargin) {
        this.targetMargin = targetMargin;
    }

    public String getVdslProfile() {
        return vdslProfile;
    }

    public void setVdslProfile(String vdslProfile) {
        this.vdslProfile = vdslProfile;
    }

    public List<WholesalePbit> getPbit() {
        return pbit;
    }

    public void setPbit(List<WholesalePbit> pbit) {
        this.pbit = pbit;
    }

    public List<WholesaleVlan> getVlans() {
        return vlans;
    }

    public void setVlans(List<WholesaleVlan> vlans) {
        this.vlans = vlans;
    }

    public String getA10nsp() {
        return a10nsp;
    }

    public void setA10nsp(String a10nsp) {
        this.a10nsp = a10nsp;
    }

    public String getA10nspPort() {
        return a10nspPort;
    }

    public void setA10nspPort(String a10nspPort) {
        this.a10nspPort = a10nspPort;
    }

}


