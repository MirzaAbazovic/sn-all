/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 07:41:51
 */
package de.augustakom.common.tools.lang;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Hilfsklasse fuer die Arbeit mit Wildcards.
 *
 *
 */
public class WildcardTools {

    private static final Logger LOGGER = Logger.getLogger(WildcardTools.class);

    /**
     * Wildcard-Zeichen fuer das Java-System.
     */
    public static final String SYSTEM_WILDCARD = "*";
    /**
     * Wildcard-Zeichen fuer ein einzelnes Zeichen innerhalb des Java-Sytems.
     */
    public static final String SYSTEM_SINGLE_WILDCARD = "?";
    /**
     * Wildcard-Zeichen fuer die Datenbank.
     */
    public static final String DB_WILDCARD = "%";
    /**
     * Wildcard-Zeichen fuer ein einezlnes Zeichen in der DB.
     */
    public static final String DB_SINGLE_WILDCARD = "_";
    /**
     * ODER Wildcard für String Suchen. Kann auch mit anderen Wildcards kombiniert werden
     */
    public static final String STRING_OR_WILDCARD = "|";

    /**
     * Suche nach Punkt
     */
    private static RE re1 = null;
    /**
     * Suche nach Fragezeichen
     */
    private static RE re2 = null;
    /**
     * Suche nach Stern
     */
    private static RE re3 = null;
    /**
     * Suche nach (
     */
    private static RE re4 = null;
    /**
     * Suche nach )
     */
    private static RE re5 = null;
    /**
     * Suche nach +
     */
    private static RE re6 = null;

