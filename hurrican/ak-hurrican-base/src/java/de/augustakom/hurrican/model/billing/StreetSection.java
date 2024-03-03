/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2008 10:24:57
 */
package de.augustakom.hurrican.model.billing;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;


/**
 * Modell-Klasse zur Abbildung einer Strassen-Sektion.
 *
 *
 */
public class StreetSection extends AbstractBillingModel {

    /**
     * Konstante fuer 'numberingKind', die 'allen Nummern' entspricht
     */
    public static final String NUMBERING_KIND_ALL = "A";
    /**
     * Konstante fuer 'numberingKind', die 'ungeraden Nummern' entspricht
     */
    public static final String NUMBERING_KIND_ODD = "U";
    /**
     * Konstante fuer 'numberingKind', die 'geraden Nummern' entspricht
     */
    public static final String NUMBERING_KIND_EVEN = "G";

    private Long sectionNo = null;
    private Long geoStreetNo = null;
    private Long houseNumStart = null;
    private String houseNumAddStart = null;
    private Long houseNumEnd = null;
    private String houseNumAddEnd = null;
    private String numberingKind = null;
    private String onkz = null;
    private Long asb = null;
    private String extId = null;
    private String recSource = null;
    private Long serviceRoomNo = null;

    /**
     * @return
     *
     * @see getSectionAsString(Long, String, Long, String, String)
     */
    public String getSectionAsString() {
        return getSectionAsString(getHouseNumStart(), getHouseNumAddStart(),
                getHouseNumEnd(), getHouseNumAddEnd(), getNumberingKind());
    }

    /**
     * Gibt eine Kombination der Werte 'houseNum(Add)Start' und 'houseNum(Add)End' sowie 'numberingKind' zurueck.
     *
     * @return zusammengesetzter String aus den o.g. Parametern. NULL Werte werden dabei ignoriert.
     *
     */
    public static String getSectionAsString(Long houseNumStart, String houseNumAddStart,
            Long houseNumEnd, String houseNumAddEnd, String numberingKind) {
        String startNum = (houseNumStart != null) ? "" + houseNumStart : null;
        String endNum = (houseNumEnd != null) ? "" + houseNumEnd : null;

        String start = StringTools.join(new String[] { startNum, houseNumAddStart }, "", true);
        String end = StringTools.join(new String[] { endNum, houseNumAddEnd }, "", true);
        String section = StringTools.join(new String[] { start, end }, "-", true);

        if (StringUtils.isNotBlank(section)) {
            return StringTools.join(new String[] { section, numberingKind }, " ", true);
        }

        return section;
    }

    /**
     * @return Returns the sectionNo.
     */
    public Long getSectionNo() {
        return sectionNo;
    }

    /**
     * @param sectionNo The sectionNo to set.
     */
    public void setSectionNo(Long sectionNo) {
        this.sectionNo = sectionNo;
    }

    /**
     * @return Returns the geoStreetNo.
     */
    public Long getGeoStreetNo() {
        return geoStreetNo;
    }

    /**
     * @param geoStreetNo The geoStreetNo to set.
     */
    public void setGeoStreetNo(Long geoStreetNo) {
        this.geoStreetNo = geoStreetNo;
    }

    /**
     * @return Returns the houseNumStart.
     */
    public Long getHouseNumStart() {
        return houseNumStart;
    }

    /**
     * @param houseNumStart The houseNumStart to set.
     */
    public void setHouseNumStart(Long houseNumStart) {
        this.houseNumStart = houseNumStart;
    }

    /**
     * @return Returns the houseNumAddStart.
     */
    public String getHouseNumAddStart() {
        return houseNumAddStart;
    }

    /**
     * @param houseNumAddStart The houseNumAddStart to set.
     */
    public void setHouseNumAddStart(String houseNumAddStart) {
        this.houseNumAddStart = houseNumAddStart;
    }

    /**
     * @return Returns the houseNumEnd.
     */
    public Long getHouseNumEnd() {
        return houseNumEnd;
    }

    /**
     * @param houseNumEnd The houseNumEnd to set.
     */
    public void setHouseNumEnd(Long houseNumEnd) {
        this.houseNumEnd = houseNumEnd;
    }

    /**
     * @return Returns the houseNumAddEnd.
     */
    public String getHouseNumAddEnd() {
        return houseNumAddEnd;
    }

    /**
     * @param houseNumAddEnd The houseNumAddEnd to set.
     */
    public void setHouseNumAddEnd(String houseNumAddEnd) {
        this.houseNumAddEnd = houseNumAddEnd;
    }

    /**
     * @return Returns the onkz.
     */
    public String getOnkz() {
        return onkz;
    }

    /**
     * @param onkz The onkz to set.
     */
    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    /**
     * @return Returns the asb.
     */
    public Long getAsb() {
        return asb;
    }

    /**
     * @param asb The asb to set.
     */
    public void setAsb(Long asb) {
        this.asb = asb;
    }

    /**
     * @return Returns the numberingKind.
     */
    public String getNumberingKind() {
        return numberingKind;
    }

    /**
     * @param numberingKind The numberingKind to set.
     */
    public void setNumberingKind(String numberingKind) {
        this.numberingKind = numberingKind;
    }

    /**
     * @return extId
     */
    public String getExtId() {
        return extId;
    }

    /**
     * @param extId Festzulegender extId
     */
    public void setExtId(String extId) {
        this.extId = extId;
    }

    /**
     * @return recSource
     */
    public String getRecSource() {
        return recSource;
    }

    /**
     * @param recSource Festzulegender recSource
     */
    public void setRecSource(String recSource) {
        this.recSource = recSource;
    }

    /**
     * @return the serviceRoomNo
     */
    public Long getServiceRoomNo() {
        return serviceRoomNo;
    }

    /**
     * @param serviceRoomNo the serviceRoomNo to set
     */
    public void setServiceRoomNo(Long serviceRoomNo) {
        this.serviceRoomNo = serviceRoomNo;
    }

}


