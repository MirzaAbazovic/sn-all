/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2004 15:09:11
 */
package de.augustakom.hurrican.dao.cc;

import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;


/**
 * DAO-Interface fuer Objekte des Typs <code>IntAccount</code>.
 *
 *
 */
public interface IntAccountDAO extends ByExampleDAO, FindDAO, StoreDAO, HistoryUpdateDAO<IntAccount> {

    /**
     * Sucht nach einem best. Account.
     *
     * @param account
     * @param typ
     * @param when    Gueltigkeitsdatum
     * @return Objekt vom Typ <code>IntAccount</code> oder <code>null</code>.
     */
    public IntAccount findAccount(String account, Integer accountTyp, LocalDate when);

    /**
     * Sucht nach allen Accounts, die einem best. CC-Auftrag zugeordnet sind.
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen Accounts gesucht werden.
     * @param accountTyp  (optional) gesuchter Account-Typ
     * @return Liste mit Objekten des Typs <code>IntAccount</code> oder <code>null</code>.
     */
    @Nonnull
    public List<IntAccount> findByAuftragIdAndTyp(Long ccAuftragId, @CheckForNull Integer accountTyp);

    /**
     * Sucht nach Account- und Auftrags-Daten fuer Auftraege, die einen Einwahlaccount besitzen (und keinem VPN
     * zugeordnet sind). <br> Ausserdem werden nach den Abrechnung-Accounts von SDSL-Auftraegen gesucht.
     *
     * @param kundeNo        Kundennummer zu der die Auftrags- und Account-Daten ermittelt werden sollen.
     * @param produktGruppen (optional) Angabe der Produkt-Gruppen, in denen sich die Auftraege bzw. Accounts befinden
     *                       muessen. Wird keine Produkt-Gruppe angegeben, werden alle aktiven Accounts des Kunden
     *                       ermittelt.
     * @return Liste mit Objekten des Typs <code>AuftragIntAccountView</code>
     */
    public List<AuftragIntAccountView> findAuftragAccountViews(Long kundeNo, List<Long> produktGruppen);

    /**
     * Ermittelt aus der Sequence SEQ_ACCOUNT den naechsten Wert und gibt diesen zurueck.
     *
     * @return
     */
    public Long getNextAccountValue();

}


