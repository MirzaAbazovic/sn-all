/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File Created: 12.05.2004 09:50:30
 */
package de.augustakom.hurrican.service.base.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.LanguageException;


/**
 * Wird geworfen, wenn beim Speichern eines Objekts ein Fehler aufgetreten ist.
 *
 *
 */
public class StoreException extends LanguageException {

    private static final long serialVersionUID = -4882165939858714621L;
    /**
     * MessageKey, dass ein nicht erwarteter Fehler aufgetreten ist.
     */
    public static final String _UNEXPECTED_ERROR = "100";
    /**
     * MessageKey, wenn eine ungueltige Session-ID uebergeben wurde.
     */
    public static final String INVALID_SESSION_ID = "101";
    /**
     * MessageKey, wenn ungueltige Parameter an die save-Methode uebergeben wurden.
     */
    public static final String ERROR_INVALID_PARAMETER_TO_STORE = "200";
    /**
     * MessageKey, wenn ein Objekt nicht historisiert werden konnte.
     */
    public static final String UNABLE_TO_CREATE_HISTORY = "201";
    /**
     * MessageKey, wenn ein CC-Auftrag nicht angelegt werden konnte.
     */
    public static final String UNABLE_TO_CREATE_CC_AUFTRAG = "1000";
    /**
     * MessageKey, wenn bei der Auftragskuendigung ein Fehler auftritt. <br> Parameter: <br> 0: ID des Auftrags.
     */
    public static final String UNABLE_TO_CANCEL_CC_AUFTRAG = "1001";
    /**
     * MessageKey, wenn eine neue VerbindungsBezeichnung nicht angelegt werden konnte.
     */
    public static final String UNABLE_TO_CREATE_VBZ = "1002";
    /**
     * MessageKey, wenn einem Auftrag eine ungültige Produkt-ID zugeordnet wurde.
     */
    public static final String INVALID_PRODUCT_ID = "1003";
    /**
     * MessageKey, wenn ein Account nicht angelegt werden konnte.
     */
    public static final String UNABLE_TO_CREATE_ACCOUNT = "1004";
    /**
     * MessageKey, wenn Endstellen nicht angelegt werden konnten.
     */
    public static final String UNABLE_TO_CREATE_ENDSTELLEN = "1005";
    /**
     * MessageKey, wenn ein Inhouse-Objekt nicht gespeichert werden konnte.
     */
    public static final String UNABLE_TO_SAVE_INHOUSE = "1007";
    /**
     * MessageKey, wenn eine Carrierbestellung nicht gespeichert werden konnte.
     */
    public static final String UNABLE_TO_SAVE_CARRIERBESTELLUNG = "1008";
    /**
     * MessageKey, wenn ein Ansprechpartner fuer eine Endstelle nicht gespeichert werden konnte. Parameter: <br> 0:
     * beliebiger Text als Grund
     */
    public static final String UNABLE_TO_SAVE_ES_ANSP = "1009";
    /**
     * MessageKey, wenn ein Auftrag einem VPN nicht zugeordnet werden konnte. Parameter: <br> 0: beliebiger Text als
     * Grund
     */
    public static final String UNABLE_TO_ADD_AUFTRAG2VPN = "1010";
    /**
     * MessageKey, wenn ein Auftrag nicht aus einem VPN entfernt werden konnte. Parameter: <br> 0: beliebiger Text als
     * Grund
     */
    public static final String UNABLE_TO_REMOVE_AUFTRAG_FROM_VPN = "1011";
    /**
     * MessageKey, dass eine IP-Adresse nicht angelegt werden kann. Parameter: <br> 0: beliebiger Text als Grund.
     */
    public static final String UNABLE_TO_ADD_IP_2_AUFTRAG = "1012";
    /**
     * MessageKey, dass die Sperre-Verteilungen nicht erstellt werden konnten.
     */
    public static final String UNABLE_TO_CREATE_SPERREVERTEILUNG = "1013";
    /**
     * MessageKey, dass eine Sperre nicht eingerichtet werden konnte. Parmeter 0: beliebiger Text als Grund.
     */
    public static final String UNABLE_TO_CREATE_SPERRE = "1014";
    /**
     * MessageKey, wenn beim Anlegen einer Rangierungsmatrix ein Fehler aufgetritt. Parameter 0: beliebiger Text.
     */
    public static final String UNABLE_TO_CREATE_MATRIX = "1015";
    /**
     * MessageKey, wenn ein CC-Auftrag nicht kopiert werden konnte.
     */
    public static final String UNABLE_TO_COPY_AUFTRAG = "1016";
    /**
     * MessageKey, wenn ein Produktwechsel nicht durchgefuehrt werden konnte.
     */
    public static final String UNABLE_TO_CHANGE_PRODUCT = "1017";
    /**
     * MessageKey, wenn ein VoIP-Zusatz fuer einen Auftrag nicht erzeugt werden konnte.
     */
    public static final String UNABLE_TO_CREATE_VOIP = "1018";
    /**
     * MessageKey, wenn ein VoIPDN-Zusatz fuer einen Auftrag nicht erzeugt werden konnte.
     */
    public static final String UNABLE_TO_CREATE_VOIP_DN = "1019";
    /**
     * MessageKey, dass ein Lock nicht eingerichtet werden konnte. Parmeter 0: beliebiger Text als Grund.
     */
    public static final String UNABLE_TO_CREATE_LOCK = "1020";

