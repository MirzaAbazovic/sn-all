/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 11:40:07
 */
package de.mnet.wita.message.common;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.OnkzValid;
import de.mnet.wita.validators.groups.Workflow;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_LEITUNGS_BEZEICHNUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_LEITUNGS_BEZEICHNUNG_0", allocationSize = 1)
public class LeitungsBezeichnung extends MwfEntity {

    private static final long serialVersionUID = 6309065167862915575L;

    public static final String LEITUNGS_SCHLUESSEL_ZAHL_PROPERTY_PATH = "geschaeftsfall.auftragsPosition.geschaeftsfallProdukt.leitungsBezeichnung.leitungsSchluesselZahl";

    private String leitungsSchluesselZahl;
    /**
     * @deprecated - removed in WITA v5
     */
    private String leitungsSchluesselZahlErgaenzung;
    private String onkzKunde;
    private String onkzKollokation;
    private String ordnungsNummer;

    public LeitungsBezeichnung() {
        // required by Hibernate
    }

    /**
     * Erstellt eine LeitungsBezeichung aus einem String, der folgende Konvention einhält:
     * {leitungsSchluesselZahl}/{onkzKunde}/{onkzKollokation}/{ordnungsNummer}
     */
    public LeitungsBezeichnung(String lbz, String standortOnkz) {
        leitungsSchluesselZahl = leitungsSchluesselZahl(lbz);
        onkzKunde = getCorrectedONKZ(getOnkzKundeFromLbz(lbz), standortOnkz);
        onkzKollokation = getCorrectedONKZ(getOnkzKollokationFromLbz(lbz), standortOnkz);
        ordnungsNummer = getOrdnungsnummerFromLbz(lbz);
    }

    private static String leitungsSchluesselZahl(String lbz) {
        return getLbzPart(0, false, lbz);
    }

    private static String getOnkzKundeFromLbz(String lbz) {
        return getLbzPart(1, true, lbz);
    }

    private static String getOnkzKollokationFromLbz(String lbz) {
        return getLbzPart(2, true, lbz);
    }

    private static String getOrdnungsnummerFromLbz(String lbz) {
        String ordnungnummer = getLbzPart(3, false, lbz);
        if (ordnungnummer != null) { return Strings.padStart(ordnungnummer, 10, '0'); }
        return null;
    }

    private static String getLbzPart(int part, boolean stripLeadingAndTrailingZeros, String lbz) {
        if (StringUtils.isNotBlank(lbz)) {
            String[] splitted = StringUtils.split(lbz, "/");
            if (splitted != null && splitted.length > part) {
                String trimmed = splitted[part].trim();
                if (stripLeadingAndTrailingZeros) {
                    while (trimmed.startsWith("0")) {
                        trimmed = trimmed.substring(1);
                    }
                    while (trimmed.endsWith("0")) {
                        trimmed = trimmed.substring(0, trimmed.length() - 1);
                    }
                }
                return trimmed;
            }
        }
        return null;
    }

    /*
     * Checkt die Laenge der ONKZ von der Leitungsbezeichnung gegen die ONKZ des HVT-Standorts
     * Sollten eine oder mehrere Nullen fehlen, wird die ONKZ von HVT Standort verwendet,
     * jedoch nur bis die ONKZ von der LBZ bis auf die nachfolgenden Nullen mit dem HVT-Standort-Onkz uebereinstimmt.
     * (Dies ist notwendig, wenn die ONKZ eines Standorts mit 0 endet, z.B. 08230 --> in WITA 8230; 089 --> 89)
     */
    private static String getCorrectedONKZ(String lbzOnkz, String hvtOnkz) {
        String correctedOnkz = lbzOnkz;
        if (StringUtils.isNotBlank(lbzOnkz) && StringUtils.isNotBlank(hvtOnkz)
                && lbzOnkz.length() < hvtOnkz.length()
                && hvtOnkz.substring(0, lbzOnkz.length()).equals(lbzOnkz)) {
            correctedOnkz = hvtOnkz;
        }
        return correctedOnkz;
    }


    public LeitungsBezeichnung(String leitungsSchluesselZahl, String onkzKunde, String onkzKollokation, String ordnungsNummer) {
        super();
        this.leitungsSchluesselZahl = leitungsSchluesselZahl;
        this.onkzKunde = onkzKunde;
        this.onkzKollokation = onkzKollokation;
        this.ordnungsNummer = ordnungsNummer;
    }

    @Override
    public String toString() {
        return getLeitungsbezeichnungString();
    }

    @Transient
    public String getLeitungsbezeichnungString() {
        return StringUtils.join(new String[] { leitungsSchluesselZahl, onkzKunde, onkzKollokation, ordnungsNummer }, '/');
    }

    @NotNull(groups = Workflow.class, message = "Leitungsschluesselzahl muss gesetzt sein.")
    @Length(groups = Workflow.class, min = 1, max = 4, message = "Leitungsschluesselzahl muss 1-4 Zeichen lang sein.")
    @Column(name = "LEITUNGS_SCHLUESSEL_ZAHL")
    public String getLeitungsSchluesselZahl() {
        return leitungsSchluesselZahl;
    }

    public void setLeitungsSchluesselZahl(String leitungsSchluesselZahl) {
        this.leitungsSchluesselZahl = leitungsSchluesselZahl;
    }

    @NotNull(groups = Workflow.class, message = "Ortskennzahl des Kundenstandorts muss gesetzt sein.")
    @OnkzValid(groups = Workflow.class)
    @Column(name = "ONKZ_KUNDE")
    public String getOnkzKunde() {
        return onkzKunde;
    }

    public void setOnkzKunde(String onkzKunde) {
        this.onkzKunde = onkzKunde;
    }

    @NotNull(groups = Workflow.class, message = "Ortskennzahl des Kollokationsstandorts muss gesetzt sein.")
    @OnkzValid(groups = Workflow.class)
    @Column(name = "ONKZ_KOLLOKATION")
    public String getOnkzKollokation() {
        return onkzKollokation;
    }

    public void setOnkzKollokation(String onkzKollokation) {
        this.onkzKollokation = onkzKollokation;
    }

    @NotNull(groups = Workflow.class, message = "Ordnungsnummer muss gesetzt sein.")
    @Pattern(groups = Workflow.class, regexp = "[0-9]{10}", message = "Ordnungsnummer muss aus exakt 10 Ziffern bestehen.")
    @Column(name = "ORDNUNGSNUMMER")
    public String getOrdnungsNummer() {
        return ordnungsNummer;
    }

    public void setOrdnungsNummer(String ordnungsNummer) {
        this.ordnungsNummer = ordnungsNummer;
    }
}
