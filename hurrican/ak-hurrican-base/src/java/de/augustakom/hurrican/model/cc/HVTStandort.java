/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 08:39:17
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell, um einen HVT abzubilden.
 *
 *
 */
public class HVTStandort extends AbstractCCHistoryModel implements HvtIdStandortModel, DebugModel {

    /**
     * Wert fuer <code>gesicherteRealisierung</code> zeigt an, dass der HVT freigegeben bzw. in Betrieb ist.
     */
    public static final String GESICHERT_IN_BETRIEB = "in Betrieb";
    public static final String GESICHERT_NICHT_IN_BETRIEB = "nicht in Betrieb";

    /**
     * ID vom HVT-Standort 'Switch AUG01'
     */
    public static final Long HVT_SWITCH_AUG01 = Long.valueOf(58);
    /**
     * ID vom HVT-Standort 'Switch AUG02'
     */
    public static final Long HVT_SWITCH_AUG02 = Long.valueOf(59);

    /**
     * ID für HVT-Standort Typ 'HVT'
     */
    public static final Long HVT_STANDORT_TYP_HVT = Long.valueOf(11000);
    /**
     * ID für HVT-Standort Typ 'KVZ'
     */
    public static final Long HVT_STANDORT_TYP_KVZ = Long.valueOf(11001);
    /**
     * ID für HVT-Standort Typ 'FTTB'
     */
    public static final Long HVT_STANDORT_TYP_FTTB = Long.valueOf(11002);
    /**
     * ID für HVT-Standort Typ 'EWSD'
     */
    public static final Long HVT_STANDORT_TYP_EWSD = Long.valueOf(11003);
    /**
     * ID für HVT-Standort Typ 'NK'
     */
    public static final Long HVT_STANDORT_TYP_NK = Long.valueOf(11004);
    /**
     * ID für HVT-Standort Typ 'TV'
     */
    public static final Long HVT_STANDORT_TYP_TV = Long.valueOf(11007);
    /**
     * ID für HVT-Standort Typ 'FTTX_FC'
     */
    public static final Long HVT_STANDORT_TYP_FTTX_FC = Long.valueOf(11008);
    /**
     * ID für HVT-Standort Typ 'GEWOFAG'
     */
    public static final Long HVT_STANDORT_TYP_GEWOFAG = Long.valueOf(11009);
    /**
     * ID für HVT-Standort Typ 'ISIS'
     */
    public static final Long HVT_STANDORT_TYP_ISIS = Long.valueOf(11010);
    /**
     * ID für HVT-Standort Typ 'FTTH'
     */
    public static final Long HVT_STANDORT_TYP_FTTH = Long.valueOf(11011);
    /**
     * ID für HVT-Standort Typ 'FTTX_BR'
     */
    public static final Long HVT_STANDORT_TYP_FTTX_BR = Long.valueOf(11012);
    /**
     * ID für HVT-Standort Typ 'FTTC_KVZ'
     */
    public static final Long HVT_STANDORT_TYP_FTTC_KVZ = Long.valueOf(11013);
    /**
     * ID für HVT-Standort Typ 'ABSTRACT'
     */
    public static final Long HVT_STANDORT_TYP_ABSTRACT = Long.valueOf(11014);
    /**
     * ID für HVT-Standort Typ 'FTTB_H'
     */
    public static final Long HVT_STANDORT_TYP_FTTB_H = Long.valueOf(11017);

    public static final String HVT_STANDORT_LINE_SPECTRUM = "G.fast Line Spectrum";

    public static final String HVT_GRUPPE_ID = "hvtGruppeId";
    private Long hvtGruppeId = null;
    private Integer asb = null;
    private Long standortTypRefId = null;
    private String gesicherteRealisierung = null;
    private Integer ewsdOr1 = null;
    private Integer ewsdOr2 = null;
    private String beschreibung = null;
    private Boolean virtuell = null;
    private Long carrierId = null;
    private Long carrierKennungId = null;
    private Long carrierContactId = null;
    private Boolean cpsProvisioning = null;
    private Boolean breakRangierung = null;
    private Long fcRaumId = null;
    private Long betriebsraumId = null;
    private Boolean autoVerteilen;
    private String clusterId;
    private String gfastStartfrequenz;

    public String getGfastStartfrequenz() {
        return gfastStartfrequenz;
    }

    public void setGfastStartfrequenz(String gfastStartfrequenz) {
        this.gfastStartfrequenz = gfastStartfrequenz;
    }

    /**
     * Ermittelt den DTAG ASB von dem HVT-Standort. Ist der Wert von 'asb' > 999 wird davon ausgegangen, dass es sich um
     * einen FttX Standort von M-net handelt. In diesem Fall sind die letzten drei Ziffern von 'asb' der DTAG ASB.
     *
     * @return
     *
     */
    @Transient
    public Integer getDTAGAsb() {
        return getDTAGAsb(getAsb());
    }

    /**
     * @see HVTStandort#getDTAGAsb()
     */
    public static Integer getDTAGAsb(Integer asb) {
        if ((asb != null) && (asb > 999)) {
            String szAsb = "" + asb;
            String dtagAsb = StringUtils.right(szAsb, 3);
            return Integer.valueOf(dtagAsb);
        }
        return asb;
    }

