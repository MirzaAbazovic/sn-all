/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2009 11:54:02
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.CfgRegularExpression.Info;


/**
 * CfgRegularExpression-Service.
 *
 *
 */
public interface RegularExpressionService extends ICCService {

    /**
     * Sucht nach einem regulaeren Ausdruck in der Datenbank und matcht den gegebenen match String. Das Resultat wird
     * zurueck geliefert.
     *
     * @param refId         Die ID des Objektes, fuer das die Regular Expression genutzt wird
     * @param refClass      Die Klasse des Objektes, fuer das die Regular Expression genutzt wird
     * @param requestedInfo Die Information, die mit der regular Expression gesucht werden soll
     * @param match         Der String, auf den das Matching angewendet wird
     * @return Das Resultat des Matchings (Gruppe ist in der DB konfiguriert), oder {@code null} falls das Matching
     * nicht erfolgreich war.
     */
    String match(Long refId, Class<?> refClass, Info requestedInfo, String match);

    /**
     * Sucht nach einem regulaeren Ausdruck in der Datenbank und matcht den gegebenen match String. Das Resultat wird
     * zurueck geliefert.
     *
     * @param refName       Der Name des Objektes, fuer das die Regular Expression genutzt wird
     * @param refClass      Die Klasse des Objektes, fuer das die Regular Expression genutzt wird
     * @param requestedInfo Die Information, die mit der regular Expression gesucht werden soll
     * @param match         Der String, auf den das Matching angewendet wird
     * @return Das Resultat des Matchings (Gruppe ist in der DB konfiguriert), oder {@code null} falls das Matching
     * nicht erfolgreich war.
     */
    String match(String refName, Class<?> refClass, Info requestedInfo, String match);

    /**
     * Sucht nach einem regulaeren Ausdruck in der Datenbank und prueft, ob der gegebenen match String matcht (matsch
     * matsch!).
     *
     * @param refId         Die ID des Objektes, fuer das die Regular Expression genutzt wird
     * @param refClass      Die Klasse des Objektes, fuer das die Regular Expression genutzt wird
     * @param requestedInfo Die Information, die mit der regular Expression gesucht werden soll
     * @param match         Der String, auf den das Matching angewendet wird
     * @return {@code null}, falls das Matching gelang. Der in der Datenbank hinterlegte description-String, falls
     * nicht.
     */
    String matches(Long refId, Class<?> refClass, Info requestedInfo, String match);

    /**
     * Sucht nach einem regulaeren Ausdruck in der Datenbank und prueft, ob der gegebenen match String matcht (matsch
     * matsch!)
     *
     * @param refName       Der Name des Objektes, fuer das die Regular Expression genutzt wird
     * @param refClass      Die Klasse des Objektes, fuer das die Regular Expression genutzt wird
     * @param requestedInfo Die Information, die mit der regular Expression gesucht werden soll
     * @param match         Der String, auf den das Matching angewendet wird
     * @return {@code null}, falls das Matching gelang. Der in der Datenbank hinterlegte description-String, falls
     * nicht.
     */
    String matches(String refName, Class<?> refClass, Info requestedInfo, String match);

}


