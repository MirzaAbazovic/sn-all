/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2011 10:22:39
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

/**
 * Klasse zur Bestimmung von Berechnungstrategietypen
 *
 *
 */
public class CcTechLeistungStrategyTyp {
    private boolean hasVoIP;
    private boolean hasBusinessCPE;
    private boolean hasIpV6;

    public CcTechLeistungStrategyTyp(boolean hasVoIP, boolean hasBusinessCPE, boolean hasIpV6) {
        this.hasVoIP = hasVoIP;
        this.hasBusinessCPE = hasBusinessCPE;
        this.hasIpV6 = hasIpV6;
    }

    public static CcTechLeistungStrategyTyp create(boolean hasVoIP, boolean hasBusinessCPE, boolean hasIpV6) {
        return new CcTechLeistungStrategyTyp(hasVoIP, hasBusinessCPE, hasIpV6);
    }

    public boolean isMatch(CcTechLeistungStrategyTyp ccTechnischeLeistungTyp) {
        if (getHasVoIP() != ccTechnischeLeistungTyp.getHasVoIP()) {return false;}
        if (getHasBusinessCPE() != ccTechnischeLeistungTyp.getHasBusinessCPE()) {return false;}
        if (isHasIpV6() != ccTechnischeLeistungTyp.isHasIpV6()) {
            return false;
        }
        return true;
    }

    public boolean getHasVoIP() {
        return hasVoIP;
    }

    public void setHasVoIP(boolean hasVoIP) {
        this.hasVoIP = hasVoIP;
    }

    public boolean getHasBusinessCPE() {
        return hasBusinessCPE;
    }

    public void setHasBusinessCPE(boolean hasBusinessCPE) {
        this.hasBusinessCPE = hasBusinessCPE;
    }

    public boolean isHasIpV6() {
        return hasIpV6;
    }

    public void setHasIpV6(boolean hasIpV6) {
        this.hasIpV6 = hasIpV6;
    }
}


