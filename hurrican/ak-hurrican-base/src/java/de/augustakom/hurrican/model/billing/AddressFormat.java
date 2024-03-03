/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2006 15:26:14
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;
import java.util.zip.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;


/**
 * Modell-Klasse zur Abbildung von Formatierungsvorschriften fuer Adressen.
 *
 *
 */
public class AddressFormat extends AbstractBillingModel {

    private static final Logger LOGGER = Logger.getLogger(AddressFormat.class);

    public static final String ADDRESS_FORMAT_NAME_BUSINESS = "BUSINESS";
    public static final String ADDRESS_FORMAT_NAME_RESIDENTIAL = "RESIDENTIAL";
    public static final String ADDRESS_FORMAT_NAME_HERRFRAU = "Herr und Frau";
    public static final String ADDRESS_FORMAT_NAME_FRAUHERR = "Frau und Herr";

    public static final String FORMAT_DEFAULT = "defaultFormat";
    public static final String FORMAT_SHORT = "shortFormat";
    public static final String FORMAT_NAME = "nameFormat";
    public static final String FORMAT_SALUTATION = "salutationFormat";

    public static final String SALUTATION_FIRMA = "FIRMA";
    public static final String SALUTATION_HERR = "HERR";
    public static final String SALUTATION_FRAU = "FRAU";
    public static final String SALUTATION_FRAUHERR = "FRAUHERR";
    public static final String SALUTATION_HERRFRAU = "HERRFRAU";
    public static final String SALUTATION_KEINE = "KEINE";

    protected static final String SALUTATION_ANREDE_DEFAULT = "Sehr geehrte Damen und Herren,";
    protected static final String SALUTATION_ANREDE_HERR = "Sehr geehrter Herr ";
    protected static final String SALUTATION_ANREDE_FRAU = "Sehr geehrte Frau ";

    protected static final String SALUTATION_HEADER_HERR = "Herr ";
    protected static final String SALUTATION_HEADER_FRAU = "Frau ";
    protected static final String SALUTATION_HEADER_HERRFRAU = "Herr und Frau ";
    protected static final String SALUTATION_HEADER_FRAUHERR = "Frau und Herr ";

    private static final String PLACEHOLDER_START = "$(";
    private static final String PLACEHOLDER_END = ")";

    private static final String PLACEHOLDER_SPECIAL_SALUTATION = "SPECIAL_SALUTATION";
    private static final String PLACEHOLDER_TITLE = "TITLE";
    private static final String PLACEHOLDER_TITLE2 = "TITEL2";
    private static final String PLACEHOLDER_VORNAME = "C_VORNAME";
    private static final String PLACEHOLDER_VORNAME2 = "VORNAME2";
    private static final String PLACEHOLDER_NAME = "C_NAME";
    private static final String PLACEHOLDER_NAME2 = "NAME2";
    private static final String PLACEHOLDER_VORSATZWORT = "VORSATZWORT";
    private static final String PLACEHOLDER_NAME_ADD = "NAME_ADD";
    private static final String PLACEHOLDER_STRASSE = "STRASSE";
    private static final String PLACEHOLDER_FLOOR = "FLOOR";
    private static final String PLACEHOLDER_NUMMER = "NUMMER";
    private static final String PLACEHOLDER_HAUSNUMMER_ZUSATZ = "HOUSE_NUM_ADD";
    private static final String PLACEHOLDER_PO_BOX_FORMATTED_NO_STREET = "PO_BOX_FORMATTED_NO_STREET";
    private static final String PLACEHOLDER_PLZ = "PLZ";
    private static final String PLACEHOLDER_ORT = "ORT";

    private static final String PLACEHOLDER_DISTRICT = "DISTRICT";

    private static final String EMPTY = "";

    private String formatName = null;
    private String defaultFormat = null;
    private String shortFormat = null;
    private String nameFormat = null;
    private String salutationFormat = null;

    /**
     * @return Returns the defaultFormat.
     */
    public String getDefaultFormat() {
        return this.defaultFormat;
    }

    /**
     * @param defaultFormat The defaultFormat to set.
     */
    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    /**
     * @return Returns the formatName.
     */
    public String getFormatName() {
        return this.formatName;
    }

