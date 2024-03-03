/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 09:44:33
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import org.apache.commons.lang.StringUtils;

/**
 * Modell bildet eine technische Dokumentationsnummer ab.
 *
 *
 */
@Entity
@Table(name = "T_TDN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_TDN_0", allocationSize = 1)
public class VerbindungsBezeichnung extends AbstractCCIDModel {

    private static final long serialVersionUID = 6598044619045073479L;

    public static final String CUST_IDENT_SEPARATOR = "-";
    public static final String TAL_ID_SEPARATOR = "_";
    // Name, der für Verbindungsbezeichnung in der GUI angezeigt wird:
    public static final String VBZ_BEZEICHNUNG = "Verbindungsbezeichnung (VBZ)";
    // TODO Wenn alles in Verbindungsbezeichnung umbenannt ist, dann entfernen.
    public static final String OLD_VBZ_NAME = "TDN";
    public static final String OLD_VBZ_NAME_CAMEL_CASE = "Tdn";
    // TODO manuelles editieren überdenken, sobald Navision abgelöst ist
    public static final Integer MAX_UNIQUE_CODE_MANUAL = 1100000;
    public static final String VBZ = "vbz";
    public static final String CUSTOMER_IDENT = "customerIdent";
    public static final String KIND_OF_USE_PRODUCT = "kindOfUseProduct";
    public static final String KIND_OF_USE_TYPE = "kindOfUseType";
    public static final String UNIQUE_CODE = "uniqueCode";
    public static final String OVERWRITTEN = "overwritten";
    public static final String WBCI_LINE_ID = "wbciLineId";
    private static final String MAIN_VBZ_FORMAT = "%s%s%08d";
    private String vbz = null;
    private String customerIdent = null;
    private String kindOfUseProduct = null;
    private String kindOfUseType = null;
    private Integer uniqueCode = null;
    private Boolean overwritten = null;
    private String wbciLineId;

    /**
     * Erstellt den VBZ-String nach folgenden Regeln: - UniqueCode gesetzt - CustomerIdent + "-" (falls vorhanden) -
     * kindOfUseProdukt + kindOfUseType + uniqueCode - sonst VBZ
     *
     * @return VBZ-String
     */
    public static String createVbzValue(String vbz, String customerIdent, Integer uniqueCode,
            String kindOfUseProduct, String kindOfUseType) {
        if (uniqueCode != null) {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(customerIdent)) {
                builder.append(customerIdent);
                builder.append(CUST_IDENT_SEPARATOR);
            }
            builder.append(String.format(MAIN_VBZ_FORMAT, kindOfUseProduct, kindOfUseType, uniqueCode));
            return builder.toString();
        }
        return vbz;
    }

    /**
     * @see #createVbzValue(String, String, Integer, String, String)
     */
    @Transient
    public String createVbzValue() {
        return createVbzValue(getVbz(), getCustomerIdent(), getUniqueCode(), getKindOfUseProduct(), getKindOfUseType());
    }

    /**
     * Gibt entweder den 'uniqueCode' inkl. der Nutzungsart oder den 'verbindungsBezeichnung' value zurueck.
     *
     * @return
     */
    @Transient
    public String getUniqeCodeWithUseTypeOrVbz() {
        if (getUniqueCode() != null) {
            return String.format(MAIN_VBZ_FORMAT, kindOfUseProduct, kindOfUseType, uniqueCode);
        }
        return getVbz();
    }

    @Column(name = "TDN")
    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    @Column(name = "CUSTOMER_IDENT")
    public String getCustomerIdent() {
        return customerIdent;
    }

    public void setCustomerIdent(String customerIdent) {
        this.customerIdent = customerIdent;
    }

    @Column(name = "KIND_OF_USE_PRODUCT")
    public String getKindOfUseProduct() {
        return kindOfUseProduct;
    }

    public void setKindOfUseProduct(String kindOfUseProduct) {
        this.kindOfUseProduct = kindOfUseProduct;
    }

    public void setKindOfUseProduct(KindOfUseProduct kindOfUseProduktEnum) {
        this.kindOfUseProduct = kindOfUseProduktEnum.name();
    }

    @Column(name = "KIND_OF_USE_TYPE")
    public String getKindOfUseType() {
        return kindOfUseType;
    }

    public void setKindOfUseType(String kindOfUseType) {
        this.kindOfUseType = kindOfUseType;
    }

    @Column(name = "UNIQUE_CODE")
    public Integer getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(Integer uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Column(name = "OVERWRITTEN")
    public Boolean getOverwritten() {
        return overwritten;
    }

    public void setOverwritten(Boolean overwritten) {
        this.overwritten = overwritten;
    }

    /**
     * Value will be used for WBCI messages to transfer a LineId in the preagreement to the receiving carrier.
     *
     * @return
     */
    @Column(name = "WBCI_LINE_ID")
    public String getWbciLineId() {
        return wbciLineId;
    }

    public void setWbciLineId(String wbciLineId) {
        this.wbciLineId = wbciLineId;
    }

    public enum KindOfUseProduct {C, F, H, M, W, V}

    public enum KindOfUseType {A, B, C, D, E, F, I, K, L, M, O, P, S, T, V, W, X}

}