    /* Initialsiere die RE's nur einmal - Perfomance-Gewinn */
    static {
        try {
            re1 = new RE("[\\.]");
            re2 = new RE("[\\?]");
            re3 = new RE("[\\*]+");
            re4 = new RE("[\\(]");
            re5 = new RE("[\\)]");
            re6 = new RE("[\\+]");
        }
        catch (RESyntaxException e) {
            // Mehr koennen wir hier nicht machen
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Sucht in dem String <code>toReplace</code> nach Zeichen vom Typ <code>SYSTEM_WILDCARD</code> und ersetzt diese
     * durch <code>DB_WILDCARD</code>. Zurueck gegeben wird ein String, der die neuen Wildcards enthaelt.
     *
     * @param toReplace String, der die zu ersetzenden Wildcards enthaelt
     * @return String, der Wildcards vom Typ <code>DB_WILDCARD</code> besitzt.
     */
    public static String replaceWildcards(String toReplace) {
        String replaced = StringUtils.replace(toReplace, SYSTEM_WILDCARD, DB_WILDCARD);
        replaced = StringUtils.replace(replaced, SYSTEM_SINGLE_WILDCARD, DB_SINGLE_WILDCARD);
        return replaced;
    }

    /**
     * Ueberprueft, ob in dem String <code>toCheck</code> ein Wildcard-Zeichen enthalten ist.
     *
     * @param toCheck String, der auf Wildcard-Zeichen ueberprueft werden soll.
     * @return <code>true</code> - String enthaelt min. ein Wildcard-Zeichen
     */
    public static boolean containsWildcard(String toCheck) {
        boolean systemWildcard = StringUtils.contains(toCheck, SYSTEM_WILDCARD);
        if (systemWildcard) {
            return true;
        }

        boolean sysSingleWildcard = StringUtils.contains(toCheck, SYSTEM_SINGLE_WILDCARD);
        if (sysSingleWildcard) {
            return true;
        }

        boolean dbWildcard = StringUtils.contains(toCheck, DB_WILDCARD);
        if (dbWildcard) {
            return true;
        }

        boolean dbSingleWildcard = StringUtils.contains(toCheck, DB_SINGLE_WILDCARD);
        return dbSingleWildcard || StringUtils.contains(toCheck, STRING_OR_WILDCARD);

    }

    /**
     * Ueberprueft, ob ein Wert mit dem uebergebenen Pattern uebereinstimmt
     *
     * @param value   Der String, in dem nach einem Pattern gesucht wird
     * @param pattern Suchpattern
     * @return Wenn der Eingangsstring dem Pattern entspricht, wird TRUE ansonsten wird FALSE
     * @throws RESyntaxException Fehler bei der Erzeugung der RE
     */
    public static boolean match(String value, String pattern) throws RESyntaxException {
        if ((pattern == null) || "".equals(pattern)) {
            return true;
        }
        String rePattern = win2RE(pattern, true);

        /* Suche in den values mit der RE */
        RE re = new RE(rePattern);

        return re.match(value);
    }

    /**
     * @param value
     * @param pattern
     * @return
     * @see #match(String, String) Unterschied: Gross-/Kleinschreibung wird nicht beachtet.
     */
    public static boolean matchIgnoreCase(String value, String pattern) {
        if (value != null && pattern != null) {
            try {
                return match(value.toLowerCase(), pattern.toLowerCase());
            }
            catch (RESyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    /**
     * Uebersetzt den uebergebenen String mit Windows-Wildcards (, ?) in einen entsprechenden String entsprechend der
     * RE-Syntax (Posix)
     *
     * @param patternIn      Der String, der das Windows-Pattern darstellt
     * @param startAtBegin Gibt an, ob das Pattern am Anfang (true) eines Wortes oder an einer beliebigen Stelle (false)
     *                     beginnen soll
     * @return Der String, der die RE darstellt
     * @throws RESyntaxException Fehler bei der Erzeugtung der RE
     */
    public static String win2RE(String patternIn, boolean startAtBegin) throws RESyntaxException {
        /*
         * Das Pattern ist null, das heisst, dass alle Werte auf das Pattern 'passen'.
         * Also kann als Pattern '*' verwendet werden.
         */
        String pattern = patternIn == null ? "*" : patternIn;

        /*
         * Alle Backslash durch Doppel-Backslash ersetzen.
         * Es kann leider keine RE verwendet werden, da der RE-Compiler
         * eine Exception wirft, sobald im Paramter fuer den Konstruktor einer RE ein "\\"
         * enthalten ist.
         */
        String result = replace(pattern, "\\", "\\\\", -1);

        /* Alle Punkte (.) durch \\. ersetzen, damit die RE
         * den Punkt als Zeichen und nicht als Ausdruck interpretiert
         */
        result = re1.subst(result, "\\.");
        // Alle Fragezeichen durch . ersetzen
        result = re2.subst(result, ".");
        // Alle * durch .* ersetzen
        result = re3.subst(result, ".*");
        // Alle ( durch \\( ersetzen
        result = re4.subst(result, "\\(");
        // Alle ) durch \\) ersetzen
        result = re5.subst(result, "\\)");
        // Alle + durch \\+ ersetzen
        result = re6.subst(result, "\\+");

        if (!result.endsWith("*")) {
            // String muss mit Suchstring enden
            result += "$";
        }

        if (startAtBegin) {
            /*
             * Pattern soll am Anfang des Wortes beginnen
             * --> "^" hinzufuegen
             */
            result = "^" + result;
        }

        return result;
    }

    /**
     * Die Methode <code>replace</code>ist der Klasse <code>org.apache.commons.lang.StringUtils</code> entnommen. <p/>
     * <p> Replace a string with another  string inside a larger string, for the first <code>max</code> values of the
     * search string. A <code>null</code> reference passed to this method is a no-op. </p>
     *
     * @param text text to search and replace in
     * @param repl String to search for
     * @param with String to replace with
     * @param maxIn  maximum number of values to replace, or <code>-1</code> if no maximum
     * @return the text with any replacements processed
     * @throws NullPointerException if repl is null
     */
    private static String replace(String text,
            String repl,
            String with,
            int maxIn) {
        int max = maxIn;
        if (text == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder(text.length());
        int start = 0;
        int end = 0;

        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }

        buf.append(text.substring(start));

        return buf.toString();
    }
}