    /**
     * MessageKey, wenn bei der Physik-Zuordnung zu einer Endstelle ein Fehler auftritt. <br> Parameter:  <br> 0: ID der
     * Endstelle <br> 1: beliebiger Text als Grund <br>
     */
    public static final String ERROR_ASSIGN_RANGIERUNG_4_ES = "1100";
    /**
     * MessageKey, wenn fuer eine ES eine Rangierung gefunden wurde, diese aber der ES nicht zugeordnet werden konnte.
     */
    public static final String RANGIERUNG_NOT_ASSIGNED = "1101";
    /**
     * MessageKey, wenn bei der Physik-Uebernahme ein Fehler aufgetreten ist.
     */
    public static final String ERROR_AT_PHYSIK_UEBERNAHME = "1200";
    /**
     * MessageKey, wenn bei der Verteilung des Bauauftrags ein Fehler aufgetreten ist.
     */
    public static final String ERROR_BA_VERTEILEN = "1300";
    /**
     * MessageKey, wenn bei der Ermittlung der Abteilungen fuer einen BA ein Fehler auftritt. Param 0: Grund
     */
    public static final String ERROR_BA_ABT_ERMITTELN = "1301";
    /**
     * MessageKey, wenn beim Abschluss eines Bauauftrags ein Fehler auftritt. Param 0: Grund
     */
    public static final String ERROR_BA_ABSCHLIESSEN = "1302";
    /**
     * MessageKey, wenn beim Stornieren eines Bauauftrags ein Fehler auftritt. Param 0: Grund
     */
    public static final String ERROR_BA_STORNO = "1303";
    /**
     * MessageKey, wenn beim Freigeben einer Rangierung ein Fehler auftitt Param 0: rangierID  Param 1: Grund
     */
    public static final String ERROR_RANGIERUNG_FREIGEBEN = "1304";

    /**
     * MessageKey, wenn eine SDHPhysik nicht gespeichert werden konnte.
     */
    public static final String UNABLE_TO_SAVE_SDH_PHYSIK = "1400";
    /**
     * MessageKey, wenn eine SDHPhysik mit dem gleichen VPI/VCI A bereits existiert.
     */
    public static final String ERROR_SDHPHYSIK_EXIST = "1401";

    /**
     * MessageKey, wenn die EQH-Faktura einen Fehler verursacht hat.
     */
    public static final String ERROR_FAKTURA_EQH = "1500";
    /**
     * MessageKey, wenn fuer die Fakturierung ungueltige Daten uebergeben wurden.
     */
    public static final String INVALID_FAKTURA_DATA = "1501";
    /**
     * MessageKey, wenn die Connect-Faktura einen Fehler verursacht hat.
     */
    public static final String ERROR_FAKTURA_CONNECT = "1510";
    /**
     * MessageKey, wenn fuer die Connect-Faktura keine Auftraege gefunden wurden.
     */
    public static final String FAKTURA_CONNECT_NO_DATA = "1511";

    /**
     * MessageKey, wenn der 'neue' Auftrag bei einer TAL-Na. noch keine Physik besitzt.
     */
    public static final String ERROR_TAL_NA_NO_PHYSIK = "1600";
    /**
     * MessageKey, wenn der 'neue' Auftrag eine ungueltige Physik besitzt.
     */
    public static final String ERROR_TAL_NA_INVALID_PHYSIK = "1601";
    /**
     * MessageKey, wenn der 'neue' Auftrag bei einer TAL-Na. bereits eine positive Carrierbestellung besitzt.
     */
    public static final String ERROR_TAL_NA_CB_EXIST = "1602";
    /**
     * MessageKey, wenn bei einer TAL-Na. fuer den neuen Auftrag keine Endstellen ermittelt werden konnten.
     */
    public static final String ERROR_TAL_NA_KEINE_ENDSTELLEN = "1603";
    /**
     * MessageKey, wenn bei einer TAL-Na. der 'alte' Auftrag keine gueltige/aktive Carrierbestellung besitzt.
     */
    public static final String ERROR_TAL_NA_OLD_CB_NOT_OK = "1604";

