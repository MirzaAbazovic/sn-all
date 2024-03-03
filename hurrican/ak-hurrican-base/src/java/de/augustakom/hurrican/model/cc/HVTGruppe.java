/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2004 08:23:35
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;


/**
 * Modell, um eine HVT-Gruppe abzubilden. <br> Eine HVT-Gruppe kann mehrere HVT-Standorte besitzen, wobei immer nur ein
 * HVT-Standort aktiv sein kann. <br> In der HVT-Gruppe werden die Ortsangaben sowie die vorgesehenen Schaltungstage
 * definiert.
 *
 *
 */
public class HVTGruppe extends StandortAdresse implements DebugModel, Comparable<HVTGruppe> {

    /**
     * Adresse fuer AUG01.
     */
    public static final String ADRESSE_AUG01 = "Hoher Weg, Augsburg";
    /**
     * Adresse fuer AUG02.
     */
    public static final String ADRESSE_AUG02 = "Lechhauser Str. 22, Augsburg";

    private String onkz = null;
    private String ortsteil = null;
    public static final String ORTSTEIL = "ortsteil";
    private String ortZusatz = null;
    private HWSwitch hwSwitch = null;
    private Long niederlassungId = null;
    private String kostenstelle = null;
    private String innenauftrag = null;
    private String telefon = null;
    private Boolean montag = null;
    private Boolean dienstag = null;
    private Boolean mittwoch = null;
    private Boolean donnerstag = null;
    private Boolean freitag = null;
    private Boolean export4Portal = null;

