/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2008 012:11:26
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN2EGPort;
import de.augustakom.hurrican.model.cc.VoipDnPlan;

/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AuftragVoIP</code>.
 */
public interface AuftragVoIPDNDAO extends FindDAO, StoreDAO {

    /**
     * Ermittelt die VoIPDN-Daten zu einem bestimmten Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen VoIPDN-Daten ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>AuftragVoIPDN</code> oder <code>null</code>.
     *
     */
    public List<AuftragVoIPDN> findAuftragVoIPDN(Long auftragId);

    /**
     * Ermittelt die VoIPDN-Daten zu einem bestimmten Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen VoIPDN-Daten ermittelt werden sollen.
     * @return Objekt vom Typ <code>AuftragVoIPDN</code> oder <code>null</code>.
     *
     */
    public AuftragVoIPDN findByAuftragIDDN(Long auftragId, Long dnNoOrig);

    public List<AuftragVoIPDN2EGPort> findAuftragVoIpdn2EgPorts(Long auftragVoipDnId, Date validAt);

    void deleteVoipDnPlan(@Nonnull VoipDnPlan voipDnPlan);
}
