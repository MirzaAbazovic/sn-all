package de.mnet.wita.message;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallKueKd;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLae;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallLmae;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallNeu;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallPv;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallRexMk;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallSerPow;
import de.mnet.wita.message.auftrag.geschaeftsfall.GeschaeftsfallVbl;
import de.mnet.wita.message.meldung.position.GeschaeftsfallArt;

public enum GeschaeftsfallTyp {

    /* Achtung: Beim Anpassen der Namen die String-Konstanten weiter unten nachziehen! */
    // @formatter:off
    BEREITSTELLUNG("NEU", "Bereitstellung", GeschaeftsfallNeu.class, GeschaeftsfallArt.BEREITSTELLUNG, "TAL-Neubestellung"),
    BESTANDSUEBERSICHT("AUS-BUE", "Bestandsuebersicht", null, GeschaeftsfallArt.AUSKUNFT, "Bestandsübersicht"),
    KUENDIGUNG_KUNDE("KUE-KD", "Kuendigung", GeschaeftsfallKueKd.class, GeschaeftsfallArt.KUENDIGUNG, "Kündigung Kunde"),
    KUENDIGUNG_TELEKOM("KUE-DT", null, null, GeschaeftsfallArt.KUENDIGUNG, "Kündigung Telekom"),
    LEISTUNGSMERKMAL_AENDERUNG("AEN-LMAE", "LeistungsmerkmalAenderung", GeschaeftsfallLmae.class, GeschaeftsfallArt.AENDERUNG, "Leistungmerkmaländerung"),
    LEISTUNGS_AENDERUNG("LAE", "Leistungsaenderung", GeschaeftsfallLae.class, GeschaeftsfallArt.AENDERUNG, "Leistungsänderung"),
    PORTWECHSEL("SER-POW", "Portwechsel", GeschaeftsfallSerPow.class, GeschaeftsfallArt.BEREITSTELLUNG_SERVICE, "Portwechsel"),
    PRODUKTGRUPPENWECHSEL("PGW", "Produktgruppenwechsel", null, GeschaeftsfallArt.PRODUKTGRUPPENWECHSEL, "Produktgruppenwechsel"),
    PROVIDERWECHSEL("PV", "Providerwechsel", GeschaeftsfallPv.class, GeschaeftsfallArt.ANBIETERWECHSEL, "Providerwechsel"),
    RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG("REX-MK", "RnrExportMitKuendigung", GeschaeftsfallRexMk.class, GeschaeftsfallArt.ANBIETERWECHSEL, "Rnr-Export mit Anschlusskündigung"),
    VERBUNDLEISTUNG("VBL", "Verbundleistung", GeschaeftsfallVbl.class, GeschaeftsfallArt.ANBIETERWECHSEL, "Verbundleistung"),
    UNBEKANNT(null, null, null, null, "Unbekannt");
    // @formatter:on

    private static final Logger LOGGER = Logger.getLogger(GeschaeftsfallTyp.class);

    /*
     * Diese Konstanten sind die Diskrimator-Spalte des Geschaeftsfalls, deshalb hier nochmal als Konstanten. Damit die
     * Eintraege in der DB zwischen Meldung und Request (mit Geschaeftsfall) konstant bleiben
     */
    public static final String BEREITSTELLUNG_NAME = "BEREITSTELLUNG";
    public static final String BESTANDSUEBERSICHT_NAME = "BESTANDSUEBERSICHT";
    public static final String KUENDIGUNG_KUNDE_NAME = "KUENDIGUNG_KUNDE";
    public static final String KUENDIGUNG_TELEKOM_NAME = "KUENDIGUNG_TELEKOM";
    public static final String LEISTUNGSMERKMAL_AENDERUNG_NAME = "LEISTUNGSMERKMAL_AENDERUNG";
    public static final String LEISTUNGS_AENDERUNG_NAME = "LEISTUNGS_AENDERUNG";
    public static final String PORTWECHSEL_NAME = "PORTWECHSEL";
    public static final String PRODUKTGRUPPENWECHSEL_NAME = "PRODUKTGRUPPENWECHSEL";
    public static final String PROVIDERWECHSEL_NAME = "PROVIDERWECHSEL";
    public static final String RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG_NAME = "RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG";
    public static final String VERBUNDLEISTUNG_NAME = "VERBUNDLEISTUNG";

    /**
     * GeschaeftsfallTyp der im XML einer Meldung steht. Zur Konvertierung des Geschaeftsfalls auf einer Meldung von der
     * Telekom.
     */
    private final String dtagMeldungGeschaeftsfall;

    /**
     * GeschaeftsfallTyp der im XML eines Requests steht. Zur Konvertierung des Geschaeftsfalls aus einem Request von
     * M-net fuer z.B. das IO-Archiv.
     */
    private final String dtagRequestGeschaeftsfall;

    /**
     * Darstellung des Geschaeftsfalls in der GUI, z.B. fuer den History-Dialog
     */
    private final String displayName;
    private final Class<? extends Geschaeftsfall> clazz;
    private GeschaeftsfallArt geschaeftsfallArt;

    private GeschaeftsfallTyp(String dtagMeldungGeschaeftsfall, String dtagRequestGeschaeftsfall,
            Class<? extends Geschaeftsfall> clazz, GeschaeftsfallArt geschaeftsfallArt, String displayName) {
        this.dtagMeldungGeschaeftsfall = dtagMeldungGeschaeftsfall;
        this.dtagRequestGeschaeftsfall = dtagRequestGeschaeftsfall;
        this.clazz = clazz;
        this.geschaeftsfallArt = geschaeftsfallArt;
        this.displayName = displayName;
    }

    public String getDtagMeldungGeschaeftsfall() {
        return dtagMeldungGeschaeftsfall;
    }

    public String getDtagRequestGeschaeftsfall() {
        return dtagRequestGeschaeftsfall;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<? extends Geschaeftsfall> getClazz() {
        return clazz;
    }

    public GeschaeftsfallArt getGeschaeftsfallArt() {
        return geschaeftsfallArt;
    }

    public static GeschaeftsfallTyp buildFromMeldung(final String value) {
        for (GeschaeftsfallTyp typ : GeschaeftsfallTyp.values()) {
            if (typ.getDtagMeldungGeschaeftsfall() != null && typ.getDtagMeldungGeschaeftsfall().equals(value)) {
                return typ;
            }
        }
        LOGGER.error("GeschaeftsfallTyp von Meldung nicht bekannt: " + value);
        return UNBEKANNT;
    }

    public static GeschaeftsfallTyp buildFromRequest(final String value) {
        for (GeschaeftsfallTyp typ : GeschaeftsfallTyp.values()) {
            if (typ.getDtagRequestGeschaeftsfall() != null && typ.getDtagRequestGeschaeftsfall().equals(value)) {
                return typ;
            }
        }
        LOGGER.error("GeschaeftsfallTyp von Request nicht bekannt: " + value);
        return UNBEKANNT;
    }

    public static List<GeschaeftsfallTyp> implementedValues() {
        return Lists.newArrayList(Iterables.filter(Arrays.asList(values()),
                new Predicate<GeschaeftsfallTyp>() {
                    @Override
                    public boolean apply(GeschaeftsfallTyp input) {
                        return input.getClazz() != null;
                    }
                }
        ));
    }

}
