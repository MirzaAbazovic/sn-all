/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Anrede;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * Command-Klasse, um Kunden-Daten anhand der KundenNoOrig zu sammeln und diese in einer HashMap zu speichern.
 *
 *
 */
public class GetKundenDatenCommand extends AbstractReportCommand {

    private static final Logger LOGGER = Logger.getLogger(GetKundenDatenCommand.class);

    public static final String KUNDENO = "KundeNo";
    public static final String KUNDENOORIG = "KundeNoOrig";
    public static final String TYP = "Typ";
    public static final String BETREUERNO = "BetreuerNo";
    public static final String HAUPTKUNDENO = "HauptKundeNo";
    public static final String TITEL = "Titel";
    public static final String VORNAME = "Vorname";
    public static final String VORNAME2 = "Vorname2";
    public static final String NAME = "Name";
    public static final String NAME2 = "Name2";
    public static final String STRASSE = "Strasse";
    public static final String NUMMER = "Nummer";
    public static final String HAUSNUMMERZUS = "HausnummerZus";
    public static final String PLZ = "PLZ";
    public static final String ORT = "Ort";
    public static final String POSTFACH = "Postfach";
    public static final String ANSCHRIFTERG = "AnschriftErg";
    public static final String ADRESSANREDE = "Adressanrede";
    public static final String BRIEFANREDE = "Briefanrede";
    public static final String TAIFUNFORMATTEDADDRESS = "TaifunFormattedAddress";
    public static final String TAIFUNFORMATTEDANREDE = "TaifunFormattedAnrede";
    public static final String ABSENDER = "AbsenderAdresse";
    public static final String HAUPTRUFNUMMER = "Hauptrufnummer";

    public static final String ABSENDER_AGB = "M-net Telekommunikations GmbH | Curt-Frenzel-Straße 4 | 86167 Augsburg";
    public static final String ABSENDER_MUC = "M-net Telekommunikations GmbH | Niederlassung NEFkom | Spittlertorgraben 13 | 90429 Nürnberg";
    public static final String ABSENDER_NBG = "M-net Telekommunikations GmbH | Emmy-Noether-Str. 2 | 80992 München";


