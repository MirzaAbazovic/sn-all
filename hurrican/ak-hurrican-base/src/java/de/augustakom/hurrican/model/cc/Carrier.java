/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.07.2004 15:45:33
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Modell bildet einen Carrier ab.
 */
@Entity
@Table(name = "T_CARRIER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CARRIER_0", allocationSize = 1)
public class Carrier extends AbstractCCIDModel {

    private static final long serialVersionUID = -2602313297793968587L;
    /**
     * ID fuer den Carrier 'AKom'.
     */
    public static final Long ID_AKOM = 4L;
    /**
     * ID fuer den Carrier 'DTAG'
     */
    public static final Long ID_DTAG = 12L;
    /**
     * ID fuer den Carrier 'Telekom Deutschland'
     */
    public static final Long ID_TELEKOM_DEUTSCHLAND = 11L;
    /**
     * ID fuer den Carrier 'MNET'
     */
    public static final Long ID_MNET = 17L;
    /**
     * ID fuer den Carrier 'MNET NGN'
     */
    public static final Long ID_MNET_NGN = 535L;
    /**
     * ID fuer den Carrier 'NEFkom'
     */
    public static final Long ID_NEFKOM = 47L;
    /**
     * ID fuer den Carrier 'QSC'
     */
    public static final Long ID_QSC = 30L;
    /**
     * ID fuer den Carrier 'O2'
     */
    public static final Long ID_O2 = 32L;
    /**
     * ID fuer den Carrier 'wilhelm' - wird fuer Tests als Carrier verwendet, der nicht auf der WITA ist.
     */
    public static final Long ID_WILHELM = 191L;

    /**
     * Bezeichnung fuer den Carrier 'DTAG'.
     */
    public static final String CARRIER_DTAG = "DTAG";

    public static final List<Long> mnetCarrierIds = Collections.unmodifiableList(Arrays.asList(ID_MNET_NGN, ID_MNET, ID_AKOM, ID_NEFKOM));

    private String name = null;
    private Integer orderNo = null;
    private Boolean cbNotwendig = null;
    private String elTalEmpfId = null;
    private String companyName = null;
    private String userW = null;
    private Boolean hasWitaInterface = null;
    private String portierungskennung = null;
    private String ituCarrierCode = null;
    private String witaProviderNameAufnehmend = null;
    private Boolean cudaKuendigungNotwendig = Boolean.TRUE;
    private CarrierVaModus vorabstimmungsModus;

    /**
     * Prueft, ob die Carrier ID zur Familie der MNet Carrier gehoert.
     */
    static public boolean isMNetCarrier(Long carrierId) {
        return mnetCarrierIds.contains(carrierId);
    }

    /**
     * used by AKReflectionListCellRenderer for rendering a ComboBox
     */
    @Transient
    public String getPortierungskennungAndName() {
        return StringTools.join(new String[] { portierungskennung, name }, " - ", true);
    }

    /**
     * Returns the carrier as String with the following format: portierungskennung - name (vorabstimmungsModus). This
     * method is used by AKReflectionListCellRenderer for rendering a ComboBox.
     */
    @Transient
    public String getPortierungskennungAndNameAndVaModus() {
        if (portierungskennung == null && name == null && vorabstimmungsModus == null) {
            return "";
        }
        return String.format("%s - %s (%s)",
                portierungskennung == null ? "" : portierungskennung,
                name == null ? "" : name,
                vorabstimmungsModus == null ? "" : vorabstimmungsModus);
    }

    /**
     * Prueft, ob der Carrier fuer eine el. TAL-Bestellung zugelassen ist. <br> Dies ist dann der Fall, wenn
     * 'elTalEmpfId' gesetzt ist.
     *
     * @return true wenn bei dem Carrier eine el. TAL-Bestellung ausgefuehrt werden kann.
     */
    @Transient
    public boolean isValid4ElTAL() {
        return StringUtils.isNotBlank(getElTalEmpfId());
    }

    @Column(name = "TEXT")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ORDER_NO")
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * Flag, ob bei dem Carrier eine Bestellung ausgeloest werden muss.
     *
     * @return Returns the cbNotwendig.
     */
    @Column(name = "CB_NOTWENDIG")
    public Boolean getCbNotwendig() {
        return cbNotwendig;
    }

    public void setCbNotwendig(Boolean cbNotwendig) {
        this.cbNotwendig = cbNotwendig;
    }

    /**
     * Definiert die Empfaenger-ID fuer el. TAL-Bestellungen.
     *
     * @return Returns the elTalEmpfId.
     */
    @Column(name = "EL_TAL_EMPF_ID")
    public String getElTalEmpfId() {
        return elTalEmpfId;
    }

    public void setElTalEmpfId(String elTalEmpfId) {
        this.elTalEmpfId = elTalEmpfId;
    }

    @Column(name = "COMPANY_NAME")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name = "USERW")
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @NotNull
    @Column(name = "HAS_WITA")
    public Boolean getHasWitaInterface() {
        return hasWitaInterface;
    }

    public void setHasWitaInterface(Boolean hasWitaInterface) {
        this.hasWitaInterface = hasWitaInterface;
    }

    @Column(name = "PORTIERUNGSKENNUNG")
    public String getPortierungskennung() {
        return portierungskennung;
    }

    public void setPortierungskennung(String portierungskennung) {
        this.portierungskennung = portierungskennung;
    }

    @Column(name = "WITA_PROVIDER_NAME_AUFNEHMEND")
    public String getWitaProviderNameAufnehmend() {
        return witaProviderNameAufnehmend;
    }

    public void setWitaProviderNameAufnehmend(String witaProviderNameAufnehmend) {
        this.witaProviderNameAufnehmend = witaProviderNameAufnehmend;
    }

    @NotNull
    @Column(name = "CUDA_KUENDIGUNG", nullable = false)
    public Boolean getCudaKuendigungNotwendig() {
        return cudaKuendigungNotwendig;
    }

    public void setCudaKuendigungNotwendig(final Boolean cudaKuendigungNotwendig) {
        this.cudaKuendigungNotwendig = cudaKuendigungNotwendig;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "VA_MODUS", nullable = false)
    public CarrierVaModus getVorabstimmungsModus() {
        return vorabstimmungsModus;
    }

    public void setVorabstimmungsModus(CarrierVaModus vorabstimmungsModus) {
        this.vorabstimmungsModus = vorabstimmungsModus;
    }

    @Column(name = "ITU_CARRIER_CODE")
    public String getItuCarrierCode() {
        return ituCarrierCode;
    }

    public void setItuCarrierCode(String ituCarrierCode) {
        this.ituCarrierCode = ituCarrierCode;
    }

}
