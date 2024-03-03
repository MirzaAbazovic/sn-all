/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 14:15:38
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;


/**
 * DAO-Interface fuer die Persistierung von HWBaugruppenChange-Modellen.
 *
 *
 */
public interface HWBaugruppenChangeDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Ermittelt alle HWBaugruppenChange Eintraege, die noch offen sind. Das bedeutet, dass der Vorgang noch nicht
     * erledigt bzw. storniert ist.
     *
     * @return Liste mit den offenen Baugruppen-Wechseln. Objekte vom Typ {@link HWBaugruppenChange}
     */
    public List<HWBaugruppenChange> findOpenHWBaugruppenChanges();

    /**
     * Loescht das angegebene {@link HWBaugruppenChangeBgType} Objekt.
     *
     * @param toDelete
     */
    public void deleteHWBaugruppenChangeBgType(HWBaugruppenChangeBgType toDelete);

    /**
     * Loescht das angegebene Port-Mapping
     *
     * @param toDelete
     */
    public void deletePort2Port(HWBaugruppenChangePort2Port toDelete);

    /**
     * Loescht alle V5-Port Eintraege zu einem bestimmten DLU-Schwenk.
     *
     * @param hwBgChangeDluId
     */
    public void deleteHWBaugruppenChangeDluV5(Long hwBgChangeDluId);

}


