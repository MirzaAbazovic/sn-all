/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2005 09:50:11
 */
package de.augustakom.hurrican.model.cc.dn;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * Modell der Rufnummernleistungen.
 *
 *
 */
public class Leistung4Dn extends AbstractCCIDModel {


    /**
     * Konstante für die Leistung Sperrklasse nötig für die Konfiguration der Sperrklassen auf dem
     * DNLeistung2AuftragPanel
     */
    public static final Long SPERRKLASSE_ID = Long.valueOf(46);

    /**
     * ID der Rufnummernleistung 'AGRU'
     */
    public static final Long DN_SERVICE_AGRU = Long.valueOf(2);
    /**
     * ID der Rufnummernleistung 'CCBS'
     */
    public static final Long DN_SERVICE_CCBS = Long.valueOf(30);
    /**
     * ID der Rufnummernleistung 'CCNR'
     */
    public static final Long DN_SERVICE_CCNR = Long.valueOf(57);
    /**
     * ID der Rufnummernleistung 'CD'
     */
    public static final Long DN_SERVICE_CD = Long.valueOf(55);
    /**
     * ID der Rufnummernleistung 'CFB'
     */
    public static final Long DN_SERVICE_CFB = Long.valueOf(26);
    /**
     * ID der Rufnummernleistung 'CFNR'
     */
    public static final Long DN_SERVICE_CFNR = Long.valueOf(28);
    /**
     * ID der Rufnummernleistung 'CFU'
     */
    public static final Long DN_SERVICE_CFU = Long.valueOf(29);
    /**
     * ID der Rufnummernleistung 'CLIP'
     */
    public static final Long DN_SERVICE_CLIP = Long.valueOf(5);
    /**
     * ID der Rufnummernleistung 'CLIR1'
     */
    public static final Long DN_SERVICE_CLIR1 = Long.valueOf(6);
    /**
     * ID der Rufnummernleistung 'CLIR2'
     */
    public static final Long DN_SERVICE_CLIR2 = Long.valueOf(7);
    /**
     * ID der Rufnummernleistung 'CLIRSUSP'
     */
    public static final Long DN_SERVICE_CLIRSUSP = Long.valueOf(8);
    /**
     * ID der Rufnummernleistung 'COLP'
     */
    public static final Long DN_SERVICE_COLP = Long.valueOf(9);
    /**
     * ID der Rufnummernleistung 'COLR'
     */
    public static final Long DN_SERVICE_COLR = Long.valueOf(10);
    /**
     * ID der Rufnummernleistung 'COLRREQ'
     */
    public static final Long DN_SERVICE_COLRREQ = Long.valueOf(11);
    /**
     * ID der Rufnummernleistung 'COLRSUSP'
     */
    public static final Long DN_SERVICE_COLRSUSP = Long.valueOf(12);
    /**
     * ID der Rufnummernleistung 'CS_IN_BLACK'
     */
    public static final Long DN_SERVICE_CS_IN_BLACK = Long.valueOf(34);
    /**
     * ID der Rufnummernleistung 'CS_IN_WHITE'
     */
    public static final Long DN_SERVICE_CS_IN_WHITE = Long.valueOf(33);
    /**
     * ID der Rufnummernleistung 'CS_OUT_WHITE'
     */
    public static final Long DN_SERVICE_CS_OUT_WHITE = Long.valueOf(31);
    /**
     * ID der Rufnummernleistung 'CS_OUT_BLACK'
     */
    public static final Long DN_SERVICE_CS_OUT_BLACK = Long.valueOf(32);
    /**
     * ID der Rufnummernleistung 'CW'
     */
    public static final Long DN_SERVICE_CW = Long.valueOf(1);
    /**
     * ID der Rufnummernleistung 'DIVBY'
     */
    public static final Long DN_SERVICE_DIVBY = Long.valueOf(52);
    /**
     * ID der Rufnummernleistung 'DIVDA'
     */
    public static final Long DN_SERVICE_DIVDA = Long.valueOf(53);
    /**
     * ID der Rufnummernleistung 'DIVEI'
     */
    public static final Long DN_SERVICE_DIVEI = Long.valueOf(25);
    /**
     * ID der Rufnummernleistung 'DIVI'
     */
    public static final Long DN_SERVICE_DIVI = Long.valueOf(51);
    /**
     * ID der Rufnummernleistung 'DIVIP'
     */
    public static final Long DN_SERVICE_DIVIP = Long.valueOf(21);
    /**
     * ID der Rufnummernleistung 'OCB'
     */
    public static final Long DN_SERVICE_OCB = Long.valueOf(46);
    /**
     * ID der Rufnummernleistung 'PORTEDDN'
     */
    public static final Long DN_SERVICE_PORTEDDN = Long.valueOf(24);
    /**
     * ID der Rufnummernleistung 'PR'
     */
    public static final Long DN_SERVICE_PR = Long.valueOf(22);
    /**
     * ID der Rufnummernleistung 'THIRDPTY'
     */
    public static final Long DN_SERVICE_THIRDPTY = Long.valueOf(13);
    /**
     * ID der Rufnummernleistung 'UUS'
     */
    public static final Long DN_SERVICE_UUS = Long.valueOf(48);
    /**
     * ID der Rufnummernleistung 'UUS3'
     */
    public static final Long DN_SERVICE_UUS3 = Long.valueOf(49);
    /**
     * ID der Rufnummernleistung 'CLIPNOSCR'
     */
    public static final Long DN_SERVICE_CLIPNOSCR = Long.valueOf(4);
    /**
     * ID der Rufnummernleistung 'MCID'
     */
    public static final Long DN_SERVICE_MCID = Long.valueOf(14);
    /**
     * ID der Rufnummernleistung 'TOLLCAT'
     */
    public static final Long DN_SERVICE_TOLLCAT = Long.valueOf(15);
    /**
     * ID der Rufnummernleistung 'AOCE'
     */
    public static final Long DN_SERVICE_AOCE = Long.valueOf(17);
    /**
     * ID der Rufnummernleistung 'AOCD'
     */
    public static final Long DN_SERVICE_AOCD = Long.valueOf(18);
    /**
     * ID der Rufnummernleistung 'CFALD'
     */
    public static final Long DN_SERVICE_CFALD = Long.valueOf(27);
    /**
     * ID der Rufnummernleistung 'ACR'
     */
    public static final Long DN_SERVICE_ACR = Long.valueOf(56);
    /**
     * ID der Rufnummernleistung 'HOLD'
     */
    public static final Long DN_SERVICE_HOLD = Long.valueOf(59);


    private String leistung = null;
    private String beschreibung = null;
    private Long externLeistungNo = null;
    private Long externSonstigesNo = null;
    private String provisioningName = null;

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * @return Returns the leistung.
     */
    public String getLeistung() {
        return leistung;
    }

    /**
     * @param leistung The leistung to set.
     */
    public void setLeistung(String leistung) {
        this.leistung = leistung;
    }

    /**
     * @return Returns the externSonstigesNo.
     */
    public Long getExternSonstigesNo() {
        return this.externSonstigesNo;
    }

    /**
     * @param externSonstigesNo The externSonstigesNo to set.
     */
    public void setExternSonstigesNo(Long externSonstigesNo) {
        this.externSonstigesNo = externSonstigesNo;
    }

    /**
     * @return Returns the externLeistungNo.
     */
    public Long getExternLeistungNo() {
        return this.externLeistungNo;
    }

    /**
     * @param externLeistungNo The externLeistungNo to set.
     */
    public void setExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
    }

    /**
     * @return Returns the provisioningName.
     */
    public String getProvisioningName() {
        return provisioningName;
    }

    /**
     * @param provisioningName The provisioningName to set.
     */
    public void setProvisioningName(String provisioningName) {
        this.provisioningName = provisioningName;
    }


}