    /**
     * @param formatName The formatName to set.
     */
    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    /**
     * @return Returns the nameFormat.
     */
    public String getNameFormat() {
        return this.nameFormat;
    }

    /**
     * @param nameFormat The nameFormat to set.
     */
    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    /**
     * @return Returns the salutationFormat.
     */
    public String getSalutationFormat() {
        return this.salutationFormat;
    }

    /**
     * @param salutationFormat The salutationFormat to set.
     */
    public void setSalutationFormat(String salutationFormat) {
        this.salutationFormat = salutationFormat;
    }

    /**
     * @return Returns the shortFormat.
     */
    public String getShortFormat() {
        return this.shortFormat;
    }

    /**
     * @param shortFormat The shortFormat to set.
     */
    public void setShortFormat(String shortFormat) {
        this.shortFormat = shortFormat;
    }

    /**
     * Formatiert die Adresse <code>adresse</code> mit der angegebenen Format-Vorschrift.
     *
     * @param adresse
     * @param formatType
     * @return die formatierte Adresse. Jede Zeile der Adresse entspricht einem Eintrag des Arrays.
     * @throws DataFormatException wenn kein Pattern vorhanden ist
     *
     */
    public String[] format(Adresse adresse, String formatType) throws DataFormatException {
        try {
            String pattern = (String) PropertyUtils.getProperty(this, formatType);
            if (StringUtils.isBlank(pattern)) {
                throw new DataFormatException("Kein Pattern fuer den angegebenen Adresstyp gefunden!");
            }

            List<String> result = new ArrayList<String>();

            String[] lines = StringUtils.split(pattern, SystemUtils.LINE_SEPARATOR);
            for (String line : lines) {
                if (StringUtils.contains(line, PLACEHOLDER_START)) {
                    line = replacePlaceholders(line, adresse, formatType);
                }

                if (StringUtils.isNotEmpty(line)) {
                    line = StringUtils.removeStart(line, " - ");
                    line = StringUtils.trim(line);                // Leerzeichen am Anfang u. Ende entfernen
                    while (StringUtils.contains(line, "  ")) {
                        line = StringUtils.replace(line, "  ", " ");  // doppeltes Leerzeichen durch einfaches ersetzen
                    }

                    // Leerzeilen werden nicht angedruckt
                    if (StringUtils.isNotBlank(line)) {
                        result.add(line);
                    }
                }
            }

            return result.toArray(new String[result.size()]);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return new String[0];
    }

    /**
     * Replaces the placeholders in the given {@code line} with the corresponding value of {@code address}
     * based on the {@code formatType}.
     * Following placeholders are taken into account:
     * <ul>
     *     <li>PLACEHOLDER_TITLE</li>
     *     <li>PLACEHOLDER_TITLE2</li>
     *     <li>PLACEHOLDER_NAME</li>
     *     <li>PLACEHOLDER_NAME2</li>
     *     <li>PLACEHOLDER_VORNAME</li>
     *     <li>PLACEHOLDER_VORNAME2</li>
     *     <li>PLACEHOLDER_VORSATZWORT</li>
     *     <li>PLACEHOLDER_NAME_ADD</li>
     *     <li>PLACEHOLDER_STRASSE</li>
     *     <li>PLACEHOLDER_FLOOR</li>
     *     <li>PLACEHOLDER_NUMMER</li>
     *     <li>PLACEHOLDER_HAUSNUMMER_ZUSATZ</li>
     *     <li>PLACEHOLDER_PO_BOX_FORMATTED_NO_STREET</li>
     *     <li>PLACEHOLDER_PLZ</li>
     *     <li>PLACEHOLDER_ORT</li>
     *     <li>PLACEHOLDER_DISTRICT</li>
     *     <li>PLACEHOLDER_SPECIAL_SALUTATION</li>
     * </ul>
     *
     * @param adressLine
     * @param adresse
     * @param formatType
     *
     */
    private String replacePlaceholders(String line, Adresse address, String formatType) {
        String adressLine = line;
        while (StringUtils.contains(adressLine, PLACEHOLDER_START)) {
            String sub = StringUtils.substringBetween(adressLine, PLACEHOLDER_START, PLACEHOLDER_END);

            if (StringUtils.equals(sub, PLACEHOLDER_TITLE)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_TITLE + PLACEHOLDER_END,
                        (address.getTitel() != null) ? address.getTitel() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_TITLE2)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_TITLE2 + PLACEHOLDER_END,
                        (address.getTitel2() != null) ? address.getTitel2() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_NAME)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_NAME + PLACEHOLDER_END,
                        (address.getName() != null) ? address.getName() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_NAME2)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_NAME2 + PLACEHOLDER_END,
                        (address.getName2() != null) ? address.getName2() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_VORNAME)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_VORNAME + PLACEHOLDER_END,
                        (address.getVorname() != null) ? address.getVorname() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_VORNAME2)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_VORNAME2 + PLACEHOLDER_END,
                        (address.getVorname2() != null) ? address.getVorname2() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_VORSATZWORT)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_VORSATZWORT + PLACEHOLDER_END,
                        (address.getVorsatzwort() != null) ? address.getVorsatzwort() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_NAME_ADD)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_NAME_ADD + PLACEHOLDER_END,
                        (address.getNameAdd() != null) ? address.getNameAdd() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_STRASSE)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_STRASSE + PLACEHOLDER_END,
                        (address.getStrasse() != null) ? address.getStrasse() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_FLOOR)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_FLOOR + PLACEHOLDER_END,
                        (address.getFloor() != null) ? address.getFloor() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_NUMMER)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_NUMMER + PLACEHOLDER_END,
                        (address.getNummer() != null) ? address.getNummer() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_HAUSNUMMER_ZUSATZ)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_HAUSNUMMER_ZUSATZ + PLACEHOLDER_END,
                        (address.getHausnummerZusatz() != null) ? address.getHausnummerZusatz() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_PO_BOX_FORMATTED_NO_STREET)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_PO_BOX_FORMATTED_NO_STREET + PLACEHOLDER_END,
                        (address.getPostfach() != null) ? "Postfach " + address.getPostfach() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_PLZ)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_PLZ + PLACEHOLDER_END,
                        (address.getPlz() != null) ? address.getPlz() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_ORT)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_ORT + PLACEHOLDER_END,
                        (address.getOrt() != null) ? StringUtils.trimToEmpty(address.getOrt()) : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_DISTRICT)) {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_DISTRICT + PLACEHOLDER_END,
                        (address.getOrtsteil() != null) ? address.getOrtsteil() : EMPTY);
            }
            else if (StringUtils.equals(sub, PLACEHOLDER_SPECIAL_SALUTATION)) {
                String replace = EMPTY;
                if (StringUtils.equals(formatType, FORMAT_SALUTATION)) {
                    // Anrede
                    if (StringUtils.equals(address.getAnrede(), SALUTATION_HERR)) {
                        replace = SALUTATION_ANREDE_HERR;
                    }
                    else if (StringUtils.equals(address.getAnrede(), SALUTATION_FRAU)) {
                        replace = SALUTATION_ANREDE_FRAU;
                    }
                    else {
                        replace = SALUTATION_ANREDE_DEFAULT;
                    }
                }
                else {
                    // Kopf
                    if (StringUtils.equals(address.getAnrede(), SALUTATION_HERR)) {
                        replace = SALUTATION_HEADER_HERR;
                    }
                    else if (StringUtils.equals(address.getAnrede(), SALUTATION_FRAU)) {
                        replace = SALUTATION_HEADER_FRAU;
                    }
                    else if (StringUtils.equals(address.getAnrede(), SALUTATION_HERRFRAU)) {
                        replace = SALUTATION_HEADER_HERRFRAU;
                    }
                    else if (StringUtils.equals(address.getAnrede(), SALUTATION_FRAUHERR)) {
                        replace = SALUTATION_HEADER_FRAUHERR;
                    }
                }

                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + PLACEHOLDER_SPECIAL_SALUTATION + PLACEHOLDER_END, replace);
            }
            else {
                adressLine = StringUtils.replace(adressLine,
                        PLACEHOLDER_START + sub + PLACEHOLDER_END, EMPTY);
            }
        }

        return adressLine;
    }

}



