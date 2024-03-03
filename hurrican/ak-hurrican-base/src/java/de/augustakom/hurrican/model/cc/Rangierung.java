/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2004 14:23:33
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;

/**
 * Modell bildet eine Rangierung ab.
 * <p/>
 * Achtung: Eigene equals()-Semantik!
 *
 *
 */
public class Rangierung extends AbstractCCHistoryModel implements HvtIdStandortModel, DebugModel {

    /**
     * Wert fuer ES_ID der anzeigt, dass die Rangierung z.Z. nicht verwendet wird.
     */
    public static final Long RANGIERUNG_NOT_ACTIVE = Long.valueOf(-1);

    /**
     * Definiert ein RegEx Pattern fuer Bemerkungen aus Nuernberg, mit dem Um-Verkabelungen einer Rangierung
     * protokolliert werden.
     */
    static final String PATTERN_BEMERKUNG_UMBAU_NBG = "Querverbindung:.*";

    /**
     * Wert fuer 'freigegeben' zeigt an, dass
     */
    public enum Freigegeben {
        /**
         * die Rangierung den Status 'gesperrt' hat.
         */
        gesperrt(0),
        /**
         * die Rangierung den Status 'frei' hat.
         */
        freigegeben(1),
        /**
         * die Rangierung deaktiviert wurde.
         */
        deactivated(2),
        /**
         * die Rangierung wg. Backbone-Ueberlastung gesperrt ist.
         */
        Backbone_down(4),
        /**
         * die Rangierung wg. WEPLA gesperrt ist (Umbau).
         */
        WEPLA(5),
        /**
         * ein Port der Rangierung defekt ist.
         */
        defekt(7),
        /**
         * Rangierung gesperrt - Grund nicht definiert
         */
        undefined(8),
        /**
         * sich die Rangierung im Aufbau befindet.
         */
        in_Aufbau(9);

        private final int freigegebenValue;

        private Freigegeben(int value) {
            this.freigegebenValue = value;

        }

        public static Freigegeben getFreigegeben(int value) {
            for (Freigegeben freigegebenEnum : values()) {
                if (freigegebenEnum.freigegebenValue == value) { return freigegebenEnum; }
            }
            return null;
        }

        public String getFreigegebenValueAsString() {
            return String.format("%s", freigegebenValue);
        }
    }

    private Long eqInId = null;
    public static final String EQ_IN_ID = "eqInId";
    private Long eqOutId = null;
    public static final String EQ_OUT_ID = "eqOutId";
    private Long hvtIdStandort = null;
    private Long physikTypId = null;
    private Freigegeben freigegeben = null;
    private Date freigabeAb = null;
    private Integer leitungGesamtId = null;
    public static final String LEITUNG_GESAMT_ID = "leitungGesamtId";
    private Integer leitungLfdNr = null;
    private Boolean leitungLoeschen = null;
    private String bemerkung = null;
    private String userW = null;
    private Date dateW = null;
    public static final String ES_ID = "esId";
    private Long esId = null;
    private Long historyFrom = null;
    private Integer historyCount = null;
    private Long rangierungsAuftragId = null;
    private String ontId = null;

    private Equipment equipmentIn = null;
    private Equipment equipmentOut = null;

    /**
     * Ueberprueft, ob der hinterlegte Physiktyp ein FTTB bzw. FTTH Physiktyp ist.
     *
     * @return <code>true</code>, wenn es sich bei dem Physiktyp um FTTB/FTTH handelt.
     */
    @Transient
    public boolean isFttBOrH() {
        return (NumberTools.isIn(getPhysikTypId(), new Number[] { PhysikTyp.PHYSIKTYP_FTTH,
                PhysikTyp.PHYSIKTYP_FTTH_RF, PhysikTyp.PHYSIKTYP_FTTH_POTS, PhysikTyp.PHYSIKTYP_FTTH_ETH,
                PhysikTyp.PHYSIKTYP_FTTB_VDSL, PhysikTyp.PHYSIKTYP_FTTB_DPO_VDSL, PhysikTyp.PHYSIKTYP_FTTB_POTS,
                PhysikTyp.PHYSIKTYP_FTTB_RF})) ? true : false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }

        Rangierung r = (Rangierung) obj;
        if (getId() != null) {
            if (!getId().equals(r.getId())) { return false; }
        }
        else if (r.getId() != null) { return false; }

        if (getEqInId() != null) {
            if (!getEqInId().equals(r.getEqInId())) { return false; }
        }
        else if (r.getEqInId() != null) { return false; }

