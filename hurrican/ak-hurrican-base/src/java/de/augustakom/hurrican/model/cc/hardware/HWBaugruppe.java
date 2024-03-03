/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2008 15:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.IRackAware;

/**
 * Modell, um eine Hardware-Baugruppe abzubilden.
 *
 *
 */
public class HWBaugruppe extends AbstractCCIDModel implements IRackAware, Comparable {

    public static class ModulNrComparator implements Comparator<HWBaugruppe>, Serializable {
        @Override
        public int compare(HWBaugruppe o1, HWBaugruppe o2) {
            return o1.getModNumber().compareTo(o2.getModNumber());
        }
    }

    private Long rackId = null;
    public static final String RACK_ID = "rackId";

    private Long subrackId = null;
    public static final String SUBRACK_ID = "subrackId";

    private HWBaugruppenTyp hwBaugruppenTyp = null;
    public static final String HW_BAUGRUPPEN_TYP = "hwBaugruppenTyp";

    private String inventarNr = null;
    public static final String INVENTAR_NR = "inventarNr";

    private String modNumber = null;
    public static final String MOD_NUMBER = "modNumber";

    private Boolean eingebaut = null;
    public static final String EINGEBAUT = "eingebaut";

    private String bemerkung = null;
    public static final String BEMERKUNG = "bemerkung";

    private String modName = null;
    public static final String MOD_NAME = "modName";

    public static final Pattern PATTERN_MODNUMBER = Pattern.compile("(R)([0-9]+)(/S)([0-9]+)(-LT)([0-9]+)");

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    @CheckForNull
    public Integer getModNumberRackPart() {
        return getModNumberPartByMatchingGroup(2);
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    @CheckForNull
    public Integer getModNumberSubrackPart() {
        return getModNumberPartByMatchingGroup(4);
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    @CheckForNull
    public Integer getModNumberLineKartePart() {
        return getModNumberPartByMatchingGroup(6);
    }

    private Integer getModNumberPartByMatchingGroup(int groupNr) {
        if (getModNumber() != null) {
            Matcher modNumberPatternMatcher = PATTERN_MODNUMBER.matcher(getModNumber());
            if (modNumberPatternMatcher.find()) {
                return Integer.parseInt(modNumberPatternMatcher.group(groupNr));
            }
        }
        return null;
    }

    public Boolean getEingebaut() {
        return eingebaut;
    }

    public void setEingebaut(Boolean eingebaut) {
        this.eingebaut = eingebaut;
    }

    @SuppressWarnings("JpaAttributeTypeInspection")
    public HWBaugruppenTyp getHwBaugruppenTyp() {
        return hwBaugruppenTyp;
    }

    public void setHwBaugruppenTyp(HWBaugruppenTyp hwBaugruppenTyp) {
        this.hwBaugruppenTyp = hwBaugruppenTyp;
    }

    public String getInventarNr() {
        return inventarNr;
    }

    public void setInventarNr(String inventarNr) {
        this.inventarNr = inventarNr;
    }

    public String getModNumber() {
        return modNumber;
    }

    public void setModNumber(String modNumber) {
        this.modNumber = modNumber;
    }

    public Long getRackId() {
        return rackId;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }

    public Long getSubrackId() {
        return subrackId;
    }

    public void setSubrackId(Long subrackId) {
        this.subrackId = subrackId;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    @Override
    /**
     * Siehe Sonar squid:S1210 -> 'Override "equals(Object obj)" to comply with the contract of the "compareTo(T o)"
     * method.'
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        HWBaugruppe model = (HWBaugruppe)obj;
        if (getId() != null) {
            if (!getId().equals(model.getId())) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (rackId != null ? rackId.hashCode() : 0);
        result = 31 * result + (subrackId != null ? subrackId.hashCode() : 0);
        result = 31 * result + (hwBaugruppenTyp != null ? hwBaugruppenTyp.hashCode() : 0);
        result = 31 * result + (inventarNr != null ? inventarNr.hashCode() : 0);
        result = 31 * result + (modNumber != null ? modNumber.hashCode() : 0);
        result = 31 * result + (eingebaut != null ? eingebaut.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (modName != null ? modName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        if (equals(o)) {
            return 0;
        }
        HWBaugruppe model = (HWBaugruppe)o;
        if (getId() != null && model.getId() != null) {
            return (getId() < model.getId())? -1: 1;
        }
        else if (getId() != null) {
            return -1;
        }
        else if (model.getId() != null) {
            return 1;
        }
        return (hashCode() < model.hashCode())? -1 : 1;
    }
}
