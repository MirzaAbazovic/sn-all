/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2004 09:49:36
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;
import de.augustakom.hurrican.service.cc.EndgeraeteService;

/**
 * Modell bildet eine Endstelle ab.
 *
 *
 */
public class Endstelle extends AbstractCCIDModel implements HvtIdStandortModel, DebugModel {

    private static final long serialVersionUID = -2864561630276556824L;

    /**
     * Wert fuer <code>endstelleTyp</code> kennzeichnet, dass es sich um die Endstelle 'A' handel.
     */
    public static final String ENDSTELLEN_TYP_A = "A";
    /**
     * Wert fuer <code>endstelleTyp</code> kennzeichnet, dass es sich um die Endstelle 'B' handel.
     */
    public static final String ENDSTELLEN_TYP_B = "B";

    public static final String CB2ES_ID = "cb2EsId";
    private Long cb2EsId = null;
    public static final String ENDSTELLE = "endstelle";
    private String endstelle = null;
    public static final String NAME = "name";
    private String name = null;
    public static final String ORT = "ort";
    private String ort = null;
    public static final String PLZ = "plz";
    private String plz = null;
    public static final String GEO_ID = "geoId";
    private Long geoId = null;
    public static final String ANSCHLUSSART = "anschlussart";
    private Long anschlussart = null;
    public static final String BEMERKUNG = "bemerkungStawa";
    private String bemerkungStawa = null;
    public static final String RANGIER_ID = "rangierId";
    private Long rangierId = null;
    public static final String RANGIER_ID_ADD = "rangierIdAdditional";
    private Long rangierIdAdditional = null;
    public static final String HVT_ID_STANDORT = "hvtIdStandort";
    private Long hvtIdStandort = null;
    public static final String ENDSTELLE_GRUPPE_ID = "endstelleGruppeId";
    private Long endstelleGruppeId = null;
    public static final String ENDSTELLE_TYP = "endstelleTyp";
    private String endstelleTyp = null;
    public static final String LAST_CHANGE = "lastChange";
    private Date lastChange = null;
    public static final String ADDRESS_ID = "addressId";
    private Long addressId = null;
    public static final String VLAN = "vlan";
    private String vlan = null;

    public static final List<String> COPY_DEFAULT = Arrays.asList(ENDSTELLE, NAME, PLZ, ORT, GEO_ID, BEMERKUNG,
            ANSCHLUSSART, HVT_ID_STANDORT);
    public static final List<String> COPY_DEFAULT_WITH_RANGIERUNG = Arrays.asList(ENDSTELLE, NAME, PLZ, ORT,
            GEO_ID, ANSCHLUSSART, HVT_ID_STANDORT, RANGIER_ID, RANGIER_ID_ADD);
    public static final List<String> COPY_SMALL = Arrays.asList(ENDSTELLE, NAME, PLZ, ORT, GEO_ID, ANSCHLUSSART,
            HVT_ID_STANDORT);
    public static final List<String> COPY_LOCATION_DATA = Arrays.asList(ENDSTELLE, NAME, PLZ, ORT, GEO_ID);
    public static final List<String> COPY_PHYSIK = Arrays.asList(GEO_ID, ANSCHLUSSART, HVT_ID_STANDORT, RANGIER_ID,
            RANGIER_ID_ADD);

    public static final List<String> ES_TYPEN = Arrays.asList(ENDSTELLEN_TYP_B, ENDSTELLEN_TYP_A);

    /**
     * @param endstellen    die Endstellen innerhalb derer gesucht werden soll
     * @param expectedEsTyp der gesuchte Endstellen-Typ
     * @return gibt die erste gefundene Endstelle des gesuchten Endstellen-Typs zurueck, oder null falls keine gefunden
     * wurde
     */
    @CheckForNull
    public static Endstelle getEndstelleOfType(@NotNull List<Endstelle> endstellen, @NotNull String expectedEsTyp) {
        for (Endstelle endstelle : endstellen) {
            if (StringUtils.equals(expectedEsTyp, endstelle.getEndstelleTyp())) {
                return endstelle;
            }
        }
        return null;
    }

    /**
     * Ueberprueft, ob es sich bei der Endstelle um die ES Typ 'A' handelt.
     */
    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isEndstelleA() {
        return StringUtils.equals(getEndstelleTyp(), ENDSTELLEN_TYP_A);
    }

    /**
     * Ueberprueft, ob es sich bei der Endstelle um die ES Typ 'B' handelt.
     */
    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isEndstelleB() {
        return StringUtils.equals(getEndstelleTyp(), ENDSTELLEN_TYP_B);
    }

    /**
     * Ueberprueft, ob es sich bei der Endstelle um den angegebenen Typ handelt.
     */
    public boolean isEndstelleOfType(String type) {
        if (!StringTools.isIn(type, new String[] { ENDSTELLEN_TYP_A, ENDSTELLEN_TYP_B })) {
            return false;
        }

        return StringUtils.equals(type, getEndstelleTyp());
    }

