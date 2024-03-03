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
 * Basisklasse fuer alle Exceptions, die sprachabhaengige Meldungen benoetigen.
 *
 *
 */
public abstract class LanguageException extends Exception {

    private static final Logger LOGGER = Logger.getLogger(LanguageException.class);

    private Locale locale = null;
    private String msgKey = null;
    private Object[] params = null;

    private String message = null;

    /**
     * Erzeugt eine Exception ohne Message-Text
     */
    public LanguageException() {
        super();
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     */
    public LanguageException(String msgKey) {
        super();
        init(msgKey, null, null);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageException(String msgKey, Locale locale) {
        super();
        init(msgKey, null, locale);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param cause  Urspruengliche Exception
     */
    public LanguageException(String msgKey, Throwable cause) {
        super(cause);
        init(msgKey, null, null);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param cause  Urspruengliche Exception
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageException(String msgKey, Throwable cause, Locale locale) {
        super(cause);
        init(msgKey, null, locale);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     */
    public LanguageException(String msgKey, Object[] params) {
        super();
        init(msgKey, params, null);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageException(String msgKey, Object[] params, Locale locale) {
        super();
        init(msgKey, params, locale);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param cause  Urspruengliche Exception
     */
    public LanguageException(String msgKey, Object[] params, Throwable cause) {
        super(cause);
        init(msgKey, params, null);
    }

    /**
     * Erzeugt eine Exception mit sprachabhaengigem Message-Text.
     *
     * @param msgKey Key, dem ein sprachabhaengiger Wert zugeordnet ist
     * @param params Parameter, die fuer evtl. vorhandene Platzhalter verwendet werden sollen.
     * @param cause  Urspruengliche Exception
     * @param locale Angabe der Locale, fuer dessen Sprache die Exception erzeugt werden soll.
     */
    public LanguageException(String msgKey, Object[] params, Throwable cause, Locale locale) {
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
     * Initialisiert die Exception.
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
            LOGGER.error("LanguageException - Error: " + e.getMessage(), e);
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
        else {
            return msgKey;
        }
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
