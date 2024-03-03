/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 11:42:10
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSDNServiceConverter;

/**
 * Modell-Klasse zur Abbildung der SIP-Account Daten eines VoIP-Anschlusses zur CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("SIPACCOUNT")
public class CPSVoIPSIPAccountData extends AbstractCpsDnWithCarrierIdData implements ICPSDNServiceAwareModel {

    /**
     * Nummern-Typ definiert einen POTS-Typ.
     */
    public static final String VOIP_NUMBER_TYPE_POTS = "POTS";
    /**
     * Nummern-Typ definiert einen ISDN-Typ.
     */
    public static final String VOIP_NUMBER_TYPE_ISDN = "ISDN";
    private static final long serialVersionUID = 30804056202517533L;

    @XStreamAlias("COUNTRYCODE")
    private String countryCode = null;
    @XStreamAlias("DNINDEX")
    private Integer dnIndex = null;
    @XStreamAlias("NUMBERTYPE")
    private String numberType = null;
    @XStreamAlias("PASSWORD")
    private String password = null;
    @XStreamAlias("SIPDOMAIN")
    private String sipDomain = null;
    @XStreamAlias("SWITCH")
    private String switchKennung = null;
    @XStreamAlias("IAD_PORTS")
    private CPSVoIPIADPortsData iadPorts;
    @XStreamAlias("MAIN_DN")
    private String mainDN = null;
    @XStreamAlias("SERVICES")
    @XStreamConverter(CPSDNServiceConverter.class)
    private List<CPSDNServiceData> dnServices = null;
    @XStreamAlias("CALL_SCREENING")
    private List<CpsCallScreening> callScreenings = null;
    @XStreamAlias("LINES")
    private Long anzahlSprachkanaele;
    @XStreamAlias("MAIN_NUMBER")
    private String mainNumber;
    @XStreamAlias(("SIP_LOGIN"))
    private String sipLogin;


    @Override
    protected void transferDNDataWithoutAdjustments(Rufnummer dn) {
        setMainDN(BooleanTools.getBooleanAsString(dn.isMainNumber()));
        super.transferDNDataWithoutAdjustments(dn);
    }

    /**
     * Fuegt dem Modell ein weiteres Objekt vom Typ <code>CPSDNServiceData</code> hinzu. <br> Das Modell wird der Liste
     * <code>dnServices</code> zugeordnet.
     *
     * @param toAdd
     */
    public void addDNService(CPSDNServiceData toAdd) {
        if (getDnServices() == null) {
            setDnServices(new ArrayList<CPSDNServiceData>());
        }
        getDnServices().add(toAdd);
    }

    public void addCallscreening(CpsCallScreening callScreening) {
        if (this.callScreenings == null) {
            this.callScreenings = Lists.newArrayList();
        }
        this.callScreenings.add(callScreening);
    }

    @CheckForNull
    public List<CpsCallScreening> getCallScreenings() {
        return callScreenings;
    }

    public Long getAnzahlSprachkanaele() {
        return anzahlSprachkanaele;
    }

    public void setAnzahlSprachkanaele(final Long anzahlSprachkanaele) {
        this.anzahlSprachkanaele = anzahlSprachkanaele;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the dnIndex
     */
    public Integer getDnIndex() {
        return dnIndex;
    }

    /**
     * @param dnIndex the dnIndex to set
     */
    public void setDnIndex(Integer dnIndex) {
        this.dnIndex = dnIndex;
    }

    /**
     * @return the numberType
     */
    public String getNumberType() {
        return numberType;
    }

    /**
     * @param numberType the numberType to set
     */
    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the sipDomain
     */
    public String getSipDomain() {
        return sipDomain;
    }

    /**
     * @param sipDomain the sipDomain to set
     */
    public void setSipDomain(String sipDomain) {
        this.sipDomain = sipDomain;
    }

    public String getSwitchKennung() {
        return switchKennung;
    }

    public void setSwitchKennung(String switchKennung) {
        this.switchKennung = switchKennung;
    }

    /**
     * @return the dnServices
     */
    public List<CPSDNServiceData> getDnServices() {
        return dnServices;
    }

    @Transient
    public List<CPSDNServiceData> getDnServicesEmptyIfNull() {
        return (dnServices != null) ? dnServices : Collections.emptyList();
    }

    /**
     * @param dnServices the dnServices to set
     */
    @Override
    public void setDnServices(List<CPSDNServiceData> dnServices) {
        this.dnServices = dnServices;
    }

    public String getMainDN() {
        return mainDN;
    }

    public void setMainDN(String mainDN) {
        this.mainDN = mainDN;
    }

    public CPSVoIPIADPortsData getIadPorts() {
        return iadPorts;
    }

    public void setIadPorts(CPSVoIPIADPortsData iadPorts) {
        this.iadPorts = iadPorts;
    }

    public String getSipLogin() {
        return sipLogin;
    }

    public void setSipLogin(String sipLogin) {
        this.sipLogin = sipLogin;
    }

    public String getMainNumber() {
        return mainNumber;
    }

    public void setMainNumber(String mainNumber) {
        this.mainNumber = mainNumber;
    }

}