    /**
     * Ueberprueft, ob der Endstelle eine Rangierung zugeordnet ist.
     */
    public boolean hasRangierung() {
        return ((rangierId != null) || (rangierIdAdditional != null)) ? true : false;
    }

    /**
     * Gibt die der Endstelle zugeordneten Rangierungs-IDs zurueck.
     *
     * @return
     *
     */
    @SuppressWarnings({ "JpaAttributeMemberSignatureInspection", "JpaAttributeTypeInspection" })
    public Long[] getRangierIds() {
        int count = (getRangierIdAdditional() != null) ? 2 : 1;
        Long[] rangierIds = new Long[count];
        rangierIds[0] = getRangierId();
        if (count == 2) {
            rangierIds[1] = getRangierIdAdditional();
        }
        return rangierIds;
    }

    public Set<Long> getRangierIdsAsSet() {
        Set<Long> result = new HashSet<>();
        CollectionUtils.addAll(result, getRangierIds());
        return result;
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public String getTypeWithName() {
        if (StringUtils.isEmpty(name)) {
            return endstelleTyp;
        }
        String showedName = name;
        if (name.length() > 25) {
            showedName = name.substring(0, 25) + "...";
        }
        return endstelleTyp + " (" + showedName + ")";
    }

    public Long getCb2EsId() {
        return cb2EsId;
    }

    public void setCb2EsId(Long cb2EsId) {
        this.cb2EsId = cb2EsId;
    }

    /**
     * @deprecated dieses Feld nach Moeglichkeit nicht mehr fuer neue Funktionen anziehen;
     */
    public Long getAnschlussart() {
        return anschlussart;
    }

    public void setAnschlussart(Long anschlussart) {
        this.anschlussart = anschlussart;
    }

    public Endstelle withAnschlussart(Long anschlussart) {
        setAnschlussart(anschlussart);
        return this;
    }

    public String getBemerkungStawa() {
        return bemerkungStawa;
    }

    public void setBemerkungStawa(String bemerkungStawa) {
        this.bemerkungStawa = bemerkungStawa;
    }

    public String getEndstelle() {
        return endstelle;
    }

    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    public Endstelle withEndstelle(String endstelle) {
        setEndstelle(endstelle);
        return this;
    }

    public Long getEndstelleGruppeId() {
        return endstelleGruppeId;
    }

    public void setEndstelleGruppeId(Long endstelleGruppeId) {
        this.endstelleGruppeId = endstelleGruppeId;
    }

    public String getEndstelleTyp() {
        return endstelleTyp;
    }

    public void setEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
    }

    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public Endstelle withHvtIdStandort(Long hvtIdStandort) {
        setHvtIdStandort(hvtIdStandort);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public Endstelle withOrt(String ort) {
        setOrt(ort);
        return this;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public Endstelle withPlz(String plz) {
        setPlz(plz);
        return this;
    }

    /**
     * Gibt die ID der zugeordneten Rangierung zurueck. <br> (Bei Kombi-Produkten - z.B. 'ADSL S0' - ist diese
     * Rangierung die Hauptrangierung. Auf dieser ist dann auch die TAL bestellt.)
     *
     * @return Returns the rangierId.
     */
    public Long getRangierId() {
        return rangierId;
    }

    /**
     * <b>Hinweis:</b> Sollte die Rangierung der Endstelle sich aendern, ist es sinnvoll nach der Endstelle die
     * zuegehoerigen Endgeraete Konfiguration zu aktualisieren. Siehe hierzu auch
     * {@link EndgeraeteService#updateSchicht2Protokoll4Endstelle(Endstelle)} und HUR-23209.
     */
    public void setRangierId(Long rangierId) {
        this.rangierId = rangierId;
    }

    /**
     * Gibt die ID der Zusatz-Rangierung an. <br> Bei der Zusatz-Rangierung handelt es sich bei xDSL-Produkten um die
     * Rangierung des Phone-Anteils.
     *
     * @return Returns the rangierIdAdditional.
     */
    public Long getRangierIdAdditional() {
        return this.rangierIdAdditional;
    }

    public void setRangierIdAdditional(Long rangierIdAdditional) {
        this.rangierIdAdditional = rangierIdAdditional;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public Endstelle withGeoId(Long geoId) {
        setGeoId(geoId);
        return this;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + Endstelle.class.getName());
            logger.debug("  ID             :" + getId());
            logger.debug("  CB2ES-ID       :" + getCb2EsId());
            logger.debug("  Typ            :" + getEndstelleTyp());
            logger.debug("  Endstelle      :" + getEndstelle());
            logger.debug("  Rangier-ID     :" + getRangierId());
            logger.debug("  Rangier-ID add.:" + getRangierIdAdditional());
            logger.debug("  HVT-ID         :" + getHvtIdStandort());
            logger.debug("  Version        :" + getVersion());
        }
    }

}
