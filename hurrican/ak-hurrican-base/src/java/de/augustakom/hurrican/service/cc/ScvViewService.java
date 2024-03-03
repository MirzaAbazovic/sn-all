/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2005 16:38:56
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.DBQueryDef;
import de.augustakom.hurrican.model.shared.view.IncompleteAuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.iface.FindByParamService;


/**
 * Service-Interface definiert Methoden zur Abfrage von Daten fuer die Abteilung SCV.
 *
 *
 */
public interface ScvViewService extends ICCService, FindByParamService<IncompleteAuftragView> {

    /**
     * Gibt eine Liste von <code>IncompleteAuftragView</code>-Objekten zurueck.
     */
    public static final short FIND_STRATEGY_ALL = (short) 0;

    /**
     * Such-Strategie, um alle offenen Auftraege zu finden, fuer die noch kein Bauauftrag erstellt wurde. <br>
     * Ergebnisliste enthaelt Objekte vom Typ <code>IncompleteAuftragView</code>.
     */
    public static final short FIND_STRATEGY_WITHOUT_BA = (short) 1;

    /**
     * Such-Strategie, um alle offenen Auftraege zu finden, fuer die noch kein Bauauftrag erstellt wurde und der
     * Realisierungstermin bereits abgelaufen ist.
     */
    public static final short FIND_STRATEGY_WITHOUT_BA_UEBERFAELLIG = (short) 2;

    /**
     * Such-Strategie, um alle offenen Auftraege zu finden, die eine CuDA-Bestellung besitzen, jedoch noch keine LBZ und
     * kein Rueckmelde-Datum eingetragen wurden.
     */
    public static final short FIND_STRATEGY_WITHOUT_LBZ = (short) 3;

    /**
     * Such-Strategie, um alle offenen Auftraege zu finden, die eine CuDA-Bestellung besitzen, jedoch keine Rueckmeldung
     * eingetragen ist.
     */
    public static final short FIND_STRATEGY_CUDA_BESTELLUNG_OFFEN = (short) 4;

    /**
     * Such-Strategie, um alle offenen Auftraege zu finden, die eine CuDA-Kuendigung besitzen, jedoch keine
     * Kuendigungs-Rueckmeldung eingetragen ist.
     */
    public static final short FIND_STRATEGY_CUDA_KUENDIGUNG_OFFEN = (short) 5;

    /**
     * Sucht-Strategie, um nach Auftraegen mit einem best. Vorgabe-SCV Datum und/oder Realisierungstermin zu suchen.
     * <br> Benoetigte Parameter: <ul> <li>Index 0: java.util.Date (oder null) fuer Vorgabe-SCV <li>Index 1:
     * java.util.Date (oder null) fuer Realisierungstermin </ul>
     */
    public static final short FIND_STRATEGY_VORGABESCV_AND_REALDATE = (short) 6;

    /**
     * Markiert den Service als nicht initialisiert.
     */
    public void reInitialize();

    /**
     * Sucht nach allen definierten DB-Queries.
     *
     * @return Liste mit Objekten des Typs <code>DBQueryDef</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<DBQueryDef> findDBQueryDefs() throws FindException;

    public List<IncompleteAuftragView> findByParam(short strategy, Object[] params, final Date gueltigVon) throws FindException;
}


