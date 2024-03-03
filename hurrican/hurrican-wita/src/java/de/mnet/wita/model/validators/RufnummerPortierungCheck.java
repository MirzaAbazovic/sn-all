/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2011 11:10:53
 */
package de.mnet.wita.model.validators;

import static com.google.common.collect.Lists.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;

/**
 * Klasse zum Vergleichen von RufnummerPortierungen.
 * <p>
 * Das Ergebnis des Vergleichs kann durch die Methoden @code{hasWarnings() und @code{generateWarningsText()} abgefragt
 * werden.
 */
public class RufnummerPortierungCheck implements Serializable {

    private static final long serialVersionUID = -8843426399679609901L;

    public static final boolean IS_AUFNEHMEND = true;
    public static final boolean IS_ABGEBEND = false;

    private static final DateTimeFormatter EXPECTED_VORGABE_DATE_FORMATTER = DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR);
    /**
     * Erwartetes Portierungsdatum (= Vorgabedatum der ABM oder der AKM-PV)
     */
    public LocalDate expectedVorgabeDate;

    /**
     * Rufnummern, bei denen das Portierungsdatum in Taifun nicht mit dem zurueckgemeldeten Portierungsdatum
     * (verbindlicher Liefertermin) uebereinstimmt.
     */
    public List<Rufnummer> falschesPortierungsdatum = newArrayList();
    /**
     * Flag, das gesetzt wird, falls die Rufnummern nicht aus Taifun geladen werden konnten.
     */
    public boolean fehlerBeimLadenDerRufnummern = false;
    /**
     * Flag, das gesetzt wird, falls des Hurrican-Auftrag nicht geladen werden kann oder keine auftragNoOrig hat.
     */
    public boolean fehlerBeimLadenDesAuftrags = false;
    /**
     * Flag, das gesetzt wird, falls keine RufnummerPortierung aus den zu portierenden Rufnummern erstellt werden
     * konnte.
     */
    public boolean fehlerBeimLadenDerTaifunPortierung = false;
    /**
     * Flag, das gesetzt wird, falls es zu einem AKM-PV-Usertask mehrere moegliche Auftraege gibt.
     */
    public boolean auftragIdNichtEindeutig = false;
    /**
     * Flag, das gesetzt wird, falls bestelltePortierung != zurueckgemeldetePortierung bei aufnehmenden
     * Rufnummerportierungen.
     */
    public boolean bestelltePortierungUngleichZurueckgemeldetePortierung = false;
    /**
     * Flag, das gesetzt wird, falls bestelltePortierung != zu portierende Rufnummern in Taifun bei abgebenden
     * Rufnummerportierungen.
     */
    public boolean taifunPortierungUngleichAkmPvPortierung = false;

    /**
     * Die von M-net erwartete RufnummerPortierung.
     */
    public RufnummernPortierung portierungMnet;
    /**
     * Die von einem anderen Carrier zurueckgemeldete oder bestellte RufnummerPortierung.
     */
    public RufnummernPortierung portierungAndererCarrier;

    public boolean hasWarnings() {
        // @formatter:off
        return CollectionTools.isNotEmpty(falschesPortierungsdatum)
                || fehlerBeimLadenDerRufnummern
                || fehlerBeimLadenDesAuftrags
                || fehlerBeimLadenDerTaifunPortierung
                || auftragIdNichtEindeutig
                || bestelltePortierungUngleichZurueckgemeldetePortierung
                || taifunPortierungUngleichAkmPvPortierung;
        // @formatter:on
    }

    private boolean isAufnehmend = false;

    public RufnummerPortierungCheck(boolean isAufnehmend) {
        this.isAufnehmend = isAufnehmend;
    }

    public String generateWarningsText() {
        StringBuilder sb = new StringBuilder();

        if (CollectionTools.isNotEmpty(falschesPortierungsdatum)) {
            sb.append("Folgende Rufnummern haben ein falsches Portierungsdatum in Taifun (erwartet wurde: ")
                    .append(EXPECTED_VORGABE_DATE_FORMATTER.format(expectedVorgabeDate)).append("):\n");
            for (Rufnummer rn : falschesPortierungsdatum) {
                sb.append(rn.toString()).append("\n");
            }
            sb.append("\n");
        }

        if (fehlerBeimLadenDerRufnummern) {
            sb.append("Es konnten nicht alle zu portierenden Rufnummern aus Taifun geladen werden.\n\n");
        }

        if (fehlerBeimLadenDesAuftrags) {
            sb.append("Fehler beim Laden des Hurrican-Auftrags bzw. der Hurrican-Auftrag ist keinem Taifun-Auftrag zugeordnet.\n\n");
        }

        if (fehlerBeimLadenDerTaifunPortierung) {
            sb.append("Fehler beim Erstellen einer RufnummerPortierung aus den zu portierenden Rufnummern in Taifun.\n\n");
        }

        if (auftragIdNichtEindeutig) {
            sb.append("Die Auftrag-Id ist nicht eindeutig. Konnte deshalb keine Prüfung der Rufnummerportierung durchführen.\n\n");
        }

        if (bestelltePortierungUngleichZurueckgemeldetePortierung) {
            sb.append("Es gibt Unterschiede zwischen der bestellten und der zurückgemeldeten Rufnummerportierung.\n\n");
            sb.append("Folgende Rufnummer-Portierungen wurden bestellt:\n");
            if (portierungMnet != null) {
                sb.append(portierungMnet.toString()).append("\n");
            }
            sb.append("Folgende Rufnummer-Portierungen wurden zurückgemeldet:\n");
            if (portierungAndererCarrier != null) {
                sb.append(portierungAndererCarrier.toString()).append("\n");
            }
        }

        if (taifunPortierungUngleichAkmPvPortierung) {
            sb.append("Es gibt Unterschiede zwischen den bestellten und den zu portierenden Rufnummern in Taifun.\n\n");
            sb.append("Folgende Rufnummer-Portierungen wurden bestellt:\n");
            if (portierungAndererCarrier != null) {
                sb.append(portierungAndererCarrier.toString()).append("\n");
            }
            sb.append("Folgende Rufnummer-Portierungen ergibt sich aus den Rufnummern in Taifun:\n");
            if (portierungMnet != null) {
                sb.append(portierungMnet.toString()).append("\n");
            }
        }

        if (hasWarnings()) {
            sb.append("\nBitte diese Warnung nicht ignorieren, sondern den/die Fehler beheben!\n");
        }

        return sb.toString();
    }

    /**
     * Check Liefertermin == Portierungsdatum (realDate) der Rufnummern
     */
    public void checkPortierungsDatum(List<Rufnummer> portierungen, LocalDate expectedDate) {
        this.expectedVorgabeDate = expectedDate;
        for (Rufnummer portierung : portierungen) {
            LocalDate realDate = DateConverterUtils.asLocalDate(portierung.getRealDate());
            if ((portierung.getRealDate() == null) || !expectedDate.equals(realDate)) {
                falschesPortierungsdatum.add(portierung);
            }
        }
    }

    /**
     * Vergleicht zwei Portierungen miteinander.
     *
     * @return true falls beide Portierungen gleich sonst false.
     */
    public void checkPortierungenEqual(RufnummernPortierung portierungMnet,
            RufnummernPortierung portierungAndererCarrier) {
        this.portierungMnet = portierungMnet;
        this.portierungAndererCarrier = portierungAndererCarrier;

        if (isOnePortierungNull()) {
            setPortierungenUngleich(isAufnehmend);
        }
        else if (portierungMnet != null
                && portierungAndererCarrier != null
                && !portierungMnet.isFachlichEqual(portierungAndererCarrier)) {
            setPortierungenUngleich(isAufnehmend);
        }
    }

    private void setPortierungenUngleich(boolean isAufnehmend) {
        if (isAufnehmend) {
            bestelltePortierungUngleichZurueckgemeldetePortierung = true;
        }
        else {
            taifunPortierungUngleichAkmPvPortierung = true;
        }
    }

    private boolean isOnePortierungNull() {
        return ((portierungMnet != null) && (portierungAndererCarrier == null))
                || ((portierungMnet == null) && (portierungAndererCarrier != null));
    }
}