    /**
     * Ueberprueft, ob der Wochentag von <code>realDate</code> mit den vorgesehenen Schaltungstagen der HVT-Gruppe
     * uebereinstimmt.
     *
     * @param realDate Realisierungsdatum
     * @return true wenn der Wochentag mit den vorgesehenen Tagen uebereinstimmt.
     */
    public boolean isRealDatePossible(Date realDate) {
        if (realDate == null) { return false; }

        int dayOfWeek = DateTools.getDayOfWeek(realDate);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                if (BooleanTools.nullToFalse(getMontag())) {
                    return true;
                }
                break;
            case Calendar.TUESDAY:
                if (BooleanTools.nullToFalse(getDienstag())) {
                    return true;
                }
                break;
            case Calendar.WEDNESDAY:
                if (BooleanTools.nullToFalse(getMittwoch())) {
                    return true;
                }
                break;
            case Calendar.THURSDAY:
                if (BooleanTools.nullToFalse(getDonnerstag())) {
                    return true;
                }
                break;
            case Calendar.FRIDAY:
                if (BooleanTools.nullToFalse(getFreitag())) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    /**
     * Erstellt einen String mit den vorgesehenen Schaltungstagen des HVTs. <br> Die Tage sind durch ein Komma (,)
     * getrennt.
     *
     * @return
     */
    public String getSchaltungstage() {
        StringBuilder sb = new StringBuilder();
        if (BooleanTools.nullToFalse(getMontag())) {
            sb.append("Montag");
        }
        if (BooleanTools.nullToFalse(getDienstag())) {
            if (sb.length() > 0) { sb.append(", "); }
            sb.append("Dienstag");
        }
        if (BooleanTools.nullToFalse(getMittwoch())) {
            if (sb.length() > 0) { sb.append(", "); }
            sb.append("Mittwoch");
        }
        if (BooleanTools.nullToFalse(getDonnerstag())) {
            if (sb.length() > 0) { sb.append(", "); }
            sb.append("Donnerstag");
        }
        if (BooleanTools.nullToFalse(getFreitag())) {
            if (sb.length() > 0) { sb.append(", "); }
            sb.append("Freitag");
        }

        return sb.toString();
    }

    public String getOnkzWithoutLeadingNulls() {
        return StringTools.removeStartToEmpty(onkz, '0');
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getOrtZusatz() {
        return ortZusatz;
    }

    public void setOrtZusatz(String ortZusatz) {
        this.ortZusatz = ortZusatz;
    }

    public String getOrtsteil() {
        return ortsteil;
    }

    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungID) {
        this.niederlassungId = niederlassungID;
    }

    public Boolean getMontag() {
        return montag;
    }

    public void setMontag(Boolean montag) {
        this.montag = montag;
    }

    public Boolean getDienstag() {
        return dienstag;
    }

    public void setDienstag(Boolean dienstag) {
        this.dienstag = dienstag;
    }

    public Boolean getMittwoch() {
        return mittwoch;
    }

    public void setMittwoch(Boolean mittwoch) {
        this.mittwoch = mittwoch;
    }

    public Boolean getDonnerstag() {
        return donnerstag;
    }

    public void setDonnerstag(Boolean donnerstag) {
        this.donnerstag = donnerstag;
    }

    public Boolean getFreitag() {
        return freitag;
    }

    public void setFreitag(Boolean freitag) {
        this.freitag = freitag;
    }

    public String getInnenauftrag() {
        return innenauftrag;
    }

    public void setInnenauftrag(String innenauftrag) {
        this.innenauftrag = innenauftrag;
    }

    public String getKostenstelle() {
        return kostenstelle;
    }

    public void setKostenstelle(String kostenstelle) {
        this.kostenstelle = kostenstelle;
    }

    public Boolean getExport4Portal() {
        return export4Portal;
    }

    public void setExport4Portal(Boolean export4Portal) {
        this.export4Portal = export4Portal;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + HVTGruppe.class.getName());
            logger.debug("  ID          : " + getId());
            logger.debug("  ONKZ        : " + getOnkz());
            logger.debug("  Strasse/Ort : " + getStrasse() + " " + getOrt());
            logger.debug("  Switch      : " + getHwSwitch());
            logger.debug("  Tage        : " + getMontag() + "-" + getDienstag() + "-" + getMittwoch() + "-" + getDonnerstag() + "-" + getFreitag());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HVTGruppe))
            return false;
        if (!super.equals(o))
            return false;

        HVTGruppe hvtGruppe = (HVTGruppe) o;

        if (onkz != null ? !onkz.equals(hvtGruppe.onkz) : hvtGruppe.onkz != null)
            return false;
        if (ortsteil != null ? !ortsteil.equals(hvtGruppe.ortsteil) : hvtGruppe.ortsteil != null)
            return false;
        if (ortZusatz != null ? !ortZusatz.equals(hvtGruppe.ortZusatz) : hvtGruppe.ortZusatz != null)
            return false;
        if (hwSwitch != null ? !hwSwitch.equals(hvtGruppe.hwSwitch) : hvtGruppe.hwSwitch != null)
            return false;
        if (niederlassungId != null ? !niederlassungId.equals(hvtGruppe.niederlassungId) : hvtGruppe.niederlassungId != null)
            return false;
        if (kostenstelle != null ? !kostenstelle.equals(hvtGruppe.kostenstelle) : hvtGruppe.kostenstelle != null)
            return false;
        if (innenauftrag != null ? !innenauftrag.equals(hvtGruppe.innenauftrag) : hvtGruppe.innenauftrag != null)
            return false;
        if (telefon != null ? !telefon.equals(hvtGruppe.telefon) : hvtGruppe.telefon != null)
            return false;
        if (montag != null ? !montag.equals(hvtGruppe.montag) : hvtGruppe.montag != null)
            return false;
        if (dienstag != null ? !dienstag.equals(hvtGruppe.dienstag) : hvtGruppe.dienstag != null)
            return false;
        if (mittwoch != null ? !mittwoch.equals(hvtGruppe.mittwoch) : hvtGruppe.mittwoch != null)
            return false;
        if (donnerstag != null ? !donnerstag.equals(hvtGruppe.donnerstag) : hvtGruppe.donnerstag != null)
            return false;
        if (freitag != null ? !freitag.equals(hvtGruppe.freitag) : hvtGruppe.freitag != null)
            return false;
        return !(export4Portal != null ? !export4Portal.equals(hvtGruppe.export4Portal) : hvtGruppe.export4Portal != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (onkz != null ? onkz.hashCode() : 0);
        result = 31 * result + (ortsteil != null ? ortsteil.hashCode() : 0);
        result = 31 * result + (ortZusatz != null ? ortZusatz.hashCode() : 0);
        result = 31 * result + (hwSwitch != null ? hwSwitch.hashCode() : 0);
        result = 31 * result + (niederlassungId != null ? niederlassungId.hashCode() : 0);
        result = 31 * result + (kostenstelle != null ? kostenstelle.hashCode() : 0);
        result = 31 * result + (innenauftrag != null ? innenauftrag.hashCode() : 0);
        result = 31 * result + (telefon != null ? telefon.hashCode() : 0);
        result = 31 * result + (montag != null ? montag.hashCode() : 0);
        result = 31 * result + (dienstag != null ? dienstag.hashCode() : 0);
        result = 31 * result + (mittwoch != null ? mittwoch.hashCode() : 0);
        result = 31 * result + (donnerstag != null ? donnerstag.hashCode() : 0);
        result = 31 * result + (freitag != null ? freitag.hashCode() : 0);
        result = 31 * result + (export4Portal != null ? export4Portal.hashCode() : 0);
        return result;
    }

    /**
     * HUR-23815: Alphabetische Sortierung des Drop-Down-Menüs bei der HVT-Auswahl
     */
    @Override
    public int compareTo(HVTGruppe hvtGruppe) {
        return this.ortsteil.compareToIgnoreCase(hvtGruppe.getOrtsteil());
    }

}


