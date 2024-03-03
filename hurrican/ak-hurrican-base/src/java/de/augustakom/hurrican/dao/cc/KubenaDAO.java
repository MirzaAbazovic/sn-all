/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 08:30:20
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.kubena.KubenaResultView;


/**
 * DAO-Interface fuer die Kubena-Daten.
 *
 *
 */
public interface KubenaDAO extends StoreDAO, FindDAO, FindAllDAO, ByExampleDAO {

    /**
     * Loescht den KubenaHVT-Datensatz mit den angegebenen Parametern.
     *
     * @param kubenaId
     * @param hvtIdStd
     */
    public void deleteKubenaHVT(Long kubenaId, Long hvtIdStd);

    /**
     * Loescht den KubenaProdukt-Datensatz mit den angegebenen Parametern.
     *
     * @param kubenaId
     * @param prodId
     */
    public void deleteKubenaProdukt(Long kubenaId, Long prodId);

    /**
     * Ermittelt alle Auftraege, die durch die Kubena-Definition <code>kubenaId</code> betroffen sind. <br> Die Abfrage
     * geht davon aus, dass nur HVT-Standorte beruecksichtigt werden!
     *
     * @param kubenaId ID der Kubena, deren betroffene Auftraege/Kunden ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>KubenaResultView</code>.
     */
    public List<KubenaResultView> queryKubenaHVT(Long kubenaId);

    /**
     * Ermittelt alle Auftraege, die durch die Kubena-Definition <code>kubenaId</code> betroffen sind. <br> Die Abfrage
     * geht davon aus, dass nur HVT-Standorte und Produkte beruecksichtigt werden!
     *
     * @param kubenaId ID der Kubena, deren betroffene Auftraege/Kunden ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>KubenaResultView</code>.
     */
    public List<KubenaResultView> queryKubenaProd(Long kubenaId);

    /**
     * Ermittelt alle Auftraege, die durch die Kubena-Definition <code>kubenaId</code> betroffen sind. <br> Die Abfrage
     * geht davon aus, dass nur Verbindungsbezeichnungen beruecksichtigt werden!
     *
     * @param kubenaId ID der Kubena, deren betroffene Auftraege/Kunden ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>KubenaResultView</code>.
     */
    public List<KubenaResultView> queryKubenaVbz(Long kubenaId);

}