        if (getEqOutId() != null) {
            if (!getEqOutId().equals(r.getEqOutId())) { return false; }
        }
        else if (r.getEqOutId() != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 81).append(getId()).append(eqInId).append(eqOutId).toHashCode();
    }

    /**
     * Prueft, ob die Rangierung 'frei' ist. <br> Eine Rangierung ist frei, wenn folgende Bedingungen erfuellt sind:
     * <br> Endstelle-ID = null <br> Flag freigegeben = true <br> GueltigVon <= now <br> GueltigBis > now <br>
     *
     * @return true wenn die Rangierung frei ist
     */
    public boolean isRangierungFrei(boolean acceptFreigabebereit) {
        if (acceptFreigabebereit) {
            if ((getEsId() != null) && NumberTools.notEqual(getEsId(), RANGIERUNG_NOT_ACTIVE)) { return false; }
        }
        else {
            if (getEsId() != null) { return false; }
        }

        if (BooleanTools.nullToFalse(getFreigegebenBoolean())) {
            Date now = new Date();
            if ((DateTools.isBefore(getGueltigVon(), now) || now.equals(getGueltigVon()))
                    && DateTools.isAfter(getGueltigBis(), now)) { return true; }
        }
        return false;
    }

    /**
     * Prueft, ob die Rangierung einem Auftrag (einer Endstelle) zugeordnet ist. <br> Dies ist dann der Fall, wenn die
     * ES_ID > 0 ist. <br> ACHTUNG: freigabebereite Rangierungen werden als nicht zu einem Auftrag zugeordnet
     * angesehen!
     *
     * @return
     */
    public boolean isAssignedToOrder() {
        return NumberTools.isGreater(getEsId(), Long.valueOf(0));
    }

    /**
     * Ueberprueft, ob die Rangierung im Status 'freigabebereit' steht.
     *
     * @return
     *
     */
    public boolean isRangierungFreigabebereit() {
        if (NumberTools.equal(getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) { return true; }
        return false;
    }

    /**
     * Ueberprueft, ob die Rangierung als 'defekt' markiert ist.
     */
    public boolean isRangierungDefekt() {
        return (getFreigegeben() == Freigegeben.defekt);
    }

    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * Setzt die Bemerkung in folgenden Faellen: <ul> <li>aktuelle Bemerkung entspricht nicht dem Pattern {@link
     * Rangierung#PATTERN_BEMERKUNG_UMBAU_NBG} <li>die zu setzende Bemerkung entspricht dem Pattern {@link
     * Rangierung#PATTERN_BEMERKUNG_UMBAU_NBG} </ul>
     *
     * @param bemerkung The bemerkung to set.
     */
    public void setBemerkung(String bemerkung) {
        boolean changeBemerkung = true;
        if (StringUtils.isNotBlank(getBemerkung())) {
            if ((bemerkung != null) && bemerkung.matches(PATTERN_BEMERKUNG_UMBAU_NBG)) {
                changeBemerkung = true;
            }
            else if (getBemerkung().matches(PATTERN_BEMERKUNG_UMBAU_NBG)) {
                // bestehende Bemerkung nicht loeschen, wenn Umverkabelung dokumentiert
                changeBemerkung = false;
            }
        }

        if (changeBemerkung) {
            this.bemerkung = bemerkung;
        }
    }

    public void removeBemerkung() {
        this.bemerkung = null;
    }

    public Date getDateW() {
        return dateW;
    }

    public void setDateW(Date dateW) {
        this.dateW = dateW;
    }

    public Long getEqInId() {
        return eqInId;
    }

    public void setEqInId(Long eqInId) {
        this.eqInId = eqInId;
    }

    public Long getEqOutId() {
        return eqOutId;
    }

    public void setEqOutId(Long eqOutId) {
        this.eqOutId = eqOutId;
    }

    public void setMatchingEqId(final Long oldId, final Long newId) {
        if (NumberTools.equal(getEqInId(), oldId)) {
            setEqInId(newId);
        }
        else if (NumberTools.equal(getEqOutId(), oldId)) {
            setEqOutId(newId);
        }
    }

    public Long getEsId() {
        return esId;
    }

    public void setEsId(Long esId) {
        this.esId = esId;
    }

    /**
     * Gibt den Status der Rangierung zurueck. <br> Moegliche Werte: <br> <ul> <li>0: nicht freigegeben (kein Grund)
     * <li>1: freigegeben <li>4: gesperrt, wg. Backbone-Ueberlastung <li>5: gesperrt, wg. WEPLA (Kartenumbau) <li>7:
     * gesperrt, weil Port defekt <li>9: Rangierung befindet sich im Aufbau </ul>
     * <p/>
     * Dieses Flag bedeutet nicht, dass die Rangierung noch 'frei' ist!!! (Also einem Auftrag zugeordnet werden kann.)
     * Dazu muessen auch die Felder endstelleId und freigabeAb beruecksichtigt werden. <br> Nur wenn freigegeben=1,
     * endstelleId=NULL und freigabeAb=NULL sind, kann die Rangierung einem neuen Auftrag zugeordnet werden.
     *
     * @return Returns the freigegeben.
     */
    public Freigegeben getFreigegeben() {
        return freigegeben;
    }

    /**
     * @return Gibt <code>true</code> zurueck, wenn der Freigabe-Status 'freigegeben' ist.
     */
    public Boolean getFreigegebenBoolean() {
        return (getFreigegeben() == Freigegeben.freigegeben);
    }

    public void setFreigegeben(Freigegeben freigegeben) {
        this.freigegeben = freigegeben;
    }

    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public Integer getLeitungGesamtId() {
        return leitungGesamtId;
    }

    public void setLeitungGesamtId(Integer leitungGesamtId) {
        this.leitungGesamtId = leitungGesamtId;
    }

    public Integer getLeitungLfdNr() {
        return leitungLfdNr;
    }

    public void setLeitungLfdNr(Integer leitungLfdNr) {
        this.leitungLfdNr = leitungLfdNr;
    }

    /**
     * Gibt an, ob die Leitungs-Kombination (LtgGesId / LtgLfdNr) bei der Rangierungsfreigabe von Hurrican geloescht
     * werden muss.
     *
     * @return Returns the leitungLoeschen.
     */
    public Boolean getLeitungLoeschen() {
        return leitungLoeschen;
    }

    /**
     * Angabe, ob die Leitungs-Kombination (LtgGesId / LtgLfdNr) bei der Rangierungsfreigabe von Hurrican geloescht
     * werden soll.
     *
     * @param leitungLoeschen The leitungLoeschen to set.
     */
    public void setLeitungLoeschen(Boolean leitungLoeschen) {
        this.leitungLoeschen = leitungLoeschen;
    }

    public Long getPhysikTypId() {
        return physikTypId;
    }

    public void setPhysikTypId(Long physikTyp) {
        this.physikTypId = physikTyp;
    }

    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    /**
     * Gibt das Datum zurueck, ab dem die Rangierung wieder verwendet werden darf.
     *
     * @return Returns the freigabeAb.
     */
    public Date getFreigabeAb() {
        return freigabeAb;
    }

    public void setFreigabeAb(Date freigabeAb) {
        this.freigabeAb = freigabeAb;
    }

    /**
     * Gibt die Rangier-ID zurueck, von der dieser Datensatz historisiert wurde.
     *
     * @return Returns the historyFrom.
     */
    public Long getHistoryFrom() {
        return historyFrom;
    }

    public void setHistoryFrom(Long historyFrom) {
        this.historyFrom = historyFrom;
    }

    public Integer getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(Integer historyCount) {
        this.historyCount = historyCount;
    }

    public Long getRangierungsAuftragId() {
        return rangierungsAuftragId;
    }

    public void setRangierungsAuftragId(Long rangierungsAuftragId) {
        this.rangierungsAuftragId = rangierungsAuftragId;
    }

    /**
     * Gibt das Equipment-Modell der IN-Seite zurueck. <br> Achtung: Modell laedt das Equipment-Objekt nicht selbst - es
     * muss uebergeben werden!
     *
     * @return Returns the equipmentIn.
     */
    public Equipment getEquipmentIn() {
        return this.equipmentIn;
    }

    public void setEquipmentIn(Equipment equipmentIn) {
        this.equipmentIn = equipmentIn;
    }

    /**
     * Gibt das Equipment-Modell der OUT-Seite zurueck. <br> Achtung: Modell laedt das Equipment-Objekt nicht selbst -
     * es muss uebergeben werden!
     *
     * @return Returns the equipmentOut.
     */
    public Equipment getEquipmentOut() {
        return this.equipmentOut;
    }

    public void setEquipmentOut(Equipment equipmentOut) {
        this.equipmentOut = equipmentOut;
    }

    public String getOntId() {
        return ontId;
    }

    public void setOntId(String ontId) {
        this.ontId = ontId;
    }

    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Rangierung.class.getName());
            logger.debug("  ID        : " + getId());
            logger.debug("  HVT-ID    : " + getHvtIdStandort());
            logger.debug("  PhysikTyp : " + getPhysikTypId());
            logger.debug("  L-Ges-ID  : " + getLeitungGesamtId());
            logger.debug("  L-Lfd-Nr  : " + getLeitungLfdNr());
            logger.debug("  LtgLoeschen:" + getLeitungLoeschen());
            logger.debug("  FreigabeAb: " + getFreigabeAb());
            logger.debug("  Freigegeben:" + getFreigegeben());
        }
    }

}
