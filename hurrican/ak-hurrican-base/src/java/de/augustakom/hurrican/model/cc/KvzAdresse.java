/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2012 13:35:14
 */
package de.augustakom.hurrican.model.cc;

import java.util.regex.*;
import java.util.regex.Pattern;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;

/**
 * Die Adresse eines (DTAG) KVZ Standortes. Eindeutig definiert durch den Standort und die KVZ Nummer.
 */
@Entity
@Table(name = "T_KVZ_ADRESSE")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_KVZ_ADRESSE_0", allocationSize = 1)
public class KvzAdresse extends StandortAdresse {
    /**
     * optional ein führender Buchstabe, gefolgt von 0-4 Zahlen. Gesamtlänge jedoch max 4 Zeichen.
     */
    public static final String KVZ_NUMMER_PATTERN_1 = "(?=^.{1,4}$)([A-Z]{0,1})([0-9]{0,4})";

    /**
     * 4-stellige Zahl gefolgt von einem Buchstaben.
     */
    public static final String KVZ_NUMMER_PATTERN_2 = "([0-9]{1,4})([A-Z])";

    private Long hvtStandortId;
    private String kvzNummer;

    @Column(name = "HVT_ID_STANDORT", nullable = false)
    @NotNull
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    @Column(name = "KVZ_NUMMER", length = 5, nullable = false)
    @NotNull
    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    /**
     * Formatiert eine KVZ-Nummer im MNET-Format, d.h. 4-stellig (Zahl mit führenden Nullen); alternativ ist auch nur
     * ein Buchstabe (z.B. 'A' oder 'B') als KVZ-Nummer erlaubt.
     *
     * @param dtagKvzNr
     * @return
     * @throws IllegalArgumentException wenn die KVZ-Nummer nicht geparst werden kann
     */
    public static String formatKvzNr(String dtagKvzNr) {
        return formatKvzNr(dtagKvzNr, true);
    }

    /**
     * Formatiert eine KVZ-Nummer in das von der WITA notwendige Format. <br> Beispiele: <br> <ul> <li>A001 --> A1
     * <li>A010 --> A10 <li>A100 --> A100 <li>B --> B </ul>
     *
     * @param dtagKvzNr
     * @return
     */
    public static String formatKvzNrForWita(String dtagKvzNr) {
        return formatKvzNr(dtagKvzNr, false);
    }

    private static String formatKvzNr(String dtagKvzNr, boolean forMnet) {
        if (dtagKvzNr == null) {
            throw new IllegalArgumentException("keine KVZ Nummer übergeben (null)");
        }
        Matcher m1 = Pattern.compile(KVZ_NUMMER_PATTERN_1).matcher(dtagKvzNr.trim());
        Matcher m2 = Pattern.compile(KVZ_NUMMER_PATTERN_2).matcher(dtagKvzNr.trim());
        if (m1.matches()) {
            String prefix = m1.group(1);
            String nrStr = m1.group(2);
            if (StringUtils.isBlank(nrStr)) {
                return prefix;
            }
            if (forMnet) {
                // KVZ-Nr in M-net Format ausgeben (prefix + Zahl 3-stellig mit fuehrenden 0)
                Integer nr = Integer.valueOf(nrStr);
                int nrLen = 3;
                if (StringUtils.isBlank(prefix)) {
                    nrLen++;
                }
                return String.format("%s%0" + nrLen + "d", prefix, nr);
            }
            else {
                // KVZ-Nr in WITA Format ausgeben (keine fuehrenden 0)
                Integer nr = Integer.valueOf(nrStr);
                return String.format("%s%s", prefix, nr);
            }
        }
        else if (m2.matches()) {
            String nrStr = m2.group(1);
            String postfix = m2.group(2);
            if (forMnet) {
                // fuehrende Nullen - gesamtlaenge 4 Ziffern + postfix (ein Buchstabe)
                return String.format("%s%s", StringUtils.leftPad(nrStr, 4, '0'), postfix);
            }
            else {
                // KVZ-Nr in WITA Format ausgeben (keine fuehrenden 0)
                return String.format("%s%s", Integer.valueOf(nrStr), postfix);
            }
        }
        else {
            throw new IllegalArgumentException("KVZ Nummer Format nicht unterstützt. KvzNr=" + dtagKvzNr);
        }
    }
}
