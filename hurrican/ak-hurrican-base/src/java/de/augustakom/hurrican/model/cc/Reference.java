/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 08:18:40
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell fuer die Abbildung von Referenz-Daten.
 *
 *
 */
public class Reference extends AbstractCCIDModel {

    private static final long serialVersionUID = 7433814237565898149L;

    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die eine Einheit darstellen.
     */
    public static final String REF_TYPE_UNIT = "EINHEIT";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die einen Mehrwertsteuersatz darstellen.
     */
    public static final String REF_TYPE_MWST = "MWST";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die einen VPN-Typ darstellen.
     */
    public static final String REF_TYPE_VPNTYPE = "VPN_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die eine Laufzeit darstellen.
     */
    public static final String REF_TYPE_LAUFZEIT = "LAUFZEIT";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die Internet-Protokolle darstellen.
     */
    public static final String REF_TYPE_INTERNET_PROTOCOL = "INTERNET_PROTOCOL";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die eine Einwahlrufnummer darstellen.
     */
    public static final String REF_TYPE_EINWAHLDN_4_PRODUKT = "EINWAHL_DN_4_PRODUKT";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die den Modus von VoIP-Endgeraeten darstellen.
     */
    public static final String REF_TYPE_VOIP_EG_MODE = "VOIP_EG_MODE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die eine TAL-Bestellungstyp darstellen.
     */
    public static final String REF_TYPE_TAL_BESTELLUNG_TYP = "TAL_BESTELLUNG_TYP";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die eine TAL-Bestellungsstatus darstellen.
     */
    public static final String REF_TYPE_TAL_BESTELLUNG_STATUS = "TAL_BESTELLUNG_STATUS";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die einen internen Geschaeftsfall-Typ darstellen.
     */
    public static final String REF_TYPE_TAL_INTERNER_GF_TYP = "TAL_INTERNER_GF_TYP";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen, die Adress-Typen darstellen.
     */
    public static final String REF_TYPE_ADDRESS_TYPE = "ADDRESS_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer das Uebertragungsverfahren von Equipments.
     */
    public static final String REF_TYPE_EQ_UETV = "EQ_UETV";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer Quality-of-Service Klassen.
     */
    public static final String REF_TYPE_QOS_CLASS = "QOS_CLASS";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen für HVT-Standort-Typen.
     */
    public static final String REF_TYPE_STANDORT_TYP = "STANDORT_TYP";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen für Installationsarten.
     */
    public static final String REF_TYPE_INSTALLATION_TYPE = "INSTALLATION_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer HW-Rack-Typen.
     */
    public static final String REF_TYPE_HW_RACK_TYPE = "HW_RACK_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer HW-DLUs.
     */
    public static final String REF_TYPE_HW_DLU_TYPE = "HW_DLU_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer HW-LTGs.
     */
    public static final String REF_TYPE_HW_LTG_TYPE = "HW_LTG_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen auf ERX-Standorte.
     */
    public static final String REF_TYPE_HW_ERX_STANDORT = "HW_ERX_STANDORT";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer Physikarten.
     */
    public static final String REF_TYPE_HW_PHYSIK_ART = "HW_PHYSIK_ART";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer die Niederlassungskennzeichnung bei Innenauftraegen.
     */
    public static final String REF_TYPE_IA_NIEDERLASSUNG = "IA_NIEDERLASSUNG";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer die Raumarten bei Innenauftraegen.
     */
    public static final String REF_TYPE_IA_BETRIEBSRAUM = "IA_BETRIEBSRAUM";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer Innenauftragstypen.
     */
    public static final String REF_TYPE_IA_ART = "IA_ART";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer interne Arbeits-Typen.
     */
    public static final String REF_TYPE_WORKING_TYPE = "WORKING_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer CPS Order-Types.
     */
    public static final String REF_TYPE_CPS_SERVICE_ORDER_TYPE = "CPS_SERVICE_ORDER_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer CPS Order-Prios.
     */
    public static final String REF_TYPE_CPS_SERVICE_ORDER_PRIO = "CPS_SERVICE_ORDER_PRIO";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer CPS Stati.
     */
    public static final String REF_TYPE_CPS_TX_STATE = "CPS_TX_STATE";
    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer Zusatzaufwaende bei Realisierungen.
     */
    public static final String REF_TYPE_ZUSATZ_AUFWAND = "ZUSATZ_AUFWAND";
    /**
     * Wert fuer <code>type</code> kennzeichnet ein Mapping zwischen Reseller-ID und Niederlassungs-ID.
     */
    public static final String REF_TYPE_RESELLER_2_NL = "RESELLER_2_NL";
    /**
     * Wert fuer <code>type</code> kennzeichnet die moeglichen Sub-Bereiche vom AM.
     */
    public static final String REF_TYPE_AM_PART = "AM_PART";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Gruende, weshalb ein Verlauf nicht durchgefuehrt werden
     * konnte.
     */
    public static final String REF_TYPE_VERLAUF_REASONS = "VERLAUF_REASONS";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Lock-Modes.
     */
    public static final String REF_TYPE_LOCK_MODE = "LOCK_MODE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Lock-Stati.
     */
    public static final String REF_TYPE_LOCK_STATE = "LOCK_STATE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Lock-Reasons.
     */
    public static final String REF_TYPE_LOCK_REASON = "LOCK_REASON";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche CrossConnection-Typen.
     */
    public static final String REF_TYPE_XCONN_TYPES = "XCONN_TYPES";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Ansprechpartner-Typen.
     */
    public static final String REF_TYPE_ANSPRECHPARTNER = "ANSPRECHPARTNER";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Werte fuer Verbindungsbezeichnung Nutzungsart 'Produkt'.
     */
    public static final String REF_TYPE_VBZ_PRODUCT = "VBZ_PRODUCT";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Werte fuer Verbindungsbezeichnung Nutzungsart 'Typ'.
     */
    public static final String REF_TYPE_VBZ_TYPE = "VBZ_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Werte fuer einen IP-Sec-Token-Status.
     */
    public static final String REF_TYPE_IPSEC_TOKEN_STATUS = "IPSEC_TOKEN_STATUS";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Gruende fuer einen Port-Wechsel.
     */
    public static final String REF_TYPE_PORT_CHANGE_REASON = "PORT_CHANGE_REASON";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Gruende fuer einen Wholesale Port-Wechsel.
     */
    public static final String REF_TYPE_PORT_CHANGE_REASON_WS = "PORT_CHANGE_REASON_WS";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Varianten fuer Port- bzw. Baugruppen-Schwenks.
     */
    public static final String REF_TYPE_PORT_SCHWENK = "PORT_SCHWENK";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Varianten fuer Port- bzw. Baugruppen-Schwenk Stati.
     */
    public static final String REF_TYPE_PORT_SCHWENK_STATUS = "PORT_SCHWENK_STATUS";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Technologietyp Varianten für Vento (ADSL, ADSL_POTS etc.).
     */
    public static final String REF_TYPE_TECHNOLOGY_TYPE = "TECHNOLOGY_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Klärungsvarianten für den Geo ID Synch zwischen Vento und
     * Hurrican.
     */
    public static final String REF_TYPE_GEOID_CLARIFICATION_STATUS = "GEOID_CLARIFICATION_STATUS";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Klärungstyp Varianten für den Geo ID Synch zwischen Vento und
     * Hurrican.
     */
    public static final String REF_TYPE_GEOID_CLARIFICATION_TYPE = "GEOID_CLARIFICATION_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche SIP Domain Varianten für ein Produkt/Auftrag.
     */
    public static final String REF_TYPE_SIP_DOMAIN_TYPE = "SIP_DOMAIN_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche IP V4-Verwendungszwecke.
     */
    public static final String REF_TYPE_IP_PURPOSE_TYPE_V4 = "IP_PURPOSE_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche IP V6-Verwendungszwecke.
     */
    public static final String REF_TYPE_IP_PURPOSE_TYPE_V6 = "IP_PURPOSE_TYPE_V6";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche IP V4/V6 Backbone Standorte.
     */
    public static final String REF_TYPE_IP_BACKBONE_LOCATION_TYPE = "IP_BACKBONE_LOCATION_TYPE";
    /**
     * Wert fuer <code>type</code> gibt die Stunden des zusaetzlichen Puffers zu den 36h vor der VLT an.
     */
    public static final String REF_TYPE_WITA_VLT_PUFFERZEIT = "WITA_VLT_PUFFERZEIT";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Kontakt Varianten für einen Carrier (WBCI-Kontakt,
     * WITA-Kontakt etc.).
     */
    public static final String REF_TYPE_CARRIER_CONTACT_TYPE = "CARRIER_CONTACT_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Storno Begründungen für STR-AEN (Terminvorziehung, Umzug
     * etc.).
     */
    public static final String REF_TYPE_STR_AEN_REASON = "STR_AEN_REASON_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche Storno Begründungen für STR-AUF (Widerruf, Tod etc.).
     */
    public static final String REF_TYPE_STR_AUF_REASON = "STR_AUF_REASON_TYPE";
    /**
     * Wert fuer <code>type</code> kennzeichnet moegliche ABBM Gründe.
     */
    public static final String REF_TYPE_ABBM_REASON_TYPE = "ABBM_REASON_TYPE";

    /**
     * Wert fuer <code>type</code> kennzeichnet Referenzen fuer HW-DSLAMs.
     */
    public static final String REF_TYPE_HW_DSLAM_TYPE = "HW_DSLAM_TYPE";


    /**
     * Wert für <code>type</code> definiert mögliche Werte für TDD-Profil in "T_HW_RACK_DPU"-Tabelle
     */
    public static final String REF_TYPE_HW_RACK_DPU_TDD_PROFIL = "HW_RACK_DPU_TDD_PROFIL";

    /**
     * Wert fuer <code>type</code> enthält kommasepariert im STR_VALUE alle gültigen OE_NO oder VATER_OE_NO Einträge.
     * Werden benötigt, um beim CustomerOrderService zu entscheiden, ob für einen Auftrag Zugangsdaten an das Portal
     * uebergeben werden duerfen.
     */
    public static final String REF_TYPE_PROVIDED_OE_NO = "PROVIDED_OE_NO_TYPE";

    /**
     * Wert fuer <code>type</code> enthaelt kommasepariert im STR_VALUE alle OE_NOs aus der Billing OE-Tabelle. Werden
     * benoetigt, um im LoginDataService zu steuern, ob auf einen IMS-Switch geprueft werden soll.
     */
    public static final String REF_NO_IMS_CHECK_4_OE_NO_TYPE = "NO_IMS_CHECK_4_OE_NO_TYPE";

    public static final Long REF_ID_WORK_TYPE_NEW = 13000L;

    public static final Long REF_ID_MONTAGE_CUSTOMER = 900L;
    public static final Long REF_ID_MONTAGE_MNET = 901L;
    public static final Long REF_ID_ABBM_REASON_TYPE_KARENZZEIT = 23302L;

    public static final Long REF_ID_SIP_DOMAIN_MAXI_M_CALL = 22344L;       // IMS
    public static final Long REF_ID_SIP_DOMAIN_MAXI_M_CALL_MUC06 = 22349L; // NSP Prod
    public static final Long REF_ID_SIP_DOMAIN_MAXI_M_CALL_MUC07 = 22354L; // NSP Test

    private String strValue;
    public static final String STR_VALUE = "strValue";
    private Integer intValue;
    private Float floatValue;
    private String type;
    private Long unitId;
    private Boolean guiVisible;
    private Integer orderNo;
    private String wbciTechnologieCode;

    /**
     * nicht persisitentes Property!
     */
    private String guiText;

    public Reference() {
        super();
    }

    /**
     * Konstruktor mit Angabe des Referenz-Typs.
     */
    public Reference(String type) {
        super();
        setType(type);
    }

    public String getGuiText() {
        return this.guiText;
    }

    public void setGuiText(String guiText) {
        this.guiText = guiText;
    }

    public Float getFloatValue() {
        return this.floatValue;
    }


    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Boolean getGuiVisible() {
        return this.guiVisible;
    }

    public void setGuiVisible(Boolean guiVisible) {
        this.guiVisible = guiVisible;
    }

    public Integer getIntValue() {
        return this.intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getStrValue() {
        return this.strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUnitId() {
        return this.unitId;
    }

    /**
     * Back-Reference auf ID um die Einheit zu definieren
     */
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the corresponding WBCI-Technologie-Code to {@link de.mnet.wbci.model.Technologie}.
     */
    public String getWbciTechnologieCode() {
        return wbciTechnologieCode;
    }

    public void setWbciTechnologieCode(String wbciTechnologieCode) {
        this.wbciTechnologieCode = wbciTechnologieCode;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((floatValue == null) ? 0 : floatValue.hashCode());
        result = (prime * result) + ((guiText == null) ? 0 : guiText.hashCode());
        result = (prime * result) + ((guiVisible == null) ? 0 : guiVisible.hashCode());
        result = (prime * result) + ((intValue == null) ? 0 : intValue.hashCode());
        result = (prime * result) + ((orderNo == null) ? 0 : orderNo.hashCode());
        result = (prime * result) + ((strValue == null) ? 0 : strValue.hashCode());
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());
        result = (prime * result) + ((unitId == null) ? 0 : unitId.hashCode());
        result = (prime * result) + ((wbciTechnologieCode == null) ? 0 : wbciTechnologieCode.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Reference)) {
            return false;
        }
        Reference other = (Reference) obj;
        if (floatValue == null) {
            if (other.floatValue != null) {
                return false;
            }
        }
        else if (!floatValue.equals(other.floatValue)) {
            return false;
        }
        if (guiText == null) {
            if (other.guiText != null) {
                return false;
            }
        }
        else if (!guiText.equals(other.guiText)) {
            return false;
        }
        if (guiVisible == null) {
            if (other.guiVisible != null) {
                return false;
            }
        }
        else if (!guiVisible.equals(other.guiVisible)) {
            return false;
        }
        if (intValue == null) {
            if (other.intValue != null) {
                return false;
            }
        }
        else if (!intValue.equals(other.intValue)) {
            return false;
        }
        if (orderNo == null) {
            if (other.orderNo != null) {
                return false;
            }
        }
        else if (!orderNo.equals(other.orderNo)) {
            return false;
        }
        if (strValue == null) {
            if (other.strValue != null) {
                return false;
            }
        }
        else if (!strValue.equals(other.strValue)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        }
        else if (!type.equals(other.type)) {
            return false;
        }
        if (unitId == null) {
            if (other.unitId != null) {
                return false;
            }
        }
        else if (!unitId.equals(other.unitId)) {
            return false;
        }
        if (wbciTechnologieCode == null) {
            if (other.wbciTechnologieCode != null) {
                return false;
            }
        }
        else if (!wbciTechnologieCode.equals(other.wbciTechnologieCode)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String
                .format("Reference [strValue=%s, intValue=%s, floatValue=%s, type=%s, unitId=%s, guiVisible=%s, orderNo=%s, guiText=%s]",
                        strValue, intValue, floatValue, type, unitId, guiVisible, orderNo, guiText);
    }

} // end
