/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2009 15:03:45
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.text.*;
import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice.CpsAccessDevice;


/**
 * Modell-Klasse (Root-Element) fuer die ServiceOrder-Data in Richtung CPS. <br> Das ServiceOrder-Data enthaelt alle
 * technischen Daten aus den Northbound- Systemen, die fuer die Provisionierung eines Auftrags erforderlich sind. <br>
 *
 *
 */
@XStreamAlias("SERVICE_ORDER")
public class CPSServiceOrderData extends AbstractCPSServiceOrderDataModel {

    private static final long serialVersionUID = -4648333181227832451L;

    /**
     * Enum definiert die moeglichen LazyInit-Varianten.
     */
    public enum LazyInitMode {
        noInitialLoad(0, 0),        // kein Initial-Load
        initialLoad(1, 1),          // Initial-Load
        initialLoadLogic(2, 2),     // Initial-Load mit Logik-Ausfuehrung auf CPS-Seite
        reInit(3, 1);               // Re-Init (modifySub mit InitialLoad=1); fuer CPS 'normaler' InitialLoad, fuer Hurrican Re-Init

        private final int lazyInitModeValue;
        private final int lazyInitModeCpsValue;

        /**
         * Konstruktor fuer den Enum.
         *
         * @param value    interner Hurrican Wert fuer die InitialLoad
         * @param cpsValue CPS Wert fuer den InitialLoad
         */
        private LazyInitMode(int value, Integer cpsValue) {
            this.lazyInitModeValue = value;
            this.lazyInitModeCpsValue = cpsValue;
        }

        public static LazyInitMode getLazyInitMode(int value) {
            for (LazyInitMode lazyInitModeEnum : values()) {
                if (lazyInitModeEnum.lazyInitModeValue == value) {
                    return lazyInitModeEnum;
                }
            }
            return null;
        }

        public Integer getLazyInitMode() {
            return lazyInitModeValue;
        }

        public Integer getLazyInitModeCps() {
            return lazyInitModeCpsValue;
        }

        /**
         * Prueft, ob es sich bei dem angegebenen Mode um ein Lazy-Init handelt. Dies ist dann der Fall, wenn der Wert >
         * 0 ist.
         *
         * @param lazyInitMode
         * @return
         */
        public static boolean isInitialLoad(LazyInitMode lazyInitMode) {
            return (lazyInitMode != null) && (lazyInitMode.lazyInitModeValue > 0);
        }
    }

    @XStreamAlias("EXEC_DATE")
    private String execDate;
    @XStreamAlias("TAIFUNNUMBER")
    private String taifunnumber;
    @XStreamAlias("VBZ")
    private String verbindungsbezeichnung;
    @XStreamAlias("ONKZ")
    private String onkz;
    @XStreamAlias("ASB")
    private Integer asb;
    @XStreamAlias("NIEDERLASSUNG")
    private String niederlassung;
    @XStreamAlias("PRODUCT_CODE")
    private String productCode;
    @XStreamAlias("INITIAL_LOAD")
    private Integer initialLoad;
    @XStreamAlias("LOCK_MODE")
    private String lockMode;
    @XStreamAlias("GEOID")
    private Long geoid;
    @XStreamAlias("TELEPHONE")
    private CPSTelephoneData telephone;
    @XStreamAlias("DSL")
    private CPSDSLData dsl;
    @XStreamAlias("SDSL")
    private CPSSdslData sdsl;
    @XStreamAlias("RADIUS")
    private CPSRadiusData radius;
    @XStreamAlias("VOIP")
    private CPSVoIPData voip;
    @XStreamAlias("PORTATIONS")
    private List<CPSDNPortation> dnPortations;
    @XStreamAlias("FTTB")
    private CPSFTTBData fttb;
    @XStreamAlias("ACCESS_DEVICE")
    private CpsAccessDevice accessDevice;
    @XStreamAlias("IAD")
    private CPSIADData iad;
    @XStreamAlias("SIPINTERTRUNK")
    private List<CPSSIPInterTrunkData> sipInterTrunk;
    @XStreamAlias("CUSTOMER_PREMISES_EQUPIMENT")
    private List<CPSBusinessCpeData> customerPremisesEquipments;
    @XStreamAlias("MVS")
    private CPSMVSData mvs;
    @XStreamAlias("PEERING_PARTNER")
    private List<CpsSbcIp> peeringPartner;
    @XStreamAlias("EKP")
    private String ekpId;

    public boolean isInitialLoad() {
        if (getInitialLoad() == null) {
            return false;
        }
        else {
            LazyInitMode lazyInitMode = LazyInitMode.getLazyInitMode(getInitialLoad());
            return LazyInitMode.isInitialLoad(lazyInitMode);
        }
    }

