/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 15:11:03
 */
package de.augustakom.hurrican.gui.auftrag.wizards;


/**
 * Interface definiert Konstanten fuer die Namen der Wizard-Objekte.
 *
 *
 */
public interface AuftragWizardObjectNames {

    /**
     * Wert kennzeichnet eine Auftrags-'Neuschaltung'.
     */
    public static final String AUFTRAGSART_NEUSCHALTUNG = "Neuschaltung";
    /**
     * Wert kennzeichnet eine Auftrags-'Uebernahme'.
     */
    public static final String AUFTRAGSART_UEBERNAHME = "Übernahme";

    /**
     * Wizard Objekt-Name fuer die original Kundennummer
     */
    public static final String WIZARD_OBJECT_KUNDEN_NO = "kunde.no.orig";

    /**
     * Wizard Objekt-Name fuer die Angabe, ob der Abgleich fuer einen speziellen Taifun-Auftrag (true) oder fuer einen
     * ganzen Kunden (false) erfolgen soll.
     */
    public static final String WIZARD_OBJECT_TAIFUN_ORDER_SELECTION = "taifun.order.selection";

    /**
     * Wizard Objekt-Name fuer den Taifun-Auftrag (Auftrags-Id), der abgeglichen werden soll.
     */
    public static final String WIZARD_OBJECT_SELECTED_TAIFUN_ORDER = "selected.taifun.order";

    /**
     * Wizard Objekt-Name fuer die Auftrags-ID eines Auftrags.
     */
    public static final String WIZARD_OBJECT_AUFTRAG_ID = "auftrag.id";

    /**
     * Wizard Objekt-Name fuer die Produkt-ID eines Auftrags.
     */
    public static final String WIZARD_OBJECT_PRODUKT_ID = "produkt.id";

    /**
     * Wizard Objekt-Name fuer das neue AuftragDaten-Objekt
     */
    public static final String WIZARD_OBJECT_CC_AUFTRAG_DATEN = "cc.auftrag.daten";

    /**
     * Wizard Objekt-Name fuer das neue AuftragTechnik-Objekt
     */
    public static final String WIZARD_OBJECT_CC_AUFTRAG_TECHNIK = "cc.auftrag.technik";

    /**
     * Wizard Objekt-Name fuer ein neues AuftragIntern-Objekt.
     */
    public static final String WIZARD_OBJECT_CC_AUFTRAG_INTERN = "cc.auftrag.intern";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf ein String-Objekt, das die Auftragsart kennzeichnet.
     */
    public static final String WIZARD_OBJECT_CC_AUFTRAGSART = "cc.auftragsart";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf eine Collection mit den moeglichen Produkt2Produkt-Objekten fuer den
     * anzulegenden Auftrag.
     */
    public static final String WIZARD_OBJECT_POSSIBLE_PRODUCTCHANGES = "prod2prod";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf das AuftragsMonitor-Objekt, das den bei einem Produktwechsel zu
     * kuendigenden Auftrag enthaelt.
     */
    public static final String WIZARD_OBJECT_AM_KUENDIUNG_4_PROD_CHANGE = "am.kuendigung.4.prod.change";

    /**
     * Wizard Objekt-Name fuer das selektiert AuftragsMonitor-Objekt, das die Daten fuer einen Auftrags-Abgleich
     * enthaelt.
     */
    public static final String WIZARD_SELECTED_AUFTRAGS_MONITOR = "selected.auftrags.monitor";
    public static final String WIZARD_SELECTED_AUFTRAGS_MONITOR_LIST = "selected.auftrags.monitor.list";

    /**
     * Wizard Objekt-Name fuer das Date-Objekt mit dem Kuendigungstermin
     */
    public static final String WIZARD_KUENDIGUNG_DATUM = "kuendigung.datum";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf ein List-Objekt mit den Auftrag-IDs, die gekuendig werden sollen.
     */
    public static final String WIZARD_AUFTRAG_IDS_4_KUENDIGUNG = "auftrag.ids.4.kuendigung";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf die ID des Aenderungs-Auftrags.
     */
    public static final String WIZARD_OBJECT_VERLAUF_AENDERUNG_ID = "ba.verlauf.aenderung.id";

    /**
     * Wizard Objekt-Name fuer den Zugriff auf das Check-Objekt fuer eine Leistungsdifferenz. <br> Ueber das Objekt
     * koennen die Messages bei einem negativen Check abgefragt werden.
     */
    public static final String WIZARD_OBJECT_LEISTUNGS_DIFF_CHECK = "leistungs.diff.possible";

    /* **************** Konstanten fuer den TAL-Nutzungsaenderungs-Wizard ***************** */
    /**
     * Wizard Objekt-Name fuer den Zugriff auf den ausgewaehlten TAL-Nutzungsaenderungstyp.
     */
    public static final String WIZARD_OBJECT_TAL_NA_TYP = "tal.na.typ";

}