    /**
     * @see HVTStandort#getDTAGAsb()
     */
    public static Integer getDTAGAsb(String asb) {
        if (StringUtils.isBlank(asb)) {
            return null;
        }
        String dtagAsb = asb;
        if (asb.length() > 3) {
            dtagAsb = StringUtils.right(asb, 3);
        }
        return Integer.valueOf(dtagAsb);
    }

    @Override
    @Transient
    public Long getHvtIdStandort() {
        return getId();
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * Ursprungskennung Leitweg.
     *
     * @return Returns the ewsdOr1.
     */
    public Integer getEwsdOr1() {
        return ewsdOr1;
    }

    /**
     * Ursprungskennung Leitweg.
     *
     * @param ewsdOr1 The ewsdOr1 to set.
     */
    public void setEwsdOr1(Integer ewsdOr1) {
        this.ewsdOr1 = ewsdOr1;
    }

    /**
     * Ursprungskennung Verzonung.
     *
     * @return Returns the ewsdOr2.
     */
    public Integer getEwsdOr2() {
        return ewsdOr2;
    }

    /**
     * Ursprungskennung Verzonung.
     *
     * @param ewsdOr2 The ewsdOr2 to set.
     */
    public void setEwsdOr2(Integer ewsdOr2) {
        this.ewsdOr2 = ewsdOr2;
    }

    public String getGesicherteRealisierung() {
        return gesicherteRealisierung;
    }

    public void setGesicherteRealisierung(String gesicherteRealisierung) {
        this.gesicherteRealisierung = gesicherteRealisierung;
    }

    public Long getHvtGruppeId() {
        return hvtGruppeId;
    }

    public void setHvtGruppeId(Long hvtGruppeId) {
        this.hvtGruppeId = hvtGruppeId;
    }

    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public Boolean getVirtuell() {
        return virtuell;
    }

    public void setVirtuell(Boolean virtuell) {
        this.virtuell = virtuell;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public Long getCarrierKennungId() {
        return carrierKennungId;
    }

    public void setCarrierKennungId(Long carrierKennungId) {
        this.carrierKennungId = carrierKennungId;
    }

    public Long getStandortTypRefId() {
        return standortTypRefId;
    }

    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }

    public Long getCarrierContactId() {
        return carrierContactId;
    }

    public void setCarrierContactId(Long carrierContactId) {
        this.carrierContactId = carrierContactId;
    }

    public Boolean getCpsProvisioning() {
        return cpsProvisioning;
    }

    public void setCpsProvisioning(Boolean cpsProvisioning) {
        this.cpsProvisioning = cpsProvisioning;
    }

    public Boolean getBreakRangierung() {
        return breakRangierung;
    }

    public void setBreakRangierung(Boolean breakRangierung) {
        this.breakRangierung = breakRangierung;
    }

    public Long getFcRaumId() {
        return fcRaumId;
    }

    public void setFcRaumId(Long fcRaumId) {
        this.fcRaumId = fcRaumId;
    }

    public Long getBetriebsraumId() {
        return betriebsraumId;
    }

    public void setBetriebsraumId(Long betriebsraumId) {
        this.betriebsraumId = betriebsraumId;
    }

    public Boolean getAutoVerteilen() {
        return autoVerteilen;
    }

    public void setAutoVerteilen(Boolean autoVerteilen) {
        this.autoVerteilen = autoVerteilen;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * Ermittelt, ob der HVT-Standort vom übergebenen Typ ist.
     *
     * @param standortTyp der zu überprüfende Typ
     * @return
     *
     */
    public boolean isStandortType(Long standortTyp) {
        if (NumberTools.notEqual(getStandortTypRefId(), standortTyp)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Ueberprueft, ob der Standort ein FttX (FTTB/FTTH) Standort bzw. FttX Funktions- bzw. Betriebsraum ist.
     *
     * @return
     */
    @Transient
    public boolean isFtthOrFttb() {
        return NumberTools.isIn(getStandortTypRefId(),
                new Long[] {
                        HVT_STANDORT_TYP_FTTB,
                        HVT_STANDORT_TYP_FTTH,
                        HVT_STANDORT_TYP_FTTX_BR,
                        HVT_STANDORT_TYP_FTTX_FC,
                        HVT_STANDORT_TYP_FTTB_H}
        );
    }

    /**
     * Ueberprueft, ob der Standort ein FttC KVZ ist.
     *
     * @return
     */
    @Transient
    public boolean isFttc() {
        return NumberTools.isIn(getStandortTypRefId(),
                new Long[] {
                        HVT_STANDORT_TYP_FTTC_KVZ }
        );
    }

    @Transient
    public boolean isActive(Date toCheck) {
        boolean isActive = false;
        if (getGueltigVon() != null) {
            Date from = DateUtils.truncate(getGueltigVon(), Calendar.DAY_OF_MONTH);
            Date to = (getGueltigBis() != null) ? DateUtils.truncate(getGueltigBis(), Calendar.DAY_OF_MONTH)
                    : DateTools.getHurricanEndDate();
            isActive = DateTools.isDateBetween(toCheck, from, to);
        }
        return isActive;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + HVTStandort.class.getName());
            logger.debug("  HVT-Gruppe  : " + getId());
            logger.debug("  Beschreibung: " + getBeschreibung());
            logger.debug("  EWSD 1 / 2  : " + getEwsdOr1() + " / " + getEwsdOr2());
        }
    }

}