    public String getExecDate() {
        return execDate;
    }

    public void setExecDate(String execDate) {
        this.execDate = execDate;
    }

    /**
     * Uebergibt dem Modell das geplante Execution-Daten als Date-Objekt. <br> Die Methode wandelt das Date-Objekt in
     * einen String mit erwartetem Format um.
     *
     * @param execDate
     */
    public void setExecDate(Date execDate) {
        if (execDate != null) {
            setExecDate(DateTools.formatDate(execDate, DEFAULT_CPS_DATE_TIME_FORMAT));
        }
    }

    /**
     * Gibt das 'execDate' als Date-Objekt zurueck.
     *
     * @return
     * @throws ParseException
     *
     */
    public Date getExecDateAsDate() throws ParseException {
        if (StringUtils.isNotBlank(getExecDate())) {
            SimpleDateFormat df = new SimpleDateFormat(DEFAULT_CPS_DATE_TIME_FORMAT);
            return df.parse(getExecDate());
        }
        return null;
    }

    public String getTaifunnumber() {
        return taifunnumber;
    }

    public void setTaifunnumber(String taifunnumber) {
        this.taifunnumber = taifunnumber;
    }

    public String getVerbindungsbezeichnung() {
        return verbindungsbezeichnung;
    }

    public void setVerbindungsbezeichnung(String verbindungsbezeichnung) {
        this.verbindungsbezeichnung = verbindungsbezeichnung;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public String getNiederlassung() {
        return niederlassung;
    }

    public void setNiederlassung(String niederlassung) {
        this.niederlassung = niederlassung;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getInitialLoad() {
        return initialLoad;
    }

    public void setInitialLoad(Integer initialLoad) {
        this.initialLoad = initialLoad;
    }

    public CPSTelephoneData getTelephone() {
        return telephone;
    }

    public void setTelephone(CPSTelephoneData telephone) {
        this.telephone = telephone;
    }

    public CPSDSLData getDsl() {
        return dsl;
    }

    public void setDsl(CPSDSLData dsl) {
        this.dsl = dsl;
    }

    public CPSSdslData getSdsl() {
        return sdsl;
    }

    public void setSdsl(CPSSdslData sdsl) {
        this.sdsl = sdsl;
    }

    public CPSRadiusData getRadius() {
        return radius;
    }

    public void setRadius(CPSRadiusData radius) {
        this.radius = radius;
    }

    public CPSVoIPData getVoip() {
        return voip;
    }

    public void setVoip(CPSVoIPData voip) {
        this.voip = voip;
    }

    public List<CPSDNPortation> getDnPortations() {
        return dnPortations;
    }

    public void setDnPortations(List<CPSDNPortation> dnPortations) {
        this.dnPortations = dnPortations;
    }

    public CPSFTTBData getFttb() {
        return fttb;
    }

    public void setFttb(CPSFTTBData fttb) {
        this.fttb = fttb;
    }

    public CpsAccessDevice getAccessDevice() {
        return accessDevice;
    }

    public void setAccessDevice(CpsAccessDevice accessDevice) {
        this.accessDevice = accessDevice;
    }

    public CPSIADData getIad() {
        return iad;
    }

    public void setIad(CPSIADData iad) {
        this.iad = iad;
    }

    public String getLockMode() {
        return lockMode;
    }

    public void setLockMode(String lockMode) {
        this.lockMode = lockMode;
    }

    public List<CPSSIPInterTrunkData> getSipInterTrunk() {
        return sipInterTrunk;
    }

    public void setSipInterTrunk(List<CPSSIPInterTrunkData> sipInterTrunk) {
        this.sipInterTrunk = sipInterTrunk;
    }

    public List<CPSBusinessCpeData> getCustomerPremisesEquipments() {
        return customerPremisesEquipments;
    }

    public void setCustomerPremisesEquipments(List<CPSBusinessCpeData> customerPremisesEquipments) {
        this.customerPremisesEquipments = customerPremisesEquipments;
    }

    public CPSMVSData getMvs() {
        return mvs;
    }

    public void setMvs(CPSMVSData mvs) {
        this.mvs = mvs;
    }

    public Long getGeoid() {
        return geoid;
    }

    public void setGeoid(Long geoid) {
        this.geoid = geoid;
    }

    public List<CpsSbcIp> getPeeringPartner() {
        return peeringPartner;
    }

    public void setPeeringPartner(List<CpsSbcIp> peeringPartner) {
        this.peeringPartner = peeringPartner;
    }

    public String getEkpId() {
        return ekpId;
    }

    public void setEkpId(String ekpId) {
        this.ekpId = ekpId;
    }
}
