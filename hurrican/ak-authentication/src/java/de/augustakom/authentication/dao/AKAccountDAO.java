/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2004 08:41:36
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKAccount;


/**
 * Interface definiert Methoden fuer DAO-Objekte  zur Account-Verwaltung.
 */
public interface AKAccountDAO {

    /**
     * Sucht nach allen Account-Objekten, die einem best. Benutzer zugeordnet sind und die fuer eine best. Applikation
     * gedacht sind.
     *
     * @param userId
     * @param applicationId
     * @return Liste mit AKAccount-Objekten (never {@code null}).
     */
    public List<AKAccount> findAccounts(final Long userId, final Long applicationId);

    /**
     * Sucht nach allen Account-Objekten, die einem best. Benutzer zugeordnet sind.
     *
     * @param userId
     * @return List mit AKAccount-Objekten (never {@code null}).
     */
    public List<AKAccount> findAccounts(final Long userId);

    /**
     * Sucht nach allen Account-Objekten, die einer best. Datenbank zugeordnet sind.
     *
     * @param dbId
     * @return List mit AKAccount-Objekten (never {@code null}).
     */
    public List<AKAccount> findByDB(final Long dbId);

    /**
     * Erzeugt oder aktualisiert das AKAccount-Objekt.
     *
     * @param account
     */
    public void saveOrUpdate(AKAccount account);

    /**
     * Loescht den Account mit der angegebenen ID.
     *
     * @param id
     */
    public void delete(final Long id);

    /**
     * Sucht nach allen AKAccount-Objekten.
     *
     * @return Liste mit AKAccount-Objekten (never {@code null}).
     */
    public List<AKAccount> findAll();

    /**
     * Entfernt einen Eintrag aus der User-Account-Zuordnung. <br> Wichtig: in dieser Methode wird nicht der Account
     * selbst geloescht, sondern nur die Zuordnung zu einem bestimmten Benutzer.
     *
     * @param userId    ID des Users, dem der Account 'weggenommen' werden soll
     * @param accountId ID des Accounts, der dem Benutzer 'weggenommen' werden soll
     */
    public void removeUserAccount(final Long userId, final Long accountId);

    /**
     * Erzeugt einen neuen Eintrag in der User-Account-Zuordnung. <br> Wichtig: in dieser Methode wird kein neuer
     * Account angelegt, sondern nur eine Zuordnung zwischen einem Benutzer und einem Account hergestellt.
     *
     * @param userId    ID des Users, dem ein Account hinzugefuegt werden soll
     * @param accountId ID des Accounts, der dem Benutzer hinzugefuegt werden soll
     * @param dbId      ID der Datenbank, der der Account zugeordnet ist.
     */
    public void addUserAccount(final Long userId, final Long accountId, final Long dbId);
}
