/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2007 15:18:16
 */
package de.augustakom.hurrican.dao.billing;

import de.augustakom.common.tools.dao.iface.FindAllDAO;


/**
 * DAO-Interface fuer die ArchPrint-Daten von Taifun. <br> In den ArchPrint-Tabellen sind Informationen ueber die
 * erstellten PostScript-Files enthalten, z.B. Anzahl Seiten, Anzahl Rechnungen etc.
 *
 *
 */
public interface ArchPrintDAO extends FindAllDAO {

    /**
     * Ermittelt die Anzahl Seiten und Rechnungen fuer eine bestimmte File-Gruppe (z.B. "%Normalversand.02%")
     *
     * @param printSetNo ID des zu verwendenden PrintSets
     * @param groupName  Name, ueber den die DB-Eintraege gruppiert werden koennen.
     * @return Array mit der Seiten- und Rechnungszahl fuer diese Gruppe. <br> Index 0: Seitenzahl <br> Index 1:
     * Rechnungszahl
     *
     */
    public Integer[] sumPagesAndBills(Long printSetNo, String groupName);

}


