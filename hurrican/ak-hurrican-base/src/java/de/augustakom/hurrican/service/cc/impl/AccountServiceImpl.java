/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2004 10:21:06
 */
package de.augustakom.hurrican.service.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.AccountArtDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.dao.cc.IntAccountDAO;
import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.mnet.annotation.ObjectsAreNonnullByDefault;


/**
 * Service-Implementierung von AccountService. <br>
 *
 *
 */
@ObjectsAreNonnullByDefault
@CcTxRequired
public class AccountServiceImpl extends DefaultCCService implements AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.impl.IntAccountDAO")
    private IntAccountDAO intAccountDAO;
    @Resource(name = "accountArtDAO")
    private AccountArtDAO accountArtDAO;
    @Resource(name = "auftragTechnikDAO")
    private AuftragTechnikDAO auftragTechnikDAO;


    @Override
    public IntAccount createIntAccount(@Nullable AuftragTechnik auftragTechnik, String accVorsatz, Integer accountTyp)
            throws StoreException {
        try {
            String accountName = null;
            if (IntAccount.LINR_VERWALTUNGSACCOUNT.equals(accountTyp)) {
                accountName = accVorsatz;
            }
            else {
                accountName = createAccountName(accVorsatz);
            }

            String password = createAccountPassword();

            IntAccount account = new IntAccount();
            account.setAccount(accountName);
            account.setPasswort(password);
            account.setLiNr(accountTyp);
            account.setGueltigVon(new Date());
            account.setGueltigBis(DateTools.getHurricanEndDate());

            intAccountDAO.store(account);

            if (!IntAccount.LINR_VERWALTUNGSACCOUNT.equals(accountTyp) && (auftragTechnik != null)) {
                boolean makeHistory = (auftragTechnik.getIntAccountId() != null);
                auftragTechnik.setIntAccountId(account.getId());
                getAuftragService().saveAuftragTechnik(auftragTechnik, makeHistory);
            }

            return account;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_ACCOUNT, e);
        }
    }

    @Override
    public IntAccount saveIntAccount(IntAccount toSave, boolean makeHistory) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            IntAccountDAO dao = intAccountDAO;
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                IntAccount newAcc = dao.update4History(toSave, toSave.getId(), now);
                // flushSession wird ausgefuehrt wegen ForeignKey-Constraint im folgenden Update
                //HibernateSessionHelper.flushSession(getServiceLocator());
                dao.flushSession();

                // zugehoerige AuftragTechnik aktualisieren
                auftragTechnikDAO.update4IntAccount(newAcc.getId(), toSave.getId());

                return newAcc;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                dao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequiresNew
    public IntAccount saveIntAccountInSeparateTransaction(IntAccount toSave, boolean makeHistory) throws StoreException {
        return this.saveIntAccount(toSave, makeHistory);
    }

    @Override
    public String getAccountRealm(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            boolean separatorDefined = false;
            StringBuilder realm = new StringBuilder();
            String realmSuffix = null;

            Produkt produkt = getProduktService().findProdukt4Auftrag(auftragId);
            ProduktGruppe produktGruppe =
                    (produkt != null) ? getProduktService().findProduktGruppe(produkt.getProduktGruppeId()) : null;
            if (produktGruppe == null) {
                throw new FindException("Produkt-Gruppe des Auftrags konnte nicht ermittelt werden!");
            }
            else if (StringUtils.isBlank(produktGruppe.getRealm())) {
                return null;
            }

            AuftragTechnik auftragTechnik = getAuftragService().findAuftragTechnikByAuftragIdTx(auftragId);
            if (auftragTechnik == null) {
                throw new FindException("Die notwendigen Auftrags-Daten konnten nicht ermittelt werden!");
            }

            if ((auftragTechnik.getVpnId() != null)) {
                VPN vpn = getVpnService().findVPN(auftragTechnik.getVpnId());
                if (vpn == null) {
                    throw new FindException("VPN zum Auftrag konnte nicht ermittelt werden!");
                }

                if (StringUtils.isNotBlank(vpn.getRealm()) && produktGruppe.getRealm().startsWith(IntAccount.REALM_START_L2TP)) {
                    // L2TP + VPN --> account%vpn_realm#mnet@ipx-dsl.de
                    realm.append(IntAccount.REALM_SEP_L2TP);
                    realm.append(vpn.getRealm());
                    realmSuffix = produktGruppe.getRealm();
                }
                else {
                    // VPN  -->  account@vpn_realm
                    realm.append(IntAccount.REALM_SEP_DEFAULT);
                    realmSuffix = vpn.getRealm();
                }
                separatorDefined = true;
            }
            else if (produktGruppe.getRealm().startsWith(IntAccount.REALM_START_L2TP)) {
                // L2TP ohne VPN  -->  account%dsl.mnet-online.de#mnet@ipx-dsl.de
                realm.append(IntAccount.REALM_SEP_L2TP);
                realm.append(IntAccount.DEFAULT_REALM);
                separatorDefined = true;
            }

            if (!separatorDefined) {
                realm.append(IntAccount.REALM_SEP_DEFAULT);
            }

            if (StringUtils.isBlank(realmSuffix)) {
                realmSuffix = produktGruppe.getRealm();
            }

            realm.append(realmSuffix);
            return realm.toString();
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void disableAccount(Long accountId) throws StoreException {
        if (accountId == null) { return; }
        try {
            IntAccount acc = intAccountDAO.findById(accountId, IntAccount.class);
            if (acc != null) {
                acc.setGesperrt(Boolean.TRUE);
                saveIntAccount(acc, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Account konnte nicht gesperrt werden!", e);
        }
    }

    @Override
    public IntAccount findIntAccountById(Long accountId) throws FindException {
        if (accountId == null) { return null; }
        try {
            return intAccountDAO.findById(accountId, IntAccount.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IntAccount findIntAccount(String account, @CheckForNull Integer accountTyp, LocalDate when)
            throws FindException {
        if (StringUtils.isBlank(account)) {
            return null;
        }
        try {
            return intAccountDAO.findAccount(account, accountTyp, when);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public IntAccount findIntAccount(String account, @CheckForNull Integer accountTyp) throws FindException {
        return findIntAccount(account, accountTyp, LocalDate.now());
    }

    @Override
    public IntAccount findIntAccount(String account) throws FindException {
        return findIntAccount(account, null);
    }

    /*
     * Erzeugt einen neuen Account-Namen.
     * Der Account wird wie folgt zusammen gesetzt:
     *   'accVorsatz' + 8stellige Zahl aus Sequence
     *
     * Es wird so lange ein neuer Account-Name erzeugt,
     * bis einer gefunden wird, der noch nicht verwendet wird.
     *
     */
    private String createAccountName(String accVorsatz) throws StoreException, FindException {
        while (true) {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(accVorsatz)) {
                builder.append(accVorsatz);
            }
            else {
                builder.append(IntAccount.ACCOUNT_PREFIX_DEFAULT);
            }

            Long nextAccountValue = intAccountDAO.getNextAccountValue();
            if (nextAccountValue == null) {
                throw new StoreException("Could not generate account value!");
            }
            builder.append(nextAccountValue.toString());
            String accountName = builder.toString();

            IntAccount foundAccount = findIntAccount(accountName);
            if (foundAccount == null) {
                return accountName;
            }
        }
    }

    /* Erzeugt ein zufaelliges Password fuer einen Account.
     * Es wird so lange ein neues Passwort erzeugt, bis eines
     * gefunden wird, das noch nicht verwendet wird. <br>
     * Das Password setzt sich immer aus einem Buchstaben,
     * 6 beliebigen Zeichen und nochmals einem Buchstaben zusammen.
     */
    @Nullable
    private String createAccountPassword() {
        String password = null;

        IntAccount example = new IntAccount();

        boolean passwordExist = true;
        while (passwordExist) {
            String pw = RandomTools.createPassword(8);

            example.setPasswort(pw);
            List<IntAccount> result = intAccountDAO.queryByExample(example, IntAccount.class);
            if ((result == null) || (result.isEmpty())) {
                passwordExist = false;
                password = pw;
            }
        }

        return password;
    }

    @Override
    public List<IntAccount> findIntAccounts4Auftrag(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) { return null; }
        try {
            return intAccountDAO.findByAuftragIdAndTyp(ccAuftragId, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<IntAccount> findIntAccounts4Auftrag(Long ccAuftragId, @Nullable Integer accountTyp)
            throws FindException {
        Preconditions.checkNotNull(ccAuftragId, "ccAuftragId");
        try {
            return intAccountDAO.findByAuftragIdAndTyp(ccAuftragId, accountTyp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isAccountMovable(Long auftragIdNew, Long auftragIdOld) throws StoreException {
        try {
            CCAuftragService auftragService = getCCService(CCAuftragService.class);
            Auftrag newAuftrag = auftragService.findAuftragById(auftragIdNew);
            Auftrag oldAuftrag = auftragService.findAuftragById(auftragIdOld);
            if (!NumberTools.equal(newAuftrag.getKundeNo(), oldAuftrag.getKundeNo())) {
                // unterschiedlicher Kunde
                return false;
            }
            List<IntAccount> newAccounts = findIntAccounts4Auftrag(auftragIdNew, IntAccount.LINR_EINWAHLACCOUNT);
            List<IntAccount> oldAccounts = findIntAccounts4Auftrag(auftragIdOld, IntAccount.LINR_EINWAHLACCOUNT);
            if (newAccounts.isEmpty() || oldAccounts.isEmpty()) {
                // keine 2 Accounts
                return false;
            }
            for (IntAccount oldAccount : oldAccounts) {
                for (IntAccount newAccount : newAccounts) {
                    if (oldAccount.getId().equals(newAccount.getId())) {
                        // bereits gleicher Account bzw. Account bereits übernommen
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Prüfen, ob ein Account umgehaengt werden kann: " + e.getMessage(), e);
        }
    }

    @Override
    public void moveAccount(Long auftragIdNew, Long auftragIdOld) throws StoreException {
        try {
            Produkt prodNew = getProduktService().findProdukt4Auftrag(auftragIdNew);
            Produkt prodOld = getProduktService().findProdukt4Auftrag(auftragIdOld);

            if (NumberTools.equal(prodNew.getLiNr(), prodOld.getLiNr())) {
                AuftragTechnik atNew = getAuftragService().findAuftragTechnikByAuftragIdTx(auftragIdNew);
                AuftragTechnik atOld = getAuftragService().findAuftragTechnikByAuftragIdTx(auftragIdOld);

                IntAccount accNew = findIntAccountById(atNew.getIntAccountId());
                IntAccount accOld = findIntAccountById(atOld.getIntAccountId());

                accNew.setGesperrt(Boolean.TRUE);
                saveIntAccount(accNew, false);

                accOld.setGesperrt(Boolean.FALSE);
                saveIntAccount(accOld, false);

                atNew.setIntAccountId(accOld.getId());
                getAuftragService().saveAuftragTechnik(atNew, false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim umhaengen des Accounts: " + e.getMessage(), e);
        }
    }

    @Override
    @Nullable
    public AccountArt findAccountArt4LiNr(Integer liNr) throws FindException {
        if (liNr == null) { return null; }
        try {
            AccountArt example = new AccountArt();
            example.setLiNr(liNr);
            List<AccountArt> result = accountArtDAO.queryByExample(example, AccountArt.class);
            return ((result != null) && (result.size() == 1)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AccountArt> findAccountArten() throws FindException {
        try {
            return accountArtDAO.findAll(AccountArt.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragIntAccountView> findAuftragAccountView(Long kundeNoOrig, List<Long> produktGruppen)
            throws FindException {
        if (kundeNoOrig == null) { return null; }
        try {
            return intAccountDAO.findAuftragAccountViews(kundeNoOrig, produktGruppen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public void createAccount4Auftrag(Long auftragId, String accountRufnummer) throws StoreException {
        if (auftragId == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            // Ermittle AuftragDaten und AuftragTechnik
            Auftrag auftrag = getAuftragService().findAuftragById(auftragId);
            AuftragDaten ad = getAuftragService().findAuftragDatenByAuftragIdTx(auftragId);
            AuftragTechnik at = getAuftragService().findAuftragTechnikByAuftragIdTx(auftragId);

            if ((auftrag == null) || (ad == null) || (at == null)) {
                throw new StoreException(
                        "Auftragsdaten konnten nicht vollständig ermittelt werden. acount kann nicht angelegt werden");
            }

            // Ermittle Produkt
            Produkt prod = getProduktService().findProdukt(ad.getProdId());
            if (prod == null) {
                throw new StoreException("Produkt zum Auftrag konnte nicht ermittelt werden. " +
                        "Accounts werden deshalb nicht angelegt!");
            }

            if (StringUtils.isNotBlank(prod.getAccountVorsatz())) {
                // Erzeuge Verwaltungsaccount, falls noch nicht vorhanden
                // Pruefe, ob Kunden bereits einen Verwaltungsaccount besitzt
                IntAccount account = findIntAccount(auftrag.getKundeNo().toString(), IntAccount.LINR_VERWALTUNGSACCOUNT);

                if (account == null) {
                    createIntAccount(null, auftrag.getKundeNo().toString(), IntAccount.LINR_VERWALTUNGSACCOUNT);
                }

                // Einwahlaccount erstellen
                if (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, prod.getLiNr())) {
                    IntAccount intAcc = createIntAccount(at, prod.getAccountVorsatz(), prod.getLiNr());
                    if (StringUtils.isNotBlank(accountRufnummer)) {
                        intAcc.setRufnummer(accountRufnummer);
                        saveIntAccount(intAcc, false);
                    }
                }
                // Abrechnungsaccount anlegen
                else if (NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT_KONFIG, prod.getLiNr())
                        || NumberTools.equal(IntAccount.LINR_ABRECHNUNGSACCOUNT, prod.getLiNr())
                        || NumberTools.equal(IntAccount.LINR_EINWAHLACCOUNT, prod.getLiNr())) {
                    createIntAccount(at, prod.getAccountVorsatz(), prod.getLiNr());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Nullable
    private ProduktService getProduktService() {
        try {
            return getCCService(ProduktService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Nullable
    private CCAuftragService getAuftragService() {
        try {
            return getCCService(CCAuftragService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Nullable
    private VPNService getVpnService() {
        try {
            return getCCService(VPNService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }

}