    /**
     * MessageKey, wenn eine DN-Leistung bereits zugeordnet ist.
     */
    public static final String ERROR_DN_SERVICE_EXISTS = "1700";

    /**
     * MessageKey, wenn Produktionsdaten zu einem Verlauf nicht erstellt werden konnten.
     */
    public static final String ERROR_CREATING_PRODUCTION_DATA = "1800";

    /**
     * MessageKey, wenn eine Kündigung nicht rückgängig gemacht werden konnte.
     */
    public static final String ERROR_REVOKE_TERMINATION = "1900";
    /**
     * MessageKey, wenn eine Inbetriebnahme nicht rückgängig gemacht werden konnte.
     */
    public static final String ERROR_REVOKE_CREATION = "1901";

    /**
     * MessageKey, wenn die Default Rufnummernleistungen nicht gesetzt werden konnten.
     */
    public static final String ERROR_ATTACHING_DEFAULT_DN_SERVICES = "2000";


    /**
     * MessageKey, wenn unerwartete Fehler beim Aufruf von Taifun auftreten. Parameter: 0 = Error-Message
     */
    public static final String ERROR_TAIFUN_UNEXPECTED = "3000";
    /**
     * MessageKey, wenn Updates auf Taifun-Auftragsdaten einen Fehler verursachen. Parameter: 0 = Error-Code, 1 =
     * Error-Message
     */
    public static final String ERROR_TAIFUN_MODIFY_ORDER = "3001";
    /**
     * MessageKey, wenn Kuendigung von dem Taifun-Auftrag einen Fehler verursacht.
     */
    public static final String ERROR_TAIFUN_TERMINATE_ORDER = "3002";
    /**
     * MessageKey, wenn beim Anlegen einer Rufnummer in Taifun ein Fehler auftritt.
     */
    public static final String ERROR_TAIFUN_ADD_DIALNUMBER = "3003";
    /**
     * MessageKey, wenn beim Löschen einer Rufnummer in Taifun ein Fehler auftritt.
     */
    public static final String ERROR_TAIFUN_DELETE_DIALNUMBER = "3004";
    /**
     * MessageKey, wenn bei der Auftragskuendigung in Taifun ein Fehler aufgetreten ist.
     */
    public static final String ERROR_TAIFUN_CANCEL_ORDER = "3005";
    /**
     * MessageKey, wenn bei der Stornierung einer Auftragskuendigung (=Auftrag wieder in Betrieb nehmen)
     * ein Fehler aufgetreten ist.
     */
    public static final String ERROR_TAIFUN_UNDO_ORDER_CANCELLATION = "3006";

    public static final String ERROR_TAIFUN_REPORT_GENERATION = "3007";
    
    private static final String RESOURCE =
            "de.augustakom.hurrican.service.base.exceptions.StoreException";

    /**
     * @see LanguageException()
     */
    public StoreException() {
        super();
    }

    /**
     * @see LanguageException(String)
     */
    public StoreException(String msgKey) {
        super(msgKey);
    }

    /**
     * @see LanguageException(String, Locale)
     */
    public StoreException(String msgKey, Locale locale) {
        super(msgKey, locale);
    }

    /**
     * @see LanguageException(String, Throwable)
     */
    public StoreException(String msgKey, Throwable cause) {
        super(msgKey, cause);
    }

    /**
     * @see LanguageException(String, Throwable, Loccale)
     */
    public StoreException(String msgKey, Throwable cause, Locale locale) {
        super(msgKey, cause, locale);
    }

    /**
     * @see LanguageException(String, Object[])
     */
    public StoreException(String msgKey, Object[] params) {
        super(msgKey, params);
    }

    /**
     * @see LanguageException(String, Object[], Locale)
     */
    public StoreException(String msgKey, Object[] params, Locale locale) {
        super(msgKey, params, locale);
    }

    /**
     * @see LanguageException(String, Object[], Throwable)
     */
    public StoreException(String msgKey, Object[] params, Throwable cause) {
        super(msgKey, params, cause);
    }

    /**
     * @see LanguageException(String, Object[], Throwable, Locale)
     */
    public StoreException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(msgKey, params, cause, locale);
    }

    /**
     * @see de.augustakom.common.tools.exceptions.LanguageException#getResourceString()
     */
    @Override
    public String getResourceString() {
        return RESOURCE;
    }

}
