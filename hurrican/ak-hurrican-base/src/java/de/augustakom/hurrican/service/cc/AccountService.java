/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2004 09:02:24
 */
package de.augustakom.hurrican.service.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;


/**
 * Service-Interface zur Verwaltung der Internet-Daten. <br> Zu den Internet-Daten gehoeren u.a. IP-Adressen, Domains
 * und Mail-Aliase.
 *
 *
 */
@ObjectsAreNonnullByDefault
public interface AccountService extends ICCService {

    /**
     * Legt einen neuen Int-Account an. <br> Wird als <code>accountTyp</code> ein Verwaltungsaccount angegeben, so wird
     * als Account-Name der Wert von <code>accVorsatz</code> verwendet. <br> Ansonsten wird ein Account-Name erzeugt und
     * an den Vorsatz angehaengt. <br><br> Der angelegte Account (sofern kein Verwaltungsaccount) wird der
     * AuftragTechnik zugeordnet. Hatte die AuftragTechnik bereits einen Account, wird der Datensatz historisiert.
     *
     * @param auftragTechnik AuftragTechnik-Objekt dem der Account zugeordnet werden soll (nicht notwendig fuer
     *                       Verwaltungsaccount!).
     * @param accVorsatz     zu verwendender Account-Vorsatz
     * @param accountTyp     anzulegender Account-Typ (als Konstante im Modell <code>IntAccount</code> definiert.
     * @return der angelegte Account.
     * @throws StoreException wenn bei der Anlage ein Fehler auftritt.
     */
    IntAccount createIntAccount(@Nullable AuftragTechnik auftragTechnik, String accVorsatz,
            Integer accountTyp)
            throws StoreException;

    /**
     * Speichert das angegebene IntAccount-Objekt. <br> Ueber <code>makeHistory</code> wird bestimmt, ob ein bestehender
     * DS historisiert werden soll.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory History-Flag
     * @return abhaengig von makeHistory eine neue Instanz von <code>IntAccount</code> oder <code>toSave</code>.
     * @throws StoreException wenn bei der Abfrage ein Fehler auftritt.
     */
    IntAccount saveIntAccount(IntAccount toSave, boolean makeHistory) throws StoreException;

    /**
     * The same as {@link AccountService#saveIntAccount(IntAccount, boolean)} but in separate transaction
     */
    IntAccount saveIntAccountInSeparateTransaction(IntAccount toSave, boolean makeHistory) throws StoreException;

    /**
     * Ermittelt den gueltigen Account-REALM zu dem Auftrag/Account. <br> Der REALM ist wie folgt aufgebaut (Werte nach
     * 'account'): account@mdsl.mnet-online.de --> fuer ADSL account@dsl.mnet-online.de --> fuer SDSL account@vpn_realm
     * --> wenn Auftrag einem VPN zugeordnet ist und VPN einen REALM besitzt account%vpn_realm#mnet@ipx-dsl.de --> bei
     * L2TPs innerhalb eines VPNs
     *
     * @param auftragId ID des betroffenen Auftrags
     * @return REALM Bezeichnung fuer den Account / Auftrag oder <code>null</code>, wenn kein REALM ermittelt werden
     * konnte.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    @Nullable
    String getAccountRealm(Long auftragId) throws FindException;

    /**
     * Sperrt einen best. Account.
     *
     * @param accountId ID des zu sperrenden Accounts.
     * @throws StoreException wenn beim Speichern des Accounts ein Fehler auftritt.
     */
    void disableAccount(Long accountId) throws StoreException;

