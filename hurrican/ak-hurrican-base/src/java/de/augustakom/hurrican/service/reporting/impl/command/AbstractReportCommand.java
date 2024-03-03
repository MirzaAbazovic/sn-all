/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2007 13:31:07
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Klasse für alle Report-Commands.
 *
 *
 */
public abstract class AbstractReportCommand extends AbstractServiceCommand {

    /**
     * Keys fuer die prepare-Methode.
     */
    public static final String AUFTRAG_ID = "auftrag.id";
    public static final String KUNDE_NO_ORIG = "kunde.no.orig";
    public static final String REQUEST_ID = "request.id";
    public static final String ORDER_NO_ORIG = "order.no.orig";
    public static final String USER_LOGINNAME = "user.loginname";
    public static final String ARCHIV_OBJECT_ID = "archiv.object.id";
    public static final String LAST_UPDATE = "last.update";

    // Konstanten für Präfixe
    public static final String AUFTRAG_DATEN = "auftragdaten";
    public static final String AUFTRAG_FAKTURA = "auftragfaktura";
    public static final String DSL_ACCOUNT = "dslaccount";
    public static final String ONLINE_ACCOUNT = "onlineaccount";
    public static final String ENDSTELLEN_DATEN = "endstellen";
    public static final String ENDSTELLE_DATEN = "es";
    public static final String HVT_DATEN = "hvt";
    public static final String KUNDEN_DATEN = "kunde";
    public static final String RANGIER_DATEN = "rangierung";
    public static final String RUFNUMMERN_DATEN = "rufnummern";
    public static final String USER_DATEN = "user";
    public static final String ALL_ACCOUNTS = "allaccounts";
    public static final String TXT_BAUSTEINE = "txt";
    public static final String AUFTRAG_LEISTUNGEN = "auftragleist";
    public static final String AUFTRAG_BANK = "auftragbank";
    public static final String RESELLER_DATEN = "reseller";
    public static final String REPORT_DATEN = "report";
    public static final String LIEFERSCHEIN = "lieferschein";
    public static final String MAXI_PORTAL = "maxiportal";
    public static final String LS_STATISTIK = "lsstatistik";
    public static final String SRN_DATEN = "srn";
    public static final String ANSPRECHPARTNER = "ansprechpartner";
    public static final String ENDGERAETE = "cpes";
    public static final String GEWOFAG = "gewofag";

    public static final String PROPERTY_NAME_SEPARATOR = "_";


    private String cmdPrefix = null;

    /**
     * @return cmdPrefix
     */
    public String getCmdPrefix() {
        return cmdPrefix;
    }

    /**
     * @param cmdPrefix Festzulegender cmdPrefix
     */
    public void setCmdPrefix(String cmdPrefix) {
        this.cmdPrefix = cmdPrefix;
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCommand#execute()
     */
    public abstract Object execute() throws Exception;

    /**
     * Funktion liefert den Dateinamen des Property-Files zurück
     *
     * @return
     *
     */
    public abstract String getPropertyFile();

    /**
     * Funktion liefert den Property-Prefix zurück
     *
     * @return
     *
     */
    public abstract String getPrefix();

    /**
     * Funktion erzeugt den Key für ein Property
     *
     * @param key
     * @return
     *
     */
    public String getPropName(String key) {
        return getPrefix() + PROPERTY_NAME_SEPARATOR + key;
    }
}


