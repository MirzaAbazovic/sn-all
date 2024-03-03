package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.sip.SipPeeringPartner;

/**
 * SipPeeringPartnerDao
 */
public interface SipPeeringPartnerDao extends StoreDAO, FindDAO, FindAllDAO, ByExampleDAO {

    /**
     * Ermittelt die Liste der aktiven/nicht aktiven/aller Peering Partner alphabetisch aufsteigend sortiert.
     * @param activeOnly null = alle, false = nur die nicht aktiven, true = nur die aktiven
     */
    List<SipPeeringPartner> findAllPeeringPartner(Boolean activeOnly);

    /**
     * Ermittelt den mit <b>id</b> angegebenen Peering Partner
     */
    SipPeeringPartner findPeeringPartnerById(Long id);

    /**
     * Ermittelt den mit <b>name</b> angegebenen Peering Partner
     */
    SipPeeringPartner findPeeringPartnerByName(String name);

}
