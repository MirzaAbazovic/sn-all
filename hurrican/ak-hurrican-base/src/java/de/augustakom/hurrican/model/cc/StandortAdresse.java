/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.07.2012 08:18:11
 */
package de.augustakom.hurrican.model.cc;

import java.util.regex.*;
import java.util.regex.Pattern;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;

/**
 * Eine Adresse eines Standortes (aktuell HvtGruppe und KvzAdresse).
 *
 *
 */
@MappedSuperclass
public abstract class StandortAdresse extends AbstractCCIDModel {
    private String strasse;
    private String hausNr;
    private String plz;
    private String ort;

    /**
     * Teilt ein von der DTAG erhaltenes Feld mit Strasse und Hausnummer auf in Strasse und HausNr.
     *
     * @param strasseHausNrDtag
     * @return Pair mit Strasse (first) und Hausnummer (second)
     */
    public static Pair<String, String> splitDTAGStrasseHausNr(final String strasseHausNrDtag) {
        String hausNr;
        String street;
        final Pattern pattern1 = Pattern.compile("(\\D+\\d*.*\\/\\D+)(\\d*.*)");
        final Matcher matcher1 = pattern1.matcher(strasseHausNrDtag);
        if (matcher1.matches()) {
            final Matcher strangeHouseNrMatcher = Pattern.compile(
                    "([a-zA-Z]+\\D*\\S{0,1}\\d{0,3}\\s+){1,2}(\\d+.{0,7}(\\d+.{0,6}){0,1})")
                    .matcher(strasseHausNrDtag);
            if (strangeHouseNrMatcher.matches()) {
                street = strangeHouseNrMatcher.group(1).trim();
                hausNr = strangeHouseNrMatcher.group(2).trim();
            }
            else {
                street = matcher1.group(1).trim();
                hausNr = matcher1.group(2).trim();
            }
        }
        else {
            final Matcher matcher2 = Pattern.compile("(\\D+)(\\d*.*)").matcher(strasseHausNrDtag);
            if (!matcher2.matches()) {
                throw new IllegalStateException(String.format(
                        "Strasse_HausNr Format kann nicht geparst werden. Wert: %s",
                        strasseHausNrDtag));
            }
            hausNr = matcher2.group(2);
            street = matcher2.group(1).trim();
        }

        return Pair.create(street, (StringUtils.isEmpty(hausNr) ? null : hausNr.trim()));
    }

    @Column(name = "STRASSE", length = 50, nullable = false)
    @NotNull
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    @Column(name = "HAUS_NR", length = 10)
    public String getHausNr() {
        return hausNr;
    }

    public void setHausNr(String hausNr) {
        this.hausNr = hausNr;
    }

    /**
     * Trennt die Hausnummer in 'HausNr' und 'HausNr-Zusatz' auf, sofern ein Zusatz erkannt wird.
     *
     * @return
     */
    @Transient
    public String[] getHausNrSplitted() {
        if (getHausNr() != null) {
            StringBuilder houseNum = new StringBuilder("");
            String houseNumAdd = null;

            for (int i = 0; i < getHausNr().length(); i++) {
                String character = String.valueOf(getHausNr().charAt(i));
                if (StringUtils.isNumeric(character)) {
                    houseNum.append(character);
                }
                else {
                    houseNumAdd = getHausNr().substring(i);
                    houseNumAdd = StringUtils.trimToNull(houseNumAdd);
                    break;
                }
            }

            return new String[] { houseNum.toString(), houseNumAdd };
        }
        return null;
    }

    @Column(name = "PLZ", length = 10, nullable = false)
    @NotNull
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    @Column(name = "ORT", length = 50, nullable = false)
    @NotNull
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Transient
    public String getStreetAndHouseNum() {
        return StringTools.join(new String[] { getStrasse(), getHausNr() }, " ", true);
    }

    /**
     * Vergleicht alle Adressfelder.
     *
     * @param other
     * @return
     */
    public boolean equalsAdresse(StandortAdresse other) {
        if (other == null) {
            return false;
        }
        return new EqualsBuilder()
                .append(strasse, other.strasse)
                .append(hausNr, other.hausNr)
                .append(plz, other.plz)
                .append(ort, other.ort)
                .isEquals();
    }

    /**
     * Kopiert alle Adressfelder von dem gegebenen StandortKollokation.
     *
     * @param other Kopier Quelle
     */
    public void copyAdresse(StandortAdresse other) {
        if (other == null) {
            return;
        }
        strasse = other.strasse;
        hausNr = other.hausNr;
        plz = other.plz;
        ort = other.ort;
    }

    public String toStringAdresse() {
        return "[strasse/hausNr=" + strasse + " " + hausNr + ", plz/ort=" + plz + " " + ort + "]";
    }

}

