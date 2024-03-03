/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 08:00:29
 */
package de.augustakom.hurrican.dao.cc;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;


/**
 * DAO-Interface fuer die Verwaltung von Modellen des Typs <code>PhysikUebernahme</code> und
 * <code>PhysikaenderungsTyp</code>.
 *
 *
 */
public interface PhysikUebernahmeDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Sucht nach der hoechsten Vorgangs-ID der Tabelle T_PHYSIKUEBERNAHME. <br> Die Vorgangs-ID kann dazu verwendet
     * werden, um mehrere Physik-Uebernahmen zu gruppieren.
     *
     * @return max-Wert fuer VORGANG
     */
    public Long getMaxVorgangId();

    /**
     * Sucht nach dem letzten Physikuebernahme-Eintrag mit der AuftragID-A <code>auftragId</code>.
     *
     * @param auftragId Auftrags-ID
     * @return letzter Physikuebernahme-Eintrag zu dem Auftrag oder <code>null</code>.
     */
    public PhysikUebernahme findLast4AuftragA(Long auftragId);

    /**
     * Sucht nach einer PhysikUebernahme, die einem best. Auftrag u. Verlauf zugeordnet ist.
     *
     * @param auftragId ID des Auftrags
     * @param verlaufId ID des Verlaufs.
     * @return PhysikUebernahme, die dem Verlauf zugeordnet ist.
     */
    public PhysikUebernahme findByVerlaufId(Long auftragId, Long verlaufId);

    /**
     * Sucht nach der Physikaenderungs-Typ, der einem best. Auftrag u. Verlauf zugeordnet ist.
     *
     * @param auftragId
     * @param verlaufId
     * @return
     */
    public PhysikaenderungsTyp findPhysikaenderungsTyp(Long auftragId, Long verlaufId);

}


