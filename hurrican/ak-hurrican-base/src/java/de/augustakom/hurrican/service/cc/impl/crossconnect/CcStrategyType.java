/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.augustakom.hurrican.service.cc.impl.crossconnect;

import java.util.*;


/**
 *
 */
public class CcStrategyType {
    public enum NlType {
        MUC,
        AGB,
        MKK,
        NBG
    }

    public enum TechType {
        SIE, ALC, HUA
    }

    public enum TermType {
        NT, LT, BRAS
    }

    public enum DslType {
        ADSL, SDSL, VDSL2
    }

    public enum HwType {
        ATM, IP
    }

    public enum CcType {
        HSI, CPE, VOIP, IAD
    }

    private NlType nlType;
    private TechType techType;
    private DslType dslType;
    private HwType hwType;
    private TermType termType;
    private CcType ccType;
    private Boolean voip;
    private Boolean ipV6;
    private String modelType;

    private static final Set<NlType> nlNull = nullSet();
    private static final Set<TechType> techNull = nullSet();
    private static final Set<DslType> dslNull = nullSet();
    private static final Set<HwType> hwNull = nullSet();
    private static final Set<TermType> termNull = nullSet();
    private static final Set<CcType> ccNull = nullSet();


    private static <T> Set<T> nullSet() {
        Set<T> result = new HashSet<T>(1);
        result.add(null);
        return Collections.unmodifiableSet(result);
    }

    public static List<CcStrategyType> create(Set<NlType> nlTypes, Set<TechType> techTypes, String modelType,
            Set<DslType> dslTypes, Set<HwType> hwTypes, Set<TermType> termTypes,
            Set<CcType> ccTypes, Boolean voip, Boolean ipV6) {
        List<CcStrategyType> result = new ArrayList<CcStrategyType>();
        for (NlType nlType : (nlTypes != null ? nlTypes : nlNull)) {
            for (TechType techType : (techTypes != null ? techTypes : techNull)) {
                for (DslType dslType : (dslTypes != null ? dslTypes : dslNull)) {
                    for (HwType hwType : (hwTypes != null ? hwTypes : hwNull)) {
                        for (TermType termType : (termTypes != null ? termTypes : termNull)) {
                            for (CcType ccType : (ccTypes != null ? ccTypes : ccNull)) {
                                result.add(new CcStrategyType(nlType, techType, modelType, dslType, hwType, termType,
                                        ccType, voip, ipV6));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public CcStrategyType(NlType nlType, TechType techType, String modelType, DslType dslType,
            HwType hwType, TermType termType, CcType ccType, Boolean voip, Boolean ipV6) {
        this.nlType = nlType;
        this.techType = techType;
        this.modelType = modelType;
        this.dslType = dslType;
        this.hwType = hwType;
        this.ccType = ccType;
        this.termType = termType;
        this.voip = voip;
        this.ipV6 = ipV6;
    }

    public CcStrategyType() {
        /* default constructor */
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[StrategyType: NL: ");
        result.append(nlType != null ? nlType.toString() : "null");
        result.append(" | Tech: ");
        result.append(techType != null ? techType.toString() : "null");
        result.append(" | ModelType: ");
        result.append(modelType);
        result.append(" | DSL: ");
        result.append(dslType != null ? dslType.toString() : "null");
        result.append(" | ATM/IP: ");
        result.append(hwType != null ? hwType.toString() : "null");
        result.append(" | Term: ");
        result.append(termType != null ? termType.toString() : "null");
        result.append(" | CC: ");
        result.append(ccType != null ? ccType.toString() : "null");
        result.append(" | VoIP: ");
        result.append(voip != null ? voip.toString() : "null");
        result.append(" | IPv6: ");
        result.append(ipV6 != null ? ipV6.toString() : "null");
        result.append("]");
        return result.toString();
    }

    /**
     * Die Reihenfolge der Pruefungen hier sollte weight() und dem Konstruktor entsprechen
     */
    public boolean compare(CcStrategyType o) {
        boolean good = true;
        if ((o.nlType != null) && !o.nlType.equals(this.nlType)) {
            good = false;
        }
        if ((o.techType != null) && !o.techType.equals(this.techType)) {
            good = false;
        }
        if ((o.modelType != null) && !o.modelType.equals(this.modelType)) {
            good = false;
        }
        if ((o.dslType != null) && !o.dslType.equals(this.dslType)) {
            good = false;
        }
        if ((o.hwType != null) && !o.hwType.equals(this.hwType)) {
            good = false;
        }
        if ((o.termType != null) && !o.termType.equals(this.termType)) {
            good = false;
        }
        if ((o.ccType != null) && !o.ccType.equals(this.ccType)) {
            good = false;
        }
        if ((o.voip != null) && !o.voip.equals(this.voip)) {
            good = false;
        }
        if ((o.ipV6 != null) && !o.ipV6.equals(this.ipV6)) {
            good = false;
        }
        return good;
    }

    // @formatter:off
    /**
     * Subset examples:
     * <pre>
     *     Referenz:    NULL    BBB    CCC
     *           Ja:    AAA     BBB    CCC
     *         Nein:    AAA     NULL   CCC
     *         Nein:    NULL    BBB    CCC
     * </pre>
     */
    // @formatter:on
    public boolean isSubsetOf(CcStrategyType other) {
        if (((nlType == null) && (other.nlType != null)) ||
                ((techType == null) && (other.techType != null)) ||
                ((modelType == null) && (other.modelType != null)) ||
                ((dslType == null) && (other.dslType != null)) ||
                ((hwType == null) && (other.hwType != null)) ||
                ((termType == null) && (other.termType != null)) ||
                ((ccType == null) && (other.ccType != null)) ||
                ((voip == null) && (other.voip != null)) ||
                ((ipV6 == null) && (other.ipV6 != null))) {
            return false;
        }
        if (((nlType != null) == (other.nlType != null)) &&
                ((techType != null) == (other.techType != null)) &&
                ((modelType != null) == (other.modelType != null)) &&
                ((dslType != null) == (other.dslType != null)) &&
                ((hwType != null) == (other.hwType != null)) &&
                ((termType != null) == (other.termType != null)) &&
                ((ccType != null) == (other.ccType != null)) &&
                ((voip != null) == (other.voip != null)) &&
                ((ipV6 != null) == (other.ipV6 != null))) {
            return false;
        }
        return true;
    }

    public TechType getTechType() {
        return techType;
    }

    public void setTechType(TechType techType) {
        this.techType = techType;
    }

    public NlType getNlType() {
        return nlType;
    }

    public void setNlType(NlType nlType) {
        this.nlType = nlType;
    }

    public DslType getDslType() {
        return dslType;
    }

    public void setDslType(DslType dslType) {
        this.dslType = dslType;
    }

    public HwType getHwType() {
        return hwType;
    }

    public void setHwType(HwType hwType) {
        this.hwType = hwType;
    }

    public CcType getCcType() {
        return ccType;
    }

    public void setCcType(CcType ccType) {
        this.ccType = ccType;
    }

    public Boolean getVoip() {
        return voip;
    }

    public void setVoip(Boolean voip) {
        this.voip = voip;
    }

    public TermType getTermType() {
        return termType;
    }

    public void setTermType(TermType termType) {
        this.termType = termType;
    }

    public Boolean getIpV6() {
        return this.ipV6;
    }

    public void setIpV6(Boolean ipV6) {
        this.ipV6 = ipV6;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

}