    private Long kundeNoOrig = null;
    private Map map = null;

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    @Override
    public Object execute() throws HurricanServiceCommandException {
        try {
            setCmdPrefix(getPrefix());
            checkValues();
            map = new HashMap();

            readKundenDaten();
            return map;
        }
        catch (Exception e) {
            throw new HurricanServiceCommandException(e);
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPrefix()
     */
    @Override
    public String getPrefix() {
        return KUNDEN_DATEN;
    }

    /* Ermittelt die Kundendaten zu einer KundenNoOrig und schreibt diese in die HashMap. */
    protected void readKundenDaten() throws HurricanServiceCommandException {
        try {
            // Suche Kunde anhand der KundeNoOrig.
            // Geliefert wird der Kundendatensatz mit hist_status=akt
            KundenService ks = getBillingService(KundenService.class);
            Kunde kunde = null;
            if (kundeNoOrig != null) {
                kunde = ks.findKunde(kundeNoOrig);
            }

            if (kunde != null) {
                map.put(getPropName(KUNDENO), NumberTools.convertToString(kunde.getKundeNo(), null));
                map.put(getPropName(KUNDENOORIG), NumberTools.convertToString(kunde.getKundeNo(), null));
                map.put(getPropName(TYP), kunde.getKundenTyp().trim());
                map.put(getPropName(BETREUERNO), NumberTools.convertToString(kunde.getKundenbetreuerNo(), null));
                map.put(getPropName(HAUPTKUNDENO), NumberTools.convertToString(kunde.getHauptKundenNo(), null));
                map.put(getPropName(HAUPTRUFNUMMER), (kunde.getHauptRufnummer() != null) ? kunde.getHauptRufnummer() : null);

                Adresse adresse = ks.getAdresse4Kunde(kunde.getKundeNo());
                map.put(getPropName(TITEL), (adresse != null) ? adresse.getTitel() : null);
                map.put(getPropName(VORNAME), (adresse != null) ? adresse.getVorname() : null);
                map.put(getPropName(VORNAME2), (adresse != null) ? adresse.getVorname2() : null);
                map.put(getPropName(NAME), (adresse != null) ? adresse.getName() : null);
                map.put(getPropName(NAME2), (adresse != null) ? adresse.getName2() : null);
                map.put(getPropName(STRASSE), (adresse != null) ? adresse.getStrasse() : null);
                map.put(getPropName(NUMMER), (adresse != null) ? adresse.getNummer() : null);
                map.put(getPropName(HAUSNUMMERZUS), (adresse != null) ? adresse.getHausnummerZusatz() : null);
                map.put(getPropName(PLZ), (adresse != null) ? adresse.getPlzTrimmed() : null);
                map.put(getPropName(ORT), (adresse != null) ? adresse.getCombinedOrtOrtsteil() : null);
                map.put(getPropName(POSTFACH), (adresse != null) ? adresse.getPostfach() : null);
                map.put(getPropName(ANSCHRIFTERG), (adresse != null) ? adresse.getNameAdd() : null);

                // Anrede suchen
                Anrede adressanrede = null;
                Anrede briefanrede = null;
                CCKundenService ccks = getCCService(CCKundenService.class);
                if (adresse != null) {
                    adressanrede = ccks.findAnrede(adresse.getFormat(), Anrede.ANREDEART_ADRESSE);
                    briefanrede = ccks.findAnrede(adresse.getAnrede(), Anrede.ANREDEART_ANSPRACHE);
                }
                map.put(getPropName(ADRESSANREDE), (adressanrede != null) ? adressanrede.getAnrede() : null);
                map.put(getPropName(BRIEFANREDE), (briefanrede != null) ? briefanrede.getAnrede() : null);

                // Taifun-Adresse formatiert als Liste von Strings einfügen
                Adresse address = ks.getAdresse4Kunde(kunde.getKundeNo());
                String[] formatted = ks.formatAddress(address, AddressFormat.FORMAT_DEFAULT);
                List<String> adressList = null;
                if (formatted != null) {
                    adressList = new ArrayList<>();
                    Collections.addAll(adressList, formatted);
                }
                map.put(getPropName(TAIFUNFORMATTEDADDRESS), adressList);

                // Formatierte Anrede aus Taifun einfügen
                String[] taifunAnrede = ks.formatAddress(address, AddressFormat.FORMAT_SALUTATION);
                List<String> anredeList = null;
                if (taifunAnrede != null) {
                    anredeList = new ArrayList<>();
                    Collections.addAll(anredeList, taifunAnrede);
                }
                map.put(getPropName(TAIFUNFORMATTEDANREDE), anredeList);

                // Belege Absenderadresse anhand der Reseller-Kundennr.
                Long reseller = kunde.getResellerKundeNo();
                if ((reseller == 100000009) || (reseller == 500101538) || (reseller == 100000081)
                        || (reseller == 400000001)) {
                    map.put(getPropName(ABSENDER), ABSENDER_AGB);
                }
                else if ((reseller == 500101539)) {
                    map.put(getPropName(ABSENDER), ABSENDER_MUC);
                }
                else if ((reseller == 500114514)) {
                    map.put(getPropName(ABSENDER), ABSENDER_NBG);
                }
                else {
                    map.put(getPropName(ABSENDER), null);
                }
            }
            else {
                throw new FindException("Kunden-Daten zu KundeNoOrig " +
                        kundeNoOrig + " konnten nicht ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(e);

        }
    }


    /* Ueberprueft, ob die Command-Klasse richtig konfiguriert wurde. */
    private void checkValues() throws HurricanServiceCommandException {
        Object tmpId = getPreparedValue(KUNDE_NO_ORIG);
        kundeNoOrig = (tmpId instanceof Long) ? (Long) tmpId : null;
        if (kundeNoOrig == null) {
            throw new HurricanServiceCommandException("Kunde__No wurde dem Command-Objekt nicht uebergeben!");
        }
    }

    /**
     * @see de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand#getPropertyFile()
     */
    @Override
    public String getPropertyFile() {
        return "/de/augustakom/hurrican/service/reporting/impl/command/resources/GetKundenDatenCommand.properties";
    }
}


