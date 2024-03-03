/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.dao.cc.ffm;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping;

/**
 * DAO-Interface fuer alle 'FFM' Objekte
 */
public interface FfmDAO extends FindDAO, FindAllDAO, StoreDAO, ByExampleDAO {

    /**
     * Finds all qualification mappings for given product id.
     * @param productId
     * @return
     */
    List<FfmQualificationMapping> findQualificationsByProduct(Long productId);

    /**
     * Finds all qualification mappings which are configured for VPN.
     * @return
     */
    List<FfmQualificationMapping> findQualifications4Vpn();

    /**
     * Finds all qualification mappings for given tech leistung id.
     * @param techLeistungId
     * @return
     */
    List<FfmQualificationMapping> findQualificationsByLeistung(Long techLeistungId);

    /**
     * Finds all qualification mappings for given standort reference id.
     * @param standortRefId
     * @return
     */
    List<FfmQualificationMapping> findQualificationsByStandortRef(Long standortRefId);
}
