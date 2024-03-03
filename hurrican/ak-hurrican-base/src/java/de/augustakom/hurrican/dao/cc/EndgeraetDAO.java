/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2004 16:53:34
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import org.hibernate.HibernateException;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>Endgeraet</code>, <code>EndgeraetTyp</code> und
 * <code>EndgeraetHerkunft</code>.
 *
 *
 */
public interface EndgeraetDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Findet alle Endgeraete mit angegebener Leistungskennzeichnung, die dem Hurrican-Produkt 'prodId' zugeordnet
     * sind.
     *
     * @param extLeistungNoOrig Leistungskennzeichnung der gesuchten Endgeraete.
     * @param prodId            Hurrican Produkt-ID, auf die die Suche beschraenkt werden soll
     * @return Liste mit Objekten des Typs 'EG'
     *
     */
    public List<EG> findValidEG(Long extLeistungNoOrig, Long prodId);

    /**
     * Ermittelt die Endgeraete, die fuer ein bestimmtes Produkt vorgesehen sind.
     *
     * @param prodId      Produkt-ID
     * @param onlyDefault Flag, ob nur nach den Default-Endgeraeten gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>EG</code>
     *
     */
    public List<EG> findEGs4Produkt(Long prodId, boolean onlyDefault);

    /**
     * Loescht alle EG-Zuordnungen zu der angegebenen Produkt-ID.
     *
     * @param prodId
     *
     */
    public void deleteProdukt2EG(Long prodId);

    /**
     * Ermittelt Views mit wichtigen Daten einer Endgeraete-Zuordnung zu einem (CC-)Auftrag.
     *
     * @param ccAuftragId CC-Auftrags ID
     * @return Liste mit Objekten des Typs <code>EG2AuftragView</code>
     *
     */
    public List<EG2AuftragView> findEG2AuftragViews(Long ccAuftragId);

    /**
     * Emittelt das Mapping eines Endgeraetes zu einem Auftrag anhand der EG2Auftrag.ID
     *
     * @param id des Modells EG2Auftrag
     * @return Objekt vom Typ EG2Auftrag
     */
    public EG2Auftrag findEG2Auftrag(Long id);

    /**
     * Entfernt eine Endgeraete-Zuordnung von einem Auftrag.
     *
     * @param eg2AuftragId ID der zu entfernenden Zuordnung.
     *
     */
    public void deleteEG2Auftrag(Long eg2AuftragId);

    /**
     * Funktion liefert alle EndgeraetAcls
     */
    public List<EndgeraetAcl> findAllEndgeraetAcls();

    /**
     * Funktion liefert EndgeraetAcl mit Name name
     *
     * @throws HibernateException
     */
    public EndgeraetAcl findEndgeraetAclByName(String name);

    /**
     * Loescht das EGAcl
     */
    public void deleteEndgeraetAcl(EndgeraetAcl endgeraetAcl);

    /**
     * Liste aller verfügbaren Endgeraetetypen
     */
    public List<EGType> findAllEGTypes();

    /**
     * Sucht den standard Endgeraet Port fuer gegebene Port Nummer. Die Ports sind nach der NO aufsteigend sortiert.
     */
    public List<EndgeraetPort> findDefaultEndgeraetPorts4Count(Integer count);
}
