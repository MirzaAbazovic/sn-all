/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:21:21
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import java.util.*;

import de.augustakom.common.model.HistoryModel;


/**
 * Model-Klasse fuer die Abbildung einer Cross-Connection zu einem Equipment (=Port).
 * <p/>
 * <dl><dt>NT</dt><dd>Network Termination (Netz-Seite)</dd> <dt>LT</dt><dd>Line Termination (CPE-Seite)</dd></dl>
 * <p/>
 * Inner/Outer: jeweils die Inner- und Outer Tags (bei Q in Q VLANs, resp. VP / VC im ATM)
 *
 *
 *
 */
public class EQCrossConnection extends AbstractCCIDModel implements HistoryModel {

    private static final long serialVersionUID = -8834708469198477938L;
    /**
     * Reference-ID fuer den CrossConnection Typ "IAD Management Residential Bridge".
     */
    public static final Long REF_ID_XCONN_IAD_MGM_RB = Long.valueOf(20002);
    /**
     * Reference-ID fuer den CrossConnection Typ "CPE Management Residential Bridge".
     */
    public static final Long REF_ID_XCONN_CPE_MGM_RB = Long.valueOf(20003);
    /**
     * Reference-ID fuer den CrossConnection Typ "Unknown CrossConnection type".
     */
    public static final Long REF_ID_XCONN_UNKNOWN_XCONN = Long.valueOf(20005);
    /**
     * Reference-ID fuer den CrossConnection Typ "VoIP CrossConnection".
     */
    public static final Long REF_ID_XCONN_VOIP_XCONN = Long.valueOf(20001);
    /**
     * Reference-ID fuer den CrossConnection Typ "HighSpeedInternet CrossConnection".
     */
    public static final Long REF_ID_XCONN_HSI_XCONN = Long.valueOf(20000);
    /**
     * Reference-ID fuer den CrossConnection Typ "QSC Data".
     */
    public static final Long REF_ID_XCONN_QSC_HSI = Long.valueOf(20010);
    /**
     * Reference-ID fuer den CrossConnection Typ "QSC Management".
     */
    public static final Long REF_ID_XCONN_QSC_MGMT = Long.valueOf(20011);

    private static final Map<Long, String> nameMap = new HashMap<Long, String>();

    static {
        nameMap.put(REF_ID_XCONN_CPE_MGM_RB, "CPE Mgmt");
        nameMap.put(REF_ID_XCONN_HSI_XCONN, "HSI");
        nameMap.put(REF_ID_XCONN_IAD_MGM_RB, "IAD Mgmt");
        nameMap.put(REF_ID_XCONN_UNKNOWN_XCONN, "Unknown");
        nameMap.put(REF_ID_XCONN_VOIP_XCONN, "VoIP");
        nameMap.put(REF_ID_XCONN_QSC_HSI, "QSC HSI");
        nameMap.put(REF_ID_XCONN_QSC_MGMT, "QSC Mgmt");
    }


    private Long equipmentId;
    public static final String EQUIPMENT_ID = "equipmentId";

    private Long crossConnectionTypeRefId;
    public static final String CROSS_CONNECTION_TYPE_REF_ID = "crossConnectionTypeRefId";

    private Integer ntOuter;
    public static final String NT_OUTER = "ntOuter";

    private Integer ntInner;
    public static final String NT_INNER = "ntInner";

    private Integer ltOuter;
    public static final String LT_OUTER = "ltOuter";

    private Integer ltInner;
    public static final String LT_INNER = "ltInner";

    private Integer brasOuter;
    public static final String BRAS_OUTER = "brasOuter";

    private Integer brasInner;
    public static final String BRAS_INNER = "brasInner";

    private Date gueltigVon;
    public static final String GUELTIG_VON = "gueltigVon";

    private Date gueltigBis;
    public static final String GUELTIG_BIS = "gueltigBis";

    private String userW;
    public static final String USER_W = "userW";

    private Long brasPoolId;
    public static final String BRAS_POOL_ID = "brasPoolId";


    /**
     * Check if this CrossConnection is a HSI CrossConnection
     */
    public boolean isHsi() {
        return REF_ID_XCONN_HSI_XCONN.equals(this.getCrossConnectionTypeRefId());
    }

    /**
     * Check if this CrossConnection is a VoIP CrossConnection
     */
    public boolean isVoip() {
        return REF_ID_XCONN_VOIP_XCONN.equals(this.getCrossConnectionTypeRefId());
    }

    /**
     * Check if this CrossConnection is a CPE Management CrossConnection
     */
    public boolean isCpe() {
        return REF_ID_XCONN_CPE_MGM_RB.equals(this.getCrossConnectionTypeRefId());
    }

    /**
     * Check if this CrossConnection is an IAD Management CrossConnection
     */
    public boolean isIad() {
        return REF_ID_XCONN_IAD_MGM_RB.equals(this.getCrossConnectionTypeRefId());
    }

    /**
     * Check if this CrossConnection is an QSC HSI CrossConnection
     */
    public boolean isQscHsi() {
        return REF_ID_XCONN_QSC_HSI.equals(this.getCrossConnectionTypeRefId());
    }

    /**
     * Check if this CrossConnection is an unknown CrossConnection type
     */
    public boolean isUnknown() {
        return REF_ID_XCONN_UNKNOWN_XCONN.equals(this.getCrossConnectionTypeRefId());
    }