    /**
     * Sucht nach einem best. Account eines best. Typs.
     *
     * @param account    Account-Name (keine Wildcards!)
     * @param accountTyp Typ des gesuchten Accounts.
     * @param when       Datum fuer das der Account gueltig sein muss
     * @return Objekt vom Typ <code>IntAccount</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    IntAccount findIntAccount(String account, @CheckForNull Integer accountTyp, LocalDate when)
            throws FindException;

    /**
     * Sucht nach einem best. Account eines best. Typs.
     *
     * @param account    Account-Name (keine Wildcards!)
     * @param accountTyp Typ des gesuchten Accounts.
     * @return Objekt vom Typ <code>IntAccount</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    IntAccount findIntAccount(String account, @CheckForNull Integer accountTyp) throws FindException;

    /**
     * Ermittelt einen Account ueber den Account-Namen.
     *
     * @param account Account-Name (keine Wildcards!)
     * @return Objekt vom Typ <code>IntAccount</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    IntAccount findIntAccount(String account) throws FindException;

    /**
     * Sucht nach einem best. IntAccount ueber die ID.
     *
     * @param accountId ID des gesuchten IntAccount
     * @return Instanz von IntAccount oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    IntAccount findIntAccountById(Long accountId) throws FindException;

    /**
     * Sucht nach allen Accounts, die einem best. Auftrag zugeordnet sind.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen Accounts gesucht werden.
     * @return Liste mit Objekten des Typs <code>IntAccount</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    List<IntAccount> findIntAccounts4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * Sucht nach allen Accounts eines bestimmten Typs, die dem angegebenen Auftrag zugeordnet sind.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen Accounts gesucht werden.
     * @param accountTyp  Account-Typ
     * @return Liste mit Objekten des Typs <code>IntAccount</code> oder <code>leere Liste</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<IntAccount> findIntAccounts4Auftrag(@Nullable Long ccAuftragId, @Nullable Integer accountTyp)
            throws FindException;

    /**
     * Ueberprueft, ob die Produkte vom neuen und alten Auftrag den gleichen Account-Typ besitzen. Ist dies der Fall,
     * wird der Account des alten Auftrags auf den neuen Auftrag gesetzt. <br> Der urspruengliche Account des
     * Neu-Auftrags wird in diesem Zug als 'gesperrt' markiert.
     *
     * @param auftragIdNew ID des neuen Auftrags.
     * @param auftragIdOld ID des alten Auftrags
     * @throws StoreException wenn beim Umhaengen des Accounts ein Fehler auftritt
     *
     */
    void moveAccount(Long auftragIdNew, Long auftragIdOld) throws StoreException;

    /**
     * HUR-8980: Account Übernahme in TAL Wizard. Überprüft, ob der Account von einem alten zu einem neuen Auftrag
     * transferiert werden kann.
     *
     * @param auftragIdNew ID des neuen Auftrags.
     * @param auftragIdOld ID des alten Auftrags
     *
     */
    boolean isAccountMovable(Long auftragIdNew, Long auftragIdOld) throws StoreException;

    /**
     * Sucht nach einer best. Account-Art ueber die LiNr.
     *
     * @param liNr
     * @return Instanz von <code>AccountArt</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    AccountArt findAccountArt4LiNr(Integer liNr) throws FindException;

    /**
     * Sucht nach allen definierten Account-Arten.
     *
     * @return Liste mit Objekten des Typs <code>AccountArt</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    @Nullable
    List<AccountArt> findAccountArten() throws FindException;

    /**
     * Sucht nach Account- und Auftrags-Daten fuer Auftraege, die einen Einwahlaccount besitzen (und keinem VPN
     * zugeordnet sind). <br> Ausserdem wird nach den Abrechnung-Accounts von SDSL-Auftraegen gesucht.
     *
     * @param kundeNoOrig    (original) Kundennummer zu der die Auftrags- und Account-Daten ermittelt werden sollen.
     * @param produktGruppen (optional) Angabe der Produkt-Gruppen, in denen sich die Auftraege bzw. Accounts befinden
     *                       muessen. Wird keine Produkt-Gruppe angegeben, werden alle aktiven Accounts des Kunden
     *                       ermittelt.
     * @return Liste mit Objekten des Typs <code>AuftragIntAccountView</code>
     * @throws FindException
     */
    List<AuftragIntAccountView> findAuftragAccountView(Long kundeNoOrig, @Nullable List<Long> produktGruppen)
            throws FindException;


    /**
     * Funktion erzeugt alle notwendigen Accounts zu einem Auftrag. Es finden keine Nachfragen bzgl. Uebernahme von
     * Accounts statt. Accounts werden anhand der Produktkonfiguration erzeugt.
     *
     * @param auftragId        Id des technischen Auftrags
     * @param accountRufnummer Rufnummer des Einwahlaccounts (optional)
     * @throws StoreException Falls eine Fehler auftrat
     *
     */
    void createAccount4Auftrag(Long auftragId, @Nullable String accountRufnummer) throws StoreException;

}


