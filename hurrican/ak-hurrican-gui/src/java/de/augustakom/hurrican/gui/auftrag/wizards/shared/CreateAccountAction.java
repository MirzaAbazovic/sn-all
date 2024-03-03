/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2004 13:28:39
 */
package de.augustakom.hurrican.gui.auftrag.wizards.shared;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Action, um Accounts fuer einen Auftrag anzulegen. <br> Folgende Accounts werden angelegt: <ul> <li>ein
 * Verwaltungsaccount (LI_NR=2) pro Kunde. <li>Abrechnungsaccounts (LI_NR=0) - abhaengig vom Produkt <li>Einwahlaccounts
 * (LI_NR=1) - abhaengig vom Produkt </ul>
 *
 *
 */
public class CreateAccountAction extends AbstractServiceAction {

    private static final long serialVersionUID = 1433212248367208645L;

    private static final Logger LOGGER = Logger.getLogger(CreateAccountAction.class);

    private Auftrag auftrag = null;
    private Component owner = null;

    private AuftragDaten auftragDaten = null;
    private AuftragTechnik auftragTechnik = null;
    private Produkt produkt = null;

    /**
     * Konstruktor.
     *
     * @param auftrag Auftrag, fuer den Accounts angelegt werden sollen.
     * @param owner   Owner fuer Dialoge.
     */
    public CreateAccountAction(Auftrag auftrag, Component owner) {
        super();
        this.auftrag = auftrag;
        this.owner = owner;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            init();
            search4Produkt();

            if (produkt != null) {
                if (StringUtils.isNotBlank(produkt.getAccountVorsatz())) {
                    createVerwaltungsaccount();

                    if (IntAccount.LINR_EINWAHLACCOUNT.equals(produkt.getLiNr())) {
                        // Einwahlaccount erstellen.
                        createEinwahlaccount();
                    }
                    else if (IntAccount.LINR_ABRECHNUNGSACCOUNT.equals(produkt.getLiNr())) {
                        // Abrechnungsaccount erstellen.
                        createAccount(IntAccount.LINR_ABRECHNUNGSACCOUNT);
                    }
                    else if (IntAccount.LINR_EINWAHLACCOUNT_KONFIG.equals((produkt.getLiNr()))) {
                        // Einwahlaccount ohne Ausdruck erstellen
                        createAccount(IntAccount.LINR_EINWAHLACCOUNT_KONFIG);
                    }
                }
            }
            else {
                StringBuilder msg = new StringBuilder();
                msg.append("Es konnte kein Produkt mit der ID {0} gefunden werden. ");
                msg.append("Accounts werden deshalb nicht angelegt!");
                String pId = (auftragDaten.getProdId() != null) ? auftragDaten.getProdId().toString() : "<null>";
                MessageHelper.showInfoDialog(owner, msg.toString(), pId);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(owner, ex);
        }
    }

    /*
     * Legt einen Verwaltungsaccount an. Fuer jeden
     * Kunden wird nur ein Verwaltungsaccount angelegt.
     */
    private void createVerwaltungsaccount() throws StoreException, ServiceNotFoundException {
        AccountService is = getCCService(AccountService.class.getName(), AccountService.class);
        IntAccount account = null;

        try {
            account = is.findIntAccount(auftrag.getKundeNo().toString(), IntAccount.LINR_VERWALTUNGSACCOUNT);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (account == null) {
            createAccount(IntAccount.LINR_VERWALTUNGSACCOUNT);
        }
    }

    /*
     * Ueberprueft, ob der Kunde bereits einen Einwahlaccount fuer die
     * Produktgruppe besitzt. Wenn ja, kann dieser verwendet werden.
     * Ansonsten wird ein neuer Einwahlaccount angelegt.
     */
    private void createEinwahlaccount() throws FindException, StoreException, ServiceNotFoundException {
        // nach bestehenden Einwahlaccounts suchen
        AuftragIntAccountQuery query = new AuftragIntAccountQuery();
        query.setKundeNo(auftrag.getKundeNo());
        query.setProduktGruppeId(produkt.getProduktGruppeId());
        query.setIntAccountTyp(IntAccount.LINR_EINWAHLACCOUNT);

        CCAuftragService as = getCCService(CCAuftragService.class.getName(), CCAuftragService.class);

        List<AuftragIntAccountView> existingAccounts = as.findAuftragAccountViews(query);
        if ((existingAccounts != null) && !existingAccounts.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append("Der Kunde besitzt bereits einen Einwahl-Account für diese Produktgruppe.\n");
            msg.append("Dieser Account sollte nach Möglichkeit übernommen werden!\n\n");
            msg.append("Wollen Sie trotzdem einen neuen Account anlegen?");

            int selection = MessageHelper.showConfirmDialog(owner, msg.toString(), "Account übernehmen?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.YES_OPTION) {
                createAccount(IntAccount.LINR_EINWAHLACCOUNT);
            }
            else {
                // Account auswaehlen und der AuftragTechnik zuordnen
                IntAccountSelectionDialog dlg = new IntAccountSelectionDialog(existingAccounts);
                Object value = DialogHelper.showDialog(owner, dlg, true, true);
                if (value instanceof AuftragIntAccountView) {
                    try {
                        AuftragIntAccountView accView = (AuftragIntAccountView) value;
                        Long intAccountId = accView.getAccountId();

                        boolean makeHistory = (auftragTechnik.getIntAccountId() != null) ? true : false;
                        auftragTechnik.setIntAccountId(intAccountId);
                        as.saveAuftragTechnik(auftragTechnik, makeHistory);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        String message = "Der ausgewählte Account konnte dem Auftrag nicht zugeordnet werden. \nSiehe Details für weitere Informationen.";
                        MessageHelper.showInfoDialog(owner, message);
                    }
                }
                else {
                    MessageHelper.showInfoDialog(owner,
                            "Sie haben den Account-Übernahmedialog abgebrochen. Deshalb wird ein neuer Einwahlaccount angelegt",
                            null, true);
                    createAccount(IntAccount.LINR_EINWAHLACCOUNT);
                }
            }
        }
        else {
            createAccount(IntAccount.LINR_EINWAHLACCOUNT);
        }
    }

    /*
     * Legt einen Einwahl- oder Abrechnungsaccount fuer
     * den Auftrag an - abhaengig vom Produkt.
     */
    private void createAccount(Integer type) throws ServiceNotFoundException, StoreException {
        AccountService accountService = getCCService(AccountService.class.getName(), AccountService.class);

        if (IntAccount.LINR_VERWALTUNGSACCOUNT.equals(type)) {
            // Verwaltungsaccount anlegen
            accountService.createIntAccount(null, auftrag.getKundeNo().toString(), type);
        }
        else {
            // Einwahl- oder Abrechnungsaccount anlegen
            accountService.createIntAccount(auftragTechnik, produkt.getAccountVorsatz(), type);
        }
    }

    /* Liest die Auftragsdaten (AuftragDaten und AuftragTechnik) zu dem Auftrag aus */
    private void init() throws ServiceNotFoundException, FindException {
        CCAuftragService as = getCCService(CCAuftragService.class.getName(), CCAuftragService.class);
        auftragDaten = as.findAuftragDatenByAuftragId(auftrag.getId());
        auftragTechnik = as.findAuftragTechnikByAuftragId(auftrag.getId());
    }

    /* Sucht nach dem Produkt, das dem Auftrag zugeordnet ist. */
    private void search4Produkt() throws ServiceNotFoundException, FindException {
        ProduktService ps = getCCService(ProduktService.class.getName(), ProduktService.class);
        produkt = ps.findProdukt(auftragDaten.getProdId());
    }

}



