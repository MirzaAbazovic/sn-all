/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:04:02
 */
package de.augustakom.hurrican.model.billing;

import java.io.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.shared.iface.ExternLeistungAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternMiscAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternProduktAwareModel;


/**
 * Abbildung einer Leistung aus dem Billing-System.
 *
 *
 */
public class Leistung extends AbstractHistoryModel implements ExternProduktAwareModel,
        ExternLeistungAwareModel, ExternMiscAwareModel, DebugModel, Serializable {

    //@formatter:off
    // externe Leistungsnummern fuer MVS Leistungen
    public static final Long EXT_MISC_NO_MVS_LIZENZPAKET = 100000L;
    public static final Long EXT_MISC_NO_MVS_IVR = 100001L;
    public static final Long EXT_MISC_NO_MVS_3WAY_CONF = 100002L;
    public static final Long EXT_MISC_NO_MVS_MOBILE_CLIENT = 100003L;
    public static final Long EXT_MISC_NO_MVS_ATTENDANT_CONSOLE = 100004L;
    public static final Long EXT_MISC_NO_MVS_FAX2MAIL = 100007L;
    public static final Long EXT_MISC_NO_MVS_CHANNELS = 100008L;
    private static final Long[] EXT_MISC_NOS_4_MVS = {
        EXT_MISC_NO_MVS_IVR, EXT_MISC_NO_MVS_MOBILE_CLIENT, EXT_MISC_NO_MVS_FAX2MAIL, EXT_MISC_NO_MVS_ATTENDANT_CONSOLE,
        EXT_MISC_NO_MVS_3WAY_CONF, EXT_MISC_NO_MVS_LIZENZPAKET, EXT_MISC_NO_MVS_CHANNELS
    };
    //@formatter:on

    /**
     * Kennzeichnung fuer Leistungen, die eine Montage durch M-net zur Folge haben.
     */
    public static final Long EXT_MISC_NO_MONTAGE_MNET = 110000L;

    /**
     * Kennzeichnung fuer eine VPN-Leistung (minimaler, moeglicher Wert).
     */
    public static final Long EXT_MISC_NO_VPN_MIN = Long.valueOf(110);
    /**
     * Kennzeichnung fuer eine VPN-Leistung (maximaler, moeglicher Wert).
     */
    public static final Long EXT_MISC_NO_VPN_MAX = Long.valueOf(119);

    /**
     * Wert fuer 'idExternLeistung' kennzeichnet Option Fastpath.
     */
    public static final Long EXT_LEISTUNG_NO_FASTPATH = Long.valueOf(10001);
    /**
     * Wert fuer 'idExternLeistung' kennzeichnet Option 'doppelter Upstream'.
     */
    public static final Long EXT_LEISTUNG_NO_DOPPELTER_UPSTREAM = Long.valueOf(10002);
    /**
     * Wert fuer 'idExternLeistung' kennzeichnet Option 'always on'.
     */
    public static final Long EXT_LEISTUNG_NO_ALWAYS_ON = Long.valueOf(10003);
    /**
     * Wert fuer 'idExternLeistung' kennzeichnet Option 'always on incl IP'.
     */
    public static final Long EXT_LEISTUNG_NO_IP_INCL_ALWAYS = Long.valueOf(10004);

    /**
     * Wert fuer 'idExternProdukt' kennzeichnet eine 4-Draht SDSL Option.
     */
    private static final Long[] EXT_PROD_NO_4DRAHTSDSLOPTION = new Long[] { Long.valueOf(99), Long.valueOf(463) };

    /**
     * Gibt die externen Produktnummern zurueck, die eine 4-Draht SDSL Option darstellen. (clone() stellt sicher, dass
     * das Array von aufrufenden Klassen nicht veraendert werden kann.)
     */
    public static Long[] getIdExternProdukt4DrahtSDSLOption() {
        return EXT_PROD_NO_4DRAHTSDSLOPTION.clone();
    }

    /**
     * Gibt die externen Leistungsnummern zurueck, die MVS Leistungen darstellen. <br> (clone() stellt sicher, dass das
     * Array von aufrufenden Klassen nicht veraendert werden kann.)
     */
    public static Long[] getExtMiscNos4Mvs() {
        return EXT_MISC_NOS_4_MVS.clone();
    }

    /**
     * Gibt an, dass es sich bei der (Volumen-)Leistung um eine Flatrate handelt.
     */
    public static final int LEISTUNG_VOL_TYPE_FLAT = 1;
    /**
     * Gibt an, dass es sich bei der (Volumen-)Leistung um einen Volumentarif handelt.
     */
    public static final int LEISTUNG_VOL_TYPE_VOLUME = 2;
    /**
     * Gibt an, dass es sich bei der (Volumen-)Leistung um einen Zeittarif handelt.
     */
    public static final int LEISTUNG_VOL_TYPE_TIME = 3;

    /**
     * Wert fuer Parameter 'leistungKat' fuer (UDR) Volumen-Leistungen.
     */
    public static final String LEISTUNG_CATEGORY_VOLUME = "VOL-QTY";

    /**
     * Prefix-Wert fuer Parameter 'billingCode' fuer UDR-Leistungen.
     */
    public static final String LEISTUNG_BILLING_CODE_PREFIX_UDR = "UDR";

    /**
     * Prefix-Wert fuer Parameter 'billingCode' fuer volumen-basierte UDR-Leistungen.
     */
    public static final String LEISTUNG_BILLING_CODE_PREFIX_UDR_VOLUME = "UDR.M";
    /**
     * Prefix-Wert fuer Parameter 'billingCode' fuer zeit-basierte UDR-Leistungen.
     */
    public static final String LEISTUNG_BILLING_CODE_PREFIX_UDR_TIME = "UDR.TZ";

    private Long leistungNo = null;
    private Long leistungNoOrig = null;
    private String name = null;
    private Long externProduktNo = null;
    private Long externLeistungNo = null;
    private Long externMiscNo = null;
    private Long oeNoOrig = null;
    private String fibuGebuehrenArt = null;
    private Boolean techExport = null;
    private Float preis = null;
    private String preisQuelle = null;
    private String vatCode = null;
    private String leistungKat = null;
    private Boolean generateBillPos = null;
    private String billingCode = null;


    public static Leistung createLeistung(long extLeistungNo) {
        Leistung l = new Leistung();
        l.setExternLeistungNo(extLeistungNo);
        return l;
    }

    /**
     * Ueberprueft, ob die Leistung fuer externe Systeme gekennzeichnet ist.
     *
     * @return true wenn eine der externen IDs gesetzt ist.
     *
     */
    public boolean hasKennzeichnungExtern() {
        if ((getExternLeistungNo() != null) || (getExternMiscNo() != null) || (getExternProduktNo() != null)) {
            return true;
        }
        return false;
    }

    /**
     * Ueberprueft, ob der Leistung eine externe Produkt- od. Leistungs-Kennzeichnung zugeordnet ist.
     *
     * @return
     *
     */
    public boolean isProduktOrLeistung() {
        return ((getExternProduktNo() != null) || (getExternLeistungNo() != null)) ? true : false;
    }

    /**
     * @return Returns the leistungNo.
     */
    public Long getLeistungNo() {
        return leistungNo;
    }

    /**
     * @param leistungNo The leistungNo to set.
     */
    public void setLeistungNo(Long leistungNo) {
        this.leistungNo = leistungNo;
    }

    /**
     * @return Returns the leistungNoOrig.
     */
    public Long getLeistungNoOrig() {
        return leistungNoOrig;
    }

    /**
     * @param leistungNoOrig The leistungNoOrig to set.
     */
    public void setLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the orderEntryNoOrig.
     */
    public Long getOeNoOrig() {
        return oeNoOrig;
    }

    /**
     * @param orderEntryNoOrig The orderEntryNoOrig to set.
     */
    public void setOeNoOrig(Long orderEntryNoOrig) {
        this.oeNoOrig = orderEntryNoOrig;
    }

    /**
     * Gibt einen Code zurueck, ueber den die Billing-Leistung im CC-System konfiguriert werden kann. <br> Identische
     * Leistungen (z.B. 'Always on') besitzen immer den gleichen Code fuer <code>idExternLeistung</code>.
     *
     * @return Returns the externLeistungNo.
     */
    @Override
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
     * @return Returns the externMiscNo.
     */
    @Override
    public Long getExternMiscNo() {
        return this.externMiscNo;
    }

    /**
     * @param externMiscNo The externMiscNo to set.
     */
    public void setExternMiscNo(Long externMiscNo) {
        this.externMiscNo = externMiscNo;
    }

    /**
     * @return Returns the externProduktNo.
     */
    @Override
    public Long getExternProduktNo() {
        return this.externProduktNo;
    }

    /**
     * @return leistungKat
     */
    public String getLeistungKat() {
        return leistungKat;
    }

    /**
     * @param leistungKat Festzulegender leistungKat
     */
    public void setLeistungKat(String leistungKat) {
        this.leistungKat = leistungKat;
    }

    /**
     * @param externProduktNo The externProduktNo to set.
     */
    public void setExternProduktNo(Long externProduktNo) {
        this.externProduktNo = externProduktNo;
    }

    /**
     * @return fibuGebuehrenArt
     */
    public String getFibuGebuehrenArt() {
        return fibuGebuehrenArt;
    }

    /**
     * @param fibuGebuehrenArt Festzulegender fibuGebuehrenArt
     */
    public void setFibuGebuehrenArt(String fibuGebuehrenArt) {
        this.fibuGebuehrenArt = fibuGebuehrenArt;
    }

    /**
     * @return techExport
     */
    public Boolean getTechExport() {
        return techExport;
    }

    /**
     * @param techExport Festzulegender techExport
     */
    public void setTechExport(Boolean techExport) {
        this.techExport = techExport;
    }

    /**
     * @return preis
     */
    public Float getPreis() {
        return preis;
    }

    /**
     * @param preis Festzulegender preis
     */
    public void setPreis(Float preis) {
        this.preis = preis;
    }

    /**
     * @return preisQuelle
     */
    public String getPreisQuelle() {
        return preisQuelle;
    }

    /**
     * @param preisQuelle Festzulegender preisQuelle
     */
    public void setPreisQuelle(String preisQuelle) {
        this.preisQuelle = preisQuelle;
    }

    /**
     * @return vatCode
     */
    public String getVatCode() {
        return vatCode;
    }

    /**
     * @param vatCode Festzulegender vatCode
     */
    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    /**
     * @return generateBillPos
     */
    public Boolean getGenerateBillPos() {
        return generateBillPos;
    }

    /**
     * @param generateBillPos Festzulegender generateBillPos
     */
    public void setGenerateBillPos(Boolean generateBillPos) {
        this.generateBillPos = generateBillPos;
    }

    /**
     * @return the billingCode
     */
    public String getBillingCode() {
        return billingCode;
    }

    /**
     * @param billingCode the billingCode to set
     */
    public void setBillingCode(String billingCode) {
        this.billingCode = billingCode;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von Modell " + this.getClass().getName());
            logger.debug("  Leistung__NO    : " + getLeistungNoOrig());
            logger.debug("  Name            : " + getName());
            logger.debug("  ext. Misc No    : " + getExternMiscNo());
            logger.debug("  ext. Produkt No : " + getExternProduktNo());
            logger.debug("  ext. Leistung No: " + getExternLeistungNo());
        }
    }

}


