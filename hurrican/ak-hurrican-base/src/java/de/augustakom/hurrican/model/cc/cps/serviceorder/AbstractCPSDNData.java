/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 11:44:21
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import de.augustakom.hurrican.model.billing.Rufnummer;


/**
 * Abstrakte Klasse fuer Modelle, die Rufnummern-Daten zur CPS-Provisionierung enthalten.
 *
 *
 */
public abstract class AbstractCPSDNData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("LAC")
    private String lac = null;
    @XStreamAlias("DN")
    private String dn = null;
    @XStreamAlias("DIRECT_DIAL")
    private String directDial = null;
    @XStreamAlias("BLOCK_START")
    private String blockStart = null;
    @XStreamAlias("BLOCK_END")
    private String blockEnd = null;
    @XStreamAlias("MNET_DN")
    private String mnetDN = null;
    /**
     * Rufnummern-Vorwahl fuer 'Deutschland'.
     */
    public static final String COUNTRY_CODE_GERMANY = "+49";

    /**
     * Uebertraegt folgende Daten vom Objekt <code>dn</code> in das aktuelle CPSDNData-Objekt: <br> <ul> <li>LAC <li>DN
     * <li>MAIN_DN <li>DIRECT_DIAL <li>BLOCK_START <li>BLOCK_END </ul>
     *
     * @param dn
     *
     */
    public void transferDNData(Rufnummer dn) {
        transferDNDataWithoutAdjustments(dn);

        // Block Start/End: nur die erste Ziffer wird benoetigt!
        // (Absprache mit CPS-Team / Benedikt Eckstein)
        if (StringUtils.isNotEmpty(dn.getRangeFrom())) {
            setBlockStart(StringUtils.substring(dn.getRangeFrom(), 0, 1));
        }
        if (StringUtils.isNotBlank(dn.getRangeTo())) {
            setBlockEnd(StringUtils.substring(dn.getRangeTo(), 0, 1));
        }
    }

    protected void transferDNDataWithoutAdjustments(Rufnummer dn) {
        setLac(StringUtils.trimToNull(dn.getOnKz()));
        setDn(StringUtils.trimToNull(dn.getDnBase()));
        setDirectDial(StringUtils.trimToNull(dn.getDirectDial()));
        setBlockStart(dn.getRangeFrom());
        setBlockEnd(dn.getRangeTo());

        if (StringUtils.isNotBlank(getDirectDial()) && (!NumberUtils.isNumber(getDirectDial()) || (getDirectDial().length() > 1))) {
            throw new IllegalArgumentException("Value of DirectDial is not valid! Must be between 0 and 9 but is: " + getDirectDial());
        }
    }

    /**
     * @return the lac
     */
    public String getLac() {
        return lac;
    }

    /**
     * @param lac the lac to set
     */
    public void setLac(String lac) {
        this.lac = lac;
    }

    /**
     * @return the dn
     */
    public String getDn() {
        return dn;
    }

    /**
     * @param dn the dn to set
     */
    public void setDn(String dn) {
        this.dn = dn;
    }

    /**
     * @return the directDial
     */
    public String getDirectDial() {
        return directDial;
    }

    /**
     * @param directDial the directDial to set
     */
    public void setDirectDial(String directDial) {
        this.directDial = directDial;
    }

    /**
     * @return the blockStart
     */
    public String getBlockStart() {
        return blockStart;
    }

    /**
     * @param blockStart the blockStart to set
     */
    public void setBlockStart(String blockStart) {
        this.blockStart = blockStart;
    }

    /**
     * @return the blockEnd
     */
    public String getBlockEnd() {
        return blockEnd;
    }

    /**
     * @param blockEnd the blockEnd to set
     */
    public void setBlockEnd(String blockEnd) {
        this.blockEnd = blockEnd;
    }

    /**
     * Definiert, ob es sich um eine "M-net eigene" ("1") oder um eine portierte ("0") Rufnummer handelt. <br>
     * Sonderfall: 2 = M-net eigene Rufnummer aus VoIP Block
     *
     * @return the mnetDN
     */
    public String getMnetDN() {
        return mnetDN;
    }

    /**
     * @param mnetDN the mnetDN to set
     */
    public void setMnetDN(String mnetDN) {
        this.mnetDN = mnetDN;
    }
}


