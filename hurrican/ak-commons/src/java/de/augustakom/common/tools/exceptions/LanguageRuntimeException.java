/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2004
 */
package de.augustakom.common.tools.exceptions;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.lang.StringTools;


/**
 * Basisklasse fuer alle RuntimeExceptions, die sprachabhaengige Meldungen benoetigen.
 *
 *
 */
public abstract class LanguageRuntimeException extends RuntimeException {

    private static final Logger LOGGER = Logger.getLogger(LanguageRuntimeException.class);

    private Locale locale = null;
    private String msgKey = null;
    private Object[] params = null;

    private String message = null;

    /**
     * Erzeugt eine RuntimeException ohne Message-Text
     */
    public LanguageRuntimeException() {
        super();
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     */
    public LanguageRuntimeException(String msgKey) {
        super();
        init(msgKey, null, null);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageRuntimeException(String msgKey, Locale locale) {
        super();
        init(msgKey, null, locale);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param cause  Urspruengliche Exception
     */
    public LanguageRuntimeException(String msgKey, Throwable cause) {
        super(cause);
        init(msgKey, null, null);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param cause  Urspruengliche Exception
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageRuntimeException(String msgKey, Throwable cause, Locale locale) {
        super(cause);
        init(msgKey, null, locale);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     */
    public LanguageRuntimeException(String msgKey, Object[] params) {
        super();
        init(msgKey, params, null);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageRuntimeException(String msgKey, Object[] params, Locale locale) {
        super();
        init(msgKey, params, locale);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param cause  Urspruengliche Exception
     */
    public LanguageRuntimeException(String msgKey, Object[] params, Throwable cause) {
        super(cause);
        init(msgKey, params, null);
    }

    /**
     * Erzeugt eine RuntimeException mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param cause  Urspruengliche Exception
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageRuntimeException(String msgKey, Object[] params, Throwable cause, Locale locale) {
        super(cause);
        init(msgKey, params, locale);
    }

    /**
     * Die Ableitungen muessen diese Methode implementieren und hier den Namen des Property-Files zurueck liefern, das
     * verwendet werden soll.
     *
     * @return
     */
    public abstract String getResourceString();

    /**
     * Initialisiert die RuntimeException.
     *
     * @param msgKey
     * @param params
     * @param locale
     */
    protected void init(String msgKey, Object[] params, Locale locale) {
        this.msgKey = msgKey;
        this.params = params;
        this.locale = locale;

        readMessage();
    }

    /**
     * Liest den sprachabhaengigen Message-Text aus.
     */
    protected void readMessage() {
        try {
            if (this.msgKey != null) {
                ResourceReader res = null;
                if (this.locale != null) {
                    res = new ResourceReader(getResourceString(), this.locale);
                }
                else {
                    res = new ResourceReader(getResourceString());
                }

                this.message = res.getValue(msgKey, this.params);
            }
        }
        catch (Exception e) {
            LOGGER.debug("LanguageRuntimeException - Error: " + e.getMessage());
        }
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (message != null) {
            return message;
        }

        // Keine Message-Definition fuer msgKey gefunden
        if (params != null) {
            return StringTools.formatString(msgKey, params, this.locale);
        }

        return msgKey;
    }

    /**
     * Gibt den Key zurueck, der der Exception uebergeben wurde.
     *
     * @return
     */
    public String getKey() {
        return msgKey;
    }
}