    /**
     * To sort / compare cross connections
     */
    public static final CCTypeComparator CC_TYPE_COMPARATOR = new CCTypeComparator();

    public static class CCTypeComparator implements Comparator<EQCrossConnection>, Serializable {
        private static final long serialVersionUID = -5749638514458827801L;

        @Override
        public int compare(EQCrossConnection o1, EQCrossConnection o2) {
            if ((o1 == null) || (o2 == null) || (o1.getCrossConnectionTypeRefId() == null) || (o2.getCrossConnectionTypeRefId() == null)) {
                return 0;
            }
            return o1.getCrossConnectionTypeRefId().compareTo(o2.getCrossConnectionTypeRefId());
        }
    }

    public boolean hasSameTypeAs(EQCrossConnection toCheck) {
        if (toCheck == null) {
            return false;
        }
        if (CC_TYPE_COMPARATOR.compare(this, toCheck) == 0) {
            return true;
        }
        return false;
    }


    /**
     * Determine if two cross connections have the same type and vc/vp values
     */
    public boolean crossConnectionEqual(EQCrossConnection toCompare) {
        if (crossConnectionTypeRefId == null) {
            if (toCompare.crossConnectionTypeRefId != null) { return false; }
        }
        else if (!crossConnectionTypeRefId.equals(toCompare.crossConnectionTypeRefId)) { return false; }
        if (ltOuter == null) {
            if (toCompare.ltOuter != null) { return false; }
        }
        else if (!ltOuter.equals(toCompare.ltOuter)) { return false; }
        if (ltInner == null) {
            if (toCompare.ltInner != null) { return false; }
        }
        else if (!ltInner.equals(toCompare.ltInner)) { return false; }
        if (ntOuter == null) {
            if (toCompare.ntOuter != null) { return false; }
        }
        else if (!ntOuter.equals(toCompare.ntOuter)) { return false; }
        if (ntInner == null) {
            if (toCompare.ntInner != null) { return false; }
        }
        else if (!ntInner.equals(toCompare.ntInner)) { return false; }
        // Wenn Werte aus einem Pool kommen, koennen berechnete Standard-Werte von
        // den vorhandenen Werten verschieden sein, obwohl die vorhandenen Werte
        // auch Standard-Werte sind, da die berechneten Werte einen neuen VC aus
        // dem Pool bekommen haben. Ausserdem haben CCs bei ATM DSLAMs entweder
        // 8 oder 10 als VP (kann also auch unterschiedlich sein, obwohl Standard).
        if (getBrasPoolId() == null) {
            if (brasInner == null) {
                if (toCompare.brasInner != null) { return false; }
            }
            else if (!brasInner.equals(toCompare.brasInner)) { return false; }
            if (brasOuter == null) {
                if (toCompare.brasOuter != null) { return false; }
            }
            else if (!brasOuter.equals(toCompare.brasOuter)) { return false; }
        }
        return true;
    }


    /**
     * Setzt bras pool id und outer tag
     */
    public void setBrasPool(BrasPool brasPool) {
        if (brasPool == null) {
            setBrasPoolId(null);
        }
        else {
            setBrasPoolId(brasPool.getId());
            setBrasOuter(brasPool.getVp());
        }
    }

    public void setCrossConnectionType(Reference crossConnectionTypeRef) {
        if (crossConnectionTypeRef == null) {
            setCrossConnectionTypeRefId(null);
        }
        else {
            setCrossConnectionTypeRefId(crossConnectionTypeRef.getId());
        }
    }


    @Override
    public String toString() {
        return String.format("CrossConnection %8s: LT [%3d/%4d] -- NT [%3d/%4d] -- BRAS [%3d/%4d]",
                nameMap.get(getCrossConnectionTypeRefId()),
                getLtOuter(), getLtInner(), getNtOuter(), getNtInner(), getBrasOuter(), getBrasInner());
    }


    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getCrossConnectionTypeRefId() {
        return crossConnectionTypeRefId;
    }

    public void setCrossConnectionTypeRefId(Long crossConnectionTypeRefId) {
        this.crossConnectionTypeRefId = crossConnectionTypeRefId;
    }

    @Override
    public Date getGueltigVon() {
        return gueltigVon;
    }

    @Override
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    @Override
    public Date getGueltigBis() {
        return gueltigBis;
    }

    @Override
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    public Integer getNtOuter() {
        return ntOuter;
    }

    public void setNtOuter(Integer ntOuter) {
        this.ntOuter = ntOuter;
    }

    public Integer getNtInner() {
        return ntInner;
    }

    public void setNtInner(Integer ntInner) {
        this.ntInner = ntInner;
    }

    public Integer getLtOuter() {
        return ltOuter;
    }

    public void setLtOuter(Integer ltOuter) {
        this.ltOuter = ltOuter;
    }

    public Integer getLtInner() {
        return ltInner;
    }

    public void setLtInner(Integer ltInner) {
        this.ltInner = ltInner;
    }

    public Integer getBrasOuter() {
        return brasOuter;
    }

    public void setBrasOuter(Integer brasOuter) {
        this.brasOuter = brasOuter;
    }

    public Integer getBrasInner() {
        return brasInner;
    }

    public void setBrasInner(Integer brasInner) {
        this.brasInner = brasInner;
    }

    public Long getBrasPoolId() {
        return brasPoolId;
    }

    public void setBrasPoolId(Long brasPoolId) {
        this.brasPoolId = brasPoolId;
    }

}
